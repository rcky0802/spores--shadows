package moldmod.integration.jade;

import moldmod.SporesShadows;
import moldmod.block.purifier.AirPurifierBlock;
import moldmod.block.purifier.AirPurifierBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum AirPurifierBlockProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    INSTANCE;

    @Override
    public boolean shouldRequestData(BlockAccessor accessor) {
        BlockState state = accessor.getBlockState();
        return state != null && state.getBlock() instanceof AirPurifierBlock;
    }

    @Override
    public void appendServerData(NbtCompound nbt, BlockAccessor accessor) {
        BlockEntity be = accessor.getBlockEntity();
        if (be instanceof AirPurifierBlockEntity purifierBe) {
            nbt.putInt("FilterWear", purifierBe.getFilterWearTicks());
            nbt.putInt("FilterMaxWear", purifierBe.getFilterDurabilityTicks());
            nbt.putInt("FilterCount", purifierBe.getFilterCount());
            nbt.putString("Status", purifierBe.getStatus().asString());
        }
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        BlockState state = accessor.getBlockState();
        if (state == null || !(state.getBlock() instanceof AirPurifierBlock)) {
            return;
        }

        NbtCompound serverData = accessor.getServerData();
        String statusStr = serverData.getString("Status");
        if (statusStr.isEmpty() && state.contains(AirPurifierBlock.STATUS)) {
            statusStr = state.get(AirPurifierBlock.STATUS).asString();
        }

        int wear = serverData.getInt("FilterWear");
        int maxWear = serverData.contains("FilterMaxWear") ? serverData.getInt("FilterMaxWear") : 2400;
        int filterCount = serverData.getInt("FilterCount");
        int pct = (maxWear > 0) ? (int) Math.round(((double) wear / maxWear) * 100.0) : 0;

        Text statusText = switch (statusStr) {
            case "running" -> Text.translatable("tooltip.spores--shadows.jade.air_purifier.status.running").formatted(Formatting.GREEN);
            case "filter_depleted" -> Text.translatable("tooltip.spores--shadows.jade.air_purifier.status.filter_depleted").formatted(Formatting.GOLD);
            default -> Text.translatable("tooltip.spores--shadows.jade.air_purifier.status.off").formatted(Formatting.GRAY);
        };

        Formatting filterColor = (pct > 50) ? Formatting.GREEN : (pct > 20 ? Formatting.YELLOW : Formatting.RED);
        Text filterText = Text.translatable("tooltip.spores--shadows.jade.air_purifier.filter", pct).formatted(filterColor);
        if (filterCount > 0) {
            filterText = filterText.copy().append(Text.literal(" (+" + filterCount + ")").formatted(Formatting.DARK_GREEN));
        }

        tooltip.add(statusText.copy().append(Text.literal(" • ").formatted(Formatting.DARK_GRAY)).append(filterText));
    }

    @Override
    public Identifier getUid() {
        return SporesShadows.id("air_purifier_info");
    }
}
