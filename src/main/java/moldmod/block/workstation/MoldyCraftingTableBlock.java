package moldmod.block.workstation;
import moldmod.block.core.MoldyBlock;
import moldmod.block.core.MoldyBlockHelper;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CraftingTableBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldView;

public class MoldyCraftingTableBlock extends CraftingTableBlock implements MoldyBlock {

    public MoldyCraftingTableBlock(Settings settings) {
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

    private static final net.minecraft.text.Text TITLE = net.minecraft.text.Text.translatable("container.crafting");

    @Override
    protected net.minecraft.screen.NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, net.minecraft.world.World world, BlockPos pos) {
        int stage = state.contains(MoldyBlock.STAGE) ? state.get(MoldyBlock.STAGE) : 0;
        return new net.minecraft.screen.SimpleNamedScreenHandlerFactory((syncId, inv, player) -> {
            net.minecraft.screen.CraftingScreenHandler handler = new net.minecraft.screen.CraftingScreenHandler(syncId, inv, net.minecraft.screen.ScreenHandlerContext.create(world, pos));
            if (handler instanceof moldmod.screen.MoldStageHolder holder) {
                holder.spores_shadows$setMoldStage(stage);
            }
            return handler;
        }, TITLE);
    }

    @Override
    public ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state) {
        return MoldyBlockHelper.getPickStack(world, pos, state);
    }
}
