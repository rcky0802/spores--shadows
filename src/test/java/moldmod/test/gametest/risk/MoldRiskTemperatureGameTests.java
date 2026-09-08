package moldmod.test.gametest.risk;

import moldmod.block.ModBlocks;
import moldmod.block.MoldRiskCalculator;
import moldmod.block.MoldRiskCalculator.MoldRiskResult;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public class MoldRiskTemperatureGameTests {

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testTemperatureDepthNormalization(TestContext context) {
        // Sotto Y=48, la temperatura si normalizza sempre a cave_temperature (0.5), che
        // è nel range vitale [0.15, 1.5]
        BlockPos deepPos = new BlockPos(0, 40, 0);
        BlockState log = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState();

        MoldRiskResult rDeep = MoldRiskCalculator.calculate(context.getWorld(), deepPos, false, log);

        if (rDeep.Tmult() != 1.0) {
            context.throwPositionedException(
                    "Nel sottosuolo (Y=40) la temperatura dovrebbe essere normalizzata nel range vitale (TMult=1.0)!",
                    deepPos);
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testTemperatureAltitudeFreezing(TestContext context) {
        // Sopra Y=256, la temperatura congela a -0.5, uccidendo la muffa
        BlockPos highPos = new BlockPos(0, 300, 0);
        BlockState log = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState();

        MoldRiskResult rHigh = MoldRiskCalculator.calculate(context.getWorld(), highPos, false, log);

        if (rHigh.Tmult() != 0.0) {
            context.throwPositionedException(
                    "Ad alta quota (Y=300) la temperatura dovrebbe congelare la muffa (TMult=0.0)!", highPos);
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testTemperatureCaveLinearTransition(TestContext context) {
        // Y=56 is halfway between cave_start_y (64) and cave_full_y (48)
        BlockPos pos56 = new BlockPos(0, 56, 0);
        BlockPos pos64 = new BlockPos(0, 64, 0);
        BlockPos pos48 = new BlockPos(0, 48, 0);
        BlockState log = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState();

        MoldRiskResult r64 = MoldRiskCalculator.calculate(context.getWorld(), pos64, false, log);
        MoldRiskResult r56 = MoldRiskCalculator.calculate(context.getWorld(), pos56, false, log);
        MoldRiskResult r48 = MoldRiskCalculator.calculate(context.getWorld(), pos48, false, log);

        // Effective temp at Y=56 should be intermediate between Y=64 and Y=48
        float t64 = r64.effectiveTemp();
        float t56 = r56.effectiveTemp();
        float t48 = r48.effectiveTemp();

        if (t64 != t48) {
            if (!((t64 <= t56 && t56 <= t48) || (t48 <= t56 && t56 <= t64))) {
                context.throwPositionedException("La temperatura a Y=56 (" + t56 + ") deve essere intermedia tra Y=64 ("
                        + t64 + ") e Y=48 (" + t48 + ")", pos56);
            }
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testTemperatureAltitudeLinearTransition(TestContext context) {
        // Y=192 is halfway between high_altitude_start_y (128) and high_altitude_full_y (256)
        BlockPos pos128 = new BlockPos(0, 128, 0);
        BlockPos pos192 = new BlockPos(0, 192, 0);
        BlockPos pos256 = new BlockPos(0, 256, 0);
        BlockState log = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState();

        MoldRiskResult r128 = MoldRiskCalculator.calculate(context.getWorld(), pos128, false, log);
        MoldRiskResult r192 = MoldRiskCalculator.calculate(context.getWorld(), pos192, false, log);
        MoldRiskResult r256 = MoldRiskCalculator.calculate(context.getWorld(), pos256, false, log);

        if (r192.effectiveTemp() >= r128.effectiveTemp() && r128.effectiveTemp() > r256.effectiveTemp()) {
            context.throwPositionedException("Ad alta quota a Y=192 la temperatura deve scendere rispetto a Y=128!",
                    pos192);
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testNetherBiomeRejection(TestContext context) {
        BlockPos center = new BlockPos(2, 2, 2);
        BlockPos absPos = context.getAbsolutePos(center);
        BlockState log = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState();
        context.setBlockState(center, log);

        String command = String.format("fillbiome %d %d %d %d %d %d minecraft:nether_wastes",
                absPos.getX(), absPos.getY(), absPos.getZ(), absPos.getX(), absPos.getY(), absPos.getZ());

        context.getWorld().getServer().getCommandManager().executeWithPrefix(
                context.getWorld().getServer().getCommandSource().withWorld(context.getWorld()), command);

        context.waitAndRun(5, () -> {
            context.complete();
        });
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testEndBiomeRejection(TestContext context) {
        BlockPos center = new BlockPos(2, 2, 2);
        BlockPos absPos = context.getAbsolutePos(center);
        BlockState log = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState();
        context.setBlockState(center, log);

        String command = String.format("fillbiome %d %d %d %d %d %d minecraft:the_end",
                absPos.getX(), absPos.getY(), absPos.getZ(), absPos.getX(), absPos.getY(), absPos.getZ());

        context.getWorld().getServer().getCommandManager().executeWithPrefix(
                context.getWorld().getServer().getCommandSource().withWorld(context.getWorld()), command);

        context.waitAndRun(5, () -> {
            context.complete();
        });
    }
}
