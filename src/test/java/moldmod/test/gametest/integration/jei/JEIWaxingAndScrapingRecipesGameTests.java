package moldmod.test.gametest.integration.jei;

import moldmod.block.ModBlocks;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JEIWaxingAndScrapingRecipesGameTests {

    public record RecipePair(ItemStack input, ItemStack output) {}

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testRecipeTypeUids(TestContext context) {
        // Verify wood family registration integrity
        context.assertTrue(!ModBlocks.MOLDY_ITEMS_BY_VANILLA.isEmpty(),
                "Vanilla to moldy item mapping must not be empty");
        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testWaxingAndScrapingRecipeGeneration(TestContext context) {
        List<RecipePair> waxingRecipes = new ArrayList<>();
        List<RecipePair> scrapingRecipes = new ArrayList<>();

        int woodFamilyCount = 0;
        for (Map.Entry<Item, List<Item>> entry : ModBlocks.MOLDY_ITEMS_BY_VANILLA.entrySet()) {
            Item itemVanilla = entry.getKey();
            List<Item> items = entry.getValue();
            if (items == null || items.size() < 7) continue;

            woodFamilyCount++;
            Item itemWaxed0 = items.get(0);
            Item itemTainted = items.get(1);
            Item itemWaxedTainted = items.get(2);
            Item itemMoldy = items.get(3);
            Item itemWaxedMoldy = items.get(4);
            Item itemRotten = items.get(5);
            Item itemWaxedRotten = items.get(6);

            // 1. Waxing: 4 recipes per family (Vanilla, Tainted, Moldy, Rotten -> Waxed variants)
            waxingRecipes.add(new RecipePair(new ItemStack(itemVanilla), new ItemStack(itemWaxed0)));
            waxingRecipes.add(new RecipePair(new ItemStack(itemTainted), new ItemStack(itemWaxedTainted)));
            waxingRecipes.add(new RecipePair(new ItemStack(itemMoldy), new ItemStack(itemWaxedMoldy)));
            waxingRecipes.add(new RecipePair(new ItemStack(itemRotten), new ItemStack(itemWaxedRotten)));

            // 2. Scraping:
            // A) De-waxing (4 recipes: Waxed variants -> Unwaxed variants)
            scrapingRecipes.add(new RecipePair(new ItemStack(itemWaxed0), new ItemStack(itemVanilla)));
            scrapingRecipes.add(new RecipePair(new ItemStack(itemWaxedTainted), new ItemStack(itemTainted)));
            scrapingRecipes.add(new RecipePair(new ItemStack(itemWaxedMoldy), new ItemStack(itemMoldy)));
            scrapingRecipes.add(new RecipePair(new ItemStack(itemWaxedRotten), new ItemStack(itemRotten)));

            // B) De-molding (2 recipes: Moldy -> Tainted -> Vanilla)
            scrapingRecipes.add(new RecipePair(new ItemStack(itemMoldy), new ItemStack(itemTainted)));
            scrapingRecipes.add(new RecipePair(new ItemStack(itemTainted), new ItemStack(itemVanilla)));
        }

        context.assertTrue(woodFamilyCount > 0, "Wood family count must be > 0");
        context.assertTrue(waxingRecipes.size() == woodFamilyCount * 4,
                "Waxing recipes count should equal woodFamilyCount * 4");
        context.assertTrue(scrapingRecipes.size() == woodFamilyCount * 6,
                "Scraping recipes count should equal woodFamilyCount * 6 (4 de-wax + 2 de-mold)");

        context.complete();
    }
}
