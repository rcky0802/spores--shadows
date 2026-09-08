package moldmod.event;

import moldmod.block.MoldyBlock;
import moldmod.config.ModConfig;
import moldmod.event.MiasmaCalculator.BlockAerationType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.GrateBlock;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.PaneBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.block.WallBlock;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.block.enums.SlabType;
import net.minecraft.block.enums.StairShape;
import net.minecraft.block.enums.WallShape;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * Preliminary BFS Exploration, 3D Spatial Connectivity, and Geometry Bitmask Engine.
 * Faithful Java 1:1 translation of Test/bfs_explorer.py.
 *
 * Responsibilities:
 * 1. Directional contact bitmasks (4 quadrants / 8 octants) for slabs, stairs, fences, gates, doors, trapdoors, grates, etc.
 * 2. Multi-source BFS domain discovery from starts to collect reachable air space.
 * 3. Unified room scanner (scanRoom) extracting air blocks, mold toxicity, susceptible perimeter, and open-air state.
 * 4. Automatic detection of open start faces and their geometric capacities.
 * 5. Backward multi-source BFS from goals (cielo / Sky Goals) to compute topological distances to exit.
 * 6. Spatial conductance weight calculation.
 */
public final class BFSExplorer {

    private static final Direction[] DIRECTIONS = Direction.values();

    private BFSExplorer() {
    }

    public record StartFace(BlockPos startPos, Direction dir, BlockPos nxt, double capacity) {
    }

    public record ReachableSpace(List<BlockPos> indexedPositions, Map<BlockPos, Integer> posToIdx, Set<BlockPos> goals) {
    }

    public record RoomScanResult(
            Set<BlockPos> airBlocks,
            Set<BlockPos> roomSusceptible,
            double toxicScore,
            boolean openAir,
            boolean hitBoundaryWithOpenAir
    ) {
    }

    // =========================================================================
    // 1. GEOMETRIA A BITMASK A 4 QUADRANTI / 8 OTTANTI
    // =========================================================================

    public static int getFaceOpenMask(BlockView world, BlockPos pos, BlockState state, Direction face) {
        if (state == null || state.isAir()) {
            return 0b1111;
        }
        if (!state.getFluidState().isEmpty()) {
            return 0b0000;
        }

        Block block = state.getBlock();

        // Copper grates & Foglie: 100% aperto
        if (block instanceof GrateBlock || block instanceof LeavesBlock) {
            return 0b1111;
        }

        // Staccionate, Barre di ferro, Cancelli
        if (block instanceof FenceBlock || state.isOf(Blocks.IRON_BARS) || block instanceof FenceGateBlock) {
            return 0b1111;
        }

        // Porte e Botole
        if (block instanceof DoorBlock || block instanceof TrapdoorBlock) {
            return isBlockAirflowBlocked(state, face) ? 0b0000 : 0b1111;
        }

        // Muretti (WallBlock)
        if (block instanceof WallBlock) {
            if (face.getAxis().isVertical() || !isWallConnected(state)) {
                return 0b1111;
            }
            return 0b0000;
        }

        // Lastre (Slabs)
        if (block instanceof SlabBlock) {
            SlabType type = state.contains(Properties.SLAB_TYPE) ? state.get(Properties.SLAB_TYPE) : SlabType.BOTTOM;
            if (type == SlabType.DOUBLE) {
                return 0b0000;
            }
            if (type == SlabType.BOTTOM) {
                if (face == Direction.DOWN) {
                    return 0b0000;
                }
                if (face == Direction.UP) {
                    return 0b1111;
                }
                return 0b0011; // bit 0, 1 (Top) aperti, bit 2, 3 (Bottom) solidi
            } else { // TOP slab
                if (face == Direction.UP) {
                    return 0b0000;
                }
                if (face == Direction.DOWN) {
                    return 0b1111;
                }
                return 0b1100; // bit 2, 3 (Bottom) aperti, bit 0, 1 (Top) solidi
            }
        }

        // Scale (Stairs)
        if (block instanceof StairsBlock) {
            return getStairsFaceOpenMask(state, face);
        }

        // Blocchi solidi completi di default
        if (state.isSideSolidFullSquare(world, pos, face)) {
            return 0b0000;
        }

        return 0b1111;
    }

