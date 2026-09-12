package moldmod.client;

import moldmod.SporesShadows;
import moldmod.client.cache.ClientMoistureCache;
import moldmod.client.cache.ClientToxicityCache;
import moldmod.client.render.armor.SporeMaskArmorRenderer;
import moldmod.client.render.model.SporeMaskModel;
import moldmod.item.ModItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.registry.Registries;

@Environment(EnvType.CLIENT)
public final class SporesShadowsClient implements ClientModInitializer {
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

		// Dynamic Model Predicate for Spore Detector
		ModelPredicateProviderRegistry.register(
			ModItems.SPORE_DETECTOR,
			SporesShadows.id("toxicity"),
			(stack, world, entity, seed) -> ClientToxicityCache.getToxicity(entity)
		);

		// Dynamic Model Predicate for Moisture Detector
		ModelPredicateProviderRegistry.register(
			ModItems.MOISTURE_DETECTOR,
			SporesShadows.id("moisture"),
			(stack, world, entity, seed) -> ClientMoistureCache.getMoisture(entity)
		);

		// Register Dehumidifier HandledScreen
		net.minecraft.client.gui.screen.ingame.HandledScreens.register(
			moldmod.screen.ModScreenHandlers.DEHUMIDIFIER,
			moldmod.client.screen.DehumidifierScreen::new
		);

		// Register Air Purifier HandledScreen
		net.minecraft.client.gui.screen.ingame.HandledScreens.register(
			moldmod.screen.ModScreenHandlers.AIR_PURIFIER,
			moldmod.client.screen.AirPurifierScreen::new
		);
	}
}
