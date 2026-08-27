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

            if (name.contains("fence_gate")) {
                fenceGates.add(block);
            } else if (name.contains("fence")) {
                fences.add(block);
                if (isHealthyWaxed) woodenFences.add(block);
            } else if (name.contains("stairs")) {
                stairs.add(block);
                if (isHealthyWaxed) woodenStairs.add(block);
            } else if (name.contains("slab")) {
                slabs.add(block);
                if (isHealthyWaxed) woodenSlabs.add(block);
            } else if (name.contains("trapdoor")) {
                trapdoors.add(block);
                if (isHealthyWaxed) woodenTrapdoors.add(block);
            } else if (name.contains("door")) {
                doors.add(block);
                if (isHealthyWaxed) woodenDoors.add(block);
            } else if (name.contains("button") || name.contains("pressure_plate")) {
                // Do not tag buttons and pressure plates as logs
            } else if (name.contains("planks")) {
                // Do not add infected planks to planks tag to prevent usage in recipes like sticks, crafting tables, etc.
                if (isHealthyWaxed) {
                    getOrCreateTagBuilder(BlockTags.PLANKS).add(block);
                }
            } else {
                if (isHealthyWaxed) {
                    logs.add(block);
                    if (!name.contains("crimson") && !name.contains("warped")) {
                        logsThatBurn.add(block);
                    }
                }
            }
        }
    }
}

