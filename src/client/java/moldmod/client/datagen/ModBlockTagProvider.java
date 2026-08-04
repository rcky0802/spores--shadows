package moldmod.client.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
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
        FabricTagBuilder planks = getOrCreateTagBuilder(BlockTags.PLANKS);
        FabricTagBuilder bamboo = getOrCreateTagBuilder(BlockTags.BAMBOO_BLOCKS);

        for (net.minecraft.block.Block block : moldmod.block.ModBlocks.VANILLA_TO_MOLDY.values()) {
            axeBuilder.add(block);
            
            String name = Registries.BLOCK.getId(block).getPath();
            
            if (name.contains("planks")) {
                // Do not add to planks tag to prevent usage in recipes like sticks, crafting tables, etc.
            } else if (name.contains("bamboo")) {
                bamboo.add(block);
            } else {
                logs.add(block);
                logsThatBurn.add(block);
            }
        }
    }
}
