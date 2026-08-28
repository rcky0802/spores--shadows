package moldmod.test;

import moldmod.SporesShadows;
import moldmod.SporesShadowsConstants;
import moldmod.config.ModConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class MoldyFuelAndSmeltingTests {

    // ============================================
    // === CHARCOAL SMELTING TESTS ===
    // ============================================

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testCharcoalSmeltingForLogsAcrossStages(TestContext context) {
        RecipeManager recipeManager = context.getWorld().getRecipeManager();
        RegistryWrapper.WrapperLookup registries = context.getWorld().getRegistryManager();

        for (SporesShadowsConstants.MoldyWoodType woodType : SporesShadowsConstants.WOOD_TYPES) {
            String logName = woodType.getLogName();

            Item vanillaLog = Registries.ITEM.get(Identifier.of("minecraft", logName));
            Item waxedLog = Registries.ITEM.get(SporesShadows.id("waxed_" + logName));
            Item taintedLog = Registries.ITEM.get(SporesShadows.id("tainted_" + logName));
            Item moldyLog = Registries.ITEM.get(SporesShadows.id("moldy_" + logName));
            Item rottenLog = Registries.ITEM.get(SporesShadows.id("rotten_" + logName));

            Item waxedTaintedLog = Registries.ITEM.get(SporesShadows.id("waxed_tainted_" + logName));
            Item waxedMoldyLog = Registries.ITEM.get(SporesShadows.id("waxed_moldy_" + logName));
            Item waxedRottenLog = Registries.ITEM.get(SporesShadows.id("waxed_rotten_" + logName));

            if (!woodType.isNether()) {
                // Overworld woods: Stage 0 (vanilla & waxed) MUST produce charcoal
                assertCanSmeltToCharcoal(context, recipeManager, registries, vanillaLog, "Vanilla Log " + logName);
                assertCanSmeltToCharcoal(context, recipeManager, registries, waxedLog, "Waxed Log " + logName);
            } else {
                // Nether woods: Cannot produce charcoal
                assertCannotSmeltToCharcoal(context, recipeManager, registries, vanillaLog, "Nether Vanilla Stem " + logName);
                assertCannotSmeltToCharcoal(context, recipeManager, registries, waxedLog, "Nether Waxed Stem " + logName);
            }

            // Infected logs (Stages 1, 2, 3 - unwaxed & waxed) must NOT produce charcoal
            assertCannotSmeltToCharcoal(context, recipeManager, registries, taintedLog, "Tainted Log " + logName);
            assertCannotSmeltToCharcoal(context, recipeManager, registries, moldyLog, "Moldy Log " + logName);
            assertCannotSmeltToCharcoal(context, recipeManager, registries, rottenLog, "Rotten Log " + logName);

            assertCannotSmeltToCharcoal(context, recipeManager, registries, waxedTaintedLog, "Waxed Tainted Log " + logName);
            assertCannotSmeltToCharcoal(context, recipeManager, registries, waxedMoldyLog, "Waxed Moldy Log " + logName);
            assertCannotSmeltToCharcoal(context, recipeManager, registries, waxedRottenLog, "Waxed Rotten Log " + logName);
        }

        context.complete();
    }

    private void assertCanSmeltToCharcoal(TestContext context, RecipeManager recipeManager,
            RegistryWrapper.WrapperLookup registries, Item item, String description) {
        if (item == Items.AIR) {
            context.throwPositionedException(description + " is AIR!", new BlockPos(0, 0, 0));
        }
        var match = recipeManager.getFirstMatch(RecipeType.SMELTING,
                new SingleStackRecipeInput(new ItemStack(item)), context.getWorld());
        if (match.isEmpty()) {
            context.throwPositionedException(
                    "Expected " + description + " to smelt into Charcoal, but no smelting recipe was found!",
                    new BlockPos(0, 0, 0));
        }
        ItemStack result = match.get().value().getResult(registries);
        if (!result.isOf(Items.CHARCOAL)) {
            context.throwPositionedException(
                    "Expected " + description + " to smelt into Charcoal, but got: " + result.getItem(),
                    new BlockPos(0, 0, 0));
        }
    }

    private void assertCannotSmeltToCharcoal(TestContext context, RecipeManager recipeManager,
            RegistryWrapper.WrapperLookup registries, Item item, String description) {
        if (item == Items.AIR)
            return;
        var match = recipeManager.getFirstMatch(RecipeType.SMELTING,
                new SingleStackRecipeInput(new ItemStack(item)), context.getWorld());
        if (match.isPresent()) {
            ItemStack result = match.get().value().getResult(registries);
            if (result.isOf(Items.CHARCOAL)) {
                context.throwPositionedException(
                        "Infected / non-fuel log " + description + " should NOT smelt into Charcoal!",
                        new BlockPos(0, 0, 0));
            }
        }
    }

    // ============================================
    // === FURNACE FUEL SCALING TESTS ===
    // ============================================

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testFuelScaling(TestContext context) {
        ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();

        for (var product : MoldyWoodTestHelper.getAllWoodProducts()) {
            String baseName = product.baseName();
            int baseFuel = product.baseFuel();
            boolean isNether = product.woodType().isNether();

            Item tainted = Registries.ITEM.get(SporesShadows.id("tainted_" + baseName));
            Item moldy = Registries.ITEM.get(SporesShadows.id("moldy_" + baseName));
            Item rotten = Registries.ITEM.get(SporesShadows.id("rotten_" + baseName));
            Item waxed = Registries.ITEM.get(SporesShadows.id("waxed_" + baseName));
            Item waxedTainted = Registries.ITEM.get(SporesShadows.id("waxed_tainted_" + baseName));
            Item waxedMoldy = Registries.ITEM.get(SporesShadows.id("waxed_moldy_" + baseName));
            Item waxedRotten = Registries.ITEM.get(SporesShadows.id("waxed_rotten_" + baseName));

            if (isNether) {
                // Nether wood products MUST NEVER be used as fuel in any stage
                assertFuelTime(context, tainted, 0, "Nether Tainted " + baseName);
                assertFuelTime(context, moldy, 0, "Nether Moldy " + baseName);
                assertFuelTime(context, rotten, 0, "Nether Rotten " + baseName);
                assertFuelTime(context, waxed, 0, "Nether Waxed " + baseName);
                assertFuelTime(context, waxedTainted, 0, "Nether Waxed Tainted " + baseName);
                assertFuelTime(context, waxedMoldy, 0, "Nether Waxed Moldy " + baseName);
                assertFuelTime(context, waxedRotten, 0, "Nether Waxed Rotten " + baseName);
            } else {
                // Overworld wood: scaled fuel according to config percentages
                int expectedTainted = Math.max(37, (int) (baseFuel * config.furnaceMultipliers.stage_1));
                int expectedMoldy = Math.max(37, (int) (baseFuel * config.furnaceMultipliers.stage_2));
                int expectedRotten = Math.max(37, (int) (baseFuel * config.furnaceMultipliers.stage_3));
                int expectedWaxed = Math.max(0, (int) (baseFuel * config.furnaceMultipliers.stage_0));

                assertFuelTime(context, tainted, expectedTainted, "Tainted " + baseName);
                assertFuelTime(context, moldy, expectedMoldy, "Moldy " + baseName);
                assertFuelTime(context, rotten, expectedRotten, "Rotten " + baseName);

                assertFuelTime(context, waxed, expectedWaxed, "Waxed " + baseName);
                assertFuelTime(context, waxedTainted, expectedTainted, "Waxed Tainted " + baseName);
                assertFuelTime(context, waxedMoldy, expectedMoldy, "Waxed Moldy " + baseName);
                assertFuelTime(context, waxedRotten, expectedRotten, "Waxed Rotten " + baseName);
            }
        }

        context.complete();
    }

    private void assertFuelTime(TestContext context, Item item, int expectedFuel, String description) {
        if (item == Items.AIR) {
            context.throwPositionedException(description + " is AIR!", new BlockPos(0, 0, 0));
        }
        Integer actualFuel = FuelRegistry.INSTANCE.get(item);
        int fuel = actualFuel != null ? actualFuel : 0;
        if (fuel != expectedFuel) {
            context.throwPositionedException(
                    "Fuel mismatch for " + description + ": expected=" + expectedFuel + " got=" + fuel,
                    new BlockPos(0, 0, 0));
        }
    }
}
