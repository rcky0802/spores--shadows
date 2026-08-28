package moldmod.registry;

import moldmod.SporesShadows;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;

public class ModFlammableRegistry {

    public static void register() {
        for (moldmod.SporesShadowsConstants.MoldyWoodType woodType : moldmod.SporesShadowsConstants.WOOD_TYPES) {
            if (woodType.isNether()) {
                continue;
            }

            String logName = woodType.getLogName();
            String woodName = woodType.getWoodName();
            String prefix = woodType.name();

            // Logs & Wood = 5, 5 base
            registerForBlock("moldy_" + logName, 5, 5);
            registerForBlock("moldy_stripped_" + logName, 5, 5);
            registerForBlock("waxed_" + logName, 5, 5);
            registerForBlock("waxed_stripped_" + logName, 5, 5);
            
            if (woodName != null) {
                registerForBlock("moldy_" + woodName, 5, 5);
                registerForBlock("moldy_stripped_" + woodName, 5, 5);
                registerForBlock("waxed_" + woodName, 5, 5);
                registerForBlock("waxed_stripped_" + woodName, 5, 5);
            }

            // Planks & Wooden products = 5, 20 base
            registerForBlock("moldy_" + prefix + "_planks", 5, 20);
            registerForBlock("moldy_" + prefix + "_stairs", 5, 20);
            registerForBlock("moldy_" + prefix + "_slab", 5, 20);
            registerForBlock("moldy_" + prefix + "_fence", 5, 20);
            registerForBlock("moldy_" + prefix + "_fence_gate", 5, 20);
            registerForBlock("moldy_" + prefix + "_door", 5, 20);
            registerForBlock("moldy_" + prefix + "_trapdoor", 5, 20);
            
            registerForBlock("waxed_" + prefix + "_planks", 5, 20);
            registerForBlock("waxed_" + prefix + "_stairs", 5, 20);
            registerForBlock("waxed_" + prefix + "_slab", 5, 20);
            registerForBlock("waxed_" + prefix + "_fence", 5, 20);
            registerForBlock("waxed_" + prefix + "_fence_gate", 5, 20);
            registerForBlock("waxed_" + prefix + "_door", 5, 20);
            registerForBlock("waxed_" + prefix + "_trapdoor", 5, 20);
        }
    }

    private static void registerForBlock(String name, int burnChance, int spreadChance) {
        Block block = Registries.BLOCK.get(SporesShadows.id(name));
        if (block != net.minecraft.block.Blocks.AIR) {
            FlammableBlockRegistry.getDefaultInstance().add(block, burnChance, spreadChance);
        }
    }
}
