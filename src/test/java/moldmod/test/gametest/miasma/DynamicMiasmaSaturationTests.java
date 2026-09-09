package moldmod.test.gametest.miasma;

import me.shedaniel.autoconfig.AutoConfig;
import moldmod.config.ModConfig;
import moldmod.atmosphere.RoomAtmosphereCalculator;
import moldmod.atmosphere.RoomAtmosphereCalculator.MiasmaResult;
import moldmod.atmosphere.RoomAtmosphereCalculator.RoomVentilationType;
import moldmod.atmosphere.RoomSaturationManager;
import moldmod.test.helper.RoomTestBuilder;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.block.FenceBlock;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class DynamicMiasmaSaturationTests {

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testGradualDissipationWhenDoorOpens(TestContext context) {
        ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        config.toxicity.enable_dynamic_spore_saturation = true;
        config.toxicity.dissipation_speed_multiplier = 0.35;

        // Build a 3x3x3 sealed room
        RoomTestBuilder.of(context)
                .stoneRoom(0, 0, 0, 4, 3, 4)
                // Add 2 Rotten Oak Logs (Stage 3): Toxic Score = 2 * 4.0 = 8.0
                .addMoldyOakLog(1, 1, 0, 3)
                .addMoldyOakLog(2, 1, 0, 3)
                // Place closed wooden door at (2, 1, 4) facing outside (SOUTH)
                .addDoor(2, 1, 4, Direction.SOUTH, false)
                .clearOpenAirColumn(2, 5, 1, 3);

        BlockPos centerAir = new BlockPos(2, 1, 2);

        // Initial scan in sealed room: M(0) = 8.0
        MiasmaResult initialResult = RoomAtmosphereCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(centerAir));
        context.assertTrue(initialResult.targetMiasma == 8.0,
                "Expected target miasma 8.0 in sealed room, got: " + initialResult.targetMiasma);
        context.assertTrue(initialResult.netMiasma == 8.0,
                "Expected initial net miasma 8.0, got: " + initialResult.netMiasma);

        // Open the door towards outside (South) -> Target Miasma becomes 0.0 (Capacity 15.0 > 8.0)
        RoomTestBuilder.of(context).addDoor(2, 1, 4, Direction.SOUTH, true);

        // Wait 40 ticks and observe gradual reduction
        context.waitAndRun(40, () -> {
            MiasmaResult midResult = RoomAtmosphereCalculator.calculateMiasma(context.getWorld(),
                    context.getAbsolutePos(centerAir));
            context.assertTrue(midResult.targetMiasma == 0.0,
                    "Expected target miasma 0.0 after opening door, got: " + midResult.targetMiasma);
            context.assertTrue(midResult.netMiasma < 8.0,
                    "Expected net miasma to decrease below 8.0, got: " + midResult.netMiasma);
            context.assertTrue(midResult.netMiasma > 0.0,
                    "Expected net miasma to still be dissipating gradually (> 0), got: " + midResult.netMiasma);
            context.complete();
        });
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testGradualSaturationWhenDoorCloses(TestContext context) {
        ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        config.toxicity.enable_dynamic_spore_saturation = true;
        config.toxicity.saturation_speed_multiplier = 0.15;

        // Build a 3x3x3 room starting with open door
        RoomTestBuilder.of(context)
                .stoneRoom(0, 0, 0, 4, 3, 4)
                .addMoldyOakLog(1, 1, 0, 3)
                .addMoldyOakLog(2, 1, 0, 3)
                .addDoor(2, 1, 4, Direction.SOUTH, true)
                .clearOpenAirColumn(2, 5, 1, 3);

        BlockPos centerAir = new BlockPos(2, 1, 2);
        MiasmaResult openResult = RoomAtmosphereCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(centerAir));
        context.assertTrue(openResult.netMiasma == 0.0,
                "Expected initial net miasma 0.0 with open door, got: " + openResult.netMiasma);

        // Close the door -> Target = 8.0
        RoomTestBuilder.of(context).addDoor(2, 1, 4, Direction.SOUTH, false);

        // Wait 40 ticks and observe gradual accumulation
        context.waitAndRun(40, () -> {
            MiasmaResult midResult = RoomAtmosphereCalculator.calculateMiasma(context.getWorld(),
                    context.getAbsolutePos(centerAir));
            context.assertTrue(Math.abs(midResult.targetMiasma - 8.0) < 0.01,
                    "Expected target miasma 8.0 after closing door, got: " + midResult.targetMiasma);
            context.assertTrue(midResult.netMiasma > 0.0, "Expected net miasma to begin accumulating (> 0), got: "
                    + midResult.netMiasma + " (target=" + midResult.targetMiasma + ")");
            context.assertTrue(midResult.netMiasma < 8.0,
                    "Expected net miasma to be accumulating gradually (< 8.0), got: " + midResult.netMiasma);

            context.complete();
        });
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testEquilibriumOnPartialVentilation(TestContext context) {
        ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        config.toxicity.enable_dynamic_spore_saturation = true;

        // Build room with 6 Rotten Logs (Stage 3): Toxic Score = 6 * 4.0 = 24.0
        RoomTestBuilder.of(context)
                .stoneRoom(0, 0, 0, 4, 3, 4)
                .addMoldyOakLog(1, 1, 0, 3)
                .addMoldyOakLog(2, 1, 0, 3)
                .addMoldyOakLog(3, 1, 0, 3)
                .addMoldyOakLog(1, 2, 0, 3)
                .addMoldyOakLog(2, 2, 0, 3)
                .addMoldyOakLog(3, 2, 0, 3)
                // Place a single oak fence gap (Ventilation modifier = 6.0)
                .set(2, 1, 4, Blocks.OAK_FENCE.getDefaultState().with(FenceBlock.NORTH, false).with(FenceBlock.SOUTH, false));

        BlockPos centerAir = new BlockPos(2, 1, 2);
        MiasmaResult result = RoomAtmosphereCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(centerAir));

        // Equilibrium: Target = 24.0 - 6.0 = 18.0
        context.assertTrue(result.toxicScore == 24.0, "Expected gross toxic score 24.0, got: " + result.toxicScore);
        context.assertTrue(result.ventilationScore == config.toxicity.ventilation_gap_bonus,
                "Expected ventilation score " + config.toxicity.ventilation_gap_bonus + ", got: "
                        + result.ventilationScore);
        context.assertTrue(result.netMiasma == (24.0 - config.toxicity.ventilation_gap_bonus),
                "Expected net miasma equilibrium " + (24.0 - config.toxicity.ventilation_gap_bonus) + ", got: "
                        + result.netMiasma);
        context.assertTrue(result.ventilationType == RoomVentilationType.VENTILATED,
                "Expected VENTILATED environment");

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testRoomSaturationManagerCleanup(TestContext context) {
        BlockPos pos = context.getAbsolutePos(new BlockPos(10, 64, 10));

        long testTick = (context.getWorld().getServer() != null ? context.getWorld().getServer().getTicks()
                : context.getWorld().getTime());
        RoomSaturationManager.getDynamicMiasma(context.getWorld(), pos, 12.0);
        context.assertTrue(RoomSaturationManager.getState(pos) != null, "State should exist in cache");

        // Calling cleanup at testTick should NOT evict this fresh entry (age < 1200)
        RoomSaturationManager.cleanup(testTick);
        context.assertTrue(RoomSaturationManager.getState(pos) != null,
                "Fresh state should NOT be evicted");

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testBottleneckEffectSingleHoleCannotPurgeMassiveMold(TestContext context) {
        ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        config.toxicity.enable_dynamic_spore_saturation = true;

        // Build a 5x3x5 sealed room
        RoomTestBuilder builder = RoomTestBuilder.of(context)
                .stoneRoom(0, 0, 0, 6, 3, 6);

        // Place 20 Rotten Oak Logs (Stage 3): Toxic Score = 20 * 2.25 = 45.0
        for (int x = 1; x <= 5; x++) {
            for (int y = 1; y <= 2; y++) {
                builder.addMoldyOakLog(x, y, 0, 3);
            }
        }
        for (int z = 1; z <= 5; z++) {
            for (int y = 1; y <= 2; y++) {
                builder.addMoldyOakLog(0, y, z, 3);
            }
        }

        // Create a single 1x1 hole in the South stone wall at (3, 1, 6) communicating with outside
        builder.setAir(3, 1, 6)
                .clearOpenAirColumn(3, 7, 1, 2);

        BlockPos centerAir = new BlockPos(3, 1, 3);
        MiasmaResult result = RoomAtmosphereCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(centerAir));

        // Bottleneck: 1 single opening gives exactly 24.0 throughput
        // Target = 80.0 - 24.0 = 56.0
        context.assertTrue(result.toxicScore == 80.0, "Expected gross toxic score 80.0, got: " + result.toxicScore);
        context.assertTrue(result.ventilationScore == config.toxicity.open_sky_ventilation_per_block,
                "Expected bottleneck throughput 24.0 for 1x1 hole, got: " + result.ventilationScore);
        context.assertTrue(result.targetMiasma == (80.0 - config.toxicity.open_sky_ventilation_per_block),
                "Expected target equilibrium 56.0 due to bottleneck, got: " + result.targetMiasma);
        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testMicroclimateNearWindowVsRemoteCorner(TestContext context) {
        ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        config.toxicity.enable_dynamic_spore_saturation = true;
        config.toxicity.enable_distributed_miasma = true;

        // Build a 5x3x5 room
        RoomTestBuilder builder = RoomTestBuilder.of(context)
                .stoneRoom(0, 0, 0, 6, 3, 6);

        // Place 20 Rotten Oak Logs (Stage 3) on North & West walls: Toxic Score = 20 * 4.0 = 80.0 > 24.0
        for (int x = 1; x <= 5; x++) {
            for (int y = 1; y <= 2; y++) {
                builder.addMoldyOakLog(x, y, 0, 3);
            }
        }
        for (int z = 1; z <= 5; z++) {
            for (int y = 1; y <= 2; y++) {
                builder.addMoldyOakLog(0, y, z, 3);
            }
        }

        // Create 1x1 hole on South wall (communicating with outside)
        builder.setAir(3, 1, 6)
                .clearOpenAirColumn(3, 7, 1, 2);

        BlockPos nearOpening = new BlockPos(3, 1, 5);
        BlockPos remoteCorner = new BlockPos(1, 1, 1);

        RoomSaturationManager.reset(context.getAbsolutePos(nearOpening));
        RoomSaturationManager.reset(context.getAbsolutePos(remoteCorner));

        MiasmaResult resultNear = RoomAtmosphereCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(nearOpening));
        MiasmaResult resultRemote = RoomAtmosphereCalculator.calculateMiasma(context.getWorld(),
                context.getAbsolutePos(remoteCorner));

        context.assertTrue(resultNear.localDensity < resultRemote.localDensity,
                "Air near ventilation must have lower spore density than remote corner! Near: "
                        + resultNear.localDensity + " vs Remote: " + resultRemote.localDensity);

        context.assertTrue(resultNear.localAeration > resultRemote.localAeration,
                "Air near ventilation must have higher local aeration than remote corner! Near: "
                        + resultNear.localAeration + " vs Remote: " + resultRemote.localAeration);

        context.complete();
    }
}
