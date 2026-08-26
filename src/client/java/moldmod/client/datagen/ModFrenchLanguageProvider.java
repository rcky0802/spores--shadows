package moldmod.client.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.registry.RegistryWrapper;

import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class ModFrenchLanguageProvider extends AbstractModLanguageProvider {

    public ModFrenchLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, "fr_fr", registryLookup);
    }

    @Override
    protected String getTranslation(String wood, String blockType, String state) {
        Map<String, String> names = new HashMap<>();
        names.put("oak", "de Chêne");
        names.put("spruce", "de Sapin");
        names.put("birch", "de Bouleau");
        names.put("jungle", "d'Acajou");
        names.put("acacia", "d'Acacia");
        names.put("dark_oak", "de Chêne Noir");
        names.put("mangrove", "de Palétuvier");
        names.put("cherry", "de Cerisier");

        String wName = names.get(wood);
        String blockName = "";
        boolean isFeminine = false;
        boolean isPlural = false;

        switch (blockType) {
            case "log": blockName = "Bûche"; isFeminine = true; break;
            case "stripped_log": blockName = "Bûche écorcée"; isFeminine = true; break;
            case "wood": blockName = "Bois"; break;
            case "stripped_wood": blockName = "Bois écorcé"; break;
            case "planks": blockName = "Planches"; isFeminine = true; isPlural = true; break;
            case "slab": blockName = "Dalle"; isFeminine = true; break;
            case "stairs": blockName = "Escaliers"; isPlural = true; break;
            case "fence": blockName = "Barrière"; isFeminine = true; break;
            case "fence_gate": blockName = "Portillon"; break;
            case "door": blockName = "Porte"; isFeminine = true; break;
            case "trapdoor": blockName = "Trappe"; isFeminine = true; break;
            case "pressure_plate": blockName = "Plaque de pression"; isFeminine = true; break;
            case "button": blockName = "Bouton"; break;
        }

        String stateStr = "";
        if (state.equals("moldy")) stateStr = isFeminine ? (isPlural ? "moisies" : "moisie") : (isPlural ? "moisis" : "moisi");
        else if (state.equals("waxed")) stateStr = isFeminine ? (isPlural ? "cirées" : "cirée") : (isPlural ? "cirés" : "ciré");
        else if (state.equals("tainted")) stateStr = isFeminine ? (isPlural ? "entachées" : "entachée") : (isPlural ? "entachés" : "entaché");
        else if (state.equals("rotten")) stateStr = isFeminine ? (isPlural ? "pourries" : "pourrie") : (isPlural ? "pourris" : "pourri");

        return blockName + " " + wName + " " + stateStr;
    }

    @Override
    protected void generateTooltipsAndConfig(TranslationBuilder translationBuilder) {
        translationBuilder.add("tooltip.spores--shadows.waxed", "Ciré");
        translationBuilder.add("item.spores--shadows.waxed_format", "%s ciré");
        translationBuilder.add("tooltip.spores--shadows.moldy_log_desc_1", "Peut être transformé en planches propres avec perte de matériau,");
        translationBuilder.add("tooltip.spores--shadows.moldy_log_desc_2", "mais ne peut être utilisé pour les recettes vanilla normales.");
        translationBuilder.add("tooltip.spores--shadows.moldy_planks_desc_1", "Utile uniquement pour les fabrications simples (bâtons, barrières).");
        translationBuilder.add("tooltip.spores--shadows.moldy_planks_desc_2", "Ne peut pas être utilisé efficacement dans des recettes complexes.");
        translationBuilder.add("tooltip.spores--shadows.moldy_general_desc_1", "Composant en bois dégradé.");
        translationBuilder.add("tooltip.spores--shadows.moldy_general_desc_2", "Structurellement affaibli par la moisissure.");
        translationBuilder.add("tooltip.spores--shadows.moldy_redstone_desc_1", "La moisissure a compromis le mécanisme.");
        translationBuilder.add("tooltip.spores--shadows.moldy_redstone_desc_2", "La durée d'activation est considérablement plus longue.");

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
        
        translationBuilder.add("text.autoconfig.spores--shadows.category.toxicity", "Toxicité");
        translationBuilder.add("text.autoconfig.spores--shadows.option.toxicity", "Toxicité");
        translationBuilder.add("text.autoconfig.spores--shadows.option.toxicity.check_interval_ticks", "Intervalle de contrôle (Ticks)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.toxicity.scan_radius", "Rayon du nuage toxique");
        translationBuilder.add("text.autoconfig.spores--shadows.option.toxicity.threshold_nausea", "Seuil de Nausée");
        translationBuilder.add("text.autoconfig.spores--shadows.option.toxicity.threshold_poison", "Seuil de Poison");
        translationBuilder.add("text.autoconfig.spores--shadows.option.toxicity.duration_nausea_ticks", "Durée de la Nausée (Ticks)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.toxicity.duration_poison_ticks", "Durée du Poison (Ticks)");
        
        translationBuilder.add("text.autoconfig.spores--shadows.category.client", "Client & Shaders");
        translationBuilder.add("text.autoconfig.spores--shadows.option.client", "Client & Shaders");
        translationBuilder.add("text.autoconfig.spores--shadows.option.client.mold_z_offset", "Z-Offset de Moisissure (Correction Shader)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.client.mold_z_offset.@Tooltip[0]", "Ajustez ceci si vous remarquez des clignotements (Z-fighting) avec les shaders.");
        translationBuilder.add("text.autoconfig.spores--shadows.option.client.mold_z_offset.@Tooltip[1]", "Par défaut : 0.002. Essayez 0.005 ou plus si nécessaire.");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.water_scan_radius", "Rayon de Balayage de l'Eau");

        // Jade Tooltips
        translationBuilder.add("tooltip.spores--shadows.jade.stage", "Stade : ");
        translationBuilder.add("tooltip.spores--shadows.jade.infection", "Risque d'Infection : %d%%");
        translationBuilder.add("tooltip.spores--shadows.jade.waxed", "Ciré : ");
        translationBuilder.add("tooltip.spores--shadows.jade.yes", "Oui");
        translationBuilder.add("tooltip.spores--shadows.jade.no", "Non");
        translationBuilder.add("tooltip.spores--shadows.jade.stage.0", "Sain");
        translationBuilder.add("tooltip.spores--shadows.jade.stage.1", "Contaminé");
        translationBuilder.add("tooltip.spores--shadows.jade.stage.2", "Moisi");
        translationBuilder.add("tooltip.spores--shadows.jade.stage.3", "Pourri");
        translationBuilder.add("config.jade.plugin_spores--shadows.moldy_info", "Spores & Shadows : Info Moisissure");

        // Advancements
        translationBuilder.add("advancements.spores--shadows.root.title", "Spores & Shadows");
        translationBuilder.add("advancements.spores--shadows.root.description", "Survivez à la décadence de la nature.");
        translationBuilder.add("advancements.spores--shadows.wax_block.title", "Prévention Naturelle");
        translationBuilder.add("advancements.spores--shadows.wax_block.description", "Utilisez un rayon de miel pour cirer un bloc de bois et arrêter la moisissure.");
        translationBuilder.add("advancements.spores--shadows.scrape_mold.title", "Huile de Coude");
        translationBuilder.add("advancements.spores--shadows.scrape_mold.description", "Grattez la moisissure d'un bloc de bois avec une hache.");
        translationBuilder.add("advancements.spores--shadows.toxic_air.title", "Souffle Court");
        translationBuilder.add("advancements.spores--shadows.toxic_air.description", "Subissez le poison du miasme en respirant trop de moisissure.");
        translationBuilder.add("advancements.spores--shadows.crumble.title", "Poussière à Poussière");
        translationBuilder.add("advancements.spores--shadows.crumble.description", "Tentez de briser un bloc de bois pourri et regardez-le s'effriter dans le néant.");
    }
}
