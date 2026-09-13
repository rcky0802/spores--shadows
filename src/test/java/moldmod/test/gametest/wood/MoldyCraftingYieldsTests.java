package moldmod.test.gametest.wood;

import moldmod.SporesShadows;
import moldmod.SporesShadowsConstants;
import moldmod.SporesShadowsConstants.MoldyWoodType;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.Registries;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.Optional;

public class MoldyCraftingYieldsTests {

    // ============================================
    // === 1. LOG TO PLANKS YIELDS TESTS ===
    // ============================================

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testLogToPlanksYields(TestContext context) {
        RecipeManager recipeManager = context.getWorld().getRecipeManager();

        for (MoldyWoodType woodType : SporesShadowsConstants.WOOD_TYPES) {
            String logName = woodType.getLogName();
            String prefix = woodType.name();
            Item expectedPlanks = Registries.ITEM.get(Identifier.of("minecraft", prefix + "_planks"));

            Item vanillaLog = Registries.ITEM.get(Identifier.of("minecraft", logName));
            Item waxedLog = Registries.ITEM.get(SporesShadows.id("waxed_" + logName));
            Item taintedLog = Registries.ITEM.get(SporesShadows.id("tainted_" + logName));
            Item waxedTaintedLog = Registries.ITEM.get(SporesShadows.id("waxed_tainted_" + logName));
            Item moldyLog = Registries.ITEM.get(SporesShadows.id("moldy_" + logName));
            Item waxedMoldyLog = Registries.ITEM.get(SporesShadows.id("waxed_moldy_" + logName));
            Item rottenLog = Registries.ITEM.get(SporesShadows.id("rotten_" + logName));
            Item waxedRottenLog = Registries.ITEM.get(SporesShadows.id("waxed_rotten_" + logName));

            int baseYield = woodType.isBamboo() ? 2 : 4;
            int taintedYield = woodType.isBamboo() ? 1 : 2;
            int moldyYield = woodType.isBamboo() ? 0 : 1;

            // Stage 0: Healthy Log (Vanilla & Waxed) -> Base Yield (4 for wood, 2 for bamboo)
            assertSingleItemYield(context, recipeManager, vanillaLog, expectedPlanks, baseYield, "Vanilla " + logName);
            assertSingleItemYield(context, recipeManager, waxedLog, expectedPlanks, baseYield, "Waxed " + logName);

            // Stage 1: Tainted Log (Unwaxed & Waxed) -> Half Yield (2 for wood, 1 for bamboo)
            assertSingleItemYield(context, recipeManager, taintedLog, expectedPlanks, taintedYield, "Tainted " + logName);
            assertSingleItemYield(context, recipeManager, waxedTaintedLog, expectedPlanks, taintedYield, "Waxed Tainted " + logName);

            // Stage 2: Moldy Log (Unwaxed & Waxed) -> Quarter Yield (1 for wood, 0 for bamboo)
            assertSingleItemYield(context, recipeManager, moldyLog, expectedPlanks, moldyYield, "Moldy " + logName);
            assertSingleItemYield(context, recipeManager, waxedMoldyLog, expectedPlanks, moldyYield, "Waxed Moldy " + logName);

            // Stage 3: Rotten Log (Unwaxed & Waxed) -> 0 Yield
            assertSingleItemYield(context, recipeManager, rottenLog, expectedPlanks, 0, "Rotten " + logName);
            assertSingleItemYield(context, recipeManager, waxedRottenLog, expectedPlanks, 0, "Waxed Rotten " + logName);
        }

        context.complete();
    }

