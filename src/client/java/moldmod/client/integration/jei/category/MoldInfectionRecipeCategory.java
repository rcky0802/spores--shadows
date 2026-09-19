package moldmod.client.integration.jei.category;

import moldmod.client.integration.jei.recipe.*;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import moldmod.SporesShadows;
import moldmod.item.ModItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;

import java.util.List;

@Environment(EnvType.CLIENT)
public final class MoldInfectionRecipeCategory implements IRecipeCategory<MoldInfectionRecipe> {

    public static final RecipeType<MoldInfectionRecipe> RECIPE_TYPE =
            RecipeType.create(SporesShadows.MOD_ID, "mold_infection", MoldInfectionRecipe.class);

    private final IDrawable icon;
    private final IDrawable slot;
    private final IDrawableAnimated arrow;
    private final Text title;

    public MoldInfectionRecipeCategory(IGuiHelper guiHelper) {
        Item iconItem = Registries.ITEM.get(SporesShadows.id("moldy_oak_planks"));
        ItemStack iconStack = (iconItem != Items.AIR)
                ? new ItemStack(iconItem)
                : new ItemStack(ModItems.MOISTURE_DETECTOR);
        this.icon = guiHelper.createDrawableItemStack(iconStack);
        this.slot = guiHelper.getSlotDrawable();
        this.arrow = guiHelper.createAnimatedRecipeArrow(40);
        this.title = Text.translatable("jei." + SporesShadows.MOD_ID + ".category.mold_infection");
    }

    @Override
    public RecipeType<MoldInfectionRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Text getTitle() {
        return this.title;
    }

    @Override
    public int getWidth() {
        return 142;
    }

    @Override
    public int getHeight() {
        return 52;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, MoldInfectionRecipe recipe, IFocusGroup focuses) {
        // Slot 1: Input (Current Block)
        builder.addSlot(RecipeIngredientRole.INPUT, 12, 17).addItemStack(recipe.input());

        // Slot 2: Measurement & Diagnostic Tools (Catalyst/Condition indicator)
        builder.addSlot(RecipeIngredientRole.CATALYST, 44, 17)
                .addItemStacks(List.of(
                        new ItemStack(ModItems.MOISTURE_DETECTOR),
                        new ItemStack(ModItems.SPORE_DETECTOR)
                ));

        // Slot 3: Output (Next Mold Stage)
        builder.addSlot(RecipeIngredientRole.OUTPUT, 108, 17).addItemStack(recipe.output());
    }

    @Override
    public void draw(MoldInfectionRecipe recipe, IRecipeSlotsView recipeSlotsView, DrawContext guiGraphics, double mouseX, double mouseY) {
        slot.draw(guiGraphics, 11, 16);
        slot.draw(guiGraphics, 43, 16);
        arrow.draw(guiGraphics, 71, 17);
        slot.draw(guiGraphics, 107, 16);

        var textRenderer = MinecraftClient.getInstance().textRenderer;
        if (textRenderer != null) {
            // Riga superiore: Transizione a sinistra, Soglia di rischio a destra
            guiGraphics.drawText(textRenderer, recipe.stageTransitionText(), 6, 4, 0x2C3E50, false);
            int threshX = 136 - textRenderer.getWidth(recipe.thresholdText());
            guiGraphics.drawText(textRenderer, recipe.thresholdText(), Math.max(70, threshX), 4, 0xC0392B, false);

            // Riga inferiore: Nota su random tick e cera
            Text noteText = Text.translatable("jei." + SporesShadows.MOD_ID + ".infection.note");
            guiGraphics.drawText(textRenderer, noteText, 6, 40, 0x7F8C8D, false);
        }
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, MoldInfectionRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if (mouseX >= 40 && mouseX <= 100 && mouseY >= 14 && mouseY <= 36) {
            tooltip.addAll(recipe.tooltipLines());
        }
    }
}
