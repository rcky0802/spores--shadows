package moldmod.client.cache;

import moldmod.block.MoistureDetectorBlock;
import moldmod.risk.MoldRiskCalculator;
import moldmod.risk.MoldRiskCalculator.MoldRiskResult;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public final class ClientMoistureCache {

    private ClientMoistureCache() {
    }

    private static long lastCheckTick = -1;
    private static float cachedMoisture = 0.0f;

    public static float getMoisture(LivingEntity entity) {
        if (entity == null) return 0.0f;
        World world = entity.getWorld();
        if (world == null) return 0.0f;

        long currentTick = world.getTime();
        if (currentTick - lastCheckTick < 10 && currentTick >= lastCheckTick) {
            return cachedMoisture;
        }

        lastCheckTick = currentTick;
        BlockPos eyePos = BlockPos.ofFloored(entity.getEyePos());

        MoldRiskResult result = MoldRiskCalculator.calculate(world, eyePos, false, null);
        int stage = MoistureDetectorBlock.getMoistureStage(result.Heff());
        cachedMoisture = switch (stage) {
            case 0 -> 0.0f;
            case 1 -> 0.33f;
            case 2 -> 0.66f;
            case 3 -> 1.0f;
            default -> 0.0f;
        };

        return cachedMoisture;
    }
}