    // ============================================
    // === 2. PLANKS RECOVERY TESTS ===
    // ============================================

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testPlanksRecovery(TestContext context) {
        RecipeManager recipeManager = context.getWorld().getRecipeManager();

        for (MoldyWoodType woodType : SporesShadowsConstants.WOOD_TYPES) {
            String prefix = woodType.name();
            Item vanillaPlanks = Registries.ITEM.get(Identifier.of("minecraft", prefix + "_planks"));
            Item taintedPlanks = Registries.ITEM.get(SporesShadows.id("tainted_" + prefix + "_planks"));
            Item waxedTaintedPlanks = Registries.ITEM.get(SporesShadows.id("waxed_tainted_" + prefix + "_planks"));
            Item moldyPlanks = Registries.ITEM.get(SporesShadows.id("moldy_" + prefix + "_planks"));
            Item waxedMoldyPlanks = Registries.ITEM.get(SporesShadows.id("waxed_moldy_" + prefix + "_planks"));
            Item rottenPlanks = Registries.ITEM.get(SporesShadows.id("rotten_" + prefix + "_planks"));
            Item waxedRottenPlanks = Registries.ITEM.get(SporesShadows.id("waxed_rotten_" + prefix + "_planks"));

            // A) Tainted Planks Recovery: 2 tainted planks -> 1 vanilla plank
            // Pure unwaxed
            assertCraftingMatch(context, recipeManager,
                    List.of(new ItemStack(taintedPlanks), new ItemStack(taintedPlanks)),
                    2, 1, vanillaPlanks, 1, "2 Tainted " + prefix + " planks -> 1 Vanilla");
            // Pure waxed
            assertCraftingMatch(context, recipeManager,
                    List.of(new ItemStack(waxedTaintedPlanks), new ItemStack(waxedTaintedPlanks)),
                    2, 1, vanillaPlanks, 1, "2 Waxed Tainted " + prefix + " planks -> 1 Vanilla");
            // Mixed unwaxed + waxed (Interchangeability test)
            assertCraftingMatch(context, recipeManager,
                    List.of(new ItemStack(taintedPlanks), new ItemStack(waxedTaintedPlanks)),
                    2, 1, vanillaPlanks, 1, "1 Tainted + 1 Waxed Tainted " + prefix + " planks -> 1 Vanilla");

            // Single tainted plank cannot craft vanilla planks
            CraftingRecipeInput singleTainted = CraftingRecipeInput.create(1, 1, List.of(new ItemStack(taintedPlanks)));
            Optional<RecipeEntry<CraftingRecipe>> singleMatch = recipeManager.getFirstMatch(RecipeType.CRAFTING, singleTainted, context.getWorld());
            if (singleMatch.isPresent()) {
                ItemStack out = singleMatch.get().value().craft(singleTainted, context.getWorld().getRegistryManager());
                if (out.isOf(vanillaPlanks)) {
                    context.throwPositionedException("1 tainted plank should not produce vanilla planks!", BlockPos.ORIGIN);
                }
            }

            // B) Moldy Planks Recovery: 4 moldy planks -> 1 vanilla plank
            // Pure unwaxed
            assertCraftingMatch(context, recipeManager,
                    List.of(new ItemStack(moldyPlanks), new ItemStack(moldyPlanks),
                            new ItemStack(moldyPlanks), new ItemStack(moldyPlanks)),
                    2, 2, vanillaPlanks, 1, "4 Moldy " + prefix + " planks -> 1 Vanilla");
            // Pure waxed
            assertCraftingMatch(context, recipeManager,
                    List.of(new ItemStack(waxedMoldyPlanks), new ItemStack(waxedMoldyPlanks),
                            new ItemStack(waxedMoldyPlanks), new ItemStack(waxedMoldyPlanks)),
                    2, 2, vanillaPlanks, 1, "4 Waxed Moldy " + prefix + " planks -> 1 Vanilla");
            // Mixed unwaxed + waxed (Interchangeability test)
            assertCraftingMatch(context, recipeManager,
                    List.of(new ItemStack(moldyPlanks), new ItemStack(waxedMoldyPlanks),
                            new ItemStack(waxedMoldyPlanks), new ItemStack(moldyPlanks)),
                    2, 2, vanillaPlanks, 1, "Mixed Moldy " + prefix + " planks -> 1 Vanilla");

            // 2 or 3 moldy planks cannot craft vanilla planks
            CraftingRecipeInput twoMoldy = CraftingRecipeInput.create(2, 1,
                    List.of(new ItemStack(moldyPlanks), new ItemStack(moldyPlanks)));
            Optional<RecipeEntry<CraftingRecipe>> twoMoldyMatch = recipeManager.getFirstMatch(RecipeType.CRAFTING, twoMoldy, context.getWorld());
            if (twoMoldyMatch.isPresent()) {
                ItemStack out = twoMoldyMatch.get().value().craft(twoMoldy, context.getWorld().getRegistryManager());
                if (out.isOf(vanillaPlanks)) {
                    context.throwPositionedException("2 moldy planks should not produce vanilla planks!", BlockPos.ORIGIN);
                }
            }

            // C) Rotten Planks Recovery: 0 (No recovery recipe allowed)
            CraftingRecipeInput rottenInput1 = CraftingRecipeInput.create(1, 1, List.of(new ItemStack(rottenPlanks)));
            assertNoPlanksOutput(context, recipeManager, rottenInput1, vanillaPlanks, "1 Rotten " + prefix + " plank");

            CraftingRecipeInput rottenInput2 = CraftingRecipeInput.create(2, 1,
                    List.of(new ItemStack(rottenPlanks), new ItemStack(rottenPlanks)));
            assertNoPlanksOutput(context, recipeManager, rottenInput2, vanillaPlanks, "2 Rotten " + prefix + " planks");

            CraftingRecipeInput rottenInput4 = CraftingRecipeInput.create(2, 2,
                    List.of(new ItemStack(rottenPlanks), new ItemStack(rottenPlanks),
                            new ItemStack(rottenPlanks), new ItemStack(rottenPlanks)));
            assertNoPlanksOutput(context, recipeManager, rottenInput4, vanillaPlanks, "4 Rotten " + prefix + " planks");

            CraftingRecipeInput waxedRottenInput4 = CraftingRecipeInput.create(2, 2,
                    List.of(new ItemStack(waxedRottenPlanks), new ItemStack(waxedRottenPlanks),
                            new ItemStack(waxedRottenPlanks), new ItemStack(waxedRottenPlanks)));
            assertNoPlanksOutput(context, recipeManager, waxedRottenInput4, vanillaPlanks, "4 Waxed Rotten " + prefix + " planks");
        }

        context.complete();
    }

