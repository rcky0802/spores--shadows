package moldmod.client.bot.scenario;

import moldmod.SporesShadows;
import moldmod.block.ModBlocks;
import moldmod.block.core.MoldyBlock;
import moldmod.block.workstation.MoldyChestBlockEntity;
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

public class CellarScenario implements BotScenario {
    public static final int OX = 0;
    public static final int OZ = 0;
    public static final int BASE_Y = 120;

    @Override
    public String getName() {
        return "Scenario 1: Damp Atmospheric Cellar with Mold";
    }

    @Override
    public String getScreenshotFilename() {
        return "scenario_01_moldy_cellar.png";
    }

    @Override
    public int getWaitTicks() {
        return 50;
    }

    @Override
    public void prepare(ServerWorld world, ServerPlayerEntity player, MinecraftClient client) {
        client.options.setPerspective(Perspective.FIRST_PERSON);

        // Clear ample space around the cellar
        BotHelper.fill(world, OX - 6, BASE_Y - 2, OZ - 3, OX + 6, BASE_Y + 8, OZ + 13, Blocks.AIR);
        BotHelper.clearDroppedItems(world, new BlockPos(OX, BASE_Y, OZ + 4), 20.0);

        Block moldyLog = Registries.BLOCK.get(SporesShadows.id("moldy_oak_log"));
        Block moldyPlanks = Registries.BLOCK.get(SporesShadows.id("moldy_oak_planks"));
        Block moldyTrapdoor = Registries.BLOCK.get(SporesShadows.id("moldy_oak_trapdoor"));

        // 1. Bedrock/Stone Sub-floor (Y = BASE_Y - 2) to prevent light leaks
        BotHelper.fill(world, OX - 5, BASE_Y - 2, OZ - 2, OX + 5, BASE_Y - 2, OZ + 12, Blocks.STONE);

        // 2. Cellar Floor (Y = BASE_Y - 1)
        for (int x = -4; x <= 4; x++) {
            for (int z = -1; z <= 11; z++) {
                Block floorBlock;
                int hash = Math.abs(x * 7 + z * 13 + (x ^ z));
                if (hash % 5 == 0) {
                    floorBlock = Blocks.MOSSY_COBBLESTONE;
                } else if (hash % 5 == 1) {
                    floorBlock = Blocks.CRACKED_STONE_BRICKS;
                } else if (hash % 5 == 2) {
                    floorBlock = Blocks.MOSSY_STONE_BRICKS;
                } else if (hash % 5 == 3) {
                    floorBlock = Blocks.COBBLESTONE;
                } else {
                    floorBlock = Blocks.STONE_BRICKS;
                }
                world.setBlockState(new BlockPos(OX + x, BASE_Y - 1, OZ + z), floorBlock.getDefaultState());
            }
        }

        // Puddles on the floor (Waterlogged slabs reflecting ambient shader light)
        BlockState waterloggedSlab = Blocks.MOSSY_COBBLESTONE_SLAB.getDefaultState()
            .with(SlabBlock.TYPE, SlabType.BOTTOM)
            .with(SlabBlock.WATERLOGGED, true);
        world.setBlockState(new BlockPos(OX - 1, BASE_Y - 1, OZ + 4), waterloggedSlab);
        world.setBlockState(new BlockPos(OX - 1, BASE_Y - 1, OZ + 5), waterloggedSlab);
        world.setBlockState(new BlockPos(OX, BASE_Y - 1, OZ + 5), waterloggedSlab);

        // 3. Side Walls (X = -4 and X = +4, Y = 0 to 4)
        for (int z = -1; z <= 11; z++) {
            for (int y = 0; y <= 4; y++) {
                // West Wall
                Block westBlock = ((z + y * 2) % 3 == 0) ? Blocks.MOSSY_STONE_BRICKS :
                                  ((z * 3 + y) % 4 == 0) ? Blocks.CRACKED_STONE_BRICKS : Blocks.STONE_BRICKS;
                world.setBlockState(new BlockPos(OX - 4, BASE_Y + y, OZ + z), westBlock.getDefaultState());

                // East Wall
                Block eastBlock = ((z * 2 + y) % 3 == 0) ? Blocks.CRACKED_STONE_BRICKS :
                                  ((z + y * 3) % 4 == 0) ? Blocks.MOSSY_STONE_BRICKS : Blocks.STONE_BRICKS;
                world.setBlockState(new BlockPos(OX + 4, BASE_Y + y, OZ + z), eastBlock.getDefaultState());
            }
        }

        // 4. Back Wall with Recessed Arch (Z = 10 and 11)
        for (int x = -4; x <= 4; x++) {
            for (int y = 0; y <= 4; y++) {
                Block b = (x + y) % 2 == 0 ? Blocks.STONE_BRICKS : Blocks.CRACKED_STONE_BRICKS;
                world.setBlockState(new BlockPos(OX + x, BASE_Y + y, OZ + 10), b.getDefaultState());
            }
        }
        // Hollow out center archway at Z = 10 and build recessed alcove at Z = 11 & 12
        for (int x = -1; x <= 1; x++) {
            for (int y = 0; y <= 2; y++) {
                world.setBlockState(new BlockPos(OX + x, BASE_Y + y, OZ + 10), Blocks.AIR.getDefaultState());
                world.setBlockState(new BlockPos(OX + x, BASE_Y + y, OZ + 11), Blocks.AIR.getDefaultState());
                world.setBlockState(new BlockPos(OX + x, BASE_Y + y, OZ + 12), Blocks.CRACKED_STONE_BRICKS.getDefaultState());
            }
        }
        // Alcove sides & ceiling
        for (int y = 0; y <= 2; y++) {
            world.setBlockState(new BlockPos(OX - 2, BASE_Y + y, OZ + 11), Blocks.MOSSY_STONE_BRICKS.getDefaultState());
            world.setBlockState(new BlockPos(OX + 2, BASE_Y + y, OZ + 11), Blocks.MOSSY_STONE_BRICKS.getDefaultState());
        }
        for (int x = -1; x <= 1; x++) {
            world.setBlockState(new BlockPos(OX + x, BASE_Y + 3, OZ + 11), Blocks.STONE_BRICKS.getDefaultState());
        }

        // Back entrance / gate in alcove: Moldy fence or iron bars
        world.setBlockState(new BlockPos(OX - 1, BASE_Y, OZ + 11), Blocks.IRON_BARS.getDefaultState());
        world.setBlockState(new BlockPos(OX + 1, BASE_Y, OZ + 11), Blocks.IRON_BARS.getDefaultState());
        world.setBlockState(new BlockPos(OX - 1, BASE_Y + 1, OZ + 11), Blocks.IRON_BARS.getDefaultState());
        world.setBlockState(new BlockPos(OX + 1, BASE_Y + 1, OZ + 11), Blocks.IRON_BARS.getDefaultState());

        // Pedestal in alcove center with Moldy Barrel and Soul Lantern
        world.setBlockState(new BlockPos(OX, BASE_Y, OZ + 11),
            ModBlocks.MOLDY_BARREL.getDefaultState().with(MoldyBlock.STAGE, 3).with(BarrelBlock.FACING, Direction.UP));
        world.setBlockState(new BlockPos(OX, BASE_Y + 1, OZ + 11), Blocks.SOUL_LANTERN.getDefaultState());

        // 5. Back wall behind player (Z = -2)
        for (int x = -4; x <= 4; x++) {
            for (int y = 0; y <= 4; y++) {
                world.setBlockState(new BlockPos(OX + x, BASE_Y + y, OZ - 2), Blocks.STONE_BRICKS.getDefaultState());
            }
        }

        // 6. Vaulted Ceiling (Y = 4 and 5)
        for (int z = -1; z <= 10; z++) {
            // Stone brick stairs sloping inwards
            world.setBlockState(new BlockPos(OX - 3, BASE_Y + 4, OZ + z),
                Blocks.STONE_BRICK_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.EAST));
            world.setBlockState(new BlockPos(OX + 3, BASE_Y + 4, OZ + z),
                Blocks.STONE_BRICK_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.WEST));

