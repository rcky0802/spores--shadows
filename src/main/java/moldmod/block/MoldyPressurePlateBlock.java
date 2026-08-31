package moldmod.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSetType;
import net.minecraft.block.BlockState;
import net.minecraft.block.PressurePlateBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.entity.Entity;

public class MoldyPressurePlateBlock extends PressurePlateBlock implements MoldyBlock {

    public MoldyPressurePlateBlock(BlockSetType type, Settings settings) {
        super(type, settings);
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
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (!state.get(POWERED) && MoldyBlockHelper.tryBreakRottenBlock(world, pos, state, 0.10f)) {
            return; // Stop processing, block is destroyed
        }
        super.onEntityCollision(state, world, pos, entity);
    }

    public int getMoldyPressTicks(BlockState state) {
        int stage = state.get(MoldyLogBlock.STAGE);
        return switch (stage) {
            case 0 -> 20;
            case 1 -> 40;
            case 2 -> 100;
            case 3 -> 300;
            default -> 20;
        };
    }

    @Override
    public net.minecraft.item.ItemStack getPickStack(net.minecraft.world.WorldView world,
            net.minecraft.util.math.BlockPos pos, net.minecraft.block.BlockState state) {
        return moldmod.block.MoldyBlockHelper.getPickStack(world, pos, state);
    }
}
