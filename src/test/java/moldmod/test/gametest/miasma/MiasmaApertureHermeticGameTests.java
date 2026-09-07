package moldmod.test.gametest.miasma;

import moldmod.block.ModBlocks;
import moldmod.event.MiasmaCalculator;
import moldmod.test.helper.RoomTestBuilder;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChainBlock;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.WallBlock;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.block.enums.SlabType;
import net.minecraft.block.enums.WallShape;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;

import java.util.Collections;

public class MiasmaApertureHermeticGameTests {

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testCleanAirThresholds(TestContext context) {
        // No mold blocks -> CLEAN
        MiasmaCalculator.MiasmaResult clean = new MiasmaCalculator.MiasmaResult(0.0, 0.0, false, 10,
                Collections.emptySet());
        context.assertTrue(clean.level == MiasmaCalculator.AirToxicityLevel.CLEAN,
                "Miasma 0 must be CLEAN, got: " + clean.level);

        // Open Air -> always CLEAN
        MiasmaCalculator.MiasmaResult openAir = new MiasmaCalculator.MiasmaResult(50.0, 0.0, true, 10,
                Collections.emptySet());
        context.assertTrue(openAir.level == MiasmaCalculator.AirToxicityLevel.CLEAN,
                "Open Air must be CLEAN, got: " + openAir.level);
        context.assertTrue(openAir.netMiasma == 0.0,
                "Open Air netMiasma must be 0.0, got: " + openAir.netMiasma);
        context.assertTrue(openAir.ventilationType == MiasmaCalculator.RoomVentilationType.CLEAN_OPEN_AIR,
                "Open Air ventilationType must be CLEAN_OPEN_AIR, got: " + openAir.ventilationType);

        // Volume >= MAX_AIR_VOLUME -> UNCONFINED_CAVERN
        MiasmaCalculator.MiasmaResult hugeRoom = new MiasmaCalculator.MiasmaResult(5.0, 0.0, false, 1024,
                Collections.emptySet());
        context.assertTrue(hugeRoom.level == MiasmaCalculator.AirToxicityLevel.CLEAN,
                "Huge cavern with low mold must be CLEAN by dilution, got: " + hugeRoom.level);
        context.assertTrue(hugeRoom.ventilationType == MiasmaCalculator.RoomVentilationType.UNCONFINED_CAVERN,
                "Huge room must be UNCONFINED_CAVERN, got: " + hugeRoom.ventilationType);

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testMiasmaDensityThresholds(TestContext context) {
        // Small dense room (volume 5, toxicScore = 22.5) -> LETHAL_POISON
        MiasmaCalculator.MiasmaResult smallDenseRoom = new MiasmaCalculator.MiasmaResult(22.5, 0.0, false, 5,
                Collections.emptySet());
        context.assertTrue(smallDenseRoom.level == MiasmaCalculator.AirToxicityLevel.LETHAL_POISON,
                "Small dense room must be LETHAL_POISON, got: " + smallDenseRoom.level);

        // Medium room (volume 20, toxicScore = 6.0) -> MODERATE_HUNGER
        MiasmaCalculator.MiasmaResult mediumRoom = new MiasmaCalculator.MiasmaResult(6.0, 0.0, false, 20,
                Collections.emptySet());
        context.assertTrue(mediumRoom.level == MiasmaCalculator.AirToxicityLevel.MODERATE_HUNGER,
                "Medium room must be MODERATE_HUNGER, got: " + mediumRoom.level);

        // Large room (volume 100, toxicScore = 4.5) -> WARNING
        MiasmaCalculator.MiasmaResult largeRoom = new MiasmaCalculator.MiasmaResult(4.5, 0.0, false, 100,
                Collections.emptySet());
        context.assertTrue(largeRoom.level == MiasmaCalculator.AirToxicityLevel.WARNING,
                "Large room must be WARNING, got: " + largeRoom.level);

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testDensityScalingWithVolume(TestContext context) {
        MiasmaCalculator.MiasmaResult small = new MiasmaCalculator.MiasmaResult(12.0, 0.0, false, 6,
                Collections.emptySet());
        MiasmaCalculator.MiasmaResult large = new MiasmaCalculator.MiasmaResult(12.0, 0.0, false, 60,
                Collections.emptySet());

        context.assertTrue(small.density > large.density,
                "Small room density (" + small.density + ") must be > large (" + large.density + ")");
        context.assertTrue(small.exposureIndex > large.exposureIndex,
                "Small room exposure (" + small.exposureIndex + ") must be > large (" + large.exposureIndex + ")");

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testBase6ExactThresholdTransitions(TestContext context) {
        // 1. CLEAN: M=0 -> CLEAN
        MiasmaCalculator.MiasmaResult rClean = new MiasmaCalculator.MiasmaResult(0.0, 0.0, false, 24, Collections.emptySet());
        context.assertTrue(rClean.level == MiasmaCalculator.AirToxicityLevel.CLEAN, "M=0 must be CLEAN");

        // 2. WARNING: M=2.5, D = 2.5 / 90 = 0.0277 -> WARNING
        MiasmaCalculator.MiasmaResult rWarning = new MiasmaCalculator.MiasmaResult(2.5, 0.0, false, 90, Collections.emptySet());
        context.assertTrue(rWarning.level == MiasmaCalculator.AirToxicityLevel.WARNING, "M=2.5/90 must be WARNING");

        // 3. MODERATE_HUNGER: M=6.0, D = 6.0 / 140 = 0.0428 -> MODERATE_HUNGER
        MiasmaCalculator.MiasmaResult rHunger1 = new MiasmaCalculator.MiasmaResult(6.0, 0.0, false, 140, Collections.emptySet());
        context.assertTrue(rHunger1.level == MiasmaCalculator.AirToxicityLevel.MODERATE_HUNGER, "M=6.0 with D>=0.0417 must be MODERATE_HUNGER");

        // 4. MODERATE_HUNGER from medium density: M=3.5, D = 3.5 / 35 = 0.10 -> MODERATE_HUNGER
        MiasmaCalculator.MiasmaResult rHunger2 = new MiasmaCalculator.MiasmaResult(3.5, 0.0, false, 35, Collections.emptySet());
        context.assertTrue(rHunger2.level == MiasmaCalculator.AirToxicityLevel.MODERATE_HUNGER, "M=3.5 with D>=0.0833 must be MODERATE_HUNGER");

        // 5. LETHAL_POISON: M=18.0, D = 18.0 / 200 = 0.09 -> LETHAL_POISON
        MiasmaCalculator.MiasmaResult rPoison1 = new MiasmaCalculator.MiasmaResult(18.0, 0.0, false, 200, Collections.emptySet());
        context.assertTrue(rPoison1.level == MiasmaCalculator.AirToxicityLevel.LETHAL_POISON, "M=18.0 with D>=0.0833 must be LETHAL_POISON");

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testOpenAirDissipation(TestContext context) {
        BlockPos center = new BlockPos(2, 2, 2);
        BlockPos absCenter = context.getAbsolutePos(center);
        int topY = context.getWorld().getTopY(Heightmap.Type.MOTION_BLOCKING, absCenter.getX(), absCenter.getZ());
        BlockPos skyPos = new BlockPos(absCenter.getX(), Math.max(topY, absCenter.getY()), absCenter.getZ());

        MiasmaCalculator.MiasmaResult result = MiasmaCalculator.calculateMiasma(context.getWorld(), skyPos);

        context.assertTrue(result.openAir, "Block at topY must have openAir = true");
        context.assertTrue(result.netMiasma == 0.0, "Open air net miasma must be 0.0");

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testSealedRoomToxicity(TestContext context) {
        BlockPos center = new BlockPos(2, 2, 2);
        RoomTestBuilder.of(context)
                .stoneRoom(1, 1, 1, 3, 3, 3)
                .addMoldyOakLog(1, 2, 2, 2);

        MiasmaCalculator.MiasmaResult result = MiasmaCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));

        context.assertFalse(result.openAir, "Sealed room must not be openAir");
        context.assertTrue(result.volume == 1, "Air volume should be 1 block, got: " + result.volume);
        context.assertTrue(result.toxicScore > 0.0, "Toxic score should be > 0 for Stage 2 log");
        context.assertTrue(result.netMiasma == result.toxicScore, "In sealed room netMiasma == toxicScore");

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testWaxedWoodMiasmaImmunity(TestContext context) {
        BlockPos center = new BlockPos(2, 2, 2);
        RoomTestBuilder.of(context)
                .stoneRoom(1, 1, 1, 3, 3, 3)
                .addMoldyLog(1, 2, 2, Blocks.OAK_LOG, 3, true);

        MiasmaCalculator.MiasmaResult result = MiasmaCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));

        context.assertTrue(result.toxicScore == 0.0, "Waxed wood must produce 0 toxicity");
        context.assertTrue(result.netMiasma == 0.0, "Net miasma for waxed wood must be 0");

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testOpenAirThroughSlabGaps(TestContext context) {
        BlockPos center = new BlockPos(2, 2, 2);
        RoomTestBuilder.of(context)
                .stoneRoom(1, 1, 1, 3, 3, 3)
                .addSlab(1, 2, 2, Blocks.OAK_SLAB, SlabType.BOTTOM)
                .setAir(0, 2, 2)
                .setAir(0, 3, 2)
                .addMoldyOakLog(3, 2, 2, 2);

        MiasmaCalculator.MiasmaResult result = MiasmaCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));

        context.assertTrue(result.ventilationType == MiasmaCalculator.RoomVentilationType.VENTILATED,
                "Half slab facing exterior must grant VENTILATED");
        context.assertTrue(result.ventilationScore > 0.0,
                "Half slab facing exterior must grant ventilation bonus > 0");

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testStairsDirectionalAirPassage(TestContext context) {
        BlockPos center = new BlockPos(2, 2, 2);

        RoomTestBuilder.of(context)
                .stoneRoom(1, 1, 1, 3, 3, 3)
                .setAir(0, 2, 2)
                .setAir(0, 3, 2)
                .addMoldyOakLog(3, 2, 2, 2);

        // 1. Solid back facing room (FACING = WEST) -> HERMETIC_SEALED
        RoomTestBuilder.of(context).addStairs(1, 2, 2, Blocks.OAK_STAIRS, Direction.WEST, BlockHalf.BOTTOM);
        MiasmaCalculator.MiasmaResult resultBackToRoom = MiasmaCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        context.assertTrue(resultBackToRoom.ventilationType == MiasmaCalculator.RoomVentilationType.HERMETIC_SEALED,
                "Stairs with back facing room must be HERMETIC_SEALED");

        // 2. Solid back facing outside (FACING = EAST) -> HERMETIC_SEALED
        RoomTestBuilder.of(context).addStairs(1, 2, 2, Blocks.OAK_STAIRS, Direction.EAST, BlockHalf.BOTTOM);
        MiasmaCalculator.MiasmaResult resultBackToOutside = MiasmaCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        context.assertTrue(resultBackToOutside.ventilationType == MiasmaCalculator.RoomVentilationType.HERMETIC_SEALED,
                "Stairs with back facing outside must be HERMETIC_SEALED");

        // 3. Sideways stairs (FACING = SOUTH) -> VENTILATED
        RoomTestBuilder.of(context).addStairs(1, 2, 2, Blocks.OAK_STAIRS, Direction.SOUTH, BlockHalf.BOTTOM);
        MiasmaCalculator.MiasmaResult resultSideways = MiasmaCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        context.assertTrue(resultSideways.ventilationType == MiasmaCalculator.RoomVentilationType.VENTILATED,
                "Sideways stairs must be VENTILATED");
        context.assertTrue(resultSideways.ventilationScore > 0.0,
                "Sideways stairs must grant ventilation score > 0");

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testDoorOpenVsClosedAirFlow(TestContext context) {
        // Two adjacent 3x3x3 stone rooms separated by wall at X=2
        RoomTestBuilder.of(context)
                .fill(0, 1, 1, 4, 3, 3, Blocks.STONE.getDefaultState())
                .setAir(1, 2, 2)
                .setAir(3, 2, 2)
                .set(2, 2, 2, Blocks.OAK_DOOR.getDefaultState()
                        .with(DoorBlock.HALF, DoubleBlockHalf.LOWER)
                        .with(DoorBlock.OPEN, false));

        BlockPos room1 = new BlockPos(1, 2, 2);

        // 1. Closed door: volume = 1
        MiasmaCalculator.MiasmaResult resultClosed = MiasmaCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(room1));
        context.assertTrue(resultClosed.volume == 1, "Closed door room volume must be 1, got: " + resultClosed.volume);

        // 2. Open door: volume > 1
        RoomTestBuilder.of(context).set(2, 2, 2, Blocks.OAK_DOOR.getDefaultState()
                .with(DoorBlock.HALF, DoubleBlockHalf.LOWER)
                .with(DoorBlock.OPEN, true));

        MiasmaCalculator.MiasmaResult resultOpen = MiasmaCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(room1));
        context.assertTrue(resultOpen.volume > 1, "Open door must merge rooms (volume > 1), got: " + resultOpen.volume);

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testTrapdoorOpenVsClosedAirFlow(TestContext context) {
        BlockPos center = new BlockPos(2, 2, 2);
        RoomTestBuilder.of(context)
                .stoneRoom(1, 1, 1, 3, 3, 3)
                .addTrapdoor(2, 3, 2, Blocks.OAK_TRAPDOOR, BlockHalf.BOTTOM, false);

        // 1. Closed trapdoor: !openAir
        MiasmaCalculator.MiasmaResult resultClosed = MiasmaCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        context.assertFalse(resultClosed.openAir, "Closed ceiling trapdoor must not be openAir");

        // 2. Open trapdoor: VENTILATED, score > 0
        RoomTestBuilder.of(context).addTrapdoor(2, 3, 2, Blocks.OAK_TRAPDOOR, BlockHalf.BOTTOM, true);
        MiasmaCalculator.MiasmaResult resultOpen = MiasmaCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        context.assertTrue(resultOpen.ventilationType == MiasmaCalculator.RoomVentilationType.VENTILATED,
                "Open ceiling trapdoor must be VENTILATED");
        context.assertTrue(resultOpen.ventilationScore > 0.0,
                "Open ceiling trapdoor must provide ventilation score > 0");

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testDoorBothHalvesAirtightVsOpen(TestContext context) {
        // Two rooms with door height 2
        RoomTestBuilder.of(context)
                .fill(0, 1, 1, 4, 4, 3, Blocks.STONE.getDefaultState())
                .setAir(1, 2, 2)
                .setAir(1, 3, 2)
                .setAir(3, 2, 2)
                .setAir(3, 3, 2)
                .addDoor(2, 2, 2, Direction.NORTH, false);

        BlockPos room1 = new BlockPos(1, 2, 2);

        // 1. Closed door (both halves): volume = 2
        MiasmaCalculator.MiasmaResult resultClosed = MiasmaCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(room1));
        context.assertTrue(resultClosed.volume == 2, "Closed door room volume must be 2, got: " + resultClosed.volume);

        // 2. Open door (both halves): volume >= 4
        RoomTestBuilder.of(context).addDoor(2, 2, 2, Direction.NORTH, true);
        MiasmaCalculator.MiasmaResult resultOpen = MiasmaCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(room1));
        context.assertTrue(resultOpen.volume >= 4, "Open door volume must be >= 4, got: " + resultOpen.volume);

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testDoorPerimeterClosedIsAirtight(TestContext context) {
        BlockPos center = new BlockPos(2, 2, 2);
        RoomTestBuilder.of(context)
                .stoneRoom(1, 1, 1, 3, 3, 3)
                .setAir(0, 2, 2)
                .setAir(0, 3, 2)
                .addMoldyOakLog(3, 2, 2, 2)
                .set(1, 2, 2, Blocks.SPRUCE_DOOR.getDefaultState()
                        .with(DoorBlock.HALF, DoubleBlockHalf.LOWER)
                        .with(DoorBlock.OPEN, false));

        MiasmaCalculator.MiasmaResult resultSpruce = MiasmaCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        context.assertTrue(resultSpruce.ventilationType == MiasmaCalculator.RoomVentilationType.HERMETIC_SEALED,
                "Closed spruce door must be HERMETIC_SEALED");
        context.assertTrue(resultSpruce.netMiasma > 0.0, "Net miasma must be calculated in hermetic room");

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testTrapdoorPerimeterClosedIsAirtight(TestContext context) {
        BlockPos center = new BlockPos(2, 2, 2);
        RoomTestBuilder.of(context)
                .stoneRoom(1, 1, 1, 3, 3, 3)
                .setAir(0, 2, 2)
                .setAir(0, 3, 2)
                .addMoldyOakLog(3, 2, 2, 2)
                .addTrapdoor(1, 2, 2, Blocks.SPRUCE_TRAPDOOR, Direction.WEST, BlockHalf.BOTTOM, true);

        MiasmaCalculator.MiasmaResult resultSpruce = MiasmaCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        context.assertTrue(resultSpruce.ventilationType == MiasmaCalculator.RoomVentilationType.HERMETIC_SEALED,
                "Closed spruce trapdoor on wall must be HERMETIC_SEALED");
        context.assertTrue(resultSpruce.netMiasma > 0.0, "Net miasma must be calculated in hermetic room");

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testLateralWallTrapdoorOpenVsClosed(TestContext context) {
        BlockPos center = new BlockPos(2, 2, 2);
        RoomTestBuilder.of(context)
                .stoneRoom(1, 1, 1, 3, 3, 3)
                .setAir(0, 2, 2)
                .setAir(0, 3, 2)
                .addMoldyOakLog(3, 2, 2, 2)
                .addTrapdoor(1, 2, 2, Blocks.SPRUCE_TRAPDOOR, Direction.WEST, BlockHalf.BOTTOM, true);

        // 1. Shutter closed on wall (in-game OPEN = true, vertical plate) -> HERMETIC_SEALED
        MiasmaCalculator.MiasmaResult resultClosed = MiasmaCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        context.assertTrue(resultClosed.ventilationType == MiasmaCalculator.RoomVentilationType.HERMETIC_SEALED,
                "Closed shutter on wall must be HERMETIC_SEALED");

        // 2. Shutter open on wall (in-game OPEN = false, horizontal shelf) -> VENTILATED
        RoomTestBuilder.of(context).addTrapdoor(1, 2, 2, Blocks.SPRUCE_TRAPDOOR, Direction.WEST, BlockHalf.BOTTOM, false);
        MiasmaCalculator.MiasmaResult resultOpen = MiasmaCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        context.assertTrue(resultOpen.ventilationType == MiasmaCalculator.RoomVentilationType.VENTILATED,
                "Open shutter on wall must be VENTILATED");
        context.assertTrue(resultOpen.ventilationScore > 0.0,
                "Open shutter on wall must provide ventilation score > 0");

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testDoorAndTrapdoorHermeticClosedVsOpen(TestContext context) {
        BlockPos center = new BlockPos(2, 2, 2);
        RoomTestBuilder.of(context)
                .stoneRoom(1, 1, 1, 3, 3, 3)
                .setAir(0, 2, 2)
                .setAir(0, 3, 2)
                .addMoldyOakLog(3, 2, 2, 2);

        // 1. Oak door closed -> HERMETIC_SEALED
        RoomTestBuilder.of(context).addDoor(1, 2, 2, Direction.NORTH, false);
        MiasmaCalculator.MiasmaResult resultOakDoor = MiasmaCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        context.assertTrue(resultOakDoor.ventilationType == MiasmaCalculator.RoomVentilationType.HERMETIC_SEALED,
                "Closed oak door must be HERMETIC_SEALED");

        // 2. Oak door open -> VENTILATED
        RoomTestBuilder.of(context).addDoor(1, 2, 2, Direction.NORTH, true);
        MiasmaCalculator.MiasmaResult resultOakDoorOpen = MiasmaCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        context.assertTrue(resultOakDoorOpen.ventilationType == MiasmaCalculator.RoomVentilationType.VENTILATED,
                "Open oak door must be VENTILATED");

        // 3. Restore stone ceiling above and test oak trapdoor closed on wall (OPEN = true, vertical plate) -> HERMETIC_SEALED
        RoomTestBuilder.of(context)
                .set(1, 3, 2, Blocks.STONE)
                .addTrapdoor(1, 2, 2, Blocks.OAK_TRAPDOOR, Direction.WEST, BlockHalf.BOTTOM, true);
        MiasmaCalculator.MiasmaResult resultOakTrapdoor = MiasmaCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        context.assertTrue(resultOakTrapdoor.ventilationType == MiasmaCalculator.RoomVentilationType.HERMETIC_SEALED,
                "Closed oak trapdoor on wall must be HERMETIC_SEALED");

        // 4. Oak trapdoor open on wall (OPEN = false, horizontal shelf) -> VENTILATED
        RoomTestBuilder.of(context).addTrapdoor(1, 2, 2, Blocks.OAK_TRAPDOOR, Direction.WEST, BlockHalf.BOTTOM, false);
        MiasmaCalculator.MiasmaResult resultOakTrapdoorOpen = MiasmaCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        context.assertTrue(resultOakTrapdoorOpen.ventilationType == MiasmaCalculator.RoomVentilationType.VENTILATED,
                "Open oak trapdoor on wall must be VENTILATED");

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testCopperBlocksAndFences(TestContext context) {
        BlockPos center = new BlockPos(2, 2, 2);
        RoomTestBuilder.of(context)
                .stoneRoom(1, 1, 1, 3, 3, 3)
                .setAir(0, 2, 2)
                .setAir(0, 3, 2)
                .addMoldyOakLog(3, 2, 2, 2);

        // 1. Fence on wall -> VENTILATED
        RoomTestBuilder.of(context).set(1, 2, 2, Blocks.OAK_FENCE);
        MiasmaCalculator.MiasmaResult resultFence = MiasmaCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        context.assertTrue(resultFence.ventilationType == MiasmaCalculator.RoomVentilationType.VENTILATED,
                "Fence on wall must be VENTILATED");
        context.assertTrue(resultFence.ventilationScore > 0.0, "Fence must provide ventilation bonus > 0");

        // 2. Copper Grate on wall -> VENTILATED
        RoomTestBuilder.of(context).set(1, 2, 2, Blocks.COPPER_GRATE);
        MiasmaCalculator.MiasmaResult resultCopperGrate = MiasmaCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        context.assertTrue(resultCopperGrate.ventilationType == MiasmaCalculator.RoomVentilationType.VENTILATED,
                "Copper grate must be VENTILATED");

        // 3. Copper Door Closed -> HERMETIC_SEALED
        RoomTestBuilder.of(context).set(1, 2, 2, Blocks.COPPER_DOOR.getDefaultState()
                .with(DoorBlock.HALF, DoubleBlockHalf.LOWER)
                .with(DoorBlock.OPEN, false));
        MiasmaCalculator.MiasmaResult resultCopperDoorClosed = MiasmaCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        context.assertTrue(resultCopperDoorClosed.ventilationType == MiasmaCalculator.RoomVentilationType.HERMETIC_SEALED,
                "Closed copper door must be HERMETIC_SEALED");

        // 4. Copper Door Open -> VENTILATED
        RoomTestBuilder.of(context).set(1, 2, 2, Blocks.COPPER_DOOR.getDefaultState()
                .with(DoorBlock.HALF, DoubleBlockHalf.LOWER)
                .with(DoorBlock.OPEN, true));
        MiasmaCalculator.MiasmaResult resultCopperDoorOpen = MiasmaCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        context.assertTrue(resultCopperDoorOpen.ventilationType == MiasmaCalculator.RoomVentilationType.VENTILATED,
                "Open copper door must be VENTILATED");

        // 5. Copper Trapdoor on wall Closed (OPEN = true) -> HERMETIC_SEALED
        RoomTestBuilder.of(context).addTrapdoor(1, 2, 2, Blocks.COPPER_TRAPDOOR, Direction.WEST, BlockHalf.BOTTOM, true);
        MiasmaCalculator.MiasmaResult resultCopperTrapdoorClosed = MiasmaCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        context.assertTrue(resultCopperTrapdoorClosed.ventilationType == MiasmaCalculator.RoomVentilationType.HERMETIC_SEALED,
                "Closed copper trapdoor must be HERMETIC_SEALED");

        // 6. Copper Trapdoor on wall Open (OPEN = false) -> VENTILATED
        RoomTestBuilder.of(context).addTrapdoor(1, 2, 2, Blocks.COPPER_TRAPDOOR, Direction.WEST, BlockHalf.BOTTOM, false);
        MiasmaCalculator.MiasmaResult resultCopperTrapdoorOpen = MiasmaCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        context.assertTrue(resultCopperTrapdoorOpen.ventilationType == MiasmaCalculator.RoomVentilationType.VENTILATED,
                "Open copper trapdoor must be VENTILATED");

        // 7. Fence on ceiling -> VENTILATED
        RoomTestBuilder.of(context)
                .set(1, 2, 2, Blocks.STONE)
                .set(2, 3, 2, Blocks.OAK_FENCE);
        MiasmaCalculator.MiasmaResult resultFenceCeiling = MiasmaCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        context.assertTrue(resultFenceCeiling.ventilationType == MiasmaCalculator.RoomVentilationType.VENTILATED,
                "Ceiling fence must be VENTILATED");
        context.assertTrue(resultFenceCeiling.ventilationScore > 0.0, "Ceiling fence must provide bonus > 0");

        // 8. Fence on floor facing exterior below -> VENTILATED
        RoomTestBuilder.of(context)
                .set(2, 3, 2, Blocks.STONE)
                .set(2, 1, 2, Blocks.OAK_FENCE)
                .setAir(2, 0, 2)
                .setAir(1, 0, 2)
                .setAir(0, 0, 2);
        MiasmaCalculator.MiasmaResult resultFenceFloor = MiasmaCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        context.assertTrue(resultFenceFloor.ventilationType == MiasmaCalculator.RoomVentilationType.VENTILATED,
                "Floor fence facing exterior below must be VENTILATED");
        context.assertTrue(resultFenceFloor.ventilationScore > 0.0, "Floor fence must provide bonus > 0");

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testWallBlocksPlacement(TestContext context) {
        BlockPos center = new BlockPos(2, 2, 2);
        RoomTestBuilder.of(context)
                .stoneRoom(1, 1, 1, 3, 3, 3)
                .setAir(0, 2, 2)
                .setAir(0, 3, 2)
                .addMoldyOakLog(3, 2, 2, 2);

        // 1. Connected cobblestone wall on lateral wall -> HERMETIC_SEALED
        RoomTestBuilder.of(context).set(1, 2, 2, Blocks.COBBLESTONE_WALL.getDefaultState()
                .with(WallBlock.NORTH_SHAPE, WallShape.LOW)
                .with(WallBlock.SOUTH_SHAPE, WallShape.LOW));
        MiasmaCalculator.MiasmaResult resultWallConnected = MiasmaCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        context.assertTrue(resultWallConnected.ventilationType == MiasmaCalculator.RoomVentilationType.HERMETIC_SEALED,
                "Connected wall on side must be HERMETIC_SEALED");

        // 2. Single connection wall -> VENTILATED
        RoomTestBuilder.of(context).set(1, 2, 2, Blocks.COBBLESTONE_WALL.getDefaultState()
                .with(WallBlock.NORTH_SHAPE, WallShape.LOW));
        MiasmaCalculator.MiasmaResult resultWallSingle = MiasmaCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        context.assertTrue(resultWallSingle.ventilationType == MiasmaCalculator.RoomVentilationType.VENTILATED,
                "Single connected wall must be VENTILATED");
        context.assertTrue(resultWallSingle.ventilationScore > 0.0, "Single connected wall must provide bonus > 0");

        // 3. Isolated wall -> VENTILATED
        RoomTestBuilder.of(context).set(1, 2, 2, Blocks.COBBLESTONE_WALL);
        MiasmaCalculator.MiasmaResult resultWallIsolated = MiasmaCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        context.assertTrue(resultWallIsolated.ventilationType == MiasmaCalculator.RoomVentilationType.VENTILATED,
                "Isolated wall must be VENTILATED");
        context.assertTrue(resultWallIsolated.ventilationScore > 0.0, "Isolated wall must provide bonus > 0");

        // 4. Cobblestone wall on ceiling -> VENTILATED
        RoomTestBuilder.of(context)
                .set(1, 2, 2, Blocks.STONE)
                .set(2, 3, 2, Blocks.COBBLESTONE_WALL);
        MiasmaCalculator.MiasmaResult resultWallCeiling = MiasmaCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        context.assertTrue(resultWallCeiling.ventilationType == MiasmaCalculator.RoomVentilationType.VENTILATED,
                "Ceiling wall must be VENTILATED");
        context.assertTrue(resultWallCeiling.ventilationScore > 0.0, "Ceiling wall must provide bonus > 0");

        // 5. Cobblestone wall on floor facing exterior below -> VENTILATED
        RoomTestBuilder.of(context)
                .set(2, 3, 2, Blocks.STONE)
                .set(2, 1, 2, Blocks.COBBLESTONE_WALL)
                .setAir(2, 0, 2)
                .setAir(1, 0, 2)
                .setAir(0, 0, 2);
        MiasmaCalculator.MiasmaResult resultWallFloor = MiasmaCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        context.assertTrue(resultWallFloor.ventilationType == MiasmaCalculator.RoomVentilationType.VENTILATED,
                "Floor wall facing exterior below must be VENTILATED");
        context.assertTrue(resultWallFloor.ventilationScore > 0.0, "Floor wall must provide bonus > 0");

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testDecorativeBlocksDoNotObstruct(TestContext context) {
        BlockPos center = new BlockPos(2, 2, 2);
        RoomTestBuilder.of(context)
                .fill(1, 1, 0, 3, 3, 3, Blocks.STONE.getDefaultState())
                .setAir(2, 2, 2)
                .set(2, 2, 1, Blocks.CHAIN.getDefaultState().with(ChainBlock.AXIS, Direction.Axis.Y))
                .addMoldyOakLog(3, 2, 2, 2);

        MiasmaCalculator.MiasmaResult result = MiasmaCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));

        context.assertTrue(result.volume >= 2, "Chains/decorations must not obstruct air volume (volume >= 2)");
        context.assertTrue(result.toxicScore > 0.0, "Toxicity must propagate through decorative blocks");

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testIronBarsVentilation(TestContext context) {
        BlockPos center = new BlockPos(2, 2, 2);
        RoomTestBuilder.of(context)
                .stoneRoom(1, 1, 1, 3, 3, 3)
                .setAir(0, 2, 2)
                .setAir(0, 3, 2)
                .addMoldyOakLog(3, 2, 2, 2)
                .set(1, 2, 2, Blocks.IRON_BARS);

        MiasmaCalculator.MiasmaResult result = MiasmaCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));

        context.assertTrue(result.ventilationType == MiasmaCalculator.RoomVentilationType.VENTILATED,
                "Iron bars facing exterior must be VENTILATED");
        context.assertTrue(result.ventilationScore > 0.0, "Iron bars must provide bonus > 0");

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testFenceGateClosedIsVentilatedAndOpenIsClean(TestContext context) {
        BlockPos center = new BlockPos(2, 2, 2);
        RoomTestBuilder.of(context)
                .stoneRoom(1, 1, 1, 3, 3, 3)
                .setAir(0, 2, 2)
                .setAir(0, 3, 2)
                .addMoldyOakLog(3, 2, 2, 2);

        // 1. Fence gate closed -> VENTILATED
        RoomTestBuilder.of(context).set(1, 2, 2, Blocks.OAK_FENCE_GATE.getDefaultState()
                .with(FenceGateBlock.FACING, Direction.WEST)
                .with(FenceGateBlock.OPEN, false));
        MiasmaCalculator.MiasmaResult resultClosed = MiasmaCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        context.assertTrue(resultClosed.ventilationType == MiasmaCalculator.RoomVentilationType.VENTILATED,
                "Closed fence gate must be VENTILATED");
        context.assertTrue(resultClosed.ventilationScore > 0.0,
                "Closed fence gate must provide ventilationScore > 0");

        // 2. Fence gate open -> VENTILATED
        RoomTestBuilder.of(context).set(1, 2, 2, Blocks.OAK_FENCE_GATE.getDefaultState()
                .with(FenceGateBlock.FACING, Direction.WEST)
                .with(FenceGateBlock.OPEN, true));
        MiasmaCalculator.MiasmaResult resultOpen = MiasmaCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        context.assertTrue(resultOpen.ventilationType == MiasmaCalculator.RoomVentilationType.VENTILATED,
                "Open fence gate must be VENTILATED");
        context.assertTrue(resultOpen.ventilationScore > 0.0,
                "Open fence gate must provide ventilationScore > 0");

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testTrapdoorFloorAndCeilingOrientations(TestContext context) {
        BlockPos center = new BlockPos(2, 2, 2);
        RoomTestBuilder.of(context)
                .stoneRoom(1, 1, 1, 3, 3, 3)
                .addMoldyOakLog(3, 2, 2, 2);

        // 1. Ceiling trapdoor closed -> HERMETIC_SEALED
        RoomTestBuilder.of(context).addTrapdoor(2, 3, 2, Blocks.OAK_TRAPDOOR, BlockHalf.BOTTOM, false);
        MiasmaCalculator.MiasmaResult resultCeilingClosed = MiasmaCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        context.assertTrue(resultCeilingClosed.ventilationType == MiasmaCalculator.RoomVentilationType.HERMETIC_SEALED,
                "Ceiling trapdoor closed must be HERMETIC_SEALED");

        // 2. Ceiling trapdoor open -> VENTILATED
        RoomTestBuilder.of(context).addTrapdoor(2, 3, 2, Blocks.OAK_TRAPDOOR, BlockHalf.BOTTOM, true);
        MiasmaCalculator.MiasmaResult resultCeilingOpen = MiasmaCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        context.assertTrue(resultCeilingOpen.ventilationType == MiasmaCalculator.RoomVentilationType.VENTILATED,
                "Ceiling trapdoor open must be VENTILATED");

        // 3. Floor trapdoor closed facing exterior below -> HERMETIC_SEALED
        RoomTestBuilder.of(context)
                .set(2, 3, 2, Blocks.STONE)
                .setAir(2, 0, 2)
                .setAir(1, 0, 2)
                .setAir(0, 0, 2)
                .setAir(0, 1, 2)
                .setAir(0, 2, 2)
                .setAir(0, 3, 2)
                .addTrapdoor(2, 1, 2, Blocks.OAK_TRAPDOOR, BlockHalf.TOP, false);
        MiasmaCalculator.MiasmaResult resultFloorClosed = MiasmaCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        context.assertTrue(resultFloorClosed.ventilationType == MiasmaCalculator.RoomVentilationType.HERMETIC_SEALED,
                "Floor trapdoor closed must be HERMETIC_SEALED");

        // 4. Floor trapdoor open -> VENTILATED
        RoomTestBuilder.of(context).addTrapdoor(2, 1, 2, Blocks.OAK_TRAPDOOR, BlockHalf.TOP, true);
        MiasmaCalculator.MiasmaResult resultFloorOpen = MiasmaCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        context.assertTrue(resultFloorOpen.ventilationType == MiasmaCalculator.RoomVentilationType.VENTILATED,
                "Floor trapdoor open must be VENTILATED");

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testCeilingHoleColumnVentilationOnlyAtAperture(TestContext context) {
        RoomTestBuilder.of(context)
                .stoneRoom(1, 1, 1, 5, 4, 5)
                .setAir(3, 4, 3)
                .setAir(3, 5, 3);

        BlockPos holePos = new BlockPos(3, 4, 3);
        BlockPos floorUnderHole = new BlockPos(3, 2, 3);
        BlockPos midUnderHole = new BlockPos(3, 3, 3);
        BlockPos skyAboveHole = new BlockPos(3, 5, 3);

        context.assertTrue(MiasmaCalculator.isCoveredByCeiling(context.getWorld(), context.getAbsolutePos(floorUnderHole)),
                "Floor under hole must be covered by ceiling");
        context.assertTrue(MiasmaCalculator.isCoveredByCeiling(context.getWorld(), context.getAbsolutePos(midUnderHole)),
                "Mid column under hole must be covered by ceiling");
        context.assertFalse(MiasmaCalculator.isCoveredByCeiling(context.getWorld(), context.getAbsolutePos(holePos)),
                "Hole itself must not be covered by ceiling");
        context.assertFalse(MiasmaCalculator.isCoveredByCeiling(context.getWorld(), context.getAbsolutePos(skyAboveHole)),
                "Air above roof must not be covered by ceiling");

        BlockPos floorTargetPos = new BlockPos(2, 2, 3);
        BlockState log = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState();
        RoomTestBuilder.of(context).set(2, 2, 3, log);

        MiasmaCalculator.BlockAirEvaluation floorEval = MiasmaCalculator.calculateBlockAirEvaluation(
                context.getWorld(), context.getAbsolutePos(floorTargetPos), log);
        context.assertTrue(floorEval.distanceToVentilation() > 0,
                "Floor target must have distanceToVentilation > 0");

        MiasmaCalculator.MiasmaResult holeResult = MiasmaCalculator.calculateMiasma(
                context.getWorld(), context.getAbsolutePos(holePos));
        context.assertTrue(holeResult.openAir, "Ceiling hole block must have openAir = true");

        MiasmaCalculator.MiasmaResult roomResult = MiasmaCalculator.calculateMiasma(
                context.getWorld(), context.getAbsolutePos(new BlockPos(2, 2, 2)));
        context.assertTrue(roomResult.ventilationType == MiasmaCalculator.RoomVentilationType.VENTILATED,
                "Room with ceiling hole must be VENTILATED");

        context.complete();
    }
}
