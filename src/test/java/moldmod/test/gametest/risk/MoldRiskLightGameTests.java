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

public class MoldRiskLightGameTests {

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testLightUV(TestContext context) {
        BlockPos center = BlockPos.ORIGIN;
        BlockState log = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState();

        // 1. Posizioniamo una fonte di luce (Glowstone) sopra il blocco
        context.setBlockState(center.up(), Blocks.GLOWSTONE.getDefaultState());

        context.waitAndRun(2, () -> {
            MoldRiskResult rLight = MoldRiskCalculator.calculate(context.getWorld(), context.getAbsolutePos(center),
                    false, log);

            // 2. Rimuoviamo la luce
            context.setBlockState(center.up(), Blocks.AIR.getDefaultState());

            context.waitAndRun(5, () -> {
                MoldRiskResult rDark = MoldRiskCalculator.calculate(context.getWorld(), context.getAbsolutePos(center),
                    false, log);
                if (rDark.Luv() < rLight.Luv()) {
                    context.throwPositionedException("Nel buio totale Luv (" + rDark.Luv()
                            + ") deve essere >= che sotto luce (" + rLight.Luv() + ")!", center);
                }
                context.complete();
            });
        });
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testSusceptibility(TestContext context) {
        // Usa coordinate assolute
        BlockPos pos = new BlockPos(0, 64, 0);

        BlockState log = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState();
        BlockState strippedLog = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.STRIPPED_OAK_LOG).getDefaultState();
        BlockState planks = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_PLANKS).getDefaultState();

        MoldRiskResult rLog = MoldRiskCalculator.calculate(context.getWorld(), pos, false, log);
        MoldRiskResult rStripped = MoldRiskCalculator.calculate(context.getWorld(), pos, false, strippedLog);
        MoldRiskResult rPlanks = MoldRiskCalculator.calculate(context.getWorld(), pos, false, planks);

        if (rStripped.Smat() <= rLog.Smat()) {
            context.throwPositionedException("Il legno scortecciato deve essere PIU' suscettibile del tronco grezzo!",
                    pos);
        }
        if (rPlanks.Smat() >= rLog.Smat()) {
            context.throwPositionedException("Le assi devono essere MENO suscettibili del tronco grezzo!", pos);
        }

        context.complete();
    }
}
