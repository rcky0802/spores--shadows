package moldmod.block.redstone;
import moldmod.block.core.MoldyBlock;
import moldmod.block.core.MoldyBlockHelper;

import moldmod.block.entity.ModBlockEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.JukeboxBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.JukeboxBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class MoldyJukeboxBlock extends JukeboxBlock implements MoldyBlock {

    public MoldyJukeboxBlock(Settings settings) {
        super(settings);
        this.setDefaultState(MoldyBlockHelper.initMoldyDefaultState(this.getDefaultState()));
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
        return new MoldyJukeboxBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return state.get(HAS_RECORD) ? validateTicker(type, ModBlockEntities.MOLDY_JUKEBOX, MoldyJukeboxBlockEntity::tick) : null;
    }

    @Override
    protected net.minecraft.util.ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos,
            net.minecraft.entity.player.PlayerEntity player, net.minecraft.util.Hand hand, net.minecraft.util.hit.BlockHitResult hit) {
        if (state.get(HAS_RECORD)) {
            return net.minecraft.util.ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        net.minecraft.component.type.JukeboxPlayableComponent playable = stack.get(net.minecraft.component.DataComponentTypes.JUKEBOX_PLAYABLE);
        if (playable != null) {
            if (!world.isClient) {
                ItemStack discToInsert = stack.splitUnlessCreative(1, player);
                BlockEntity blockEntity = world.getBlockEntity(pos);
                if (blockEntity instanceof JukeboxBlockEntity jukeboxBlockEntity) {
                    jukeboxBlockEntity.setStack(discToInsert);
                    world.emitGameEvent(net.minecraft.world.event.GameEvent.BLOCK_CHANGE, pos, net.minecraft.world.event.GameEvent.Emitter.of(player, state));
                }
                player.incrementStat(net.minecraft.stat.Stats.PLAY_RECORD);
            }
            return net.minecraft.util.ItemActionResult.success(world.isClient);
        }

        return net.minecraft.util.ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            // When transitioning between moldy and waxed variants, do not drop record
            if (newState.getBlock() instanceof MoldyJukeboxBlock) {
                return;
            }
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof JukeboxBlockEntity jukeboxBlockEntity) {
                jukeboxBlockEntity.dropRecord();
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }
}
