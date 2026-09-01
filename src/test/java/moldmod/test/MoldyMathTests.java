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
        BlockPos midPos = new BlockPos(0, 32, 0); 
        BlockPos capPos = new BlockPos(0, 0, 0); 
        BlockPos deepPos = new BlockPos(0, -50, 0); 

        BlockState log = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState();

        MoldyBlockHelper.MoldRiskResult surfaceR = MoldyBlockHelper.calculateDetailedR(context.getWorld(), surfacePos, false, log);
        MoldyBlockHelper.MoldRiskResult midR = MoldyBlockHelper.calculateDetailedR(context.getWorld(), midPos, false, log);
        MoldyBlockHelper.MoldRiskResult capR = MoldyBlockHelper.calculateDetailedR(context.getWorld(), capPos, false, log);
        MoldyBlockHelper.MoldRiskResult deepR = MoldyBlockHelper.calculateDetailedR(context.getWorld(), deepPos, false, log);

        if (surfaceR.depthModifier() != 0.0) {
            context.throwPositionedException("In superficie a Y=70 il depthModifier deve essere 0.0, trovato: " + surfaceR.depthModifier(), surfacePos);
        }
        if (midR.depthModifier() <= surfaceR.depthModifier()) {
            context.throwPositionedException("A Y=32 (" + midR.depthModifier() + ") il depthModifier deve essere > di Y=70", midPos);
        }
        if (capR.depthModifier() <= midR.depthModifier()) {
            context.throwPositionedException("A Y=0 (" + capR.depthModifier() + ") il depthModifier deve essere > di Y=32", capPos);
        }
        if (deepR.depthModifier() != capR.depthModifier()) {
            context.throwPositionedException("In Deepslate a Y=-50 (" + deepR.depthModifier() + ") il depthModifier deve essere bloccato al CAP di Y=0 (" + capR.depthModifier() + ")", deepPos);
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

        // Circondiamo di luce (Glowstone) per garantire luce massima 15
        context.setBlockState(center.up(), Blocks.GLOWSTONE.getDefaultState());

        context.waitAndRun(2, () -> {
            MoldyBlockHelper.MoldRiskResult rLight15 = MoldyBlockHelper.calculateDetailedR(context.getWorld(), context.getAbsolutePos(center), false, log);
            if (rLight15.Luv() > 0.0) {
                context.throwPositionedException("La luce massima 15 dovrebbe dare 0.0 UV, trovato: " + rLight15.Luv(), center);
            }

            // Rimuoviamo la fonte di luce e creiamo una scatola di pietra per il buio totale
            context.setBlockState(center.up(), Blocks.AIR.getDefaultState());
            for (int x = 0; x <= 4; x++) {
                for (int y = 0; y <= 4; y++) {
                    for (int z = 0; z <= 4; z++) {
                        if (x == 0 || x == 4 || y == 0 || y == 4 || z == 0 || z == 4) {
                            context.setBlockState(new BlockPos(x, y, z), Blocks.STONE.getDefaultState());
                        }
                    }
                }
            }

            context.waitAndRun(5, () -> {
                MoldyBlockHelper.MoldRiskResult rDark = MoldyBlockHelper.calculateDetailedR(context.getWorld(), context.getAbsolutePos(center), false, log);
                if (rDark.Luv() <= 0.0) {
                    context.throwPositionedException("L'oscurità totale dovrebbe dare > 0.0 UV, trovato: " + rDark.Luv(), center);
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
    
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testNetherBiomeRejection(TestContext context) {
        BlockPos center = new BlockPos(2, 2, 2);
        BlockPos absPos = context.getAbsolutePos(center);
        BlockState log = ModBlocks.VANILLA_TO_MOLDY.get(net.minecraft.block.Blocks.OAK_LOG).getDefaultState();
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
        BlockState log = ModBlocks.VANILLA_TO_MOLDY.get(net.minecraft.block.Blocks.OAK_LOG).getDefaultState();
        context.setBlockState(center, log);
        
        String command = String.format("fillbiome %d %d %d %d %d %d minecraft:the_end", 
            absPos.getX(), absPos.getY(), absPos.getZ(), absPos.getX(), absPos.getY(), absPos.getZ());
            
        context.getWorld().getServer().getCommandManager().executeWithPrefix(
            context.getWorld().getServer().getCommandSource().withWorld(context.getWorld()), command);
            
        context.waitAndRun(5, () -> {
            context.complete();
        });
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testAerationDryingOpenAir(TestContext context) {
        BlockPos center = new BlockPos(2, 2, 2);
        BlockState log = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState();
        context.setBlockState(center, log);

        MoldyBlockHelper.MoldRiskResult result = MoldyBlockHelper.calculateDetailedR(context.getWorld(), context.getAbsolutePos(center), false, log);
        if (result.aeration() != 1.0) {
            context.throwPositionedException("All'aperto l'areazione deve essere 1.0, trovato: " + result.aeration(), center);
        }
        if (result.aerationDryingBonus() <= 0.0) {
            context.throwPositionedException("All'aperto il bonus di asciugatura deve essere > 0.0, trovato: " + result.aerationDryingBonus(), center);
        }
        if (result.Heff() >= result.Hraw() && result.Hraw() > 0.0) {
            context.throwPositionedException("L'areazione dovrebbe ridurre Heff rispetto a Hraw!", center);
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testSealedCellNoAeration(TestContext context) {
        // Costruiamo una cella sigillata 3x3x3 di pietra attorno al centro
        for (int x = 1; x <= 3; x++) {
            for (int y = 1; y <= 3; y++) {
                for (int z = 1; z <= 3; z++) {
                    context.setBlockState(new BlockPos(x, y, z), Blocks.STONE.getDefaultState());
                }
            }
        }
        // Il blocco al centro (2, 2, 2) è circondato da pietra solida su tutte le 6 facce
        BlockPos center = new BlockPos(2, 2, 2);
        BlockState log = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState();
        context.setBlockState(center, log);

        MoldyBlockHelper.MoldRiskResult result = MoldyBlockHelper.calculateDetailedR(context.getWorld(), context.getAbsolutePos(center), false, log);
        if (result.aeration() != 0.0) {
            context.throwPositionedException("In una cella completamente sigillata l'areazione deve essere 0.0, trovato: " + result.aeration(), center);
        }
        if (result.aerationDryingBonus() != 0.0) {
            context.throwPositionedException("In una cella sigillata il bonus asciugatura deve essere 0.0, trovato: " + result.aerationDryingBonus(), center);
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testAirMiasmaSporePressure(TestContext context) {
        // Stanza chiusa 5x4x5 in pietra
        for (int x = 0; x <= 4; x++) {
            for (int y = 0; y <= 3; y++) {
                for (int z = 0; z <= 4; z++) {
                    boolean isWall = (x == 0 || x == 4 || y == 0 || y == 3 || z == 0 || z == 4);
                    context.setBlockState(new BlockPos(x, y, z), isWall ? Blocks.STONE.getDefaultState() : Blocks.AIR.getDefaultState());
                }
            }
        }

        // Blocco bersaglio pulito in una parete interna
        BlockPos targetPos = new BlockPos(1, 1, 1);
        BlockState cleanLog = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState();
        context.setBlockState(targetPos, cleanLog);

        // Blocco marcio posto a distanza 3 blocchi (non adiacente, quindi catalystBonus = 0)
        BlockPos distantRottenPos = new BlockPos(3, 1, 3);
        BlockState rottenLog = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState().with(MoldyLogBlock.STAGE, 3);
        context.setBlockState(distantRottenPos, rottenLog);

        MoldyBlockHelper.MoldRiskResult result = MoldyBlockHelper.calculateDetailedR(context.getWorld(), context.getAbsolutePos(targetPos), false, cleanLog);
        
        if (result.miasmaBonus() <= 0.0) {
            context.throwPositionedException("In una stanza chiusa con legno marcio, il contagio aereo da miasma deve essere > 0.0, trovato: " + result.miasmaBonus(), targetPos);
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testWaxedAndRottenStage3EarlyExit(TestContext context) {
        BlockPos pos = new BlockPos(0, 64, 0);
        BlockState log = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState();
        BlockState rottenStage3 = log.with(MoldyLogBlock.STAGE, 3);

        // 1. Waxed = true -> R must be exactly 0.0
        MoldyBlockHelper.MoldRiskResult rWaxed = MoldyBlockHelper.calculateDetailedR(context.getWorld(), pos, true, log);
        if (rWaxed.R() != 0.0) {
            context.throwPositionedException("Il blocco cerato deve avere R = 0.0, trovato: " + rWaxed.R(), pos);
        }

        // 2. Stage = 3 (Rotten) -> R must be exactly 0.0
        MoldyBlockHelper.MoldRiskResult rRotten = MoldyBlockHelper.calculateDetailedR(context.getWorld(), pos, false, rottenStage3);
        if (rRotten.R() != 0.0) {
            context.throwPositionedException("Il blocco allo Stadio 3 deve avere R = 0.0, trovato: " + rRotten.R(), pos);
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

        MoldyBlockHelper.MoldRiskResult r64 = MoldyBlockHelper.calculateDetailedR(context.getWorld(), pos64, false, log);
        MoldyBlockHelper.MoldRiskResult r56 = MoldyBlockHelper.calculateDetailedR(context.getWorld(), pos56, false, log);
        MoldyBlockHelper.MoldRiskResult r48 = MoldyBlockHelper.calculateDetailedR(context.getWorld(), pos48, false, log);

        // Effective temp at Y=56 should be intermediate between Y=64 and Y=48
        float t64 = r64.effectiveTemp();
        float t56 = r56.effectiveTemp();
        float t48 = r48.effectiveTemp();

        if (t64 != t48) {
            if (!((t64 <= t56 && t56 <= t48) || (t48 <= t56 && t56 <= t64))) {
                context.throwPositionedException("La temperatura a Y=56 (" + t56 + ") deve essere intermedia tra Y=64 (" + t64 + ") e Y=48 (" + t48 + ")", pos56);
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

        MoldyBlockHelper.MoldRiskResult r128 = MoldyBlockHelper.calculateDetailedR(context.getWorld(), pos128, false, log);
        MoldyBlockHelper.MoldRiskResult r192 = MoldyBlockHelper.calculateDetailedR(context.getWorld(), pos192, false, log);
        MoldyBlockHelper.MoldRiskResult r256 = MoldyBlockHelper.calculateDetailedR(context.getWorld(), pos256, false, log);

        if (r192.effectiveTemp() >= r128.effectiveTemp() && r128.effectiveTemp() > r256.effectiveTemp()) {
            context.throwPositionedException("Ad alta quota a Y=192 la temperatura deve scendere rispetto a Y=128!", pos192);
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testAllCatalystsVarieties(TestContext context) {
        BlockPos center = new BlockPos(2, 2, 2);
        BlockState log = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState();
        BlockPos absCenter = context.getAbsolutePos(center);

        MoldyBlockHelper.MoldRiskResult rBase = MoldyBlockHelper.calculateDetailedR(context.getWorld(), absCenter, false, log);

        // 1. Spore Blossom
        context.setBlockState(center.add(1, 0, 0), Blocks.SPORE_BLOSSOM.getDefaultState());
        MoldyBlockHelper.MoldRiskResult rSpore = MoldyBlockHelper.calculateDetailedR(context.getWorld(), absCenter, false, log);
        if (rSpore.catalystBonus() < 0.70) {
            context.throwPositionedException("Spore Blossom deve dare alto bonus catalizzatore!", center);
        }
        context.setBlockState(center.add(1, 0, 0), Blocks.AIR.getDefaultState());

        // 2. Podzol & Mycelium
        context.setBlockState(center.add(1, 0, 0), Blocks.PODZOL.getDefaultState());
        MoldyBlockHelper.MoldRiskResult rPodzol = MoldyBlockHelper.calculateDetailedR(context.getWorld(), absCenter, false, log);
        if (rPodzol.catalystBonus() <= rBase.catalystBonus()) {
            context.throwPositionedException("Podzol deve dare bonus catalizzatore!", center);
        }
        context.setBlockState(center.add(1, 0, 0), Blocks.AIR.getDefaultState());

        // 3. Fungi (Red and Brown mushrooms)
        context.setBlockState(center.add(1, 0, 0), Blocks.RED_MUSHROOM.getDefaultState());
        MoldyBlockHelper.MoldRiskResult rMushroom = MoldyBlockHelper.calculateDetailedR(context.getWorld(), absCenter, false, log);
        if (rMushroom.catalystBonus() <= rBase.catalystBonus()) {
            context.throwPositionedException("Fungo rosso deve dare bonus catalizzatore!", center);
        }
        context.setBlockState(center.add(1, 0, 0), Blocks.AIR.getDefaultState());

        // 4. Cauldron (Water Cauldron)
        context.setBlockState(center.add(1, 0, 0), Blocks.WATER_CAULDRON.getDefaultState());
        MoldyBlockHelper.MoldRiskResult rCauldron = MoldyBlockHelper.calculateDetailedR(context.getWorld(), absCenter, false, log);
        if (rCauldron.localHumidityBonus() <= rBase.localHumidityBonus()) {
            context.throwPositionedException("Il calderone deve incrementare l'umidità locale!", center);
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testVentilationGapsIncreasesAerationAndReducesHumidity(TestContext context) {
        // Stanza 3x3x3 in pietra
        for (int x = 1; x <= 3; x++) {
            for (int y = 1; y <= 3; y++) {
                for (int z = 1; z <= 3; z++) {
                    context.setBlockState(new BlockPos(x, y, z), Blocks.STONE.getDefaultState());
                }
            }
        }
        BlockPos center = new BlockPos(2, 2, 2);
        context.setBlockState(center, Blocks.AIR.getDefaultState());

        BlockPos targetPos = new BlockPos(3, 2, 2);
        BlockState targetLog = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState();
        context.setBlockState(targetPos, targetLog);

        // Misura in stanza chiusa sigillata
        MoldyBlockHelper.MoldRiskResult rSealed = MoldyBlockHelper.calculateDetailedR(context.getWorld(), context.getAbsolutePos(targetPos), false, targetLog);

        // Aggiungi fessura/staccionata comunicante con l'esterno su parete opposta (1, 2, 2) con aria sopra (1, 3, 2)
        context.setBlockState(new BlockPos(1, 2, 2), Blocks.IRON_BARS.getDefaultState());
        context.setBlockState(new BlockPos(1, 3, 2), Blocks.AIR.getDefaultState());

        MoldyBlockHelper.MoldRiskResult rVented = MoldyBlockHelper.calculateDetailedR(context.getWorld(), context.getAbsolutePos(targetPos), false, targetLog);

        if (rVented.aeration() <= rSealed.aeration()) {
            context.throwPositionedException("Le sbarre di ferro verso l'esterno dovrebbero aumentare l'areazione!", targetPos);
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testExposedFacesAerationAveraging(TestContext context) {
        // Costruiamo una parete divisoria con blocco bersaglio a (2, 2, 2)
        // Lato Ovest (X=1): stanza sigillata chiusa (Aer = 0.0)
        // Lato Est (X=3): all'aperto / cielo (Aer = 1.0)
        // Sopra, sotto, nord, sud: pietra solida
        // Riempiamo la stanza ovest (X=0..2, Y=1..3, Z=1..3) di pietra
        for (int x = 0; x <= 2; x++) {
            for (int y = 1; y <= 3; y++) {
                for (int z = 1; z <= 3; z++) {
                    context.setBlockState(new BlockPos(x, y, z), Blocks.STONE.getDefaultState());
                }
            }
        }
        // Interno stanza ovest a (1, 2, 2): aria sigillata
        context.setBlockState(new BlockPos(1, 2, 2), Blocks.AIR.getDefaultState());

        // Blocco bersaglio al centro della parete a (2, 2, 2)
        BlockPos targetPos = new BlockPos(2, 2, 2);
        BlockState log = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState();
        context.setBlockState(targetPos, log);

        // Esterno a est (3, 2, 2) è aria aperta sotto il cielo
        context.setBlockState(new BlockPos(3, 2, 2), Blocks.AIR.getDefaultState());

        MoldyBlockHelper.MoldRiskResult result = MoldyBlockHelper.calculateDetailedR(context.getWorld(), context.getAbsolutePos(targetPos), false, log);

        // Il blocco ha 2 facce esposte: 1 a est (Aer=1.0) e 1 a ovest (Aer=0.0)
        if (result.exposedFaces() != 2) {
            context.throwPositionedException("Facce esposte attese: 2, trovate: " + result.exposedFaces(), targetPos);
        }
        // La media dell'areazione deve essere (1.0 + 0.0) / 2 = 0.50
        if (Math.abs(result.aeration() - 0.50) > 0.05) {
            context.throwPositionedException("L'areazione media attesa su 2 facce (aperta e chiusa) è ~0.50, trovata: " + result.aeration(), targetPos);
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testMiasmaMitigationByVentilationFencesAndBars(TestContext context) {
        // Stanza chiusa 5x4x5 in pietra posizionata a x=1..5, y=0..3, z=1..5
        for (int x = 1; x <= 5; x++) {
            for (int y = 0; y <= 3; y++) {
                for (int z = 1; z <= 5; z++) {
                    boolean isWall = (x == 1 || x == 5 || y == 0 || y == 3 || z == 1 || z == 5);
                    context.setBlockState(new BlockPos(x, y, z), isWall ? Blocks.STONE.getDefaultState() : Blocks.AIR.getDefaultState());
                }
            }
        }
        // Esterno a x=0 a cielo aperto
        for (int y = 0; y <= 3; y++) {
            for (int z = 1; z <= 5; z++) {
                context.setBlockState(new BlockPos(0, y, z), Blocks.AIR.getDefaultState());
            }
        }

        // Trave bersaglio pulita a (2, 1, 2)
        BlockPos targetPos = new BlockPos(2, 1, 2);
        BlockState cleanLog = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState();
        context.setBlockState(targetPos, cleanLog);

        // Blocco marcio a distanza (4, 1, 4) per generare miasma senza contatto diretto
        BlockState rottenLog = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState().with(MoldyLogBlock.STAGE, 3);
        context.setBlockState(new BlockPos(4, 1, 4), rottenLog);

        // 1. In stanza sigillata: miasma presente, aerazione 0
        MoldyBlockHelper.MoldRiskResult rSealed = MoldyBlockHelper.calculateDetailedR(context.getWorld(), context.getAbsolutePos(targetPos), false, cleanLog);
        if (rSealed.miasmaBonus() <= 0.0) {
            context.throwPositionedException("In stanza chiusa il miasmaBonus deve essere > 0!", targetPos);
        }
        if (rSealed.aeration() != 0.0) {
            context.throwPositionedException("In stanza chiusa l'aeration deve essere 0.0!", targetPos);
        }

        // 2. Aggiungiamo staccionata e grata comunicanti con l'esterno a (1, 1, 2) e (1, 1, 3)
        context.setBlockState(new BlockPos(1, 1, 2), Blocks.OAK_FENCE.getDefaultState());
        context.setBlockState(new BlockPos(1, 1, 3), Blocks.IRON_BARS.getDefaultState());

        MoldyBlockHelper.MoldRiskResult rVented = MoldyBlockHelper.calculateDetailedR(context.getWorld(), context.getAbsolutePos(targetPos), false, cleanLog);
        if (rVented.aeration() <= rSealed.aeration()) {
            context.throwPositionedException("L'aerazione deve aumentare grazie a staccionate e grate!", targetPos);
        }
        if (rVented.miasmaBonus() >= rSealed.miasmaBonus()) {
            context.throwPositionedException("La ventilazione deve ridurre la pressione delle spore da miasma!", targetPos);
        }
        if (rVented.R() >= rSealed.R()) {
            context.throwPositionedException("Il rischio R complessivo deve diminuire nella stanza ventilata! Sigillato: " + rSealed.R() + ", Ventilato: " + rVented.R(), targetPos);
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testDoorPurgesMiasmaAndReducesRisk(TestContext context) {
        // Stanza 5x4x5 in pietra posizionata a x=1..5, y=0..3, z=1..5
        for (int x = 1; x <= 5; x++) {
            for (int y = 0; y <= 3; y++) {
                for (int z = 1; z <= 5; z++) {
                    boolean isWall = (x == 1 || x == 5 || y == 0 || y == 3 || z == 1 || z == 5);
                    context.setBlockState(new BlockPos(x, y, z), isWall ? Blocks.STONE.getDefaultState() : Blocks.AIR.getDefaultState());
                }
            }
        }
        // Esterno a x=0 a cielo aperto
        for (int y = 0; y <= 3; y++) {
            for (int z = 1; z <= 5; z++) {
                context.setBlockState(new BlockPos(0, y, z), Blocks.AIR.getDefaultState());
            }
        }

        // Trave bersaglio pulita a (2, 1, 2)
        BlockPos targetPos = new BlockPos(2, 1, 2);
        BlockState cleanLog = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState();
        context.setBlockState(targetPos, cleanLog);

        // Blocco marcio a distanza a (4, 1, 4)
        BlockState rottenLog = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState().with(MoldyLogBlock.STAGE, 3);
        context.setBlockState(new BlockPos(4, 1, 4), rottenLog);

        // Porta a 2 blocchi su parete esterna a (1, 1, 3) e (1, 2, 3)
        BlockPos doorLower = new BlockPos(1, 1, 3);
        BlockPos doorUpper = new BlockPos(1, 2, 3);
        BlockState closedLower = Blocks.OAK_DOOR.getDefaultState()
                .with(net.minecraft.block.DoorBlock.HALF, net.minecraft.block.enums.DoubleBlockHalf.LOWER)
                .with(net.minecraft.block.DoorBlock.OPEN, false);
        BlockState closedUpper = Blocks.OAK_DOOR.getDefaultState()
                .with(net.minecraft.block.DoorBlock.HALF, net.minecraft.block.enums.DoubleBlockHalf.UPPER)
                .with(net.minecraft.block.DoorBlock.OPEN, false);
        context.setBlockState(doorLower, closedLower);
        context.setBlockState(doorUpper, closedUpper);

        // 1. A porta chiusa: miasma presente, aerazione 0, R elevato
        MoldyBlockHelper.MoldRiskResult rClosed = MoldyBlockHelper.calculateDetailedR(context.getWorld(), context.getAbsolutePos(targetPos), false, cleanLog);
        if (rClosed.miasmaBonus() <= 0.0) {
            context.throwPositionedException("A porta chiusa deve esserci miasmaBonus > 0!", targetPos);
        }

        // 2. A porta aperta verso l'esterno: miasma azzerato, aerazione massima (1.0), R crolla
        context.setBlockState(doorLower, closedLower.with(net.minecraft.block.DoorBlock.OPEN, true));
        context.setBlockState(doorUpper, closedUpper.with(net.minecraft.block.DoorBlock.OPEN, true));

        MoldyBlockHelper.MoldRiskResult rOpen = MoldyBlockHelper.calculateDetailedR(context.getWorld(), context.getAbsolutePos(targetPos), false, cleanLog);
        if (rOpen.miasmaBonus() != 0.0) {
            context.throwPositionedException("A porta aperta verso l'esterno il miasma deve essere azzerato (0.0), trovato: " + rOpen.miasmaBonus(), targetPos);
        }
        if (rOpen.aeration() < 0.99) {
            context.throwPositionedException("A porta aperta verso l'esterno l'aerazione deve salire a 1.0, trovato: " + rOpen.aeration(), targetPos);
        }
        if (rOpen.R() >= rClosed.R()) {
            context.throwPositionedException("Aprire la porta deve ridurre drasticamente il rischio R! Chiusa: " + rClosed.R() + ", Aperta: " + rOpen.R(), targetPos);
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testTrapdoorCeilingPurgesMiasmaAndReducesRisk(TestContext context) {
        // Stanza 3x3x3 in pietra
        for (int x = 1; x <= 3; x++) {
            for (int y = 1; y <= 3; y++) {
                for (int z = 1; z <= 3; z++) {
                    context.setBlockState(new BlockPos(x, y, z), Blocks.STONE.getDefaultState());
                }
            }
        }
        BlockPos center = new BlockPos(2, 2, 2);
        context.setBlockState(center, Blocks.AIR.getDefaultState());

        // Trave bersaglio a (3, 2, 2)
        BlockPos targetPos = new BlockPos(3, 2, 2);
        BlockState cleanLog = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState();
        context.setBlockState(targetPos, cleanLog);

        // Blocco marcio sulla parete opposta a (1, 2, 2)
        BlockState rottenLog = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState().with(MoldyLogBlock.STAGE, 2);
        context.setBlockState(new BlockPos(1, 2, 2), rottenLog);

        // Botola sul soffitto a (2, 3, 2)
        BlockPos trapdoorPos = new BlockPos(2, 3, 2);
        BlockState closedTrapdoor = Blocks.OAK_TRAPDOOR.getDefaultState()
                .with(net.minecraft.block.TrapdoorBlock.HALF, net.minecraft.block.enums.BlockHalf.BOTTOM)
                .with(net.minecraft.block.TrapdoorBlock.OPEN, false);
        context.setBlockState(trapdoorPos, closedTrapdoor);

        // 1. Botola chiusa: miasma presente
        MoldyBlockHelper.MoldRiskResult rClosed = MoldyBlockHelper.calculateDetailedR(context.getWorld(), context.getAbsolutePos(targetPos), false, cleanLog);
        if (rClosed.miasmaBonus() <= 0.0) {
            context.throwPositionedException("A botola chiusa il miasmaBonus deve essere > 0!", targetPos);
        }

        // 2. Botola aperta verso il cielo: miasma dissipato, aerazione massima
        context.setBlockState(trapdoorPos, closedTrapdoor.with(net.minecraft.block.TrapdoorBlock.OPEN, true));
        MoldyBlockHelper.MoldRiskResult rOpen = MoldyBlockHelper.calculateDetailedR(context.getWorld(), context.getAbsolutePos(targetPos), false, cleanLog);
        if (rOpen.miasmaBonus() != 0.0) {
            context.throwPositionedException("A botola aperta verso il cielo il miasma deve dissiparsi (0.0), trovato: " + rOpen.miasmaBonus(), targetPos);
        }
        if (rOpen.aeration() < 0.99) {
            context.throwPositionedException("A botola aperta verso il cielo l'aerazione deve essere 1.0, trovato: " + rOpen.aeration(), targetPos);
        }
        if (rOpen.R() >= rClosed.R()) {
            context.throwPositionedException("La botola aperta sul soffitto deve ridurre il rischio R!", targetPos);
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testWaxingRottenLogsEliminatesAirborneSporeRisk(TestContext context) {
        // Stanza chiusa 5x4x5 in pietra
        for (int x = 0; x <= 4; x++) {
            for (int y = 0; y <= 3; y++) {
                for (int z = 0; z <= 4; z++) {
                    boolean isWall = (x == 0 || x == 4 || y == 0 || y == 3 || z == 0 || z == 4);
                    context.setBlockState(new BlockPos(x, y, z), isWall ? Blocks.STONE.getDefaultState() : Blocks.AIR.getDefaultState());
                }
            }
        }

        // Trave bersaglio pulita a (1, 1, 1)
        BlockPos targetPos = new BlockPos(1, 1, 1);
        BlockState cleanLog = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState();
        context.setBlockState(targetPos, cleanLog);

        // Blocco marcio a distanza a (3, 1, 3) non cerato
        BlockPos distantRottenPos = new BlockPos(3, 1, 3);
        BlockState unwaxedRotten = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState()
                .with(MoldyLogBlock.STAGE, 3)
                .with(MoldyLogBlock.WAXED, false);
        context.setBlockState(distantRottenPos, unwaxedRotten);

        MoldyBlockHelper.MoldRiskResult rUnwaxed = MoldyBlockHelper.calculateDetailedR(context.getWorld(), context.getAbsolutePos(targetPos), false, cleanLog);
        if (rUnwaxed.miasmaBonus() <= 0.0) {
            context.throwPositionedException("Con legno marcio non cerato il miasmaBonus deve essere > 0!", targetPos);
        }

        // Ceriamo il blocco marcio (Waxed = true)
        BlockState waxedRotten = unwaxedRotten.with(MoldyLogBlock.WAXED, true);
        context.setBlockState(distantRottenPos, waxedRotten);

        MoldyBlockHelper.MoldRiskResult rWaxed = MoldyBlockHelper.calculateDetailedR(context.getWorld(), context.getAbsolutePos(targetPos), false, cleanLog);
        if (rWaxed.miasmaBonus() != 0.0) {
            context.throwPositionedException("Cerare il legno marcio deve azzerare la sua emissione di miasma (0.0), trovato: " + rWaxed.miasmaBonus(), targetPos);
        }
        if (rWaxed.R() >= rUnwaxed.R()) {
            context.throwPositionedException("Cerare i focolai marci deve abbassare il rischio R sui blocchi sani circostanti!", targetPos);
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testRoomVolumeDilutesMiasmaAndLowersRisk(TestContext context) {
        // Stanza Piccola (3x3x3, volume aria = 1 blocco a (2, 2, 2))
        for (int x = 1; x <= 3; x++) {
            for (int y = 1; y <= 3; y++) {
                for (int z = 1; z <= 3; z++) {
                    context.setBlockState(new BlockPos(x, y, z), Blocks.STONE.getDefaultState());
                }
            }
        }
        BlockPos centerSmall = new BlockPos(2, 2, 2);
        context.setBlockState(centerSmall, Blocks.AIR.getDefaultState());

        // Trave bersaglio a (3, 2, 2) e focolare marcio a (1, 2, 2)
        BlockPos targetPosSmall = new BlockPos(3, 2, 2);
        BlockState cleanLog = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState();
        context.setBlockState(targetPosSmall, cleanLog);

        BlockState moldyLog = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState().with(MoldyLogBlock.STAGE, 2);
        context.setBlockState(new BlockPos(1, 2, 2), moldyLog);

        MoldyBlockHelper.MoldRiskResult rSmall = MoldyBlockHelper.calculateDetailedR(context.getWorld(), context.getAbsolutePos(targetPosSmall), false, cleanLog);

        // Ora costruiamo una Stanza Grande 6x4x6 attorno al bersaglio
        for (int x = 0; x <= 6; x++) {
            for (int y = 0; y <= 3; y++) {
                for (int z = 0; z <= 6; z++) {
                    boolean isWall = (x == 0 || x == 6 || y == 0 || y == 3 || z == 0 || z == 6);
                    context.setBlockState(new BlockPos(x, y, z), isWall ? Blocks.STONE.getDefaultState() : Blocks.AIR.getDefaultState());
                }
            }
        }
        BlockPos targetPosLarge = new BlockPos(1, 1, 1);
        context.setBlockState(targetPosLarge, cleanLog);
        // Stesso singolo blocco marcio posto a distanza
        context.setBlockState(new BlockPos(5, 1, 5), moldyLog);

        MoldyBlockHelper.MoldRiskResult rLarge = MoldyBlockHelper.calculateDetailedR(context.getWorld(), context.getAbsolutePos(targetPosLarge), false, cleanLog);

        if (rLarge.miasmaBonus() >= rSmall.miasmaBonus()) {
            context.throwPositionedException("La diluizione in un volume maggiore deve ridurre la concentrazione di spore (miasmaBonus)! Piccolo: " + rSmall.miasmaBonus() + ", Grande: " + rLarge.miasmaBonus(), targetPosLarge);
        }
        if (rLarge.R() >= rSmall.R()) {
            context.throwPositionedException("Una stanza più ampia deve avere un rischio R inferiore a parità di muffa emittente!", targetPosLarge);
        }

        context.complete();
    }
}