    // ============================================
    // === 3. NEGATIVE ASSERTIONS: COMPLEX RECIPES ===
    // ============================================

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testInfectedPlanksCannotCraftComplexItems(TestContext context) {
        RecipeManager recipeManager = context.getWorld().getRecipeManager();

        String[] testWoods = {"oak", "bamboo", "crimson"};

        for (String prefix : testWoods) {
            Item taintedPlanks = Registries.ITEM.get(SporesShadows.id("tainted_" + prefix + "_planks"));
            Item moldyPlanks = Registries.ITEM.get(SporesShadows.id("moldy_" + prefix + "_planks"));
            Item rottenPlanks = Registries.ITEM.get(SporesShadows.id("rotten_" + prefix + "_planks"));

            Item vanillaStairs = Registries.ITEM.get(Identifier.of("minecraft", prefix.equals("bamboo") ? "bamboo_mosaic_stairs" : prefix + "_stairs"));
            Item vanillaSlab = Registries.ITEM.get(Identifier.of("minecraft", prefix + "_slab"));
            Item vanillaDoor = Registries.ITEM.get(Identifier.of("minecraft", prefix + "_door"));
            Item vanillaTrapdoor = Registries.ITEM.get(Identifier.of("minecraft", prefix + "_trapdoor"));
            Item vanillaFence = Registries.ITEM.get(Identifier.of("minecraft", prefix + "_fence"));
            Item fenceSupport = prefix.equals("bamboo") ? Items.STRING : Items.STICK;

            Item[] infectedVariants = {taintedPlanks, moldyPlanks, rottenPlanks};

            for (Item infectedPlank : infectedVariants) {
                // A) Stairs pattern (3x3):
                // # . .
                // # # .
                // # # #
                CraftingRecipeInput stairsInput = CraftingRecipeInput.create(3, 3, List.of(
                        new ItemStack(infectedPlank), ItemStack.EMPTY, ItemStack.EMPTY,
                        new ItemStack(infectedPlank), new ItemStack(infectedPlank), ItemStack.EMPTY,
                        new ItemStack(infectedPlank), new ItemStack(infectedPlank), new ItemStack(infectedPlank)
                ));
                assertRecipeDoesNotProduce(context, recipeManager, stairsInput, vanillaStairs,
                        "Infected plank " + Registries.ITEM.getId(infectedPlank) + " must NOT craft stairs");

                // B) Slab pattern (3x1):
                // # # #
                CraftingRecipeInput slabInput = CraftingRecipeInput.create(3, 1, List.of(
                        new ItemStack(infectedPlank), new ItemStack(infectedPlank), new ItemStack(infectedPlank)
                ));
                assertRecipeDoesNotProduce(context, recipeManager, slabInput, vanillaSlab,
                        "Infected plank " + Registries.ITEM.getId(infectedPlank) + " must NOT craft slab");

                // C) Door pattern (2x3):
                // # #
                // # #
                // # #
                CraftingRecipeInput doorInput = CraftingRecipeInput.create(2, 3, List.of(
                        new ItemStack(infectedPlank), new ItemStack(infectedPlank),
                        new ItemStack(infectedPlank), new ItemStack(infectedPlank),
                        new ItemStack(infectedPlank), new ItemStack(infectedPlank)
                ));
                assertRecipeDoesNotProduce(context, recipeManager, doorInput, vanillaDoor,
                        "Infected plank " + Registries.ITEM.getId(infectedPlank) + " must NOT craft door");

                // D) Trapdoor pattern (3x2):
                // # # #
                // # # #
                CraftingRecipeInput trapdoorInput = CraftingRecipeInput.create(3, 2, List.of(
                        new ItemStack(infectedPlank), new ItemStack(infectedPlank), new ItemStack(infectedPlank),
                        new ItemStack(infectedPlank), new ItemStack(infectedPlank), new ItemStack(infectedPlank)
                ));
                assertRecipeDoesNotProduce(context, recipeManager, trapdoorInput, vanillaTrapdoor,
                        "Infected plank " + Registries.ITEM.getId(infectedPlank) + " must NOT craft trapdoor");

                // E) Stick pattern (1x2):
                // #
                // #
                CraftingRecipeInput stickInput = CraftingRecipeInput.create(1, 2, List.of(
                        new ItemStack(infectedPlank),
                        new ItemStack(infectedPlank)
                ));
                assertRecipeDoesNotProduce(context, recipeManager, stickInput, Items.STICK,
                        "Infected plank " + Registries.ITEM.getId(infectedPlank) + " must NOT craft sticks");

                // F) Fence pattern (3x2):
                // # | #
                // # | #
                CraftingRecipeInput fenceInput = CraftingRecipeInput.create(3, 2, List.of(
                        new ItemStack(infectedPlank), new ItemStack(fenceSupport), new ItemStack(infectedPlank),
                        new ItemStack(infectedPlank), new ItemStack(fenceSupport), new ItemStack(infectedPlank)
                ));
                assertRecipeDoesNotProduce(context, recipeManager, fenceInput, vanillaFence,
                        "Infected plank " + Registries.ITEM.getId(infectedPlank) + " must NOT craft fence");
            }
        }

        context.complete();
    }