    public static int getStairsFaceOpenMask(BlockState state, Direction face) {
        BlockHalf half = state.contains(Properties.BLOCK_HALF) ? state.get(Properties.BLOCK_HALF) : BlockHalf.BOTTOM;
        Direction facing = state.contains(Properties.HORIZONTAL_FACING) ? state.get(Properties.HORIZONTAL_FACING)
                : Direction.NORTH;
        StairShape shape = state.contains(Properties.STAIR_SHAPE) ? state.get(Properties.STAIR_SHAPE)
                : StairShape.STRAIGHT;

        int solidOctants = 0;

        if (half == BlockHalf.BOTTOM) {
            solidOctants |= (1 << 4) | (1 << 5) | (1 << 6) | (1 << 7);
            solidOctants |= getStairStepOctants(facing, shape);
        } else {
            solidOctants |= (1 << 0) | (1 << 1) | (1 << 2) | (1 << 3);
            solidOctants |= (getStairStepOctants(facing, shape) << 4);
        }

        int oct0, oct1, oct2, oct3;
        switch (face) {
            case NORTH -> { oct0 = 0; oct1 = 1; oct2 = 4; oct3 = 5; }
            case SOUTH -> { oct0 = 2; oct1 = 3; oct2 = 6; oct3 = 7; }
            case WEST  -> { oct0 = 0; oct1 = 2; oct2 = 4; oct3 = 6; }
            case EAST  -> { oct0 = 1; oct1 = 3; oct2 = 5; oct3 = 7; }
            case UP    -> { oct0 = 0; oct1 = 1; oct2 = 2; oct3 = 3; }
            case DOWN  -> { oct0 = 4; oct1 = 5; oct2 = 6; oct3 = 7; }
            default    -> { return 0b0000; }
        }

        int mask = 0;
        if ((solidOctants & (1 << oct0)) == 0) mask |= 1;
        if ((solidOctants & (1 << oct1)) == 0) mask |= 2;
        if ((solidOctants & (1 << oct2)) == 0) mask |= 4;
        if ((solidOctants & (1 << oct3)) == 0) mask |= 8;
        return mask;
    }

    private static int getStairStepOctants(Direction facing, StairShape shape) {
        if (shape == StairShape.STRAIGHT) {
            return switch (facing) {
                case NORTH -> (1 << 0) | (1 << 1);
                case SOUTH -> (1 << 2) | (1 << 3);
                case WEST  -> (1 << 0) | (1 << 2);
                case EAST  -> (1 << 1) | (1 << 3);
                default    -> (1 << 0) | (1 << 1);
            };
        } else if (shape == StairShape.INNER_LEFT) {
            return switch (facing) {
                case NORTH -> (1 << 0) | (1 << 1) | (1 << 2);
                case SOUTH -> (1 << 1) | (1 << 2) | (1 << 3);
                case WEST  -> (1 << 0) | (1 << 2) | (1 << 3);
                case EAST  -> (1 << 0) | (1 << 1) | (1 << 3);
                default    -> 0b1111;
            };
        } else if (shape == StairShape.INNER_RIGHT) {
            return switch (facing) {
                case NORTH -> (1 << 0) | (1 << 1) | (1 << 3);
                case SOUTH -> (1 << 0) | (1 << 2) | (1 << 3);
                case WEST  -> (1 << 0) | (1 << 1) | (1 << 2);
                case EAST  -> (1 << 1) | (1 << 2) | (1 << 3);
                default    -> 0b1111;
            };
        } else if (shape == StairShape.OUTER_LEFT) {
            return switch (facing) {
                case NORTH -> (1 << 0);
                case SOUTH -> (1 << 3);
                case WEST  -> (1 << 2);
                case EAST  -> (1 << 1);
                default    -> 0;
            };
        } else if (shape == StairShape.OUTER_RIGHT) {
            return switch (facing) {
                case NORTH -> (1 << 1);
                case SOUTH -> (1 << 2);
                case WEST  -> (1 << 0);
                case EAST  -> (1 << 3);
                default    -> 0;
            };
        }
        return 0;
    }

