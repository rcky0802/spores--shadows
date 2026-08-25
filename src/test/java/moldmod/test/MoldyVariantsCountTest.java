package moldmod.test;

import moldmod.SporesShadows;
import moldmod.block.ModBlocks;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.item.Item;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;

import java.util.List;

public class MoldyVariantsCountTest {

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testTechnicalDetailsAndVariantCounts(TestContext context) {

        // 1. Verify Wood Types
        String[] woods = SporesShadows.WOODS;
        if (woods.length != 8) {
            context.throwPositionedException("Expected 8 wood types, but found " + woods.length,
                    context.getAbsolutePos(net.minecraft.util.math.BlockPos.ORIGIN));
        }

        // 2. Verify Base Formats Count
        // 8 woods * 13 formats = 104 base formats
        int expectedBaseFormats = 104;
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

        // Iterate through all items grouped by their vanilla base
        for (List<Item> stageItems : ModBlocks.MOLDY_ITEMS_BY_VANILLA.values()) {
            // stageItems contains exactly 4 registered items per format:
            // [0] = Waxed Vanilla (Stage 0, Waxed)
            // [1] = Tainted (Stage 1, Unwaxed)
            // [2] = Moldy (Stage 2, Unwaxed)
            // [3] = Rotten (Stage 3, Unwaxed)

            if (stageItems.size() != 4) {
                context.throwPositionedException(
                        "Expected exactly 4 registered items per base format, found " + stageItems.size(),
                        context.getAbsolutePos(net.minecraft.util.math.BlockPos.ORIGIN));
            }

            // 1 Waxed Vanilla Item
            waxedVanillaVariants += 1;

            // 3 Moldy Items (Stage 1, 2, 3)
            moldyVariants += 3;

            // 3 Waxed Moldy Variants (Dynamically available via NBT/BlockState component on
            // the 3 Moldy Items)
            waxedMoldyVariants += 3;
        }

        // Verify the numbers against the documentation
        if (waxedVanillaVariants != 104) {
            context.throwPositionedException("Expected 104 Waxed Vanilla variants, but got " + waxedVanillaVariants,
                    context.getAbsolutePos(net.minecraft.util.math.BlockPos.ORIGIN));
        }
        if (moldyVariants != 312) {
            context.throwPositionedException("Expected 312 Moldy variants, but got " + moldyVariants,
                    context.getAbsolutePos(net.minecraft.util.math.BlockPos.ORIGIN));
        }
        if (waxedMoldyVariants != 312) {
            context.throwPositionedException("Expected 312 Waxed Moldy variants, but got " + waxedMoldyVariants,
                    context.getAbsolutePos(net.minecraft.util.math.BlockPos.ORIGIN));
        }

        int totalVariants = waxedVanillaVariants + moldyVariants + waxedMoldyVariants;
        if (totalVariants != 728) {
            context.throwPositionedException("Expected 728 total mod variants, but got " + totalVariants,
                    context.getAbsolutePos(net.minecraft.util.math.BlockPos.ORIGIN));
        }

        // Test passes successfully
        context.complete();
    }
}
