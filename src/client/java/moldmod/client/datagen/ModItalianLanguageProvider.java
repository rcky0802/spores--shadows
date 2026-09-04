package moldmod.client.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.registry.RegistryWrapper;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ModItalianLanguageProvider extends AbstractModLanguageProvider {

    private static final Map<String, String> WOOD_NAMES = Map.ofEntries(
        Map.entry("oak", "di Quercia"),
        Map.entry("spruce", "di Abete"),
        Map.entry("birch", "di Betulla"),
        Map.entry("jungle", "della Giungla"),
        Map.entry("acacia", "di Acacia"),
        Map.entry("dark_oak", "di Quercia Scura"),
        Map.entry("mangrove", "di Mangrovia"),
        Map.entry("cherry", "di Ciliegio"),
        Map.entry("crimson", "Cremisi"),
        Map.entry("warped", "Distorto")
    );

    public ModItalianLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, "it_it", registryLookup);
    }

    @Override
    protected String getTranslation(String wood, String blockType, String state) {
        String wName = WOOD_NAMES.get(wood);
        String blockName = "";
        boolean isFeminine = false;
        boolean isPlural = false;

        switch (blockType) {
            case "log": blockName = "Tronco"; break;
            case "stripped_log": blockName = "Tronco Scortecciato"; break;
            case "wood": blockName = "Legno"; break;
            case "stripped_wood": blockName = "Legno Scortecciato"; break;
            case "stem": blockName = "Gambo"; break;
            case "stripped_stem": blockName = "Gambo Scortecciato"; break;
            case "hyphae": blockName = "Ife"; isFeminine = true; isPlural = true; break;
            case "stripped_hyphae": blockName = "Ife Scortecciate"; isFeminine = true; isPlural = true; break;
            case "planks": blockName = "Assi"; isFeminine = true; isPlural = true; break;
            case "slab": blockName = "Lastra"; isFeminine = true; break;
            case "stairs": blockName = "Scale"; isFeminine = true; isPlural = true; break;
            case "fence": blockName = "Staccionata"; isFeminine = true; break;
            case "fence_gate": blockName = "Cancello"; break;
            case "door": blockName = "Porta"; isFeminine = true; break;
            case "trapdoor": blockName = "Botola"; isFeminine = true; break;
            case "pressure_plate": blockName = "Pedana a pressione"; isFeminine = true; break;
            case "button": blockName = "Pulsante"; break;
        }

        String stateStr = "";
        if (state.equals("moldy")) stateStr = isFeminine ? (isPlural ? "Ammuffite" : "Ammuffita") : (isPlural ? "Ammuffiti" : "Ammuffito");
        else if (state.equals("waxed")) stateStr = isFeminine ? (isPlural ? "Cerate" : "Cerata") : (isPlural ? "Cerati" : "Cerato");
        else if (state.equals("tainted")) stateStr = isFeminine ? (isPlural ? "Intaccate" : "Intaccata") : (isPlural ? "Intaccati" : "Intaccato");
        else if (state.equals("rotten")) stateStr = isFeminine ? (isPlural ? "Marce" : "Marcia") : (isPlural ? "Marci" : "Marcio");
        else if (state.equals("waxed_tainted")) stateStr = (isFeminine ? (isPlural ? "Intaccate" : "Intaccata") : (isPlural ? "Intaccati" : "Intaccato")) + " " + (isFeminine ? (isPlural ? "Cerate" : "Cerata") : (isPlural ? "Cerati" : "Cerato"));
        else if (state.equals("waxed_moldy")) stateStr = (isFeminine ? (isPlural ? "Ammuffite" : "Ammuffita") : (isPlural ? "Ammuffiti" : "Ammuffito")) + " " + (isFeminine ? (isPlural ? "Cerate" : "Cerata") : (isPlural ? "Cerati" : "Cerato"));
        else if (state.equals("waxed_rotten")) stateStr = (isFeminine ? (isPlural ? "Marce" : "Marcia") : (isPlural ? "Marci" : "Marcio")) + " " + (isFeminine ? (isPlural ? "Cerate" : "Cerata") : (isPlural ? "Cerati" : "Cerato"));

        return blockName + " " + wName + " " + stateStr;
    }

    @Override
    protected void generateTooltipsAndConfig(TranslationBuilder translationBuilder) {
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".waxed", "Cerato");
        translationBuilder.add("item." + moldmod.SporesShadows.MOD_ID + ".waxed_format", "%s Cerato");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".moldy_log_desc_1", "Può essere trasformato in assi pulite con perdita di materiale,");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".moldy_log_desc_2", "ma non può essere usato per normali ricette vanilla.");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".moldy_planks_desc_1", "Utile solo per crafting semplici (bastoni, staccionate).");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".moldy_planks_desc_2", "Non può essere usato efficacemente in ricette complesse.");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".moldy_general_desc_1", "Componente di legno degradato.");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".moldy_general_desc_2", "Strutturalmente indebolito dalla muffa.");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".moldy_redstone_desc_1", "La muffa ha compromesso il meccanismo.");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".moldy_redstone_desc_2", "Il tempo di attivazione è notevolmente aumentato.");

        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".title", "Configurazione Spores & Shadows");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.general", "Generale");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.environment", "Ambiente");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.susceptibility", "Suscettibilità");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.catalysts", "Catalizzatori");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.drops", "Drop");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.structures", "Strutture");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.furnace_multipliers", "Efficienza Fornace");

        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.general", "Generale");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.susceptibility", "Suscettibilità");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.catalysts", "Catalizzatori");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment", "Ambiente");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.drops", "Drop");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures", "Strutture");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.furnaceMultipliers", "Efficienza Fornace");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.cave_temperature.@Tooltip", "Temperatura per ignorare il sole nelle caverne.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.high_altitude_freezing_temperature.@Tooltip", "Temperatura di congelamento che ferma la muffa ad alta quota.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.general.enable_mold_growth", "Abilita Crescita Muffa");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.general.infection_threshold", "Soglia di Infezione (R > X)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.general.scan_radius", "Raggio di Scansione");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.general.scan_radius.@Tooltip", "1 = 3x3x3 blocchi, 2 = 5x5x5 blocchi. Valori alti causano lag.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.general.structures_immune", "Strutture Generate sono Immuni");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.general.structures_immune.@Tooltip", "Se attivato, relitti e villaggi non marciranno da soli prima dell'interazione del giocatore.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.general.show_debug_in_chat", "Mostra Logica Debug in Chat");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.general.axe_scrape_damage", "Danno Ascia (Raschiatura)");

        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.rain_humidity_base", "Umidità Base (Pioggia/Neve)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.dry_humidity_base", "Umidità Base (Sereno/Secco)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.water_adjacent_bonus", "Bonus Vicinanza Acqua");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.cauldron_adjacent_bonus", "Bonus Vicinanza Calderoni/Fango");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.max_local_humidity_bonus", "Bonus Max Umidità Locale");
        
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.cave_start_y", "Inizio Caverne (Livello Y)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.cave_full_y", "Profondità Totale Caverne (Livello Y)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.cave_temperature", "Temperatura Caverne");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.depth_modifier_per_level", "Modificatore Umidità per Profondità (+ per blocco)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.max_depth_modifier", "Malus Max Umidità Profondità");
        
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.high_altitude_start_y", "Inizio Alta Quota (Livello Y)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.high_altitude_full_y", "Picco Alta Quota (Livello Y)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.high_altitude_freezing_temperature", "Temperatura Congelamento Alta Quota");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.min_temperature_survival", "Temp Minima Sopravvivenza");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.max_temperature_survival", "Temp Massima Sopravvivenza");

        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.susceptibility.stripped_wood_multiplier", "Suscettibilità Legno Scortecciato");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.susceptibility.planks_multiplier", "Suscettibilità Assi di Legno");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.susceptibility.default_multiplier", "Suscettibilità Legno Predefinita");
        
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.catalysts.mud_bonus", "Malus Fango (Mud)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.catalysts.fungi_bonus", "Malus Vicinanza Funghi");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.catalysts.spore_blossom_bonus", "Malus Fiore di Spora (Spore Blossom)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.catalysts.podzol_mycelium_bonus", "Malus Podzol/Micelio");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.catalysts.tainted_block_bonus", "Malus Vicinanza Blocco Intaccato");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.catalysts.moldy_block_bonus", "Malus Vicinanza Blocco Ammuffito");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.catalysts.rotten_block_bonus", "Malus Vicinanza Blocco Marcio");

        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.drops.stage_2_drop_chance", "Prob. Drop Blocco Ammuffito (0.0 a 1.0)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.drops.stage_2_drop_chance.@Tooltip", "Probabilità che un blocco Ammuffito droppi sé stesso quando rotto.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.drops.stage_3_drop_chance", "Prob. Drop Blocco Marcio (0.0 a 1.0)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.drops.stage_3_drop_chance.@Tooltip", "Probabilità che un blocco Marcio droppi sé stesso quando rotto.");

        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat1_critical", "Categoria 1 (Degrado Critico)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat2_high", "Categoria 2 (Degrado Alto)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat3_moderate", "Categoria 3 (Degrado Moderato)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat4_low", "Categoria 4 (Degrado Basso)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat1_critical.moldy_chance", "% Ammuffito");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat2_high.moldy_chance", "% Ammuffito");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat3_moderate.moldy_chance", "% Ammuffito");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat4_low.moldy_chance", "% Ammuffito");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat1_critical.tainted_chance", "% Intaccato");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat2_high.tainted_chance", "% Intaccato");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat3_moderate.tainted_chance", "% Intaccato");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat4_low.tainted_chance", "% Intaccato");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat1_critical.rotten_chance", "% Marcio");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat2_high.rotten_chance", "% Marcio");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat3_moderate.rotten_chance", "% Marcio");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat4_low.rotten_chance", "% Marcio");

        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.furnaceMultipliers.stage_0", "Efficienza Fornace (Sano)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.furnaceMultipliers.stage_1", "Efficienza Fornace (Contagiato)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.furnaceMultipliers.stage_2", "Efficienza Fornace (Muffito)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.furnaceMultipliers.stage_3", "Efficienza Fornace (Marcio)");

        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.toxicity", "Tossicità");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity", "Tossicità");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.enable_toxic_air", "Abilita Aria Tossica / Miasma");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.check_interval_ticks", "Intervallo Controlli (Ticks)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.scan_radius", "Raggio Nube Tossica");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.max_air_volume", "Volume Massimo Aria (m³)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.max_euclidean_radius", "Raggio Sferico Massimo");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.mold_toxicity_multiplier", "Moltiplicatore Tossicità Muffa");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.ventilation_gap_bonus", "Bonus Ventilazione per Fessura");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.threshold_hunger", "Soglia Fame");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.threshold_nausea", "Soglia Nausea");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.threshold_poison", "Soglia Veleno");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.density_threshold_high", "Soglia Densità Alta");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.density_threshold_medium", "Soglia Densità Media");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.density_threshold_low", "Soglia Densità Bassa");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.duration_hunger_ticks", "Durata Fame (Tick)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.duration_nausea_ticks", "Durata Nausea (Tick)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.duration_poison_ticks", "Durata Veleno (Tick)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.hunger_amplifier", "Potenza Fame (Amplificatore)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.nausea_amplifier", "Potenza Nausea (Amplificatore)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.poison_amplifier", "Potenza Veleno (Amplificatore)");
        
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.client", "Client & Shader");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.client", "Client & Shader");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.client.mold_z_offset", "Z-Offset Muffa (Fix Shader)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.client.mold_z_offset.@Tooltip[0]", "Aumenta questo valore se noti problemi grafici (Z-fighting) con shader.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.client.mold_z_offset.@Tooltip[1]", "Predefinito: 0.002. Prova 0.005 o superiori se necessario.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.water_scan_radius", "Raggio Scansione Acqua");

        // Flammability
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.flammability", "Infiammabilità");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.flammability", "Infiammabilità");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.flammability.enable_flammability", "Abilita Scalamento Infiammabilità");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.flammability.stage_1_burn_bonus", "Bonus Probabilità Innesco Stadio 1 (Intaccato)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.flammability.stage_1_spread_bonus", "Bonus Propagazione Fuoco Stadio 1 (Intaccato)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.flammability.stage_2_burn_bonus", "Bonus Probabilità Innesco Stadio 2 (Ammuffito)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.flammability.stage_2_spread_bonus", "Bonus Propagazione Fuoco Stadio 2 (Ammuffito)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.flammability.stage_3_burn_bonus", "Bonus Probabilità Innesco Stadio 3 (Marcio)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.flammability.stage_3_spread_bonus", "Bonus Propagazione Fuoco Stadio 3 (Marcio)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.flammability.waxed_burn_bonus", "Bonus Innesco Legno Cerato");

        // Blast Resistance
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.blast_resistance", "Resistenza Esplosioni");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.blast_resistance", "Resistenza Esplosioni");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.blast_resistance.enable_blast_resistance_scaling", "Abilita Scalamento Resistenza Esplosioni");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.blast_resistance.stage_1_multiplier", "Moltiplicatore Resistenza Stadio 1 (Intaccato)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.blast_resistance.stage_2_multiplier", "Moltiplicatore Resistenza Stadio 2 (Ammuffito)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.blast_resistance.stage_3_multiplier", "Moltiplicatore Resistenza Stadio 3 (Marcio)");

        // Hardness
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.hardness", "Durezza e Degrado");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.hardness.enable_hardness_scaling", "Abilita Scalamento Durezza");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.hardness.stage_1_multiplier", "Moltiplicatore Durezza Stadio 1 (Contaminato)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.hardness.stage_2_multiplier", "Moltiplicatore Durezza Stadio 2 (Ammuffito)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.hardness.stage_3_multiplier", "Moltiplicatore Durezza Stadio 3 (Marcio)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.hardness.enable_break_spore_cloud", "Nuvola di Spore alla Rottura (Senza Tocco di Velluto)");

        // Jade Tooltips
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.stage", "Stadio: ");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.infection", "Rischio Infezione: %d%%");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.waxed", "Cerato: ");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.yes", "Sì");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.no", "No");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.stage.0", "Sano");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.stage.1", "Intaccato");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.stage.2", "Ammuffito");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.stage.3", "Marcio");
        translationBuilder.add("config.jade.plugin_" + moldmod.SporesShadows.MOD_ID + ".moldy_info", "Spores & Shadows: Info Muffa");

        // Advancements
        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".root.title", "Spores & Shadows");
        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".root.description", "Sopravvivi al decadimento della natura.");
        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".wax_block.title", "Prevenzione Naturale");
        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".wax_block.description", "Usa un favo di miele per cerare un blocco e fermare la muffa.");
        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".scrape_mold.title", "Olio di Gomito");
        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".scrape_mold.description", "Raschia via la muffa da un blocco di legno con un'ascia.");
        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".toxic_air.title", "Respiro Corto");
        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".toxic_air.description", "Subisci il veleno del miasma respirando troppa muffa.");
        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".crumble.title", "Polvere alla Polvere");
        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".crumble.description", "Tenta di rompere un blocco di legno marcio e guardalo sgretolarsi nel nulla.");

        // JEI Integration
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".category.waxing", "Ceratura");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".category.scraping", "Raschiamento con Ascia");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".info.rotten_wood", "Il legno marcio è friabile e degradato. Non può essere curato con un'ascia. Richiede Tocco di Velluto per essere raccolto, altrimenti si disintegrerà nel nulla quando viene rotto.");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".info.spore_mask", "La Maschera Antispore offre protezione totale dal miasma tossico (Veleno, Nausea e Fame). Consuma durabilità mentre filtra l'aria tossica. Sostituisci il filtro riparandola all'incudine con Lana (#minecraft:wool). Può essere incantata solo con Indistruttibilità, Ripristino e Maledizione della Scomparsa.");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".info.spore_filtration", "Filtrazione Spore è un incantesimo per elmi che neutralizza il miasma tossico e l'inalazione di spore. Consuma durabilità dell'elmo quando esposto al miasma (Livello I: 2 durabilità, Livello II: 1 durabilità, Livello III: 50% probabilità di risparmio). Compatibile con tutti gli elmi convenzionali.");

        // Jade Tooltips for Spore Protection & Spore Detector
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.spore_protection_mask", "Protezione Spore: Attiva (Maschera Antispore)");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.spore_protection_enchant", "Filtrazione Spore: Livello %d");
        translationBuilder.add("config.jade.plugin_" + moldmod.SporesShadows.MOD_ID + ".spore_protection_info", "Spores & Shadows: Info Protezione Spore");
        translationBuilder.add("config.jade.plugin_" + moldmod.SporesShadows.MOD_ID + ".spore_detector_info", "Spores & Shadows: Info Rilevatore di Miasma");

        translationBuilder.add("item." + moldmod.SporesShadows.MOD_ID + ".spore_mask", "Maschera Antispore");
        translationBuilder.add("block." + moldmod.SporesShadows.MOD_ID + ".spore_detector", "Rilevatore di Miasma");
        translationBuilder.add("item." + moldmod.SporesShadows.MOD_ID + ".spore_detector", "Rilevatore di Miasma");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".info.spore_detector", "Il Rilevatore di Miasma misura la tossicità dell'aria e la ventilazione della stanza. Usa Tasto Destro nel vuoto per scansionare l'ambiente. Può essere posizionato su pareti o pavimenti ed emette un segnale di Pietrarossa proporzionale alla densità di spore.");
        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".spore_mask_protection.title", "Aria Pura");
        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".spore_mask_protection.description", "Filtra le spore tossiche respirando con una Maschera Antispore in una stanza contaminata.");

        // Enchantments
        translationBuilder.add("enchantment." + moldmod.SporesShadows.MOD_ID + ".spore_filtration", "Filtrazione Spore");
        translationBuilder.add("enchantment." + moldmod.SporesShadows.MOD_ID + ".spore_filtration.desc", "Neutralizza il miasma tossico consumando durabilità dell'elmo.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.enable_spore_filtration_enchantment", "Abilita Incantesimo Filtrazione Spore");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.filtration_level_1_durability_cost", "Costo Durabilità Filtrazione Livello I");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.filtration_level_2_durability_cost", "Costo Durabilità Filtrazione Livello II");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.filtration_level_3_save_chance", "Probabilità Risparmio Durabilità Livello III");
    }
}
