package moldmod.client.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public abstract class AbstractModLanguageProvider extends FabricLanguageProvider {

    public AbstractModLanguageProvider(FabricDataOutput dataOutput, String languageCode, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, languageCode, registryLookup);
    }

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup registryLookup, TranslationBuilder translationBuilder) {
        String[] woods = moldmod.SporesShadows.WOODS;

        for (String wood : woods) {
            String logName = wood + "_log";
            String woodName = wood + "_wood";
            String prefix = wood;

            // Logs
            translationBuilder.add("block.spores--shadows.moldy_" + logName, getTranslation(wood, "log", "moldy"));
            translationBuilder.add("item.spores--shadows.waxed_" + logName, getTranslation(wood, "log", "waxed"));
            translationBuilder.add("item.spores--shadows.tainted_" + logName, getTranslation(wood, "log", "tainted"));
            translationBuilder.add("item.spores--shadows.moldy_" + logName, getTranslation(wood, "log", "moldy"));
            translationBuilder.add("item.spores--shadows.rotten_" + logName, getTranslation(wood, "log", "rotten"));

            // Stripped Logs
            translationBuilder.add("block.spores--shadows.moldy_stripped_" + logName, getTranslation(wood, "stripped_log", "moldy"));
            translationBuilder.add("item.spores--shadows.waxed_stripped_" + logName, getTranslation(wood, "stripped_log", "waxed"));
            translationBuilder.add("item.spores--shadows.tainted_stripped_" + logName, getTranslation(wood, "stripped_log", "tainted"));
            translationBuilder.add("item.spores--shadows.moldy_stripped_" + logName, getTranslation(wood, "stripped_log", "moldy"));
            translationBuilder.add("item.spores--shadows.rotten_stripped_" + logName, getTranslation(wood, "stripped_log", "rotten"));

            // Wood
            translationBuilder.add("block.spores--shadows.moldy_" + woodName, getTranslation(wood, "wood", "moldy"));
            translationBuilder.add("item.spores--shadows.waxed_" + woodName, getTranslation(wood, "wood", "waxed"));
            translationBuilder.add("item.spores--shadows.tainted_" + woodName, getTranslation(wood, "wood", "tainted"));
            translationBuilder.add("item.spores--shadows.moldy_" + woodName, getTranslation(wood, "wood", "moldy"));
            translationBuilder.add("item.spores--shadows.rotten_" + woodName, getTranslation(wood, "wood", "rotten"));

            // Stripped Wood
            translationBuilder.add("block.spores--shadows.moldy_stripped_" + woodName, getTranslation(wood, "stripped_wood", "moldy"));
            translationBuilder.add("item.spores--shadows.waxed_stripped_" + woodName, getTranslation(wood, "stripped_wood", "waxed"));
            translationBuilder.add("item.spores--shadows.tainted_stripped_" + woodName, getTranslation(wood, "stripped_wood", "tainted"));
            translationBuilder.add("item.spores--shadows.moldy_stripped_" + woodName, getTranslation(wood, "stripped_wood", "moldy"));
            translationBuilder.add("item.spores--shadows.rotten_stripped_" + woodName, getTranslation(wood, "stripped_wood", "rotten"));

            // Planks
            translationBuilder.add("block.spores--shadows.moldy_" + prefix + "_planks", getTranslation(wood, "planks", "moldy"));
            translationBuilder.add("item.spores--shadows.waxed_" + prefix + "_planks", getTranslation(wood, "planks", "waxed"));
            translationBuilder.add("item.spores--shadows.tainted_" + prefix + "_planks", getTranslation(wood, "planks", "tainted"));
            translationBuilder.add("item.spores--shadows.moldy_" + prefix + "_planks", getTranslation(wood, "planks", "moldy"));
            translationBuilder.add("item.spores--shadows.rotten_" + prefix + "_planks", getTranslation(wood, "planks", "rotten"));

            // Mod Blocks
            String[] blocks = {"slab", "stairs", "fence", "fence_gate", "door", "trapdoor", "pressure_plate", "button"};
            for (String blockKey : blocks) {
                translationBuilder.add("block.spores--shadows.moldy_" + prefix + "_" + blockKey, getTranslation(wood, blockKey, "moldy"));
                translationBuilder.add("item.spores--shadows.waxed_" + prefix + "_" + blockKey, getTranslation(wood, blockKey, "waxed"));
                translationBuilder.add("item.spores--shadows.tainted_" + prefix + "_" + blockKey, getTranslation(wood, blockKey, "tainted"));
                translationBuilder.add("item.spores--shadows.moldy_" + prefix + "_" + blockKey, getTranslation(wood, blockKey, "moldy"));
                translationBuilder.add("item.spores--shadows.rotten_" + prefix + "_" + blockKey, getTranslation(wood, blockKey, "rotten"));
            }
        }
        
        generateTooltipsAndConfig(translationBuilder);
    }

    /**
     * Helper to assemble the localized string for a specific combination.
     * @param wood The wood type, e.g. "oak" or "dark_oak"
     * @param blockType The type of block, e.g. "log", "stripped_wood", "planks", "stairs"
     * @param state The degradation state: "moldy", "waxed", "tainted", "rotten"
     * @return The fully translated name.
     */
    protected abstract String getTranslation(String wood, String blockType, String state);

    protected abstract void generateTooltipsAndConfig(TranslationBuilder builder);
}
