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

public class MoldyScenariosTableTests {

    private static final double INFECTION_THRESHOLD = 0.40;

    /**
     * Scenario 1: All'aperto di notte
     * Hraw = 0.30, Aer = 1.00, Heff = 0.00, Luv = 1.00 (Buio), Contatto = 0.00, Maria = 0.00 => R = 0.00 (SICURO)
     */
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testScenario1_OpenAirAtNight(TestContext context) {
        BlockPos pos = new BlockPos(2, 2, 2);
        BlockState log = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState();
        context.setBlockState(pos, log);

        // Blocco a quota di superficie (Y >= 64) all'aperto
        BlockPos absPos = context.getAbsolutePos(pos);
        MoldyBlockHelper.MoldRiskResult result = MoldyBlockHelper.calculateDetailedR(context.getWorld(), absPos, false, log);

        if (result.aeration() != 1.0) {
            context.throwPositionedException("Scenario 1: Aeration attesa: 1.0, trovata: " + result.aeration(), pos);
        }
        if (result.Heff() > 0.01) {
            context.throwPositionedException("Scenario 1: Heff attesa: 0.0 (asciugata dal vento), trovata: " + result.Heff(), pos);
        }
        if (result.catalystBonus() != 0.0) {
            context.throwPositionedException("Scenario 1: Catalizzatori attesi: 0.0, trovati: " + result.catalystBonus(), pos);
        }
        if (result.miasmaBonus() != 0.0) {
            context.throwPositionedException("Scenario 1: Miasma atteso: 0.0, trovato: " + result.miasmaBonus(), pos);
        }
        if (result.R() >= INFECTION_THRESHOLD) {
            context.throwPositionedException("Scenario 1: Rischio atteso < 0.40 (SICURO), trovato R=" + result.R(), pos);
        }

        context.complete();
    }

    /**
     * Scenario 2: Cantina buia sigillata pulita
     * Hraw = 0.60, Aer = 0.00, Heff = 0.60, Luv = 1.00, Contatto = 0.00, Maria = 0.00 => R = 0.60 (INFETTA)
     */
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testScenario2_SealedDarkBasementClean(TestContext context) {
        // Stanza sigillata 3x3x3 in pietra
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
        BlockState log = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState();
        context.setBlockState(targetPos, log);

        // Simuliamo posizione a Y=34 (sottosuolo con malus profondità ~0.30 => Hraw = 0.60)
        BlockPos deepPos = new BlockPos(context.getAbsolutePos(targetPos).getX(), 34, context.getAbsolutePos(targetPos).getZ());
        MoldyBlockHelper.MoldRiskResult result = MoldyBlockHelper.calculateDetailedR(context.getWorld(), deepPos, false, log);

        if (result.aeration() != 0.0) {
            context.throwPositionedException("Scenario 2: In cantina sigillata Aeration attesa: 0.0, trovata: " + result.aeration(), targetPos);
        }
        if (result.Heff() < 0.50) {
            context.throwPositionedException("Scenario 2: Heff attesa >= 0.50, trovata: " + result.Heff(), targetPos);
        }
        if (result.catalystBonus() != 0.0) {
            context.throwPositionedException("Scenario 2: Catalizzatori attesi: 0.0, trovati: " + result.catalystBonus(), targetPos);
        }
        if (result.R() < INFECTION_THRESHOLD) {
            context.throwPositionedException("Scenario 2: Rischio atteso >= 0.40 (INFETTA), trovato R=" + result.R(), targetPos);
        }

        context.complete();
    }

