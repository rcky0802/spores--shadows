package moldmod.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.enums.SlabType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BlockStateComponent;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldView;

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
    public boolean canReplace(BlockState state, ItemPlacementContext context) {
        ItemStack itemStack = context.getStack();
        SlabType slabType = state.get(TYPE);

        if (slabType != SlabType.DOUBLE) {
            boolean isMatch = false;
            int itemStage = 0;
            boolean itemWaxed = false;

            if (itemStack.getItem() instanceof BlockItem blockItem) {
                Block itemBlock = blockItem.getBlock();
                if (itemBlock == this) {
                    isMatch = true;
                    BlockStateComponent comp = itemStack.getOrDefault(
                            DataComponentTypes.BLOCK_STATE,
                            BlockStateComponent.DEFAULT);
                    Integer compStage = comp.getValue(MoldyBlock.STAGE);
                    itemStage = compStage != null ? compStage : 0;
                    Boolean compWaxed = comp.getValue(MoldyBlock.WAXED);
                    itemWaxed = compWaxed != null ? compWaxed : false;
                } else if (ModBlocks.VANILLA_TO_MOLDY.get(itemBlock) == this) {
                    isMatch = true;
                    itemStage = 0;
                    itemWaxed = false;
                }
            }

            if (isMatch) {
                if (state.get(MoldyBlock.STAGE) != itemStage || state.get(MoldyBlock.WAXED) != itemWaxed) {
                    return false;
                }

                if (context.canReplaceExisting()) {
                    boolean bl = context.getHitPos().y - (double) context.getBlockPos().getY() > 0.5D;
                    Direction direction = context.getSide();
                    if (slabType == SlabType.BOTTOM) {
                        return direction == Direction.UP
                                || bl && direction.getAxis().isHorizontal();
                    } else {
                        return direction == Direction.DOWN
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
    public ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state) {
        return MoldyBlockHelper.getPickStack(world, pos, state);
    }
}
