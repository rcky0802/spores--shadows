package moldmod.client.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.registry.RegistryWrapper;

import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class ModItalianLanguageProvider extends AbstractModLanguageProvider {

    public ModItalianLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, "it_it", registryLookup);
    }

    @Override
    protected String getTranslation(String wood, String blockType, String state) {
        Map<String, String> names = new HashMap<>();
        names.put("oak", "di Quercia");
        names.put("spruce", "di Abete");
        names.put("birch", "di Betulla");
        names.put("jungle", "della Giungla");
        names.put("acacia", "di Acacia");
        names.put("dark_oak", "di Quercia Scura");
        names.put("mangrove", "di Mangrovia");
        names.put("cherry", "di Ciliegio");

        String wName = names.get(wood);
        String blockName = "";
        boolean isFeminine = false;
        boolean isPlural = false;

        switch (blockType) {
            case "log": blockName = "Tronco"; break;
            case "stripped_log": blockName = "Tronco Sortecciato"; break;
            case "wood": blockName = "Legno"; break;
            case "stripped_wood": blockName = "Legno Sortecciato"; break;
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
        else if (state.equals("rotten")) stateStr = isFeminine ? (isPlural ? "Marcie" : "Marcia") : (isPlural ? "Marci" : "Marcio");

        return blockName + " " + wName + " " + stateStr;
    }

    @Override
    protected void generateTooltipsAndConfig(TranslationBuilder translationBuilder) {
        translationBuilder.add("tooltip.spores--shadows.waxed", "Cerato");
        translationBuilder.add("item.spores--shadows.waxed_format", "%s Cerato");
        translationBuilder.add("tooltip.spores--shadows.moldy_log_desc_1", "Può essere trasformato in assi pulite con perdita di materiale,");
        translationBuilder.add("tooltip.spores--shadows.moldy_log_desc_2", "ma non può essere usato per normali ricette vanilla.");
        translationBuilder.add("tooltip.spores--shadows.moldy_planks_desc_1", "Utile solo per crafting semplici (bastoni, staccionate).");
        translationBuilder.add("tooltip.spores--shadows.moldy_planks_desc_2", "Non può essere usato efficacemente in ricette complesse.");
        translationBuilder.add("tooltip.spores--shadows.moldy_general_desc_1", "Componente di legno degradato.");
        translationBuilder.add("tooltip.spores--shadows.moldy_general_desc_2", "Strutturalmente indebolito dalla muffa.");
        translationBuilder.add("tooltip.spores--shadows.moldy_redstone_desc_1", "La muffa ha compromesso il meccanismo.");
        translationBuilder.add("tooltip.spores--shadows.moldy_redstone_desc_2", "Il tempo di attivazione è notevolmente aumentato.");

        translationBuilder.add("text.autoconfig.spores--shadows.title", "Configurazione Spores & Shadows");
        translationBuilder.add("text.autoconfig.spores--shadows.category.general", "Generale");
        translationBuilder.add("text.autoconfig.spores--shadows.category.environment", "Ambiente");
        translationBuilder.add("text.autoconfig.spores--shadows.category.susceptibility", "Suscettibilità");
        translationBuilder.add("text.autoconfig.spores--shadows.category.catalysts", "Catalizzatori");
        translationBuilder.add("text.autoconfig.spores--shadows.category.drops", "Drop");
        translationBuilder.add("text.autoconfig.spores--shadows.category.structures", "Strutture");
        translationBuilder.add("text.autoconfig.spores--shadows.category.furnace_multipliers", "Efficienza Fornace");

        translationBuilder.add("text.autoconfig.spores--shadows.option.general", "Generale");
        translationBuilder.add("text.autoconfig.spores--shadows.option.susceptibility", "Suscettibilità");
        translationBuilder.add("text.autoconfig.spores--shadows.option.catalysts", "Catalizzatori");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment", "Ambiente");
        translationBuilder.add("text.autoconfig.spores--shadows.option.drops", "Drop");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures", "Strutture");
        translationBuilder.add("text.autoconfig.spores--shadows.option.furnaceMultipliers", "Efficienza Fornace");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.cave_temperature.@Tooltip", "Temperatura per ignorare il sole nelle caverne.");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.high_altitude_freezing_temperature.@Tooltip", "Temperatura di congelamento che ferma la muffa ad alta quota.");
        translationBuilder.add("text.autoconfig.spores--shadows.option.general.enable_mold_growth", "Abilita Crescita Muffa");
        translationBuilder.add("text.autoconfig.spores--shadows.option.general.infection_threshold", "Soglia di Infezione (R > X)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.general.scan_radius", "Raggio di Scansione");
        translationBuilder.add("text.autoconfig.spores--shadows.option.general.scan_radius.@Tooltip", "1 = 3x3x3 blocchi, 2 = 5x5x5 blocchi. Valori alti causano lag.");
        translationBuilder.add("text.autoconfig.spores--shadows.option.general.structures_immune", "Strutture Generate sono Immuni");
        translationBuilder.add("text.autoconfig.spores--shadows.option.general.structures_immune.@Tooltip", "Se attivato, relitti e villaggi non marciranno da soli prima dell'interazione del giocatore.");
        translationBuilder.add("text.autoconfig.spores--shadows.option.general.show_debug_in_chat", "Mostra Logica Debug in Chat");

        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.rain_humidity_base", "Umidità Base (Pioggia/Neve)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.dry_humidity_base", "Umidità Base (Sereno/Secco)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.water_adjacent_bonus", "Bonus Vicinanza Acqua");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.cauldron_adjacent_bonus", "Bonus Vicinanza Calderoni/Fango");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.max_local_humidity_bonus", "Bonus Max Umidità Locale");
        
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.cave_start_y", "Inizio Caverne (Livello Y)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.cave_full_y", "Profondità Totale Caverne (Livello Y)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.cave_temperature", "Temperatura Caverne");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.depth_modifier_per_level", "Modificatore Umidità per Profondità (+ per blocco)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.max_depth_modifier", "Malus Max Umidità Profondità");
        
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.high_altitude_start_y", "Inizio Alta Quota (Livello Y)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.high_altitude_full_y", "Picco Alta Quota (Livello Y)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.high_altitude_freezing_temperature", "Temperatura Congelamento Alta Quota");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.min_temperature_survival", "Temp Minima Sopravvivenza");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.max_temperature_survival", "Temp Massima Sopravvivenza");

        translationBuilder.add("text.autoconfig.spores--shadows.option.susceptibility.stripped_wood_multiplier", "Suscettibilità Legno Sortecciato");
        translationBuilder.add("text.autoconfig.spores--shadows.option.susceptibility.planks_multiplier", "Suscettibilità Assi di Legno");
        translationBuilder.add("text.autoconfig.spores--shadows.option.susceptibility.default_multiplier", "Suscettibilità Legno Predefinita");
        
        translationBuilder.add("text.autoconfig.spores--shadows.option.catalysts.stage_2_drop_chance", "Stage 2 Drop Chance (Deprecato)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.catalysts.stage_3_drop_chance", "Stage 3 Drop Chance (Deprecato)");

        translationBuilder.add("text.autoconfig.spores--shadows.option.catalysts.mud_bonus", "Malus Fango (Mud)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.catalysts.fungi_bonus", "Malus Vicinanza Funghi");
        translationBuilder.add("text.autoconfig.spores--shadows.option.catalysts.spore_blossom_bonus", "Malus Fiore di Spora (Spore Blossom)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.catalysts.podzol_mycelium_bonus", "Malus Podzol/Micelio");
        translationBuilder.add("text.autoconfig.spores--shadows.option.catalysts.tainted_block_bonus", "Malus Vicinanza Blocco Intaccato");
        translationBuilder.add("text.autoconfig.spores--shadows.option.catalysts.moldy_block_bonus", "Malus Vicinanza Blocco Ammuffito");
        translationBuilder.add("text.autoconfig.spores--shadows.option.catalysts.rotten_block_bonus", "Malus Vicinanza Blocco Marcio");

        translationBuilder.add("text.autoconfig.spores--shadows.option.drops.stage_2_drop_chance", "Prob. Drop Blocco Ammuffito (0.0 a 1.0)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.drops.stage_2_drop_chance.@Tooltip", "Probabilità che un blocco Ammuffito droppi sé stesso quando rotto.");
        translationBuilder.add("text.autoconfig.spores--shadows.option.drops.stage_3_drop_chance", "Prob. Drop Blocco Marcio (0.0 a 1.0)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.drops.stage_3_drop_chance.@Tooltip", "Probabilità che un blocco Marcio droppi sé stesso quando rotto.");

        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat1_critical", "Categoria 1 (Degrado Critico)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat2_high", "Categoria 2 (Degrado Alto)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat3_moderate", "Categoria 3 (Degrado Moderato)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat4_low", "Categoria 4 (Degrado Basso)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat1_critical.moldy_chance", "% Ammuffito");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat2_high.moldy_chance", "% Ammuffito");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat3_moderate.moldy_chance", "% Ammuffito");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat4_low.moldy_chance", "% Ammuffito");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat1_critical.tainted_chance", "% Intaccato");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat2_high.tainted_chance", "% Intaccato");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat3_moderate.tainted_chance", "% Intaccato");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat4_low.tainted_chance", "% Intaccato");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat1_critical.rotten_chance", "% Marcio");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat2_high.rotten_chance", "% Marcio");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat3_moderate.rotten_chance", "% Marcio");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat4_low.rotten_chance", "% Marcio");

        translationBuilder.add("text.autoconfig.spores--shadows.option.furnaceMultipliers.stage_0", "Efficienza Fornace (Sano)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.furnaceMultipliers.stage_1", "Efficienza Fornace (Contagiato)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.furnaceMultipliers.stage_2", "Efficienza Fornace (Muffito)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.furnaceMultipliers.stage_3", "Efficienza Fornace (Marcio)");

        translationBuilder.add("text.autoconfig.spores--shadows.category.toxicity", "Tossicità");
        translationBuilder.add("text.autoconfig.spores--shadows.option.toxicity", "Tossicità");
        translationBuilder.add("text.autoconfig.spores--shadows.option.toxicity.check_interval_ticks", "Intervallo Controlli (Ticks)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.toxicity.scan_radius", "Raggio Nube Tossica");
        translationBuilder.add("text.autoconfig.spores--shadows.option.toxicity.threshold_nausea", "Soglia Nausea");
        translationBuilder.add("text.autoconfig.spores--shadows.option.toxicity.threshold_poison", "Soglia Veleno");
        translationBuilder.add("text.autoconfig.spores--shadows.option.toxicity.duration_nausea_ticks", "Durata Nausea (Ticks)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.toxicity.duration_poison_ticks", "Durata Veleno (Ticks)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.water_scan_radius", "Raggio Scansione Acqua");
    }
}
