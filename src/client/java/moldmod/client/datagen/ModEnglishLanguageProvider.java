package moldmod.client.datagen;

import moldmod.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;
import java.util.concurrent.CompletableFuture;

public class ModEnglishLanguageProvider extends FabricLanguageProvider {
    public ModEnglishLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup registryLookup, TranslationBuilder translationBuilder) {
        translationBuilder.add(ModBlocks.MOLDY_OAK_LOG_STAGE_1, "Moldy Oak Log - Stage 1");
        translationBuilder.add(ModBlocks.MOLDY_OAK_LOG_STAGE_2, "Moldy Oak Log - Stage 2");
        translationBuilder.add(ModBlocks.MOLDY_OAK_LOG_STAGE_3, "Moldy Oak Log - Stage 3");
    }
}
