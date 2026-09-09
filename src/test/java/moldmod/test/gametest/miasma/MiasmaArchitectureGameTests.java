package moldmod.test.gametest.miasma;

import me.shedaniel.autoconfig.AutoConfig;
import moldmod.config.ModConfig;
import moldmod.atmosphere.RoomAtmosphereCalculator;
import moldmod.atmosphere.RoomAtmosphereCalculator.RoomVentilationType;
import moldmod.risk.MoldRiskCalculator;
import moldmod.test.helper.RoomTestBuilder;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.block.enums.SlabType;
import net.minecraft.block.enums.StairShape;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class MiasmaArchitectureGameTests {

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testVerticalChimneyFlueDraft(TestContext context) {
        ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();

        // Build a 5x3x5 stone room with RoomTestBuilder
        RoomTestBuilder.of(context)
                .stoneRoom(0, 0, 0, 4, 3, 4)
                // Add 4 moldy logs on the floor (Toxic score = 4 * 4.0 = 16.0)
                .addMoldyOakLog(1, 0, 1, 3)
                .addMoldyOakLog(3, 0, 1, 3)
                .addMoldyOakLog(1, 0, 3, 3)
                .addMoldyOakLog(3, 0, 3, 3)
                // Vertical chimney flue opening at ceiling center (2, 3, 2) going up to sky
                .setAir(2, 3, 2)
                .clearOpenAirColumn(2, 2, 4, 10);

        BlockPos indoorPos = new BlockPos(1, 1, 1);
        RoomAtmosphereCalculator.MiasmaResult result = RoomAtmosphereCalculator.calculateMiasma(
                context.getWorld(), context.getAbsolutePos(indoorPos));

        context.assertTrue(result.ventilationType == RoomAtmosphereCalculator.RoomVentilationType.VENTILATED,
                "Room with vertical chimney flue must be VENTILATED");
        context.assertTrue(result.ventilationScore >= config.toxicity.open_sky_ventilation_per_block,
                "Chimney flue opening to sky must provide full open sky flow throughput");
        context.assertTrue(result.targetMiasma == 0.0,
                "Target miasma should be 0.0 with chimney capacity (24.0) exceeding mold toxicity (16.0)");

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testOppositeCrossVentilationOpenings(TestContext context) {
        // 7x3x5 room
        RoomTestBuilder.of(context)
                .stoneRoom(0, 0, 0, 6, 3, 4)
                // 10 moldy logs on ceiling (Toxic score = 10 * 4.0 = 40.0)
                .addMoldyOakLog(2, 3, 1, 3)
                .addMoldyOakLog(3, 3, 1, 3)
                .addMoldyOakLog(4, 3, 1, 3)
                .addMoldyOakLog(2, 3, 2, 3)
                .addMoldyOakLog(3, 3, 2, 3)
                .addMoldyOakLog(4, 3, 2, 3)
                .addMoldyOakLog(2, 3, 3, 3)
                .addMoldyOakLog(3, 3, 3, 3)
                .addMoldyOakLog(4, 3, 3, 3)
                .addMoldyOakLog(1, 3, 2, 3)
                // Window on West wall at (0, 1, 2)
                .setAir(0, 1, 2)
                .clearOpenAirColumn(-1, 2, 1, 5)
                // Window on East wall at (6, 1, 2)
                .setAir(6, 1, 2)
                .clearOpenAirColumn(7, 2, 1, 5);

        BlockPos centerPos = new BlockPos(3, 1, 2);
        RoomAtmosphereCalculator.MiasmaResult result = RoomAtmosphereCalculator.calculateMiasma(
                context.getWorld(), context.getAbsolutePos(centerPos));

        context.assertTrue(result.ventilationType == RoomAtmosphereCalculator.RoomVentilationType.VENTILATED,
                "Cross ventilated room must be VENTILATED");
        context.assertTrue(result.ventilationScore >= 24.0,
                "Cross ventilation with dual openings must provide high ventilation score");
        context.assertTrue(result.targetMiasma == 0.0,
                "Target miasma should be 0.0 with dual openings clearing 40.0 toxic score");

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testInvertedStairsVentilation(TestContext context) {
        // 5x3x5 room with inverted stairs at window gap
        RoomTestBuilder.of(context)
                .stoneRoom(0, 0, 0, 4, 3, 4)
                // Inverted stair at (0, 1, 2) facing South, top half
                .set(0, 1, 2, Blocks.STONE_STAIRS.getDefaultState()
                        .with(StairsBlock.FACING, Direction.SOUTH)
                        .with(StairsBlock.HALF, BlockHalf.TOP)
                        .with(StairsBlock.SHAPE, StairShape.STRAIGHT))
                .clearOpenAirColumn(-1, 2, 1, 5);

        BlockPos centerPos = new BlockPos(2, 1, 2);
        RoomAtmosphereCalculator.MiasmaResult result = RoomAtmosphereCalculator.calculateMiasma(
                context.getWorld(), context.getAbsolutePos(centerPos));

        context.assertTrue(result.ventilationScore > 0.0,
                "Inverted stairs opening must provide ventilation flow");
        context.assertTrue(result.ventilationType == RoomAtmosphereCalculator.RoomVentilationType.VENTILATED,
                "Room with inverted stair opening must be VENTILATED");

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testOpenTrapdoorCeilingVentilation(TestContext context) {
        // 5x3x5 room with open trapdoor in the ceiling
        RoomTestBuilder.of(context)
                .stoneRoom(0, 0, 0, 4, 3, 4)
                .set(2, 3, 2, Blocks.OAK_TRAPDOOR.getDefaultState()
                        .with(TrapdoorBlock.OPEN, true)
                        .with(TrapdoorBlock.HALF, BlockHalf.BOTTOM)
                        .with(TrapdoorBlock.FACING, Direction.NORTH))
                .clearOpenAirColumn(2, 2, 4, 15);

        BlockPos centerPos = new BlockPos(2, 1, 2);
        RoomAtmosphereCalculator.MiasmaResult result = RoomAtmosphereCalculator.calculateMiasma(
                context.getWorld(), context.getAbsolutePos(centerPos));

        context.assertTrue(result.ventilationType == RoomAtmosphereCalculator.RoomVentilationType.VENTILATED,
                "Room with open ceiling trapdoor must be VENTILATED");
        context.assertTrue(result.ventilationScore > 0.0,
                "Open trapdoor must provide ventilation flow");

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testHalfSlabGapVentilation(TestContext context) {
        // 5x3x5 room with top half slab at window gap
        RoomTestBuilder.of(context)
                .stoneRoom(0, 0, 0, 4, 3, 4)
                .set(0, 1, 2, Blocks.STONE_SLAB.getDefaultState().with(SlabBlock.TYPE, SlabType.TOP))
                .clearOpenAirColumn(-1, 2, 1, 5);

        BlockPos centerPos = new BlockPos(2, 1, 2);
        RoomAtmosphereCalculator.MiasmaResult result = RoomAtmosphereCalculator.calculateMiasma(
                context.getWorld(), context.getAbsolutePos(centerPos));

        context.assertTrue(result.ventilationType == RoomAtmosphereCalculator.RoomVentilationType.VENTILATED,
                "Room with top half slab gap must be VENTILATED");
        context.assertTrue(result.ventilationScore >= 12.0,
                "Slab gap must provide slab ventilation value (~12.0)");

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testChimneyWithRightAngleBend(TestContext context) {
        // 5x3x5 room with chimney that goes up, bends horizontal, then goes up to sky
        RoomTestBuilder.of(context)
                .stoneRoom(0, 0, 0, 4, 3, 4)
                .addMoldyOakLog(1, 1, 1, 3)
                // Flue vertical section 1 (Y: 3)
                .setAir(2, 3, 2)
                // Enclosing stone casing around Y:4
                .set(1, 4, 2, Blocks.STONE.getDefaultState())
                .set(3, 4, 2, Blocks.STONE.getDefaultState())
                .set(2, 4, 1, Blocks.STONE.getDefaultState())
                .set(2, 4, 3, Blocks.STONE.getDefaultState())
                .setAir(2, 4, 2)
                // Horizontal bend to (2, 4, 3)
                .setAir(2, 4, 3)
                .set(2, 5, 2, Blocks.STONE.getDefaultState()) // cap over the first vertical leg
                // Vertical leg 2 at (2, 5, 3) up to sky
                .clearOpenAirColumn(2, 3, 5, 8);

        BlockPos centerPos = new BlockPos(2, 1, 2);
        RoomAtmosphereCalculator.MiasmaResult result = RoomAtmosphereCalculator.calculateMiasma(
                context.getWorld(), context.getAbsolutePos(centerPos));

        context.assertTrue(result.ventilationType == RoomAtmosphereCalculator.RoomVentilationType.VENTILATED,
                "Room with bent chimney must be VENTILATED to sky");
        context.assertTrue(result.ventilationScore > 0.0,
                "Bent chimney must conduct ventilation flow");

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testChimneyCappedBlocksVentilation(TestContext context) {
        // Build 5x3x5 room with vertical chimney that is capped by stone at top
        RoomTestBuilder.of(context)
                .stoneRoom(0, 0, 0, 4, 3, 4)
                .addMoldyOakLog(1, 1, 1, 3)
                // Flue up to Y:5
                .addVerticalChimney(2, 2, 3, 5)
                // Solid cap at Y:6
                .set(2, 6, 2, Blocks.STONE.getDefaultState());

        BlockPos centerPos = new BlockPos(2, 1, 2);
        RoomAtmosphereCalculator.MiasmaResult result = RoomAtmosphereCalculator.calculateMiasma(
                context.getWorld(), context.getAbsolutePos(centerPos));

        context.assertTrue(result.ventilationType == RoomAtmosphereCalculator.RoomVentilationType.HERMETIC_SEALED,
                "Room with capped chimney must be HERMETIC_SEALED");
        context.assertTrue(result.ventilationScore == 0.0,
                "Capped chimney must give 0.0 ventilation score");

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testCornerBlock9x9x3IsHermetic(TestContext context) {
        // Inner room 9x9x3 (X: 1..9, Y: 1..3, Z: 1..9), 5-block chimney at center (5,5), closed trapdoor at base
        RoomTestBuilder.of(context)
                .stoneRoom(0, 0, 0, 10, 4, 10)
                .addVerticalChimney(5, 5, 4, 8)
                .clearOpenAirColumn(5, 5, 9, 15)
                .addTrapdoor(5, 4, 5, Blocks.OAK_TRAPDOOR, BlockHalf.BOTTOM, false)
                .set(1, 1, 1, Blocks.OAK_LOG);

        BlockPos cornerPos = new BlockPos(1, 1, 1);

        // 1. When trapdoor is closed, evaluating corner block must yield HERMETIC_SEALED (not UNCONFINED_CAVERN)
        MoldRiskCalculator.MoldRiskResult closedResult = MoldRiskCalculator.calculate(
                context.getWorld(), context.getAbsolutePos(cornerPos), false, Blocks.OAK_LOG.getDefaultState());

        context.assertTrue(closedResult.roomVentilationType() == RoomVentilationType.HERMETIC_SEALED,
                "Corner block of closed 9x9x3 room must be HERMETIC_SEALED, got: " + closedResult.roomVentilationType());
        context.assertTrue(closedResult.airVolume() == 242,
                "All 242 air blocks of room must be explored, got: " + closedResult.airVolume());

        // 2. When trapdoor is opened, evaluating corner block must yield VENTILATED
        context.setBlockState(new BlockPos(5, 4, 5), Blocks.OAK_TRAPDOOR.getDefaultState()
                .with(TrapdoorBlock.HALF, BlockHalf.BOTTOM)
                .with(TrapdoorBlock.OPEN, true));

        MoldRiskCalculator.MoldRiskResult openResult = MoldRiskCalculator.calculate(
                context.getWorld(), context.getAbsolutePos(cornerPos), false, Blocks.OAK_LOG.getDefaultState());

        context.assertTrue(openResult.roomVentilationType() == RoomVentilationType.VENTILATED,
                "Corner block of 9x9x3 room with open chimney must be VENTILATED, got: " + openResult.roomVentilationType());
        context.assertTrue(openResult.aerationFlow() > 0.0,
                "Ventilation flow must be > 0 with open chimney");

        context.complete();
    }
}
