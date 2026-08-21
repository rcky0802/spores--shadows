package moldmod.client;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

import moldmod.client.datagen.ModBlockTagProvider;
import moldmod.client.datagen.ModEnglishLanguageProvider;
import moldmod.client.datagen.ModItalianLanguageProvider;
import moldmod.client.datagen.ModLootTableProvider;
import moldmod.client.datagen.ModModelProvider;
import moldmod.client.datagen.ModRecipeProvider;
import moldmod.client.datagen.ModSpanishLanguageProvider;
import moldmod.client.datagen.ModFrenchLanguageProvider;
import moldmod.client.datagen.ModGermanLanguageProvider;

public class SporesShadowsDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        pack.addProvider(ModBlockTagProvider::new);
        pack.addProvider(ModLootTableProvider::new);
        pack.addProvider(ModModelProvider::new);
        pack.addProvider(ModEnglishLanguageProvider::new);
        pack.addProvider(ModItalianLanguageProvider::new);
        pack.addProvider(ModSpanishLanguageProvider::new);
        pack.addProvider(ModFrenchLanguageProvider::new);
        pack.addProvider(ModGermanLanguageProvider::new);
        pack.addProvider(ModRecipeProvider::new);
	}
}
