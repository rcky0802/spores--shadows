package moldmod.test;

import moldmod.SporesShadows;
import moldmod.block.MoldyLogBlock;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class MoldyDropAndLootTests {

    // ============================================
    // === EXHAUSTIVE LOOT & DROP TESTS ===
    // ============================================

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testDropsAcrossAllProductsAndStages(TestContext context) {
        BlockPos pos = new BlockPos(0, 2, 0);

        PlayerEntity normalPlayer = context.createMockPlayer(net.minecraft.world.GameMode.SURVIVAL);

        PlayerEntity silkTouchPlayer = context.createMockPlayer(net.minecraft.world.GameMode.SURVIVAL);
        ItemStack silkTouchPick = new ItemStack(Items.DIAMOND_PICKAXE);
        silkTouchPick.addEnchantment(context.getWorld().getRegistryManager().get(RegistryKeys.ENCHANTMENT).getEntry(Enchantments.SILK_TOUCH).get(), 1);
        silkTouchPlayer.setStackInHand(net.minecraft.util.Hand.MAIN_HAND, silkTouchPick);

        int totalStage2Trials = 0;
        int totalStage2Successes = 0;

        for (var product : MoldyWoodTestHelper.getAllWoodProducts()) {
            String baseName = product.baseName();

            Block moldyBlock = Registries.BLOCK.get(SporesShadows.id("moldy_" + baseName));
            Block waxedBlock = Registries.BLOCK.get(SporesShadows.id("waxed_" + baseName));

            if (moldyBlock == Blocks.AIR || waxedBlock == Blocks.AIR) {
                context.throwPositionedException("Block for " + baseName + " is AIR!", pos);
            }

            // Expected items for stages 0, 1, 2, 3
            Item itemVanilla = Registries.ITEM.get(net.minecraft.util.Identifier.of("minecraft", baseName));
            Item itemTainted = Registries.ITEM.get(SporesShadows.id("tainted_" + baseName));
            Item itemMoldy = Registries.ITEM.get(SporesShadows.id("moldy_" + baseName));
            Item itemRotten = Registries.ITEM.get(SporesShadows.id("rotten_" + baseName));

            Item itemWaxed0 = Registries.ITEM.get(SporesShadows.id("waxed_" + baseName));
            Item itemWaxedTainted = Registries.ITEM.get(SporesShadows.id("waxed_tainted_" + baseName));
            Item itemWaxedMoldy = Registries.ITEM.get(SporesShadows.id("waxed_moldy_" + baseName));
            Item itemWaxedRotten = Registries.ITEM.get(SporesShadows.id("waxed_rotten_" + baseName));

            // Test Waxed Block across all stages 0 to 3 -> Guaranteed drop (100%) of corresponding waxed item
            testWaxedDrops(context, waxedBlock, baseName, itemWaxed0, itemWaxedTainted, itemWaxedMoldy, itemWaxedRotten, normalPlayer, pos);

            // Test Normal Moldy Block across all stages 0 to 3
            int[] stage2Stats = testNormalDrops(context, moldyBlock, baseName, itemVanilla, itemTainted, itemMoldy, itemRotten, normalPlayer, silkTouchPlayer, silkTouchPick, pos);
            totalStage2Trials += stage2Stats[0];
            totalStage2Successes += stage2Stats[1];
        }

        // Global statistical verification for Stage 2 (50% drop rate without Silk Touch)
        double stage2Rate = (double) totalStage2Successes / totalStage2Trials;
        if (stage2Rate < 0.35 || stage2Rate > 0.65) {
            context.throwPositionedException("Stage 2 50% drop rate out of bounds! Successes: " + totalStage2Successes + "/" + totalStage2Trials + " (" + stage2Rate + ")", pos);
        }

        context.complete();
    }

    private void testWaxedDrops(TestContext context, Block waxedBlock, String baseName,
                               Item item0, Item item1, Item item2, Item item3,
                               PlayerEntity player, BlockPos pos) {
        Item[] expectedItems = new Item[]{item0, item1, item2, item3};

        for (int stage = 0; stage <= 3; stage++) {
            BlockState state = waxedBlock.getDefaultState()
                    .with(MoldyLogBlock.STAGE, stage)
                    .with(MoldyLogBlock.WAXED, true);

            List<ItemStack> drops = Block.getDroppedStacks(state, context.getWorld(), context.getAbsolutePos(pos), null, player, player.getMainHandStack());

            if (drops.isEmpty()) {
                context.throwPositionedException("Waxed " + baseName + " stage " + stage + " MUST drop guaranteed!", pos);
            }

            Item droppedItem = drops.get(0).getItem();
            if (!droppedItem.equals(expectedItems[stage])) {
                context.throwPositionedException("Waxed " + baseName + " stage " + stage + " dropped wrong item! Expected: " + expectedItems[stage] + " got: " + droppedItem, pos);
            }
        }
    }

    private int[] testNormalDrops(TestContext context, Block moldyBlock, String baseName,
                                  Item itemVanilla, Item itemTainted, Item itemMoldy, Item itemRotten,
                                  PlayerEntity normalPlayer, PlayerEntity silkTouchPlayer, ItemStack silkTouchTool, BlockPos pos) {
        // Stage 0 without Silk Touch: 100% drop of vanilla item
        BlockState state0 = moldyBlock.getDefaultState().with(MoldyLogBlock.STAGE, 0).with(MoldyLogBlock.WAXED, false);
        List<ItemStack> drops0 = Block.getDroppedStacks(state0, context.getWorld(), context.getAbsolutePos(pos), null, normalPlayer, normalPlayer.getMainHandStack());
        if (drops0.isEmpty() || !drops0.get(0).getItem().equals(itemVanilla)) {
            context.throwPositionedException("Stage 0 " + baseName + " should drop vanilla item (100%)!", pos);
        }

        // Stage 1 (Tainted) without Silk Touch: 100% drop of tainted item
        BlockState state1 = moldyBlock.getDefaultState().with(MoldyLogBlock.STAGE, 1).with(MoldyLogBlock.WAXED, false);
        List<ItemStack> drops1 = Block.getDroppedStacks(state1, context.getWorld(), context.getAbsolutePos(pos), null, normalPlayer, normalPlayer.getMainHandStack());
        if (drops1.isEmpty() || !drops1.get(0).getItem().equals(itemTainted)) {
            context.throwPositionedException("Stage 1 " + baseName + " should drop tainted item (100%)!", pos);
        }

        // Stage 2 (Moldy) with Silk Touch: 100% drop of moldy item
        BlockState state2 = moldyBlock.getDefaultState().with(MoldyLogBlock.STAGE, 2).with(MoldyLogBlock.WAXED, false);
        List<ItemStack> drops2Silk = Block.getDroppedStacks(state2, context.getWorld(), context.getAbsolutePos(pos), null, silkTouchPlayer, silkTouchTool);
        if (drops2Silk.isEmpty() || !drops2Silk.get(0).getItem().equals(itemMoldy)) {
            context.throwPositionedException("Stage 2 " + baseName + " with Silk Touch should drop moldy item (100%)!", pos);
        }

        // Stage 2 (Moldy) without Silk Touch: 50% drop rate of moldy item
        int stage2Trials = 20;
        int stage2Successes = 0;
        for (int i = 0; i < stage2Trials; i++) {
            List<ItemStack> drops2Normal = Block.getDroppedStacks(state2, context.getWorld(), context.getAbsolutePos(pos), null, normalPlayer, normalPlayer.getMainHandStack());
            if (!drops2Normal.isEmpty()) {
                if (!drops2Normal.get(0).getItem().equals(itemMoldy)) {
                    context.throwPositionedException("Stage 2 " + baseName + " dropped incorrect item without Silk Touch! Got: " + drops2Normal.get(0).getItem(), pos);
                }
                stage2Successes++;
            }
        }

        // Stage 3 (Rotten) without Silk Touch: 0% drop rate (always empty, disintegrates)
        BlockState state3 = moldyBlock.getDefaultState().with(MoldyLogBlock.STAGE, 3).with(MoldyLogBlock.WAXED, false);
        for (int i = 0; i < 10; i++) {
            List<ItemStack> drops3Normal = Block.getDroppedStacks(state3, context.getWorld(), context.getAbsolutePos(pos), null, normalPlayer, normalPlayer.getMainHandStack());
            if (!drops3Normal.isEmpty()) {
                context.throwPositionedException("Stage 3 " + baseName + " without Silk Touch MUST NOT drop anything (0%)!", pos);
            }
        }

        // Stage 3 (Rotten) with Silk Touch: 100% drop of rotten item
        List<ItemStack> drops3Silk = Block.getDroppedStacks(state3, context.getWorld(), context.getAbsolutePos(pos), null, silkTouchPlayer, silkTouchTool);
        if (drops3Silk.isEmpty() || !drops3Silk.get(0).getItem().equals(itemRotten)) {
            context.throwPositionedException("Stage 3 " + baseName + " with Silk Touch MUST drop rotten item (100%)!", pos);
        }

        return new int[]{stage2Trials, stage2Successes};
    }
}
