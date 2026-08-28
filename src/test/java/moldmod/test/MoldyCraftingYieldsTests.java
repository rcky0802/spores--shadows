package moldmod.test;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;

public class MoldyCraftingYieldsTests {

    // ============================================
    // === CRAFTING YIELDS TESTS ===
    // ============================================

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testCraftingYields(TestContext context) {
        context.complete();
    }
}
