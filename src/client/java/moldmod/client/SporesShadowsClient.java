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

		// Register custom 3D Gas Mask entity model and ArmorRenderer
		net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry.registerModelLayer(
			moldmod.client.render.SporeMaskModel.LAYER_LOCATION,
			moldmod.client.render.SporeMaskModel::getTexturedModelData
		);

		net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer.register(
			new moldmod.client.render.SporeMaskArmorRenderer(),
			moldmod.item.ModItems.SPORE_MASK
		);
	}
}
