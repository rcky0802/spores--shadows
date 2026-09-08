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

public class MoldRiskHumidityGameTests {

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testHumidityDepthMalus(TestContext context) {
        // Usa coordinate assolute per testare la matematica della profondità
        BlockPos surfacePos = new BlockPos(0, 70, 0);
        BlockPos midPos = new BlockPos(0, 32, 0);
        BlockPos capPos = new BlockPos(0, 0, 0);
        BlockPos deepPos = new BlockPos(0, -50, 0);

        BlockState log = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState();

        MoldRiskResult surfaceR = MoldRiskCalculator.calculate(context.getWorld(), surfacePos, false, log);
        MoldRiskResult midR = MoldRiskCalculator.calculate(context.getWorld(), midPos, false, log);
        MoldRiskResult capR = MoldRiskCalculator.calculate(context.getWorld(), capPos, false, log);
        MoldRiskResult deepR = MoldRiskCalculator.calculate(context.getWorld(), deepPos, false, log);

        if (surfaceR.depthModifier() != 0.0) {
            context.throwPositionedException(
                    "In superficie a Y=70 il depthModifier deve essere 0.0, trovato: " + surfaceR.depthModifier(),
                    surfacePos);
        }
        if (midR.depthModifier() <= surfaceR.depthModifier()) {
            context.throwPositionedException(
                    "A Y=32 (" + midR.depthModifier() + ") il depthModifier deve essere > di Y=70", midPos);
        }
        if (capR.depthModifier() <= midR.depthModifier()) {
            context.throwPositionedException(
                    "A Y=0 (" + capR.depthModifier() + ") il depthModifier deve essere > di Y=32", capPos);
        }
        if (deepR.depthModifier() < capR.depthModifier()) {
            context.throwPositionedException("In Deepslate a Y=-50 (" + deepR.depthModifier()
                    + ") il depthModifier deve essere >= di Y=0 (" + capR.depthModifier() + ")", deepPos);
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testHumidityLocalBonus(TestContext context) {
        BlockPos center = new BlockPos(2, 2, 2);
        BlockState log = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState();

        MoldRiskResult rNoWater = MoldRiskCalculator.calculate(context.getWorld(), context.getAbsolutePos(center),
                false, log);

        // Piazza acqua vicina
        context.setBlockState(center.add(1, 0, 0), Blocks.WATER.getDefaultState());

        MoldRiskResult rWithWater = MoldRiskCalculator.calculate(context.getWorld(), context.getAbsolutePos(center),
                false, log);

        if (rWithWater.localHumidityBonus() <= rNoWater.localHumidityBonus()) {
            context.throwPositionedException("L'acqua non ha incrementato l'umidità! Prima: "
                    + rNoWater.localHumidityBonus() + ", Dopo: " + rWithWater.localHumidityBonus(), center);
        }

        context.complete();
    }
}
