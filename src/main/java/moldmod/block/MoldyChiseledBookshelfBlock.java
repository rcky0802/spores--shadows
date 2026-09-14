package moldmod.block;

import moldmod.block.entity.MoldyChiseledBookshelfBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChiseledBookshelfBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChiseledBookshelfBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class MoldyChiseledBookshelfBlock extends ChiseledBookshelfBlock implements MoldyBlock {

    public MoldyChiseledBookshelfBlock(Settings settings) {
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
        return new MoldyChiseledBookshelfBlockEntity(pos, state);
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            // When transitioning between moldy and waxed variants, do not scatter inventory
            if (newState.getBlock() instanceof MoldyChiseledBookshelfBlock) {
                return;
            }
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof ChiseledBookshelfBlockEntity chiseledBookshelfBlockEntity) {
                if (!chiseledBookshelfBlockEntity.isEmpty()) {
                    for (int i = 0; i < 6; ++i) {
                        ItemStack itemStack = chiseledBookshelfBlockEntity.getStack(i);
                        if (!itemStack.isEmpty()) {
                            ItemScatterer.spawn(world, (double) pos.getX(), (double) pos.getY(), (double) pos.getZ(),
                                    itemStack);
                        }
                    }
                    chiseledBookshelfBlockEntity.clear();
                }
                world.updateComparators(pos, this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }
}
