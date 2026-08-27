package moldmod.test;

import moldmod.block.ModBlocks;
import moldmod.block.MoldyLogBlock;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

public class MoldyMiningAndLootTests {

    // ============================================
    // === MINING SPEED TESTS (calcBlockBreakingDelta)
    // ============================================

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testAxeIsEffectiveOnStage1(TestContext context) {
        BlockPos pos = new BlockPos(0, 2, 0);
        BlockState taintedLog = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState()
                .with(MoldyLogBlock.STAGE, 1);
        context.setBlockState(pos, taintedLog);

        net.minecraft.entity.player.PlayerEntity playerWithAxe = context.createMockPlayer(net.minecraft.world.GameMode.SURVIVAL);
        playerWithAxe.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.IRON_AXE));
        
        net.minecraft.entity.player.PlayerEntity playerWithHand = context.createMockPlayer(net.minecraft.world.GameMode.SURVIVAL);
        playerWithHand.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);

        float axeSpeed = taintedLog.calcBlockBreakingDelta(playerWithAxe, context.getWorld(), context.getAbsolutePos(pos));
        float handSpeed = taintedLog.calcBlockBreakingDelta(playerWithHand, context.getWorld(), context.getAbsolutePos(pos));
        
        if (axeSpeed <= handSpeed) {
            context.throwPositionedException("Axe should be faster than hand on Stage 1! Axe: " + axeSpeed + ", Hand: " + handSpeed, pos);
        }
        
        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testAxeIsNOTEffectiveOnStage3(TestContext context) {
        BlockPos pos = new BlockPos(0, 2, 0);
        BlockState rottenLog = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState()
                .with(MoldyLogBlock.STAGE, 3);
        context.setBlockState(pos, rottenLog);

        net.minecraft.entity.player.PlayerEntity playerWithAxe = context.createMockPlayer(net.minecraft.world.GameMode.SURVIVAL);
        playerWithAxe.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.IRON_AXE));
        
        net.minecraft.entity.player.PlayerEntity playerWithHand = context.createMockPlayer(net.minecraft.world.GameMode.SURVIVAL);
        playerWithHand.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);

        float axeSpeed = rottenLog.calcBlockBreakingDelta(playerWithAxe, context.getWorld(), context.getAbsolutePos(pos));
        float handSpeed = rottenLog.calcBlockBreakingDelta(playerWithHand, context.getWorld(), context.getAbsolutePos(pos));
        
        if (axeSpeed != handSpeed) {
            context.throwPositionedException("Axe and Hand should have the EXACT SAME mining speed on Stage 3! Axe: " + axeSpeed + ", Hand: " + handSpeed, pos);
        }
        
        context.complete();
    }

    // ============================================
    // === LOOT TABLE TESTS ===
    // ============================================

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testWaxedBlockAlwaysDrops(TestContext context) {
        BlockPos pos = new BlockPos(0, 2, 0);
        // Even at Stage 3, if it's WAXED, it MUST drop guaranteed!
        BlockState waxedRottenLog = ModBlocks.MOLDY_TO_WAXED.get(ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG)).getDefaultState()
                .with(MoldyLogBlock.STAGE, 3)
                .with(MoldyLogBlock.WAXED, true);
        context.setBlockState(pos, waxedRottenLog);
        
        net.minecraft.entity.player.PlayerEntity player = context.createMockPlayer(net.minecraft.world.GameMode.SURVIVAL);
        java.util.List<ItemStack> drops = net.minecraft.block.Block.getDroppedStacks(waxedRottenLog, context.getWorld(), context.getAbsolutePos(pos), null, player, player.getMainHandStack());
        
        if (drops.isEmpty()) {
            context.throwPositionedException("Waxed block MUST drop even at Stage 3 without Silk Touch!", pos);
        }
        
        if (!drops.get(0).getItem().equals(net.minecraft.registry.Registries.ITEM.get(moldmod.SporesShadows.id("waxed_rotten_oak_log")))) {
            context.throwPositionedException("Did not drop correct item!", pos);
        }

        if (!drops.get(0).getComponents().contains(net.minecraft.component.DataComponentTypes.BLOCK_STATE) || 
            !drops.get(0).getComponents().get(net.minecraft.component.DataComponentTypes.BLOCK_STATE).properties().containsKey("waxed")) {
            context.throwPositionedException("The dropped item did NOT preserve the WAXED property!", pos);
        }
        
        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testSilkTouchGuaranteesDropOnStage3(TestContext context) {
        BlockPos pos = new BlockPos(0, 2, 0);
        BlockState rottenLog = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState()
                .with(MoldyLogBlock.STAGE, 3)
                .with(MoldyLogBlock.WAXED, false);
        context.setBlockState(pos, rottenLog);
        
        net.minecraft.entity.player.PlayerEntity player = context.createMockPlayer(net.minecraft.world.GameMode.SURVIVAL);
        ItemStack silkTouchPick = new ItemStack(Items.DIAMOND_PICKAXE);
        silkTouchPick.addEnchantment(context.getWorld().getRegistryManager().get(net.minecraft.registry.RegistryKeys.ENCHANTMENT).getEntry(net.minecraft.enchantment.Enchantments.SILK_TOUCH).get(), 1);
        
        java.util.List<ItemStack> drops = net.minecraft.block.Block.getDroppedStacks(rottenLog, context.getWorld(), context.getAbsolutePos(pos), null, player, silkTouchPick);
        
        if (drops.isEmpty()) {
            context.throwPositionedException("Silk Touch MUST guarantee a drop on Stage 3!", pos);
        }
        
        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testStage3DropsNothingNormally(TestContext context) {
        BlockPos pos = new BlockPos(0, 2, 0);
        BlockState rottenLog = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState()
                .with(MoldyLogBlock.STAGE, 3)
                .with(MoldyLogBlock.WAXED, false);
        context.setBlockState(pos, rottenLog);
        
        net.minecraft.entity.player.PlayerEntity player = context.createMockPlayer(net.minecraft.world.GameMode.SURVIVAL);
        java.util.List<ItemStack> drops = net.minecraft.block.Block.getDroppedStacks(rottenLog, context.getWorld(), context.getAbsolutePos(pos), null, player, player.getMainHandStack());
        
        if (!drops.isEmpty()) {
            context.throwPositionedException("Stage 3 unwaxed MUST NOT drop anything without Silk Touch!", pos);
        }
        
        context.complete();
    }
}
