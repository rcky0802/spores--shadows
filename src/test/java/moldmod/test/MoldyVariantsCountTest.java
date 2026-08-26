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
        if (woods.length != 11) {
            context.throwPositionedException("Expected 11 wood types, but found " + woods.length,
                    context.getAbsolutePos(net.minecraft.util.math.BlockPos.ORIGIN));
        }

        // 2. Verify Base Formats Count
        // 8 normal + 2 nether = 10 * 13 = 130 base formats
        // 1 bamboo = 11 base formats (no wood, stripped_wood)
        // Total = 141
        int expectedBaseFormats = 141;
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
            if (stageItems.size() != 4) {
                context.throwPositionedException(
                        "Expected exactly 4 registered items per base format, found " + stageItems.size(),
                        context.getAbsolutePos(net.minecraft.util.math.BlockPos.ORIGIN));
            }

            waxedVanillaVariants += 1;
            moldyVariants += 3;
            waxedMoldyVariants += 3;
        }

        // Verify the numbers against the documentation
        if (waxedVanillaVariants != 141) {
            context.throwPositionedException("Expected 141 Waxed Vanilla variants, but got " + waxedVanillaVariants,
                    context.getAbsolutePos(net.minecraft.util.math.BlockPos.ORIGIN));
        }
        if (moldyVariants != 423) {
            context.throwPositionedException("Expected 423 Moldy variants, but got " + moldyVariants,
                    context.getAbsolutePos(net.minecraft.util.math.BlockPos.ORIGIN));
        }
        if (waxedMoldyVariants != 423) {
            context.throwPositionedException("Expected 423 Waxed Moldy variants, but got " + waxedMoldyVariants,
                    context.getAbsolutePos(net.minecraft.util.math.BlockPos.ORIGIN));
        }

        int totalVariants = waxedVanillaVariants + moldyVariants + waxedMoldyVariants;
        if (totalVariants != 987) {
            context.throwPositionedException("Expected 987 total mod variants, but got " + totalVariants,
                    context.getAbsolutePos(net.minecraft.util.math.BlockPos.ORIGIN));
        }

        // Test passes successfully
        context.complete();
    }
}
