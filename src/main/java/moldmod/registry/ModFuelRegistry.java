package moldmod.registry;

import moldmod.SporesShadows;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;

public class ModFuelRegistry {

    public static void register() {
        // Ticks base values: 
        // Log, Wood, Stripped variants = 300
        // Planks, Stairs = 300
        // Slabs = 150
        // Fence, Gate = 300
        // Door = 200
        // Trapdoor = 300

        registerForSet("oak", "oak_log", "oak_wood");
    }

    private static void registerForSet(String prefix, String logName, String woodName) {
        registerFuelStage(logName, 300);
        registerFuelStage("stripped_" + logName, 300);
        registerFuelStage(prefix + "_planks", 300);
        registerFuelStage(prefix + "_stairs", 300);
        registerFuelStage(prefix + "_slab", 150);
        registerFuelStage(prefix + "_fence", 300);
        registerFuelStage(prefix + "_fence_gate", 300);
        registerFuelStage(prefix + "_door", 200);
        registerFuelStage(prefix + "_trapdoor", 300);

        if (woodName != null) {
            registerFuelStage(woodName, 300);
            registerFuelStage("stripped_" + woodName, 300);
        }
    }

    private static void registerFuelStage(String baseName, int baseTicks) {
        moldmod.config.ModConfig config = me.shedaniel.autoconfig.AutoConfig.getConfigHolder(moldmod.config.ModConfig.class).getConfig();
        int stage1Ticks = Math.max(1, (int)(baseTicks * config.furnaceMultipliers.stage_1));
        int stage2Ticks = Math.max(1, (int)(baseTicks * config.furnaceMultipliers.stage_2));
        int stage3Ticks = Math.max(37, (int)(baseTicks * config.furnaceMultipliers.stage_3)); // Minimum 37 ticks to avoid redstone glitches

        Item tainted = Registries.ITEM.get(SporesShadows.id("tainted_" + baseName));
        Item moldy = Registries.ITEM.get(SporesShadows.id("moldy_" + baseName));
        Item rotten = Registries.ITEM.get(SporesShadows.id("rotten_" + baseName));

        if (tainted != net.minecraft.item.Items.AIR) FuelRegistry.INSTANCE.add(tainted, stage1Ticks);
        if (moldy != net.minecraft.item.Items.AIR) FuelRegistry.INSTANCE.add(moldy, stage2Ticks);
        if (rotten != net.minecraft.item.Items.AIR) FuelRegistry.INSTANCE.add(rotten, stage3Ticks);
    }
}
