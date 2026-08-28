package moldmod.registry;

import moldmod.SporesShadows;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;

public class ModFuelRegistry {

    public static void register() {
        for (moldmod.SporesShadowsConstants.MoldyWoodType woodType : moldmod.SporesShadowsConstants.WOOD_TYPES) {
            if (woodType.isNether()) {
                continue;
            }
            String logName = woodType.getLogName();
            String woodName = woodType.getWoodName();
            String prefix = woodType.name();

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

        Item waxedTainted = Registries.ITEM.get(SporesShadows.id("waxed_tainted_" + baseName));
        Item waxedMoldy = Registries.ITEM.get(SporesShadows.id("waxed_moldy_" + baseName));
        Item waxedRotten = Registries.ITEM.get(SporesShadows.id("waxed_rotten_" + baseName));

        moldmod.config.ModConfig config = me.shedaniel.autoconfig.AutoConfig.getConfigHolder(moldmod.config.ModConfig.class).getConfig();
        float m0 = config.furnaceMultipliers.stage_0;
        float m1 = config.furnaceMultipliers.stage_1;
        float m2 = config.furnaceMultipliers.stage_2;
        float m3 = config.furnaceMultipliers.stage_3;

        if (waxed != net.minecraft.item.Items.AIR) {
            FuelRegistry.INSTANCE.add(waxed, Math.max(0, (int)(baseFuel * m0)));
        }
        if (tainted != net.minecraft.item.Items.AIR) {
            FuelRegistry.INSTANCE.add(tainted, Math.max(37, (int)(baseFuel * m1)));
        }
        if (waxedTainted != net.minecraft.item.Items.AIR) {
            FuelRegistry.INSTANCE.add(waxedTainted, Math.max(37, (int)(baseFuel * m1)));
        }
        if (moldy != net.minecraft.item.Items.AIR) {
            FuelRegistry.INSTANCE.add(moldy, Math.max(37, (int)(baseFuel * m2)));
        }
        if (waxedMoldy != net.minecraft.item.Items.AIR) {
            FuelRegistry.INSTANCE.add(waxedMoldy, Math.max(37, (int)(baseFuel * m2)));
        }
        if (rotten != net.minecraft.item.Items.AIR) {
            FuelRegistry.INSTANCE.add(rotten, Math.max(37, (int)(baseFuel * m3)));
        }
        if (waxedRotten != net.minecraft.item.Items.AIR) {
            FuelRegistry.INSTANCE.add(waxedRotten, Math.max(37, (int)(baseFuel * m3)));
        }
    }
}
