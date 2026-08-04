package moldmod.client.datagen;

import moldmod.SporesShadows;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends FabricRecipeProvider {

    public ModRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        String[] woods = {"oak", "spruce", "birch", "jungle", "acacia", "dark_oak", "mangrove", "cherry", "bamboo"};

        for (String wood : woods) {
            boolean isBamboo = wood.equals("bamboo");
            String logName = isBamboo ? "bamboo_block" : wood + "_log";
            String woodName = isBamboo ? null : wood + "_wood";
            String prefix = isBamboo ? "bamboo" : wood;

            // Generate Planks Recipes
            generatePlanksRecipe(exporter, logName, prefix + "_planks", false);
            generatePlanksRecipe(exporter, "stripped_" + logName, prefix + "_planks", false);
            if (woodName != null) {
                generatePlanksRecipe(exporter, woodName, prefix + "_planks", false);
                generatePlanksRecipe(exporter, "stripped_" + woodName, prefix + "_planks", false);
            }
        }
    }

    private void generatePlanksRecipe(RecipeExporter exporter, String sourceBase, String destBase, boolean isBamboo) {
        Item vanillaDest = Registries.ITEM.get(net.minecraft.util.Identifier.of("minecraft", destBase));

        // Stage 1: Tainted (Half yield: 2 instead of 4)
        Item taintedSource = Registries.ITEM.get(SporesShadows.id("tainted_" + sourceBase));
        
        ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, vanillaDest, 2)
                .input(taintedSource)
                .criterion(hasItem(taintedSource), conditionsFromItem(taintedSource))
                .offerTo(exporter, SporesShadows.id(destBase + "_from_tainted_" + sourceBase));

        // Stage 2: Moldy (Quarter yield: 1 instead of 4)
        Item moldySource = Registries.ITEM.get(SporesShadows.id("moldy_" + sourceBase));

        ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, vanillaDest, 1)
                .input(moldySource)
                .criterion(hasItem(moldySource), conditionsFromItem(moldySource))
                .offerTo(exporter, SporesShadows.id(destBase + "_from_moldy_" + sourceBase));

        // Stage 3 is uncraftable, so no recipe.
    }
}
