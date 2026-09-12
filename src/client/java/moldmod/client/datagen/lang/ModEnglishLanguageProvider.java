package moldmod.client.datagen.lang;

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
        else if (blockType.equals("stem")) typeStr = "Stem";
        else if (blockType.equals("stripped_stem")) typeStr = "Stripped Stem";
        else if (blockType.equals("hyphae")) typeStr = "Hyphae";
        else if (blockType.equals("stripped_hyphae")) typeStr = "Stripped Hyphae";
        
        return stateStr + " " + capitalizedWood + " " + typeStr;
    }

    @Override
    protected void generateTooltipsAndConfig(TranslationBuilder translationBuilder) {
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".waxed", "Waxed");
        translationBuilder.add("item." + moldmod.SporesShadows.MOD_ID + ".waxed_format", "Waxed %s");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".moldy_log_desc_1", "Can be broken down into clean planks with material loss,");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".moldy_log_desc_2", "but cannot be used for normal vanilla recipes.");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".moldy_planks_desc_1", "Only useful for simple crafting (sticks, fences, etc).");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".moldy_planks_desc_2", "Cannot be used in complex recipes at full efficiency.");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".moldy_general_desc_1", "Degraded wood component.");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".moldy_general_desc_2", "Structurally weakened by mold.");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".moldy_redstone_desc_1", "Mold has compromised the mechanism.");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".moldy_redstone_desc_2", "Activation duration is significantly longer.");

        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".title", "Spores & Shadows Config");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.general", "General");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.environment", "Environment");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.susceptibility", "Susceptibility");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.catalysts", "Catalysts");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.drops", "Drops");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.structures", "Structures");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.furnace_multipliers", "Furnace Fuel Efficiency");

        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.general", "General");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.susceptibility", "Susceptibility");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.catalysts", "Catalysts");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment", "Environment");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.drops", "Drops");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures", "Structures");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.furnaceMultipliers", "Furnace Fuel Efficiency");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.cave_temperature.@Tooltip", "Temperature to ignore the sun in caves.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.high_altitude_freezing_temperature.@Tooltip", "Freezing temperature that stops mold at high altitudes.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.general.enable_mold_growth", "Enable Mold Growth");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.general.infection_threshold", "Infection Threshold (R > X)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.general.scan_radius", "Scan Radius");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.general.scan_radius.@Tooltip", "1 = 3x3x3 blocks, 2 = 5x5x5 blocks. Higher values affect performance.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.general.structures_immune", "Generated Structures are Immune");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.general.structures_immune.@Tooltip", "If true, naturally generated structures (shipwrecks, villages) will not rot until interacted with.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.general.show_debug_in_chat", "Show Debug Math in Chat");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.general.axe_scrape_damage", "Axe Scrape Damage");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.general.rotten_break_chance_on_use", "Rotten Block Break Chance On Use");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.general.rotten_break_chance_on_use.@Tooltip", "Chance for a rotten functional wooden block (doors, buttons, trapdoors, etc.) to break when used.");

        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.rain_humidity_base", "Base Humidity (Raining/Snowing)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.dry_humidity_base", "Base Humidity (Clear/Dry)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.cauldron_adjacent_bonus", "Cauldron/Mud Adjacency Bonus");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.water_source_humidity_contribution", "Water Source Humidity Contribution");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.water_source_humidity_contribution.@Tooltip", "Linear humidity added by each water block facing the room.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.max_room_water_humidity_bonus", "Max Room Water Humidity Bonus");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.max_room_water_humidity_bonus.@Tooltip", "Maximum saturation cap for humidity produced by room water sources.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.enable_dynamic_room_humidity", "Dynamic Room Humidity");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.enable_dynamic_room_humidity.@Tooltip", "Simulates asymptotic humidity accumulation and dissipation over time.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.humidity_saturation_speed", "Humidity Saturation Speed (Alpha)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.humidity_saturation_speed.@Tooltip", "Speed at which room humidity rises in enclosed or damp spaces.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.humidity_dissipation_speed", "Humidity Dissipation Speed (Alpha)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.humidity_dissipation_speed.@Tooltip", "Speed at which room humidity drops in ventilated spaces.");
        
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.cave_start_y", "Cave Start (Y Level)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.cave_full_y", "Cave Full Depth (Y Level)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.cave_temperature", "Cave Temperature");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.depth_modifier_per_level", "Depth Humidity Modifier (+ per level)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.max_depth_modifier", "Max Depth Humidity Modifier");
        
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.high_altitude_start_y", "High Altitude Start (Y Level)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.high_altitude_full_y", "High Altitude Peak (Y Level)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.high_altitude_freezing_temperature", "High Altitude Freezing Temp");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.min_temperature_survival", "Min Survival Temp");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.max_temperature_survival", "Max Survival Temp");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.enable_ventilation_drying", "Enable Ventilation Drying");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.enable_ventilation_drying.@Tooltip", "Reduces effective humidity in ventilated spaces.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.aeration_drying_bonus", "Aeration Drying Bonus");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.aeration_drying_bonus.@Tooltip", "Maximum humidity reduction applied by ventilation.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.ventilation_threshold_full_aeration", "Full Aeration Threshold");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.ventilation_threshold_full_aeration.@Tooltip", "Ventilation score required to achieve 100% drying effect.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.enable_miasma_spore_pressure", "Enable Miasma Spore Pressure");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.enable_miasma_spore_pressure.@Tooltip", "Causes trapped airborne spores to accelerate mold risk.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.miasma_spore_multiplier", "Miasma Spore Multiplier");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.miasma_spore_multiplier.@Tooltip", "Multiplier for mold risk increase from trapped miasma.");

        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.susceptibility.stripped_wood_multiplier", "Stripped Wood Susceptibility");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.susceptibility.planks_multiplier", "Planks Susceptibility");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.susceptibility.default_multiplier", "Default Wood Susceptibility");
        
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.catalysts.mud_bonus", "Mud Bonus");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.catalysts.fungi_bonus", "Mushroom/Fungi Bonus");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.catalysts.spore_blossom_bonus", "Spore Blossom Bonus");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.catalysts.podzol_mycelium_bonus", "Podzol/Mycelium Bonus");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.catalysts.tainted_block_bonus", "Tainted Block Proximity Bonus");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.catalysts.moldy_block_bonus", "Moldy Block Proximity Bonus");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.catalysts.rotten_block_bonus", "Rotten Block Proximity Bonus");

        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.drops.stage_2_drop_chance", "Moldy Block Drop Chance (0.0 to 1.0)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.drops.stage_2_drop_chance.@Tooltip", "Chance for a moldy block to drop itself when broken.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.drops.stage_3_drop_chance", "Rotten Block Drop Chance (0.0 to 1.0)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.drops.stage_3_drop_chance.@Tooltip", "Chance for a rotten block to drop itself when broken.");

        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat1_critical", "Category 1 (Critical Decay)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat2_high", "Category 2 (High Decay)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat3_moderate", "Category 3 (Moderate Decay)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat4_low", "Category 4 (Low Decay)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat1_critical.moldy_chance", "Moldy %");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat2_high.moldy_chance", "Moldy %");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat3_moderate.moldy_chance", "Moldy %");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat4_low.moldy_chance", "Moldy %");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat1_critical.tainted_chance", "Tainted %");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat2_high.tainted_chance", "Tainted %");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat3_moderate.tainted_chance", "Tainted %");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat4_low.tainted_chance", "Tainted %");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat1_critical.rotten_chance", "Rotten %");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat2_high.rotten_chance", "Rotten %");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat3_moderate.rotten_chance", "Rotten %");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat4_low.rotten_chance", "Rotten %");

        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.furnaceMultipliers.stage_0", "Vanilla Stage Fuel Multiplier");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.furnaceMultipliers.stage_1", "Tainted Stage Fuel Multiplier");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.furnaceMultipliers.stage_2", "Moldy Stage Fuel Multiplier");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.furnaceMultipliers.stage_3", "Rotten Stage Fuel Multiplier");

        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.toxicity", "Toxicity");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity", "Toxicity");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.enable_toxic_air", "Enable Toxic Air / Miasma");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.check_interval_ticks", "Check Interval (Ticks)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.scan_radius", "Scan Radius");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.max_air_volume", "Max Air Volume (m³)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.max_euclidean_radius", "Max Spherical Scan Radius");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.max_euclidean_radius.@Tooltip", "Maximum spherical scan distance for toxic air room checks.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.mold_toxicity_multiplier", "Mold Toxicity Multiplier");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.open_sky_ventilation_per_block", "Open Sky Ventilation Rate");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.slab_ventilation_value", "Slab Ventilation Value");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.stairs_ventilation_value", "Stairs Ventilation Value");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.copper_grate_ventilation_per_block", "Copper Grate Ventilation Rate");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.leaves_ventilation_value", "Leaves Ventilation Value");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.door_ventilation_value", "Door Ventilation Value");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.trapdoor_ventilation_value", "Trapdoor Ventilation Value");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.fence_gate_open_ventilation_value", "Fence Gate Open Ventilation Value");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.ventilation_gap_bonus", "Ventilation Gap Bonus");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.ventilation_distance_alpha", "Ventilation Distance Alpha");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.ventilation_distance_alpha.@Tooltip", "Resistance factor over distance for room airflow.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.enable_distributed_miasma", "Enable Distributed Miasma Flow");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.enable_distributed_miasma.@Tooltip", "Enables discrete graph flow calculations for miasma across connected room volumes.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.enable_dynamic_spore_saturation", "Enable Dynamic Spore Saturation");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.enable_dynamic_spore_saturation.@Tooltip", "Simulates gradual miasma accumulation and dissipation over time.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.dissipation_speed_multiplier", "Dissipation Speed Multiplier");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.dissipation_speed_multiplier.@Tooltip", "Rate at which miasma clears when room is ventilated.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.saturation_speed_multiplier", "Saturation Speed Multiplier");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.saturation_speed_multiplier.@Tooltip", "Rate at which miasma builds up in sealed contaminated rooms.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.threshold_hunger", "Hunger Threshold");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.threshold_nausea", "Nausea Threshold");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.threshold_poison", "Poison Threshold");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.density_threshold_high", "High Density Threshold");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.density_threshold_medium", "Medium Density Threshold");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.density_threshold_low", "Low Density Threshold");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.duration_hunger_ticks", "Hunger Duration (Ticks)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.duration_nausea_ticks", "Nausea Duration (Ticks)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.duration_poison_ticks", "Poison Duration (Ticks)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.hunger_amplifier", "Hunger Amplifier");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.nausea_amplifier", "Nausea Amplifier");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.poison_amplifier", "Poison Amplifier");
        
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.client", "Client & Shaders");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.client", "Client & Shaders");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.client.mold_z_offset", "Mold Z-Offset (Shader Fix)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.client.mold_z_offset.@Tooltip[0]", "Adjust this if you experience Z-fighting (flickering) with shaders.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.client.mold_z_offset.@Tooltip[1]", "Default: 0.002. Try 0.005 or higher if needed.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.water_scan_radius", "Water Scan Radius");

        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.flammability", "Flammability");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.flammability", "Flammability");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.flammability.enable_flammability", "Enable Flammability Scaling");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.flammability.stage_1_burn_bonus", "Stage 1 (Tainted) Burn Chance Bonus");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.flammability.stage_1_spread_bonus", "Stage 1 (Tainted) Spread Chance Bonus");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.flammability.stage_2_burn_bonus", "Stage 2 (Moldy) Burn Chance Bonus");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.flammability.stage_2_spread_bonus", "Stage 2 (Moldy) Spread Chance Bonus");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.flammability.stage_3_burn_bonus", "Stage 3 (Rotten) Burn Chance Bonus");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.flammability.stage_3_spread_bonus", "Stage 3 (Rotten) Spread Chance Bonus");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.flammability.waxed_burn_bonus", "Waxed Wood Burn Chance Bonus");

        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.blast_resistance", "Blast Resistance");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.blastResistance", "Blast Resistance");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.blastResistance.enable_blast_resistance_scaling", "Enable Blast Resistance Scaling");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.blastResistance.stage_1_multiplier", "Stage 1 (Tainted) Resistance Multiplier");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.blastResistance.stage_2_multiplier", "Stage 2 (Moldy) Resistance Multiplier");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.blastResistance.stage_3_multiplier", "Stage 3 (Rotten) Resistance Multiplier");

        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.hardness", "Hardness & Degradation");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.hardness", "Hardness & Degradation");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.hardness.enable_hardness_scaling", "Enable Hardness Scaling");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.hardness.stage_1_multiplier", "Stage 1 Hardness Multiplier (Tainted)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.hardness.stage_2_multiplier", "Stage 2 Hardness Multiplier (Moldy)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.hardness.stage_3_multiplier", "Stage 3 Hardness Multiplier (Rotten)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.hardness.enable_break_spore_cloud", "Spawn Spore Cloud on Break (No Silk Touch)");

        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.redstone", "Redstone");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.redstone", "Redstone");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.redstone.duration_multiplier", "Redstone Pulse Duration Multiplier");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.redstone.duration_multiplier.@Tooltip", "Multiplier applied to pulse duration for moldy buttons and pressure plates.");

        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.composter", "Composter");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.composter", "Composter");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.composter.tainted_chance", "Stage 1 (Tainted) Composting Chance");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.composter.tainted_chance.@Tooltip", "Chance for tainted wooden items to increase composter level.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.composter.moldy_chance", "Stage 2 (Moldy) Composting Chance");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.composter.moldy_chance.@Tooltip", "Chance for moldy wooden items to increase composter level.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.composter.rotten_chance", "Stage 3 (Rotten) Composting Chance");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.composter.rotten_chance.@Tooltip", "Chance for rotten wooden items to increase composter level.");

        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.particles", "Particles");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.particles", "Particles");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.particles.break_cloud_stage_2_air", "Stage 2 Spore Cloud Air Particle Count");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.particles.break_cloud_stage_2_falling", "Stage 2 Spore Cloud Falling Particle Count");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.particles.break_cloud_stage_2_mycelium", "Stage 2 Spore Cloud Mycelium Particle Count");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.particles.break_cloud_stage_3_air", "Stage 3 Spore Cloud Air Particle Count");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.particles.break_cloud_stage_3_falling", "Stage 3 Spore Cloud Falling Particle Count");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.particles.break_cloud_stage_3_mycelium", "Stage 3 Spore Cloud Mycelium Particle Count");

        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.spore_detector", "Spore Detector & Mask");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector", "Spore Detector & Mask");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.block_initial_delay_ticks", "Block Initial Delay (ticks)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.block_initial_delay_ticks.@Tooltip", "Ticks before first scan after block placement.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.block_periodic_delay_ticks", "Block Periodic Delay (ticks)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.block_periodic_delay_ticks.@Tooltip", "Ticks between periodic scans.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.redstone_level_multiplier", "Redstone Level Multiplier");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.redstone_level_multiplier.@Tooltip", "Multiplied by toxicity level (0-3) to produce the redstone signal.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.item_use_cooldown_ticks", "Item Use Cooldown (ticks)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.item_use_cooldown_ticks.@Tooltip", "Cooldown after right-click scan with the item.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.geiger_check_interval_ticks", "Geiger Check Interval (ticks)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.geiger_check_interval_ticks.@Tooltip", "How often the passive Geiger counter audio check runs when held.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.geiger_density_threshold", "Geiger Density Threshold");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.geiger_density_threshold.@Tooltip", "Minimum spore density to trigger Geiger counter sound.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.spore_mask_durability", "Spore Mask Durability");

        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.moisture_detector", "Moisture Detector");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.moistureDetector", "Moisture Detector");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.moistureDetector.block_initial_delay_ticks", "Block Initial Delay (ticks)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.moistureDetector.block_initial_delay_ticks.@Tooltip", "Ticks before first scan after block placement.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.moistureDetector.block_periodic_delay_ticks", "Block Periodic Delay (ticks)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.moistureDetector.block_periodic_delay_ticks.@Tooltip", "Ticks between periodic scans.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.moistureDetector.item_use_cooldown_ticks", "Item Use Cooldown (ticks)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.moistureDetector.item_use_cooldown_ticks.@Tooltip", "Cooldown after right-click scan with the item.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.spore_mask_durability.@Tooltip", "Maximum durability of the Spore Mask (requires restart).");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.spore_mask_armor_points", "Spore Mask Armor Points");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.spore_mask_armor_points.@Tooltip", "Armor points provided by the Spore Mask (requires restart).");

        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.dehumidifier", "Dehumidifier");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.dehumidifier", "Dehumidifier");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.dehumidifier.capacity_mb", "Water Tank Capacity (mB)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.dehumidifier.capacity_mb.@Tooltip", "Maximum condensed water storage in milliBuckets (1000 mB = 1 Bucket).");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.dehumidifier.ticks_per_mb", "Ticks Per mB Condensed");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.dehumidifier.ticks_per_mb.@Tooltip", "Base operating ticks required to condense 1 mB of water at high humidity.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.dehumidifier.fuel_multiplier", "Fuel Burn Time Multiplier");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.dehumidifier.fuel_multiplier.@Tooltip", "Multiplier applied to standard furnace burn times (e.g. 4.0 = coal lasts 4x longer).");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.dehumidifier.drying_power", "Drying Power (Bonus per Unit)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.dehumidifier.drying_power.@Tooltip", "Linear reduction subtracted from room target humidity per active running dehumidifier.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.dehumidifier.energy_capacity", "Energy Buffer Capacity (E/RF)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.dehumidifier.energy_capacity.@Tooltip", "Internal energy storage when external energy mods (TR/RF) are present.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.dehumidifier.energy_cost_per_tick", "Energy Consumption (E/tick)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.dehumidifier.energy_cost_per_tick.@Tooltip", "Energy consumed per tick while running instead of burning solid fuel.");

        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.underwater_rotten_bonus", "Underwater/Deep Rotten Bonus (%)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.underwater_rotten_bonus.@Tooltip", "Rotten percentage bonus when underwater or Y <= 60.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.underwater_tainted_bonus", "Underwater/Deep Tainted Bonus (%)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.underwater_tainted_bonus.@Tooltip", "Tainted percentage bonus when underwater or Y <= 60.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.underground_moldy_bonus", "Underground Moldy Bonus (%)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.underground_moldy_bonus.@Tooltip", "Moldy percentage bonus when deep underground (not underwater).");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.ground_contact_moldy_bonus", "Ground Contact Moldy Bonus (%)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.ground_contact_moldy_bonus.@Tooltip", "Moldy percentage bonus when near ground (rising damp).");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.sky_access_rotten_to_moldy", "Sky Access Rotten-to-Moldy Conversion (%)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.sky_access_rotten_to_moldy.@Tooltip", "Max rotten percentage converted to moldy with sky access.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.sky_access_moldy_bonus", "Sky Access Moldy Bonus (%)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.sky_access_moldy_bonus.@Tooltip", "Moldy percentage bonus when exposed to air/rain.");

        // Jade Tooltips
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.infection", "Infection Risk: %d%%");
        translationBuilder.add("config.jade.plugin_" + moldmod.SporesShadows.MOD_ID + ".moldy_info", "Spores & Shadows: Mold Info");

        // Advancements
        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".root.title", "Spores & Shadows");
        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".root.description", "Survive the decay of nature.");
        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".wax_block.title", "Natural Prevention");
        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".wax_block.description", "Use a honeycomb to wax a wood block and stop the mold.");
        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".scrape_mold.title", "Elbow Grease");
        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".scrape_mold.description", "Scrape the mold off a wood block using an axe.");
        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".toxic_air.title", "Short Breath");
        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".toxic_air.description", "Suffer the poison of the miasma by breathing too much mold.");
        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".crumble.title", "Dust to Dust");
        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".crumble.description", "Attempt to break a rotten wood block and watch it crumble into nothing.");

        // JEI
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".category.waxing", "Waxing");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".category.scraping", "Axe Scraping");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".info.rotten_wood", "Rotten wood is brittle and crumbling. It cannot be cured with an axe. It requires Silk Touch to be harvested, otherwise it will disintegrate into nothing when broken.");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".info.spore_mask", "The Spore Mask provides complete protection against toxic miasma (Poison, Nausea, and Hunger). It consumes durability while filtering toxic air. Replace the filter by repairing it with a Spore Filter in an anvil (fully repairs in one use). In a crafting grid, you can only combine two masks for a quick field repair. Can be enchanted only with Unbreaking, Mending, and Curse of Vanishing.");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".info.spore_filter", "Essential filtration cartridge. Used to craft and repair the Spore Mask on an anvil, and as a consumable replacement cartridge in the Air Purifier.");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".info.spore_filtration", "Spore Filtration is a helmet enchantment that neutralizes toxic miasma and spore inhalation. Consumes helmet durability when exposed to miasma (Level I: 2 durability, Level II: 1 durability, Level III: 50% durability save chance). Compatible with all conventional helmets.");

        // Jade Tooltips for Spore Protection & Spore Detector
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.spore_protection_mask", "Spore Protection: Active (Spore Mask)");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.spore_protection_enchant", "Spore Filtration: Level %d");
        translationBuilder.add("config.jade.plugin_" + moldmod.SporesShadows.MOD_ID + ".spore_protection_info", "Spores & Shadows: Spore Protection Info");
        translationBuilder.add("config.jade.plugin_" + moldmod.SporesShadows.MOD_ID + ".spore_detector_info", "Spores & Shadows: Spore Detector Info");

        translationBuilder.add("item." + moldmod.SporesShadows.MOD_ID + ".spore_mask", "Spore Mask");
        translationBuilder.add("item." + moldmod.SporesShadows.MOD_ID + ".spore_filter", "Spore Filter");
        translationBuilder.add("block." + moldmod.SporesShadows.MOD_ID + ".spore_detector", "Spore Detector");
        translationBuilder.add("item." + moldmod.SporesShadows.MOD_ID + ".spore_detector", "Spore Detector");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".info.spore_detector", "The Spore Detector measures air toxicity and room ventilation. Right-Click in the air to scan. Can be mounted on walls or floors.");

        translationBuilder.add("config.jade.plugin_" + moldmod.SporesShadows.MOD_ID + ".moisture_detector_info", "Spores & Shadows: Moisture Detector Info");
        translationBuilder.add("block." + moldmod.SporesShadows.MOD_ID + ".moisture_detector", "Moisture Detector");
        translationBuilder.add("item." + moldmod.SporesShadows.MOD_ID + ".moisture_detector", "Moisture Detector");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".info.moisture_detector", "The Moisture Detector measures effective humidity and environmental saturation. Right-Click in the air to scan. Can be mounted on walls, floors, or ceilings.");

        // Dehumidifier
        translationBuilder.add("block." + moldmod.SporesShadows.MOD_ID + ".dehumidifier", "Dehumidifier");
        translationBuilder.add("item." + moldmod.SporesShadows.MOD_ID + ".dehumidifier", "Dehumidifier");
        translationBuilder.add("container." + moldmod.SporesShadows.MOD_ID + ".dehumidifier", "Dehumidifier");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".info.dehumidifier", "Active environmental machinery for humidity regulation. Features dual operating modes: Dehumidification (extracts airborne moisture with 1.0 power and condenses pure water) or Humidification/Nebulization (consumes water to vaporize mist into dry rooms). Equipped with a 2000 mB internal water tank.");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".info.dehumidifier.energy", "Energy & Fuel: Supports hybrid dual-power. Accepts external RF / FE / TR Energy (32,000 FE buffer, 10 FE/tick operating cost with top priority). Alternatively burns solid fuels (Coal multiplied by 4x, lasting up to 6400 ticks) to generate electricity internally. Power cannot be extracted.");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".info.dehumidifier.water", "Water Production: In Dehumidify mode, the machine condenses moisture extracted from the room and produces pure liquid water in the internal tank (2000 mB at 1 mB per 24 ticks), extractable via buckets or fluid pipes. In Humidify mode, it consumes pure water supplied to the tank to vaporize it. Fluid pipes automatically adapt to the active mode (output in dehumidification, input in humidification).");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".info.dehumidifier.automation", "Automation & Controls: All sides accept solid fuel insertion via hoppers. Features GUI buttons to switch operating mode (Dehumidify/Humidify) and cycle redstone control modes (Ignored, Active with Signal, Active without Signal). Comparators emit a redstone signal proportional to the water level (0..15).");

        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".category.dehumidifier", "Dehumidification & Water");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".dehumidifier.dehumidify", "● Dehumidify Mode");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".dehumidifier.humidify", "● Humidify Mode");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".dehumidifier.rate", "+1 mB per 24 ticks");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".dehumidifier.humidify_rate", "-1 mB per 24 ticks");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".dehumidifier.tank", "Tank: 2000 mB");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".dehumidifier.power", "Drying Power: 1.0");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".dehumidifier.humidify_desc", "Vaporizes Moisture");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".dehumidifier.short_dehumidify", "Dehumidify");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".dehumidifier.short_humidify", "Humidify");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".dehumidifier.short_rate", "+1 mB / 24t");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".dehumidifier.short_humidify_rate", "-1 mB / 24t");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".dehumidifier.short_tank", "Max: 2000 mB");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".dehumidifier.short_power", "Pwr: 1.0");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".dehumidifier.energy_desc", "Power: 10 FE/t or Coal x4 (6400t)");

        // Air Purifier
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".category.air_purifier", "Purification & Filters");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".air_purifier.standard", "● Standard Decontamination");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".air_purifier.lethal", "● Lethal Miasma");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".air_purifier.cleaning", "-48 Miasma (Clean Air)");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".air_purifier.duration", "Duration: 2400 ticks (2 min)");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".air_purifier.lethal_duration", "Duration: 1200 ticks (1 min)");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".air_purifier.rate_standard", "1x normal consumption");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".air_purifier.rate_lethal", "2x doubled consumption");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".air_purifier.short_standard", "Purification");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".air_purifier.short_lethal", "Lethal Miasma");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".air_purifier.short_cleaning", "-48 Miasma");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".air_purifier.short_duration", "2400t (2 min)");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".air_purifier.short_lethal_duration", "1200t (1 min)");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".air_purifier.short_rate_standard", "Cons: 1x");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".air_purifier.short_rate_lethal", "Cons: 2x");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".air_purifier.energy_desc", "Power: 10 FE/t or Coal x4 (6400t)");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".info.air_purifier", "Active environmental machinery for toxic miasma decontamination. Purifies air in enclosed or underground rooms by removing 48 points of toxic miasma by default and restoring clean air quality (CLEAN). Requires filter cartridges to operate.");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".info.air_purifier.energy", "Energy & Fuel: Supports hybrid dual-power. Accepts external RF / FE / TR Energy (32,000 FE buffer, 10 FE/tick operating cost with top priority). Alternatively burns solid fuels (Coal multiplied by 4x, lasting up to 6400 ticks) to generate electricity internally. Power cannot be extracted.");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".info.air_purifier.filters", "Filter Consumption: To cleanse the air and remove 48 miasma points, the machine consumes Spore Filter cartridges loaded into the dedicated slot. Each filter provides 2400 ticks (2 minutes) of continuous purification, consuming twice as fast in lethal miasma. When depleted, the next filter is automatically drawn from the stack.");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".info.air_purifier.automation", "Automation & Controls: The top face (UP) accepts Spore Filters from hoppers, while side and bottom faces accept solid fuel. Features a GUI button to cycle redstone control modes (Ignored, Active with Signal, Active without Signal). Comparators emit a redstone signal proportional to filter reserve and integrity (0..15).");

        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".dehumidifier.water", "Water: %d / %d mB");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".dehumidifier.energy", "Energy: %d / %d E");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".dehumidifier.energy_usage", "Usage: %d E/t (%d E/s)");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".dehumidifier.redstone_mode", "Redstone Mode: %s");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".dehumidifier.redstone.ignored", "Ignored");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".dehumidifier.redstone.ignored.desc", "Always active while fueled");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".dehumidifier.redstone.low", "Low");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".dehumidifier.redstone.low.desc", "Active without signal; paused by redstone");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".dehumidifier.redstone.high", "High");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".dehumidifier.redstone.high.desc", "Active only when receiving redstone signal");
        translationBuilder.add("gui." + moldmod.SporesShadows.MOD_ID + ".dehumidifier.status.running", "● Active");
        translationBuilder.add("gui." + moldmod.SporesShadows.MOD_ID + ".dehumidifier.status.full", "● Full");
        translationBuilder.add("gui." + moldmod.SporesShadows.MOD_ID + ".dehumidifier.status.off", "● Idle");
        translationBuilder.add("gui." + moldmod.SporesShadows.MOD_ID + ".dehumidifier.status_desc.running", "Machine is actively dehumidifying the room.");
        translationBuilder.add("gui." + moldmod.SporesShadows.MOD_ID + ".dehumidifier.status_desc.full", "Internal water tank is full (2000 mB). Drain water to resume.");
        translationBuilder.add("gui." + moldmod.SporesShadows.MOD_ID + ".dehumidifier.status_desc.off", "Machine is idle (needs energy/fuel, valid room, or redstone signal).");

        translationBuilder.add("config.jade.plugin_" + moldmod.SporesShadows.MOD_ID + ".dehumidifier_info", "Spores & Shadows: Dehumidifier Info");
        translationBuilder.add("config.jade.plugin_" + moldmod.SporesShadows.MOD_ID + ".air_purifier_info", "Spores & Shadows: Air Purifier Info");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.air_purifier.filter", "Filter: %d%%");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.air_purifier.status.running", "Running");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.air_purifier.status.off", "Off");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.air_purifier.status.filter_depleted", "Filter Depleted");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.dehumidifier.status", "Status: ");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.dehumidifier.status.running", "Running");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.dehumidifier.status.full", "Standby (Full)");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.dehumidifier.status.off", "Off");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.dehumidifier.water", "Water: %d / %d mB");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.dehumidifier.energy", "Energy: %d E");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.dehumidifier.fuel_min_sec", "Fuel: %dm %ds");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.dehumidifier.fuel_sec", "Fuel: %ds");

        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".moisture_detector.title", "Moisture Sensing");
        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".moisture_detector.description", "Craft a Moisture Detector to monitor room humidity.");
        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".dehumidifier_craft.title", "Climate Control");
        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".dehumidifier_craft.description", "Craft a Dehumidifier to actively dry enclosed rooms and collect condensed water.");
        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".dry_oasis.title", "Subterranean Oasis");
        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".dry_oasis.description", "Dry an underground chamber (Y <= 40) down to less than 15% humidity using a Dehumidifier.");

        // Jade Tooltips for Detectors
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.spore_detector.air_quality", "Air Quality: ");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.spore_detector.clean", "Clean Air");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.spore_detector.warning", "Warning (Low)");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.spore_detector.moderate", "Moderate (Hunger)");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.spore_detector.lethal", "Lethal (Poison)");

        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.moisture_detector.effective_moisture", "Moisture Level: ");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.moisture_detector.dry", "Dry");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.moisture_detector.moderate", "Moderate");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.moisture_detector.humid", "Humid");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.moisture_detector.critical", "Critical");

        // Shared Detector Messages
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".detector.blocks_dist", "%d blocks");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".detector.none", "None");

        // Spore Detector Chat Messages
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".spore_detector.header", "§6[Spore Detector] ");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".spore_detector.clean", "§aCLEAN AIR §7(Safe)");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".spore_detector.warning", "§eWARNING §7(Low Spores floating in air)");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".spore_detector.moderate", "§6MODERATE RISK §7(Hunger imminent)");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".spore_detector.lethal", "§4LETHAL HAZARD §7(Poison & Nausea imminent!)");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".spore_detector.metric", "§7- Spore Density: §d%s/b");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".spore_detector.open_air", "§7- Environment: §aOpen Air §7| Dist to Vent: §b0 blocks");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".spore_detector.room", "§7- Room Volume: §f%d blocks §7| Dist to Vent: §b%s");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".spore_detector.aeration", "§7- Local Aeration: §a%s flow §7(§b%s§7) | Room Miasma: §6%s");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".spore_detector.trend_stable", "§7- Trend: §a= STABLE");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".spore_detector.trend_purifying", "§7- Trend: §b▼ PURIFYING / DISSIPATING §7(Target: §f%s§7)");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".spore_detector.trend_accumulating", "§7- Trend: §c▲ ACCUMULATING / SATURATING §7(Target: §f%s§7)");

        // Moisture Detector Chat Messages
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".moisture_detector.header", "§6[Moisture Detector] ");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".moisture_detector.dry", "§aDRY §7(Safe for wood structures)");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".moisture_detector.moderate", "§eMODERATE §7(Caution: mold threshold approaching)");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".moisture_detector.humid", "§6HUMID §7(Warning: high risk of mold growth!)");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".moisture_detector.critical", "§4CRITICAL §7(Danger: rapid mold decay & saturation!)");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".moisture_detector.metric", "§7- Effective Humidity: §b%s");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".moisture_detector.open_air", "§7- Environment: §aOpen Air §7| Dist to Vent: §b0 blocks");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".moisture_detector.room", "§7- Room Volume: §f%d blocks §7| Dist to Vent: §b%s");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".moisture_detector.aeration", "§7- Local Aeration: §a%s flow §7(§b%s§7) | Raw Humidity: §f%s");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".moisture_detector.trend_stable", "§7- Trend: §a= STABLE");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".moisture_detector.trend_drying", "§7- Trend: §b▼ DRYING / VENTILATING §7(Target: §f%s§7)");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".moisture_detector.trend_humidifying", "§7- Trend: §c▲ HUMIDIFYING / SATURATING §7(Target: §f%s§7)");

        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".spore_mask_protection.title", "Pure Air");
        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".spore_mask_protection.description", "Filter toxic spores by breathing through a Spore Mask in a contaminated room.");

        // Enchantments
        translationBuilder.add("enchantment." + moldmod.SporesShadows.MOD_ID + ".spore_filtration", "Spore Filtration");
        translationBuilder.add("enchantment." + moldmod.SporesShadows.MOD_ID + ".spore_filtration.desc", "Neutralizes toxic miasma by consuming helmet durability.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.enable_spore_mask_protection", "Enable Spore Mask Protection");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.enable_spore_mask_protection.@Tooltip", "Protects the wearer from toxic miasma and status effects.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.spore_mask_damage_per_exposure", "Spore Mask Exposure Durability Cost");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.spore_mask_damage_per_exposure.@Tooltip", "Durability consumed by the Spore Mask per miasma check.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.enable_spore_filtration_enchantment", "Enable Spore Filtration Enchantment");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.enable_spore_filtration_enchantment.@Tooltip", "Helmet enchantment that neutralizes toxic spores.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.filtration_level_1_durability_cost", "Filtration Level I Durability Cost");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.filtration_level_1_durability_cost.@Tooltip", "Helmet durability consumed per check with Level I.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.filtration_level_2_durability_cost", "Filtration Level II Durability Cost");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.filtration_level_2_durability_cost.@Tooltip", "Helmet durability consumed per check with Level II.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.filtration_level_3_save_chance", "Filtration Level III Durability Save Chance");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.filtration_level_3_save_chance.@Tooltip", "Chance to prevent durability loss with Level III.");

        // Tag Translations (Fabric Tag Convention v2)
        translationBuilder.add("tag.item." + moldmod.SporesShadows.MOD_ID + ".moldy_items", "Moldy Items");
        translationBuilder.add("tag.item." + moldmod.SporesShadows.MOD_ID + ".enchantable.filtration_helmets", "Filtration Helmets");
        translationBuilder.add("tag.item." + moldmod.SporesShadows.MOD_ID + ".moldy_blocks", "Moldy Blocks");
        translationBuilder.add("tag.block." + moldmod.SporesShadows.MOD_ID + ".moldy_blocks", "Moldy Blocks");
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


