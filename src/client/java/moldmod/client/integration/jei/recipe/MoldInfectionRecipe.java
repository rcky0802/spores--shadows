package moldmod.client.integration.jei.recipe;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.List;

@Environment(EnvType.CLIENT)
public record MoldInfectionRecipe(
        ItemStack input,
        ItemStack output,
        int fromStage,
        int toStage,
        Text stageTransitionText,
        Text thresholdText,
        List<Text> tooltipLines
) {
}
