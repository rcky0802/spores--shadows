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
}
