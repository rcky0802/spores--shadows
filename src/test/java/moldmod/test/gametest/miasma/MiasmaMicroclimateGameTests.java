package moldmod.test.gametest.miasma;

import me.shedaniel.autoconfig.AutoConfig;
import moldmod.config.ModConfig;
import moldmod.event.MiasmaCalculator;
import moldmod.event.RoomSaturationManager;
import moldmod.test.helper.RoomTestBuilder;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public class MiasmaMicroclimateGameTests {

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testLShapedRoomPathConductance(TestContext context) {
        ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        config.toxicity.enable_distributed_miasma = true;

        // Build an L-shaped room:
        // Arm 1: X: 0..6, Y: 0..3, Z: 0..2
        // Arm 2: X: 4..6, Y: 0..3, Z: 2..6
        // Moldy nest at end of Arm 2: (5, 1, 5) and (5, 2, 5)
        // Window at end of Arm 1: (0, 1, 1)

        // Solid envelope 7x4x7 stone first
        RoomTestBuilder.of(context)
                .fill(0, 0, 0, 7, 3, 7, Blocks.STONE.getDefaultState())
                // Carve Arm 1 (X: 1..5, Y: 1..2, Z: 1)
                .fill(1, 1, 1, 5, 2, 1, Blocks.AIR.getDefaultState())
                // Carve Arm 2 (X: 5, Y: 1..2, Z: 2..5)
                .fill(5, 1, 2, 5, 2, 5, Blocks.AIR.getDefaultState())
                // 12 moldy logs at end of Arm 2 (Toxic score = 12 * 2.25 = 27.0 > 24.0)
                .addMoldyOakLog(4, 1, 5, 3)
                .addMoldyOakLog(6, 1, 5, 3)
                .addMoldyOakLog(5, 1, 6, 3)
                .addMoldyOakLog(5, 0, 5, 3)
                .addMoldyOakLog(5, 3, 5, 3)
                .addMoldyOakLog(4, 2, 5, 3)
                .addMoldyOakLog(6, 2, 5, 3)
                .addMoldyOakLog(5, 2, 6, 3)
                .addMoldyOakLog(4, 1, 4, 3)
                .addMoldyOakLog(6, 1, 4, 3)
                .addMoldyOakLog(5, 0, 4, 3)
                .addMoldyOakLog(5, 3, 4, 3)
                // Window at end of Arm 1 at (0, 1, 1)
                .setAir(0, 1, 1)
                .clearOpenAirColumn(-1, 1, 1, 5);

        BlockPos nearWindowPos = new BlockPos(1, 1, 1);
        BlockPos cornerPos = new BlockPos(5, 1, 1);
        BlockPos moldNestPos = new BlockPos(5, 1, 4);

        RoomSaturationManager.reset(context.getAbsolutePos(nearWindowPos));
        RoomSaturationManager.reset(context.getAbsolutePos(cornerPos));
        RoomSaturationManager.reset(context.getAbsolutePos(moldNestPos));

        MiasmaCalculator.MiasmaResult rNear = MiasmaCalculator.calculateMiasma(
                context.getWorld(), context.getAbsolutePos(nearWindowPos));
        MiasmaCalculator.MiasmaResult rCorner = MiasmaCalculator.calculateMiasma(
                context.getWorld(), context.getAbsolutePos(cornerPos));
        MiasmaCalculator.MiasmaResult rNest = MiasmaCalculator.calculateMiasma(
                context.getWorld(), context.getAbsolutePos(moldNestPos));

        // Spatial BFS distances must follow the L-bend path:
        context.assertTrue(rNear.distanceToVentilation < rCorner.distanceToVentilation,
                "Distance at window (" + rNear.distanceToVentilation + ") must be < corner (" + rCorner.distanceToVentilation + ")");
        context.assertTrue(rCorner.distanceToVentilation < rNest.distanceToVentilation,
                "Distance at corner (" + rCorner.distanceToVentilation + ") must be < mold nest (" + rNest.distanceToVentilation + ")");

        // Local aeration must decay along the L path
        context.assertTrue(rNear.localAeration > rCorner.localAeration,
                "Aeration near window must be > corner");
        context.assertTrue(rCorner.localAeration > rNest.localAeration,
                "Aeration at corner must be > deep nest");

        // Local spore density must increase towards the mold nest
        context.assertTrue(rNear.localDensity < rCorner.localDensity,
                "Density near window (" + rNear.localDensity + ") must be < corner (" + rCorner.localDensity + ")");
        context.assertTrue(rCorner.localDensity < rNest.localDensity,
                "Density at corner (" + rCorner.localDensity + ") must be < deep nest (" + rNest.localDensity + ")");

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testMezzanineTwoFloorStratification(TestContext context) {
        ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        config.toxicity.enable_distributed_miasma = true;

        // Build a 2-floor structure: 5x5 footprint, Y: 0..5 (height 6)
        // Ground floor: Y: 1..2. Upper floor: Y: 3..4.
        // Mold on upper floor ceiling (Toxic score = 6 * 2.25 = 13.5)
        // Window on ground floor at (0, 1, 2)
        RoomTestBuilder.of(context)
                .fill(0, 0, 0, 4, 5, 4, Blocks.STONE.getDefaultState())
                .fill(1, 1, 1, 3, 4, 3, Blocks.AIR.getDefaultState())
                // Mezzanine floor slab at Y:2 leaving stair gap at (1, 2, 1)
                .fill(2, 2, 1, 3, 2, 3, Blocks.STONE.getDefaultState())
                .fill(1, 2, 2, 3, 2, 3, Blocks.STONE.getDefaultState())
                // 12 Moldy logs on upper ceiling (Y: 5) to produce net miasma > 0
                .addMoldyOakLog(2, 5, 2, 3)
                .addMoldyOakLog(2, 5, 3, 3)
                .addMoldyOakLog(3, 5, 2, 3)
                .addMoldyOakLog(3, 5, 3, 3)
                .addMoldyOakLog(1, 5, 2, 3)
                .addMoldyOakLog(1, 5, 3, 3)
                .addMoldyOakLog(2, 4, 2, 3)
                .addMoldyOakLog(2, 4, 3, 3)
                .addMoldyOakLog(3, 4, 2, 3)
                .addMoldyOakLog(3, 4, 3, 3)
                .addMoldyOakLog(1, 4, 2, 3)
                .addMoldyOakLog(1, 4, 3, 3)
                // Window on lower ground floor at (0, 1, 2)
                .setAir(0, 1, 2)
                .clearOpenAirColumn(-1, 2, 1, 8);

        BlockPos lowerGroundPos = new BlockPos(1, 1, 2);
        BlockPos upperFloorPos = new BlockPos(2, 3, 2);

        RoomSaturationManager.reset(context.getAbsolutePos(lowerGroundPos));
        RoomSaturationManager.reset(context.getAbsolutePos(upperFloorPos));

        MiasmaCalculator.MiasmaResult rLower = MiasmaCalculator.calculateMiasma(
                context.getWorld(), context.getAbsolutePos(lowerGroundPos));
        MiasmaCalculator.MiasmaResult rUpper = MiasmaCalculator.calculateMiasma(
                context.getWorld(), context.getAbsolutePos(upperFloorPos));

        context.assertTrue(rLower.distanceToVentilation < rUpper.distanceToVentilation,
                "Lower floor near window must have shorter distance to ventilation than mezzanine");
        context.assertTrue(rLower.localAeration > rUpper.localAeration,
                "Lower floor must have higher local aeration than mezzanine");

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testAsymmetricDualWindowDistanceFairness(TestContext context) {
        ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        config.toxicity.enable_distributed_miasma = true;

        // Long corridor 9x3x3: X: 0..8, Y: 0..2, Z: 0..2
        // Window 1 at West (0, 1, 1)
        // Window 2 at East (8, 1, 1)
        RoomTestBuilder.of(context)
                .fill(0, 0, 0, 8, 2, 2, Blocks.STONE.getDefaultState())
                .fill(1, 1, 1, 7, 1, 1, Blocks.AIR.getDefaultState())
                // Mold nest at center (4, 1, 1)
                .addMoldyOakLog(4, 2, 1, 3)
                .addMoldyOakLog(4, 0, 1, 3)
                // Window West (0, 1, 1)
                .setAir(0, 1, 1)
                .clearOpenAirColumn(-1, 1, 1, 5)
                // Window East (8, 1, 1)
                .setAir(8, 1, 1)
                .clearOpenAirColumn(9, 1, 1, 5);

        BlockPos closeToWest = new BlockPos(1, 1, 1);
        BlockPos midCorridor = new BlockPos(3, 1, 1);

        RoomSaturationManager.reset(context.getAbsolutePos(closeToWest));
        RoomSaturationManager.reset(context.getAbsolutePos(midCorridor));

        MiasmaCalculator.MiasmaResult rWest = MiasmaCalculator.calculateMiasma(
                context.getWorld(), context.getAbsolutePos(closeToWest));
        MiasmaCalculator.MiasmaResult rMid = MiasmaCalculator.calculateMiasma(
                context.getWorld(), context.getAbsolutePos(midCorridor));

        context.assertTrue(rWest.localAeration > rMid.localAeration,
                "Air adjacent to opening must have higher aeration than interior corridor");

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testTCorridorBranchDifferential(TestContext context) {
        ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        config.toxicity.enable_distributed_miasma = true;

        // T-junction:
        // Stem: Z: 0..4, X: 3, Y: 1
        // Bar: X: 0..6, Z: 4, Y: 1
        // Window at North end of stem (3, 1, 0)
        // Short arm East (X: 4..5, Z: 4)
        // Long arm West (X: 1..2, Z: 4) with 12 mold logs at (0, 1, 4)
        RoomTestBuilder.of(context)
                .fill(0, 0, 0, 6, 2, 5, Blocks.STONE.getDefaultState())
                // Carve stem
                .fill(3, 1, 1, 3, 1, 4, Blocks.AIR.getDefaultState())
                // Carve cross bar
                .fill(1, 1, 4, 5, 1, 4, Blocks.AIR.getDefaultState())
                // Mold nest at West end of bar (0, 1, 4)
                .addMoldyOakLog(0, 1, 4, 3)
                .addMoldyOakLog(0, 2, 4, 3)
                .addMoldyOakLog(0, 0, 4, 3)
                .addMoldyOakLog(1, 2, 4, 3)
                .addMoldyOakLog(1, 0, 4, 3)
                .addMoldyOakLog(2, 2, 4, 3)
                .addMoldyOakLog(2, 0, 4, 3)
                .addMoldyOakLog(0, 1, 3, 3)
                .addMoldyOakLog(0, 1, 5, 3)
                .addMoldyOakLog(1, 1, 3, 3)
                .addMoldyOakLog(1, 1, 5, 3)
                .addMoldyOakLog(2, 1, 3, 3)
                // Window at North stem (3, 1, 0)
                .setAir(3, 1, 0)
                .clearOpenAirColumn(3, -1, 1, 8);

        BlockPos junctionPos = new BlockPos(3, 1, 4);
        BlockPos eastEndPos = new BlockPos(5, 1, 4);
        BlockPos westMoldPos = new BlockPos(1, 1, 4);

        RoomSaturationManager.reset(context.getAbsolutePos(junctionPos));
        RoomSaturationManager.reset(context.getAbsolutePos(eastEndPos));
        RoomSaturationManager.reset(context.getAbsolutePos(westMoldPos));

        MiasmaCalculator.MiasmaResult rJunction = MiasmaCalculator.calculateMiasma(
                context.getWorld(), context.getAbsolutePos(junctionPos));
        MiasmaCalculator.MiasmaResult rEast = MiasmaCalculator.calculateMiasma(
                context.getWorld(), context.getAbsolutePos(eastEndPos));
        MiasmaCalculator.MiasmaResult rWest = MiasmaCalculator.calculateMiasma(
                context.getWorld(), context.getAbsolutePos(westMoldPos));

        context.assertTrue(rJunction.distanceToVentilation < rEast.distanceToVentilation,
                "Junction must have shorter distance to window than East end");
        context.assertTrue(rJunction.localAeration > rEast.localAeration,
                "Junction must have higher local aeration than East blind end");
        context.assertTrue(rJunction.localAeration >= rWest.localAeration,
                "Junction must have higher or equal local aeration than West mold nest");

        context.complete();
    }
}
