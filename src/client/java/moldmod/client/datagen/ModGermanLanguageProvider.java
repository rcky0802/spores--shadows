package moldmod.client.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.registry.RegistryWrapper;

import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class ModGermanLanguageProvider extends AbstractModLanguageProvider {

    public ModGermanLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, "de_de", registryLookup);
    }

    @Override
    protected String getTranslation(String wood, String blockType, String state) {
        Map<String, String> names = new HashMap<>();
        names.put("oak", "Eichen");
        names.put("spruce", "Fichten");
        names.put("birch", "Birken");
        names.put("jungle", "Tropenbaum");
        names.put("acacia", "Akazien");
        names.put("dark_oak", "Schwarzeichen");
        names.put("mangrove", "Mangroven");
        names.put("cherry", "Kirschblüten");

        String wName = names.get(wood);

        String blockSuffix = "";
        String gender = "n"; // n = neuter, m = masculine, f = feminine

        switch (blockType) {
            case "log": blockSuffix = "stamm"; gender = "m"; break;
            case "stripped_log": blockSuffix = "stamm"; gender = "m"; wName = "entrindeter " + wName; break;
            case "wood": blockSuffix = "holz"; gender = "n"; break;
            case "stripped_wood": blockSuffix = "holz"; gender = "n"; wName = "entrindetes " + wName; break;
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
        }

        return stateStr + " " + wName + blockSuffix;
    }

    @Override
    protected void generateTooltipsAndConfig(TranslationBuilder translationBuilder) {
        translationBuilder.add("tooltip.spores--shadows.waxed", "Gewachst");
        translationBuilder.add("item.spores--shadows.waxed_format", "Gewachstes %s");
        translationBuilder.add("tooltip.spores--shadows.moldy_log_desc_1", "Kann unter Materialverlust zu sauberen Brettern verarbeitet werden,");
        translationBuilder.add("tooltip.spores--shadows.moldy_log_desc_2", "ist aber für normale Vanilla-Rezepte unbrauchbar.");
        translationBuilder.add("tooltip.spores--shadows.moldy_planks_desc_1", "Nur für einfache Rezepte nützlich (Stöcke, Zäune).");
        translationBuilder.add("tooltip.spores--shadows.moldy_planks_desc_2", "Nicht effektiv in komplexen Rezepten einsetzbar.");
        translationBuilder.add("tooltip.spores--shadows.moldy_general_desc_1", "Degradiertes Holzbauteil.");
        translationBuilder.add("tooltip.spores--shadows.moldy_general_desc_2", "Strukturell durch Schimmel geschwächt.");

        translationBuilder.add("text.autoconfig.spores--shadows.title", "Spores & Shadows Konfiguration");
        translationBuilder.add("text.autoconfig.spores--shadows.category.general", "Allgemein");
        translationBuilder.add("text.autoconfig.spores--shadows.category.environment", "Umwelt");
        translationBuilder.add("text.autoconfig.spores--shadows.category.susceptibility", "Anfälligkeit");
        translationBuilder.add("text.autoconfig.spores--shadows.category.catalysts", "Katalysatoren");
        translationBuilder.add("text.autoconfig.spores--shadows.category.drops", "Drops");
        translationBuilder.add("text.autoconfig.spores--shadows.category.structures", "Strukturen");
        translationBuilder.add("text.autoconfig.spores--shadows.category.furnace_multipliers", "Ofeneffizienz");

        translationBuilder.add("text.autoconfig.spores--shadows.option.general", "Allgemein");
        translationBuilder.add("text.autoconfig.spores--shadows.option.susceptibility", "Anfälligkeit");
        translationBuilder.add("text.autoconfig.spores--shadows.option.catalysts", "Katalysatoren");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment", "Umwelt");
        translationBuilder.add("text.autoconfig.spores--shadows.option.drops", "Drops");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures", "Strukturen");
        translationBuilder.add("text.autoconfig.spores--shadows.option.furnaceMultipliers", "Ofeneffizienz");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.cave_temperature.@Tooltip", "Temperatur, um die Sonne in Höhlen zu ignorieren.");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.high_altitude_freezing_temperature.@Tooltip", "Gefriertemperatur, die Schimmel in großen Höhen stoppt.");
        translationBuilder.add("text.autoconfig.spores--shadows.option.general.enable_mold_growth", "Schimmelwachstum aktivieren");
        translationBuilder.add("text.autoconfig.spores--shadows.option.general.infection_threshold", "Infektionsschwelle (R > X)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.general.scan_radius", "Scan-Radius");
        translationBuilder.add("text.autoconfig.spores--shadows.option.general.scan_radius.@Tooltip", "1 = 3x3x3, 2 = 5x5x5. Höhere Werte verursachen Lag.");
        translationBuilder.add("text.autoconfig.spores--shadows.option.general.structures_immune", "Generierte Strukturen sind immun");
        translationBuilder.add("text.autoconfig.spores--shadows.option.general.structures_immune.@Tooltip", "Wenn aktiviert, verrotten Schiffswracks und Dörfer nicht, bis der Spieler interagiert.");
        translationBuilder.add("text.autoconfig.spores--shadows.option.general.show_debug_in_chat", "Debug-Mathematik im Chat anzeigen");

        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.rain_humidity_base", "Grundfeuchtigkeit (Regen/Schnee)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.dry_humidity_base", "Grundfeuchtigkeit (Klar/Trocken)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.water_adjacent_bonus", "Wasser-Nachbarschaftsbonus");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.cauldron_adjacent_bonus", "Kessel/Schlamm-Nachbarschaftsbonus");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.max_local_humidity_bonus", "Max. lokaler Feuchtigkeitsbonus");
        
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.cave_start_y", "Höhlenbeginn (Y-Level)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.cave_full_y", "Volle Höhlentiefe (Y-Level)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.cave_temperature", "Höhlentemperatur");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.depth_modifier_per_level", "Tiefenfeuchtigkeits-Modifikator (+ pro Block)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.max_depth_modifier", "Max. Tiefenfeuchtigkeits-Malus");
        
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.high_altitude_start_y", "Hochgebirgsbeginn (Y-Level)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.high_altitude_full_y", "Hochgebirgsgipfel (Y-Level)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.high_altitude_freezing_temperature", "Gefriertemperatur im Hochgebirge");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.min_temperature_survival", "Minimale Überlebenstemperatur");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.max_temperature_survival", "Maximale Überlebenstemperatur");

        translationBuilder.add("text.autoconfig.spores--shadows.option.susceptibility.stripped_wood_multiplier", "Anfälligkeit von entrindetem Holz");
        translationBuilder.add("text.autoconfig.spores--shadows.option.susceptibility.planks_multiplier", "Anfälligkeit von Holzbrettern");
        translationBuilder.add("text.autoconfig.spores--shadows.option.susceptibility.default_multiplier", "Standard-Holzanfälligkeit");
        
        translationBuilder.add("text.autoconfig.spores--shadows.option.catalysts.stage_2_drop_chance", "Stage 2 Drop Chance (Veraltet)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.catalysts.stage_3_drop_chance", "Stage 3 Drop Chance (Veraltet)");

        translationBuilder.add("text.autoconfig.spores--shadows.option.catalysts.mud_bonus", "Schlamm-Malus");
        translationBuilder.add("text.autoconfig.spores--shadows.option.catalysts.fungi_bonus", "Pilz-Malus");
        translationBuilder.add("text.autoconfig.spores--shadows.option.catalysts.spore_blossom_bonus", "Sporenblüten-Malus");
        translationBuilder.add("text.autoconfig.spores--shadows.option.catalysts.podzol_mycelium_bonus", "Podsol-/Myzel-Malus");
        translationBuilder.add("text.autoconfig.spores--shadows.option.catalysts.tainted_block_bonus", "Befallener Block-Malus");
        translationBuilder.add("text.autoconfig.spores--shadows.option.catalysts.moldy_block_bonus", "Schimmelblock-Malus");
        translationBuilder.add("text.autoconfig.spores--shadows.option.catalysts.rotten_block_bonus", "Verrotteter Block-Malus");

        translationBuilder.add("text.autoconfig.spores--shadows.option.drops.stage_2_drop_chance", "Dropchance Schimmelblock (0.0 bis 1.0)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.drops.stage_2_drop_chance.@Tooltip", "Chance, dass ein Schimmelblock sich selbst droppt.");
        translationBuilder.add("text.autoconfig.spores--shadows.option.drops.stage_3_drop_chance", "Dropchance Verrotteter Block (0.0 bis 1.0)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.drops.stage_3_drop_chance.@Tooltip", "Chance, dass ein verrotteter Block sich selbst droppt.");

        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat1_critical", "Kategorie 1 (Kritischer Zerfall)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat2_high", "Kategorie 2 (Hoher Zerfall)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat3_moderate", "Kategorie 3 (Mäßiger Zerfall)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat4_low", "Kategorie 4 (Geringer Zerfall)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat1_critical.moldy_chance", "% Schimmelig");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat2_high.moldy_chance", "% Schimmelig");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat3_moderate.moldy_chance", "% Schimmelig");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat4_low.moldy_chance", "% Schimmelig");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat1_critical.tainted_chance", "% Befallen");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat2_high.tainted_chance", "% Befallen");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat3_moderate.tainted_chance", "% Befallen");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat4_low.tainted_chance", "% Befallen");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat1_critical.rotten_chance", "% Verrottet");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat2_high.rotten_chance", "% Verrottet");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat3_moderate.rotten_chance", "% Verrottet");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat4_low.rotten_chance", "% Verrottet");

        translationBuilder.add("text.autoconfig.spores--shadows.option.furnaceMultipliers.stage_0", "Ofeneffizienz (Gesund)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.furnaceMultipliers.stage_1", "Ofeneffizienz (Befallen)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.furnaceMultipliers.stage_2", "Ofeneffizienz (Schimmelig)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.furnaceMultipliers.stage_3", "Ofeneffizienz (Verrottet)");
        
        translationBuilder.add("text.autoconfig.spores--shadows.category.toxicity", "Toxizität");
        translationBuilder.add("text.autoconfig.spores--shadows.option.toxicity", "Toxizität");
        translationBuilder.add("text.autoconfig.spores--shadows.option.toxicity.check_interval_ticks", "Kontrollintervall (Ticks)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.toxicity.scan_radius", "Giftwolken-Radius");
        translationBuilder.add("text.autoconfig.spores--shadows.option.toxicity.threshold_nausea", "Übelkeit-Schwelle");
        translationBuilder.add("text.autoconfig.spores--shadows.option.toxicity.threshold_poison", "Gift-Schwelle");
        translationBuilder.add("text.autoconfig.spores--shadows.option.toxicity.duration_nausea_ticks", "Dauer Übelkeit (Ticks)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.toxicity.duration_poison_ticks", "Dauer Gift (Ticks)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.water_scan_radius", "Wasser-Suchradius");
    }
}
