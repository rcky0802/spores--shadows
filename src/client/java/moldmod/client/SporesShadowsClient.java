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
	}
}
