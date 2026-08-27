package moldmod.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;

public class SporesShadowsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		for (net.minecraft.block.Block block : net.minecraft.registry.Registries.BLOCK) {
			if (net.minecraft.registry.Registries.BLOCK.getId(block).getNamespace().equals(moldmod.SporesShadows.MOD_ID)) {
				BlockRenderLayerMap.INSTANCE.putBlock(block, RenderLayer.getCutout());
			}
		}
	}
}
