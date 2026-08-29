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
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.general.show_debug_in_chat", "Debug-Mathematik im Chat anzeigen");

        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.rain_humidity_base", "Grundfeuchtigkeit (Regen/Schnee)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.dry_humidity_base", "Grundfeuchtigkeit (Klar/Trocken)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.water_adjacent_bonus", "Wasser-Nachbarschaftsbonus");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.cauldron_adjacent_bonus", "Kessel/Schlamm-Nachbarschaftsbonus");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.max_local_humidity_bonus", "Max. lokaler Feuchtigkeitsbonus");
        
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.cave_start_y", "Höhlenbeginn (Y-Level)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.cave_full_y", "Volle Höhlentiefe (Y-Level)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.cave_temperature", "Höhlentemperatur");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.depth_modifier_per_level", "Tiefenfeuchtigkeits-Modifikator (+ pro Block)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.max_depth_modifier", "Max. Tiefenfeuchtigkeits-Malus");
        
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.high_altitude_start_y", "Hochgebirgsbeginn (Y-Level)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.high_altitude_full_y", "Hochgebirgsgipfel (Y-Level)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.high_altitude_freezing_temperature", "Gefriertemperatur im Hochgebirge");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.min_temperature_survival", "Minimale Überlebenstemperatur");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.max_temperature_survival", "Maximale Überlebenstemperatur");

        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.susceptibility.stripped_wood_multiplier", "Anfälligkeit von entrindetem Holz");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.susceptibility.planks_multiplier", "Anfälligkeit von Holzbrettern");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.susceptibility.default_multiplier", "Standard-Holzanfälligkeit");
        
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.catalysts.stage_2_drop_chance", "Stage 2 Drop Chance (Veraltet)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.catalysts.stage_3_drop_chance", "Stage 3 Drop Chance (Veraltet)");

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
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.mold_toxicity_multiplier", "Schimmel-Toxizitätsmultiplikator");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.ventilation_gap_bonus", "Belüftungsbonus pro Öffnung");
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
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.blast_resistance", "Explosionsresistenz");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.blast_resistance.enable_blast_resistance_scaling", "Explosionsresistenzskalierung aktivieren");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.blast_resistance.stage_1_multiplier", "Stufe 1 (Befallen) Resistenz-Multiplikator");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.blast_resistance.stage_2_multiplier", "Stufe 2 (Schimmlig) Resistenz-Multiplikator");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.blast_resistance.stage_3_multiplier", "Stufe 3 (Verfault) Resistenz-Multiplikator");

        // Hardness & Degradation
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.hardness", "Härte & Zersetzung");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.hardness.enable_hardness_scaling", "Härteskalierung aktivieren");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.hardness.stage_1_multiplier", "Stufe 1 Härtemultiplikator (Befallen)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.hardness.stage_2_multiplier", "Stufe 2 Härtemultiplikator (Schimmelig)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.hardness.stage_3_multiplier", "Stufe 3 Härtemultiplikator (Morsch)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.hardness.enable_break_spore_cloud", "Sporenwolke beim Abbau erzeugen (ohne Behutsamkeit)");

        // Jade Tooltips
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.stage", "Stadium: ");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.infection", "Infektionsrisiko: %d%%");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.waxed", "Gewachst: ");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.yes", "Ja");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.no", "Nein");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.stage.0", "Gesund");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.stage.1", "Befallen");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.stage.2", "Schimmelig");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.stage.3", "Verfault");
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
        translationBuilder.add("jei.spores--shadows.category.waxing", "Wachsen");
        translationBuilder.add("jei.spores--shadows.category.scraping", "Axt-Schaben");
        translationBuilder.add("jei.spores--shadows.info.rotten_wood", "Morsches Holz ist brüchig und zerfällt. Es kann nicht mit einer Axt geheilt werden. Es erfordert Behutsamkeit zum Abbau, sonst zerfällt es beim Zerstören zu Staub.");
    }
}
