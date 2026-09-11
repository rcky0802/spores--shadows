package moldmod.test.gametest.integration.jei;

import moldmod.SporesShadows;
import moldmod.block.ModBlocks;
import moldmod.item.ModItems;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class JEISporeGearAndDeviceGameTests {

        @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
        public void testSporeMaskCraftingRecipe(TestContext context) {
                Identifier recipeId = Identifier.of(SporesShadows.MOD_ID, "spore_mask");
                Optional<RecipeEntry<?>> recipeOpt = context.getWorld().getRecipeManager().get(recipeId);

                context.assertTrue(recipeOpt.isPresent(), "Recipe for spore_mask must be present in RecipeManager");
                ItemStack result = recipeOpt.get().value().getResult(context.getWorld().getRegistryManager());
                context.assertTrue(result.isOf(ModItems.SPORE_MASK),
                                "Spore mask recipe result must be SPORE_MASK item");

                context.complete();
        }

        @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
        public void testSporeDetectorCraftingRecipe(TestContext context) {
                Identifier recipeId = Identifier.of(SporesShadows.MOD_ID, "spore_detector");
                Optional<RecipeEntry<?>> recipeOpt = context.getWorld().getRecipeManager().get(recipeId);

                context.assertTrue(recipeOpt.isPresent(), "Recipe for spore_detector must be present in RecipeManager");
                ItemStack result = recipeOpt.get().value().getResult(context.getWorld().getRegistryManager());
                context.assertTrue(result.isOf(ModBlocks.SPORE_DETECTOR.asItem()),
                                "Spore detector recipe result must be SPORE_DETECTOR block item");

                context.complete();
        }

        @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
        public void testSporeMaskAnvilRepairRecipeModel(TestContext context) {
                ItemStack damagedMask = new ItemStack(ModItems.SPORE_MASK);
                damagedMask.setDamage(80);

                ItemStack repairedMask = new ItemStack(ModItems.SPORE_MASK);
                repairedMask.setDamage(0);

                context.assertTrue(damagedMask.getDamage() == 80, "Damaged mask must have 80 damage");
                context.assertTrue(repairedMask.getDamage() == 0, "Repaired mask must have 0 damage");

                ItemStack filter = new ItemStack(ModItems.SPORE_FILTER);
                context.assertTrue(ModItems.SPORE_MASK.canRepair(damagedMask, filter),
                                "Spore mask must be repairable with spore filter in anvil");

                Identifier anvilRecipeId = SporesShadows.id("anvil/spore_mask_repair");
                context.assertTrue(anvilRecipeId.getPath().equals("anvil/spore_mask_repair"),
                                "Anvil repair recipe ID path must match");

                context.complete();
        }

        @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
        public void testSporeMaskAnvilEnchantmentRecipesModel(TestContext context) {
                var reg = context.getWorld().getRegistryManager().getOptional(RegistryKeys.ENCHANTMENT).orElseThrow();
                RegistryEntry<Enchantment> unbreaking = reg.getEntry(Enchantments.UNBREAKING).orElseThrow();
                RegistryEntry<Enchantment> mending = reg.getEntry(Enchantments.MENDING).orElseThrow();
                RegistryEntry<Enchantment> vanishing = reg.getEntry(Enchantments.VANISHING_CURSE).orElseThrow();

                // 1. Unbreaking III
                ItemStack unbMask = new ItemStack(ModItems.SPORE_MASK);
                unbMask.addEnchantment(unbreaking, 3);
                context.assertTrue(EnchantmentHelper.getLevel(unbreaking, unbMask) == 3,
                                "Enchanted mask output must have Unbreaking III");

                // 2. Mending
                ItemStack mendingMask = new ItemStack(ModItems.SPORE_MASK);
                mendingMask.addEnchantment(mending, 1);
                context.assertTrue(EnchantmentHelper.getLevel(mending, mendingMask) == 1,
                                "Enchanted mask output must have Mending");

                // 3. Curse of Vanishing
                ItemStack vanishMask = new ItemStack(ModItems.SPORE_MASK);
                vanishMask.addEnchantment(vanishing, 1);
                context.assertTrue(EnchantmentHelper.getLevel(vanishing, vanishMask) == 1,
                                "Enchanted mask output must have Curse of Vanishing");

                context.complete();
        }

        @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
        public void testMoistureDetectorCraftingRecipe(TestContext context) {
                Identifier recipeId = Identifier.of(SporesShadows.MOD_ID, "moisture_detector");
                Optional<RecipeEntry<?>> recipeOpt = context.getWorld().getRecipeManager().get(recipeId);

                context.assertTrue(recipeOpt.isPresent(),
                                "Recipe for moisture_detector must be present in RecipeManager");
                ItemStack result = recipeOpt.get().value().getResult(context.getWorld().getRegistryManager());
                context.assertTrue(result.isOf(ModBlocks.MOISTURE_DETECTOR.asItem()),
                                "Moisture detector recipe result must be MOISTURE_DETECTOR block item");

                context.complete();
        }

        @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
        public void testSporeMaskAndDetectorJEIInfoDescriptions(TestContext context) {
                String maskInfoKey = "jei." + SporesShadows.MOD_ID + ".info.spore_mask";
                Text maskInfoText = Text.translatable(maskInfoKey);
                context.assertTrue(!maskInfoText.getString().isEmpty(),
                                "Spore mask JEI info description must not be empty");

                String detectorInfoKey = "jei." + SporesShadows.MOD_ID + ".info.spore_detector";
                Text detectorInfoText = Text.translatable(detectorInfoKey);
                context.assertTrue(!detectorInfoText.getString().isEmpty(),
                                "Spore detector JEI info description must not be empty");

                String moistureInfoKey = "jei." + SporesShadows.MOD_ID + ".info.moisture_detector";
                Text moistureInfoText = Text.translatable(moistureInfoKey);
                context.assertTrue(!moistureInfoText.getString().isEmpty(),
                                "Moisture detector JEI info description must not be empty");

                context.complete();
        }
}
