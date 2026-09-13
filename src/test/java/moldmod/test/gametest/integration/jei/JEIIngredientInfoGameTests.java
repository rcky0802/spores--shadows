package moldmod.test.gametest.integration.jei;

import moldmod.block.ModBlocks;
import moldmod.item.ModItems;
import moldmod.registry.ModEnchantments;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JEIIngredientInfoGameTests {

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testRottenIngredientsInfoCoverage(TestContext context) {
        List<ItemStack> rottenStacks = new ArrayList<>();

        for (Map.Entry<Item, List<Item>> entry : ModBlocks.MOLDY_ITEMS_BY_VANILLA.entrySet()) {
            List<Item> items = entry.getValue();
            if (items == null || items.size() < 7) continue;

            Item itemRotten = items.get(5);
            Item itemWaxedRotten = items.get(6);

            rottenStacks.add(new ItemStack(itemRotten));
            rottenStacks.add(new ItemStack(itemWaxedRotten));
        }

        context.assertTrue(!rottenStacks.isEmpty(), "Rotten stacks list must not be empty");
        for (ItemStack stack : rottenStacks) {
            context.assertTrue(!stack.isEmpty(), "Rotten stack item must not be empty");
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testSporeItemsInfoCoverage(TestContext context) {
        ItemStack mask = new ItemStack(ModItems.SPORE_MASK);
        context.assertTrue(!mask.isEmpty(), "Spore mask stack must not be empty");

        ItemStack detector = new ItemStack(ModItems.SPORE_DETECTOR);
        context.assertTrue(!detector.isEmpty(), "Spore detector stack must not be empty");

        ItemStack moistureDetector = new ItemStack(ModItems.MOISTURE_DETECTOR);
        context.assertTrue(!moistureDetector.isEmpty(), "Moisture detector stack must not be empty");

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testSporeFiltrationBooksInfoGeneration(TestContext context) {
        var reg = context.getWorld().getRegistryManager().getOptional(RegistryKeys.ENCHANTMENT).orElseThrow();
        var filtrationEntry = reg.getEntry(ModEnchantments.SPORE_FILTRATION).orElseThrow();

        List<ItemStack> books = new ArrayList<>();
        for (int lvl = 1; lvl <= 3; lvl++) {
            books.add(EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(filtrationEntry, lvl)));
        }

        context.assertTrue(books.size() == 3, "Must generate 3 enchanted books (Levels I, II, III)");
        for (ItemStack book : books) {
            context.assertTrue(!book.isEmpty(), "Enchanted book must not be empty");
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testInfectedPlanksInfoCoverage(TestContext context) {
        List<ItemStack> taintedPlankStacks = new ArrayList<>();
        List<ItemStack> moldyPlankStacks = new ArrayList<>();

        for (Map.Entry<Item, List<Item>> entry : ModBlocks.MOLDY_ITEMS_BY_VANILLA.entrySet()) {
            Item itemVanilla = entry.getKey();
            List<Item> items = entry.getValue();
            if (items == null || items.size() < 7) continue;

            String path = net.minecraft.registry.Registries.ITEM.getId(itemVanilla).getPath();
            if (path.endsWith("_planks")) {
                Item itemTainted = items.get(1);
                Item itemWaxedTainted = items.get(2);
                Item itemMoldy = items.get(3);
                Item itemWaxedMoldy = items.get(4);

                taintedPlankStacks.add(new ItemStack(itemTainted));
                taintedPlankStacks.add(new ItemStack(itemWaxedTainted));
                moldyPlankStacks.add(new ItemStack(itemMoldy));
                moldyPlankStacks.add(new ItemStack(itemWaxedMoldy));
            }
        }

        context.assertTrue(taintedPlankStacks.size() == 22,
                "Tainted plank stacks count should equal 22 (11 wood types * 2 variants), found: " + taintedPlankStacks.size());
        context.assertTrue(moldyPlankStacks.size() == 22,
                "Moldy plank stacks count should equal 22 (11 wood types * 2 variants), found: " + moldyPlankStacks.size());

        for (ItemStack stack : taintedPlankStacks) {
            context.assertTrue(!stack.isEmpty(), "Tainted plank stack must not be empty");
        }
        for (ItemStack stack : moldyPlankStacks) {
            context.assertTrue(!stack.isEmpty(), "Moldy plank stack must not be empty");
        }

        Text taintedText = Text.translatable("jei.spores--shadows.info.tainted_planks");
        context.assertTrue(!taintedText.getString().isEmpty() && !taintedText.getString().equals("jei.spores--shadows.info.tainted_planks"),
                "Tainted planks JEI info description must be resolved and non-empty");

        Text moldyText = Text.translatable("jei.spores--shadows.info.moldy_planks");
        context.assertTrue(!moldyText.getString().isEmpty() && !moldyText.getString().equals("jei.spores--shadows.info.moldy_planks"),
                "Moldy planks JEI info description must be resolved and non-empty");

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testPlanksRecoveryRecipesInRecipeManager(TestContext context) {
        var recipeManager = context.getWorld().getRecipeManager();

        for (moldmod.SporesShadowsConstants.MoldyWoodType woodType : moldmod.SporesShadowsConstants.WOOD_TYPES) {
            String prefix = woodType.name();

            net.minecraft.util.Identifier taintedRecipeId = moldmod.SporesShadows.id(prefix + "_planks_from_tainted_planks");
            context.assertTrue(recipeManager.get(taintedRecipeId).isPresent(),
                    "Recipe " + taintedRecipeId + " must be present in RecipeManager for JEI display");

            net.minecraft.util.Identifier moldyRecipeId = moldmod.SporesShadows.id(prefix + "_planks_from_moldy_planks");
            context.assertTrue(recipeManager.get(moldyRecipeId).isPresent(),
                    "Recipe " + moldyRecipeId + " must be present in RecipeManager for JEI display");
        }

        context.complete();
    }
}
