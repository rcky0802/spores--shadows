package moldmod.test.gametest.wood;

import moldmod.SporesShadows;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FenceBlock;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public class FenceConnectionGameTests {

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testFenceTagsInclusion(TestContext context) {
        String[] fenceIds = {
            "moldy_bamboo_fence", "waxed_bamboo_fence",
            "moldy_oak_fence", "waxed_oak_fence",
            "moldy_crimson_fence", "waxed_crimson_fence",
            "moldy_warped_fence", "waxed_warped_fence"
        };

        for (String id : fenceIds) {
            Block block = Registries.BLOCK.get(SporesShadows.id(id));
            context.assertTrue(block != Blocks.AIR, "Block must be registered: " + id);
            context.assertTrue(block.getDefaultState().isIn(BlockTags.WOODEN_FENCES),
                    "Fence " + id + " must be in #minecraft:wooden_fences");
            context.assertTrue(block.getDefaultState().isIn(BlockTags.FENCES),
                    "Fence " + id + " must be in #minecraft:fences");
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testMoldyBambooFenceConnectsToVanillaBambooFence(TestContext context) {
        Block moldyBambooFence = Registries.BLOCK.get(SporesShadows.id("moldy_bamboo_fence"));
        BlockPos pos1 = new BlockPos(1, 1, 1);
        BlockPos pos2 = new BlockPos(1, 1, 2);

        context.setBlockState(pos1, Blocks.BAMBOO_FENCE.getDefaultState());
        context.setBlockState(pos2, moldyBambooFence.getDefaultState());

        BlockState state1 = context.getBlockState(pos1);
        BlockState state2 = context.getBlockState(pos2);

        context.assertTrue(state1.get(FenceBlock.SOUTH), "Vanilla bamboo fence must connect south to moldy bamboo fence");
        context.assertTrue(state2.get(FenceBlock.NORTH), "Moldy bamboo fence must connect north to vanilla bamboo fence");

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testMoldyBambooFenceConnectsToVanillaOakFence(TestContext context) {
        Block moldyBambooFence = Registries.BLOCK.get(SporesShadows.id("moldy_bamboo_fence"));
        BlockPos pos1 = new BlockPos(2, 1, 1);
        BlockPos pos2 = new BlockPos(2, 1, 2);

        context.setBlockState(pos1, Blocks.OAK_FENCE.getDefaultState());
        context.setBlockState(pos2, moldyBambooFence.getDefaultState());

        BlockState state1 = context.getBlockState(pos1);
        BlockState state2 = context.getBlockState(pos2);

        context.assertTrue(state1.get(FenceBlock.SOUTH), "Vanilla oak fence must connect south to moldy bamboo fence");
        context.assertTrue(state2.get(FenceBlock.NORTH), "Moldy bamboo fence must connect north to vanilla oak fence");

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testMoldyFencesConnectToEachOther(TestContext context) {
        Block moldyBambooFence = Registries.BLOCK.get(SporesShadows.id("moldy_bamboo_fence"));
        Block moldyOakFence = Registries.BLOCK.get(SporesShadows.id("moldy_oak_fence"));
        BlockPos pos1 = new BlockPos(3, 1, 1);
        BlockPos pos2 = new BlockPos(3, 1, 2);

        context.setBlockState(pos1, moldyBambooFence.getDefaultState());
        context.setBlockState(pos2, moldyOakFence.getDefaultState());

        BlockState state1 = context.getBlockState(pos1);
        BlockState state2 = context.getBlockState(pos2);

        context.assertTrue(state1.get(FenceBlock.SOUTH), "Moldy bamboo fence must connect south to moldy oak fence");
        context.assertTrue(state2.get(FenceBlock.NORTH), "Moldy oak fence must connect north to moldy bamboo fence");

        context.complete();
    }
}
