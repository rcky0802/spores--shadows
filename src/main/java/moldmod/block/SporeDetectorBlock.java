package moldmod.block;

import com.mojang.serialization.MapCodec;
import me.shedaniel.autoconfig.AutoConfig;
import moldmod.atmosphere.RoomAtmosphereCalculator;
import moldmod.atmosphere.RoomAtmosphereCalculator.MiasmaResult;
import moldmod.config.ModConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.WallMountedBlock;
import net.minecraft.block.enums.BlockFace;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
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

public final class SporeDetectorBlock extends WallMountedBlock {

    public static final MapCodec<SporeDetectorBlock> CODEC = createCodec(SporeDetectorBlock::new);

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final EnumProperty<BlockFace> FACE = Properties.BLOCK_FACE;
    public static final IntProperty TOXICITY_LEVEL = IntProperty.of("toxicity_level", 0, 3);

    // Voxel Shapes
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

    public SporeDetectorBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(FACE, BlockFace.FLOOR)
                .with(TOXICITY_LEVEL, 0));
    }

    @Override
    protected MapCodec<? extends WallMountedBlock> getCodec() {
        return CODEC;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, FACE, TOXICITY_LEVEL);
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
            int initialDelay = (config != null && config.sporeDetector != null)
                    ? config.sporeDetector.block_initial_delay_ticks
                    : 10;
            world.scheduleBlockTick(pos, this, initialDelay);
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        MiasmaResult result = RoomAtmosphereCalculator.calculateMiasma(world, pos);
        ModConfig config = getConfig();

        int toxLevel = switch (result.level) {
            case CLEAN -> 0;
            case WARNING -> 1;
            case MODERATE_HUNGER -> 2;
            case LETHAL_POISON -> 3;
        };

        if (state.get(TOXICITY_LEVEL) != toxLevel) {
            world.setBlockState(pos, state.with(TOXICITY_LEVEL, toxLevel), Block.NOTIFY_ALL);
        }

        // Programma il prossimo controllo
        int periodicDelay = (config != null && config.sporeDetector != null)
                ? config.sporeDetector.block_periodic_delay_ticks
                : 30;
        world.scheduleBlockTick(pos, this, periodicDelay);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient && world instanceof ServerWorld serverWorld) {
            MiasmaResult result = RoomAtmosphereCalculator.calculateMiasma(serverWorld, pos);
            sendDiagnosticMessage(player, result);
            world.playSound(null, pos, SoundEvents.BLOCK_COPPER_BULB_TURN_ON, SoundCategory.BLOCKS, 0.8f, 1.2f);
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public ItemActionResult onUseWithItem(ItemStack stack, BlockState state,
            World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient && world instanceof ServerWorld serverWorld) {
            MiasmaResult result = RoomAtmosphereCalculator.calculateMiasma(serverWorld, pos);
            sendDiagnosticMessage(player, result);
            world.playSound(null, pos, SoundEvents.BLOCK_COPPER_BULB_TURN_ON, SoundCategory.BLOCKS, 0.8f, 1.2f);
        }
        return ItemActionResult.SUCCESS;
    }

    public static void sendDiagnosticMessage(PlayerEntity player, MiasmaResult result) {
        MutableText header = Text.translatable("message.spores--shadows.spore_detector.header");

        MutableText statusText = switch (result.level) {
            case CLEAN -> Text.translatable("message.spores--shadows.spore_detector.clean");
            case WARNING -> Text.translatable("message.spores--shadows.spore_detector.warning");
            case MODERATE_HUNGER -> Text.translatable("message.spores--shadows.spore_detector.moderate");
            case LETHAL_POISON -> Text.translatable("message.spores--shadows.spore_detector.lethal");
        };

        // Line 1: Header + Status
        player.sendMessage(header.append(statusText), false);

        // Line 2: Metric
        player.sendMessage(Text.translatable("message.spores--shadows.spore_detector.metric",
                String.format("%.3f", result.localDensity)), false);

        // Line 3: Room Volume & Distance to Ventilation
        if (result.openAir || result.volume >= 2048) {
            player.sendMessage(Text.translatable("message.spores--shadows.spore_detector.open_air"), false);
        } else {
            String distStr = (result.distanceToVentilation < 900)
                    ? Text.translatable("message.spores--shadows.detector.blocks_dist", result.distanceToVentilation)
                            .getString()
                    : Text.translatable("message.spores--shadows.detector.none").getString();
            player.sendMessage(Text.translatable("message.spores--shadows.spore_detector.room",
                    result.volume, distStr), false);
        }

        // Line 4: Local Aeration & Room Miasma
        player.sendMessage(Text.translatable("message.spores--shadows.spore_detector.aeration",
                String.format("%.1f", result.localFlow),
                String.format("%.1f%%", result.localAeration * 100.0),
                String.format("%.2f", result.netMiasma)), false);

        // Line 5: Dynamic Trend
        if (result.netMiasma > result.targetMiasma + 0.05) {
            player.sendMessage(Text.translatable("message.spores--shadows.spore_detector.trend_purifying",
                    String.format("%.2f", result.targetMiasma)), false);
        } else if (result.netMiasma < result.targetMiasma - 0.05) {
            player.sendMessage(Text.translatable("message.spores--shadows.spore_detector.trend_accumulating",
                    String.format("%.2f", result.targetMiasma)), false);
        } else {
            player.sendMessage(Text.translatable("message.spores--shadows.spore_detector.trend_stable"), false);
        }
    }
}
