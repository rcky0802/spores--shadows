package moldmod.test.unit.lang;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class LocalizationUnitTest {

    private static final String[] LANGUAGES = {"en_us", "it_it", "es_es", "fr_fr", "de_de"};
    private static final Gson GSON = new Gson();
    private static final Type MAP_TYPE = new TypeToken<Map<String, String>>() {}.getType();

    private static final List<String> CRITICAL_DETECTOR_KEYS = List.of(
            "message.spores--shadows.detector.none",
            "message.spores--shadows.detector.blocks_dist",
            "message.spores--shadows.detector.purifiers_active",
            "message.spores--shadows.detector.purifiers_none",
            "message.spores--shadows.detector.dehumidifiers_active",
            "message.spores--shadows.detector.dehumidifiers_none",
            "message.spores--shadows.detector.humidifiers_active",
            "message.spores--shadows.spore_detector.header",
            "message.spores--shadows.spore_detector.clean",
            "message.spores--shadows.spore_detector.warning",
            "message.spores--shadows.spore_detector.moderate",
            "message.spores--shadows.spore_detector.lethal",
            "message.spores--shadows.spore_detector.metric",
            "message.spores--shadows.spore_detector.room",
            "message.spores--shadows.spore_detector.open_air",
            "message.spores--shadows.spore_detector.aeration",
            "message.spores--shadows.spore_detector.trend_stable",
            "message.spores--shadows.spore_detector.trend_purifying",
            "message.spores--shadows.spore_detector.trend_accumulating",
            "message.spores--shadows.moisture_detector.header",
            "message.spores--shadows.moisture_detector.dry",
            "message.spores--shadows.moisture_detector.moderate",
            "message.spores--shadows.moisture_detector.humid",
            "message.spores--shadows.moisture_detector.critical",
            "message.spores--shadows.moisture_detector.metric",
            "message.spores--shadows.moisture_detector.room",
            "message.spores--shadows.moisture_detector.open_air",
            "message.spores--shadows.moisture_detector.aeration",
            "message.spores--shadows.moisture_detector.trend_stable",
            "message.spores--shadows.moisture_detector.trend_drying",
            "message.spores--shadows.moisture_detector.trend_humidifying"
    );

    private static final List<String> CRITICAL_JEI_MOLD_KEYS = List.of(
            "jei.spores--shadows.category.mold_infection",
            "jei.spores--shadows.infection.threshold",
            "jei.spores--shadows.infection.stage_0_to_1",
            "jei.spores--shadows.infection.stage_1_to_2",
            "jei.spores--shadows.infection.stage_2_to_3",
            "jei.spores--shadows.infection.note",
            "jei.spores--shadows.infection.tooltip.title",
            "jei.spores--shadows.infection.tooltip.condition",
            "jei.spores--shadows.infection.tooltip.desc1",
            "jei.spores--shadows.infection.tooltip.desc2",
            "jei.spores--shadows.infection.tooltip.prevention",
            "jei.spores--shadows.infection.tooltip.cure"
    );

    @Test
    @DisplayName("Verify critical detector localization keys exist in all languages")
    void testCriticalDetectorKeysExist() throws Exception {
        for (String lang : LANGUAGES) {
            File resFile = new File("src/main/resources/assets/spores--shadows/lang/" + lang + ".json");
            File genFile = new File("src/main/generated/assets/spores--shadows/lang/" + lang + ".json");

            assertTrue(resFile.exists(), "Resource lang file missing: " + resFile.getPath());
            assertTrue(genFile.exists(), "Generated lang file missing: " + genFile.getPath());

            Map<String, String> resMap;
            try (FileReader reader = new FileReader(resFile, StandardCharsets.UTF_8)) {
                resMap = GSON.fromJson(reader, MAP_TYPE);
            }

            for (String key : CRITICAL_DETECTOR_KEYS) {
                assertTrue(resMap.containsKey(key),
                        "Missing key '" + key + "' in " + lang + ".json");
                assertFalse(resMap.get(key).isBlank(),
                        "Blank value for key '" + key + "' in " + lang + ".json");
            }

            for (String key : CRITICAL_JEI_MOLD_KEYS) {
                assertTrue(resMap.containsKey(key),
                        "Missing JEI mold infection key '" + key + "' in " + lang + ".json");
                assertFalse(resMap.get(key).isBlank(),
                        "Blank value for key '" + key + "' in " + lang + ".json");
            }
        }
    }

    @Test
    @DisplayName("Verify resources and generated language files are in sync")
    void testResourcesAndGeneratedInSync() throws Exception {
        for (String lang : LANGUAGES) {
            File resFile = new File("src/main/resources/assets/spores--shadows/lang/" + lang + ".json");
            File genFile = new File("src/main/generated/assets/spores--shadows/lang/" + lang + ".json");

            Map<String, String> resMap;
            try (FileReader reader = new FileReader(resFile, StandardCharsets.UTF_8)) {
                resMap = GSON.fromJson(reader, MAP_TYPE);
            }

            Map<String, String> genMap;
            try (FileReader reader = new FileReader(genFile, StandardCharsets.UTF_8)) {
                genMap = GSON.fromJson(reader, MAP_TYPE);
            }

            assertEquals(genMap.keySet(), resMap.keySet(),
                    "Keys discrepancy between resources and generated for " + lang);
        }
    }

    @Test
    @DisplayName("Verify all keys in English exist in all supported languages")
    void testAllKeysCoveredInAllLanguages() throws Exception {
        File enFile = new File("src/main/resources/assets/spores--shadows/lang/en_us.json");
        Map<String, String> enMap;
        try (FileReader reader = new FileReader(enFile, StandardCharsets.UTF_8)) {
            enMap = GSON.fromJson(reader, MAP_TYPE);
        }

        for (String lang : LANGUAGES) {
            if ("en_us".equals(lang)) continue;

            File langFile = new File("src/main/resources/assets/spores--shadows/lang/" + lang + ".json");
            Map<String, String> langMap;
            try (FileReader reader = new FileReader(langFile, StandardCharsets.UTF_8)) {
                langMap = GSON.fromJson(reader, MAP_TYPE);
            }

            for (String key : enMap.keySet()) {
                assertTrue(langMap.containsKey(key),
                        "Language '" + lang + "' is missing translation key '" + key + "'");
            }
        }
    }
}