    public static boolean isFaceSolid(BlockView world, BlockPos pos, BlockState state, Direction face) {
        return getFaceOpenMask(world, pos, state, face) == 0;
    }

    public static boolean isWallConnected(BlockState state) {
        if (!(state.getBlock() instanceof WallBlock)) {
            return false;
        }
        boolean ns = (state.contains(WallBlock.NORTH_SHAPE) && state.get(WallBlock.NORTH_SHAPE) != WallShape.NONE) &&
                     (state.contains(WallBlock.SOUTH_SHAPE) && state.get(WallBlock.SOUTH_SHAPE) != WallShape.NONE);
        boolean ew = (state.contains(WallBlock.EAST_SHAPE) && state.get(WallBlock.EAST_SHAPE) != WallShape.NONE) &&
                     (state.contains(WallBlock.WEST_SHAPE) && state.get(WallBlock.WEST_SHAPE) != WallShape.NONE);
        return ns || ew;
    }

    public static BlockAerationType getAerationType(BlockView world, BlockPos pos, BlockState state, Direction entryFace) {
        if (state == null || state.isAir()) {
            return BlockAerationType.OPEN_AIR;
        }

        Block block = state.getBlock();

        // 1. Copper Grates: treated like air
        if (block instanceof GrateBlock) {
            return BlockAerationType.OPEN_AIR;
        }

        // 2. Fences & Fence Gates:
        if (block instanceof FenceBlock) {
            return BlockAerationType.VENTILATED;
        }
        if (block instanceof FenceGateBlock) {
            boolean isOpen = state.contains(Properties.OPEN) && state.get(Properties.OPEN);
            return isOpen ? BlockAerationType.OPEN_AIR : BlockAerationType.VENTILATED;
        }

        // 3. Wall blocks:
        if (block instanceof WallBlock) {
            if (entryFace.getAxis().isVertical() || !isWallConnected(state)) {
                return BlockAerationType.VENTILATED;
            }
            return BlockAerationType.HERMETIC;
        }

        // 4. Doors, Trapdoors:
        if (block instanceof DoorBlock || block instanceof TrapdoorBlock) {
            Direction flowDir = entryFace.getOpposite();
            boolean isBlockingFlow = isBlockAirflowBlocked(state, flowDir);
            return isBlockingFlow ? BlockAerationType.HERMETIC : BlockAerationType.OPEN_AIR;
        }

        // 5. Grates / Panes (Iron Bars)
        if (block instanceof PaneBlock) {
            if (state.isOf(Blocks.IRON_BARS)) {
                return BlockAerationType.VENTILATED;
            }
            return BlockAerationType.HERMETIC;
        }

        if (isFaceSolid(world, pos, state, entryFace)) {
            return BlockAerationType.HERMETIC;
        }

        boolean hasNonSolidExit = false;
        for (Direction d : DIRECTIONS) {
            if (d != entryFace) {
                if (!isFaceSolid(world, pos, state, d)) {
                    hasNonSolidExit = true;
                    break;
                }
            }
        }

        if (!hasNonSolidExit) {
            return BlockAerationType.HERMETIC;
        }

        if (block instanceof SlabBlock || block instanceof StairsBlock) {
            return BlockAerationType.VENTILATED;
        }

        return BlockAerationType.OPEN_AIR;
    }

    public static boolean isBlockAirflowBlocked(BlockState state, Direction flowDir) {
        if (state == null) {
            return false;
        }
        if (state.getBlock() instanceof TrapdoorBlock) {
            boolean isOpen = state.contains(Properties.OPEN) && state.get(Properties.OPEN);
            if (!isOpen) {
                return flowDir.getAxis().isVertical();
            } else {
                Direction facing = state.contains(Properties.HORIZONTAL_FACING)
                        ? state.get(Properties.HORIZONTAL_FACING)
                        : Direction.NORTH;
                return flowDir.getAxis() == facing.getAxis();
            }
        }
        if (state.getBlock() instanceof DoorBlock) {
            boolean isOpen = state.contains(Properties.OPEN) && state.get(Properties.OPEN);
            return !isOpen;
        }
        return false;
    }

