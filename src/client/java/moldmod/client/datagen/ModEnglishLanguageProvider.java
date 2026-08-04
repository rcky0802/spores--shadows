package moldmod.client.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class ModEnglishLanguageProvider extends FabricLanguageProvider {

    public ModEnglishLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, "en_us", registryLookup);
    }

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup registryLookup, TranslationBuilder translationBuilder) {
        String[] woods = {"oak", "spruce", "birch", "jungle", "acacia", "dark_oak", "mangrove", "cherry", "bamboo"};

        for (String wood : woods) {
            boolean isBamboo = wood.equals("bamboo");
            String logName = isBamboo ? "bamboo_block" : wood + "_log";
            String woodName = isBamboo ? null : wood + "_wood";
            String prefix = isBamboo ? "bamboo" : wood;
            
            String capitalizedWood = capitalize(wood.replace("_", " "));

            translationBuilder.add("block.spores--shadows.moldy_" + logName, "Moldy " + capitalizedWood + (isBamboo ? " Block" : " Log"));
            translationBuilder.add("item.spores--shadows.tainted_" + logName, "Tainted " + capitalizedWood + (isBamboo ? " Block" : " Log"));
            translationBuilder.add("item.spores--shadows.moldy_" + logName, "Moldy " + capitalizedWood + (isBamboo ? " Block" : " Log"));
            translationBuilder.add("item.spores--shadows.rotten_" + logName, "Rotten " + capitalizedWood + (isBamboo ? " Block" : " Log"));

            translationBuilder.add("block.spores--shadows.moldy_stripped_" + logName, "Moldy Stripped " + capitalizedWood + (isBamboo ? " Block" : " Log"));
            translationBuilder.add("item.spores--shadows.tainted_stripped_" + logName, "Tainted Stripped " + capitalizedWood + (isBamboo ? " Block" : " Log"));
            translationBuilder.add("item.spores--shadows.moldy_stripped_" + logName, "Moldy Stripped " + capitalizedWood + (isBamboo ? " Block" : " Log"));
            translationBuilder.add("item.spores--shadows.rotten_stripped_" + logName, "Rotten Stripped " + capitalizedWood + (isBamboo ? " Block" : " Log"));

            if (woodName != null) {
                translationBuilder.add("block.spores--shadows.moldy_" + woodName, "Moldy " + capitalizedWood + " Wood");
                translationBuilder.add("item.spores--shadows.tainted_" + woodName, "Tainted " + capitalizedWood + " Wood");
                translationBuilder.add("item.spores--shadows.moldy_" + woodName, "Moldy " + capitalizedWood + " Wood");
                translationBuilder.add("item.spores--shadows.rotten_" + woodName, "Rotten " + capitalizedWood + " Wood");

                translationBuilder.add("block.spores--shadows.moldy_stripped_" + woodName, "Moldy Stripped " + capitalizedWood + " Wood");
                translationBuilder.add("item.spores--shadows.tainted_stripped_" + woodName, "Tainted Stripped " + capitalizedWood + " Wood");
                translationBuilder.add("item.spores--shadows.moldy_stripped_" + woodName, "Moldy Stripped " + capitalizedWood + " Wood");
                translationBuilder.add("item.spores--shadows.rotten_stripped_" + woodName, "Rotten Stripped " + capitalizedWood + " Wood");
            }

            translationBuilder.add("block.spores--shadows.moldy_" + prefix + "_planks", "Moldy " + capitalizedWood + " Planks");
            translationBuilder.add("item.spores--shadows.tainted_" + prefix + "_planks", "Tainted " + capitalizedWood + " Planks");
            translationBuilder.add("item.spores--shadows.moldy_" + prefix + "_planks", "Moldy " + capitalizedWood + " Planks");
            translationBuilder.add("item.spores--shadows.rotten_" + prefix + "_planks", "Rotten " + capitalizedWood + " Planks");
        }

        translationBuilder.add("tooltip.spores--shadows.waxed", "Waxed");
    }
    
    private String capitalize(String str) {
        String[] words = str.split(" ");
        StringBuilder result = new StringBuilder();
        for (String w : words) {
            result.append(Character.toUpperCase(w.charAt(0))).append(w.substring(1)).append(" ");
        }
        return result.toString().trim();
    }
}
