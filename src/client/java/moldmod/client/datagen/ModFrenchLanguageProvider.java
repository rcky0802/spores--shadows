package moldmod.client.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;
import java.util.Map;
import java.util.HashMap;

public class ModFrenchLanguageProvider extends FabricLanguageProvider {

    public ModFrenchLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, "fr_fr", registryLookup);
    }

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup registryLookup, TranslationBuilder translationBuilder) {
        String[] woods = moldmod.SporesShadows.WOODS;

        Map<String, String> names = new HashMap<>();
        names.put("oak", "de Chêne");
        names.put("spruce", "de Sapin");
        names.put("birch", "de Bouleau");
        names.put("jungle", "d'Acajou");
        names.put("acacia", "d'Acacia");
        names.put("dark_oak", "de Chêne Noir");
        names.put("mangrove", "de Palétuvier");
        names.put("cherry", "de Cerisier");

        for (String wood : woods) {
            String logName = wood + "_log";
            String woodName = wood + "_wood";
            String prefix = wood;
            
            String wName = names.get(wood);
            
            translationBuilder.add("block.spores--shadows.moldy_" + logName, "Bûche " + wName + " moisie");
            translationBuilder.add("item.spores--shadows.waxed_" + logName, "Bûche " + wName + " cirée");
            translationBuilder.add("item.spores--shadows.tainted_" + logName, "Bûche " + wName + " entachée");
            translationBuilder.add("item.spores--shadows.moldy_" + logName, "Bûche " + wName + " moisie");
            translationBuilder.add("item.spores--shadows.rotten_" + logName, "Bûche " + wName + " pourrie");

            translationBuilder.add("block.spores--shadows.moldy_stripped_" + logName, "Bûche écorcée " + wName + " moisie");
            translationBuilder.add("item.spores--shadows.waxed_stripped_" + logName, "Bûche écorcée " + wName + " cirée");
            translationBuilder.add("item.spores--shadows.tainted_stripped_" + logName, "Bûche écorcée " + wName + " entachée");
            translationBuilder.add("item.spores--shadows.moldy_stripped_" + logName, "Bûche écorcée " + wName + " moisie");
            translationBuilder.add("item.spores--shadows.rotten_stripped_" + logName, "Bûche écorcée " + wName + " pourrie");

            if (woodName != null) {
                translationBuilder.add("block.spores--shadows.moldy_" + woodName, "Bois " + wName + " moisi");
                translationBuilder.add("item.spores--shadows.waxed_" + woodName, "Bois " + wName + " ciré");
                translationBuilder.add("item.spores--shadows.tainted_" + woodName, "Bois " + wName + " entaché");
                translationBuilder.add("item.spores--shadows.moldy_" + woodName, "Bois " + wName + " moisi");
                translationBuilder.add("item.spores--shadows.rotten_" + woodName, "Bois " + wName + " pourri");

                translationBuilder.add("block.spores--shadows.moldy_stripped_" + woodName, "Bois écorcé " + wName + " moisi");
                translationBuilder.add("item.spores--shadows.waxed_stripped_" + woodName, "Bois écorcé " + wName + " ciré");
                translationBuilder.add("item.spores--shadows.tainted_stripped_" + woodName, "Bois écorcé " + wName + " entaché");
                translationBuilder.add("item.spores--shadows.moldy_stripped_" + woodName, "Bois écorcé " + wName + " moisi");
                translationBuilder.add("item.spores--shadows.rotten_stripped_" + woodName, "Bois écorcé " + wName + " pourri");
            }

            translationBuilder.add("block.spores--shadows.moldy_" + prefix + "_planks", "Planches " + wName + " moisies");
            translationBuilder.add("item.spores--shadows.waxed_" + prefix + "_planks", "Planches " + wName + " cirées");
            translationBuilder.add("item.spores--shadows.tainted_" + prefix + "_planks", "Planches " + wName + " entachées");
            translationBuilder.add("item.spores--shadows.moldy_" + prefix + "_planks", "Planches " + wName + " moisies");
            translationBuilder.add("item.spores--shadows.rotten_" + prefix + "_planks", "Planches " + wName + " pourries");
            
            String[] blocks = {"slab", "stairs", "fence", "fence_gate", "door", "trapdoor", "pressure_plate", "button"};
            String[] blockNames = {"Dalle", "Escaliers", "Barrière", "Portillon", "Porte", "Trappe", "Plaque de pression", "Bouton"};
            
            String[] femC = {"cirée", "cirés", "cirée", "ciré", "cirée", "cirée", "cirée", "ciré"};
            String[] fem = {"moisie", "moisis", "moisie", "moisi", "moisie", "moisie", "moisie", "moisi"};
            String[] femT = {"entachée", "entachés", "entachée", "entaché", "entachée", "entachée", "entachée", "entaché"};
            String[] femR = {"pourrie", "pourris", "pourrie", "pourri", "pourrie", "pourrie", "pourrie", "pourri"};
            
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

        translationBuilder.add("tooltip.spores--shadows.waxed", "Ciré");
        translationBuilder.add("item.spores--shadows.waxed_format", "%s ciré");
        translationBuilder.add("tooltip.spores--shadows.moldy_log_desc_1", "Peut être transformé en planches propres avec perte de matériau,");
        translationBuilder.add("tooltip.spores--shadows.moldy_log_desc_2", "mais ne peut être utilisé pour les recettes vanilla normales.");
        translationBuilder.add("tooltip.spores--shadows.moldy_planks_desc_1", "Utile uniquement pour les fabrications simples (bâtons, barrières).");
        translationBuilder.add("tooltip.spores--shadows.moldy_planks_desc_2", "Ne peut pas être utilisé efficacement dans des recettes complexes.");
        translationBuilder.add("tooltip.spores--shadows.moldy_general_desc_1", "Composant en bois dégradé.");
        translationBuilder.add("tooltip.spores--shadows.moldy_general_desc_2", "Structurellement affaibli par la moisissure.");

        translationBuilder.add("text.autoconfig.spores--shadows.title", "Configuration Spores & Shadows");
        translationBuilder.add("text.autoconfig.spores--shadows.category.general", "Général");
        translationBuilder.add("text.autoconfig.spores--shadows.category.environment", "Environnement");
        translationBuilder.add("text.autoconfig.spores--shadows.category.susceptibility", "Sensibilité");
        translationBuilder.add("text.autoconfig.spores--shadows.category.catalysts", "Catalyseurs");
        translationBuilder.add("text.autoconfig.spores--shadows.category.drops", "Butins");
        translationBuilder.add("text.autoconfig.spores--shadows.category.structures", "Structures");
        translationBuilder.add("text.autoconfig.spores--shadows.category.furnace_multipliers", "Efficacité du Four");

        translationBuilder.add("text.autoconfig.spores--shadows.option.general", "Général");
        translationBuilder.add("text.autoconfig.spores--shadows.option.susceptibility", "Sensibilité");
        translationBuilder.add("text.autoconfig.spores--shadows.option.catalysts", "Catalyseurs");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment", "Environnement");
        translationBuilder.add("text.autoconfig.spores--shadows.option.drops", "Butins");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures", "Structures");
        translationBuilder.add("text.autoconfig.spores--shadows.option.furnaceMultipliers", "Efficacité du Four");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.cave_temperature.@Tooltip", "Température pour ignorer le soleil dans les grottes.");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.high_altitude_freezing_temperature.@Tooltip", "Température de gel qui arrête la moisissure en haute altitude.");
        translationBuilder.add("text.autoconfig.spores--shadows.option.general.enable_mold_growth", "Activer la croissance de la moisissure");
        translationBuilder.add("text.autoconfig.spores--shadows.option.general.infection_threshold", "Seuil d'infection (R > X)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.general.scan_radius", "Rayon de recherche");
        translationBuilder.add("text.autoconfig.spores--shadows.option.general.scan_radius.@Tooltip", "1 = 3x3x3, 2 = 5x5x5. Les grandes valeurs causent du lag.");
        translationBuilder.add("text.autoconfig.spores--shadows.option.general.structures_immune", "Immunité des structures générées");
        translationBuilder.add("text.autoconfig.spores--shadows.option.general.structures_immune.@Tooltip", "Si activé, les épaves et villages ne pourriront pas seuls avant l'interaction du joueur.");
        translationBuilder.add("text.autoconfig.spores--shadows.option.general.show_debug_in_chat", "Afficher le débogage dans le tchat");

        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.rain_humidity_base", "Humidité de base (Pluie/Neige)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.dry_humidity_base", "Humidité de base (Clair/Sec)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.water_adjacent_bonus", "Bonus de proximité de l'eau");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.cauldron_adjacent_bonus", "Bonus de proximité Chaudrons/Boue");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.max_local_humidity_bonus", "Bonus max d'humidité locale");
        
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.cave_start_y", "Début des grottes (Niveau Y)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.cave_full_y", "Profondeur pleine des grottes (Niveau Y)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.cave_temperature", "Température des grottes");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.depth_modifier_per_level", "Modificateur d'humidité par profondeur (+ par bloc)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.max_depth_modifier", "Malus max d'humidité de profondeur");
        
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.high_altitude_start_y", "Début de la haute altitude (Niveau Y)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.high_altitude_full_y", "Pic de haute altitude (Niveau Y)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.high_altitude_freezing_temperature", "Température de gel en haute altitude");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.min_temperature_survival", "Température minimale de survie");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.max_temperature_survival", "Température maximale de survie");

        translationBuilder.add("text.autoconfig.spores--shadows.option.susceptibility.stripped_wood_multiplier", "Sensibilité du bois écorcé");
        translationBuilder.add("text.autoconfig.spores--shadows.option.susceptibility.planks_multiplier", "Sensibilité des planches");
        translationBuilder.add("text.autoconfig.spores--shadows.option.susceptibility.default_multiplier", "Sensibilité du bois par défaut");
        
        translationBuilder.add("text.autoconfig.spores--shadows.option.catalysts.stage_2_drop_chance", "Stage 2 Drop Chance (Obsolète)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.catalysts.stage_3_drop_chance", "Stage 3 Drop Chance (Obsolète)");

        translationBuilder.add("text.autoconfig.spores--shadows.option.catalysts.mud_bonus", "Malus de Boue (Mud)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.catalysts.fungi_bonus", "Malus de Champignons");
        translationBuilder.add("text.autoconfig.spores--shadows.option.catalysts.spore_blossom_bonus", "Malus de Fleur sporifère (Spore Blossom)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.catalysts.podzol_mycelium_bonus", "Malus de Podzol/Mycélium");
        translationBuilder.add("text.autoconfig.spores--shadows.option.catalysts.tainted_block_bonus", "Malus de bloc entaché proche");
        translationBuilder.add("text.autoconfig.spores--shadows.option.catalysts.moldy_block_bonus", "Malus de bloc moisi proche");
        translationBuilder.add("text.autoconfig.spores--shadows.option.catalysts.rotten_block_bonus", "Malus de bloc pourri proche");

        translationBuilder.add("text.autoconfig.spores--shadows.option.drops.stage_2_drop_chance", "Probabilité de butin bloc moisi (0.0 à 1.0)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.drops.stage_2_drop_chance.@Tooltip", "Probabilité qu'un bloc moisi lâche l'objet lui-même.");
        translationBuilder.add("text.autoconfig.spores--shadows.option.drops.stage_3_drop_chance", "Probabilité de butin bloc pourri (0.0 à 1.0)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.drops.stage_3_drop_chance.@Tooltip", "Probabilité qu'un bloc pourri lâche l'objet lui-même.");

        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat1_critical", "Catégorie 1 (Dégradation critique)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat2_high", "Catégorie 2 (Dégradation forte)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat3_moderate", "Catégorie 3 (Dégradation modérée)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat4_low", "Catégorie 4 (Dégradation faible)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat1_critical.moldy_chance", "% Moisi");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat2_high.moldy_chance", "% Moisi");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat3_moderate.moldy_chance", "% Moisi");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat4_low.moldy_chance", "% Moisi");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat1_critical.tainted_chance", "% Entaché");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat2_high.tainted_chance", "% Entaché");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat3_moderate.tainted_chance", "% Entaché");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat4_low.tainted_chance", "% Entaché");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat1_critical.rotten_chance", "% Pourri");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat2_high.rotten_chance", "% Pourri");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat3_moderate.rotten_chance", "% Pourri");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat4_low.rotten_chance", "% Pourri");

        translationBuilder.add("text.autoconfig.spores--shadows.option.furnaceMultipliers.stage_0", "Efficacité du Four (Sain)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.furnaceMultipliers.stage_1", "Efficacité du Four (Entaché)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.furnaceMultipliers.stage_2", "Efficacité du Four (Moisi)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.furnaceMultipliers.stage_3", "Efficacité du Four (Pourri)");
    }
}
