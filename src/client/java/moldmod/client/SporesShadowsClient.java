package moldmod.client;

import moldmod.client.registry.ClientRendererRegistry;
import moldmod.client.registry.ClientScreenRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class SporesShadowsClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		ClientScreenRegistry.registerScreens();
		ClientRendererRegistry.registerAll();

		if (Boolean.getBoolean("moldmod.bot") || "true".equalsIgnoreCase(System.getenv("MOLDMOD_BOT"))) {
			moldmod.client.bot.ScreenshotBot.initialize();
		}
	}
}
