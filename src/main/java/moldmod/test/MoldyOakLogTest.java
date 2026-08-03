package moldmod.test;

import moldmod.block.ModBlocks;
import moldmod.block.MoldyOakLogBlock;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import net.minecraft.world.GameMode;

public class MoldyOakLogTest implements FabricGameTest {

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testMoldyOakLogStages(TestContext context) {
        BlockPos pos = new BlockPos(0, 1, 0);
        
        // Test Stage 0 placing
        BlockState state0 = ModBlocks.MOLDY_OAK_LOG.getDefaultState().with(MoldyOakLogBlock.STAGE, 0);
        context.setBlockState(pos, state0);
        context.assertTrue(context.getBlockState(pos).isOf(ModBlocks.MOLDY_OAK_LOG), "Block is not MOLDY_OAK_LOG");
        context.assertTrue(context.getBlockState(pos).get(MoldyOakLogBlock.STAGE) == 0, "Stage is not 0");
        context.assertTrue(context.getBlockState(pos).hasRandomTicks(), "Stage 0 should receive random ticks");
        
        // Test Waxed max limit
        BlockState waxedState = ModBlocks.MOLDY_OAK_LOG.getDefaultState().with(MoldyOakLogBlock.STAGE, 2).with(MoldyOakLogBlock.WAXED, true);
        context.setBlockState(pos, waxedState);
        context.assertTrue(!context.getBlockState(pos).hasRandomTicks(), "Waxed block should NOT receive random ticks");
        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testHoneycombWaxing(TestContext context) {
        BlockPos pos = new BlockPos(0, 1, 0);
        
        // Place Stage 1 unwaxed
        BlockState state = ModBlocks.MOLDY_OAK_LOG.getDefaultState().with(MoldyOakLogBlock.STAGE, 1).with(MoldyOakLogBlock.WAXED, false);
        context.setBlockState(pos, state);

        PlayerEntity player = context.createMockPlayer(GameMode.SURVIVAL);
        player.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.HONEYCOMB));
        BlockHitResult hitResult = new BlockHitResult(new Vec3d(0.5, 1.5, 0.5), Direction.UP, context.getAbsolutePos(pos), false);

        // Simulate interaction
        context.getBlockState(pos).onUseWithItem(player.getStackInHand(Hand.MAIN_HAND), context.getWorld(), player, Hand.MAIN_HAND, hitResult);

