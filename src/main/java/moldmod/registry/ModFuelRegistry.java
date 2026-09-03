package moldmod.registry;

import me.shedaniel.autoconfig.AutoConfig;
import moldmod.SporesShadows;
import moldmod.SporesShadowsConstants.MoldyWoodType;
import moldmod.config.ModConfig;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;

public class ModFuelRegistry {

    public static void register() {
        for (MoldyWoodType woodType : moldmod.SporesShadowsConstants.WOOD_TYPES) {
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

        ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        float m0 = config.furnaceMultipliers.stage_0;
        float m1 = config.furnaceMultipliers.stage_1;
        float m2 = config.furnaceMultipliers.stage_2;
        float m3 = config.furnaceMultipliers.stage_3;

        FuelRegistry fuelRegistry = FuelRegistry.INSTANCE;

        if (waxed != Items.AIR) {
            fuelRegistry.add(waxed, Math.max(0, (int)(baseFuel * m0)));
        }
        if (tainted != Items.AIR) {
            fuelRegistry.add(tainted, Math.max(37, (int)(baseFuel * m1)));
        }
        if (waxedTainted != Items.AIR) {
            fuelRegistry.add(waxedTainted, Math.max(37, (int)(baseFuel * m1)));
        }
        if (moldy != Items.AIR) {
            fuelRegistry.add(moldy, Math.max(37, (int)(baseFuel * m2)));
        }
        if (waxedMoldy != Items.AIR) {
            fuelRegistry.add(waxedMoldy, Math.max(37, (int)(baseFuel * m2)));
        }
        if (rotten != Items.AIR) {
            fuelRegistry.add(rotten, Math.max(37, (int)(baseFuel * m3)));
        }
        if (waxedRotten != Items.AIR) {
            fuelRegistry.add(waxedRotten, Math.max(37, (int)(baseFuel * m3)));
        }
    }
}
