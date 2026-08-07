package moldmod.block;

import net.minecraft.block.DoorBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.block.BlockSetType;

public class MoldyDoorBlock extends DoorBlock {

    @SuppressWarnings("this-escape")
    public MoldyDoorBlock(BlockSetType type, Settings settings) {
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
        // Only tick the LOWER half so the door doesn't age twice as fast.
        // The lower half will sync changes to the upper half automatically via MoldyBlockHelper.
        if (state.get(net.minecraft.block.DoorBlock.HALF) == net.minecraft.block.enums.DoubleBlockHalf.LOWER) {
            MoldyBlockHelper.randomTick(state, world, pos, random, this);
        }
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

    @Override
    protected net.minecraft.util.ActionResult onUse(BlockState state, net.minecraft.world.World world, BlockPos pos, PlayerEntity player, net.minecraft.util.hit.BlockHitResult hit) {
        if (!world.isClient) {
            int stage = state.get(MoldyLogBlock.STAGE);
            if (stage == 3) {
                if (world.random.nextFloat() < 0.10f) {
                    world.breakBlock(pos, false);
                    world.playSound(null, pos, net.minecraft.sound.SoundEvents.BLOCK_WOOD_BREAK, net.minecraft.sound.SoundCategory.BLOCKS, 1.0f, 0.8f);
                    return net.minecraft.util.ActionResult.SUCCESS;
                }
            }
        }
        return super.onUse(state, world, pos, player, hit);
    }
}