            for (int x = -2; x <= 2; x++) {
                Block ceil = ((x + z) % 3 == 0) ? Blocks.MOSSY_STONE_BRICKS : Blocks.STONE_BRICKS;
                world.setBlockState(new BlockPos(OX + x, BASE_Y + 5, OZ + z), ceil.getDefaultState());
            }
        }

        // 7. Structural Arches with Moldy Oak Logs & Brackets (at Z = 2, Z = 6)
        int[] archZ = {2, 6};
        for (int az : archZ) {
            // Pillars
            world.setBlockState(new BlockPos(OX - 3, BASE_Y, OZ + az), moldyLog.getDefaultState().with(MoldyBlock.STAGE, 2));
            world.setBlockState(new BlockPos(OX - 3, BASE_Y + 1, OZ + az), moldyLog.getDefaultState().with(MoldyBlock.STAGE, 3));
            world.setBlockState(new BlockPos(OX - 3, BASE_Y + 2, OZ + az), moldyLog.getDefaultState().with(MoldyBlock.STAGE, 3));
            world.setBlockState(new BlockPos(OX - 3, BASE_Y + 3, OZ + az), moldyLog.getDefaultState().with(MoldyBlock.STAGE, 2));

            world.setBlockState(new BlockPos(OX + 3, BASE_Y, OZ + az), moldyLog.getDefaultState().with(MoldyBlock.STAGE, 2));
            world.setBlockState(new BlockPos(OX + 3, BASE_Y + 1, OZ + az), moldyLog.getDefaultState().with(MoldyBlock.STAGE, 3));
            world.setBlockState(new BlockPos(OX + 3, BASE_Y + 2, OZ + az), moldyLog.getDefaultState().with(MoldyBlock.STAGE, 3));
            world.setBlockState(new BlockPos(OX + 3, BASE_Y + 3, OZ + az), moldyLog.getDefaultState().with(MoldyBlock.STAGE, 2));

            // Cross-beam across ceiling at Y = 4
            for (int x = -3; x <= 3; x++) {
                world.setBlockState(new BlockPos(OX + x, BASE_Y + 4, OZ + az),
                    moldyLog.getDefaultState()
                        .with(MoldyBlock.STAGE, (Math.abs(x) % 2 == 0) ? 3 : 2)
                        .with(PillarBlock.AXIS, Direction.Axis.X));
            }
        }

        // 8. Hanging Lantern (Centerpiece Warm Lighting)
        world.setBlockState(new BlockPos(OX, BASE_Y + 4, OZ + 3), Blocks.CHAIN.getDefaultState().with(ChainBlock.AXIS, Direction.Axis.Y));
        world.setBlockState(new BlockPos(OX, BASE_Y + 3, OZ + 3), Blocks.CHAIN.getDefaultState().with(ChainBlock.AXIS, Direction.Axis.Y));
        world.setBlockState(new BlockPos(OX, BASE_Y + 2, OZ + 3), Blocks.LANTERN.getDefaultState().with(LanternBlock.HANGING, true));

        // 9. Dripping Water Feature (Pointed Dripstone directly above puddle)
        world.setBlockState(new BlockPos(OX - 1, BASE_Y + 5, OZ + 5), Blocks.WATER.getDefaultState());
        world.setBlockState(new BlockPos(OX - 1, BASE_Y + 4, OZ + 5), Blocks.DRIPSTONE_BLOCK.getDefaultState());
        world.setBlockState(new BlockPos(OX - 1, BASE_Y + 3, OZ + 5),
            Blocks.POINTED_DRIPSTONE.getDefaultState()
                .with(PointedDripstoneBlock.VERTICAL_DIRECTION, Direction.DOWN));

        // 10. Floating Spores (Spore Blossom hidden above ceiling beam)
        world.setBlockState(new BlockPos(OX + 1, BASE_Y + 4, OZ + 4), Blocks.SPORE_BLOSSOM.getDefaultState());

        // 11. Left Side: Wine Casks & Decayed Storage Alcove
        // Moldy Barrels stacked sideways and upright
        world.setBlockState(new BlockPos(OX - 3, BASE_Y, OZ + 3),
            ModBlocks.MOLDY_BARREL.getDefaultState()
                .with(MoldyBlock.STAGE, 2)
                .with(BarrelBlock.FACING, Direction.EAST));
        world.setBlockState(new BlockPos(OX - 3, BASE_Y + 1, OZ + 3),
            ModBlocks.MOLDY_BARREL.getDefaultState()
                .with(MoldyBlock.STAGE, 3)
                .with(BarrelBlock.FACING, Direction.EAST));

        world.setBlockState(new BlockPos(OX - 3, BASE_Y, OZ + 4),
            ModBlocks.MOLDY_BARREL.getDefaultState()
                .with(MoldyBlock.STAGE, 3)
                .with(BarrelBlock.FACING, Direction.UP));
        world.setBlockState(new BlockPos(OX - 3, BASE_Y + 1, OZ + 4),
            ModBlocks.MOLDY_BARREL.getDefaultState()
                .with(MoldyBlock.STAGE, 2)
                .with(BarrelBlock.FACING, Direction.NORTH));

        world.setBlockState(new BlockPos(OX - 3, BASE_Y, OZ + 5),
            ModBlocks.MOLDY_BARREL.getDefaultState()
                .with(MoldyBlock.STAGE, 3)
                .with(BarrelBlock.FACING, Direction.EAST));

        // Barrel resting in front of the stack
        world.setBlockState(new BlockPos(OX - 2, BASE_Y, OZ + 3),
            ModBlocks.MOLDY_BARREL.getDefaultState()
                .with(MoldyBlock.STAGE, 3)
                .with(BarrelBlock.FACING, Direction.NORTH));

        // Cobwebs around wine barrels
        world.setBlockState(new BlockPos(OX - 3, BASE_Y + 2, OZ + 3), Blocks.COBWEB.getDefaultState());
        world.setBlockState(new BlockPos(OX - 3, BASE_Y + 2, OZ + 5), Blocks.COBWEB.getDefaultState());
        world.setBlockState(new BlockPos(OX - 3, BASE_Y + 1, OZ + 6), Blocks.COBWEB.getDefaultState());

        // Moldy Chest tucked in dark corner (Stage 3)
        BlockPos chestPos = new BlockPos(OX - 3, BASE_Y, OZ + 7);
        world.setBlockState(chestPos,
            ModBlocks.MOLDY_CHEST.getDefaultState()
                .with(MoldyBlock.STAGE, 3)
                .with(HorizontalFacingBlock.FACING, Direction.EAST));
        if (world.getBlockEntity(chestPos) instanceof MoldyChestBlockEntity be) {
            be.setMoldStage(3);
        }
        world.setBlockState(new BlockPos(OX - 3, BASE_Y + 1, OZ + 7), Blocks.COBWEB.getDefaultState());

        // Moldy Chiseled Bookshelf (Archival cellar records / vintage cellar rack)
        world.setBlockState(new BlockPos(OX - 3, BASE_Y, OZ + 8),
            ModBlocks.MOLDY_CHISELED_BOOKSHELF.getDefaultState()
                .with(MoldyBlock.STAGE, 3)
                .with(HorizontalFacingBlock.FACING, Direction.EAST));

        // Red mushroom growing in damp shadow near chest
        world.setBlockState(new BlockPos(OX - 2, BASE_Y, OZ + 7), Blocks.RED_MUSHROOM.getDefaultState());

        // 12. Right Side: Cellar Workstation & Decayed Hatch
        // Cellar Access Pillar with Moldy Ladder climbing up
        world.setBlockState(new BlockPos(OX + 3, BASE_Y, OZ + 2), Blocks.CRACKED_STONE_BRICKS.getDefaultState());
        world.setBlockState(new BlockPos(OX + 3, BASE_Y + 1, OZ + 2), Blocks.STONE_BRICKS.getDefaultState());
        world.setBlockState(new BlockPos(OX + 3, BASE_Y + 2, OZ + 2), Blocks.CRACKED_STONE_BRICKS.getDefaultState());
        world.setBlockState(new BlockPos(OX + 3, BASE_Y + 3, OZ + 2), Blocks.STONE_BRICKS.getDefaultState());

        world.setBlockState(new BlockPos(OX + 2, BASE_Y, OZ + 2),
            ModBlocks.MOLDY_LADDER.getDefaultState()
                .with(MoldyBlock.STAGE, 3)
                .with(LadderBlock.FACING, Direction.WEST));
        world.setBlockState(new BlockPos(OX + 2, BASE_Y + 1, OZ + 2),
            ModBlocks.MOLDY_LADDER.getDefaultState()
                .with(MoldyBlock.STAGE, 3)
                .with(LadderBlock.FACING, Direction.WEST));
        world.setBlockState(new BlockPos(OX + 2, BASE_Y + 2, OZ + 2),
            ModBlocks.MOLDY_LADDER.getDefaultState()
                .with(MoldyBlock.STAGE, 2)
                .with(LadderBlock.FACING, Direction.WEST));

        // Moldy Trapdoor at the ceiling hatch
        world.setBlockState(new BlockPos(OX + 2, BASE_Y + 4, OZ + 2),
            moldyTrapdoor.getDefaultState()
                .with(MoldyBlock.STAGE, 3)
                .with(TrapdoorBlock.OPEN, false));

        // Moldy Crafting Table
        world.setBlockState(new BlockPos(OX + 3, BASE_Y, OZ + 4),
            ModBlocks.MOLDY_CRAFTING_TABLE.getDefaultState().with(MoldyBlock.STAGE, 3));

        // Atmospheric Candle lit on top of the crafting table
        world.setBlockState(new BlockPos(OX + 3, BASE_Y + 1, OZ + 4),
            Blocks.CANDLE.getDefaultState()
                .with(CandleBlock.LIT, true)
                .with(CandleBlock.CANDLES, 2));

        // Moldy Barrel on right
        world.setBlockState(new BlockPos(OX + 3, BASE_Y, OZ + 5),
            ModBlocks.MOLDY_BARREL.getDefaultState()
                .with(MoldyBlock.STAGE, 2)
                .with(BarrelBlock.FACING, Direction.WEST));

        // Moldy Composter
        world.setBlockState(new BlockPos(OX + 3, BASE_Y, OZ + 6),
            ModBlocks.MOLDY_COMPOSTER.getDefaultState().with(MoldyBlock.STAGE, 3));

        // Moldy Lectern with ancient book
        world.setBlockState(new BlockPos(OX + 3, BASE_Y, OZ + 7),
            ModBlocks.MOLDY_LECTERN.getDefaultState()
                .with(MoldyBlock.STAGE, 3)
                .with(HorizontalFacingBlock.FACING, Direction.WEST));

        // Rotten planks & Brown Mushroom in dark right corner
        world.setBlockState(new BlockPos(OX + 3, BASE_Y, OZ + 8),
            moldyPlanks.getDefaultState().with(MoldyBlock.STAGE, 3));
        world.setBlockState(new BlockPos(OX + 2, BASE_Y, OZ + 8), Blocks.BROWN_MUSHROOM.getDefaultState());

        // Cobweb in top right corner
        world.setBlockState(new BlockPos(OX + 3, BASE_Y + 2, OZ + 6), Blocks.COBWEB.getDefaultState());

        // 13. Camera & Player Positioning
        // Looking down the center of the cellar, slightly down towards floor & barrels
        player.teleport(world, OX + 0.0, BASE_Y, OZ + 0.7, 0f, 6f);
        client.player.setYaw(0f);
        client.player.setPitch(6f);
    }

    @Override
    public void onCapture(ServerWorld world, ServerPlayerEntity player, MinecraftClient client) {
        // Spawn additional spore blossom and dripping water particles to ensure rich density
        world.spawnParticles(ParticleTypes.SPORE_BLOSSOM_AIR, OX + 0.0, BASE_Y + 1.8, OZ + 4.0, 40, 2.0, 1.0, 3.0, 0.01);
        world.spawnParticles(ParticleTypes.DRIPPING_WATER, OX - 1.0, BASE_Y + 2.8, OZ + 5.0, 8, 0.1, 0.1, 0.1, 0.01);
    }
}
