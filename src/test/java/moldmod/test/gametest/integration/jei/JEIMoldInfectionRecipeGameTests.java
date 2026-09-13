package moldmod.test.gametest.integration.jei;

import moldmod.block.ModBlocks;
import moldmod.client.integration.jei.MoldInfectionRecipe;
import moldmod.client.integration.jei.MoldInfectionRecipeCategory;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JEIMoldInfectionRecipeGameTests {

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testMoldInfectionRecipeType(TestContext context) {
        context.assertTrue(MoldInfectionRecipeCategory.RECIPE_TYPE != null,
                "MoldInfectionRecipeCategory.RECIPE_TYPE must not be null");
        context.assertTrue(MoldInfectionRecipeCategory.RECIPE_TYPE.getUid().getPath().equals("mold_infection"),
                "RecipeType UID path must be 'mold_infection'");
        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testMoldInfectionRecipeGeneration(TestContext context) {
        List<MoldInfectionRecipe> recipes = new ArrayList<>();

        Text thresholdText = Text.translatable("jei.spores--shadows.infection.threshold", 50);
        List<Text> commonTooltip = List.of(
                Text.translatable("jei.spores--shadows.infection.tooltip.title"),
                Text.translatable("jei.spores--shadows.infection.tooltip.condition", 50)
        );

        Text stage0To1 = Text.translatable("jei.spores--shadows.infection.stage_0_to_1");
        Text stage1To2 = Text.translatable("jei.spores--shadows.infection.stage_1_to_2");
        Text stage2To3 = Text.translatable("jei.spores--shadows.infection.stage_2_to_3");

        int woodFamilyCount = 0;
        for (Map.Entry<Item, List<Item>> entry : ModBlocks.MOLDY_ITEMS_BY_VANILLA.entrySet()) {
            Item itemVanilla = entry.getKey();
            List<Item> items = entry.getValue();
            if (items == null || items.size() < 7) continue;

            woodFamilyCount++;
            Item itemTainted = items.get(1);
            Item itemMoldy = items.get(3);
            Item itemRotten = items.get(5);

            // Clean -> Tainted (Stage 0 -> 1)
            recipes.add(new MoldInfectionRecipe(
                    new ItemStack(itemVanilla), new ItemStack(itemTainted),
                    0, 1, stage0To1, thresholdText, commonTooltip
            ));
            // Tainted -> Moldy (Stage 1 -> 2)
            recipes.add(new MoldInfectionRecipe(
                    new ItemStack(itemTainted), new ItemStack(itemMoldy),
                    1, 2, stage1To2, thresholdText, commonTooltip
            ));
            // Moldy -> Rotten (Stage 2 -> 3)
            recipes.add(new MoldInfectionRecipe(
                    new ItemStack(itemMoldy), new ItemStack(itemRotten),
                    2, 3, stage2To3, thresholdText, commonTooltip
            ));
        }

        context.assertTrue(woodFamilyCount >= 10, "Must have at least 10 wood families registered");
        context.assertTrue(recipes.size() >= 30, "Must have generated at least 30 mold infection progression recipes");

        for (MoldInfectionRecipe recipe : recipes) {
            context.assertTrue(!recipe.input().isEmpty(), "Recipe input must not be empty");
            context.assertTrue(!recipe.output().isEmpty(), "Recipe output must not be empty");
            context.assertTrue(recipe.toStage() == recipe.fromStage() + 1,
                    "Target stage must be exactly one above source stage");
            context.assertTrue(!recipe.stageTransitionText().getString().isEmpty(),
                    "Transition text must not be empty");
            context.assertTrue(!recipe.thresholdText().getString().isEmpty(),
                    "Threshold text must not be empty");
            context.assertTrue(!recipe.tooltipLines().isEmpty(),
                    "Tooltip lines must not be empty");
        }

        context.complete();
    }
}
