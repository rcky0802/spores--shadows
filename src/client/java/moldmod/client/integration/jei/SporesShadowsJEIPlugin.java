package moldmod.client.integration.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import moldmod.SporesShadows;
import moldmod.block.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class SporesShadowsJEIPlugin implements IModPlugin {

    public static final Identifier PLUGIN_ID = SporesShadows.id("jei_plugin");

    @Override
    public Identifier getPluginUid() {
        return PLUGIN_ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        var guiHelper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(
                new WaxingRecipeCategory(guiHelper),
                new ScrapingRecipeCategory(guiHelper)
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        List<WaxingRecipe> waxingRecipes = new ArrayList<>();
        List<ScrapingRecipe> scrapingRecipes = new ArrayList<>();
        List<ItemStack> rottenStacks = new ArrayList<>();

        for (Block vanillaBlock : ModBlocks.VANILLA_TO_MOLDY.keySet()) {
            String baseName = Registries.BLOCK.getId(vanillaBlock).getPath();

            Item itemVanilla = vanillaBlock.asItem();
            Item itemTainted = Registries.ITEM.get(SporesShadows.id("tainted_" + baseName));
            Item itemMoldy = Registries.ITEM.get(SporesShadows.id("moldy_" + baseName));
            Item itemRotten = Registries.ITEM.get(SporesShadows.id("rotten_" + baseName));

            Item itemWaxed0 = Registries.ITEM.get(SporesShadows.id("waxed_" + baseName));
            Item itemWaxedTainted = Registries.ITEM.get(SporesShadows.id("waxed_tainted_" + baseName));
            Item itemWaxedMoldy = Registries.ITEM.get(SporesShadows.id("waxed_moldy_" + baseName));
            Item itemWaxedRotten = Registries.ITEM.get(SporesShadows.id("waxed_rotten_" + baseName));

            if (itemVanilla == Items.AIR || itemTainted == Items.AIR || itemMoldy == Items.AIR || itemRotten == Items.AIR) {
                continue;
            }

            // 1. Waxing Recipes (Shift + Right Click with Honeycomb)
            waxingRecipes.add(new WaxingRecipe(new ItemStack(itemVanilla), new ItemStack(itemWaxed0)));
            waxingRecipes.add(new WaxingRecipe(new ItemStack(itemTainted), new ItemStack(itemWaxedTainted)));
            waxingRecipes.add(new WaxingRecipe(new ItemStack(itemMoldy), new ItemStack(itemWaxedMoldy)));
            waxingRecipes.add(new WaxingRecipe(new ItemStack(itemRotten), new ItemStack(itemWaxedRotten)));

            // 2. Scraping Recipes (Shift + Right Click with Axe)
            // A) De-waxing
            scrapingRecipes.add(new ScrapingRecipe(new ItemStack(itemWaxed0), new ItemStack(itemVanilla), "dewax"));
            scrapingRecipes.add(new ScrapingRecipe(new ItemStack(itemWaxedTainted), new ItemStack(itemTainted), "dewax"));
            scrapingRecipes.add(new ScrapingRecipe(new ItemStack(itemWaxedMoldy), new ItemStack(itemMoldy), "dewax"));
            scrapingRecipes.add(new ScrapingRecipe(new ItemStack(itemWaxedRotten), new ItemStack(itemRotten), "dewax"));

            // B) De-molding (Cure mold)
            scrapingRecipes.add(new ScrapingRecipe(new ItemStack(itemMoldy), new ItemStack(itemTainted), "demold"));
            scrapingRecipes.add(new ScrapingRecipe(new ItemStack(itemTainted), new ItemStack(itemVanilla), "demold"));

            // 3. Rotten items info
            rottenStacks.add(new ItemStack(itemRotten));
            rottenStacks.add(new ItemStack(itemWaxedRotten));
        }

        registration.addRecipes(WaxingRecipeCategory.RECIPE_TYPE, waxingRecipes);
        registration.addRecipes(ScrapingRecipeCategory.RECIPE_TYPE, scrapingRecipes);

        if (!rottenStacks.isEmpty()) {
            registration.addIngredientInfo(rottenStacks, VanillaTypes.ITEM_STACK, Text.translatable("jei.spores--shadows.info.rotten_wood"));
        }
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(Items.HONEYCOMB), WaxingRecipeCategory.RECIPE_TYPE);

        registration.addRecipeCatalyst(new ItemStack(Items.WOODEN_AXE), ScrapingRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(Items.STONE_AXE), ScrapingRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(Items.IRON_AXE), ScrapingRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(Items.GOLDEN_AXE), ScrapingRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(Items.DIAMOND_AXE), ScrapingRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(Items.NETHERITE_AXE), ScrapingRecipeCategory.RECIPE_TYPE);
    }
}
