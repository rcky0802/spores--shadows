package moldmod.client.datagen.lang;

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
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.general.axe_scrape_damage", "Dégâts de Hache (Grattage)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.general.rotten_break_chance_on_use", "Chance de cassure du bloc pourri à l'utilisation");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.general.rotten_break_chance_on_use.@Tooltip", "Chance qu'un bloc fonctionnel en bois pourri se brise lors de son utilisation.");

        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.rain_humidity_base", "Humidité de base (Pluie/Neige)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.dry_humidity_base", "Humidité de base (Clair/Sec)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.cauldron_adjacent_bonus", "Bonus de proximité Chaudrons/Boue");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.water_source_humidity_contribution", "Contribution d'humidité par source d'eau");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.water_source_humidity_contribution.@Tooltip", "Humidité linéaire ajoutée par chaque bloc d'eau donnant sur la pièce.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.max_room_water_humidity_bonus", "Plafond max d'humidité d'eau de pièce");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.max_room_water_humidity_bonus.@Tooltip", "Plafond de saturation maximale pour l'humidité produite par les sources d'eau.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.enable_dynamic_room_humidity", "Humidité dynamique de la pièce");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.enable_dynamic_room_humidity.@Tooltip", "Simule l'accumulation et la dissipation asymptotiques de l'humidité avec le facteur alpha.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.humidity_saturation_speed", "Vitesse de saturation d'humidité (Alpha)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.humidity_saturation_speed.@Tooltip", "Vitesse à laquelle l'humidité monte dans les espaces clos ou humides.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.humidity_dissipation_speed", "Vitesse de dissipation d'humidité (Alpha)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.humidity_dissipation_speed.@Tooltip", "Vitesse à laquelle l'humidité diminue dans les espaces ventilés.");
        
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
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.enable_ventilation_drying", "Activer le séchage par ventilation");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.enable_ventilation_drying.@Tooltip", "Réduit l'humidité effective dans les espaces bien ventilés.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.aeration_drying_bonus", "Bonus de séchage par aération");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.aeration_drying_bonus.@Tooltip", "Réduction maximale d'humidité fournie par la ventilation.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.ventilation_threshold_full_aeration", "Seuil d'aération maximale");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.ventilation_threshold_full_aeration.@Tooltip", "Score de ventilation requis pour 100% de séchage.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.enable_miasma_spore_pressure", "Activer la pression des spores de miasme");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.enable_miasma_spore_pressure.@Tooltip", "Les spores en suspension accélèrent le risque de moisissure.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.miasma_spore_multiplier", "Multiplicateur de pression des spores");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.miasma_spore_multiplier.@Tooltip", "Multiplicateur du risque de moisissure dû au miasme confiné.");

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
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.max_euclidean_radius.@Tooltip", "Rayon sphérique maximal pour les vérifications d'air toxique.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.mold_toxicity_multiplier", "Multiplicateur de Toxicité de Moisissure");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.open_sky_ventilation_per_block", "Taux de Ventilation à Ciel Ouvert");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.slab_ventilation_value", "Valeur de Ventilation des Dalles");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.stairs_ventilation_value", "Valeur de Ventilation des Escaliers");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.copper_grate_ventilation_per_block", "Taux de Ventilation de la Grille de Cuivre");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.leaves_ventilation_value", "Valeur de Ventilation des Feuilles");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.door_ventilation_value", "Valeur de Ventilation des Portes");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.trapdoor_ventilation_value", "Valeur de Ventilation des Trappes");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.fence_gate_open_ventilation_value", "Valeur de Ventilation du Portillon Ouvert");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.ventilation_gap_bonus", "Bonus de Ventilation par Ouverture");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.ventilation_distance_alpha", "Résistance de Distance de Ventilation");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.ventilation_distance_alpha.@Tooltip", "Facteur d'atténuation du flux d'air selon la distance.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.enable_distributed_miasma", "Activer la Distribution du Miasme");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.enable_distributed_miasma.@Tooltip", "Active le calcul de flux discret de miasma à travers les ouvertures de la pièce.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.enable_dynamic_spore_saturation", "Activer la Saturation Dynamique des Spores");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.enable_dynamic_spore_saturation.@Tooltip", "Simule l'accumulation et la dissipation progressives du miasme.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.dissipation_speed_multiplier", "Multiplicateur de Vitesse de Dissipation");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.dissipation_speed_multiplier.@Tooltip", "Vitesse à laquelle le miasme s'évacue lors de la ventilation.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.saturation_speed_multiplier", "Multiplicateur de Vitesse de Saturation");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.saturation_speed_multiplier.@Tooltip", "Vitesse à laquelle le miasme s'accumule dans les pièces fermées.");
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
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.blastResistance", "Résistance aux explosions");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.blastResistance.enable_blast_resistance_scaling", "Activer la mise à l'échelle de résistance aux explosions");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.blastResistance.stage_1_multiplier", "Multiplicateur de résistance Stade 1 (Altéré)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.blastResistance.stage_2_multiplier", "Multiplicateur de résistance Stade 2 (Moisi)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.blastResistance.stage_3_multiplier", "Multiplicateur de résistance Stade 3 (Pourri)");

        // Hardness
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.hardness", "Dureté et Dégradation");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.hardness", "Dureté et Dégradation");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.hardness.enable_hardness_scaling", "Activer l'Échelle de Dureté");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.hardness.stage_1_multiplier", "Multiplicateur de Dureté Stade 1 (Contaminé)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.hardness.stage_2_multiplier", "Multiplicateur de Dureté Stade 2 (Moisi)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.hardness.stage_3_multiplier", "Multiplicateur de Dureté Stade 3 (Pourri)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.hardness.enable_break_spore_cloud", "Nuage de Spores à la Destruction (Sans Toucher de Soie)");

        // Redstone
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.redstone", "Redstone");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.redstone", "Redstone");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.redstone.duration_multiplier", "Multiplicateur de durée Redstone");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.redstone.duration_multiplier.@Tooltip", "Multiplicateur appliqué à la durée d'impulsion pour les boutons et plaques moisis.");

        // Composter
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.composter", "Composteur");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.composter", "Composteur");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.composter.tainted_chance", "Chance de compostage Stade 1 (Altéré)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.composter.tainted_chance.@Tooltip", "Chance d'ajouter un niveau au composteur avec du bois altéré.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.composter.moldy_chance", "Chance de compostage Stade 2 (Moisi)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.composter.moldy_chance.@Tooltip", "Chance d'ajouter un niveau au composteur avec du bois moisi.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.composter.rotten_chance", "Chance de compostage Stade 3 (Pourri)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.composter.rotten_chance.@Tooltip", "Chance d'ajouter un niveau au composteur avec du bois pourri.");

        // Particles
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.particles", "Particules");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.particles", "Particules");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.particles.break_cloud_stage_2_air", "Nombre de particules de spore d'air (Stade 2)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.particles.break_cloud_stage_2_falling", "Nombre de particules de spores tombantes (Stade 2)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.particles.break_cloud_stage_2_mycelium", "Nombre de particules de mycélium (Stade 2)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.particles.break_cloud_stage_3_air", "Nombre de particules de spore d'air (Stade 3)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.particles.break_cloud_stage_3_falling", "Nombre de particules de spores tombantes (Stade 3)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.particles.break_cloud_stage_3_mycelium", "Nombre de particules de mycélium (Stade 3)");

        // Spore Detector & Mask
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.spore_detector", "Détecteur de Spores & Masque");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector", "Détecteur de Spores & Masque");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.block_initial_delay_ticks", "Délai Initial du Bloc (ticks)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.block_initial_delay_ticks.@Tooltip", "Ticks avant la première analyse après le placement du bloc.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.block_periodic_delay_ticks", "Délai Périodique du Bloc (ticks)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.block_periodic_delay_ticks.@Tooltip", "Ticks entre les analyses périodiques.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.redstone_level_multiplier", "Multiplicateur de Niveau Redstone");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.redstone_level_multiplier.@Tooltip", "Multiplié par le niveau de toxicité (0-3) pour générer le signal.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.item_use_cooldown_ticks", "Délai de Récupération Objet (ticks)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.item_use_cooldown_ticks.@Tooltip", "Délai de récupération après une analyse au clic droit.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.geiger_check_interval_ticks", "Intervalle Contrôle Geiger (ticks)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.geiger_check_interval_ticks.@Tooltip", "Fréquence du son de compteur Geiger passif lorsque tenu en main.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.geiger_density_threshold", "Seuil de Densité Geiger");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.geiger_density_threshold.@Tooltip", "Densité minimale de spores pour déclencher le compteur Geiger.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.spore_mask_durability", "Durabilité du Masque Anti-Spores");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.spore_mask_durability.@Tooltip", "Durabilité maximale du Masque Anti-Spores (nécessite un redémarrage).");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.spore_mask_armor_points", "Points d'Armure du Masque");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.spore_mask_armor_points.@Tooltip", "Points d'armure conférés par le masque (nécessite un redémarrage).");

        // Moisture Detector
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.moisture_detector", "Détecteur d'Humidité");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.moistureDetector", "Détecteur d'Humidité");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.moistureDetector.block_initial_delay_ticks", "Délai Initial du Bloc (ticks)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.moistureDetector.block_initial_delay_ticks.@Tooltip", "Ticks avant la première analyse après le placement du bloc.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.moistureDetector.block_periodic_delay_ticks", "Délai Périodique du Bloc (ticks)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.moistureDetector.block_periodic_delay_ticks.@Tooltip", "Ticks entre les analyses périodiques.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.moistureDetector.item_use_cooldown_ticks", "Délai de Récupération Objet (ticks)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.moistureDetector.item_use_cooldown_ticks.@Tooltip", "Délai de récupération après une analyse au clic droit.");

        // Dehumidifier
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.dehumidifier", "Déshumidificateur");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.dehumidifier", "Déshumidificateur");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.dehumidifier.capacity_mb", "Capacité du Réservoir d'Eau (mB)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.dehumidifier.capacity_mb.@Tooltip", "Stockage maximal d'eau condensée en milliBuckets (1000 mB = 1 Seau).");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.dehumidifier.ticks_per_mb", "Ticks par mB Condensé");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.dehumidifier.ticks_per_mb.@Tooltip", "Ticks opérationnels de base nécessaires pour condenser 1 mB d'eau à forte humidité.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.dehumidifier.fuel_multiplier", "Multiplicateur de Durée du Combustible");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.dehumidifier.fuel_multiplier.@Tooltip", "Multiplicateur appliqué aux durées de combustion du four (ex. 4.0 = le charbon dure 4x plus longtemps).");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.dehumidifier.drying_power", "Pouvoir Déshumidifiant (Bonus par Unité)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.dehumidifier.drying_power.@Tooltip", "Réduction linéaire soustraite de l'humidité cible par déshumidificateur actif.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.dehumidifier.energy_capacity", "Capacité du Tampon d'Énergie (E/RF)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.dehumidifier.energy_capacity.@Tooltip", "Stockage d'énergie interne en présence de mods d'énergie compatibles (TR/RF).");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.dehumidifier.energy_cost_per_tick", "Consommation d'Énergie (E/tick)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.dehumidifier.energy_cost_per_tick.@Tooltip", "Énergie consommée par tick en fonctionnement au lieu de brûler du combustible solide.");

        // Structures environmental bonuses
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.underwater_rotten_bonus", "Bonus Pourri Sous l'Eau/Profondeur (%)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.underwater_rotten_bonus.@Tooltip", "Bonus de pourcentage pourri sous l'eau ou Y <= 60.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.underwater_tainted_bonus", "Bonus Entaché Sous l'Eau/Profondeur (%)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.underwater_tainted_bonus.@Tooltip", "Bonus de pourcentage entaché sous l'eau ou Y <= 60.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.underground_moldy_bonus", "Bonus Moisi Souterrain (%)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.underground_moldy_bonus.@Tooltip", "Bonus de pourcentage moisi sous terre (hors eau).");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.ground_contact_moldy_bonus", "Bonus Moisi Contact Sol (%)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.ground_contact_moldy_bonus.@Tooltip", "Bonus de pourcentage moisi près du sol (humidité ascensionnelle).");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.sky_access_rotten_to_moldy", "Conversion Pourri→Moisi Ciel Ouvert (%)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.sky_access_rotten_to_moldy.@Tooltip", "Pourcentage max de pourri converti en moisi avec accès au ciel.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.sky_access_moldy_bonus", "Bonus Moisi Ciel Ouvert (%)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.sky_access_moldy_bonus.@Tooltip", "Bonus de pourcentage moisi exposé à l'air/pluie.");

        // Jade Tooltips
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.infection", "Risque d'Infection : %d%%");
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
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".info.spore_mask", "Le Masque Anti-Spores offre une protection totale contre le miasme toxique (Poison, Nausée et Faim). Il s'use en filtrant l'air toxique. Remplacez le filtre en le réparant avec un Filtre à Spores sur une enclume (le répare entièrement en une seule utilisation). Dans un établi, vous pouvez uniquement combiner deux masques pour une réparation rapide sur le terrain. Peut uniquement être enchanté avec Solidité, Raccommodage et Malédiction de Disparition.");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".info.spore_filter", "Cartouche de filtration essentielle. Utilisée pour fabriquer et réparer le Masque Anti-Spores sur une enclume, ainsi que comme cartouche consommable remplaçable dans le Purificateur d'Air.");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".info.spore_filtration", "Filtration de Spores est un enchantement de casque qui neutralise le miasme toxique et l'inhalation de spores. Consomme la durabilité du casque lors de l'exposition au miasme (Niveau I: 2 durabilité, Niveau II: 1 durabilité, Niveau III: 50% de chance d'économiser la durabilité). Compatible avec tous les casques conventionnels.");

        // Jade Tooltips for Spore Protection & Spore Detector
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.spore_protection_mask", "Protection contre les Spores : Active (Masque Anti-Spores)");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.spore_protection_enchant", "Filtration de Spores : Niveau %d");
        translationBuilder.add("config.jade.plugin_" + moldmod.SporesShadows.MOD_ID + ".spore_protection_info", "Spores & Shadows : Info Protection Spores");
        translationBuilder.add("config.jade.plugin_" + moldmod.SporesShadows.MOD_ID + ".spore_detector_info", "Spores & Shadows : Info Détecteur de Spores");

        translationBuilder.add("item." + moldmod.SporesShadows.MOD_ID + ".spore_mask", "Masque Anti-Spores");
        translationBuilder.add("item." + moldmod.SporesShadows.MOD_ID + ".spore_filter", "Filtre à Spores");
        translationBuilder.add("block." + moldmod.SporesShadows.MOD_ID + ".spore_detector", "Détecteur de Spores");
        translationBuilder.add("item." + moldmod.SporesShadows.MOD_ID + ".spore_detector", "Détecteur de Spores");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".info.spore_detector", "Le Détecteur de Miasme mesure la toxicité de l'air et la ventilation de la pièce. Clic Droit dans l'air pour analyser. Peut être placé sur les murs ou sols.");

        translationBuilder.add("config.jade.plugin_" + moldmod.SporesShadows.MOD_ID + ".moisture_detector_info", "Spores & Shadows : Info Détecteur d'Humidité");
        translationBuilder.add("block." + moldmod.SporesShadows.MOD_ID + ".moisture_detector", "Détecteur d'Humidité");
        translationBuilder.add("item." + moldmod.SporesShadows.MOD_ID + ".moisture_detector", "Détecteur d'Humidité");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".info.moisture_detector", "Le Détecteur d'Humidité mesure l'humidité effective et la saturation de la pièce. Clic Droit dans l'air pour analyser. Peut être placé sur les murs, sols ou plafonds.");

        // Dehumidifier
        translationBuilder.add("block." + moldmod.SporesShadows.MOD_ID + ".dehumidifier", "Déshumidificateur");
        translationBuilder.add("item." + moldmod.SporesShadows.MOD_ID + ".dehumidifier", "Déshumidificateur");
        translationBuilder.add("container." + moldmod.SporesShadows.MOD_ID + ".dehumidifier", "Déshumidificateur");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".info.dehumidifier", "Machine environnementale active pour la régulation de l'humidité. Dispose de deux modes de fonctionnement : Déshumidification (extrait l'humidité de l'air avec une puissance de 1.0 et condense de l'eau pure) ou Humidification/Nébulisation (consomme de l'eau pour vaporiser de l'humidité dans les pièces sèches). Équipée d'un réservoir interne de 2000 mB.");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".info.dehumidifier.energy", "Énergie et Combustible : Prend en charge une alimentation hybride double. Accepte l'énergie externe RF / FE / TR (tampon de 32 000 FE, coût de fonctionnement de 10 FE/tick avec priorité maximale). Brûle sinon des combustibles solides (Charbon multiplié par 4x, jusqu'à 6400 ticks) pour produire de l'électricité interne. L'énergie ne peut pas être extraite.");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".info.dehumidifier.water", "Production d'Eau : En mode Déshumidification, la machine condense l'humidité extraite de la pièce et produit de l'eau liquide pure dans le réservoir interne (2000 mB à 1 mB toutes les 24 ticks), extractible avec des seaux ou des tuyaux de fluides. En mode Humidification, elle consomme l'eau pure fournie pour la vaporiser. Les tuyaux de fluides s'adaptent automatiquement au mode actif (sortie en déshumidification, entrée en humidification).");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".info.dehumidifier.automation", "Automatisation et Contrôles : Toutes les faces acceptent le combustible solide via des entonnoirs. Comprend des boutons dans l'interface pour basculer le mode de fonctionnement (Déshumidifier/Humidifier) et le mode redstone (Ignoré, Actif avec signal, Actif sans signal). Les comparateurs émettent un signal redstone proportionnel au niveau d'eau (0..15).");

        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".category.dehumidifier", "Déshumidification & Eau");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".dehumidifier.dehumidify", "● Mode Déshumidifier");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".dehumidifier.humidify", "● Mode Humidifier");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".dehumidifier.rate", "+1 mB tous les 24 ticks");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".dehumidifier.humidify_rate", "-1 mB tous les 24 ticks");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".dehumidifier.tank", "Réservoir : 2000 mB");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".dehumidifier.power", "Pouvoir d'Assèchement : 1.0");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".dehumidifier.humidify_desc", "Vaporise l'Humidité");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".dehumidifier.short_dehumidify", "Déshumidifier");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".dehumidifier.short_humidify", "Humidifier");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".dehumidifier.short_rate", "+1 mB / 24t");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".dehumidifier.short_humidify_rate", "-1 mB / 24t");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".dehumidifier.short_tank", "Max : 2000 mB");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".dehumidifier.short_power", "Puis : 1.0");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".dehumidifier.energy_desc", "Énergie : 10 FE/t ou Charbon x4 (6400t)");

        // Air Purifier
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".category.air_purifier", "Purification & Filtres");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".air_purifier.standard", "● Décontamination Standard");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".air_purifier.lethal", "● Miasme Létal");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".air_purifier.cleaning", "-48 Miasme (Air Sain)");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".air_purifier.duration", "Durée : 2400 ticks (2 min)");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".air_purifier.lethal_duration", "Durée : 1200 ticks (1 min)");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".air_purifier.rate_standard", "Consommation normale 1x");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".air_purifier.rate_lethal", "Consommation doublée 2x");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".air_purifier.short_standard", "Purification");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".air_purifier.short_lethal", "Miasme Létal");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".air_purifier.short_cleaning", "-48 Miasme");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".air_purifier.short_duration", "2400t (2 min)");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".air_purifier.short_lethal_duration", "1200t (1 min)");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".air_purifier.short_rate_standard", "Cons : 1x");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".air_purifier.short_rate_lethal", "Cons : 2x");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".air_purifier.energy_desc", "Énergie : 10 FE/t ou Charbon x4 (6400t)");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".info.air_purifier", "Machine environnementale active pour la décontamination du miasme toxique. Purifie l'air des pièces closes ou souterraines en éliminant par défaut 48 points de miasma toxique et en restaurant une qualité d'air saine (CLEAN). Nécessite des cartouches filtrantes pour fonctionner.");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".info.air_purifier.energy", "Énergie et Combustible : Prend en charge une alimentation hybride double. Accepte l'énergie externe RF / FE / TR (tampon de 32 000 FE, coût de fonctionnement de 10 FE/tick avec priorité maximale). Brûle sinon des combustibles solides (Charbon multiplié par 4x, jusqu'à 6400 ticks) pour produire de l'électricité interne. L'énergie ne peut pas être extraite.");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".info.air_purifier.filters", "Consommation des Filtres : Pour assainir l'air et éliminer 48 points de miasme, la machine consomme des cartouches de Filtre Anti-Spores dans l'emplacement dédié. Chaque filtre assure 2400 ticks (2 minutes) de purification continue, se consommant deux fois plus vite sous un miasme létal. Lorsqu'une cartouche s'épuise, la suivante est automatiquement prélevée dans la pile.");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".info.air_purifier.automation", "Automatisation et Contrôles : La face supérieure (UP) accepte les Filtres Anti-Spores via des entonnoirs, tandis que les faces latérales et inférieure acceptent le combustible solide. Comprend un bouton dans l'interface pour basculer le mode redstone (Ignoré, Actif avec signal, Actif sans signal). Les comparateurs émettent un signal redstone proportionnel à la réserve et à l'intégrité des filtres (0..15).");

        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".dehumidifier.water", "Eau : %d / %d mB");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".dehumidifier.energy", "Énergie : %d / %d E");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".dehumidifier.energy_usage", "Consommation : %d E/t (%d E/s)");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".dehumidifier.redstone_mode", "Mode Redstone : %s");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".dehumidifier.redstone.ignored", "Ignoré");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".dehumidifier.redstone.ignored.desc", "Toujours actif tant qu'alimenté");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".dehumidifier.redstone.low", "Faible");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".dehumidifier.redstone.low.desc", "Actif sans signal ; en pause avec redstone");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".dehumidifier.redstone.high", "Élevé");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".dehumidifier.redstone.high.desc", "Actif uniquement avec signal redstone");
        translationBuilder.add("gui." + moldmod.SporesShadows.MOD_ID + ".dehumidifier.status.running", "● Actif");
        translationBuilder.add("gui." + moldmod.SporesShadows.MOD_ID + ".dehumidifier.status.full", "● Plein");
        translationBuilder.add("gui." + moldmod.SporesShadows.MOD_ID + ".dehumidifier.status.off", "● Inactif");
        translationBuilder.add("gui." + moldmod.SporesShadows.MOD_ID + ".dehumidifier.status_desc.running", "La machine déshumidifie activement la pièce.");
        translationBuilder.add("gui." + moldmod.SporesShadows.MOD_ID + ".dehumidifier.status_desc.full", "Le réservoir d'eau est plein (2000 mB). Videz l'eau pour reprendre.");
        translationBuilder.add("gui." + moldmod.SporesShadows.MOD_ID + ".dehumidifier.status_desc.off", "Machine inactive (nécessite énergie/combustible, pièce valide ou signal redstone).");

        translationBuilder.add("config.jade.plugin_" + moldmod.SporesShadows.MOD_ID + ".dehumidifier_info", "Spores & Shadows : Info Déshumidificateur");
        translationBuilder.add("config.jade.plugin_" + moldmod.SporesShadows.MOD_ID + ".air_purifier_info", "Spores & Shadows : Info Purificateur d'Air");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.air_purifier.filter", "Filtre : %d%%");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.air_purifier.status.running", "En Fonctionnement");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.air_purifier.status.off", "Éteint");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.air_purifier.status.filter_depleted", "Filtre Épuisé");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.dehumidifier.status", "Statut : ");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.dehumidifier.status.running", "En Fonctionnement");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.dehumidifier.status.full", "Veille (Plein)");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.dehumidifier.status.off", "Éteint");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.dehumidifier.water", "Eau : %d / %d mB");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.dehumidifier.energy", "Énergie : %d E");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.dehumidifier.fuel_min_sec", "Combustible : %dm %ds");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.dehumidifier.fuel_sec", "Combustible : %ds");

        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".moisture_detector.title", "Détection d'Humidité");
        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".moisture_detector.description", "Fabriquez un Détecteur d'Humidité pour surveiller l'hygrométrie.");
        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".dehumidifier_craft.title", "Contrôle du Climat");
        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".dehumidifier_craft.description", "Fabriquez un Déshumidificateur pour assécher des pièces et collecter de l'eau condensée.");
        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".dry_oasis.title", "Oasis Souterraine");
        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".dry_oasis.description", "Asséchez une pièce souterraine (Y <= 40) sous 15% d'humidité à l'aide d'un Déshumidificateur.");

        // Jade Tooltips for Detectors
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.spore_detector.air_quality", "Qualité de l'Air : ");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.spore_detector.clean", "Air Pur");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.spore_detector.warning", "Avertissement (Faible)");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.spore_detector.moderate", "Modéré (Faim)");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.spore_detector.lethal", "Mortel (Poison)");

        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.moisture_detector.effective_moisture", "Niveau d'Humidité : ");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.moisture_detector.dry", "Sec");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.moisture_detector.moderate", "Modéré");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.moisture_detector.humid", "Humide");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.moisture_detector.critical", "Critique");

        // Shared Detector Messages
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".detector.blocks_dist", "%d blocs");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".detector.none", "Aucune");

        // Spore Detector Chat Messages
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".spore_detector.header", "§6[Détecteur de Spores] ");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".spore_detector.clean", "§aAIR PUR §7(Sûr)");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".spore_detector.warning", "§eAVERTISSEMENT §7(Faibles spores dans l'air)");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".spore_detector.moderate", "§6RISQUE MODÉRÉ §7(Faim imminente)");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".spore_detector.lethal", "§4DANGER MORTEL §7(Poison et nausée imminents !)");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".spore_detector.metric", "§7- Densité de spores : §d%s/b");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".spore_detector.open_air", "§7- Environnement : §aPlein Air §7| Dist. à la ventilation : §b0 blocs");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".spore_detector.room", "§7- Volume de la pièce : §f%d blocs §7| Dist. à la ventilation : §b%s");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".spore_detector.aeration", "§7- Aération locale : §a%s flux §7(§b%s§7) | Miasme de la pièce : §6%s");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".spore_detector.trend_stable", "§7- Tendance : §a= STABLE");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".spore_detector.trend_purifying", "§7- Tendance : §b▼ PURIFICATION / DISSIPATION §7(Cible : §f%s§7)");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".spore_detector.trend_accumulating", "§7- Tendance : §c▲ ACCUMULATION / SATURATION §7(Cible : §f%s§7)");

        // Moisture Detector Chat Messages
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".moisture_detector.header", "§6[Détecteur d'Humidité] ");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".moisture_detector.dry", "§aSEC §7(Sans danger pour le bois)");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".moisture_detector.moderate", "§eMODÉRÉ §7(Attention : seuil de moisissure proche)");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".moisture_detector.humid", "§6HUMIDE §7(Alerte : risque élevé de moisissure !)");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".moisture_detector.critical", "§4CRITIQUE §7(Danger : décomposition rapide et saturation !)");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".moisture_detector.metric", "§7- Humidité Effective : §b%s");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".moisture_detector.open_air", "§7- Environnement : §aPlein Air §7| Dist. à la ventilation : §b0 blocs");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".moisture_detector.room", "§7- Volume de la pièce : §f%d blocs §7| Dist. à la ventilation : §b%s");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".moisture_detector.aeration", "§7- Aération locale : §a%s flux §7(§b%s§7) | Humidité Brute : §f%s");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".moisture_detector.trend_stable", "§7- Tendance : §a= STABLE");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".moisture_detector.trend_drying", "§7- Tendance : §b▼ SÉCHAGE / VENTILATION §7(Cible : §f%s§7)");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".moisture_detector.trend_humidifying", "§7- Tendance : §c▲ HUMIDIFICATION / SATURATION §7(Cible : §f%s§7)");

        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".spore_mask_protection.title", "Air Pur");
        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".spore_mask_protection.description", "Filtrez les spores toxiques en respirant à travers un Masque Anti-Spores dans une pièce contaminée.");

        // Enchantments
        translationBuilder.add("enchantment." + moldmod.SporesShadows.MOD_ID + ".spore_filtration", "Filtration de Spores");
        translationBuilder.add("enchantment." + moldmod.SporesShadows.MOD_ID + ".spore_filtration.desc", "Neutralise le miasme toxique en consommant la durabilité du casque.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.enable_spore_mask_protection", "Activer la Protection Masque Anti-Spores");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.enable_spore_mask_protection.@Tooltip", "Protège le porteur contre le miasme toxique et les altérations d'état.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.spore_mask_damage_per_exposure", "Perte de Durabilité du Masque par Exposition");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.spore_mask_damage_per_exposure.@Tooltip", "Durabilité consommée par le masque à chaque test de miasme.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.enable_spore_filtration_enchantment", "Activer l'enchantement Filtration de Spores");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.enable_spore_filtration_enchantment.@Tooltip", "Enchantement de casque neutralisant les spores toxiques.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.filtration_level_1_durability_cost", "Coût de durabilité Filtration Niveau I");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.filtration_level_1_durability_cost.@Tooltip", "Perte de durabilité du casque par exposition au Niveau I.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.filtration_level_2_durability_cost", "Coût de durabilité Filtration Niveau II");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.filtration_level_2_durability_cost.@Tooltip", "Perte de durabilité du casque par exposition au Niveau II.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.filtration_level_3_save_chance", "Chances de préserver la durabilité Niveau III");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.filtration_level_3_save_chance.@Tooltip", "Chances d'annuler la perte de durabilité au Niveau III.");

        // Tag Translations (Fabric Tag Convention v2)
        translationBuilder.add("tag.item." + moldmod.SporesShadows.MOD_ID + ".moldy_items", "Objets Moysis");
        translationBuilder.add("tag.item." + moldmod.SporesShadows.MOD_ID + ".enchantable.filtration_helmets", "Casques de Filtration");
        translationBuilder.add("tag.item." + moldmod.SporesShadows.MOD_ID + ".moldy_blocks", "Blocs Moysis");
        translationBuilder.add("tag.block." + moldmod.SporesShadows.MOD_ID + ".moldy_blocks", "Blocs Moysis");
    }
}
