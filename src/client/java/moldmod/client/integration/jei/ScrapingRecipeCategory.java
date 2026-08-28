package moldmod.client.integration.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import moldmod.SporesShadows;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

import java.util.List;

public class ScrapingRecipeCategory implements IRecipeCategory<ScrapingRecipe> {

    public static final RecipeType<ScrapingRecipe> RECIPE_TYPE = RecipeType.create(SporesShadows.MOD_ID, "scraping", ScrapingRecipe.class);

    private static final List<ItemStack> AXES = List.of(
            new ItemStack(Items.WOODEN_AXE),
            new ItemStack(Items.STONE_AXE),
            new ItemStack(Items.IRON_AXE),
            new ItemStack(Items.GOLDEN_AXE),
            new ItemStack(Items.DIAMOND_AXE),
            new ItemStack(Items.NETHERITE_AXE)
    );

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable slot;
    private final IDrawableAnimated arrow;

    public ScrapingRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(124, 38);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(Items.IRON_AXE));
        this.slot = guiHelper.getSlotDrawable();
        this.arrow = guiHelper.createAnimatedRecipeArrow(40);
    }

    @Override
    public RecipeType<ScrapingRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Text getTitle() {
        return Text.translatable("jei.spores--shadows.category.scraping");
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ScrapingRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 5, 11).addItemStack(recipe.input());
        builder.addSlot(RecipeIngredientRole.CATALYST, 36, 11).addIngredients(VanillaTypes.ITEM_STACK, AXES);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 99, 11).addItemStack(recipe.output());
    }

    @Override
    public void draw(ScrapingRecipe recipe, IRecipeSlotsView recipeSlotsView, DrawContext guiGraphics, double mouseX, double mouseY) {
        slot.draw(guiGraphics, 4, 10);
        slot.draw(guiGraphics, 35, 10);
        slot.draw(guiGraphics, 98, 10);
        arrow.draw(guiGraphics, 62, 11);
    }
}
