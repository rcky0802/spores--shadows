package moldmod.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSetType;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class MoldyDoorBlock extends DoorBlock implements MoldyBlock {

    public MoldyDoorBlock(BlockSetType type, Settings settings) {
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
        // Only tick the LOWER half so the door doesn't age twice as fast.
        // The lower half will sync changes to the upper half automatically via MoldyBlockHelper.
        if (state.get(HALF) == DoubleBlockHalf.LOWER) {
            MoldyBlockHelper.randomTick(state, world, pos, random);
        }
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos,
            PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            int stage = state.get(MoldyBlock.STAGE);
            boolean waxed = state.get(MoldyBlock.WAXED);
            if (stage == 3 && !waxed) {
                if (world.random.nextFloat() < 0.10f) {
                    BlockPos otherPos = state.get(HALF) == DoubleBlockHalf.LOWER
                            ? pos.up()
                            : pos.down();
                    if (world.getBlockState(otherPos).isOf(this)) {
                        world.breakBlock(otherPos, false);
                    }
                    world.breakBlock(pos, false);
                    world.playSound(null, pos, SoundEvents.BLOCK_WOOD_BREAK,
                            SoundCategory.BLOCKS, 1.0f, 0.8f);
                    return ActionResult.SUCCESS;
                }
            }
        }
        return super.onUse(state, world, pos, player, hit);
    }

    @Override
    public ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state) {
        return MoldyBlockHelper.getPickStack(world, pos, state);
    }
}
