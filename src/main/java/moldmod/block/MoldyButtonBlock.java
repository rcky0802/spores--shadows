package moldmod.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSetType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ButtonBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;

public class MoldyButtonBlock extends ButtonBlock {

    @SuppressWarnings("this-escape")
    public MoldyButtonBlock(BlockSetType blockSetType, int pressTicks, Settings settings) {
        super(blockSetType, pressTicks, settings);
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

    public int getMoldyPressTicks(BlockState state) {
        int stage = state.get(MoldyLogBlock.STAGE);
        return switch (stage) {
            case 0 -> 30;
            case 1 -> 60;
            case 2 -> 120;
            case 3 -> 300;
            default -> 30;
        };
    }
}
