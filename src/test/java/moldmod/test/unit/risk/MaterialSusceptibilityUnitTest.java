package moldmod.test.unit.risk;

import moldmod.config.ModConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MaterialSusceptibilityUnitTest {

    @Test
    @DisplayName("Verify susceptibility default values and order: Stripped > Log > Planks")
    public void testSusceptibilityOrdering() {
        ModConfig.Susceptibility susceptibility = new ModConfig.Susceptibility();

        assertTrue(susceptibility.stripped_wood_multiplier > susceptibility.default_multiplier,
                "Stripped wood multiplier (" + susceptibility.stripped_wood_multiplier + ") must be > default log ("
                        + susceptibility.default_multiplier + ")");

        assertTrue(susceptibility.default_multiplier > susceptibility.planks_multiplier,
                "Default log multiplier (" + susceptibility.default_multiplier + ") must be > planks ("
                        + susceptibility.planks_multiplier + ")");
    }
}
