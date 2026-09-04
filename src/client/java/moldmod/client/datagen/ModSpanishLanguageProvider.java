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

        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.rain_humidity_base", "Humedad Base (Lluvia/Nieve)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.dry_humidity_base", "Humedad Base (Sol/Seco)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.water_adjacent_bonus", "Bono de Adyacencia al Agua");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.cauldron_adjacent_bonus", "Bono de Adyacencia a Calderos/Barro");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.max_local_humidity_bonus", "Bono Máximo de Humedad Local");
        
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.cave_start_y", "Inicio de Cuevas (Nivel Y)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.cave_full_y", "Profundidad Total de Cuevas (Nivel Y)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.cave_temperature", "Temperatura de Cuevas");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.depth_modifier_per_level", "Modificador de Humedad por Profundidad (+ por bloque)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.max_depth_modifier", "Malus Máximo de Humedad por Profundidad");
        
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.high_altitude_start_y", "Inicio de Gran Altitud (Nivel Y)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.high_altitude_full_y", "Pico de Gran Altitud (Nivel Y)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.high_altitude_freezing_temperature", "Temperatura de Congelación a Gran Altitud");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.min_temperature_survival", "Temp Mínima de Supervivencia");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.environment.max_temperature_survival", "Temp Máxima de Supervivencia");

        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.susceptibility.stripped_wood_multiplier", "Susceptibilidad de Madera sin Corteza");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.susceptibility.planks_multiplier", "Susceptibilidad de Tablones de Madera");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.susceptibility.default_multiplier", "Susceptibilidad de Madera por Defecto");
        
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.catalysts.mud_bonus", "Malus de Barro (Mud)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.catalysts.fungi_bonus", "Malus de Adyacencia a Hongos");
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
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.mold_toxicity_multiplier", "Multiplicador de Toxicidad del Moho");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.ventilation_gap_bonus", "Bonificación de Ventilación por Abertura");
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
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.blast_resistance", "Resistencia a explosiones");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.blast_resistance.enable_blast_resistance_scaling", "Habilitar escalado de resistencia a explosiones");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.blast_resistance.stage_1_multiplier", "Multiplicador de resistencia Fase 1 (Afectado)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.blast_resistance.stage_2_multiplier", "Multiplicador de resistencia Fase 2 (Enmohecido)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.blast_resistance.stage_3_multiplier", "Multiplicador de resistencia Fase 3 (Podrido)");

        // Hardness & Degradation
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".category.hardness", "Dureza y Degradación");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.hardness.enable_hardness_scaling", "Activar Escala de Dureza");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.hardness.stage_1_multiplier", "Multiplicador de Dureza Fase 1 (Contaminado)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.hardness.stage_2_multiplier", "Multiplicador de Dureza Fase 2 (Enmohecido)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.hardness.stage_3_multiplier", "Multiplicador de Dureza Fase 3 (Podrido)");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.hardness.enable_break_spore_cloud", "Nube de Esporas al Romper (Sin Toque de Seda)");

        // Jade Tooltips
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.stage", "Fase: ");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.infection", "Riesgo de Infección: %d%%");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.waxed", "Encerado: ");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.yes", "Sí");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.no", "No");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.stage.0", "Sano");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.stage.1", "Contaminado");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.stage.2", "Mohoso");
        translationBuilder.add("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.stage.3", "Podrido");
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
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.enable_spore_filtration_enchantment", "Habilitar Encantamiento Filtración de Esporas");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.filtration_level_1_durability_cost", "Coste de Durabilidad Filtración Nivel I");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.filtration_level_2_durability_cost", "Coste de Durabilidad Filtración Nivel II");
        translationBuilder.add("text.autoconfig." + moldmod.SporesShadows.MOD_ID + ".option.toxicity.filtration_level_3_save_chance", "Probabilidad de Ahorro de Durabilidad Nivel III");
    }
}
