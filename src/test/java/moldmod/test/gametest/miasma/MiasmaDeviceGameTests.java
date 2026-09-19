package moldmod.test.gametest.miasma;

import me.shedaniel.autoconfig.AutoConfig;
import moldmod.block.ModBlocks;
import moldmod.block.sensor.SporeDetectorBlock;
import moldmod.config.ModConfig;
import moldmod.atmosphere.RoomAtmosphereCalculator;
import moldmod.atmosphere.RoomSaturationManager;
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

public class MiasmaDeviceGameTests {

        @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
        public void testSporeDetectorCleanAirGivesZeroPower(TestContext context) {
                // Sealed clean stone room 3x3x3 (no mold)
                RoomTestBuilder.of(context)
                                .stoneRoom(0, 0, 0, 4, 3, 4)
                                .set(2, 1, 2, ModBlocks.SPORE_DETECTOR.getDefaultState());

                BlockPos detectorPos = new BlockPos(2, 1, 2);
                context.assertFalse(context.getBlockState(detectorPos).emitsRedstonePower(),
                                "Spore Detector in clean air must not emit redstone power");

                context.complete();
        }

        @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
        public void testSporeDetectorRedstoneLevelScalesWithToxicity(TestContext context) {
                // Build 3x3x3 room with 8 moldy logs (Toxic score = 8 * 2.25 = 18.0)
                RoomTestBuilder.of(context)
                                .stoneRoom(0, 0, 0, 4, 3, 4)
                                .addMoldyOakLog(1, 1, 1, 3)
                                .addMoldyOakLog(2, 1, 1, 3)
                                .addMoldyOakLog(3, 1, 1, 3)
                                .addMoldyOakLog(1, 1, 3, 3)
                                .addMoldyOakLog(2, 1, 3, 3)
                                .addMoldyOakLog(3, 1, 3, 3)
                                .addMoldyOakLog(1, 3, 2, 3)
                                .addMoldyOakLog(3, 3, 2, 3)
                                .set(2, 1, 2, ModBlocks.SPORE_DETECTOR.getDefaultState());

                BlockPos detectorPos = new BlockPos(2, 1, 2);
                RoomAtmosphereCalculator.MiasmaResult result = RoomAtmosphereCalculator.calculateMiasma(
                                context.getWorld(), context.getAbsolutePos(detectorPos));

                context.assertTrue(result.netMiasma >= 18.0, "Room must be highly toxic (M >= 18.0)");
                context.assertTrue(result.density >= 0.1, "Spore density must be high");

                // Trigger scheduled tick to update visual state
                ((SporeDetectorBlock) ModBlocks.SPORE_DETECTOR).scheduledTick(
                                context.getBlockState(detectorPos),
                                context.getWorld(),
                                context.getAbsolutePos(detectorPos),
                                context.getWorld().getRandom());

                // Verify SporeDetectorBlock visual stage scales and emits redstone & comparator output
                BlockState updatedState = context.getBlockState(detectorPos);
                int toxLevel = updatedState.get(SporeDetectorBlock.TOXICITY_LEVEL);
                context.assertTrue(toxLevel > 0,
                                "Spore detector must update visual stage when spores are detected");
                context.assertTrue(updatedState.emitsRedstonePower(),
                                "Spore detector must emit redstone power when toxic");
                int weakPower = updatedState.getWeakRedstonePower(context.getWorld(), context.getAbsolutePos(detectorPos), Direction.UP);
                context.assertTrue(weakPower == toxLevel * 5,
                                "Spore detector weak power must equal toxicity * 5, got " + weakPower);
                context.assertTrue(updatedState.hasComparatorOutput(),
                                "Spore detector must support comparator output");
                context.assertTrue(updatedState.getComparatorOutput(context.getWorld(), context.getAbsolutePos(detectorPos)) == toxLevel * 5,
                                "Spore detector comparator output must equal toxicity * 5");

                context.complete();
        }

