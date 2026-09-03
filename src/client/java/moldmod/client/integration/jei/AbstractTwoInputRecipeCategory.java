package moldmod.client.integration.jei;

import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

/**
 * Base recipe category for two-input recipes (item + tool/catalyst -> output).
 */
@Environment(EnvType.CLIENT)
public abstract class AbstractTwoInputRecipeCategory<T> implements IRecipeCategory<T> {

    private final IDrawable icon;
    private final IDrawable slot;
    private final IDrawableAnimated arrow;
    private final Text title;

    public AbstractTwoInputRecipeCategory(IGuiHelper guiHelper, ItemStack iconStack, String titleKey) {
        this.icon = guiHelper.createDrawableItemStack(iconStack);
        this.slot = guiHelper.getSlotDrawable();
        this.arrow = guiHelper.createAnimatedRecipeArrow(40);
        this.title = Text.translatable(titleKey);
    }

    @Override
    public Text getTitle() {
        return this.title;
    }

    @Override
    public int getWidth() {
        return 124;
    }

    @Override
    public int getHeight() {
        return 38;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void draw(T recipe, IRecipeSlotsView recipeSlotsView, DrawContext guiGraphics, double mouseX, double mouseY) {
        slot.draw(guiGraphics, 4, 10);
        slot.draw(guiGraphics, 35, 10);
        slot.draw(guiGraphics, 98, 10);
        arrow.draw(guiGraphics, 62, 11);
    }
}
