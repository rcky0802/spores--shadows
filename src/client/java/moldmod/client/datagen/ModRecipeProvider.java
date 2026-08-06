package moldmod.client.datagen;

import moldmod.SporesShadows;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
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
            String logName = wood + "_log";
            String woodName = wood + "_wood";
            String prefix = wood;

            // Generate Planks Recipes
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
        
        // Log -> Wood Recipe Generation
        boolean isLogToWood = false;
        Item vanillaWood = null;
        if (sourceBase.endsWith("_log") && !sourceBase.startsWith("stripped_")) {
            isLogToWood = true;
            vanillaWood = Registries.ITEM.get(net.minecraft.util.Identifier.of("minecraft", sourceBase.replace("_log", "_wood")));
        } else if (sourceBase.startsWith("stripped_") && sourceBase.endsWith("_log")) {
            isLogToWood = true;
            vanillaWood = Registries.ITEM.get(net.minecraft.util.Identifier.of("minecraft", sourceBase.replace("_log", "_wood")));
        }

        Item taintedSource = Registries.ITEM.get(SporesShadows.id("tainted_" + sourceBase));
        if (taintedSource != Items.AIR) {
            ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, vanillaDest, 2)
                    .input(taintedSource)
                    .criterion(hasItem(taintedSource), conditionsFromItem(taintedSource))
                    .offerTo(exporter, SporesShadows.id(destBase + "_from_tainted_" + sourceBase));
                    
            if (isLogToWood && vanillaWood != Items.AIR) {
                ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, vanillaWood, 1)
                    .pattern("##")
                    .pattern("##")
                    .input('#', taintedSource)
                    .criterion(hasItem(taintedSource), conditionsFromItem(taintedSource))
                    .offerTo(exporter, SporesShadows.id(net.minecraft.registry.Registries.ITEM.getId(vanillaWood).getPath() + "_from_tainted_" + sourceBase));
            }
        }

        Item moldySource = Registries.ITEM.get(SporesShadows.id("moldy_" + sourceBase));
        if (moldySource != Items.AIR) {
            ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, vanillaDest, 1)
                    .input(moldySource)
                    .criterion(hasItem(moldySource), conditionsFromItem(moldySource))
                    .offerTo(exporter, SporesShadows.id(destBase + "_from_moldy_" + sourceBase));
                    
            // Moldy log -> wood (3 / 4 = 0), so NO RECIPE
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
        Item moldyPlanks = Registries.ITEM.get(SporesShadows.id("moldy_" + prefix + "_planks"));
        
        // --- STAGE 1 (Tainted) ---
        if (taintedPlanks != Items.AIR) {
            // Sticks (2 planks -> 2)
            ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, sticks, 2)
                .pattern("#")
                .pattern("#")
                .input('#', taintedPlanks)
                .criterion(hasItem(taintedPlanks), conditionsFromItem(taintedPlanks))
                .offerTo(exporter, SporesShadows.id("sticks_from_tainted_" + prefix + "_planks"));

            // Slab (3 planks -> 3)
            ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, vanillaSlab, 3)
                .pattern("###")
                .input('#', taintedPlanks)
                .criterion(hasItem(taintedPlanks), conditionsFromItem(taintedPlanks))
                .offerTo(exporter, SporesShadows.id(prefix + "_slab_from_tainted"));
            
            // Stairs (6 planks -> 2)
            ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, vanillaStairs, 2)
                .pattern("#  ")
                .pattern("## ")
                .pattern("###")
                .input('#', taintedPlanks)
                .criterion(hasItem(taintedPlanks), conditionsFromItem(taintedPlanks))
                .offerTo(exporter, SporesShadows.id(prefix + "_stairs_from_tainted"));
                
            // Door (6 planks -> 1)
            ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, vanillaDoor, 1)
                .pattern("##")
                .pattern("##")
                .pattern("##")
                .input('#', taintedPlanks)
                .criterion(hasItem(taintedPlanks), conditionsFromItem(taintedPlanks))
                .offerTo(exporter, SporesShadows.id(prefix + "_door_from_tainted"));
                
            // Trapdoor (6 planks -> 1)
            ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, vanillaTrapdoor, 1)
                .pattern("###")
                .pattern("###")
                .input('#', taintedPlanks)
                .criterion(hasItem(taintedPlanks), conditionsFromItem(taintedPlanks))
                .offerTo(exporter, SporesShadows.id(prefix + "_trapdoor_from_tainted"));
                
            // Fence (4 planks, 2 sticks -> 1)
            ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, vanillaFence, 1)
                .pattern("#|#")
                .pattern("#|#")
                .input('#', taintedPlanks)
                .input('|', sticks)
                .criterion(hasItem(taintedPlanks), conditionsFromItem(taintedPlanks))
                .offerTo(exporter, SporesShadows.id(prefix + "_fence_from_tainted"));
                
            // Gate (2 planks, 4 sticks -> 0, so no recipe for Stage 1)
        }
        
        // --- STAGE 2 (Moldy) ---
        if (moldyPlanks != Items.AIR) {
            // Sticks (2 planks -> 1)
            ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, sticks, 1)
                .pattern("#")
                .pattern("#")
                .input('#', moldyPlanks)
                .criterion(hasItem(moldyPlanks), conditionsFromItem(moldyPlanks))
                .offerTo(exporter, SporesShadows.id("sticks_from_moldy_" + prefix + "_planks"));

            // Slab (3 planks -> 1)
            ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, vanillaSlab, 1)
                .pattern("###")
                .input('#', moldyPlanks)
                .criterion(hasItem(moldyPlanks), conditionsFromItem(moldyPlanks))
                .offerTo(exporter, SporesShadows.id(prefix + "_slab_from_moldy"));
            
            // Stairs (6 planks -> 1)
            ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, vanillaStairs, 1)
                .pattern("#  ")
                .pattern("## ")
                .pattern("###")
                .input('#', moldyPlanks)
                .criterion(hasItem(moldyPlanks), conditionsFromItem(moldyPlanks))
                .offerTo(exporter, SporesShadows.id(prefix + "_stairs_from_moldy"));
                
            // Door (6 planks -> 0, so no recipe)
            // Trapdoor (6 planks -> 0, so no recipe)
            // Fence (4 planks, 2 sticks -> 0, so no recipe)
            // Gate (2 planks, 4 sticks -> 0, so no recipe)
        }
    }
}
