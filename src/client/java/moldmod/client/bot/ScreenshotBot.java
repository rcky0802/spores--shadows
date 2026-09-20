package moldmod.client.bot;

import moldmod.client.bot.scenario.*;
import moldmod.client.bot.util.BotHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Environment(EnvType.CLIENT)
public final class ScreenshotBot {
    private static final Logger LOGGER = LoggerFactory.getLogger("ScreenshotBot");

    private static final List<BotScenario> SCENARIOS = List.of(
        new CellarScenario(),
        new CaveScenario(),
        new FiltrationScenario(),
        new ComparisonCleanScenario(),
        new ComparisonMoldyScenario(),
        new StagesEvolutionScenario(),
        new GuiDehumidifierScenario(),
        new GuiCraftingTableScenario()
    );

    private static boolean initialized = false;
    private static int scenarioIndex = 0;
    private static int tickCounter = 0;

    public static void initialize() {
        LOGGER.info("========================================");
        LOGGER.info("   MODULAR SCENARIO PHOTOGRAPHER BOT    ");
        LOGGER.info("========================================");
        ClientTickEvents.END_CLIENT_TICK.register(ScreenshotBot::onClientTick);
    }

    private static void onClientTick(MinecraftClient client) {
        if (client.world == null || client.player == null) {
            return;
        }

        MinecraftServer server = client.getServer();
        if (server == null) {
            return;
        }

        tickCounter++;

        if (!initialized) {
            if (tickCounter >= 40) {
                client.options.hudHidden = true;

                ServerPlayerEntity sPlayer = server.getPlayerManager().getPlayer(client.player.getUuid());
                if (sPlayer == null) return;
                ServerWorld sWorld = sPlayer.getServerWorld();

                sWorld.getGameRules().get(GameRules.DO_DAYLIGHT_CYCLE).set(false, server);
                sWorld.getGameRules().get(GameRules.DO_WEATHER_CYCLE).set(false, server);
                sWorld.setTimeOfDay(18000L);
                sWorld.setWeather(0, 0, false, false);

                initialized = true;
                scenarioIndex = 0;
                tickCounter = 0;

                BotScenario first = SCENARIOS.get(0);
                LOGGER.info("[ScreenshotBot] Preparing {}: {}...", 1, first.getName());
                first.prepare(sWorld, sPlayer, client);
            }
            return;
        }

        if (scenarioIndex < SCENARIOS.size()) {
            BotScenario current = SCENARIOS.get(scenarioIndex);

            if (tickCounter >= current.getWaitTicks()) {
                ServerPlayerEntity sPlayer = server.getPlayerManager().getPlayer(client.player.getUuid());
                if (sPlayer != null) {
                    ServerWorld sWorld = sPlayer.getServerWorld();
                    current.onCapture(sWorld, sPlayer, client);
                }

                LOGGER.info("[ScreenshotBot] Capturing {}: {} -> {}", scenarioIndex + 1, current.getName(), current.getScreenshotFilename());
                BotHelper.takeScreenshot(client, current.getScreenshotFilename());

                if (sPlayer != null) {
                    current.cleanup(sPlayer.getServerWorld(), sPlayer, client);
                }

                scenarioIndex++;
                tickCounter = 0;

                if (scenarioIndex < SCENARIOS.size()) {
                    BotScenario next = SCENARIOS.get(scenarioIndex);
                    if (sPlayer != null) {
                        LOGGER.info("[ScreenshotBot] Preparing {}: {}...", scenarioIndex + 1, next.getName());
                        next.prepare(sPlayer.getServerWorld(), sPlayer, client);
                    }
                } else {
                    LOGGER.info("===============================================================");
                    LOGGER.info("   [ScreenshotBot] ALL {} SCENARIOS CAPTURED! EXITING...      ", SCENARIOS.size());
                    LOGGER.info("===============================================================");
                    client.scheduleStop();
                }
            }
        }
    }
}
