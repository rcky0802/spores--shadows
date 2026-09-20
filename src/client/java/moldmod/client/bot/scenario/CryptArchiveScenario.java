package moldmod.client.bot.scenario;

import moldmod.SporesShadows;
import moldmod.block.ModBlocks;
import moldmod.block.core.MoldyBlock;
import moldmod.block.workstation.MoldyChestBlockEntity;
import moldmod.block.workstation.MoldyTrappedChestBlockEntity;
import moldmod.client.bot.util.BotHelper;
import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class CryptArchiveScenario implements BotScenario {
    public static final int OX = 480;
    public static final int OZ = 0;
    public static final int BASE_Y = 120;

    @Override
    public String getName() {
        return "Scenario 9: Cathedral Crypt Archives";
    }

    @Override
    public String getScreenshotFilename() {
        return "scenario_09_crypt_archives.png";
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

        // 1. Ancient Flagstone Floor (Y = BASE_Y - 1)
        BotHelper.fill(world, OX - 5, BASE_Y - 2, OZ - 2, OX + 5, BASE_Y - 2, OZ + 11, Blocks.STONE);

        for (int x = -4; x <= 4; x++) {
            for (int z = -1; z <= 10; z++) {
                int hash = Math.abs(x * 7 + z * 11 + (x ^ z));
                Block fb = (hash % 3 == 0) ? Blocks.CRACKED_STONE_BRICKS :
                           (hash % 3 == 1) ? Blocks.MOSSY_STONE_BRICKS : Blocks.STONE_BRICKS;
                world.setBlockState(new BlockPos(OX + x, BASE_Y - 1, OZ + z), fb.getDefaultState());
            }
        }

        // Red Carpet runner leading up to the lectern
        for (int z = 0; z <= 3; z++) {
            world.setBlockState(new BlockPos(OX, BASE_Y, OZ + z), Blocks.RED_CARPET.getDefaultState());
        }

        // 2. Stone Walls (X = -3 and X = +3, Y = 0 to 5)
        for (int z = -1; z <= 9; z++) {
            for (int y = 0; y <= 5; y++) {
                Block wb = ((z + y) % 3 == 0) ? Blocks.MOSSY_STONE_BRICKS :
                           ((z * 2 + y) % 4 == 0) ? Blocks.CRACKED_STONE_BRICKS : Blocks.STONE_BRICKS;
                world.setBlockState(new BlockPos(OX - 3, BASE_Y + y, OZ + z), wb.getDefaultState());
                world.setBlockState(new BlockPos(OX + 3, BASE_Y + y, OZ + z), wb.getDefaultState());
            }
        }

        // Back wall at Z = 8 with recessed arch
        for (int x = -3; x <= 3; x++) {
            for (int y = 0; y <= 5; y++) {
                world.setBlockState(new BlockPos(OX + x, BASE_Y + y, OZ + 8), Blocks.STONE_BRICKS.getDefaultState());
            }
        }

        // Recessed gothic arch niche at Z = 8 (center X = -1 to 1)
        for (int x = -1; x <= 1; x++) {
            for (int y = 0; y <= 3; y++) {
                world.setBlockState(new BlockPos(OX + x, BASE_Y + y, OZ + 8), Blocks.AIR.getDefaultState());
                world.setBlockState(new BlockPos(OX + x, BASE_Y + y, OZ + 9), Blocks.CRACKED_STONE_BRICKS.getDefaultState());
            }
        }

        // 3. Gothic Vaulted Ceiling (Y = 4 and 5)
        for (int z = -1; z <= 8; z++) {
            world.setBlockState(new BlockPos(OX - 2, BASE_Y + 4, OZ + z),
                Blocks.STONE_BRICK_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.EAST));
            world.setBlockState(new BlockPos(OX + 2, BASE_Y + 4, OZ + z),
                Blocks.STONE_BRICK_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.WEST));

            for (int x = -1; x <= 1; x++) {
                world.setBlockState(new BlockPos(OX + x, BASE_Y + 5, OZ + z), Blocks.CRACKED_STONE_BRICKS.getDefaultState());
            }
        }

        // 4. Prominent Towering Bookshelves along both sides (X = -2 and X = +2, Y = 0 to 3)
        // Left Side Book Stacks (facing into room)
        for (int z = 1; z <= 6; z++) {
            world.setBlockState(new BlockPos(OX - 2, BASE_Y, OZ + z),
                ModBlocks.MOLDY_BOOKSHELF.getDefaultState().with(MoldyBlock.STAGE, 3));
            world.setBlockState(new BlockPos(OX - 2, BASE_Y + 1, OZ + z),
                ModBlocks.MOLDY_CHISELED_BOOKSHELF.getDefaultState().with(MoldyBlock.STAGE, 3).with(HorizontalFacingBlock.FACING, Direction.EAST));
            world.setBlockState(new BlockPos(OX - 2, BASE_Y + 2, OZ + z),
                ModBlocks.MOLDY_BOOKSHELF.getDefaultState().with(MoldyBlock.STAGE, 2));
            world.setBlockState(new BlockPos(OX - 2, BASE_Y + 3, OZ + z),
                ModBlocks.MOLDY_BOOKSHELF.getDefaultState().with(MoldyBlock.STAGE, 1));
        }

        // Right Side Book Stacks
        for (int z = 1; z <= 6; z++) {
            world.setBlockState(new BlockPos(OX + 2, BASE_Y, OZ + z),
                ModBlocks.MOLDY_BOOKSHELF.getDefaultState().with(MoldyBlock.STAGE, 3));
            world.setBlockState(new BlockPos(OX + 2, BASE_Y + 1, OZ + z),
                ModBlocks.MOLDY_CHISELED_BOOKSHELF.getDefaultState().with(MoldyBlock.STAGE, 2).with(HorizontalFacingBlock.FACING, Direction.WEST));
            world.setBlockState(new BlockPos(OX + 2, BASE_Y + 2, OZ + z),
                ModBlocks.MOLDY_BOOKSHELF.getDefaultState().with(MoldyBlock.STAGE, 2));
            world.setBlockState(new BlockPos(OX + 2, BASE_Y + 3, OZ + z),
                ModBlocks.MOLDY_BOOKSHELF.getDefaultState().with(MoldyBlock.STAGE, 1));
        }

        // Moldy Ladder leaning against right book stack
        world.setBlockState(new BlockPos(OX + 1, BASE_Y, OZ + 2),
            ModBlocks.MOLDY_LADDER.getDefaultState().with(MoldyBlock.STAGE, 3).with(LadderBlock.FACING, Direction.WEST));
        world.setBlockState(new BlockPos(OX + 1, BASE_Y + 1, OZ + 2),
            ModBlocks.MOLDY_LADDER.getDefaultState().with(MoldyBlock.STAGE, 3).with(LadderBlock.FACING, Direction.WEST));
        world.setBlockState(new BlockPos(OX + 1, BASE_Y + 2, OZ + 2),
            ModBlocks.MOLDY_LADDER.getDefaultState().with(MoldyBlock.STAGE, 2).with(LadderBlock.FACING, Direction.WEST));

        // 5. Central Dais & Moldy Lectern (Centerpiece at Z = 4)
        world.setBlockState(new BlockPos(OX, BASE_Y, OZ + 4),
            ModBlocks.MOLDY_LECTERN.getDefaultState().with(MoldyBlock.STAGE, 3).with(HorizontalFacingBlock.FACING, Direction.NORTH));

        // Candelabras flanking the lectern on chiseled pedestals
        world.setBlockState(new BlockPos(OX - 1, BASE_Y, OZ + 4),
            Blocks.CANDLE.getDefaultState().with(CandleBlock.LIT, true).with(CandleBlock.CANDLES, 3));

        world.setBlockState(new BlockPos(OX + 1, BASE_Y, OZ + 4),
            Blocks.CANDLE.getDefaultState().with(CandleBlock.LIT, true).with(CandleBlock.CANDLES, 2));

        // 6. Deep Background Alcove: Moldy Chests
        BlockPos chestPos = new BlockPos(OX, BASE_Y, OZ + 8);
        world.setBlockState(chestPos,
            ModBlocks.MOLDY_CHEST.getDefaultState().with(MoldyBlock.STAGE, 3).with(HorizontalFacingBlock.FACING, Direction.NORTH));
        if (world.getBlockEntity(chestPos) instanceof MoldyChestBlockEntity be) {
            be.setMoldStage(3);
        }

        // Soul lantern in the back alcove
        world.setBlockState(new BlockPos(OX, BASE_Y + 2, OZ + 8), Blocks.CHAIN.getDefaultState().with(ChainBlock.AXIS, Direction.Axis.Y));
        world.setBlockState(new BlockPos(OX, BASE_Y + 1, OZ + 8), Blocks.SOUL_LANTERN.getDefaultState().with(LanternBlock.HANGING, true));

        // 7. Hanging Lanterns & Spores in the Foreground Vault
        world.setBlockState(new BlockPos(OX, BASE_Y + 4, OZ + 2), Blocks.CHAIN.getDefaultState().with(ChainBlock.AXIS, Direction.Axis.Y));
        world.setBlockState(new BlockPos(OX, BASE_Y + 3, OZ + 2), Blocks.LANTERN.getDefaultState().with(LanternBlock.HANGING, true));

        world.setBlockState(new BlockPos(OX, BASE_Y + 4, OZ + 4), Blocks.SPORE_BLOSSOM.getDefaultState());

        // Cobwebs draped over old shelves and arch corners
        world.setBlockState(new BlockPos(OX - 2, BASE_Y + 3, OZ + 3), Blocks.COBWEB.getDefaultState());
        world.setBlockState(new BlockPos(OX + 2, BASE_Y + 3, OZ + 5), Blocks.COBWEB.getDefaultState());
        world.setBlockState(new BlockPos(OX - 1, BASE_Y + 1, OZ + 6), Blocks.COBWEB.getDefaultState());

        // 8. Camera Positioning
        player.teleport(world, OX + 0.0, BASE_Y, OZ + 0.5, 0f, 7f);
        client.player.setYaw(0f);
        client.player.setPitch(7f);
    }

    @Override
    public void onCapture(ServerWorld world, ServerPlayerEntity player, MinecraftClient client) {
        world.spawnParticles(ParticleTypes.SPORE_BLOSSOM_AIR, OX + 0.0, BASE_Y + 1.8, OZ + 3.5, 50, 1.2, 0.8, 2.0, 0.01);
    }
}
