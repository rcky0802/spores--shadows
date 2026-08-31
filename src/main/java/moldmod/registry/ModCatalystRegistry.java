package moldmod.registry;

import moldmod.config.ModConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Registry mapping vanilla blocks to catalyst types for O(1) performance lookup
 * during mold risk evaluation.
 */
public final class ModCatalystRegistry {

    public enum CatalystType {
        MUD,
        WATER_CAULDRON,
        PODZOL_MYCELIUM,
        FUNGI,
        SPORE_BLOSSOM
    }

    public record CatalystContribution(double localHumidityBonus, double catalystBonus) {
        public static final CatalystContribution NONE = new CatalystContribution(0.0, 0.0);
    }

    private static final Map<Block, CatalystType> STATIC_CATALYSTS = new IdentityHashMap<>();

    static {
        STATIC_CATALYSTS.put(Blocks.MUD, CatalystType.MUD);
        STATIC_CATALYSTS.put(Blocks.WATER_CAULDRON, CatalystType.WATER_CAULDRON);
        STATIC_CATALYSTS.put(Blocks.MYCELIUM, CatalystType.PODZOL_MYCELIUM);
        STATIC_CATALYSTS.put(Blocks.PODZOL, CatalystType.PODZOL_MYCELIUM);
        STATIC_CATALYSTS.put(Blocks.BROWN_MUSHROOM, CatalystType.FUNGI);
        STATIC_CATALYSTS.put(Blocks.RED_MUSHROOM, CatalystType.FUNGI);
        STATIC_CATALYSTS.put(Blocks.BROWN_MUSHROOM_BLOCK, CatalystType.FUNGI);
        STATIC_CATALYSTS.put(Blocks.RED_MUSHROOM_BLOCK, CatalystType.FUNGI);
        STATIC_CATALYSTS.put(Blocks.MUSHROOM_STEM, CatalystType.FUNGI);
        STATIC_CATALYSTS.put(Blocks.SPORE_BLOSSOM, CatalystType.SPORE_BLOSSOM);
    }

    public static CatalystContribution getContribution(BlockState state, ModConfig config) {
        Block block = state.getBlock();
        CatalystType type = STATIC_CATALYSTS.get(block);
        if (type != null) {
            return switch (type) {
                case MUD -> new CatalystContribution(config.environment.cauldron_adjacent_bonus, config.catalysts.mud_bonus);
                case WATER_CAULDRON -> new CatalystContribution(config.environment.cauldron_adjacent_bonus, 0.0);
                case PODZOL_MYCELIUM -> new CatalystContribution(0.0, config.catalysts.podzol_mycelium_bonus);
                case FUNGI -> new CatalystContribution(0.0, config.catalysts.fungi_bonus);
                case SPORE_BLOSSOM -> new CatalystContribution(0.0, config.catalysts.spore_blossom_bonus);
            };
        }
        return CatalystContribution.NONE;
    }
}
