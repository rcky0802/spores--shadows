package moldmod.client.integration.jei;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;

@Environment(EnvType.CLIENT)
public record WaxingRecipe(ItemStack input, ItemStack output) {
}