    /**
     * Scenario 3: Cantina con 2 grate esterne
     * Hraw = 0.60, Aer = 1.00, Heff = 0.10, Luv = 1.00, Contatto = 0.00, Maria = 0.00 => R = 0.10 (SICURO)
     */
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testScenario3_BasementWithTwoVents(TestContext context) {
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

        // 2 Grate esterne (Iron Bars) con cielo comunicante sopra
        context.setBlockState(new BlockPos(1, 2, 2), Blocks.IRON_BARS.getDefaultState());
        context.setBlockState(new BlockPos(1, 3, 2), Blocks.AIR.getDefaultState());
        context.setBlockState(new BlockPos(2, 2, 1), Blocks.IRON_BARS.getDefaultState());
        context.setBlockState(new BlockPos(2, 3, 1), Blocks.AIR.getDefaultState());

        BlockPos targetPos = new BlockPos(3, 2, 2);
        BlockState log = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState();
        context.setBlockState(targetPos, log);

        // Simuliamo quota Y=34 (Hraw ~ 0.60)
        BlockPos deepPos = new BlockPos(context.getAbsolutePos(targetPos).getX(), 34, context.getAbsolutePos(targetPos).getZ());
        MoldyBlockHelper.MoldRiskResult result = MoldyBlockHelper.calculateDetailedR(context.getWorld(), deepPos, false, log);

        if (result.aeration() < 0.99) {
            context.throwPositionedException("Scenario 3: Con 2 grate Aeration attesa: 1.0, trovata: " + result.aeration(), targetPos);
        }
        if (result.Heff() > 0.20) {
            context.throwPositionedException("Scenario 3: Heff attesa ridotta a ~0.10, trovata: " + result.Heff(), targetPos);
        }
        if (result.R() >= INFECTION_THRESHOLD) {
            context.throwPositionedException("Scenario 3: Rischio atteso < 0.40 (SICURO), trovato R=" + result.R(), targetPos);
        }

        context.complete();
    }

    /**
     * Scenario 4: Trave a contatto con legno marcio all'aperto
     * Hraw = 0.30, Aer = 1.00, Heff = 0.00, Luv = 0.00 (Luce sole), Contatto = +0.12, Maria = 0.00 => R = 0.12 (RESISTE)
     */
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testScenario4_RottenContactInSunlight(TestContext context) {
        BlockPos targetPos = new BlockPos(2, 2, 2);
        BlockState targetLog = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState();
        context.setBlockState(targetPos, targetLog);

        // Blocco marcio a contatto diretto (adiacente a distanza 1)
        BlockPos adjacentRottenPos = targetPos.add(1, 0, 0);
        BlockState rottenLog = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState().with(MoldyLogBlock.STAGE, 3);
        context.setBlockState(adjacentRottenPos, rottenLog);

        BlockPos absPos = context.getAbsolutePos(targetPos);
        MoldyBlockHelper.MoldRiskResult result = MoldyBlockHelper.calculateDetailedR(context.getWorld(), absPos, false, targetLog);

        if (result.aeration() != 1.0) {
            context.throwPositionedException("Scenario 4: All'aperto Aeration attesa: 1.0, trovata: " + result.aeration(), targetPos);
        }
        if (result.catalystBonus() < 0.10) {
            context.throwPositionedException("Scenario 4: Contatto diretto marcio atteso ~+0.12, trovato: " + result.catalystBonus(), targetPos);
        }
        if (result.miasmaBonus() != 0.0) {
            context.throwPositionedException("Scenario 4: Miasma all'aperto deve essere 0.0, trovato: " + result.miasmaBonus(), targetPos);
        }
        if (result.R() >= INFECTION_THRESHOLD) {
            context.throwPositionedException("Scenario 4: Rischio atteso < 0.40 (RESISTE grazie a sole e aria), trovato R=" + result.R(), targetPos);
        }

        context.complete();
    }

