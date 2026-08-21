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
        String[] woods = moldmod.SporesShadows.WOODS;

        for (String wood : woods) {
            String logName = wood + "_log";
            String woodName = wood + "_wood";
            String prefix = wood;
            
            String capitalizedWood = capitalize(wood.replace("_", " "));

            translationBuilder.add("block.spores--shadows.moldy_" + logName, "Moldy " + capitalizedWood + " Log");
            translationBuilder.add("item.spores--shadows.waxed_" + logName, "Waxed " + capitalizedWood + " Log");
            translationBuilder.add("item.spores--shadows.tainted_" + logName, "Tainted " + capitalizedWood + " Log");
            translationBuilder.add("item.spores--shadows.moldy_" + logName, "Moldy " + capitalizedWood + " Log");
            translationBuilder.add("item.spores--shadows.rotten_" + logName, "Rotten " + capitalizedWood + " Log");

            translationBuilder.add("block.spores--shadows.moldy_stripped_" + logName, "Moldy Stripped " + capitalizedWood + " Log");
            translationBuilder.add("item.spores--shadows.waxed_stripped_" + logName, "Waxed Stripped " + capitalizedWood + " Log");
            translationBuilder.add("item.spores--shadows.tainted_stripped_" + logName, "Tainted Stripped " + capitalizedWood + " Log");
            translationBuilder.add("item.spores--shadows.moldy_stripped_" + logName, "Moldy Stripped " + capitalizedWood + " Log");
            translationBuilder.add("item.spores--shadows.rotten_stripped_" + logName, "Rotten Stripped " + capitalizedWood + " Log");

            if (woodName != null) {
                translationBuilder.add("block.spores--shadows.moldy_" + woodName, "Moldy " + capitalizedWood + " Wood");
                translationBuilder.add("item.spores--shadows.waxed_" + woodName, "Waxed " + capitalizedWood + " Wood");
                translationBuilder.add("item.spores--shadows.tainted_" + woodName, "Tainted " + capitalizedWood + " Wood");
                translationBuilder.add("item.spores--shadows.moldy_" + woodName, "Moldy " + capitalizedWood + " Wood");
                translationBuilder.add("item.spores--shadows.rotten_" + woodName, "Rotten " + capitalizedWood + " Wood");

                translationBuilder.add("block.spores--shadows.moldy_stripped_" + woodName, "Moldy Stripped " + capitalizedWood + " Wood");
                translationBuilder.add("item.spores--shadows.waxed_stripped_" + woodName, "Waxed Stripped " + capitalizedWood + " Wood");
                translationBuilder.add("item.spores--shadows.tainted_stripped_" + woodName, "Tainted Stripped " + capitalizedWood + " Wood");
                translationBuilder.add("item.spores--shadows.moldy_stripped_" + woodName, "Moldy Stripped " + capitalizedWood + " Wood");
                translationBuilder.add("item.spores--shadows.rotten_stripped_" + woodName, "Rotten Stripped " + capitalizedWood + " Wood");
            }

            translationBuilder.add("block.spores--shadows.moldy_" + prefix + "_planks", "Moldy " + capitalizedWood + " Planks");
            translationBuilder.add("item.spores--shadows.waxed_" + prefix + "_planks", "Waxed " + capitalizedWood + " Planks");
            translationBuilder.add("item.spores--shadows.tainted_" + prefix + "_planks", "Tainted " + capitalizedWood + " Planks");
            translationBuilder.add("item.spores--shadows.moldy_" + prefix + "_planks", "Moldy " + capitalizedWood + " Planks");
            translationBuilder.add("item.spores--shadows.rotten_" + prefix + "_planks", "Rotten " + capitalizedWood + " Planks");
            
            String[] blocks = {"slab", "stairs", "fence", "fence_gate", "door", "trapdoor", "pressure_plate", "button"};
            String[] blockNames = {"Slab", "Stairs", "Fence", "Fence Gate", "Door", "Trapdoor", "Pressure Plate", "Button"};
            
            for (int i = 0; i < blocks.length; i++) {
                String blockKey = blocks[i];
                String blockDisplayName = blockNames[i];
                translationBuilder.add("block.spores--shadows.moldy_" + prefix + "_" + blockKey, "Moldy " + capitalizedWood + " " + blockDisplayName);
                translationBuilder.add("item.spores--shadows.waxed_" + prefix + "_" + blockKey, "Waxed " + capitalizedWood + " " + blockDisplayName);
                translationBuilder.add("item.spores--shadows.tainted_" + prefix + "_" + blockKey, "Tainted " + capitalizedWood + " " + blockDisplayName);
                translationBuilder.add("item.spores--shadows.moldy_" + prefix + "_" + blockKey, "Moldy " + capitalizedWood + " " + blockDisplayName);
                translationBuilder.add("item.spores--shadows.rotten_" + prefix + "_" + blockKey, "Rotten " + capitalizedWood + " " + blockDisplayName);
            }
        }

        translationBuilder.add("tooltip.spores--shadows.waxed", "Waxed");
        translationBuilder.add("item.spores--shadows.waxed_format", "Waxed %s");
        translationBuilder.add("tooltip.spores--shadows.moldy_log_desc_1", "Can be broken down into clean planks with material loss,");
        translationBuilder.add("tooltip.spores--shadows.moldy_log_desc_2", "but cannot be used for normal vanilla recipes.");
        translationBuilder.add("tooltip.spores--shadows.moldy_planks_desc_1", "Only useful for simple crafting (sticks, fences, etc).");
        translationBuilder.add("tooltip.spores--shadows.moldy_planks_desc_2", "Cannot be used in complex recipes at full efficiency.");
        translationBuilder.add("tooltip.spores--shadows.moldy_general_desc_1", "Degraded wood component.");
        translationBuilder.add("tooltip.spores--shadows.moldy_general_desc_2", "Structurally weakened by mold.");

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