    // ============================================
    // === 4. POSITIVE TESTS: WAXED VANILLA CRAFTING ===
    // ============================================

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testWaxedVanillaCanCraftComplexItems(TestContext context) {
        RecipeManager recipeManager = context.getWorld().getRecipeManager();

        Item vanillaPlanks = Items.OAK_PLANKS;
        Item waxedPlanks = Registries.ITEM.get(SporesShadows.id("waxed_oak_planks"));

        // A) 6 Waxed Oak Planks -> 4 Vanilla Oak Stairs (Unwaxed!)
        CraftingRecipeInput stairsInput = CraftingRecipeInput.create(3, 3, List.of(
                new ItemStack(waxedPlanks), ItemStack.EMPTY, ItemStack.EMPTY,
                new ItemStack(waxedPlanks), new ItemStack(waxedPlanks), ItemStack.EMPTY,
                new ItemStack(waxedPlanks), new ItemStack(waxedPlanks), new ItemStack(waxedPlanks)
        ));
        assertCraftingMatch(context, recipeManager, stairsInput, Items.OAK_STAIRS, 4,
                "Waxed Oak Planks should craft 4 Vanilla Oak Stairs");

        // B) Mixed Vanilla + Waxed Oak Planks -> 4 Sticks (Unwaxed!)
        CraftingRecipeInput stickInput = CraftingRecipeInput.create(1, 2, List.of(
                new ItemStack(vanillaPlanks),
                new ItemStack(waxedPlanks)
        ));
        assertCraftingMatch(context, recipeManager, stickInput, Items.STICK, 4,
                "Mixed Vanilla + Waxed Oak Planks should craft 4 Sticks");

        // C) 3 Waxed Oak Planks -> 6 Vanilla Oak Slabs (Unwaxed!)
        CraftingRecipeInput slabInput = CraftingRecipeInput.create(3, 1, List.of(
                new ItemStack(waxedPlanks), new ItemStack(waxedPlanks), new ItemStack(waxedPlanks)
        ));
        assertCraftingMatch(context, recipeManager, slabInput, Items.OAK_SLAB, 6,
                "Waxed Oak Planks should craft 6 Vanilla Oak Slabs");

        context.complete();
    }

