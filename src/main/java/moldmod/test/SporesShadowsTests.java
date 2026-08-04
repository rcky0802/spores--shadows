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
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class SporesShadowsTests {

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testWaxing(TestContext context) {
        BlockPos pos = new BlockPos(0, 2, 0);
        BlockState moldyLog = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState().with(MoldyLogBlock.STAGE, 0);
        context.setBlockState(pos, moldyLog);

        net.minecraft.entity.player.PlayerEntity player = context.createMockPlayer(net.minecraft.world.GameMode.SURVIVAL);
        player.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.HONEYCOMB));
        context.useBlock(pos, player);

        context.expectBlockProperty(pos, MoldyLogBlock.WAXED, true);
        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testScrapingWax(TestContext context) {
        BlockPos pos = new BlockPos(0, 2, 0);
        BlockState waxedLog = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState().with(MoldyLogBlock.STAGE, 0).with(MoldyLogBlock.WAXED, true);
        context.setBlockState(pos, waxedLog);

        net.minecraft.entity.player.PlayerEntity player = context.createMockPlayer(net.minecraft.world.GameMode.SURVIVAL);
        player.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.IRON_AXE));
        context.useBlock(pos, player);

        context.expectBlockProperty(pos, MoldyLogBlock.WAXED, false);
        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testScrapingMold(TestContext context) {
        BlockPos pos = new BlockPos(0, 2, 0);
        // Stage 1 is Tainted, can be scraped down to Stage 0
        BlockState taintedLog = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState().with(MoldyLogBlock.STAGE, 1);
        context.setBlockState(pos, taintedLog);

        net.minecraft.entity.player.PlayerEntity player = context.createMockPlayer(net.minecraft.world.GameMode.SURVIVAL);
        player.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.IRON_AXE));
        context.useBlock(pos, player);

        context.expectBlockProperty(pos, MoldyLogBlock.STAGE, 0);
        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testStripping(TestContext context) {
        BlockPos pos = new BlockPos(0, 2, 0);
        // Stage 0 (clean) when scraped becomes stripped
        BlockState cleanLog = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState().with(MoldyLogBlock.STAGE, 0);
        context.setBlockState(pos, cleanLog);

        net.minecraft.entity.player.PlayerEntity player = context.createMockPlayer(net.minecraft.world.GameMode.SURVIVAL);
        player.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.IRON_AXE));
        context.useBlock(pos, player);

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

        double r = MoldyBlockHelper.calculateR(context.getWorld(), context.getAbsolutePos(center), false, cleanLog);
        
        // R should be > 0.0 because of the water nearby
        if (r <= 0.0) {
            context.throwPositionedException("R value was not increased by water!", center);
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

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testCraftingYieldTainted(TestContext context) {
        net.minecraft.recipe.RecipeManager recipeManager = context.getWorld().getServer().getRecipeManager();
        net.minecraft.recipe.input.CraftingRecipeInput input = net.minecraft.recipe.input.CraftingRecipeInput.create(1, 1, java.util.List.of(new ItemStack(net.minecraft.registry.Registries.ITEM.get(moldmod.SporesShadows.id("tainted_oak_log")))));

        java.util.Optional<net.minecraft.recipe.RecipeEntry<net.minecraft.recipe.CraftingRecipe>> recipe = recipeManager.getFirstMatch(net.minecraft.recipe.RecipeType.CRAFTING, input, context.getWorld());
        
        if (recipe.isEmpty() || recipe.get().value().getResult(context.getWorld().getRegistryManager()).getCount() != 2 || !recipe.get().value().getResult(context.getWorld().getRegistryManager()).getItem().equals(Items.OAK_PLANKS)) {
            context.throwPositionedException("Tainted Oak Log should craft into exactly 2 Vanilla Oak Planks!", new BlockPos(0,0,0));
        }
        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testCraftingYieldMoldy(TestContext context) {
        net.minecraft.recipe.RecipeManager recipeManager = context.getWorld().getServer().getRecipeManager();
        net.minecraft.recipe.input.CraftingRecipeInput input = net.minecraft.recipe.input.CraftingRecipeInput.create(1, 1, java.util.List.of(new ItemStack(net.minecraft.registry.Registries.ITEM.get(moldmod.SporesShadows.id("moldy_oak_log")))));

        java.util.Optional<net.minecraft.recipe.RecipeEntry<net.minecraft.recipe.CraftingRecipe>> recipe = recipeManager.getFirstMatch(net.minecraft.recipe.RecipeType.CRAFTING, input, context.getWorld());
        
        if (recipe.isEmpty() || recipe.get().value().getResult(context.getWorld().getRegistryManager()).getCount() != 1 || !recipe.get().value().getResult(context.getWorld().getRegistryManager()).getItem().equals(Items.OAK_PLANKS)) {
            context.throwPositionedException("Moldy Oak Log should craft into exactly 1 Vanilla Oak Plank!", new BlockPos(0,0,0));
        }
        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testCraftingYieldRotten(TestContext context) {
        net.minecraft.recipe.RecipeManager recipeManager = context.getWorld().getServer().getRecipeManager();
        net.minecraft.recipe.input.CraftingRecipeInput input = net.minecraft.recipe.input.CraftingRecipeInput.create(1, 1, java.util.List.of(new ItemStack(net.minecraft.registry.Registries.ITEM.get(moldmod.SporesShadows.id("rotten_oak_log")))));

        java.util.Optional<net.minecraft.recipe.RecipeEntry<net.minecraft.recipe.CraftingRecipe>> recipe = recipeManager.getFirstMatch(net.minecraft.recipe.RecipeType.CRAFTING, input, context.getWorld());
        
        if (recipe.isPresent()) {
            context.throwPositionedException("Rotten Oak Log should not be craftable!", new BlockPos(0,0,0));
        }
        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testFuelTimes(TestContext context) {
        Integer taintedTime = net.fabricmc.fabric.api.registry.FuelRegistry.INSTANCE.get(net.minecraft.registry.Registries.ITEM.get(moldmod.SporesShadows.id("tainted_oak_log")));
        Integer moldyTime = net.fabricmc.fabric.api.registry.FuelRegistry.INSTANCE.get(net.minecraft.registry.Registries.ITEM.get(moldmod.SporesShadows.id("moldy_oak_log")));
        Integer rottenTime = net.fabricmc.fabric.api.registry.FuelRegistry.INSTANCE.get(net.minecraft.registry.Registries.ITEM.get(moldmod.SporesShadows.id("rotten_oak_log")));

        if (taintedTime == null || taintedTime != 150) {
            context.throwPositionedException("Tainted fuel time should be 150!", new BlockPos(0,0,0));
        }
        if (moldyTime == null || moldyTime != 75) {
            context.throwPositionedException("Moldy fuel time should be 75!", new BlockPos(0,0,0));
        }
        if (rottenTime == null || rottenTime != 25) {
            context.throwPositionedException("Rotten fuel time should be 25!", new BlockPos(0,0,0));
        }
        
        context.complete();
    }
}
