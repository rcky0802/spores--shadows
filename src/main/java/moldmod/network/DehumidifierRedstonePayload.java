package moldmod.network;

import moldmod.SporesShadows;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;

public record DehumidifierRedstonePayload(BlockPos pos) implements CustomPayload {

    public static final Id<DehumidifierRedstonePayload> ID = new Id<>(SporesShadows.id("dehumidifier_redstone"));
    public static final PacketCodec<RegistryByteBuf, DehumidifierRedstonePayload> CODEC = PacketCodec.tuple(
            BlockPos.PACKET_CODEC, DehumidifierRedstonePayload::pos,
            DehumidifierRedstonePayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
