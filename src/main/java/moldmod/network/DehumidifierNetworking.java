package moldmod.network;

import moldmod.block.dehumidifier.DehumidifierBlockEntity;
import moldmod.screen.DehumidifierScreenHandler;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;

public final class DehumidifierNetworking {

    private DehumidifierNetworking() {}

    public static void registerPayloads() {
        PayloadTypeRegistry.playC2S().register(DehumidifierRedstonePayload.ID, DehumidifierRedstonePayload.CODEC);
        PayloadTypeRegistry.playC2S().register(DehumidifierModePayload.ID, DehumidifierModePayload.CODEC);
    }

    public static void registerServerReceivers() {
        ServerPlayNetworking.registerGlobalReceiver(DehumidifierRedstonePayload.ID, (payload, context) -> {
            context.server().execute(() -> {
                if (context.player().currentScreenHandler instanceof DehumidifierScreenHandler handler) {
                    if (handler.getInventory() instanceof DehumidifierBlockEntity be) {
                        be.setRedstoneMode(be.getRedstoneMode().next());
                        return;
                    }
                }
                if (context.player().getWorld() instanceof ServerWorld world) {
                    if (payload.pos() != null && world.canSetBlock(payload.pos())) {
                        BlockEntity be = world.getBlockEntity(payload.pos());
                        if (be instanceof DehumidifierBlockEntity dehumidifierBe) {
                            dehumidifierBe.setRedstoneMode(dehumidifierBe.getRedstoneMode().next());
                        }
                    }
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(DehumidifierModePayload.ID, (payload, context) -> {
            context.server().execute(() -> {
                if (context.player().currentScreenHandler instanceof DehumidifierScreenHandler handler) {
                    if (handler.getInventory() instanceof DehumidifierBlockEntity be) {
                        be.setMode(be.getMode().next());
                        return;
                    }
                }
                if (context.player().getWorld() instanceof ServerWorld world) {
                    if (payload.pos() != null && world.canSetBlock(payload.pos())) {
                        BlockEntity be = world.getBlockEntity(payload.pos());
                        if (be instanceof DehumidifierBlockEntity dehumidifierBe) {
                            dehumidifierBe.setMode(dehumidifierBe.getMode().next());
                        }
                    }
                }
            });
        });
    }
}
