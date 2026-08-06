package moldmod.client.datagen;

import moldmod.SporesShadows;
import moldmod.block.MoldyLogBlock;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Block;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;

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


public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator generator) {



        String[] woods = moldmod.SporesShadows.WOODS;

        for (String wood : woods) {
            String logName = wood + "_log";
            String woodName = wood + "_wood";
            String prefix = wood;

            Block log = Registries.BLOCK.get(SporesShadows.id("moldy_" + logName));
            Block strippedLog = Registries.BLOCK.get(SporesShadows.id("moldy_stripped_" + logName));
            Block planks = Registries.BLOCK.get(SporesShadows.id("moldy_" + prefix + "_planks"));


            // Log
            generatePillarModels(generator, log, 
                Registries.ITEM.get(SporesShadows.id("tainted_" + logName)), 
                Registries.ITEM.get(SporesShadows.id("moldy_" + logName)), 
                Registries.ITEM.get(SporesShadows.id("rotten_" + logName)), 
                logName, logName + "_top", logName, true);

            // Stripped Log
            generatePillarModels(generator, strippedLog, 
                Registries.ITEM.get(SporesShadows.id("tainted_stripped_" + logName)), 
                Registries.ITEM.get(SporesShadows.id("moldy_stripped_" + logName)), 
                Registries.ITEM.get(SporesShadows.id("rotten_stripped_" + logName)), 
                "stripped_" + logName, "stripped_" + logName + "_top", "stripped_" + logName, true);

            if (woodName != null) {
                Block woodBlock = Registries.BLOCK.get(SporesShadows.id("moldy_" + woodName));
                Block strippedWood = Registries.BLOCK.get(SporesShadows.id("moldy_stripped_" + woodName));

                // Wood (wood uses log texture)
                generatePillarModels(generator, woodBlock, 
                    Registries.ITEM.get(SporesShadows.id("tainted_" + woodName)), 
                    Registries.ITEM.get(SporesShadows.id("moldy_" + woodName)), 
                    Registries.ITEM.get(SporesShadows.id("rotten_" + woodName)), 
                    logName, logName, logName, false);

                // Stripped Wood (stripped wood uses stripped log texture)
                generatePillarModels(generator, strippedWood, 
                    Registries.ITEM.get(SporesShadows.id("tainted_stripped_" + woodName)), 
                    Registries.ITEM.get(SporesShadows.id("moldy_stripped_" + woodName)), 
                    Registries.ITEM.get(SporesShadows.id("rotten_stripped_" + woodName)), 
                    "stripped_" + logName, "stripped_" + logName, "stripped_" + logName, false);
            }

            // Planks
            TextureMap map1 = new TextureMap().put(TextureKey.ALL, Identifier.of("minecraft", "block/" + prefix + "_planks")).put(OVERLAY_KEY, Identifier.of("spores--shadows", "block/mold_stage_1"));
            Identifier planks1 = new net.minecraft.data.client.Model(java.util.Optional.of(Identifier.of("spores--shadows", "block/moldy_cube_all")), java.util.Optional.empty(), TextureKey.ALL, OVERLAY_KEY).upload(planks, "_stage_1", map1, generator.modelCollector);

            TextureMap map2 = new TextureMap().put(TextureKey.ALL, Identifier.of("minecraft", "block/" + prefix + "_planks")).put(OVERLAY_KEY, Identifier.of("spores--shadows", "block/mold_stage_2"));
            Identifier planks2 = new net.minecraft.data.client.Model(java.util.Optional.of(Identifier.of("spores--shadows", "block/moldy_cube_all")), java.util.Optional.empty(), TextureKey.ALL, OVERLAY_KEY).upload(planks, "_stage_2", map2, generator.modelCollector);

            TextureMap map3 = new TextureMap().put(TextureKey.ALL, Identifier.of("minecraft", "block/" + prefix + "_planks")).put(OVERLAY_KEY, Identifier.of("spores--shadows", "block/mold_stage_3"));
            Identifier planks3 = new net.minecraft.data.client.Model(java.util.Optional.of(Identifier.of("spores--shadows", "block/moldy_cube_all")), java.util.Optional.empty(), TextureKey.ALL, OVERLAY_KEY).upload(planks, "_stage_3", map3, generator.modelCollector);

            generator.blockStateCollector.accept(
                VariantsBlockStateSupplier.create(planks)
                    .coordinate(BlockStateVariantMap.create(MoldyLogBlock.STAGE)
                        .register(0, BlockStateVariant.create().put(VariantSettings.MODEL, Identifier.of("minecraft", "block/" + prefix + "_planks")))
                        .register(1, BlockStateVariant.create().put(VariantSettings.MODEL, planks1))
                        .register(2, BlockStateVariant.create().put(VariantSettings.MODEL, planks2))
                        .register(3, BlockStateVariant.create().put(VariantSettings.MODEL, planks3))
                    )
                    .coordinate(BlockStateVariantMap.create(MoldyLogBlock.WAXED)
                        .register(true, BlockStateVariant.create())
                        .register(false, BlockStateVariant.create())
                    )
                    .coordinate(BlockStateVariantMap.create(MoldyLogBlock.STRUCTURAL)
                        .register(true, BlockStateVariant.create())
                        .register(false, BlockStateVariant.create())
                    )
            );

            generator.registerParentedItemModel(Registries.ITEM.get(SporesShadows.id("tainted_" + prefix + "_planks")), planks1);
            generator.registerParentedItemModel(Registries.ITEM.get(SporesShadows.id("moldy_" + prefix + "_planks")), planks2);
            generator.registerParentedItemModel(Registries.ITEM.get(SporesShadows.id("rotten_" + prefix + "_planks")), planks3);
        }
    }

    private static final TextureKey OVERLAY_KEY = TextureKey.of("overlay");

    private void generatePillarModels(BlockStateModelGenerator generator, net.minecraft.block.Block block, net.minecraft.item.Item t1, net.minecraft.item.Item t2, net.minecraft.item.Item t3, String vanillaSide, String vanillaTop, String textureBaseName, boolean hasUniqueTop) {
        String topSuffix = hasUniqueTop ? "_top" : "";

        net.minecraft.data.client.Model colModel = new net.minecraft.data.client.Model(java.util.Optional.of(Identifier.of("spores--shadows", "block/moldy_cube_column")), java.util.Optional.empty(), TextureKey.SIDE, TextureKey.END, OVERLAY_KEY);
        net.minecraft.data.client.Model colModelH = new net.minecraft.data.client.Model(java.util.Optional.of(Identifier.of("spores--shadows", "block/moldy_cube_column_horizontal")), java.util.Optional.empty(), TextureKey.SIDE, TextureKey.END, OVERLAY_KEY);

        TextureMap map1 = new TextureMap().put(TextureKey.SIDE, Identifier.of("minecraft", "block/" + textureBaseName)).put(TextureKey.END, Identifier.of("minecraft", "block/" + textureBaseName + topSuffix)).put(OVERLAY_KEY, Identifier.of("spores--shadows", "block/mold_stage_1"));
        Identifier m1 = colModel.upload(block, "_stage_1", map1, generator.modelCollector);
        TextureMap map1h = new TextureMap().put(TextureKey.SIDE, Identifier.of("minecraft", "block/" + textureBaseName)).put(TextureKey.END, Identifier.of("minecraft", "block/" + textureBaseName + topSuffix)).put(OVERLAY_KEY, Identifier.of("spores--shadows", "block/mold_stage_1"));
        Identifier m1h = colModelH.upload(block, "_horizontal_stage_1", map1h, generator.modelCollector);

        TextureMap map2 = new TextureMap().put(TextureKey.SIDE, Identifier.of("minecraft", "block/" + textureBaseName)).put(TextureKey.END, Identifier.of("minecraft", "block/" + textureBaseName + topSuffix)).put(OVERLAY_KEY, Identifier.of("spores--shadows", "block/mold_stage_2"));
        Identifier m2 = colModel.upload(block, "_stage_2", map2, generator.modelCollector);
        TextureMap map2h = new TextureMap().put(TextureKey.SIDE, Identifier.of("minecraft", "block/" + textureBaseName)).put(TextureKey.END, Identifier.of("minecraft", "block/" + textureBaseName + topSuffix)).put(OVERLAY_KEY, Identifier.of("spores--shadows", "block/mold_stage_2"));
        Identifier m2h = colModelH.upload(block, "_horizontal_stage_2", map2h, generator.modelCollector);

        TextureMap map3 = new TextureMap().put(TextureKey.SIDE, Identifier.of("minecraft", "block/" + textureBaseName)).put(TextureKey.END, Identifier.of("minecraft", "block/" + textureBaseName + topSuffix)).put(OVERLAY_KEY, Identifier.of("spores--shadows", "block/mold_stage_3"));
        Identifier m3 = colModel.upload(block, "_stage_3", map3, generator.modelCollector);
        TextureMap map3h = new TextureMap().put(TextureKey.SIDE, Identifier.of("minecraft", "block/" + textureBaseName)).put(TextureKey.END, Identifier.of("minecraft", "block/" + textureBaseName + topSuffix)).put(OVERLAY_KEY, Identifier.of("spores--shadows", "block/mold_stage_3"));
        Identifier m3h = colModelH.upload(block, "_horizontal_stage_3", map3h, generator.modelCollector);

        Identifier vanillaModel = Identifier.of("minecraft", "block/" + vanillaSide);
        Identifier vanillaModelH = Identifier.of("minecraft", "block/" + vanillaSide);
        if (!hasUniqueTop) {
            // If it's wood, horizontal model is the same as vertical
        }

        generator.blockStateCollector.accept(
            VariantsBlockStateSupplier.create(block)
                .coordinate(BlockStateVariantMap.create(Properties.AXIS, MoldyLogBlock.STAGE)
                    .register(Direction.Axis.Y, 0, BlockStateVariant.create().put(VariantSettings.MODEL, vanillaModel))
                    .register(Direction.Axis.Z, 0, BlockStateVariant.create().put(VariantSettings.MODEL, vanillaModelH).put(VariantSettings.X, VariantSettings.Rotation.R90))
                    .register(Direction.Axis.X, 0, BlockStateVariant.create().put(VariantSettings.MODEL, vanillaModelH).put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.Y, VariantSettings.Rotation.R90))
                    
                    .register(Direction.Axis.Y, 1, BlockStateVariant.create().put(VariantSettings.MODEL, m1))
                    .register(Direction.Axis.Z, 1, BlockStateVariant.create().put(VariantSettings.MODEL, m1h).put(VariantSettings.X, VariantSettings.Rotation.R90))
                    .register(Direction.Axis.X, 1, BlockStateVariant.create().put(VariantSettings.MODEL, m1h).put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.Y, VariantSettings.Rotation.R90))
                    
                    .register(Direction.Axis.Y, 2, BlockStateVariant.create().put(VariantSettings.MODEL, m2))
                    .register(Direction.Axis.Z, 2, BlockStateVariant.create().put(VariantSettings.MODEL, m2h).put(VariantSettings.X, VariantSettings.Rotation.R90))
                    .register(Direction.Axis.X, 2, BlockStateVariant.create().put(VariantSettings.MODEL, m2h).put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.Y, VariantSettings.Rotation.R90))
                    
                    .register(Direction.Axis.Y, 3, BlockStateVariant.create().put(VariantSettings.MODEL, m3))
                    .register(Direction.Axis.Z, 3, BlockStateVariant.create().put(VariantSettings.MODEL, m3h).put(VariantSettings.X, VariantSettings.Rotation.R90))
                    .register(Direction.Axis.X, 3, BlockStateVariant.create().put(VariantSettings.MODEL, m3h).put(VariantSettings.X, VariantSettings.Rotation.R90).put(VariantSettings.Y, VariantSettings.Rotation.R90))
                )
                .coordinate(BlockStateVariantMap.create(MoldyLogBlock.WAXED)
                    .register(true, BlockStateVariant.create())
                    .register(false, BlockStateVariant.create())
                )
                .coordinate(BlockStateVariantMap.create(MoldyLogBlock.STRUCTURAL)
                    .register(true, BlockStateVariant.create())
                    .register(false, BlockStateVariant.create())
                )
        );

        generator.registerParentedItemModel(t1, m1);
        generator.registerParentedItemModel(t2, m2);
        generator.registerParentedItemModel(t3, m3);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {}
}
