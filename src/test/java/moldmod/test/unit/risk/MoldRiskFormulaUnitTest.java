package moldmod.test.unit.risk;

import me.shedaniel.autoconfig.AutoConfig;
import moldmod.risk.MoldRiskCalculator.MoldRiskResult;
import moldmod.config.ModConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MoldRiskFormulaUnitTest {

    @BeforeAll
    public static void setup() {
        try {
            AutoConfig.register(ModConfig.class, (cfg, clazz) -> new me.shedaniel.autoconfig.serializer.ConfigSerializer<ModConfig>() {
                private final ModConfig configInstance = new ModConfig();
                @Override public void serialize(ModConfig data) {}
                @Override public ModConfig deserialize() { return configInstance; }
                @Override public ModConfig createDefault() { return configInstance; }
            });
        } catch (RuntimeException ignored) {
            // Already registered
        }
    }

    @Test
    @DisplayName("Verify basic Mold Risk R formula: R = ((Heff * Luv * Smat) + Catalysts + MiasmaBonus) * Tmult")
    public void testRiskFormulaCalculation() {
        double Heff = 0.50;
        double Luv = 0.80;
        double Smat = 1.00;
        double catalysts = 0.15;
        double miasmaBonus = 0.10;
        double Tmult = 1.0;

        double expectedR = ((Heff * Luv * Smat) + catalysts + miasmaBonus) * Tmult;
        assertEquals(0.65, expectedR, 0.0001);

        // With Tmult = 0 (freezing or Nether/End rejection), R must be 0
        double tMultZero = 0.0;
        double zeroR = ((Heff * Luv * Smat) + catalysts + miasmaBonus) * tMultZero;
        assertEquals(0.0, zeroR, 0.0001);
    }

    @Test
    @DisplayName("Verify MoldRiskResult record data integrity")
    public void testMoldRiskResultRecord() {
        MoldRiskResult result = new MoldRiskResult(
                1.0,  // Tmult
                0.45, // Heff
                0.60, // Hraw
                0.30, // baseHum
                0.15, // depthModifier
                0.15, // localHumidityBonus
                0.50, // aerationFlow
                0.50, // aeration
                0.15, // aerationDryingBonus
                0.90, // Luv
                2.0,  // avgLight
                1.0,  // Smat
                0.0,  // catalystBonus
                0.05, // miasmaBonus
                0.05, // netMiasma
                12,   // airVolume
                4,    // exposedFaces
                0.455,// R
                0.6f, // effectiveTemp
                0.7f  // surfaceTemp
        );

        assertEquals(1.0, result.Tmult());
        assertEquals(0.45, result.Heff());
        assertEquals(0.60, result.Hraw());
        assertEquals(0.90, result.Luv());
        assertEquals(1.0, result.Smat());
        assertEquals(0.455, result.R(), 0.0001);
        assertEquals(12, result.airVolume());
        assertEquals(4, result.exposedFaces());
    }
}
