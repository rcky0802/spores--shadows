package moldmod.network;

import moldmod.SporesShadows;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;

public record AirPurifierRedstonePayload(BlockPos pos) implements CustomPayload {

    public static final Id<AirPurifierRedstonePayload> ID = new Id<>(SporesShadows.id("air_purifier_redstone"));
    public static final PacketCodec<RegistryByteBuf, AirPurifierRedstonePayload> CODEC = PacketCodec.tuple(
            BlockPos.PACKET_CODEC, AirPurifierRedstonePayload::pos,
            AirPurifierRedstonePayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
