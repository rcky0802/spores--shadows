package moldmod.client.bot.scenario;

import moldmod.SporesShadows;
import moldmod.block.ModBlocks;
import moldmod.block.core.MoldyBlock;
import moldmod.block.workstation.MoldyChestBlockEntity;
import moldmod.client.bot.util.BotHelper;
import net.minecraft.block.*;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.block.enums.SlabType;
import net.minecraft.block.enums.StairShape;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class AtticScenario implements BotScenario {
    public static final int OX = 300;
    public static final int OZ = 0;
    public static final int BASE_Y = 120;

    @Override
    public String getName() {
        return "Scenario 6: The Forgotten Attic";
    }

    @Override
    public String getScreenshotFilename() {
        return "scenario_06_forgotten_attic.png";
    }

    @Override
    public int getWaitTicks() {
        return 50;
    }

    @Override
    public void prepare(ServerWorld world, ServerPlayerEntity player, MinecraftClient client) {
        client.options.setPerspective(Perspective.FIRST_PERSON);

        // Clear ample space
        BotHelper.fill(world, OX - 6, BASE_Y - 2, OZ - 3, OX + 6, BASE_Y + 9, OZ + 12, Blocks.AIR);
        BotHelper.clearDroppedItems(world, new BlockPos(OX, BASE_Y, OZ + 4), 20.0);

        Block moldyLog = Registries.BLOCK.get(SporesShadows.id("moldy_oak_log"));
        Block moldyPlanks = Registries.BLOCK.get(SporesShadows.id("moldy_oak_planks"));
        Block moldyStairs = Registries.BLOCK.get(SporesShadows.id("moldy_oak_stairs"));
        Block moldySlab = Registries.BLOCK.get(SporesShadows.id("moldy_oak_slab"));
        Block moldyTrapdoor = Registries.BLOCK.get(SporesShadows.id("moldy_oak_trapdoor"));

        // Set time to morning sun to get dramatic volumetric god rays through roof cracks
        world.setTimeOfDay(4500L);

        // 1. Sub-floor
        BotHelper.fill(world, OX - 5, BASE_Y - 2, OZ - 2, OX + 5, BASE_Y - 2, OZ + 11, Blocks.STONE);

        // 2. Attic Floor (Y = BASE_Y - 1): Mix of old oak planks, moldy planks, and rotten slabs
        for (int x = -4; x <= 4; x++) {
            for (int z = -1; z <= 10; z++) {
                int hash = Math.abs(x * 11 + z * 7 + (x ^ z));
                BlockPos fPos = new BlockPos(OX + x, BASE_Y - 1, OZ + z);
                if (hash % 4 == 0) {
                    world.setBlockState(fPos, moldyPlanks.getDefaultState().with(MoldyBlock.STAGE, 3));
                } else if (hash % 4 == 1) {
                    world.setBlockState(fPos, moldyPlanks.getDefaultState().with(MoldyBlock.STAGE, 2));
                } else if (hash % 4 == 2) {
                    world.setBlockState(fPos, moldyPlanks.getDefaultState().with(MoldyBlock.STAGE, 1));
                } else {
                    world.setBlockState(fPos, Blocks.OAK_PLANKS.getDefaultState());
                }
            }
        }

        // 3. Gable End Walls (Front Z = -2 and Back Z = 10)
        // Back wall at Z = 10
        for (int x = -4; x <= 4; x++) {
            for (int y = 0; y <= 6; y++) {
                int dist = Math.abs(x);
                if (y <= 5 - dist) {
                    world.setBlockState(new BlockPos(OX + x, BASE_Y + y, OZ + 10),
                        (y % 2 == 0) ? Blocks.DARK_OAK_PLANKS.getDefaultState() : Blocks.STRIPPED_DARK_OAK_LOG.getDefaultState());
                }
            }
        }
        // Small circular/slitted attic window at Z = 10, Y = 2
        world.setBlockState(new BlockPos(OX, BASE_Y + 2, OZ + 10), Blocks.GLASS_PANE.getDefaultState());
        world.setBlockState(new BlockPos(OX, BASE_Y + 3, OZ + 10), Blocks.GLASS_PANE.getDefaultState());

        // Front wall behind player (Z = -2)
        for (int x = -4; x <= 4; x++) {
            for (int y = 0; y <= 6; y++) {
                world.setBlockState(new BlockPos(OX + x, BASE_Y + y, OZ - 2), Blocks.DARK_OAK_PLANKS.getDefaultState());
            }
        }

        // 4. Sloping Pitched Roof (A-Frame rafters)
        // Roof slope rises from X = -4 / +4 up to apex at X = 0, Y = 5
        for (int z = -1; z <= 9; z++) {
            // Left slope (facing East)
            world.setBlockState(new BlockPos(OX - 4, BASE_Y, OZ + z), Blocks.DARK_OAK_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.EAST));
            world.setBlockState(new BlockPos(OX - 3, BASE_Y + 1, OZ + z), Blocks.DARK_OAK_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.EAST));
            world.setBlockState(new BlockPos(OX - 2, BASE_Y + 2, OZ + z), Blocks.DARK_OAK_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.EAST));
            world.setBlockState(new BlockPos(OX - 1, BASE_Y + 3, OZ + z), Blocks.DARK_OAK_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.EAST));

            // Right slope (facing West)
            world.setBlockState(new BlockPos(OX + 4, BASE_Y, OZ + z), Blocks.DARK_OAK_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.WEST));
            world.setBlockState(new BlockPos(OX + 3, BASE_Y + 1, OZ + z), Blocks.DARK_OAK_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.WEST));
            world.setBlockState(new BlockPos(OX + 2, BASE_Y + 2, OZ + z), Blocks.DARK_OAK_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.WEST));
            world.setBlockState(new BlockPos(OX + 1, BASE_Y + 3, OZ + z), Blocks.DARK_OAK_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.WEST));

            // Ridge beam
            world.setBlockState(new BlockPos(OX, BASE_Y + 4, OZ + z), Blocks.DARK_OAK_SLAB.getDefaultState().with(SlabBlock.TYPE, SlabType.BOTTOM));
        }

        // Roof Break / Skylight Gap where sunlight streams in (Z = 3 to 4 on left roof slope)
        world.setBlockState(new BlockPos(OX - 2, BASE_Y + 2, OZ + 3), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(OX - 1, BASE_Y + 3, OZ + 3), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(OX - 2, BASE_Y + 2, OZ + 4), Blocks.AIR.getDefaultState());

        // 5. Moldy Timber Roof Trusses (Cross-ties & King Posts at Z = 1, Z = 5, Z = 8)
        int[] trussZ = {1, 5, 8};
        for (int tz : trussZ) {
            // Horizontal tie-beam at Y = 2
            for (int x = -2; x <= 2; x++) {
                world.setBlockState(new BlockPos(OX + x, BASE_Y + 2, OZ + tz),
                    moldyLog.getDefaultState()
                        .with(MoldyBlock.STAGE, 3)
                        .with(PillarBlock.AXIS, Direction.Axis.X));
            }
            // Vertical king post
            world.setBlockState(new BlockPos(OX, BASE_Y + 3, OZ + tz),
                moldyLog.getDefaultState().with(MoldyBlock.STAGE, 2).with(PillarBlock.AXIS, Direction.Axis.Y));
        }

        // 6. Center Hatch & Moldy Ladder descending down
        world.setBlockState(new BlockPos(OX, BASE_Y - 1, OZ + 2), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(OX, BASE_Y - 2, OZ + 2), Blocks.AIR.getDefaultState());
        world.setBlockState(new BlockPos(OX + 1, BASE_Y - 1, OZ + 2),
            ModBlocks.MOLDY_LADDER.getDefaultState().with(MoldyBlock.STAGE, 3).with(LadderBlock.FACING, Direction.WEST));
        world.setBlockState(new BlockPos(OX, BASE_Y, OZ + 2),
            moldyTrapdoor.getDefaultState().with(MoldyBlock.STAGE, 3).with(TrapdoorBlock.OPEN, true).with(TrapdoorBlock.FACING, Direction.NORTH));

        // 7. Left Side Furniture: Decayed Storage & Moldy Chest
        BlockPos chestPos = new BlockPos(OX - 2, BASE_Y, OZ + 5);
        world.setBlockState(chestPos,
            ModBlocks.MOLDY_CHEST.getDefaultState().with(MoldyBlock.STAGE, 3).with(HorizontalFacingBlock.FACING, Direction.EAST));
        if (world.getBlockEntity(chestPos) instanceof MoldyChestBlockEntity be) {
            be.setMoldStage(3);
        }
        world.setBlockState(new BlockPos(OX - 2, BASE_Y + 1, OZ + 5), Blocks.COBWEB.getDefaultState());

        world.setBlockState(new BlockPos(OX - 2, BASE_Y, OZ + 6),
            ModBlocks.MOLDY_BARREL.getDefaultState().with(MoldyBlock.STAGE, 2).with(BarrelBlock.FACING, Direction.UP));
        world.setBlockState(new BlockPos(OX - 2, BASE_Y + 1, OZ + 6),
            ModBlocks.MOLDY_BARREL.getDefaultState().with(MoldyBlock.STAGE, 3).with(BarrelBlock.FACING, Direction.EAST));

        // 8. Right Side Furniture: Old Chiseled Bookshelf & Workstation
        world.setBlockState(new BlockPos(OX + 2, BASE_Y, OZ + 4),
            ModBlocks.MOLDY_CHISELED_BOOKSHELF.getDefaultState().with(MoldyBlock.STAGE, 3).with(HorizontalFacingBlock.FACING, Direction.WEST));
        world.setBlockState(new BlockPos(OX + 2, BASE_Y + 1, OZ + 4), Blocks.COBWEB.getDefaultState());

        world.setBlockState(new BlockPos(OX + 2, BASE_Y, OZ + 6),
            ModBlocks.MOLDY_BARREL.getDefaultState().with(MoldyBlock.STAGE, 3).with(BarrelBlock.FACING, Direction.WEST));
        world.setBlockState(new BlockPos(OX + 1, BASE_Y, OZ + 6),
            ModBlocks.MOLDY_CRAFTING_TABLE.getDefaultState().with(MoldyBlock.STAGE, 3));

        // Candle on the crafting table
        world.setBlockState(new BlockPos(OX + 1, BASE_Y + 1, OZ + 6),
            Blocks.CANDLE.getDefaultState().with(CandleBlock.LIT, true).with(CandleBlock.CANDLES, 1));

        // 9. Back Wall Details: Stacks of Barrels & Books
        world.setBlockState(new BlockPos(OX - 1, BASE_Y, OZ + 9),
            ModBlocks.MOLDY_BARREL.getDefaultState().with(MoldyBlock.STAGE, 3).with(BarrelBlock.FACING, Direction.NORTH));
        world.setBlockState(new BlockPos(OX + 1, BASE_Y, OZ + 9),
            ModBlocks.MOLDY_BOOKSHELF.getDefaultState().with(MoldyBlock.STAGE, 2));

        // 10. Atmospheric Spore Blossom & Hanging Lantern
        world.setBlockState(new BlockPos(OX, BASE_Y + 3, OZ + 5), Blocks.SPORE_BLOSSOM.getDefaultState());

        world.setBlockState(new BlockPos(OX, BASE_Y + 2, OZ + 7), Blocks.CHAIN.getDefaultState().with(ChainBlock.AXIS, Direction.Axis.Y));
        world.setBlockState(new BlockPos(OX, BASE_Y + 1, OZ + 7), Blocks.LANTERN.getDefaultState().with(LanternBlock.HANGING, true));

        // Cobwebs in truss apexes
        world.setBlockState(new BlockPos(OX, BASE_Y + 3, OZ + 1), Blocks.COBWEB.getDefaultState());
        world.setBlockState(new BlockPos(OX, BASE_Y + 3, OZ + 8), Blocks.COBWEB.getDefaultState());

        // 11. Camera setup
        player.teleport(world, OX + 0.0, BASE_Y, OZ + 0.5, 0f, 4f);
        client.player.setYaw(0f);
        client.player.setPitch(4f);
    }

    @Override
    public void onCapture(ServerWorld world, ServerPlayerEntity player, MinecraftClient client) {
        world.spawnParticles(ParticleTypes.SPORE_BLOSSOM_AIR, OX + 0.0, BASE_Y + 1.5, OZ + 4.5, 50, 1.8, 1.0, 3.0, 0.01);
    }

    @Override
    public void cleanup(ServerWorld world, ServerPlayerEntity player, MinecraftClient client) {
        // Reset time back to midnight for subsequent scenarios
        world.setTimeOfDay(18000L);
    }
}
