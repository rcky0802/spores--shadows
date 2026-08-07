package moldmod.test;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.item.Items;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public class MoldyCraftingAndFuelTests {

    // ============================================
    // === FUEL SCALING TESTS ===
    // ============================================

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testFuelScaling(TestContext context) {
        Integer vanillaTime = net.fabricmc.fabric.api.registry.FuelRegistry.INSTANCE.get(Items.OAK_LOG);
        // If vanilla is not registered directly, we assume 300 (vanilla standard for
        // logs/planks)
        int baseTime = (vanillaTime != null && vanillaTime > 0) ? vanillaTime : 300;

        Integer taintedTime = net.fabricmc.fabric.api.registry.FuelRegistry.INSTANCE
                .get(net.minecraft.registry.Registries.ITEM.get(moldmod.SporesShadows.id("tainted_oak_log")));
        Integer moldyTime = net.fabricmc.fabric.api.registry.FuelRegistry.INSTANCE
                .get(net.minecraft.registry.Registries.ITEM.get(moldmod.SporesShadows.id("moldy_oak_log")));
        Integer rottenTime = net.fabricmc.fabric.api.registry.FuelRegistry.INSTANCE
                .get(net.minecraft.registry.Registries.ITEM.get(moldmod.SporesShadows.id("rotten_oak_log")));

        int expectedTainted = baseTime / 2; // 50%
        int expectedMoldy = baseTime / 4; // 25%
        int expectedRotten = Math.max(1, baseTime / 8); // 12.5%, min 1

        if (taintedTime == null || taintedTime != expectedTainted) {
            context.throwPositionedException(
                    "Tainted fuel time incorrect! Expected " + expectedTainted + " got " + taintedTime,
                    new BlockPos(0, 0, 0));
        }
        if (moldyTime == null || moldyTime != expectedMoldy) {
            context.throwPositionedException(
                    "Moldy fuel time incorrect! Expected " + expectedMoldy + " got " + moldyTime,
                    new BlockPos(0, 0, 0));
        }
        if (rottenTime == null || rottenTime != expectedRotten) {
            context.throwPositionedException(
                    "Rotten fuel time incorrect! Expected " + expectedRotten + " got " + rottenTime,
                    new BlockPos(0, 0, 0));
        }

        context.complete();
    }

}
