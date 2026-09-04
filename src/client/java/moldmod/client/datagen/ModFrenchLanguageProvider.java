package moldmod.client.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.registry.RegistryWrapper;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ModFrenchLanguageProvider extends AbstractModLanguageProvider {

    private static final Map<String, String> WOOD_NAMES = Map.of(
        "oak", "de Chêne",
        "spruce", "de Sapin",
        "birch", "de Bouleau",
        "jungle", "d'Acajou",
        "acacia", "d'Acacia",
        "dark_oak", "de Chêne Noir",
        "mangrove", "de Palétuvier",
        "cherry", "de Cerisier",
        "crimson", "carmin",
        "warped", "biscornu"
    );

    public ModFrenchLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, "fr_fr", registryLookup);
    }

    @Override
    protected String getTranslation(String wood, String blockType, String state) {
        String wName = WOOD_NAMES.get(wood);
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
            case "stem": blockName = "Tige"; isFeminine = true; break;
            case "stripped_stem": blockName = "Tige écorcée"; isFeminine = true; break;
            case "hyphae": blockName = "Hyphes"; isFeminine = true; isPlural = true; break;
            case "stripped_hyphae": blockName = "Hyphes écorcées"; isFeminine = true; isPlural = true; break;
        }

        String stateStr = "";
        if (state.equals("moldy")) stateStr = isFeminine ? (isPlural ? "moisies" : "moisie") : (isPlural ? "moisis" : "moisi");
        else if (state.equals("waxed")) stateStr = isFeminine ? (isPlural ? "cirées" : "cirée") : (isPlural ? "cirés" : "ciré");
        else if (state.equals("tainted")) stateStr = isFeminine ? (isPlural ? "entachées" : "entachée") : (isPlural ? "entachés" : "entaché");
        else if (state.equals("rotten")) stateStr = isFeminine ? (isPlural ? "pourries" : "pourrie") : (isPlural ? "pourris" : "pourri");
        else if (state.equals("waxed_tainted")) stateStr = (isFeminine ? (isPlural ? "entachées" : "entachée") : (isPlural ? "entachés" : "entaché")) + " " + (isFeminine ? (isPlural ? "cirées" : "cirée") : (isPlural ? "cirés" : "ciré"));
        else if (state.equals("waxed_moldy")) stateStr = (isFeminine ? (isPlural ? "moisies" : "moisie") : (isPlural ? "moisis" : "moisi")) + " " + (isFeminine ? (isPlural ? "cirées" : "cirée") : (isPlural ? "cirés" : "ciré"));
        else if (state.equals("waxed_rotten")) stateStr = (isFeminine ? (isPlural ? "pourries" : "pourrie") : (isPlural ? "pourris" : "pourri")) + " " + (isFeminine ? (isPlural ? "cirées" : "cirée") : (isPlural ? "cirés" : "ciré"));

        return blockName + " " + wName + " " + stateStr;
    }

    @Override
    protected void generateTooltipsAndConfig(TranslationBuilder translationBuilder) {
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".waxed", "Ciré");
        translationBuilder.add("item." + moldmod.SporesShadows.MOD_ID + ".waxed_format", "%s ciré");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".moldy_log_desc_1", "Peut être transformé en planches propres avec perte de matériau,");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".moldy_log_desc_2", "mais ne peut être utilisé pour les recettes vanilla normales.");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".moldy_planks_desc_1", "Utile uniquement pour les fabrications simples (bâtons, barrières).");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".moldy_planks_desc_2", "Ne peut pas être utilisé efficacement dans des recettes complexes.");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".moldy_general_desc_1", "Composant en bois dégradé.");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".moldy_general_desc_2", "Structurellement affaibli par la moisissure.");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".moldy_redstone_desc_1", "La moisissure a compromis le mécanisme.");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".moldy_redstone_desc_2", "La durée d'activation est considérablement plus longue.");

        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".title", "Configuration Spores & Shadows");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.general", "Général");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.environment", "Environnement");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.susceptibility", "Sensibilité");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.catalysts", "Catalyseurs");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.drops", "Butins");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.structures", "Structures");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.furnace_multipliers", "Efficacité du Four");

        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.general", "Général");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.susceptibility", "Sensibilité");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.catalysts", "Catalyseurs");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment", "Environnement");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.drops", "Butins");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures", "Structures");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.furnaceMultipliers", "Efficacité du Four");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.cave_temperature.@Tooltip", "Température pour ignorer le soleil dans les grottes.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.high_altitude_freezing_temperature.@Tooltip", "Température de gel qui arrête la moisissure en haute altitude.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.general.enable_mold_growth", "Activer la croissance de la moisissure");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.general.infection_threshold", "Seuil d'infection (R > X)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.general.scan_radius", "Rayon de recherche");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.general.scan_radius.@Tooltip", "1 = 3x3x3, 2 = 5x5x5. Les grandes valeurs causent du lag.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.general.structures_immune", "Immunité des structures générées");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.general.structures_immune.@Tooltip", "Si activé, les épaves et villages ne pourriront pas seuls avant l'interaction du joueur.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.general.show_debug_in_chat", "Afficher le débogage dans le tchat");

        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.rain_humidity_base", "Humidité de base (Pluie/Neige)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.dry_humidity_base", "Humidité de base (Clair/Sec)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.water_adjacent_bonus", "Bonus de proximité de l'eau");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.cauldron_adjacent_bonus", "Bonus de proximité Chaudrons/Boue");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.max_local_humidity_bonus", "Bonus max d'humidité locale");
        
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.cave_start_y", "Début des grottes (Niveau Y)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.cave_full_y", "Profondeur pleine des grottes (Niveau Y)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.cave_temperature", "Température des grottes");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.depth_modifier_per_level", "Modificateur d'humidité par profondeur (+ par bloc)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.max_depth_modifier", "Malus max d'humidité de profondeur");
        
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.high_altitude_start_y", "Début de la haute altitude (Niveau Y)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.high_altitude_full_y", "Pic de haute altitude (Niveau Y)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.high_altitude_freezing_temperature", "Température de gel en haute altitude");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.min_temperature_survival", "Température minimale de survie");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.max_temperature_survival", "Température maximale de survie");

        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.susceptibility.stripped_wood_multiplier", "Sensibilité du bois écorcé");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.susceptibility.planks_multiplier", "Sensibilité des planches");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.susceptibility.default_multiplier", "Sensibilité du bois par défaut");
        
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.catalysts.mud_bonus", "Malus de Boue (Mud)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.catalysts.fungi_bonus", "Malus de Champignons");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.catalysts.spore_blossom_bonus", "Malus de Fleur sporifère (Spore Blossom)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.catalysts.podzol_mycelium_bonus", "Malus de Podzol/Mycélium");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.catalysts.tainted_block_bonus", "Malus de bloc entaché proche");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.catalysts.moldy_block_bonus", "Malus de bloc moisi proche");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.catalysts.rotten_block_bonus", "Malus de bloc pourri proche");

        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.drops.stage_2_drop_chance", "Probabilité de butin bloc moisi (0.0 à 1.0)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.drops.stage_2_drop_chance.@Tooltip", "Probabilité qu'un bloc moisi lâche l'objet lui-même.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.drops.stage_3_drop_chance", "Probabilité de butin bloc pourri (0.0 à 1.0)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.drops.stage_3_drop_chance.@Tooltip", "Probabilité qu'un bloc pourri lâche l'objet lui-même.");

        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat1_critical", "Catégorie 1 (Dégradation critique)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat2_high", "Catégorie 2 (Dégradation forte)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat3_moderate", "Catégorie 3 (Dégradation modérée)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat4_low", "Catégorie 4 (Dégradation faible)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat1_critical.moldy_chance", "% Moisi");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat2_high.moldy_chance", "% Moisi");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat3_moderate.moldy_chance", "% Moisi");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat4_low.moldy_chance", "% Moisi");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat1_critical.tainted_chance", "% Entaché");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat2_high.tainted_chance", "% Entaché");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat3_moderate.tainted_chance", "% Entaché");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat4_low.tainted_chance", "% Entaché");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat1_critical.rotten_chance", "% Pourri");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat2_high.rotten_chance", "% Pourri");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat3_moderate.rotten_chance", "% Pourri");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat4_low.rotten_chance", "% Pourri");

        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.furnaceMultipliers.stage_0", "Efficacité du Four (Sain)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.furnaceMultipliers.stage_1", "Efficacité du Four (Entaché)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.furnaceMultipliers.stage_2", "Efficacité du Four (Moisi)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.furnaceMultipliers.stage_3", "Efficacité du Four (Pourri)");
        
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.toxicity", "Toxicité");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity", "Toxicité");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.enable_toxic_air", "Activer l'Air Toxique / Miasme");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.check_interval_ticks", "Intervalle de contrôle (Ticks)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.scan_radius", "Rayon du nuage toxique");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.max_air_volume", "Volume d'Air Maximal (m³)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.max_euclidean_radius", "Rayon Sphérique Maximal");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.mold_toxicity_multiplier", "Multiplicateur de Toxicité de Moisissure");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.ventilation_gap_bonus", "Bonus de Ventilation par Ouverture");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.threshold_hunger", "Seuil de Faim");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.threshold_nausea", "Seuil de Nausée");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.threshold_poison", "Seuil de Poison");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.density_threshold_high", "Seuil de Densité Haute");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.density_threshold_medium", "Seuil de Densité Moyenne");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.density_threshold_low", "Seuil de Densité Basse");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.duration_hunger_ticks", "Durée de la Faim (Ticks)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.duration_nausea_ticks", "Durée de la Nausée (Ticks)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.duration_poison_ticks", "Durée du Poison (Ticks)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.hunger_amplifier", "Amplificateur de Faim");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.nausea_amplifier", "Amplificateur de Nausée");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.poison_amplifier", "Amplificateur de Poison");
        
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.client", "Client & Shaders");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.client", "Client & Shaders");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.client.mold_z_offset", "Z-Offset de Moisissure (Correction Shader)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.client.mold_z_offset.@Tooltip[0]", "Ajustez ceci si vous remarquez des clignotements (Z-fighting) avec les shaders.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.client.mold_z_offset.@Tooltip[1]", "Par défaut : 0.002. Essayez 0.005 ou plus si nécessaire.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.water_scan_radius", "Rayon de Balayage de l'Eau");

        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.flammability", "Inflammabilité");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.flammability", "Inflammabilité");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.flammability.enable_flammability", "Activer la mise à l'échelle de l'inflammabilité");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.flammability.stage_1_burn_bonus", "Bonus de chance d'inflammation Stade 1 (Altéré)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.flammability.stage_1_spread_bonus", "Bonus de propagation du feu Stade 1 (Altéré)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.flammability.stage_2_burn_bonus", "Bonus de chance d'inflammation Stade 2 (Moisi)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.flammability.stage_2_spread_bonus", "Bonus de propagation du feu Stade 2 (Moisi)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.flammability.stage_3_burn_bonus", "Bonus de chance d'inflammation Stade 3 (Pourri)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.flammability.stage_3_spread_bonus", "Bonus de propagation du feu Stade 3 (Pourri)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.flammability.waxed_burn_bonus", "Bonus d'inflammation pour bois ciré");

        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.blast_resistance", "Résistance aux explosions");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.blast_resistance", "Résistance aux explosions");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.blast_resistance.enable_blast_resistance_scaling", "Activer la mise à l'échelle de résistance aux explosions");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.blast_resistance.stage_1_multiplier", "Multiplicateur de résistance Stade 1 (Altéré)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.blast_resistance.stage_2_multiplier", "Multiplicateur de résistance Stade 2 (Moisi)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.blast_resistance.stage_3_multiplier", "Multiplicateur de résistance Stade 3 (Pourri)");

        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.hardness", "Dureté et Dégradation");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.hardness.enable_hardness_scaling", "Activer l'Échelle de Dureté");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.hardness.stage_1_multiplier", "Multiplicateur de Dureté Stade 1 (Contaminé)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.hardness.stage_2_multiplier", "Multiplicateur de Dureté Stade 2 (Moisi)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.hardness.stage_3_multiplier", "Multiplicateur de Dureté Stade 3 (Pourri)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.hardness.enable_break_spore_cloud", "Nuage de Spores à la Destruction (Sans Toucher de Soie)");

        // Jade Tooltips
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.stage", "Stade : ");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.infection", "Risque d'Infection : %d%%");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.waxed", "Ciré : ");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.yes", "Oui");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.no", "Non");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.stage.0", "Sain");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.stage.1", "Contaminé");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.stage.2", "Moisi");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.stage.3", "Pourri");
        translationBuilder.add("config.jade.plugin_" + moldmod.SporesShadows.MOD_ID + ".moldy_info", "Spores & Shadows : Info Moisissure");

        // Advancements
        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".root.title", "Spores & Shadows");
        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".root.description", "Survivez à la décadence de la nature.");
        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".wax_block.title", "Prévention Naturelle");
        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".wax_block.description", "Utilisez un rayon de miel pour cirer un bloc de bois et arrêter la moisissure.");
        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".scrape_mold.title", "Huile de Coude");
        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".scrape_mold.description", "Grattez la moisissure d'un bloc de bois avec une hache.");
        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".toxic_air.title", "Souffle Court");
        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".toxic_air.description", "Subissez le poison du miasme en respirant trop de moisissure.");
        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".crumble.title", "Poussière à Poussière");
        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".crumble.description", "Tentez de briser un bloc de bois pourri et regardez-le s'effriter dans le néant.");

        // JEI
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".category.waxing", "Cirage");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".category.scraping", "Grattage à la Hache");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".info.rotten_wood", "Le bois pourri est fragile et friable. Il ne peut pas être soigné avec une hache. Il nécessite Toucher de Soie pour être récolté, sinon il se désintégrera dans le néant.");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".info.spore_mask", "Le Masque Anti-Spores offre une protection totale contre le miasme toxique (Poison, Nausée et Faim). Il s'use en filtrant l'air toxique. Remplacez le filtre en le réparant sur une enclume avec de la Laine (#minecraft:wool). Peut uniquement être enchanté avec Solidité, Raccommodage et Malédiction de Disparition.");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".info.spore_filtration", "Filtration de Spores est un enchantement de casque qui neutralise le miasme toxique et l'inhalation de spores. Consomme la durabilité du casque lors de l'exposition au miasme (Niveau I: 2 durabilité, Niveau II: 1 durabilité, Niveau III: 50% de chance d'économiser la durabilité). Compatible avec tous les casques conventionnels.");

        // Jade Tooltips for Spore Protection & Spore Detector
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.spore_protection_mask", "Protection contre les Spores : Active (Masque Anti-Spores)");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.spore_protection_enchant", "Filtration de Spores : Niveau %d");
        translationBuilder.add("config.jade.plugin_" + moldmod.SporesShadows.MOD_ID + ".spore_protection_info", "Spores & Shadows : Info Protection Spores");
        translationBuilder.add("config.jade.plugin_" + moldmod.SporesShadows.MOD_ID + ".spore_detector_info", "Spores & Shadows : Info Détecteur de Miasme");

        translationBuilder.add("item." + moldmod.SporesShadows.MOD_ID + ".spore_mask", "Masque Anti-Spores");
        translationBuilder.add("block." + moldmod.SporesShadows.MOD_ID + ".spore_detector", "Détecteur de Miasme");
        translationBuilder.add("item." + moldmod.SporesShadows.MOD_ID + ".spore_detector", "Détecteur de Miasme");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".info.spore_detector", "Le Détecteur de Miasme mesure la toxicité de l'air et la ventilation de la pièce. Clic Droit dans l'air pour analyser. Peut être placé sur les murs ou sols pour émettre un signal de Redstone proportionnel à la densité de spores.");
        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".spore_mask_protection.title", "Air Pur");
        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".spore_mask_protection.description", "Filtrez les spores toxiques en respirant à travers un Masque Anti-Spores dans une pièce contaminée.");

        // Enchantments
        translationBuilder.add("enchantment." + moldmod.SporesShadows.MOD_ID + ".spore_filtration", "Filtration de Spores");
        translationBuilder.add("enchantment." + moldmod.SporesShadows.MOD_ID + ".spore_filtration.desc", "Neutralise le miasme toxique en consommant la durabilité du casque.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.enable_spore_filtration_enchantment", "Activer l'enchantement Filtration de Spores");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.filtration_level_1_durability_cost", "Coût de durabilité Filtration Niveau I");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.filtration_level_2_durability_cost", "Coût de durabilité Filtration Niveau II");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.filtration_level_3_save_chance", "Chances de préserver la durabilité Niveau III");
    }
}
