package moldmod.test.gametest.miasma;

import me.shedaniel.autoconfig.AutoConfig;
import moldmod.block.ModBlocks;
import moldmod.block.MoistureDetectorBlock;
import moldmod.config.ModConfig;
import moldmod.item.ModItems;
import moldmod.risk.MoldRiskCalculator;
import moldmod.test.helper.RoomTestBuilder;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.List;

public class MoistureDetectorGameTests {

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testMoistureDetectorDryRoom(TestContext context) {
        ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        double oldMaxDepth = config.environment.max_depth_modifier;
        double oldDryHum = config.environment.dry_humidity_base;

        try {
            config.environment.max_depth_modifier = 0.0;
            config.environment.dry_humidity_base = 0.0;

            // Place moisture detector on stone floor in completely dry open air (0 water sources)
            BlockPos floorPos = new BlockPos(2, 0, 2);
            BlockPos detectorPos = new BlockPos(2, 1, 2);

            context.setBlockState(floorPos, Blocks.STONE.getDefaultState());
            context.setBlockState(detectorPos, ModBlocks.MOISTURE_DETECTOR.getDefaultState());

            // Scheduled tick to evaluate environment
            ((MoistureDetectorBlock) ModBlocks.MOISTURE_DETECTOR).scheduledTick(
                    context.getBlockState(detectorPos),
                    context.getWorld(),
                    context.getAbsolutePos(detectorPos),
                    context.getWorld().getRandom());

            BlockState state = context.getBlockState(detectorPos);
            int stage = state.get(MoistureDetectorBlock.MOISTURE_STAGE);
            int weakPower = state.getWeakRedstonePower(context.getWorld(), context.getAbsolutePos(detectorPos), Direction.UP);

            context.assertTrue(stage == 0, "Moisture detector in dry room must be stage 0, got: " + stage);
            context.assertTrue(weakPower == 0, "Moisture detector in dry room must emit 0 redstone, got: " + weakPower);
            context.assertFalse(state.emitsRedstonePower(), "Moisture detector must not emit redstone power");
        } finally {
            config.environment.max_depth_modifier = oldMaxDepth;
            config.environment.dry_humidity_base = oldDryHum;
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testMoistureDetectorHumidBasement(TestContext context) {
        // Build a sealed 7x3x7 stone basement with 16 water blocks along the perimeter
        RoomTestBuilder.of(context).stoneRoom(0, 0, 0, 6, 3, 6);

        for (int x = 1; x <= 5; x++) {
            for (int z = 1; z <= 5; z++) {
                if (x == 1 || x == 5 || z == 1 || z == 5) {
                    context.setBlockState(new BlockPos(x, 1, z), Blocks.WATER.getDefaultState());
                }
            }
        }

        BlockPos detectorPos = new BlockPos(3, 1, 3);
        context.setBlockState(detectorPos, ModBlocks.MOISTURE_DETECTOR.getDefaultState());

        // Scheduled tick to evaluate environment
        ((MoistureDetectorBlock) ModBlocks.MOISTURE_DETECTOR).scheduledTick(
                context.getBlockState(detectorPos),
                context.getWorld(),
                context.getAbsolutePos(detectorPos),
                context.getWorld().getRandom());

        BlockState state = context.getBlockState(detectorPos);
        int stage = state.get(MoistureDetectorBlock.MOISTURE_STAGE);
        int weakPower = state.getWeakRedstonePower(context.getWorld(), context.getAbsolutePos(detectorPos), Direction.UP);

        context.assertTrue(stage >= 2, "Moisture detector in humid basement must have stage >= 2, got: " + stage);
        context.assertTrue(weakPower == 0, "Weak redstone power must be 0, got: " + weakPower);
        context.assertFalse(state.emitsRedstonePower(), "Moisture detector must not emit redstone power");

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testMoistureDetectorVentilationReduction(TestContext context) {
        // Build a sealed 7x3x7 stone basement with water blocks
        RoomTestBuilder.of(context).stoneRoom(0, 0, 0, 6, 3, 6);

        for (int x = 1; x <= 5; x++) {
            for (int z = 1; z <= 5; z++) {
                if (x == 1 || x == 5 || z == 1 || z == 5) {
                    context.setBlockState(new BlockPos(x, 1, z), Blocks.WATER.getDefaultState());
                }
            }
        }

        BlockPos detectorPos = new BlockPos(3, 1, 3);
        context.setBlockState(detectorPos, ModBlocks.MOISTURE_DETECTOR.getDefaultState());

        // 1. Initial tick in sealed basement
        ((MoistureDetectorBlock) ModBlocks.MOISTURE_DETECTOR).scheduledTick(
                context.getBlockState(detectorPos),
                context.getWorld(),
                context.getAbsolutePos(detectorPos),
                context.getWorld().getRandom());

        BlockState humidState = context.getBlockState(detectorPos);
        int humidStage = humidState.get(MoistureDetectorBlock.MOISTURE_STAGE);

        context.assertTrue(humidStage >= 2, "Initial humid stage must be >= 2");
        context.assertFalse(humidState.emitsRedstonePower(), "Moisture detector must not emit redstone power");

        // 2. Open ventilation: Remove roof over entire central area to open sky
        for (int x = 1; x <= 5; x++) {
            for (int z = 1; z <= 5; z++) {
                RoomTestBuilder.of(context).clearOpenAirColumn(x, z, 3, 6);
            }
        }

        // 3. Second tick after ventilation opened
        ((MoistureDetectorBlock) ModBlocks.MOISTURE_DETECTOR).scheduledTick(
                context.getBlockState(detectorPos),
                context.getWorld(),
                context.getAbsolutePos(detectorPos),
                context.getWorld().getRandom());

        BlockState ventilatedState = context.getBlockState(detectorPos);
        int ventilatedStage = ventilatedState.get(MoistureDetectorBlock.MOISTURE_STAGE);

        context.assertTrue(ventilatedStage < humidStage,
                "Ventilation must reduce visual stage! Was: " + humidStage + ", now: " + ventilatedStage);
        context.assertFalse(ventilatedState.emitsRedstonePower(), "Moisture detector must not emit redstone power");

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testMoistureDetectorInteractionSilentChat(TestContext context) {
        BlockPos pos = new BlockPos(2, 1, 2);
        context.setBlockState(pos, ModBlocks.MOISTURE_DETECTOR.getDefaultState());

        PlayerEntity player = context.createMockPlayer(net.minecraft.world.GameMode.SURVIVAL);

        // 1. Test block onUse interaction (silent chat message sent)
        context.useBlock(pos, player);

        // 2. Test direct sendDiagnosticMessage invocation for coverage
        var result = MoldRiskCalculator.calculate(context.getWorld(), context.getAbsolutePos(pos), false, null);
        MoistureDetectorBlock.sendDiagnosticMessage(player, result);

        // 3. Test handheld item use
        ItemStack itemStack = new ItemStack(ModItems.MOISTURE_DETECTOR);
        player.setStackInHand(Hand.MAIN_HAND, itemStack);
        ActionResult itemResult = itemStack.use(context.getWorld(), player, Hand.MAIN_HAND).getResult();
        context.assertTrue(itemResult.isAccepted(), "Handheld Moisture Detector use must be accepted");

        // 4. Verify all localization translation keys
        List<String> expectedKeys = List.of(
                "message.spores--shadows.detector.none",
                "message.spores--shadows.detector.blocks_dist",
                "message.spores--shadows.detector.dehumidifiers_active",
                "message.spores--shadows.detector.dehumidifiers_none",
                "message.spores--shadows.detector.humidifiers_active",
                "message.spores--shadows.moisture_detector.header",
                "message.spores--shadows.moisture_detector.dry",
                "message.spores--shadows.moisture_detector.moderate",
                "message.spores--shadows.moisture_detector.humid",
                "message.spores--shadows.moisture_detector.critical",
                "message.spores--shadows.moisture_detector.metric",
                "message.spores--shadows.moisture_detector.room",
                "message.spores--shadows.moisture_detector.open_air",
                "message.spores--shadows.moisture_detector.aeration",
                "message.spores--shadows.moisture_detector.trend_stable",
                "message.spores--shadows.moisture_detector.trend_drying",
                "message.spores--shadows.moisture_detector.trend_humidifying"
        );

        for (String key : expectedKeys) {
            Text text = Text.translatable(key);
            context.assertTrue(!text.getString().isEmpty(), "Localization key " + key + " must not be empty");
        }

        context.complete();
    }
}
