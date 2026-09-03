package moldmod.client.datagen;

import moldmod.SporesShadows;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public ModItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture, BlockTagProvider blockTagProvider) {
        super(output, completableFuture, blockTagProvider);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        // Copy block tags to item tags automatically!
        copy(BlockTags.LOGS_THAT_BURN, ItemTags.LOGS_THAT_BURN);
        copy(BlockTags.LOGS, ItemTags.LOGS);
        copy(BlockTags.CRIMSON_STEMS, ItemTags.CRIMSON_STEMS);
        copy(BlockTags.WARPED_STEMS, ItemTags.WARPED_STEMS);
        copy(BlockTags.PLANKS, ItemTags.PLANKS);
        
        copy(BlockTags.WOODEN_SLABS, ItemTags.WOODEN_SLABS);
        copy(BlockTags.WOODEN_STAIRS, ItemTags.WOODEN_STAIRS);
        copy(BlockTags.WOODEN_FENCES, ItemTags.WOODEN_FENCES);
        copy(BlockTags.WOODEN_DOORS, ItemTags.WOODEN_DOORS);
        copy(BlockTags.WOODEN_TRAPDOORS, ItemTags.WOODEN_TRAPDOORS);
        copy(BlockTags.SLABS, ItemTags.SLABS);
        copy(BlockTags.STAIRS, ItemTags.STAIRS);
        copy(BlockTags.FENCES, ItemTags.FENCES);
        copy(BlockTags.DOORS, ItemTags.DOORS);
        copy(BlockTags.TRAPDOORS, ItemTags.TRAPDOORS);

        FabricTagBuilder nonFlammable = getOrCreateTagBuilder(ItemTags.NON_FLAMMABLE_WOOD);
        for (Item item : Registries.ITEM) {
            Identifier id = Registries.ITEM.getId(item);
            if (!id.getNamespace().equals(SporesShadows.MOD_ID)) continue;
            String name = id.getPath();
            if ((name.contains("crimson") || name.contains("warped")) && ModBlockTagProvider.isHealthyWaxed(name)) {
                nonFlammable.add(item);
            }
        }
    }
}
