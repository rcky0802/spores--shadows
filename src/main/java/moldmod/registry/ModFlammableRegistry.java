package moldmod.registry;

import moldmod.SporesShadows;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;

public class ModFlammableRegistry {

    public static void register() {
        String[] woods = moldmod.SporesShadows.WOODS;

        for (String wood : woods) {
            String logName = wood + "_log";
            String woodName = wood + "_wood";
            String prefix = wood;

            // Vanilla Log = 5, 5
            registerForBlock("moldy_" + logName, 10, 10);
            registerForBlock("moldy_stripped_" + logName, 10, 10);
            
            if (woodName != null) {
                registerForBlock("moldy_" + woodName, 10, 10);
                registerForBlock("moldy_stripped_" + woodName, 10, 10);
            }

            // Vanilla Planks = 5, 20
            registerForBlock("moldy_" + prefix + "_planks", 10, 30);
            registerForBlock("moldy_" + prefix + "_stairs", 10, 30);
            registerForBlock("moldy_" + prefix + "_slab", 10, 30);
            registerForBlock("moldy_" + prefix + "_fence", 10, 30);
            registerForBlock("moldy_" + prefix + "_fence_gate", 10, 30);
        }
    }

    private static void registerForBlock(String name, int burnChance, int spreadChance) {
        Block block = Registries.BLOCK.get(SporesShadows.id(name));
        if (block != net.minecraft.block.Blocks.AIR) {
            FlammableBlockRegistry.getDefaultInstance().add(block, burnChance, spreadChance);
        }
    }
}
