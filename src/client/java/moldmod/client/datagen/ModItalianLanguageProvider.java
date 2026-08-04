package moldmod.client.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;
import java.util.Map;
import java.util.HashMap;

public class ModItalianLanguageProvider extends FabricLanguageProvider {

    public ModItalianLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, "it_it", registryLookup);
    }

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup registryLookup, TranslationBuilder translationBuilder) {
        String[] woods = {"oak", "spruce", "birch", "jungle", "acacia", "dark_oak", "mangrove", "cherry", "bamboo"};

        Map<String, String> names = new HashMap<>();
        names.put("oak", "di Quercia");
        names.put("spruce", "di Abete");
        names.put("birch", "di Betulla");
        names.put("jungle", "della Giungla");
        names.put("acacia", "di Acacia");
        names.put("dark_oak", "di Rovere Scuro");
        names.put("mangrove", "di Mangrovia");
        names.put("cherry", "di Ciliegio");
        names.put("bamboo", "di Bambù");

        for (String wood : woods) {
            boolean isBamboo = wood.equals("bamboo");
            String logName = isBamboo ? "bamboo_block" : wood + "_log";
            String woodName = isBamboo ? null : wood + "_wood";
            String prefix = isBamboo ? "bamboo" : wood;
            
            String wName = names.get(wood);

            String logType = isBamboo ? "Blocco" : "Tronco";
            
            translationBuilder.add("block.spores--shadows.moldy_" + logName, logType + " " + wName + " Ammuffito");
            translationBuilder.add("item.spores--shadows.tainted_" + logName, logType + " " + wName + " Intaccato");
            translationBuilder.add("item.spores--shadows.moldy_" + logName, logType + " " + wName + " Ammuffito");
            translationBuilder.add("item.spores--shadows.rotten_" + logName, logType + " " + wName + " Marcio");

            translationBuilder.add("block.spores--shadows.moldy_stripped_" + logName, logType + " " + wName + " Scortecciato Ammuffito");
            translationBuilder.add("item.spores--shadows.tainted_stripped_" + logName, logType + " " + wName + " Scortecciato Intaccato");
            translationBuilder.add("item.spores--shadows.moldy_stripped_" + logName, logType + " " + wName + " Scortecciato Ammuffito");
            translationBuilder.add("item.spores--shadows.rotten_stripped_" + logName, logType + " " + wName + " Scortecciato Marcio");

            if (woodName != null) {
                translationBuilder.add("block.spores--shadows.moldy_" + woodName, "Legno " + wName + " Ammuffito");
                translationBuilder.add("item.spores--shadows.tainted_" + woodName, "Legno " + wName + " Intaccato");
                translationBuilder.add("item.spores--shadows.moldy_" + woodName, "Legno " + wName + " Ammuffito");
                translationBuilder.add("item.spores--shadows.rotten_" + woodName, "Legno " + wName + " Marcio");

                translationBuilder.add("block.spores--shadows.moldy_stripped_" + woodName, "Legno " + wName + " Scortecciato Ammuffito");
                translationBuilder.add("item.spores--shadows.tainted_stripped_" + woodName, "Legno " + wName + " Scortecciato Intaccato");
                translationBuilder.add("item.spores--shadows.moldy_stripped_" + woodName, "Legno " + wName + " Scortecciato Ammuffito");
                translationBuilder.add("item.spores--shadows.rotten_stripped_" + woodName, "Legno " + wName + " Scortecciato Marcio");
            }

            translationBuilder.add("block.spores--shadows.moldy_" + prefix + "_planks", "Assi " + wName + " Ammuffite");
            translationBuilder.add("item.spores--shadows.tainted_" + prefix + "_planks", "Assi " + wName + " Intaccate");
            translationBuilder.add("item.spores--shadows.moldy_" + prefix + "_planks", "Assi " + wName + " Ammuffite");
            translationBuilder.add("item.spores--shadows.rotten_" + prefix + "_planks", "Assi " + wName + " Marce");
        }

        translationBuilder.add("tooltip.spores--shadows.waxed", "Cerato");
    }
}
