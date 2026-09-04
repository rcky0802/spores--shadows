package moldmod.client;

import moldmod.event.ToxicAirEvent;
import moldmod.event.ToxicAirEvent.MiasmaResult;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class ClientToxicityCache {

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

        if (!ToxicAirEvent.hasMoldNearby(world, eyePos, 8)) {
            cachedToxicity = 0.0f;
            return 0.0f;
        }

        MiasmaResult result = ToxicAirEvent.calculateMiasma(world, eyePos);
        cachedToxicity = switch (result.level) {
            case CLEAN -> 0.0f;
            case WARNING -> 0.33f;
            case MODERATE_HUNGER -> 0.66f;
            case LETHAL_POISON -> 1.0f;
        };

        return cachedToxicity;
    }
}