        // Verify waxed
        BlockState newState = context.getBlockState(pos);
        context.assertTrue(newState.get(MoldyOakLogBlock.WAXED), "Block should be waxed after using honeycomb");
        context.assertTrue(newState.get(MoldyOakLogBlock.STAGE) == 1, "Stage should remain 1");
        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testAxeInteractions(TestContext context) {
        BlockPos pos = new BlockPos(0, 1, 0);
        
        // Place Stage 1 WAXED
        BlockState state = ModBlocks.MOLDY_OAK_LOG.getDefaultState().with(MoldyOakLogBlock.STAGE, 1).with(MoldyOakLogBlock.WAXED, true);
        context.setBlockState(pos, state);

        PlayerEntity player = context.createMockPlayer(GameMode.SURVIVAL);
        player.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.WOODEN_AXE));
        BlockHitResult hitResult = new BlockHitResult(new Vec3d(0.5, 1.5, 0.5), Direction.UP, context.getAbsolutePos(pos), false);

        // 1. Axe on Waxed Stage 1 -> Should remove wax
        context.getBlockState(pos).onUseWithItem(player.getStackInHand(Hand.MAIN_HAND), context.getWorld(), player, Hand.MAIN_HAND, hitResult);
        BlockState stateUnwaxed = context.getBlockState(pos);
        context.assertTrue(!stateUnwaxed.get(MoldyOakLogBlock.WAXED), "Axe should remove wax");
        context.assertTrue(stateUnwaxed.get(MoldyOakLogBlock.STAGE) == 1, "Stage should remain 1 when removing wax");

        // 2. Axe on Unwaxed Stage 1 -> Should revert to Stage 0
        context.getBlockState(pos).onUseWithItem(player.getStackInHand(Hand.MAIN_HAND), context.getWorld(), player, Hand.MAIN_HAND, hitResult);
        BlockState stateStage0 = context.getBlockState(pos);
        context.assertTrue(!stateStage0.get(MoldyOakLogBlock.WAXED), "Should remain unwaxed");
        context.assertTrue(stateStage0.get(MoldyOakLogBlock.STAGE) == 0, "Axe should scrape mold, reverting to stage 0");

        // 3. Axe on Unwaxed Stage 0 -> Should strip to MOLDY_STRIPPED_OAK_LOG
        context.getBlockState(pos).onUseWithItem(player.getStackInHand(Hand.MAIN_HAND), context.getWorld(), player, Hand.MAIN_HAND, hitResult);
        BlockState stateStripped = context.getBlockState(pos);
        context.assertTrue(stateStripped.isOf(ModBlocks.MOLDY_STRIPPED_OAK_LOG), "Axe on stage 0 should yield stripped log");
        context.assertTrue(!stateStripped.get(MoldyOakLogBlock.WAXED), "Stripped log should be unwaxed");
        context.assertTrue(stateStripped.get(MoldyOakLogBlock.STAGE) == 0, "Stripped log should be stage 0");

        // 4. Axe on Stripped Log -> Should do nothing to the block itself in this mod
        context.getBlockState(pos).onUseWithItem(player.getStackInHand(Hand.MAIN_HAND), context.getWorld(), player, Hand.MAIN_HAND, hitResult);
        BlockState stillStripped = context.getBlockState(pos);
        context.assertTrue(stillStripped.isOf(ModBlocks.MOLDY_STRIPPED_OAK_LOG), "Axe on stripped log should not alter it further");

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testPlanksAndWoodAxe(TestContext context) {
        BlockPos pos = new BlockPos(0, 1, 0);
        
        PlayerEntity player = context.createMockPlayer(GameMode.SURVIVAL);
        player.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.WOODEN_AXE));
        BlockHitResult hitResult = new BlockHitResult(new Vec3d(0.5, 1.5, 0.5), Direction.UP, context.getAbsolutePos(pos), false);

        // Test Oak Wood -> Stripped Oak Wood
        context.setBlockState(pos, ModBlocks.MOLDY_OAK_WOOD.getDefaultState());
        context.getBlockState(pos).onUseWithItem(player.getStackInHand(Hand.MAIN_HAND), context.getWorld(), player, Hand.MAIN_HAND, hitResult);
        context.assertTrue(context.getBlockState(pos).isOf(ModBlocks.MOLDY_STRIPPED_OAK_WOOD), "Oak wood should strip to stripped oak wood");

        // Test Planks -> Should not strip
        context.setBlockState(pos, ModBlocks.MOLDY_OAK_PLANKS.getDefaultState());
        context.getBlockState(pos).onUseWithItem(player.getStackInHand(Hand.MAIN_HAND), context.getWorld(), player, Hand.MAIN_HAND, hitResult);
        context.assertTrue(context.getBlockState(pos).isOf(ModBlocks.MOLDY_OAK_PLANKS), "Planks should not be stripped");

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testCalculateR(TestContext context) {
        BlockPos pos = new BlockPos(0, 1, 0);
        context.setBlockState(pos, ModBlocks.MOLDY_OAK_LOG.getDefaultState());
        
        double riskUnwaxed = moldmod.block.MoldyBlockHelper.calculateR(context.getWorld(), pos, false, context.getBlockState(pos));
        context.assertTrue(riskUnwaxed >= 0.0 && riskUnwaxed <= 2.0, "Risk R should be within bounds");

        double riskWaxed = moldmod.block.MoldyBlockHelper.calculateR(context.getWorld(), pos, true, context.getBlockState(pos));
        context.assertTrue(riskWaxed == 0.0, "Waxed risk must be strictly 0.0");
        
        context.complete();
    }
}