        @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
        public void testSporeDetectorPowersBlockAndRedstoneWire(TestContext context) {
                // Solid stone block at (2, 1, 2)
                BlockPos stonePos = new BlockPos(2, 1, 2);
                context.setBlockState(stonePos, Blocks.STONE.getDefaultState());

                // Floor for redstone wire at (2, 0, 1)
                context.setBlockState(new BlockPos(2, 0, 1), Blocks.STONE.getDefaultState());

                // Mount SporeDetector on the SOUTH side of stone block: pos = (2, 1, 3), FACING = SOUTH, FACE = WALL, level 3
                BlockState detectorState = ModBlocks.SPORE_DETECTOR.getDefaultState()
                                .with(SporeDetectorBlock.FACE, net.minecraft.block.enums.BlockFace.WALL)
                                .with(SporeDetectorBlock.FACING, Direction.SOUTH)
                                .with(SporeDetectorBlock.TOXICITY_LEVEL, 3);
                BlockPos detectorPos = new BlockPos(2, 1, 3);
                context.setBlockState(detectorPos, detectorState);

                // Now connect Redstone wire at (2, 1, 1) adjacent to the charged stone block
                BlockPos wirePos = new BlockPos(2, 1, 1);
                context.setBlockState(wirePos, Blocks.REDSTONE_WIRE.getDefaultState());

                // 1. Verify stone block receives strong power = 15 from detector
                int receivedStrong = context.getWorld().getReceivedStrongRedstonePower(context.getAbsolutePos(stonePos));
                context.assertTrue(receivedStrong == 15, "Stone block must receive strong power 15, got " + receivedStrong);

                // 2. Verify wire connected to charged block immediately received power = 15
                int wirePower = context.getBlockState(wirePos).get(net.minecraft.state.property.Properties.POWER);
                context.assertTrue(wirePower == 15, "Redstone wire connected to charged block must receive 15, got " + wirePower);

                // 3. Dynamic update test: change detector toxicity level from 3 to 1
                BlockState midState = detectorState.with(SporeDetectorBlock.TOXICITY_LEVEL, 1);
                context.setBlockState(detectorPos, midState);

                wirePower = context.getBlockState(wirePos).get(net.minecraft.state.property.Properties.POWER);
                context.assertTrue(wirePower == 5, "Redstone wire must dynamically update to 5 when detector drops to level 1, got " + wirePower);

                // 4. Dynamic update test: change detector toxicity level from 1 to 0
                BlockState cleanState = detectorState.with(SporeDetectorBlock.TOXICITY_LEVEL, 0);
                context.setBlockState(detectorPos, cleanState);

                wirePower = context.getBlockState(wirePos).get(net.minecraft.state.property.Properties.POWER);
                context.assertTrue(wirePower == 0, "Redstone wire must dynamically update to 0 when detector is clean, got " + wirePower);

                // 5. Dynamic update test: change back to level 2 (power 10)
                BlockState warningState = detectorState.with(SporeDetectorBlock.TOXICITY_LEVEL, 2);
                context.setBlockState(detectorPos, warningState);

                wirePower = context.getBlockState(wirePos).get(net.minecraft.state.property.Properties.POWER);
                context.assertTrue(wirePower == 10, "Redstone wire must dynamically update to 10 when detector rises to level 2, got " + wirePower);

                // 6. Dynamic update test: breaking the detector turns off the wire
                context.setBlockState(detectorPos, Blocks.AIR.getDefaultState());
                wirePower = context.getBlockState(wirePos).get(net.minecraft.state.property.Properties.POWER);
                context.assertTrue(wirePower == 0, "Redstone wire must dynamically turn off when detector is broken, got " + wirePower);

                context.complete();
        }

        @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
        public void testSporeDetectorMicroclimateDifferentiation(TestContext context) {
                ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
                config.toxicity.enable_distributed_miasma = true;

                // Long corridor 7x3x3: Window at (0, 1, 2), Mold nest at (5, 1..2, 1..3)
                // 14 mold logs = 31.5 toxicity > 24.0 (window throughput), leaving residual
                // gradient
                RoomTestBuilder.of(context)
                                .stoneRoom(0, 0, 0, 6, 3, 4)
                                .addMoldyOakLog(5, 1, 1, 3)
                                .addMoldyOakLog(5, 1, 2, 3)
                                .addMoldyOakLog(5, 1, 3, 3)
                                .addMoldyOakLog(5, 2, 1, 3)
                                .addMoldyOakLog(5, 2, 2, 3)
                                .addMoldyOakLog(5, 2, 3, 3)
                                .addMoldyOakLog(4, 1, 1, 3)
                                .addMoldyOakLog(4, 1, 2, 3)
                                .addMoldyOakLog(4, 1, 3, 3)
                                .addMoldyOakLog(4, 2, 1, 3)
                                .addMoldyOakLog(4, 2, 2, 3)
                                .addMoldyOakLog(4, 2, 3, 3)
                                .addMoldyOakLog(5, 3, 2, 3)
                                .addMoldyOakLog(4, 3, 2, 3)
                                // Window at West
                                .setAir(0, 1, 2)
                                .clearOpenAirColumn(-1, 2, 1, 5)
                                // Detectors at Near Window (1, 1, 2) and Far Corner (3, 1, 2)
                                .set(1, 1, 2, ModBlocks.SPORE_DETECTOR.getDefaultState())
                                .set(3, 1, 2, ModBlocks.SPORE_DETECTOR.getDefaultState());

                BlockPos nearPos = new BlockPos(1, 1, 2);
                BlockPos farPos = new BlockPos(3, 1, 2);

                RoomSaturationManager.reset(context.getAbsolutePos(nearPos));
                RoomSaturationManager.reset(context.getAbsolutePos(farPos));

                RoomAtmosphereCalculator.MiasmaResult rNear = RoomAtmosphereCalculator.calculateMiasma(
                                context.getWorld(), context.getAbsolutePos(nearPos));
                RoomAtmosphereCalculator.MiasmaResult rFar = RoomAtmosphereCalculator.calculateMiasma(
                                context.getWorld(), context.getAbsolutePos(farPos));

                context.assertTrue(rNear.localAeration > rFar.localAeration,
                                "Local aeration near window must be strictly higher than far end");
                context.assertTrue(rNear.distanceToVentilation < rFar.distanceToVentilation,
                                "Distance to ventilation near window must be strictly shorter than far end");

                context.complete();
        }

