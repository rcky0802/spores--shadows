package moldmod.client.bot.scenario;

import net.minecraft.client.MinecraftClient;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public interface BotScenario {
    String getName();
    String getScreenshotFilename();
    default int getWaitTicks() { return 45; }
    void prepare(ServerWorld world, ServerPlayerEntity player, MinecraftClient client);
    default void onCapture(ServerWorld world, ServerPlayerEntity player, MinecraftClient client) {}
    default void cleanup(ServerWorld world, ServerPlayerEntity player, MinecraftClient client) {}
}
