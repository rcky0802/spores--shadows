package moldmod.block.workstation;
import moldmod.block.core.MoldyBlock;
import moldmod.block.core.MoldyBlockHelper;

import net.minecraft.block.BarrelBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class MoldyBarrelBlock extends BarrelBlock implements MoldyBlock {

    public MoldyBarrelBlock(Settings settings) {
        super(settings);
        this.setDefaultState(MoldyBlockHelper.initMoldyDefaultState(
                this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(OPEN, false)
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
        return new MoldyBarrelBlockEntity(pos, state);
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            // When transitioning between moldy and waxed variants, do not scatter inventory
            if (newState.getBlock() instanceof MoldyBarrelBlock) {
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
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        } else {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof BarrelBlockEntity barrelBlockEntity) {
                int stage = state.contains(MoldyBlock.STAGE) ? state.get(MoldyBlock.STAGE) : 0;
                player.openHandledScreen(new net.minecraft.screen.NamedScreenHandlerFactory() {
                    @Override
                    public net.minecraft.text.Text getDisplayName() {
                        return barrelBlockEntity.getDisplayName();
                    }

                    @Override
                    public net.minecraft.screen.ScreenHandler createMenu(int syncId, net.minecraft.entity.player.PlayerInventory playerInventory, PlayerEntity player) {
                        net.minecraft.screen.ScreenHandler handler = net.minecraft.screen.GenericContainerScreenHandler.createGeneric9x3(syncId, playerInventory, barrelBlockEntity);
                        if (handler instanceof moldmod.screen.MoldStageHolder holder) {
                            holder.spores_shadows$setMoldStage(stage);
                        }
                        return handler;
                    }
                });
                player.incrementStat(Stats.OPEN_BARREL);
                PiglinBrain.onGuardedBlockInteracted(player, true);

                if (stage >= 2) {
                    world.playSound(null, pos, SoundEvents.BLOCK_MUD_STEP, SoundCategory.BLOCKS, 0.7f, 0.8f);
                    if (world instanceof ServerWorld serverWorld) {
                        serverWorld.spawnParticles(ParticleTypes.SPORE_BLOSSOM_AIR,
                                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                                6, 0.2, 0.2, 0.2, 0.01);
                    }
                }
            }
            return ActionResult.CONSUME;
        }
    }
}
