package moldmod.client.integration.jei;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;

@Environment(EnvType.CLIENT)
public record ScrapingRecipe(ItemStack input, ItemStack output) {
}
