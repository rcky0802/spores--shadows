package moldmod.test;

import moldmod.block.MoldyLogBlock;
import moldmod.block.ModBlocks;
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
    public void testCleanAirThresholds(TestContext context) {
        // Nessun blocco marcio -> CLEAN
        ToxicAirEvent.MiasmaResult clean = new ToxicAirEvent.MiasmaResult(0.0, 0.0, false, 10,
                java.util.Collections.emptySet());
        if (clean.level != ToxicAirEvent.AirToxicityLevel.CLEAN) {
            context.throwPositionedException("Miasma a 0 deve essere CLEAN, trovato: " + clean.level,
                    new BlockPos(0, 1, 0));
        }

        // Open Air -> sempre CLEAN
        ToxicAirEvent.MiasmaResult openAir = new ToxicAirEvent.MiasmaResult(50.0, 0.0, true, 10,
                java.util.Collections.emptySet());
        if (openAir.level != ToxicAirEvent.AirToxicityLevel.CLEAN) {
            context.throwPositionedException("Open Air deve essere sempre CLEAN, trovato: " + openAir.level,
                    new BlockPos(0, 1, 0));
        }
        if (openAir.netMiasma != 0.0) {
            context.throwPositionedException("Open Air deve avere netMiasma = 0.0, trovato: " + openAir.netMiasma,
                    new BlockPos(0, 1, 0));
        }
        if (openAir.ventilationType != ToxicAirEvent.RoomVentilationType.CLEAN_OPEN_AIR) {
            context.throwPositionedException(
                    "Open Air deve avere ventilationType = CLEAN_OPEN_AIR, trovato: " + openAir.ventilationType,
                    new BlockPos(0, 1, 0));
        }

        // Volume >= MAX_AIR_VOLUME -> UNCONFINED_CAVERN (miasma diluted in massive
        // volume -> CLEAN)
        ToxicAirEvent.MiasmaResult hugeRoom = new ToxicAirEvent.MiasmaResult(5.0, 0.0, false, 1024,
                java.util.Collections.emptySet());
        if (hugeRoom.level != ToxicAirEvent.AirToxicityLevel.CLEAN) {
            context.throwPositionedException(
                    "Stanza >= 1024 blocchi con bassa muffa deve essere considerata CLEAN per diluizione, trovato: "
                            + hugeRoom.level,
                    new BlockPos(0, 1, 0));
        }
        if (hugeRoom.ventilationType != ToxicAirEvent.RoomVentilationType.UNCONFINED_CAVERN) {
            context.throwPositionedException(
                    "Stanza >= 1024 blocchi deve risultare UNCONFINED_CAVERN, trovato: " + hugeRoom.ventilationType,
                    new BlockPos(0, 1, 0));
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testMiasmaDensityThresholds(TestContext context) {
        // Stanza piccola (volume 5) con 5 tronchi stadio 3 marci (toxicScore = 5 * 3 *
        // 1.5 = 22.5)
        // netMiasma = 22.5, densità = 22.5 / 5 = 4.5 -> LETHAL_POISON
        ToxicAirEvent.MiasmaResult smallDenseRoom = new ToxicAirEvent.MiasmaResult(22.5, 0.0, false, 5,
                java.util.Collections.emptySet());
        if (smallDenseRoom.level != ToxicAirEvent.AirToxicityLevel.LETHAL_POISON) {
            context.throwPositionedException(
                    "Stanza 5 blocchi con 22.5 tossicità deve essere LETHAL_POISON, trovato: " + smallDenseRoom.level,
                    new BlockPos(0, 1, 0));
        }

        // Stanza media (volume 20) con 2 tronchi stadio 2 (toxicScore = 2 * 2 * 1.5 =
        // 6.0)
        // netMiasma = 6.0, densità = 6.0 / 20 = 0.30 -> MODERATE_HUNGER
        ToxicAirEvent.MiasmaResult mediumRoom = new ToxicAirEvent.MiasmaResult(6.0, 0.0, false, 20,
                java.util.Collections.emptySet());
        if (mediumRoom.level != ToxicAirEvent.AirToxicityLevel.MODERATE_HUNGER) {
            context.throwPositionedException(
                    "Stanza media con 6.0 tossicità deve essere MODERATE_HUNGER, trovato: " + mediumRoom.level,
                    new BlockPos(0, 1, 0));
        }

        // Stanza grande (volume 100) con 3 tronchi stadio 1 (toxicScore = 3 * 1 * 1.5 =
        // 4.5)
        // netMiasma = 4.5, densità = 4.5 / 100 = 0.045 -> WARNING
        ToxicAirEvent.MiasmaResult largeRoom = new ToxicAirEvent.MiasmaResult(4.5, 0.0, false, 100,
                java.util.Collections.emptySet());
        if (largeRoom.level != ToxicAirEvent.AirToxicityLevel.WARNING) {
            context.throwPositionedException(
                    "Stanza grande con 4.5 tossicità deve essere WARNING, trovato: " + largeRoom.level,
                    new BlockPos(0, 1, 0));
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testDensityScalingWithVolume(TestContext context) {
        // Stesso carico di muffa (netMiasma = 12.0) in una stanza piccola vs grande
        ToxicAirEvent.MiasmaResult small = new ToxicAirEvent.MiasmaResult(12.0, 0.0, false, 6,
                java.util.Collections.emptySet());
        ToxicAirEvent.MiasmaResult large = new ToxicAirEvent.MiasmaResult(12.0, 0.0, false, 60,
                java.util.Collections.emptySet());

        if (small.density <= large.density) {
            context.throwPositionedException("La densità nella stanza piccola (" + small.density
                    + ") deve essere maggiore della grande (" + large.density + ")", new BlockPos(0, 1, 0));
        }
        if (small.exposureIndex <= large.exposureIndex) {
            context.throwPositionedException(
                    "L'indice di esposizione nella stanza piccola (" + small.exposureIndex
                            + ") deve essere maggiore della grande (" + large.exposureIndex + ")",
                    new BlockPos(0, 1, 0));
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testSealedRoomToxicity(TestContext context) {
        // Costruiamo una stanza 3x3x3 in pietra chiusa (1 blocco d'aria al centro: (2,
        // 2, 2))
        BlockPos center = new BlockPos(2, 2, 2);
        for (int x = 1; x <= 3; x++) {
            for (int y = 1; y <= 3; y++) {
                for (int z = 1; z <= 3; z++) {
                    context.setBlockState(new BlockPos(x, y, z), Blocks.STONE.getDefaultState());
                }
            }
        }
        context.setBlockState(center, Blocks.AIR.getDefaultState());

        // Mettiamo un blocco di legno infetto Stadio 2 su una parete interna (1, 2, 2)
        BlockState moldyLog = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState()
                .with(MoldyLogBlock.STAGE, 2)
                .with(MoldyLogBlock.WAXED, false);
        context.setBlockState(new BlockPos(1, 2, 2), moldyLog);

        ToxicAirEvent.MiasmaResult result = ToxicAirEvent.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));

        if (result.openAir) {
            context.throwPositionedException("La stanza sigillata non dovrebbe essere considerata openAir!", center);
        }
        if (result.volume != 1) {
            context.throwPositionedException(
                    "Il volume d'aria della stanza dovrebbe essere esattamente 1 blocco, trovato: " + result.volume,
                    center);
        }
        if (result.toxicScore <= 0.0) {
            context.throwPositionedException(
                    "Il punteggio di tossicità dovrebbe essere positivo per il legno allo Stadio 2!", center);
        }
        if (result.netMiasma != result.toxicScore) {
            context.throwPositionedException("In stanza chiusa, netMiasma deve coincidere con toxicScore!", center);
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testOpenAirDissipation(TestContext context) {
        BlockPos center = new BlockPos(2, 2, 2);
        BlockPos absCenter = context.getAbsolutePos(center);
        int topY = context.getWorld().getTopY(net.minecraft.world.Heightmap.Type.MOTION_BLOCKING, absCenter.getX(),
                absCenter.getZ());
        BlockPos skyPos = new BlockPos(absCenter.getX(), Math.max(topY, absCenter.getY()), absCenter.getZ());

        ToxicAirEvent.MiasmaResult result = ToxicAirEvent.calculateMiasma(context.getWorld(), skyPos);

        if (!result.openAir) {
            context.throwPositionedException("Un blocco alla coordinata topY del cielo deve avere openAir = true!",
                    center);
        }
        if (result.netMiasma != 0.0) {
            context.throwPositionedException("All'aperto il miasma netto deve essere 0.0!", center);
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
                    context.setBlockState(new BlockPos(x, y, z), Blocks.STONE.getDefaultState());
                }
            }
        }
        context.setBlockState(center, Blocks.AIR.getDefaultState());

        // Blocco Marcio (Stadio 3) ma Cerato
        BlockState waxedRotten = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState()
                .with(MoldyLogBlock.STAGE, 3)
                .with(MoldyLogBlock.WAXED, true);
        context.setBlockState(new BlockPos(1, 2, 2), waxedRotten);

        ToxicAirEvent.MiasmaResult result = ToxicAirEvent.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));

        if (result.toxicScore != 0.0) {
            context.throwPositionedException(
                    "Il legno cerato non deve produrre alcuno score di tossicità, trovato: " + result.toxicScore,
                    center);
        }
        if (result.netMiasma != 0.0) {
            context.throwPositionedException(
                    "Il miasma netto per legno cerato deve essere 0, trovato: " + result.netMiasma, center);
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testOpenAirThroughSlabGaps(TestContext context) {
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

        // Parete con bottom slab che affaccia l'apertura laterale verso il centro (1,
        // 2, 2) ed esterno a Ovest (0, 2, 2) con cielo aperto sopra (0, 3, 2)
        BlockPos slabPos = new BlockPos(1, 2, 2);
        BlockState bottomSlab = Blocks.OAK_SLAB.getDefaultState().with(net.minecraft.block.SlabBlock.TYPE,
                net.minecraft.block.enums.SlabType.BOTTOM);
        context.setBlockState(slabPos, bottomSlab);
        context.setBlockState(new BlockPos(0, 2, 2), Blocks.AIR.getDefaultState());
        context.setBlockState(new BlockPos(0, 3, 2), Blocks.AIR.getDefaultState());

        // Aggiungi focolare marcio
        BlockState moldy = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState().with(MoldyLogBlock.STAGE,
                2);
        context.setBlockState(new BlockPos(3, 2, 2), moldy);

        ToxicAirEvent.MiasmaResult result = ToxicAirEvent.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        if (result.ventilationType != ToxicAirEvent.RoomVentilationType.VENTILATED) {
            context.throwPositionedException(
                    "La mezza lastra verso l'esterno deve conferire VENTILATED, trovato: " + result.ventilationType,
                    center);
        }
        if (result.ventilationScore <= 0.0) {
            context.throwPositionedException(
                    "La mezza lastra verso l'esterno deve conferire un bonus di ventilazione > 0!", center);
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

        // Esterno a Ovest (0, 2, 2) con cielo aperto sopra (0, 3, 2)
        context.setBlockState(new BlockPos(0, 2, 2), Blocks.AIR.getDefaultState());
        context.setBlockState(new BlockPos(0, 3, 2), Blocks.AIR.getDefaultState());

        // Focolare marcio
        BlockState moldy = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState().with(MoldyLogBlock.STAGE,
                2);
        context.setBlockState(new BlockPos(3, 2, 2), moldy);

        BlockPos stairPos = new BlockPos(1, 2, 2);

        // 1. Scala con retro solido rivolto verso l'interno (FACING = WEST -> retro a
        // EST verso la stanza) -> BLOCCA (HERMETIC_SEALED)
        BlockState stairsBackFacingRoom = Blocks.OAK_STAIRS.getDefaultState()
                .with(net.minecraft.block.StairsBlock.FACING, Direction.WEST)
                .with(net.minecraft.block.StairsBlock.HALF, net.minecraft.block.enums.BlockHalf.BOTTOM);
        context.setBlockState(stairPos, stairsBackFacingRoom);

        ToxicAirEvent.MiasmaResult resultBackToRoom = ToxicAirEvent.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        if (resultBackToRoom.ventilationType != ToxicAirEvent.RoomVentilationType.HERMETIC_SEALED) {
            context.throwPositionedException(
                    "Scala con retro solido verso l'interno deve risultare HERMETIC_SEALED, trovato: "
                            + resultBackToRoom.ventilationType,
                    center);
        }

        // 2. Scala con gradino verso la stanza e retro solido verso l'esterno (FACING =
        // EAST -> retro a OVEST verso l'esterno) -> BLOCCA (HERMETIC_SEALED)
        BlockState stairsBackFacingOutside = Blocks.OAK_STAIRS.getDefaultState()
                .with(net.minecraft.block.StairsBlock.FACING, Direction.EAST)
                .with(net.minecraft.block.StairsBlock.HALF, net.minecraft.block.enums.BlockHalf.BOTTOM);
        context.setBlockState(stairPos, stairsBackFacingOutside);

        ToxicAirEvent.MiasmaResult resultBackToOutside = ToxicAirEvent.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        if (resultBackToOutside.ventilationType != ToxicAirEvent.RoomVentilationType.HERMETIC_SEALED) {
            context.throwPositionedException(
                    "Scala con gradino verso la stanza e retro solido verso l'esterno deve risultare HERMETIC_SEALED, trovato: "
                            + resultBackToOutside.ventilationType,
                    center);
        }

        // 3. Scala orientata lateralmente (FACING = SOUTH) -> il profilo aperto
        // permette il passaggio trasversale verso l'esterno a OVEST -> VENTILATED
        BlockState stairsSideways = Blocks.OAK_STAIRS.getDefaultState()
                .with(net.minecraft.block.StairsBlock.FACING, Direction.SOUTH)
                .with(net.minecraft.block.StairsBlock.HALF, net.minecraft.block.enums.BlockHalf.BOTTOM);
        context.setBlockState(stairPos, stairsSideways);

        ToxicAirEvent.MiasmaResult resultSideways = ToxicAirEvent.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        if (resultSideways.ventilationType != ToxicAirEvent.RoomVentilationType.VENTILATED) {
            context.throwPositionedException("Scala con profilo aperto trasversale deve risultare VENTILATED, trovato: "
                    + resultSideways.ventilationType, center);
        }
        if (resultSideways.ventilationScore <= 0.0) {
            context.throwPositionedException(
                    "Scala con profilo aperto trasversale deve conferire un bonus di ventilazione > 0!", center);
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testDoorOpenVsClosedAirFlow(TestContext context) {
        // Due stanzette adiacenti 3x3x3 in pietra separate da una parete a X=2
        for (int x = 0; x <= 4; x++) {
            for (int y = 1; y <= 3; y++) {
                for (int z = 1; z <= 3; z++) {
                    context.setBlockState(new BlockPos(x, y, z), Blocks.STONE.getDefaultState());
                }
            }
        }
        // Spazio aria stanza 1: (1, 2, 2)
        context.setBlockState(new BlockPos(1, 2, 2), Blocks.AIR.getDefaultState());
        // Spazio aria stanza 2: (3, 2, 2)
        context.setBlockState(new BlockPos(3, 2, 2), Blocks.AIR.getDefaultState());

        // Porta a (2, 2, 2)
        BlockPos doorPos = new BlockPos(2, 2, 2);
        BlockState closedDoor = Blocks.OAK_DOOR.getDefaultState()
                .with(net.minecraft.block.DoorBlock.HALF, net.minecraft.block.enums.DoubleBlockHalf.LOWER)
                .with(net.minecraft.block.DoorBlock.OPEN, false);
        context.setBlockState(doorPos, closedDoor);

        // 1. A porta CHIUSA: le stanze sono isolate ermeticamente (volume = 1)
        ToxicAirEvent.MiasmaResult resultClosed = ToxicAirEvent.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(new BlockPos(1, 2, 2)));
        if (resultClosed.volume != 1) {
            context.throwPositionedException(
                    "A porta chiusa il volume della stanza 1 deve essere 1, trovato: " + resultClosed.volume,
                    new BlockPos(1, 2, 2));
        }

        // 2. A porta APERTA: l'aria fluisce unendo le stanze (volume > 1)
        BlockState openDoor = closedDoor.with(net.minecraft.block.DoorBlock.OPEN, true);
        context.setBlockState(doorPos, openDoor);

        ToxicAirEvent.MiasmaResult resultOpen = ToxicAirEvent.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(new BlockPos(1, 2, 2)));
        if (resultOpen.volume <= 1) {
            context.throwPositionedException(
                    "A porta aperta il volume d'aria deve espandersi nella stanza adiacente, trovato: "
                            + resultOpen.volume,
                    new BlockPos(1, 2, 2));
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

        // Botola sul soffitto a (2, 3, 2)
        BlockPos trapdoorPos = new BlockPos(2, 3, 2);
        BlockState closedTrapdoor = Blocks.OAK_TRAPDOOR.getDefaultState()
                .with(net.minecraft.block.TrapdoorBlock.HALF, net.minecraft.block.enums.BlockHalf.BOTTOM)
                .with(net.minecraft.block.TrapdoorBlock.OPEN, false);
        context.setBlockState(trapdoorPos, closedTrapdoor);

        // 1. A botola CHIUSA: camera stagna, non comunica con il cielo
        ToxicAirEvent.MiasmaResult resultClosed = ToxicAirEvent.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        if (resultClosed.openAir) {
            context.throwPositionedException("A botola chiusa la stanza non deve essere a cielo aperto!", center);
        }

        // 2. A botola APERTA: comunica direttamente con il cielo (openAir = true)
        BlockState openTrapdoor = closedTrapdoor.with(net.minecraft.block.TrapdoorBlock.OPEN, true);
        context.setBlockState(trapdoorPos, openTrapdoor);

        ToxicAirEvent.MiasmaResult resultOpen = ToxicAirEvent.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        if (!resultOpen.openAir) {
            context.throwPositionedException("A botola aperta verso il cielo, openAir deve essere true!", center);
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testDoorBothHalvesAirtightVsOpen(TestContext context) {
        // Due stanze separate da una parete divisoria con una porta da 2 blocchi di
        // altezza
        for (int x = 0; x <= 4; x++) {
            for (int y = 1; y <= 4; y++) {
                for (int z = 1; z <= 3; z++) {
                    context.setBlockState(new BlockPos(x, y, z), Blocks.STONE.getDefaultState());
                }
            }
        }
        // Spazio aria stanza 1: (1, 2, 2) e (1, 3, 2)
        context.setBlockState(new BlockPos(1, 2, 2), Blocks.AIR.getDefaultState());
        context.setBlockState(new BlockPos(1, 3, 2), Blocks.AIR.getDefaultState());

        // Spazio aria stanza 2: (3, 2, 2) e (3, 3, 2)
        context.setBlockState(new BlockPos(3, 2, 2), Blocks.AIR.getDefaultState());
        context.setBlockState(new BlockPos(3, 3, 2), Blocks.AIR.getDefaultState());

        // Porta a 2 blocchi a (2, 2, 2) e (2, 3, 2)
        BlockPos doorLowerPos = new BlockPos(2, 2, 2);
        BlockPos doorUpperPos = new BlockPos(2, 3, 2);
        BlockState lowerClosed = Blocks.OAK_DOOR.getDefaultState()
                .with(net.minecraft.block.DoorBlock.HALF, net.minecraft.block.enums.DoubleBlockHalf.LOWER)
                .with(net.minecraft.block.DoorBlock.OPEN, false);
        BlockState upperClosed = Blocks.OAK_DOOR.getDefaultState()
                .with(net.minecraft.block.DoorBlock.HALF, net.minecraft.block.enums.DoubleBlockHalf.UPPER)
                .with(net.minecraft.block.DoorBlock.OPEN, false);

        context.setBlockState(doorLowerPos, lowerClosed);
        context.setBlockState(doorUpperPos, upperClosed);

        // 1. A porta CHIUSA (entrambi i blocchi): camera stagna, volume = 2 blocchi
        // della sola stanza 1
        ToxicAirEvent.MiasmaResult resultClosed = ToxicAirEvent.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(new BlockPos(1, 2, 2)));
        if (resultClosed.volume != 2) {
            context.throwPositionedException(
                    "A porta chiusa (entrambi i blocchi) il volume deve essere esattamente 2, trovato: "
                            + resultClosed.volume,
                    new BlockPos(1, 2, 2));
        }

        // 2. A porta APERTA (entrambi i blocchi): l'aria fluisce liberamente attraverso
        // entrambi i blocchi della porta (volume >= 4)
        BlockState lowerOpen = lowerClosed.with(net.minecraft.block.DoorBlock.OPEN, true);
        BlockState upperOpen = upperClosed.with(net.minecraft.block.DoorBlock.OPEN, true);
        context.setBlockState(doorLowerPos, lowerOpen);
        context.setBlockState(doorUpperPos, upperOpen);

        ToxicAirEvent.MiasmaResult resultOpen = ToxicAirEvent.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(new BlockPos(1, 2, 2)));
        if (resultOpen.volume < 4) {
            context.throwPositionedException(
                    "A porta aperta l'aria deve fluire attraverso entrambi i blocchi della porta (volume >= 4), trovato: "
                            + resultOpen.volume,
                    new BlockPos(1, 2, 2));
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testDoorPerimeterClosedIsAirtight(TestContext context) {
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

        // Esterno a Ovest (0, 2, 2) con cielo aperto sopra (0, 3, 2)
        context.setBlockState(new BlockPos(0, 2, 2), Blocks.AIR.getDefaultState());
        context.setBlockState(new BlockPos(0, 3, 2), Blocks.AIR.getDefaultState());

        // Focolare marcio per generare miasma
        BlockState moldy = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState().with(MoldyLogBlock.STAGE,
                2);
        context.setBlockState(new BlockPos(3, 2, 2), moldy);

        // Porta in Abete (Spruce) Chiusa: fa camera stagna (HERMETIC_SEALED)
        BlockPos doorPos = new BlockPos(1, 2, 2);
        BlockState spruceDoorClosed = Blocks.SPRUCE_DOOR.getDefaultState()
                .with(net.minecraft.block.DoorBlock.HALF, net.minecraft.block.enums.DoubleBlockHalf.LOWER)
                .with(net.minecraft.block.DoorBlock.OPEN, false);
        context.setBlockState(doorPos, spruceDoorClosed);

        ToxicAirEvent.MiasmaResult resultSpruce = ToxicAirEvent.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        if (resultSpruce.ventilationType != ToxicAirEvent.RoomVentilationType.HERMETIC_SEALED) {
            context.throwPositionedException(
                    "La porta in abete chiusa deve risultare HERMETIC_SEALED, trovato: " + resultSpruce.ventilationType,
                    center);
        }
        if (resultSpruce.netMiasma <= 0.0) {
            context.throwPositionedException("In stanza ermetica il miasma deve essere calcolato!", center);
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testTrapdoorPerimeterClosedIsAirtight(TestContext context) {
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

        // Esterno a Ovest (0, 2, 2) con cielo aperto sopra (0, 3, 2)
        context.setBlockState(new BlockPos(0, 2, 2), Blocks.AIR.getDefaultState());
        context.setBlockState(new BlockPos(0, 3, 2), Blocks.AIR.getDefaultState());

        // Focolare marcio per generare miasma
        BlockState moldy = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState().with(MoldyLogBlock.STAGE,
                2);
        context.setBlockState(new BlockPos(3, 2, 2), moldy);

        // Botola in Abete (Spruce) Chiusa su parete (OPEN = true, piastra verticale):
        // fa camera stagna (HERMETIC_SEALED)
        BlockPos trapdoorPos = new BlockPos(1, 2, 2);
        BlockState spruceTrapdoorClosed = Blocks.SPRUCE_TRAPDOOR.getDefaultState()
                .with(net.minecraft.block.TrapdoorBlock.FACING, Direction.WEST)
                .with(net.minecraft.block.TrapdoorBlock.HALF, net.minecraft.block.enums.BlockHalf.BOTTOM)
                .with(net.minecraft.block.TrapdoorBlock.OPEN, true);
        context.setBlockState(trapdoorPos, spruceTrapdoorClosed);

        ToxicAirEvent.MiasmaResult resultSpruce = ToxicAirEvent.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        if (resultSpruce.ventilationType != ToxicAirEvent.RoomVentilationType.HERMETIC_SEALED) {
            context.throwPositionedException("La botola in abete chiusa deve risultare HERMETIC_SEALED, trovato: "
                    + resultSpruce.ventilationType, center);
        }
        if (resultSpruce.netMiasma <= 0.0) {
            context.throwPositionedException("In stanza ermetica il miasma deve essere calcolato!", center);
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testLateralWallTrapdoorOpenVsClosed(TestContext context) {
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

        // Esterno a Ovest (0, 2, 2) con cielo aperto sopra (0, 3, 2)
        context.setBlockState(new BlockPos(0, 2, 2), Blocks.AIR.getDefaultState());
        context.setBlockState(new BlockPos(0, 3, 2), Blocks.AIR.getDefaultState());

        // Focolare marcio per generare miasma
        BlockState moldy = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState().with(MoldyLogBlock.STAGE,
                2);
        context.setBlockState(new BlockPos(3, 2, 2), moldy);

        // Botola in Abete su parete laterale Ovest (1, 2, 2) con FACING = WEST
        BlockPos trapdoorPos = new BlockPos(1, 2, 2);

        // 1. Chiusa su parete (piastra verticale a coprire il foro: in-game OPEN =
        // true) -> Camera Stagna
        BlockState lateralShutterClosed = Blocks.SPRUCE_TRAPDOOR.getDefaultState()
                .with(net.minecraft.block.TrapdoorBlock.FACING, Direction.WEST)
                .with(net.minecraft.block.TrapdoorBlock.HALF, net.minecraft.block.enums.BlockHalf.BOTTOM)
                .with(net.minecraft.block.TrapdoorBlock.OPEN, true);
        context.setBlockState(trapdoorPos, lateralShutterClosed);

        ToxicAirEvent.MiasmaResult resultClosed = ToxicAirEvent.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        if (resultClosed.ventilationType != ToxicAirEvent.RoomVentilationType.HERMETIC_SEALED) {
            context.throwPositionedException(
                    "La botola ermetica su parete laterale chiusa deve risultare HERMETIC_SEALED!", center);
        }

        // 2. Aperta su parete (piastra orizzontale a mensola: in-game OPEN = false) ->
        // Areazione verso l'esterno
        BlockState lateralShutterOpen = lateralShutterClosed.with(net.minecraft.block.TrapdoorBlock.OPEN, false);
        context.setBlockState(trapdoorPos, lateralShutterOpen);

        ToxicAirEvent.MiasmaResult resultOpen = ToxicAirEvent.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        if (resultOpen.ventilationType != ToxicAirEvent.RoomVentilationType.CLEAN_OPEN_AIR) {
            context.throwPositionedException(
                    "La botola su parete laterale aperta deve permettere CLEAN_OPEN_AIR verso l'esterno!", center);
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testDoorAndTrapdoorHermeticClosedVsOpen(TestContext context) {
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

        // Esterno a Ovest (0, 2, 2) con cielo aperto sopra (0, 3, 2)
        context.setBlockState(new BlockPos(0, 2, 2), Blocks.AIR.getDefaultState());
        context.setBlockState(new BlockPos(0, 3, 2), Blocks.AIR.getDefaultState());

        // Focolare marcio per generare miasma (stage 2 = toxicScore 3.0)
        BlockState moldy = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState().with(MoldyLogBlock.STAGE,
                2);
        context.setBlockState(new BlockPos(3, 2, 2), moldy);

        BlockPos doorLower = new BlockPos(1, 2, 2);
        BlockPos doorUpper = new BlockPos(1, 3, 2);

        // 1. Porta in Quercia Chiusa: sempre HERMETIC_SEALED
        BlockState oakDoorLower = Blocks.OAK_DOOR.getDefaultState()
                .with(net.minecraft.block.DoorBlock.HALF, net.minecraft.block.enums.DoubleBlockHalf.LOWER)
                .with(net.minecraft.block.DoorBlock.OPEN, false);
        BlockState oakDoorUpper = Blocks.OAK_DOOR.getDefaultState()
                .with(net.minecraft.block.DoorBlock.HALF, net.minecraft.block.enums.DoubleBlockHalf.UPPER)
                .with(net.minecraft.block.DoorBlock.OPEN, false);
        context.setBlockState(doorLower, oakDoorLower);
        context.setBlockState(doorUpper, oakDoorUpper);

        ToxicAirEvent.MiasmaResult resultOakDoor = ToxicAirEvent.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        if (resultOakDoor.ventilationType != ToxicAirEvent.RoomVentilationType.HERMETIC_SEALED) {
            context.throwPositionedException("La porta in quercia chiusa deve risultare HERMETIC_SEALED, trovato: "
                    + resultOakDoor.ventilationType, center);
        }

        // 2. Porta in Quercia Aperta: sempre CLEAN_OPEN_AIR
        context.setBlockState(doorLower, oakDoorLower.with(net.minecraft.block.DoorBlock.OPEN, true));
        context.setBlockState(doorUpper, oakDoorUpper.with(net.minecraft.block.DoorBlock.OPEN, true));
        ToxicAirEvent.MiasmaResult resultOakDoorOpen = ToxicAirEvent.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        if (resultOakDoorOpen.ventilationType != ToxicAirEvent.RoomVentilationType.CLEAN_OPEN_AIR) {
            context.throwPositionedException(
                    "La porta in quercia aperta verso l'esterno deve risultare CLEAN_OPEN_AIR, trovato: "
                            + resultOakDoorOpen.ventilationType,
                    center);
        }

        // Ripristina soffitto sopra la porta
        context.setBlockState(doorUpper, Blocks.STONE.getDefaultState());

        // 3. Botola in Quercia su parete Chiusa (OPEN = true, piastra verticale):
        // sempre HERMETIC_SEALED
        BlockState oakTrapdoorClosed = Blocks.OAK_TRAPDOOR.getDefaultState()
                .with(net.minecraft.block.TrapdoorBlock.FACING, Direction.WEST)
                .with(net.minecraft.block.TrapdoorBlock.HALF, net.minecraft.block.enums.BlockHalf.BOTTOM)
                .with(net.minecraft.block.TrapdoorBlock.OPEN, true);
        context.setBlockState(doorLower, oakTrapdoorClosed);

        ToxicAirEvent.MiasmaResult resultOakTrapdoor = ToxicAirEvent.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        if (resultOakTrapdoor.ventilationType != ToxicAirEvent.RoomVentilationType.HERMETIC_SEALED) {
            context.throwPositionedException(
                    "La botola in quercia chiusa su parete deve risultare HERMETIC_SEALED, trovato: "
                            + resultOakTrapdoor.ventilationType,
                    center);
        }

        // 4. Botola in Quercia su parete Aperta (OPEN = false, piastra orizzontale):
        // sempre CLEAN_OPEN_AIR
        context.setBlockState(doorLower, oakTrapdoorClosed.with(net.minecraft.block.TrapdoorBlock.OPEN, false));
        ToxicAirEvent.MiasmaResult resultOakTrapdoorOpen = ToxicAirEvent.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        if (resultOakTrapdoorOpen.ventilationType != ToxicAirEvent.RoomVentilationType.CLEAN_OPEN_AIR) {
            context.throwPositionedException(
                    "La botola in quercia aperta su parete deve risultare CLEAN_OPEN_AIR, trovato: "
                            + resultOakTrapdoorOpen.ventilationType,
                    center);
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testCopperBlocksAndFences(TestContext context) {
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

        // Esterno a Ovest (0, 2, 2) con cielo aperto sopra (0, 3, 2)
        context.setBlockState(new BlockPos(0, 2, 2), Blocks.AIR.getDefaultState());
        context.setBlockState(new BlockPos(0, 3, 2), Blocks.AIR.getDefaultState());

        // Focolare marcio per generare miasma
        BlockState moldy = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState().with(MoldyLogBlock.STAGE,
                2);
        context.setBlockState(new BlockPos(3, 2, 2), moldy);

        BlockPos wallPos = new BlockPos(1, 2, 2);

        // 1. Staccionata (Fence): sempre modificatore di ventilazione verso l'esterno
        // -> VENTILATED
        context.setBlockState(wallPos, Blocks.OAK_FENCE.getDefaultState());
        ToxicAirEvent.MiasmaResult resultFence = ToxicAirEvent.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        if (resultFence.ventilationType != ToxicAirEvent.RoomVentilationType.VENTILATED) {
            context.throwPositionedException(
                    "La staccionata verso l'esterno deve risultare VENTILATED, trovato: " + resultFence.ventilationType,
                    center);
        }
        if (resultFence.ventilationScore <= 0.0) {
            context.throwPositionedException(
                    "La staccionata verso l'esterno deve conferire un bonus di ventilazione > 0!", center);
        }

        // 2. Grata di rame (Copper Grate): trattata come aria (areazione totale) ->
        // CLEAN_OPEN_AIR
        context.setBlockState(wallPos, Blocks.COPPER_GRATE.getDefaultState());
        ToxicAirEvent.MiasmaResult resultCopperGrate = ToxicAirEvent.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        if (resultCopperGrate.ventilationType != ToxicAirEvent.RoomVentilationType.CLEAN_OPEN_AIR) {
            context.throwPositionedException("La grata di rame verso l'esterno deve risultare CLEAN_OPEN_AIR, trovato: "
                    + resultCopperGrate.ventilationType, center);
        }

        // 3. Porta di rame chiusa -> HERMETIC_SEALED
        BlockState copperDoorClosed = Blocks.COPPER_DOOR.getDefaultState()
                .with(net.minecraft.block.DoorBlock.HALF, net.minecraft.block.enums.DoubleBlockHalf.LOWER)
                .with(net.minecraft.block.DoorBlock.OPEN, false);
        context.setBlockState(wallPos, copperDoorClosed);
        ToxicAirEvent.MiasmaResult resultCopperDoorClosed = ToxicAirEvent.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        if (resultCopperDoorClosed.ventilationType != ToxicAirEvent.RoomVentilationType.HERMETIC_SEALED) {
            context.throwPositionedException("La porta di rame chiusa deve risultare HERMETIC_SEALED, trovato: "
                    + resultCopperDoorClosed.ventilationType, center);
        }

        // 4. Porta di rame aperta -> CLEAN_OPEN_AIR
        BlockState copperDoorOpen = copperDoorClosed.with(net.minecraft.block.DoorBlock.OPEN, true);
        context.setBlockState(wallPos, copperDoorOpen);
        ToxicAirEvent.MiasmaResult resultCopperDoorOpen = ToxicAirEvent.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        if (resultCopperDoorOpen.ventilationType != ToxicAirEvent.RoomVentilationType.CLEAN_OPEN_AIR) {
            context.throwPositionedException(
                    "La porta di rame aperta verso l'esterno deve risultare CLEAN_OPEN_AIR, trovato: "
                            + resultCopperDoorOpen.ventilationType,
                    center);
        }

        // 5. Botola di rame su parete chiusa (OPEN = true, piastra verticale):
        // HERMETIC_SEALED
        BlockState copperTrapdoorClosed = Blocks.COPPER_TRAPDOOR.getDefaultState()
                .with(net.minecraft.block.TrapdoorBlock.FACING, Direction.WEST)
                .with(net.minecraft.block.TrapdoorBlock.HALF, net.minecraft.block.enums.BlockHalf.BOTTOM)
                .with(net.minecraft.block.TrapdoorBlock.OPEN, true);
        context.setBlockState(wallPos, copperTrapdoorClosed);
        ToxicAirEvent.MiasmaResult resultCopperTrapdoorClosed = ToxicAirEvent.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        if (resultCopperTrapdoorClosed.ventilationType != ToxicAirEvent.RoomVentilationType.HERMETIC_SEALED) {
            context.throwPositionedException("La botola di rame chiusa deve risultare HERMETIC_SEALED, trovato: "
                    + resultCopperTrapdoorClosed.ventilationType, center);
        }

        // 6. Botola di rame su parete aperta (OPEN = false, piastra orizzontale):
        // CLEAN_OPEN_AIR
        BlockState copperTrapdoorOpen = copperTrapdoorClosed.with(net.minecraft.block.TrapdoorBlock.OPEN, false);
        context.setBlockState(wallPos, copperTrapdoorOpen);
        ToxicAirEvent.MiasmaResult resultCopperTrapdoorOpen = ToxicAirEvent.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        if (resultCopperTrapdoorOpen.ventilationType != ToxicAirEvent.RoomVentilationType.CLEAN_OPEN_AIR) {
            context.throwPositionedException("La botola di rame aperta deve risultare CLEAN_OPEN_AIR, trovato: "
                    + resultCopperTrapdoorOpen.ventilationType, center);
        }

        // 7. Staccionata sul soffitto -> VENTILATED
        context.setBlockState(wallPos, Blocks.STONE.getDefaultState());
        BlockPos ceilingPos = new BlockPos(2, 3, 2);
        context.setBlockState(ceilingPos, Blocks.OAK_FENCE.getDefaultState());
        ToxicAirEvent.MiasmaResult resultFenceCeiling = ToxicAirEvent.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        if (resultFenceCeiling.ventilationType != ToxicAirEvent.RoomVentilationType.VENTILATED) {
            context.throwPositionedException("La staccionata sul soffitto deve risultare VENTILATED, trovato: "
                    + resultFenceCeiling.ventilationType, center);
        }
        if (resultFenceCeiling.ventilationScore <= 0.0) {
            context.throwPositionedException("La staccionata sul soffitto deve conferire un bonus di ventilazione > 0!",
                    center);
        }
        context.setBlockState(ceilingPos, Blocks.STONE.getDefaultState());

        // 8. Staccionata sul pavimento con esterno/aria sotto -> VENTILATED
        BlockPos floorPos = new BlockPos(2, 1, 2);
        context.setBlockState(floorPos, Blocks.OAK_FENCE.getDefaultState());
        context.setBlockState(new BlockPos(2, 0, 2), Blocks.AIR.getDefaultState());
        context.setBlockState(new BlockPos(1, 0, 2), Blocks.AIR.getDefaultState());
        context.setBlockState(new BlockPos(0, 0, 2), Blocks.AIR.getDefaultState());
        ToxicAirEvent.MiasmaResult resultFenceFloor = ToxicAirEvent.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        if (resultFenceFloor.ventilationType != ToxicAirEvent.RoomVentilationType.VENTILATED) {
            context.throwPositionedException(
                    "La staccionata sul pavimento verso l'esterno deve risultare VENTILATED, trovato: "
                            + resultFenceFloor.ventilationType,
                    center);
        }
        if (resultFenceFloor.ventilationScore <= 0.0) {
            context.throwPositionedException(
                    "La staccionata sul pavimento deve conferire un bonus di ventilazione > 0!", center);
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testWallBlocksPlacement(TestContext context) {
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

        // Esterno a Ovest (0, 2, 2) con cielo aperto sopra (0, 3, 2)
        context.setBlockState(new BlockPos(0, 2, 2), Blocks.AIR.getDefaultState());
        context.setBlockState(new BlockPos(0, 3, 2), Blocks.AIR.getDefaultState());

        // Focolare marcio per generare miasma
        BlockState moldy = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState().with(MoldyLogBlock.STAGE,
                2);
        context.setBlockState(new BlockPos(3, 2, 2), moldy);

        // 1. Muretto su parete laterale CONNESSO (1, 2, 2) -> chiude ERMETICAMENTE
        // (HERMETIC_SEALED)
        BlockPos wallPos = new BlockPos(1, 2, 2);
        BlockState connectedWall = Blocks.COBBLESTONE_WALL.getDefaultState()
                .with(net.minecraft.block.WallBlock.NORTH_SHAPE, net.minecraft.block.enums.WallShape.LOW)
                .with(net.minecraft.block.WallBlock.SOUTH_SHAPE, net.minecraft.block.enums.WallShape.LOW);
        context.setBlockState(wallPos, connectedWall);
        ToxicAirEvent.MiasmaResult resultWallConnected = ToxicAirEvent.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        if (resultWallConnected.ventilationType != ToxicAirEvent.RoomVentilationType.HERMETIC_SEALED) {
            context.throwPositionedException(
                    "Il muretto connesso su parete laterale deve chiudere ermeticamente (HERMETIC_SEALED), trovato: "
                            + resultWallConnected.ventilationType,
                    center);
        }

        // 2. Muretto su parete laterale con SOLO 1 CONNESSIONE (1, 2, 2) -> dà bonus di
        // ventilazione (VENTILATED)
        BlockState singleConnectedWall = Blocks.COBBLESTONE_WALL.getDefaultState()
                .with(net.minecraft.block.WallBlock.NORTH_SHAPE, net.minecraft.block.enums.WallShape.LOW);
        context.setBlockState(wallPos, singleConnectedWall);
        ToxicAirEvent.MiasmaResult resultWallSingle = ToxicAirEvent.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        if (resultWallSingle.ventilationType != ToxicAirEvent.RoomVentilationType.VENTILATED) {
            context.throwPositionedException(
                    "Il muretto con solo 1 connessione su parete laterale deve risultare VENTILATED, trovato: "
                            + resultWallSingle.ventilationType,
                    center);
        }
        if (resultWallSingle.ventilationScore <= 0.0) {
            context.throwPositionedException(
                    "Il muretto con 1 connessione su parete deve conferire un bonus di ventilazione > 0!", center);
        }

        // 3. Muretto su parete laterale NON CONNESSO / ISOLATO (1, 2, 2) -> dà bonus di
        // ventilazione (VENTILATED)
        BlockState isolatedWall = Blocks.COBBLESTONE_WALL.getDefaultState();
        context.setBlockState(wallPos, isolatedWall);
        ToxicAirEvent.MiasmaResult resultWallIsolated = ToxicAirEvent.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        if (resultWallIsolated.ventilationType != ToxicAirEvent.RoomVentilationType.VENTILATED) {
            context.throwPositionedException(
                    "Il muretto isolato su parete laterale deve risultare VENTILATED, trovato: "
                            + resultWallIsolated.ventilationType,
                    center);
        }
        if (resultWallIsolated.ventilationScore <= 0.0) {
            context.throwPositionedException(
                    "Il muretto isolato su parete deve conferire un bonus di ventilazione > 0!", center);
        }
        context.setBlockState(wallPos, Blocks.STONE.getDefaultState());

        // 4. Muretto sul soffitto (2, 3, 2) -> conferisce bonus VENTILATED
        BlockPos ceilingPos = new BlockPos(2, 3, 2);
        context.setBlockState(ceilingPos, Blocks.COBBLESTONE_WALL.getDefaultState());
        ToxicAirEvent.MiasmaResult resultWallCeiling = ToxicAirEvent.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        if (resultWallCeiling.ventilationType != ToxicAirEvent.RoomVentilationType.VENTILATED) {
            context.throwPositionedException(
                    "Il muretto sul soffitto deve risultare VENTILATED, trovato: " + resultWallCeiling.ventilationType,
                    center);
        }
        if (resultWallCeiling.ventilationScore <= 0.0) {
            context.throwPositionedException("Il muretto sul soffitto deve conferire un bonus di ventilazione > 0!",
                    center);
        }
        context.setBlockState(ceilingPos, Blocks.STONE.getDefaultState());

        // 5. Muretto sul pavimento (2, 1, 2) verso esterno/aria sotto -> conferisce
        // bonus VENTILATED
        BlockPos floorPos = new BlockPos(2, 1, 2);
        context.setBlockState(floorPos, Blocks.COBBLESTONE_WALL.getDefaultState());
        context.setBlockState(new BlockPos(2, 0, 2), Blocks.AIR.getDefaultState());
        context.setBlockState(new BlockPos(1, 0, 2), Blocks.AIR.getDefaultState());
        context.setBlockState(new BlockPos(0, 0, 2), Blocks.AIR.getDefaultState());
        ToxicAirEvent.MiasmaResult resultWallFloor = ToxicAirEvent.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        if (resultWallFloor.ventilationType != ToxicAirEvent.RoomVentilationType.VENTILATED) {
            context.throwPositionedException(
                    "Il muretto sul pavimento verso l'esterno deve risultare VENTILATED, trovato: "
                            + resultWallFloor.ventilationType,
                    center);
        }
        if (resultWallFloor.ventilationScore <= 0.0) {
            context.throwPositionedException("Il muretto sul pavimento deve conferire un bonus di ventilazione > 0!",
                    center);
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testDecorativeBlocksDoNotObstruct(TestContext context) {
        // Stanza in pietra chiusa (x: 1..3, y: 1..3, z: 0..3)
        BlockPos center = new BlockPos(2, 2, 2);
        for (int x = 1; x <= 3; x++) {
            for (int y = 1; y <= 3; y++) {
                for (int z = 0; z <= 3; z++) {
                    context.setBlockState(new BlockPos(x, y, z), Blocks.STONE.getDefaultState());
                }
            }
        }
        context.setBlockState(center, Blocks.AIR.getDefaultState());
        context.setBlockState(new BlockPos(2, 2, 1), Blocks.AIR.getDefaultState());

        // Catena decorativa nel secondo blocco d'aria
        context.setBlockState(new BlockPos(2, 2, 1),
                Blocks.CHAIN.getDefaultState().with(net.minecraft.block.ChainBlock.AXIS, Direction.Axis.Y));

        // Focolare marcio su parete (3, 2, 2)
        BlockState moldy = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState().with(MoldyLogBlock.STAGE,
                2);
        context.setBlockState(new BlockPos(3, 2, 2), moldy);

        ToxicAirEvent.MiasmaResult result = ToxicAirEvent.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        if (result.volume < 2) {
            context.throwPositionedException(
                    "Le catene/decorazioni non devono ostacolare la diffusione dell'aria (volume >= 2), trovato: "
                            + result.volume,
                    center);
        }
        if (result.toxicScore <= 0.0) {
            context.throwPositionedException("La tossicità deve propagarsi attraverso blocchi decorativi!", center);
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testIronBarsVentilation(TestContext context) {
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

        // Esterno a Ovest (0, 2, 2) con cielo aperto sopra (0, 3, 2)
        context.setBlockState(new BlockPos(0, 2, 2), Blocks.AIR.getDefaultState());
        context.setBlockState(new BlockPos(0, 3, 2), Blocks.AIR.getDefaultState());

        // Focolare marcio
        BlockState moldy = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState().with(MoldyLogBlock.STAGE,
                2);
        context.setBlockState(new BlockPos(3, 2, 2), moldy);

        // Grata di ferro su parete (1, 2, 2)
        context.setBlockState(new BlockPos(1, 2, 2), Blocks.IRON_BARS.getDefaultState());
        ToxicAirEvent.MiasmaResult result = ToxicAirEvent.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        if (result.ventilationType != ToxicAirEvent.RoomVentilationType.VENTILATED) {
            context.throwPositionedException(
                    "Le grate di ferro verso l'esterno devono risultare VENTILATED, trovato: " + result.ventilationType,
                    center);
        }
        if (result.ventilationScore <= 0.0) {
            context.throwPositionedException("Le grate di ferro devono conferire un bonus di ventilazione > 0!",
                    center);
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testFenceGateClosedIsVentilatedAndOpenIsClean(TestContext context) {
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

        // Esterno a Ovest (0, 2, 2) con cielo aperto sopra (0, 3, 2)
        context.setBlockState(new BlockPos(0, 2, 2), Blocks.AIR.getDefaultState());
        context.setBlockState(new BlockPos(0, 3, 2), Blocks.AIR.getDefaultState());

        // Focolare marcio
        BlockState moldy = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState().with(MoldyLogBlock.STAGE,
                2);
        context.setBlockState(new BlockPos(3, 2, 2), moldy);

        BlockPos gatePos = new BlockPos(1, 2, 2);

        // 1. Cancelletto Chiuso (OPEN = false) -> deve dare bonus VENTILATED (+3.0)
        // come le staccionate
        BlockState gateClosed = Blocks.OAK_FENCE_GATE.getDefaultState()
                .with(net.minecraft.block.FenceGateBlock.FACING, Direction.WEST)
                .with(net.minecraft.block.FenceGateBlock.OPEN, false);
        context.setBlockState(gatePos, gateClosed);

        ToxicAirEvent.MiasmaResult resultClosed = ToxicAirEvent.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        if (resultClosed.ventilationType != ToxicAirEvent.RoomVentilationType.VENTILATED) {
            context.throwPositionedException(
                    "Il cancelletto chiuso deve risultare VENTILATED, trovato: " + resultClosed.ventilationType,
                    center);
        }
        if (resultClosed.ventilationScore != 3.0) {
            context.throwPositionedException(
                    "Il cancelletto chiuso deve dare ventilationScore = 3.0, trovato: " + resultClosed.ventilationScore,
                    center);
        }

        // 2. Cancelletto Aperto -> CLEAN_OPEN_AIR
        BlockState gateOpen = gateClosed.with(net.minecraft.block.FenceGateBlock.OPEN, true);
        context.setBlockState(gatePos, gateOpen);

        ToxicAirEvent.MiasmaResult resultOpen = ToxicAirEvent.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        if (resultOpen.ventilationType != ToxicAirEvent.RoomVentilationType.CLEAN_OPEN_AIR) {
            context.throwPositionedException(
                    "Il cancelletto aperto verso l'esterno deve risultare CLEAN_OPEN_AIR, trovato: "
                            + resultOpen.ventilationType,
                    center);
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testTrapdoorFloorAndCeilingOrientations(TestContext context) {
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

        // Focolare marcio
        BlockState moldy = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState().with(MoldyLogBlock.STAGE,
                2);
        context.setBlockState(new BlockPos(3, 2, 2), moldy);

        // 1. Botola su soffitto Chiusa (OPEN = false) -> HERMETIC_SEALED
        BlockPos ceilingPos = new BlockPos(2, 3, 2);
        BlockState trapdoorCeilingClosed = Blocks.OAK_TRAPDOOR.getDefaultState()
                .with(net.minecraft.block.TrapdoorBlock.HALF, net.minecraft.block.enums.BlockHalf.BOTTOM)
                .with(net.minecraft.block.TrapdoorBlock.OPEN, false);
        context.setBlockState(ceilingPos, trapdoorCeilingClosed);

        ToxicAirEvent.MiasmaResult resultCeilingClosed = ToxicAirEvent.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        if (resultCeilingClosed.ventilationType != ToxicAirEvent.RoomVentilationType.HERMETIC_SEALED) {
            context.throwPositionedException("Botola chiusa sul soffitto deve risultare HERMETIC_SEALED, trovato: "
                    + resultCeilingClosed.ventilationType, center);
        }

        // 2. Botola su soffitto Aperta (OPEN = true) -> CLEAN_OPEN_AIR
        context.setBlockState(ceilingPos, trapdoorCeilingClosed.with(net.minecraft.block.TrapdoorBlock.OPEN, true));
        ToxicAirEvent.MiasmaResult resultCeilingOpen = ToxicAirEvent.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        if (resultCeilingOpen.ventilationType != ToxicAirEvent.RoomVentilationType.CLEAN_OPEN_AIR) {
            context.throwPositionedException("Botola aperta sul soffitto deve risultare CLEAN_OPEN_AIR, trovato: "
                    + resultCeilingOpen.ventilationType, center);
        }
        context.setBlockState(ceilingPos, Blocks.STONE.getDefaultState());

        // 3. Botola su pavimento Chiusa (OPEN = false) -> HERMETIC_SEALED
        BlockPos floorPos = new BlockPos(2, 1, 2);
        context.setBlockState(new BlockPos(2, 0, 2), Blocks.AIR.getDefaultState());
        context.setBlockState(new BlockPos(1, 0, 2), Blocks.AIR.getDefaultState());
        context.setBlockState(new BlockPos(0, 0, 2), Blocks.AIR.getDefaultState()); // esterno sotto
        context.setBlockState(new BlockPos(0, 1, 2), Blocks.AIR.getDefaultState());
        context.setBlockState(new BlockPos(0, 2, 2), Blocks.AIR.getDefaultState());
        context.setBlockState(new BlockPos(0, 3, 2), Blocks.AIR.getDefaultState());
        BlockState trapdoorFloorClosed = Blocks.OAK_TRAPDOOR.getDefaultState()
                .with(net.minecraft.block.TrapdoorBlock.HALF, net.minecraft.block.enums.BlockHalf.TOP)
                .with(net.minecraft.block.TrapdoorBlock.OPEN, false);
        context.setBlockState(floorPos, trapdoorFloorClosed);

        ToxicAirEvent.MiasmaResult resultFloorClosed = ToxicAirEvent.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        if (resultFloorClosed.ventilationType != ToxicAirEvent.RoomVentilationType.HERMETIC_SEALED) {
            context.throwPositionedException("Botola chiusa sul pavimento deve risultare HERMETIC_SEALED, trovato: "
                    + resultFloorClosed.ventilationType, center);
        }

        // 4. Botola su pavimento Aperta (OPEN = true) -> CLEAN_OPEN_AIR
        context.setBlockState(floorPos, trapdoorFloorClosed.with(net.minecraft.block.TrapdoorBlock.OPEN, true));
        ToxicAirEvent.MiasmaResult resultFloorOpen = ToxicAirEvent.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        if (resultFloorOpen.ventilationType != ToxicAirEvent.RoomVentilationType.CLEAN_OPEN_AIR) {
            context.throwPositionedException(
                    "Botola aperta sul pavimento verso l'esterno deve risultare CLEAN_OPEN_AIR, trovato: "
                            + resultFloorOpen.ventilationType,
                    center);
        }

        context.complete();
    }
}
