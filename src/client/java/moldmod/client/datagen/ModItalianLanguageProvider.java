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
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.general.rotten_break_chance_on_use", "Probabilità Rottura Blocco Marcio all'Uso");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.general.rotten_break_chance_on_use.@Tooltip", "Probabilità che un blocco funzionale in legno marcio (porte, botole, pulsanti, ecc.) si rompa all'uso.");

        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.rain_humidity_base", "Umidità Base (Pioggia/Neve)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.dry_humidity_base", "Umidità Base (Sereno/Secco)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.cauldron_adjacent_bonus", "Bonus Vicinanza Calderoni/Fango");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.water_source_humidity_contribution", "Contributo Umidità per Fonte d'Acqua");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.water_source_humidity_contribution.@Tooltip", "Umidità lineare aggiunta da ogni blocco d'acqua affacciato sulla stanza.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.max_room_water_humidity_bonus", "Tetto Massimo Umidità da Acqua");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.max_room_water_humidity_bonus.@Tooltip", "Limite massimo di saturazione dell'umidità prodotta dall'acqua nella stanza.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.enable_dynamic_room_humidity", "Umidità Dinamica di Stanza");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.enable_dynamic_room_humidity.@Tooltip", "Attiva la transizione asintotica dell'umidità con fattore alpha.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.humidity_saturation_speed", "Velocità Saturazione Umidità (Alpha)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.humidity_saturation_speed.@Tooltip", "Velocità con cui l'umidità sale quando l'ambiente è chiuso o umido.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.humidity_dissipation_speed", "Velocità Dissipazione Umidità (Alpha)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.humidity_dissipation_speed.@Tooltip", "Velocità con cui l'umidità si dissipa quando l'ambiente è arieggiato.");
        
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
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.enable_ventilation_drying", "Abilita Asciugatura da Ventilazione");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.enable_ventilation_drying.@Tooltip", "Riduce l'umidità effettiva negli spazi ben ventilati.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.aeration_drying_bonus", "Bonus Asciugatura Areazione");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.aeration_drying_bonus.@Tooltip", "Riduzione massima dell'umidità fornita dalla ventilazione.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.ventilation_threshold_full_aeration", "Soglia Areazione Massima");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.ventilation_threshold_full_aeration.@Tooltip", "Punteggio di ventilazione necessario per il 100% dell'asciugatura.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.enable_miasma_spore_pressure", "Abilita Pressione Spore Miasma");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.enable_miasma_spore_pressure.@Tooltip", "Le spore sospese nell'aria accelerano il rischio di muffa.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.miasma_spore_multiplier", "Moltiplicatore Pressione Spore Miasma");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.miasma_spore_multiplier.@Tooltip", "Moltiplicatore del rischio muffa dovuto al miasma intrappolato.");

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
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.max_euclidean_radius.@Tooltip", "Raggio sferico massimo per i controlli dell'aria tossica.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.mold_toxicity_multiplier", "Moltiplicatore Tossicità Muffa");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.open_sky_ventilation_per_block", "Portata Ventilazione Cielo Aperto");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.slab_ventilation_value", "Valore Ventilazione Lastre");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.stairs_ventilation_value", "Valore Ventilazione Scale");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.copper_grate_ventilation_per_block", "Portata Ventilazione Grata di Rame");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.leaves_ventilation_value", "Valore Ventilazione Foglie");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.door_ventilation_value", "Valore Ventilazione Porte");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.trapdoor_ventilation_value", "Valore Ventilazione Botole");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.fence_gate_open_ventilation_value", "Valore Ventilazione Cancelletto Aperto");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.ventilation_gap_bonus", "Bonus Ventilazione per Fessura");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.ventilation_distance_alpha", "Resistenza Distanza Ventilazione");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.ventilation_distance_alpha.@Tooltip", "Fattore di decadimento del flusso d'aria in base alla distanza.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.enable_distributed_miasma", "Abilita Flusso Miasma Distribuito");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.enable_distributed_miasma.@Tooltip", "Abilita il calcolo a grafo del flusso di miasma tra aperture e volumi comunicanti.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.enable_dynamic_spore_saturation", "Abilita Saturazione Dinamica Spore");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.enable_dynamic_spore_saturation.@Tooltip", "Simula l'accumulo e la dissipazione progressiva del miasma nel tempo.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.dissipation_speed_multiplier", "Moltiplicatore Velocità Dissipazione");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.dissipation_speed_multiplier.@Tooltip", "Velocità con cui il miasma si disperde quando la stanza è arieggiata.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.saturation_speed_multiplier", "Moltiplicatore Velocità Saturazione");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.saturation_speed_multiplier.@Tooltip", "Velocità con cui il miasma si accumula in stanze chiuse e contaminate.");
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
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.blastResistance", "Resistenza Esplosioni");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.blastResistance.enable_blast_resistance_scaling", "Abilita Scalamento Resistenza Esplosioni");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.blastResistance.stage_1_multiplier", "Moltiplicatore Resistenza Stadio 1 (Intaccato)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.blastResistance.stage_2_multiplier", "Moltiplicatore Resistenza Stadio 2 (Ammuffito)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.blastResistance.stage_3_multiplier", "Moltiplicatore Resistenza Stadio 3 (Marcio)");

        // Hardness
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.hardness", "Durezza e Degrado");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.hardness", "Durezza e Degrado");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.hardness.enable_hardness_scaling", "Abilita Scalamento Durezza");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.hardness.stage_1_multiplier", "Moltiplicatore Durezza Stadio 1 (Contaminato)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.hardness.stage_2_multiplier", "Moltiplicatore Durezza Stadio 2 (Ammuffito)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.hardness.stage_3_multiplier", "Moltiplicatore Durezza Stadio 3 (Marcio)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.hardness.enable_break_spore_cloud", "Nuvola di Spore alla Rottura (Senza Tocco di Velluto)");

        // Redstone
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.redstone", "Pietrarossa");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.redstone", "Pietrarossa");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.redstone.duration_multiplier", "Moltiplicatore Durata Segnale Pietrarossa");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.redstone.duration_multiplier.@Tooltip", "Moltiplicatore applicato alla durata del segnale per pulsanti e pedane ammuffite.");

        // Composter
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.composter", "Compostiera");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.composter", "Compostiera");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.composter.tainted_chance", "Probabilità Compostaggio Stadio 1 (Intaccato)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.composter.tainted_chance.@Tooltip", "Probabilità per gli oggetti di legno intaccato di riempire un livello del composter.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.composter.moldy_chance", "Probabilità Compostaggio Stadio 2 (Ammuffito)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.composter.moldy_chance.@Tooltip", "Probabilità per gli oggetti di legno ammuffito di riempire un livello del composter.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.composter.rotten_chance", "Probabilità Compostaggio Stadio 3 (Marcio)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.composter.rotten_chance.@Tooltip", "Probabilità per gli oggetti di legno marcio di riempire un livello del composter.");

        // Particles
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.particles", "Particelle");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.particles", "Particelle");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.particles.break_cloud_stage_2_air", "Conteggio Particelle Spore Aria (Stadio 2)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.particles.break_cloud_stage_2_falling", "Conteggio Particelle Spore Cadenti (Stadio 2)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.particles.break_cloud_stage_2_mycelium", "Conteggio Particelle Micelio (Stadio 2)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.particles.break_cloud_stage_3_air", "Conteggio Particelle Spore Aria (Stadio 3)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.particles.break_cloud_stage_3_falling", "Conteggio Particelle Spore Cadenti (Stadio 3)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.particles.break_cloud_stage_3_mycelium", "Conteggio Particelle Micelio (Stadio 3)");

        // Spore Detector & Mask
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.spore_detector", "Rilevatore Spore & Maschera");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector", "Rilevatore Spore & Maschera");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.block_initial_delay_ticks", "Ritardo Iniziale Blocco (tick)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.block_initial_delay_ticks.@Tooltip", "Tick prima della prima scansione dopo il posizionamento del blocco.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.block_periodic_delay_ticks", "Ritardo Periodico Blocco (tick)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.block_periodic_delay_ticks.@Tooltip", "Tick tra le scansioni periodiche.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.redstone_level_multiplier", "Moltiplicatore Livello Pietrarossa");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.redstone_level_multiplier.@Tooltip", "Moltiplicato per il livello di tossicità (0-3) per produrre il segnale.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.item_use_cooldown_ticks", "Cooldown Uso Oggetto (tick)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.item_use_cooldown_ticks.@Tooltip", "Cooldown dopo la scansione con click destro.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.geiger_check_interval_ticks", "Intervallo Controllo Geiger (tick)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.geiger_check_interval_ticks.@Tooltip", "Frequenza del controllo audio passivo del contatore Geiger quando tenuto in mano.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.geiger_density_threshold", "Soglia Densità Geiger");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.geiger_density_threshold.@Tooltip", "Densità minima di spore per attivare il suono del contatore Geiger.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.spore_mask_durability", "Durabilità Maschera Antispore");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.spore_mask_durability.@Tooltip", "Durabilità massima della Maschera Antispore (richiede riavvio).");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.spore_mask_armor_points", "Punti Armatura Maschera Antispore");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.spore_mask_armor_points.@Tooltip", "Punti armatura forniti dalla Maschera Antispore (richiede riavvio).");

        // Moisture Detector
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.moisture_detector", "Rilevatore di Umidità");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.moistureDetector", "Rilevatore di Umidità");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.moistureDetector.block_initial_delay_ticks", "Ritardo Iniziale Blocco (tick)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.moistureDetector.block_initial_delay_ticks.@Tooltip", "Tick prima della prima scansione dopo il posizionamento del blocco.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.moistureDetector.block_periodic_delay_ticks", "Ritardo Periodico Blocco (tick)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.moistureDetector.block_periodic_delay_ticks.@Tooltip", "Tick tra le scansioni periodiche.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.moistureDetector.item_use_cooldown_ticks", "Cooldown Uso Oggetto (tick)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.moistureDetector.item_use_cooldown_ticks.@Tooltip", "Cooldown dopo la scansione con click destro.");

        // Dehumidifier
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.dehumidifier", "Deumidificatore");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.dehumidifier", "Deumidificatore");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.dehumidifier.capacity_mb", "Capacità Serbatoio Acqua (mB)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.dehumidifier.capacity_mb.@Tooltip", "Massima quantità di acqua condensata conservabile in milliBucket (1000 mB = 1 Secchio).");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.dehumidifier.ticks_per_mb", "Tick per mB Condensato");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.dehumidifier.ticks_per_mb.@Tooltip", "Tick operativi base necessari per condensare 1 mB di acqua ad alta umidità.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.dehumidifier.fuel_multiplier", "Moltiplicatore Durata Carburante");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.dehumidifier.fuel_multiplier.@Tooltip", "Moltiplicatore applicato ai tempi di combustione della fornace (es. 4.0 = il carbone dura 4 volte di più).");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.dehumidifier.drying_power", "Potere Deumidificante (Bonus per Unità)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.dehumidifier.drying_power.@Tooltip", "Riduzione lineare sottratta all'umidità target della stanza per ogni deumidificatore attivo.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.dehumidifier.energy_capacity", "Capacità Buffer Energetico (E/RF)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.dehumidifier.energy_capacity.@Tooltip", "Capacità di accumulo interno di energia se sono presenti mod compatibili (TR/RF).");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.dehumidifier.energy_cost_per_tick", "Consumo Energetico (E/tick)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.dehumidifier.energy_cost_per_tick.@Tooltip", "Energia consumata per tick durante il funzionamento al posto del combustibile solido.");

        // Structures environmental bonuses
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.underwater_rotten_bonus", "Bonus Marcio Sott'acqua/Profondità (%)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.underwater_rotten_bonus.@Tooltip", "Bonus percentuale di marcio quando sott'acqua o Y <= 60.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.underwater_tainted_bonus", "Bonus Intaccato Sott'acqua/Profondità (%)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.underwater_tainted_bonus.@Tooltip", "Bonus percentuale di intaccato quando sott'acqua o Y <= 60.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.underground_moldy_bonus", "Bonus Ammuffito Sotterraneo (%)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.underground_moldy_bonus.@Tooltip", "Bonus percentuale di ammuffito in profondità (non sott'acqua).");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.ground_contact_moldy_bonus", "Bonus Ammuffito Contatto Terreno (%)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.ground_contact_moldy_bonus.@Tooltip", "Bonus percentuale di ammuffito vicino al terreno (umidità di risalita).");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.sky_access_rotten_to_moldy", "Conversione Marcio→Ammuffito Cielo Aperto (%)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.sky_access_rotten_to_moldy.@Tooltip", "Massimo marcio convertito in ammuffito con accesso al cielo.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.sky_access_moldy_bonus", "Bonus Ammuffito Cielo Aperto (%)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.sky_access_moldy_bonus.@Tooltip", "Bonus percentuale di ammuffito esposto ad aria/pioggia.");

        // Jade Tooltips
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.infection", "Rischio Infezione: %d%%");
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
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".info.spore_mask", "La Maschera Antispore offre protezione totale dal miasma tossico (Veleno, Nausea e Fame). Consuma durabilità mentre filtra l'aria tossica. Sostituisci il filtro riparandola con un Filtro per Spore nell'incudine (la ripara completamente con un solo filtro). Nel banco da lavoro puoi combinare solo due maschere tra loro per una rapida riparazione d'emergenza. Può essere incantata solo con Indistruttibilità, Ripristino e Maledizione della Scomparsa.");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".info.spore_filter", "Utilizzato per fabbricare e riparare completamente la Maschera Antispore, nonché per futuri dispositivi di filtraggio.");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".info.spore_filtration", "Filtrazione Spore è un incantesimo per elmi che neutralizza il miasma tossico e l'inalazione di spore. Consuma durabilità dell'elmo quando esposto al miasma (Livello I: 2 durabilità, Livello II: 1 durabilità, Livello III: 50% probabilità di risparmio). Compatibile con tutti gli elmi convenzionali.");

        // Jade Tooltips for Spore Protection & Spore Detector
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.spore_protection_mask", "Protezione Spore: Attiva (Maschera Antispore)");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.spore_protection_enchant", "Filtrazione Spore: Livello %d");
        translationBuilder.add("config.jade.plugin_" + moldmod.SporesShadows.MOD_ID + ".spore_protection_info", "Spores & Shadows: Info Protezione Spore");
        translationBuilder.add("config.jade.plugin_" + moldmod.SporesShadows.MOD_ID + ".spore_detector_info", "Spores & Shadows: Info Rilevatore di Spore");

        translationBuilder.add("item." + moldmod.SporesShadows.MOD_ID + ".spore_mask", "Maschera Antispore");
        translationBuilder.add("item." + moldmod.SporesShadows.MOD_ID + ".spore_filter", "Filtro per Spore");
        translationBuilder.add("block." + moldmod.SporesShadows.MOD_ID + ".spore_detector", "Rilevatore di Spore");
        translationBuilder.add("item." + moldmod.SporesShadows.MOD_ID + ".spore_detector", "Rilevatore di Spore");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".info.spore_detector", "Il Rilevatore di Spore misura la tossicità dell'aria e la ventilazione della stanza. Usa Tasto Destro nel vuoto per scansionare l'ambiente. Può essere posizionato su pareti o pavimenti.");

        translationBuilder.add("config.jade.plugin_" + moldmod.SporesShadows.MOD_ID + ".moisture_detector_info", "Spores & Shadows: Info Rilevatore di Umidità");
        translationBuilder.add("block." + moldmod.SporesShadows.MOD_ID + ".moisture_detector", "Rilevatore di Umidità");
        translationBuilder.add("item." + moldmod.SporesShadows.MOD_ID + ".moisture_detector", "Rilevatore di Umidità");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".info.moisture_detector", "Il Rilevatore di Umidità misura l'umidità effettiva e la saturazione ambientale. Usa Tasto Destro nel vuoto per scansionare l'ambiente. Può essere posizionato su pareti, pavimenti o soffitti.");

        // Dehumidifier
        translationBuilder.add("block." + moldmod.SporesShadows.MOD_ID + ".dehumidifier", "Deumidificatore");
        translationBuilder.add("item." + moldmod.SporesShadows.MOD_ID + ".dehumidifier", "Deumidificatore");
        translationBuilder.add("container." + moldmod.SporesShadows.MOD_ID + ".dehumidifier", "Deumidificatore");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".info.dehumidifier", "Macchinario ambientale attivo che estrae l'umidità dalle stanze chiuse e la condensa in acqua liquida (serbatoio interno da 2000 mB).");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".info.dehumidifier.energy", "Energia e Carburante: Accetta energia esterna RF / FE / TR (buffer fino a 16.000 FE, consumo operativo 10 FE/tick). I combustibili solidi (Carbone, Carbonella, ecc.) inseriti nello slot vengono bruciati immediatamente per generare elettricità interna. L'energia non può essere estratta.");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".info.dehumidifier.water", "Condensazione Acqua: L'umidità estratta si accumula come acqua nel serbatoio interno. Il liquido può SOLO essere estratto (usando un secchio vuoto o tubi per fluidi tramite Fabric Transfer API). L'inserimento di liquidi dall'esterno è rigorosamente bloccato.");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".info.dehumidifier.automation", "Automazione e Controlli: Supporta l'alimentazione automatica con tramogge/hopper. Include un pulsante nella GUI per cambiare la modalità redstone (Ignora, Attivo con segnale, Attivo senza segnale). I comparatori rilevano il livello dell'acqua.");

        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".dehumidifier.water", "Acqua: %d / %d mB");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".dehumidifier.energy", "Energia: %d / %d E");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".dehumidifier.energy_usage", "Consumo: %d E/t (%d E/s)");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".dehumidifier.redstone_mode", "Modalità Redstone: %s");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".dehumidifier.redstone.ignored", "Ignorato");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".dehumidifier.redstone.ignored.desc", "Sempre attivo finché alimentato");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".dehumidifier.redstone.low", "Basso");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".dehumidifier.redstone.low.desc", "Attivo senza segnale; in pausa con redstone");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".dehumidifier.redstone.high", "Alto");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".dehumidifier.redstone.high.desc", "Attivo solo con segnale redstone");
        translationBuilder.add("gui." + moldmod.SporesShadows.MOD_ID + ".dehumidifier.status.running", "● Attivo");
        translationBuilder.add("gui." + moldmod.SporesShadows.MOD_ID + ".dehumidifier.status.full", "● Saturo");
        translationBuilder.add("gui." + moldmod.SporesShadows.MOD_ID + ".dehumidifier.status.off", "● Spento");
        translationBuilder.add("gui." + moldmod.SporesShadows.MOD_ID + ".dehumidifier.status_desc.running", "Il deumidificatore è in funzione ed estrae umidità dalla stanza.");
        translationBuilder.add("gui." + moldmod.SporesShadows.MOD_ID + ".dehumidifier.status_desc.full", "Il serbatoio dell'acqua è saturo (2000 mB). Estrarre l'acqua per riprendere.");
        translationBuilder.add("gui." + moldmod.SporesShadows.MOD_ID + ".dehumidifier.status_desc.off", "Macchinario a riposo (richiede combustibile/energia, stanza valida o segnale redstone).");

        translationBuilder.add("config.jade.plugin_" + moldmod.SporesShadows.MOD_ID + ".dehumidifier_info", "Spores & Shadows: Info Deumidificatore");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.dehumidifier.status", "Stato: ");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.dehumidifier.status.running", "In Funzione");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.dehumidifier.status.full", "Standby (Pieno)");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.dehumidifier.status.off", "Spento");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.dehumidifier.water", "Acqua: %d / %d mB");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.dehumidifier.energy", "Energia: %d E");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.dehumidifier.fuel_min_sec", "Combustibile: %dm %ds");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.dehumidifier.fuel_sec", "Combustibile: %ds");

        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".moisture_detector.title", "Sensore di Umidità");
        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".moisture_detector.description", "Costruisci un Rilevatore di Umidità per monitorare l'umidità della stanza.");
        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".dehumidifier_craft.title", "Controllo del Clima");
        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".dehumidifier_craft.description", "Costruisci un Deumidificatore per asciugare stanze chiuse e raccogliere acqua di condensa.");
        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".dry_oasis.title", "Oasi Sotterranea");
        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".dry_oasis.description", "Asciuga una stanza sotterranea (Y <= 40) sotto il 15% di umidità usando un Deumidificatore.");

        // Jade Tooltips for Detectors
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.spore_detector.air_quality", "Qualità dell'Aria: ");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.spore_detector.clean", "Aria Pulita");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.spore_detector.warning", "Attenzione (Basso)");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.spore_detector.moderate", "Moderato (Fame)");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.spore_detector.lethal", "Letale (Veleno)");

        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.moisture_detector.effective_moisture", "Livello di Umidità: ");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.moisture_detector.dry", "Asciutto");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.moisture_detector.moderate", "Moderato");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.moisture_detector.humid", "Umido");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.moisture_detector.critical", "Critico");

        // Shared Detector Messages
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".detector.blocks_dist", "%d blocchi");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".detector.none", "Nessuna");

        // Spore Detector Chat Messages
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".spore_detector.header", "§6[Rilevatore di Spore] ");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".spore_detector.clean", "§aARIA PULITA §7(Sicuro)");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".spore_detector.warning", "§eATTENZIONE §7(Bassa presenza di spore nell'aria)");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".spore_detector.moderate", "§6RISCHIO MODERATO §7(Fame imminente)");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".spore_detector.lethal", "§4PERICOLO LETALE §7(Veleno e nausea imminenti!)");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".spore_detector.metric", "§7- Densità Spore: §d%s/b");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".spore_detector.open_air", "§7- Ambiente: §aAria Aperta §7| Dist. Ventilazione: §b0 blocchi");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".spore_detector.room", "§7- Volume Stanza: §f%d blocchi §7| Dist. Ventilazione: §b%s");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".spore_detector.aeration", "§7- Areazione Locale: §a%s flusso §7(§b%s§7) | Miasma Stanza: §6%s");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".spore_detector.trend_stable", "§7- Tendenza: §a= STABILE");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".spore_detector.trend_purifying", "§7- Tendenza: §b▼ DEPURAZIONE / DISSIPAZIONE §7(Target: §f%s§7)");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".spore_detector.trend_accumulating", "§7- Tendenza: §c▲ ACCUMULO / SATURAZIONE §7(Target: §f%s§7)");

        // Moisture Detector Chat Messages
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".moisture_detector.header", "§6[Rilevatore di Umidità] ");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".moisture_detector.dry", "§aASCIUTTO §7(Sicuro per strutture in legno)");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".moisture_detector.moderate", "§eMODERATO §7(Attenzione: soglia muffa vicina)");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".moisture_detector.humid", "§6UMIDO §7(Pericolo: alto rischio proliferazione muffa!)");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".moisture_detector.critical", "§4CRITICO §7(Pericolo: rapido marciume e saturazione!)");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".moisture_detector.metric", "§7- Umidità Effettiva: §b%s");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".moisture_detector.open_air", "§7- Ambiente: §aAria Aperta §7| Dist. Ventilazione: §b0 blocchi");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".moisture_detector.room", "§7- Volume Stanza: §f%d blocchi §7| Dist. Ventilazione: §b%s");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".moisture_detector.aeration", "§7- Areazione Locale: §a%s flusso §7(§b%s§7) | Umidità Grezza: §f%s");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".moisture_detector.trend_stable", "§7- Tendenza: §a= STABILE");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".moisture_detector.trend_drying", "§7- Tendenza: §b▼ ASCIUGATURA / VENTILAZIONE §7(Target: §f%s§7)");
        translationBuilder.add("message." + moldmod.SporesShadows.MOD_ID + ".moisture_detector.trend_humidifying", "§7- Tendenza: §c▲ UMIDIFICAZIONE / SATURAZIONE §7(Target: §f%s§7)");

        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".spore_mask_protection.title", "Aria Pura");
        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".spore_mask_protection.description", "Filtra le spore tossiche respirando con una Maschera Antispore in una stanza contaminata.");

        // Enchantments
        translationBuilder.add("enchantment." + moldmod.SporesShadows.MOD_ID + ".spore_filtration", "Filtrazione Spore");
        translationBuilder.add("enchantment." + moldmod.SporesShadows.MOD_ID + ".spore_filtration.desc", "Neutralizza il miasma tossico consumando durabilità dell'elmo.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.enable_spore_mask_protection", "Abilita Protezione Maschera Antispore");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.enable_spore_mask_protection.@Tooltip", "Protegge chi la indossa dal miasma tossico e dagli effetti negativi.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.spore_mask_damage_per_exposure", "Consumo Durabilità Maschera per Esposizione");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.spore_mask_damage_per_exposure.@Tooltip", "Durabilità consumata dalla maschera ad ogni controllo miasma.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.enable_spore_filtration_enchantment", "Abilita Incantesimo Filtrazione Spore");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.enable_spore_filtration_enchantment.@Tooltip", "Incantesimo per elmi che neutralizza le spore tossiche.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.filtration_level_1_durability_cost", "Costo Durabilità Filtrazione Livello I");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.filtration_level_1_durability_cost.@Tooltip", "Costo durabilità dell'elmo per esposizione con Livello I.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.filtration_level_2_durability_cost", "Costo Durabilità Filtrazione Livello II");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.filtration_level_2_durability_cost.@Tooltip", "Costo durabilità dell'elmo per esposizione con Livello II.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.filtration_level_3_save_chance", "Probabilità Risparmio Durabilità Livello III");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.filtration_level_3_save_chance.@Tooltip", "Probabilità di annullare il consumo di durabilità con Livello III.");

        // Tag Translations (Fabric Tag Convention v2)
        translationBuilder.add("tag.item." + moldmod.SporesShadows.MOD_ID + ".moldy_items", "Oggetti Muffosi");
        translationBuilder.add("tag.item." + moldmod.SporesShadows.MOD_ID + ".enchantable.filtration_helmets", "Elmi per Filtrazione");
        translationBuilder.add("tag.item." + moldmod.SporesShadows.MOD_ID + ".moldy_blocks", "Blocchi Muffosi");
        translationBuilder.add("tag.block." + moldmod.SporesShadows.MOD_ID + ".moldy_blocks", "Blocchi Muffosi");
    }
}
