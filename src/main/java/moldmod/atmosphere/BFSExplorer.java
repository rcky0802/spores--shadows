package moldmod.atmosphere;

import moldmod.block.MoldyBlock;
import moldmod.config.ModConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
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
            Set<BlockPos> roomWaterSources,
            Set<BlockPos> roomDehumidifiers,
            Set<BlockPos> roomHumidifiers,
            Set<BlockPos> roomPurifiers,
            double toxicScore,
            boolean openAir,
            boolean hitBoundaryWithOpenAir
    ) {
    }

    // =========================================================================
    // 1. GEOMETRIA A BITMASK A 4 QUADRANTI / 8 OTTANTI
    // =========================================================================

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
                if (!GeometryMaskHelper.isCoveredByCeiling(world, curr)) {
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

                int exitMask = GeometryMaskHelper.getFaceOpenMask(world, curr, currState, dir);
                int enterMask = GeometryMaskHelper.getFaceOpenMask(world, nxt, nxtState, dir.getOpposite());
                int sharedBits = Integer.bitCount(exitMask & enterMask);

                if (sharedBits > 0) {
                    double nodeCap = FlowDistributor.getNodeInternalCapacity(world, nxt, nxtState, config);
                    if (nodeCap > 0.0 || !GeometryMaskHelper.isCoveredByCeiling(world, nxt)) {
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
        Set<BlockPos> roomWaterSources = new HashSet<>();
        Set<BlockPos> roomDehumidifiers = new HashSet<>();
        Set<BlockPos> roomHumidifiers = new HashSet<>();
        Set<BlockPos> roomPurifiers = new HashSet<>();
        int maxRadiusSq = maxEuclideanRadius * maxEuclideanRadius;
        float moldToxMult = config.toxicity.mold_toxicity_multiplier;
        double toxicScore = 0.0;
        boolean hitBoundaryWithOpenAir = false;

        boolean openAir = !GeometryMaskHelper.isCoveredByCeiling(world, startPos);

        queue.add(startPos);
        visited.add(startPos);

        while (!queue.isEmpty() && visited.size() < maxAirVolume) {
            BlockPos currentPos = queue.poll();
            BlockState currentState = world.getBlockState(currentPos);

            for (Direction dir : DIRECTIONS) {
                BlockPos neighborPos = currentPos.offset(dir);
                BlockState neighborState = world.getBlockState(neighborPos);

                if (GeometryMaskHelper.canAirPass(world, currentPos, currentState, neighborPos, neighborState, dir)) {
                    if (!GeometryMaskHelper.isCoveredByCeiling(world, neighborPos)) {
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
                            int stageWeight = (stage == 3) ? 4 : stage;
                            toxicScore += (stageWeight * moldToxMult);
                        }
                    }
                    if (neighborState.getFluidState().isIn(FluidTags.WATER) || neighborState.isOf(Blocks.WATER_CAULDRON)) {
                        roomWaterSources.add(neighborPos.toImmutable());
                    }
                    if (neighborState.isOf(moldmod.block.ModBlocks.DEHUMIDIFIER)) {
                        if (neighborState.contains(moldmod.block.dehumidifier.DehumidifierBlock.STATUS)
                                && neighborState.get(moldmod.block.dehumidifier.DehumidifierBlock.STATUS) == moldmod.block.dehumidifier.DehumidifierStatus.RUNNING) {
                            if (neighborState.contains(moldmod.block.dehumidifier.DehumidifierBlock.MODE)
                                    && neighborState.get(moldmod.block.dehumidifier.DehumidifierBlock.MODE) == moldmod.block.dehumidifier.DehumidifierMode.HUMIDIFY) {
                                roomHumidifiers.add(neighborPos.toImmutable());
                            } else {
                                roomDehumidifiers.add(neighborPos.toImmutable());
                            }
                        }
                    }
                    if (neighborState.isOf(moldmod.block.ModBlocks.AIR_PURIFIER)) {
                        if (neighborState.contains(moldmod.block.purifier.AirPurifierBlock.STATUS)
                                && neighborState.get(moldmod.block.purifier.AirPurifierBlock.STATUS) == moldmod.block.purifier.PurifierStatus.RUNNING) {
                            roomPurifiers.add(neighborPos.toImmutable());
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
                roomWaterSources,
                roomDehumidifiers,
                roomHumidifiers,
                roomPurifiers,
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
                    int exitMask = GeometryMaskHelper.getFaceOpenMask(world, s, sState, dir);
                    int enterMask = GeometryMaskHelper.getFaceOpenMask(world, nxt, nxtState, dir.getOpposite());
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
                    int exitMask = GeometryMaskHelper.getFaceOpenMask(world, curr, currState, dir);
                    int enterMask = GeometryMaskHelper.getFaceOpenMask(world, nxt, nxtState, dir.getOpposite());
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
