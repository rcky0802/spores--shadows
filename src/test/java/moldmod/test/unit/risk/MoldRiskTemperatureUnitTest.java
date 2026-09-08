package moldmod.test.unit.risk;

import moldmod.config.ModConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

public class MoldRiskTemperatureUnitTest {

    private float computeEffectiveTemp(int y, float surfaceTemp, ModConfig config) {
        float temp = surfaceTemp;
        if (y < config.environment.cave_start_y) {
            float caveTemp = config.environment.cave_temperature;
            if (y <= config.environment.cave_full_y) {
                temp = caveTemp;
            } else {
                float range = (float) (config.environment.cave_start_y - config.environment.cave_full_y);
                float depthFactor = (config.environment.cave_start_y - y) / range;
                depthFactor = Math.max(0.0f, Math.min(1.0f, depthFactor));
                temp = surfaceTemp + (caveTemp - surfaceTemp) * depthFactor;
            }
        } else if (y > config.environment.high_altitude_start_y) {
            float freezingTemp = config.environment.high_altitude_freezing_temperature;
            float range = (float) (config.environment.high_altitude_full_y - config.environment.high_altitude_start_y);
            float altitudeFactor = (y - config.environment.high_altitude_start_y) / range;
            altitudeFactor = Math.max(0.0f, Math.min(1.0f, altitudeFactor));
            temp = surfaceTemp + (freezingTemp - surfaceTemp) * altitudeFactor;
        }
        return temp;
    }

    private double computeTmult(float effectiveTemp, ModConfig config) {
        return (effectiveTemp >= config.environment.min_temperature_survival
                && effectiveTemp <= config.environment.max_temperature_survival) ? 1.0 : 0.0;
    }

    @ParameterizedTest(name = "Y={0}, surfaceTemp={1} -> expectedEffectiveTemp={2}, expectedTmult={3}")
    @CsvSource({
            // Deep underground (capped at cave_temperature = 0.5f)
            "-64, 0.8, 0.5, 1.0",
            "-50, 0.8, 0.5, 1.0",
            "0,   0.8, 0.5, 1.0",
            "32,  0.8, 0.5, 1.0",
            "48,  0.8, 0.5, 1.0",
            // Cave transition zone (Y=48..64)
            "56,  0.8, 0.65, 1.0",
            // Surface level (Y=64..128)
            "64,  0.8, 0.8, 1.0",
            "100, 0.8, 0.8, 1.0",
            "128, 0.8, 0.8, 1.0",
            // High altitude transition zone (Y=128..256)
            "192, 0.8, 0.15, 1.0",
            "200, 0.8, 0.06875, 0.0",
            // Freezing zone (Y >= 256, capped at -0.5f)
            "256, 0.8, -0.5, 0.0",
            "300, 0.8, -0.5, 0.0",
            "320, 0.8, -0.5, 0.0"
    })
    @DisplayName("Verify temperature scaling and Tmult across varied altitudes Y")
    public void testTemperatureAltitudeScaling(int y, float surfaceTemp, float expectedTemp, double expectedTmult) {
        ModConfig config = new ModConfig();
        float effectiveTemp = computeEffectiveTemp(y, surfaceTemp, config);
        assertEquals(expectedTemp, effectiveTemp, 0.001f, "Effective temperature at Y=" + y + " should match expected");

        double tmult = computeTmult(effectiveTemp, config);
        assertEquals(expectedTmult, tmult, 0.001, "Tmult at Y=" + y + " should match expected");
    }

    @ParameterizedTest(name = "temp={0} -> expectedTmult={1}")
    @CsvSource({
            "-1.00, 0.0",
            "-0.50, 0.0",
            " 0.00, 0.0",
            " 0.14, 0.0",
            " 0.15, 1.0",
            " 0.50, 1.0",
            " 0.80, 1.0",
            " 1.50, 1.0",
            " 1.51, 0.0",
            " 2.00, 0.0"
    })
    @DisplayName("Verify temperature survival bounds [min_temperature_survival, max_temperature_survival]")
    public void testTemperatureSurvivalBounds(float temp, double expectedTmult) {
        ModConfig config = new ModConfig();
        double tmult = computeTmult(temp, config);
        assertEquals(expectedTmult, tmult, 0.001, "Tmult for temperature=" + temp + " should match expected");
    }
}
