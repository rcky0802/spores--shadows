package moldmod.client;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

import moldmod.client.datagen.ModBlockTagProvider;
import moldmod.client.datagen.ModEnglishLanguageProvider;
import moldmod.client.datagen.ModFrenchLanguageProvider;
import moldmod.client.datagen.ModGermanLanguageProvider;
import moldmod.client.datagen.ModItalianLanguageProvider;
import moldmod.client.datagen.ModItemTagProvider;
import moldmod.client.datagen.ModLootTableProvider;
import moldmod.client.datagen.ModRecipeProvider;
import moldmod.client.datagen.ModSpanishLanguageProvider;

public class SporesShadowsDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        ModBlockTagProvider blockTagProvider = pack.addProvider(ModBlockTagProvider::new);
        pack.addProvider((output, registriesFuture) -> new ModItemTagProvider(output, registriesFuture, blockTagProvider));
        pack.addProvider(ModLootTableProvider::new);
        pack.addProvider(ModEnglishLanguageProvider::new);
        pack.addProvider(ModItalianLanguageProvider::new);
        pack.addProvider(ModSpanishLanguageProvider::new);
        pack.addProvider(ModFrenchLanguageProvider::new);
        pack.addProvider(ModGermanLanguageProvider::new);
        pack.addProvider(ModRecipeProvider::new);
	}
}
