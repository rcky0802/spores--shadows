package moldmod.test.gametest.integration.jei;

import moldmod.item.ModItems;
import moldmod.registry.ModEnchantments;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;

import java.util.List;

public class JEIAnvilRecipesGameTests {

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testFilterForSporeMaskRepair(TestContext context) {
        ItemStack filter = new ItemStack(ModItems.SPORE_FILTER);
        context.assertTrue(ModItems.SPORE_MASK.canRepair(new ItemStack(ModItems.SPORE_MASK), filter),
                "Spore mask must be repairable with Spore Filter in anvil");

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testSporeMaskAllowedAnvilEnchantments(TestContext context) {
        var reg = context.getWorld().getRegistryManager().getOptional(RegistryKeys.ENCHANTMENT).orElseThrow();
        ItemStack mask = new ItemStack(ModItems.SPORE_MASK);

        RegistryEntry<Enchantment> unbreaking = reg.getEntry(Enchantments.UNBREAKING).orElseThrow();
        RegistryEntry<Enchantment> mending = reg.getEntry(Enchantments.MENDING).orElseThrow();
        RegistryEntry<Enchantment> vanishing = reg.getEntry(Enchantments.VANISHING_CURSE).orElseThrow();

        context.assertTrue(unbreaking.value().isAcceptableItem(mask), "Unbreaking must be acceptable on Spore Mask");
        context.assertTrue(mending.value().isAcceptableItem(mask), "Mending must be acceptable on Spore Mask");
        context.assertTrue(vanishing.value().isAcceptableItem(mask),
                "Curse of Vanishing must be acceptable on Spore Mask");

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testSporeFiltrationHelmetsCompatibility(TestContext context) {
        var reg = context.getWorld().getRegistryManager().getOptional(RegistryKeys.ENCHANTMENT).orElseThrow();
        RegistryEntry<Enchantment> filtration = reg.getEntry(ModEnchantments.SPORE_FILTRATION).orElseThrow();

        for (Item helmetItem : List.of(Items.DIAMOND_HELMET, Items.NETHERITE_HELMET, Items.IRON_HELMET,
                Items.GOLDEN_HELMET, Items.CHAINMAIL_HELMET, Items.LEATHER_HELMET)) {
            ItemStack helm = new ItemStack(helmetItem);
            context.assertTrue(filtration.value().isAcceptableItem(helm),
                    "Spore Filtration must be acceptable on " + helmetItem);
        }

        context.complete();
    }
}
