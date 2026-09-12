package moldmod.client.integration.jei;

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
import moldmod.block.ModBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.Collections;
import java.util.List;

@Environment(EnvType.CLIENT)
public final class AirPurifierRecipeCategory implements IRecipeCategory<AirPurifierRecipe> {

    public static final RecipeType<AirPurifierRecipe> RECIPE_TYPE =
            RecipeType.create(SporesShadows.MOD_ID, "air_purification", AirPurifierRecipe.class);

    private final IDrawable icon;
    private final IDrawable slot;
    private final IDrawableAnimated arrow;
    private final Text title;

    public AirPurifierRecipeCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ModBlocks.AIR_PURIFIER));
        this.slot = guiHelper.getSlotDrawable();
        this.arrow = guiHelper.createAnimatedRecipeArrow(40);
        this.title = Text.translatable("jei." + SporesShadows.MOD_ID + ".category.air_purifier");
    }

    @Override
    public RecipeType<AirPurifierRecipe> getRecipeType() {
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
        return 54;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, AirPurifierRecipe recipe, IFocusGroup focuses) {
        // Slot 1: Filtro Antispore (x=15, y=17)
        if (!recipe.filterInput().isEmpty()) {
            builder.addSlot(RecipeIngredientRole.INPUT, 15, 17).addItemStack(recipe.filterInput());
        }
        // Slot 2: Catalizzatore Depuratore (x=47, y=17)
        builder.addSlot(RecipeIngredientRole.CATALYST, 47, 17).addItemStack(recipe.catalyst());
        // Slot 3: Combustibile / Alimentazione (x=109, y=17)
        if (recipe.fuels() != null && !recipe.fuels().isEmpty()) {
            builder.addSlot(RecipeIngredientRole.INPUT, 109, 17).addItemStacks(recipe.fuels());
        }
    }

    @Override
    public void draw(AirPurifierRecipe recipe, IRecipeSlotsView recipeSlotsView, DrawContext guiGraphics, double mouseX, double mouseY) {
        // Disegna slot grafici
        slot.draw(guiGraphics, 14, 16);
        slot.draw(guiGraphics, 46, 16);
        arrow.draw(guiGraphics, 72, 17);
        slot.draw(guiGraphics, 108, 16);

        // Testi compatti contenuti all'interno dell'inquadratura (0..142 px)
        var textRenderer = MinecraftClient.getInstance().textRenderer;
        if (textRenderer != null) {
            // Riga superiore (y=4): Modalità a sinistra, Bonifica a destra
            guiGraphics.drawText(textRenderer, recipe.shortModeTitle(), 6, 4, 0x1E824C, false);
            int cleanX = 136 - textRenderer.getWidth(recipe.shortCleaningText());
            guiGraphics.drawText(textRenderer, recipe.shortCleaningText(), Math.max(70, cleanX), 4, 0x27AE60, false);

            // Riga inferiore (y=42): Durata filtro a sinistra, Tasso consumo a destra
            guiGraphics.drawText(textRenderer, recipe.shortDurationText(), 6, 42, 0x2C3E50, false);
            int rateX = 136 - textRenderer.getWidth(recipe.shortRateText());
            guiGraphics.drawText(textRenderer, recipe.shortRateText(), Math.max(80, rateX), 42, 0xD35400, false);
        }
    }

    @Override
    public void getTooltip(mezz.jei.api.gui.builder.ITooltipBuilder tooltip, AirPurifierRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        // Mostra tooltip dettagliato passando con il mouse sopra l'area centrale o della freccia
        if (mouseX >= 70 && mouseX <= 98 && mouseY >= 14 && mouseY <= 34) {
            tooltip.addAll(recipe.tooltipLines());
        }
    }
}
