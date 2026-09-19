package moldmod.registry;

import me.shedaniel.autoconfig.AutoConfig;
import moldmod.block.ModBlocks;
import moldmod.block.core.MoldyBlock;
import moldmod.config.ModConfig;
import net.fabricmc.fabric.api.registry.CompostingChanceRegistry;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BlockStateComponent;
import net.minecraft.item.Item;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

        CompostingChanceRegistry composter = CompostingChanceRegistry.INSTANCE;
        Set<Item> processed = new HashSet<>();

        // Register all moldy items (stages 1, 2, 3 and their waxed counterparts)
        // Clean items (stage 0, unwaxed or waxed) can never be composted!
        for (List<Item> items : ModBlocks.MOLDY_ITEMS_BY_BLOCK.values()) {
            for (Item item : items) {
                if (!processed.add(item)) continue;

                BlockStateComponent comp = item.getComponents().get(DataComponentTypes.BLOCK_STATE);
                if (comp != null) {
                    Integer stage = comp.getValue(MoldyBlock.STAGE);
                    if (stage != null) {
                        if (stage == 1) {
                            composter.add(item, taintedChance);
                        } else if (stage == 2) {
                            composter.add(item, moldyChance);
                        } else if (stage == 3) {
                            composter.add(item, rottenChance);
                        }
                    }
                }
            }
        }
    }
}
