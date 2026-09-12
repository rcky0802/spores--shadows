package moldmod.client.integration.jei;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.List;

@Environment(EnvType.CLIENT)
public record AirPurifierRecipe(
    ItemStack filterInput,
    List<ItemStack> fuels,
    ItemStack catalyst,
    Text shortModeTitle,
    Text shortCleaningText,
    Text shortDurationText,
    Text shortRateText,
    List<Text> tooltipLines
) {}
