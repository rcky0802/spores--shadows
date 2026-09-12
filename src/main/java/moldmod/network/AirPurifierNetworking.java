package moldmod.network;

import moldmod.block.purifier.AirPurifierBlockEntity;
import moldmod.screen.AirPurifierScreenHandler;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;

public final class AirPurifierNetworking {

    private AirPurifierNetworking() {}

    public static void registerPayloads() {
        PayloadTypeRegistry.playC2S().register(AirPurifierRedstonePayload.ID, AirPurifierRedstonePayload.CODEC);
    }

    public static void registerServerReceivers() {
        ServerPlayNetworking.registerGlobalReceiver(AirPurifierRedstonePayload.ID, (payload, context) -> {
            context.server().execute(() -> {
                if (context.player().currentScreenHandler instanceof AirPurifierScreenHandler handler) {
                    if (handler.getInventory() instanceof AirPurifierBlockEntity be) {
                        be.setRedstoneMode(be.getRedstoneMode().next());
                        return;
                    }
                }
                if (context.player().getWorld() instanceof ServerWorld world) {
                    if (payload.pos() != null && world.canSetBlock(payload.pos())) {
                        BlockEntity be = world.getBlockEntity(payload.pos());
                        if (be instanceof AirPurifierBlockEntity purifierBe) {
                            purifierBe.setRedstoneMode(purifierBe.getRedstoneMode().next());
                        }
                    }
                }
            });
        });
    }
}
