package moldmod.test;

import moldmod.block.ModBlocks;
import moldmod.block.MoldyLogBlock;
import moldmod.event.ToxicAirEvent;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class ToxicAirTests {

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testCanAirPass(TestContext context) {
        BlockPos pos1 = new BlockPos(1, 2, 1);
        BlockPos pos2 = pos1.north();

        // 1. Aria e Aria (passa)
        context.setBlockState(pos1, Blocks.AIR);
        context.setBlockState(pos2, Blocks.AIR);
        if (!ToxicAirEvent.canAirPass(context.getWorld(), context.getAbsolutePos(pos1), Blocks.AIR.getDefaultState(), context.getAbsolutePos(pos2), Blocks.AIR.getDefaultState(), Direction.NORTH)) {
            context.throwPositionedException("L'aria dovrebbe passare tra due blocchi vuoti", pos1);
        }

        // 2. Muro di pietra e Aria (non passa)
        context.setBlockState(pos1, Blocks.STONE);
        if (ToxicAirEvent.canAirPass(context.getWorld(), context.getAbsolutePos(pos1), Blocks.STONE.getDefaultState(), context.getAbsolutePos(pos2), Blocks.AIR.getDefaultState(), Direction.NORTH)) {
            context.throwPositionedException("L'aria NON dovrebbe passare da una pietra", pos1);
        }

        // 3. Staccionata (passa)
        context.setBlockState(pos1, Blocks.OAK_FENCE);
        if (!ToxicAirEvent.canAirPass(context.getWorld(), context.getAbsolutePos(pos1), Blocks.OAK_FENCE.getDefaultState(), context.getAbsolutePos(pos2), Blocks.AIR.getDefaultState(), Direction.NORTH)) {
            context.throwPositionedException("L'aria dovrebbe passare attraverso una staccionata (non è full square)", pos1);
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testHasMoldNearby(TestContext context) {
        BlockPos center = new BlockPos(2, 2, 2);
        
        // 1. Nessuna muffa (FALSO)
        if (ToxicAirEvent.hasMoldNearby(context.getWorld(), context.getAbsolutePos(center), 4)) {
            context.throwPositionedException("Non dovrebbe esserci muffa vicina!", center);
        }

        // 2. Aggiungo blocco di legno sano (FALSO)
        BlockState log = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState();
        context.setBlockState(center.add(2, 0, 0), log);
        if (ToxicAirEvent.hasMoldNearby(context.getWorld(), context.getAbsolutePos(center), 4)) {
            context.throwPositionedException("Il legno sano NON è muffa!", center);
        }

        // 3. Aggiungo blocco di legno ammuffito (VERO)
        BlockState moldyLog = log.with(MoldyLogBlock.STAGE, 2);
        context.setBlockState(center.add(-2, 0, 0), moldyLog);
        if (!ToxicAirEvent.hasMoldNearby(context.getWorld(), context.getAbsolutePos(center), 4)) {
            context.throwPositionedException("Dovrebbe rilevare il legno ammuffito!", center);
        }

        // 4. Se il legno è cerato, non conta (FALSO) - Rimuovo quello precedente e ne metto uno cerato
        context.setBlockState(center.add(-2, 0, 0), Blocks.AIR); // rimuovo il vecchio
        BlockState waxedMoldyLog = moldyLog.with(MoldyLogBlock.WAXED, true);
        context.setBlockState(center.add(0, 2, 0), waxedMoldyLog);
        
        if (ToxicAirEvent.hasMoldNearby(context.getWorld(), context.getAbsolutePos(center), 4)) {
            context.throwPositionedException("Il legno cerato non dovrebbe diffondere miasma!", center);
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testManhattanRadiusLimit(TestContext context) {
        BlockPos center = new BlockPos(2, 2, 2);
        
        // Creiamo un tunnel 1x1x15 sigillato su tutti i lati tranne lungo X
        for (int dx = -1; dx <= 15; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    BlockPos p = center.add(dx, dy, dz);
                    if (dy == 0 && dz == 0 && dx >= 0 && dx < 15) {
                        context.setBlockState(p, Blocks.AIR);
                    } else {
                        context.setBlockState(p, Blocks.STONE);
                    }
                }
            }
        }
        
        ToxicAirEvent.MiasmaResult result = ToxicAirEvent.calculateMiasma(context.getWorld(), context.getAbsolutePos(center));
        
        // Il raggio di manhattan è 8. Quindi partendo da 2,2,2 può esplorare in +X al massimo per 8 blocchi.
        // Volume esplorato non dovrebbe MAI superare 9 (centro + 8 blocchi avanti)
        if (result.volume > 9) {
            context.throwPositionedException("Il raggio di manhattan non sta limitando la ricerca! Volume esplorato: " + result.volume, center);
        }
        
        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testEnclosedRoomToxicity(TestContext context) {
        // Costruiamo una scatola sigillata 3x3x3 di pietra attorno al centro (2,2,2)
        BlockPos center = new BlockPos(2, 2, 2);
        for (int x = 1; x <= 3; x++) {
            for (int y = 1; y <= 3; y++) {
                for (int z = 1; z <= 3; z++) {
                    context.setBlockState(new BlockPos(x, y, z), Blocks.STONE);
                }
            }
        }
        // Centro vuoto (aria)
        context.setBlockState(center, Blocks.AIR);

        // Mettiamo un blocco di legno infetto Stadio 2 su una parete interna (1, 2, 2)
        BlockState moldyLog = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState()
                .with(MoldyLogBlock.STAGE, 2)
                .with(MoldyLogBlock.WAXED, false);
        context.setBlockState(new BlockPos(1, 2, 2), moldyLog);

        ToxicAirEvent.MiasmaResult result = ToxicAirEvent.calculateMiasma(context.getWorld(), context.getAbsolutePos(center));

        if (result.openAir) {
            context.throwPositionedException("La stanza sigillata non dovrebbe essere considerata openAir!", center);
        }
        if (result.volume != 1) {
            context.throwPositionedException("Il volume d'aria della stanza dovrebbe essere esattamente 1 blocco, trovato: " + result.volume, center);
        }
        if (result.toxicScore <= 0.0) {
            context.throwPositionedException("Il punteggio di tossicità dovrebbe essere positivo per il legno allo Stadio 2!", center);
        }
        if (result.ventilationScore != 0.0) {
            context.throwPositionedException("In una stanza sigillata il punteggio di ventilazione deve essere 0!", center);
        }
        if (result.netMiasma != result.toxicScore) {
            context.throwPositionedException("In assenza di ventilazione, netMiasma deve coincidere con toxicScore!", center);
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testOpenAirDissipation(TestContext context) {
        BlockPos center = new BlockPos(2, 2, 2);
        BlockPos absCenter = context.getAbsolutePos(center);
        int topY = context.getWorld().getTopY(net.minecraft.world.Heightmap.Type.MOTION_BLOCKING, absCenter.getX(), absCenter.getZ());
        BlockPos skyPos = new BlockPos(absCenter.getX(), Math.max(topY, absCenter.getY()), absCenter.getZ());

        ToxicAirEvent.MiasmaResult result = ToxicAirEvent.calculateMiasma(context.getWorld(), skyPos);

        if (!result.openAir) {
            context.throwPositionedException("Un blocco alla coordinata topY del cielo deve avere openAir = true!", center);
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testWaxedWoodMiasmaImmunity(TestContext context) {
        // Stanza sigillata con blocchi infetti tutti cerati
        BlockPos center = new BlockPos(2, 2, 2);
        for (int x = 1; x <= 3; x++) {
            for (int y = 1; y <= 3; y++) {
                for (int z = 1; z <= 3; z++) {
                    context.setBlockState(new BlockPos(x, y, z), Blocks.STONE);
                }
            }
        }
        context.setBlockState(center, Blocks.AIR);

        // Blocco Marcio (Stadio 3) ma Cerato
        BlockState waxedRotten = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState()
                .with(MoldyLogBlock.STAGE, 3)
                .with(MoldyLogBlock.WAXED, true);
        context.setBlockState(new BlockPos(1, 2, 2), waxedRotten);

        ToxicAirEvent.MiasmaResult result = ToxicAirEvent.calculateMiasma(context.getWorld(), context.getAbsolutePos(center));

        if (result.toxicScore != 0.0) {
            context.throwPositionedException("Il legno cerato non deve produrre alcuno score di tossicità, trovato: " + result.toxicScore, center);
        }
        if (result.netMiasma != 0.0) {
            context.throwPositionedException("Il miasma netto per legno cerato deve essere 0, trovato: " + result.netMiasma, center);
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testVentilationWithGaps(TestContext context) {
        // Stanza con soffitto parziale per permettere la ventilazione esterna
        BlockPos center = new BlockPos(2, 2, 2);
        for (int x = 1; x <= 3; x++) {
            for (int y = 1; y <= 3; y++) {
                for (int z = 1; z <= 3; z++) {
                    context.setBlockState(new BlockPos(x, y, z), Blocks.STONE);
                }
            }
        }
        context.setBlockState(center, Blocks.AIR);

        // Parete con staccionata verso l'esterno (e aria sopra di essa per comunicare col cielo)
        context.setBlockState(new BlockPos(1, 2, 2), Blocks.OAK_FENCE);
        context.setBlockState(new BlockPos(1, 3, 2), Blocks.AIR);

        // Blocco infetto su altra parete
        BlockState moldy = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState()
                .with(MoldyLogBlock.STAGE, 2)
                .with(MoldyLogBlock.WAXED, false);
        context.setBlockState(new BlockPos(3, 2, 2), moldy);

        ToxicAirEvent.MiasmaResult result = ToxicAirEvent.calculateMiasma(context.getWorld(), context.getAbsolutePos(center));

        if (result.toxicScore <= 0.0) {
            context.throwPositionedException("La tossicità da muffa deve essere presente!", center);
        }
        // Il calcolo deve restituire un netMiasma inferiore o uguale al toxicScore grazie alla ventilazione
        if (result.netMiasma > result.toxicScore) {
            context.throwPositionedException("Il miasma netto non può superare il toxicScore lordo!", center);
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testAirToxicityLevelsCleanWarningHungerPoison(TestContext context) {
        // 1. Clean level (open air or 0 miasma)
        ToxicAirEvent.MiasmaResult clean = new ToxicAirEvent.MiasmaResult(0.0, 0.0, 0.0, true, 10, java.util.Set.of());
        if (clean.level != ToxicAirEvent.AirToxicityLevel.CLEAN) {
            context.throwPositionedException("Livello atteso: CLEAN, trovato: " + clean.level, new BlockPos(0, 0, 0));
        }

        // 2. Warning level (netMiasma = 4.0, volume = 50 -> density = 0.08)
        ToxicAirEvent.MiasmaResult warning = new ToxicAirEvent.MiasmaResult(4.0, 0.0, 4.0, false, 50, java.util.Set.of());
        if (warning.level != ToxicAirEvent.AirToxicityLevel.WARNING) {
            context.throwPositionedException("Livello atteso: WARNING, trovato: " + warning.level, new BlockPos(0, 0, 0));
        }

        // 3. Moderate hunger level (netMiasma = 9.0, volume = 50)
        ToxicAirEvent.MiasmaResult hunger = new ToxicAirEvent.MiasmaResult(9.0, 0.0, 9.0, false, 50, java.util.Set.of());
        if (hunger.level != ToxicAirEvent.AirToxicityLevel.MODERATE_HUNGER) {
            context.throwPositionedException("Livello atteso: MODERATE_HUNGER, trovato: " + hunger.level, new BlockPos(0, 0, 0));
        }

        // 4. Lethal poison level (netMiasma = 20.0, volume = 50)
        ToxicAirEvent.MiasmaResult poison = new ToxicAirEvent.MiasmaResult(20.0, 0.0, 20.0, false, 50, java.util.Set.of());
        if (poison.level != ToxicAirEvent.AirToxicityLevel.LETHAL_POISON) {
            context.throwPositionedException("Livello atteso: LETHAL_POISON, trovato: " + poison.level, new BlockPos(0, 0, 0));
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testExposureIndexContinuousCalculation(TestContext context) {
        // High density small room (vol = 8, net = 2.25)
        ToxicAirEvent.MiasmaResult small = new ToxicAirEvent.MiasmaResult(2.25, 0.0, 2.25, false, 8, java.util.Set.of());
        
        // Lower density large room with massive total sources (vol = 100, net = 30.0)
        ToxicAirEvent.MiasmaResult large = new ToxicAirEvent.MiasmaResult(30.0, 0.0, 30.0, false, 100, java.util.Set.of());

        if (small.exposureIndex <= 0.0 || large.exposureIndex <= 0.0) {
            context.throwPositionedException("Exposure index deve essere positivo per stanze con miasma!", new BlockPos(0, 0, 0));
        }

        // Clean room must have 0.0 exposure index
        ToxicAirEvent.MiasmaResult clean = new ToxicAirEvent.MiasmaResult(0.0, 0.0, 0.0, false, 10, java.util.Set.of());
        if (clean.exposureIndex != 0.0) {
            context.throwPositionedException("Stanza pulita deve avere exposureIndex = 0.0, trovato: " + clean.exposureIndex, new BlockPos(0, 0, 0));
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testSlabDirectionalAirPassage(TestContext context) {
        // Stanza 3x3x3 in pietra
        BlockPos center = new BlockPos(2, 2, 2);
        for (int x = 1; x <= 3; x++) {
            for (int y = 1; y <= 3; y++) {
                for (int z = 1; z <= 3; z++) {
                    context.setBlockState(new BlockPos(x, y, z), Blocks.STONE.getDefaultState());
                }
            }
        }
        context.setBlockState(center, Blocks.AIR.getDefaultState());

        // Parete con bottom slab che affaccia l'apertura laterale verso il centro (1, 2, 2) e cielo sopra (1, 3, 2)
        BlockPos slabPos = new BlockPos(1, 2, 2);
        BlockState bottomSlab = Blocks.OAK_SLAB.getDefaultState().with(net.minecraft.block.SlabBlock.TYPE, net.minecraft.block.enums.SlabType.BOTTOM);
        context.setBlockState(slabPos, bottomSlab);
        context.setBlockState(new BlockPos(1, 3, 2), Blocks.AIR.getDefaultState());

        // Aggiungi focolare marcio per avere miasma
        BlockState moldy = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState().with(MoldyLogBlock.STAGE, 2);
        context.setBlockState(new BlockPos(3, 2, 2), moldy);

        ToxicAirEvent.MiasmaResult result = ToxicAirEvent.calculateMiasma(context.getWorld(), context.getAbsolutePos(center));
        if (result.ventilationScore < 3.0) {
            context.throwPositionedException("La mezza lastra con apertura verso la stanza deve conferire il bonus di ventilazione +3.0, trovato: " + result.ventilationScore, center);
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testStairsDirectionalAirPassage(TestContext context) {
        // Stanza 3x3x3 in pietra
        BlockPos center = new BlockPos(2, 2, 2);
        for (int x = 1; x <= 3; x++) {
            for (int y = 1; y <= 3; y++) {
                for (int z = 1; z <= 3; z++) {
                    context.setBlockState(new BlockPos(x, y, z), Blocks.STONE.getDefaultState());
                }
            }
        }
        context.setBlockState(center, Blocks.AIR.getDefaultState());

        // Scala a NORD (1, 2, 2) con il retro solido rivolto verso l'interno della stanza (a SUD) -> DEVE BLOCCARE ALLA RADICE (ventilationScore = 0)
        BlockPos stairPos = new BlockPos(1, 2, 2);
        BlockState stairsBackFacingRoom = Blocks.OAK_STAIRS.getDefaultState()
                .with(net.minecraft.block.StairsBlock.FACING, Direction.SOUTH) // retro a SUD (verso il centro stanza)
                .with(net.minecraft.block.StairsBlock.HALF, net.minecraft.block.enums.BlockHalf.BOTTOM);
        context.setBlockState(stairPos, stairsBackFacingRoom);
        context.setBlockState(new BlockPos(1, 3, 2), Blocks.AIR.getDefaultState());

        ToxicAirEvent.MiasmaResult blockedResult = ToxicAirEvent.calculateMiasma(context.getWorld(), context.getAbsolutePos(center));
        if (blockedResult.ventilationScore != 0.0) {
            context.throwPositionedException("Una scala con il retro solido rivolto verso la stanza non deve dare ventilazione!", center);
        }

        // Ora giriamo la scala con il gradino aperto verso la stanza (FACING NORTH, retro all'esterno)
        BlockState stairsOpenToRoom = Blocks.OAK_STAIRS.getDefaultState()
                .with(net.minecraft.block.StairsBlock.FACING, Direction.NORTH)
                .with(net.minecraft.block.StairsBlock.HALF, net.minecraft.block.enums.BlockHalf.BOTTOM);
        context.setBlockState(stairPos, stairsOpenToRoom);

        ToxicAirEvent.MiasmaResult openResult = ToxicAirEvent.calculateMiasma(context.getWorld(), context.getAbsolutePos(center));
        if (openResult.ventilationScore < 3.0) {
            context.throwPositionedException("Una scala con gradino aperto verso la stanza deve conferire il bonus di ventilazione +3.0!", center);
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testDoorOpenVsClosedAirFlow(TestContext context) {
        // Due stanzette adiacenti 3x3x3 in pietra separate da una parete a X=2
        for (int y = 1; y <= 3; y++) {
            for (int z = 1; z <= 3; z++) {
                context.setBlockState(new BlockPos(0, y, z), Blocks.STONE.getDefaultState());
                context.setBlockState(new BlockPos(2, y, z), Blocks.STONE.getDefaultState());
                context.setBlockState(new BlockPos(4, y, z), Blocks.STONE.getDefaultState());
            }
        }
        for (int x = 0; x <= 4; x++) {
            for (int z = 1; z <= 3; z++) {
                context.setBlockState(new BlockPos(x, 1, z), Blocks.STONE.getDefaultState());
                context.setBlockState(new BlockPos(x, 3, z), Blocks.STONE.getDefaultState());
            }
        }
        // Aria nelle due stanze
        context.setBlockState(new BlockPos(1, 2, 2), Blocks.AIR.getDefaultState());
        context.setBlockState(new BlockPos(3, 2, 2), Blocks.AIR.getDefaultState());

        // Porta sulla parete divisoria a (2, 2, 2)
        BlockPos doorPos = new BlockPos(2, 2, 2);
        BlockState closedDoor = Blocks.OAK_DOOR.getDefaultState()
                .with(net.minecraft.block.DoorBlock.HALF, net.minecraft.block.enums.DoubleBlockHalf.LOWER)
                .with(net.minecraft.block.DoorBlock.OPEN, false);
        context.setBlockState(doorPos, closedDoor);

        // 1. A porta CHIUSA: la scansione dalla stanza 1 (1, 2, 2) non deve entrare nella stanza 2 (volume = 1)
        ToxicAirEvent.MiasmaResult resultClosed = ToxicAirEvent.calculateMiasma(context.getWorld(), context.getAbsolutePos(new BlockPos(1, 2, 2)));
        if (resultClosed.volume != 1) {
            context.throwPositionedException("A porta chiusa il volume d'aria deve essere isolato a 1 blocco, trovato: " + resultClosed.volume, new BlockPos(1, 2, 2));
        }

        // 2. A porta APERTA: la scansione fluisce attraverso il vano porta aperto unendo i volumi (volume >= 2)
        BlockState openDoor = closedDoor.with(net.minecraft.block.DoorBlock.OPEN, true);
        context.setBlockState(doorPos, openDoor);

        ToxicAirEvent.MiasmaResult resultOpen = ToxicAirEvent.calculateMiasma(context.getWorld(), context.getAbsolutePos(new BlockPos(1, 2, 2)));
        if (resultOpen.volume <= 1) {
            context.throwPositionedException("A porta aperta l'aria deve fluire attraverso il vano aperto (volume > 1), trovato: " + resultOpen.volume, new BlockPos(1, 2, 2));
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testTrapdoorOpenVsClosedAirFlow(TestContext context) {
        // Stanza 3x3x3 in pietra
        BlockPos center = new BlockPos(2, 2, 2);
        for (int x = 1; x <= 3; x++) {
            for (int y = 1; y <= 3; y++) {
                for (int z = 1; z <= 3; z++) {
                    context.setBlockState(new BlockPos(x, y, z), Blocks.STONE.getDefaultState());
                }
            }
        }
        context.setBlockState(center, Blocks.AIR.getDefaultState());

        // Botola sul soffitto a (2, 3, 2) che comunica direttamente con l'aria aperta sopra
        BlockPos trapdoorPos = new BlockPos(2, 3, 2);
        BlockState closedTrapdoor = Blocks.OAK_TRAPDOOR.getDefaultState()
                .with(net.minecraft.block.TrapdoorBlock.HALF, net.minecraft.block.enums.BlockHalf.BOTTOM)
                .with(net.minecraft.block.TrapdoorBlock.OPEN, false);
        context.setBlockState(trapdoorPos, closedTrapdoor);

        // 1. Botola CHIUSA: stanza ermetica (openAir = false, volume = 1)
        ToxicAirEvent.MiasmaResult resultClosed = ToxicAirEvent.calculateMiasma(context.getWorld(), context.getAbsolutePos(center));
        if (resultClosed.openAir) {
            context.throwPositionedException("A botola chiusa la stanza deve rimanere sigillata (openAir = false)!", center);
        }

        // 2. Botola APERTA: l'aria sale verso il cielo (openAir = true)
        BlockState openTrapdoor = closedTrapdoor.with(net.minecraft.block.TrapdoorBlock.OPEN, true);
        context.setBlockState(trapdoorPos, openTrapdoor);

        ToxicAirEvent.MiasmaResult resultOpen = ToxicAirEvent.calculateMiasma(context.getWorld(), context.getAbsolutePos(center));
        if (!resultOpen.openAir) {
            context.throwPositionedException("A botola aperta verso il cielo l'aria deve poter salire (openAir = true)!", center);
        }

        context.complete();
    }
}
