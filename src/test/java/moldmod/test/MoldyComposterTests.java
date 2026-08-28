package moldmod.test;

import moldmod.SporesShadows;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.fabricmc.fabric.api.registry.CompostingChanceRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public class MoldyComposterTests {

    // ============================================
    // === COMPOSTER PROBABILITY TESTS ===
    // ============================================

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testComposterProbabilities(TestContext context) {
        for (var product : MoldyWoodTestHelper.getAllWoodProducts()) {
            String baseName = product.baseName();

            Item tainted = Registries.ITEM.get(SporesShadows.id("tainted_" + baseName));
            Item moldy = Registries.ITEM.get(SporesShadows.id("moldy_" + baseName));
            Item rotten = Registries.ITEM.get(SporesShadows.id("rotten_" + baseName));

            Item waxedTainted = Registries.ITEM.get(SporesShadows.id("waxed_tainted_" + baseName));
            Item waxedMoldy = Registries.ITEM.get(SporesShadows.id("waxed_moldy_" + baseName));
            Item waxedRotten = Registries.ITEM.get(SporesShadows.id("waxed_rotten_" + baseName));

            // Stage 1 (Tainted & Waxed Tainted) -> 0.50f (50%)
            assertCompostChance(context, tainted, 0.50f, "Tainted " + baseName);
            assertCompostChance(context, waxedTainted, 0.50f, "Waxed Tainted " + baseName);

            // Stage 2 (Moldy & Waxed Moldy) -> 0.65f (65%)
            assertCompostChance(context, moldy, 0.65f, "Moldy " + baseName);
            assertCompostChance(context, waxedMoldy, 0.65f, "Waxed Moldy " + baseName);

            // Stage 3 (Rotten & Waxed Rotten) -> 0.85f (85%)
            assertCompostChance(context, rotten, 0.85f, "Rotten " + baseName);
            assertCompostChance(context, waxedRotten, 0.85f, "Waxed Rotten " + baseName);
        }

        context.complete();
    }

    private void assertCompostChance(TestContext context, Item item, float expectedChance, String description) {
        if (item == Items.AIR) {
            context.throwPositionedException(description + " is AIR!", new BlockPos(0, 0, 0));
        }
        float actual = CompostingChanceRegistry.INSTANCE.get(item);
        if (Math.abs(actual - expectedChance) > 1e-4f) {
            context.throwPositionedException(
                    "Compost chance mismatch for " + description + ": expected=" + expectedChance + " got=" + actual,
                    new BlockPos(0, 0, 0));
        }
    }
}
