package moldmod.client.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.Identifier;
import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public ModBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        FabricTagBuilder axeBuilder = getOrCreateTagBuilder(BlockTags.AXE_MINEABLE);
        FabricTagBuilder logsThatBurn = getOrCreateTagBuilder(BlockTags.LOGS_THAT_BURN);
        FabricTagBuilder logs = getOrCreateTagBuilder(BlockTags.LOGS);
        FabricTagBuilder crimsonStems = getOrCreateTagBuilder(BlockTags.CRIMSON_STEMS);
        FabricTagBuilder warpedStems = getOrCreateTagBuilder(BlockTags.WARPED_STEMS);

        FabricTagBuilder fences = getOrCreateTagBuilder(BlockTags.FENCES);
        FabricTagBuilder woodenFences = getOrCreateTagBuilder(BlockTags.WOODEN_FENCES);
        FabricTagBuilder fenceGates = getOrCreateTagBuilder(BlockTags.FENCE_GATES);

        FabricTagBuilder stairs = getOrCreateTagBuilder(BlockTags.STAIRS);
        FabricTagBuilder woodenStairs = getOrCreateTagBuilder(BlockTags.WOODEN_STAIRS);
        FabricTagBuilder slabs = getOrCreateTagBuilder(BlockTags.SLABS);
        FabricTagBuilder woodenSlabs = getOrCreateTagBuilder(BlockTags.WOODEN_SLABS);
        FabricTagBuilder doors = getOrCreateTagBuilder(BlockTags.DOORS);
        FabricTagBuilder woodenDoors = getOrCreateTagBuilder(BlockTags.WOODEN_DOORS);
        FabricTagBuilder trapdoors = getOrCreateTagBuilder(BlockTags.TRAPDOORS);
        FabricTagBuilder woodenTrapdoors = getOrCreateTagBuilder(BlockTags.WOODEN_TRAPDOORS);

        for (net.minecraft.block.Block block : Registries.BLOCK) {
            Identifier id = Registries.BLOCK.getId(block);
            if (!id.getNamespace().equals(moldmod.SporesShadows.MOD_ID)) continue;
            
            String name = id.getPath();
            
            if (!name.contains(moldmod.SporesShadowsConstants.MoldStage.ROTTEN.getName() + "_")) {
                axeBuilder.add(block);
            }
            
            boolean isHealthyWaxed = name.startsWith("waxed_") && !name.contains("moldy") && !name.contains("tainted") && !name.contains("rotten");
            boolean isNether = name.contains("crimson") || name.contains("warped");

            if (block instanceof net.minecraft.block.FenceGateBlock) {
                fenceGates.add(block);
            } else if (block instanceof net.minecraft.block.FenceBlock) {
                fences.add(block);
                if (isHealthyWaxed && !isNether) woodenFences.add(block);
            } else if (block instanceof net.minecraft.block.StairsBlock) {
                stairs.add(block);
                if (isHealthyWaxed && !isNether) woodenStairs.add(block);
            } else if (block instanceof net.minecraft.block.SlabBlock) {
                slabs.add(block);
                if (isHealthyWaxed && !isNether) woodenSlabs.add(block);
            } else if (block instanceof net.minecraft.block.TrapdoorBlock) {
                trapdoors.add(block);
                if (isHealthyWaxed && !isNether) woodenTrapdoors.add(block);
            } else if (block instanceof net.minecraft.block.DoorBlock) {
                doors.add(block);
                if (isHealthyWaxed && !isNether) woodenDoors.add(block);
            } else if (block instanceof net.minecraft.block.ButtonBlock || block instanceof net.minecraft.block.PressurePlateBlock) {
                // Do not tag buttons and pressure plates as logs
            } else if (block instanceof moldmod.block.MoldyPlanksBlock) {
                // Do not add infected planks to planks tag to prevent usage in recipes like sticks, crafting tables, etc.
                if (isHealthyWaxed) {
                    getOrCreateTagBuilder(BlockTags.PLANKS).add(block);
                }
            } else if (block instanceof moldmod.block.MoldyLogBlock) {
                if (isHealthyWaxed) {
                    if (name.contains("crimson")) {
                        crimsonStems.add(block);
                    } else if (name.contains("warped")) {
                        warpedStems.add(block);
                    } else {
                        logs.add(block);
                        logsThatBurn.add(block);
                    }
                }
            }
        }
    }
}

