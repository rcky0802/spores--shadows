package moldmod.network;

import moldmod.SporesShadows;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;

public record DehumidifierModePayload(BlockPos pos) implements CustomPayload {

    public static final Id<DehumidifierModePayload> ID = new Id<>(SporesShadows.id("dehumidifier_mode"));
    public static final PacketCodec<RegistryByteBuf, DehumidifierModePayload> CODEC = PacketCodec.tuple(
            BlockPos.PACKET_CODEC, DehumidifierModePayload::pos,
            DehumidifierModePayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
