package moldmod.client.integration.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import moldmod.SporesShadows;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.List;

public class ScrapingRecipeCategory extends AbstractTwoInputRecipeCategory<ScrapingRecipe> {

    public static final RecipeType<ScrapingRecipe> RECIPE_TYPE = RecipeType.create(SporesShadows.MOD_ID, "scraping", ScrapingRecipe.class);

    private static final List<ItemStack> AXES = List.of(
            new ItemStack(Items.WOODEN_AXE),
            new ItemStack(Items.STONE_AXE),
            new ItemStack(Items.IRON_AXE),
            new ItemStack(Items.GOLDEN_AXE),
            new ItemStack(Items.DIAMOND_AXE),
            new ItemStack(Items.NETHERITE_AXE)
    );

    public ScrapingRecipeCategory(IGuiHelper guiHelper) {
        super(guiHelper, new ItemStack(Items.IRON_AXE), "jei.spores--shadows.category.scraping");
    }

    @Override
    public RecipeType<ScrapingRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ScrapingRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 5, 11).addItemStack(recipe.input());
        builder.addSlot(RecipeIngredientRole.CATALYST, 36, 11).addIngredients(VanillaTypes.ITEM_STACK, AXES);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 99, 11).addItemStack(recipe.output());
    }
}
