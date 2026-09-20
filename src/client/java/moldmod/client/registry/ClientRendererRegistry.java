package moldmod.client.registry;

import moldmod.SporesShadows;
import moldmod.block.ModBlocks;
import moldmod.block.core.MoldyBlock;
import moldmod.block.entity.ModBlockEntities;
import moldmod.block.workstation.MoldyChestBlockEntity;
import moldmod.block.workstation.MoldyTrappedChestBlockEntity;
import moldmod.client.cache.ClientMoistureCache;
import moldmod.client.cache.ClientToxicityCache;
import moldmod.client.render.armor.SporeMaskArmorRenderer;
import moldmod.client.render.model.SporeMaskModel;
import moldmod.item.ModItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.block.entity.ChestBlockEntityRenderer;
import net.minecraft.client.render.block.entity.HangingSignBlockEntityRenderer;
import net.minecraft.client.render.block.entity.LecternBlockEntityRenderer;
import net.minecraft.client.render.block.entity.SignBlockEntityRenderer;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BlockStateComponent;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public final class ClientRendererRegistry {

    private ClientRendererRegistry() {}

    public static void registerAll() {
        registerBlockRenderLayers();
        registerArmorAndEntityModels();
        registerModelPredicates();
        registerBlockEntityRenderers();
        registerItemRenderers();
    }

    public static void registerBlockRenderLayers() {
        for (Block block : Registries.BLOCK) {
            if (Registries.BLOCK.getId(block).getNamespace().equals(SporesShadows.MOD_ID)) {
                if (block.getDefaultState().getRenderType() == BlockRenderType.MODEL) {
                    BlockRenderLayerMap.INSTANCE.putBlock(block, RenderLayer.getCutout());
                }
            }
        }
    }

    public static void registerArmorAndEntityModels() {
        EntityModelLayerRegistry.registerModelLayer(
            SporeMaskModel.LAYER_LOCATION,
            SporeMaskModel::getTexturedModelData
        );

        ArmorRenderer.register(
            new SporeMaskArmorRenderer(),
            ModItems.SPORE_MASK
        );
    }

    public static void registerModelPredicates() {
        ModelPredicateProviderRegistry.register(
            ModItems.SPORE_DETECTOR,
            SporesShadows.id("toxicity"),
            (stack, world, entity, seed) -> ClientToxicityCache.getToxicity(entity)
        );

        ModelPredicateProviderRegistry.register(
            ModItems.MOISTURE_DETECTOR,
            SporesShadows.id("moisture"),
            (stack, world, entity, seed) -> ClientMoistureCache.getMoisture(entity)
        );
    }

    public static void registerBlockEntityRenderers() {
        BlockEntityRendererFactories.register(
            ModBlockEntities.MOLDY_SIGN,
            SignBlockEntityRenderer::new
        );
        BlockEntityRendererFactories.register(
            ModBlockEntities.MOLDY_HANGING_SIGN,
            HangingSignBlockEntityRenderer::new
        );
        BlockEntityRendererFactories.register(
            ModBlockEntities.MOLDY_CHEST,
            ChestBlockEntityRenderer::new
        );
        BlockEntityRendererFactories.register(
            ModBlockEntities.MOLDY_TRAPPED_CHEST,
            ChestBlockEntityRenderer::new
        );
        BlockEntityRendererFactories.register(
            ModBlockEntities.MOLDY_LECTERN,
            LecternBlockEntityRenderer::new
        );
    }

    @SuppressWarnings("deprecation")
    public static void registerItemRenderers() {
        BlockPos origin = BlockPos.ORIGIN;
        MoldyChestBlockEntity dummyChest = new MoldyChestBlockEntity(origin, ModBlocks.MOLDY_CHEST.getDefaultState());
        MoldyTrappedChestBlockEntity dummyTrappedChest = new MoldyTrappedChestBlockEntity(origin, ModBlocks.MOLDY_TRAPPED_CHEST.getDefaultState());

        for (Item item : ModBlocks.MOLDY_ITEMS_BY_BLOCK.get(ModBlocks.MOLDY_CHEST)) {
            BuiltinItemRendererRegistry.INSTANCE.register(item, (stack, mode, matrices, vertexConsumers, light, overlay) -> {
                int stage = 0;
                boolean waxed = false;
                BlockStateComponent comp = stack.get(DataComponentTypes.BLOCK_STATE);
                if (comp != null) {
                    Integer s = comp.getValue(MoldyBlock.STAGE);
                    if (s != null) stage = s;
                    Boolean w = comp.getValue(MoldyBlock.WAXED);
                    if (w != null) waxed = w;
                }
                dummyChest.setMoldStage(stage);
                dummyChest.setMoldWaxed(waxed);
                dummyChest.setCachedState(ModBlocks.MOLDY_CHEST.getDefaultState().with(MoldyBlock.STAGE, stage).with(MoldyBlock.WAXED, waxed));
                MinecraftClient.getInstance().getBlockEntityRenderDispatcher().renderEntity(dummyChest, matrices, vertexConsumers, light, overlay);
            });
        }

        for (Item item : ModBlocks.MOLDY_ITEMS_BY_BLOCK.get(ModBlocks.MOLDY_TRAPPED_CHEST)) {
            BuiltinItemRendererRegistry.INSTANCE.register(item, (stack, mode, matrices, vertexConsumers, light, overlay) -> {
                int stage = 0;
                boolean waxed = false;
                BlockStateComponent comp = stack.get(DataComponentTypes.BLOCK_STATE);
                if (comp != null) {
                    Integer s = comp.getValue(MoldyBlock.STAGE);
                    if (s != null) stage = s;
                    Boolean w = comp.getValue(MoldyBlock.WAXED);
                    if (w != null) waxed = w;
                }
                dummyTrappedChest.setMoldStage(stage);
                dummyTrappedChest.setMoldWaxed(waxed);
                dummyTrappedChest.setCachedState(ModBlocks.MOLDY_TRAPPED_CHEST.getDefaultState().with(MoldyBlock.STAGE, stage).with(MoldyBlock.WAXED, waxed));
                MinecraftClient.getInstance().getBlockEntityRenderDispatcher().renderEntity(dummyTrappedChest, matrices, vertexConsumers, light, overlay);
            });
        }
    }
}
