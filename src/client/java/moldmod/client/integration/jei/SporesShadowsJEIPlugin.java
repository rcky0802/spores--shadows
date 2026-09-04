package moldmod.client.integration.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.recipe.vanilla.IJeiAnvilRecipe;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import moldmod.SporesShadows;
import moldmod.block.ModBlocks;
import moldmod.item.ModItems;
import moldmod.registry.ModEnchantments;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@JeiPlugin
@Environment(EnvType.CLIENT)
public class SporesShadowsJEIPlugin implements IModPlugin {

    public static final Identifier PLUGIN_ID = SporesShadows.id("jei_plugin");

    @Override
    public Identifier getPluginUid() {
        return PLUGIN_ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        var guiHelper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(
                new WaxingRecipeCategory(guiHelper),
                new ScrapingRecipeCategory(guiHelper)
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        List<WaxingRecipe> waxingRecipes = new ArrayList<>();
        List<ScrapingRecipe> scrapingRecipes = new ArrayList<>();
        List<ItemStack> rottenStacks = new ArrayList<>();

        for (Map.Entry<Item, List<Item>> entry : ModBlocks.MOLDY_ITEMS_BY_VANILLA.entrySet()) {
            Item itemVanilla = entry.getKey();
            List<Item> items = entry.getValue();
            if (items == null || items.size() < 7) continue;

            Item itemWaxed0 = items.get(0);
            Item itemTainted = items.get(1);
            Item itemWaxedTainted = items.get(2);
            Item itemMoldy = items.get(3);
            Item itemWaxedMoldy = items.get(4);
            Item itemRotten = items.get(5);
            Item itemWaxedRotten = items.get(6);

            // 1. Waxing Recipes (Shift + Right Click with Honeycomb)
            waxingRecipes.add(new WaxingRecipe(new ItemStack(itemVanilla), new ItemStack(itemWaxed0)));
            waxingRecipes.add(new WaxingRecipe(new ItemStack(itemTainted), new ItemStack(itemWaxedTainted)));
            waxingRecipes.add(new WaxingRecipe(new ItemStack(itemMoldy), new ItemStack(itemWaxedMoldy)));
            waxingRecipes.add(new WaxingRecipe(new ItemStack(itemRotten), new ItemStack(itemWaxedRotten)));

            // 2. Scraping Recipes (Shift + Right Click with Axe)
            // A) De-waxing
            scrapingRecipes.add(new ScrapingRecipe(new ItemStack(itemWaxed0), new ItemStack(itemVanilla)));
            scrapingRecipes.add(new ScrapingRecipe(new ItemStack(itemWaxedTainted), new ItemStack(itemTainted)));
            scrapingRecipes.add(new ScrapingRecipe(new ItemStack(itemWaxedMoldy), new ItemStack(itemMoldy)));
            scrapingRecipes.add(new ScrapingRecipe(new ItemStack(itemWaxedRotten), new ItemStack(itemRotten)));

            // B) De-molding (Cure mold)
            scrapingRecipes.add(new ScrapingRecipe(new ItemStack(itemMoldy), new ItemStack(itemTainted)));
            scrapingRecipes.add(new ScrapingRecipe(new ItemStack(itemTainted), new ItemStack(itemVanilla)));

            // 3. Rotten items info
            rottenStacks.add(new ItemStack(itemRotten));
            rottenStacks.add(new ItemStack(itemWaxedRotten));
        }

        registration.addRecipes(WaxingRecipeCategory.RECIPE_TYPE, waxingRecipes);
        registration.addRecipes(ScrapingRecipeCategory.RECIPE_TYPE, scrapingRecipes);

        if (!rottenStacks.isEmpty()) {
            registration.addIngredientInfo(rottenStacks, VanillaTypes.ITEM_STACK, Text.translatable("jei." + SporesShadows.MOD_ID + ".info.rotten_wood"));
        }

        // 4. Spore Mask Info, Spore Detector Info & Spore Filtration Info on Enchanted Books
        registration.addIngredientInfo(new ItemStack(ModItems.SPORE_MASK), VanillaTypes.ITEM_STACK, Text.translatable("jei." + SporesShadows.MOD_ID + ".info.spore_mask"));
        registration.addIngredientInfo(new ItemStack(ModItems.SPORE_DETECTOR), VanillaTypes.ITEM_STACK, Text.translatable("jei." + SporesShadows.MOD_ID + ".info.spore_detector"));

        var client = MinecraftClient.getInstance();
        var registryManager = (client != null && client.world != null) ? client.world.getRegistryManager() : null;
        
        List<ItemStack> sporeFiltrationBooks = new ArrayList<>();
        if (registryManager != null) {
            var regOpt = registryManager.getOptional(RegistryKeys.ENCHANTMENT);
            if (regOpt.isPresent()) {
                var entryOpt = regOpt.get().getEntry(ModEnchantments.SPORE_FILTRATION);
                if (entryOpt.isPresent()) {
                    for (int lvl = 1; lvl <= 3; lvl++) {
                        sporeFiltrationBooks.add(EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(entryOpt.get(), lvl)));
                    }
                }
            }
        }
        if (!sporeFiltrationBooks.isEmpty()) {
            registration.addIngredientInfo(sporeFiltrationBooks, VanillaTypes.ITEM_STACK, Text.translatable("jei." + SporesShadows.MOD_ID + ".info.spore_filtration"));
        }

        // 5. Anvil Recipes
        var vanillaRecipes = registration.getVanillaRecipeFactory();
        List<IJeiAnvilRecipe> anvilRecipes = new ArrayList<>();

        // A) Spore Mask Filter Repair with Wool
        ItemStack damagedMask = new ItemStack(ModItems.SPORE_MASK);
        damagedMask.setDamage(80);
        ItemStack repairedMask = new ItemStack(ModItems.SPORE_MASK);
        repairedMask.setDamage(0);

        List<ItemStack> woolList = List.of(
            new ItemStack(Items.WHITE_WOOL),
            new ItemStack(Items.ORANGE_WOOL),
            new ItemStack(Items.MAGENTA_WOOL),
            new ItemStack(Items.LIGHT_BLUE_WOOL),
            new ItemStack(Items.YELLOW_WOOL),
            new ItemStack(Items.LIME_WOOL),
            new ItemStack(Items.PINK_WOOL),
            new ItemStack(Items.GRAY_WOOL),
            new ItemStack(Items.LIGHT_GRAY_WOOL),
            new ItemStack(Items.CYAN_WOOL),
            new ItemStack(Items.PURPLE_WOOL),
            new ItemStack(Items.BLUE_WOOL),
            new ItemStack(Items.BROWN_WOOL),
            new ItemStack(Items.GREEN_WOOL),
            new ItemStack(Items.RED_WOOL),
            new ItemStack(Items.BLACK_WOOL)
        );

        anvilRecipes.add(vanillaRecipes.createAnvilRecipe(
            List.of(damagedMask),
            woolList,
            List.of(repairedMask),
            SporesShadows.id("anvil/spore_mask_repair")
        ));

        // B) Spore Mask Allowed Enchantments (Unbreaking, Mending, Curse of Vanishing)
        if (registryManager != null) {
            var regOpt = registryManager.getOptional(RegistryKeys.ENCHANTMENT);
            if (regOpt.isPresent()) {
                var enchRegistry = regOpt.get();
                
                // Unbreaking III
                enchRegistry.getEntry(Enchantments.UNBREAKING).ifPresent(unbEntry -> {
                    ItemStack book = EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(unbEntry, 3));
                    ItemStack mask = new ItemStack(ModItems.SPORE_MASK);
                    ItemStack out = new ItemStack(ModItems.SPORE_MASK);
                    out.addEnchantment(unbEntry, 3);
                    anvilRecipes.add(vanillaRecipes.createAnvilRecipe(List.of(mask), List.of(book), List.of(out), SporesShadows.id("anvil/spore_mask_unbreaking")));
                });

                // Mending
                enchRegistry.getEntry(Enchantments.MENDING).ifPresent(mendingEntry -> {
                    ItemStack book = EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(mendingEntry, 1));
                    ItemStack mask = new ItemStack(ModItems.SPORE_MASK);
                    ItemStack out = new ItemStack(ModItems.SPORE_MASK);
                    out.addEnchantment(mendingEntry, 1);
                    anvilRecipes.add(vanillaRecipes.createAnvilRecipe(List.of(mask), List.of(book), List.of(out), SporesShadows.id("anvil/spore_mask_mending")));
                });

                // Curse of Vanishing
                enchRegistry.getEntry(Enchantments.VANISHING_CURSE).ifPresent(vanishEntry -> {
                    ItemStack book = EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(vanishEntry, 1));
                    ItemStack mask = new ItemStack(ModItems.SPORE_MASK);
                    ItemStack out = new ItemStack(ModItems.SPORE_MASK);
                    out.addEnchantment(vanishEntry, 1);
                    anvilRecipes.add(vanillaRecipes.createAnvilRecipe(List.of(mask), List.of(book), List.of(out), SporesShadows.id("anvil/spore_mask_vanishing")));
                });

