package moldmod.client.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;
import java.util.Map;
import java.util.HashMap;

public class ModGermanLanguageProvider extends FabricLanguageProvider {

    public ModGermanLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, "de_de", registryLookup);
    }

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup registryLookup, TranslationBuilder translationBuilder) {
        String[] woods = moldmod.SporesShadows.WOODS;

        Map<String, String> names = new HashMap<>();
        names.put("oak", "Eichen");
        names.put("spruce", "Fichten");
        names.put("birch", "Birken");
        names.put("jungle", "Tropenbaum");
        names.put("acacia", "Akazien");
        names.put("dark_oak", "Schwarzeichen");
        names.put("mangrove", "Mangroven");
        names.put("cherry", "Kirschblüten");

        for (String wood : woods) {
            String logName = wood + "_log";
            String woodName = wood + "_wood";
            String prefix = wood;
            
            String wName = names.get(wood);

            translationBuilder.add("block.spores--shadows.moldy_" + logName, "Schimmeliger " + wName + "stamm");
            translationBuilder.add("item.spores--shadows.waxed_" + logName, "Gewachster " + wName + "stamm");
            translationBuilder.add("item.spores--shadows.tainted_" + logName, "Befallener " + wName + "stamm");
            translationBuilder.add("item.spores--shadows.moldy_" + logName, "Schimmeliger " + wName + "stamm");
            translationBuilder.add("item.spores--shadows.rotten_" + logName, "Verrotteter " + wName + "stamm");

            translationBuilder.add("block.spores--shadows.moldy_stripped_" + logName, "Schimmeliger entrindeter " + wName + "stamm");
            translationBuilder.add("item.spores--shadows.waxed_stripped_" + logName, "Gewachster entrindeter " + wName + "stamm");
            translationBuilder.add("item.spores--shadows.tainted_stripped_" + logName, "Befallener entrindeter " + wName + "stamm");
            translationBuilder.add("item.spores--shadows.moldy_stripped_" + logName, "Schimmeliger entrindeter " + wName + "stamm");
            translationBuilder.add("item.spores--shadows.rotten_stripped_" + logName, "Verrotteter entrindeter " + wName + "stamm");

            if (woodName != null) {
                translationBuilder.add("block.spores--shadows.moldy_" + woodName, "Schimmeliges " + wName + "holz");
                translationBuilder.add("item.spores--shadows.waxed_" + woodName, "Gewachstes " + wName + "holz");
                translationBuilder.add("item.spores--shadows.tainted_" + woodName, "Befallenes " + wName + "holz");
                translationBuilder.add("item.spores--shadows.moldy_" + woodName, "Schimmeliges " + wName + "holz");
                translationBuilder.add("item.spores--shadows.rotten_" + woodName, "Verrottetes " + wName + "holz");

                translationBuilder.add("block.spores--shadows.moldy_stripped_" + woodName, "Schimmeliges entrindetes " + wName + "holz");
                translationBuilder.add("item.spores--shadows.waxed_stripped_" + woodName, "Gewachstes entrindetes " + wName + "holz");
                translationBuilder.add("item.spores--shadows.tainted_stripped_" + woodName, "Befallenes entrindetes " + wName + "holz");
                translationBuilder.add("item.spores--shadows.moldy_stripped_" + woodName, "Schimmeliges entrindetes " + wName + "holz");
                translationBuilder.add("item.spores--shadows.rotten_stripped_" + woodName, "Verrottetes entrindetes " + wName + "holz");
            }

            translationBuilder.add("block.spores--shadows.moldy_" + prefix + "_planks", "Schimmelige " + wName + "holzbretter");
            translationBuilder.add("item.spores--shadows.waxed_" + prefix + "_planks", "Gewachste " + wName + "holzbretter");
            translationBuilder.add("item.spores--shadows.tainted_" + prefix + "_planks", "Befallene " + wName + "holzbretter");
            translationBuilder.add("item.spores--shadows.moldy_" + prefix + "_planks", "Schimmelige " + wName + "holzbretter");
            translationBuilder.add("item.spores--shadows.rotten_" + prefix + "_planks", "Verrottete " + wName + "holzbretter");
            
            String[] blocks = {"slab", "stairs", "fence", "fence_gate", "door", "trapdoor", "pressure_plate", "button"};
            String[] blockNames = {"stufe", "treppe", "zaun", "zauntor", "tür", "falltür", "druckplatte", "knopf"};
            
            String[] mC = {"Gewachste", "Gewachste", "Gewachster", "Gewachstes", "Gewachste", "Gewachste", "Gewachste", "Gewachster"};
            String[] m = {"Schimmelige", "Schimmelige", "Schimmeliger", "Schimmeliges", "Schimmelige", "Schimmelige", "Schimmelige", "Schimmeliger"};
            String[] mT = {"Befallene", "Befallene", "Befallener", "Befallenes", "Befallene", "Befallene", "Befallene", "Befallener"};
            String[] mR = {"Verrottete", "Verrottete", "Verrotteter", "Verrottetes", "Verrottete", "Verrottete", "Verrottete", "Verrotteter"};
            
            for (int i = 0; i < blocks.length; i++) {
                String blockKey = blocks[i];
                String bName = wName + "holz" + blockNames[i];
                
                translationBuilder.add("block.spores--shadows.moldy_" + prefix + "_" + blockKey, m[i] + " " + bName);
                translationBuilder.add("item.spores--shadows.waxed_" + prefix + "_" + blockKey, mC[i] + " " + bName);
                translationBuilder.add("item.spores--shadows.tainted_" + prefix + "_" + blockKey, mT[i] + " " + bName);
                translationBuilder.add("item.spores--shadows.moldy_" + prefix + "_" + blockKey, m[i] + " " + bName);
                translationBuilder.add("item.spores--shadows.rotten_" + prefix + "_" + blockKey, mR[i] + " " + bName);
            }
        }

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
    }
}
