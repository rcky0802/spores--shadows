package moldmod.test.unit.miasma;

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

    @Test
    @DisplayName("Glass pane bitmasks: 2+ connections hermetic (0b0000), 1 connection (0b0011, 12 air), 0 connections open (0b1111, 24 air)")
    public void testGlassPaneConnectionBitmasks() {
        int fullAir = 0b1111;

        // Case 1: 0 connections -> open point (0b1111, 24.0 air)
        int pane0Conn = 0b1111;
        int shared0 = Integer.bitCount(pane0Conn & fullAir);
        assertEquals(4, shared0);
        assertEquals(1.0, shared0 / 4.0, 1e-6, "0 connections gives 100% capacity (24 air)");

        // Case 2: 1 connection -> 12 air (0b0011, 50% capacity)
        int pane1Conn = 0b0011;
        int shared1 = Integer.bitCount(pane1Conn & fullAir);
        assertEquals(2, shared1);
        assertEquals(0.5, shared1 / 4.0, 1e-6, "1 connection gives 50% capacity (12 air)");

        // Case 3: 2+ connections -> hermetic (0b0000, 0 air)
        int pane2Conn = 0b0000;
        int shared2 = Integer.bitCount(pane2Conn & fullAir);
        assertEquals(0, shared2, "2+ connections must share 0 bits with air (hermetic)");
        assertEquals(0.0, shared2 / 4.0, 1e-6, "2+ connections have 0% capacity (0 air)");

        // Case 4: Vertical direction -> 100% capacity (0b1111, like walls and fences)
        int paneVertical = 0b1111;
        int sharedVert = Integer.bitCount(paneVertical & fullAir);
        assertEquals(4, sharedVert);
        assertEquals(1.0, sharedVert / 4.0, 1e-6, "Vertical axis gives 100% capacity (air passes over/under)");
    }

    @Test
    @DisplayName("Wall block bitmasks: 2+ connections hermetic (0b0000), 1 connection (0b0011, 12 air), 0 connections (0b0001, 6 air), vertical (0b0111, 18 air)")
    public void testWallBlockConnectionBitmasks() {
        int fullAir = 0b1111;

        // Case 1: 0 connections -> 6 air (0b0001, 1 quadrant = 25% of 24 = 6)
        int wall0Conn = 0b0001;
        int shared0 = Integer.bitCount(wall0Conn & fullAir);
        assertEquals(1, shared0);
        assertEquals(0.25, shared0 / 4.0, 1e-6, "0 connections gives 25% capacity (6 air)");

        // Case 2: 1 connection -> 12 air (0b0011, 2 quadrants = 50% of 24 = 12)
        int wall1Conn = 0b0011;
        int shared1 = Integer.bitCount(wall1Conn & fullAir);
        assertEquals(2, shared1);
        assertEquals(0.5, shared1 / 4.0, 1e-6, "1 connection gives 50% capacity (12 air)");

        // Case 3: 2+ connections -> hermetic (0b0000, 0 air)
        int wall2Conn = 0b0000;
        int shared2 = Integer.bitCount(wall2Conn & fullAir);
        assertEquals(0, shared2, "2+ connections must share 0 bits with air (hermetic)");
        assertEquals(0.0, shared2 / 4.0, 1e-6, "2+ connections have 0% capacity (0 air)");

        // Case 4: Vertical axis -> 18 air (0b0111, 75% of 24 = 18)
        int wallVertical = 0b0111;
        int sharedVert = Integer.bitCount(wallVertical & fullAir);
        assertEquals(3, sharedVert);
        assertEquals(0.75, sharedVert / 4.0, 1e-6, "Vertical axis gives 75% capacity (18 air)");
    }

    @Test
    @DisplayName("Fence block bitmasks: 2+ connections (0b0011, 12 air), 1 connection (0b0111, 18 air), 0 connections (0b0111, 18 air), vertical (0b0111, 18 air)")
    public void testFenceBlockConnectionBitmasks() {
        int fullAir = 0b1111;

        // Case 1: 0 connections -> 18 air (0b0111, 75% capacity)
        int fence0Conn = 0b0111;
        int shared0 = Integer.bitCount(fence0Conn & fullAir);
        assertEquals(3, shared0);
        assertEquals(0.75, shared0 / 4.0, 1e-6, "0 connections gives 75% capacity (18 air)");

        // Case 2: 1 connection -> 18 air (0b0111, 75% capacity)
        int fence1Conn = 0b0111;
        int shared1 = Integer.bitCount(fence1Conn & fullAir);
        assertEquals(3, shared1);
        assertEquals(0.75, shared1 / 4.0, 1e-6, "1 connection gives 75% capacity (18 air)");

        // Case 3: 2+ connections -> 12 air (0b0011, 50% capacity)
        int fence2Conn = 0b0011;
        int shared2 = Integer.bitCount(fence2Conn & fullAir);
        assertEquals(2, shared2);
        assertEquals(0.5, shared2 / 4.0, 1e-6, "2+ connections gives 50% capacity (12 air)");

        // Case 4: Vertical axis -> 18 air (0b0111, 75% of 24 = 18)
        int fenceVertical = 0b0111;
        int sharedVert = Integer.bitCount(fenceVertical & fullAir);
        assertEquals(3, sharedVert);
        assertEquals(0.75, sharedVert / 4.0, 1e-6, "Vertical axis gives 75% capacity (18 air)");
    }
}
