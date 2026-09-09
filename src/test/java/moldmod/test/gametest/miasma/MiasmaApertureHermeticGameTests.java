package moldmod.test.gametest.miasma;

import moldmod.block.ModBlocks;
import moldmod.atmosphere.RoomAtmosphereCalculator;
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
        RoomAtmosphereCalculator.MiasmaResult clean = new RoomAtmosphereCalculator.MiasmaResult(0.0, 0.0, false, 10,
                Collections.emptySet());
        context.assertTrue(clean.level == RoomAtmosphereCalculator.AirToxicityLevel.CLEAN,
                "Miasma 0 must be CLEAN, got: " + clean.level);

        // Open Air -> always CLEAN
        RoomAtmosphereCalculator.MiasmaResult openAir = new RoomAtmosphereCalculator.MiasmaResult(50.0, 0.0, true, 10,
                Collections.emptySet());
        context.assertTrue(openAir.level == RoomAtmosphereCalculator.AirToxicityLevel.CLEAN,
                "Open Air must be CLEAN, got: " + openAir.level);
        context.assertTrue(openAir.netMiasma == 0.0,
                "Open Air netMiasma must be 0.0, got: " + openAir.netMiasma);
        context.assertTrue(openAir.ventilationType == RoomAtmosphereCalculator.RoomVentilationType.CLEAN_OPEN_AIR,
                "Open Air ventilationType must be CLEAN_OPEN_AIR, got: " + openAir.ventilationType);

        // Volume >= MAX_AIR_VOLUME -> UNCONFINED_CAVERN
        RoomAtmosphereCalculator.MiasmaResult hugeRoom = new RoomAtmosphereCalculator.MiasmaResult(5.0, 0.0, false, 2048,
                Collections.emptySet());
        context.assertTrue(hugeRoom.level == RoomAtmosphereCalculator.AirToxicityLevel.CLEAN,
                "Huge cavern with low mold must be CLEAN by dilution, got: " + hugeRoom.level);
        context.assertTrue(hugeRoom.ventilationType == RoomAtmosphereCalculator.RoomVentilationType.UNCONFINED_CAVERN,
                "Huge room must be UNCONFINED_CAVERN, got: " + hugeRoom.ventilationType);

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testMiasmaDensityThresholds(TestContext context) {
        // Small dense room (volume 5, toxicScore = 22.5) -> LETHAL_POISON
        RoomAtmosphereCalculator.MiasmaResult smallDenseRoom = new RoomAtmosphereCalculator.MiasmaResult(22.5, 0.0, false, 5,
                Collections.emptySet());
        context.assertTrue(smallDenseRoom.level == RoomAtmosphereCalculator.AirToxicityLevel.LETHAL_POISON,
                "Small dense room must be LETHAL_POISON, got: " + smallDenseRoom.level);

        // Medium room (volume 20, toxicScore = 6.0) -> MODERATE_HUNGER
        RoomAtmosphereCalculator.MiasmaResult mediumRoom = new RoomAtmosphereCalculator.MiasmaResult(6.0, 0.0, false, 20,
                Collections.emptySet());
        context.assertTrue(mediumRoom.level == RoomAtmosphereCalculator.AirToxicityLevel.MODERATE_HUNGER,
                "Medium room must be MODERATE_HUNGER, got: " + mediumRoom.level);

        // Large room (volume 100, toxicScore = 4.5) -> WARNING
        RoomAtmosphereCalculator.MiasmaResult largeRoom = new RoomAtmosphereCalculator.MiasmaResult(4.5, 0.0, false, 100,
                Collections.emptySet());
        context.assertTrue(largeRoom.level == RoomAtmosphereCalculator.AirToxicityLevel.WARNING,
                "Large room must be WARNING, got: " + largeRoom.level);

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testDensityScalingWithVolume(TestContext context) {
        RoomAtmosphereCalculator.MiasmaResult small = new RoomAtmosphereCalculator.MiasmaResult(12.0, 0.0, false, 6,
                Collections.emptySet());
        RoomAtmosphereCalculator.MiasmaResult large = new RoomAtmosphereCalculator.MiasmaResult(12.0, 0.0, false, 60,
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
        RoomAtmosphereCalculator.MiasmaResult rClean = new RoomAtmosphereCalculator.MiasmaResult(null, 0.0, 0.0, false, 24, Collections.emptySet(), new BlockPos(101, 1, 101));
        context.assertTrue(rClean.level == RoomAtmosphereCalculator.AirToxicityLevel.CLEAN, "M=0 must be CLEAN");

        // 2. WARNING: M=4.5, D = 4.5 / 100 = 0.045 >= 0.0417 -> WARNING
        RoomAtmosphereCalculator.MiasmaResult rWarning = new RoomAtmosphereCalculator.MiasmaResult(null, 4.5, 0.0, false, 100, Collections.emptySet(), new BlockPos(102, 2, 102));
        context.assertTrue(rWarning.level == RoomAtmosphereCalculator.AirToxicityLevel.WARNING, "M=4.5/100 must be WARNING");

        // 3. MODERATE_HUNGER: M=6.0, D = 6.0 / 50 = 0.12 -> MODERATE_HUNGER
        RoomAtmosphereCalculator.MiasmaResult rHunger = new RoomAtmosphereCalculator.MiasmaResult(null, 6.0, 0.0, false, 50, Collections.emptySet(), new BlockPos(103, 3, 103));
        context.assertTrue(rHunger.level == RoomAtmosphereCalculator.AirToxicityLevel.MODERATE_HUNGER, "M=6.0 with D>=0.0417 must be MODERATE_HUNGER");

        // 4. LETHAL_POISON: M=18.0, D = 18.0 / 50 = 0.36 -> LETHAL_POISON
        RoomAtmosphereCalculator.MiasmaResult rPoison = new RoomAtmosphereCalculator.MiasmaResult(null, 18.0, 0.0, false, 50, Collections.emptySet(), new BlockPos(104, 4, 104));
        context.assertTrue(rPoison.level == RoomAtmosphereCalculator.AirToxicityLevel.LETHAL_POISON, "M=18.0 with D>=0.0833 must be LETHAL_POISON");

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testOpenAirDissipation(TestContext context) {
        BlockPos center = new BlockPos(2, 2, 2);
        BlockPos absCenter = context.getAbsolutePos(center);
        int topY = context.getWorld().getTopY(Heightmap.Type.MOTION_BLOCKING, absCenter.getX(), absCenter.getZ());
        BlockPos skyPos = new BlockPos(absCenter.getX(), Math.max(topY, absCenter.getY()), absCenter.getZ());

        RoomAtmosphereCalculator.MiasmaResult result = RoomAtmosphereCalculator.calculateMiasma(context.getWorld(), skyPos);

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

        RoomAtmosphereCalculator.MiasmaResult result = RoomAtmosphereCalculator.calculateMiasma(context.getWorld(),
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

        RoomAtmosphereCalculator.MiasmaResult result = RoomAtmosphereCalculator.calculateMiasma(context.getWorld(),
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

        RoomAtmosphereCalculator.MiasmaResult result = RoomAtmosphereCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));

        context.assertTrue(result.ventilationType == RoomAtmosphereCalculator.RoomVentilationType.VENTILATED,
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
        RoomAtmosphereCalculator.MiasmaResult resultBackToRoom = RoomAtmosphereCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        context.assertTrue(resultBackToRoom.ventilationType == RoomAtmosphereCalculator.RoomVentilationType.HERMETIC_SEALED,
                "Stairs with back facing room must be HERMETIC_SEALED");

        // 2. Solid back facing outside (FACING = EAST) -> HERMETIC_SEALED
        RoomTestBuilder.of(context).addStairs(1, 2, 2, Blocks.OAK_STAIRS, Direction.EAST, BlockHalf.BOTTOM);
        RoomAtmosphereCalculator.MiasmaResult resultBackToOutside = RoomAtmosphereCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        context.assertTrue(resultBackToOutside.ventilationType == RoomAtmosphereCalculator.RoomVentilationType.HERMETIC_SEALED,
                "Stairs with back facing outside must be HERMETIC_SEALED");

        // 3. Sideways stairs (FACING = SOUTH) -> VENTILATED
        RoomTestBuilder.of(context).addStairs(1, 2, 2, Blocks.OAK_STAIRS, Direction.SOUTH, BlockHalf.BOTTOM);
        RoomAtmosphereCalculator.MiasmaResult resultSideways = RoomAtmosphereCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        context.assertTrue(resultSideways.ventilationType == RoomAtmosphereCalculator.RoomVentilationType.VENTILATED,
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
        RoomAtmosphereCalculator.MiasmaResult resultClosed = RoomAtmosphereCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(room1));
        context.assertTrue(resultClosed.volume == 1, "Closed door room volume must be 1, got: " + resultClosed.volume);

        // 2. Open door: volume > 1
        RoomTestBuilder.of(context).set(2, 2, 2, Blocks.OAK_DOOR.getDefaultState()
                .with(DoorBlock.HALF, DoubleBlockHalf.LOWER)
                .with(DoorBlock.OPEN, true));

        RoomAtmosphereCalculator.MiasmaResult resultOpen = RoomAtmosphereCalculator.calculateMiasma(context.getWorld(),
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
        RoomAtmosphereCalculator.MiasmaResult resultClosed = RoomAtmosphereCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        context.assertFalse(resultClosed.openAir, "Closed ceiling trapdoor must not be openAir");

        // 2. Open trapdoor: VENTILATED, score > 0
        RoomTestBuilder.of(context).addTrapdoor(2, 3, 2, Blocks.OAK_TRAPDOOR, BlockHalf.BOTTOM, true);
        RoomAtmosphereCalculator.MiasmaResult resultOpen = RoomAtmosphereCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        context.assertTrue(resultOpen.ventilationType == RoomAtmosphereCalculator.RoomVentilationType.VENTILATED,
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
        RoomAtmosphereCalculator.MiasmaResult resultClosed = RoomAtmosphereCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(room1));
        context.assertTrue(resultClosed.volume == 2, "Closed door room volume must be 2, got: " + resultClosed.volume);

        // 2. Open door (both halves): volume >= 4
        RoomTestBuilder.of(context).addDoor(2, 2, 2, Direction.NORTH, true);
        RoomAtmosphereCalculator.MiasmaResult resultOpen = RoomAtmosphereCalculator.calculateMiasma(context.getWorld(),
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

        RoomAtmosphereCalculator.MiasmaResult resultSpruce = RoomAtmosphereCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        context.assertTrue(resultSpruce.ventilationType == RoomAtmosphereCalculator.RoomVentilationType.HERMETIC_SEALED,
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

        RoomAtmosphereCalculator.MiasmaResult resultSpruce = RoomAtmosphereCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        context.assertTrue(resultSpruce.ventilationType == RoomAtmosphereCalculator.RoomVentilationType.HERMETIC_SEALED,
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
        RoomAtmosphereCalculator.MiasmaResult resultClosed = RoomAtmosphereCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        context.assertTrue(resultClosed.ventilationType == RoomAtmosphereCalculator.RoomVentilationType.HERMETIC_SEALED,
                "Closed shutter on wall must be HERMETIC_SEALED");

        // 2. Shutter open on wall (in-game OPEN = false, horizontal shelf) -> VENTILATED
        RoomTestBuilder.of(context).addTrapdoor(1, 2, 2, Blocks.SPRUCE_TRAPDOOR, Direction.WEST, BlockHalf.BOTTOM, false);
        RoomAtmosphereCalculator.MiasmaResult resultOpen = RoomAtmosphereCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        context.assertTrue(resultOpen.ventilationType == RoomAtmosphereCalculator.RoomVentilationType.VENTILATED,
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
        RoomAtmosphereCalculator.MiasmaResult resultOakDoor = RoomAtmosphereCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        context.assertTrue(resultOakDoor.ventilationType == RoomAtmosphereCalculator.RoomVentilationType.HERMETIC_SEALED,
                "Closed oak door must be HERMETIC_SEALED");

        // 2. Oak door open -> VENTILATED
        RoomTestBuilder.of(context).addDoor(1, 2, 2, Direction.NORTH, true);
        RoomAtmosphereCalculator.MiasmaResult resultOakDoorOpen = RoomAtmosphereCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        context.assertTrue(resultOakDoorOpen.ventilationType == RoomAtmosphereCalculator.RoomVentilationType.VENTILATED,
                "Open oak door must be VENTILATED");

        // 3. Restore stone ceiling above and test oak trapdoor closed on wall (OPEN = true, vertical plate) -> HERMETIC_SEALED
        RoomTestBuilder.of(context)
                .set(1, 3, 2, Blocks.STONE)
                .addTrapdoor(1, 2, 2, Blocks.OAK_TRAPDOOR, Direction.WEST, BlockHalf.BOTTOM, true);
        RoomAtmosphereCalculator.MiasmaResult resultOakTrapdoor = RoomAtmosphereCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        context.assertTrue(resultOakTrapdoor.ventilationType == RoomAtmosphereCalculator.RoomVentilationType.HERMETIC_SEALED,
                "Closed oak trapdoor on wall must be HERMETIC_SEALED");

        // 4. Oak trapdoor open on wall (OPEN = false, horizontal shelf) -> VENTILATED
        RoomTestBuilder.of(context).addTrapdoor(1, 2, 2, Blocks.OAK_TRAPDOOR, Direction.WEST, BlockHalf.BOTTOM, false);
        RoomAtmosphereCalculator.MiasmaResult resultOakTrapdoorOpen = RoomAtmosphereCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        context.assertTrue(resultOakTrapdoorOpen.ventilationType == RoomAtmosphereCalculator.RoomVentilationType.VENTILATED,
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
        RoomAtmosphereCalculator.MiasmaResult resultFence = RoomAtmosphereCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        context.assertTrue(resultFence.ventilationType == RoomAtmosphereCalculator.RoomVentilationType.VENTILATED,
                "Fence on wall must be VENTILATED");
        context.assertTrue(resultFence.ventilationScore > 0.0, "Fence must provide ventilation bonus > 0");

        // 2. Copper Grate on wall -> VENTILATED
        RoomTestBuilder.of(context).set(1, 2, 2, Blocks.COPPER_GRATE);
        RoomAtmosphereCalculator.MiasmaResult resultCopperGrate = RoomAtmosphereCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        context.assertTrue(resultCopperGrate.ventilationType == RoomAtmosphereCalculator.RoomVentilationType.VENTILATED,
                "Copper grate must be VENTILATED");

        // 3. Copper Door Closed -> HERMETIC_SEALED
        RoomTestBuilder.of(context).set(1, 2, 2, Blocks.COPPER_DOOR.getDefaultState()
                .with(DoorBlock.HALF, DoubleBlockHalf.LOWER)
                .with(DoorBlock.OPEN, false));
        RoomAtmosphereCalculator.MiasmaResult resultCopperDoorClosed = RoomAtmosphereCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        context.assertTrue(resultCopperDoorClosed.ventilationType == RoomAtmosphereCalculator.RoomVentilationType.HERMETIC_SEALED,
                "Closed copper door must be HERMETIC_SEALED");

        // 4. Copper Door Open -> VENTILATED
        RoomTestBuilder.of(context).set(1, 2, 2, Blocks.COPPER_DOOR.getDefaultState()
                .with(DoorBlock.HALF, DoubleBlockHalf.LOWER)
                .with(DoorBlock.OPEN, true));
        RoomAtmosphereCalculator.MiasmaResult resultCopperDoorOpen = RoomAtmosphereCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        context.assertTrue(resultCopperDoorOpen.ventilationType == RoomAtmosphereCalculator.RoomVentilationType.VENTILATED,
                "Open copper door must be VENTILATED");

        // 5. Copper Trapdoor on wall Closed (OPEN = true) -> HERMETIC_SEALED
        RoomTestBuilder.of(context).addTrapdoor(1, 2, 2, Blocks.COPPER_TRAPDOOR, Direction.WEST, BlockHalf.BOTTOM, true);
        RoomAtmosphereCalculator.MiasmaResult resultCopperTrapdoorClosed = RoomAtmosphereCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        context.assertTrue(resultCopperTrapdoorClosed.ventilationType == RoomAtmosphereCalculator.RoomVentilationType.HERMETIC_SEALED,
                "Closed copper trapdoor must be HERMETIC_SEALED");

        // 6. Copper Trapdoor on wall Open (OPEN = false) -> VENTILATED
        RoomTestBuilder.of(context).addTrapdoor(1, 2, 2, Blocks.COPPER_TRAPDOOR, Direction.WEST, BlockHalf.BOTTOM, false);
        RoomAtmosphereCalculator.MiasmaResult resultCopperTrapdoorOpen = RoomAtmosphereCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        context.assertTrue(resultCopperTrapdoorOpen.ventilationType == RoomAtmosphereCalculator.RoomVentilationType.VENTILATED,
                "Open copper trapdoor must be VENTILATED");

        // 7. Fence on ceiling -> VENTILATED
        RoomTestBuilder.of(context)
                .set(1, 2, 2, Blocks.STONE)
                .set(2, 3, 2, Blocks.OAK_FENCE);
        RoomAtmosphereCalculator.MiasmaResult resultFenceCeiling = RoomAtmosphereCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        context.assertTrue(resultFenceCeiling.ventilationType == RoomAtmosphereCalculator.RoomVentilationType.VENTILATED,
                "Ceiling fence must be VENTILATED");
        context.assertTrue(resultFenceCeiling.ventilationScore > 0.0, "Ceiling fence must provide bonus > 0");

        // 8. Fence on floor facing exterior below -> VENTILATED
        RoomTestBuilder.of(context)
                .set(2, 3, 2, Blocks.STONE)
                .set(2, 1, 2, Blocks.OAK_FENCE)
                .setAir(2, 0, 2)
                .setAir(1, 0, 2)
                .setAir(0, 0, 2);
        RoomAtmosphereCalculator.MiasmaResult resultFenceFloor = RoomAtmosphereCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        context.assertTrue(resultFenceFloor.ventilationType == RoomAtmosphereCalculator.RoomVentilationType.VENTILATED,
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
        RoomAtmosphereCalculator.MiasmaResult resultWallConnected = RoomAtmosphereCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        context.assertTrue(resultWallConnected.ventilationType == RoomAtmosphereCalculator.RoomVentilationType.HERMETIC_SEALED,
                "Connected wall on side must be HERMETIC_SEALED");

        // 2. Single connection wall -> VENTILATED
        RoomTestBuilder.of(context).set(1, 2, 2, Blocks.COBBLESTONE_WALL.getDefaultState()
                .with(WallBlock.NORTH_SHAPE, WallShape.LOW));
        RoomAtmosphereCalculator.MiasmaResult resultWallSingle = RoomAtmosphereCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        context.assertTrue(resultWallSingle.ventilationType == RoomAtmosphereCalculator.RoomVentilationType.VENTILATED,
                "Single connected wall must be VENTILATED");
        context.assertTrue(resultWallSingle.ventilationScore > 0.0, "Single connected wall must provide bonus > 0");

        // 3. Isolated wall -> VENTILATED
        RoomTestBuilder.of(context).set(1, 2, 2, Blocks.COBBLESTONE_WALL);
        RoomAtmosphereCalculator.MiasmaResult resultWallIsolated = RoomAtmosphereCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        context.assertTrue(resultWallIsolated.ventilationType == RoomAtmosphereCalculator.RoomVentilationType.VENTILATED,
                "Isolated wall must be VENTILATED");
        context.assertTrue(resultWallIsolated.ventilationScore > 0.0, "Isolated wall must provide bonus > 0");

        // 4. Cobblestone wall on ceiling -> VENTILATED
        RoomTestBuilder.of(context)
                .set(1, 2, 2, Blocks.STONE)
                .set(2, 3, 2, Blocks.COBBLESTONE_WALL);
        RoomAtmosphereCalculator.MiasmaResult resultWallCeiling = RoomAtmosphereCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        context.assertTrue(resultWallCeiling.ventilationType == RoomAtmosphereCalculator.RoomVentilationType.VENTILATED,
                "Ceiling wall must be VENTILATED");
        context.assertTrue(resultWallCeiling.ventilationScore > 0.0, "Ceiling wall must provide bonus > 0");

        // 5. Cobblestone wall on floor facing exterior below -> VENTILATED
        RoomTestBuilder.of(context)
                .set(2, 3, 2, Blocks.STONE)
                .set(2, 1, 2, Blocks.COBBLESTONE_WALL)
                .setAir(2, 0, 2)
                .setAir(1, 0, 2)
                .setAir(0, 0, 2);
        RoomAtmosphereCalculator.MiasmaResult resultWallFloor = RoomAtmosphereCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        context.assertTrue(resultWallFloor.ventilationType == RoomAtmosphereCalculator.RoomVentilationType.VENTILATED,
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

        RoomAtmosphereCalculator.MiasmaResult result = RoomAtmosphereCalculator.calculateMiasma(context.getWorld(),
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

        RoomAtmosphereCalculator.MiasmaResult result = RoomAtmosphereCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));

        context.assertTrue(result.ventilationType == RoomAtmosphereCalculator.RoomVentilationType.VENTILATED,
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
        RoomAtmosphereCalculator.MiasmaResult resultClosed = RoomAtmosphereCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        context.assertTrue(resultClosed.ventilationType == RoomAtmosphereCalculator.RoomVentilationType.VENTILATED,
                "Closed fence gate must be VENTILATED");
        context.assertTrue(resultClosed.ventilationScore > 0.0,
                "Closed fence gate must provide ventilationScore > 0");

        // 2. Fence gate open -> VENTILATED
        RoomTestBuilder.of(context).set(1, 2, 2, Blocks.OAK_FENCE_GATE.getDefaultState()
                .with(FenceGateBlock.FACING, Direction.WEST)
                .with(FenceGateBlock.OPEN, true));
        RoomAtmosphereCalculator.MiasmaResult resultOpen = RoomAtmosphereCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        context.assertTrue(resultOpen.ventilationType == RoomAtmosphereCalculator.RoomVentilationType.VENTILATED,
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
        RoomAtmosphereCalculator.MiasmaResult resultCeilingClosed = RoomAtmosphereCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        context.assertTrue(resultCeilingClosed.ventilationType == RoomAtmosphereCalculator.RoomVentilationType.HERMETIC_SEALED,
                "Ceiling trapdoor closed must be HERMETIC_SEALED");

        // 2. Ceiling trapdoor open -> VENTILATED
        RoomTestBuilder.of(context).addTrapdoor(2, 3, 2, Blocks.OAK_TRAPDOOR, BlockHalf.BOTTOM, true);
        RoomAtmosphereCalculator.MiasmaResult resultCeilingOpen = RoomAtmosphereCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        context.assertTrue(resultCeilingOpen.ventilationType == RoomAtmosphereCalculator.RoomVentilationType.VENTILATED,
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
        RoomAtmosphereCalculator.MiasmaResult resultFloorClosed = RoomAtmosphereCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        context.assertTrue(resultFloorClosed.ventilationType == RoomAtmosphereCalculator.RoomVentilationType.HERMETIC_SEALED,
                "Floor trapdoor closed must be HERMETIC_SEALED");

        // 4. Floor trapdoor open -> VENTILATED
        RoomTestBuilder.of(context).addTrapdoor(2, 1, 2, Blocks.OAK_TRAPDOOR, BlockHalf.TOP, true);
        RoomAtmosphereCalculator.MiasmaResult resultFloorOpen = RoomAtmosphereCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(center));
        context.assertTrue(resultFloorOpen.ventilationType == RoomAtmosphereCalculator.RoomVentilationType.VENTILATED,
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

        context.assertTrue(RoomAtmosphereCalculator.isCoveredByCeiling(context.getWorld(), context.getAbsolutePos(floorUnderHole)),
                "Floor under hole must be covered by ceiling");
        context.assertTrue(RoomAtmosphereCalculator.isCoveredByCeiling(context.getWorld(), context.getAbsolutePos(midUnderHole)),
                "Mid column under hole must be covered by ceiling");
        context.assertFalse(RoomAtmosphereCalculator.isCoveredByCeiling(context.getWorld(), context.getAbsolutePos(holePos)),
                "Hole itself must not be covered by ceiling");
        context.assertFalse(RoomAtmosphereCalculator.isCoveredByCeiling(context.getWorld(), context.getAbsolutePos(skyAboveHole)),
                "Air above roof must not be covered by ceiling");

        BlockPos floorTargetPos = new BlockPos(2, 2, 3);
        BlockState log = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState();
        RoomTestBuilder.of(context).set(2, 2, 3, log);

        RoomAtmosphereCalculator.BlockAirEvaluation floorEval = RoomAtmosphereCalculator.calculateBlockAirEvaluation(
                context.getWorld(), context.getAbsolutePos(floorTargetPos), log);
        context.assertTrue(floorEval.distanceToVentilation() > 0,
                "Floor target must have distanceToVentilation > 0");

        RoomAtmosphereCalculator.MiasmaResult holeResult = RoomAtmosphereCalculator.calculateMiasma(
                context.getWorld(), context.getAbsolutePos(holePos));
        context.assertTrue(holeResult.openAir, "Ceiling hole block must have openAir = true");

        RoomAtmosphereCalculator.MiasmaResult roomResult = RoomAtmosphereCalculator.calculateMiasma(
                context.getWorld(), context.getAbsolutePos(new BlockPos(2, 2, 2)));
        context.assertTrue(roomResult.ventilationType == RoomAtmosphereCalculator.RoomVentilationType.VENTILATED,
                "Room with ceiling hole must be VENTILATED");

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testUnconfinedTunnelBeyondRadiusIsCavern(TestContext context) {
        // Build a long 1x1 tunnel encased in stone along X from 1 to 20
        for (int x = 1; x <= 20; x++) {
            context.setBlockState(new BlockPos(x, 1, 2), Blocks.STONE.getDefaultState());
            context.setBlockState(new BlockPos(x, 3, 2), Blocks.STONE.getDefaultState());
            context.setBlockState(new BlockPos(x, 2, 1), Blocks.STONE.getDefaultState());
            context.setBlockState(new BlockPos(x, 2, 3), Blocks.STONE.getDefaultState());
            context.setBlockState(new BlockPos(x, 2, 2), Blocks.AIR.getDefaultState());
        }
        // Cap the start at x=0
        context.setBlockState(new BlockPos(0, 2, 2), Blocks.STONE.getDefaultState());
        // Leave x=21 open air under ceiling
        context.setBlockState(new BlockPos(21, 3, 2), Blocks.STONE.getDefaultState());
        context.setBlockState(new BlockPos(21, 2, 2), Blocks.AIR.getDefaultState());

        BlockPos eyePos = new BlockPos(1, 2, 2);
        RoomAtmosphereCalculator.MiasmaResult result = RoomAtmosphereCalculator.calculateMiasma(
                context.getWorld(), context.getAbsolutePos(eyePos));

        context.assertTrue(result.ventilationType == RoomAtmosphereCalculator.RoomVentilationType.UNCONFINED_CAVERN,
                "Long tunnel hitting radius boundary with open air must be UNCONFINED_CAVERN, got: " + result.ventilationType);

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testVerticalShaftWithinRadiusIsVentilated(TestContext context) {
        // Pozzo verticale profondo 5 blocchi (entro il raggio di 16 e dentro la struttura)
        // Fondo a y=1, colonna d'aria fino a y=5, cielo a y=6
        RoomTestBuilder.of(context)
                .set(2, 0, 2, Blocks.STONE)
                .addVerticalChimney(2, 2, 1, 5)
                .clearOpenAirColumn(2, 2, 6, 8);

        BlockPos bottomPos = new BlockPos(2, 1, 2);
        RoomAtmosphereCalculator.MiasmaResult result = RoomAtmosphereCalculator.calculateMiasma(
                context.getWorld(), context.getAbsolutePos(bottomPos));

        context.assertTrue(result.ventilationType == RoomAtmosphereCalculator.RoomVentilationType.VENTILATED,
                "Shaft within radius 16 must be VENTILATED, got: " + result.ventilationType);
        context.assertTrue(result.ventilationScore > 0.0,
                "Ventilation score must be > 0 in shaft reaching sky within radius");

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testVerticalShaftBeyondRadiusIsUnconfined(TestContext context) {
        // Pozzo verticale profondo 20 blocchi (oltre il raggio di 16)
        // Fondo a y=1, colonna d'aria fino a y=20, cielo a y=21
        RoomTestBuilder.of(context)
                .set(2, 0, 2, Blocks.STONE)
                .addVerticalChimney(2, 2, 1, 20)
                .clearOpenAirColumn(2, 2, 21, 46);

        BlockPos bottomPos = new BlockPos(2, 1, 2);
        RoomAtmosphereCalculator.MiasmaResult result = RoomAtmosphereCalculator.calculateMiasma(
                context.getWorld(), context.getAbsolutePos(bottomPos));

        context.assertTrue(result.ventilationType == RoomAtmosphereCalculator.RoomVentilationType.UNCONFINED_CAVERN,
                "Shaft deeper than radius 16 must be UNCONFINED_CAVERN, got: " + result.ventilationType);

        context.complete();
    }
}
