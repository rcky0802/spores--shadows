package moldmod.client.datagen;

import moldmod.SporesShadows;
import moldmod.block.MoldyLogBlock;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Block;
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
import net.minecraft.registry.Registries;
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

        String[] woods = {"oak", "spruce", "birch", "jungle", "acacia", "dark_oak", "mangrove", "cherry", "bamboo"};

        for (String wood : woods) {
            boolean isBamboo = wood.equals("bamboo");
            String logName = isBamboo ? "bamboo_block" : wood + "_log";
            String woodName = isBamboo ? null : wood + "_wood";
            String prefix = isBamboo ? "bamboo" : wood;

            Block log = Registries.BLOCK.get(SporesShadows.id("moldy_" + logName));
            Block strippedLog = Registries.BLOCK.get(SporesShadows.id("moldy_stripped_" + logName));
            Block planks = Registries.BLOCK.get(SporesShadows.id("moldy_" + prefix + "_planks"));

            // We reuse oak mold texture for all woods for now
            String moldTextureBase = "moldy_oak_log";
            
            // Log
            generatePillarModels(generator, log, 
                Registries.ITEM.get(SporesShadows.id("tainted_" + logName)), 
                Registries.ITEM.get(SporesShadows.id("moldy_" + logName)), 
                Registries.ITEM.get(SporesShadows.id("rotten_" + logName)), 
                logName, isBamboo ? logName + "_top" : logName + "_top", moldTextureBase, true, LAYERED_CUBE_COLUMN, LAYERED_CUBE_COLUMN_HORIZONTAL, baseSide, baseEnd, overlaySide, overlayEnd);

            // Stripped Log
            generatePillarModels(generator, strippedLog, 
                Registries.ITEM.get(SporesShadows.id("tainted_stripped_" + logName)), 
                Registries.ITEM.get(SporesShadows.id("moldy_stripped_" + logName)), 
                Registries.ITEM.get(SporesShadows.id("rotten_stripped_" + logName)), 
                "stripped_" + logName, "stripped_" + logName + "_top", moldTextureBase, true, LAYERED_CUBE_COLUMN, LAYERED_CUBE_COLUMN_HORIZONTAL, baseSide, baseEnd, overlaySide, overlayEnd);

            if (woodName != null) {
                Block woodBlock = Registries.BLOCK.get(SporesShadows.id("moldy_" + woodName));
                Block strippedWood = Registries.BLOCK.get(SporesShadows.id("moldy_stripped_" + woodName));

                // Wood
                generatePillarModels(generator, woodBlock, 
                    Registries.ITEM.get(SporesShadows.id("tainted_" + woodName)), 
                    Registries.ITEM.get(SporesShadows.id("moldy_" + woodName)), 
                    Registries.ITEM.get(SporesShadows.id("rotten_" + woodName)), 
                    logName, logName, moldTextureBase, false, LAYERED_CUBE_COLUMN, LAYERED_CUBE_COLUMN_HORIZONTAL, baseSide, baseEnd, overlaySide, overlayEnd);

                // Stripped Wood
                generatePillarModels(generator, strippedWood, 
                    Registries.ITEM.get(SporesShadows.id("tainted_stripped_" + woodName)), 
                    Registries.ITEM.get(SporesShadows.id("moldy_stripped_" + woodName)), 
                    Registries.ITEM.get(SporesShadows.id("rotten_stripped_" + woodName)), 
                    "stripped_" + logName, "stripped_" + logName, moldTextureBase, false, LAYERED_CUBE_COLUMN, LAYERED_CUBE_COLUMN_HORIZONTAL, baseSide, baseEnd, overlaySide, overlayEnd);
            }

            // Planks
            TextureMap map1 = new TextureMap().put(baseAll, Identifier.of("minecraft", "block/" + prefix + "_planks")).put(overlayAll, SporesShadows.id("block/moldy_oak_log_stage_1"));
            Identifier planks1 = LAYERED_CUBE_ALL.upload(planks, "_stage_1", map1, generator.modelCollector);

            TextureMap map2 = new TextureMap().put(baseAll, Identifier.of("minecraft", "block/" + prefix + "_planks")).put(overlayAll, SporesShadows.id("block/moldy_oak_log_stage_2"));
            Identifier planks2 = LAYERED_CUBE_ALL.upload(planks, "_stage_2", map2, generator.modelCollector);

            TextureMap map3 = new TextureMap().put(baseAll, Identifier.of("minecraft", "block/" + prefix + "_planks")).put(overlayAll, SporesShadows.id("block/moldy_oak_log_stage_3"));
            Identifier planks3 = LAYERED_CUBE_ALL.upload(planks, "_stage_3", map3, generator.modelCollector);

            generator.blockStateCollector.accept(
                VariantsBlockStateSupplier.create(planks)
                    .coordinate(BlockStateVariantMap.create(MoldyLogBlock.STAGE)
                        .register(0, BlockStateVariant.create().put(VariantSettings.MODEL, Identifier.of("minecraft", "block/" + prefix + "_planks")))
                        .register(1, BlockStateVariant.create().put(VariantSettings.MODEL, planks1))
                        .register(2, BlockStateVariant.create().put(VariantSettings.MODEL, planks2))
                        .register(3, BlockStateVariant.create().put(VariantSettings.MODEL, planks3))
                    )
            );

            generator.registerParentedItemModel(Registries.ITEM.get(SporesShadows.id("tainted_" + prefix + "_planks")), planks1);
            generator.registerParentedItemModel(Registries.ITEM.get(SporesShadows.id("moldy_" + prefix + "_planks")), planks2);
            generator.registerParentedItemModel(Registries.ITEM.get(SporesShadows.id("rotten_" + prefix + "_planks")), planks3);
        }
    }

    private void generatePillarModels(BlockStateModelGenerator generator, net.minecraft.block.Block block, net.minecraft.item.Item t1, net.minecraft.item.Item t2, net.minecraft.item.Item t3, String vanillaSide, String vanillaTop, String modBaseName, boolean hasUniqueTop, Model model, Model modelH, TextureKey bSide, TextureKey bEnd, TextureKey oSide, TextureKey oEnd) {
        Identifier baseSideId = Identifier.of("minecraft", "block/" + vanillaSide);
        Identifier baseEndId = Identifier.of("minecraft", "block/" + vanillaTop);

        String topSuffix = hasUniqueTop ? "_top" : "";

        TextureMap map1 = new TextureMap().put(bSide, baseSideId).put(bEnd, baseEndId).put(oSide, SporesShadows.id("block/" + modBaseName + "_stage_1")).put(oEnd, SporesShadows.id("block/" + modBaseName + "_stage_1" + topSuffix));
        Identifier m1 = model.upload(block, "_stage_1", map1, generator.modelCollector);

        TextureMap map2 = new TextureMap().put(bSide, baseSideId).put(bEnd, baseEndId).put(oSide, SporesShadows.id("block/" + modBaseName + "_stage_2")).put(oEnd, SporesShadows.id("block/" + modBaseName + "_stage_2" + topSuffix));
        Identifier m2 = model.upload(block, "_stage_2", map2, generator.modelCollector);

        TextureMap map3 = new TextureMap().put(bSide, baseSideId).put(bEnd, baseEndId).put(oSide, SporesShadows.id("block/" + modBaseName + "_stage_3")).put(oEnd, SporesShadows.id("block/" + modBaseName + "_stage_3" + topSuffix));
        Identifier m3 = model.upload(block, "_stage_3", map3, generator.modelCollector);

        generator.blockStateCollector.accept(
            VariantsBlockStateSupplier.create(block)
                .coordinate(BlockStateVariantMap.create(Properties.AXIS, MoldyLogBlock.STAGE)
                    .register(Direction.Axis.Y, 0, BlockStateVariant.create().put(VariantSettings.MODEL, Identifier.of("minecraft", "block/" + vanillaSide)))
                    .register(Direction.Axis.Z, 0, BlockStateVariant.create().put(VariantSettings.MODEL, Identifier.of("minecraft", "block/" + vanillaSide)).put(VariantSettings.X, VariantSettings.Rotation.R90))
                    .register(Direction.Axis.X, 0, BlockStateVariant.create().put(VariantSettings.MODEL, Identifier.of("minecraft", "block/" + vanillaSide)).put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.Y, VariantSettings.Rotation.R90))
                    
                    .register(Direction.Axis.Y, 1, BlockStateVariant.create().put(VariantSettings.MODEL, m1))
                    .register(Direction.Axis.Z, 1, BlockStateVariant.create().put(VariantSettings.MODEL, m1).put(VariantSettings.X, VariantSettings.Rotation.R90))
                    .register(Direction.Axis.X, 1, BlockStateVariant.create().put(VariantSettings.MODEL, m1).put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.Y, VariantSettings.Rotation.R90))
                    
                    .register(Direction.Axis.Y, 2, BlockStateVariant.create().put(VariantSettings.MODEL, m2))
                    .register(Direction.Axis.Z, 2, BlockStateVariant.create().put(VariantSettings.MODEL, m2).put(VariantSettings.X, VariantSettings.Rotation.R90))
                    .register(Direction.Axis.X, 2, BlockStateVariant.create().put(VariantSettings.MODEL, m2).put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.Y, VariantSettings.Rotation.R90))
                    
                    .register(Direction.Axis.Y, 3, BlockStateVariant.create().put(VariantSettings.MODEL, m3))
                    .register(Direction.Axis.Z, 3, BlockStateVariant.create().put(VariantSettings.MODEL, m3).put(VariantSettings.X, VariantSettings.Rotation.R90))
                    .register(Direction.Axis.X, 3, BlockStateVariant.create().put(VariantSettings.MODEL, m3).put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.Y, VariantSettings.Rotation.R90))
                )
        );

        generator.registerParentedItemModel(t1, m1);
        generator.registerParentedItemModel(t2, m2);
        generator.registerParentedItemModel(t3, m3);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {}
}
