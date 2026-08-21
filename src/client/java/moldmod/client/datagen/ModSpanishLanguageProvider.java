package moldmod.client.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;
import java.util.Map;
import java.util.HashMap;

public class ModSpanishLanguageProvider extends FabricLanguageProvider {

    public ModSpanishLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, "es_es", registryLookup);
    }

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup registryLookup, TranslationBuilder translationBuilder) {
        String[] woods = moldmod.SporesShadows.WOODS;

        Map<String, String> names = new HashMap<>();
        names.put("oak", "de Roble");
        names.put("spruce", "de Abeto");
        names.put("birch", "de Abedul");
        names.put("jungle", "de Jungla");
        names.put("acacia", "de Acacia");
        names.put("dark_oak", "de Roble Oscuro");
        names.put("mangrove", "de Manglar");
        names.put("cherry", "de Cerezo");

        for (String wood : woods) {
            String logName = wood + "_log";
            String woodName = wood + "_wood";
            String prefix = wood;
            
            String wName = names.get(wood);

            String logType = "Tronco";
            
            translationBuilder.add("block.spores--shadows.moldy_" + logName, logType + " " + wName + " mohoso");
            translationBuilder.add("item.spores--shadows.waxed_" + logName, logType + " " + wName + " encerado");
            translationBuilder.add("item.spores--shadows.tainted_" + logName, logType + " " + wName + " manchado");
            translationBuilder.add("item.spores--shadows.moldy_" + logName, logType + " " + wName + " mohoso");
            translationBuilder.add("item.spores--shadows.rotten_" + logName, logType + " " + wName + " podrido");

            translationBuilder.add("block.spores--shadows.moldy_stripped_" + logName, logType + " sin corteza " + wName + " mohoso");
            translationBuilder.add("item.spores--shadows.waxed_stripped_" + logName, logType + " sin corteza " + wName + " encerado");
            translationBuilder.add("item.spores--shadows.tainted_stripped_" + logName, logType + " sin corteza " + wName + " manchado");
            translationBuilder.add("item.spores--shadows.moldy_stripped_" + logName, logType + " sin corteza " + wName + " mohoso");
            translationBuilder.add("item.spores--shadows.rotten_stripped_" + logName, logType + " sin corteza " + wName + " podrido");

            if (woodName != null) {
                translationBuilder.add("block.spores--shadows.moldy_" + woodName, "Madera " + wName + " mohosa");
                translationBuilder.add("item.spores--shadows.waxed_" + woodName, "Madera " + wName + " encerada");
                translationBuilder.add("item.spores--shadows.tainted_" + woodName, "Madera " + wName + " manchada");
                translationBuilder.add("item.spores--shadows.moldy_" + woodName, "Madera " + wName + " mohosa");
                translationBuilder.add("item.spores--shadows.rotten_" + woodName, "Madera " + wName + " podrida");

                translationBuilder.add("block.spores--shadows.moldy_stripped_" + woodName, "Madera sin corteza " + wName + " mohosa");
                translationBuilder.add("item.spores--shadows.waxed_stripped_" + woodName, "Madera sin corteza " + wName + " encerada");
                translationBuilder.add("item.spores--shadows.tainted_stripped_" + woodName, "Madera sin corteza " + wName + " manchada");
                translationBuilder.add("item.spores--shadows.moldy_stripped_" + woodName, "Madera sin corteza " + wName + " mohosa");
                translationBuilder.add("item.spores--shadows.rotten_stripped_" + woodName, "Madera sin corteza " + wName + " podrida");
            }

            translationBuilder.add("block.spores--shadows.moldy_" + prefix + "_planks", "Tablones " + wName + " mohosos");
            translationBuilder.add("item.spores--shadows.waxed_" + prefix + "_planks", "Tablones " + wName + " encerados");
            translationBuilder.add("item.spores--shadows.tainted_" + prefix + "_planks", "Tablones " + wName + " manchados");
            translationBuilder.add("item.spores--shadows.moldy_" + prefix + "_planks", "Tablones " + wName + " mohosos");
            translationBuilder.add("item.spores--shadows.rotten_" + prefix + "_planks", "Tablones " + wName + " podridos");
            
            String[] blocks = {"slab", "stairs", "fence", "fence_gate", "door", "trapdoor", "pressure_plate", "button"};
            String[] blockNames = {"Losa", "Escaleras", "Valla", "Puerta de valla", "Puerta", "Trampilla", "Placa de presión", "Botón"};
            
            String[] femC = {"encerada", "enceradas", "encerada", "encerada", "encerada", "encerada", "encerada", "encerado"};
            String[] fem = {"mohosa", "mohosas", "mohosa", "mohosa", "mohosa", "mohosa", "mohosa", "mohoso"};
            String[] femT = {"manchada", "manchadas", "manchada", "manchada", "manchada", "manchada", "manchada", "manchado"};
            String[] femR = {"podrida", "podridas", "podrida", "podrida", "podrida", "podrida", "podrida", "podrido"};
            
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

        translationBuilder.add("tooltip.spores--shadows.waxed", "Encerado");
        translationBuilder.add("item.spores--shadows.waxed_format", "%s encerado");
        translationBuilder.add("tooltip.spores--shadows.moldy_log_desc_1", "Se puede transformar en tablones limpios perdiendo material,");
        translationBuilder.add("tooltip.spores--shadows.moldy_log_desc_2", "pero no se puede usar para recetas vanilla normales.");
        translationBuilder.add("tooltip.spores--shadows.moldy_planks_desc_1", "Solo útil para crafteos simples (palos, vallas, etc).");
        translationBuilder.add("tooltip.spores--shadows.moldy_planks_desc_2", "No se puede usar en recetas complejas a máxima eficiencia.");
        translationBuilder.add("tooltip.spores--shadows.moldy_general_desc_1", "Componente de madera degradado.");
        translationBuilder.add("tooltip.spores--shadows.moldy_general_desc_2", "Estructuralmente debilitado por el moho.");

        translationBuilder.add("text.autoconfig.spores--shadows.title", "Configuración de Spores & Shadows");
        translationBuilder.add("text.autoconfig.spores--shadows.category.general", "General");
        translationBuilder.add("text.autoconfig.spores--shadows.category.environment", "Entorno");
        translationBuilder.add("text.autoconfig.spores--shadows.category.susceptibility", "Susceptibilidad");
        translationBuilder.add("text.autoconfig.spores--shadows.category.catalysts", "Catalizadores");
        translationBuilder.add("text.autoconfig.spores--shadows.category.drops", "Botín (Drops)");
        translationBuilder.add("text.autoconfig.spores--shadows.category.structures", "Estructuras");
        translationBuilder.add("text.autoconfig.spores--shadows.category.furnace_multipliers", "Eficiencia de Horno");

        translationBuilder.add("text.autoconfig.spores--shadows.option.general", "General");
        translationBuilder.add("text.autoconfig.spores--shadows.option.susceptibility", "Susceptibilidad");
        translationBuilder.add("text.autoconfig.spores--shadows.option.catalysts", "Catalizadores");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment", "Entorno");
        translationBuilder.add("text.autoconfig.spores--shadows.option.drops", "Botín (Drops)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures", "Estructuras");
        translationBuilder.add("text.autoconfig.spores--shadows.option.furnaceMultipliers", "Eficiencia de Horno");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.cave_temperature.@Tooltip", "Temperatura para ignorar el sol en cuevas.");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.high_altitude_freezing_temperature.@Tooltip", "Temperatura de congelación que detiene el moho a gran altitud.");
        translationBuilder.add("text.autoconfig.spores--shadows.option.general.enable_mold_growth", "Habilitar Crecimiento de Moho");
        translationBuilder.add("text.autoconfig.spores--shadows.option.general.infection_threshold", "Umbral de Infección (R > X)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.general.scan_radius", "Radio de Escaneo");
        translationBuilder.add("text.autoconfig.spores--shadows.option.general.scan_radius.@Tooltip", "1 = 3x3x3, 2 = 5x5x5. Valores altos causan lag.");
        translationBuilder.add("text.autoconfig.spores--shadows.option.general.structures_immune", "Estructuras Generadas Inmunes");
        translationBuilder.add("text.autoconfig.spores--shadows.option.general.structures_immune.@Tooltip", "Si está activado, naufragios y aldeas no se pudrirán solos antes de que el jugador los toque.");
        translationBuilder.add("text.autoconfig.spores--shadows.option.general.show_debug_in_chat", "Mostrar Matemáticas de Depuración en Chat");

        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.rain_humidity_base", "Humedad Base (Lluvia/Nieve)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.dry_humidity_base", "Humedad Base (Sol/Seco)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.water_adjacent_bonus", "Bono de Adyacencia al Agua");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.cauldron_adjacent_bonus", "Bono de Adyacencia a Calderos/Barro");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.max_local_humidity_bonus", "Bono Máximo de Humedad Local");
        
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.cave_start_y", "Inicio de Cuevas (Nivel Y)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.cave_full_y", "Profundidad Total de Cuevas (Nivel Y)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.cave_temperature", "Temperatura de Cuevas");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.depth_modifier_per_level", "Modificador de Humedad por Profundidad (+ por bloque)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.max_depth_modifier", "Malus Máximo de Humedad por Profundidad");
        
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.high_altitude_start_y", "Inicio de Gran Altitud (Nivel Y)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.high_altitude_full_y", "Pico de Gran Altitud (Nivel Y)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.high_altitude_freezing_temperature", "Temperatura de Congelación a Gran Altitud");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.min_temperature_survival", "Temp Mínima de Supervivencia");
        translationBuilder.add("text.autoconfig.spores--shadows.option.environment.max_temperature_survival", "Temp Máxima de Supervivencia");

        translationBuilder.add("text.autoconfig.spores--shadows.option.susceptibility.stripped_wood_multiplier", "Susceptibilidad de Madera sin Corteza");
        translationBuilder.add("text.autoconfig.spores--shadows.option.susceptibility.planks_multiplier", "Susceptibilidad de Tablones de Madera");
        translationBuilder.add("text.autoconfig.spores--shadows.option.susceptibility.default_multiplier", "Susceptibilidad de Madera por Defecto");
        
        translationBuilder.add("text.autoconfig.spores--shadows.option.catalysts.stage_2_drop_chance", "Probabilidad Drop Stage 2 (Obsoleto)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.catalysts.stage_3_drop_chance", "Probabilidad Drop Stage 3 (Obsoleto)");

        translationBuilder.add("text.autoconfig.spores--shadows.option.catalysts.mud_bonus", "Malus de Barro (Mud)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.catalysts.fungi_bonus", "Malus de Adyacencia a Hongos");
        translationBuilder.add("text.autoconfig.spores--shadows.option.catalysts.spore_blossom_bonus", "Malus de Flor de Esporas (Spore Blossom)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.catalysts.podzol_mycelium_bonus", "Malus de Podzol/Micelio");
        translationBuilder.add("text.autoconfig.spores--shadows.option.catalysts.tainted_block_bonus", "Malus de Adyacencia a Bloque Manchado");
        translationBuilder.add("text.autoconfig.spores--shadows.option.catalysts.moldy_block_bonus", "Malus de Adyacencia a Bloque Mohoso");
        translationBuilder.add("text.autoconfig.spores--shadows.option.catalysts.rotten_block_bonus", "Malus de Adyacencia a Bloque Podrido");

        translationBuilder.add("text.autoconfig.spores--shadows.option.drops.stage_2_drop_chance", "Prob. Drop Bloque Mohoso (0.0 a 1.0)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.drops.stage_2_drop_chance.@Tooltip", "Probabilidad de que un bloque Mohoso se suelte a sí mismo al romperse.");
        translationBuilder.add("text.autoconfig.spores--shadows.option.drops.stage_3_drop_chance", "Prob. Drop Bloque Podrido (0.0 a 1.0)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.drops.stage_3_drop_chance.@Tooltip", "Probabilidad de que un bloque Podrido se suelte a sí mismo al romperse.");

        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat1_critical", "Categoría 1 (Degradación Crítica)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat2_high", "Categoría 2 (Degradación Alta)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat3_moderate", "Categoría 3 (Degradación Moderada)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat4_low", "Categoría 4 (Degradación Baja)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat1_critical.moldy_chance", "% Mohoso");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat2_high.moldy_chance", "% Mohoso");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat3_moderate.moldy_chance", "% Mohoso");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat4_low.moldy_chance", "% Mohoso");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat1_critical.tainted_chance", "% Manchado");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat2_high.tainted_chance", "% Manchado");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat3_moderate.tainted_chance", "% Manchado");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat4_low.tainted_chance", "% Manchado");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat1_critical.rotten_chance", "% Podrido");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat2_high.rotten_chance", "% Podrido");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat3_moderate.rotten_chance", "% Podrido");
        translationBuilder.add("text.autoconfig.spores--shadows.option.structures.cat4_low.rotten_chance", "% Podrido");

        translationBuilder.add("text.autoconfig.spores--shadows.option.furnaceMultipliers.stage_0", "Multiplicador Horno Vanilla (Sano)");
        translationBuilder.add("text.autoconfig.spores--shadows.option.furnaceMultipliers.stage_1", "Multiplicador Horno Manchado");
        translationBuilder.add("text.autoconfig.spores--shadows.option.furnaceMultipliers.stage_2", "Multiplicador Horno Mohoso");
        translationBuilder.add("text.autoconfig.spores--shadows.option.furnaceMultipliers.stage_3", "Multiplicador Horno Podrido");
    }
}
