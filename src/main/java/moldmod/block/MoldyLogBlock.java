package moldmod.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PillarBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

/**
 * A custom pillar block representing a moldy oak log.
 * It simulates mold growth over time based on environmental factors (humidity,
 * temperature, light).
 * Players can interact with the block to scrape off mold (using an Axe) or wax
 * the block (using Honeycomb) to prevent further growth.
 */
public class MoldyLogBlock extends PillarBlock implements MoldyBlock {
    public static final IntProperty STAGE = MoldyBlock.STAGE;
    public static final BooleanProperty WAXED = MoldyBlock.WAXED;
    public static final BooleanProperty STRUCTURAL = MoldyBlock.STRUCTURAL;

    private final Block strippedBlock;

    public MoldyLogBlock(Settings settings, Block strippedBlock) {
        super(settings);
        this.strippedBlock = strippedBlock;
        this.setDefaultState(MoldyBlockHelper
                .initMoldyDefaultState(this.getDefaultState().with(AXIS, Axis.Y)));
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
    public BlockSoundGroup getSoundGroup(BlockState state) {
        if (state.contains(STAGE) && state.get(STAGE) == 3) {
            return BlockSoundGroup.SLIME;
        }
        return super.getSoundGroup(state);
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos,
            PlayerEntity player, Hand hand, BlockHitResult hit) {
        // If the player right-clicks with an Axe (without sneaking, since sneaking is caught by UseBlockCallback),
        // we strip the log. We preserve the STAGE and WAXED, but set STRUCTURAL to false!
        if (stack.getItem() instanceof AxeItem && strippedBlock != null) {
            BlockState stripped = strippedBlock.getDefaultState();
            if (state.contains(AXIS)) {
                stripped = stripped.with(AXIS, state.get(AXIS));
            }
            if (state.contains(STAGE)) {
                stripped = stripped.with(STAGE, state.get(STAGE));
            }
            if (state.contains(WAXED)) {
                stripped = stripped.with(WAXED, state.get(WAXED));
            }
            // Ensure structural is always false after player interaction
            if (state.contains(STRUCTURAL)) {
                stripped = stripped.with(STRUCTURAL, false);
            }

            world.setBlockState(pos, stripped);
            world.playSound(player, pos, SoundEvents.ITEM_AXE_STRIP,
                    SoundCategory.BLOCKS, 1.0f, 1.0f);
            stack.damage(1, player, PlayerEntity.getSlotForHand(hand));
            return ItemActionResult.SUCCESS;
        }
        return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
    }

    @Override
    public ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state) {
        return MoldyBlockHelper.getPickStack(world, pos, state);
    }
}
