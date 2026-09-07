package moldmod.test.unit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GeometryBitmaskUnitTest {

    @Test
    @DisplayName("Fully open masks (0b1111) connect with 100% capacity (4 shared bits)")
    public void testFullOpenMaskConnection() {
        int fullOpen1 = 0b1111;
        int fullOpen2 = 0b1111;

        int sharedBits = Integer.bitCount(fullOpen1 & fullOpen2);
        assertEquals(4, sharedBits);
        assertEquals(1.0, sharedBits / 4.0, 1e-6, "Two fully open faces have 1.0 (100%) capacity");
    }

    @Test
    @DisplayName("Bottom Slab (0b0011) and Top Slab (0b1100) horizontal alignment logic")
    public void testSlabBitmaskAlignments() {
        int bottomSlabSide = 0b0011; // Upper half open
        int topSlabSide = 0b1100;    // Lower half open

        // Bottom next to Bottom: shares upper 2 quadrants (50% capacity)
        int sharedBB = Integer.bitCount(bottomSlabSide & bottomSlabSide);
        assertEquals(2, sharedBB);
        assertEquals(0.5, sharedBB / 4.0, 1e-6);

        // Top next to Top: shares lower 2 quadrants (50% capacity)
        int sharedTT = Integer.bitCount(topSlabSide & topSlabSide);
        assertEquals(2, sharedTT);
        assertEquals(0.5, sharedTT / 4.0, 1e-6);

        // Bottom next to Top: 0b0011 & 0b1100 = 0 (0% capacity, completely blocked)
        int sharedBT = Integer.bitCount(bottomSlabSide & topSlabSide);
        assertEquals(0, sharedBT, "Opposite half slabs must have 0 shared bits");
        assertEquals(0.0, sharedBT / 4.0, 1e-6);
    }

    @Test
    @DisplayName("Vertical Slab to Air connections")
    public void testSlabVerticalAirConnections() {
        int bottomSlabUp = 0b1111;
        int bottomSlabDown = 0b0000;
        int airFace = 0b1111;

        // Upwards connection from bottom slab to air above is 100%
        assertEquals(4, Integer.bitCount(bottomSlabUp & airFace));
        assertEquals(1.0, Integer.bitCount(bottomSlabUp & airFace) / 4.0, 1e-6);

        // Downwards connection from bottom slab to block below is 0% (solid bottom)
        assertEquals(0, Integer.bitCount(bottomSlabDown & airFace));
        assertEquals(0.0, Integer.bitCount(bottomSlabDown & airFace) / 4.0, 1e-6);
    }

    @Test
    @DisplayName("Stair step 2-quadrant half-open mask logic")
    public void testStairStepHalfOpenLogic() {
        int stairStepFront = 0b0011; // 2 quadrants open
        int fullAir = 0b1111;
        int solidWall = 0b0000;

        assertEquals(2, Integer.bitCount(stairStepFront & fullAir));
        assertEquals(0.5, Integer.bitCount(stairStepFront & fullAir) / 4.0, 1e-6, "Step front connects to air with 50% capacity");

        assertEquals(0, Integer.bitCount(stairStepFront & solidWall));
        assertEquals(0.0, Integer.bitCount(stairStepFront & solidWall) / 4.0, 1e-6);
    }

    @Test
    @DisplayName("Quarter-opening (1 quadrant open, 0b0001) gives 25% flow capacity")
    public void testQuarterOpeningCapacity() {
        int singleQuadrant = 0b0001;
        int fullAir = 0b1111;

        int shared = Integer.bitCount(singleQuadrant & fullAir);
        assertEquals(1, shared);
        assertEquals(0.25, shared / 4.0, 1e-6, "1 quadrant gives 25% base capacity");
    }

    @Test
    @DisplayName("Three-quarter opening (3 quadrants open, 0b0111) gives 75% flow capacity")
    public void testThreeQuarterOpeningCapacity() {
        int threeQuadrants = 0b0111;
        int fullAir = 0b1111;

        int shared = Integer.bitCount(threeQuadrants & fullAir);
        assertEquals(3, shared);
        assertEquals(0.75, shared / 4.0, 1e-6, "3 quadrants give 75% base capacity");
    }
}