    public static boolean canAirPass(BlockView world, BlockPos fromPos, BlockState fromState,
                                     BlockPos toPos, BlockState toState, Direction dir) {
        if (fromState == null || toState == null) {
            return false;
        }
        if (!toState.getFluidState().isEmpty()) {
            return false;
        }

        int fromMask = getFaceOpenMask(world, fromPos, fromState, dir);
        int toMask = getFaceOpenMask(world, toPos, toState, dir.getOpposite());
        if ((fromMask & toMask) == 0) {
            return false;
        }

        BlockAerationType fromType = getAerationType(world, fromPos, fromState, dir.getOpposite());
        if (fromType != BlockAerationType.OPEN_AIR) {
            return false;
        }

        BlockAerationType toType = getAerationType(world, toPos, toState, dir.getOpposite());
        return toType == BlockAerationType.OPEN_AIR;
    }

    public static boolean isCoveredByCeiling(WorldAccess world, BlockPos pos) {
        for (int dy = 1; dy <= 24; dy++) {
            BlockPos upPos = pos.up(dy);
            BlockState upState = world.getBlockState(upPos);
            if (upState.isOf(Blocks.BARRIER) || upState.isOf(Blocks.STRUCTURE_BLOCK)
                    || upState.isOf(Blocks.STRUCTURE_VOID)) {
                continue;
            }
            if (isCeilingBarrier(world, upPos, upState)) {
                return true;
            }
        }

        for (int dy = 1; dy <= 24; dy++) {
            BlockPos checkPos = pos.up(dy);
            BlockState checkState = world.getBlockState(checkPos);
            if (checkState.isOf(Blocks.BARRIER) || checkState.isOf(Blocks.STRUCTURE_BLOCK)
                    || checkState.isOf(Blocks.STRUCTURE_VOID)) {
                break;
            }

            boolean hasNorth = hasCeilingBarrierInDirection(world, checkPos, Direction.NORTH, 3);
            boolean hasSouth = hasCeilingBarrierInDirection(world, checkPos, Direction.SOUTH, 3);
            boolean hasEast = hasCeilingBarrierInDirection(world, checkPos, Direction.EAST, 3);
            boolean hasWest = hasCeilingBarrierInDirection(world, checkPos, Direction.WEST, 3);

            int count = (hasNorth ? 1 : 0) + (hasSouth ? 1 : 0) + (hasEast ? 1 : 0) + (hasWest ? 1 : 0);
            if ((count == 4) || (count >= 3 && ((hasNorth && hasSouth) || (hasEast && hasWest)))) {
                return true;
            }
        }

        return false;
    }