                // Spore Filtration III on Helmets
                enchRegistry.getEntry(ModEnchantments.SPORE_FILTRATION).ifPresent(filtEntry -> {
                    ItemStack book = EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(filtEntry, 3));
                    for (Item helmetItem : List.of(Items.DIAMOND_HELMET, Items.NETHERITE_HELMET, Items.IRON_HELMET)) {
                        ItemStack helm = new ItemStack(helmetItem);
                        ItemStack out = new ItemStack(helmetItem);
                        out.addEnchantment(filtEntry, 3);
                        anvilRecipes.add(vanillaRecipes.createAnvilRecipe(
                            List.of(helm),
                            List.of(book),
                            List.of(out),
                            SporesShadows.id("anvil/spore_filtration_" + Registries.ITEM.getId(helmetItem).getPath())
                        ));
                    }
                });
            }
        }

        registration.addRecipes(RecipeTypes.ANVIL, anvilRecipes);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(Items.HONEYCOMB), WaxingRecipeCategory.RECIPE_TYPE);

        registration.addRecipeCatalyst(new ItemStack(Items.WOODEN_AXE), ScrapingRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(Items.STONE_AXE), ScrapingRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(Items.IRON_AXE), ScrapingRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(Items.GOLDEN_AXE), ScrapingRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(Items.DIAMOND_AXE), ScrapingRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(Items.NETHERITE_AXE), ScrapingRecipeCategory.RECIPE_TYPE);
    }
}
