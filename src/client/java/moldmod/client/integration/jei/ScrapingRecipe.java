package moldmod.client.integration.jei;

import net.minecraft.item.ItemStack;

public record ScrapingRecipe(ItemStack input, ItemStack output, String actionType) {
}
