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

public class MoldyPressurePlateBlock extends PressurePlateBlock {

    public MoldyPressurePlateBlock(BlockSetType type, Settings settings) {
        super(type, settings);
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
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (!world.isClient && state.get(POWERED) == false) {
            int stage = state.get(MoldyLogBlock.STAGE);
            if (stage == 3) {
                // 10% chance to break when stepped on!
                if (world.random.nextFloat() < 0.10f) {
                    world.breakBlock(pos, false);
                    world.playSound(null, pos, net.minecraft.sound.SoundEvents.BLOCK_WOOD_BREAK,
                            net.minecraft.sound.SoundCategory.BLOCKS, 1.0f, 0.8f);
                    return; // Stop processing, block is destroyed
                }
            }
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
}