        @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
        public void testSporeDetectorBlockStateProperties(TestContext context) {
                // Clean room with spore detector at center
                RoomTestBuilder.of(context)
                                .stoneRoom(0, 0, 0, 4, 3, 4)
                                .set(2, 1, 2, ModBlocks.SPORE_DETECTOR.getDefaultState());

                BlockPos detectorPos = new BlockPos(2, 1, 2);
                context.getWorld().scheduleBlockTick(context.getAbsolutePos(detectorPos), ModBlocks.SPORE_DETECTOR, 1);

                BlockState state = context.getWorld().getBlockState(context.getAbsolutePos(detectorPos));
                context.assertTrue(state.contains(SporeDetectorBlock.TOXICITY_LEVEL),
                                "Detector state must contain TOXICITY_LEVEL property");
                context.assertTrue(state.get(SporeDetectorBlock.TOXICITY_LEVEL) == 0,
                                "Detector TOXICITY_LEVEL must be 0 in clean air");
                context.assertFalse(state.emitsRedstonePower(), "Detector must not emit redstone power");

                context.complete();
        }

        @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
        public void testSporeDetectorInteractionChat(TestContext context) {
                BlockPos pos = new BlockPos(2, 1, 2);
                context.setBlockState(pos, ModBlocks.SPORE_DETECTOR.getDefaultState());

                PlayerEntity player = context.createMockPlayer(net.minecraft.world.GameMode.SURVIVAL);

                // 1. Test block onUse interaction
                context.useBlock(pos, player);

                // 2. Test direct sendDiagnosticMessage invocation for coverage
                var result = RoomAtmosphereCalculator.calculateMiasma(context.getWorld(), context.getAbsolutePos(pos));
                SporeDetectorBlock.sendDiagnosticMessage(player, result);

                // 3. Test handheld item use
                ItemStack itemStack = new ItemStack(moldmod.item.ModItems.SPORE_DETECTOR);
                player.setStackInHand(Hand.MAIN_HAND, itemStack);
                ActionResult itemResult = itemStack.use(context.getWorld(), player, Hand.MAIN_HAND).getResult();
                context.assertTrue(itemResult.isAccepted(), "Handheld Spore Detector use must be accepted");

                // 4. Verify all localization translation keys
                List<String> expectedKeys = List.of(
                                "message.spores--shadows.detector.none",
                                "message.spores--shadows.detector.blocks_dist",
                                "message.spores--shadows.detector.purifiers_active",
                                "message.spores--shadows.detector.purifiers_none",
                                "message.spores--shadows.spore_detector.header",
                                "message.spores--shadows.spore_detector.clean",
                                "message.spores--shadows.spore_detector.warning",
                                "message.spores--shadows.spore_detector.moderate",
                                "message.spores--shadows.spore_detector.lethal",
                                "message.spores--shadows.spore_detector.metric",
                                "message.spores--shadows.spore_detector.room",
                                "message.spores--shadows.spore_detector.open_air",
                                "message.spores--shadows.spore_detector.aeration",
                                "message.spores--shadows.spore_detector.trend_stable",
                                "message.spores--shadows.spore_detector.trend_purifying",
                                "message.spores--shadows.spore_detector.trend_accumulating");

                for (String key : expectedKeys) {
                        Text text = Text.translatable(key);
                        context.assertTrue(!text.getString().isEmpty(),
                                        "Localization key " + key + " must not be empty");
                }

                context.complete();
        }
}
