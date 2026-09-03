package moldmod.client;

import moldmod.SporesShadows;
import moldmod.client.render.SporeMaskArmorRenderer;
import moldmod.client.render.SporeMaskModel;
import moldmod.item.ModItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.registry.Registries;

@Environment(EnvType.CLIENT)
public class SporesShadowsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		for (Block block : Registries.BLOCK) {
			if (Registries.BLOCK.getId(block).getNamespace().equals(SporesShadows.MOD_ID)) {
				BlockRenderLayerMap.INSTANCE.putBlock(block, RenderLayer.getCutout());
			}
		}

		// Register custom 3D Gas Mask entity model and ArmorRenderer
		EntityModelLayerRegistry.registerModelLayer(
			SporeMaskModel.LAYER_LOCATION,
			SporeMaskModel::getTexturedModelData
		);

		ArmorRenderer.register(
			new SporeMaskArmorRenderer(),
			ModItems.SPORE_MASK
		);
	}
}
