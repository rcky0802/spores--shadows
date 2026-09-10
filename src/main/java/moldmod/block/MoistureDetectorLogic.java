package moldmod.block;

/**
 * Pure mathematical logic and mapping utilities for the Moisture Detector device.
 * Decoupled from Minecraft block class loading so it can be verified in lightweight unit tests.
 */
public final class MoistureDetectorLogic {

    private MoistureDetectorLogic() {
    }

    /**
     * Maps effective humidity (Heff) to one of 4 visual indicator stages:
     * - Stage 0: Dry (Heff < 0.25)
     * - Stage 1: Moderate (0.25 <= Heff < 0.50)
     * - Stage 2: Humid / Warning (0.50 <= Heff < 0.75)
     * - Stage 3: Critical / Saturation (Heff >= 0.75)
     */
    public static int getMoistureStage(double heff) {
        if (heff < 0.25) {
            return 0;
        } else if (heff < 0.50) {
            return 1;
        } else if (heff < 0.75) {
            return 2;
        } else {
            return 3;
        }
    }
}
