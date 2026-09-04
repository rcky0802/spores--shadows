package moldmod.block;

import com.mojang.serialization.MapCodec;
import moldmod.event.ToxicAirEvent;
import moldmod.event.ToxicAirEvent.MiasmaResult;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.WallMountedBlock;
import net.minecraft.block.enums.BlockFace;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
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
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class SporeDetectorBlock extends WallMountedBlock {

    public static final MapCodec<SporeDetectorBlock> CODEC = createCodec(SporeDetectorBlock::new);

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final EnumProperty<BlockFace> FACE = Properties.BLOCK_FACE;
    public static final IntProperty TOXICITY_LEVEL = IntProperty.of("toxicity_level", 0, 3);
    public static final IntProperty POWER = Properties.POWER;

    // Voxel Shapes
    private static final VoxelShape FLOOR_SHAPE = Block.createCuboidShape(4.0, 0.0, 4.0, 12.0, 15.0, 12.0);
    private static final VoxelShape CEILING_SHAPE = Block.createCuboidShape(4.0, 1.0, 4.0, 12.0, 16.0, 12.0);
    private static final VoxelShape NORTH_WALL_SHAPE = Block.createCuboidShape(4.0, 1.0, 13.0, 12.0, 15.0, 16.0);
    private static final VoxelShape SOUTH_WALL_SHAPE = Block.createCuboidShape(4.0, 1.0, 0.0, 12.0, 15.0, 3.0);
    private static final VoxelShape EAST_WALL_SHAPE = Block.createCuboidShape(0.0, 1.0, 4.0, 3.0, 15.0, 12.0);
    private static final VoxelShape WEST_WALL_SHAPE = Block.createCuboidShape(13.0, 1.0, 4.0, 16.0, 15.0, 12.0);

    public SporeDetectorBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(FACE, BlockFace.FLOOR)
                .with(TOXICITY_LEVEL, 0)
                .with(POWER, 0));
    }

    @Override
    protected MapCodec<? extends WallMountedBlock> getCodec() {
        return CODEC;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, FACE, TOXICITY_LEVEL, POWER);
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
            world.scheduleBlockTick(pos, this, 10);
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        MiasmaResult result = ToxicAirEvent.calculateMiasma(world, pos);

        int toxLevel = switch (result.level) {
            case CLEAN -> 0;
            case WARNING -> 1;
            case MODERATE_HUNGER -> 2;
            case LETHAL_POISON -> 3;
        };

        // Calcolo emissione Redstone proporzionale allo stage di tossicità (0, 1, 2, 3)
        // Stage 0 -> Power 0 (Off)
        // Stage 1 -> Power 5
        // Stage 2 -> Power 10
        // Stage 3 -> Power 15 (Max)
        int redstonePower = toxLevel * 5;

        if (state.get(TOXICITY_LEVEL) != toxLevel || state.get(POWER) != redstonePower) {
            world.setBlockState(pos, state.with(TOXICITY_LEVEL, toxLevel).with(POWER, redstonePower), Block.NOTIFY_ALL);
        }

        // Programma il prossimo controllo
        world.scheduleBlockTick(pos, this, 30);
    }

    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return state.get(POWER) > 0;
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.get(POWER);
    }

    @Override
    public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.get(FACE) == BlockFace.FLOOR && direction == Direction.UP ? state.get(POWER) : 0;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient && world instanceof ServerWorld serverWorld) {
            MiasmaResult result = ToxicAirEvent.calculateMiasma(serverWorld, pos);
            sendDiagnosticMessage((ServerPlayerEntity) player, result, state.get(POWER));
            world.playSound(null, pos, SoundEvents.BLOCK_COPPER_BULB_TURN_ON, SoundCategory.BLOCKS, 0.8f, 1.2f);
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public net.minecraft.util.ItemActionResult onUseWithItem(net.minecraft.item.ItemStack stack, BlockState state,
            World world, BlockPos pos, PlayerEntity player, net.minecraft.util.Hand hand, BlockHitResult hit) {
        if (!world.isClient && world instanceof ServerWorld serverWorld) {
            MiasmaResult result = ToxicAirEvent.calculateMiasma(serverWorld, pos);
            sendDiagnosticMessage((ServerPlayerEntity) player, result, state.get(POWER));
            world.playSound(null, pos, SoundEvents.BLOCK_COPPER_BULB_TURN_ON, SoundCategory.BLOCKS, 0.8f, 1.2f);
        }
        return net.minecraft.util.ItemActionResult.SUCCESS;
    }

    public static void sendDiagnosticMessage(ServerPlayerEntity player, MiasmaResult result, int redstonePower) {
        MutableText header = Text.literal("§6[Miasma Scanner] ");

        MutableText statusText = switch (result.level) {
            case CLEAN -> Text.literal("§aCLEAN AIR §7(Safe)");
            case WARNING -> Text.literal("§eWARNING §7(Low Spores floating in air)");
            case MODERATE_HUNGER -> Text.literal("§6MODERATE RISK §7(Hunger imminent)");
            case LETHAL_POISON -> Text.literal("§4LETHAL HAZARD §7(Poison & Nausea imminent!)");
        };

        String trend = "§a= STABLE";
        if (result.netMiasma > result.targetMiasma + 0.05) {
            trend = String.format("§b▼ PURIFYING / DISSIPATING §7(Target: §f%.2f§7)", result.targetMiasma);
        } else if (result.netMiasma < result.targetMiasma - 0.05) {
            trend = String.format("§c▲ ACCUMULATING / SATURATING §7(Target: §f%.2f§7)", result.targetMiasma);
        }

        // Invio diagnostica privata in CHAT solo al giocatore che ha usato lo strumento
        player.sendMessage(header.append(statusText), false);
        player.sendMessage(Text.literal(String.format("§7- Volume: §f%d blocks §7| Ventilation Flow: §a%.2f",
                result.volume, result.ventilationScore)), false);
        player.sendMessage(
                Text.literal(String.format("§7- Current Miasma: §6%.2f §7| Density: §d%.3f/b §7| Redstone: §c%d",
                        result.netMiasma, result.density, redstonePower)),
                false);
        player.sendMessage(Text.literal(String.format("§7- Status: %s", trend)), false);
    }
}
