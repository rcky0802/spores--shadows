package moldmod.test.helper;

import moldmod.block.ModBlocks;
import moldmod.block.MoldyLogBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.block.enums.SlabType;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

/**
 * Fluent builder utility to construct test rooms, mold colonies, chimneys, and openings in GameTests.
 */
public class RoomTestBuilder {

    private final TestContext context;

    public RoomTestBuilder(TestContext context) {
        this.context = context;
    }

    public static RoomTestBuilder of(TestContext context) {
        return new RoomTestBuilder(context);
    }

    public RoomTestBuilder fill(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, BlockState state) {
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    context.setBlockState(new BlockPos(x, y, z), state);
                }
            }
        }
        return this;
    }

    public RoomTestBuilder hollowBox(int minX, int minY, int minZ, int maxX, int maxY, int maxZ,
                                     BlockState wallState, BlockState innerState) {
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    boolean isWall = (x == minX || x == maxX || y == minY || y == maxY || z == minZ || z == maxZ);
                    context.setBlockState(new BlockPos(x, y, z), isWall ? wallState : innerState);
                }
            }
        }
        return this;
    }

    public RoomTestBuilder stoneRoom(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        return hollowBox(minX, minY, minZ, maxX, maxY, maxZ, Blocks.STONE.getDefaultState(), Blocks.AIR.getDefaultState());
    }

    public RoomTestBuilder set(int x, int y, int z, BlockState state) {
        context.setBlockState(new BlockPos(x, y, z), state);
        return this;
    }

    public RoomTestBuilder set(int x, int y, int z, Block block) {
        return set(x, y, z, block.getDefaultState());
    }

    public RoomTestBuilder setAir(int x, int y, int z) {
        return set(x, y, z, Blocks.AIR.getDefaultState());
    }

    public RoomTestBuilder addMoldyLog(int x, int y, int z, Block vanillaLog, int stage, boolean waxed) {
        Block moldyBlock = ModBlocks.VANILLA_TO_MOLDY.get(vanillaLog);
        if (moldyBlock != null) {
            BlockState state = moldyBlock.getDefaultState()
                    .with(MoldyLogBlock.STAGE, stage)
                    .with(MoldyLogBlock.WAXED, waxed);
            context.setBlockState(new BlockPos(x, y, z), state);
        }
        return this;
    }

    public RoomTestBuilder addMoldyOakLog(int x, int y, int z, int stage) {
        return addMoldyLog(x, y, z, Blocks.OAK_LOG, stage, false);
    }

    public RoomTestBuilder addDoor(int x, int y, int z, Block doorBlock, Direction facing, boolean open) {
        BlockState lower = doorBlock.getDefaultState()
                .with(DoorBlock.FACING, facing)
                .with(DoorBlock.HALF, DoubleBlockHalf.LOWER)
                .with(DoorBlock.OPEN, open);
        BlockState upper = doorBlock.getDefaultState()
                .with(DoorBlock.FACING, facing)
                .with(DoorBlock.HALF, DoubleBlockHalf.UPPER)
                .with(DoorBlock.OPEN, open);
        context.setBlockState(new BlockPos(x, y, z), lower);
        context.setBlockState(new BlockPos(x, y + 1, z), upper);
        return this;
    }

    public RoomTestBuilder addDoor(int x, int y, int z, Direction facing, boolean open) {
        return addDoor(x, y, z, Blocks.OAK_DOOR, facing, open);
    }

    public RoomTestBuilder addTrapdoor(int x, int y, int z, Block trapdoorBlock, Direction facing, BlockHalf half, boolean open) {
        BlockState state = trapdoorBlock.getDefaultState()
                .with(TrapdoorBlock.FACING, facing)
                .with(TrapdoorBlock.HALF, half)
                .with(TrapdoorBlock.OPEN, open);
        context.setBlockState(new BlockPos(x, y, z), state);
        return this;
    }

    public RoomTestBuilder addTrapdoor(int x, int y, int z, Block trapdoorBlock, BlockHalf half, boolean open) {
        return addTrapdoor(x, y, z, trapdoorBlock, Direction.NORTH, half, open);
    }

    public RoomTestBuilder addSlab(int x, int y, int z, Block slabBlock, SlabType type) {
        BlockState state = slabBlock.getDefaultState().with(SlabBlock.TYPE, type);
        context.setBlockState(new BlockPos(x, y, z), state);
        return this;
    }

    public RoomTestBuilder addStairs(int x, int y, int z, Block stairsBlock, Direction facing, BlockHalf half) {
        BlockState state = stairsBlock.getDefaultState()
                .with(StairsBlock.FACING, facing)
                .with(StairsBlock.HALF, half);
        context.setBlockState(new BlockPos(x, y, z), state);
        return this;
    }

    public RoomTestBuilder addVerticalChimney(int x, int z, int yBottom, int yTop) {
        for (int y = yBottom; y <= yTop; y++) {
            // Air flue in center
            context.setBlockState(new BlockPos(x, y, z), Blocks.AIR.getDefaultState());
            // Stone casing surrounding the chimney flue
            context.setBlockState(new BlockPos(x + 1, y, z), Blocks.STONE.getDefaultState());
            context.setBlockState(new BlockPos(x - 1, y, z), Blocks.STONE.getDefaultState());
            context.setBlockState(new BlockPos(x, y, z + 1), Blocks.STONE.getDefaultState());
            context.setBlockState(new BlockPos(x, y, z - 1), Blocks.STONE.getDefaultState());
        }
        return this;
    }

    public RoomTestBuilder clearOpenAirColumn(int x, int z, int yStart, int yEnd) {
        for (int y = yStart; y <= yEnd; y++) {
            context.setBlockState(new BlockPos(x, y, z), Blocks.AIR.getDefaultState());
        }
        return this;
    }
}
