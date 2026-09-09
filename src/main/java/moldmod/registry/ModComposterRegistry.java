package moldmod.registry;

import moldmod.SporesShadows;
import moldmod.SporesShadowsConstants;
import moldmod.SporesShadowsConstants.MoldyWoodType;
import me.shedaniel.autoconfig.AutoConfig;
import moldmod.config.ModConfig;
import net.fabricmc.fabric.api.registry.CompostingChanceRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;

public final class ModComposterRegistry {

    private ModComposterRegistry() {
    }

    public static void register() {
        float taintedChance = 0.50f;
        float moldyChance = 0.65f;
        float rottenChance = 0.85f;
        try {
            ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
            if (config != null && config.composter != null) {
                taintedChance = config.composter.tainted_chance;
                moldyChance = config.composter.moldy_chance;
                rottenChance = config.composter.rotten_chance;
            }
        } catch (Exception ignored) {
        }

        for (MoldyWoodType woodType : SporesShadowsConstants.WOOD_TYPES) {
            String logName = woodType.getLogName();
            String woodName = woodType.getWoodName();
            String prefix = woodType.name();

            registerForSet(logName, taintedChance, moldyChance, rottenChance);
            registerForSet("stripped_" + logName, taintedChance, moldyChance, rottenChance);
            registerForSet(prefix + "_planks", taintedChance, moldyChance, rottenChance);
            registerForSet(prefix + "_stairs", taintedChance, moldyChance, rottenChance);
            registerForSet(prefix + "_slab", taintedChance, moldyChance, rottenChance);
            registerForSet(prefix + "_fence", taintedChance, moldyChance, rottenChance);
            registerForSet(prefix + "_fence_gate", taintedChance, moldyChance, rottenChance);
            registerForSet(prefix + "_door", taintedChance, moldyChance, rottenChance);
            registerForSet(prefix + "_trapdoor", taintedChance, moldyChance, rottenChance);
            registerForSet(prefix + "_button", taintedChance, moldyChance, rottenChance);
            registerForSet(prefix + "_pressure_plate", taintedChance, moldyChance, rottenChance);

            if (woodName != null) {
                registerForSet(woodName, taintedChance, moldyChance, rottenChance);
                registerForSet("stripped_" + woodName, taintedChance, moldyChance, rottenChance);
            }
        }
    }

    private static void registerForSet(String baseName, float taintedChance, float moldyChance, float rottenChance) {
        Item tainted = Registries.ITEM.get(SporesShadows.id("tainted_" + baseName));
        Item moldy = Registries.ITEM.get(SporesShadows.id("moldy_" + baseName));
        Item rotten = Registries.ITEM.get(SporesShadows.id("rotten_" + baseName));

        Item waxedTainted = Registries.ITEM.get(SporesShadows.id("waxed_tainted_" + baseName));
        Item waxedMoldy = Registries.ITEM.get(SporesShadows.id("waxed_moldy_" + baseName));
        Item waxedRotten = Registries.ITEM.get(SporesShadows.id("waxed_rotten_" + baseName));

        CompostingChanceRegistry composter = CompostingChanceRegistry.INSTANCE;

        if (tainted != Items.AIR) {
            composter.add(tainted, taintedChance);
        }
        if (waxedTainted != Items.AIR) {
            composter.add(waxedTainted, taintedChance);
        }

        if (moldy != Items.AIR) {
            composter.add(moldy, moldyChance);
        }
        if (waxedMoldy != Items.AIR) {
            composter.add(waxedMoldy, moldyChance);
        }

        if (rotten != Items.AIR) {
            composter.add(rotten, rottenChance);
        }
        if (waxedRotten != Items.AIR) {
            composter.add(waxedRotten, rottenChance);
        }
    }
}
