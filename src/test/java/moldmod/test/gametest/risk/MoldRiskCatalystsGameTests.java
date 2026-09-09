package moldmod.test.gametest.risk;

import moldmod.block.ModBlocks;
import moldmod.risk.MoldRiskCalculator;
import moldmod.risk.MoldRiskCalculator.MoldRiskResult;
import moldmod.block.MoldyLogBlock;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public class MoldRiskCatalystsGameTests {

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testContagionCatalysts(TestContext context) {
        BlockPos center = new BlockPos(2, 2, 2);
        BlockState log = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState();

        MoldRiskResult rClean = MoldRiskCalculator.calculate(context.getWorld(), context.getAbsolutePos(center), false,
                log);

        // Aggiungo Fango (Mud)
        context.setBlockState(center.add(1, 0, 0), Blocks.MUD.getDefaultState());
        MoldRiskResult rMud = MoldRiskCalculator.calculate(context.getWorld(), context.getAbsolutePos(center), false,
                log);

        if (rMud.catalystBonus() <= rClean.catalystBonus()) {
            context.throwPositionedException("Il Fango dovrebbe incrementare il contagio!", center);
        }

        // Aggiungo Legno Marcio (Rotten Log)
        BlockState rottenLog = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState()
                .with(MoldyLogBlock.STAGE, 3);
        context.setBlockState(center.add(-1, 0, 0), rottenLog);
        MoldRiskResult rRotten = MoldRiskCalculator.calculate(context.getWorld(), context.getAbsolutePos(center), false,
                log);

        if (rRotten.catalystBonus() <= rMud.catalystBonus()) {
            context.throwPositionedException("Il legno marcio dovrebbe incrementare il contagio maggiormente!", center);
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testAllCatalystsVarieties(TestContext context) {
        BlockPos center = new BlockPos(2, 2, 2);
        BlockState log = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState();
        BlockPos absCenter = context.getAbsolutePos(center);

        MoldRiskResult rBase = MoldRiskCalculator.calculate(context.getWorld(), absCenter, false, log);

        // 1. Spore Blossom
        context.setBlockState(center.add(1, 0, 0), Blocks.SPORE_BLOSSOM.getDefaultState());
        MoldRiskResult rSpore = MoldRiskCalculator.calculate(context.getWorld(), absCenter, false, log);
        if (rSpore.catalystBonus() < 0.70) {
            context.throwPositionedException("Spore Blossom deve dare alto bonus catalizzatore!", center);
        }
        context.setBlockState(center.add(1, 0, 0), Blocks.AIR.getDefaultState());

        // 2. Podzol & Mycelium
        context.setBlockState(center.add(1, 0, 0), Blocks.PODZOL.getDefaultState());
        MoldRiskResult rPodzol = MoldRiskCalculator.calculate(context.getWorld(), absCenter, false, log);
        if (rPodzol.catalystBonus() <= rBase.catalystBonus()) {
            context.throwPositionedException("Podzol deve dare bonus catalizzatore!", center);
        }
        context.setBlockState(center.add(1, 0, 0), Blocks.AIR.getDefaultState());

        // 3. Fungi (Red and Brown mushrooms)
        context.setBlockState(center.add(1, 0, 0), Blocks.RED_MUSHROOM.getDefaultState());
        MoldRiskResult rMushroom = MoldRiskCalculator.calculate(context.getWorld(), absCenter, false, log);
        if (rMushroom.catalystBonus() <= rBase.catalystBonus()) {
            context.throwPositionedException("Fungo rosso deve dare bonus catalizzatore!", center);
        }
        context.setBlockState(center.add(1, 0, 0), Blocks.AIR.getDefaultState());

        // 4. Cauldron (Water Cauldron)
        context.setBlockState(center.add(1, 0, 0), Blocks.WATER_CAULDRON.getDefaultState());
        MoldRiskResult rCauldron = MoldRiskCalculator.calculate(context.getWorld(), absCenter, false, log);
        if (rCauldron.localHumidityBonus() <= rBase.localHumidityBonus()) {
            context.throwPositionedException("Il calderone deve incrementare l'umidità locale!", center);
        }

        context.complete();
    }
}
