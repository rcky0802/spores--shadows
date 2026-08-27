package moldmod.client.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class ModEnglishLanguageProvider extends AbstractModLanguageProvider {

    public ModEnglishLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, "en_us", registryLookup);
    }

    @Override
    protected String getTranslation(String wood, String blockType, String state) {
        String capitalizedWood = capitalize(wood.replace("_", " "));
        String stateStr = capitalize(state.replace("_", " "));
        
        String typeStr = capitalize(blockType.replace("_", " "));
        if (blockType.equals("pressure_plate")) typeStr = "Pressure Plate";
        else if (blockType.equals("fence_gate")) typeStr = "Fence Gate";
        else if (blockType.equals("stripped_log")) typeStr = "Stripped Log";
        else if (blockType.equals("stripped_wood")) typeStr = "Stripped Wood";
        
        return stateStr + " " + capitalizedWood + " " + typeStr;
    }

    @Override
    protected void generateTooltipsAndConfig(TranslationBuilder translationBuilder) {
        translationBuilder.add("tooltip.spores--shadows.waxed", "Waxed");
        translationBuilder.add("item.spores--shadows.waxed_format", "Waxed %s");
        translationBuilder.add("tooltip.spores--shadows.moldy_log_desc_1", "Can be broken down into clean planks with material loss,");
        translationBuilder.add("tooltip.spores--shadows.moldy_log_desc_2", "but cannot be used for normal vanilla recipes.");
        translationBuilder.add("tooltip.spores--shadows.moldy_planks_desc_1", "Only useful for simple crafting (sticks, fences, etc).");
        translationBuilder.add("tooltip.spores--shadows.moldy_planks_desc_2", "Cannot be used in complex recipes at full efficiency.");
        translationBuilder.add("tooltip.spores--shadows.moldy_general_desc_1", "Degraded wood component.");
        translationBuilder.add("tooltip.spores--shadows.moldy_general_desc_2", "Structurally weakened by mold.");
        translationBuilder.add("tooltip.spores--shadows.moldy_redstone_desc_1", "Mold has compromised the mechanism.");
        translationBuilder.add("tooltip.spores--shadows.moldy_redstone_desc_2", "Activation duration is significantly longer.");

        translationBuilder.add("text.autoconfig.spores--shadows.title", "Spores & Shadows Config");
        translationBuilder.add("text.autoconfig.spores--shadows.category.general", "General");
        translationBuilder.add("text.autoconfig.spores--shadows.category.environment", "Environment");
        translationBuilder.add("text.autoconfig.spores--shadows.category.susceptibility", "Susceptibility");
        translationBuilder.add("text.autoconfig.spores--shadows.category.catalysts", "Catalysts");
        translationBuilder.add("text.autoconfig.spores--shadows.category.drops", "Drops");
        translationBuilder.add("text.autoconfig.spores--shadows.category.structures", "Structures");
        translationBuilder.add("text.autoconfig.spores--shadows.category.furnace_multipliers", "Furnace Fuel Efficiency");

        translationBuilder.add("text.autoconfig.spores--shadows.option.general", "General");
        translationBuilder.add("text.autoconfig.spores--shadows.option.susceptibility", "Susceptibility");
        translationBuilder.add("text.autoconfig.spores--shadows.option.catalysts", "Catalysts");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment", "Environment");
        translationBuilder.add("text.autoconfig.spores--shadows.option.drops", "Drops");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures", "Structures");
        translationBuilder.add("text.autoconfig.spores--shadows.option.furnaceMultipliers", "Furnace Fuel Efficiency");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.cave_temperature.@Tooltip", "Temperature to ignore the sun in caves.");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.high_altitude_freezing_temperature.@Tooltip", "Freezing temperature that stops mold at high altitudes.");
        translationBuilder.add("text.autoconfig.spores--shadows.option.general.enable_mold_growth", "Enable Mold Growth");
        translationBuilder.add("text.autoconfig.spores--shadows.option.general.infection_threshold", "Infection Threshold (R > X)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.general.scan_radius", "Scan Radius");
        translationBuilder.add("text.autoconfig.spores--shadows.option.general.scan_radius.@Tooltip", "1 = 3x3x3 blocks, 2 = 5x5x5 blocks. Higher values affect performance.");
        translationBuilder.add("text.autoconfig.spores--shadows.option.general.structures_immune", "Generated Structures are Immune");
        translationBuilder.add("text.autoconfig.spores--shadows.option.general.structures_immune.@Tooltip", "If true, naturally generated structures (shipwrecks, villages) will not rot until interacted with.");
        translationBuilder.add("text.autoconfig.spores--shadows.option.general.show_debug_in_chat", "Show Debug Math in Chat");

        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.rain_humidity_base", "Base Humidity (Raining/Snowing)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.dry_humidity_base", "Base Humidity (Clear/Dry)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.water_adjacent_bonus", "Water Adjacency Bonus");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.cauldron_adjacent_bonus", "Cauldron/Mud Adjacency Bonus");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.max_local_humidity_bonus", "Max Local Humidity Bonus");
        
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.cave_start_y", "Cave Start (Y Level)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.cave_full_y", "Cave Full Depth (Y Level)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.cave_temperature", "Cave Temperature");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.depth_modifier_per_level", "Depth Humidity Modifier (+ per level)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.max_depth_modifier", "Max Depth Humidity Modifier");
        
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.high_altitude_start_y", "High Altitude Start (Y Level)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.high_altitude_full_y", "High Altitude Peak (Y Level)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.high_altitude_freezing_temperature", "High Altitude Freezing Temp");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.min_temperature_survival", "Min Survival Temp");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.max_temperature_survival", "Max Survival Temp");

        translationBuilder.add("text.autoconfig.spores--shadows.option.susceptibility.stripped_wood_multiplier", "Stripped Wood Susceptibility");
        translationBuilder.add("text.autoconfig.spores--shadows.option.susceptibility.planks_multiplier", "Planks Susceptibility");
        translationBuilder.add("text.autoconfig.spores--shadows.option.susceptibility.default_multiplier", "Default Wood Susceptibility");
        
        translationBuilder.add("text.autoconfig.spores--shadows.option.catalysts.stage_2_drop_chance", "Stage 2 Drop Chance (Deprecated)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.catalysts.stage_3_drop_chance", "Stage 3 Drop Chance (Deprecated)");

        translationBuilder.add("text.autoconfig.spores--shadows.option.catalysts.mud_bonus", "Mud Bonus");
        translationBuilder.add("text.autoconfig.spores--shadows.option.catalysts.fungi_bonus", "Mushroom/Fungi Bonus");
        translationBuilder.add("text.autoconfig.spores--shadows.option.catalysts.spore_blossom_bonus", "Spore Blossom Bonus");
        translationBuilder.add("text.autoconfig.spores--shadows.option.catalysts.podzol_mycelium_bonus", "Podzol/Mycelium Bonus");
        translationBuilder.add("text.autoconfig.spores--shadows.option.catalysts.tainted_block_bonus", "Tainted Block Proximity Bonus");
        translationBuilder.add("text.autoconfig.spores--shadows.option.catalysts.moldy_block_bonus", "Moldy Block Proximity Bonus");
        translationBuilder.add("text.autoconfig.spores--shadows.option.catalysts.rotten_block_bonus", "Rotten Block Proximity Bonus");

        translationBuilder.add("text.autoconfig.spores--shadows.option.drops.stage_2_drop_chance", "Moldy Block Drop Chance (0.0 to 1.0)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.drops.stage_2_drop_chance.@Tooltip", "Chance for a moldy block to drop itself when broken.");
        translationBuilder.add("text.autoconfig.spores--shadows.option.drops.stage_3_drop_chance", "Rotten Block Drop Chance (0.0 to 1.0)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.drops.stage_3_drop_chance.@Tooltip", "Chance for a rotten block to drop itself when broken.");

        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat1_critical", "Category 1 (Critical Decay)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat2_high", "Category 2 (High Decay)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat3_moderate", "Category 3 (Moderate Decay)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat4_low", "Category 4 (Low Decay)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat1_critical.moldy_chance", "Moldy %");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat2_high.moldy_chance", "Moldy %");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat3_moderate.moldy_chance", "Moldy %");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat4_low.moldy_chance", "Moldy %");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat1_critical.tainted_chance", "Tainted %");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat2_high.tainted_chance", "Tainted %");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat3_moderate.tainted_chance", "Tainted %");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat4_low.tainted_chance", "Tainted %");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat1_critical.rotten_chance", "Rotten %");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat2_high.rotten_chance", "Rotten %");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat3_moderate.rotten_chance", "Rotten %");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat4_low.rotten_chance", "Rotten %");

        translationBuilder.add("text.autoconfig.spores--shadows.option.furnaceMultipliers.stage_0", "Vanilla Stage Fuel Multiplier");
        translationBuilder.add("text.autoconfig.spores--shadows.option.furnaceMultipliers.stage_1", "Tainted Stage Fuel Multiplier");
        translationBuilder.add("text.autoconfig.spores--shadows.option.furnaceMultipliers.stage_2", "Moldy Stage Fuel Multiplier");
        translationBuilder.add("text.autoconfig.spores--shadows.option.furnaceMultipliers.stage_3", "Rotten Stage Fuel Multiplier");

        translationBuilder.add("text.autoconfig.spores--shadows.category.toxicity", "Toxicity");
        translationBuilder.add("text.autoconfig.spores--shadows.option.toxicity", "Toxicity");
        translationBuilder.add("text.autoconfig.spores--shadows.option.toxicity.check_interval_ticks", "Check Interval (Ticks)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.toxicity.scan_radius", "Scan Radius");
        translationBuilder.add("text.autoconfig.spores--shadows.option.toxicity.threshold_nausea", "Nausea Threshold");
        translationBuilder.add("text.autoconfig.spores--shadows.option.toxicity.threshold_poison", "Poison Threshold");
        translationBuilder.add("text.autoconfig.spores--shadows.option.toxicity.duration_nausea_ticks", "Nausea Duration (Ticks)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.toxicity.duration_poison_ticks", "Poison Duration (Ticks)");
        
        translationBuilder.add("text.autoconfig.spores--shadows.category.client", "Client & Shaders");
        translationBuilder.add("text.autoconfig.spores--shadows.option.client", "Client & Shaders");
        translationBuilder.add("text.autoconfig.spores--shadows.option.client.mold_z_offset", "Mold Z-Offset (Shader Fix)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.client.mold_z_offset.@Tooltip[0]", "Adjust this if you experience Z-fighting (flickering) with shaders.");
        translationBuilder.add("text.autoconfig.spores--shadows.option.client.mold_z_offset.@Tooltip[1]", "Default: 0.002. Try 0.005 or higher if needed.");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.water_scan_radius", "Water Scan Radius");

        // Jade Tooltips
        translationBuilder.add("tooltip.spores--shadows.jade.stage", "Stage: ");
        translationBuilder.add("tooltip.spores--shadows.jade.infection", "Infection Risk: %d%%");
        translationBuilder.add("tooltip.spores--shadows.jade.waxed", "Waxed: ");
        translationBuilder.add("tooltip.spores--shadows.jade.yes", "Yes");
        translationBuilder.add("tooltip.spores--shadows.jade.no", "No");
        translationBuilder.add("tooltip.spores--shadows.jade.stage.0", "Healthy");
        translationBuilder.add("tooltip.spores--shadows.jade.stage.1", "Tainted");
        translationBuilder.add("tooltip.spores--shadows.jade.stage.2", "Moldy");
        translationBuilder.add("tooltip.spores--shadows.jade.stage.3", "Rotten");
        translationBuilder.add("config.jade.plugin_spores--shadows.moldy_info", "Spores & Shadows: Mold Info");

        // Advancements
        translationBuilder.add("advancements.spores--shadows.root.title", "Spores & Shadows");
        translationBuilder.add("advancements.spores--shadows.root.description", "Survive the decay of nature.");
        translationBuilder.add("advancements.spores--shadows.wax_block.title", "Natural Prevention");
        translationBuilder.add("advancements.spores--shadows.wax_block.description", "Use a honeycomb to wax a wood block and stop the mold.");
        translationBuilder.add("advancements.spores--shadows.scrape_mold.title", "Elbow Grease");
        translationBuilder.add("advancements.spores--shadows.scrape_mold.description", "Scrape the mold off a wood block using an axe.");
        translationBuilder.add("advancements.spores--shadows.toxic_air.title", "Short Breath");
        translationBuilder.add("advancements.spores--shadows.toxic_air.description", "Suffer the poison of the miasma by breathing too much mold.");
        translationBuilder.add("advancements.spores--shadows.crumble.title", "Dust to Dust");
        translationBuilder.add("advancements.spores--shadows.crumble.description", "Attempt to break a rotten wood block and watch it crumble into nothing.");
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
