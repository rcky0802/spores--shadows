package moldmod.item;

import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import moldmod.SporesShadows;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.sound.SoundEvents;

import java.util.List;
import java.util.Map;

public class ModItems {

    public static final RegistryEntry<ArmorMaterial> SPORE_MASK_ARMOR_MATERIAL = Registry.registerReference(
        Registries.ARMOR_MATERIAL,
        SporesShadows.id("spore_mask"),
        new ArmorMaterial(
            Map.of(
                ArmorItem.Type.HELMET, 1,
                ArmorItem.Type.CHESTPLATE, 0,
                ArmorItem.Type.LEGGINGS, 0,
                ArmorItem.Type.BOOTS, 0
            ),
            15,
            SoundEvents.ITEM_ARMOR_EQUIP_LEATHER,
            () -> Ingredient.fromTag(ItemTags.WOOL),
            List.of(new ArmorMaterial.Layer(SporesShadows.id("spore_mask"))),
            0.0F,
            0.0F
        )
    );

    public static final Item SPORE_MASK = new SporeMaskItem(SPORE_MASK_ARMOR_MATERIAL, new Item.Settings().maxDamage(165));

    public static void registerModItems() {
        SporesShadows.LOGGER.info("Registering ModItems for " + SporesShadows.MOD_ID);

        Registry.register(Registries.ITEM, SporesShadows.id("spore_mask"), SPORE_MASK);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
            entries.add(SPORE_MASK);
        });
    }
}