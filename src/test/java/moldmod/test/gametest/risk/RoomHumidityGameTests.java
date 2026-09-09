package moldmod.test.gametest.risk;

import me.shedaniel.autoconfig.AutoConfig;
import moldmod.risk.MoldRiskCalculator;
import moldmod.risk.MoldRiskCalculator.MoldRiskResult;
import moldmod.config.ModConfig;
import moldmod.test.helper.RoomTestBuilder;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.state.property.Properties;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public class RoomHumidityGameTests {

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testWaterThroughWallDoesNotAffectRoom(TestContext context) {
        // 1. Build a sealed stone room at (0, 0, 0) to (4, 3, 4)
        RoomTestBuilder.of(context)
                .stoneRoom(0, 0, 0, 4, 3, 4)
                .set(2, 1, 1, Blocks.OAK_LOG);

        // Place water outside the room separated by solid stone wall at (2, 1, 6)
        context.setBlockState(new BlockPos(2, 1, 5), Blocks.STONE.getDefaultState());
        context.setBlockState(new BlockPos(2, 1, 6), Blocks.WATER.getDefaultState());

        BlockPos insideLog = context.getAbsolutePos(new BlockPos(2, 1, 1));
        MoldRiskResult rOutsideWater = MoldRiskCalculator.calculate(context.getWorld(), insideLog,
                false, Blocks.OAK_LOG.getDefaultState());

        context.assertTrue(rOutsideWater.roomWaterSourcesCount() == 0,
                "Water behind solid wall must not affect indoor room! Found: " + rOutsideWater.roomWaterSourcesCount());
        context.assertTrue(rOutsideWater.localHumidityBonus() == 0.0,
                "Expected 0.0 water bonus from water outside wall, got: " + rOutsideWater.localHumidityBonus());

        // Now place 1 water source INSIDE the room at (2, 1, 3)
        context.setBlockState(new BlockPos(2, 1, 3), Blocks.WATER.getDefaultState());

        MoldRiskResult rInsideWater = MoldRiskCalculator.calculate(context.getWorld(), insideLog,
                false, Blocks.OAK_LOG.getDefaultState());

        context.assertTrue(rInsideWater.roomWaterSourcesCount() == 1,
                "Water inside room must be detected! Expected 1, got: " + rInsideWater.roomWaterSourcesCount());
        context.assertTrue(rInsideWater.localHumidityBonus() > 0.0,
                "Expected water bonus > 0.0 for inside water, got: " + rInsideWater.localHumidityBonus());

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testWaterInsideRoomIncreasesHumidityLinearly(TestContext context) {
        ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        double contribution = config.environment.water_source_humidity_contribution;

        // Build a sealed stone room
        RoomTestBuilder.of(context)
                .stoneRoom(0, 0, 0, 4, 3, 4)
                .set(1, 1, 1, Blocks.OAK_LOG);

        BlockPos logPos = context.getAbsolutePos(new BlockPos(1, 1, 1));

        // Baseline: 0 water
        MoldRiskResult r0 = MoldRiskCalculator.calculate(context.getWorld(), logPos, false, Blocks.OAK_LOG.getDefaultState());
        context.assertTrue(r0.roomWaterSourcesCount() == 0, "Baseline room must have 0 water sources");

        // Add 1 water block inside room at (3, 1, 3)
        context.setBlockState(new BlockPos(3, 1, 3), Blocks.WATER.getDefaultState());
        MoldRiskResult r1 = MoldRiskCalculator.calculate(context.getWorld(), logPos, false, Blocks.OAK_LOG.getDefaultState());
        context.assertTrue(r1.roomWaterSourcesCount() == 1, "Expected 1 water source");
        context.assertTrue(Math.abs((r0.targetHumidity() + contribution) - r1.targetHumidity()) < 0.001,
                "Expected linear increment of +" + contribution + ", but target went from " + r0.targetHumidity() + " to " + r1.targetHumidity());

        // Add a 2nd water block inside room at (3, 1, 2)
        context.setBlockState(new BlockPos(3, 1, 2), Blocks.WATER.getDefaultState());
        MoldRiskResult r2 = MoldRiskCalculator.calculate(context.getWorld(), logPos, false, Blocks.OAK_LOG.getDefaultState());
        context.assertTrue(r2.roomWaterSourcesCount() == 2, "Expected 2 water sources");
        context.assertTrue(Math.abs((r0.targetHumidity() + 2 * contribution) - r2.targetHumidity()) < 0.001,
                "Expected linear increment of +" + (2 * contribution) + ", but target went from " + r0.targetHumidity() + " to " + r2.targetHumidity());

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testRoomWaterBonusCapped(TestContext context) {
        ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        double cap = config.environment.max_room_water_humidity_bonus;

        // Build a large 7x3x7 stone room
        RoomTestBuilder.of(context)
                .stoneRoom(0, 0, 0, 6, 3, 6)
                .set(1, 1, 1, Blocks.OAK_LOG);

        // Place 20 water blocks on floor inside the room (X: 2..5, Z: 2..5)
        for (int x = 2; x <= 5; x++) {
            for (int z = 2; z <= 5; z++) {
                context.setBlockState(new BlockPos(x, 1, z), Blocks.WATER.getDefaultState());
            }
        }
        // Total water blocks placed: 4 * 4 = 16. Add 4 more at (1, 1, 2..5)
        for (int z = 2; z <= 5; z++) {
            context.setBlockState(new BlockPos(1, 1, z), Blocks.WATER.getDefaultState());
        }

        BlockPos logPos = context.getAbsolutePos(new BlockPos(1, 1, 1));
        MoldRiskResult r = MoldRiskCalculator.calculate(context.getWorld(), logPos, false, Blocks.OAK_LOG.getDefaultState());

        context.assertTrue(r.roomWaterSourcesCount() >= 20,
                "Expected at least 20 water sources, found: " + r.roomWaterSourcesCount());
        context.assertTrue(r.localHumidityBonus() <= cap + 1e-4,
                "Room water bonus must be capped at " + cap + ", got: " + r.localHumidityBonus());
        context.assertTrue(Math.abs(r.localHumidityBonus() - cap) < 0.001,
                "Room water bonus should reach cap " + cap + ", got: " + r.localHumidityBonus());

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testWaterloggedBlockReceivesMaxWaterHumidity(TestContext context) {
        BlockPos stairPos = new BlockPos(2, 1, 2);
        context.setBlockState(stairPos, Blocks.OAK_STAIRS.getDefaultState().with(Properties.WATERLOGGED, true));

        BlockPos absPos = context.getAbsolutePos(stairPos);
        MoldRiskResult r = MoldRiskCalculator.calculate(context.getWorld(), absPos,
                false, context.getWorld().getBlockState(absPos));

        context.assertTrue(r.isWaterlogged(), "Block must be recognized as waterlogged");
        context.assertTrue(r.Hraw() == 1.0, "Hraw must be 1.0 for waterlogged block, got: " + r.Hraw());
        context.assertTrue(r.Heff() == 1.0, "Heff must be 1.0 for waterlogged block, got: " + r.Heff());

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testOpenAirDockReceivesWaterAndSunlight(TestContext context) {
        // Dock plank over water in open air
        context.setBlockState(new BlockPos(2, 0, 2), Blocks.WATER.getDefaultState());
        context.setBlockState(new BlockPos(2, 1, 2), Blocks.OAK_PLANKS.getDefaultState());
        RoomTestBuilder.of(context).clearOpenAirColumn(2, 2, 2, 6);

        // Control dry plank in open air with stone underneath (dist > 3 from water at 2,0,2)
        context.setBlockState(new BlockPos(8, 0, 8), Blocks.STONE.getDefaultState());
        context.setBlockState(new BlockPos(8, 1, 8), Blocks.OAK_PLANKS.getDefaultState());
        RoomTestBuilder.of(context).clearOpenAirColumn(8, 8, 2, 6);

        MoldRiskResult rDock = MoldRiskCalculator.calculate(context.getWorld(), context.getAbsolutePos(new BlockPos(2, 1, 2)),
                false, Blocks.OAK_PLANKS.getDefaultState());
        MoldRiskResult rDry = MoldRiskCalculator.calculate(context.getWorld(), context.getAbsolutePos(new BlockPos(8, 1, 8)),
                false, Blocks.OAK_PLANKS.getDefaultState());

        context.assertTrue(rDock.localHumidityBonus() > rDry.localHumidityBonus(),
                "Dock over water must receive water bonus! Dock: " + rDock.localHumidityBonus() + " vs Dry: " + rDry.localHumidityBonus());
        context.assertTrue(rDock.aeration() > 0.0,
                "Dock under open sky must receive aeration flow! Got: " + rDock.aeration());
        context.assertTrue(rDock.aerationDryingBonus() > 0.0,
                "Dock under open sky must receive drying bonus! Got: " + rDock.aerationDryingBonus());
        context.assertTrue(rDock.Heff() < rDock.Hraw(),
                "Effective humidity must be dried by open sky ventilation! Heff: " + rDock.Heff() + " vs Hraw: " + rDock.Hraw());

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testDynamicHumiditySaturation(TestContext context) {
        ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        config.environment.enable_dynamic_room_humidity = true;
        config.environment.humidity_saturation_speed = 0.05;

        // Build a sealed stone room starting dry
        RoomTestBuilder.of(context)
                .stoneRoom(0, 0, 0, 4, 3, 4)
                .set(1, 1, 1, Blocks.OAK_LOG);

        BlockPos logPos = context.getAbsolutePos(new BlockPos(1, 1, 1));
        MoldRiskResult rInitial = MoldRiskCalculator.calculate(context.getWorld(), logPos, false, Blocks.OAK_LOG.getDefaultState());
        double initialHum = rInitial.currentHumidity();

        // Place water in contained floor basin at (3, 0, 3)
        context.setBlockState(new BlockPos(3, 0, 3), Blocks.WATER.getDefaultState());

        // Wait 40 ticks (1 check interval)
        context.waitAndRun(40, () -> {
            MoldRiskResult rMid = MoldRiskCalculator.calculate(context.getWorld(), logPos, false, Blocks.OAK_LOG.getDefaultState());
            context.assertTrue(rMid.targetHumidity() > initialHum,
                    "Target humidity must increase after water is added. Target: " + rMid.targetHumidity() + ", Initial: " + initialHum);
            context.assertTrue(rMid.currentHumidity() > initialHum,
                    "Dynamic humidity must accumulate towards target! Current: " + rMid.currentHumidity() + ", Initial: " + initialHum);
            context.assertTrue(rMid.currentHumidity() < rMid.targetHumidity(),
                    "Dynamic humidity should be saturating gradually (< target)! Current: " + rMid.currentHumidity() + ", Target: " + rMid.targetHumidity());

            context.complete();
        });
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testDynamicHumidityDissipationOnWaterRemoval(TestContext context) {
        ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        config.environment.enable_dynamic_room_humidity = true;
        config.environment.humidity_dissipation_speed = 0.08;

        // Build a sealed stone room starting with water in contained floor basin
        RoomTestBuilder.of(context)
                .stoneRoom(0, 0, 0, 4, 3, 4)
                .set(1, 1, 1, Blocks.OAK_LOG)
                .set(3, 0, 3, Blocks.WATER);

        BlockPos logPos = context.getAbsolutePos(new BlockPos(1, 1, 1));
        MoldRiskResult rInitial = MoldRiskCalculator.calculate(context.getWorld(), logPos, false, Blocks.OAK_LOG.getDefaultState());
        double saturatedHum = rInitial.currentHumidity();

        // Replace water in floor basin with stone
        context.setBlockState(new BlockPos(3, 0, 3), Blocks.STONE.getDefaultState());

        // Wait 40 ticks
        context.waitAndRun(40, () -> {
            MoldRiskResult rMid = MoldRiskCalculator.calculate(context.getWorld(), logPos, false, Blocks.OAK_LOG.getDefaultState());
            context.assertTrue(rMid.targetHumidity() < saturatedHum,
                    "Target humidity must decrease after water is removed. Target: " + rMid.targetHumidity() + ", Saturated: " + saturatedHum);
            context.assertTrue(rMid.currentHumidity() < saturatedHum,
                    "Dynamic humidity must dissipate gradually (< saturated)! Current: " + rMid.currentHumidity() + ", Saturated: " + saturatedHum);
            context.assertTrue(rMid.currentHumidity() > rMid.targetHumidity(),
                    "Dynamic humidity should still be dissipating (> target)! Current: " + rMid.currentHumidity() + ", Target: " + rMid.targetHumidity());

            context.complete();
        });
    }
}
