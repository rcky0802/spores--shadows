package moldmod.test.gametest.wood;

import moldmod.SporesShadows;
import moldmod.block.ModBlocks;
import moldmod.block.core.MoldyBlock;
import moldmod.block.core.MoldyBlockHelper;
import moldmod.block.redstone.MoldyJukeboxBlock;
import moldmod.block.entity.ModBlockEntities;
import moldmod.block.redstone.MoldyJukeboxBlockEntity;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.state.property.Properties;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;

import java.util.List;

public class MoldySpecialDevicesGameTests {

    private ActionResult simulatePlayerUse(TestContext context, BlockPos pos, PlayerEntity player) {
        BlockHitResult hit = new BlockHitResult(context.getAbsolutePos(pos).toCenterPos(), Direction.UP, context.getAbsolutePos(pos), false);
        return UseBlockCallback.EVENT.invoker().interact(player, context.getWorld(), Hand.MAIN_HAND, hit);
    }

    // =========================================================================
    // === 1. LADDER TESTS                                                   ===
    // =========================================================================

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testLadderRegistrationAndClimbableTag(TestContext context) {
        Block moldyLadder = Registries.BLOCK.get(SporesShadows.id("moldy_ladder"));
        Block waxedLadder = Registries.BLOCK.get(SporesShadows.id("waxed_ladder"));

        if (moldyLadder == Blocks.AIR || waxedLadder == Blocks.AIR) {
            context.throwPositionedException("Ladder blocks not registered!", BlockPos.ORIGIN);
        }

        if (!moldyLadder.getDefaultState().isIn(BlockTags.CLIMBABLE)) {
            context.throwPositionedException("moldy_ladder is not in BlockTags.CLIMBABLE!", BlockPos.ORIGIN);
        }
        if (!waxedLadder.getDefaultState().isIn(BlockTags.CLIMBABLE)) {
            context.throwPositionedException("waxed_ladder is not in BlockTags.CLIMBABLE!", BlockPos.ORIGIN);
        }

        // Test facing orientation
        BlockPos pos = new BlockPos(1, 1, 1);
        context.setBlockState(pos, moldyLadder.getDefaultState().with(Properties.HORIZONTAL_FACING, Direction.NORTH));
        if (context.getBlockState(pos).get(Properties.HORIZONTAL_FACING) != Direction.NORTH) {
            context.throwPositionedException("Ladder facing not preserved!", pos);
        }

        context.complete();
    }

