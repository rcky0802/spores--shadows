package moldmod.client.bot.scenario;

import moldmod.block.ModBlocks;
import moldmod.block.core.MoldyBlock;
import moldmod.block.workstation.MoldyChestBlockEntity;
import net.minecraft.block.BarrelBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class ComparisonMoldyScenario implements BotScenario {
    public static final int OX = 120;
    public static final int OZ = 0;
    public static final int BASE_Y = 120;

    @Override
    public String getName() {
        return "Scenario 4b: Comparison Room (Moldy Decay Stage 3)";
    }

    @Override
    public String getScreenshotFilename() {
        return "bot_room_moldy.png";
    }

    @Override
    public int getWaitTicks() {
        return 40;
    }

    @Override
    public void prepare(ServerWorld world, ServerPlayerEntity player, MinecraftClient client) {
        client.options.setPerspective(Perspective.FIRST_PERSON);

        net.minecraft.block.Block moldyPlanks = Registries.BLOCK.get(moldmod.SporesShadows.id("moldy_oak_planks"));
        net.minecraft.block.Block moldyLog = Registries.BLOCK.get(moldmod.SporesShadows.id("moldy_oak_log"));

        for (int x = 0; x <= 4; x++) {
            for (int z = 0; z <= 4; z++) {
                world.setBlockState(new BlockPos(OX + x, BASE_Y - 1, OZ + z),
                    moldyPlanks.getDefaultState().with(MoldyBlock.STAGE, 3));
            }
        }

        for (int x = 0; x <= 4; x++) {
            for (int y = 0; y <= 3; y++) {
                world.setBlockState(new BlockPos(OX + x, BASE_Y + y, OZ + 4),
                    moldyLog.getDefaultState().with(MoldyBlock.STAGE, 3));
            }
        }

        BlockPos chestPos = new BlockPos(OX + 1, BASE_Y, OZ + 3);
        world.setBlockState(chestPos,
            ModBlocks.MOLDY_CHEST.getDefaultState().with(MoldyBlock.STAGE, 3).with(HorizontalFacingBlock.FACING, Direction.NORTH));
        if (world.getBlockEntity(chestPos) instanceof MoldyChestBlockEntity be) {
            be.setMoldStage(3);
        }

        world.setBlockState(new BlockPos(OX + 2, BASE_Y, OZ + 3),
            ModBlocks.MOLDY_CRAFTING_TABLE.getDefaultState().with(MoldyBlock.STAGE, 3));

        world.setBlockState(new BlockPos(OX + 3, BASE_Y, OZ + 3),
            ModBlocks.MOLDY_BARREL.getDefaultState().with(MoldyBlock.STAGE, 3).with(BarrelBlock.FACING, Direction.NORTH));

        world.setBlockState(new BlockPos(OX + 2, BASE_Y + 2, OZ + 3), Blocks.LANTERN.getDefaultState());

        player.teleport(world, OX + 2.0, BASE_Y, OZ + 0.8, 0f, 10f);
        client.player.setYaw(0f);
        client.player.setPitch(10f);
    }
}
