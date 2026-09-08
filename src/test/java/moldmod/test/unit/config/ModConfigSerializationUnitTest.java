package moldmod.test.unit.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import moldmod.config.ModConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ModConfigSerializationUnitTest {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Test
    @DisplayName("ModConfig can be serialized to JSON and deserialized back without data loss")
    void testModConfigJsonRoundTrip() {
        ModConfig original = new ModConfig();
        original.general.enable_mold_growth = false;
        original.general.infection_threshold = 0.75f;
        original.general.scan_radius = 3;
        original.catalysts.fungi_bonus = 0.42f;
        original.environment.water_scan_radius = 5;
        original.toxicity.scan_radius = 12;
        original.structures.cat1_critical.moldy_chance = 99;

        String json = gson.toJson(original);
        assertNotNull(json);
        assertFalse(json.isEmpty());

        ModConfig restored = gson.fromJson(json, ModConfig.class);
        assertNotNull(restored);

        assertFalse(restored.general.enable_mold_growth);
        assertEquals(0.75f, restored.general.infection_threshold, 1e-4);
        assertEquals(3, restored.general.scan_radius);
        assertEquals(0.42f, restored.catalysts.fungi_bonus, 1e-4);
        assertEquals(5, restored.environment.water_scan_radius);
        assertEquals(12, restored.toxicity.scan_radius);
        assertEquals(99, restored.structures.cat1_critical.moldy_chance);
    }

    @Test
    @DisplayName("Null nested categories can be re-instantiated or handled properly")
    void testNestedCategoriesIntegrity() {
        ModConfig config = new ModConfig();
        assertNotNull(config.general);
        assertNotNull(config.susceptibility);
        assertNotNull(config.catalysts);
        assertNotNull(config.environment);
        assertNotNull(config.drops);
        assertNotNull(config.structures);
        assertNotNull(config.furnaceMultipliers);
        assertNotNull(config.flammability);
        assertNotNull(config.blastResistance);
        assertNotNull(config.hardness);
        assertNotNull(config.toxicity);
        assertNotNull(config.client);
    }
}
