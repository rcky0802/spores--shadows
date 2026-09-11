package moldmod.integration.jade;

import moldmod.SporesShadows;
import moldmod.block.dehumidifier.DehumidifierBlock;
import moldmod.block.dehumidifier.DehumidifierBlockEntity;
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

public enum DehumidifierBlockProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    INSTANCE;

    @Override
    public boolean shouldRequestData(BlockAccessor accessor) {
        BlockState state = accessor.getBlockState();
        return state != null && state.getBlock() instanceof DehumidifierBlock;
    }

    @Override
    public void appendServerData(NbtCompound nbt, BlockAccessor accessor) {
        BlockEntity be = accessor.getBlockEntity();
        if (be instanceof DehumidifierBlockEntity dehumBe) {
            nbt.putInt("WaterMb", dehumBe.getWaterMb());
            nbt.putInt("CapacityMb", dehumBe.getCapacityMb());
            nbt.putString("Status", dehumBe.getStatus().asString());
            nbt.putString("Mode", dehumBe.getMode().asString());
        }
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        BlockState state = accessor.getBlockState();
        if (state == null || !(state.getBlock() instanceof DehumidifierBlock)) {
            return;
        }

        NbtCompound serverData = accessor.getServerData();
        String statusStr = serverData.getString("Status");
        if (statusStr.isEmpty() && state.contains(DehumidifierBlock.STATUS)) {
            statusStr = state.get(DehumidifierBlock.STATUS).asString();
        }

        String modeStr = serverData.getString("Mode");
        if (modeStr.isEmpty() && state.contains(DehumidifierBlock.MODE)) {
            modeStr = state.get(DehumidifierBlock.MODE).asString();
        }

        int waterMb = serverData.getInt("WaterMb");
        int capacityMb = serverData.contains("CapacityMb") ? serverData.getInt("CapacityMb") : 2000;

        Text statusText;
        if ("humidify".equalsIgnoreCase(modeStr)) {
            if ("running".equalsIgnoreCase(statusStr)) {
                statusText = Text.translatable("tooltip.spores--shadows.jade.dehumidifier.status.humidifying").formatted(Formatting.AQUA);
            } else if (waterMb <= 0) {
                statusText = Text.translatable("tooltip.spores--shadows.jade.dehumidifier.status.water_empty").formatted(Formatting.GOLD);
            } else {
                statusText = Text.translatable("tooltip.spores--shadows.jade.dehumidifier.status.off").formatted(Formatting.GRAY);
            }
        } else {
            statusText = switch (statusStr) {
                case "running" -> Text.translatable("tooltip.spores--shadows.jade.dehumidifier.status.running").formatted(Formatting.GREEN);
                case "full" -> Text.translatable("tooltip.spores--shadows.jade.dehumidifier.status.full").formatted(Formatting.AQUA);
                default -> Text.translatable("tooltip.spores--shadows.jade.dehumidifier.status.off").formatted(Formatting.GRAY);
            };
        }

        Text waterText = Text.translatable("tooltip.spores--shadows.jade.dehumidifier.water", waterMb, capacityMb).formatted(Formatting.AQUA);
        tooltip.add(statusText.copy().append(Text.literal(" • ").formatted(Formatting.DARK_GRAY)).append(waterText));
    }

    @Override
    public Identifier getUid() {
        return SporesShadows.id("dehumidifier_info");
    }
}
