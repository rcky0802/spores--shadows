package moldmod.client.bot.scenario;

import moldmod.block.ModBlocks;
import moldmod.block.machine.dehumidifier.DehumidifierBlock;
import moldmod.block.machine.dehumidifier.DehumidifierBlockEntity;
import moldmod.block.machine.dehumidifier.DehumidifierStatus;
import moldmod.block.machine.purifier.AirPurifierBlock;
import moldmod.block.machine.purifier.AirPurifierBlockEntity;
import moldmod.block.machine.purifier.PurifierStatus;
import moldmod.block.sensor.MoistureDetectorBlock;
import moldmod.block.sensor.SporeDetectorBlock;
import moldmod.client.bot.util.BotHelper;
import moldmod.item.ModItems;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.enums.BlockFace;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class FiltrationScenario implements BotScenario {
    public static final int OX = -60;
    public static final int OZ = 0;
    public static final int BASE_Y = 120;

    @Override
    public String getName() {
        return "Scenario 3: Industrial Filtration Chamber (Machines in Action)";
    }

    @Override
    public String getScreenshotFilename() {
        return "scenario_03_filtration_machines.png";
    }

    @Override
    public void prepare(ServerWorld world, ServerPlayerEntity player, MinecraftClient client) {
        client.options.setPerspective(Perspective.FIRST_PERSON);

        BotHelper.fill(world, OX - 4, BASE_Y - 1, OZ - 2, OX + 4, BASE_Y + 5, OZ + 6, Blocks.AIR);

        // Polished floor
        for (int x = -4; x <= 4; x++) {
            for (int z = -2; z <= 6; z++) {
                world.setBlockState(new BlockPos(OX + x, BASE_Y - 1, OZ + z),
                    (x % 2 == 0) ? Blocks.SMOOTH_STONE.getDefaultState() : Blocks.POLISHED_ANDESITE.getDefaultState());
            }
        }

        // Back wall: Polished Deepslate
        for (int x = -4; x <= 4; x++) {
            for (int y = 0; y <= 4; y++) {
                world.setBlockState(new BlockPos(OX + x, BASE_Y + y, OZ + 5), Blocks.POLISHED_DEEPSLATE.getDefaultState());
            }
        }

        // Iron bars on back wall for industrial accent
        world.setBlockState(new BlockPos(OX - 2, BASE_Y + 2, OZ + 5), Blocks.IRON_BARS.getDefaultState());
        world.setBlockState(new BlockPos(OX + 2, BASE_Y + 2, OZ + 5), Blocks.IRON_BARS.getDefaultState());

        // Ambient lighting
        world.setBlockState(new BlockPos(OX - 3, BASE_Y + 2, OZ + 4), Blocks.SOUL_LANTERN.getDefaultState());
        world.setBlockState(new BlockPos(OX + 3, BASE_Y + 2, OZ + 4), Blocks.SOUL_LANTERN.getDefaultState());
        world.setBlockState(new BlockPos(OX, BASE_Y + 3, OZ + 4), Blocks.CHAIN.getDefaultState());
        world.setBlockState(new BlockPos(OX, BASE_Y + 2, OZ + 4), Blocks.LANTERN.getDefaultState());

        // Left duo: Dehumidifier machine (RUNNING, active vapor) + Moisture Detector wall instrument
        BlockPos dehumPos = new BlockPos(OX - 2, BASE_Y, OZ + 3);
        world.setBlockState(dehumPos, ModBlocks.DEHUMIDIFIER.getDefaultState()
            .with(HorizontalFacingBlock.FACING, Direction.NORTH)
            .with(DehumidifierBlock.STATUS, DehumidifierStatus.RUNNING)
            .with(DehumidifierBlock.WATER_LEVEL, 2));
        if (world.getBlockEntity(dehumPos) instanceof DehumidifierBlockEntity be) {
            be.setEnergy(10000);
            be.setWaterMb(2400);
            be.setStack(0, new ItemStack(Items.BUCKET));
        }

        world.setBlockState(new BlockPos(OX - 1, BASE_Y + 1, OZ + 4), ModBlocks.MOISTURE_DETECTOR.getDefaultState()
            .with(MoistureDetectorBlock.FACE, BlockFace.WALL)
            .with(MoistureDetectorBlock.FACING, Direction.NORTH)
            .with(MoistureDetectorBlock.MOISTURE_STAGE, 2));

        // Right duo: Spore Detector wall instrument + Air Purifier machine (RUNNING, active intake)
        world.setBlockState(new BlockPos(OX + 1, BASE_Y + 1, OZ + 4), ModBlocks.SPORE_DETECTOR.getDefaultState()
            .with(SporeDetectorBlock.FACE, BlockFace.WALL)
            .with(SporeDetectorBlock.FACING, Direction.NORTH)
            .with(SporeDetectorBlock.TOXICITY_LEVEL, 2));

        BlockPos purifierPos = new BlockPos(OX + 2, BASE_Y, OZ + 3);
        world.setBlockState(purifierPos, ModBlocks.AIR_PURIFIER.getDefaultState()
            .with(HorizontalFacingBlock.FACING, Direction.NORTH)
            .with(AirPurifierBlock.STATUS, PurifierStatus.RUNNING)
            .with(AirPurifierBlock.FILTER_LEVEL, 3));
        if (world.getBlockEntity(purifierPos) instanceof AirPurifierBlockEntity pbe) {
            pbe.setEnergy(10000);
            pbe.setFilterWearTicks(10000);
            pbe.setStack(0, new ItemStack(ModItems.SPORE_FILTER));
        }

        // Centered camera facing NORTH (no redstone dust)
        player.teleport(world, OX + 0.0, BASE_Y, OZ + 0.5, 0f, 10f);
        client.player.setYaw(0f);
        client.player.setPitch(10f);

        // Ensure all dropped items in the room are discarded
        BotHelper.clearDroppedItems(world, new BlockPos(OX, BASE_Y, OZ + 3), 15.0);
    }

    @Override
    public void onCapture(ServerWorld world, ServerPlayerEntity player, MinecraftClient client) {
        BotHelper.clearDroppedItems(world, new BlockPos(OX, BASE_Y, OZ + 3), 15.0);

        // Active vapor / cloud particles emitted from machine grates
        world.spawnParticles(ParticleTypes.CLOUD, OX - 2 + 0.5, BASE_Y + 1.05, OZ + 3.5, 14, 0.15, 0.05, 0.15, 0.02);
        world.spawnParticles(ParticleTypes.CLOUD, OX + 2 + 0.5, BASE_Y + 1.05, OZ + 3.5, 14, 0.15, 0.05, 0.15, 0.02);
    }
}
