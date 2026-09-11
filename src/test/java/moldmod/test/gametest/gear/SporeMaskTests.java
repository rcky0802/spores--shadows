package moldmod.test.gametest.gear;

import me.shedaniel.autoconfig.AutoConfig;
import moldmod.config.ModConfig;
import moldmod.item.ModItems;
import moldmod.item.SporeMaskItem;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.Optional;

public class SporeMaskTests {

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testMaskProperties(TestContext context) {
        if (!(ModItems.SPORE_MASK instanceof SporeMaskItem maskItem)) {
            context.throwPositionedException("SPORE_MASK deve essere un'istanza di SporeMaskItem", BlockPos.ORIGIN);
            return;
        }

        if (maskItem.getSlotType() != EquipmentSlot.HEAD) {
            context.throwPositionedException("SPORE_MASK deve essere equipaggiabile nello slot HEAD, trovato: " + maskItem.getSlotType(), BlockPos.ORIGIN);
        }

        if (maskItem.getProtection() != 1) {
            context.throwPositionedException("SPORE_MASK deve fornire 1 punto armatura, trovato: " + maskItem.getProtection(), BlockPos.ORIGIN);
        }

        ItemStack stack = new ItemStack(ModItems.SPORE_MASK);
        if (stack.getMaxDamage() != 165) {
            context.throwPositionedException("SPORE_MASK deve avere 165 di durabilità, trovato: " + stack.getMaxDamage(), BlockPos.ORIGIN);
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testMaskFilterRepair(TestContext context) {
        ItemStack maskStack = new ItemStack(ModItems.SPORE_MASK);
        maskStack.setDamage(50);

        // Verifica che il Filtro per Spore sia accettato per la riparazione
        if (!ModItems.SPORE_MASK.canRepair(maskStack, new ItemStack(ModItems.SPORE_FILTER))) {
            context.throwPositionedException("Il Filtro per Spore deve poter riparare la Spore Mask", BlockPos.ORIGIN);
        }

        // Verifica che materiali non validi (inclusa la vecchia lana) vengano rifiutati
        if (ModItems.SPORE_MASK.canRepair(maskStack, new ItemStack(Items.WHITE_WOOL))) {
            context.throwPositionedException("La lana non deve piu' riparare direttamente la Spore Mask", BlockPos.ORIGIN);
        }
        if (ModItems.SPORE_MASK.canRepair(maskStack, new ItemStack(Items.DIRT))) {
            context.throwPositionedException("La terra non deve poter riparare la Spore Mask", BlockPos.ORIGIN);
        }
        if (ModItems.SPORE_MASK.canRepair(maskStack, new ItemStack(Items.IRON_INGOT))) {
            context.throwPositionedException("Il ferro non deve poter riparare la Spore Mask", BlockPos.ORIGIN);
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testMaskAnvilFullRepairWithFilter(TestContext context) {
        PlayerEntity player = context.createMockPlayer(net.minecraft.world.GameMode.SURVIVAL);
        player.addExperienceLevels(10);

        AnvilScreenHandler anvil = new AnvilScreenHandler(1, player.getInventory(), ScreenHandlerContext.EMPTY);

        // 1. Damaged mask (damage = 120 / 165) + 5 Spore Filters
        ItemStack damagedMask = new ItemStack(ModItems.SPORE_MASK);
        damagedMask.setDamage(120);
        damagedMask.set(net.minecraft.component.DataComponentTypes.CUSTOM_NAME, net.minecraft.text.Text.literal("My Custom Gas Mask"));

        ItemStack filterStack = new ItemStack(ModItems.SPORE_FILTER, 5);

        anvil.getSlot(0).setStack(damagedMask);
        anvil.getSlot(1).setStack(filterStack);
        anvil.setNewItemName("My Custom Gas Mask");
        anvil.updateResult();

        ItemStack output = anvil.getSlot(2).getStack();
        if (output.isEmpty()) {
            context.throwPositionedException("L'incudine deve produrre un output per maschera danneggiata + filtro", BlockPos.ORIGIN);
        }
        if (output.getDamage() != 0) {
            context.throwPositionedException("Il filtro nell'incudine deve riparare completamente (100%) la maschera a danno 0, trovato: " + output.getDamage(), BlockPos.ORIGIN);
        }
        if (!"My Custom Gas Mask".equals(output.getName().getString())) {
            context.throwPositionedException("Il nome personalizzato deve essere preservato, trovato: " + output.getName().getString(), BlockPos.ORIGIN);
        }

        // 2. Simulate player taking the repaired mask from the anvil
        anvil.getSlot(2).onTakeItem(player, output);

        ItemStack remainingFilters = anvil.getSlot(1).getStack();
        if (remainingFilters.getCount() != 4) {
            context.throwPositionedException("L'incudine deve consumare esattamente 1 filtro (rimasti: " + remainingFilters.getCount() + ")", BlockPos.ORIGIN);
        }

        // 3. Undamaged mask + filter must NOT produce an output
        ItemStack pristineMask = new ItemStack(ModItems.SPORE_MASK);
        anvil.getSlot(0).setStack(pristineMask);
        anvil.getSlot(1).setStack(new ItemStack(ModItems.SPORE_FILTER));
        anvil.updateResult();

        if (!anvil.getSlot(2).getStack().isEmpty()) {
            context.throwPositionedException("La maschera integra non deve poter essere riparata nell'incudine", BlockPos.ORIGIN);
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testCraftingGridFilterRejectedAndMaskCombiningAllowed(TestContext context) {
        ItemStack damagedMask1 = new ItemStack(ModItems.SPORE_MASK);
        damagedMask1.setDamage(120);
        ItemStack filter = new ItemStack(ModItems.SPORE_FILTER);

        // A) Crafting grid: Damaged mask + Filter -> MUST NOT MATCH ANY RECIPE
        CraftingRecipeInput filterInput = CraftingRecipeInput.create(
                2, 2, List.of(damagedMask1, filter, ItemStack.EMPTY, ItemStack.EMPTY));
        Optional<RecipeEntry<CraftingRecipe>> filterMatch = context.getWorld().getRecipeManager().getFirstMatch(RecipeType.CRAFTING, filterInput, context.getWorld());
        if (filterMatch.isPresent()) {
            context.throwPositionedException("La riparazione con filtro non deve funzionare nel banco da lavoro!", BlockPos.ORIGIN);
        }

        // B) Crafting grid: Damaged mask + Damaged mask -> MUST MATCH vanilla RepairItemRecipe (quick field repair)
        ItemStack damagedMask2 = new ItemStack(ModItems.SPORE_MASK);
        damagedMask2.setDamage(80);
        CraftingRecipeInput twoMasksInput = CraftingRecipeInput.create(
                2, 2, List.of(damagedMask1, damagedMask2, ItemStack.EMPTY, ItemStack.EMPTY));
        Optional<RecipeEntry<CraftingRecipe>> twoMasksMatch = context.getWorld().getRecipeManager().getFirstMatch(RecipeType.CRAFTING, twoMasksInput, context.getWorld());
        if (twoMasksMatch.isEmpty()) {
            context.throwPositionedException("Il banco da lavoro deve consentire di combinare due maschere danneggiate per riparazione d'emergenza!", BlockPos.ORIGIN);
        }
        ItemStack combinedOutput = twoMasksMatch.get().value().craft(twoMasksInput, context.getWorld().getRegistryManager());
        if (!combinedOutput.isOf(ModItems.SPORE_MASK)) {
            context.throwPositionedException("L'unione di due maschere deve produrre una Spore Mask!", BlockPos.ORIGIN);
        }
        if (!combinedOutput.isDamaged() || combinedOutput.getDamage() >= 120) {
            context.throwPositionedException("La combinazione nel banco da lavoro deve sommare le durabilità residue delle maschere", BlockPos.ORIGIN);
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testMaskEnchantability(TestContext context) {
        if (ModItems.SPORE_MASK.getEnchantability() != 0) {
            context.throwPositionedException("SPORE_MASK deve avere un'incantabilità == 0 per disabilitare il tavolo degli incantesimi, trovato: " + ModItems.SPORE_MASK.getEnchantability(), BlockPos.ORIGIN);
        }
        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testMaskDurabilityWearOnDamage(TestContext context) {
        ItemStack stack = new ItemStack(ModItems.SPORE_MASK);
        int initialDamage = stack.getDamage();
        stack.setDamage(initialDamage + 1);

        if (stack.getDamage() != initialDamage + 1) {
            context.throwPositionedException("Il danno alla Spore Mask non è stato registrato correttamente", BlockPos.ORIGIN);
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testMaskProtectionLogic(TestContext context) {
        ItemStack mask = new ItemStack(ModItems.SPORE_MASK);
        if (!mask.isOf(ModItems.SPORE_MASK)) {
            context.throwPositionedException("L'item creato non è riconosciuto come SPORE_MASK", BlockPos.ORIGIN);
        }

        ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        if (!config.toxicity.enable_spore_mask_protection) {
            context.throwPositionedException("enable_spore_mask_protection deve essere abilitato di default", BlockPos.ORIGIN);
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testMaskArmorMaterialAndLayer(TestContext context) {
        if (!(ModItems.SPORE_MASK instanceof SporeMaskItem maskItem)) {
            context.throwPositionedException("SPORE_MASK deve essere SporeMaskItem", BlockPos.ORIGIN);
            return;
        }

        if (!maskItem.getMaterial().equals(ModItems.SPORE_MASK_ARMOR_MATERIAL)) {
            context.throwPositionedException("SPORE_MASK deve utilizzare SPORE_MASK_ARMOR_MATERIAL", BlockPos.ORIGIN);
        }

        var layers = maskItem.getMaterial().value().layers();
        if (layers.isEmpty() || !layers.getFirst().getTexture(false).toString().contains("spore_mask_layer_1")) {
            context.throwPositionedException("Il layer armatura di SPORE_MASK deve puntare a spore_mask_layer_1, trovato: " + (layers.isEmpty() ? "vuoto" : layers.getFirst().getTexture(false)), BlockPos.ORIGIN);
        }

        context.complete();
    }
}
