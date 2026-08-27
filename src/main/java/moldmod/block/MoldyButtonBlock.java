package moldmod.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSetType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ButtonBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;

public class MoldyButtonBlock extends ButtonBlock {

    public MoldyButtonBlock(BlockSetType type, int pressTicks, Settings settings) {
        super(type, pressTicks, settings);
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
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient && state.get(POWERED) == false) {
            int stage = state.get(MoldyLogBlock.STAGE);
            boolean waxed = state.get(MoldyLogBlock.WAXED);
            if (stage == 3 && !waxed) {
                // 10% chance to break when pressed!
                if (world.random.nextFloat() < 0.10f) {
                    world.breakBlock(pos, false);
                    world.playSound(null, pos, net.minecraft.sound.SoundEvents.BLOCK_WOOD_BREAK,
                            net.minecraft.sound.SoundCategory.BLOCKS, 1.0f, 0.8f);
                    return ActionResult.SUCCESS;
                }
            }
        }
        return super.onUse(state, world, pos, player, hit);
    }

    public int getMoldyPressTicks(BlockState state) {
        int stage = state.get(MoldyLogBlock.STAGE);
        return switch (stage) {
            case 0 -> 30;
            case 1 -> 60;
            case 2 -> 150;
            case 3 -> 450;
            default -> 30;
        };
    }

    @Override
    public net.minecraft.item.ItemStack getPickStack(net.minecraft.world.WorldView world, net.minecraft.util.math.BlockPos pos, net.minecraft.block.BlockState state) {
        return moldmod.block.MoldyBlockHelper.getPickStack(world, pos, state);
    }
}
