package moldmod.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSetType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ButtonBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class MoldyButtonBlock extends ButtonBlock implements MoldyBlock {

    public MoldyButtonBlock(BlockSetType type, int pressTicks, Settings settings) {
        super(type, pressTicks, settings);
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
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!state.get(POWERED) && MoldyBlockHelper.tryBreakRottenBlock(world, pos, state, 0.10f)) {
            return ActionResult.SUCCESS;
        }
        return super.onUse(state, world, pos, player, hit);
    }

    public int getMoldyPressTicks(BlockState state) {
        int stage = state.get(MoldyBlock.STAGE);
        return switch (stage) {
            case 0 -> 30;
            case 1 -> 60;
            case 2 -> 150;
            case 3 -> 450;
            default -> 30;
        };
    }

    @Override
    public ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state) {
        return MoldyBlockHelper.getPickStack(world, pos, state);
    }
}
