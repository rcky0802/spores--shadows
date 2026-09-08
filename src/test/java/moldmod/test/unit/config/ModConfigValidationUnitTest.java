package moldmod.test.unit.config;

import me.shedaniel.autoconfig.ConfigData;
import moldmod.config.ModConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

public class ModConfigValidationUnitTest {

    private ModConfig config;

    @BeforeEach
    void setUp() {
        config = new ModConfig();
    }

    @Test
    @DisplayName("Negative and excessive values in General category get properly clamped")
    void testGeneralClamping() throws ConfigData.ValidationException {
        config.general.infection_threshold = -1.5f;
        config.general.scan_radius = -10;
        config.general.axe_scrape_damage = -5;

        config.validatePostLoad();

        assertEquals(0.0f, config.general.infection_threshold, 1e-4);
        assertEquals(1, config.general.scan_radius);
        assertEquals(0, config.general.axe_scrape_damage);

        config.general.infection_threshold = 99.0f;
        config.general.scan_radius = 50;

        config.validatePostLoad();

        assertEquals(2.0f, config.general.infection_threshold, 1e-4);
        assertEquals(5, config.general.scan_radius);
    }

    @ParameterizedTest(name = "Water scan radius {0} clamped to {1}")
    @CsvSource({
            "-10, 1",
            "0, 1",
            "1, 1",
            "5, 5",
            "10, 10",
            "25, 10"
    })
    void testEnvironmentWaterScanRadiusClamping(int input, int expected) throws ConfigData.ValidationException {
        config.environment.water_scan_radius = input;
        config.validatePostLoad();
        assertEquals(expected, config.environment.water_scan_radius);
    }

    @Test
    @DisplayName("Environment boundaries get clamped correctly")
    void testEnvironmentClamping() throws ConfigData.ValidationException {
        config.environment.max_depth_modifier = -2.0;
        config.environment.depth_modifier_per_level = 5.0;
        config.environment.rain_humidity_base = -1.0;
        config.environment.dry_humidity_base = 3.5;
        config.environment.max_local_humidity_bonus = -0.5;
        config.environment.water_adjacent_bonus = 2.0;
        config.environment.cauldron_adjacent_bonus = -1.0;
        config.environment.aeration_drying_bonus = 10.0;
        config.environment.ventilation_threshold_full_aeration = 0.01;
        config.environment.miasma_spore_multiplier = 10.0;

        config.validatePostLoad();

        assertEquals(0.0, config.environment.max_depth_modifier, 1e-4);
        assertEquals(1.0, config.environment.depth_modifier_per_level, 1e-4);
        assertEquals(0.0, config.environment.rain_humidity_base, 1e-4);
        assertEquals(2.0, config.environment.dry_humidity_base, 1e-4);
        assertEquals(0.0, config.environment.max_local_humidity_bonus, 1e-4);
        assertEquals(1.0, config.environment.water_adjacent_bonus, 1e-4);
        assertEquals(0.0, config.environment.cauldron_adjacent_bonus, 1e-4);
        assertEquals(2.0, config.environment.aeration_drying_bonus, 1e-4);
        assertEquals(0.1, config.environment.ventilation_threshold_full_aeration, 1e-4);
        assertEquals(5.0, config.environment.miasma_spore_multiplier, 1e-4);
    }

    @Test
    @DisplayName("Drops drop chances get clamped between 0.0 and 1.0")
    void testDropsClamping() throws ConfigData.ValidationException {
        config.drops.stage_2_drop_chance = -0.5f;
        config.drops.stage_3_drop_chance = 1.5f;

        config.validatePostLoad();

        assertEquals(0.0f, config.drops.stage_2_drop_chance, 1e-4);
        assertEquals(1.0f, config.drops.stage_3_drop_chance, 1e-4);
    }

    @Test
    @DisplayName("Furnace, Blast Resistance, and Hardness multipliers get clamped")
    void testMultipliersClamping() throws ConfigData.ValidationException {
        config.furnaceMultipliers.stage_0 = -1.0f;
        config.furnaceMultipliers.stage_3 = 10.0f;

        config.blastResistance.stage_1_multiplier = -0.5f;
        config.blastResistance.stage_3_multiplier = 5.0f;

        config.hardness.stage_1_multiplier = -1.0f;
        config.hardness.stage_3_multiplier = 10.0f;

        config.validatePostLoad();

        assertEquals(0.0f, config.furnaceMultipliers.stage_0, 1e-4);
        assertEquals(5.0f, config.furnaceMultipliers.stage_3, 1e-4);

        assertEquals(0.0f, config.blastResistance.stage_1_multiplier, 1e-4);
        assertEquals(2.0f, config.blastResistance.stage_3_multiplier, 1e-4);

        assertEquals(0.0f, config.hardness.stage_1_multiplier, 1e-4);
        assertEquals(2.0f, config.hardness.stage_3_multiplier, 1e-4);
    }

    @Test
    @DisplayName("Toxicity scan radius, air volume, and distance alpha get clamped")
    void testToxicityGeometryClamping() throws ConfigData.ValidationException {
        config.toxicity.check_interval_ticks = 5;
        config.toxicity.scan_radius = 50;
        config.toxicity.max_air_volume = 5;
        config.toxicity.max_euclidean_radius = 100;
        config.toxicity.ventilation_distance_alpha = 5.0;

        config.validatePostLoad();

        assertEquals(10, config.toxicity.check_interval_ticks);
        assertEquals(16, config.toxicity.scan_radius);
        assertEquals(10, config.toxicity.max_air_volume);
        assertEquals(32, config.toxicity.max_euclidean_radius);
        assertEquals(2.0, config.toxicity.ventilation_distance_alpha, 1e-4);
    }

    @Test
    @DisplayName("Toxicity effects thresholds, durations, and amplifiers get clamped")
    void testToxicityEffectsClamping() throws ConfigData.ValidationException {
        config.toxicity.threshold_hunger = -10.0;
        config.toxicity.density_threshold_high = 2.5;
        config.toxicity.duration_hunger_ticks = -50;
        config.toxicity.hunger_amplifier = 10;
        config.toxicity.poison_amplifier = -2;

        config.validatePostLoad();

        assertEquals(0.0, config.toxicity.threshold_hunger, 1e-4);
        assertEquals(1.0, config.toxicity.density_threshold_high, 1e-4);
        assertEquals(1, config.toxicity.duration_hunger_ticks);
        assertEquals(5, config.toxicity.hunger_amplifier);
        assertEquals(0, config.toxicity.poison_amplifier);
    }

    @Test
    @DisplayName("Gear durability damage and protection chances get clamped")
    void testGearProtectionClamping() throws ConfigData.ValidationException {
        config.toxicity.spore_mask_damage_per_exposure = 0;
        config.toxicity.filtration_level_1_durability_cost = 50;
        config.toxicity.filtration_level_2_durability_cost = -1;
        config.toxicity.filtration_level_3_save_chance = 1.8f;
        config.client.mold_z_offset = -0.5f;

        config.validatePostLoad();

        assertEquals(1, config.toxicity.spore_mask_damage_per_exposure);
        assertEquals(20, config.toxicity.filtration_level_1_durability_cost);
        assertEquals(1, config.toxicity.filtration_level_2_durability_cost);
        assertEquals(1.0f, config.toxicity.filtration_level_3_save_chance, 1e-4);
        assertEquals(0.0f, config.client.mold_z_offset, 1e-4);
    }
}
