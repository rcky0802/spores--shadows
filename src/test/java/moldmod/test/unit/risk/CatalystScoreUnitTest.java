package moldmod.test.unit.risk;

import moldmod.config.ModConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CatalystScoreUnitTest {

    @Test
    @DisplayName("Verify default catalyst bonus configurations are positive and properly tiered")
    public void testCatalystBonusDefaults() {
        ModConfig.Catalysts catalysts = new ModConfig.Catalysts();

        assertTrue(catalysts.spore_blossom_bonus > 0.0, "Spore blossom bonus must be positive");
        assertTrue(catalysts.mud_bonus > 0.0, "Mud bonus must be positive");
        assertTrue(catalysts.podzol_mycelium_bonus > 0.0, "Podzol/Mycelium bonus must be positive");
        assertTrue(catalysts.fungi_bonus > 0.0, "Fungi bonus must be positive");

        // Spore blossom should be a strong catalyst
        assertTrue(catalysts.spore_blossom_bonus >= catalysts.mud_bonus,
                "Spore blossom should provide higher or equal bonus compared to mud");
    }
}
