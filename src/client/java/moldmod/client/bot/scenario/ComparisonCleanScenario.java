package moldmod.client.bot.scenario;

import net.minecraft.block.BarrelBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class ComparisonCleanScenario implements BotScenario {
    public static final int OX = 120;
    public static final int OZ = 0;
    public static final int BASE_Y = 120;

    @Override
    public String getName() {
        return "Scenario 4a: Comparison Room (Clean Vanilla)";
    }

    @Override
    public String getScreenshotFilename() {
        return "bot_room_clean.png";
    }

    @Override
    public int getWaitTicks() {
        return 40;
    }

    @Override
    public void prepare(ServerWorld world, ServerPlayerEntity player, MinecraftClient client) {
        client.options.setPerspective(Perspective.FIRST_PERSON);

        for (int x = 0; x <= 4; x++) {
            for (int z = 0; z <= 4; z++) {
                world.setBlockState(new BlockPos(OX + x, BASE_Y - 1, OZ + z), Blocks.OAK_PLANKS.getDefaultState());
            }
        }

        for (int x = 0; x <= 4; x++) {
            for (int y = 0; y <= 3; y++) {
                world.setBlockState(new BlockPos(OX + x, BASE_Y + y, OZ + 4), Blocks.OAK_LOG.getDefaultState());
            }
        }

        world.setBlockState(new BlockPos(OX + 1, BASE_Y, OZ + 3),
            Blocks.CHEST.getDefaultState().with(HorizontalFacingBlock.FACING, Direction.NORTH));
        world.setBlockState(new BlockPos(OX + 2, BASE_Y, OZ + 3), Blocks.CRAFTING_TABLE.getDefaultState());
        world.setBlockState(new BlockPos(OX + 3, BASE_Y, OZ + 3),
            Blocks.BARREL.getDefaultState().with(BarrelBlock.FACING, Direction.NORTH));
        world.setBlockState(new BlockPos(OX + 2, BASE_Y + 2, OZ + 3), Blocks.LANTERN.getDefaultState());

        player.teleport(world, OX + 2.0, BASE_Y, OZ + 0.8, 0f, 10f);
        client.player.setYaw(0f);
        client.player.setPitch(10f);
    }
}
