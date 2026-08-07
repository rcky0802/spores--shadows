package moldmod.block;

import net.minecraft.block.SlabBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;


public class MoldySlabBlock extends SlabBlock {

    @SuppressWarnings("this-escape")
    public MoldySlabBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState()
            .with(MoldyLogBlock.STAGE, 0)
            .with(MoldyLogBlock.WAXED, false)
            .with(MoldyLogBlock.STRUCTURAL, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(MoldyLogBlock.STAGE, MoldyLogBlock.WAXED, MoldyLogBlock.STRUCTURAL);
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
                    net.minecraft.component.type.BlockStateComponent comp = itemStack.getOrDefault(net.minecraft.component.DataComponentTypes.BLOCK_STATE, net.minecraft.component.type.BlockStateComponent.DEFAULT);
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
                    boolean bl = context.getHitPos().y - (double)context.getBlockPos().getY() > 0.5D;
                    net.minecraft.util.math.Direction direction = context.getSide();
                    if (slabType == net.minecraft.block.enums.SlabType.BOTTOM) {
                        return direction == net.minecraft.util.math.Direction.UP || bl && direction.getAxis().isHorizontal();
                    } else {
                        return direction == net.minecraft.util.math.Direction.DOWN || !bl && direction.getAxis().isHorizontal();
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
        MoldyBlockHelper.randomTick(state, world, pos, random, this);
    }

    @Override
    public float calcBlockBreakingDelta(BlockState state, PlayerEntity player, net.minecraft.world.BlockView world, BlockPos pos) {
        if (state.get(MoldyLogBlock.STAGE) == 3) {
            float f = state.getHardness(world, pos);
            if (f == -1.0F) return 0.0F;
            return 1.0F / f / 30.0F; // Same speed always
        }
        return super.calcBlockBreakingDelta(state, player, world, pos);
    }
}
