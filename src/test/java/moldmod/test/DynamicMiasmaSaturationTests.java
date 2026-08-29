package moldmod.test;

import me.shedaniel.autoconfig.AutoConfig;
import moldmod.block.ModBlocks;
import moldmod.block.MoldyLogBlock;
import moldmod.config.ModConfig;
import moldmod.event.ToxicAirEvent;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.enums.DoubleBlockHalf;
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
        for (int x = 0; x <= 4; x++) {
            for (int y = 0; y <= 3; y++) {
                for (int z = 0; z <= 4; z++) {
                    if (x == 0 || x == 4 || z == 0 || z == 4 || y == 0 || y == 3) {
                        context.setBlockState(new BlockPos(x, y, z), Blocks.STONE.getDefaultState());
                    } else {
                        context.setBlockState(new BlockPos(x, y, z), Blocks.AIR.getDefaultState());
                    }
                }
            }
        }

        // Add 2 Rotten Oak Logs (Stage 3) on the wall: Toxic Score = 2 * (3 * 0.75) = 4.5
        context.setBlockState(new BlockPos(1, 1, 0),
                ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState().with(MoldyLogBlock.STAGE, 3).with(MoldyLogBlock.WAXED, false));
        context.setBlockState(new BlockPos(2, 1, 0),
                ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState().with(MoldyLogBlock.STAGE, 3).with(MoldyLogBlock.WAXED, false));

        // Place a closed wooden door at (2, 1, 4) facing outside (SOUTH)
        context.setBlockState(new BlockPos(2, 1, 4),
                Blocks.OAK_DOOR.getDefaultState().with(DoorBlock.FACING, Direction.SOUTH).with(DoorBlock.HALF, DoubleBlockHalf.LOWER).with(DoorBlock.OPEN, false));
        context.setBlockState(new BlockPos(2, 2, 4),
                Blocks.OAK_DOOR.getDefaultState().with(DoorBlock.FACING, Direction.SOUTH).with(DoorBlock.HALF, DoubleBlockHalf.UPPER).with(DoorBlock.OPEN, false));

        BlockPos centerAir = new BlockPos(2, 1, 2);

        // Initial scan in sealed room: M(0) = 4.5
        ToxicAirEvent.MiasmaResult initialResult = ToxicAirEvent.calculateMiasma(context.getWorld(), context.getAbsolutePos(centerAir));
        context.assertTrue(initialResult.targetMiasma == 4.5, "Expected target miasma 4.5 in sealed room, got: " + initialResult.targetMiasma);
        context.assertTrue(initialResult.netMiasma == 4.5, "Expected initial net miasma 4.5, got: " + initialResult.netMiasma);

        // Open the door towards outside (South) -> Target Miasma becomes 0.0 (Capacity 15.0 > 4.5)
        context.setBlockState(new BlockPos(2, 1, 4),
                Blocks.OAK_DOOR.getDefaultState().with(DoorBlock.FACING, Direction.SOUTH).with(DoorBlock.HALF, DoubleBlockHalf.LOWER).with(DoorBlock.OPEN, true));
        context.setBlockState(new BlockPos(2, 2, 4),
                Blocks.OAK_DOOR.getDefaultState().with(DoorBlock.FACING, Direction.SOUTH).with(DoorBlock.HALF, DoubleBlockHalf.UPPER).with(DoorBlock.OPEN, true));

        // Wait 40 ticks and observe gradual reduction
        context.waitAndRun(40, () -> {
            ToxicAirEvent.MiasmaResult midResult = ToxicAirEvent.calculateMiasma(context.getWorld(), context.getAbsolutePos(centerAir));
            context.assertTrue(midResult.targetMiasma == 0.0, "Expected target miasma 0.0 after opening door, got: " + midResult.targetMiasma);
            context.assertTrue(midResult.netMiasma < 4.5, "Expected net miasma to decrease below 4.5, got: " + midResult.netMiasma);
            context.assertTrue(midResult.netMiasma > 0.0, "Expected net miasma to still be dissipating gradually (> 0), got: " + midResult.netMiasma);
            context.complete();
        });
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testGradualSaturationWhenDoorCloses(TestContext context) {
        ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        config.toxicity.enable_dynamic_spore_saturation = true;
        config.toxicity.saturation_speed_multiplier = 0.15;

        // Build a 3x3x3 room
        for (int x = 0; x <= 4; x++) {
            for (int y = 0; y <= 3; y++) {
                for (int z = 0; z <= 4; z++) {
                    if (x == 0 || x == 4 || z == 0 || z == 4 || y == 0 || y == 3) {
                        context.setBlockState(new BlockPos(x, y, z), Blocks.STONE.getDefaultState());
                    } else {
                        context.setBlockState(new BlockPos(x, y, z), Blocks.AIR.getDefaultState());
                    }
                }
            }
        }

        // Add 2 Rotten Oak Logs (Stage 3) on the wall: Toxic Score = 4.5
        context.setBlockState(new BlockPos(1, 1, 0),
                ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState().with(MoldyLogBlock.STAGE, 3).with(MoldyLogBlock.WAXED, false));
        context.setBlockState(new BlockPos(2, 1, 0),
                ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState().with(MoldyLogBlock.STAGE, 3).with(MoldyLogBlock.WAXED, false));

        // Start with OPEN door -> Target = 0.0, Initial = 0.0
        context.setBlockState(new BlockPos(2, 1, 4),
                Blocks.OAK_DOOR.getDefaultState().with(DoorBlock.FACING, Direction.SOUTH).with(DoorBlock.HALF, DoubleBlockHalf.LOWER).with(DoorBlock.OPEN, true));
        context.setBlockState(new BlockPos(2, 2, 4),
                Blocks.OAK_DOOR.getDefaultState().with(DoorBlock.FACING, Direction.SOUTH).with(DoorBlock.HALF, DoubleBlockHalf.UPPER).with(DoorBlock.OPEN, true));

        BlockPos centerAir = new BlockPos(2, 1, 2);
        ToxicAirEvent.MiasmaResult openResult = ToxicAirEvent.calculateMiasma(context.getWorld(), context.getAbsolutePos(centerAir));
        context.assertTrue(openResult.netMiasma == 0.0, "Expected initial net miasma 0.0 with open door, got: " + openResult.netMiasma);

        // Close the door -> Target = 4.5
        context.setBlockState(new BlockPos(2, 1, 4),
                Blocks.OAK_DOOR.getDefaultState().with(DoorBlock.FACING, Direction.SOUTH).with(DoorBlock.HALF, DoubleBlockHalf.LOWER).with(DoorBlock.OPEN, false));
        context.setBlockState(new BlockPos(2, 2, 4),
                Blocks.OAK_DOOR.getDefaultState().with(DoorBlock.FACING, Direction.SOUTH).with(DoorBlock.HALF, DoubleBlockHalf.UPPER).with(DoorBlock.OPEN, false));

        // Wait 40 ticks and observe gradual accumulation
        context.waitAndRun(40, () -> {
            ToxicAirEvent.MiasmaResult midResult = ToxicAirEvent.calculateMiasma(context.getWorld(), context.getAbsolutePos(centerAir));
            context.assertTrue(Math.abs(midResult.targetMiasma - 4.5) < 0.01, "Expected target miasma 4.5 after closing door, got: " + midResult.targetMiasma);
            context.assertTrue(midResult.netMiasma > 0.0, "Expected net miasma to begin accumulating (> 0), got: " + midResult.netMiasma + " (target=" + midResult.targetMiasma + ")");
            context.assertTrue(midResult.netMiasma < 4.5, "Expected net miasma to be accumulating gradually (< 4.5), got: " + midResult.netMiasma);

            context.complete();
        });
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testEquilibriumOnPartialVentilation(TestContext context) {
        ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        config.toxicity.enable_dynamic_spore_saturation = true;

        // Build room with 6 Rotten Logs (Stage 3): Toxic Score = 6 * (3 * 0.75) = 13.5
        for (int x = 0; x <= 4; x++) {
            for (int y = 0; y <= 3; y++) {
                for (int z = 0; z <= 4; z++) {
                    if (x == 0 || x == 4 || z == 0 || z == 4 || y == 0 || y == 3) {
                        context.setBlockState(new BlockPos(x, y, z), Blocks.STONE.getDefaultState());
                    } else {
                        context.setBlockState(new BlockPos(x, y, z), Blocks.AIR.getDefaultState());
                    }
                }
            }
        }

        for (int x = 1; x <= 3; x++) {
            context.setBlockState(new BlockPos(x, 1, 0),
                    ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState().with(MoldyLogBlock.STAGE, 3).with(MoldyLogBlock.WAXED, false));
            context.setBlockState(new BlockPos(x, 2, 0),
                    ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState().with(MoldyLogBlock.STAGE, 3).with(MoldyLogBlock.WAXED, false));
        }

        // Place a single oak fence gap (Ventilation modifier = 3.0)
        context.setBlockState(new BlockPos(2, 1, 4),
                Blocks.OAK_FENCE.getDefaultState().with(FenceBlock.NORTH, false).with(FenceBlock.SOUTH, false));

        BlockPos centerAir = new BlockPos(2, 1, 2);
        ToxicAirEvent.MiasmaResult result = ToxicAirEvent.calculateMiasma(context.getWorld(), context.getAbsolutePos(centerAir));

        // Equilibrium: Target = 13.5 - 3.0 = 10.5
        context.assertTrue(result.toxicScore == 13.5, "Expected gross toxic score 13.5, got: " + result.toxicScore);
        context.assertTrue(result.ventilationScore == 3.0, "Expected ventilation score 3.0, got: " + result.ventilationScore);
        context.assertTrue(result.netMiasma == 10.5, "Expected net miasma equilibrium 10.5, got: " + result.netMiasma);
        context.assertTrue(result.ventilationType == ToxicAirEvent.RoomVentilationType.VENTILATED, "Expected VENTILATED environment");

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testRoomSaturationManagerCleanup(TestContext context) {
        BlockPos pos = context.getAbsolutePos(new BlockPos(10, 64, 10));

        long testTick = (context.getWorld().getServer() != null ? context.getWorld().getServer().getTicks() : context.getWorld().getTime());
        ToxicAirEvent.RoomSaturationManager.getDynamicMiasma(context.getWorld(), pos, 12.0);
        context.assertTrue(ToxicAirEvent.RoomSaturationManager.getState(pos) != null, "State should exist in cache");

        // Calling cleanup at testTick should NOT evict this fresh entry (age < 1200)
        ToxicAirEvent.RoomSaturationManager.cleanup(testTick);
        context.assertTrue(ToxicAirEvent.RoomSaturationManager.getState(pos) != null, "Fresh state should NOT be evicted");

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testBottleneckEffectSingleHoleCannotPurgeMassiveMold(TestContext context) {
        ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        config.toxicity.enable_dynamic_spore_saturation = true;

        // Build a 5x3x5 sealed room
        for (int x = 0; x <= 6; x++) {
            for (int y = 0; y <= 3; y++) {
                for (int z = 0; z <= 6; z++) {
                    if (x == 0 || x == 6 || z == 0 || z == 6 || y == 0 || y == 3) {
                        context.setBlockState(new BlockPos(x, y, z), Blocks.STONE.getDefaultState());
                    } else {
                        context.setBlockState(new BlockPos(x, y, z), Blocks.AIR.getDefaultState());
                    }
                }
            }
        }

        // Place 20 Rotten Oak Logs (Stage 3): Toxic Score = 20 * (3 * 0.75) = 45.0
        // 10 on North wall (z=0) and 10 on West wall (x=0)
        for (int x = 1; x <= 5; x++) {
            for (int y = 1; y <= 2; y++) {
                context.setBlockState(new BlockPos(x, y, 0),
                        ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState().with(MoldyLogBlock.STAGE, 3).with(MoldyLogBlock.WAXED, false));
            }
        }
        for (int z = 1; z <= 5; z++) {
            for (int y = 1; y <= 2; y++) {
                context.setBlockState(new BlockPos(0, y, z),
                        ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState().with(MoldyLogBlock.STAGE, 3).with(MoldyLogBlock.WAXED, false));
            }
        }

        // Create a single 1x1 hole in the South stone wall at (3, 1, 6) communicating with the outside
        context.setBlockState(new BlockPos(3, 1, 6), Blocks.AIR.getDefaultState());

        BlockPos centerAir = new BlockPos(3, 1, 3);
        ToxicAirEvent.MiasmaResult result = ToxicAirEvent.calculateMiasma(context.getWorld(), context.getAbsolutePos(centerAir));

        // Bottleneck: 1 single opening gives exactly 25.0 throughput
        // Target = 45.0 - 25.0 = 20.0
        context.assertTrue(result.toxicScore == 45.0, "Expected gross toxic score 45.0, got: " + result.toxicScore);
        context.assertTrue(result.ventilationScore == 25.0, "Expected bottleneck throughput 25.0 for 1x1 hole, got: " + result.ventilationScore);
        context.assertTrue(result.targetMiasma == 20.0, "Expected target equilibrium 20.0 due to bottleneck, got: " + result.targetMiasma);
        context.assertTrue(result.ventilationType == ToxicAirEvent.RoomVentilationType.CLEAN_OPEN_AIR, "Expected room communicating with outside to be CLEAN_OPEN_AIR");

        context.complete();
    }
}
