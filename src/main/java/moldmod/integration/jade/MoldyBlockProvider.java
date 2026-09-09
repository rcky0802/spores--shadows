package moldmod.integration.jade;

import me.shedaniel.autoconfig.AutoConfig;
import moldmod.SporesShadows;
import moldmod.atmosphere.FlowDistributor;
import moldmod.block.MoldyBlock;
import moldmod.config.ModConfig;
import moldmod.risk.MoldRiskCalculator;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum MoldyBlockProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    INSTANCE;

    @Override
    public boolean shouldRequestData(BlockAccessor accessor) {
        BlockState state = accessor.getBlockState();
        if (state == null || state.isAir()) {
            return false;
        }
        if (state.contains(MoldyBlock.STAGE)) {
            boolean waxed = state.contains(MoldyBlock.WAXED) && state.get(MoldyBlock.WAXED);
            int stage = state.get(MoldyBlock.STAGE);
            return stage < 3 && !waxed;
        }
        return FlowDistributor.isSusceptibleOrMoldy(state);
    }

    @Override
    public void appendServerData(NbtCompound nbt, BlockAccessor accessor) {
        BlockState state = accessor.getBlockState();
        if (state == null || state.isAir()) {
            return;
        }
        boolean isMoldy = state.contains(MoldyBlock.STAGE);
        boolean isSusceptible = FlowDistributor.isSusceptibleOrMoldy(state);

        if (isMoldy || isSusceptible) {
            boolean waxed = state.contains(MoldyBlock.WAXED) && state.get(MoldyBlock.WAXED);
            int stage = isMoldy ? state.get(MoldyBlock.STAGE) : 0;

            if (stage < 3 && !waxed) {
                MoldRiskCalculator.MoldRiskResult result = MoldRiskCalculator.calculate(
                        accessor.getLevel(), accessor.getPosition(), waxed, state);
                nbt.putDouble("MoldRisk", result.R());
            }
        }
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        BlockState state = accessor.getBlockState();
        if (state == null || state.isAir()) {
            return;
        }
        boolean isMoldy = state.contains(MoldyBlock.STAGE);
        boolean isSusceptible = FlowDistributor.isSusceptibleOrMoldy(state);

        if (isMoldy || isSusceptible) {
            boolean waxed = state.contains(MoldyBlock.WAXED) && state.get(MoldyBlock.WAXED);
            int stage = isMoldy ? state.get(MoldyBlock.STAGE) : 0;

            // Infection risk (only if not completely healthy/waxed/rotten)
            if (stage < 3 && !waxed) {
                ModConfig modConfig = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
                double risk;
                NbtCompound serverData = accessor.getServerData();
                if (serverData != null && serverData.contains("MoldRisk")) {
                    risk = serverData.getDouble("MoldRisk");
                } else {
                    risk = MoldRiskCalculator.calculateR(accessor.getLevel(), accessor.getPosition(), waxed, state);
                }

                Formatting color = risk > modConfig.general.infection_threshold ? Formatting.RED : Formatting.GRAY;
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



