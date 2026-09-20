package moldmod.client.bot.scenario;

import moldmod.SporesShadows;
import moldmod.block.ModBlocks;
import moldmod.block.core.MoldyBlock;
import moldmod.block.machine.dehumidifier.DehumidifierBlock;
import moldmod.block.machine.dehumidifier.DehumidifierStatus;
import moldmod.block.machine.purifier.AirPurifierBlock;
import moldmod.block.machine.purifier.PurifierStatus;
import moldmod.block.sensor.MoistureDetectorBlock;
import moldmod.block.sensor.SporeDetectorBlock;
import moldmod.client.bot.util.BotHelper;
import net.minecraft.block.*;
import net.minecraft.block.enums.BlockFace;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class AlchemistScenario implements BotScenario {
    public static final int OX = 360;
    public static final int OZ = 0;
    public static final int BASE_Y = 120;

    @Override
    public String getName() {
        return "Scenario 7: Contaminated Alchemist's Laboratory";
    }

    @Override
    public String getScreenshotFilename() {
        return "scenario_07_contaminated_lab.png";
    }

    @Override
    public int getWaitTicks() {
        return 50;
    }

    @Override
    public void prepare(ServerWorld world, ServerPlayerEntity player, MinecraftClient client) {
        client.options.setPerspective(Perspective.FIRST_PERSON);

        // Clear space
        BotHelper.fill(world, OX - 6, BASE_Y - 2, OZ - 3, OX + 6, BASE_Y + 8, OZ + 12, Blocks.AIR);
        BotHelper.clearDroppedItems(world, new BlockPos(OX, BASE_Y, OZ + 4), 20.0);

        Block moldyLog = Registries.BLOCK.get(SporesShadows.id("moldy_oak_log"));

        // 1. Sub-floor & Deepslate Flagstone Floor (Y = BASE_Y - 1)
        BotHelper.fill(world, OX - 5, BASE_Y - 2, OZ - 2, OX + 5, BASE_Y - 2, OZ + 11, Blocks.DEEPSLATE);

        for (int x = -4; x <= 4; x++) {
            for (int z = -1; z <= 10; z++) {
                int hash = Math.abs(x * 7 + z * 13 + (x ^ z));
                Block fb = (hash % 3 == 0) ? Blocks.CRACKED_DEEPSLATE_TILES :
                           (hash % 3 == 1) ? Blocks.DEEPSLATE_TILES : Blocks.POLISHED_DEEPSLATE;
                world.setBlockState(new BlockPos(OX + x, BASE_Y - 1, OZ + z), fb.getDefaultState());
            }
        }

        // 2. Deepslate Walls & Vaulted Ceiling
        for (int z = -1; z <= 10; z++) {
            for (int y = 0; y <= 4; y++) {
                world.setBlockState(new BlockPos(OX - 4, BASE_Y + y, OZ + z), Blocks.DEEPSLATE_BRICKS.getDefaultState());
                world.setBlockState(new BlockPos(OX + 4, BASE_Y + y, OZ + z), Blocks.DEEPSLATE_BRICKS.getDefaultState());
            }
        }

        // Back wall at Z = 10
        for (int x = -4; x <= 4; x++) {
            for (int y = 0; y <= 4; y++) {
                Block wb = (x % 2 == 0) ? Blocks.CRACKED_DEEPSLATE_BRICKS : Blocks.DEEPSLATE_BRICKS;
                world.setBlockState(new BlockPos(OX + x, BASE_Y + y, OZ + 10), wb.getDefaultState());
            }
        }

        // Vaulted Arch ceiling at Y = 4 and 5
        for (int z = -1; z <= 10; z++) {
            world.setBlockState(new BlockPos(OX - 3, BASE_Y + 4, OZ + z),
                Blocks.DEEPSLATE_BRICK_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.EAST));
            world.setBlockState(new BlockPos(OX + 3, BASE_Y + 4, OZ + z),
                Blocks.DEEPSLATE_BRICK_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.WEST));

            for (int x = -2; x <= 2; x++) {
                world.setBlockState(new BlockPos(OX + x, BASE_Y + 5, OZ + z), Blocks.DEEPSLATE_TILES.getDefaultState());
            }
        }

        // 3. Moldy Timber Reinforcements at Z = 2 and Z = 6
        int[] archZ = {2, 6};
        for (int az : archZ) {
            world.setBlockState(new BlockPos(OX - 3, BASE_Y + 1, OZ + az), moldyLog.getDefaultState().with(MoldyBlock.STAGE, 3));
            world.setBlockState(new BlockPos(OX - 3, BASE_Y + 2, OZ + az), moldyLog.getDefaultState().with(MoldyBlock.STAGE, 3));
            world.setBlockState(new BlockPos(OX - 3, BASE_Y + 3, OZ + az), moldyLog.getDefaultState().with(MoldyBlock.STAGE, 2));

            world.setBlockState(new BlockPos(OX + 3, BASE_Y + 1, OZ + az), moldyLog.getDefaultState().with(MoldyBlock.STAGE, 3));
            world.setBlockState(new BlockPos(OX + 3, BASE_Y + 2, OZ + az), moldyLog.getDefaultState().with(MoldyBlock.STAGE, 3));
            world.setBlockState(new BlockPos(OX + 3, BASE_Y + 3, OZ + az), moldyLog.getDefaultState().with(MoldyBlock.STAGE, 2));

            for (int x = -3; x <= 3; x++) {
                world.setBlockState(new BlockPos(OX + x, BASE_Y + 4, OZ + az),
                    moldyLog.getDefaultState().with(MoldyBlock.STAGE, 3).with(PillarBlock.AXIS, Direction.Axis.X));
            }
        }

        // 4. Centerpiece: Alchemical Station & Contaminated Books (Left & Center)
        // Table row on left
        world.setBlockState(new BlockPos(OX - 2, BASE_Y, OZ + 4),
            ModBlocks.MOLDY_CRAFTING_TABLE.getDefaultState().with(MoldyBlock.STAGE, 3));
        world.setBlockState(new BlockPos(OX - 2, BASE_Y + 1, OZ + 4), Blocks.BREWING_STAND.getDefaultState());

        world.setBlockState(new BlockPos(OX - 2, BASE_Y, OZ + 5), Blocks.CAULDRON.getDefaultState());

        world.setBlockState(new BlockPos(OX - 3, BASE_Y, OZ + 4),
            ModBlocks.MOLDY_CHISELED_BOOKSHELF.getDefaultState().with(MoldyBlock.STAGE, 3).with(HorizontalFacingBlock.FACING, Direction.EAST));
        world.setBlockState(new BlockPos(OX - 3, BASE_Y + 1, OZ + 4),
            ModBlocks.MOLDY_BOOKSHELF.getDefaultState().with(MoldyBlock.STAGE, 3));

        // Potted Warped and Crimson Mushrooms (toxic specimen jars)
        world.setBlockState(new BlockPos(OX - 3, BASE_Y, OZ + 6), Blocks.POLISHED_DEEPSLATE.getDefaultState());
        world.setBlockState(new BlockPos(OX - 3, BASE_Y + 1, OZ + 6), Blocks.POTTED_WARPED_FUNGUS.getDefaultState());

        world.setBlockState(new BlockPos(OX - 2, BASE_Y, OZ + 6), Blocks.POLISHED_DEEPSLATE.getDefaultState());
        world.setBlockState(new BlockPos(OX - 2, BASE_Y + 1, OZ + 6), Blocks.POTTED_CRIMSON_FUNGUS.getDefaultState());

        // Lectern with moldy grim book
        world.setBlockState(new BlockPos(OX - 1, BASE_Y, OZ + 4),
            ModBlocks.MOLDY_LECTERN.getDefaultState().with(MoldyBlock.STAGE, 3).with(HorizontalFacingBlock.FACING, Direction.NORTH));

        // 5. Emergency Filtration System (Right Side): Air Purifier & Sensors
        BlockPos purifierPos = new BlockPos(OX + 2, BASE_Y, OZ + 4);
        world.setBlockState(purifierPos, ModBlocks.AIR_PURIFIER.getDefaultState()
            .with(HorizontalFacingBlock.FACING, Direction.NORTH)
            .with(AirPurifierBlock.STATUS, PurifierStatus.RUNNING)
            .with(AirPurifierBlock.FILTER_LEVEL, 3));

        BlockPos dehumPos = new BlockPos(OX + 3, BASE_Y, OZ + 5);
        world.setBlockState(dehumPos, ModBlocks.DEHUMIDIFIER.getDefaultState()
            .with(HorizontalFacingBlock.FACING, Direction.WEST)
            .with(DehumidifierBlock.STATUS, DehumidifierStatus.RUNNING)
            .with(DehumidifierBlock.WATER_LEVEL, 2));

        // Spore Detector on wall glowing with redstone alert
        world.setBlockState(new BlockPos(OX + 3, BASE_Y + 1, OZ + 4),
            ModBlocks.SPORE_DETECTOR.getDefaultState()
                .with(SporeDetectorBlock.FACE, BlockFace.WALL)
                .with(SporeDetectorBlock.FACING, Direction.WEST)
                .with(SporeDetectorBlock.TOXICITY_LEVEL, 2));

        // Moisture Detector
        world.setBlockState(new BlockPos(OX + 3, BASE_Y + 2, OZ + 4),
            ModBlocks.MOISTURE_DETECTOR.getDefaultState()
                .with(MoistureDetectorBlock.FACE, BlockFace.WALL)
                .with(MoistureDetectorBlock.FACING, Direction.WEST)
                .with(MoistureDetectorBlock.MOISTURE_STAGE, 2));

        // Barrels of chemical reagents
        world.setBlockState(new BlockPos(OX + 3, BASE_Y, OZ + 6),
            ModBlocks.MOLDY_BARREL.getDefaultState().with(MoldyBlock.STAGE, 3).with(BarrelBlock.FACING, Direction.WEST));
        world.setBlockState(new BlockPos(OX + 3, BASE_Y + 1, OZ + 6),
            ModBlocks.MOLDY_BARREL.getDefaultState().with(MoldyBlock.STAGE, 2).with(BarrelBlock.FACING, Direction.UP));

        // 6. Lighting & Spore Atmosphere
        // Soul Lantern hanging from ceiling
        world.setBlockState(new BlockPos(OX, BASE_Y + 3, OZ + 4), Blocks.CHAIN.getDefaultState().with(ChainBlock.AXIS, Direction.Axis.Y));
        world.setBlockState(new BlockPos(OX, BASE_Y + 2, OZ + 4), Blocks.SOUL_LANTERN.getDefaultState().with(LanternBlock.HANGING, true));

        // Lit candles on the back shelf (cyan and purple)
        world.setBlockState(new BlockPos(OX, BASE_Y, OZ + 8), Blocks.CHISELED_DEEPSLATE.getDefaultState());
        world.setBlockState(new BlockPos(OX, BASE_Y + 1, OZ + 8),
            Blocks.PURPLE_CANDLE.getDefaultState().with(CandleBlock.LIT, true).with(CandleBlock.CANDLES, 3));

        // Spore Blossom hanging above
        world.setBlockState(new BlockPos(OX, BASE_Y + 4, OZ + 5), Blocks.SPORE_BLOSSOM.getDefaultState());

        // Cobwebs draped over contaminated shelves
        world.setBlockState(new BlockPos(OX - 3, BASE_Y + 2, OZ + 4), Blocks.COBWEB.getDefaultState());
        world.setBlockState(new BlockPos(OX + 3, BASE_Y + 2, OZ + 6), Blocks.COBWEB.getDefaultState());

        // 7. Camera Positioning
        player.teleport(world, OX + 0.0, BASE_Y, OZ + 0.8, 0f, 6f);
        client.player.setYaw(0f);
        client.player.setPitch(6f);
    }

    @Override
    public void onCapture(ServerWorld world, ServerPlayerEntity player, MinecraftClient client) {
        // Alchemical brewing steam and spores
        world.spawnParticles(ParticleTypes.WITCH, OX - 2.0, BASE_Y + 1.8, OZ + 4.0, 15, 0.3, 0.3, 0.3, 0.02);
        world.spawnParticles(ParticleTypes.SPORE_BLOSSOM_AIR, OX + 0.0, BASE_Y + 1.8, OZ + 4.5, 45, 1.8, 1.0, 2.5, 0.01);
        world.spawnParticles(ParticleTypes.GLOW, OX + 2.0, BASE_Y + 1.2, OZ + 4.0, 10, 0.2, 0.4, 0.2, 0.01);
    }
}
