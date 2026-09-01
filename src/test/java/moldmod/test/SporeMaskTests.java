package moldmod.test;

import moldmod.block.ModBlocks;
import moldmod.event.ToxicAirEvent;
import moldmod.item.ModItems;
import moldmod.item.SporeMaskItem;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

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
    public void testMaskWoolRepair(TestContext context) {
        ItemStack maskStack = new ItemStack(ModItems.SPORE_MASK);
        maskStack.setDamage(50);

        // Verifica che qualsiasi lana sia accettata per la riparazione del filtro
        if (!ModItems.SPORE_MASK.canRepair(maskStack, new ItemStack(Items.WHITE_WOOL))) {
            context.throwPositionedException("La lana bianca deve poter riparare la Spore Mask", BlockPos.ORIGIN);
        }
        if (!ModItems.SPORE_MASK.canRepair(maskStack, new ItemStack(Items.BLACK_WOOL))) {
            context.throwPositionedException("La lana nera deve poter riparare la Spore Mask", BlockPos.ORIGIN);
        }
        if (!ModItems.SPORE_MASK.canRepair(maskStack, new ItemStack(Items.RED_WOOL))) {
            context.throwPositionedException("La lana rossa deve poter riparare la Spore Mask", BlockPos.ORIGIN);
        }

        // Verifica che materiali non validi vengano rifiutati
        if (ModItems.SPORE_MASK.canRepair(maskStack, new ItemStack(Items.DIRT))) {
            context.throwPositionedException("La terra non deve poter riparare la Spore Mask", BlockPos.ORIGIN);
        }
        if (ModItems.SPORE_MASK.canRepair(maskStack, new ItemStack(Items.IRON_INGOT))) {
            context.throwPositionedException("Il ferro non deve poter riparare la Spore Mask", BlockPos.ORIGIN);
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

        moldmod.config.ModConfig config = me.shedaniel.autoconfig.AutoConfig.getConfigHolder(moldmod.config.ModConfig.class).getConfig();
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
