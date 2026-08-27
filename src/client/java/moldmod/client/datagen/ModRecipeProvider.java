package moldmod.client.datagen;

import moldmod.SporesShadows;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
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
        String[] woods = moldmod.SporesShadows.WOODS;

        for (String wood : woods) {
            String logName = (wood.equals("crimson") || wood.equals("warped")) ? wood + "_stem" : wood + "_log"; String woodName = (wood.equals("crimson") || wood.equals("warped")) ? wood + "_hyphae" : wood + "_wood";
            String prefix = wood;
            
            // Planks from Logs / Wood
            generatePlanksRecipe(exporter, logName, prefix + "_planks");
            generatePlanksRecipe(exporter, "stripped_" + logName, prefix + "_planks");
            
            if (woodName != null) {
                generatePlanksRecipe(exporter, woodName, prefix + "_planks");
                generatePlanksRecipe(exporter, "stripped_" + woodName, prefix + "_planks");
            }
            
            // Processed Block Recipes
            generateProcessedRecipes(exporter, prefix);
        }
    }

    private void generatePlanksRecipe(RecipeExporter exporter, String sourceBase, String destBase) {
        Item vanillaDest = Registries.ITEM.get(net.minecraft.util.Identifier.of("minecraft", destBase));
        
        boolean isLogToWood = false;
        Item vanillaWood = null;
        if ((sourceBase.endsWith("_log") || sourceBase.endsWith("_stem")) && !sourceBase.startsWith("stripped_")) {
            isLogToWood = true;
            vanillaWood = Registries.ITEM.get(net.minecraft.util.Identifier.of("minecraft", sourceBase.replace("_log", "_wood").replace("_stem", "_hyphae")));
        } else if (sourceBase.startsWith("stripped_") && (sourceBase.endsWith("_log") || sourceBase.endsWith("_stem"))) {
            isLogToWood = true;
            vanillaWood = Registries.ITEM.get(net.minecraft.util.Identifier.of("minecraft", sourceBase.replace("_log", "_wood").replace("_stem", "_hyphae")));
        }

        Item taintedSource = Registries.ITEM.get(SporesShadows.id("tainted_" + sourceBase));
        Item waxedTaintedSource = Registries.ITEM.get(SporesShadows.id("waxed_tainted_" + sourceBase));
        if (taintedSource != Items.AIR && waxedTaintedSource != Items.AIR) {
            Ingredient taintedIngredient = Ingredient.ofItems(taintedSource, waxedTaintedSource);
            ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, vanillaDest, 2)
                    .input(taintedIngredient)
                    .criterion("has_tainted", conditionsFromItem(taintedSource))
                    .criterion("has_waxed_tainted", conditionsFromItem(waxedTaintedSource))
                    .offerTo(exporter, SporesShadows.id(destBase + "_from_tainted_" + sourceBase));
                    
            if (isLogToWood && vanillaWood != Items.AIR) {
                ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, vanillaWood, 1)
                    .pattern("##")
                    .pattern("##")
                    .input('#', taintedIngredient)
                    .criterion("has_tainted", conditionsFromItem(taintedSource))
                    .criterion("has_waxed_tainted", conditionsFromItem(waxedTaintedSource))
                    .offerTo(exporter, SporesShadows.id(net.minecraft.registry.Registries.ITEM.getId(vanillaWood).getPath() + "_from_tainted_" + sourceBase));
            }
        }

        Item moldySource = Registries.ITEM.get(SporesShadows.id("moldy_" + sourceBase));
        Item waxedMoldySource = Registries.ITEM.get(SporesShadows.id("waxed_moldy_" + sourceBase));
        if (moldySource != Items.AIR && waxedMoldySource != Items.AIR) {
            Ingredient moldyIngredient = Ingredient.ofItems(moldySource, waxedMoldySource);
            ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, vanillaDest, 1)
                    .input(moldyIngredient)
                    .criterion("has_moldy", conditionsFromItem(moldySource))
                    .criterion("has_waxed_moldy", conditionsFromItem(waxedMoldySource))
                    .offerTo(exporter, SporesShadows.id(destBase + "_from_moldy_" + sourceBase));
        }
    }
    
    private void generateProcessedRecipes(RecipeExporter exporter, String prefix) {
        Item vanillaSlab = Registries.ITEM.get(net.minecraft.util.Identifier.of("minecraft", prefix + "_slab"));
        Item vanillaStairs = Registries.ITEM.get(net.minecraft.util.Identifier.of("minecraft", prefix + "_stairs"));
        Item vanillaFence = Registries.ITEM.get(net.minecraft.util.Identifier.of("minecraft", prefix + "_fence"));
        Item vanillaDoor = Registries.ITEM.get(net.minecraft.util.Identifier.of("minecraft", prefix + "_door"));
        Item vanillaTrapdoor = Registries.ITEM.get(net.minecraft.util.Identifier.of("minecraft", prefix + "_trapdoor"));
        Item sticks = Items.STICK;
        
        Item taintedPlanks = Registries.ITEM.get(SporesShadows.id("tainted_" + prefix + "_planks"));
        Item waxedTaintedPlanks = Registries.ITEM.get(SporesShadows.id("waxed_tainted_" + prefix + "_planks"));
        
        Item moldyPlanks = Registries.ITEM.get(SporesShadows.id("moldy_" + prefix + "_planks"));
        Item waxedMoldyPlanks = Registries.ITEM.get(SporesShadows.id("waxed_moldy_" + prefix + "_planks"));
        
        // --- STAGE 1 (Tainted) ---
        if (taintedPlanks != Items.AIR && waxedTaintedPlanks != Items.AIR) {
            Ingredient taintedIngredient = Ingredient.ofItems(taintedPlanks, waxedTaintedPlanks);
            
            ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, sticks, 2).pattern("#").pattern("#").input('#', taintedIngredient)
                .criterion("has_tainted", conditionsFromItem(taintedPlanks)).criterion("has_waxed_tainted", conditionsFromItem(waxedTaintedPlanks))
                .offerTo(exporter, SporesShadows.id("sticks_from_tainted_" + prefix + "_planks"));

            ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, vanillaSlab, 3).pattern("###").input('#', taintedIngredient)
                .criterion("has_tainted", conditionsFromItem(taintedPlanks)).criterion("has_waxed_tainted", conditionsFromItem(waxedTaintedPlanks))
                .offerTo(exporter, SporesShadows.id(prefix + "_slab_from_tainted"));
            
            ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, vanillaStairs, 2).pattern("#  ").pattern("## ").pattern("###").input('#', taintedIngredient)
                .criterion("has_tainted", conditionsFromItem(taintedPlanks)).criterion("has_waxed_tainted", conditionsFromItem(waxedTaintedPlanks))
                .offerTo(exporter, SporesShadows.id(prefix + "_stairs_from_tainted"));
                
            ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, vanillaDoor, 1).pattern("##").pattern("##").pattern("##").input('#', taintedIngredient)
                .criterion("has_tainted", conditionsFromItem(taintedPlanks)).criterion("has_waxed_tainted", conditionsFromItem(waxedTaintedPlanks))
                .offerTo(exporter, SporesShadows.id(prefix + "_door_from_tainted"));
                
            ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, vanillaTrapdoor, 1).pattern("###").pattern("###").input('#', taintedIngredient)
                .criterion("has_tainted", conditionsFromItem(taintedPlanks)).criterion("has_waxed_tainted", conditionsFromItem(waxedTaintedPlanks))
                .offerTo(exporter, SporesShadows.id(prefix + "_trapdoor_from_tainted"));
                
            ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, vanillaFence, 1).pattern("#|#").pattern("#|#").input('#', taintedIngredient).input('|', sticks)
                .criterion("has_tainted", conditionsFromItem(taintedPlanks)).criterion("has_waxed_tainted", conditionsFromItem(waxedTaintedPlanks))
                .offerTo(exporter, SporesShadows.id(prefix + "_fence_from_tainted"));
        }
        
        // --- STAGE 2 (Moldy) ---
        if (moldyPlanks != Items.AIR && waxedMoldyPlanks != Items.AIR) {
            Ingredient moldyIngredient = Ingredient.ofItems(moldyPlanks, waxedMoldyPlanks);
            
            ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, sticks, 1).pattern("#").pattern("#").input('#', moldyIngredient)
                .criterion("has_moldy", conditionsFromItem(moldyPlanks)).criterion("has_waxed_moldy", conditionsFromItem(waxedMoldyPlanks))
                .offerTo(exporter, SporesShadows.id("sticks_from_moldy_" + prefix + "_planks"));

            ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, vanillaSlab, 1).pattern("###").input('#', moldyIngredient)
                .criterion("has_moldy", conditionsFromItem(moldyPlanks)).criterion("has_waxed_moldy", conditionsFromItem(waxedMoldyPlanks))
                .offerTo(exporter, SporesShadows.id(prefix + "_slab_from_moldy"));
            
            ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, vanillaStairs, 1).pattern("#  ").pattern("## ").pattern("###").input('#', moldyIngredient)
                .criterion("has_moldy", conditionsFromItem(moldyPlanks)).criterion("has_waxed_moldy", conditionsFromItem(waxedMoldyPlanks))
                .offerTo(exporter, SporesShadows.id(prefix + "_stairs_from_moldy"));
        }
    }
}
