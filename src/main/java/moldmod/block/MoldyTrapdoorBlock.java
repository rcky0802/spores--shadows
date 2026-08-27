package moldmod.block;

import net.minecraft.block.TrapdoorBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.block.BlockSetType;

public class MoldyTrapdoorBlock extends TrapdoorBlock {

    @SuppressWarnings("this-escape")
    public MoldyTrapdoorBlock(BlockSetType type, Settings settings) {
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
        MoldyBlockHelper.randomTick(state, world, pos, random);
    }



    @Override
    protected net.minecraft.util.ActionResult onUse(BlockState state, net.minecraft.world.World world, BlockPos pos, PlayerEntity player, net.minecraft.util.hit.BlockHitResult hit) {
        if (!world.isClient) {
            int stage = state.get(MoldyLogBlock.STAGE);
            boolean waxed = state.get(MoldyLogBlock.WAXED);
            if (stage == 3 && !waxed) {
                if (world.random.nextFloat() < 0.10f) {
                    world.breakBlock(pos, false);
                    world.playSound(null, pos, net.minecraft.sound.SoundEvents.BLOCK_WOOD_BREAK, net.minecraft.sound.SoundCategory.BLOCKS, 1.0f, 0.8f);
                    return net.minecraft.util.ActionResult.SUCCESS;
                }
            }
        }
        return super.onUse(state, world, pos, player, hit);
    }

    @Override
    public net.minecraft.item.ItemStack getPickStack(net.minecraft.world.WorldView world, net.minecraft.util.math.BlockPos pos, net.minecraft.block.BlockState state) {
        return moldmod.block.MoldyBlockHelper.getPickStack(world, pos, state);
    }
}
