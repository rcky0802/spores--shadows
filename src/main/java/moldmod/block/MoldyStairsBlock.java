package moldmod.block;

import net.minecraft.block.StairsBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;


public class MoldyStairsBlock extends StairsBlock {

    @SuppressWarnings("this-escape")
    public MoldyStairsBlock(BlockState baseBlockState, Settings settings) {
        super(baseBlockState, settings);
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

