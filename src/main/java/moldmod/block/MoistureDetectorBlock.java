package moldmod.block;

import com.mojang.serialization.MapCodec;
import me.shedaniel.autoconfig.AutoConfig;
import moldmod.atmosphere.RoomAtmosphereCalculator;
import moldmod.config.ModConfig;
import moldmod.risk.MoldRiskCalculator;
import moldmod.risk.MoldRiskCalculator.MoldRiskResult;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.WallMountedBlock;
import net.minecraft.block.enums.BlockFace;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public final class MoistureDetectorBlock extends WallMountedBlock {

    public static final MapCodec<MoistureDetectorBlock> CODEC = createCodec(MoistureDetectorBlock::new);

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final EnumProperty<BlockFace> FACE = Properties.BLOCK_FACE;
    public static final IntProperty MOISTURE_STAGE = IntProperty.of("moisture_stage", 0, 3);

    // Voxel Shapes (Steampunk wall, floor and ceiling mount)
    private static final VoxelShape FLOOR_SHAPE = Block.createCuboidShape(4.0, 0.0, 4.0, 12.0, 15.0, 12.0);
    private static final VoxelShape CEILING_SHAPE = Block.createCuboidShape(4.0, 1.0, 4.0, 12.0, 16.0, 12.0);
    private static final VoxelShape NORTH_WALL_SHAPE = Block.createCuboidShape(4.0, 1.0, 13.0, 12.0, 15.0, 16.0);
    private static final VoxelShape SOUTH_WALL_SHAPE = Block.createCuboidShape(4.0, 1.0, 0.0, 12.0, 15.0, 3.0);
    private static final VoxelShape EAST_WALL_SHAPE = Block.createCuboidShape(0.0, 1.0, 4.0, 3.0, 15.0, 12.0);
    private static final VoxelShape WEST_WALL_SHAPE = Block.createCuboidShape(13.0, 1.0, 4.0, 16.0, 15.0, 12.0);

    private static ModConfig getConfig() {
        try {
            return AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        } catch (Exception e) {
            return null;
        }
    }

    public MoistureDetectorBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(FACE, BlockFace.FLOOR)
                .with(MOISTURE_STAGE, 0));
    }

    @Override
    protected MapCodec<? extends WallMountedBlock> getCodec() {
        return CODEC;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, FACE, MOISTURE_STAGE);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        BlockFace face = state.get(FACE);
        if (face == BlockFace.FLOOR) {
            return FLOOR_SHAPE;
        } else if (face == BlockFace.CEILING) {
            return CEILING_SHAPE;
        }
        Direction facing = state.get(FACING);
        return switch (facing) {
            case NORTH -> NORTH_WALL_SHAPE;
            case SOUTH -> SOUTH_WALL_SHAPE;
            case EAST -> EAST_WALL_SHAPE;
            case WEST -> WEST_WALL_SHAPE;
            default -> FLOOR_SHAPE;
        };
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!world.isClient && !state.isOf(oldState.getBlock())) {
            ModConfig config = getConfig();
            int initialDelay = (config != null && config.moistureDetector != null)
                    ? config.moistureDetector.block_initial_delay_ticks : 10;
            world.scheduleBlockTick(pos, this, initialDelay);
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        MoldRiskResult result = MoldRiskCalculator.calculate(world, pos, false, state);
        double heff = result.Heff();
        int stage = getMoistureStage(heff);

        if (state.get(MOISTURE_STAGE) != stage) {
            world.setBlockState(pos, state.with(MOISTURE_STAGE, stage), Block.NOTIFY_ALL);
        }

        ModConfig config = getConfig();
        int periodicDelay = (config != null && config.moistureDetector != null)
                ? config.moistureDetector.block_periodic_delay_ticks : 20;
        world.scheduleBlockTick(pos, this, periodicDelay);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient && world instanceof ServerWorld serverWorld) {
            MoldRiskResult result = MoldRiskCalculator.calculate(serverWorld, pos, false, state);
            sendDiagnosticMessage(player, result);
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public ItemActionResult onUseWithItem(ItemStack stack, BlockState state,
            World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient && world instanceof ServerWorld serverWorld) {
            MoldRiskResult result = MoldRiskCalculator.calculate(serverWorld, pos, false, state);
            sendDiagnosticMessage(player, result);
        }
        return ItemActionResult.SUCCESS;
    }

    public static int getMoistureStage(double heff) {
        return MoistureDetectorLogic.getMoistureStage(heff);
    }

    public static void sendDiagnosticMessage(PlayerEntity player, MoldRiskResult result) {
        // Line 1: Header + Status
        MutableText header = Text.translatable("message.spores--shadows.moisture_detector.header");

        int stage = getMoistureStage(result.Heff());
        MutableText statusText = switch (stage) {
            case 0 -> Text.translatable("message.spores--shadows.moisture_detector.dry");
            case 1 -> Text.translatable("message.spores--shadows.moisture_detector.moderate");
            case 2 -> Text.translatable("message.spores--shadows.moisture_detector.humid");
            case 3 -> Text.translatable("message.spores--shadows.moisture_detector.critical");
            default -> Text.translatable("message.spores--shadows.moisture_detector.dry");
        };

        // Private chat message without playing sound
        player.sendMessage(header.append(statusText), false);

        // Line 2: Metric
        double heffPercent = result.Heff() * 100.0;
        player.sendMessage(Text.translatable("message.spores--shadows.moisture_detector.metric",
                String.format("%.1f%%", heffPercent)), false);

        // Line 3: Room Volume & Distance to Ventilation
        if (result.roomVentilationType() == RoomAtmosphereCalculator.RoomVentilationType.CLEAN_OPEN_AIR || result.airVolume() >= 2048) {
            player.sendMessage(Text.translatable("message.spores--shadows.moisture_detector.open_air"), false);
        } else {
            String distStr = (result.distanceToVentilation() < 900)
                    ? Text.translatable("message.spores--shadows.detector.blocks_dist", result.distanceToVentilation()).getString()
                    : Text.translatable("message.spores--shadows.detector.none").getString();
            player.sendMessage(Text.translatable("message.spores--shadows.moisture_detector.room",
                    result.airVolume(), distStr), false);
        }

        // Line 4: Local Aeration & Raw Humidity
        double rawPercent = result.Hraw() * 100.0;
        player.sendMessage(Text.translatable("message.spores--shadows.moisture_detector.aeration",
                String.format("%.1f", result.aerationFlow()),
                String.format("%.1f%%", result.aeration() * 100.0),
                String.format("%.1f%%", rawPercent)), false);

        // Line 5: Dehumidifiers (and Humidifiers)
        if (result.dehumidifierCount() > 0 && result.humidifierCount() > 0) {
            double dehumPercent = result.dehumidifierDryingBonus() * 100.0;
            double humPercent = result.humidifierMoistureBonus() * 100.0;
            player.sendMessage(Text.translatable("message.spores--shadows.detector.dehumidifiers_active",
                    result.dehumidifierCount(), String.format("%.1f%%", dehumPercent)), false);
            player.sendMessage(Text.translatable("message.spores--shadows.detector.humidifiers_active",
                    result.humidifierCount(), String.format("%.1f%%", humPercent)), false);
        } else if (result.dehumidifierCount() > 0) {
            double dehumPercent = result.dehumidifierDryingBonus() * 100.0;
            player.sendMessage(Text.translatable("message.spores--shadows.detector.dehumidifiers_active",
                    result.dehumidifierCount(), String.format("%.1f%%", dehumPercent)), false);
        } else if (result.humidifierCount() > 0) {
            double humPercent = result.humidifierMoistureBonus() * 100.0;
            player.sendMessage(Text.translatable("message.spores--shadows.detector.humidifiers_active",
                    result.humidifierCount(), String.format("%.1f%%", humPercent)), false);
        } else {
            player.sendMessage(Text.translatable("message.spores--shadows.detector.dehumidifiers_none"), false);
        }

        // Line 6: Dynamic Trend
        if (result.currentHumidity() > result.targetHumidity() + 0.02) {
            player.sendMessage(Text.translatable("message.spores--shadows.moisture_detector.trend_drying",
                    String.format("%.1f%%", result.targetHumidity() * 100.0)), false);
        } else if (result.currentHumidity() < result.targetHumidity() - 0.02) {
            player.sendMessage(Text.translatable("message.spores--shadows.moisture_detector.trend_humidifying",
                    String.format("%.1f%%", result.targetHumidity() * 100.0)), false);
        } else {
            player.sendMessage(Text.translatable("message.spores--shadows.moisture_detector.trend_stable"), false);
        }
    }
}
