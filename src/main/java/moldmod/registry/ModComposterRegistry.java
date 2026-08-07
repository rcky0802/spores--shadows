package moldmod.registry;

import moldmod.SporesShadows;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;

public class ModComposterRegistry {

    public static void register() {
        String[] woods = moldmod.SporesShadows.WOODS;

        for (String wood : woods) {
            String logName = wood + "_log";
            String woodName = wood + "_wood";
            String prefix = wood;

            registerForSet(logName);
            registerForSet("stripped_" + logName);
            registerForSet(prefix + "_planks");
            registerForSet(prefix + "_stairs");
            registerForSet(prefix + "_slab");
            registerForSet(prefix + "_fence");
            registerForSet(prefix + "_fence_gate");
            registerForSet(prefix + "_door");
            registerForSet(prefix + "_trapdoor");

            if (woodName != null) {
                registerForSet(woodName);
                registerForSet("stripped_" + woodName);
            }
        }
    }

    private static void registerForSet(String baseName) {
        Item waxed = Registries.ITEM.get(SporesShadows.id("waxed_" + baseName));
        Item tainted = Registries.ITEM.get(SporesShadows.id("tainted_" + baseName));
        Item moldy = Registries.ITEM.get(SporesShadows.id("moldy_" + baseName));
        Item rotten = Registries.ITEM.get(SporesShadows.id("rotten_" + baseName));

        if (waxed != net.minecraft.item.Items.AIR) {
            net.fabricmc.fabric.api.registry.CompostingChanceRegistry.INSTANCE.add(waxed, 0.30f);
        }
        if (tainted != net.minecraft.item.Items.AIR) {
            net.fabricmc.fabric.api.registry.CompostingChanceRegistry.INSTANCE.add(tainted, 0.50f);
        }
        if (moldy != net.minecraft.item.Items.AIR) {
            net.fabricmc.fabric.api.registry.CompostingChanceRegistry.INSTANCE.add(moldy, 0.65f);
        }
        if (rotten != net.minecraft.item.Items.AIR) {
            net.fabricmc.fabric.api.registry.CompostingChanceRegistry.INSTANCE.add(rotten, 0.85f);
        }
    }
}
