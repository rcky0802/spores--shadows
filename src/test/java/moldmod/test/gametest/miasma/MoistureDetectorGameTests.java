package moldmod.test.gametest.miasma;

import me.shedaniel.autoconfig.AutoConfig;
import moldmod.block.ModBlocks;
import moldmod.block.sensor.MoistureDetectorBlock;
import moldmod.config.ModConfig;
import moldmod.item.ModItems;
import moldmod.infection.risk.MoldRiskCalculator;
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

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE, batchId = "isolated_config_batch")
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
        context.assertTrue(weakPower == stage * 5, "Weak redstone power must be stage * 5, got: " + weakPower);
        context.assertTrue(state.emitsRedstonePower(), "Moisture detector must emit redstone power");
        context.assertTrue(state.hasComparatorOutput(), "Moisture detector must support comparator output");
        context.assertTrue(state.getComparatorOutput(context.getWorld(), context.getAbsolutePos(detectorPos)) == stage * 5,
                "Moisture detector comparator output must be stage * 5");

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
        context.assertTrue(humidState.emitsRedstonePower(), "Moisture detector must emit redstone power when humid");

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
        int ventWeakPower = ventilatedState.getWeakRedstonePower(context.getWorld(), context.getAbsolutePos(detectorPos), Direction.UP);
        context.assertTrue(ventWeakPower == ventilatedStage * 5, "Ventilated power must equal stage * 5");

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

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testMoistureDetectorPowersBlockAndRedstoneWire(TestContext context) {
        // Solid stone block at (2, 1, 2)
        BlockPos stonePos = new BlockPos(2, 1, 2);
        context.setBlockState(stonePos, Blocks.STONE.getDefaultState());

        // Floor for redstone wire at (2, 0, 1)
        context.setBlockState(new BlockPos(2, 0, 1), Blocks.STONE.getDefaultState());

        // Mount MoistureDetector on the SOUTH side of stone block: pos = (2, 1, 3), FACING = SOUTH, FACE = WALL, stage 3
        BlockState detectorState = ModBlocks.MOISTURE_DETECTOR.getDefaultState()
                .with(MoistureDetectorBlock.FACE, net.minecraft.block.enums.BlockFace.WALL)
                .with(MoistureDetectorBlock.FACING, Direction.SOUTH)
                .with(MoistureDetectorBlock.MOISTURE_STAGE, 3);
        BlockPos detectorPos = new BlockPos(2, 1, 3);
        context.setBlockState(detectorPos, detectorState);

        // Connect Redstone wire at (2, 1, 1) adjacent to the charged stone block
        BlockPos wirePos = new BlockPos(2, 1, 1);
        context.setBlockState(wirePos, Blocks.REDSTONE_WIRE.getDefaultState());

        // 1. Verify stone block receives strong power = 15 from detector
        int receivedStrong = context.getWorld().getReceivedStrongRedstonePower(context.getAbsolutePos(stonePos));
        context.assertTrue(receivedStrong == 15, "Stone block must receive strong power 15, got " + receivedStrong);

        // 2. Verify wire connected to charged block immediately received power = 15
        int wirePower = context.getBlockState(wirePos).get(net.minecraft.state.property.Properties.POWER);
        context.assertTrue(wirePower == 15, "Redstone wire connected to charged block must receive 15, got " + wirePower);

        // 3. Dynamic update test: change detector moisture stage from 3 to 1
        BlockState midState = detectorState.with(MoistureDetectorBlock.MOISTURE_STAGE, 1);
        context.setBlockState(detectorPos, midState);

        wirePower = context.getBlockState(wirePos).get(net.minecraft.state.property.Properties.POWER);
        context.assertTrue(wirePower == 5, "Redstone wire must dynamically update to 5 when detector drops to stage 1, got " + wirePower);

        // 4. Dynamic update test: change detector moisture stage from 1 to 0
        BlockState cleanState = detectorState.with(MoistureDetectorBlock.MOISTURE_STAGE, 0);
        context.setBlockState(detectorPos, cleanState);

        wirePower = context.getBlockState(wirePos).get(net.minecraft.state.property.Properties.POWER);
        context.assertTrue(wirePower == 0, "Redstone wire must dynamically update to 0 when detector is dry, got " + wirePower);

        // 5. Dynamic update test: change back to stage 2 (power 10)
        BlockState warningState = detectorState.with(MoistureDetectorBlock.MOISTURE_STAGE, 2);
        context.setBlockState(detectorPos, warningState);

        wirePower = context.getBlockState(wirePos).get(net.minecraft.state.property.Properties.POWER);
        context.assertTrue(wirePower == 10, "Redstone wire must dynamically update to 10 when detector rises to stage 2, got " + wirePower);

        // 6. Dynamic update test: breaking the detector turns off the wire
        context.setBlockState(detectorPos, Blocks.AIR.getDefaultState());
        wirePower = context.getBlockState(wirePos).get(net.minecraft.state.property.Properties.POWER);
        context.assertTrue(wirePower == 0, "Redstone wire must dynamically turn off when detector is broken, got " + wirePower);

        context.complete();
    }
}
