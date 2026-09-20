package moldmod.client.bot.scenario;

import moldmod.SporesShadows;
import moldmod.block.ModBlocks;
import moldmod.block.core.MoldyBlock;
import moldmod.client.bot.util.BotHelper;
import net.minecraft.block.*;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.block.enums.SlabType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class WatermillScenario implements BotScenario {
    public static final int OX = 540;
    public static final int OZ = 0;
    public static final int BASE_Y = 120;

    @Override
    public String getName() {
        return "Scenario 10: Swamp Watermill Wheelhouse";
    }

    @Override
    public String getScreenshotFilename() {
        return "scenario_10_swamp_watermill.png";
    }

    @Override
    public int getWaitTicks() {
        return 50;
    }

    @Override
    public void prepare(ServerWorld world, ServerPlayerEntity player, MinecraftClient client) {
        client.options.setPerspective(Perspective.FIRST_PERSON);

        // Clear ample space
        BotHelper.fill(world, OX - 6, BASE_Y - 3, OZ - 3, OX + 6, BASE_Y + 9, OZ + 12, Blocks.AIR);
        BotHelper.clearDroppedItems(world, new BlockPos(OX, BASE_Y, OZ + 4), 20.0);

        Block moldyLog = Registries.BLOCK.get(SporesShadows.id("moldy_oak_log"));
        Block moldyPlanks = Registries.BLOCK.get(SporesShadows.id("moldy_oak_planks"));
        Block moldyStairs = Registries.BLOCK.get(SporesShadows.id("moldy_oak_stairs"));
        Block moldySlab = Registries.BLOCK.get(SporesShadows.id("moldy_oak_slab"));
        Block moldyFence = Registries.BLOCK.get(SporesShadows.id("moldy_oak_fence"));

        // 1. Foundation Riverbed (Y = BASE_Y - 2)
        BotHelper.fill(world, OX - 5, BASE_Y - 2, OZ - 2, OX + 5, BASE_Y - 2, OZ + 11, Blocks.MUD);

        // 2. Water Channel (X = -1 to +3, Z = -1 to 10, Y = BASE_Y - 1 and BASE_Y)
        for (int x = -1; x <= 3; x++) {
            for (int z = -1; z <= 10; z++) {
                world.setBlockState(new BlockPos(OX + x, BASE_Y - 1, OZ + z), Blocks.WATER.getDefaultState());
                world.setBlockState(new BlockPos(OX + x, BASE_Y, OZ + z), Blocks.WATER.getDefaultState());
            }
        }

        // Floating Lily Pads
        world.setBlockState(new BlockPos(OX - 1, BASE_Y + 1, OZ + 2), Blocks.LILY_PAD.getDefaultState());
        world.setBlockState(new BlockPos(OX, BASE_Y + 1, OZ + 6), Blocks.LILY_PAD.getDefaultState());
        world.setBlockState(new BlockPos(OX + 2, BASE_Y + 1, OZ + 7), Blocks.LILY_PAD.getDefaultState());

        // 3. Heavy Stone Mill Wall (East Wall at X = 4)
        for (int z = -1; z <= 10; z++) {
            for (int y = -1; y <= 5; y++) {
                Block mb = ((z + y) % 3 == 0) ? Blocks.MOSSY_STONE_BRICKS : Blocks.STONE_BRICKS;
                world.setBlockState(new BlockPos(OX + 4, BASE_Y + y, OZ + z), mb.getDefaultState());
            }
        }

        // Back wall of sluice at Z = 10
        for (int x = -4; x <= 4; x++) {
            for (int y = -1; y <= 5; y++) {
                world.setBlockState(new BlockPos(OX + x, BASE_Y + y, OZ + 10), Blocks.CRACKED_STONE_BRICKS.getDefaultState());
            }
        }

        // Left containment wall (X = -4)
        for (int z = -1; z <= 10; z++) {
            for (int y = -1; y <= 5; y++) {
                world.setBlockState(new BlockPos(OX - 4, BASE_Y + y, OZ + z), Blocks.MOSSY_COBBLESTONE.getDefaultState());
            }
        }

        // 4. Inspection Catwalk (Left Side: X = -3 and -2, Y = BASE_Y + 1)
        for (int z = 0; z <= 8; z++) {
            world.setBlockState(new BlockPos(OX - 3, BASE_Y + 1, OZ + z), moldyPlanks.getDefaultState().with(MoldyBlock.STAGE, 3));
            world.setBlockState(new BlockPos(OX - 2, BASE_Y + 1, OZ + z), moldyPlanks.getDefaultState().with(MoldyBlock.STAGE, 2));

            // Support pillars underneath catwalk
            if (z % 3 == 0) {
                world.setBlockState(new BlockPos(OX - 2, BASE_Y, OZ + z), moldyLog.getDefaultState().with(MoldyBlock.STAGE, 3));
                world.setBlockState(new BlockPos(OX - 2, BASE_Y - 1, OZ + z), moldyLog.getDefaultState().with(MoldyBlock.STAGE, 3));
            }

            // Railing along water edge
            if (z != 3 && z != 4) {
                world.setBlockState(new BlockPos(OX - 2, BASE_Y + 2, OZ + z), moldyFence.getDefaultState().with(MoldyBlock.STAGE, 2));
            }
        }

        // 5. The Massive Waterwheel (Centred at X = 1, Z = 5)
        // Horizontal Axle penetrating the stone mill wall at Y = 2
        for (int x = 0; x <= 4; x++) {
            world.setBlockState(new BlockPos(OX + x, BASE_Y + 2, OZ + 5),
                moldyLog.getDefaultState().with(MoldyBlock.STAGE, 3).with(PillarBlock.AXIS, Direction.Axis.X));
        }

        // Wheel Structure around axle at X = 1:
        // Center hub
        int wx = 1;
        int wz = 5;
        int wy = BASE_Y + 2;

        // Vertical spokes (Z = 5, Y from wy - 2 to wy + 2)
        world.setBlockState(new BlockPos(OX + wx, wy + 1, OZ + wz), moldyLog.getDefaultState().with(MoldyBlock.STAGE, 3).with(PillarBlock.AXIS, Direction.Axis.Y));
        world.setBlockState(new BlockPos(OX + wx, wy + 2, OZ + wz), moldyPlanks.getDefaultState().with(MoldyBlock.STAGE, 3));

        world.setBlockState(new BlockPos(OX + wx, wy - 1, OZ + wz), moldyLog.getDefaultState().with(MoldyBlock.STAGE, 3).with(PillarBlock.AXIS, Direction.Axis.Y));
        world.setBlockState(new BlockPos(OX + wx, wy - 2, OZ + wz), moldyPlanks.getDefaultState().with(MoldyBlock.STAGE, 3));

        // Horizontal spokes (Y = wy, Z from wz - 2 to wz + 2)
        world.setBlockState(new BlockPos(OX + wx, wy, OZ + wz - 1), moldyLog.getDefaultState().with(MoldyBlock.STAGE, 3).with(PillarBlock.AXIS, Direction.Axis.Z));
        world.setBlockState(new BlockPos(OX + wx, wy, OZ + wz - 2), moldyPlanks.getDefaultState().with(MoldyBlock.STAGE, 3));

        world.setBlockState(new BlockPos(OX + wx, wy, OZ + wz + 1), moldyLog.getDefaultState().with(MoldyBlock.STAGE, 3).with(PillarBlock.AXIS, Direction.Axis.Z));
        world.setBlockState(new BlockPos(OX + wx, wy, OZ + wz + 2), moldyPlanks.getDefaultState().with(MoldyBlock.STAGE, 3));

        // Diagonal wheel rim (Outer paddle circumference)
        world.setBlockState(new BlockPos(OX + wx, wy + 2, OZ + wz - 1), moldyStairs.getDefaultState().with(MoldyBlock.STAGE, 3).with(StairsBlock.FACING, Direction.SOUTH));
        world.setBlockState(new BlockPos(OX + wx, wy + 1, OZ + wz - 2), moldyStairs.getDefaultState().with(MoldyBlock.STAGE, 3).with(StairsBlock.FACING, Direction.SOUTH).with(StairsBlock.HALF, BlockHalf.TOP));

        world.setBlockState(new BlockPos(OX + wx, wy + 2, OZ + wz + 1), moldyStairs.getDefaultState().with(MoldyBlock.STAGE, 3).with(StairsBlock.FACING, Direction.NORTH));
        world.setBlockState(new BlockPos(OX + wx, wy + 1, OZ + wz + 2), moldyStairs.getDefaultState().with(MoldyBlock.STAGE, 3).with(StairsBlock.FACING, Direction.NORTH).with(StairsBlock.HALF, BlockHalf.TOP));

        // 6. Floating & Stacked Barrels
        // Floating barrel in water near wheel
        world.setBlockState(new BlockPos(OX, BASE_Y, OZ + 3),
            ModBlocks.MOLDY_BARREL.getDefaultState().with(MoldyBlock.STAGE, 3).with(BarrelBlock.FACING, Direction.EAST));

        // Stacked barrels on the catwalk
        world.setBlockState(new BlockPos(OX - 3, BASE_Y + 2, OZ + 6),
            ModBlocks.MOLDY_BARREL.getDefaultState().with(MoldyBlock.STAGE, 3).with(BarrelBlock.FACING, Direction.UP));
        world.setBlockState(new BlockPos(OX - 3, BASE_Y + 3, OZ + 6),
            ModBlocks.MOLDY_BARREL.getDefaultState().with(MoldyBlock.STAGE, 2).with(BarrelBlock.FACING, Direction.NORTH));

        world.setBlockState(new BlockPos(OX - 3, BASE_Y + 2, OZ + 7),
            ModBlocks.MOLDY_BARREL.getDefaultState().with(MoldyBlock.STAGE, 3).with(BarrelBlock.FACING, Direction.EAST));

        // 7. Ceiling Beams & Hanging Lantern
        for (int x = -3; x <= 3; x++) {
            world.setBlockState(new BlockPos(OX + x, BASE_Y + 5, OZ + 4),
                moldyLog.getDefaultState().with(MoldyBlock.STAGE, 3).with(PillarBlock.AXIS, Direction.Axis.X));
        }

        // Lantern hanging over the water wheel casting reflections
        world.setBlockState(new BlockPos(OX, BASE_Y + 4, OZ + 4), Blocks.CHAIN.getDefaultState().with(ChainBlock.AXIS, Direction.Axis.Y));
        world.setBlockState(new BlockPos(OX, BASE_Y + 3, OZ + 4), Blocks.LANTERN.getDefaultState().with(LanternBlock.HANGING, true));

        // Dripstone dripping from water flume ceiling
        world.setBlockState(new BlockPos(OX + 1, BASE_Y + 6, OZ + 5), Blocks.WATER.getDefaultState());
        world.setBlockState(new BlockPos(OX + 1, BASE_Y + 5, OZ + 5), Blocks.DRIPSTONE_BLOCK.getDefaultState());
        world.setBlockState(new BlockPos(OX + 1, BASE_Y + 4, OZ + 5),
            Blocks.POINTED_DRIPSTONE.getDefaultState().with(PointedDripstoneBlock.VERTICAL_DIRECTION, Direction.DOWN));

        // Spore Blossom above catwalk
        world.setBlockState(new BlockPos(OX - 2, BASE_Y + 5, OZ + 5), Blocks.SPORE_BLOSSOM.getDefaultState());

        // Cobwebs draped over wet timbers
        world.setBlockState(new BlockPos(OX - 3, BASE_Y + 3, OZ + 5), Blocks.COBWEB.getDefaultState());
        world.setBlockState(new BlockPos(OX + 2, BASE_Y + 3, OZ + 5), Blocks.COBWEB.getDefaultState());

        // 8. Camera Positioning (Standing on the catwalk looking toward the waterwheel)
        player.teleport(world, OX - 2.0, BASE_Y + 2.0, OZ + 1.2, -35f, 8f);
        client.player.setYaw(-35f);
        client.player.setPitch(8f);
    }

    @Override
    public void onCapture(ServerWorld world, ServerPlayerEntity player, MinecraftClient client) {
        world.spawnParticles(ParticleTypes.SPLASH, OX + 1.0, BASE_Y + 0.5, OZ + 5.0, 30, 0.5, 0.1, 0.5, 0.1);
        world.spawnParticles(ParticleTypes.SPORE_BLOSSOM_AIR, OX + 0.0, BASE_Y + 2.0, OZ + 4.5, 45, 1.8, 1.0, 2.5, 0.01);
    }
}
