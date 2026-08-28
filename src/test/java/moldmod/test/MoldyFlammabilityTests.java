package moldmod.test;

import moldmod.SporesShadows;
import moldmod.block.MoldyLogBlock;
import moldmod.config.ModConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FireBlock;
import net.minecraft.registry.Registries;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

import java.lang.reflect.Method;

public class MoldyFlammabilityTests {

    // ============================================
    // === FLAMMABILITY TESTS ===
    // ============================================

    private static int getFireBurnChance(FireBlock fireBlock, BlockState state) {
        try {
            Method m = FireBlock.class.getDeclaredMethod("getBurnChance", BlockState.class);
            m.setAccessible(true);
            return (int) m.invoke(fireBlock, state);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static int getFireSpreadChance(FireBlock fireBlock, BlockState state) {
        try {
            Method m = FireBlock.class.getDeclaredMethod("getSpreadChance", BlockState.class);
            m.setAccessible(true);
            return (int) m.invoke(fireBlock, state);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testFlammabilityScaling(TestContext context) {
        FireBlock fireBlock = (FireBlock) Blocks.FIRE;
        ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();

        for (var product : MoldyWoodTestHelper.getAllWoodProducts()) {
            String baseName = product.baseName();
            boolean isNether = product.woodType().isNether();

            Block moldyBlock = Registries.BLOCK.get(SporesShadows.id("moldy_" + baseName));
            Block waxedBlock = Registries.BLOCK.get(SporesShadows.id("waxed_" + baseName));

            if (moldyBlock == Blocks.AIR || waxedBlock == Blocks.AIR) {
                context.throwPositionedException("Block for " + baseName + " is AIR!", new BlockPos(0, 0, 0));
            }

            if (isNether) {
                // Nether wood variants (even waxed or across all infected stages 0-3) MUST NEVER burn
                for (int stage = 0; stage <= 3; stage++) {
                    BlockState moldyState = moldyBlock.getDefaultState().with(MoldyLogBlock.STAGE, stage);
                    BlockState waxedState = waxedBlock.getDefaultState().with(MoldyLogBlock.STAGE, stage).with(MoldyLogBlock.WAXED, true);

                    int moldyBurn = getFireBurnChance(fireBlock, moldyState);
                    int moldySpread = getFireSpreadChance(fireBlock, moldyState);
                    int waxedBurn = getFireBurnChance(fireBlock, waxedState);
                    int waxedSpread = getFireSpreadChance(fireBlock, waxedState);

                    if (moldyBurn != 0 || moldySpread != 0) {
                        context.throwPositionedException("Nether wood " + baseName + " stage " + stage + " is flammable! burn=" + moldyBurn + " spread=" + moldySpread, new BlockPos(0, 0, 0));
                    }
                    if (waxedBurn != 0 || waxedSpread != 0) {
                        context.throwPositionedException("Nether waxed wood " + baseName + " stage " + stage + " is flammable! burn=" + waxedBurn + " spread=" + waxedSpread, new BlockPos(0, 0, 0));
                    }
                }
            } else {
                // Non-redstone overworld wood products are flammable and scale across stages
                if (!baseName.contains("button") && !baseName.contains("pressure_plate")) {
                    int prevNormalBurn = -1;
                    int prevNormalSpread = -1;
                    int prevWaxedBurn = -1;
                    int prevWaxedSpread = -1;

                    for (int stage = 0; stage <= 3; stage++) {
                        BlockState normalState = moldyBlock.getDefaultState().with(MoldyLogBlock.STAGE, stage).with(MoldyLogBlock.WAXED, false);
                        BlockState waxedState = waxedBlock.getDefaultState().with(MoldyLogBlock.STAGE, stage).with(MoldyLogBlock.WAXED, true);

                        int normalBurn = getFireBurnChance(fireBlock, normalState);
                        int normalSpread = getFireSpreadChance(fireBlock, normalState);
                        int waxedBurn = getFireBurnChance(fireBlock, waxedState);
                        int waxedSpread = getFireSpreadChance(fireBlock, waxedState);

                        // Strict monotonic increase from Stage 0 to Stage 3 for normal wood
                        if (normalBurn <= prevNormalBurn || normalSpread <= prevNormalSpread) {
                            context.throwPositionedException("Normal flammability did not strictly increase for " + baseName + " at stage " + stage + "! burn=" + normalBurn + " spread=" + normalSpread, new BlockPos(0, 0, 0));
                        }

                        // Strict monotonic increase from Stage 0 to Stage 3 for waxed wood
                        if (waxedBurn <= prevWaxedBurn || waxedSpread <= prevWaxedSpread) {
                            context.throwPositionedException("Waxed flammability did not strictly increase for " + baseName + " at stage " + stage + "! burn=" + waxedBurn + " spread=" + waxedSpread, new BlockPos(0, 0, 0));
                        }

                        // Waxed burn bonus check
                        if (waxedBurn != normalBurn + config.flammability.waxed_burn_bonus) {
                            context.throwPositionedException("Waxed bonus mismatch for " + baseName + " at stage " + stage + "! expected=" + (normalBurn + config.flammability.waxed_burn_bonus) + " got=" + waxedBurn, new BlockPos(0, 0, 0));
                        }

                        prevNormalBurn = normalBurn;
                        prevNormalSpread = normalSpread;
                        prevWaxedBurn = waxedBurn;
                        prevWaxedSpread = waxedSpread;
                    }
                }
            }
        }
        context.complete();
    }
}
