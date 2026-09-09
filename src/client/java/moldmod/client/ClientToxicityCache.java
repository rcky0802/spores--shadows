package moldmod.client;

import me.shedaniel.autoconfig.AutoConfig;
import moldmod.atmosphere.RoomAtmosphereCalculator;
import moldmod.atmosphere.RoomAtmosphereCalculator.MiasmaResult;
import moldmod.config.ModConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public final class ClientToxicityCache {

    private ClientToxicityCache() {
    }

    private static long lastCheckTick = -1;
    private static float cachedToxicity = 0.0f;

    public static float getToxicity(LivingEntity entity) {
        if (entity == null) return 0.0f;
        World world = entity.getWorld();
        if (world == null) return 0.0f;

        long currentTick = world.getTime();
        if (currentTick - lastCheckTick < 10 && currentTick >= lastCheckTick) {
            return cachedToxicity;
        }

        lastCheckTick = currentTick;
        BlockPos eyePos = BlockPos.ofFloored(entity.getEyePos());

        ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        int radius = config != null && config.toxicity != null ? config.toxicity.scan_radius : 8;
        if (!RoomAtmosphereCalculator.hasMoldNearby(world, eyePos, radius)) {
            cachedToxicity = 0.0f;
            return 0.0f;
        }

        MiasmaResult result = RoomAtmosphereCalculator.calculateMiasma(world, eyePos);
        cachedToxicity = switch (result.level) {
            case CLEAN -> 0.0f;
            case WARNING -> 0.33f;
            case MODERATE_HUNGER -> 0.66f;
            case LETHAL_POISON -> 1.0f;
        };

        return cachedToxicity;
    }
}
