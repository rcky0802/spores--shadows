package moldmod.client.integration.jei;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.List;

@Environment(EnvType.CLIENT)
public record DehumidifierRecipe(
    ItemStack input,
    List<ItemStack> fuels,
    ItemStack output,
    Text shortModeTitle,
    Text shortRateText,
    Text shortTankText,
    Text shortPowerText,
    List<Text> tooltipLines
) {}
