package moldmod.client.datagen;

import moldmod.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;
import java.util.concurrent.CompletableFuture;

public class ModEnglishLanguageProvider extends FabricLanguageProvider {
    public ModEnglishLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup registryLookup, TranslationBuilder translationBuilder) {
        translationBuilder.add(ModBlocks.MOLDY_OAK_LOG, "Moldy Oak Log");
        translationBuilder.add(ModBlocks.TAINTED_OAK_LOG, "Tainted Oak Log");
        translationBuilder.add(ModBlocks.MOLDY_OAK_LOG_ITEM, "Moldy Oak Log");
        translationBuilder.add(ModBlocks.ROTTEN_OAK_LOG, "Rotten Oak Log");
        
        translationBuilder.add(ModBlocks.MOLDY_STRIPPED_OAK_LOG, "Moldy Stripped Oak Log");
        translationBuilder.add(ModBlocks.TAINTED_STRIPPED_OAK_LOG, "Tainted Stripped Oak Log");
        translationBuilder.add(ModBlocks.MOLDY_STRIPPED_OAK_LOG_ITEM, "Moldy Stripped Oak Log");
        translationBuilder.add(ModBlocks.ROTTEN_STRIPPED_OAK_LOG, "Rotten Stripped Oak Log");

        translationBuilder.add(ModBlocks.MOLDY_OAK_WOOD, "Moldy Oak Wood");
        translationBuilder.add(ModBlocks.TAINTED_OAK_WOOD, "Tainted Oak Wood");
        translationBuilder.add(ModBlocks.MOLDY_OAK_WOOD_ITEM, "Moldy Oak Wood");
        translationBuilder.add(ModBlocks.ROTTEN_OAK_WOOD, "Rotten Oak Wood");

        translationBuilder.add(ModBlocks.MOLDY_STRIPPED_OAK_WOOD, "Moldy Stripped Oak Wood");
        translationBuilder.add(ModBlocks.TAINTED_STRIPPED_OAK_WOOD, "Tainted Stripped Oak Wood");
        translationBuilder.add(ModBlocks.MOLDY_STRIPPED_OAK_WOOD_ITEM, "Moldy Stripped Oak Wood");
        translationBuilder.add(ModBlocks.ROTTEN_STRIPPED_OAK_WOOD, "Rotten Stripped Oak Wood");

        translationBuilder.add(ModBlocks.MOLDY_OAK_PLANKS, "Moldy Oak Planks");
        translationBuilder.add(ModBlocks.TAINTED_OAK_PLANKS, "Tainted Oak Planks");
        translationBuilder.add(ModBlocks.MOLDY_OAK_PLANKS_ITEM, "Moldy Oak Planks");
        translationBuilder.add(ModBlocks.ROTTEN_OAK_PLANKS, "Rotten Oak Planks");

        translationBuilder.add("tooltip.spores--shadows.waxed", "Waxed");

        // Mod Menu / Cloth Config
        translationBuilder.add("text.autoconfig.spores_shadows.title", "Spores & Shadows Config");
        translationBuilder.add("text.autoconfig.spores_shadows.category.general", "General Settings");
        translationBuilder.add("text.autoconfig.spores_shadows.category.debug", "Debug Settings");
        translationBuilder.add("text.autoconfig.spores_shadows.option.enableMoldSpread", "Enable Mold Spread");
        translationBuilder.add("text.autoconfig.spores_shadows.option.enableMoldSpread.@Tooltip", "If false, mold will never grow naturally.");
        translationBuilder.add("text.autoconfig.spores_shadows.option.globalMoldRiskMultiplier", "Mold Risk Multiplier");
        translationBuilder.add("text.autoconfig.spores_shadows.option.globalMoldRiskMultiplier.@Tooltip[0]", "Multiplies the base risk calculated by the environment.");
        translationBuilder.add("text.autoconfig.spores_shadows.option.globalMoldRiskMultiplier.@Tooltip[1]", "1.0 = Default, 0.5 = Half speed, 2.0 = Double speed.");
        translationBuilder.add("text.autoconfig.spores_shadows.option.showDebugInChat", "Show Debug Info in Chat");
        translationBuilder.add("text.autoconfig.spores_shadows.option.showDebugInChat.@Tooltip", "Prints the calculated risk 'R' in the console/chat when a block ticks.");
    }
}
