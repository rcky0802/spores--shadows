package moldmod.integration.jade;

import moldmod.SporesShadows;
import moldmod.block.MoistureDetectorBlock;
import net.minecraft.block.BlockState;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum MoistureDetectorBlockProvider implements IBlockComponentProvider {
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        BlockState state = accessor.getBlockState();
        if (!(state.getBlock() instanceof MoistureDetectorBlock)) {
            return;
        }

        int stage = state.get(MoistureDetectorBlock.MOISTURE_STAGE);

        Text stageText = switch (stage) {
            case 0 -> Text.translatable("tooltip.spores--shadows.jade.moisture_detector.dry").formatted(Formatting.GREEN);
            case 1 -> Text.translatable("tooltip.spores--shadows.jade.moisture_detector.moderate").formatted(Formatting.YELLOW);
            case 2 -> Text.translatable("tooltip.spores--shadows.jade.moisture_detector.humid").formatted(Formatting.GOLD);
            case 3 -> Text.translatable("tooltip.spores--shadows.jade.moisture_detector.critical").formatted(Formatting.RED);
            default -> Text.translatable("tooltip.spores--shadows.jade.unknown").formatted(Formatting.GRAY);
        };

        tooltip.add(Text.translatable("tooltip.spores--shadows.jade.moisture_detector.effective_moisture").formatted(Formatting.GRAY).append(stageText));
    }

    @Override
    public Identifier getUid() {
        return SporesShadows.id("moisture_detector_info");
    }
}
