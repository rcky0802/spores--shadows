package moldmod.client;

import moldmod.block.ModBlocks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;

public class SporesShadowsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		for (net.minecraft.block.Block block : moldmod.block.ModBlocks.VANILLA_TO_MOLDY.values()) {
			BlockRenderLayerMap.INSTANCE.putBlock(block, RenderLayer.getCutout());
		}
	}
}