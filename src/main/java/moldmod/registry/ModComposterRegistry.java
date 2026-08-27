package moldmod.registry;

import moldmod.SporesShadows;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;

public class ModComposterRegistry {

    public static void register() {
        for (moldmod.SporesShadowsConstants.MoldyWoodType woodType : moldmod.SporesShadowsConstants.WOOD_TYPES) {
            String logName = woodType.getLogName();
            String woodName = woodType.getWoodName();
            String prefix = woodType.name();

            registerForSet(logName);
            registerForSet("stripped_" + logName);
            registerForSet(prefix + "_planks");
            registerForSet(prefix + "_stairs");
            registerForSet(prefix + "_slab");
            registerForSet(prefix + "_fence");
            registerForSet(prefix + "_fence_gate");
            registerForSet(prefix + "_door");
            registerForSet(prefix + "_trapdoor");
            registerForSet(prefix + "_button");
            registerForSet(prefix + "_pressure_plate");

            if (woodName != null) {
                registerForSet(woodName);
                registerForSet("stripped_" + woodName);
            }
        }
    }

    private static void registerForSet(String baseName) {
        Item tainted = Registries.ITEM.get(SporesShadows.id("tainted_" + baseName));
        Item moldy = Registries.ITEM.get(SporesShadows.id("moldy_" + baseName));
        Item rotten = Registries.ITEM.get(SporesShadows.id("rotten_" + baseName));

        Item waxedTainted = Registries.ITEM.get(SporesShadows.id("waxed_tainted_" + baseName));
        Item waxedMoldy = Registries.ITEM.get(SporesShadows.id("waxed_moldy_" + baseName));
        Item waxedRotten = Registries.ITEM.get(SporesShadows.id("waxed_rotten_" + baseName));

        if (tainted != net.minecraft.item.Items.AIR) {
            net.fabricmc.fabric.api.registry.CompostingChanceRegistry.INSTANCE.add(tainted, 0.50f);
        }
        if (waxedTainted != net.minecraft.item.Items.AIR) {
            net.fabricmc.fabric.api.registry.CompostingChanceRegistry.INSTANCE.add(waxedTainted, 0.50f);
        }

        if (moldy != net.minecraft.item.Items.AIR) {
            net.fabricmc.fabric.api.registry.CompostingChanceRegistry.INSTANCE.add(moldy, 0.65f);
        }
        if (waxedMoldy != net.minecraft.item.Items.AIR) {
            net.fabricmc.fabric.api.registry.CompostingChanceRegistry.INSTANCE.add(waxedMoldy, 0.65f);
        }

        if (rotten != net.minecraft.item.Items.AIR) {
            net.fabricmc.fabric.api.registry.CompostingChanceRegistry.INSTANCE.add(rotten, 0.85f);
        }
        if (waxedRotten != net.minecraft.item.Items.AIR) {
            net.fabricmc.fabric.api.registry.CompostingChanceRegistry.INSTANCE.add(waxedRotten, 0.85f);
        }
    }
}
