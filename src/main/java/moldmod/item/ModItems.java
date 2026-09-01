package moldmod.item;

import moldmod.SporesShadows;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModItems {

    public static final Item SPORE_MASK = new SporeMaskItem(ArmorMaterials.LEATHER, new Item.Settings().maxDamage(165));

    public static void registerModItems() {
        SporesShadows.LOGGER.info("Registering ModItems for " + SporesShadows.MOD_ID);

        Registry.register(Registries.ITEM, SporesShadows.id("spore_mask"), SPORE_MASK);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
            entries.add(SPORE_MASK);
        });
    }
}