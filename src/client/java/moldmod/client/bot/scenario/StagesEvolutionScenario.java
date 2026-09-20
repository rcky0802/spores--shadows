package moldmod.client.bot.scenario;

import moldmod.block.ModBlocks;
import moldmod.block.core.MoldyBlock;
import moldmod.block.workstation.MoldyChestBlockEntity;
import moldmod.client.bot.util.BotHelper;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class StagesEvolutionScenario implements BotScenario {
    public static final int OX = -120;
    public static final int OZ = 0;
    public static final int BASE_Y = 120;

    @Override
    public String getName() {
        return "Scenario 5: Mold Stages Evolution Showcase (0 -> 3)";
    }

    @Override
    public String getScreenshotFilename() {
        return "scenario_05_mold_stages_evolution.png";
    }

    @Override
    public void prepare(ServerWorld world, ServerPlayerEntity player, MinecraftClient client) {
        client.options.setPerspective(Perspective.FIRST_PERSON);

        BotHelper.fill(world, OX - 5, BASE_Y - 1, OZ - 2, OX + 5, BASE_Y + 5, OZ + 6, Blocks.AIR);

        // Floor: Polished Deepslate
        for (int x = -5; x <= 5; x++) {
            for (int z = -2; z <= 6; z++) {
                world.setBlockState(new BlockPos(OX + x, BASE_Y - 1, OZ + z), Blocks.POLISHED_DEEPSLATE.getDefaultState());
            }
        }

        // Backdrop wall
        for (int x = -5; x <= 5; x++) {
            for (int y = 0; y <= 4; y++) {
                world.setBlockState(new BlockPos(OX + x, BASE_Y + y, OZ + 5), Blocks.SMOOTH_STONE.getDefaultState());
            }
        }

        // Balanced lighting for 4 columns
        world.setBlockState(new BlockPos(OX - 2, BASE_Y + 3, OZ + 4), Blocks.LANTERN.getDefaultState());
        world.setBlockState(new BlockPos(OX + 1, BASE_Y + 3, OZ + 4), Blocks.LANTERN.getDefaultState());

        net.minecraft.block.Block moldyLog = Registries.BLOCK.get(moldmod.SporesShadows.id("moldy_oak_log"));

        // 4 Columns: strictly Stages 0 to 3 from Left (+X) to Right (-X)
        // Col 0 (Leftmost): STAGE 0 (Clean Vanilla)
        BlockPos chestPos0 = new BlockPos(OX + 1, BASE_Y, OZ + 3);
        world.setBlockState(chestPos0, Blocks.CHEST.getDefaultState().with(HorizontalFacingBlock.FACING, Direction.NORTH));
        world.setBlockState(new BlockPos(OX + 1, BASE_Y + 1, OZ + 3), Blocks.OAK_LOG.getDefaultState());

        // Col 1: STAGE 1 (Incipient Mold)
        BlockPos chestPos1 = new BlockPos(OX, BASE_Y, OZ + 3);
        world.setBlockState(chestPos1, ModBlocks.MOLDY_CHEST.getDefaultState()
            .with(MoldyBlock.STAGE, 1).with(HorizontalFacingBlock.FACING, Direction.NORTH));
        world.setBlockState(new BlockPos(OX, BASE_Y + 1, OZ + 3), moldyLog.getDefaultState().with(MoldyBlock.STAGE, 1));
        if (world.getBlockEntity(chestPos1) instanceof MoldyChestBlockEntity be1) {
            be1.setMoldStage(1);
        }

        // Col 2: STAGE 2 (Active Mold)
        BlockPos chestPos2 = new BlockPos(OX - 1, BASE_Y, OZ + 3);
        world.setBlockState(chestPos2, ModBlocks.MOLDY_CHEST.getDefaultState()
            .with(MoldyBlock.STAGE, 2).with(HorizontalFacingBlock.FACING, Direction.NORTH));
        world.setBlockState(new BlockPos(OX - 1, BASE_Y + 1, OZ + 3), moldyLog.getDefaultState().with(MoldyBlock.STAGE, 2));
        if (world.getBlockEntity(chestPos2) instanceof MoldyChestBlockEntity be2) {
            be2.setMoldStage(2);
        }

        // Col 3 (Rightmost): STAGE 3 (Fully Rotten & Decayed)
        BlockPos chestPos3 = new BlockPos(OX - 2, BASE_Y, OZ + 3);
        world.setBlockState(chestPos3, ModBlocks.MOLDY_CHEST.getDefaultState()
            .with(MoldyBlock.STAGE, 3).with(HorizontalFacingBlock.FACING, Direction.NORTH));
        world.setBlockState(new BlockPos(OX - 2, BASE_Y + 1, OZ + 3), moldyLog.getDefaultState().with(MoldyBlock.STAGE, 3));
        if (world.getBlockEntity(chestPos3) instanceof MoldyChestBlockEntity be3) {
            be3.setMoldStage(3);
        }

        // Camera centered directly in front of the 4 columns
        player.teleport(world, OX - 0.5, BASE_Y, OZ - 0.5, 0f, 10f);
        client.player.setYaw(0f);
        client.player.setPitch(10f);

        BotHelper.clearDroppedItems(world, new BlockPos(OX, BASE_Y, OZ + 3), 15.0);
    }
}
