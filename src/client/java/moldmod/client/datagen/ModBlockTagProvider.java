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
            if (!id.getNamespace().equals("spores--shadows")) continue;
            
            axeBuilder.add(block);
            String name = id.getPath();
            
            if (name.contains("fence_gate")) {
                fenceGates.add(block);
            } else if (name.contains("fence")) {
                fences.add(block);
                woodenFences.add(block);
            } else if (name.contains("stairs")) {
                stairs.add(block);
                woodenStairs.add(block);
            } else if (name.contains("slab")) {
                slabs.add(block);
                woodenSlabs.add(block);
            } else if (name.contains("trapdoor")) {
                trapdoors.add(block);
                woodenTrapdoors.add(block);
            } else if (name.contains("door")) {
                doors.add(block);
                woodenDoors.add(block);
            } else if (name.contains("planks")) {
                // Do not add to planks tag to prevent usage in recipes like sticks, crafting tables, etc.
            } else {
                logs.add(block);
                logsThatBurn.add(block);
            }
        }
    }
}
