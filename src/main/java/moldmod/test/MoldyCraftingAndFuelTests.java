package moldmod.test;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public class MoldyCraftingAndFuelTests {

    // ============================================
    // === FUEL SCALING TESTS ===
    // ============================================

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testFuelScaling(TestContext context) {
        Integer vanillaTime = net.fabricmc.fabric.api.registry.FuelRegistry.INSTANCE.get(Items.OAK_LOG);
        // If vanilla is not registered directly, we assume 300 (vanilla standard for logs/planks)
        int baseTime = (vanillaTime != null && vanillaTime > 0) ? vanillaTime : 300;

        Integer taintedTime = net.fabricmc.fabric.api.registry.FuelRegistry.INSTANCE.get(net.minecraft.registry.Registries.ITEM.get(moldmod.SporesShadows.id("tainted_oak_log")));
        Integer moldyTime = net.fabricmc.fabric.api.registry.FuelRegistry.INSTANCE.get(net.minecraft.registry.Registries.ITEM.get(moldmod.SporesShadows.id("moldy_oak_log")));
        Integer rottenTime = net.fabricmc.fabric.api.registry.FuelRegistry.INSTANCE.get(net.minecraft.registry.Registries.ITEM.get(moldmod.SporesShadows.id("rotten_oak_log")));

        int expectedTainted = baseTime / 2; // 50%
        int expectedMoldy = baseTime / 4;   // 25%
        int expectedRotten = Math.max(1, baseTime / 8); // 12.5%, min 1

        if (taintedTime == null || taintedTime != expectedTainted) {
            context.throwPositionedException("Tainted fuel time incorrect! Expected " + expectedTainted + " got " + taintedTime, new BlockPos(0,0,0));
        }
        if (moldyTime == null || moldyTime != expectedMoldy) {
            context.throwPositionedException("Moldy fuel time incorrect! Expected " + expectedMoldy + " got " + moldyTime, new BlockPos(0,0,0));
        }
        if (rottenTime == null || rottenTime != expectedRotten) {
            context.throwPositionedException("Rotten fuel time incorrect! Expected " + expectedRotten + " got " + rottenTime, new BlockPos(0,0,0));
        }
        
        context.complete();
    }

    // ============================================
    // === CRAFTING DECOMPOSITION TESTS ===
    // ============================================
    // Vanilla Logs -> Planks = 4
    // Tainted Logs -> Planks = 2
    // Moldy Logs -> Planks = 1
    // Rotten Logs -> Planks = 0

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testCraftingYieldTaintedLogToPlanks(TestContext context) {
        net.minecraft.recipe.RecipeManager recipeManager = context.getWorld().getServer().getRecipeManager();
        net.minecraft.recipe.input.CraftingRecipeInput input = net.minecraft.recipe.input.CraftingRecipeInput.create(1, 1, java.util.List.of(new ItemStack(net.minecraft.registry.Registries.ITEM.get(moldmod.SporesShadows.id("tainted_oak_log")))));

        java.util.Optional<net.minecraft.recipe.RecipeEntry<net.minecraft.recipe.CraftingRecipe>> recipe = recipeManager.getFirstMatch(net.minecraft.recipe.RecipeType.CRAFTING, input, context.getWorld());
        
        if (recipe.isEmpty() || recipe.get().value().getResult(context.getWorld().getRegistryManager()).getCount() != 2 || !recipe.get().value().getResult(context.getWorld().getRegistryManager()).getItem().equals(Items.OAK_PLANKS)) {
            context.throwPositionedException("Tainted Oak Log should craft into exactly 2 Vanilla Oak Planks!", new BlockPos(0,0,0));
        }
        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testCraftingYieldMoldyLogToPlanks(TestContext context) {
        net.minecraft.recipe.RecipeManager recipeManager = context.getWorld().getServer().getRecipeManager();
        net.minecraft.recipe.input.CraftingRecipeInput input = net.minecraft.recipe.input.CraftingRecipeInput.create(1, 1, java.util.List.of(new ItemStack(net.minecraft.registry.Registries.ITEM.get(moldmod.SporesShadows.id("moldy_oak_log")))));

        java.util.Optional<net.minecraft.recipe.RecipeEntry<net.minecraft.recipe.CraftingRecipe>> recipe = recipeManager.getFirstMatch(net.minecraft.recipe.RecipeType.CRAFTING, input, context.getWorld());
        
        if (recipe.isEmpty() || recipe.get().value().getResult(context.getWorld().getRegistryManager()).getCount() != 1 || !recipe.get().value().getResult(context.getWorld().getRegistryManager()).getItem().equals(Items.OAK_PLANKS)) {
            context.throwPositionedException("Moldy Oak Log should craft into exactly 1 Vanilla Oak Plank!", new BlockPos(0,0,0));
        }
        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testCraftingYieldRottenLogToPlanksIsDisabled(TestContext context) {
        net.minecraft.recipe.RecipeManager recipeManager = context.getWorld().getServer().getRecipeManager();
        net.minecraft.recipe.input.CraftingRecipeInput input = net.minecraft.recipe.input.CraftingRecipeInput.create(1, 1, java.util.List.of(new ItemStack(net.minecraft.registry.Registries.ITEM.get(moldmod.SporesShadows.id("rotten_oak_log")))));

        java.util.Optional<net.minecraft.recipe.RecipeEntry<net.minecraft.recipe.CraftingRecipe>> recipe = recipeManager.getFirstMatch(net.minecraft.recipe.RecipeType.CRAFTING, input, context.getWorld());
        
        if (recipe.isPresent()) {
            context.throwPositionedException("Rotten Oak Log should NOT be craftable into anything!", new BlockPos(0,0,0));
        }
        context.complete();
    }

    // ============================================
    // === CRAFTING DECOMPOSITION: PLANKS -> STICKS
    // Vanilla 2 Planks -> 4 Sticks
    // Tainted 2 Planks -> 2 Sticks
    // Moldy 2 Planks -> 1 Stick
    // Rotten 2 Planks -> 0 Sticks

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testCraftingYieldTaintedPlanksToSticks(TestContext context) {
        net.minecraft.recipe.RecipeManager recipeManager = context.getWorld().getServer().getRecipeManager();
        ItemStack taintedPlanks = new ItemStack(net.minecraft.registry.Registries.ITEM.get(moldmod.SporesShadows.id("tainted_oak_planks")));
        net.minecraft.recipe.input.CraftingRecipeInput input = net.minecraft.recipe.input.CraftingRecipeInput.create(1, 2, java.util.List.of(taintedPlanks, taintedPlanks));

        java.util.Optional<net.minecraft.recipe.RecipeEntry<net.minecraft.recipe.CraftingRecipe>> recipe = recipeManager.getFirstMatch(net.minecraft.recipe.RecipeType.CRAFTING, input, context.getWorld());
        
        if (recipe.isEmpty() || recipe.get().value().getResult(context.getWorld().getRegistryManager()).getCount() != 2 || !recipe.get().value().getResult(context.getWorld().getRegistryManager()).getItem().equals(Items.STICK)) {
            context.throwPositionedException("2 Tainted Oak Planks should craft into exactly 2 Sticks!", new BlockPos(0,0,0));
        }
        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testCraftingYieldMoldyPlanksToSticks(TestContext context) {
        net.minecraft.recipe.RecipeManager recipeManager = context.getWorld().getServer().getRecipeManager();
        ItemStack moldyPlanks = new ItemStack(net.minecraft.registry.Registries.ITEM.get(moldmod.SporesShadows.id("moldy_oak_planks")));
        net.minecraft.recipe.input.CraftingRecipeInput input = net.minecraft.recipe.input.CraftingRecipeInput.create(1, 2, java.util.List.of(moldyPlanks, moldyPlanks));

        java.util.Optional<net.minecraft.recipe.RecipeEntry<net.minecraft.recipe.CraftingRecipe>> recipe = recipeManager.getFirstMatch(net.minecraft.recipe.RecipeType.CRAFTING, input, context.getWorld());
        
        if (recipe.isEmpty() || recipe.get().value().getResult(context.getWorld().getRegistryManager()).getCount() != 1 || !recipe.get().value().getResult(context.getWorld().getRegistryManager()).getItem().equals(Items.STICK)) {
            context.throwPositionedException("2 Moldy Oak Planks should craft into exactly 1 Stick!", new BlockPos(0,0,0));
        }
        context.complete();
    }

    // ============================================
    // === CRAFTING DECOMPOSITION: LOGS -> WOOD
    // Vanilla 4 Logs -> 3 Wood
    // Tainted 4 Logs -> 1 Wood (3 / 2 = 1.5 -> floor -> 1)
    // Moldy 4 Logs -> 0 Wood (3 / 4 = 0.75 -> floor -> 0)

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testCraftingYieldTaintedLogToWood(TestContext context) {
        net.minecraft.recipe.RecipeManager recipeManager = context.getWorld().getServer().getRecipeManager();
        ItemStack taintedLog = new ItemStack(net.minecraft.registry.Registries.ITEM.get(moldmod.SporesShadows.id("tainted_oak_log")));
        net.minecraft.recipe.input.CraftingRecipeInput input = net.minecraft.recipe.input.CraftingRecipeInput.create(2, 2, java.util.List.of(taintedLog, taintedLog, taintedLog, taintedLog));

        java.util.Optional<net.minecraft.recipe.RecipeEntry<net.minecraft.recipe.CraftingRecipe>> recipe = recipeManager.getFirstMatch(net.minecraft.recipe.RecipeType.CRAFTING, input, context.getWorld());
        
        if (recipe.isEmpty() || recipe.get().value().getResult(context.getWorld().getRegistryManager()).getCount() != 1 || !recipe.get().value().getResult(context.getWorld().getRegistryManager()).getItem().equals(Items.OAK_WOOD)) {
            context.throwPositionedException("4 Tainted Oak Logs should craft into exactly 1 Vanilla Oak Wood!", new BlockPos(0,0,0));
        }
        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testCraftingYieldMoldyLogToWoodIsDisabled(TestContext context) {
        net.minecraft.recipe.RecipeManager recipeManager = context.getWorld().getServer().getRecipeManager();
        ItemStack moldyLog = new ItemStack(net.minecraft.registry.Registries.ITEM.get(moldmod.SporesShadows.id("moldy_oak_log")));
        net.minecraft.recipe.input.CraftingRecipeInput input = net.minecraft.recipe.input.CraftingRecipeInput.create(2, 2, java.util.List.of(moldyLog, moldyLog, moldyLog, moldyLog));

        java.util.Optional<net.minecraft.recipe.RecipeEntry<net.minecraft.recipe.CraftingRecipe>> recipe = recipeManager.getFirstMatch(net.minecraft.recipe.RecipeType.CRAFTING, input, context.getWorld());
        
        if (recipe.isPresent()) {
            context.throwPositionedException("Moldy Oak Logs should NOT be craftable into Wood (yield is 0)!", new BlockPos(0,0,0));
        }
        context.complete();
    }
}
