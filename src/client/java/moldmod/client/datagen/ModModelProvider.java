package moldmod.client.datagen;

import moldmod.SporesShadows;
import moldmod.block.ModBlocks;
import moldmod.block.MoldyOakLogBlock;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Model;
import net.minecraft.data.client.TextureKey;
import net.minecraft.data.client.TextureMap;
import net.minecraft.data.client.BlockStateVariantMap;
import net.minecraft.data.client.BlockStateVariant;
import net.minecraft.data.client.VariantsBlockStateSupplier;
import net.minecraft.data.client.VariantSettings;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;
import net.minecraft.util.Identifier;
import java.util.Optional;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator generator) {
        TextureKey baseSide = TextureKey.of("base_side");
        TextureKey baseEnd = TextureKey.of("base_end");
        TextureKey overlaySide = TextureKey.of("overlay_side");
        TextureKey overlayEnd = TextureKey.of("overlay_end");
        TextureKey baseAll = TextureKey.of("base");
        TextureKey overlayAll = TextureKey.of("overlay");

        Model LAYERED_CUBE_COLUMN = new Model(Optional.of(SporesShadows.id("block/layered_cube_column")), Optional.empty(), baseEnd, baseSide, overlayEnd, overlaySide);
        Model LAYERED_CUBE_COLUMN_HORIZONTAL = new Model(Optional.of(SporesShadows.id("block/layered_cube_column_horizontal")), Optional.empty(), baseEnd, baseSide, overlayEnd, overlaySide);
        Model LAYERED_CUBE_ALL = new Model(Optional.of(SporesShadows.id("block/layered_cube_all")), Optional.empty(), baseAll, overlayAll);

        // OAK LOG (Uses moldy_oak_log overlays)
        generatePillarModels(generator, ModBlocks.MOLDY_OAK_LOG, ModBlocks.TAINTED_OAK_LOG, ModBlocks.MOLDY_OAK_LOG_ITEM, ModBlocks.ROTTEN_OAK_LOG, "oak_log", "oak_log_top", "moldy_oak_log", true, LAYERED_CUBE_COLUMN, LAYERED_CUBE_COLUMN_HORIZONTAL, baseSide, baseEnd, overlaySide, overlayEnd);

        // STRIPPED OAK LOG (Uses the same moldy_oak_log overlays to retain the transparent layer approach)
        generatePillarModels(generator, ModBlocks.MOLDY_STRIPPED_OAK_LOG, ModBlocks.TAINTED_STRIPPED_OAK_LOG, ModBlocks.MOLDY_STRIPPED_OAK_LOG_ITEM, ModBlocks.ROTTEN_STRIPPED_OAK_LOG, "stripped_oak_log", "stripped_oak_log_top", "moldy_oak_log", true, LAYERED_CUBE_COLUMN, LAYERED_CUBE_COLUMN_HORIZONTAL, baseSide, baseEnd, overlaySide, overlayEnd);

        // OAK WOOD (Same texture on all sides)
        generatePillarModels(generator, ModBlocks.MOLDY_OAK_WOOD, ModBlocks.TAINTED_OAK_WOOD, ModBlocks.MOLDY_OAK_WOOD_ITEM, ModBlocks.ROTTEN_OAK_WOOD, "oak_log", "oak_log", "moldy_oak_log", false, LAYERED_CUBE_COLUMN, LAYERED_CUBE_COLUMN_HORIZONTAL, baseSide, baseEnd, overlaySide, overlayEnd);

        // STRIPPED OAK WOOD (Same texture on all sides)
        generatePillarModels(generator, ModBlocks.MOLDY_STRIPPED_OAK_WOOD, ModBlocks.TAINTED_STRIPPED_OAK_WOOD, ModBlocks.MOLDY_STRIPPED_OAK_WOOD_ITEM, ModBlocks.ROTTEN_STRIPPED_OAK_WOOD, "stripped_oak_log", "stripped_oak_log", "moldy_oak_log", false, LAYERED_CUBE_COLUMN, LAYERED_CUBE_COLUMN_HORIZONTAL, baseSide, baseEnd, overlaySide, overlayEnd);

        // OAK PLANKS (Uses the moldy_oak_log side overlays for all faces)
        TextureMap map1 = new TextureMap().put(baseAll, Identifier.of("minecraft", "block/oak_planks")).put(overlayAll, SporesShadows.id("block/moldy_oak_log_stage_1"));
        Identifier planks1 = LAYERED_CUBE_ALL.upload(ModBlocks.MOLDY_OAK_PLANKS, "_stage_1", map1, generator.modelCollector);

        TextureMap map2 = new TextureMap().put(baseAll, Identifier.of("minecraft", "block/oak_planks")).put(overlayAll, SporesShadows.id("block/moldy_oak_log_stage_2"));
        Identifier planks2 = LAYERED_CUBE_ALL.upload(ModBlocks.MOLDY_OAK_PLANKS, "_stage_2", map2, generator.modelCollector);

        TextureMap map3 = new TextureMap().put(baseAll, Identifier.of("minecraft", "block/oak_planks")).put(overlayAll, SporesShadows.id("block/moldy_oak_log_stage_3"));
        Identifier planks3 = LAYERED_CUBE_ALL.upload(ModBlocks.MOLDY_OAK_PLANKS, "_stage_3", map3, generator.modelCollector);

        generator.blockStateCollector.accept(
            VariantsBlockStateSupplier.create(ModBlocks.MOLDY_OAK_PLANKS)
                .coordinate(BlockStateVariantMap.create(MoldyOakLogBlock.STAGE)
                    .register(0, BlockStateVariant.create().put(VariantSettings.MODEL, Identifier.of("minecraft", "block/oak_planks")))
                    .register(1, BlockStateVariant.create().put(VariantSettings.MODEL, planks1))
                    .register(2, BlockStateVariant.create().put(VariantSettings.MODEL, planks2))
                    .register(3, BlockStateVariant.create().put(VariantSettings.MODEL, planks3))
                )
        );

        generator.registerParentedItemModel(ModBlocks.TAINTED_OAK_PLANKS, planks1);
        generator.registerParentedItemModel(ModBlocks.MOLDY_OAK_PLANKS_ITEM, planks2);
        generator.registerParentedItemModel(ModBlocks.ROTTEN_OAK_PLANKS, planks3);
    }

    private void generatePillarModels(BlockStateModelGenerator generator, net.minecraft.block.Block block, net.minecraft.item.Item t1, net.minecraft.item.Item t2, net.minecraft.item.Item t3, String vanillaSide, String vanillaTop, String modBaseName, boolean hasUniqueTop, Model model, Model modelH, TextureKey bSide, TextureKey bEnd, TextureKey oSide, TextureKey oEnd) {
        Identifier baseSideId = Identifier.of("minecraft", "block/" + vanillaSide);
        Identifier baseEndId = Identifier.of("minecraft", "block/" + vanillaTop);

        String topSuffix = hasUniqueTop ? "_top" : "";

        TextureMap map1 = new TextureMap().put(bSide, baseSideId).put(bEnd, baseEndId).put(oSide, SporesShadows.id("block/" + modBaseName + "_stage_1")).put(oEnd, SporesShadows.id("block/" + modBaseName + "_stage_1" + topSuffix));
        Identifier m1 = model.upload(block, "_stage_1", map1, generator.modelCollector);
        Identifier m1H = modelH.upload(block, "_stage_1_horizontal", map1, generator.modelCollector);

        TextureMap map2 = new TextureMap().put(bSide, baseSideId).put(bEnd, baseEndId).put(oSide, SporesShadows.id("block/" + modBaseName + "_stage_2")).put(oEnd, SporesShadows.id("block/" + modBaseName + "_stage_2" + topSuffix));
        Identifier m2 = model.upload(block, "_stage_2", map2, generator.modelCollector);
        Identifier m2H = modelH.upload(block, "_stage_2_horizontal", map2, generator.modelCollector);

        TextureMap map3 = new TextureMap().put(bSide, baseSideId).put(bEnd, baseEndId).put(oSide, SporesShadows.id("block/" + modBaseName + "_stage_3")).put(oEnd, SporesShadows.id("block/" + modBaseName + "_stage_3" + topSuffix));
        Identifier m3 = model.upload(block, "_stage_3", map3, generator.modelCollector);
        Identifier m3H = modelH.upload(block, "_stage_3_horizontal", map3, generator.modelCollector);

        generator.blockStateCollector.accept(
            VariantsBlockStateSupplier.create(block)
                .coordinate(BlockStateVariantMap.create(Properties.AXIS, MoldyOakLogBlock.STAGE)
                    .register(Direction.Axis.Y, 0, BlockStateVariant.create().put(VariantSettings.MODEL, Identifier.of("minecraft", "block/" + vanillaSide)))
                    .register(Direction.Axis.Z, 0, BlockStateVariant.create().put(VariantSettings.MODEL, Identifier.of("minecraft", "block/" + vanillaSide + "_horizontal")).put(VariantSettings.X, VariantSettings.Rotation.R90))
                    .register(Direction.Axis.X, 0, BlockStateVariant.create().put(VariantSettings.MODEL, Identifier.of("minecraft", "block/" + vanillaSide + "_horizontal")).put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.Y, VariantSettings.Rotation.R90))
                    
                    .register(Direction.Axis.Y, 1, BlockStateVariant.create().put(VariantSettings.MODEL, m1))
                    .register(Direction.Axis.Z, 1, BlockStateVariant.create().put(VariantSettings.MODEL, m1H).put(VariantSettings.X, VariantSettings.Rotation.R90))
                    .register(Direction.Axis.X, 1, BlockStateVariant.create().put(VariantSettings.MODEL, m1H).put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.Y, VariantSettings.Rotation.R90))
                    
                    .register(Direction.Axis.Y, 2, BlockStateVariant.create().put(VariantSettings.MODEL, m2))
                    .register(Direction.Axis.Z, 2, BlockStateVariant.create().put(VariantSettings.MODEL, m2H).put(VariantSettings.X, VariantSettings.Rotation.R90))
                    .register(Direction.Axis.X, 2, BlockStateVariant.create().put(VariantSettings.MODEL, m2H).put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.Y, VariantSettings.Rotation.R90))
                    
                    .register(Direction.Axis.Y, 3, BlockStateVariant.create().put(VariantSettings.MODEL, m3))
                    .register(Direction.Axis.Z, 3, BlockStateVariant.create().put(VariantSettings.MODEL, m3H).put(VariantSettings.X, VariantSettings.Rotation.R90))
                    .register(Direction.Axis.X, 3, BlockStateVariant.create().put(VariantSettings.MODEL, m3H).put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.Y, VariantSettings.Rotation.R90))
                )
        );

        generator.registerParentedItemModel(t1, m1);
        generator.registerParentedItemModel(t2, m2);
        generator.registerParentedItemModel(t3, m3);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {}
}
