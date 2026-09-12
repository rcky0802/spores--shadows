package moldmod.client;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

import moldmod.client.datagen.lang.ModEnglishLanguageProvider;
import moldmod.client.datagen.lang.ModFrenchLanguageProvider;
import moldmod.client.datagen.lang.ModGermanLanguageProvider;
import moldmod.client.datagen.lang.ModItalianLanguageProvider;
import moldmod.client.datagen.lang.ModSpanishLanguageProvider;
import moldmod.client.datagen.loot.ModLootTableProvider;
import moldmod.client.datagen.recipe.ModRecipeProvider;
import moldmod.client.datagen.tag.ModBlockTagProvider;
import moldmod.client.datagen.tag.ModItemTagProvider;

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