    // =========================================================================
    // === 2. NOTE BLOCK TESTS                                               ===
    // =========================================================================

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testNoteBlockPropertiesAndAcousticEvent(TestContext context) {
        Block moldyNoteBlock = Registries.BLOCK.get(SporesShadows.id("moldy_note_block"));
        Block waxedNoteBlock = Registries.BLOCK.get(SporesShadows.id("waxed_note_block"));

        if (moldyNoteBlock == Blocks.AIR || waxedNoteBlock == Blocks.AIR) {
            context.throwPositionedException("Note block blocks not registered!", BlockPos.ORIGIN);
        }

        BlockPos pos = new BlockPos(1, 1, 1);

        // Test note tuning across stages
        for (int stage = 0; stage <= 3; stage++) {
            context.setBlockState(pos, moldyNoteBlock.getDefaultState()
                    .with(MoldyBlock.STAGE, stage)
                    .with(Properties.NOTE, 12)
                    .with(Properties.INSTRUMENT, NoteBlockInstrument.HARP));

            BlockState state = context.getBlockState(pos);
            if (state.get(Properties.NOTE) != 12) {
                context.throwPositionedException("Note pitch 12 not retained at stage " + stage, pos);
            }
            if (state.get(Properties.INSTRUMENT) != NoteBlockInstrument.HARP) {
                context.throwPositionedException("Instrument HARP not retained at stage " + stage, pos);
            }

            // Trigger redstone play event
            context.getWorld().addSyncedBlockEvent(context.getAbsolutePos(pos), moldyNoteBlock, 0, 0);
            boolean eventResult = state.onSyncedBlockEvent(context.getWorld(), context.getAbsolutePos(pos), 0, 0);
            if (!eventResult) {
                context.throwPositionedException("onSyncedBlockEvent returned false for stage " + stage, pos);
            }
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testMoldyNoteBlockAcousticDistortionAcrossInstruments(TestContext context) {
        Block moldyNoteBlock = Registries.BLOCK.get(SporesShadows.id("moldy_note_block"));
        BlockPos pos = new BlockPos(1, 1, 1);

        NoteBlockInstrument[] instruments = new NoteBlockInstrument[]{
                NoteBlockInstrument.HARP,
                NoteBlockInstrument.BASEDRUM,
                NoteBlockInstrument.SNARE,
                NoteBlockInstrument.FLUTE,
                NoteBlockInstrument.BELL
        };

        for (NoteBlockInstrument inst : instruments) {
            for (int stage = 0; stage <= 3; stage++) {
                context.setBlockState(pos, moldyNoteBlock.getDefaultState()
                        .with(MoldyBlock.STAGE, stage)
                        .with(Properties.NOTE, 6 * stage)
                        .with(Properties.INSTRUMENT, inst));

                BlockState state = context.getBlockState(pos);
                boolean result = state.onSyncedBlockEvent(context.getWorld(), context.getAbsolutePos(pos), 0, 0);
                if (!result) {
                    context.throwPositionedException("Distortion play failed for " + inst + " at stage " + stage, pos);
                }
            }
        }

        // Test CUSTOM_HEAD without head above: must return false across all stages (matching vanilla NoteBlock)
        for (int stage = 0; stage <= 3; stage++) {
            context.setBlockState(pos, moldyNoteBlock.getDefaultState()
                    .with(MoldyBlock.STAGE, stage)
                    .with(Properties.NOTE, 12)
                    .with(Properties.INSTRUMENT, NoteBlockInstrument.CUSTOM_HEAD));
            context.setBlockState(pos.up(), Blocks.AIR.getDefaultState());

            BlockState state = context.getBlockState(pos);
            boolean result = state.onSyncedBlockEvent(context.getWorld(), context.getAbsolutePos(pos), 0, 0);
            if (result) {
                context.throwPositionedException("CUSTOM_HEAD without head above must return false at stage " + stage, pos);
            }
        }

        context.complete();
    }

    // =========================================================================
    // === 3. JUKEBOX INVENTORY PRESERVATION & REDSTONE                      ===
    // =========================================================================

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testJukeboxDiscPreservationAcrossDecayAndWaxing(TestContext context) {
        Block moldyJukebox = Registries.BLOCK.get(SporesShadows.id("moldy_jukebox"));
        Block waxedJukebox = Registries.BLOCK.get(SporesShadows.id("waxed_jukebox"));

        if (moldyJukebox == Blocks.AIR || waxedJukebox == Blocks.AIR) {
            context.throwPositionedException("Jukebox blocks not registered!", BlockPos.ORIGIN);
        }
        if (!ModBlockEntities.MOLDY_JUKEBOX.supports(moldyJukebox.getDefaultState())) {
            context.throwPositionedException("MOLDY_JUKEBOX does not support moldy_jukebox!", BlockPos.ORIGIN);
        }
        if (!ModBlockEntities.MOLDY_JUKEBOX.supports(waxedJukebox.getDefaultState())) {
            context.throwPositionedException("MOLDY_JUKEBOX does not support waxed_jukebox!", BlockPos.ORIGIN);
        }

        BlockPos pos = new BlockPos(1, 2, 1);
        context.setBlockState(pos, moldyJukebox.getDefaultState()
                .with(MoldyBlock.STAGE, 1)
                .with(MoldyBlock.WAXED, false)
                .with(MoldyJukeboxBlock.HAS_RECORD, true));

        MoldyJukeboxBlockEntity be = (MoldyJukeboxBlockEntity) context.getBlockEntity(pos);
        if (be == null) {
            context.throwPositionedException("Jukebox block entity is null!", pos);
        }

        // Set record (e.g. Music Disc 13)
        ItemStack disc = new ItemStack(Items.MUSIC_DISC_13);
        be.setStack(0, disc);

        // Verify comparator output
        int comparatorSignal = context.getBlockState(pos).getComparatorOutput(context.getWorld(), context.getAbsolutePos(pos));
        if (comparatorSignal <= 0) {
            context.throwPositionedException("Jukebox with record must emit positive comparator signal! Got: " + comparatorSignal, pos);
        }

        // Advance stage to 2 with MoldyBlockHelper.setStage
        BlockState state1 = context.getBlockState(pos);
        MoldyBlockHelper.setStage(context.getWorld(), context.getAbsolutePos(pos), state1, 2);

        MoldyJukeboxBlockEntity beStage2 = (MoldyJukeboxBlockEntity) context.getBlockEntity(pos);
        if (beStage2 == null || !beStage2.getStack(0).isOf(Items.MUSIC_DISC_13)) {
            context.throwPositionedException("Music disc lost after decaying to stage 2!", pos);
        }

        // Advance stage to 3
        BlockState state2 = context.getBlockState(pos);
        MoldyBlockHelper.setStage(context.getWorld(), context.getAbsolutePos(pos), state2, 3);

        MoldyJukeboxBlockEntity beStage3 = (MoldyJukeboxBlockEntity) context.getBlockEntity(pos);
        if (beStage3 == null || !beStage3.getStack(0).isOf(Items.MUSIC_DISC_13)) {
            context.throwPositionedException("Music disc lost after decaying to stage 3!", pos);
        }

        // Wax via Sneak + Honeycomb
        PlayerEntity player = context.createMockPlayer(net.minecraft.world.GameMode.SURVIVAL);
        player.setSneaking(true);
        player.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.HONEYCOMB));
        ActionResult waxResult = simulatePlayerUse(context, pos, player);
        if (waxResult != ActionResult.SUCCESS) {
            context.throwPositionedException("Waxing jukebox failed!", pos);
        }

        context.expectBlock(waxedJukebox, pos);
        MoldyJukeboxBlockEntity beWaxed = (MoldyJukeboxBlockEntity) context.getBlockEntity(pos);
        if (beWaxed == null || !beWaxed.getStack(0).isOf(Items.MUSIC_DISC_13)) {
            context.throwPositionedException("Music disc lost after waxing jukebox!", pos);
        }

        // Dewax with Axe
        player.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.DIAMOND_AXE));
        ActionResult dewaxResult = simulatePlayerUse(context, pos, player);
        if (dewaxResult != ActionResult.SUCCESS) {
            context.throwPositionedException("Dewaxing jukebox failed!", pos);
        }

        context.expectBlock(moldyJukebox, pos);
        MoldyJukeboxBlockEntity beDewaxed = (MoldyJukeboxBlockEntity) context.getBlockEntity(pos);
        if (beDewaxed == null || !beDewaxed.getStack(0).isOf(Items.MUSIC_DISC_13)) {
            context.throwPositionedException("Music disc lost after dewaxing jukebox!", pos);
        }

        // NBT serialization
        NbtCompound nbt = beDewaxed.createNbtWithId(context.getWorld().getRegistryManager());
        MoldyJukeboxBlockEntity restored = new MoldyJukeboxBlockEntity(context.getAbsolutePos(pos), moldyJukebox.getDefaultState());
        restored.read(nbt, context.getWorld().getRegistryManager());
        if (!restored.getStack(0).isOf(Items.MUSIC_DISC_13)) {
            context.throwPositionedException("Music disc lost after NBT restore!", pos);
        }

        context.complete();
    }

    // =========================================================================
    // === 4. JUKEBOX BREAK SCATTERS DISC                                    ===
    // =========================================================================

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testJukeboxBreakScattersDisc(TestContext context) {
        BlockPos pos = new BlockPos(1, 2, 1);
        Block moldyJukebox = Registries.BLOCK.get(SporesShadows.id("moldy_jukebox"));

        context.setBlockState(pos, moldyJukebox.getDefaultState()
                .with(MoldyBlock.STAGE, 1)
                .with(MoldyJukeboxBlock.HAS_RECORD, true));

        MoldyJukeboxBlockEntity be = (MoldyJukeboxBlockEntity) context.getBlockEntity(pos);
        be.setStack(0, new ItemStack(Items.MUSIC_DISC_CAT));

        // Break block by setting to AIR (triggers onStateReplaced)
        context.setBlockState(pos, Blocks.AIR.getDefaultState());

        List<ItemEntity> items = context.getWorld().getEntitiesByClass(
                ItemEntity.class,
                new Box(context.getAbsolutePos(pos)).expand(2.0),
                entity -> entity.getStack().isOf(Items.MUSIC_DISC_CAT)
        );

        if (items.isEmpty()) {
            context.throwPositionedException("Breaking jukebox with disc must scatter the disc!", pos);
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testJukeboxDiscInsertionViaUseWithItem(TestContext context) {
        BlockPos pos = new BlockPos(1, 2, 1);
        Block moldyJukebox = Registries.BLOCK.get(SporesShadows.id("moldy_jukebox"));

        context.setBlockState(pos, moldyJukebox.getDefaultState()
                .with(MoldyBlock.STAGE, 1)
                .with(MoldyJukeboxBlock.HAS_RECORD, false));

        PlayerEntity player = context.createMockPlayer(net.minecraft.world.GameMode.SURVIVAL);
        ItemStack disc = new ItemStack(Items.MUSIC_DISC_CAT);
        player.setStackInHand(Hand.MAIN_HAND, disc);

        net.minecraft.util.hit.BlockHitResult hit = new net.minecraft.util.hit.BlockHitResult(
                net.minecraft.util.math.Vec3d.ofCenter(context.getAbsolutePos(pos)),
                Direction.UP,
                context.getAbsolutePos(pos),
                false
        );

        net.minecraft.util.ItemActionResult result = context.getBlockState(pos).onUseWithItem(disc, context.getWorld(), player, Hand.MAIN_HAND, hit);
        if (!result.isAccepted()) {
            context.throwPositionedException("Failed to insert music disc into moldy jukebox via onUseWithItem! Result: " + result, pos);
        }

        BlockState stateAfter = context.getBlockState(pos);
        if (!stateAfter.get(MoldyJukeboxBlock.HAS_RECORD)) {
            context.throwPositionedException("HAS_RECORD must be true after inserting music disc!", pos);
        }

        MoldyJukeboxBlockEntity be = (MoldyJukeboxBlockEntity) context.getBlockEntity(pos);
        if (be == null || !be.getStack().isOf(Items.MUSIC_DISC_CAT)) {
            context.throwPositionedException("JukeboxBlockEntity does not contain MUSIC_DISC_CAT after insertion!", pos);
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testJukeboxStage3MechanicalJamming(TestContext context) {
        BlockPos pos = new BlockPos(1, 2, 1);
        Block moldyJukebox = Registries.BLOCK.get(SporesShadows.id("moldy_jukebox"));

        context.setBlockState(pos, moldyJukebox.getDefaultState()
                .with(MoldyBlock.STAGE, 3)
                .with(MoldyBlock.WAXED, false)
                .with(MoldyJukeboxBlock.HAS_RECORD, true));

        MoldyJukeboxBlockEntity be = (MoldyJukeboxBlockEntity) context.getBlockEntity(pos);
        if (be == null) {
            context.throwPositionedException("JukeboxBlockEntity is null!", pos);
        }

        be.setStack(new ItemStack(Items.MUSIC_DISC_13));
        if (!be.getManager().isPlaying()) {
            context.throwPositionedException("Jukebox must be playing music after setStack!", pos);
        }

        // Fast forward jam counter to 1 tick
        be.setJamTicksRemaining(1);

        context.waitAndRun(2, () -> {
            if (be.getManager().isPlaying()) {
                context.throwPositionedException("Stage 3 moldy jukebox must jam and stop playing!", pos);
            }
            if (!be.getStack().isOf(Items.MUSIC_DISC_13)) {
                context.throwPositionedException("Jammed jukebox must preserve disc in inventory!", pos);
            }
            context.complete();
        });
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testSpecialDevicesCreativeTabEntries(TestContext context) {
        Item[] vanillaItems = new Item[]{
                Blocks.LADDER.asItem(),
                Blocks.BOOKSHELF.asItem(),
                Blocks.CHISELED_BOOKSHELF.asItem(),
                Blocks.NOTE_BLOCK.asItem(),
                Blocks.JUKEBOX.asItem()
        };

        for (Item vanillaItem : vanillaItems) {
            List<Item> variants = ModBlocks.MOLDY_ITEMS_BY_VANILLA.get(vanillaItem);
            if (variants == null || variants.size() != 7) {
                context.throwPositionedException("Expected 7 moldy variants for " + vanillaItem + " but got: " + (variants == null ? "null" : variants.size()), BlockPos.ORIGIN);
            }
        }

        context.complete();
    }

    // =========================================================================
    // === 5. PICK STACK & JADE RAYTRACE ACCURACY                            ===
    // =========================================================================

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testSpecialDevicesPickStackAndJadeRaytrace(TestContext context) {
        BlockPos pos = new BlockPos(1, 1, 1);
        Block[] blocks = new Block[]{
                Registries.BLOCK.get(SporesShadows.id("moldy_ladder")),
                Registries.BLOCK.get(SporesShadows.id("moldy_note_block")),
                Registries.BLOCK.get(SporesShadows.id("moldy_jukebox"))
        };

        for (Block moldyBlock : blocks) {
            Block waxedBlock = ModBlocks.MOLDY_TO_WAXED.get(moldyBlock);
            Block vanillaBlock = ModBlocks.MOLDY_TO_VANILLA.get(moldyBlock);

            for (int stage = 0; stage <= 3; stage++) {
                // Unwaxed
                BlockState unwaxedState = moldyBlock.getDefaultState().with(MoldyBlock.STAGE, stage).with(MoldyBlock.WAXED, false);
                ItemStack unwaxedPick = unwaxedState.getBlock().getPickStack(context.getWorld(), context.getAbsolutePos(pos), unwaxedState);
                if (stage == 0) {
                    if (!unwaxedPick.isOf(vanillaBlock.asItem())) {
                        context.throwPositionedException("Stage 0 unwaxed " + moldyBlock + " must pick vanilla item " + vanillaBlock, pos);
                    }
                } else {
                    List<Item> items = ModBlocks.MOLDY_ITEMS_BY_BLOCK.get(moldyBlock);
                    Item expected = items.get(stage * 2 - 1);
                    if (!unwaxedPick.isOf(expected)) {
                        context.throwPositionedException("Stage " + stage + " unwaxed " + moldyBlock + " pick mismatch! Expected " + expected + " got " + unwaxedPick.getItem(), pos);
                    }
                }

                // Waxed
                BlockState waxedState = waxedBlock.getDefaultState().with(MoldyBlock.STAGE, stage).with(MoldyBlock.WAXED, true);
                ItemStack waxedPick = waxedState.getBlock().getPickStack(context.getWorld(), context.getAbsolutePos(pos), waxedState);
                List<Item> items = ModBlocks.MOLDY_ITEMS_BY_BLOCK.get(waxedBlock);
                Item expectedWaxed = items.get(stage * 2);
                if (!waxedPick.isOf(expectedWaxed)) {
                    context.throwPositionedException("Stage " + stage + " waxed " + waxedBlock + " pick mismatch! Expected " + expectedWaxed + " got " + waxedPick.getItem(), pos);
                }
            }
        }

        context.complete();
    }

    // =========================================================================
    // === 6. LADDER COLLISION PHYSICS                                       ===
    // =========================================================================

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testLadderPhysicsAndSlipperyClimb(TestContext context) {
        Block moldyLadder = Registries.BLOCK.get(SporesShadows.id("moldy_ladder"));
        BlockPos pos = new BlockPos(1, 2, 1);

        // Stage 2: Slippery downward acceleration
        BlockState stage2 = moldyLadder.getDefaultState().with(MoldyBlock.STAGE, 2).with(MoldyBlock.WAXED, false);
        context.setBlockState(pos, stage2);

        PlayerEntity player = context.createMockPlayer(net.minecraft.world.GameMode.SURVIVAL);
        player.setSneaking(false);
        player.setVelocity(0.0, -0.1, 0.0);

        stage2.onEntityCollision(context.getWorld(), context.getAbsolutePos(pos), player);
        if (player.getVelocity().y >= -0.1) {
            context.throwPositionedException("Slippery moldy ladder must accelerate downward velocity", pos);
        }

        // Stage 3: Rotten ladder crumbling collision call
        BlockState stage3 = moldyLadder.getDefaultState().with(MoldyBlock.STAGE, 3).with(MoldyBlock.WAXED, false);
        context.setBlockState(pos, stage3);
        stage3.onEntityCollision(context.getWorld(), context.getAbsolutePos(pos), player);

        context.complete();
    }

    // =========================================================================
    // === 7. CREATIVE TAB REGISTRATION                                      ===
    // =========================================================================

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testCreativeTabRegistration(TestContext context) {
        net.minecraft.item.ItemGroup group = Registries.ITEM_GROUP.get(ModBlocks.SPORES_SHADOWS_GROUP_KEY);
        if (group == null) {
            context.throwPositionedException("Spores & Shadows creative tab is not registered in Registries.ITEM_GROUP!", BlockPos.ORIGIN);
        }

        if (group.getIcon().isEmpty()) {
            context.throwPositionedException("Spores & Shadows creative tab icon must not be empty!", BlockPos.ORIGIN);
        }

        net.minecraft.item.ItemGroup.DisplayContext displayContext = new net.minecraft.item.ItemGroup.DisplayContext(
                context.getWorld().getEnabledFeatures(),
                false,
                context.getWorld().getRegistryManager()
        );

        // 1. Building Blocks: oak_planks -> moldy_oak_planks
        assertItemFollowsVanilla(context, net.minecraft.item.ItemGroups.BUILDING_BLOCKS, displayContext,
                Items.OAK_PLANKS, Registries.ITEM.get(SporesShadows.id("moldy_oak_planks")));

        // 2. Natural Blocks: oak_log -> moldy_oak_log
        assertItemFollowsVanilla(context, net.minecraft.item.ItemGroups.NATURAL, displayContext,
                Items.OAK_LOG, Registries.ITEM.get(SporesShadows.id("moldy_oak_log")));

        // 3. Functional Blocks: ladder -> moldy_ladder, bookshelf -> moldy_bookshelf, jukebox -> moldy_jukebox
        assertItemFollowsVanilla(context, net.minecraft.item.ItemGroups.FUNCTIONAL, displayContext,
                Items.LADDER, Registries.ITEM.get(SporesShadows.id("moldy_ladder")));
        assertItemFollowsVanilla(context, net.minecraft.item.ItemGroups.FUNCTIONAL, displayContext,
                Items.BOOKSHELF, Registries.ITEM.get(SporesShadows.id("moldy_bookshelf")));
        assertItemFollowsVanilla(context, net.minecraft.item.ItemGroups.FUNCTIONAL, displayContext,
                Items.JUKEBOX, Registries.ITEM.get(SporesShadows.id("moldy_jukebox")));

        // 4. Redstone: note_block -> moldy_note_block, oak_button -> moldy_oak_button
        assertItemFollowsVanilla(context, net.minecraft.item.ItemGroups.REDSTONE, displayContext,
                Items.NOTE_BLOCK, Registries.ITEM.get(SporesShadows.id("moldy_note_block")));
        assertItemFollowsVanilla(context, net.minecraft.item.ItemGroups.REDSTONE, displayContext,
                Items.OAK_BUTTON, Registries.ITEM.get(SporesShadows.id("moldy_oak_button")));

        context.complete();
    }

    private void assertItemFollowsVanilla(TestContext context, net.minecraft.registry.RegistryKey<net.minecraft.item.ItemGroup> groupKey,
            net.minecraft.item.ItemGroup.DisplayContext displayContext, Item vanillaItem, Item moldyItem) {
        net.minecraft.item.ItemGroup tab = Registries.ITEM_GROUP.get(groupKey);
        if (tab == null) {
            context.throwPositionedException("Creative tab " + groupKey.getValue() + " not found!", BlockPos.ORIGIN);
        }
        tab.updateEntries(displayContext);
        java.util.List<ItemStack> list = new java.util.ArrayList<>(tab.getDisplayStacks());
        int vanillaIndex = -1;
        int moldyIndex = -1;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).isOf(vanillaItem) && vanillaIndex == -1) vanillaIndex = i;
            if (list.get(i).isOf(moldyItem) && moldyIndex == -1) moldyIndex = i;
        }
        if (vanillaIndex == -1) {
            context.throwPositionedException("Vanilla item " + vanillaItem + " not found in " + groupKey.getValue(), BlockPos.ORIGIN);
        }
        if (moldyIndex == -1) {
            context.throwPositionedException("Moldy item " + moldyItem + " not found in " + groupKey.getValue(), BlockPos.ORIGIN);
        }
        if (moldyIndex <= vanillaIndex) {
            context.throwPositionedException("Moldy item " + moldyItem + " (index " + moldyIndex + ") is not after vanilla item " + vanillaItem + " (index " + vanillaIndex + ") in " + groupKey.getValue(), BlockPos.ORIGIN);
        }
    }
}
