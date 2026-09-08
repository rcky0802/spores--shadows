package moldmod.test.unit.config;

import moldmod.config.ModConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ModConfigDefaultsUnitTest {

    private ModConfig config;

    @BeforeEach
    void setUp() {
        config = new ModConfig();
    }

    @Test
    @DisplayName("General configuration defaults match expected game balance")
    void testGeneralDefaults() {
        assertTrue(config.general.enable_mold_growth);
        assertEquals(0.50f, config.general.infection_threshold, 1e-4);
        assertEquals(1, config.general.scan_radius);
        assertTrue(config.general.structures_immune);
        assertFalse(config.general.show_debug_in_chat);
        assertEquals(1, config.general.axe_scrape_damage);
    }

    @Test
    @DisplayName("Susceptibility multipliers defaults")
    void testSusceptibilityDefaults() {
        assertEquals(1.4f, config.susceptibility.stripped_wood_multiplier, 1e-4);
        assertEquals(0.8f, config.susceptibility.planks_multiplier, 1e-4);
        assertEquals(1.0f, config.susceptibility.default_multiplier, 1e-4);
    }

    @Test
    @DisplayName("Catalysts bonus score defaults")
    void testCatalystsDefaults() {
        assertEquals(0.05f, config.catalysts.mud_bonus, 1e-4);
        assertEquals(0.15f, config.catalysts.podzol_mycelium_bonus, 1e-4);
        assertEquals(0.25f, config.catalysts.fungi_bonus, 1e-4);
        assertEquals(0.80f, config.catalysts.spore_blossom_bonus, 1e-4);
        assertEquals(0.03f, config.catalysts.tainted_block_bonus, 1e-4);
        assertEquals(0.06f, config.catalysts.moldy_block_bonus, 1e-4);
        assertEquals(0.12f, config.catalysts.rotten_block_bonus, 1e-4);
    }

    @Test
    @DisplayName("Environment moisture, temperature, and aeration defaults")
    void testEnvironmentDefaults() {
        assertEquals(0.8, config.environment.rain_humidity_base, 1e-4);
        assertEquals(0.3, config.environment.dry_humidity_base, 1e-4);
        assertEquals(0.40, config.environment.max_depth_modifier, 1e-4);
        assertEquals(0.00625, config.environment.depth_modifier_per_level, 1e-6);
        assertEquals(0.60, config.environment.max_local_humidity_bonus, 1e-4);
        assertEquals(0.15, config.environment.water_adjacent_bonus, 1e-4);
        assertEquals(0.1, config.environment.cauldron_adjacent_bonus, 1e-4);
        assertEquals(3, config.environment.water_scan_radius);

        assertTrue(config.environment.enable_ventilation_drying);
        assertEquals(0.50, config.environment.aeration_drying_bonus, 1e-4);
        assertEquals(24.0, config.environment.ventilation_threshold_full_aeration, 1e-4);

        assertTrue(config.environment.enable_miasma_spore_pressure);
        assertEquals(0.50, config.environment.miasma_spore_multiplier, 1e-4);

        assertEquals(0.15f, config.environment.min_temperature_survival, 1e-4);
        assertEquals(1.5f, config.environment.max_temperature_survival, 1e-4);
        assertEquals(0.5f, config.environment.cave_temperature, 1e-4);
        assertEquals(64, config.environment.cave_start_y);
        assertEquals(48, config.environment.cave_full_y);

        assertEquals(-0.5f, config.environment.high_altitude_freezing_temperature, 1e-4);
        assertEquals(128, config.environment.high_altitude_start_y);
        assertEquals(256, config.environment.high_altitude_full_y);
    }

    @Test
    @DisplayName("Drops configuration defaults")
    void testDropsDefaults() {
        assertEquals(0.50f, config.drops.stage_2_drop_chance, 1e-4);
        assertEquals(0.00f, config.drops.stage_3_drop_chance, 1e-4);
    }

    @Test
    @DisplayName("Structure degradation categories defaults")
    void testStructuresDefaults() {
        assertNotNull(config.structures.cat1_critical);
        assertEquals(10, config.structures.cat1_critical.moldy_chance);
        assertEquals(30, config.structures.cat1_critical.tainted_chance);
        assertEquals(60, config.structures.cat1_critical.rotten_chance);

        assertNotNull(config.structures.cat2_high);
        assertEquals(35, config.structures.cat2_high.moldy_chance);
        assertEquals(35, config.structures.cat2_high.tainted_chance);
        assertEquals(20, config.structures.cat2_high.rotten_chance);

        assertNotNull(config.structures.cat3_moderate);
        assertEquals(35, config.structures.cat3_moderate.moldy_chance);
        assertEquals(15, config.structures.cat3_moderate.tainted_chance);
        assertEquals(0, config.structures.cat3_moderate.rotten_chance);

        assertNotNull(config.structures.cat4_low);
        assertEquals(5, config.structures.cat4_low.moldy_chance);
        assertEquals(0, config.structures.cat4_low.tainted_chance);
        assertEquals(0, config.structures.cat4_low.rotten_chance);
    }

    @Test
    @DisplayName("Furnace fuel multipliers defaults")
    void testFurnaceMultipliersDefaults() {
        assertEquals(1.0f, config.furnaceMultipliers.stage_0, 1e-4);
        assertEquals(0.5f, config.furnaceMultipliers.stage_1, 1e-4);
        assertEquals(0.25f, config.furnaceMultipliers.stage_2, 1e-4);
        assertEquals(0.125f, config.furnaceMultipliers.stage_3, 1e-4);
    }

    @Test
    @DisplayName("Flammability burn and spread bonuses defaults")
    void testFlammabilityDefaults() {
        assertTrue(config.flammability.enable_flammability);
        assertEquals(5, config.flammability.stage_1_burn_bonus);
        assertEquals(10, config.flammability.stage_1_spread_bonus);
        assertEquals(10, config.flammability.stage_2_burn_bonus);
        assertEquals(25, config.flammability.stage_2_spread_bonus);
        assertEquals(20, config.flammability.stage_3_burn_bonus);
        assertEquals(60, config.flammability.stage_3_spread_bonus);
        assertEquals(5, config.flammability.waxed_burn_bonus);
    }

    @Test
    @DisplayName("Blast resistance scaling defaults")
    void testBlastResistanceDefaults() {
        assertTrue(config.blastResistance.enable_blast_resistance_scaling);
        assertEquals(0.80f, config.blastResistance.stage_1_multiplier, 1e-4);
        assertEquals(0.50f, config.blastResistance.stage_2_multiplier, 1e-4);
        assertEquals(0.10f, config.blastResistance.stage_3_multiplier, 1e-4);
    }

    @Test
    @DisplayName("Hardness and mining speed scaling defaults")
    void testHardnessDefaults() {
        assertTrue(config.hardness.enable_hardness_scaling);
        assertEquals(0.80f, config.hardness.stage_1_multiplier, 1e-4);
        assertEquals(0.50f, config.hardness.stage_2_multiplier, 1e-4);
        assertEquals(0.20f, config.hardness.stage_3_multiplier, 1e-4);
        assertTrue(config.hardness.enable_break_spore_cloud);
    }

    @Test
    @DisplayName("Toxicity, ventilation, and gear defaults")
    void testToxicityDefaults() {
        assertTrue(config.toxicity.enable_toxic_air);
        assertEquals(40, config.toxicity.check_interval_ticks);
        assertEquals(8, config.toxicity.scan_radius);
        assertEquals(2048, config.toxicity.max_air_volume);
        assertEquals(16, config.toxicity.max_euclidean_radius);
        assertEquals(0.75f, config.toxicity.mold_toxicity_multiplier, 1e-4);

        assertEquals(24.0, config.toxicity.open_sky_ventilation_per_block, 1e-4);
        assertEquals(12.0, config.toxicity.slab_ventilation_value, 1e-4);
        assertEquals(6.0, config.toxicity.stairs_ventilation_value, 1e-4);
        assertEquals(6.0f, config.toxicity.ventilation_gap_bonus, 1e-4);
        assertEquals(18.0, config.toxicity.copper_grate_ventilation_per_block, 1e-4);
        assertEquals(18.0, config.toxicity.leaves_ventilation_value, 1e-4);
        assertEquals(18.0, config.toxicity.door_ventilation_value, 1e-4);
        assertEquals(18.0, config.toxicity.trapdoor_ventilation_value, 1e-4);
        assertEquals(18.0, config.toxicity.fence_gate_open_ventilation_value, 1e-4);
        assertEquals(0.25, config.toxicity.ventilation_distance_alpha, 1e-4);

        assertTrue(config.toxicity.enable_distributed_miasma);
        assertTrue(config.toxicity.enable_dynamic_spore_saturation);
        assertEquals(0.35, config.toxicity.dissipation_speed_multiplier, 1e-4);
        assertEquals(0.15, config.toxicity.saturation_speed_multiplier, 1e-4);

        assertEquals(6.0, config.toxicity.threshold_hunger, 1e-4);
        assertEquals(12.0, config.toxicity.threshold_nausea, 1e-4);
        assertEquals(18.0, config.toxicity.threshold_poison, 1e-4);

        assertEquals(0.1667, config.toxicity.density_threshold_high, 1e-4);
        assertEquals(0.0833, config.toxicity.density_threshold_medium, 1e-4);
        assertEquals(0.0417, config.toxicity.density_threshold_low, 1e-4);

        assertEquals(80, config.toxicity.duration_hunger_ticks);
        assertEquals(140, config.toxicity.duration_nausea_ticks);
        assertEquals(100, config.toxicity.duration_poison_ticks);

        assertEquals(0, config.toxicity.hunger_amplifier);
        assertEquals(0, config.toxicity.nausea_amplifier);
        assertEquals(0, config.toxicity.poison_amplifier);

        assertTrue(config.toxicity.enable_spore_mask_protection);
        assertEquals(1, config.toxicity.spore_mask_damage_per_exposure);

        assertTrue(config.toxicity.enable_spore_filtration_enchantment);
        assertEquals(2, config.toxicity.filtration_level_1_durability_cost);
        assertEquals(1, config.toxicity.filtration_level_2_durability_cost);
        assertEquals(0.50f, config.toxicity.filtration_level_3_save_chance, 1e-4);
    }

    @Test
    @DisplayName("Client category defaults")
    void testClientDefaults() {
        assertEquals(0.002f, config.client.mold_z_offset, 1e-4);
    }
}
