package moldmod.test.gametest.wood;

import moldmod.SporesShadows;
import moldmod.SporesShadowsConstants;
import moldmod.block.MoldyBlock;
import moldmod.block.MoldyHangingSignBlock;
import moldmod.block.MoldySignBlock;
import moldmod.block.MoldyWallHangingSignBlock;
import moldmod.block.MoldyWallSignBlock;
import moldmod.block.entity.ModBlockEntities;
import moldmod.block.entity.MoldyHangingSignBlockEntity;
import moldmod.block.entity.MoldySignBlockEntity;
import moldmod.block.MoldyBlockHelper;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SignText;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class MoldySignGameTests {

    private ActionResult simulatePlayerUse(TestContext context, BlockPos pos, PlayerEntity player) {
        BlockHitResult hit = new BlockHitResult(context.getAbsolutePos(pos).toCenterPos(), Direction.UP, context.getAbsolutePos(pos), false);
        return UseBlockCallback.EVENT.invoker().interact(player, context.getWorld(), Hand.MAIN_HAND, hit);
    }

    // =========================================================================
    // === 1. REGISTRATION & BLOCK ENTITY SUPPORT TESTS                      ===
    // =========================================================================

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testSignBlocksAndBlockEntitiesRegisteredForAllWoods(TestContext context) {
        for (SporesShadowsConstants.MoldyWoodType wood : SporesShadowsConstants.WOOD_TYPES) {
            String prefix = wood.name();

            String[] standingSignIds = { "moldy_" + prefix + "_sign", "waxed_" + prefix + "_sign" };
            String[] wallSignIds = { "moldy_" + prefix + "_wall_sign", "waxed_" + prefix + "_wall_sign" };
            String[] hangingSignIds = { "moldy_" + prefix + "_hanging_sign", "waxed_" + prefix + "_hanging_sign" };
            String[] wallHangingSignIds = { "moldy_" + prefix + "_wall_hanging_sign", "waxed_" + prefix + "_wall_hanging_sign" };

            // Check standing signs
            for (String id : standingSignIds) {
                Block block = Registries.BLOCK.get(SporesShadows.id(id));
                if (block == Blocks.AIR || !(block instanceof MoldySignBlock)) {
                    context.throwPositionedException("Block " + id + " is not registered as MoldySignBlock", BlockPos.ORIGIN);
                }
                BlockState state = block.getDefaultState();
                if (!ModBlockEntities.MOLDY_SIGN.supports(state)) {
                    context.throwPositionedException("MOLDY_SIGN does not support " + id, BlockPos.ORIGIN);
                }
            }

            // Check wall signs
            for (String id : wallSignIds) {
                Block block = Registries.BLOCK.get(SporesShadows.id(id));
                if (block == Blocks.AIR || !(block instanceof MoldyWallSignBlock)) {
                    context.throwPositionedException("Block " + id + " is not registered as MoldyWallSignBlock", BlockPos.ORIGIN);
                }
                BlockState state = block.getDefaultState();
                if (!ModBlockEntities.MOLDY_SIGN.supports(state)) {
                    context.throwPositionedException("MOLDY_SIGN does not support " + id, BlockPos.ORIGIN);
                }
            }

            // Check hanging signs
            for (String id : hangingSignIds) {
                Block block = Registries.BLOCK.get(SporesShadows.id(id));
                if (block == Blocks.AIR || !(block instanceof MoldyHangingSignBlock)) {
                    context.throwPositionedException("Block " + id + " is not registered as MoldyHangingSignBlock", BlockPos.ORIGIN);
                }
                BlockState state = block.getDefaultState();
                if (!ModBlockEntities.MOLDY_HANGING_SIGN.supports(state)) {
                    context.throwPositionedException("MOLDY_HANGING_SIGN does not support " + id, BlockPos.ORIGIN);
                }
            }

            // Check wall hanging signs
            for (String id : wallHangingSignIds) {
                Block block = Registries.BLOCK.get(SporesShadows.id(id));
                if (block == Blocks.AIR || !(block instanceof MoldyWallHangingSignBlock)) {
                    context.throwPositionedException("Block " + id + " is not registered as MoldyWallHangingSignBlock", BlockPos.ORIGIN);
                }
                BlockState state = block.getDefaultState();
                if (!ModBlockEntities.MOLDY_HANGING_SIGN.supports(state)) {
                    context.throwPositionedException("MOLDY_HANGING_SIGN does not support " + id, BlockPos.ORIGIN);
                }
            }
        }
        context.complete();
    }

    // =========================================================================
    // === 2. SIGN TEXT PRESERVATION ACROSS STAGE PROGRESSION                ===
    // =========================================================================

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testSignTextPreservedAcrossStageProgression(TestContext context) {
        BlockPos pos = new BlockPos(1, 2, 1);
        Block moldySign = Registries.BLOCK.get(SporesShadows.id("moldy_oak_sign"));
        context.setBlockState(pos, moldySign.getDefaultState().with(MoldyBlock.STAGE, 0));

        BlockEntity be = context.getBlockEntity(pos);
        if (!(be instanceof MoldySignBlockEntity signBe)) {
            context.throwPositionedException("Expected MoldySignBlockEntity at " + pos, pos);
            return;
        }

        SignText front = new SignText()
                .withMessage(0, Text.literal("Front Line 1"))
                .withMessage(1, Text.literal("Front Line 2"));
        SignText back = new SignText()
                .withMessage(0, Text.literal("Back Line 1"))
                .withMessage(1, Text.literal("Back Line 2"));

        signBe.setText(front, true);
        signBe.setText(back, false);
        signBe.markDirty();

        // Progress through all stages 0 -> 1 -> 2 -> 3
        for (int stage = 1; stage <= 3; stage++) {
            BlockState currentState = context.getBlockState(pos);
            MoldyBlockHelper.setStage(context.getWorld(), context.getAbsolutePos(pos), currentState, stage);

            BlockEntity currentBe = context.getBlockEntity(pos);
            if (!(currentBe instanceof MoldySignBlockEntity updatedSignBe)) {
                context.throwPositionedException("BlockEntity lost at stage " + stage, pos);
                return;
            }

            if (updatedSignBe.getMoldStage() != stage) {
                context.throwPositionedException("Expected mold stage " + stage + " but got " + updatedSignBe.getMoldStage(), pos);
            }

            String f0 = updatedSignBe.getText(true).getMessage(0, false).getString();
            String f1 = updatedSignBe.getText(true).getMessage(1, false).getString();
            String b0 = updatedSignBe.getText(false).getMessage(0, false).getString();
            String b1 = updatedSignBe.getText(false).getMessage(1, false).getString();

            if (!"Front Line 1".equals(f0) || !"Front Line 2".equals(f1)) {
                context.throwPositionedException("Front text corrupted at stage " + stage + ": " + f0 + ", " + f1, pos);
            }
            if (!"Back Line 1".equals(b0) || !"Back Line 2".equals(b1)) {
                context.throwPositionedException("Back text corrupted at stage " + stage + ": " + b0 + ", " + b1, pos);
            }
        }

        context.complete();
    }

    // =========================================================================
    // === 3. SIGN TEXT PRESERVATION ACROSS WAXING & DE-WAXING               ===
    // =========================================================================

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testSignTextPreservedAcrossWaxingAndDewaxing(TestContext context) {
        BlockPos pos = new BlockPos(1, 2, 1);
        Block moldySign = Registries.BLOCK.get(SporesShadows.id("moldy_oak_sign"));
        Block waxedSign = Registries.BLOCK.get(SporesShadows.id("waxed_oak_sign"));

        context.setBlockState(pos, moldySign.getDefaultState().with(MoldyBlock.STAGE, 1).with(MoldyBlock.WAXED, false));

        BlockEntity be = context.getBlockEntity(pos);
        if (!(be instanceof MoldySignBlockEntity signBe)) {
            context.throwPositionedException("Expected MoldySignBlockEntity at " + pos, pos);
            return;
        }

        SignText front = new SignText().withMessage(0, Text.literal("Secret Area"));
        SignText back = new SignText().withMessage(0, Text.literal("Turn Around"));
        signBe.setText(front, true);
        signBe.setText(back, false);
        signBe.markDirty();

        PlayerEntity player = context.createMockPlayer(net.minecraft.world.GameMode.SURVIVAL);
        player.setSneaking(true);
        player.setPose(EntityPose.CROUCHING);

        // Step 1: Wax with Honeycomb
        player.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.HONEYCOMB, 2));
        ActionResult waxResult = simulatePlayerUse(context, pos, player);
        if (waxResult != ActionResult.SUCCESS) {
            context.throwPositionedException("Waxing sign failed with result " + waxResult, pos);
        }

        context.expectBlock(waxedSign, pos);

        BlockEntity waxedBe = context.getBlockEntity(pos);
        if (!(waxedBe instanceof MoldySignBlockEntity waxedSignBe)) {
            context.throwPositionedException("Block entity missing after waxing sign", pos);
            return;
        }

        if (!waxedSignBe.isMoldWaxed()) {
            context.throwPositionedException("Sign is not marked as mold waxed after waxing", pos);
        }

        if (!"Secret Area".equals(waxedSignBe.getText(true).getMessage(0, false).getString())) {
            context.throwPositionedException("Front text lost after waxing: " + waxedSignBe.getText(true).getMessage(0, false).getString(), pos);
        }
        if (!"Turn Around".equals(waxedSignBe.getText(false).getMessage(0, false).getString())) {
            context.throwPositionedException("Back text lost after waxing: " + waxedSignBe.getText(false).getMessage(0, false).getString(), pos);
        }

        // Step 2: Scrape wax with Axe
        player.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.DIAMOND_AXE));
        ActionResult dewaxResult = simulatePlayerUse(context, pos, player);
        if (dewaxResult != ActionResult.SUCCESS) {
            context.throwPositionedException("Dewaxing sign failed with result " + dewaxResult, pos);
        }

        context.expectBlock(moldySign, pos);

        BlockEntity dewaxedBe = context.getBlockEntity(pos);
        if (!(dewaxedBe instanceof MoldySignBlockEntity dewaxedSignBe)) {
            context.throwPositionedException("Block entity missing after dewaxing sign", pos);
            return;
        }

        if (dewaxedSignBe.isMoldWaxed()) {
            context.throwPositionedException("Sign is still marked as mold waxed after scraping wax", pos);
        }

        if (!"Secret Area".equals(dewaxedSignBe.getText(true).getMessage(0, false).getString())) {
            context.throwPositionedException("Front text lost after dewaxing: " + dewaxedSignBe.getText(true).getMessage(0, false).getString(), pos);
        }
        if (!"Turn Around".equals(dewaxedSignBe.getText(false).getMessage(0, false).getString())) {
            context.throwPositionedException("Back text lost after dewaxing: " + dewaxedSignBe.getText(false).getMessage(0, false).getString(), pos);
        }

        context.complete();
    }

    // =========================================================================
    // === 4. AXE MOLD SCRAPING ON SIGN PRESERVES TEXT                       ===
    // =========================================================================

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testSignAxeMoldScrapingPreservesText(TestContext context) {
        BlockPos pos = new BlockPos(1, 2, 1);
        Block moldySign = Registries.BLOCK.get(SporesShadows.id("moldy_birch_sign"));

        context.setBlockState(pos, moldySign.getDefaultState().with(MoldyBlock.STAGE, 2).with(MoldyBlock.WAXED, false));

        BlockEntity be = context.getBlockEntity(pos);
        if (!(be instanceof MoldySignBlockEntity signBe)) {
            context.throwPositionedException("Expected MoldySignBlockEntity at " + pos, pos);
            return;
        }

        SignText front = new SignText().withMessage(0, Text.literal("Stage 2 Text"));
        signBe.setText(front, true);
        signBe.markDirty();

        PlayerEntity player = context.createMockPlayer(net.minecraft.world.GameMode.SURVIVAL);
        player.setSneaking(true);
        player.setPose(EntityPose.CROUCHING);
        player.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.IRON_AXE));

        // Scrape 2 -> 1
        ActionResult scrape1 = simulatePlayerUse(context, pos, player);
        if (scrape1 != ActionResult.SUCCESS) {
            context.throwPositionedException("Scraping stage 2 failed", pos);
        }
        BlockState state1 = context.getBlockState(pos);
        if (state1.get(MoldyBlock.STAGE) != 1) {
            context.throwPositionedException("Expected stage 1 after scrape, got " + state1.get(MoldyBlock.STAGE), pos);
        }
        MoldySignBlockEntity be1 = (MoldySignBlockEntity) context.getBlockEntity(pos);
        if (!"Stage 2 Text".equals(be1.getText(true).getMessage(0, false).getString())) {
            context.throwPositionedException("Text lost after scraping stage 2->1", pos);
        }

        // Scrape 1 -> 0
        ActionResult scrape2 = simulatePlayerUse(context, pos, player);
        if (scrape2 != ActionResult.SUCCESS) {
            context.throwPositionedException("Scraping stage 1 failed", pos);
        }
        BlockState state2 = context.getBlockState(pos);
        if (state2.get(MoldyBlock.STAGE) != 0) {
            context.throwPositionedException("Expected stage 0 after scrape, got " + state2.get(MoldyBlock.STAGE), pos);
        }
        MoldySignBlockEntity be2 = (MoldySignBlockEntity) context.getBlockEntity(pos);
        if (!"Stage 2 Text".equals(be2.getText(true).getMessage(0, false).getString())) {
            context.throwPositionedException("Text lost after scraping stage 1->0", pos);
        }

        context.complete();
    }

    // =========================================================================
    // === 5. HANGING SIGN TEXT PRESERVATION ACROSS WAXING & SCRAPING        ===
    // =========================================================================

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testHangingSignTextPreservedAcrossWaxingAndScraping(TestContext context) {
        BlockPos pos = new BlockPos(1, 2, 1);
        Block moldyHanging = Registries.BLOCK.get(SporesShadows.id("moldy_bamboo_hanging_sign"));
        Block waxedHanging = Registries.BLOCK.get(SporesShadows.id("waxed_bamboo_hanging_sign"));

        context.setBlockState(pos, moldyHanging.getDefaultState().with(MoldyBlock.STAGE, 1).with(MoldyBlock.WAXED, false));

        BlockEntity be = context.getBlockEntity(pos);
        if (!(be instanceof MoldyHangingSignBlockEntity hangingBe)) {
            context.throwPositionedException("Expected MoldyHangingSignBlockEntity at " + pos, pos);
            return;
        }

        SignText front = new SignText().withMessage(0, Text.literal("Hanging Bamboo"));
        SignText back = new SignText().withMessage(0, Text.literal("Hanging Back"));
        hangingBe.setText(front, true);
        hangingBe.setText(back, false);
        hangingBe.markDirty();

        PlayerEntity player = context.createMockPlayer(net.minecraft.world.GameMode.SURVIVAL);
        player.setSneaking(true);
        player.setPose(EntityPose.CROUCHING);

        // Wax
        player.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.HONEYCOMB));
        ActionResult waxResult = simulatePlayerUse(context, pos, player);
        if (waxResult != ActionResult.SUCCESS) {
            context.throwPositionedException("Waxing hanging sign failed", pos);
        }

        context.expectBlock(waxedHanging, pos);
        MoldyHangingSignBlockEntity waxedBe = (MoldyHangingSignBlockEntity) context.getBlockEntity(pos);
        if (!"Hanging Bamboo".equals(waxedBe.getText(true).getMessage(0, false).getString())) {
            context.throwPositionedException("Hanging front text lost after waxing", pos);
        }
        if (!"Hanging Back".equals(waxedBe.getText(false).getMessage(0, false).getString())) {
            context.throwPositionedException("Hanging back text lost after waxing", pos);
        }

        // De-wax with Axe
        player.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.NETHERITE_AXE));
        ActionResult dewaxResult = simulatePlayerUse(context, pos, player);
        if (dewaxResult != ActionResult.SUCCESS) {
            context.throwPositionedException("Dewaxing hanging sign failed", pos);
        }

        context.expectBlock(moldyHanging, pos);
        MoldyHangingSignBlockEntity dewaxedBe = (MoldyHangingSignBlockEntity) context.getBlockEntity(pos);
        if (!"Hanging Bamboo".equals(dewaxedBe.getText(true).getMessage(0, false).getString())) {
            context.throwPositionedException("Hanging front text lost after dewaxing", pos);
        }
        if (!"Hanging Back".equals(dewaxedBe.getText(false).getMessage(0, false).getString())) {
            context.throwPositionedException("Hanging back text lost after dewaxing", pos);
        }

        context.complete();
    }

    // =========================================================================
    // === 6. NBT SERIALIZATION & DESERIALIZATION TEST                       ===
    // =========================================================================

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testSignNbtSerialization(TestContext context) {
        BlockPos pos = new BlockPos(1, 2, 1);
        Block moldySign = Registries.BLOCK.get(SporesShadows.id("moldy_spruce_sign"));
        context.setBlockState(pos, moldySign.getDefaultState().with(MoldyBlock.STAGE, 2).with(MoldyBlock.WAXED, true));

        MoldySignBlockEntity originalBe = (MoldySignBlockEntity) context.getBlockEntity(pos);
        SignText front = new SignText().withMessage(0, Text.literal("Serialized Front"));
        SignText back = new SignText().withMessage(0, Text.literal("Serialized Back"));
        originalBe.setText(front, true);
        originalBe.setText(back, false);
        originalBe.setMoldStage(2);
        originalBe.setMoldWaxed(true);

        // Serialize to NBT
        NbtCompound nbt = originalBe.createNbtWithId(context.getWorld().getRegistryManager());

        if (!nbt.contains("MoldStage") || nbt.getInt("MoldStage") != 2) {
            context.throwPositionedException("NBT missing MoldStage or value != 2", pos);
        }
        if (!nbt.contains("MoldWaxed") || !nbt.getBoolean("MoldWaxed")) {
            context.throwPositionedException("NBT missing MoldWaxed or value != true", pos);
        }

        // Deserialize into new block entity
        MoldySignBlockEntity restoredBe = new MoldySignBlockEntity(pos, moldySign.getDefaultState());
        restoredBe.read(nbt, context.getWorld().getRegistryManager());

        if (restoredBe.getMoldStage() != 2) {
            context.throwPositionedException("Restored MoldStage != 2, got " + restoredBe.getMoldStage(), pos);
        }
        if (!restoredBe.isMoldWaxed()) {
            context.throwPositionedException("Restored MoldWaxed != true", pos);
        }
        if (!"Serialized Front".equals(restoredBe.getText(true).getMessage(0, false).getString())) {
            context.throwPositionedException("Restored front text incorrect: " + restoredBe.getText(true).getMessage(0, false).getString(), pos);
        }
        if (!"Serialized Back".equals(restoredBe.getText(false).getMessage(0, false).getString())) {
            context.throwPositionedException("Restored back text incorrect: " + restoredBe.getText(false).getMessage(0, false).getString(), pos);
        }

        context.complete();
    }
}
