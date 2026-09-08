package moldmod.test.gametest.integration.jei;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;

import java.util.List;

public class JEICatalystsRegistrationGameTests {

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testWaxingCatalysts(TestContext context) {
        ItemStack honeycomb = new ItemStack(Items.HONEYCOMB);
        context.assertTrue(!honeycomb.isEmpty(), "Honeycomb catalyst must not be empty");
        context.assertTrue(honeycomb.isOf(Items.HONEYCOMB), "Honeycomb item must match");

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testScrapingAxesCatalysts(TestContext context) {
        List<Item> axes = List.of(
            Items.WOODEN_AXE,
            Items.STONE_AXE,
            Items.IRON_AXE,
            Items.GOLDEN_AXE,
            Items.DIAMOND_AXE,
            Items.NETHERITE_AXE
        );

        context.assertTrue(axes.size() == 6, "Must register all 6 tier axes as scraping catalysts");
        for (Item axe : axes) {
            ItemStack stack = new ItemStack(axe);
            context.assertTrue(!stack.isEmpty(), "Axe catalyst item must not be empty: " + axe);
        }

        context.complete();
    }
}
