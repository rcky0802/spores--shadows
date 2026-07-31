package moldmod.client.datagen;

import moldmod.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.registry.RegistryWrapper;
import java.util.concurrent.CompletableFuture;

public class ModLootTableProvider extends FabricBlockLootTableProvider {
    public ModLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        addDrop(ModBlocks.MOLDY_OAK_LOG_STAGE_1);
        addDrop(ModBlocks.MOLDY_OAK_LOG_STAGE_2);
        addDrop(ModBlocks.MOLDY_OAK_LOG_STAGE_3);
        addDrop(ModBlocks.PLACED_OAK_LOG, net.minecraft.block.Blocks.OAK_LOG);
    }
}
