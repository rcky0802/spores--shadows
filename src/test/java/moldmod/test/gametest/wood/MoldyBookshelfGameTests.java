package moldmod.test.gametest.wood;

import moldmod.SporesShadows;
import moldmod.block.ModBlocks;
import moldmod.block.core.MoldyBlock;
import moldmod.block.core.MoldyBlockHelper;
import moldmod.block.entity.ModBlockEntities;
import moldmod.block.workstation.MoldyChiseledBookshelfBlockEntity;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.EnchantingTableBlock;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.List;

public class MoldyBookshelfGameTests {

    private ActionResult simulatePlayerUse(TestContext context, BlockPos pos, PlayerEntity player) {
        BlockHitResult hit = new BlockHitResult(context.getAbsolutePos(pos).toCenterPos(), Direction.UP, context.getAbsolutePos(pos), false);
        return UseBlockCallback.EVENT.invoker().interact(player, context.getWorld(), Hand.MAIN_HAND, hit);
    }

    // =========================================================================
    // === 1. REGISTRATION & BLOCK ENTITY SUPPORT                            ===
    // =========================================================================

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testBookshelfBlocksRegistered(TestContext context) {
        Block moldyBookshelf = Registries.BLOCK.get(SporesShadows.id("moldy_bookshelf"));
        Block waxedBookshelf = Registries.BLOCK.get(SporesShadows.id("waxed_bookshelf"));
        Block moldyChiseled = Registries.BLOCK.get(SporesShadows.id("moldy_chiseled_bookshelf"));
        Block waxedChiseled = Registries.BLOCK.get(SporesShadows.id("waxed_chiseled_bookshelf"));

        if (moldyBookshelf == Blocks.AIR || waxedBookshelf == Blocks.AIR) {
            context.throwPositionedException("Bookshelf blocks not registered!", BlockPos.ORIGIN);
        }
        if (moldyChiseled == Blocks.AIR || waxedChiseled == Blocks.AIR) {
            context.throwPositionedException("Chiseled bookshelf blocks not registered!", BlockPos.ORIGIN);
        }

        if (!ModBlockEntities.MOLDY_CHISELED_BOOKSHELF.supports(moldyChiseled.getDefaultState())) {
            context.throwPositionedException("MOLDY_CHISELED_BOOKSHELF does not support moldy_chiseled_bookshelf!", BlockPos.ORIGIN);
        }
        if (!ModBlockEntities.MOLDY_CHISELED_BOOKSHELF.supports(waxedChiseled.getDefaultState())) {
            context.throwPositionedException("MOLDY_CHISELED_BOOKSHELF does not support waxed_chiseled_bookshelf!", BlockPos.ORIGIN);
        }

        context.complete();
    }

