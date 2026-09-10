package moldmod.test.unit.device;

import moldmod.block.MoistureDetectorLogic;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MoistureDetectorUnitTest {

    @Test
    @DisplayName("Verify visual stage mapping from effective humidity (0 to 3)")
    void testMoistureStageMapping() {
        // Stage 0: Dry (Heff < 0.25)
        assertEquals(0, MoistureDetectorLogic.getMoistureStage(0.0));
        assertEquals(0, MoistureDetectorLogic.getMoistureStage(0.24));

        // Stage 1: Moderate (0.25 <= Heff < 0.50)
        assertEquals(1, MoistureDetectorLogic.getMoistureStage(0.25));
        assertEquals(1, MoistureDetectorLogic.getMoistureStage(0.49));

        // Stage 2: Humid (0.50 <= Heff < 0.75)
        assertEquals(2, MoistureDetectorLogic.getMoistureStage(0.50));
        assertEquals(2, MoistureDetectorLogic.getMoistureStage(0.74));

        // Stage 3: Critical (Heff >= 0.75)
        assertEquals(3, MoistureDetectorLogic.getMoistureStage(0.75));
        assertEquals(3, MoistureDetectorLogic.getMoistureStage(1.0));
        assertEquals(3, MoistureDetectorLogic.getMoistureStage(1.2));
    }
}
