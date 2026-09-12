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
public final class DehumidifierRecipeCategory implements IRecipeCategory<DehumidifierRecipe> {

    public static final RecipeType<DehumidifierRecipe> RECIPE_TYPE =
            RecipeType.create(SporesShadows.MOD_ID, "dehumidifying", DehumidifierRecipe.class);

    private final IDrawable icon;
    private final IDrawable slot;
    private final IDrawableAnimated arrow;
    private final Text title;

    public DehumidifierRecipeCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ModBlocks.DEHUMIDIFIER));
        this.slot = guiHelper.getSlotDrawable();
        this.arrow = guiHelper.createAnimatedRecipeArrow(40);
        this.title = Text.translatable("jei." + SporesShadows.MOD_ID + ".category.dehumidifier");
    }

    @Override
    public RecipeType<DehumidifierRecipe> getRecipeType() {
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
    public void setRecipe(IRecipeLayoutBuilder builder, DehumidifierRecipe recipe, IFocusGroup focuses) {
        // Slot 1: Input contenitore / fluido (x=7, y=17)
        if (!recipe.input().isEmpty()) {
            builder.addSlot(RecipeIngredientRole.INPUT, 7, 17).addItemStack(recipe.input());
        }
        // Slot 2: Catalizzatore Deumidificatore (x=35, y=17)
        builder.addSlot(RecipeIngredientRole.CATALYST, 35, 17).addItemStack(new ItemStack(ModBlocks.DEHUMIDIFIER));
        // Slot 3: Output (x=87, y=17)
        if (!recipe.output().isEmpty()) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 87, 17).addItemStack(recipe.output());
        }
        // Slot 4: Combustibile / Alimentazione (x=117, y=17)
        if (recipe.fuels() != null && !recipe.fuels().isEmpty()) {
            builder.addSlot(RecipeIngredientRole.INPUT, 117, 17).addItemStacks(recipe.fuels());
        }
    }

    @Override
    public void draw(DehumidifierRecipe recipe, IRecipeSlotsView recipeSlotsView, DrawContext guiGraphics, double mouseX, double mouseY) {
        // Disegna slot grafici
        slot.draw(guiGraphics, 6, 16);
        slot.draw(guiGraphics, 34, 16);
        arrow.draw(guiGraphics, 58, 17);
        slot.draw(guiGraphics, 86, 16);
        slot.draw(guiGraphics, 116, 16);

        // Testi compatti e contenuti all'interno dell'inquadratura (0..142 px)
        var textRenderer = MinecraftClient.getInstance().textRenderer;
        if (textRenderer != null) {
            // Riga superiore (y=4): Modalità a sinistra, Tasso allineato a destra
            guiGraphics.drawText(textRenderer, recipe.shortModeTitle(), 6, 4, 0x1B6CA8, false);
            int rateX = 136 - textRenderer.getWidth(recipe.shortRateText());
            guiGraphics.drawText(textRenderer, recipe.shortRateText(), Math.max(65, rateX), 4, 0x27AE60, false);

            // Riga inferiore (y=42): Capacità serbatoio a sinistra, Potenza allineata a destra
            guiGraphics.drawText(textRenderer, recipe.shortTankText(), 6, 42, 0x555555, false);
            int pwrX = 136 - textRenderer.getWidth(recipe.shortPowerText());
            guiGraphics.drawText(textRenderer, recipe.shortPowerText(), Math.max(75, pwrX), 42, 0x7F8C8D, false);
        }
    }

    @Override
    public void getTooltip(mezz.jei.api.gui.builder.ITooltipBuilder tooltip, DehumidifierRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        // Mostra tooltip dettagliato passando con il mouse sopra l'area della ricetta (particolarmente sulla freccia)
        if (mouseX >= 56 && mouseX <= 84 && mouseY >= 14 && mouseY <= 34) {
            tooltip.addAll(recipe.tooltipLines());
        }
    }
}