    // ============================================
    // === HELPER METHODS ===
    // ============================================

    private void assertSingleItemYield(TestContext context, RecipeManager recipeManager,
                                      Item inputItem, Item expectedOutput, int expectedCount, String label) {
        CraftingRecipeInput input = CraftingRecipeInput.create(1, 1, List.of(new ItemStack(inputItem)));
        Optional<RecipeEntry<CraftingRecipe>> match = recipeManager.getFirstMatch(RecipeType.CRAFTING, input, context.getWorld());

        if (expectedCount == 0) {
            if (match.isPresent()) {
                ItemStack output = match.get().value().craft(input, context.getWorld().getRegistryManager());
                if (output.isOf(expectedOutput)) {
                    context.throwPositionedException(label + " must NOT produce planks! Got: " + output.getCount(), BlockPos.ORIGIN);
                }
            }
        } else {
            if (match.isEmpty()) {
                context.throwPositionedException(label + " should produce " + expectedCount + " planks, but matched no recipe!", BlockPos.ORIGIN);
            }
            ItemStack output = match.get().value().craft(input, context.getWorld().getRegistryManager());
            if (!output.isOf(expectedOutput)) {
                context.throwPositionedException(label + " produced wrong item: " + Registries.ITEM.getId(output.getItem()) + " instead of " + Registries.ITEM.getId(expectedOutput), BlockPos.ORIGIN);
            }
            if (output.getCount() != expectedCount) {
                context.throwPositionedException(label + " produced count " + output.getCount() + ", expected " + expectedCount, BlockPos.ORIGIN);
            }
        }
    }

    private void assertCraftingMatch(TestContext context, RecipeManager recipeManager,
                                    List<ItemStack> items, int width, int height,
                                    Item expectedOutput, int expectedCount, String label) {
        CraftingRecipeInput input = CraftingRecipeInput.create(width, height, items);
        assertCraftingMatch(context, recipeManager, input, expectedOutput, expectedCount, label);
    }

    private void assertCraftingMatch(TestContext context, RecipeManager recipeManager,
                                    CraftingRecipeInput input, Item expectedOutput, int expectedCount, String label) {
        Optional<RecipeEntry<CraftingRecipe>> match = recipeManager.getFirstMatch(RecipeType.CRAFTING, input, context.getWorld());
        if (match.isEmpty()) {
            context.throwPositionedException(label + " matched no recipe!", BlockPos.ORIGIN);
        }
        ItemStack output = match.get().value().craft(input, context.getWorld().getRegistryManager());
        if (!output.isOf(expectedOutput)) {
            context.throwPositionedException(label + " produced wrong item: " + Registries.ITEM.getId(output.getItem()) + " instead of " + Registries.ITEM.getId(expectedOutput), BlockPos.ORIGIN);
        }
        if (output.getCount() != expectedCount) {
            context.throwPositionedException(label + " produced count " + output.getCount() + ", expected " + expectedCount, BlockPos.ORIGIN);
        }
    }

    private void assertNoPlanksOutput(TestContext context, RecipeManager recipeManager,
                                      CraftingRecipeInput input, Item expectedPlanks, String label) {
        Optional<RecipeEntry<CraftingRecipe>> match = recipeManager.getFirstMatch(RecipeType.CRAFTING, input, context.getWorld());
        if (match.isPresent()) {
            ItemStack output = match.get().value().craft(input, context.getWorld().getRegistryManager());
            if (output.isOf(expectedPlanks)) {
                context.throwPositionedException(label + " must NOT produce planks!", BlockPos.ORIGIN);
            }
        }
    }

    private void assertRecipeDoesNotProduce(TestContext context, RecipeManager recipeManager,
                                            CraftingRecipeInput input, Item prohibitedOutput, String errorMessage) {
        Optional<RecipeEntry<CraftingRecipe>> match = recipeManager.getFirstMatch(RecipeType.CRAFTING, input, context.getWorld());
        if (match.isPresent()) {
            ItemStack output = match.get().value().craft(input, context.getWorld().getRegistryManager());
            if (output.isOf(prohibitedOutput)) {
                context.throwPositionedException(errorMessage + ", but crafted: " + Registries.ITEM.getId(output.getItem()), BlockPos.ORIGIN);
            }
        }
    }
}
