package moldmod.client.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;
import java.util.Map;
import java.util.HashMap;

public class ModItalianLanguageProvider extends FabricLanguageProvider {

    public ModItalianLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, "it_it", registryLookup);
    }

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup registryLookup, TranslationBuilder translationBuilder) {
        String[] woods = moldmod.SporesShadows.WOODS;

        Map<String, String> names = new HashMap<>();
        names.put("oak", "di Quercia");
        names.put("spruce", "di Abete");
        names.put("birch", "di Betulla");
        names.put("jungle", "della Giungla");
        names.put("acacia", "di Acacia");
        names.put("dark_oak", "di Quercia Scura");
        names.put("mangrove", "di Mangrovia");
        names.put("cherry", "di Ciliegio");

        for (String wood : woods) {
            String logName = wood + "_log";
            String woodName = wood + "_wood";
            String prefix = wood;
            
            String wName = names.get(wood);

            String logType = "Tronco";
            
            translationBuilder.add("block.spores--shadows.moldy_" + logName, logType + " " + wName + " ammuffito");
            translationBuilder.add("item.spores--shadows.waxed_" + logName, logType + " " + wName + " cerato");
            translationBuilder.add("item.spores--shadows.tainted_" + logName, logType + " " + wName + " intaccato");
            translationBuilder.add("item.spores--shadows.moldy_" + logName, logType + " " + wName + " ammuffito");
            translationBuilder.add("item.spores--shadows.rotten_" + logName, logType + " " + wName + " marcio");

            translationBuilder.add("block.spores--shadows.moldy_stripped_" + logName, logType + " " + wName + " Scortecciato ammuffito");
            translationBuilder.add("item.spores--shadows.waxed_stripped_" + logName, logType + " " + wName + " Scortecciato cerato");
            translationBuilder.add("item.spores--shadows.tainted_stripped_" + logName, logType + " " + wName + " Scortecciato intaccato");
            translationBuilder.add("item.spores--shadows.moldy_stripped_" + logName, logType + " " + wName + " Scortecciato ammuffito");
            translationBuilder.add("item.spores--shadows.rotten_stripped_" + logName, logType + " " + wName + " Scortecciato marcio");

            if (woodName != null) {
                translationBuilder.add("block.spores--shadows.moldy_" + woodName, "Legno " + wName + " ammuffito");
                translationBuilder.add("item.spores--shadows.waxed_" + woodName, "Legno " + wName + " cerato");
                translationBuilder.add("item.spores--shadows.tainted_" + woodName, "Legno " + wName + " intaccato");
                translationBuilder.add("item.spores--shadows.moldy_" + woodName, "Legno " + wName + " ammuffito");
                translationBuilder.add("item.spores--shadows.rotten_" + woodName, "Legno " + wName + " marcio");

                translationBuilder.add("block.spores--shadows.moldy_stripped_" + woodName, "Legno " + wName + " Scortecciato ammuffito");
                translationBuilder.add("item.spores--shadows.waxed_stripped_" + woodName, "Legno " + wName + " Scortecciato cerato");
                translationBuilder.add("item.spores--shadows.tainted_stripped_" + woodName, "Legno " + wName + " Scortecciato intaccato");
                translationBuilder.add("item.spores--shadows.moldy_stripped_" + woodName, "Legno " + wName + " Scortecciato ammuffito");
                translationBuilder.add("item.spores--shadows.rotten_stripped_" + woodName, "Legno " + wName + " Scortecciato marcio");
            }

            translationBuilder.add("block.spores--shadows.moldy_" + prefix + "_planks", "Assi " + wName + " ammuffite");
            translationBuilder.add("item.spores--shadows.waxed_" + prefix + "_planks", "Assi " + wName + " cerate");
            translationBuilder.add("item.spores--shadows.tainted_" + prefix + "_planks", "Assi " + wName + " intaccate");
            translationBuilder.add("item.spores--shadows.moldy_" + prefix + "_planks", "Assi " + wName + " ammuffite");
            translationBuilder.add("item.spores--shadows.rotten_" + prefix + "_planks", "Assi " + wName + " marce");
            
            String[] blocks = {"slab", "stairs", "fence", "fence_gate", "door", "trapdoor", "pressure_plate", "button"};
            String[] blockNames = {"Lastra", "Scale", "Staccionata", "Cancelletto", "Porta", "Botola", "Pedana a Pressione", "Pulsante"};
            String[] femC = {"cerata", "cerate", "cerata", "cerato", "cerata", "cerata", "cerata", "cerato"};
            String[] fem = {"ammuffita", "ammuffite", "ammuffita", "ammuffito", "ammuffita", "ammuffita", "ammuffita", "ammuffito"};
            String[] femT = {"intaccata", "intaccate", "intaccata", "intaccato", "intaccata", "intaccata", "intaccata", "intaccato"};
            String[] femR = {"marcia", "marce", "marcia", "marcio", "marcia", "marcia", "marcia", "marcio"};
            
            for (int i = 0; i < blocks.length; i++) {
                String blockKey = blocks[i];
                String bName = blockNames[i];
                
                translationBuilder.add("block.spores--shadows.moldy_" + prefix + "_" + blockKey, bName + " " + wName + " " + fem[i]);
                translationBuilder.add("item.spores--shadows.waxed_" + prefix + "_" + blockKey, bName + " " + wName + " " + femC[i]);
                translationBuilder.add("item.spores--shadows.tainted_" + prefix + "_" + blockKey, bName + " " + wName + " " + femT[i]);
                translationBuilder.add("item.spores--shadows.moldy_" + prefix + "_" + blockKey, bName + " " + wName + " " + fem[i]);
                translationBuilder.add("item.spores--shadows.rotten_" + prefix + "_" + blockKey, bName + " " + wName + " " + femR[i]);
            }
        }

        translationBuilder.add("tooltip.spores--shadows.waxed", "cerato");
        translationBuilder.add("item.spores--shadows.waxed_format", "%s cerato");
        translationBuilder.add("tooltip.spores--shadows.moldy_log_desc_1", "Può essere trasformato in assi pulite perdendo materiale,");
        translationBuilder.add("tooltip.spores--shadows.moldy_log_desc_2", "ma non è utilizzabile per le ricette vanilla normali.");
        translationBuilder.add("tooltip.spores--shadows.moldy_planks_desc_1", "Utile solo per crafting semplici (bastoni, staccionate, ecc).");
        translationBuilder.add("tooltip.spores--shadows.moldy_planks_desc_2", "Non può essere usato in ricette complesse a piena efficienza.");
        translationBuilder.add("tooltip.spores--shadows.moldy_general_desc_1", "Componente di legno degradato.");
        translationBuilder.add("tooltip.spores--shadows.moldy_general_desc_2", "Strutturalmente indebolito dalla muffa.");

        translationBuilder.add("text.autoconfig.spores--shadows.title", "Configurazione Spores & Shadows");
        translationBuilder.add("text.autoconfig.spores--shadows.category.general", "Generale");
        translationBuilder.add("text.autoconfig.spores--shadows.category.environment", "Ambiente");
        translationBuilder.add("text.autoconfig.spores--shadows.category.susceptibility", "Suscettibilità");
        translationBuilder.add("text.autoconfig.spores--shadows.category.catalysts", "Catalizzatori");
        translationBuilder.add("text.autoconfig.spores--shadows.category.drops", "Lascito (Drop)");
        translationBuilder.add("text.autoconfig.spores--shadows.category.structures", "Strutture");
        translationBuilder.add("text.autoconfig.spores--shadows.category.furnace_multipliers", "Efficienza Fornace");

        translationBuilder.add("text.autoconfig.spores--shadows.option.general", "Generale");
        translationBuilder.add("text.autoconfig.spores--shadows.option.susceptibility", "Suscettibilità");
        translationBuilder.add("text.autoconfig.spores--shadows.option.catalysts", "Catalizzatori");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment", "Ambiente");
        translationBuilder.add("text.autoconfig.spores--shadows.option.drops", "Lascito (Drop)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures", "Strutture");
        translationBuilder.add("text.autoconfig.spores--shadows.option.furnaceMultipliers", "Efficienza Fornace");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.cave_temperature.@Tooltip", "Temperatura per ignorare il sole nelle caverne.");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.high_altitude_freezing_temperature.@Tooltip", "Temperatura di congelamento che blocca la muffa in alta quota.");
        translationBuilder.add("text.autoconfig.spores--shadows.option.general.enable_mold_growth", "Abilita Crescita Muffa");
        translationBuilder.add("text.autoconfig.spores--shadows.option.general.infection_threshold", "Soglia Infezione (R > X)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.general.scan_radius", "Raggio Scansione");
        translationBuilder.add("text.autoconfig.spores--shadows.option.general.scan_radius.@Tooltip", "1 = 3x3x3, 2 = 5x5x5. Valori alti causano lag.");
        translationBuilder.add("text.autoconfig.spores--shadows.option.general.structures_immune", "Strutture Generate Immuni");
        translationBuilder.add("text.autoconfig.spores--shadows.option.general.structures_immune.@Tooltip", "Se abilitato, relitti e villaggi non marciranno da soli prima che il giocatore li tocchi.");
        translationBuilder.add("text.autoconfig.spores--shadows.option.general.show_debug_in_chat", "Mostra Matematica Debug in Chat");

        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.rain_humidity_base", "Umidità Base (Pioggia/Neve)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.dry_humidity_base", "Umidità Base (Sole/Secco)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.water_adjacent_bonus", "Bonus Adiacenza Acqua");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.cauldron_adjacent_bonus", "Bonus Adiacenza Calderoni/Fango");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.max_local_humidity_bonus", "Bonus Umidità Locale Max");
        
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.cave_start_y", "Inizio Caverne (Livello Y)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.cave_full_y", "Profondità Piena Caverne (Livello Y)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.cave_temperature", "Temperatura Caverne");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.depth_modifier_per_level", "Modificatore Umidità Profondità (+ per blocco)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.max_depth_modifier", "Malus Umidità Profondità Massimo");
        
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.high_altitude_start_y", "Inizio Alta Quota (Livello Y)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.high_altitude_full_y", "Picco Alta Quota (Livello Y)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.high_altitude_freezing_temperature", "Temperatura Congelamento Alta Quota");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.min_temperature_survival", "Temp Sopravvivenza Minima");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.max_temperature_survival", "Temp Sopravvivenza Massima");

        translationBuilder.add("text.autoconfig.spores--shadows.option.susceptibility.stripped_wood_multiplier", "Suscettibilità Legno Scortecciato");
        translationBuilder.add("text.autoconfig.spores--shadows.option.susceptibility.planks_multiplier", "Suscettibilità Assi di Legno");
        translationBuilder.add("text.autoconfig.spores--shadows.option.susceptibility.default_multiplier", "Suscettibilità Legno Default");
        
        translationBuilder.add("text.autoconfig.spores--shadows.option.catalysts.stage_2_drop_chance", "Stage 2 Drop Chance (Deprecated)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.catalysts.stage_3_drop_chance", "Stage 3 Drop Chance (Deprecated)");

        translationBuilder.add("text.autoconfig.spores--shadows.option.catalysts.mud_bonus", "Malus Fango (Mud)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.catalysts.fungi_bonus", "Malus Adiacenza Funghi");
        translationBuilder.add("text.autoconfig.spores--shadows.option.catalysts.spore_blossom_bonus", "Malus Fiore di Spore (Spore Blossom)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.catalysts.podzol_mycelium_bonus", "Malus Podzol/Micelio");
        translationBuilder.add("text.autoconfig.spores--shadows.option.catalysts.tainted_block_bonus", "Malus Adiacenza Blocco Intaccato");
        translationBuilder.add("text.autoconfig.spores--shadows.option.catalysts.moldy_block_bonus", "Malus Adiacenza Blocco Ammuffito");
        translationBuilder.add("text.autoconfig.spores--shadows.option.catalysts.rotten_block_bonus", "Malus Adiacenza Blocco Marcio");

        translationBuilder.add("text.autoconfig.spores--shadows.option.drops.stage_2_drop_chance", "Prob. Drop Blocco Ammuffito (0.0 a 1.0)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.drops.stage_2_drop_chance.@Tooltip", "Probabilità che un blocco Ammuffito droppi se stesso se rotto.");
        translationBuilder.add("text.autoconfig.spores--shadows.option.drops.stage_3_drop_chance", "Prob. Drop Blocco Marcio (0.0 a 1.0)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.drops.stage_3_drop_chance.@Tooltip", "Probabilità che un blocco Marcio droppi se stesso se rotto.");

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

        translationBuilder.add("text.autoconfig.spores--shadows.option.furnaceMultipliers.stage_0", "Moltiplicatore Cottura Vanilla (Sano)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.furnaceMultipliers.stage_1", "Moltiplicatore Cottura Intaccato");
        translationBuilder.add("text.autoconfig.spores--shadows.option.furnaceMultipliers.stage_2", "Moltiplicatore Cottura Ammuffito");
        translationBuilder.add("text.autoconfig.spores--shadows.option.furnaceMultipliers.stage_3", "Moltiplicatore Cottura Marcio");
    }
}
