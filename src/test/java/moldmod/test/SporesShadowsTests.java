package moldmod.test;

import moldmod.block.ModBlocks;
import moldmod.block.MoldyBlockHelper;
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
import net.minecraft.util.math.Direction;

public class SporesShadowsTests {

    // ============================================
    // === EXHAUSTIVE INTERACTION TESTS (SNEAK) ===
    // ============================================

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testWaxingConsumesHoneycombAndSetsWaxed(TestContext context) {
        BlockPos pos = new BlockPos(0, 2, 0);
        BlockState cleanLog = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState()
                .with(MoldyLogBlock.STAGE, 0).with(MoldyLogBlock.WAXED, false);
        context.setBlockState(pos, cleanLog);

        net.minecraft.entity.player.PlayerEntity player = context.createMockPlayer(net.minecraft.world.GameMode.SURVIVAL);
        player.setSneaking(true);
        player.setPose(net.minecraft.entity.EntityPose.CROUCHING);
        
        ItemStack honeycomb = new ItemStack(Items.HONEYCOMB, 5);
        player.setStackInHand(Hand.MAIN_HAND, honeycomb);
        
        simulatePlayerUse(context, pos, player);

        context.expectBlockProperty(pos, MoldyLogBlock.WAXED, true);
        
        if (honeycomb.getCount() != 4) {
            context.throwPositionedException("Honeycomb was not consumed! Expected 4, got " + honeycomb.getCount(), pos);
        }
        
        context.complete();
    }
    
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testWaxingFailsIfNotSneaking(TestContext context) {
        BlockPos pos = new BlockPos(0, 2, 0);
        BlockState cleanLog = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState()
                .with(MoldyLogBlock.STAGE, 0).with(MoldyLogBlock.WAXED, false);
        context.setBlockState(pos, cleanLog);

        net.minecraft.entity.player.PlayerEntity player = context.createMockPlayer(net.minecraft.world.GameMode.SURVIVAL);
        player.setSneaking(false); // NO SNEAK
        
        ItemStack honeycomb = new ItemStack(Items.HONEYCOMB, 5);
        player.setStackInHand(Hand.MAIN_HAND, honeycomb);
        
        simulatePlayerUse(context, pos, player);

        context.expectBlockProperty(pos, MoldyLogBlock.WAXED, false);
        
        if (honeycomb.getCount() != 5) {
            context.throwPositionedException("Honeycomb was consumed but player wasn't sneaking!", pos);
        }
        
        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testScrapingWaxTakesPriorityAndConsumesDurability(TestContext context) {
        BlockPos pos = new BlockPos(0, 2, 0);
        // Stage 1 AND Waxed -> Should only remove wax!
        BlockState waxedTaintedLog = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState()
                .with(MoldyLogBlock.STAGE, 1).with(MoldyLogBlock.WAXED, true);
        context.setBlockState(pos, waxedTaintedLog);

        net.minecraft.entity.player.PlayerEntity player = context.createMockPlayer(net.minecraft.world.GameMode.SURVIVAL);
        player.setSneaking(true);
        player.setPose(net.minecraft.entity.EntityPose.CROUCHING);
        
        ItemStack axe = new ItemStack(Items.IRON_AXE);
        player.setStackInHand(Hand.MAIN_HAND, axe);
        
        simulatePlayerUse(context, pos, player);

        context.expectBlockProperty(pos, MoldyLogBlock.WAXED, false);
        context.expectBlockProperty(pos, MoldyLogBlock.STAGE, 1); // Stage MUST NOT change
        
        if (axe.getDamage() != 1) {
            context.throwPositionedException("Axe durability was not consumed for removing wax!", pos);
        }
        
        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testScrapingMoldLevel2To1(TestContext context) {
        BlockPos pos = new BlockPos(0, 2, 0);
        BlockState moldyLog = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState()
                .with(MoldyLogBlock.STAGE, 2).with(MoldyLogBlock.WAXED, false);
        context.setBlockState(pos, moldyLog);

        net.minecraft.entity.player.PlayerEntity player = context.createMockPlayer(net.minecraft.world.GameMode.SURVIVAL);
        player.setSneaking(true);
        player.setPose(net.minecraft.entity.EntityPose.CROUCHING);
        
        ItemStack axe = new ItemStack(Items.IRON_AXE);
        player.setStackInHand(Hand.MAIN_HAND, axe);
        
        simulatePlayerUse(context, pos, player);

        context.expectBlockProperty(pos, MoldyLogBlock.STAGE, 1);
        
        if (axe.getDamage() != 1) {
            context.throwPositionedException("Axe durability was not consumed for removing mold!", pos);
        }
        
        context.complete();
    }
    
    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testScrapingMoldLevel1To0(TestContext context) {
        BlockPos pos = new BlockPos(0, 2, 0);
        BlockState taintedLog = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState()
                .with(MoldyLogBlock.STAGE, 1).with(MoldyLogBlock.WAXED, false);
        context.setBlockState(pos, taintedLog);

        net.minecraft.entity.player.PlayerEntity player = context.createMockPlayer(net.minecraft.world.GameMode.SURVIVAL);
        player.setSneaking(true);
        player.setPose(net.minecraft.entity.EntityPose.CROUCHING);
        
        ItemStack axe = new ItemStack(Items.IRON_AXE);
        player.setStackInHand(Hand.MAIN_HAND, axe);
        
        simulatePlayerUse(context, pos, player);

        context.expectBlockProperty(pos, MoldyLogBlock.STAGE, 0);
        
        if (axe.getDamage() != 1) {
            context.throwPositionedException("Axe durability was not consumed for removing mold!", pos);
        }
        
        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testScrapingMoldLevel3HasNoEffect(TestContext context) {
        BlockPos pos = new BlockPos(0, 2, 0);
        BlockState rottenLog = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState()
                .with(MoldyLogBlock.STAGE, 3).with(MoldyLogBlock.WAXED, false);
        context.setBlockState(pos, rottenLog);

        net.minecraft.entity.player.PlayerEntity player = context.createMockPlayer(net.minecraft.world.GameMode.SURVIVAL);
        player.setSneaking(true);
        player.setPose(net.minecraft.entity.EntityPose.CROUCHING);
        
        ItemStack axe = new ItemStack(Items.IRON_AXE);
        player.setStackInHand(Hand.MAIN_HAND, axe);
        
        simulatePlayerUse(context, pos, player);

        // State should still be 3
        context.expectBlockProperty(pos, MoldyLogBlock.STAGE, 3);
        
        if (axe.getDamage() != 0) {
            context.throwPositionedException("Axe durability should NOT be consumed on Level 3!", pos);
        }
        
        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testWaxingDoorBottomHalfSyncsTopHalf(TestContext context) {
        BlockPos bottomPos = new BlockPos(0, 2, 0);
        BlockPos topPos = new BlockPos(0, 3, 0);
        
        net.minecraft.block.Block doorBlock = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_DOOR);
        
        BlockState bottomState = doorBlock.getDefaultState()
                .with(net.minecraft.block.DoorBlock.HALF, net.minecraft.block.enums.DoubleBlockHalf.LOWER)
                .with(MoldyLogBlock.STAGE, 1).with(MoldyLogBlock.WAXED, false);
                
        BlockState topState = doorBlock.getDefaultState()
                .with(net.minecraft.block.DoorBlock.HALF, net.minecraft.block.enums.DoubleBlockHalf.UPPER)
                .with(MoldyLogBlock.STAGE, 1).with(MoldyLogBlock.WAXED, false);
                
        context.setBlockState(bottomPos, bottomState);
        context.setBlockState(topPos, topState);

        net.minecraft.entity.player.PlayerEntity player = context.createMockPlayer(net.minecraft.world.GameMode.SURVIVAL);
        player.setSneaking(true);
        player.setPose(net.minecraft.entity.EntityPose.CROUCHING);
        
        ItemStack honeycomb = new ItemStack(Items.HONEYCOMB, 5);
        player.setStackInHand(Hand.MAIN_HAND, honeycomb);
        
        simulatePlayerUse(context, bottomPos, player);

        // Bottom should be waxed
        context.expectBlockProperty(bottomPos, MoldyLogBlock.WAXED, true);
        // Top should be auto-synced to waxed
        context.expectBlockProperty(topPos, MoldyLogBlock.WAXED, true);
        
        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testScrapingDoorTopHalfSyncsBottomHalf(TestContext context) {
        BlockPos bottomPos = new BlockPos(0, 2, 0);
        BlockPos topPos = new BlockPos(0, 3, 0);
        
        net.minecraft.block.Block doorBlock = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_DOOR);
        
        // Stage 1, Waxed
        BlockState bottomState = doorBlock.getDefaultState()
                .with(net.minecraft.block.DoorBlock.HALF, net.minecraft.block.enums.DoubleBlockHalf.LOWER)
                .with(MoldyLogBlock.STAGE, 1).with(MoldyLogBlock.WAXED, true);
                
        BlockState topState = doorBlock.getDefaultState()
                .with(net.minecraft.block.DoorBlock.HALF, net.minecraft.block.enums.DoubleBlockHalf.UPPER)
                .with(MoldyLogBlock.STAGE, 1).with(MoldyLogBlock.WAXED, true);
                
        context.setBlockState(bottomPos, bottomState);
        context.setBlockState(topPos, topState);

        net.minecraft.entity.player.PlayerEntity player = context.createMockPlayer(net.minecraft.world.GameMode.SURVIVAL);
        player.setSneaking(true);
        player.setPose(net.minecraft.entity.EntityPose.CROUCHING);
        
        ItemStack axe = new ItemStack(Items.IRON_AXE);
        player.setStackInHand(Hand.MAIN_HAND, axe);
        
        // Use axe on TOP half
        simulatePlayerUse(context, topPos, player);

        // Top should have wax removed
        context.expectBlockProperty(topPos, MoldyLogBlock.WAXED, false);
        // Bottom should auto-sync and have wax removed
        context.expectBlockProperty(bottomPos, MoldyLogBlock.WAXED, false);
        
        context.complete();
    }

    // ============================================
    // === OTHER TESTS ===
    // ============================================

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testStripping(TestContext context) {
        BlockPos pos = new BlockPos(0, 2, 0);
        // Stage 0 (clean) when scraped becomes stripped (vanilla logic passes through)
        BlockState cleanLog = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState().with(MoldyLogBlock.STAGE, 0);
        context.setBlockState(pos, cleanLog);

        net.minecraft.entity.player.PlayerEntity player = context.createMockPlayer(net.minecraft.world.GameMode.SURVIVAL);
        ItemStack axe = new ItemStack(Items.IRON_AXE);
        player.setStackInHand(Hand.MAIN_HAND, axe);
        
        simulatePlayerUse(context, pos, player); // Without sneaking, it just strips it

        context.expectBlock(ModBlocks.VANILLA_TO_MOLDY.get(Blocks.STRIPPED_OAK_LOG), pos);
        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testHumidityCalculation(TestContext context) {
        BlockPos center = new BlockPos(2, 2, 2);
        BlockState cleanLog = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState();
        context.setBlockState(center, cleanLog);

        // Put water nearby
        context.setBlockState(center.add(1, 0, 0), Blocks.WATER.getDefaultState());

        double localBonus = MoldyBlockHelper.calculateDetailedR(context.getWorld(), context.getAbsolutePos(center), false, cleanLog).localHumidityBonus();
        
        // localBonus should be > 0.0 because of the water nearby
        if (localBonus <= 0.0) {
            context.throwPositionedException("Humidity bonus was not increased by water!", center);
        }
        
        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testDropRottenNoSilkTouch(TestContext context) {
        BlockState rottenLog = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState().with(MoldyLogBlock.STAGE, 3);
        
        net.minecraft.entity.player.PlayerEntity player = context.createMockPlayer(net.minecraft.world.GameMode.SURVIVAL);
        java.util.List<ItemStack> drops = net.minecraft.block.Block.getDroppedStacks(rottenLog, context.getWorld(), context.getAbsolutePos(new BlockPos(0, 0, 0)), null, player, player.getMainHandStack());
        
        if (!drops.isEmpty()) {
            context.throwPositionedException("Rotten log should drop nothing without Silk Touch! Dropped: " + drops.get(0).getItem(), new BlockPos(0, 0, 0));
        }
        
        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testDropRottenWithSilkTouch(TestContext context) {
        BlockState rottenLog = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState().with(MoldyLogBlock.STAGE, 3);
        
        net.minecraft.entity.player.PlayerEntity player = context.createMockPlayer(net.minecraft.world.GameMode.SURVIVAL);
        ItemStack silkTouchPick = new ItemStack(Items.DIAMOND_PICKAXE);
        silkTouchPick.addEnchantment(context.getWorld().getRegistryManager().get(net.minecraft.registry.RegistryKeys.ENCHANTMENT).getEntry(net.minecraft.enchantment.Enchantments.SILK_TOUCH).get(), 1);
        
        java.util.List<ItemStack> drops = net.minecraft.block.Block.getDroppedStacks(rottenLog, context.getWorld(), context.getAbsolutePos(new BlockPos(0, 0, 0)), null, player, silkTouchPick);
        
        if (drops.isEmpty() || !drops.get(0).getItem().equals(net.minecraft.registry.Registries.ITEM.get(moldmod.SporesShadows.id("rotten_oak_log")))) {
            context.throwPositionedException("Rotten log should drop 'rotten_oak_log' with Silk Touch!", new BlockPos(0, 0, 0));
        }
        
        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testDropTainted(TestContext context) {
        BlockState taintedLog = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState().with(MoldyLogBlock.STAGE, 1);
        
        net.minecraft.entity.player.PlayerEntity player = context.createMockPlayer(net.minecraft.world.GameMode.SURVIVAL);
        java.util.List<ItemStack> drops = net.minecraft.block.Block.getDroppedStacks(taintedLog, context.getWorld(), context.getAbsolutePos(new BlockPos(0, 0, 0)), null, player, player.getMainHandStack());
        
        if (drops.isEmpty() || !drops.get(0).getItem().equals(net.minecraft.registry.Registries.ITEM.get(moldmod.SporesShadows.id("tainted_oak_log")))) {
            context.throwPositionedException("Tainted log should drop 'tainted_oak_log' even without Silk Touch!", new BlockPos(0, 0, 0));
        }
        
        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testPlacementConversion(TestContext context) {
        net.minecraft.entity.player.PlayerEntity player = context.createMockPlayer(net.minecraft.world.GameMode.SURVIVAL);
        ItemStack oakLogItem = new ItemStack(Items.OAK_LOG);
        
        // Place on the EAST face of the block below, which should result in X axis for PillarBlock
        net.minecraft.util.hit.BlockHitResult hit = new net.minecraft.util.hit.BlockHitResult(
            context.getAbsolutePos(new BlockPos(0, 1, 0)).toCenterPos(), 
            Direction.EAST, 
            context.getAbsolutePos(new BlockPos(0, 1, 0)), 
            false
        );
        
        net.minecraft.item.ItemPlacementContext placementContext = new net.minecraft.item.ItemPlacementContext(player, Hand.MAIN_HAND, oakLogItem, hit);
        
        net.minecraft.util.ActionResult result = ((net.minecraft.item.BlockItem)oakLogItem.getItem()).place(placementContext);
        
        if (!result.isAccepted()) {
            context.throwPositionedException("Placing Vanilla Oak Log failed!", new BlockPos(0, 1, 0));
        }
        
        BlockState placedState = context.getBlockState(new BlockPos(0, 1, 0));
        if (!placedState.isOf(ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG))) {
            context.throwPositionedException("Did not convert to Moldy Log!", new BlockPos(0, 1, 0));
        }
        if (placedState.contains(net.minecraft.state.property.Properties.AXIS) && placedState.get(net.minecraft.state.property.Properties.AXIS) != Direction.Axis.X) {
            context.throwPositionedException("Axis was not preserved! Expected X, got " + placedState.get(net.minecraft.state.property.Properties.AXIS), new BlockPos(0, 1, 0));
        }
        
        context.complete();
    }

    private void simulatePlayerUse(TestContext context, BlockPos pos, net.minecraft.entity.player.PlayerEntity player) {
        net.minecraft.util.hit.BlockHitResult hit = new net.minecraft.util.hit.BlockHitResult(context.getAbsolutePos(pos).toCenterPos(), Direction.UP, context.getAbsolutePos(pos), false);
        net.minecraft.util.ActionResult result = net.fabricmc.fabric.api.event.player.UseBlockCallback.EVENT.invoker().interact(player, context.getWorld(), Hand.MAIN_HAND, hit);
        if (result == net.minecraft.util.ActionResult.PASS) {
            context.useBlock(pos, player);
        }
    }

    // Old tests moved to MoldyCraftingAndFuelTests.java
}
