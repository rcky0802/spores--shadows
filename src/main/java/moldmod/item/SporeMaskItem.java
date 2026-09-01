package moldmod.item;

import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.resourcepack.api.PolymerArmorModel;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import moldmod.SporesShadows;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

public class SporeMaskItem extends ArmorItem implements PolymerItem {

    private final PolymerModelData modelData;
    private final PolymerArmorModel armorModel;

    public SporeMaskItem(RegistryEntry<ArmorMaterial> material, Settings settings) {
        super(material, Type.HELMET, settings);
        this.modelData = PolymerResourcePackUtils.requestModel(Items.LEATHER_HELMET, SporesShadows.id("item/spore_mask"));
        this.armorModel = PolymerResourcePackUtils.requestArmor(SporesShadows.id("spore_mask"));
    }

    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return ingredient.isIn(ItemTags.WOOL) || super.canRepair(stack, ingredient);
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return this.modelData != null ? this.modelData.item() : Items.LEATHER_HELMET;
    }

    @Override
    public int getPolymerCustomModelData(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return this.modelData != null ? this.modelData.value() : -1;
    }

    @Override
    public int getPolymerArmorColor(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return this.armorModel != null ? this.armorModel.color() : -1;
    }
}