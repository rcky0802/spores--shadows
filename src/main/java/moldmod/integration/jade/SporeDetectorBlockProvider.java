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
        int power = state.get(SporeDetectorBlock.POWER);

        Text levelText = switch (level) {
            case 0 -> Text.literal("Clean Air").formatted(Formatting.GREEN);
            case 1 -> Text.literal("Warning (Low)").formatted(Formatting.YELLOW);
            case 2 -> Text.literal("Moderate (Hunger)").formatted(Formatting.GOLD);
            case 3 -> Text.literal("Lethal (Poison)").formatted(Formatting.RED);
            default -> Text.literal("Unknown").formatted(Formatting.GRAY);
        };

        tooltip.add(Text.literal("Air Quality: ").formatted(Formatting.GRAY).append(levelText));
        if (power > 0) {
            tooltip.add(Text.literal("Redstone Signal: ").formatted(Formatting.GRAY)
                    .append(Text.literal(String.valueOf(power)).formatted(Formatting.RED)));
        }
    }

    @Override
    public Identifier getUid() {
        return SporesShadows.id("spore_detector_info");
    }
}
