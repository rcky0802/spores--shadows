package moldmod.integration.jade;

import moldmod.block.MoldyLogBlock;
import moldmod.block.MoldyBlockHelper;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import net.minecraft.block.BlockState;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Formatting;

public enum MoldyBlockProvider implements IBlockComponentProvider {
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        BlockState state = accessor.getBlockState();
        if (state.contains(MoldyLogBlock.STAGE)) {
            int stage = state.get(MoldyLogBlock.STAGE);
            boolean waxed = state.contains(MoldyLogBlock.WAXED) && state.get(MoldyLogBlock.WAXED);
            
            String stageKey = "tooltip.spores--shadows.jade.stage." + stage;

            moldmod.config.ModConfig modConfig = me.shedaniel.autoconfig.AutoConfig.getConfigHolder(moldmod.config.ModConfig.class).getConfig();
            double rValue = MoldyBlockHelper.calculateR(accessor.getLevel(), accessor.getPosition(), false, state);
            int riskPercent = Math.min(100, (int)Math.round((rValue / modConfig.general.infection_threshold) * 100));
            
            if (stage == 3) {
                riskPercent = 100;
            }

            tooltip.add(Text.translatable("tooltip.spores--shadows.jade.stage").append(Text.translatable(stageKey).formatted(
                stage == 0 ? Formatting.GREEN :
                stage == 1 ? Formatting.YELLOW :
                stage == 2 ? Formatting.GOLD :
                Formatting.RED
            )));
            
            if (stage < 3) {
                tooltip.add(Text.translatable("tooltip.spores--shadows.jade.infection", riskPercent));
            }
            
            tooltip.add(Text.translatable("tooltip.spores--shadows.jade.waxed").append(Text.translatable(waxed ? "tooltip.spores--shadows.jade.yes" : "tooltip.spores--shadows.jade.no").formatted(
                waxed ? Formatting.AQUA : Formatting.GRAY
            )));
        }
    }

    @Override
    public Identifier getUid() {
        return Identifier.of("spores--shadows", "moldy_info");
    }
}
