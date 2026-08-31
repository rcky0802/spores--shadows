package moldmod.client.integration.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import moldmod.SporesShadows;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class WaxingRecipeCategory extends AbstractTwoInputRecipeCategory<WaxingRecipe> {

    public static final RecipeType<WaxingRecipe> RECIPE_TYPE = RecipeType.create(SporesShadows.MOD_ID, "waxing", WaxingRecipe.class);

    public WaxingRecipeCategory(IGuiHelper guiHelper) {
        super(guiHelper, new ItemStack(Items.HONEYCOMB), "jei.spores--shadows.category.waxing");
    }

    @Override
    public RecipeType<WaxingRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, WaxingRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 5, 11).addItemStack(recipe.input());
        builder.addSlot(RecipeIngredientRole.CATALYST, 36, 11).addItemStack(new ItemStack(Items.HONEYCOMB));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 99, 11).addItemStack(recipe.output());
    }
}