    public static boolean hasCeilingBarrierInDirection(WorldAccess world, BlockPos pos, Direction dir, int maxDist) {
        for (int step = 1; step <= maxDist; step++) {
            BlockPos p = pos.offset(dir, step);
            BlockState state = world.getBlockState(p);
            if (state.isOf(Blocks.BARRIER) || state.isOf(Blocks.STRUCTURE_BLOCK) || state.isOf(Blocks.STRUCTURE_VOID)) {
                return false;
            }
            if (isCeilingBarrier(world, p, state)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isCeilingBarrier(WorldAccess world, BlockPos pos, BlockState state) {
        if (state == null || state.isAir()) {
            return false;
        }
        if (state.getBlock() instanceof GrateBlock) {
            return false;
        }
        BlockAerationType type = getAerationType(world, pos, state, Direction.DOWN);
        return type == BlockAerationType.HERMETIC || type == BlockAerationType.VENTILATED;
    }

    public static boolean isVentilatedToOutside(WorldAccess world, BlockPos gapPos, Direction outwardDir) {
        Direction entryFace = outwardDir.getOpposite();
        BlockState gapState = world.getBlockState(gapPos);

        if (isFaceSolid(world, gapPos, gapState, entryFace)) {
            return false;
        }

        if (!isCoveredByCeiling(world, gapPos)) {
            return true;
        }

        if (outwardDir == Direction.UP) {
            int exitMask = getFaceOpenMask(world, gapPos, gapState, Direction.UP);
            int enterMask = getFaceOpenMask(world, gapPos.up(), world.getBlockState(gapPos.up()), Direction.DOWN);
            if ((exitMask & enterMask) != 0) {
                return !isCoveredByCeiling(world, gapPos.up());
            }
            return false;
        }

        if (outwardDir == Direction.DOWN) {
            BlockPos prevPos = gapPos;
            BlockState prevState = gapState;
            for (int step = 1; step <= 4; step++) {
                BlockPos checkPos = gapPos.down(step);
                BlockState checkState = world.getBlockState(checkPos);
                if (checkState.isOf(Blocks.BARRIER) || checkState.isOf(Blocks.STRUCTURE_BLOCK)) {
                    break;
                }
                int exitMask = getFaceOpenMask(world, prevPos, prevState, Direction.DOWN);
                int enterMask = getFaceOpenMask(world, checkPos, checkState, Direction.UP);
                if ((exitMask & enterMask) == 0) {
                    break;
                }
                if (!isCoveredByCeiling(world, checkPos)) {
                    return true;
                }
                for (Direction side : Direction.Type.HORIZONTAL) {
                    BlockPos sidePrevPos = checkPos;
                    BlockState sidePrevState = checkState;
                    for (int s = 1; s <= 4; s++) {
                        BlockPos sidePos = sidePrevPos.offset(side, 1);
                        BlockState sideState = world.getBlockState(sidePos);
                        int sExitMask = getFaceOpenMask(world, sidePrevPos, sidePrevState, side);
                        int sEnterMask = getFaceOpenMask(world, sidePos, sideState, side.getOpposite());
                        if ((sExitMask & sEnterMask) == 0) {
                            break;
                        }
                        if (!isCoveredByCeiling(world, sidePos)) {
                            return true;
                        }
                        sidePrevPos = sidePos;
                        sidePrevState = sideState;
                    }
                }
                prevPos = checkPos;
                prevState = checkState;
            }
            return false;
        }

        BlockPos prevPos = gapPos;
        BlockState prevState = gapState;

        for (int step = 1; step <= 5; step++) {
            BlockPos checkPos = gapPos.offset(outwardDir, step);
            BlockState checkState = world.getBlockState(checkPos);

            if (checkState.isOf(Blocks.BARRIER) || checkState.isOf(Blocks.STRUCTURE_BLOCK)) {
                break;
            }

            int exitMask = getFaceOpenMask(world, prevPos, prevState, outwardDir);
            int enterMask = getFaceOpenMask(world, checkPos, checkState, outwardDir.getOpposite());
            if ((exitMask & enterMask) == 0) {
                break;
            }

            if (!isCoveredByCeiling(world, checkPos)) {
                return true;
            }

            for (Direction perp : Direction.Type.HORIZONTAL) {
                if (perp != outwardDir && perp != outwardDir.getOpposite()) {
                    BlockPos sidePrevPos = checkPos;
                    BlockState sidePrevState = checkState;
                    for (int sideStep = 1; sideStep <= 3; sideStep++) {
                        BlockPos perpPos = sidePrevPos.offset(perp, 1);
                        BlockState perpState = world.getBlockState(perpPos);
                        int sideExitMask = getFaceOpenMask(world, sidePrevPos, sidePrevState, perp);
                        int sideEnterMask = getFaceOpenMask(world, perpPos, perpState, perp.getOpposite());
                        if ((sideExitMask & sideEnterMask) == 0) {
                            break;
                        }
                        if (!isCoveredByCeiling(world, perpPos)) {
                            return true;
                        }
                        sidePrevPos = perpPos;
                        sidePrevState = perpState;
                    }
                }
            }

            prevPos = checkPos;
            prevState = checkState;
        }
        return false;
    }

    // =========================================================================
    // 2. ESPLORATORE PREVENTIVO BFS (exploreReachableSpace, scanRoom, detectStartFaces, computeGoalDistances)
    // =========================================================================

    public static ReachableSpace exploreReachableSpace(WorldAccess world, Set<BlockPos> starts,
                                                      int maxAirVolume, int maxEuclideanRadius, ModConfig config) {
        Map<BlockPos, Integer> posToIdx = new HashMap<>();
        List<BlockPos> indexedPositions = new ArrayList<>();
        Queue<BlockPos> queue = new ArrayDeque<>();
        Set<BlockPos> goals = new HashSet<>();
        int maxRadiusSq = maxEuclideanRadius * maxEuclideanRadius;

        for (BlockPos s : starts) {
            if (!posToIdx.containsKey(s)) {
                posToIdx.put(s, indexedPositions.size());
                indexedPositions.add(s);
                queue.add(s);
            }
        }

        while (!queue.isEmpty() && indexedPositions.size() < maxAirVolume) {
            BlockPos curr = queue.poll();
            BlockState currState = world.getBlockState(curr);

            if (!starts.contains(curr)) {
                if (!isCoveredByCeiling(world, curr)) {
                    goals.add(curr);
                    continue;
                }
            }

            for (Direction dir : DIRECTIONS) {
                BlockPos nxt = curr.offset(dir);
                if (posToIdx.containsKey(nxt)) {
                    continue;
                }

                int minDistSq = Integer.MAX_VALUE;
                for (BlockPos s : starts) {
                    int dx = nxt.getX() - s.getX();
                    int dy = nxt.getY() - s.getY();
                    int dz = nxt.getZ() - s.getZ();
                    int dSq = dx * dx + dy * dy + dz * dz;
                    if (dSq < minDistSq) {
                        minDistSq = dSq;
                    }
                }
                if (minDistSq > maxRadiusSq) {
                    continue;
                }

                BlockState nxtState = world.getBlockState(nxt);

                int exitMask = getFaceOpenMask(world, curr, currState, dir);
                int enterMask = getFaceOpenMask(world, nxt, nxtState, dir.getOpposite());
                int sharedBits = Integer.bitCount(exitMask & enterMask);

                if (sharedBits > 0) {
                    double nodeCap = FlowDistributor.getNodeInternalCapacity(world, nxt, nxtState, config);
                    if (nodeCap > 0.0 || !isCoveredByCeiling(world, nxt)) {
                        posToIdx.put(nxt, indexedPositions.size());
                        indexedPositions.add(nxt);
                        queue.add(nxt);
                    }
                }
            }
        }

        return new ReachableSpace(indexedPositions, posToIdx, goals);
    }

    /**
     * Unified Room Scanner.
     * Scans reachable air blocks, gathers mold toxicity, and evaluates open-air connectivity.
     */
    public static RoomScanResult scanRoom(WorldAccess world, BlockPos startPos,
                                          int maxAirVolume, int maxEuclideanRadius, ModConfig config) {
        Queue<BlockPos> queue = new ArrayDeque<>();
        Set<BlockPos> visited = new HashSet<>();
        Set<BlockPos> countedMold = new HashSet<>();
        Set<BlockPos> roomSusceptible = new HashSet<>();
        int maxRadiusSq = maxEuclideanRadius * maxEuclideanRadius;
        float moldToxMult = config.toxicity.mold_toxicity_multiplier;
        double toxicScore = 0.0;
        boolean hitBoundaryWithOpenAir = false;

        boolean openAir = !isCoveredByCeiling(world, startPos);

        queue.add(startPos);
        visited.add(startPos);

        while (!queue.isEmpty() && visited.size() < maxAirVolume) {
            BlockPos currentPos = queue.poll();
            BlockState currentState = world.getBlockState(currentPos);

            for (Direction dir : DIRECTIONS) {
                BlockPos neighborPos = currentPos.offset(dir);
                BlockState neighborState = world.getBlockState(neighborPos);

                if (canAirPass(world, currentPos, currentState, neighborPos, neighborState, dir)) {
                    if (!isCoveredByCeiling(world, neighborPos)) {
                        visited.add(neighborPos);
                    } else {
                        int dx = startPos.getX() - neighborPos.getX();
                        int dy = startPos.getY() - neighborPos.getY();
                        int dz = startPos.getZ() - neighborPos.getZ();
                        if ((dx * dx + dy * dy + dz * dz) <= maxRadiusSq) {
                            if (visited.add(neighborPos)) {
                                queue.add(neighborPos);
                            }
                        } else {
                            hitBoundaryWithOpenAir = true;
                        }
                    }
                } else {
                    if (FlowDistributor.isSusceptibleOrMoldy(neighborState)) {
                        roomSusceptible.add(neighborPos);
                    }
                    if (neighborState.contains(MoldyBlock.STAGE)) {
                        boolean isWaxed = neighborState.contains(MoldyBlock.WAXED) && neighborState.get(MoldyBlock.WAXED);
                        if (!isWaxed && countedMold.add(neighborPos)) {
                            int stage = neighborState.get(MoldyBlock.STAGE);
                            toxicScore += (stage * moldToxMult);
                        }
                    }
                }
            }
        }

        if (visited.size() >= maxAirVolume || !queue.isEmpty()) {
            hitBoundaryWithOpenAir = true;
        }

        return new RoomScanResult(
                visited,
                roomSusceptible,
                toxicScore,
                openAir,
                hitBoundaryWithOpenAir
        );
    }

    public static List<StartFace> detectStartFaces(WorldAccess world, Set<BlockPos> starts,
                                                   Map<BlockPos, Integer> posToIdx, double baseUnitCapacity) {
        List<StartFace> startFaces = new ArrayList<>();
        for (BlockPos s : starts) {
            BlockState sState = world.getBlockState(s);
            for (Direction dir : DIRECTIONS) {
                BlockPos nxt = s.offset(dir);
                if (posToIdx.containsKey(nxt)) {
                    BlockState nxtState = world.getBlockState(nxt);
                    int exitMask = getFaceOpenMask(world, s, sState, dir);
                    int enterMask = getFaceOpenMask(world, nxt, nxtState, dir.getOpposite());
                    int sharedBits = Integer.bitCount(exitMask & enterMask);
                    if (sharedBits > 0) {
                        double faceCap = baseUnitCapacity * (sharedBits / 4.0);
                        startFaces.add(new StartFace(s, dir, nxt, faceCap));
                    }
                }
            }
        }
        return startFaces;
    }

    public static Map<BlockPos, Integer> computeGoalDistances(WorldAccess world, Set<BlockPos> goals,
                                                             Map<BlockPos, Integer> posToIdx) {
        Map<BlockPos, Integer> distToGoal = new HashMap<>();
        Queue<BlockPos> goalQueue = new ArrayDeque<>();
        for (BlockPos g : goals) {
            if (posToIdx.containsKey(g)) {
                distToGoal.put(g, 0);
                goalQueue.add(g);
            }
        }

        while (!goalQueue.isEmpty()) {
            BlockPos curr = goalQueue.poll();
            int currD = distToGoal.get(curr);
            BlockState currState = world.getBlockState(curr);

            for (Direction dir : DIRECTIONS) {
                BlockPos nxt = curr.offset(dir);
                if (posToIdx.containsKey(nxt) && !distToGoal.containsKey(nxt)) {
                    BlockState nxtState = world.getBlockState(nxt);
                    int exitMask = getFaceOpenMask(world, curr, currState, dir);
                    int enterMask = getFaceOpenMask(world, nxt, nxtState, dir.getOpposite());
                    int sharedBits = Integer.bitCount(exitMask & enterMask);
                    if (sharedBits > 0) {
                        distToGoal.put(nxt, currD + 1);
                        goalQueue.add(nxt);
                    }
                }
            }
        }
        return distToGoal;
    }

    public static double computeConductanceWeight(int distance, double alpha) {
        if (distance < 900) {
            return 1.0 / (1.0 + alpha * distance);
        }
        return 0.0;
    }
}
