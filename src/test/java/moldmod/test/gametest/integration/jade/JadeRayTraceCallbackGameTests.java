package moldmod.test.gametest.integration.jade;

import moldmod.block.ModBlocks;
import moldmod.block.wood.MoldyLogBlock;
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

        // 4. Moldy Oak Sign
        BlockState moldySignState = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_SIGN).getDefaultState().with(moldmod.block.core.MoldyBlock.STAGE, 2);
        context.setBlockState(pos, moldySignState);
        ItemStack pickSign = moldySignState.getBlock().getPickStack(context.getWorld(), context.getAbsolutePos(pos), moldySignState);
        context.assertTrue(pickSign != null && !pickSign.isEmpty(), "Moldy sign pick stack must not be empty");
        context.assertEquals("item.spores--shadows.moldy_oak_sign", pickSign.getItem().getTranslationKey(),
                "Pick stack item must be moldy_oak_sign");

        // 5. Moldy Oak Wall Sign
        BlockState moldyWallSignState = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_WALL_SIGN).getDefaultState().with(moldmod.block.core.MoldyBlock.STAGE, 1);
        context.setBlockState(pos, moldyWallSignState);
        ItemStack pickWallSign = moldyWallSignState.getBlock().getPickStack(context.getWorld(), context.getAbsolutePos(pos), moldyWallSignState);
        context.assertTrue(pickWallSign != null && !pickWallSign.isEmpty(), "Moldy wall sign pick stack must not be empty");
        context.assertEquals("item.spores--shadows.tainted_oak_sign", pickWallSign.getItem().getTranslationKey(),
                "Wall sign pick stack item must resolve to standing sign item tainted_oak_sign");

        // 6. Moldy Oak Hanging Sign
        BlockState moldyHangingSignState = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_HANGING_SIGN).getDefaultState().with(moldmod.block.core.MoldyBlock.STAGE, 3);
        context.setBlockState(pos, moldyHangingSignState);
        ItemStack pickHangingSign = moldyHangingSignState.getBlock().getPickStack(context.getWorld(), context.getAbsolutePos(pos), moldyHangingSignState);
        context.assertTrue(pickHangingSign != null && !pickHangingSign.isEmpty(), "Moldy hanging sign pick stack must not be empty");
        context.assertEquals("item.spores--shadows.rotten_oak_hanging_sign", pickHangingSign.getItem().getTranslationKey(),
                "Hanging sign pick stack item must be rotten_oak_hanging_sign");

        // 7. Moldy Oak Wall Hanging Sign
        BlockState moldyWallHangingSignState = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_WALL_HANGING_SIGN).getDefaultState().with(moldmod.block.core.MoldyBlock.STAGE, 0).with(moldmod.block.core.MoldyBlock.WAXED, true);
        context.setBlockState(pos, moldyWallHangingSignState);
        ItemStack pickWallHangingSign = moldyWallHangingSignState.getBlock().getPickStack(context.getWorld(), context.getAbsolutePos(pos), moldyWallHangingSignState);
        context.assertTrue(pickWallHangingSign != null && !pickWallHangingSign.isEmpty(), "Waxed wall hanging sign pick stack must not be empty");
        context.assertEquals("item.spores--shadows.waxed_oak_hanging_sign", pickWallHangingSign.getItem().getTranslationKey(),
                "Wall hanging sign pick stack item must resolve to hanging sign item waxed_oak_hanging_sign");

        context.complete();
    }
}
