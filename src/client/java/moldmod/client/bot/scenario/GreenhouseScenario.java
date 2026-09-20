package moldmod.client.bot.scenario;

import moldmod.SporesShadows;
import moldmod.block.ModBlocks;
import moldmod.block.core.MoldyBlock;
import moldmod.client.bot.util.BotHelper;
import net.minecraft.block.*;
import net.minecraft.block.enums.SlabType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class GreenhouseScenario implements BotScenario {
    public static final int OX = 420;
    public static final int OZ = 0;
    public static final int BASE_Y = 120;

    @Override
    public String getName() {
        return "Scenario 8: Decaying Greenhouse";
    }

    @Override
    public String getScreenshotFilename() {
        return "scenario_08_decaying_greenhouse.png";
    }

    @Override
    public int getWaitTicks() {
        return 50;
    }

    @Override
    public void prepare(ServerWorld world, ServerPlayerEntity player, MinecraftClient client) {
        client.options.setPerspective(Perspective.FIRST_PERSON);

        // Clear space
        BotHelper.fill(world, OX - 6, BASE_Y - 2, OZ - 3, OX + 6, BASE_Y + 9, OZ + 12, Blocks.AIR);
        BotHelper.clearDroppedItems(world, new BlockPos(OX, BASE_Y, OZ + 4), 20.0);

        Block moldyLog = Registries.BLOCK.get(SporesShadows.id("moldy_oak_log"));
        Block moldyPlanks = Registries.BLOCK.get(SporesShadows.id("moldy_oak_planks"));
        Block moldySlab = Registries.BLOCK.get(SporesShadows.id("moldy_oak_slab"));

        // Set daytime for greenhouse glass lighting
        world.setTimeOfDay(4000L);

        // 1. Foundation & Natural Floor (Moss, Mud, Puddles)
        BotHelper.fill(world, OX - 5, BASE_Y - 2, OZ - 2, OX + 5, BASE_Y - 2, OZ + 11, Blocks.DIRT);

        for (int x = -4; x <= 4; x++) {
            for (int z = -1; z <= 10; z++) {
                int hash = Math.abs(x * 13 + z * 5 + (x ^ z));
                BlockPos pos = new BlockPos(OX + x, BASE_Y - 1, OZ + z);
                if (x == 0 || x == -1) {
                    // Center rotting boardwalk
                    world.setBlockState(pos, moldyPlanks.getDefaultState().with(MoldyBlock.STAGE, (hash % 2 == 0) ? 3 : 2));
                } else {
                    Block g = (hash % 4 == 0) ? Blocks.MUD :
                             (hash % 4 == 1) ? Blocks.MOSS_BLOCK :
                             (hash % 4 == 2) ? Blocks.MOSS_CARPET : Blocks.MOSS_BLOCK;
                    world.setBlockState(pos, g.getDefaultState());
                }
            }
        }

        // Puddle with waterlogged slab
        world.setBlockState(new BlockPos(OX + 1, BASE_Y - 1, OZ + 3),
            Blocks.MOSSY_COBBLESTONE_SLAB.getDefaultState().with(SlabBlock.TYPE, SlabType.BOTTOM).with(SlabBlock.WATERLOGGED, true));
        world.setBlockState(new BlockPos(OX + 1, BASE_Y - 1, OZ + 4),
            Blocks.MOSSY_COBBLESTONE_SLAB.getDefaultState().with(SlabBlock.TYPE, SlabType.BOTTOM).with(SlabBlock.WATERLOGGED, true));

        // 2. Glass Walls with Moldy Wooden Framework
        // Base low wall (Y = 0)
        for (int z = -1; z <= 10; z++) {
            world.setBlockState(new BlockPos(OX - 4, BASE_Y, OZ + z), Blocks.MOSSY_STONE_BRICKS.getDefaultState());
            world.setBlockState(new BlockPos(OX + 4, BASE_Y, OZ + z), Blocks.MOSSY_STONE_BRICKS.getDefaultState());
        }

        // Glass side walls (Y = 1 to 3)
        for (int z = -1; z <= 10; z++) {
            for (int y = 1; y <= 3; y++) {
                // Ribs at z = 1, 4, 7, 10
                if (z % 3 == 1) {
                    world.setBlockState(new BlockPos(OX - 4, BASE_Y + y, OZ + z), moldyLog.getDefaultState().with(MoldyBlock.STAGE, 3));
                    world.setBlockState(new BlockPos(OX + 4, BASE_Y + y, OZ + z), moldyLog.getDefaultState().with(MoldyBlock.STAGE, 3));
                } else {
                    Block glass = (z + y) % 5 == 0 ? Blocks.AIR : Blocks.GLASS_PANE;
                    world.setBlockState(new BlockPos(OX - 4, BASE_Y + y, OZ + z), glass.getDefaultState());
                    world.setBlockState(new BlockPos(OX + 4, BASE_Y + y, OZ + z), glass.getDefaultState());
                }
            }
        }

        // Back wall at Z = 10
        for (int x = -4; x <= 4; x++) {
            for (int y = 0; y <= 4; y++) {
                if (Math.abs(x) == 4 || y == 0 || y == 4) {
                    world.setBlockState(new BlockPos(OX + x, BASE_Y + y, OZ + 10), moldyLog.getDefaultState().with(MoldyBlock.STAGE, 2));
                } else {
                    world.setBlockState(new BlockPos(OX + x, BASE_Y + y, OZ + 10), Blocks.GLASS.getDefaultState());
                }
            }
        }

        // 3. Arched Glass Roof (Y = 4 and 5)
        for (int z = -1; z <= 10; z++) {
            boolean isRib = (z % 3 == 1);
            BlockState rib = isRib ? moldyLog.getDefaultState().with(MoldyBlock.STAGE, 3).with(PillarBlock.AXIS, Direction.Axis.X) : Blocks.GLASS.getDefaultState();

            world.setBlockState(new BlockPos(OX - 3, BASE_Y + 4, OZ + z), rib);
            world.setBlockState(new BlockPos(OX + 3, BASE_Y + 4, OZ + z), rib);

            world.setBlockState(new BlockPos(OX - 2, BASE_Y + 5, OZ + z), Blocks.GLASS.getDefaultState());
            world.setBlockState(new BlockPos(OX - 1, BASE_Y + 5, OZ + z), Blocks.GLASS.getDefaultState());
            world.setBlockState(new BlockPos(OX, BASE_Y + 5, OZ + z), isRib ? moldyLog.getDefaultState().with(MoldyBlock.STAGE, 2).with(PillarBlock.AXIS, Direction.Axis.Z) : Blocks.GLASS.getDefaultState());
            world.setBlockState(new BlockPos(OX + 1, BASE_Y + 5, OZ + z), Blocks.GLASS.getDefaultState());
            world.setBlockState(new BlockPos(OX + 2, BASE_Y + 5, OZ + z), Blocks.GLASS.getDefaultState());
        }

        // 4. Overgrown Greenhouse Flora & Mold Composter
        // Left side: Overflowing Moldy Composter & Spores
        world.setBlockState(new BlockPos(OX - 3, BASE_Y, OZ + 3),
            ModBlocks.MOLDY_COMPOSTER.getDefaultState().with(MoldyBlock.STAGE, 3));
        world.setBlockState(new BlockPos(OX - 3, BASE_Y, OZ + 4),
            ModBlocks.MOLDY_BARREL.getDefaultState().with(MoldyBlock.STAGE, 3).with(BarrelBlock.FACING, Direction.UP));
        world.setBlockState(new BlockPos(OX - 3, BASE_Y + 1, OZ + 4), Blocks.FLOWERING_AZALEA.getDefaultState());

        world.setBlockState(new BlockPos(OX - 2, BASE_Y, OZ + 5), Blocks.FERN.getDefaultState());
        world.setBlockState(new BlockPos(OX - 3, BASE_Y, OZ + 6), Blocks.BIG_DRIPLEAF.getDefaultState());

        // Right side: Moisture Detector, Barrels, Plants
        world.setBlockState(new BlockPos(OX + 3, BASE_Y, OZ + 3),
            ModBlocks.MOLDY_BARREL.getDefaultState().with(MoldyBlock.STAGE, 2).with(BarrelBlock.FACING, Direction.WEST));
        world.setBlockState(new BlockPos(OX + 3, BASE_Y + 1, OZ + 3),
            ModBlocks.MOISTURE_DETECTOR.getDefaultState().with(HorizontalFacingBlock.FACING, Direction.WEST));

        world.setBlockState(new BlockPos(OX + 2, BASE_Y, OZ + 5), Blocks.BROWN_MUSHROOM.getDefaultState());
        world.setBlockState(new BlockPos(OX + 3, BASE_Y, OZ + 5), Blocks.RED_MUSHROOM.getDefaultState());
        world.setBlockState(new BlockPos(OX + 3, BASE_Y, OZ + 7),
            ModBlocks.MOLDY_CRAFTING_TABLE.getDefaultState().with(MoldyBlock.STAGE, 3));
        world.setBlockState(new BlockPos(OX + 3, BASE_Y + 1, OZ + 7), Blocks.POTTED_WARPED_FUNGUS.getDefaultState());

        // 5. Hanging Spore Blossom from Roof Rib
        world.setBlockState(new BlockPos(OX, BASE_Y + 4, OZ + 4), Blocks.SPORE_BLOSSOM.getDefaultState());
        world.setBlockState(new BlockPos(OX, BASE_Y + 4, OZ + 7), Blocks.SPORE_BLOSSOM.getDefaultState());

        // Hanging lantern from rib
        world.setBlockState(new BlockPos(OX - 1, BASE_Y + 3, OZ + 3), Blocks.LANTERN.getDefaultState().with(LanternBlock.HANGING, true));

        // Cobwebs hanging from glass arches
        world.setBlockState(new BlockPos(OX - 2, BASE_Y + 3, OZ + 4), Blocks.COBWEB.getDefaultState());
        world.setBlockState(new BlockPos(OX + 2, BASE_Y + 3, OZ + 6), Blocks.COBWEB.getDefaultState());

        // 6. Camera Setup
        player.teleport(world, OX - 0.5, BASE_Y, OZ + 0.5, 0f, 6f);
        client.player.setYaw(0f);
        client.player.setPitch(6f);
    }

    @Override
    public void onCapture(ServerWorld world, ServerPlayerEntity player, MinecraftClient client) {
        world.spawnParticles(ParticleTypes.SPORE_BLOSSOM_AIR, OX + 0.0, BASE_Y + 1.8, OZ + 4.5, 60, 2.0, 1.2, 3.0, 0.01);
    }

    @Override
    public void cleanup(ServerWorld world, ServerPlayerEntity player, MinecraftClient client) {
        world.setTimeOfDay(18000L);
    }
}
