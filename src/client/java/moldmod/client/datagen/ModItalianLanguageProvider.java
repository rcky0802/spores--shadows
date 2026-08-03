package moldmod.client.datagen;

import moldmod.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;
import java.util.concurrent.CompletableFuture;

public class ModItalianLanguageProvider extends FabricLanguageProvider {
    public ModItalianLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, "it_it", registryLookup);
    }

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup registryLookup, TranslationBuilder translationBuilder) {
        translationBuilder.add(ModBlocks.MOLDY_OAK_LOG, "Tronco di Quercia Ammuffito");
        translationBuilder.add(ModBlocks.TAINTED_OAK_LOG, "Tronco di Quercia Intaccato");
        translationBuilder.add(ModBlocks.MOLDY_OAK_LOG_ITEM, "Tronco di Quercia Ammuffito");
        translationBuilder.add(ModBlocks.ROTTEN_OAK_LOG, "Tronco di Quercia Marcio");

        translationBuilder.add(ModBlocks.MOLDY_STRIPPED_OAK_LOG, "Tronco di Quercia Scortecciato Ammuffito");
        translationBuilder.add(ModBlocks.TAINTED_STRIPPED_OAK_LOG, "Tronco di Quercia Scortecciato Intaccato");
        translationBuilder.add(ModBlocks.MOLDY_STRIPPED_OAK_LOG_ITEM, "Tronco di Quercia Scortecciato Ammuffito");
        translationBuilder.add(ModBlocks.ROTTEN_STRIPPED_OAK_LOG, "Tronco di Quercia Scortecciato Marcio");

        translationBuilder.add(ModBlocks.MOLDY_OAK_WOOD, "Legno di Quercia Ammuffito");
        translationBuilder.add(ModBlocks.TAINTED_OAK_WOOD, "Legno di Quercia Intaccato");
        translationBuilder.add(ModBlocks.MOLDY_OAK_WOOD_ITEM, "Legno di Quercia Ammuffito");
        translationBuilder.add(ModBlocks.ROTTEN_OAK_WOOD, "Legno di Quercia Marcio");

        translationBuilder.add(ModBlocks.MOLDY_STRIPPED_OAK_WOOD, "Legno di Quercia Scortecciato Ammuffito");
        translationBuilder.add(ModBlocks.TAINTED_STRIPPED_OAK_WOOD, "Legno di Quercia Scortecciato Intaccato");
        translationBuilder.add(ModBlocks.MOLDY_STRIPPED_OAK_WOOD_ITEM, "Legno di Quercia Scortecciato Ammuffito");
        translationBuilder.add(ModBlocks.ROTTEN_STRIPPED_OAK_WOOD, "Legno di Quercia Scortecciato Marcio");

        translationBuilder.add(ModBlocks.MOLDY_OAK_PLANKS, "Assi di Quercia Ammuffite");
        translationBuilder.add(ModBlocks.TAINTED_OAK_PLANKS, "Assi di Quercia Intaccate");
        translationBuilder.add(ModBlocks.MOLDY_OAK_PLANKS_ITEM, "Assi di Quercia Ammuffite");
        translationBuilder.add(ModBlocks.ROTTEN_OAK_PLANKS, "Assi di Quercia Marce");

        translationBuilder.add("tooltip.spores--shadows.waxed", "Cerato");

        // Mod Menu / Cloth Config
        translationBuilder.add("text.autoconfig.spores_shadows.title", "Configurazione Spores & Shadows");
        translationBuilder.add("text.autoconfig.spores_shadows.category.general", "Impostazioni Generali");
        translationBuilder.add("text.autoconfig.spores_shadows.category.debug", "Impostazioni Debug");
        translationBuilder.add("text.autoconfig.spores_shadows.option.enableMoldSpread", "Abilita Diffusione Muffa");
        translationBuilder.add("text.autoconfig.spores_shadows.option.enableMoldSpread.@Tooltip", "Se disabilitato, la muffa non crescerà mai in modo naturale.");
        translationBuilder.add("text.autoconfig.spores_shadows.option.globalMoldRiskMultiplier", "Moltiplicatore Rischio Muffa");
        translationBuilder.add("text.autoconfig.spores_shadows.option.globalMoldRiskMultiplier.@Tooltip[0]", "Moltiplica il rischio base calcolato in base all'ambiente.");
        translationBuilder.add("text.autoconfig.spores_shadows.option.globalMoldRiskMultiplier.@Tooltip[1]", "1.0 = Default, 0.5 = Metà velocità, 2.0 = Doppia velocità.");
        translationBuilder.add("text.autoconfig.spores_shadows.option.showDebugInChat", "Mostra Debug in Chat");
        translationBuilder.add("text.autoconfig.spores_shadows.option.showDebugInChat.@Tooltip", "Stampa il rischio calcolato 'R' in console/chat quando un blocco esegue un tick.");
    }
}
