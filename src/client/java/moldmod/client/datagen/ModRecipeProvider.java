package moldmod.client.datagen;

import moldmod.SporesShadows;
import moldmod.SporesShadowsConstants;
import moldmod.SporesShadowsConstants.MoldyWoodType;
import moldmod.item.ModItems;
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
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        for (MoldyWoodType woodTypeObj : SporesShadowsConstants.WOOD_TYPES) {
            String wood = woodTypeObj.name();
            String logName = woodTypeObj.getLogName();
            String woodName = woodTypeObj.getWoodName();
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

        // Equipment Recipes
        generateEquipmentRecipes(exporter);
    }

    private void generateEquipmentRecipes(RecipeExporter exporter) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, ModItems.SPORE_MASK)
                .pattern("LGL")
                .pattern("CWC")
                .pattern(" H ")
                .input('L', Items.LEATHER)
                .input('G', Items.GLASS_PANE)
                .input('C', Items.COPPER_INGOT)
                .input('W', ItemTags.WOOL)
                .input('H', Items.HONEYCOMB)
                .criterion("has_leather", conditionsFromItem(Items.LEATHER))
                .criterion("has_honeycomb", conditionsFromItem(Items.HONEYCOMB))
                .criterion("has_copper", conditionsFromItem(Items.COPPER_INGOT))
                .offerTo(exporter, SporesShadows.id("spore_mask"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, ModItems.SPORE_DETECTOR)
                .pattern(" C ")
                .pattern("RGR")
                .pattern(" C ")
                .input('C', Items.COPPER_INGOT)
                .input('G', Items.GLASS_BOTTLE)
                .input('R', Items.REDSTONE)
                .criterion("has_copper", conditionsFromItem(Items.COPPER_INGOT))
                .criterion("has_redstone", conditionsFromItem(Items.REDSTONE))
                .offerTo(exporter, SporesShadows.id("spore_detector"));
    }

    private void generatePlanksRecipe(RecipeExporter exporter, String sourceBase, String destBase) {
        Item vanillaDest = Registries.ITEM.get(Identifier.of("minecraft", destBase));
        
        boolean isLogToWood = false;
        Item vanillaWood = null;
        if (sourceBase.endsWith("_log") || sourceBase.endsWith("_stem")) {
            isLogToWood = true;
            vanillaWood = Registries.ITEM.get(Identifier.of("minecraft", sourceBase.replace("_log", "_wood").replace("_stem", "_hyphae")));
        }

        Item waxedVanillaSource = Registries.ITEM.get(SporesShadows.id("waxed_" + sourceBase));
        if (waxedVanillaSource != Items.AIR) {
            ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, vanillaDest, 4)
                    .input(waxedVanillaSource)
                    .criterion("has_waxed_vanilla", conditionsFromItem(waxedVanillaSource))
                    .offerTo(exporter, SporesShadows.id(destBase + "_from_waxed_" + sourceBase));
                    
            if (isLogToWood && vanillaWood != Items.AIR) {
                ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, vanillaWood, 3)
                    .pattern("##")
                    .pattern("##")
                    .input('#', waxedVanillaSource)
                    .criterion("has_waxed_vanilla", conditionsFromItem(waxedVanillaSource))
                    .offerTo(exporter, SporesShadows.id(Registries.ITEM.getId(vanillaWood).getPath() + "_from_waxed_" + sourceBase));
            }
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
                    .offerTo(exporter, SporesShadows.id(Registries.ITEM.getId(vanillaWood).getPath() + "_from_tainted_" + sourceBase));
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
        Item vanillaSlab = Registries.ITEM.get(Identifier.of("minecraft", prefix + "_slab"));
        Item vanillaStairs = Registries.ITEM.get(Identifier.of("minecraft", prefix + "_stairs"));
        Item vanillaFence = Registries.ITEM.get(Identifier.of("minecraft", prefix + "_fence"));
        Item vanillaDoor = Registries.ITEM.get(Identifier.of("minecraft", prefix + "_door"));
        Item vanillaTrapdoor = Registries.ITEM.get(Identifier.of("minecraft", prefix + "_trapdoor"));
        Item vanillaFenceGate = Registries.ITEM.get(Identifier.of("minecraft", prefix + "_fence_gate"));
        Item vanillaButton = Registries.ITEM.get(Identifier.of("minecraft", prefix + "_button"));
        Item vanillaPressurePlate = Registries.ITEM.get(Identifier.of("minecraft", prefix + "_pressure_plate"));
        Item vanillaSign = Registries.ITEM.get(Identifier.of("minecraft", prefix + "_sign"));
        Item vanillaBoat = Registries.ITEM.get(Identifier.of("minecraft", prefix + "_boat"));
        Item vanillaChestBoat = Registries.ITEM.get(Identifier.of("minecraft", prefix + "_chest_boat"));
        Item sticks = Items.STICK;
        
        Item waxedVanillaPlanks = Registries.ITEM.get(SporesShadows.id("waxed_" + prefix + "_planks"));
        Item vanillaPlanks = Registries.ITEM.get(Identifier.of("minecraft", prefix + "_planks"));
        
        if (waxedVanillaPlanks != Items.AIR && vanillaPlanks != Items.AIR) {
            Ingredient mixedPlanks = Ingredient.ofItems(vanillaPlanks, waxedVanillaPlanks);
            
            ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, sticks, 4).pattern("#").pattern("#").input('#', mixedPlanks)
                .criterion("has_waxed_vanilla", conditionsFromItem(waxedVanillaPlanks))
                .offerTo(exporter, SporesShadows.id("sticks_from_waxed_" + prefix + "_planks"));

            ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, vanillaSlab, 6).pattern("###").input('#', mixedPlanks)
                .criterion("has_waxed_vanilla", conditionsFromItem(waxedVanillaPlanks))
                .offerTo(exporter, SporesShadows.id(prefix + "_slab_from_waxed"));
            
            ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, vanillaStairs, 4).pattern("#  ").pattern("## ").pattern("###").input('#', mixedPlanks)
                .criterion("has_waxed_vanilla", conditionsFromItem(waxedVanillaPlanks))
                .offerTo(exporter, SporesShadows.id(prefix + "_stairs_from_waxed"));
                
            ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, vanillaDoor, 3).pattern("##").pattern("##").pattern("##").input('#', mixedPlanks)
                .criterion("has_waxed_vanilla", conditionsFromItem(waxedVanillaPlanks))
                .offerTo(exporter, SporesShadows.id(prefix + "_door_from_waxed"));
                
            ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, vanillaTrapdoor, 2).pattern("###").pattern("###").input('#', mixedPlanks)
                .criterion("has_waxed_vanilla", conditionsFromItem(waxedVanillaPlanks))
                .offerTo(exporter, SporesShadows.id(prefix + "_trapdoor_from_waxed"));
                
            ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, vanillaFence, 3).pattern("#|#").pattern("#|#").input('#', mixedPlanks).input('|', sticks)
                .criterion("has_waxed_vanilla", conditionsFromItem(waxedVanillaPlanks))
                .offerTo(exporter, SporesShadows.id(prefix + "_fence_from_waxed"));

            if (vanillaFenceGate != Items.AIR) {
                ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, vanillaFenceGate, 1).pattern("|#|").pattern("|#|").input('#', mixedPlanks).input('|', sticks)
                    .criterion("has_waxed_vanilla", conditionsFromItem(waxedVanillaPlanks))
                    .offerTo(exporter, SporesShadows.id(prefix + "_fence_gate_from_waxed"));
            }
            if (vanillaButton != Items.AIR) {
                ShapelessRecipeJsonBuilder.create(RecipeCategory.REDSTONE, vanillaButton, 1).input(mixedPlanks)
                    .criterion("has_waxed_vanilla", conditionsFromItem(waxedVanillaPlanks))
                    .offerTo(exporter, SporesShadows.id(prefix + "_button_from_waxed"));
            }
            if (vanillaPressurePlate != Items.AIR) {
                ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, vanillaPressurePlate, 1).pattern("##").input('#', mixedPlanks)
                    .criterion("has_waxed_vanilla", conditionsFromItem(waxedVanillaPlanks))
                    .offerTo(exporter, SporesShadows.id(prefix + "_pressure_plate_from_waxed"));
            }
            if (vanillaSign != Items.AIR) {
                ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, vanillaSign, 3).pattern("###").pattern("###").pattern(" | ").input('#', mixedPlanks).input('|', sticks)
                    .criterion("has_waxed_vanilla", conditionsFromItem(waxedVanillaPlanks))
                    .offerTo(exporter, SporesShadows.id(prefix + "_sign_from_waxed"));
            }
            if (vanillaBoat != Items.AIR) {
                ShapedRecipeJsonBuilder.create(RecipeCategory.TRANSPORTATION, vanillaBoat, 1).pattern("# #").pattern("###").input('#', mixedPlanks)
                    .criterion("has_waxed_vanilla", conditionsFromItem(waxedVanillaPlanks))
                    .offerTo(exporter, SporesShadows.id(prefix + "_boat_from_waxed"));
            }
            if (vanillaChestBoat != Items.AIR && vanillaBoat != Items.AIR) {
                ShapelessRecipeJsonBuilder.create(RecipeCategory.TRANSPORTATION, vanillaChestBoat, 1).input(vanillaBoat).input(Items.CHEST)
                    .criterion("has_waxed_vanilla", conditionsFromItem(waxedVanillaPlanks))
                    .offerTo(exporter, SporesShadows.id(prefix + "_chest_boat_from_waxed"));
            }
        }
        
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
