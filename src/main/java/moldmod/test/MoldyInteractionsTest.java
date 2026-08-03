package moldmod.test;

import moldmod.block.ModBlocks;
import moldmod.block.MoldyOakLogBlock;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class MoldyInteractionsTest implements FabricGameTest {

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testAxeInteractions(TestContext context) {
        BlockPos pos = new BlockPos(0, 1, 0);
        net.minecraft.entity.player.PlayerEntity player = context.createMockPlayer(net.minecraft.world.GameMode.SURVIVAL);
        ItemStack axe = new ItemStack(Items.WOODEN_AXE);
        
        BlockHitResult hit = new BlockHitResult(Vec3d.ZERO, Direction.UP, context.getAbsolutePos(pos), false);

        // Test Waxed block gets unwaxed
        context.setBlockState(pos, ModBlocks.MOLDY_OAK_LOG.getDefaultState().with(MoldyOakLogBlock.STAGE, 2).with(MoldyOakLogBlock.WAXED, true));
        context.getBlockState(pos).onUseWithItem(axe, context.getWorld(), player, Hand.MAIN_HAND, hit);
        context.assertTrue(!context.getBlockState(pos).get(MoldyOakLogBlock.WAXED), "Block should be unwaxed");
        
        // Test Stage 1 gets scraped to Stage 0
        context.setBlockState(pos, ModBlocks.MOLDY_OAK_LOG.getDefaultState().with(MoldyOakLogBlock.STAGE, 1).with(MoldyOakLogBlock.WAXED, false));
        context.getBlockState(pos).onUseWithItem(axe, context.getWorld(), player, Hand.MAIN_HAND, hit);
        context.assertTrue(context.getBlockState(pos).get(MoldyOakLogBlock.STAGE) == 0, "Stage 1 should be scraped to Stage 0");

        // Test Stage 0 gets stripped
        context.setBlockState(pos, ModBlocks.MOLDY_OAK_LOG.getDefaultState().with(MoldyOakLogBlock.STAGE, 0).with(MoldyOakLogBlock.WAXED, false));
        context.getBlockState(pos).onUseWithItem(axe, context.getWorld(), player, Hand.MAIN_HAND, hit);
        context.assertTrue(context.getBlockState(pos).isOf(Blocks.STRIPPED_OAK_LOG), "Stage 0 should be stripped to STRIPPED_OAK_LOG");
        
        // Test Stage 2 ignores scraping
        context.setBlockState(pos, ModBlocks.MOLDY_OAK_LOG.getDefaultState().with(MoldyOakLogBlock.STAGE, 2).with(MoldyOakLogBlock.WAXED, false));
        context.getBlockState(pos).onUseWithItem(axe, context.getWorld(), player, Hand.MAIN_HAND, hit);
        context.assertTrue(context.getBlockState(pos).isOf(ModBlocks.MOLDY_OAK_LOG) && context.getBlockState(pos).get(MoldyOakLogBlock.STAGE) == 2, "Stage 2 should not be affected by axe scraping");

        context.complete();
    }
    
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testNaturalLogImmunity(TestContext context) {
        // Natural OAK_LOG does not get infected by nearby MOLDY_OAK_LOG ticks
        BlockPos naturalPos = new BlockPos(0, 1, 0);
        BlockPos moldyPos = new BlockPos(1, 1, 0);
        
        context.setBlockState(naturalPos, Blocks.OAK_LOG.getDefaultState());
        context.setBlockState(moldyPos, ModBlocks.MOLDY_OAK_LOG.getDefaultState().with(MoldyOakLogBlock.STAGE, 1));
        
        // Try ticking the moldy log multiple times. Since our tick logic checks !neighborState.isOf(Blocks.OAK_LOG),
        // it should NEVER infect the natural log.
        MoldyOakLogBlock block = (MoldyOakLogBlock) ModBlocks.MOLDY_OAK_LOG;
        for (int i = 0; i < 50; i++) {
            block.randomTick(context.getBlockState(moldyPos), context.getWorld(), context.getAbsolutePos(moldyPos), context.getWorld().getRandom());
        }
        
        context.assertTrue(context.getBlockState(naturalPos).isOf(Blocks.OAK_LOG), "Natural OAK_LOG must remain immune to spread");
        context.complete();
    }
}
