package moldmod.client.datagen;

import moldmod.SporesShadows;
import moldmod.SporesShadowsConstants;
import moldmod.SporesShadowsConstants.MoldStage;
import moldmod.SporesShadowsConstants.MoldyWoodType;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractModLanguageProvider extends FabricLanguageProvider {

    public AbstractModLanguageProvider(FabricDataOutput dataOutput, String languageCode, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, languageCode, registryLookup);
    }

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup registryLookup, TranslationBuilder translationBuilder) {
        for (MoldyWoodType woodType : SporesShadowsConstants.WOOD_TYPES) {
            String wood = woodType.name();
            String logName = woodType.getLogName();
            String woodName = woodType.getWoodName();
            String prefix = wood;

            // Block mapping for translations
            Map<String, String> blockSuffixMap = new LinkedHashMap<>();
            blockSuffixMap.put(logName, "log");
            blockSuffixMap.put("stripped_" + logName, "stripped_log");
            blockSuffixMap.put(woodName, "wood");
            blockSuffixMap.put("stripped_" + woodName, "stripped_wood");
            blockSuffixMap.put(prefix + "_planks", "planks");

            for (String blockKey : SporesShadowsConstants.BLOCK_TYPES) {
                blockSuffixMap.put(prefix + "_" + blockKey, blockKey);
            }

            for (Map.Entry<String, String> entry : blockSuffixMap.entrySet()) {
                String suffix = entry.getKey();
                String type = entry.getValue();

                // Block translations (only for moldy and waxed base)
                translationBuilder.add("block." + SporesShadows.MOD_ID + ".moldy_" + suffix, getTranslation(wood, type, "moldy"));
                translationBuilder.add("block." + SporesShadows.MOD_ID + ".waxed_" + suffix, getTranslation(wood, type, "waxed"));

                // Item translations for all stages
                for (MoldStage stage : MoldStage.values()) {
                    if (stage == MoldStage.WAXED) {
                        translationBuilder.add("item." + SporesShadows.MOD_ID + ".waxed_" + suffix, getTranslation(wood, type, "waxed"));
                    } else {
                        translationBuilder.add("item." + SporesShadows.MOD_ID + "." + stage.getName() + "_" + suffix, getTranslation(wood, type, stage.getName()));
                        translationBuilder.add("item." + SporesShadows.MOD_ID + ".waxed_" + stage.getName() + "_" + suffix, getTranslation(wood, type, "waxed_" + stage.getName()));
                    }
                }
            }
        }
        
        generateTooltipsAndConfig(translationBuilder);
    }

    /**
     * Helper to assemble the localized string for a specific combination.
     * @param wood The wood type, e.g. "oak" or "dark_oak"
     * @param blockType The type of block, e.g. "log", "stripped_wood", "planks", "stairs"
     * @param state The degradation state: "moldy", "waxed", "tainted", "rotten", "waxed_tainted", "waxed_moldy", "waxed_rotten"
     * @return The fully translated name.
     */
    protected abstract String getTranslation(String wood, String blockType, String state);

    protected abstract void generateTooltipsAndConfig(TranslationBuilder builder);
}



