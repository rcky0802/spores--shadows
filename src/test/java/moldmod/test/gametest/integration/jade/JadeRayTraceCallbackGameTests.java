package moldmod.test.gametest.integration.jade;

import moldmod.block.ModBlocks;
import moldmod.block.MoldyLogBlock;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public class JadeRayTraceCallbackGameTests {

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testMoldyBlockPickStackNonNull(TestContext context) {
        BlockPos pos = new BlockPos(2, 2, 2);

        // 1. Moldy Oak Log
        BlockState moldyLogState = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState().with(MoldyLogBlock.STAGE, 2);
        context.setBlockState(pos, moldyLogState);

        ItemStack pickLog = moldyLogState.getBlock().getPickStack(context.getWorld(), context.getAbsolutePos(pos), moldyLogState);
        context.assertTrue(pickLog != null && !pickLog.isEmpty(), "Moldy log pick stack must not be empty");

        // 2. Moldy Oak Planks
        BlockState moldyPlanksState = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_PLANKS).getDefaultState();
        context.setBlockState(pos, moldyPlanksState);

        ItemStack pickPlanks = moldyPlanksState.getBlock().getPickStack(context.getWorld(), context.getAbsolutePos(pos), moldyPlanksState);
        context.assertTrue(pickPlanks != null && !pickPlanks.isEmpty(), "Moldy planks pick stack must not be empty");

        // 3. Moldy Oak Slab
        BlockState moldySlabState = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_SLAB).getDefaultState();
        context.setBlockState(pos, moldySlabState);

        ItemStack pickSlab = moldySlabState.getBlock().getPickStack(context.getWorld(), context.getAbsolutePos(pos), moldySlabState);
        context.assertTrue(pickSlab != null && !pickSlab.isEmpty(), "Moldy slab pick stack must not be empty");

        context.complete();
    }
}
