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
        String[] woods = moldmod.SporesShadows.WOODS;

        Map<String, String> names = new HashMap<>();
        names.put("oak", "di Quercia");
        names.put("spruce", "di Abete");
        names.put("birch", "di Betulla");
        names.put("jungle", "della Giungla");
        names.put("acacia", "di Acacia");
        names.put("dark_oak", "di Quercia Scura");
        names.put("mangrove", "di Mangrovia");
        names.put("cherry", "di Ciliegio");

        for (String wood : woods) {
            String logName = wood + "_log";
            String woodName = wood + "_wood";
            String prefix = wood;
            
            String wName = names.get(wood);

            String logType = "Tronco";
            
            translationBuilder.add("block.spores--shadows.moldy_" + logName, logType + " " + wName + " ammuffito");
            translationBuilder.add("item.spores--shadows.waxed_" + logName, logType + " " + wName + " cerato");
            translationBuilder.add("item.spores--shadows.tainted_" + logName, logType + " " + wName + " intaccato");
            translationBuilder.add("item.spores--shadows.moldy_" + logName, logType + " " + wName + " ammuffito");
            translationBuilder.add("item.spores--shadows.rotten_" + logName, logType + " " + wName + " marcio");

            translationBuilder.add("block.spores--shadows.moldy_stripped_" + logName, logType + " " + wName + " Scortecciato ammuffito");
            translationBuilder.add("item.spores--shadows.waxed_stripped_" + logName, logType + " " + wName + " Scortecciato cerato");
            translationBuilder.add("item.spores--shadows.tainted_stripped_" + logName, logType + " " + wName + " Scortecciato intaccato");
            translationBuilder.add("item.spores--shadows.moldy_stripped_" + logName, logType + " " + wName + " Scortecciato ammuffito");
            translationBuilder.add("item.spores--shadows.rotten_stripped_" + logName, logType + " " + wName + " Scortecciato marcio");

            if (woodName != null) {
                translationBuilder.add("block.spores--shadows.moldy_" + woodName, "Legno " + wName + " ammuffito");
                translationBuilder.add("item.spores--shadows.waxed_" + woodName, "Legno " + wName + " cerato");
                translationBuilder.add("item.spores--shadows.tainted_" + woodName, "Legno " + wName + " intaccato");
                translationBuilder.add("item.spores--shadows.moldy_" + woodName, "Legno " + wName + " ammuffito");
                translationBuilder.add("item.spores--shadows.rotten_" + woodName, "Legno " + wName + " marcio");

                translationBuilder.add("block.spores--shadows.moldy_stripped_" + woodName, "Legno " + wName + " Scortecciato ammuffito");
                translationBuilder.add("item.spores--shadows.waxed_stripped_" + woodName, "Legno " + wName + " Scortecciato cerato");
                translationBuilder.add("item.spores--shadows.tainted_stripped_" + woodName, "Legno " + wName + " Scortecciato intaccato");
                translationBuilder.add("item.spores--shadows.moldy_stripped_" + woodName, "Legno " + wName + " Scortecciato ammuffito");
                translationBuilder.add("item.spores--shadows.rotten_stripped_" + woodName, "Legno " + wName + " Scortecciato marcio");
            }

            translationBuilder.add("block.spores--shadows.moldy_" + prefix + "_planks", "Assi " + wName + " ammuffite");
            translationBuilder.add("item.spores--shadows.waxed_" + prefix + "_planks", "Assi " + wName + " cerate");
            translationBuilder.add("item.spores--shadows.tainted_" + prefix + "_planks", "Assi " + wName + " intaccate");
            translationBuilder.add("item.spores--shadows.moldy_" + prefix + "_planks", "Assi " + wName + " ammuffite");
            translationBuilder.add("item.spores--shadows.rotten_" + prefix + "_planks", "Assi " + wName + " marce");
            
            String[] blocks = {"slab", "stairs", "fence", "fence_gate", "door", "trapdoor", "pressure_plate", "button"};
            String[] blockNames = {"Lastra", "Scale", "Staccionata", "Cancelletto", "Porta", "Botola", "Pedana a Pressione", "Pulsante"};
            String[] femC = {"cerata", "cerate", "cerata", "cerato", "cerata", "cerata", "cerata", "cerato"};
            String[] fem = {"ammuffita", "ammuffite", "ammuffita", "ammuffito", "ammuffita", "ammuffita", "ammuffita", "ammuffito"};
            String[] femT = {"intaccata", "intaccate", "intaccata", "intaccato", "intaccata", "intaccata", "intaccata", "intaccato"};
            String[] femR = {"marcia", "marce", "marcia", "marcio", "marcia", "marcia", "marcia", "marcio"};
            
            for (int i = 0; i < blocks.length; i++) {
                String blockKey = blocks[i];
                String bName = blockNames[i];
                
                translationBuilder.add("block.spores--shadows.moldy_" + prefix + "_" + blockKey, bName + " " + wName + " " + fem[i]);
                translationBuilder.add("item.spores--shadows.waxed_" + prefix + "_" + blockKey, bName + " " + wName + " " + femC[i]);
                translationBuilder.add("item.spores--shadows.tainted_" + prefix + "_" + blockKey, bName + " " + wName + " " + femT[i]);
                translationBuilder.add("item.spores--shadows.moldy_" + prefix + "_" + blockKey, bName + " " + wName + " " + fem[i]);
                translationBuilder.add("item.spores--shadows.rotten_" + prefix + "_" + blockKey, bName + " " + wName + " " + femR[i]);
            }
        }

        translationBuilder.add("tooltip.spores--shadows.waxed", "cerato");
        translationBuilder.add("item.spores--shadows.waxed_format", "%s cerato");
        translationBuilder.add("tooltip.spores--shadows.moldy_log_desc_1", "Può essere trasformato in assi pulite perdendo materiale,");
        translationBuilder.add("tooltip.spores--shadows.moldy_log_desc_2", "ma non è utilizzabile per le ricette vanilla normali.");
        translationBuilder.add("tooltip.spores--shadows.moldy_planks_desc_1", "Utile solo per crafting semplici (bastoni, staccionate, ecc).");
        translationBuilder.add("tooltip.spores--shadows.moldy_planks_desc_2", "Non può essere usato in ricette complesse a piena efficienza.");
        translationBuilder.add("tooltip.spores--shadows.moldy_general_desc_1", "Componente di legno degradato.");
        translationBuilder.add("tooltip.spores--shadows.moldy_general_desc_2", "Strutturalmente indebolito dalla muffa.");
    }
}
