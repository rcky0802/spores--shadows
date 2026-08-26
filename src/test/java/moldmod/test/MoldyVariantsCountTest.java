package moldmod.test;

import moldmod.SporesShadows;
import moldmod.block.ModBlocks;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.item.Item;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;

public class MoldyVariantsCountTest {

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testTechnicalDetailsAndVariantCounts(TestContext context) {

        // 1. Verify Wood Types
        String[] woods = SporesShadows.WOODS;
        if (woods.length != 10) {
            context.throwPositionedException("Expected 10 wood types, but found " + woods.length,
                    context.getAbsolutePos(net.minecraft.util.math.BlockPos.ORIGIN));
        }

        // 2. Verify Base Formats Count
        // 10 woods * 13 formats = 130 base formats.
        int expectedBaseFormats = 130;
        int actualBaseFormats = ModBlocks.VANILLA_TO_MOLDY.size();
        if (actualBaseFormats != expectedBaseFormats) {
            context.throwPositionedException(
                    "Expected " + expectedBaseFormats + " base formats mapped, but found " + actualBaseFormats,
                    context.getAbsolutePos(net.minecraft.util.math.BlockPos.ORIGIN));
        }

        // 3. Count Registered Items and Available Variants
        int waxedVanillaVariants = 0;
        int moldyVariants = 0;
        int waxedMoldyVariants = 0;

        for (Item item : net.minecraft.registry.Registries.ITEM) {
            net.minecraft.util.Identifier id = net.minecraft.registry.Registries.ITEM.getId(item);
            if (!id.getNamespace().equals("spores--shadows")) continue;
            
            String name = id.getPath();
            if (name.startsWith("waxed_") && !name.contains("moldy") && !name.contains("tainted") && !name.contains("rotten")) {
                waxedVanillaVariants++;
            } else if (name.startsWith("moldy_") || name.startsWith("tainted_") || name.startsWith("rotten_")) {
                moldyVariants++;
            } else if (name.startsWith("waxed_moldy_") || name.startsWith("waxed_tainted_") || name.startsWith("waxed_rotten_")) {
                waxedMoldyVariants++;
            }
        }

        if (waxedVanillaVariants != 130) {
            context.throwPositionedException("Expected 130 Waxed Vanilla variants, but got " + waxedVanillaVariants,
                    context.getAbsolutePos(net.minecraft.util.math.BlockPos.ORIGIN));
        }
        if (moldyVariants != 390) {
            context.throwPositionedException("Expected 390 Moldy variants, but got " + moldyVariants,
                    context.getAbsolutePos(net.minecraft.util.math.BlockPos.ORIGIN));
        }
        if (waxedMoldyVariants != 0) {
            context.throwPositionedException("Expected 0 separate Waxed Moldy item registrations (they use NBT), but got " + waxedMoldyVariants,
                    context.getAbsolutePos(net.minecraft.util.math.BlockPos.ORIGIN));
        }

        int totalVariants = waxedVanillaVariants + moldyVariants + waxedMoldyVariants;
        if (totalVariants != 520) {
            context.throwPositionedException(
                    "Expected exactly 520 total unique items, but counted " + totalVariants,
                    context.getAbsolutePos(net.minecraft.util.math.BlockPos.ORIGIN));
        }

        // Test passes successfully
        context.complete();
    }
}
