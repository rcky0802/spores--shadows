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
	@SuppressWarnings("deprecation")
	public void onInitializeClient() {
		for (Block block : Registries.BLOCK) {
			if (Registries.BLOCK.getId(block).getNamespace().equals(SporesShadows.MOD_ID)) {
				if (block.getDefaultState().getRenderType() == net.minecraft.block.BlockRenderType.MODEL) {
					BlockRenderLayerMap.INSTANCE.putBlock(block, RenderLayer.getCutout());
				}
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

		// Register Sign Block Entity Renderers
		net.minecraft.client.render.block.entity.BlockEntityRendererFactories.register(
			moldmod.block.entity.ModBlockEntities.MOLDY_SIGN,
			net.minecraft.client.render.block.entity.SignBlockEntityRenderer::new
		);
		net.minecraft.client.render.block.entity.BlockEntityRendererFactories.register(
			moldmod.block.entity.ModBlockEntities.MOLDY_HANGING_SIGN,
			net.minecraft.client.render.block.entity.HangingSignBlockEntityRenderer::new
		);

		// Register Chest Block Entity Renderers
		net.minecraft.client.render.block.entity.BlockEntityRendererFactories.register(
			moldmod.block.entity.ModBlockEntities.MOLDY_CHEST,
			net.minecraft.client.render.block.entity.ChestBlockEntityRenderer::new
		);
		net.minecraft.client.render.block.entity.BlockEntityRendererFactories.register(
			moldmod.block.entity.ModBlockEntities.MOLDY_TRAPPED_CHEST,
			net.minecraft.client.render.block.entity.ChestBlockEntityRenderer::new
		);
		net.minecraft.client.render.block.entity.BlockEntityRendererFactories.register(
			moldmod.block.entity.ModBlockEntities.MOLDY_LECTERN,
			net.minecraft.client.render.block.entity.LecternBlockEntityRenderer::new
		);

		// Register Chest BuiltinItemRenderers for inventory/hand 3D model rendering
		net.minecraft.util.math.BlockPos origin = net.minecraft.util.math.BlockPos.ORIGIN;
		moldmod.block.entity.MoldyChestBlockEntity dummyChest = new moldmod.block.entity.MoldyChestBlockEntity(origin, moldmod.block.ModBlocks.MOLDY_CHEST.getDefaultState());
		moldmod.block.entity.MoldyTrappedChestBlockEntity dummyTrappedChest = new moldmod.block.entity.MoldyTrappedChestBlockEntity(origin, moldmod.block.ModBlocks.MOLDY_TRAPPED_CHEST.getDefaultState());

		for (net.minecraft.item.Item item : moldmod.block.ModBlocks.MOLDY_ITEMS_BY_BLOCK.get(moldmod.block.ModBlocks.MOLDY_CHEST)) {
			net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry.INSTANCE.register(item, (stack, mode, matrices, vertexConsumers, light, overlay) -> {
				int stage = 0;
				boolean waxed = false;
				net.minecraft.component.type.BlockStateComponent comp = stack.get(net.minecraft.component.DataComponentTypes.BLOCK_STATE);
				if (comp != null) {
					Integer s = comp.getValue(moldmod.block.MoldyBlock.STAGE);
					if (s != null) stage = s;
					Boolean w = comp.getValue(moldmod.block.MoldyBlock.WAXED);
					if (w != null) waxed = w;
				}
				dummyChest.setMoldStage(stage);
				dummyChest.setMoldWaxed(waxed);
				dummyChest.setCachedState(moldmod.block.ModBlocks.MOLDY_CHEST.getDefaultState().with(moldmod.block.MoldyBlock.STAGE, stage).with(moldmod.block.MoldyBlock.WAXED, waxed));
				net.minecraft.client.MinecraftClient.getInstance().getBlockEntityRenderDispatcher().renderEntity(dummyChest, matrices, vertexConsumers, light, overlay);
			});
		}

		for (net.minecraft.item.Item item : moldmod.block.ModBlocks.MOLDY_ITEMS_BY_BLOCK.get(moldmod.block.ModBlocks.MOLDY_TRAPPED_CHEST)) {
			net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry.INSTANCE.register(item, (stack, mode, matrices, vertexConsumers, light, overlay) -> {
				int stage = 0;
				boolean waxed = false;
				net.minecraft.component.type.BlockStateComponent comp = stack.get(net.minecraft.component.DataComponentTypes.BLOCK_STATE);
				if (comp != null) {
					Integer s = comp.getValue(moldmod.block.MoldyBlock.STAGE);
					if (s != null) stage = s;
					Boolean w = comp.getValue(moldmod.block.MoldyBlock.WAXED);
					if (w != null) waxed = w;
				}
				dummyTrappedChest.setMoldStage(stage);
				dummyTrappedChest.setMoldWaxed(waxed);
				dummyTrappedChest.setCachedState(moldmod.block.ModBlocks.MOLDY_TRAPPED_CHEST.getDefaultState().with(moldmod.block.MoldyBlock.STAGE, stage).with(moldmod.block.MoldyBlock.WAXED, waxed));
				net.minecraft.client.MinecraftClient.getInstance().getBlockEntityRenderDispatcher().renderEntity(dummyTrappedChest, matrices, vertexConsumers, light, overlay);
			});
		}
	}
}
