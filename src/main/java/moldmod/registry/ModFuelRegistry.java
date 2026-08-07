package moldmod.registry;

import moldmod.SporesShadows;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;

public class ModFuelRegistry {

    public static void register() {
        String[] woods = moldmod.SporesShadows.WOODS;

        for (String wood : woods) {
            String logName = wood + "_log";
            String woodName = wood + "_wood";
            String prefix = wood;

            // Logs/Wood base fuel is 300
            registerForSet(logName, 300);
            registerForSet("stripped_" + logName, 300);
            registerForSet(prefix + "_planks", 300);
            registerForSet(prefix + "_stairs", 300);
            registerForSet(prefix + "_slab", 150);
            registerForSet(prefix + "_fence", 300);
            registerForSet(prefix + "_fence_gate", 300);
            registerForSet(prefix + "_door", 200);
            registerForSet(prefix + "_trapdoor", 300);
            registerForSet(prefix + "_button", 100);
            registerForSet(prefix + "_pressure_plate", 300);

            if (woodName != null) {
                registerForSet(woodName, 300);
                registerForSet("stripped_" + woodName, 300);
            }
        }
    }

    private static void registerForSet(String baseName, int baseFuel) {
        Item waxed = Registries.ITEM.get(SporesShadows.id("waxed_" + baseName));
        Item tainted = Registries.ITEM.get(SporesShadows.id("tainted_" + baseName));
        Item moldy = Registries.ITEM.get(SporesShadows.id("moldy_" + baseName));
        Item rotten = Registries.ITEM.get(SporesShadows.id("rotten_" + baseName));

        // Stage 0 = 100%, Stage 1 = 50%, Stage 2 = 25%, Stage 3 = 12.5% (min 37 ticks)
        if (waxed != net.minecraft.item.Items.AIR) {
            FuelRegistry.INSTANCE.add(waxed, baseFuel);
        }
        if (tainted != net.minecraft.item.Items.AIR) {
            FuelRegistry.INSTANCE.add(tainted, Math.max(37, (int)(baseFuel * 0.5f)));
        }
        if (moldy != net.minecraft.item.Items.AIR) {
            FuelRegistry.INSTANCE.add(moldy, Math.max(37, (int)(baseFuel * 0.25f)));
        }
        if (rotten != net.minecraft.item.Items.AIR) {
            FuelRegistry.INSTANCE.add(rotten, Math.max(37, (int)(baseFuel * 0.125f)));
        }
    }
}
