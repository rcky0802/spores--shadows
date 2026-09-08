package moldmod.event;

import me.shedaniel.autoconfig.AutoConfig;
import moldmod.config.ModConfig;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.WorldAccess;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class RoomSaturationManager {
    private static final Map<Long, RoomGasState> ACTIVE_ROOMS = new ConcurrentHashMap<>();

    public record RoomGasState(
            double currentMiasma,
            double targetMiasma,
            long lastUpdateTick) {
    }

    public static BlockPos calculateAnchor(Set<BlockPos> airBlocks, BlockPos defaultPos) {
        if (airBlocks != null && !airBlocks.isEmpty()) {
            BlockPos min = null;
            for (BlockPos pos : airBlocks) {
                if (min == null || pos.compareTo(min) < 0) {
                    min = pos;
                }
            }
            return min;
        }
        return defaultPos != null ? defaultPos : BlockPos.ORIGIN;
    }

    public static double getDynamicMiasma(WorldAccess world, BlockPos anchor, double targetMiasma) {
        ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        if (!config.toxicity.enable_dynamic_spore_saturation) {
            return targetMiasma;
        }
        if (!(world instanceof ServerWorld serverWorld)) {
            return targetMiasma;
        }

        long currentTick = serverWorld.getServer() != null ? serverWorld.getServer().getTicks()
                : serverWorld.getTime();
        long key = anchor.asLong();
        RoomGasState state = ACTIVE_ROOMS.get(key);

        if (state == null) {
            ACTIVE_ROOMS.put(key, new RoomGasState(targetMiasma, targetMiasma, currentTick));
            return targetMiasma;
        }

        long elapsedTicks = currentTick - state.lastUpdateTick();
        double current = state.currentMiasma();

        if (elapsedTicks > 0) {
            double effectiveTarget = (Math.abs(state.targetMiasma() - targetMiasma) > 1e-4) ? targetMiasma : state.targetMiasma();
            double alpha = (current > effectiveTarget)
                    ? config.toxicity.dissipation_speed_multiplier
                    : config.toxicity.saturation_speed_multiplier;

            double steps = elapsedTicks / (double) Math.max(1, config.toxicity.check_interval_ticks);
            double factor = 1.0 - Math.pow(1.0 - MathHelper.clamp(alpha, 0.01, 1.0), Math.max(1.0, steps));
            current = current + factor * (effectiveTarget - current);

            if (Math.abs(current - effectiveTarget) < 0.05) {
                current = effectiveTarget;
            }
        }

        ACTIVE_ROOMS.put(key, new RoomGasState(current, targetMiasma, currentTick));
        return current;
    }

    public static RoomGasState getState(BlockPos anchor) {
        return ACTIVE_ROOMS.get(anchor.asLong());
    }

    public static void reset(BlockPos anchor) {
        if (anchor != null) {
            ACTIVE_ROOMS.remove(anchor.asLong());
        }
    }

    public static void cleanup(long currentTick) {
        ACTIVE_ROOMS.entrySet().removeIf(e -> (currentTick - e.getValue().lastUpdateTick()) > 1200);
    }

    public static void clear() {
        ACTIVE_ROOMS.clear();
    }
}
