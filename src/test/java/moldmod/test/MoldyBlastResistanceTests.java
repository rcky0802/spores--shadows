package moldmod.test;

import moldmod.SporesShadows;
import moldmod.block.MoldyLogBlock;
import moldmod.config.ModConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluids;
import net.minecraft.registry.Registries;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.explosion.ExplosionBehavior;

import java.util.Optional;

public class MoldyBlastResistanceTests {

    // ============================================
    // === BLAST RESISTANCE TESTS ===
    // ============================================

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testBlastResistanceScaling(TestContext context) {
        ExplosionBehavior behavior = new ExplosionBehavior();
        ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();

        for (var product : MoldyWoodTestHelper.getAllWoodProducts()) {
            String baseName = product.baseName();

            Block moldyBlock = Registries.BLOCK.get(SporesShadows.id("moldy_" + baseName));
            Block waxedBlock = Registries.BLOCK.get(SporesShadows.id("waxed_" + baseName));

            if (moldyBlock == Blocks.AIR || waxedBlock == Blocks.AIR) {
                context.throwPositionedException("Block for " + baseName + " is AIR!", new BlockPos(0, 0, 0));
            }

            // Test normal and waxed across all stages 0, 1, 2, 3
            testProductBlastResistance(context, behavior, config, moldyBlock, baseName, false);
            testProductBlastResistance(context, behavior, config, waxedBlock, baseName, true);
        }
        context.complete();
    }

    private void testProductBlastResistance(TestContext context, ExplosionBehavior behavior, ModConfig config, Block block, String baseName, boolean isWaxed) {
        float stage0Res = -1f;
        float prevRes = 9999.0f;

        for (int stage = 0; stage <= 3; stage++) {
            BlockState state = block.getDefaultState()
                    .with(MoldyLogBlock.STAGE, stage)
                    .with(MoldyLogBlock.WAXED, isWaxed);

            Optional<Float> resOpt = behavior.getBlastResistance(null, context.getWorld(), new BlockPos(0, 0, 0), state, Fluids.EMPTY.getDefaultState());

            if (resOpt.isEmpty()) {
                context.throwPositionedException("Blast resistance missing for " + baseName + " stage " + stage + " (waxed=" + isWaxed + ")", new BlockPos(0, 0, 0));
            }
            float res = resOpt.get();

            if (stage == 0) {
                stage0Res = res;
            } else {
                // Must decrease strictly: Stage 0 > Stage 1 > Stage 2 > Stage 3
                if (res >= prevRes) {
                    context.throwPositionedException("Blast resistance did not decrease for " + baseName + " (waxed=" + isWaxed + ") at stage " + stage + "! prev=" + prevRes + " current=" + res, new BlockPos(0, 0, 0));
                }

                // Verify exact multipliers
                float expectedMultiplier = (stage == 1) ? config.blastResistance.stage_1_multiplier
                        : (stage == 2) ? config.blastResistance.stage_2_multiplier
                        : config.blastResistance.stage_3_multiplier;

                float expectedRes = stage0Res * expectedMultiplier;
                if (Math.abs(res - expectedRes) > 1e-3f) {
                    context.throwPositionedException("Blast resistance multiplier mismatch for " + baseName + " stage " + stage + "! expected=" + expectedRes + " got=" + res, new BlockPos(0, 0, 0));
                }
            }
            prevRes = res;
        }
    }
}
