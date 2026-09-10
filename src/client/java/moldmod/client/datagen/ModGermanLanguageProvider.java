package moldmod.client.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.registry.RegistryWrapper;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ModGermanLanguageProvider extends AbstractModLanguageProvider {

    private static final Map<String, String> WOOD_TRANSLATIONS = Map.of(
        "oak", "Eichen",
        "spruce", "Fichten",
        "birch", "Birken",
        "jungle", "Tropenbaum",
        "acacia", "Akazien",
        "dark_oak", "Schwarzeichen",
        "mangrove", "Mangroven",
        "cherry", "Kirschblüten",
        "crimson", "Karmesin",
        "warped", "Wirr"
    );

    public ModGermanLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, "de_de", registryLookup);
    }

    @Override
    protected String getTranslation(String wood, String blockType, String state) {
        String wName = WOOD_TRANSLATIONS.get(wood);

        String blockSuffix = "";
        String gender = "n"; // n = neuter, m = masculine, f = feminine

        switch (blockType) {
            case "log": blockSuffix = "stamm"; gender = "m"; break;
            case "stripped_log": blockSuffix = "stamm"; gender = "m"; wName = "entrindeter " + wName; break;
            case "wood": blockSuffix = "holz"; gender = "n"; break;
            case "stripped_wood": blockSuffix = "holz"; gender = "n"; wName = "entrindetes " + wName; break;
            case "stem": blockSuffix = "stiel"; gender = "m"; break;
            case "stripped_stem": blockSuffix = "stiel"; gender = "m"; wName = "entrindeter " + wName; break;
            case "hyphae": blockSuffix = "hyphen"; gender = "f"; break;
            case "stripped_hyphae": blockSuffix = "hyphen"; gender = "f"; wName = "entrindete " + wName; break;
            case "planks": blockSuffix = "holzbretter"; gender = "f"; break; // Plural acts like feminine for adjectives
            case "slab": blockSuffix = "holzstufe"; gender = "f"; break;
            case "stairs": blockSuffix = "holztreppe"; gender = "f"; break;
            case "fence": blockSuffix = "holzzaun"; gender = "m"; break;
            case "fence_gate": blockSuffix = "holzzauntor"; gender = "n"; break;
            case "door": blockSuffix = "holztür"; gender = "f"; break;
            case "trapdoor": blockSuffix = "holzfalltür"; gender = "f"; break;
            case "pressure_plate": blockSuffix = "holzdruckplatte"; gender = "f"; break;
            case "button": blockSuffix = "holzknopf"; gender = "m"; break;
        }

        String stateStr = "";
        if (state.equals("moldy")) {
            if (gender.equals("m")) stateStr = "Schimmeliger";
            else if (gender.equals("f")) stateStr = "Schimmelige";
            else stateStr = "Schimmeliges";
        } else if (state.equals("waxed")) {
            if (gender.equals("m")) stateStr = "Gewachster";
            else if (gender.equals("f")) stateStr = "Gewachste";
            else stateStr = "Gewachstes";
        } else if (state.equals("tainted")) {
            if (gender.equals("m")) stateStr = "Befallener";
            else if (gender.equals("f")) stateStr = "Befallene";
            else stateStr = "Befallenes";
        } else if (state.equals("rotten")) {
            if (gender.equals("m")) stateStr = "Verrotteter";
            else if (gender.equals("f")) stateStr = "Verrottete";
            else stateStr = "Verrottetes";
        } else if (state.equals("waxed_tainted")) {
            if (gender.equals("m")) stateStr = "Gewachster befallener";
            else if (gender.equals("f")) stateStr = "Gewachste befallene";
            else stateStr = "Gewachstes befallenes";
        } else if (state.equals("waxed_moldy")) {
            if (gender.equals("m")) stateStr = "Gewachster schimmeliger";
            else if (gender.equals("f")) stateStr = "Gewachste schimmelige";
            else stateStr = "Gewachstes schimmeliges";
        } else if (state.equals("waxed_rotten")) {
            if (gender.equals("m")) stateStr = "Gewachster verrotteter";
            else if (gender.equals("f")) stateStr = "Gewachste verrottete";
            else stateStr = "Gewachstes verrottetes";
        }

        return stateStr + " " + wName + blockSuffix;
    }

    @Override
    protected void generateTooltipsAndConfig(TranslationBuilder translationBuilder) {
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".waxed", "Gewachst");
        translationBuilder.add("item." + moldmod.SporesShadows.MOD_ID + ".waxed_format", "Gewachstes %s");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".moldy_log_desc_1", "Kann unter Materialverlust zu sauberen Brettern verarbeitet werden,");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".moldy_log_desc_2", "ist aber für normale Vanilla-Rezepte unbrauchbar.");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".moldy_planks_desc_1", "Nur für einfache Rezepte nützlich (Stöcke, Zäune).");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".moldy_planks_desc_2", "Nicht effektiv in komplexen Rezepten einsetzbar.");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".moldy_general_desc_1", "Degradiertes Holzbauteil.");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".moldy_general_desc_2", "Strukturell durch Schimmel geschwächt.");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".moldy_redstone_desc_1", "Schimmel hat den Mechanismus beeinträchtigt.");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".moldy_redstone_desc_2", "Die Aktivierungsdauer ist deutlich länger.");

        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".title", "Spores & Shadows Konfiguration");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.general", "Allgemein");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.environment", "Umwelt");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.susceptibility", "Anfälligkeit");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.catalysts", "Katalysatoren");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.drops", "Drops");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.structures", "Strukturen");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.furnace_multipliers", "Ofeneffizienz");

        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.general", "Allgemein");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.susceptibility", "Anfälligkeit");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.catalysts", "Katalysatoren");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment", "Umwelt");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.drops", "Drops");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures", "Strukturen");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.furnaceMultipliers", "Ofeneffizienz");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.cave_temperature.@Tooltip", "Temperatur, um die Sonne in Höhlen zu ignorieren.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.high_altitude_freezing_temperature.@Tooltip", "Gefriertemperatur, die Schimmel in großen Höhen stoppt.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.general.enable_mold_growth", "Schimmelwachstum aktivieren");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.general.infection_threshold", "Infektionsschwelle (R > X)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.general.scan_radius", "Scan-Radius");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.general.scan_radius.@Tooltip", "1 = 3x3x3, 2 = 5x5x5. Höhere Werte verursachen Lag.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.general.structures_immune", "Generierte Strukturen sind immun");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.general.structures_immune.@Tooltip", "Wenn aktiviert, verrotten Schiffswracks und Dörfer nicht, bis der Spieler interagiert.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.general.show_debug_in_chat", "Debug-Logik im Chat anzeigen");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.general.axe_scrape_damage", "Axt-Schabeschaden");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.general.rotten_break_chance_on_use", "Bruchwahrscheinlichkeit bei Benutzung");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.general.rotten_break_chance_on_use.@Tooltip", "Wahrscheinlichkeit, dass morsche Funktionsblöcke bei Benutzung zerbrechen.");

        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.rain_humidity_base", "Basisfeuchtigkeit (Regen/Schnee)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.dry_humidity_base", "Basisfeuchtigkeit (Klar/Trocken)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.cauldron_adjacent_bonus", "Kessel-/Schlammnähe-Bonus");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.water_source_humidity_contribution", "Feuchtigkeitsbeitrag pro Wasserquelle");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.water_source_humidity_contribution.@Tooltip", "Lineare Feuchtigkeit durch jeden an den Raum angrenzenden Wasserblock.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.max_room_water_humidity_bonus", "Max. Feuchtigkeitsgrenze durch Wasser");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.max_room_water_humidity_bonus.@Tooltip", "Maximale Sättigungsgrenze für Feuchtigkeit aus Wasserquellen im Raum.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.enable_dynamic_room_humidity", "Dynamische Raumfeuchtigkeit");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.enable_dynamic_room_humidity.@Tooltip", "Simuliert asymptotische Feuchtigkeitsakkumulation und -verflüchtigung.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.humidity_saturation_speed", "Feuchtigkeitssättigungsgeschwindigkeit (Alpha)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.humidity_saturation_speed.@Tooltip", "Geschwindigkeit, mit der Feuchtigkeit in geschlossenen Räumen steigt.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.humidity_dissipation_speed", "Feuchtigkeitsverflüchtigungsgeschwindigkeit (Alpha)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.humidity_dissipation_speed.@Tooltip", "Geschwindigkeit, mit der Feuchtigkeit in belüfteten Räumen sinkt.");
        
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.cave_start_y", "Höhlenbeginn (Y-Level)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.cave_full_y", "Volle Höhlentiefe (Y-Level)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.cave_temperature", "Höhlentemperatur");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.depth_modifier_per_level", "Tiefenfeuchtigkeits-Modifikator (+ pro Level)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.max_depth_modifier", "Max. Tiefenfeuchtigkeits-Modifikator");
        
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.high_altitude_start_y", "Hochgebirgsbeginn (Y-Level)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.high_altitude_full_y", "Hochgebirgsgipfel (Y-Level)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.high_altitude_freezing_temperature", "Gefriertemperatur im Hochgebirge");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.min_temperature_survival", "Minimale Überlebenstemperatur");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.max_temperature_survival", "Maximale Überlebenstemperatur");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.enable_ventilation_drying", "Belüftungstrocknung aktivieren");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.enable_ventilation_drying.@Tooltip", "Verringert die effektive Feuchtigkeit in gut belüfteten Räumen.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.aeration_drying_bonus", "Belüftungs-Trocknungsbonus");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.aeration_drying_bonus.@Tooltip", "Maximale Feuchtigkeitsreduktion durch Belüftung.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.ventilation_threshold_full_aeration", "Schwelle für volle Belüftung");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.ventilation_threshold_full_aeration.@Tooltip", "Erforderlicher Belüftungswert für 100% Trocknungswirkung.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.enable_miasma_spore_pressure", "Miasma-Sporendruck aktivieren");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.enable_miasma_spore_pressure.@Tooltip", "In der Luft gefangene Sporen erhöhen das Schimmelrisiko.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.miasma_spore_multiplier", "Miasma-Sporenmultiplikator");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.miasma_spore_multiplier.@Tooltip", "Multiplikator für Schimmelrisiko durch gefangenes Miasma.");

        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.susceptibility.stripped_wood_multiplier", "Anfälligkeit von entrindetem Holz");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.susceptibility.planks_multiplier", "Anfälligkeit von Holzbrettern");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.susceptibility.default_multiplier", "Standard-Holzanfälligkeit");
        
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.catalysts.mud_bonus", "Schlamm-Malus");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.catalysts.fungi_bonus", "Pilz-Malus");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.catalysts.spore_blossom_bonus", "Sporenblüten-Malus");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.catalysts.podzol_mycelium_bonus", "Podsol-/Myzel-Malus");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.catalysts.tainted_block_bonus", "Befallener Block-Malus");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.catalysts.moldy_block_bonus", "Schimmelblock-Malus");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.catalysts.rotten_block_bonus", "Verrotteter Block-Malus");

        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.drops.stage_2_drop_chance", "Dropchance Schimmelblock (0.0 bis 1.0)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.drops.stage_2_drop_chance.@Tooltip", "Chance, dass ein Schimmelblock sich selbst droppt.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.drops.stage_3_drop_chance", "Dropchance Verrotteter Block (0.0 bis 1.0)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.drops.stage_3_drop_chance.@Tooltip", "Chance, dass ein verrotteter Block sich selbst droppt.");

        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat1_critical", "Kategorie 1 (Kritischer Zerfall)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat2_high", "Kategorie 2 (Hoher Zerfall)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat3_moderate", "Kategorie 3 (Mäßiger Zerfall)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat4_low", "Kategorie 4 (Geringer Zerfall)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat1_critical.moldy_chance", "% Schimmelig");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat2_high.moldy_chance", "% Schimmelig");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat3_moderate.moldy_chance", "% Schimmelig");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat4_low.moldy_chance", "% Schimmelig");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat1_critical.tainted_chance", "% Befallen");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat2_high.tainted_chance", "% Befallen");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat3_moderate.tainted_chance", "% Befallen");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat4_low.tainted_chance", "% Befallen");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat1_critical.rotten_chance", "% Verrottet");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat2_high.rotten_chance", "% Verrottet");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat3_moderate.rotten_chance", "% Verrottet");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat4_low.rotten_chance", "% Verrottet");

        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.furnaceMultipliers.stage_0", "Ofeneffizienz (Gesund)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.furnaceMultipliers.stage_1", "Ofeneffizienz (Befallen)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.furnaceMultipliers.stage_2", "Ofeneffizienz (Schimmelig)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.furnaceMultipliers.stage_3", "Ofeneffizienz (Verrottet)");
        
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.toxicity", "Toxizität");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity", "Toxizität");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.enable_toxic_air", "Toxische Luft / Miasma aktivieren");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.check_interval_ticks", "Kontrollintervall (Ticks)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.scan_radius", "Giftwolken-Radius");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.max_air_volume", "Max. Luftvolumen (m³)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.max_euclidean_radius", "Max. Sphärischer Radius");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.max_euclidean_radius.@Tooltip", "Maximaler sphärischer Scan-Radius für Räume.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.mold_toxicity_multiplier", "Schimmel-Toxizitätsmultiplikator");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.open_sky_ventilation_per_block", "Belüftungsrate bei offenem Himmel");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.slab_ventilation_value", "Stufen-Belüftungswert");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.stairs_ventilation_value", "Treppen-Belüftungswert");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.copper_grate_ventilation_per_block", "Kupfergitter-Belüftungsrate");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.leaves_ventilation_value", "Blätter-Belüftungswert");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.door_ventilation_value", "Türen-Belüftungswert");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.trapdoor_ventilation_value", "Falltüren-Belüftungswert");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.fence_gate_open_ventilation_value", "Zauntor-Belüftungswert");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.ventilation_gap_bonus", "Belüftungsbonus pro Öffnung");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.ventilation_distance_alpha", "Belüftungsdistanz-Widerstand");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.ventilation_distance_alpha.@Tooltip", "Widerstandsfaktor über Distanz für Raumluftströmung.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.enable_distributed_miasma", "Verteilte Miasma-Berechnung aktivieren");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.enable_distributed_miasma.@Tooltip", "Aktiviert die diskrete Flussberechnung für Miasma durch Raumöffnungen.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.enable_dynamic_spore_saturation", "Dynamische Sporensättigung aktivieren");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.enable_dynamic_spore_saturation.@Tooltip", "Simuliert die schrittweise Ansammlung und Verflüchtigung von Miasma.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.dissipation_speed_multiplier", "Verflüchtigungs-Multiplikator");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.dissipation_speed_multiplier.@Tooltip", "Geschwindigkeit, mit der sich Miasma bei Belüftung auflöst.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.saturation_speed_multiplier", "Sättigungs-Multiplikator");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.saturation_speed_multiplier.@Tooltip", "Geschwindigkeit, mit der sich Miasma in geschlossenen Räumen anstaut.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.threshold_hunger", "Hunger-Schwellenwert");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.threshold_nausea", "Übelkeit-Schwelle");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.threshold_poison", "Gift-Schwellenwert");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.density_threshold_high", "Hohe Dichteschwelle");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.density_threshold_medium", "Mittlere Dichteschwelle");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.density_threshold_low", "Niedrige Dichteschwelle");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.duration_hunger_ticks", "Dauer Hunger (Ticks)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.duration_nausea_ticks", "Dauer Übelkeit (Ticks)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.duration_poison_ticks", "Dauer Gift (Ticks)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.hunger_amplifier", "Hunger-Verstärker");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.nausea_amplifier", "Übelkeit-Verstärker");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.poison_amplifier", "Gift-Verstärker");
        
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.client", "Client & Shader");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.client", "Client & Shader");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.client.mold_z_offset", "Schimmel Z-Versatz (Shader-Fix)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.client.mold_z_offset.@Tooltip[0]", "Passen Sie dies an, wenn Sie bei Shadern Z-Fighting (Flackern) bemerken.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.client.mold_z_offset.@Tooltip[1]", "Standard: 0.002. Versuchen Sie 0.005 oder höher, falls erforderlich.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.water_scan_radius", "Wasser-Scan-Radius");

        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.flammability", "Entflammbarkeit");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.flammability", "Entflammbarkeit");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.flammability.enable_flammability", "Entflammbarkeitsskalierung aktivieren");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.flammability.stage_1_burn_bonus", "Stufe 1 (Befallen) Entzündungs-Bonus");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.flammability.stage_1_spread_bonus", "Stufe 1 (Befallen) Ausbreitungs-Bonus");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.flammability.stage_2_burn_bonus", "Stufe 2 (Schimmlig) Entzündungs-Bonus");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.flammability.stage_2_spread_bonus", "Stufe 2 (Schimmlig) Ausbreitungs-Bonus");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.flammability.stage_3_burn_bonus", "Stufe 3 (Verfault) Entzündungs-Bonus");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.flammability.stage_3_spread_bonus", "Stufe 3 (Verfault) Ausbreitungs-Bonus");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.flammability.waxed_burn_bonus", "Gewachstes Holz Entzündungs-Bonus");

        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.blast_resistance", "Explosionsresistenz");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.blastResistance", "Explosionsresistenz");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.blastResistance.enable_blast_resistance_scaling", "Explosionsresistenzskalierung aktivieren");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.blastResistance.stage_1_multiplier", "Stufe 1 (Befallen) Resistenz-Multiplikator");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.blastResistance.stage_2_multiplier", "Stufe 2 (Schimmlig) Resistenz-Multiplikator");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.blastResistance.stage_3_multiplier", "Stufe 3 (Verfault) Resistenz-Multiplikator");

        // Hardness & Degradation
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.hardness", "Härte & Zersetzung");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.hardness", "Härte & Zersetzung");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.hardness.enable_hardness_scaling", "Härteskalierung aktivieren");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.hardness.stage_1_multiplier", "Stufe 1 Härtemultiplikator (Befallen)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.hardness.stage_2_multiplier", "Stufe 2 Härtemultiplikator (Schimmelig)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.hardness.stage_3_multiplier", "Stufe 3 Härtemultiplikator (Morsch)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.hardness.enable_break_spore_cloud", "Sporenwolke beim Abbau erzeugen (ohne Behutsamkeit)");

        // Redstone
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.redstone", "Redstone");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.redstone", "Redstone");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.redstone.duration_multiplier", "Redstone-Impulsdauermultiplikator");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.redstone.duration_multiplier.@Tooltip", "Multiplikator für die Impulsdauer von schimmeligen Knöpfen und Druckplatten.");

        // Composter
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.composter", "Komposter");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.composter", "Komposter");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.composter.tainted_chance", "Kompostierungschance Stufe 1 (Befallen)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.composter.tainted_chance.@Tooltip", "Wahrscheinlichkeit, dass befallene Holzgegenstände eine Kompostschicht füllen.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.composter.moldy_chance", "Kompostierungschance Stufe 2 (Schimmelig)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.composter.moldy_chance.@Tooltip", "Wahrscheinlichkeit, dass schimmelige Holzgegenstände eine Kompostschicht füllen.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.composter.rotten_chance", "Kompostierungschance Stufe 3 (Verrottet)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.composter.rotten_chance.@Tooltip", "Wahrscheinlichkeit, dass verrottete Holzgegenstände eine Kompostschicht füllen.");

        // Particles
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.particles", "Partikel");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.particles", "Partikel");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.particles.break_cloud_stage_2_air", "Sporenwolken-Partikelanzahl Luft (Stufe 2)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.particles.break_cloud_stage_2_falling", "Fallende Sporen-Partikelanzahl (Stufe 2)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.particles.break_cloud_stage_2_mycelium", "Myzel-Partikelanzahl (Stufe 2)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.particles.break_cloud_stage_3_air", "Sporenwolken-Partikelanzahl Luft (Stufe 3)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.particles.break_cloud_stage_3_falling", "Fallende Sporen-Partikelanzahl (Stufe 3)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.particles.break_cloud_stage_3_mycelium", "Myzel-Partikelanzahl (Stufe 3)");

        // Spore Detector & Mask
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.spore_detector", "Sporendetektor & Maske");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector", "Sporendetektor & Maske");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.block_initial_delay_ticks", "Block-Initialverzögerung (Ticks)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.block_initial_delay_ticks.@Tooltip", "Ticks vor dem ersten Scan nach dem Platzieren des Blocks.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.block_periodic_delay_ticks", "Block-Periodenverzögerung (Ticks)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.block_periodic_delay_ticks.@Tooltip", "Ticks zwischen periodischen Scans.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.redstone_level_multiplier", "Redstone-Signal-Multiplikator");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.redstone_level_multiplier.@Tooltip", "Wird mit der Toxizitätsstufe (0-3) multipliziert, um das Redstone-Signal zu erzeugen.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.item_use_cooldown_ticks", "Gegenstands-Abklingzeit (Ticks)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.item_use_cooldown_ticks.@Tooltip", "Abklingzeit nach dem Rechtsklick-Scan mit dem Gegenstand.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.geiger_check_interval_ticks", "Geigerzähler-Prüfintervall (Ticks)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.geiger_check_interval_ticks.@Tooltip", "Häufigkeit der passiven Geigerzähler-Tonprüfung beim Halten.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.geiger_density_threshold", "Geigerzähler-Dichteschwelle");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.geiger_density_threshold.@Tooltip", "Minimale Sporendichte zur Auslösung des Geigerzähler-Tons.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.spore_mask_durability", "Sporenmasken-Haltbarkeit");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.spore_mask_durability.@Tooltip", "Maximale Haltbarkeit der Sporenmaske (Neustart erforderlich).");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.spore_mask_armor_points", "Sporenmasken-Rüstungspunkte");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.spore_mask_armor_points.@Tooltip", "Rüstungspunkte der Sporenmaske (Neustart erforderlich).");

        // Moisture Detector
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.moisture_detector", "Feuchtigkeitsdetektor");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.moistureDetector", "Feuchtigkeitsdetektor");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.moistureDetector.block_initial_delay_ticks", "Block-Initialverzögerung (Ticks)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.moistureDetector.block_initial_delay_ticks.@Tooltip", "Ticks vor dem ersten Scan nach dem Platzieren des Blocks.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.moistureDetector.block_periodic_delay_ticks", "Block-Periodenverzögerung (Ticks)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.moistureDetector.block_periodic_delay_ticks.@Tooltip", "Ticks zwischen periodischen Scans.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.moistureDetector.item_use_cooldown_ticks", "Gegenstands-Abklingzeit (Ticks)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.moistureDetector.item_use_cooldown_ticks.@Tooltip", "Abklingzeit nach dem Rechtsklick-Scan mit dem Gegenstand.");

        // Structures environmental bonuses
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.underwater_rotten_bonus", "Unterwasser-/Tiefen-Fäulnisbonus (%)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.underwater_rotten_bonus.@Tooltip", "Fäulnis-Prozentbonus unter Wasser oder bei Y <= 60.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.underwater_tainted_bonus", "Unterwasser-/Tiefen-Befallbonus (%)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.underwater_tainted_bonus.@Tooltip", "Befall-Prozentbonus unter Wasser oder bei Y <= 60.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.underground_moldy_bonus", "Unterirdischer Schimmelbonus (%)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.underground_moldy_bonus.@Tooltip", "Schimmel-Prozentbonus tief unter der Erde (nicht unter Wasser).");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.ground_contact_moldy_bonus", "Bodenkontakt-Schimmelbonus (%)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.ground_contact_moldy_bonus.@Tooltip", "Schimmel-Prozentbonus in Bodennähe (aufsteigende Feuchte).");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.sky_access_rotten_to_moldy", "Himmelszugang Fäulnis-zu-Schimmel-Umwandlung (%)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.sky_access_rotten_to_moldy.@Tooltip", "Max. Fäulnis-Prozentanteil, der bei Himmelszugang zu Schimmel wird.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.sky_access_moldy_bonus", "Himmelszugang Schimmelbonus (%)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.sky_access_moldy_bonus.@Tooltip", "Schimmel-Prozentbonus bei Exposition gegenüber Luft/Regen.");

        // Jade Tooltips
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.infection", "Infektionsrisiko: %d%%");
        translationBuilder.add("config.jade.plugin_" + moldmod.SporesShadows.MOD_ID + ".moldy_info", "Spores & Shadows: Schimmel Info");

        // Advancements
        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".root.title", "Spores & Shadows");
        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".root.description", "Überlebe den Zerfall der Natur.");
        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".wax_block.title", "Natürliche Prävention");
        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".wax_block.description", "Verwende eine Honigwabe, um einen Holzblock zu wachsen und den Schimmel aufzuhalten.");
        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".scrape_mold.title", "Muskelschmalz");
        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".scrape_mold.description", "Kratze den Schimmel mit einer Axt von einem Holzblock ab.");
        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".toxic_air.title", "Kurzer Atem");
        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".toxic_air.description", "Leide unter dem Gift des Miasmas, weil du zu viel Schimmel eingeatmet hast.");
        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".crumble.title", "Staub zu Staub");
        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".crumble.description", "Versuche, einen verfaulten Holzblock abzubauen und sieh zu, wie er zu nichts zerfällt.");

        // JEI
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".category.waxing", "Wachsen");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".category.scraping", "Axt-Schaben");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".info.rotten_wood", "Morsches Holz ist brüchig und zerfällt. Es kann nicht mit einer Axt geheilt werden. Es erfordert Behutsamkeit zum Abbau, sonst zerfällt es beim Zerstören zu Staub.");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".info.spore_mask", "Die Sporenmaske bietet vollständigen Schutz vor giftigem Miasma (Gift, Übelkeit und Hunger). Sie verbraucht Haltbarkeit beim Filtern giftiger Luft. Tausche den Filter aus, indem du sie im Amboss mit Wolle (#minecraft:wool) reparierst. Kann nur mit Haltbarkeit, Reparatur und Fluch des Verschwindens verzaubert werden.");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".info.spore_filtration", "Sporenfiltration ist eine Helm-Verzauberung, die giftiges Miasma und das Einatmen von Sporen neutralisiert. Verbraucht Helm-Haltbarkeit bei Miasma-Exposition (Stufe I: 2 Haltbarkeit, Stufe II: 1 Haltbarkeit, Stufe III: 50% Haltbarkeits-Ersparnis). Kompatibel mit allen konventionellen Helmen.");

        // Jade Tooltips for Spore Protection & Spore Detector
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.spore_protection_mask", "Sporenschutz: Aktiv (Sporenmaske)");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.spore_protection_enchant", "Sporenfiltration: Stufe %d");
        translationBuilder.add("config.jade.plugin_" + moldmod.SporesShadows.MOD_ID + ".spore_protection_info", "Spores & Shadows: Sporenschutz Info");
        translationBuilder.add("config.jade.plugin_" + moldmod.SporesShadows.MOD_ID + ".spore_detector_info", "Spores & Shadows: Sporendetektor Info");

        translationBuilder.add("item." + moldmod.SporesShadows.MOD_ID + ".spore_mask", "Sporenmaske");
        translationBuilder.add("block." + moldmod.SporesShadows.MOD_ID + ".spore_detector", "Sporendetektor");
        translationBuilder.add("item." + moldmod.SporesShadows.MOD_ID + ".spore_detector", "Sporendetektor");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".info.spore_detector", "Der Sporendetektor misst Lufttoxizität und Raumbelüftung. Rechtsklick in die Luft zum Scannen. Kann an Wänden oder Böden platziert werden.");

        translationBuilder.add("config.jade.plugin_" + moldmod.SporesShadows.MOD_ID + ".moisture_detector_info", "Spores & Shadows: Feuchtigkeitsdetektor Info");
        translationBuilder.add("block." + moldmod.SporesShadows.MOD_ID + ".moisture_detector", "Feuchtigkeitsdetektor");
        translationBuilder.add("item." + moldmod.SporesShadows.MOD_ID + ".moisture_detector", "Feuchtigkeitsdetektor");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".info.moisture_detector", "Der Feuchtigkeitsdetektor misst effektive Feuchtigkeit und Umweltsättigung. Rechtsklick in die Luft zum Scannen. Kann an Wänden, Böden oder Decken platziert werden.");

        // Jade Tooltips for Detectors
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.spore_detector.air_quality", "Luftqualität: ");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.spore_detector.clean", "Reine Luft");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.spore_detector.warning", "Warnung (Gering)");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.spore_detector.moderate", "Mäßig (Hunger)");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.spore_detector.lethal", "Tödlich (Gift)");

        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.moisture_detector.effective_moisture", "Feuchtigkeitsstufe: ");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.moisture_detector.dry", "Trocken");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.moisture_detector.moderate", "Mäßig");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.moisture_detector.humid", "Feucht");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.moisture_detector.critical", "Kritisch");

        // Shared Detector Messages
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".detector.blocks_dist", "%d Blöcke");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".detector.none", "Keine");

        // Spore Detector Chat Messages
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".spore_detector.header", "§6[Sporendetektor] ");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".spore_detector.clean", "§aREINE LUFT §7(Sicher)");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".spore_detector.warning", "§eWARNUNG §7(Geringe Sporenkonzentration)");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".spore_detector.moderate", "§6MÄSSIGES RISIKO §7(Hunger droht)");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".spore_detector.lethal", "§4TÖDLICHE GEFAHR §7(Gift & Übelkeit drohen!)");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".spore_detector.metric", "§7- Sporendichte: §d%s/b");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".spore_detector.open_air", "§7- Umgebung: §aFreie Luft §7| Distanz zur Belüftung: §b0 Blöcke");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".spore_detector.room", "§7- Raumvolumen: §f%d Blöcke §7| Distanz zur Belüftung: §b%s");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".spore_detector.aeration", "§7- Lokale Belüftung: §a%s Fluss §7(§b%s§7) | Raum-Miasma: §6%s");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".spore_detector.trend_stable", "§7- Trend: §a= STABIL");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".spore_detector.trend_purifying", "§7- Trend: §b▼ REINIGEND / ABBAUEND §7(Ziel: §f%s§7)");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".spore_detector.trend_accumulating", "§7- Trend: §c▲ ANSAMMELND / SÄTTIGEND §7(Ziel: §f%s§7)");

        // Moisture Detector Chat Messages
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".moisture_detector.header", "§6[Feuchtigkeitsdetektor] ");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".moisture_detector.dry", "§aTROCKEN §7(Sicher für Holzkonstruktionen)");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".moisture_detector.moderate", "§eMÄSSIG §7(Vorsicht: Schimmelschwelle naht)");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".moisture_detector.humid", "§6FEUCHT §7(Warnung: hohes Schimmelrisiko!)");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".moisture_detector.critical", "§4KRITISCH §7(Gefahr: rascher Verfall & Sättigung!)");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".moisture_detector.metric", "§7- Effektive Feuchtigkeit: §b%s");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".moisture_detector.open_air", "§7- Umgebung: §aFreie Luft §7| Distanz zur Belüftung: §b0 Blöcke");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".moisture_detector.room", "§7- Raumvolumen: §f%d Blöcke §7| Distanz zur Belüftung: §b%s");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".moisture_detector.aeration", "§7- Lokale Belüftung: §a%s Fluss §7(§b%s§7) | Rohfeuchtigkeit: §f%s");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".moisture_detector.trend_stable", "§7- Trend: §a= STABIL");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".moisture_detector.trend_drying", "§7- Trend: §b▼ TROCKNEND / LÜFTEND §7(Ziel: §f%s§7)");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".moisture_detector.trend_humidifying", "§7- Trend: §c▲ BEFEUCHTEND / SÄTTIGEND §7(Ziel: §f%s§7)");

        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".spore_mask_protection.title", "Reine Luft");
        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".spore_mask_protection.description", "Filtere giftige Sporen, indem du in einem kontaminierten Raum durch eine Sporenmaske atmest.");

        // Enchantments
        translationBuilder.add("enchantment." + moldmod.SporesShadows.MOD_ID + ".spore_filtration", "Sporenfiltration");
        translationBuilder.add("enchantment." + moldmod.SporesShadows.MOD_ID + ".spore_filtration.desc", "Neutralisiert giftiges Miasma auf Kosten der Helm-Haltbarkeit.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.enable_spore_mask_protection", "Sporenmaskenschutz aktivieren");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.enable_spore_mask_protection.@Tooltip", "Schützt den Träger vor giftigem Miasma und Statuseffekten.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.spore_mask_damage_per_exposure", "Sporenmasken-Haltbarkeitskosten pro Belastung");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.spore_mask_damage_per_exposure.@Tooltip", "Haltbarkeit, die die Maske pro Miasma-Prüfung verbraucht.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.enable_spore_filtration_enchantment", "Sporenfiltration-Verzauberung aktivieren");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.enable_spore_filtration_enchantment.@Tooltip", "Helm-Verzauberung zur Neutralisierung giftiger Sporen.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.filtration_level_1_durability_cost", "Filtration Stufe I Haltbarkeitskosten");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.filtration_level_1_durability_cost.@Tooltip", "Helm-Haltbarkeitskosten pro Prüfung mit Stufe I.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.filtration_level_2_durability_cost", "Filtration Stufe II Haltbarkeitskosten");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.filtration_level_2_durability_cost.@Tooltip", "Helm-Haltbarkeitskosten pro Prüfung mit Stufe II.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.filtration_level_3_save_chance", "Filtration Stufe III Haltbarkeit-Ersparnischance");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.filtration_level_3_save_chance.@Tooltip", "Chance, Haltbarkeitsverlust mit Stufe III zu verhindern.");
    }
}
