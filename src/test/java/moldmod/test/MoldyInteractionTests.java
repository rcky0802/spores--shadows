package moldmod.test;

import moldmod.SporesShadows;
import moldmod.block.MoldyLogBlock;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class MoldyInteractionTests {

    private ActionResult simulatePlayerUse(TestContext context, BlockPos pos, PlayerEntity player) {
        BlockHitResult hit = new BlockHitResult(context.getAbsolutePos(pos).toCenterPos(), Direction.UP, context.getAbsolutePos(pos), false);
        return UseBlockCallback.EVENT.invoker().interact(player, context.getWorld(), Hand.MAIN_HAND, hit);
    }

    private void setupBlockInWorld(TestContext context, BlockPos pos, Block block, int stage, boolean isWaxed) {
        BlockState state = block.getDefaultState()
                .with(MoldyLogBlock.STAGE, stage)
                .with(MoldyLogBlock.WAXED, isWaxed);

        if (block instanceof DoorBlock) {
            context.setBlockState(pos, state.with(DoorBlock.HALF, DoubleBlockHalf.LOWER));
            context.setBlockState(pos.up(), state.with(DoorBlock.HALF, DoubleBlockHalf.UPPER));
        } else {
            context.setBlockState(pos, state);
            context.setBlockState(pos.up(), Blocks.AIR.getDefaultState());
        }
    }

    // =========================================================================
    // === 1. EXHAUSTIVE WAXING TESTS (Shift + Right Click with Honeycomb)   ===
    // =========================================================================

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testWaxingAcrossAllProductsAndStages(TestContext context) {
        BlockPos pos = new BlockPos(1, 2, 1);

        PlayerEntity player = context.createMockPlayer(net.minecraft.world.GameMode.SURVIVAL);
        player.setSneaking(true);
        player.setPose(EntityPose.CROUCHING);

        for (var product : MoldyWoodTestHelper.getAllWoodProducts()) {
            String baseName = product.baseName();

            Block moldyBlock = Registries.BLOCK.get(SporesShadows.id("moldy_" + baseName));
            Block waxedBlock = Registries.BLOCK.get(SporesShadows.id("waxed_" + baseName));

            if (moldyBlock == Blocks.AIR || waxedBlock == Blocks.AIR) {
                context.throwPositionedException("Block for " + baseName + " is AIR!", pos);
            }

            for (int stage = 0; stage <= 3; stage++) {
                setupBlockInWorld(context, pos, moldyBlock, stage, false);

                ItemStack honeycomb = new ItemStack(Items.HONEYCOMB, 5);
                player.setStackInHand(Hand.MAIN_HAND, honeycomb);

                ActionResult result = simulatePlayerUse(context, pos, player);
                if (result != ActionResult.SUCCESS) {
                    context.throwPositionedException("Waxing interaction was not SUCCESS for " + baseName + " at stage " + stage, pos);
                }

                // Verify block became waxed
                BlockState resultState = context.getBlockState(pos);
                if (!resultState.contains(MoldyLogBlock.WAXED) || !resultState.get(MoldyLogBlock.WAXED)) {
                    context.throwPositionedException("Waxing failed for " + baseName + " at stage " + stage + "! WAXED is false", pos);
                }

                // Verify stage was preserved
                if (resultState.get(MoldyLogBlock.STAGE) != stage) {
                    context.throwPositionedException("Waxing altered stage for " + baseName + "! Expected stage " + stage + ", got " + resultState.get(MoldyLogBlock.STAGE), pos);
                }

                // Verify honeycomb was consumed
                if (honeycomb.getCount() != 4) {
                    context.throwPositionedException("Honeycomb was not consumed for " + baseName + " stage " + stage, pos);
                }
            }
        }

        context.complete();
    }

    // =========================================================================
    // === 2. EXHAUSTIVE 2-CLICK DE-WAX & DE-MOLD TESTS (Shift + Right Click)===
    // =========================================================================

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testTwoClickDeWaxAndDeMoldAcrossAllProductsAndStages(TestContext context) {
        BlockPos pos = new BlockPos(1, 2, 1);

        PlayerEntity player = context.createMockPlayer(net.minecraft.world.GameMode.SURVIVAL);
        player.setSneaking(true);
        player.setPose(EntityPose.CROUCHING);

        for (var product : MoldyWoodTestHelper.getAllWoodProducts()) {
            String baseName = product.baseName();

            Block moldyBlock = Registries.BLOCK.get(SporesShadows.id("moldy_" + baseName));
            Block waxedBlock = Registries.BLOCK.get(SporesShadows.id("waxed_" + baseName));

            if (moldyBlock == Blocks.AIR || waxedBlock == Blocks.AIR) {
                context.throwPositionedException("Block for " + baseName + " is AIR!", pos);
            }

            for (int stage = 0; stage <= 3; stage++) {
                // Start with WAXED block at given stage
                setupBlockInWorld(context, pos, waxedBlock, stage, true);

                ItemStack axe = new ItemStack(Items.IRON_AXE);
                player.setStackInHand(Hand.MAIN_HAND, axe);

                // --- CLICK 1: Remove Wax ---
                ActionResult res1 = simulatePlayerUse(context, pos, player);
                if (res1 != ActionResult.SUCCESS) {
                    context.throwPositionedException("Click 1 (de-wax) interaction was not SUCCESS for " + baseName + " at stage " + stage, pos);
                }

                BlockState click1State = context.getBlockState(pos);
                if (click1State.get(MoldyLogBlock.WAXED)) {
                    context.throwPositionedException("Click 1 did NOT remove wax for " + baseName + " at stage " + stage + "!", pos);
                }
                if (click1State.get(MoldyLogBlock.STAGE) != stage) {
                    context.throwPositionedException("Click 1 (de-wax) should NOT change stage for " + baseName + "! Expected " + stage + " got " + click1State.get(MoldyLogBlock.STAGE), pos);
                }
                if (axe.getDamage() != 1) {
                    context.throwPositionedException("Click 1 did not damage axe for " + baseName + " at stage " + stage + "!", pos);
                }

                // --- CLICK 2: Scrape Mold ---
                ActionResult res2 = simulatePlayerUse(context, pos, player);

                BlockState click2State = context.getBlockState(pos);
                if (stage == 1) {
                    if (res2 != ActionResult.SUCCESS) context.throwPositionedException("Click 2 on stage 1 should be SUCCESS for " + baseName, pos);
                    if (click2State.get(MoldyLogBlock.STAGE) != 0) context.throwPositionedException("Click 2 should cure stage 1 to 0 for " + baseName, pos);
                    if (axe.getDamage() != 2) context.throwPositionedException("Click 2 should consume axe durability for " + baseName, pos);
                } else if (stage == 2) {
                    if (res2 != ActionResult.SUCCESS) context.throwPositionedException("Click 2 on stage 2 should be SUCCESS for " + baseName, pos);
                    if (click2State.get(MoldyLogBlock.STAGE) != 1) context.throwPositionedException("Click 2 should reduce stage 2 to 1 for " + baseName, pos);
                    if (axe.getDamage() != 2) context.throwPositionedException("Click 2 should consume axe durability for " + baseName, pos);
                } else if (stage == 3) {
                    if (res2 != ActionResult.SUCCESS) context.throwPositionedException("Click 2 on stage 3 should be SUCCESS for " + baseName, pos);
                    if (click2State.get(MoldyLogBlock.STAGE) != 3) context.throwPositionedException("Click 2 on stage 3 should not change stage (incurable) for " + baseName, pos);
                    if (axe.getDamage() != 1) context.throwPositionedException("Click 2 on stage 3 should not damage axe for " + baseName, pos);
                } else if (stage == 0) {
                    // Stage 0 is already clean
                    if (res2 != ActionResult.PASS) context.throwPositionedException("Click 2 on stage 0 should PASS for " + baseName, pos);
                    if (click2State.get(MoldyLogBlock.STAGE) != 0) context.throwPositionedException("Click 2 on stage 0 should remain stage 0 for " + baseName, pos);
                    if (axe.getDamage() != 1) context.throwPositionedException("Click 2 on clean stage 0 should not damage axe for " + baseName, pos);
                }
            }
        }

        context.complete();
    }
}