    /**
     * Scenario 5: Trave a contatto con legno marcio in cantina areata
     * Hraw = 0.60, Aer = 1.00, Heff = 0.10, Luv = 1.00, Contatto = +0.12, Maria = 0.00 => R = 0.22 (RESISTE)
     */
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testScenario5_RottenContactInVentedBasement(TestContext context) {
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

        // 2 Grate esterne
        context.setBlockState(new BlockPos(1, 2, 2), Blocks.IRON_BARS.getDefaultState());
        context.setBlockState(new BlockPos(1, 3, 2), Blocks.AIR.getDefaultState());
        context.setBlockState(new BlockPos(2, 2, 1), Blocks.IRON_BARS.getDefaultState());
        context.setBlockState(new BlockPos(2, 3, 1), Blocks.AIR.getDefaultState());

        // Trave bersaglio
        BlockPos targetPos = new BlockPos(3, 2, 2);
        BlockState targetLog = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState();
        context.setBlockState(targetPos, targetLog);

        // Blocco marcio a contatto diretto (adiacente)
        BlockPos adjacentRottenPos = targetPos.add(0, 1, 0); // sopra il target
        BlockState rottenLog = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState().with(MoldyLogBlock.STAGE, 3);
        context.setBlockState(adjacentRottenPos, rottenLog);

        // Quota Y=34 (Hraw ~ 0.60)
        BlockPos deepPos = new BlockPos(context.getAbsolutePos(targetPos).getX(), 34, context.getAbsolutePos(targetPos).getZ());
        MoldyBlockHelper.MoldRiskResult result = MoldyBlockHelper.calculateDetailedR(context.getWorld(), deepPos, false, targetLog);

        if (result.aeration() < 0.99) {
            context.throwPositionedException("Scenario 5: Aeration attesa: 1.0, trovata: " + result.aeration(), targetPos);
        }
        if (result.catalystBonus() < 0.10) {
            context.throwPositionedException("Scenario 5: Contatto diretto marcio atteso ~+0.12, trovato: " + result.catalystBonus(), targetPos);
        }
        if (result.R() >= INFECTION_THRESHOLD) {
            context.throwPositionedException("Scenario 5: Rischio atteso R < 0.40 (~0.22 RESISTE), trovato R=" + result.R(), targetPos);
        }

        context.complete();
    }

    /**
     * Scenario 6: Cantina chiusa mefitica (Trave sul soffitto distante)
     * Hraw = 0.60, Aer = 0.00, Heff = 0.60, Luv = 1.00, Contatto = 0.00, Maria > 0.20 => R > 0.80 (INFETTA)
     */
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testScenario6_MiasmicBasementDistantCeilingBeam(TestContext context) {
        // Stanza chiusa 5x4x5 in pietra
        for (int x = 0; x <= 4; x++) {
            for (int y = 0; y <= 3; y++) {
                for (int z = 0; z <= 4; z++) {
                    boolean isWall = (x == 0 || x == 4 || y == 0 || y == 3 || z == 0 || z == 4);
                    context.setBlockState(new BlockPos(x, y, z), isWall ? Blocks.STONE.getDefaultState() : Blocks.AIR.getDefaultState());
                }
            }
        }

        // Trave sana sul soffitto (1, 2, 1)
        BlockPos ceilingBeamPos = new BlockPos(1, 2, 1);
        BlockState cleanLog = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState();
        context.setBlockState(ceilingBeamPos, cleanLog);

        // Tre blocchi marci sul pavimento opposto (3, 1, 3), (3, 1, 2), (2, 1, 3) - distanza > 2 blocchi, nessun contatto diretto
        BlockState rottenLog = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState().with(MoldyLogBlock.STAGE, 3);
        context.setBlockState(new BlockPos(3, 1, 3), rottenLog);
        context.setBlockState(new BlockPos(3, 1, 2), rottenLog);
        context.setBlockState(new BlockPos(2, 1, 3), rottenLog);

        // Quota Y=34 (Hraw ~ 0.60)
        BlockPos deepPos = new BlockPos(context.getAbsolutePos(ceilingBeamPos).getX(), 34, context.getAbsolutePos(ceilingBeamPos).getZ());
        MoldyBlockHelper.MoldRiskResult result = MoldyBlockHelper.calculateDetailedR(context.getWorld(), deepPos, false, cleanLog);

        if (result.aeration() != 0.0) {
            context.throwPositionedException("Scenario 6: In stanza sigillata Aeration attesa: 0.0, trovata: " + result.aeration(), ceilingBeamPos);
        }
        if (result.catalystBonus() != 0.0) {
            context.throwPositionedException("Scenario 6: Non essendoci contatto diretto, catalystBonus deve essere 0.0, trovato: " + result.catalystBonus(), ceilingBeamPos);
        }
        if (result.miasmaBonus() <= 0.15) {
            context.throwPositionedException("Scenario 6: Pressione aerea del miasma attesa > 0.15, trovata: " + result.miasmaBonus(), ceilingBeamPos);
        }
        if (result.R() < INFECTION_THRESHOLD) {
            context.throwPositionedException("Scenario 6: Rischio atteso R > 0.40 (INFETTA per contagio aereo da miasma), trovato R=" + result.R(), ceilingBeamPos);
        }

        context.complete();
    }
}
