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

public enum MoldyBlockProvider implements IBlockComponentProvider {
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        BlockState state = accessor.getBlockState();
        if (state.contains(MoldyLogBlock.STAGE)) {
            boolean waxed = state.contains(MoldyLogBlock.WAXED) && state.get(MoldyLogBlock.WAXED);
            
            moldmod.config.ModConfig modConfig = me.shedaniel.autoconfig.AutoConfig.getConfigHolder(moldmod.config.ModConfig.class).getConfig();
            double risk = MoldyBlockHelper.calculateR(accessor.getLevel(), accessor.getPosition(), waxed, state);
            
            int stage = state.get(MoldyLogBlock.STAGE);
            // Infection risk (only if not completely healthy/waxed)
            if (stage < 3 && !waxed) {
                net.minecraft.util.Formatting color = risk >= modConfig.general.infection_threshold ? net.minecraft.util.Formatting.RED : net.minecraft.util.Formatting.GRAY;
                int riskPercent = (int) (risk * 100);
                tooltip.add(Text.translatable("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.infection", riskPercent).formatted(color));
            }

        }
    }

    @Override
    public Identifier getUid() {
        return Identifier.of(moldmod.SporesShadows.MOD_ID, "moldy_info");
    }
}



