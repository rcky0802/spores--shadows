package moldmod.integration.jade;

import moldmod.SporesShadows;
import moldmod.block.SporeDetectorBlock;
import net.minecraft.block.BlockState;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum SporeDetectorBlockProvider implements IBlockComponentProvider {
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        BlockState state = accessor.getBlockState();
        if (!(state.getBlock() instanceof SporeDetectorBlock)) {
            return;
        }

        int level = state.get(SporeDetectorBlock.TOXICITY_LEVEL);

        Text levelText = switch (level) {
            case 0 -> Text.translatable("tooltip.spores--shadows.jade.spore_detector.clean").formatted(Formatting.GREEN);
            case 1 -> Text.translatable("tooltip.spores--shadows.jade.spore_detector.warning").formatted(Formatting.YELLOW);
            case 2 -> Text.translatable("tooltip.spores--shadows.jade.spore_detector.moderate").formatted(Formatting.GOLD);
            case 3 -> Text.translatable("tooltip.spores--shadows.jade.spore_detector.lethal").formatted(Formatting.RED);
            default -> Text.translatable("tooltip.spores--shadows.jade.unknown").formatted(Formatting.GRAY);
        };

        tooltip.add(Text.translatable("tooltip.spores--shadows.jade.spore_detector.air_quality").formatted(Formatting.GRAY).append(levelText));
    }

    @Override
    public Identifier getUid() {
        return SporesShadows.id("spore_detector_info");
    }
}
