package moldmod.item;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;

public class SporeMaskItem extends ArmorItem {

    public SporeMaskItem(RegistryEntry<ArmorMaterial> material, Settings settings) {
        super(material, Type.HELMET, settings);
    }

    @Override
    public int getEnchantability() {
        return 0; // Disabled at enchanting table
    }

    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return ingredient.isIn(ItemTags.WOOL) || super.canRepair(stack, ingredient);
    }
}