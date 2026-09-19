package moldmod.block;

import moldmod.block.entity.ModBlockEntities;
import moldmod.block.entity.MoldyChestBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class MoldyChestBlock extends ChestBlock implements MoldyBlock {

    public MoldyChestBlock(Settings settings) {
        this(settings, () -> ModBlockEntities.MOLDY_CHEST);
    }

    public MoldyChestBlock(Settings settings, Supplier<BlockEntityType<? extends ChestBlockEntity>> supplier) {
        super(settings, supplier);
        this.setDefaultState(MoldyBlockHelper.initMoldyDefaultState(
                this.stateManager.getDefaultState()
                        .with(FACING, Direction.NORTH)
                        .with(CHEST_TYPE, ChestType.SINGLE)
                        .with(WATERLOGGED, false)
        ));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        MoldyBlockHelper.appendMoldyProperties(builder);
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return MoldyBlockHelper.hasRandomTicks(state);
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.randomTick(state, world, pos, random);
        MoldyBlockHelper.randomTick(state, world, pos, random);
    }

    @Override
    public ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state) {
        return MoldyBlockHelper.getPickStack(world, pos, state);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new MoldyChestBlockEntity(pos, state);
    }

    @Override
    protected net.minecraft.screen.NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        net.minecraft.screen.NamedScreenHandlerFactory factory = super.createScreenHandlerFactory(state, world, pos);
        if (factory == null) return null;
        int stage = state.contains(MoldyBlock.STAGE) ? state.get(MoldyBlock.STAGE) : 0;
        return new net.minecraft.screen.NamedScreenHandlerFactory() {
            @Override
            public net.minecraft.text.Text getDisplayName() {
                return factory.getDisplayName();
            }

            @Override
            public net.minecraft.screen.ScreenHandler createMenu(int syncId, net.minecraft.entity.player.PlayerInventory playerInventory, PlayerEntity player) {
                net.minecraft.screen.ScreenHandler handler = factory.createMenu(syncId, playerInventory, player);
                if (handler instanceof moldmod.screen.MoldStageHolder holder) {
                    holder.spores_shadows$setMoldStage(stage);
                }
                return handler;
            }
        };
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            // When transitioning between moldy and waxed variants, do not scatter inventory
            if (newState.getBlock() instanceof MoldyChestBlock) {
                return;
            }
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof Inventory inventory) {
                ItemScatterer.spawn(world, pos, inventory);
                world.updateComparators(pos, this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        ChestType chestType = ChestType.SINGLE;
        Direction direction = ctx.getHorizontalPlayerFacing().getOpposite();
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        boolean bl = ctx.shouldCancelInteraction();
        Direction direction2 = ctx.getSide();

        BlockState defaultState = this.getDefaultState();
        int currentStage = defaultState.contains(MoldyBlock.STAGE) ? defaultState.get(MoldyBlock.STAGE) : 0;
        boolean currentWaxed = defaultState.contains(MoldyBlock.WAXED) && defaultState.get(MoldyBlock.WAXED);

        net.minecraft.component.type.BlockStateComponent comp = ctx.getStack().get(net.minecraft.component.DataComponentTypes.BLOCK_STATE);
        if (comp != null) {
            Integer s = comp.getValue(MoldyBlock.STAGE);
            if (s != null) currentStage = s;
            Boolean w = comp.getValue(MoldyBlock.WAXED);
            if (w != null) currentWaxed = w;
        }

        if (direction2.getAxis().isHorizontal() && bl) {
            Direction direction3 = this.getMatchingNeighborChestDirection(ctx, direction2.getOpposite(), currentStage, currentWaxed);
            if (direction3 != null && direction3.getAxis() != direction2.getAxis()) {
                direction = direction3;
                chestType = direction.rotateYCounterclockwise() == direction2.getOpposite() ? ChestType.RIGHT : ChestType.LEFT;
            }
        }

        if (chestType == ChestType.SINGLE && !bl) {
            if (direction == this.getMatchingNeighborChestDirection(ctx, direction.rotateYClockwise(), currentStage, currentWaxed)) {
                chestType = ChestType.LEFT;
            } else if (direction == this.getMatchingNeighborChestDirection(ctx, direction.rotateYCounterclockwise(), currentStage, currentWaxed)) {
                chestType = ChestType.RIGHT;
            }
        }

        return MoldyBlockHelper.initMoldyDefaultState(
                this.getDefaultState()
                        .with(FACING, direction)
                        .with(CHEST_TYPE, chestType)
                        .with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER)
        ).with(MoldyBlock.STAGE, currentStage).with(MoldyBlock.WAXED, currentWaxed);
    }

    public boolean isMatchingChestBlock(Block block) {
        if (this instanceof MoldyTrappedChestBlock) {
            return block instanceof MoldyTrappedChestBlock;
        }
        return block instanceof MoldyChestBlock && !(block instanceof MoldyTrappedChestBlock);
    }

    @Nullable
    private Direction getMatchingNeighborChestDirection(ItemPlacementContext ctx, Direction dir, int stage, boolean waxed) {
        BlockState blockState = ctx.getWorld().getBlockState(ctx.getBlockPos().offset(dir));
        if (isMatchingChestBlock(blockState.getBlock()) && blockState.contains(CHEST_TYPE) && blockState.get(CHEST_TYPE) == ChestType.SINGLE) {
            int neighborStage = blockState.contains(MoldyBlock.STAGE) ? blockState.get(MoldyBlock.STAGE) : 0;
            boolean neighborWaxed = blockState.contains(MoldyBlock.WAXED) && blockState.get(MoldyBlock.WAXED);
            if (neighborStage == stage && neighborWaxed == waxed) {
                return blockState.get(FACING);
            }
        }
        return null;
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState,
                                                   WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(WATERLOGGED)) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

        if (isMatchingChestBlock(neighborState.getBlock()) && direction.getAxis().isHorizontal()) {
            ChestType neighborChestType = neighborState.get(CHEST_TYPE);
            int neighborStage = neighborState.contains(MoldyBlock.STAGE) ? neighborState.get(MoldyBlock.STAGE) : 0;
            boolean neighborWaxed = neighborState.contains(MoldyBlock.WAXED) && neighborState.get(MoldyBlock.WAXED);
            int stage = state.contains(MoldyBlock.STAGE) ? state.get(MoldyBlock.STAGE) : 0;
            boolean waxed = state.contains(MoldyBlock.WAXED) && state.get(MoldyBlock.WAXED);

            if (state.get(CHEST_TYPE) == ChestType.SINGLE && neighborChestType != ChestType.SINGLE
                    && state.get(FACING) == neighborState.get(FACING) && getFacing(neighborState) == direction.getOpposite()) {
                if (neighborStage == stage && neighborWaxed == waxed) {
                    return state.with(CHEST_TYPE, neighborChestType.getOpposite());
                }
            } else if (getFacing(state) == direction) {
                if (neighborChestType == ChestType.SINGLE || state.get(FACING) != neighborState.get(FACING)
                        || neighborStage != stage || neighborWaxed != waxed) {
                    return state.with(CHEST_TYPE, ChestType.SINGLE);
                }
            }
        } else if (getFacing(state) == direction) {
            return state.with(CHEST_TYPE, ChestType.SINGLE);
        }

        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        ActionResult result = super.onUse(state, world, pos, player, hit);
        if (result.isAccepted() && !world.isClient()) {
            PiglinBrain.onGuardedBlockInteracted(player, true);
            int stage = state.contains(MoldyBlock.STAGE) ? state.get(MoldyBlock.STAGE) : 0;
            if (stage >= 2) {
                world.playSound(null, pos, SoundEvents.BLOCK_MUD_STEP, SoundCategory.BLOCKS, 0.7f, 0.8f);
                if (world instanceof ServerWorld serverWorld) {
                    serverWorld.spawnParticles(ParticleTypes.SPORE_BLOSSOM_AIR,
                            pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                            8, 0.25, 0.25, 0.25, 0.01);
                }
            }
        }
        return result;
    }
}
