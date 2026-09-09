package moldmod.test.gametest.miasma;

import me.shedaniel.autoconfig.AutoConfig;
import moldmod.config.ModConfig;
import moldmod.atmosphere.RoomAtmosphereCalculator;
import moldmod.test.helper.RoomTestBuilder;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public class MiasmaDynamicKineticsGameTests {

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testActiveDecontaminationScrapingMold(TestContext context) {
        ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        config.toxicity.enable_dynamic_spore_saturation = true;
        config.toxicity.dissipation_speed_multiplier = 0.35;

        // Add 2 moldy logs on wall: Toxic Score = 2 * 4.0 = 8.0
        RoomTestBuilder.of(context)
                .stoneRoom(0, 0, 0, 4, 3, 4)
                .addMoldyOakLog(1, 1, 0, 3)
                .addMoldyOakLog(2, 1, 0, 3);

        BlockPos centerPos = new BlockPos(2, 1, 2);
        RoomAtmosphereCalculator.MiasmaResult initial = RoomAtmosphereCalculator.calculateMiasma(
                context.getWorld(), context.getAbsolutePos(centerPos));

        context.assertTrue(initial.targetMiasma == 8.0, "Initial target miasma should be 8.0");
        context.assertTrue(initial.netMiasma == 8.0, "Initial net miasma should be 8.0");

        // Player actively scrapes / cleans all mold blocks (replaces with stone)
        RoomTestBuilder.of(context)
                .set(1, 1, 0, Blocks.STONE.getDefaultState())
                .set(2, 1, 0, Blocks.STONE.getDefaultState());

        // Wait 40 ticks and observe immediate return to CLEAN state
        context.waitAndRun(40, () -> {
            RoomAtmosphereCalculator.MiasmaResult purged = RoomAtmosphereCalculator.calculateMiasma(
                    context.getWorld(), context.getAbsolutePos(centerPos));

            context.assertTrue(purged.targetMiasma == 0.0,
                    "Target miasma must become 0.0 after clearing mold");
            context.assertTrue(purged.netMiasma == 0.0,
                    "Net miasma must be 0.0 after clearing all mold, got: " + purged.netMiasma);
            context.assertTrue(purged.level == RoomAtmosphereCalculator.AirToxicityLevel.CLEAN,
                    "Air toxicity level must be CLEAN after clearing mold");

            context.complete();
        });
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testWallBreachVentilationPurge(TestContext context) {
        ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        config.toxicity.enable_dynamic_spore_saturation = true;
        config.toxicity.dissipation_speed_multiplier = 0.35;

        // Build sealed room 3x3x3 with moldy logs
        RoomTestBuilder.of(context)
                .stoneRoom(0, 0, 0, 4, 3, 4)
                .addMoldyOakLog(1, 1, 1, 3)
                .addMoldyOakLog(2, 1, 1, 3);

        BlockPos centerPos = new BlockPos(2, 1, 2);
        RoomAtmosphereCalculator.MiasmaResult initial = RoomAtmosphereCalculator.calculateMiasma(
                context.getWorld(), context.getAbsolutePos(centerPos));

        context.assertTrue(initial.netMiasma == 8.0, "Initial sealed miasma should be 8.0");

        // Break wall at (2, 1, 4) breaching to open sky
        RoomTestBuilder.of(context)
                .setAir(2, 1, 4)
                .setAir(2, 2, 4)
                .clearOpenAirColumn(2, 5, 1, 5);

        context.waitAndRun(40, () -> {
            RoomAtmosphereCalculator.MiasmaResult purged = RoomAtmosphereCalculator.calculateMiasma(
                    context.getWorld(), context.getAbsolutePos(centerPos));

            context.assertTrue(purged.targetMiasma == 0.0,
                    "Target miasma must be 0.0 after breach to outside");
            context.assertTrue(purged.netMiasma < 8.0,
                    "Miasma must be decreasing through wall breach");

            context.complete();
        });
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testHermeticRoomRetainsConcentration(TestContext context) {
        ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        config.toxicity.enable_dynamic_spore_saturation = true;

        // Build hermetic stone room 3x3x3 with 2 moldy logs (Toxic score = 8.0)
        RoomTestBuilder.of(context)
                .stoneRoom(0, 0, 0, 4, 3, 4)
                .addMoldyOakLog(1, 1, 1, 3)
                .addMoldyOakLog(2, 1, 1, 3);

        BlockPos centerPos = new BlockPos(2, 1, 2);
        RoomAtmosphereCalculator.MiasmaResult initial = RoomAtmosphereCalculator.calculateMiasma(
                context.getWorld(), context.getAbsolutePos(centerPos));

        context.assertTrue(initial.netMiasma == 8.0, "Hermetic room initial miasma should be 8.0");
        context.assertTrue(initial.ventilationType == RoomAtmosphereCalculator.RoomVentilationType.HERMETIC_SEALED,
                "Room with no gaps must be HERMETIC_SEALED");

        // Wait 40 ticks: with no ventilation and constant mold source, net miasma must remain at 8.0
        context.waitAndRun(40, () -> {
            RoomAtmosphereCalculator.MiasmaResult sustained = RoomAtmosphereCalculator.calculateMiasma(
                    context.getWorld(), context.getAbsolutePos(centerPos));

            context.assertTrue(sustained.targetMiasma == 8.0, "Target miasma remains 8.0 in hermetic room");
            context.assertTrue(sustained.netMiasma == 8.0, "Hermetic sealed room retains its equilibrium miasma");

            context.complete();
        });
    }
}
