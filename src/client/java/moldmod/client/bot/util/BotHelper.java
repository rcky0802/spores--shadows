package moldmod.client.bot.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class BotHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger("ScreenshotBot");

    private BotHelper() {}

    public static void fill(ServerWorld world, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, Block block) {
        fill(world, minX, minY, minZ, maxX, maxY, maxZ, block.getDefaultState());
    }

    public static void fill(ServerWorld world, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, BlockState state) {
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    world.setBlockState(new BlockPos(x, y, z), state);
                }
            }
        }
    }

    public static void clearDroppedItems(ServerWorld world, BlockPos center, double radius) {
        net.minecraft.util.math.Box box = net.minecraft.util.math.Box.of(center.toCenterPos(), radius * 2, radius * 2, radius * 2);
        world.getEntitiesByClass(net.minecraft.entity.ItemEntity.class, box, e -> true).forEach(net.minecraft.entity.Entity::discard);
    }

    public static void takeScreenshot(MinecraftClient client, String filename) {
        ScreenshotRecorder.saveScreenshot(
            client.runDirectory,
            filename,
            client.getFramebuffer(),
            text -> LOGGER.info("[ScreenshotBot] Saved screenshot: {}", filename)
        );
    }
}
