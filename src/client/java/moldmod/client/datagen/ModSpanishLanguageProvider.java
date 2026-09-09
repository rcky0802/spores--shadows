package moldmod.client.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.registry.RegistryWrapper;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ModSpanishLanguageProvider extends AbstractModLanguageProvider {

    private static final Map<String, String> WOOD_NAMES = Map.ofEntries(
            Map.entry("oak", "de Roble"),
            Map.entry("spruce", "de Abeto"),
            Map.entry("birch", "de Abedul"),
            Map.entry("jungle", "de Jungla"),
            Map.entry("acacia", "de Acacia"),
            Map.entry("dark_oak", "de Roble Oscuro"),
            Map.entry("mangrove", "de Manglar"),
            Map.entry("cherry", "de Cerezo"),
            Map.entry("crimson", "Carmesí"),
            Map.entry("warped", "Deformado")
    );

    public ModSpanishLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, "es_es", registryLookup);
    }

    @Override
    protected String getTranslation(String wood, String blockType, String state) {
        String wName = WOOD_NAMES.get(wood);
        String blockName = "";
        boolean isFeminine = false;
        boolean isPlural = false;

        switch (blockType) {
            case "log": blockName = "Tronco"; break;
            case "stripped_log": blockName = "Tronco sin corteza"; break;
            case "wood": blockName = "Madera"; isFeminine = true; break;
            case "stripped_wood": blockName = "Madera sin corteza"; isFeminine = true; break;
            case "stem": blockName = "Tallo"; break;
            case "stripped_stem": blockName = "Tallo sin corteza"; break;
            case "hyphae": blockName = "Hifas"; isFeminine = true; isPlural = true; break;
            case "stripped_hyphae": blockName = "Hifas sin corteza"; isFeminine = true; isPlural = true; break;
            case "planks": blockName = "Tablones"; isPlural = true; break;
            case "slab": blockName = "Losa"; isFeminine = true; break;
            case "stairs": blockName = "Escaleras"; isFeminine = true; isPlural = true; break;
            case "fence": blockName = "Valla"; isFeminine = true; break;
            case "fence_gate": blockName = "Puerta de valla"; isFeminine = true; break;
            case "door": blockName = "Puerta"; isFeminine = true; break;
            case "trapdoor": blockName = "Trampilla"; isFeminine = true; break;
            case "pressure_plate": blockName = "Placa de presión"; isFeminine = true; break;
            case "button": blockName = "Botón"; break;
        }

        String stateStr = "";
        if (state.equals("moldy")) stateStr = isFeminine ? (isPlural ? "mohosas" : "mohosa") : (isPlural ? "mohosos" : "mohoso");
        else if (state.equals("waxed")) stateStr = isFeminine ? (isPlural ? "enceradas" : "encerada") : (isPlural ? "encerados" : "encerado");
        else if (state.equals("tainted")) stateStr = isFeminine ? (isPlural ? "manchadas" : "manchada") : (isPlural ? "manchados" : "manchado");
        else if (state.equals("rotten")) stateStr = isFeminine ? (isPlural ? "podridas" : "podrida") : (isPlural ? "podridos" : "podrido");
        else if (state.equals("waxed_tainted")) stateStr = (isFeminine ? (isPlural ? "manchadas" : "manchada") : (isPlural ? "manchados" : "manchado")) + " " + (isFeminine ? (isPlural ? "enceradas" : "encerada") : (isPlural ? "encerados" : "encerado"));
        else if (state.equals("waxed_moldy")) stateStr = (isFeminine ? (isPlural ? "mohosas" : "mohosa") : (isPlural ? "mohosos" : "mohoso")) + " " + (isFeminine ? (isPlural ? "enceradas" : "encerada") : (isPlural ? "encerados" : "encerado"));
        else if (state.equals("waxed_rotten")) stateStr = (isFeminine ? (isPlural ? "podridas" : "podrida") : (isPlural ? "podridos" : "podrido")) + " " + (isFeminine ? (isPlural ? "enceradas" : "encerada") : (isPlural ? "encerados" : "encerado"));

        return blockName + " " + wName + " " + stateStr;
    }

    @Override
    protected void generateTooltipsAndConfig(TranslationBuilder translationBuilder) {
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".waxed", "Encerado");
        translationBuilder.add("item." + moldmod.SporesShadows.MOD_ID + ".waxed_format", "%s encerado");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".moldy_log_desc_1", "Se puede transformar en tablones limpios perdiendo material,");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".moldy_log_desc_2", "pero no se puede usar para recetas vanilla normales.");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".moldy_planks_desc_1", "Solo útil para crafteos simples (palos, vallas, etc).");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".moldy_planks_desc_2", "No se puede usar en recetas complejas a máxima eficiencia.");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".moldy_general_desc_1", "Componente de madera degradado.");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".moldy_general_desc_2", "Estructuralmente debilitado por el moho.");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".moldy_redstone_desc_1", "El moho ha comprometido el mecanismo.");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".moldy_redstone_desc_2", "El tiempo de activación es significativamente mayor.");

        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".title", "Configuración de Spores & Shadows");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.general", "General");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.environment", "Entorno");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.susceptibility", "Susceptibilidad");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.catalysts", "Catalizadores");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.drops", "Botín (Drops)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.structures", "Estructuras");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.furnace_multipliers", "Eficiencia de Horno");

        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.general", "General");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.susceptibility", "Susceptibilidad");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.catalysts", "Catalizadores");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment", "Entorno");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.drops", "Botín (Drops)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures", "Estructuras");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.furnaceMultipliers", "Eficiencia de Horno");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.cave_temperature.@Tooltip", "Temperatura para ignorar el sol en cuevas.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.high_altitude_freezing_temperature.@Tooltip", "Temperatura de congelación que detiene el moho a gran altitud.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.general.enable_mold_growth", "Habilitar Crecimiento de Moho");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.general.infection_threshold", "Umbral de Infección (R > X)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.general.scan_radius", "Radio de Escaneo");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.general.scan_radius.@Tooltip", "1 = 3x3x3, 2 = 5x5x5. Valores altos causan lag.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.general.structures_immune", "Estructuras Generadas Inmunes");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.general.structures_immune.@Tooltip", "Si está activado, naufragios y aldeas no se pudrirán solos antes de que el jugador los toque.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.general.show_debug_in_chat", "Mostrar Matemáticas de Depuración en Chat");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.general.axe_scrape_damage", "Daño Hacha (Raspado)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.general.rotten_break_chance_on_use", "Probabilidad Rotura Bloque Podrido al Usar");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.general.rotten_break_chance_on_use.@Tooltip", "Probabilidad de que un bloque funcional de madera podrida se rompa al interactuar.");

        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.rain_humidity_base", "Humedad Base (Lluvia/Nieve)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.dry_humidity_base", "Humedad Base (Sol/Seco)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.cauldron_adjacent_bonus", "Bono de Adyacencia a Calderos/Barro");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.water_source_humidity_contribution", "Contribución de Humedad por Fuente de Agua");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.water_source_humidity_contribution.@Tooltip", "Humedad lineal añadida por cada bloque de agua expuesto a la habitación.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.max_room_water_humidity_bonus", "Bono Máximo de Humedad por Agua en Habitación");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.max_room_water_humidity_bonus.@Tooltip", "Límite máximo de saturación de humedad producida por fuentes de agua.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.enable_dynamic_room_humidity", "Humedad Dinámica de Habitación");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.enable_dynamic_room_humidity.@Tooltip", "Simula la acumulación y disipación asintótica de humedad con el factor alfa.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.humidity_saturation_speed", "Velocidad de Saturación de Humedad (Alfa)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.humidity_saturation_speed.@Tooltip", "Velocidad a la que sube la humedad en espacios cerrados o húmedos.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.humidity_dissipation_speed", "Velocidad de Disipación de Humedad (Alfa)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.humidity_dissipation_speed.@Tooltip", "Velocidad a la que baja la humedad en espacios ventilados.");
        
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.cave_start_y", "Inicio de Cuevas (Nivel Y)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.cave_full_y", "Profundidad Total de Cuevas (Nivel Y)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.cave_temperature", "Temperatura de Cuevas");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.depth_modifier_per_level", "Modificador de Humedad por Profundidad (+ por bloque)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.max_depth_modifier", "Bono Máximo de Humedad por Profundidad");
        
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.high_altitude_start_y", "Inicio de Gran Altitud (Nivel Y)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.high_altitude_full_y", "Pico de Gran Altitud (Nivel Y)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.high_altitude_freezing_temperature", "Temperatura de Congelación a Gran Altitud");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.min_temperature_survival", "Temperatura Mínima de Supervivencia");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.max_temperature_survival", "Temperatura Máxima de Supervivencia");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.enable_ventilation_drying", "Habilitar Secado por Ventilación");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.enable_ventilation_drying.@Tooltip", "Reduce la humedad efectiva en espacios bien ventilados.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.aeration_drying_bonus", "Bonus de Secado por Aireación");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.aeration_drying_bonus.@Tooltip", "Reducción máxima de humedad proporcionada por la ventilación.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.ventilation_threshold_full_aeration", "Umbral de Aireación Máxima");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.ventilation_threshold_full_aeration.@Tooltip", "Puntuación de ventilación requerida para 100% de secado.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.enable_miasma_spore_pressure", "Habilitar Presión de Esporas de Miasma");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.enable_miasma_spore_pressure.@Tooltip", "Las esporas atrapadas en el aire aceleran el riesgo de moho.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.miasma_spore_multiplier", "Multiplicador de Presión de Esporas");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.miasma_spore_multiplier.@Tooltip", "Multiplicador del riesgo de moho por miasma atrapado.");

        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.susceptibility.stripped_wood_multiplier", "Susceptibilidad de Madera Descortezada");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.susceptibility.planks_multiplier", "Susceptibilidad de Tablones");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.susceptibility.default_multiplier", "Susceptibilidad de Madera por Defecto");
        
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.catalysts.mud_bonus", "Malus de Barro (Mud)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.catalysts.fungi_bonus", "Malus de Hongos");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.catalysts.spore_blossom_bonus", "Malus de Flor de Esporas (Spore Blossom)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.catalysts.podzol_mycelium_bonus", "Malus de Podzol/Micelio");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.catalysts.tainted_block_bonus", "Malus de Adyacencia a Bloque Manchado");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.catalysts.moldy_block_bonus", "Malus de Adyacencia a Bloque Mohoso");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.catalysts.rotten_block_bonus", "Malus de Adyacencia a Bloque Podrido");

        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.drops.stage_2_drop_chance", "Prob. Drop Bloque Mohoso (0.0 a 1.0)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.drops.stage_2_drop_chance.@Tooltip", "Probabilidad de que un bloque Mohoso se suelte a sí mismo al romperse.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.drops.stage_3_drop_chance", "Prob. Drop Bloque Podrido (0.0 a 1.0)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.drops.stage_3_drop_chance.@Tooltip", "Probabilidad de que un bloque Podrido se suelte a sí mismo al romperse.");

        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat1_critical", "Categoría 1 (Degradación Crítica)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat2_high", "Categoría 2 (Degradación Alta)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat3_moderate", "Categoría 3 (Degradación Moderada)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat4_low", "Categoría 4 (Degradación Baja)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat1_critical.moldy_chance", "% Mohoso");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat2_high.moldy_chance", "% Mohoso");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat3_moderate.moldy_chance", "% Mohoso");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat4_low.moldy_chance", "% Mohoso");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat1_critical.tainted_chance", "% Manchado");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat2_high.tainted_chance", "% Manchado");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat3_moderate.tainted_chance", "% Manchado");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat4_low.tainted_chance", "% Manchado");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat1_critical.rotten_chance", "% Podrido");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat2_high.rotten_chance", "% Podrido");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat3_moderate.rotten_chance", "% Podrido");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.cat4_low.rotten_chance", "% Podrido");

        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.furnaceMultipliers.stage_0", "Eficiencia de Horno (Sano)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.furnaceMultipliers.stage_1", "Eficiencia de Horno (Manchado)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.furnaceMultipliers.stage_2", "Eficiencia de Horno (Mohoso)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.furnaceMultipliers.stage_3", "Eficiencia de Horno (Podrido)");
        
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.toxicity", "Toxicidad");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity", "Toxicidad");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.enable_toxic_air", "Habilitar Aire Tóxico / Miasma");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.check_interval_ticks", "Intervalo de Control (Ticks)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.scan_radius", "Radio de Nube Tóxica");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.max_air_volume", "Volumen Máximo de Aire (m³)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.max_euclidean_radius", "Radio Esférico Máximo");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.max_euclidean_radius.@Tooltip", "Radio esférico máximo para controles de aire tóxico.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.mold_toxicity_multiplier", "Multiplicador de Toxicidad del Moho");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.open_sky_ventilation_per_block", "Tasa de Ventilación a Cielo Abierto");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.slab_ventilation_value", "Valor de Ventilación de Losas");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.stairs_ventilation_value", "Valor de Ventilación de Escaleras");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.copper_grate_ventilation_per_block", "Tasa de Ventilación de Rejilla de Cobre");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.leaves_ventilation_value", "Valor de Ventilación de Hojas");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.door_ventilation_value", "Valor de Ventilación de Puertas");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.trapdoor_ventilation_value", "Valor de Ventilación de Trampillas");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.fence_gate_open_ventilation_value", "Valor de Ventilación de Puerta de Valla Abierta");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.ventilation_gap_bonus", "Bonificación de Ventilación por Abertura");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.ventilation_distance_alpha", "Resistencia de Distancia de Ventilación");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.ventilation_distance_alpha.@Tooltip", "Factor de decaimiento del flujo de aire con la distancia.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.enable_distributed_miasma", "Habilitar Flujo de Miasma Distribuido");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.enable_distributed_miasma.@Tooltip", "Habilita el cálculo discreto de flujo de miasma a través de las aberturas de la sala.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.enable_dynamic_spore_saturation", "Habilitar Saturación Dinámica de Esporas");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.enable_dynamic_spore_saturation.@Tooltip", "Simula acumulación y disipación progresiva de miasma.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.dissipation_speed_multiplier", "Multiplicador de Velocidad de Disipación");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.dissipation_speed_multiplier.@Tooltip", "Velocidad de disipación del miasma al ventilar.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.saturation_speed_multiplier", "Multiplicador de Velocidad de Saturación");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.saturation_speed_multiplier.@Tooltip", "Velocidad con la que el miasma se acumula en salas selladas.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.threshold_hunger", "Umbral de Hambre");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.threshold_nausea", "Umbral de Náuseas");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.threshold_poison", "Umbral de Veneno");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.density_threshold_high", "Umbral de Densidad Alta");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.density_threshold_medium", "Umbral de Densidad Media");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.density_threshold_low", "Umbral de Densidad Baja");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.duration_hunger_ticks", "Duración Hambre (Ticks)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.duration_nausea_ticks", "Duración Náuseas (Ticks)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.duration_poison_ticks", "Duración Veneno (Ticks)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.hunger_amplifier", "Amplificador de Hambre");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.nausea_amplifier", "Amplificador de Náuseas");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.poison_amplifier", "Amplificador de Veneno");
        
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.client", "Cliente y Shaders");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.client", "Cliente y Shaders");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.client.mold_z_offset", "Z-Offset de Moho (Arreglo Shader)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.client.mold_z_offset.@Tooltip[0]", "Ajusta esto si notas parpadeos (Z-fighting) con shaders.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.client.mold_z_offset.@Tooltip[1]", "Por defecto: 0.002. Intenta con 0.005 o superior si es necesario.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.water_scan_radius", "Radio de Escaneo de Agua");

        // Flammability
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.flammability", "Inflamabilidad");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.flammability", "Inflamabilidad");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.flammability.enable_flammability", "Habilitar escalado de inflamabilidad");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.flammability.stage_1_burn_bonus", "Bonus probabilidad de ignición Fase 1 (Afectado)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.flammability.stage_1_spread_bonus", "Bonus propagación de fuego Fase 1 (Afectado)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.flammability.stage_2_burn_bonus", "Bonus probabilidad de ignición Fase 2 (Enmohecido)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.flammability.stage_2_spread_bonus", "Bonus propagación de fuego Fase 2 (Enmohecido)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.flammability.stage_3_burn_bonus", "Bonus probabilidad de ignición Fase 3 (Podrido)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.flammability.stage_3_spread_bonus", "Bonus propagación de fuego Fase 3 (Podrido)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.flammability.waxed_burn_bonus", "Bonus ignición madera encerada");

        // Blast Resistance
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.blast_resistance", "Resistencia a explosiones");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.blastResistance", "Resistencia a explosiones");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.blastResistance.enable_blast_resistance_scaling", "Habilitar escalado de resistencia a explosiones");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.blastResistance.stage_1_multiplier", "Multiplicador de resistencia Fase 1 (Afectado)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.blastResistance.stage_2_multiplier", "Multiplicador de resistencia Fase 2 (Enmohecido)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.blastResistance.stage_3_multiplier", "Multiplicador de resistencia Fase 3 (Podrido)");

        // Hardness & Degradation
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.hardness", "Dureza y Degradación");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.hardness", "Dureza y Degradación");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.hardness.enable_hardness_scaling", "Activar Escala de Dureza");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.hardness.stage_1_multiplier", "Multiplicador de Dureza Fase 1 (Contaminado)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.hardness.stage_2_multiplier", "Multiplicador de Dureza Fase 2 (Enmohecido)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.hardness.stage_3_multiplier", "Multiplicador de Dureza Fase 3 (Podrido)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.hardness.enable_break_spore_cloud", "Nube de Esporas al Romper (Sin Toque de Seda)");

        // Redstone
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.redstone", "Redstone");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.redstone", "Redstone");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.redstone.duration_multiplier", "Multiplicador de Duración de Redstone");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.redstone.duration_multiplier.@Tooltip", "Multiplicador aplicado a la duración de pulsación de botones y placas mohosas.");

        // Composter
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.composter", "Compostador");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.composter", "Compostador");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.composter.tainted_chance", "Probabilidad Compostaje Fase 1 (Deteriorado)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.composter.tainted_chance.@Tooltip", "Probabilidad de añadir una capa al compostador con madera deteriorada.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.composter.moldy_chance", "Probabilidad Compostaje Fase 2 (Mohoso)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.composter.moldy_chance.@Tooltip", "Probabilidad de añadir una capa al compostador con madera mohosa.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.composter.rotten_chance", "Probabilidad Compostaje Fase 3 (Podrido)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.composter.rotten_chance.@Tooltip", "Probabilidad de añadir una capa al compostador con madera podrida.");

        // Particles
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.particles", "Partículas");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.particles", "Partículas");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.particles.break_cloud_stage_2_air", "Cantidad Partículas Nube de Esporas Aire (Fase 2)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.particles.break_cloud_stage_2_falling", "Cantidad Partículas Esporas Cayendo (Fase 2)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.particles.break_cloud_stage_2_mycelium", "Cantidad Partículas Micelio (Fase 2)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.particles.break_cloud_stage_3_air", "Cantidad Partículas Nube de Esporas Aire (Fase 3)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.particles.break_cloud_stage_3_falling", "Cantidad Partículas Esporas Cayendo (Fase 3)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.particles.break_cloud_stage_3_mycelium", "Cantidad Partículas Micelio (Fase 3)");

        // Spore Detector & Mask
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.spore_detector", "Detector de Esporas y Máscara");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector", "Detector de Esporas y Máscara");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.block_initial_delay_ticks", "Retardo Inicial del Bloque (ticks)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.block_initial_delay_ticks.@Tooltip", "Ticks antes del primer escaneo tras colocar el bloque.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.block_periodic_delay_ticks", "Retardo Periódico del Bloque (ticks)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.block_periodic_delay_ticks.@Tooltip", "Ticks entre escaneos periódicos.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.redstone_level_multiplier", "Multiplicador de Nivel de Redstone");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.redstone_level_multiplier.@Tooltip", "Multiplicado por el nivel de toxicidad (0-3) para producir la señal de redstone.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.item_use_cooldown_ticks", "Tiempo de Recarga del Objeto (ticks)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.item_use_cooldown_ticks.@Tooltip", "Tiempo de recarga tras el escaneo con clic derecho.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.geiger_check_interval_ticks", "Intervalo de Control Geiger (ticks)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.geiger_check_interval_ticks.@Tooltip", "Frecuencia del sonido pasivo de contador Geiger al sostenerlo en mano.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.geiger_density_threshold", "Umbral de Densidad Geiger");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.geiger_density_threshold.@Tooltip", "Densidad mínima de esporas para activar el sonido del contador Geiger.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.spore_mask_durability", "Durabilidad de la Máscara Antiesporas");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.spore_mask_durability.@Tooltip", "Durabilidad máxima de la Máscara Antiesporas (requiere reinicio).");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.spore_mask_armor_points", "Puntos de Armadura de la Máscara");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.sporeDetector.spore_mask_armor_points.@Tooltip", "Puntos de armadura proporcionados por la máscara (requiere reinicio).");

        // Structures environmental bonuses
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.underwater_rotten_bonus", "Bonus Podrido Bajo el Agua/Profundidad (%)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.underwater_rotten_bonus.@Tooltip", "Bonus de porcentaje podrido bajo el agua o Y <= 60.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.underwater_tainted_bonus", "Bonus Manchado Bajo el Agua/Profundidad (%)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.underwater_tainted_bonus.@Tooltip", "Bonus de porcentaje manchado bajo el agua o Y <= 60.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.underground_moldy_bonus", "Bonus Mohoso Subterráneo (%)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.underground_moldy_bonus.@Tooltip", "Bonus de porcentaje mohoso bajo tierra (fuera del agua).");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.ground_contact_moldy_bonus", "Bonus Mohoso Contacto con el Suelo (%)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.ground_contact_moldy_bonus.@Tooltip", "Bonus de porcentaje mohoso cerca del suelo (humedad ascendente).");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.sky_access_rotten_to_moldy", "Conversión Podrido→Mohoso Cielo Abierto (%)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.sky_access_rotten_to_moldy.@Tooltip", "Porcentaje máximo de podrido convertido a mohoso con acceso al cielo.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.sky_access_moldy_bonus", "Bonus Mohoso Cielo Abierto (%)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.structures.sky_access_moldy_bonus.@Tooltip", "Bonus de porcentaje mohoso expuesto al aire/lluvia.");

        // Jade Tooltips
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.infection", "Riesgo de Infección: %d%%");
        translationBuilder.add("config.jade.plugin_" + moldmod.SporesShadows.MOD_ID + ".moldy_info", "Spores & Shadows: Info de Moho");

        // Advancements
        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".root.title", "Spores & Shadows");
        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".root.description", "Sobrevive a la decadencia de la naturaleza.");
        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".wax_block.title", "Prevención Natural");
        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".wax_block.description", "Usa un panal de miel para encerar un bloque de madera y detener el moho.");
        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".scrape_mold.title", "Grasa de Codo");
        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".scrape_mold.description", "Raspa el moho de un bloque de madera con un hacha.");
        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".toxic_air.title", "Respiración Corta");
        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".toxic_air.description", "Sufre el veneno del miasma al respirar demasiado moho.");
        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".crumble.title", "Polvo al Polvo");
        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".crumble.description", "Intenta romper un bloque de madera podrido y mira cómo se desmorona en la nada.");

        // JEI
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".category.waxing", "Encerado");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".category.scraping", "Raspado con Hacha");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".info.rotten_wood", "La madera podrida es quebradiza y frágil. No se puede curar con un hacha. Requiere Toque de Seda para ser recolectada, de lo contrario se desintegrará en la nada al romperse.");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".info.spore_mask", "La Máscara Antiesporas proporciona protección total contra el miasma tóxico (Veneno, Náusea y Hambre). Consume durabilidad al filtrar el aire tóxico. Cambia el filtro reparándola en un yunque con Lana (#minecraft:wool). Solo se puede encantar con Irrompibilidad, Reparación y Maldición de Desaparición.");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".info.spore_filtration", "Filtración de Esporas es un encantamiento de casco que neutraliza el miasma tóxico y la inhalación de esporas. Consume durabilidad del casco al exponerse al miasma (Nivel I: 2 durabilidad, Nivel II: 1 durabilidad, Nivel III: 50% de ahorro). Compatible con todos los cascos convencionales.");

        // Jade Tooltips for Spore Protection & Spore Detector
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.spore_protection_mask", "Protección contra Esporas: Activa (Máscara Antiesporas)");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.spore_protection_enchant", "Filtración de Esporas: Nivel %d");
        translationBuilder.add("config.jade.plugin_" + moldmod.SporesShadows.MOD_ID + ".spore_protection_info", "Spores & Shadows: Info de Protección de Esporas");
        translationBuilder.add("config.jade.plugin_" + moldmod.SporesShadows.MOD_ID + ".spore_detector_info", "Spores & Shadows: Info del Detector de Miasma");

        translationBuilder.add("item." + moldmod.SporesShadows.MOD_ID + ".spore_mask", "Máscara Antiesporas");
        translationBuilder.add("block." + moldmod.SporesShadows.MOD_ID + ".spore_detector", "Detector de Miasma");
        translationBuilder.add("item." + moldmod.SporesShadows.MOD_ID + ".spore_detector", "Detector de Miasma");
        translationBuilder.add("jei." + moldmod.SporesShadows.MOD_ID + ".info.spore_detector", "El Detector de Miasma mide la toxicidad del aire y la ventilación de la habitación. Clic Derecho en el aire para escanear. Se puede colocar en paredes o suelos y emite una señal de Redstone proporcional a la densidad de esporas.");
        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".spore_mask_protection.title", "Aire Puro");
        translationBuilder.add("advancements." + moldmod.SporesShadows.MOD_ID + ".spore_mask_protection.description", "Filtra las esporas tóxicas respirando a través de una Máscara Antiesporas en una sala contaminada.");

        // Enchantments
        translationBuilder.add("enchantment." + moldmod.SporesShadows.MOD_ID + ".spore_filtration", "Filtración de Esporas");
        translationBuilder.add("enchantment." + moldmod.SporesShadows.MOD_ID + ".spore_filtration.desc", "Neutraliza el miasma tóxico consumiendo durabilidad del casco.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.enable_spore_mask_protection", "Habilitar Protección Máscara Antiesporas");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.enable_spore_mask_protection.@Tooltip", "Protege al portador del miasma tóxico y efectos negativos.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.spore_mask_damage_per_exposure", "Coste Durabilidad de Máscara por Exposición");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.spore_mask_damage_per_exposure.@Tooltip", "Durabilidad consumida por la máscara en cada comprobación de miasma.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.enable_spore_filtration_enchantment", "Habilitar Encantamiento Filtración de Esporas");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.enable_spore_filtration_enchantment.@Tooltip", "Encantamiento para cascos que neutraliza esporas tóxicas.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.filtration_level_1_durability_cost", "Coste de Durabilidad Filtración Nivel I");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.filtration_level_1_durability_cost.@Tooltip", "Coste de durabilidad del casco por comprobación con Nivel I.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.filtration_level_2_durability_cost", "Coste de Durabilidad Filtración Nivel II");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.filtration_level_2_durability_cost.@Tooltip", "Coste de durabilidad del casco por comprobación con Nivel II.");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.filtration_level_3_save_chance", "Probabilidad de Ahorro de Durabilidad Nivel III");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.filtration_level_3_save_chance.@Tooltip", "Probabilidad de evitar la pérdida de durabilidad con Nivel III.");
    }
}
