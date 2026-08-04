package moldmod.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PillarBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

/**
 * A custom pillar block representing a moldy oak log.
 * It simulates mold growth over time based on environmental factors (humidity, temperature, light).
 * Players can interact with the block to scrape off mold (using an Axe) or wax the block (using Honeycomb) to prevent further growth.
 */
public class MoldyLogBlock extends PillarBlock {
    /**
     * The STAGE property represents the current state of mold growth (0 = clean, 1-3 = moldy).
     */
    public static final IntProperty STAGE = IntProperty.of("stage", 0, 3);
    
    /**
     * The WAXED property indicates whether the block has been waxed by a player to prevent further mold growth.
     */
    public static final BooleanProperty WAXED = BooleanProperty.of("waxed");

    /**
     * The STRUCTURAL property indicates whether the block is a structural support.
     */
    public static final BooleanProperty STRUCTURAL = BooleanProperty.of("structural");

    private final Block strippedBlock;

    @SuppressWarnings("this-escape")
    public MoldyLogBlock(Settings settings, Block strippedBlock) {
        super(settings);
        this.strippedBlock = strippedBlock;
        this.setDefaultState(this.getDefaultState().with(AXIS, net.minecraft.util.math.Direction.Axis.Y).with(STAGE, 0).with(WAXED, false).with(STRUCTURAL, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(STAGE, WAXED, STRUCTURAL);
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
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemActionResult result = MoldyBlockHelper.onUseWithItem(stack, state, world, pos, player, hand, strippedBlock);
        if (result != ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION) {
            return result;
        }
        return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
    }
}
