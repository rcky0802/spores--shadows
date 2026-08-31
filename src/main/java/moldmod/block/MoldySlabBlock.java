package moldmod.block;

import net.minecraft.block.SlabBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public class MoldySlabBlock extends SlabBlock implements MoldyBlock {

    public MoldySlabBlock(Settings settings) {
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
    public boolean canReplace(BlockState state, net.minecraft.item.ItemPlacementContext context) {
        net.minecraft.item.ItemStack itemStack = context.getStack();
        net.minecraft.block.enums.SlabType slabType = state.get(TYPE);

        if (slabType != net.minecraft.block.enums.SlabType.DOUBLE) {
            boolean isMatch = false;
            int itemStage = 0;
            boolean itemWaxed = false;

            if (itemStack.getItem() instanceof net.minecraft.item.BlockItem blockItem) {
                net.minecraft.block.Block itemBlock = blockItem.getBlock();
                if (itemBlock == this) {
                    isMatch = true;
                    net.minecraft.component.type.BlockStateComponent comp = itemStack.getOrDefault(
                            net.minecraft.component.DataComponentTypes.BLOCK_STATE,
                            net.minecraft.component.type.BlockStateComponent.DEFAULT);
                    Integer compStage = comp.getValue(MoldyLogBlock.STAGE);
                    itemStage = compStage != null ? compStage : 0;
                    Boolean compWaxed = comp.getValue(MoldyLogBlock.WAXED);
                    itemWaxed = compWaxed != null ? compWaxed : false;
                } else if (moldmod.block.ModBlocks.VANILLA_TO_MOLDY.get(itemBlock) == this) {
                    isMatch = true;
                    itemStage = 0;
                    itemWaxed = false;
                }
            }

            if (isMatch) {
                if (state.get(MoldyLogBlock.STAGE) != itemStage || state.get(MoldyLogBlock.WAXED) != itemWaxed) {
                    return false;
                }

                if (context.canReplaceExisting()) {
                    boolean bl = context.getHitPos().y - (double) context.getBlockPos().getY() > 0.5D;
                    net.minecraft.util.math.Direction direction = context.getSide();
                    if (slabType == net.minecraft.block.enums.SlabType.BOTTOM) {
                        return direction == net.minecraft.util.math.Direction.UP
                                || bl && direction.getAxis().isHorizontal();
                    } else {
                        return direction == net.minecraft.util.math.Direction.DOWN
                                || !bl && direction.getAxis().isHorizontal();
                    }
                } else {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.randomTick(state, world, pos, random);
        MoldyBlockHelper.randomTick(state, world, pos, random);
    }

    @Override
    public net.minecraft.item.ItemStack getPickStack(net.minecraft.world.WorldView world,
            net.minecraft.util.math.BlockPos pos, net.minecraft.block.BlockState state) {
        return moldmod.block.MoldyBlockHelper.getPickStack(world, pos, state);
    }
}
