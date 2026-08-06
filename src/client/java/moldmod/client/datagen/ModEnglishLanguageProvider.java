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
        String[] woods = {"oak"};

        for (String wood : woods) {
            String logName = wood + "_log";
            String woodName = wood + "_wood";
            String prefix = wood;
            
            String capitalizedWood = capitalize(wood.replace("_", " "));

            translationBuilder.add("block.spores--shadows.moldy_" + logName, "Moldy " + capitalizedWood + " Log");
            translationBuilder.add("item.spores--shadows.tainted_" + logName, "Tainted " + capitalizedWood + " Log");
            translationBuilder.add("item.spores--shadows.moldy_" + logName, "Moldy " + capitalizedWood + " Log");
            translationBuilder.add("item.spores--shadows.rotten_" + logName, "Rotten " + capitalizedWood + " Log");

            translationBuilder.add("block.spores--shadows.moldy_stripped_" + logName, "Moldy Stripped " + capitalizedWood + " Log");
            translationBuilder.add("item.spores--shadows.tainted_stripped_" + logName, "Tainted Stripped " + capitalizedWood + " Log");
            translationBuilder.add("item.spores--shadows.moldy_stripped_" + logName, "Moldy Stripped " + capitalizedWood + " Log");
            translationBuilder.add("item.spores--shadows.rotten_stripped_" + logName, "Rotten Stripped " + capitalizedWood + " Log");

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
            
            String[] blocks = {"slab", "stairs", "fence", "fence_gate", "door", "trapdoor"};
            String[] blockNames = {"Slab", "Stairs", "Fence", "Fence Gate", "Door", "Trapdoor"};
            
            for (int i = 0; i < blocks.length; i++) {
                String blockKey = blocks[i];
                String blockDisplayName = blockNames[i];
                translationBuilder.add("block.spores--shadows.moldy_" + prefix + "_" + blockKey, "Moldy " + capitalizedWood + " " + blockDisplayName);
                translationBuilder.add("item.spores--shadows.tainted_" + prefix + "_" + blockKey, "Tainted " + capitalizedWood + " " + blockDisplayName);
                translationBuilder.add("item.spores--shadows.moldy_" + prefix + "_" + blockKey, "Moldy " + capitalizedWood + " " + blockDisplayName);
                translationBuilder.add("item.spores--shadows.rotten_" + prefix + "_" + blockKey, "Rotten " + capitalizedWood + " " + blockDisplayName);
            }
        }

        translationBuilder.add("tooltip.spores--shadows.waxed", "Waxed");
        translationBuilder.add("tooltip.spores--shadows.moldy_log_desc_1", "Can be broken down into clean planks with material loss,");
        translationBuilder.add("tooltip.spores--shadows.moldy_log_desc_2", "but cannot be used for normal vanilla recipes.");
        translationBuilder.add("tooltip.spores--shadows.moldy_planks_desc_1", "Only useful for simple crafting (sticks, fences, etc).");
        translationBuilder.add("tooltip.spores--shadows.moldy_planks_desc_2", "Cannot be used in complex recipes at full efficiency.");
        translationBuilder.add("tooltip.spores--shadows.moldy_general_desc_1", "Degraded wood component.");
        translationBuilder.add("tooltip.spores--shadows.moldy_general_desc_2", "Structurally weakened by mold.");
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
