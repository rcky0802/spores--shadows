package moldmod.test;

import moldmod.block.ModBlocks;
import moldmod.block.MoldyBlockHelper;
import moldmod.block.MoldyLogBlock;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public class MoldyMathTests {

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testHumidityDepthMalus(TestContext context) {
        // Usa coordinate assolute per testare la matematica della profondità
        BlockPos surfacePos = new BlockPos(0, 70, 0); 
        BlockPos deepPos = new BlockPos(0, 40, 0); 

        BlockState log = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState();

        MoldyBlockHelper.MoldRiskResult surfaceR = MoldyBlockHelper.calculateDetailedR(context.getWorld(), surfacePos, false, log);
        MoldyBlockHelper.MoldRiskResult deepR = MoldyBlockHelper.calculateDetailedR(context.getWorld(), deepPos, false, log);

        if (deepR.depthModifier() <= surfaceR.depthModifier()) {
            context.throwPositionedException("Il modificatore di profondità a Y=40 (" + deepR.depthModifier() + ") dovrebbe essere > di Y=70 (" + surfaceR.depthModifier() + ")", deepPos);
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testHumidityLocalBonus(TestContext context) {
        BlockPos center = new BlockPos(2, 2, 2);
        BlockState log = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState();
        
        MoldyBlockHelper.MoldRiskResult rNoWater = MoldyBlockHelper.calculateDetailedR(context.getWorld(), context.getAbsolutePos(center), false, log);
        
        // Piazza acqua vicina
        context.setBlockState(center.add(1, 0, 0), Blocks.WATER.getDefaultState());
        
        MoldyBlockHelper.MoldRiskResult rWithWater = MoldyBlockHelper.calculateDetailedR(context.getWorld(), context.getAbsolutePos(center), false, log);
        
        if (rWithWater.localHumidityBonus() <= rNoWater.localHumidityBonus()) {
            context.throwPositionedException("L'acqua non ha incrementato l'umidità! Prima: " + rNoWater.localHumidityBonus() + ", Dopo: " + rWithWater.localHumidityBonus(), center);
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testLightUV(TestContext context) {
        BlockPos center = new BlockPos(2, 2, 2);
        BlockState log = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState();
        
        // A cielo aperto la luce è 15, l'UV deve essere 0.0
        MoldyBlockHelper.MoldRiskResult rLight15 = MoldyBlockHelper.calculateDetailedR(context.getWorld(), context.getAbsolutePos(center), false, log);
        if (rLight15.Luv() > 0.0) {
            context.throwPositionedException("La luce solare 15 dovrebbe dare 0.0 UV, trovato: " + rLight15.Luv(), center);
        }
        
        // Costruiamo un tetto di pietra per bloccare la luce solare
        for(int x = 0; x <= 4; x++) {
            for(int z = 0; z <= 4; z++) {
                context.setBlockState(new BlockPos(x, 4, z), Blocks.STONE.getDefaultState());
            }
        }
        
        // Aspettiamo 5 tick per dare tempo al motore di illuminazione di propagare l'ombra
        context.waitAndRun(5, () -> {
            MoldyBlockHelper.MoldRiskResult rDark = MoldyBlockHelper.calculateDetailedR(context.getWorld(), context.getAbsolutePos(center), false, log);
            if (rDark.Luv() <= 0.0) {
                context.throwPositionedException("L'ombra dovrebbe dare > 0.0 UV, trovato: " + rDark.Luv(), center);
            }
            context.complete();
        });
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testSusceptibility(TestContext context) {
        // Usa coordinate assolute
        BlockPos pos = new BlockPos(0, 64, 0);
        
        BlockState log = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState();
        BlockState strippedLog = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.STRIPPED_OAK_LOG).getDefaultState();
        BlockState planks = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_PLANKS).getDefaultState();
        
        MoldyBlockHelper.MoldRiskResult rLog = MoldyBlockHelper.calculateDetailedR(context.getWorld(), pos, false, log);
        MoldyBlockHelper.MoldRiskResult rStripped = MoldyBlockHelper.calculateDetailedR(context.getWorld(), pos, false, strippedLog);
        MoldyBlockHelper.MoldRiskResult rPlanks = MoldyBlockHelper.calculateDetailedR(context.getWorld(), pos, false, planks);
        
        if (rStripped.Smat() <= rLog.Smat()) {
            context.throwPositionedException("Il legno scortecciato deve essere PIU' suscettibile del tronco grezzo!", pos);
        }
        if (rPlanks.Smat() >= rLog.Smat()) {
            context.throwPositionedException("Le assi devono essere MENO suscettibili del tronco grezzo!", pos);
        }
        
        context.complete();
    }
    
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testTemperatureDepthNormalization(TestContext context) {
        // Sotto Y=48, la temperatura si normalizza sempre a cave_temperature (0.5), che è nel range vitale [0.15, 1.5]
        BlockPos deepPos = new BlockPos(0, 40, 0); 
        BlockState log = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState();
        
        MoldyBlockHelper.MoldRiskResult rDeep = MoldyBlockHelper.calculateDetailedR(context.getWorld(), deepPos, false, log);
        
        if (rDeep.Tmult() != 1.0) {
            context.throwPositionedException("Nel sottosuolo (Y=40) la temperatura dovrebbe essere normalizzata nel range vitale (TMult=1.0)!", deepPos);
        }
        
        context.complete();
    }
    
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testTemperatureAltitudeFreezing(TestContext context) {
        // Sopra Y=256, la temperatura congela a -0.5, uccidendo la muffa
        BlockPos highPos = new BlockPos(0, 300, 0); 
        BlockState log = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState();
        
        MoldyBlockHelper.MoldRiskResult rHigh = MoldyBlockHelper.calculateDetailedR(context.getWorld(), highPos, false, log);
        
        if (rHigh.Tmult() != 0.0) {
            context.throwPositionedException("Ad alta quota (Y=300) la temperatura dovrebbe congelare la muffa (TMult=0.0)!", highPos);
        }
        
        context.complete();
    }
    
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testContagionCatalysts(TestContext context) {
        BlockPos center = new BlockPos(2, 2, 2);
        BlockState log = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState();
        
        MoldyBlockHelper.MoldRiskResult rClean = MoldyBlockHelper.calculateDetailedR(context.getWorld(), context.getAbsolutePos(center), false, log);
        
        // Aggiungo Fango (Mud)
        context.setBlockState(center.add(1, 0, 0), Blocks.MUD.getDefaultState());
        MoldyBlockHelper.MoldRiskResult rMud = MoldyBlockHelper.calculateDetailedR(context.getWorld(), context.getAbsolutePos(center), false, log);
        
        if (rMud.catalystBonus() <= rClean.catalystBonus()) {
            context.throwPositionedException("Il Fango dovrebbe incrementare il contagio!", center);
        }
        
        // Aggiungo Legno Marcio (Rotten Log)
        BlockState rottenLog = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState().with(MoldyLogBlock.STAGE, 3);
        context.setBlockState(center.add(-1, 0, 0), rottenLog);
        MoldyBlockHelper.MoldRiskResult rRotten = MoldyBlockHelper.calculateDetailedR(context.getWorld(), context.getAbsolutePos(center), false, log);
        
        if (rRotten.catalystBonus() <= rMud.catalystBonus()) {
            context.throwPositionedException("Il legno marcio dovrebbe incrementare il contagio maggiormente!", center);
        }
        
        context.complete();
    }
}