    // =========================================================================
    // === 2. BOOKSHELF DROPS WITHOUT SILK TOUCH (3 -> 2 -> 1 -> 0 BOOKS)    ===
    // =========================================================================

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testBookshelfDropsWithoutSilkTouch(TestContext context) {
        BlockPos pos = new BlockPos(1, 2, 1);
        PlayerEntity player = context.createMockPlayer(net.minecraft.world.GameMode.SURVIVAL);
        player.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.DIAMOND_AXE));

        Block moldyBookshelf = Registries.BLOCK.get(SporesShadows.id("moldy_bookshelf"));
        Block waxedBookshelf = Registries.BLOCK.get(SporesShadows.id("waxed_bookshelf"));

        // Moldy Bookshelf unwaxed
        // Stage 0: 3 books
        assertBookDropCount(context, moldyBookshelf, 0, false, player, pos, 3);
        // Stage 1 (Tainted): 2 books
        assertBookDropCount(context, moldyBookshelf, 1, false, player, pos, 2);
        // Stage 2 (Moldy): 1 book
        assertBookDropCount(context, moldyBookshelf, 2, false, player, pos, 1);
        // Stage 3 (Rotten): 0 books
        assertBookDropCount(context, moldyBookshelf, 3, false, player, pos, 0);

        // Waxed Bookshelf
        // Stage 0: 3 books
        assertBookDropCount(context, waxedBookshelf, 0, true, player, pos, 3);
        // Stage 1: 2 books
        assertBookDropCount(context, waxedBookshelf, 1, true, player, pos, 2);
        // Stage 2: 1 book
        assertBookDropCount(context, waxedBookshelf, 2, true, player, pos, 1);
        // Stage 3: 0 books
        assertBookDropCount(context, waxedBookshelf, 3, true, player, pos, 0);

        context.complete();
    }

    private void assertBookDropCount(TestContext context, Block block, int stage, boolean waxed, PlayerEntity player, BlockPos pos, int expectedBooks) {
        BlockState state = block.getDefaultState().with(MoldyBlock.STAGE, stage).with(MoldyBlock.WAXED, waxed);
        List<ItemStack> drops = Block.getDroppedStacks(state, context.getWorld(), context.getAbsolutePos(pos), null, player, player.getMainHandStack());

        int bookCount = 0;
        for (ItemStack stack : drops) {
            if (stack.isOf(Items.BOOK)) {
                bookCount += stack.getCount();
            } else {
                context.throwPositionedException("Unexpected drop " + stack.getItem() + " for " + block + " stage " + stage, pos);
            }
        }

        if (bookCount != expectedBooks) {
            context.throwPositionedException("Expected " + expectedBooks + " books for " + block + " stage " + stage + " (waxed=" + waxed + "), got " + bookCount, pos);
        }
    }

    // =========================================================================
    // === 3. BOOKSHELF DROPS WITH SILK TOUCH                                ===
    // =========================================================================

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testBookshelfDropsWithSilkTouch(TestContext context) {
        BlockPos pos = new BlockPos(1, 2, 1);
        PlayerEntity player = context.createMockPlayer(net.minecraft.world.GameMode.SURVIVAL);
        ItemStack silkTouchAxe = new ItemStack(Items.DIAMOND_AXE);
        silkTouchAxe.addEnchantment(context.getWorld().getRegistryManager().get(RegistryKeys.ENCHANTMENT).getEntry(Enchantments.SILK_TOUCH).get(), 1);
        player.setStackInHand(Hand.MAIN_HAND, silkTouchAxe);

        Block moldyBookshelf = Registries.BLOCK.get(SporesShadows.id("moldy_bookshelf"));
        Block waxedBookshelf = Registries.BLOCK.get(SporesShadows.id("waxed_bookshelf"));

        // Unwaxed stages with Silk Touch
        assertSingleDropItem(context, moldyBookshelf, 0, false, player, pos, Items.BOOKSHELF);
        assertSingleDropItem(context, moldyBookshelf, 1, false, player, pos, Registries.ITEM.get(SporesShadows.id("tainted_bookshelf")));
        assertSingleDropItem(context, moldyBookshelf, 2, false, player, pos, Registries.ITEM.get(SporesShadows.id("moldy_bookshelf")));
        assertSingleDropItem(context, moldyBookshelf, 3, false, player, pos, Registries.ITEM.get(SporesShadows.id("rotten_bookshelf")));

        // Waxed stages with Silk Touch
        assertSingleDropItem(context, waxedBookshelf, 0, true, player, pos, Registries.ITEM.get(SporesShadows.id("waxed_bookshelf")));
        assertSingleDropItem(context, waxedBookshelf, 1, true, player, pos, Registries.ITEM.get(SporesShadows.id("waxed_tainted_bookshelf")));
        assertSingleDropItem(context, waxedBookshelf, 2, true, player, pos, Registries.ITEM.get(SporesShadows.id("waxed_moldy_bookshelf")));
        assertSingleDropItem(context, waxedBookshelf, 3, true, player, pos, Registries.ITEM.get(SporesShadows.id("waxed_rotten_bookshelf")));

        context.complete();
    }

    private void assertSingleDropItem(TestContext context, Block block, int stage, boolean waxed, PlayerEntity player, BlockPos pos, Item expectedItem) {
        BlockState state = block.getDefaultState().with(MoldyBlock.STAGE, stage).with(MoldyBlock.WAXED, waxed);
        List<ItemStack> drops = Block.getDroppedStacks(state, context.getWorld(), context.getAbsolutePos(pos), null, player, player.getMainHandStack());

        if (drops.size() != 1) {
            context.throwPositionedException("Expected exactly 1 drop stack for " + block + " stage " + stage + ", got " + drops.size(), pos);
        }
        ItemStack drop = drops.get(0);
        if (!drop.isOf(expectedItem) || drop.getCount() != 1) {
            context.throwPositionedException("Expected 1 " + expectedItem + " but got " + drop.getCount() + " " + drop.getItem(), pos);
        }
    }

    // =========================================================================
    // === 4. CHISELED BOOKSHELF INVENTORY PRESERVATION & COMPARATOR         ===
    // =========================================================================

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testChiseledBookshelfInventoryPreservation(TestContext context) {
        BlockPos pos = new BlockPos(1, 2, 1);
        Block moldyChiseled = Registries.BLOCK.get(SporesShadows.id("moldy_chiseled_bookshelf"));
        Block waxedChiseled = Registries.BLOCK.get(SporesShadows.id("waxed_chiseled_bookshelf"));

        // Place stage 1 moldy chiseled bookshelf
        context.setBlockState(pos, moldyChiseled.getDefaultState().with(MoldyBlock.STAGE, 1).with(MoldyBlock.WAXED, false));

        MoldyChiseledBookshelfBlockEntity be = (MoldyChiseledBookshelfBlockEntity) context.getBlockEntity(pos);
        if (be == null) {
            context.throwPositionedException("BlockEntity is null!", pos);
        }

        // Put books in slot 0, 2, 5
        be.setStack(0, new ItemStack(Items.BOOK));
        be.setStack(2, new ItemStack(Items.WRITTEN_BOOK));
        be.setStack(5, new ItemStack(Items.ENCHANTED_BOOK));

        // Advance stage to 2 using MoldyBlockHelper.setStage
        BlockState state1 = context.getBlockState(pos);
        MoldyBlockHelper.setStage(context.getWorld(), context.getAbsolutePos(pos), state1, 2);

        MoldyChiseledBookshelfBlockEntity beStage2 = (MoldyChiseledBookshelfBlockEntity) context.getBlockEntity(pos);
        if (beStage2 == null) {
            context.throwPositionedException("BlockEntity is null after setStage to 2!", pos);
        }
        if (!beStage2.getStack(0).isOf(Items.BOOK) || !beStage2.getStack(2).isOf(Items.WRITTEN_BOOK) || !beStage2.getStack(5).isOf(Items.ENCHANTED_BOOK)) {
            context.throwPositionedException("Inventory contents lost after decaying to stage 2!", pos);
        }

        // Advance stage to 3
        BlockState state2 = context.getBlockState(pos);
        MoldyBlockHelper.setStage(context.getWorld(), context.getAbsolutePos(pos), state2, 3);

        MoldyChiseledBookshelfBlockEntity beStage3 = (MoldyChiseledBookshelfBlockEntity) context.getBlockEntity(pos);
        if (!beStage3.getStack(0).isOf(Items.BOOK) || !beStage3.getStack(2).isOf(Items.WRITTEN_BOOK) || !beStage3.getStack(5).isOf(Items.ENCHANTED_BOOK)) {
            context.throwPositionedException("Inventory contents lost after decaying to stage 3!", pos);
        }

        // Test waxing via MoldyInteractionEvents (Sneak + Honeycomb)
        PlayerEntity player = context.createMockPlayer(net.minecraft.world.GameMode.SURVIVAL);
        player.setSneaking(true);
        player.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.HONEYCOMB));
        ActionResult waxResult = simulatePlayerUse(context, pos, player);
        if (waxResult != ActionResult.SUCCESS) {
            context.throwPositionedException("Waxing chiseled bookshelf failed!", pos);
        }

        context.expectBlock(waxedChiseled, pos);
        MoldyChiseledBookshelfBlockEntity beWaxed = (MoldyChiseledBookshelfBlockEntity) context.getBlockEntity(pos);
        if (!beWaxed.getStack(0).isOf(Items.BOOK) || !beWaxed.getStack(2).isOf(Items.WRITTEN_BOOK) || !beWaxed.getStack(5).isOf(Items.ENCHANTED_BOOK)) {
            context.throwPositionedException("Inventory contents lost after waxing chiseled bookshelf!", pos);
        }

        // Test dewaxing with Axe
        player.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.DIAMOND_AXE));
        ActionResult dewaxResult = simulatePlayerUse(context, pos, player);
        if (dewaxResult != ActionResult.SUCCESS) {
            context.throwPositionedException("Dewaxing chiseled bookshelf failed!", pos);
        }

        context.expectBlock(moldyChiseled, pos);
        MoldyChiseledBookshelfBlockEntity beDewaxed = (MoldyChiseledBookshelfBlockEntity) context.getBlockEntity(pos);
        if (!beDewaxed.getStack(0).isOf(Items.BOOK) || !beDewaxed.getStack(2).isOf(Items.WRITTEN_BOOK) || !beDewaxed.getStack(5).isOf(Items.ENCHANTED_BOOK)) {
            context.throwPositionedException("Inventory contents lost after dewaxing chiseled bookshelf!", pos);
        }

        // Test NBT serialization/deserialization
        NbtCompound nbt = beDewaxed.createNbtWithId(context.getWorld().getRegistryManager());
        MoldyChiseledBookshelfBlockEntity restored = new MoldyChiseledBookshelfBlockEntity(context.getAbsolutePos(pos), moldyChiseled.getDefaultState());
        restored.read(nbt, context.getWorld().getRegistryManager());
        if (!restored.getStack(0).isOf(Items.BOOK) || !restored.getStack(2).isOf(Items.WRITTEN_BOOK) || !restored.getStack(5).isOf(Items.ENCHANTED_BOOK)) {
            context.throwPositionedException("Inventory contents lost after NBT restore!", pos);
        }

        context.complete();
    }

    // =========================================================================
    // === 5. ENCHANTING POWER SCALING TESTS                                 ===
    // =========================================================================

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testEnchantingPowerScaling(TestContext context) {
        BlockPos tablePos = new BlockPos(2, 2, 2);
        context.setBlockState(tablePos, Blocks.ENCHANTING_TABLE.getDefaultState());

        Block moldyBookshelf = Registries.BLOCK.get(SporesShadows.id("moldy_bookshelf"));

        // Setup 15 clean bookshelves around table
        // POWER_PROVIDER_OFFSETS contains all valid offset positions around table
        List<BlockPos> validOffsets = EnchantingTableBlock.POWER_PROVIDER_OFFSETS;
        if (validOffsets.size() < 15) {
            context.throwPositionedException("POWER_PROVIDER_OFFSETS has fewer than 15 positions!", tablePos);
        }

        // Place 15 clean bookshelves
        for (int i = 0; i < 15; i++) {
            BlockPos bPos = tablePos.add(validOffsets.get(i));
            context.setBlockState(bPos, Blocks.BOOKSHELF.getDefaultState());
        }

        // ScreenHandler with clean bookshelves
        PlayerEntity player = context.createMockPlayer(net.minecraft.world.GameMode.SURVIVAL);
        EnchantmentScreenHandler handler = new EnchantmentScreenHandler(0, player.getInventory(), ScreenHandlerContext.create(context.getWorld(), context.getAbsolutePos(tablePos)));
        ItemStack sword = new ItemStack(Items.DIAMOND_SWORD);
        handler.getSlot(0).setStack(sword);
        handler.onContentChanged(handler.getSlot(0).inventory);

        int cleanLevelSlot3 = handler.enchantmentPower[2];
        if (cleanLevelSlot3 < 30) {
            context.throwPositionedException("15 clean bookshelves should yield level 30 enchant in slot 3, got: " + cleanLevelSlot3, tablePos);
        }

        // Test with 15 Rotten bookshelves: should give 0 bookshelf power
        for (int i = 0; i < 15; i++) {
            BlockPos bPos = tablePos.add(validOffsets.get(i));
            context.setBlockState(bPos, moldyBookshelf.getDefaultState().with(MoldyBlock.STAGE, 3).with(MoldyBlock.WAXED, false));
        }

        EnchantmentScreenHandler rottenHandler = new EnchantmentScreenHandler(0, player.getInventory(), ScreenHandlerContext.create(context.getWorld(), context.getAbsolutePos(tablePos)));
        rottenHandler.getSlot(0).setStack(sword);
        rottenHandler.onContentChanged(rottenHandler.getSlot(0).inventory);

        int rottenLevelSlot3 = rottenHandler.enchantmentPower[2];
        if (rottenLevelSlot3 > 10) {
            context.throwPositionedException("15 rotten bookshelves should yield near 0 power (<= 10), got: " + rottenLevelSlot3, tablePos);
        }

        // Check canAccessPowerProvider directly for rotten bookshelves: must be false
        for (int i = 0; i < 15; i++) {
            boolean accessible = EnchantingTableBlock.canAccessPowerProvider(context.getWorld(), context.getAbsolutePos(tablePos), validOffsets.get(i));
            if (accessible) {
                context.throwPositionedException("Rotten bookshelf should NOT be accessible power provider at offset " + validOffsets.get(i), tablePos);
            }
        }

        // Test with 15 Tainted bookshelves: stage 1 unwaxed
        for (int i = 0; i < 15; i++) {
            BlockPos bPos = tablePos.add(validOffsets.get(i));
            context.setBlockState(bPos, moldyBookshelf.getDefaultState().with(MoldyBlock.STAGE, 1).with(MoldyBlock.WAXED, false));
        }

        EnchantmentScreenHandler taintedHandler = new EnchantmentScreenHandler(0, player.getInventory(), ScreenHandlerContext.create(context.getWorld(), context.getAbsolutePos(tablePos)));
        taintedHandler.getSlot(0).setStack(sword);
        taintedHandler.onContentChanged(taintedHandler.getSlot(0).inventory);

        int taintedLevelSlot3 = taintedHandler.enchantmentPower[2];
        // 15 tainted bookshelves give 15 * 0.66 = ~10 bookshelves power. Slot 3 level requirement with ~10 power should be strictly between clean and rotten.
        if (taintedLevelSlot3 >= cleanLevelSlot3 || taintedLevelSlot3 <= rottenLevelSlot3) {
            context.throwPositionedException("Tainted level requirement (" + taintedLevelSlot3 + ") must be strictly between clean (" + cleanLevelSlot3 + ") and rotten (" + rottenLevelSlot3 + ")", tablePos);
        }

        context.complete();
    }

    // =========================================================================
    // === 6. CHISELED BOOKSHELF BREAK SCATTERS STORED BOOKS                 ===
    // =========================================================================

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testChiseledBookshelfBreakScattersItems(TestContext context) {
        BlockPos pos = new BlockPos(1, 2, 1);
        Block moldyChiseled = Registries.BLOCK.get(SporesShadows.id("moldy_chiseled_bookshelf"));

        context.setBlockState(pos, moldyChiseled.getDefaultState().with(MoldyBlock.STAGE, 1));
        MoldyChiseledBookshelfBlockEntity be = (MoldyChiseledBookshelfBlockEntity) context.getBlockEntity(pos);
        be.setStack(0, new ItemStack(Items.BOOK));
        be.setStack(3, new ItemStack(Items.WRITTEN_BOOK));

        // Break block by setting to AIR (triggers onStateReplaced)
        context.setBlockState(pos, Blocks.AIR.getDefaultState());

        // Check that item entities were spawned
        List<net.minecraft.entity.ItemEntity> items = context.getWorld().getEntitiesByClass(
                net.minecraft.entity.ItemEntity.class,
                new net.minecraft.util.math.Box(context.getAbsolutePos(pos)).expand(2.0),
                entity -> entity.getStack().isOf(Items.BOOK) || entity.getStack().isOf(Items.WRITTEN_BOOK)
        );

        if (items.size() < 2) {
            context.throwPositionedException("Breaking chiseled bookshelf must scatter stored books! Found: " + items.size(), pos);
        }

        context.complete();
    }

    // =========================================================================
    // === 6. PICK STACK & JADE RAYTRACE ACCURACY                            ===
    // =========================================================================

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testBookshelfPickStackAndJadeRaytrace(TestContext context) {
        Block moldyBookshelf = Registries.BLOCK.get(SporesShadows.id("moldy_bookshelf"));
        Block waxedBookshelf = Registries.BLOCK.get(SporesShadows.id("waxed_bookshelf"));
        BlockPos pos = new BlockPos(1, 1, 1);

        for (int stage = 0; stage <= 3; stage++) {
            // Unwaxed
            BlockState unwaxedState = moldyBookshelf.getDefaultState().with(MoldyBlock.STAGE, stage).with(MoldyBlock.WAXED, false);
            ItemStack unwaxedPick = unwaxedState.getBlock().getPickStack(context.getWorld(), context.getAbsolutePos(pos), unwaxedState);
            if (stage == 0) {
                if (!unwaxedPick.isOf(Blocks.BOOKSHELF.asItem())) {
                    context.throwPositionedException("Stage 0 unwaxed bookshelf must pick vanilla bookshelf", pos);
                }
            } else {
                List<Item> items = ModBlocks.MOLDY_ITEMS_BY_BLOCK.get(moldyBookshelf);
                Item expected = items.get(stage * 2 - 1);
                if (!unwaxedPick.isOf(expected)) {
                    context.throwPositionedException("Stage " + stage + " unwaxed bookshelf pick stack mismatch! Expected " + expected + " got " + unwaxedPick.getItem(), pos);
                }
            }

            // Waxed
            BlockState waxedState = waxedBookshelf.getDefaultState().with(MoldyBlock.STAGE, stage).with(MoldyBlock.WAXED, true);
            ItemStack waxedPick = waxedState.getBlock().getPickStack(context.getWorld(), context.getAbsolutePos(pos), waxedState);
            List<Item> items = ModBlocks.MOLDY_ITEMS_BY_BLOCK.get(waxedBookshelf);
            Item expectedWaxed = items.get(stage * 2);
            if (!waxedPick.isOf(expectedWaxed)) {
                context.throwPositionedException("Stage " + stage + " waxed bookshelf pick stack mismatch! Expected " + expectedWaxed + " got " + waxedPick.getItem(), pos);
            }
        }

        context.complete();
    }

    // =========================================================================
    // === 7. SCALAR BOOK DROPS                                              ===
    // =========================================================================

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testBookshelfScalarBookDrops(TestContext context) {
        Block moldyBookshelf = Registries.BLOCK.get(SporesShadows.id("moldy_bookshelf"));
        Block waxedBookshelf = Registries.BLOCK.get(SporesShadows.id("waxed_bookshelf"));
        BlockPos pos = new BlockPos(1, 1, 1);

        PlayerEntity normalPlayer = context.createMockPlayer(net.minecraft.world.GameMode.SURVIVAL);
        ItemStack axe = new ItemStack(Items.DIAMOND_AXE);
        normalPlayer.setStackInHand(Hand.MAIN_HAND, axe);

        PlayerEntity silkPlayer = context.createMockPlayer(net.minecraft.world.GameMode.SURVIVAL);
        ItemStack silkAxe = new ItemStack(Items.DIAMOND_AXE);
        silkAxe.addEnchantment(context.getWorld().getRegistryManager().get(RegistryKeys.ENCHANTMENT).getEntry(Enchantments.SILK_TOUCH).get(), 1);
        silkPlayer.setStackInHand(Hand.MAIN_HAND, silkAxe);

        int[] expectedBooks = { 3, 2, 1, 0 };

        for (int stage = 0; stage <= 3; stage++) {
            // Test unwaxed
            BlockState unwaxedState = moldyBookshelf.getDefaultState().with(MoldyBlock.STAGE, stage).with(MoldyBlock.WAXED, false);
            context.setBlockState(pos, unwaxedState);
            List<ItemStack> normalDrops = Block.getDroppedStacks(unwaxedState, context.getWorld(), context.getAbsolutePos(pos), null, normalPlayer, axe);
            int bookCount = normalDrops.stream().filter(s -> s.isOf(Items.BOOK)).mapToInt(ItemStack::getCount).sum();
            if (bookCount != expectedBooks[stage]) {
                context.throwPositionedException("Expected " + expectedBooks[stage] + " books at stage " + stage + ", but got " + bookCount, pos);
            }

            // Test unwaxed with Silk Touch
            List<ItemStack> silkDrops = Block.getDroppedStacks(unwaxedState, context.getWorld(), context.getAbsolutePos(pos), null, silkPlayer, silkAxe);
            if (stage == 0) {
                if (silkDrops.stream().noneMatch(s -> s.isOf(Blocks.BOOKSHELF.asItem()))) {
                    context.throwPositionedException("Silk Touch at stage 0 unwaxed must drop bookshelf", pos);
                }
            } else {
                List<Item> items = ModBlocks.MOLDY_ITEMS_BY_BLOCK.get(moldyBookshelf);
                Item expectedItem = items.get(stage * 2 - 1);
                if (silkDrops.stream().noneMatch(s -> s.isOf(expectedItem))) {
                    context.throwPositionedException("Silk Touch at stage " + stage + " unwaxed must drop " + expectedItem, pos);
                }
            }

            // Test waxed
            BlockState waxedState = waxedBookshelf.getDefaultState().with(MoldyBlock.STAGE, stage).with(MoldyBlock.WAXED, true);
            context.setBlockState(pos, waxedState);
            List<ItemStack> waxedNormalDrops = Block.getDroppedStacks(waxedState, context.getWorld(), context.getAbsolutePos(pos), null, normalPlayer, axe);
            int waxedBookCount = waxedNormalDrops.stream().filter(s -> s.isOf(Items.BOOK)).mapToInt(ItemStack::getCount).sum();
            if (waxedBookCount != expectedBooks[stage]) {
                context.throwPositionedException("Expected " + expectedBooks[stage] + " books at waxed stage " + stage + ", but got " + waxedBookCount, pos);
            }

            // Test waxed with Silk Touch
            List<ItemStack> waxedSilkDrops = Block.getDroppedStacks(waxedState, context.getWorld(), context.getAbsolutePos(pos), null, silkPlayer, silkAxe);
            List<Item> items = ModBlocks.MOLDY_ITEMS_BY_BLOCK.get(waxedBookshelf);
            Item expectedWaxedItem = items.get(stage * 2);
            if (waxedSilkDrops.stream().noneMatch(s -> s.isOf(expectedWaxedItem))) {
                context.throwPositionedException("Silk Touch at stage " + stage + " waxed must drop " + expectedWaxedItem, pos);
            }
        }

        context.complete();
    }
}
