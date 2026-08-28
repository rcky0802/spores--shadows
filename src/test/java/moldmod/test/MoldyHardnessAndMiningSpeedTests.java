package moldmod.test;

import moldmod.SporesShadows;
import moldmod.block.MoldyLogBlock;
import moldmod.config.ModConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

public class MoldyHardnessAndMiningSpeedTests {

    // ============================================
    // === HARDNESS SCALING TESTS (ALL PRODUCTS) ===
    // ============================================

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testHardnessScalingAcrossAllProductsAndStages(TestContext context) {
        BlockPos pos = new BlockPos(0, 2, 0);
        ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();

        for (var product : MoldyWoodTestHelper.getAllWoodProducts()) {
            String baseName = product.baseName();

            Block moldyBlock = Registries.BLOCK.get(SporesShadows.id("moldy_" + baseName));
            Block waxedBlock = Registries.BLOCK.get(SporesShadows.id("waxed_" + baseName));

            if (moldyBlock == Blocks.AIR || waxedBlock == Blocks.AIR) {
                context.throwPositionedException("Block for " + baseName + " is AIR!", pos);
            }

            testProductHardness(context, config, moldyBlock, baseName, false, pos);
            testProductHardness(context, config, waxedBlock, baseName, true, pos);
        }

        context.complete();
    }

    private void testProductHardness(TestContext context, ModConfig config, Block block, String baseName, boolean isWaxed, BlockPos pos) {
        float stage0Hardness = -1.0f;
        float prevHardness = 999.0f;

        for (int stage = 0; stage <= 3; stage++) {
            BlockState state = block.getDefaultState()
                    .with(MoldyLogBlock.STAGE, stage)
                    .with(MoldyLogBlock.WAXED, isWaxed);

            float hardness = state.getHardness(context.getWorld(), context.getAbsolutePos(pos));

            if (stage == 0) {
                stage0Hardness = hardness;
            } else {
                // Must strictly decrease across stages
                if (hardness >= prevHardness) {
                    context.throwPositionedException("Hardness did not decrease for " + baseName + " (waxed=" + isWaxed + ") at stage " + stage + "! prev=" + prevHardness + " current=" + hardness, pos);
                }

                float expectedMultiplier = (stage == 1) ? config.hardness.stage_1_multiplier
                        : (stage == 2) ? config.hardness.stage_2_multiplier
                        : config.hardness.stage_3_multiplier;

                float expectedHardness = stage0Hardness * expectedMultiplier;
                if (Math.abs(hardness - expectedHardness) > 1e-3f) {
                    context.throwPositionedException("Hardness mismatch for " + baseName + " (waxed=" + isWaxed + ") stage " + stage + "! expected=" + expectedHardness + " got=" + hardness, pos);
                }
            }

            prevHardness = hardness;
        }
    }

    // ============================================
    // === MINING SPEED TESTS (calcBlockBreakingDelta) ===
    // ============================================

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testMiningSpeedAcrossStages(TestContext context) {
        BlockPos pos = new BlockPos(0, 2, 0);

        PlayerEntity playerWithAxe = context.createMockPlayer(net.minecraft.world.GameMode.SURVIVAL);
        playerWithAxe.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.IRON_AXE));

        PlayerEntity playerWithHand = context.createMockPlayer(net.minecraft.world.GameMode.SURVIVAL);
        playerWithHand.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);

        for (var product : MoldyWoodTestHelper.getAllWoodProducts()) {
            String baseName = product.baseName();
            if (baseName.contains("button") || baseName.contains("pressure_plate")) continue;

            Block moldyBlock = Registries.BLOCK.get(SporesShadows.id("moldy_" + baseName));
            Block waxedBlock = Registries.BLOCK.get(SporesShadows.id("waxed_" + baseName));

            for (Block block : new Block[]{moldyBlock, waxedBlock}) {
                boolean isWaxed = (block == waxedBlock);

                // Stage 1: Axe is faster than hand
                BlockState stage1State = block.getDefaultState()
                        .with(MoldyLogBlock.STAGE, 1)
                        .with(MoldyLogBlock.WAXED, isWaxed);
                float axeSpeed1 = stage1State.calcBlockBreakingDelta(playerWithAxe, context.getWorld(), context.getAbsolutePos(pos));
                float handSpeed1 = stage1State.calcBlockBreakingDelta(playerWithHand, context.getWorld(), context.getAbsolutePos(pos));
                if (axeSpeed1 <= handSpeed1) {
                    context.throwPositionedException("Axe should be faster than hand on Stage 1 for " + baseName + "! Axe: " + axeSpeed1 + ", Hand: " + handSpeed1, pos);
                }

                // Stage 3: Axe and Hand have exact same speed (tool efficiency neutralised)
                BlockState stage3State = block.getDefaultState()
                        .with(MoldyLogBlock.STAGE, 3)
                        .with(MoldyLogBlock.WAXED, isWaxed);
                float axeSpeed3 = stage3State.calcBlockBreakingDelta(playerWithAxe, context.getWorld(), context.getAbsolutePos(pos));
                float handSpeed3 = stage3State.calcBlockBreakingDelta(playerWithHand, context.getWorld(), context.getAbsolutePos(pos));
                if (Math.abs(axeSpeed3 - handSpeed3) > 1e-5f) {
                    context.throwPositionedException("Axe and Hand should have the EXACT SAME mining speed on Stage 3 for " + baseName + "! Axe: " + axeSpeed3 + ", Hand: " + handSpeed3, pos);
                }
            }
        }

        context.complete();
    }
}
