package moldmod.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PillarBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import java.util.function.Supplier;

public class MoldyOakLogBlock extends PillarBlock {
    private final Supplier<Block> nextStage;

    public MoldyOakLogBlock(Settings settings, Supplier<Block> nextStage) {
        super(settings);
        this.nextStage = nextStage;
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return this.nextStage != null;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.randomTick(state, world, pos, random);
        
        // Progressione dinamica dello stadio della muffa
        if (this.nextStage != null) {
            if (random.nextDouble() < 0.15) { // 15% di chance ad ogni random tick di peggiorare
                world.setBlockState(pos, this.nextStage.get().getDefaultState().with(AXIS, state.get(AXIS)));
            }
        }
    }
}
