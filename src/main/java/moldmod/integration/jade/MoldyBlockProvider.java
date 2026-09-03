package moldmod.integration.jade;

import me.shedaniel.autoconfig.AutoConfig;
import moldmod.SporesShadows;
import moldmod.block.MoldyBlock;
import moldmod.block.MoldyBlockHelper;
import moldmod.config.ModConfig;
import net.minecraft.block.BlockState;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum MoldyBlockProvider implements IBlockComponentProvider {
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        BlockState state = accessor.getBlockState();
        if (state.contains(MoldyBlock.STAGE)) {
            boolean waxed = state.contains(MoldyBlock.WAXED) && state.get(MoldyBlock.WAXED);
            
            ModConfig modConfig = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
            double risk = MoldyBlockHelper.calculateR(accessor.getLevel(), accessor.getPosition(), waxed, state);
            
            int stage = state.get(MoldyBlock.STAGE);
            // Infection risk (only if not completely healthy/waxed)
            if (stage < 3 && !waxed) {
                Formatting color = risk >= modConfig.general.infection_threshold ? Formatting.RED : Formatting.GRAY;
                int riskPercent = (int) (risk * 100);
                tooltip.add(Text.translatable("tooltip." + SporesShadows.MOD_ID + ".jade.infection", riskPercent).formatted(color));
            }

        }
    }

    @Override
    public Identifier getUid() {
        return Identifier.of(SporesShadows.MOD_ID, "moldy_info");
    }
}



