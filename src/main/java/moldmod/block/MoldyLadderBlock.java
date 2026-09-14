package moldmod.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LadderBlock;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class MoldyLadderBlock extends LadderBlock implements MoldyBlock {

    public MoldyLadderBlock(Settings settings) {
        super(settings);
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
    public ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state) {
        return MoldyBlockHelper.getPickStack(world, pos, state);
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        super.onEntityCollision(state, world, pos, entity);
        if (state.contains(MoldyBlock.STAGE) && state.contains(MoldyBlock.WAXED)) {
            int stage = state.get(MoldyBlock.STAGE);
            boolean waxed = state.get(MoldyBlock.WAXED);
            if (!waxed) {
                if (stage == 3) {
                    // Rotten ladder rungs crumble under weight
                    MoldyBlockHelper.tryBreakRottenBlock(world, pos, state, 0.05f);
                } else if (stage == 2) {
                    // Moldy ladder is slippery/wet when descending without sneaking
                    Vec3d vel = entity.getVelocity();
                    if (!entity.isSneaking() && vel.y < -0.05) {
                        entity.setVelocity(vel.x, vel.y * 1.15, vel.z);
                    }
                }
            }
        }
    }
}
