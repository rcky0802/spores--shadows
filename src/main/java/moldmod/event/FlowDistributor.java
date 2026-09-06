package moldmod.event;

import me.shedaniel.autoconfig.AutoConfig;
import moldmod.block.ModBlocks;
import moldmod.block.MoldyBlock;
import moldmod.config.ModConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.GrateBlock;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.block.WallBlock;
import net.minecraft.block.enums.SlabType;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 3D Air Resistance, Flow Distribution, and Weighted Water-Filling Engine.
 * Faithful Java 1:1 translation of Test/flow_distributor.py and Test/voxel_world.py.
 *
 * Responsibilities:
 * 1. High-level max-flow solve orchestration (consolidating VoxelMaxFlowEngine).
 * 2. Node-Splitting (u_IN -> u_OUT) with finite volumetric bandwidth (24.0 for pure air).
 * 3. Independent injection channels for each open face of START emitters (pure emission, c(s_IN, s_OUT) = 0).
 * 4. Weighted Water-Filling (Spatial Conductance Max-Min Fairness):
 *    Conductance weights w_i = 1 / (1 + alpha * d_i) prioritize shortest paths to sky/goals.
 * 5. Flow extraction and assignment across faces, blocks, and voxels.
 */
public class FlowDistributor {

    private static final Direction[] DIRECTIONS = Direction.values();

    public record MaxFlowResult(
            double totalMaxFlow,
            double totalRequested,
            double normalizedAeration,
            Map<BlockPos, Double> startBlockFlows,
            Map<BlockPos, Double> startBlockCaps,
            Map<BlockPos, Double> startBlockScores,
            Map<BlockPos, Integer> startBlockDistances,
            Map<BlockPos, Integer> distToGoal
    ) {
        public MaxFlowResult(double totalMaxFlow, double totalRequested, double normalizedAeration,
                             Map<BlockPos, Double> startBlockFlows, Map<BlockPos, Double> startBlockCaps,
                             Map<BlockPos, Double> startBlockScores, Map<BlockPos, Integer> startBlockDistances) {
            this(totalMaxFlow, totalRequested, normalizedAeration, startBlockFlows, startBlockCaps, startBlockScores, startBlockDistances, Collections.emptyMap());
        }

        public MaxFlowResult(double totalMaxFlow, double totalRequested, double normalizedAeration,
                             Map<BlockPos, Double> startBlockFlows, Map<BlockPos, Double> startBlockCaps,
                             Map<BlockPos, Double> startBlockScores) {
            this(totalMaxFlow, totalRequested, normalizedAeration, startBlockFlows, startBlockCaps, startBlockScores, Collections.emptyMap(), Collections.emptyMap());
        }
    }

    public static final class SourceFaceChannel {
        public final int sourceEdgeIdx;
        public final BlockPos startPos;
        public final Direction dir;
        public final BlockPos nxt;
        public final double faceCap;
        public final double weight;
        public final int dGoal;
        public double currentCap;

        public SourceFaceChannel(int sourceEdgeIdx, BlockPos startPos, Direction dir, BlockPos nxt,
                                 double faceCap, double weight, int dGoal) {
            this.sourceEdgeIdx = sourceEdgeIdx;
            this.startPos = startPos;
            this.dir = dir;
            this.nxt = nxt;
            this.faceCap = faceCap;
            this.weight = weight;
            this.dGoal = dGoal;
            this.currentCap = 0.0;
        }
    }

    private final WorldAccess world;
    private final ModConfig config;
    private final List<BlockPos> indexedPositions;
    private final Map<BlockPos, Integer> posToIdx;
    private final Set<BlockPos> starts;
    private final Set<BlockPos> goals;
    private final List<BFSExplorer.StartFace> startFaces;
    private final Map<BlockPos, Integer> distToGoal;
    private final double baseUnitCapacity;
    private final double alpha;

    private final int N;
    private final int M;
    public final int sourceIdx;
    public final int sinkIdx;

    public final FastDinicSolver dinic;
    public final List<SourceFaceChannel> sourceFaceChannels;

    public FlowDistributor(WorldAccess world, ModConfig config, List<BlockPos> indexedPositions,
                           Map<BlockPos, Integer> posToIdx, Set<BlockPos> starts, Set<BlockPos> goals,
                           List<BFSExplorer.StartFace> startFaces, Map<BlockPos, Integer> distToGoal) {
        this.world = world;
        this.config = config;
        this.indexedPositions = indexedPositions;
        this.posToIdx = posToIdx;
        this.starts = starts;
        this.goals = goals;
        this.startFaces = startFaces;
        this.distToGoal = distToGoal;
        this.baseUnitCapacity = config.toxicity.open_sky_ventilation_per_block;
        this.alpha = config.toxicity.ventilation_distance_alpha;

        this.N = indexedPositions.size();
        this.M = startFaces.size();
        this.sourceIdx = 2 * N + M;
        this.sinkIdx = 2 * N + M + 1;

        int estimatedEdges = 6 * N + N + goals.size() + 2 * M + 16;
        this.dinic = new FastDinicSolver(2 * N + M + 2, estimatedEdges);
        this.sourceFaceChannels = new ArrayList<>(M);
    }

    public static MaxFlowResult solve(WorldAccess world, BlockPos startPos, int maxAirVolume, int maxEuclideanRadius) {
        return solve(world, Collections.singleton(startPos), maxAirVolume, maxEuclideanRadius);
    }

    public static MaxFlowResult solve(WorldAccess world, Set<BlockPos> starts, int maxAirVolume, int maxEuclideanRadius) {
        if (starts == null || starts.isEmpty()) {
            return new MaxFlowResult(0.0, 0.0, 0.0, Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap());
        }
        ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        BFSExplorer.ReachableSpace space = BFSExplorer.exploreReachableSpace(world, starts, maxAirVolume, maxEuclideanRadius, config);
        return solve(world, space, starts, config);
    }

    public static MaxFlowResult solve(WorldAccess world, BFSExplorer.ReachableSpace space, Set<BlockPos> starts, ModConfig config) {
        if (starts == null || starts.isEmpty() || space.indexedPositions().isEmpty()) {
            return new MaxFlowResult(0.0, 0.0, 0.0, Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap());
        }
        double baseUnitCapacity = config.toxicity.open_sky_ventilation_per_block;

        List<BFSExplorer.StartFace> startFaces = BFSExplorer.detectStartFaces(world, starts, space.posToIdx(), baseUnitCapacity);
        Map<BlockPos, Integer> distToGoal = BFSExplorer.computeGoalDistances(world, space.goals(), space.posToIdx());

        FlowDistributor distributor = new FlowDistributor(
                world, config, space.indexedPositions(), space.posToIdx(), starts, space.goals(), startFaces, distToGoal);
        distributor.buildNetwork();
        distributor.distributeFlow(0.5);

        return distributor.extractAssignedFlows();
    }

    public static double calculateAerationAt(WorldAccess world, BlockPos pos) {
        ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        return solve(world, pos, config.toxicity.max_air_volume, config.toxicity.max_euclidean_radius).normalizedAeration();
    }

    public static double calculateVentilationFlowAt(WorldAccess world, BlockPos pos) {
        ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        return solve(world, pos, config.toxicity.max_air_volume, config.toxicity.max_euclidean_radius).totalMaxFlow();
    }

    public void buildNetwork() {
        // 1. Node-Splitting (u_IN -> u_OUT) and Spatial Adjacency
        for (int i = 0; i < N; i++) {
            BlockPos u = indexedPositions.get(i);
            int uIn = 2 * i;
            int uOut = 2 * i + 1;
            BlockState uState = world.getBlockState(u);

            double nodeCap = starts.contains(u) ? 0.0 : getNodeInternalCapacity(world, u, uState, config);
            dinic.addEdge(uIn, uOut, nodeCap);

            if (goals.contains(u)) {
                if (uState.isAir()) {
                    dinic.addEdge(uIn, sinkIdx, baseUnitCapacity);
                } else {
                    dinic.addEdge(uOut, sinkIdx, baseUnitCapacity);
                }
            }

            if (!starts.contains(u)) {
                for (Direction dir : DIRECTIONS) {
                    BlockPos v = u.offset(dir);
                    Integer vIdx = posToIdx.get(v);
                    if (vIdx != null && !starts.contains(v)) {
                        int vIn = 2 * vIdx;
                        BlockState vState = world.getBlockState(v);

                        int exitMask = BFSExplorer.getFaceOpenMask(world, u, uState, dir);
                        int enterMask = BFSExplorer.getFaceOpenMask(world, v, vState, dir.getOpposite());
                        int sharedBits = Integer.bitCount(exitMask & enterMask);
                        if (sharedBits > 0) {
                            double edgeCap = baseUnitCapacity * (sharedBits / 4.0);
                            dinic.addEdge(uOut, vIn, edgeCap);
                        }
                    }
                }
            }
        }

        // 2. Single-Face Injection Channels with Spatial Conductance Weights
        sourceFaceChannels.clear();
        for (int j = 0; j < M; j++) {
            BFSExplorer.StartFace sf = startFaces.get(j);
            int faceNode = 2 * N + j;
            int nxtIn = 2 * posToIdx.get(sf.nxt());

            int sourceEdgeIdx = dinic.addEdge(sourceIdx, faceNode, 0.0);
            dinic.addEdge(faceNode, nxtIn, sf.capacity());

            int dGoal = distToGoal.getOrDefault(sf.nxt(), 999);
            double weight = BFSExplorer.computeConductanceWeight(dGoal, alpha);

            sourceFaceChannels.add(new SourceFaceChannel(
                    sourceEdgeIdx, sf.startPos(), sf.dir(), sf.nxt(), sf.capacity(), weight, dGoal));
        }
    }

    public void distributeFlow(double deltaF) {
        while (true) {
            boolean addedAnyCap = false;
            for (SourceFaceChannel ch : sourceFaceChannels) {
                if (ch.currentCap < ch.faceCap) {
                    double step = (ch.weight > 0.0) ? Math.max(0.05, deltaF * ch.weight) : 0.0;
                    ch.currentCap = Math.min(ch.faceCap, ch.currentCap + step);
                    dinic.setEdgeCap(ch.sourceEdgeIdx, ch.currentCap);
                    addedAnyCap = true;
                }
            }

            if (!addedAnyCap) {
                break;
            }

            double pushedInRound = 0.0;
            while (dinic.bfs(sourceIdx, sinkIdx)) {
                System.arraycopy(dinic.head, 0, dinic.ptr, 0, dinic.n);
                while (true) {
                    double tr = dinic.dfs(sourceIdx, sinkIdx, Double.POSITIVE_INFINITY);
                    if (tr <= 1e-6) {
                        break;
                    }
                    pushedInRound += tr;
                }
            }

            if (pushedInRound <= 1e-6) {
                break;
            }
        }
    }

    public MaxFlowResult extractAssignedFlows() {
        Map<BlockPos, Double> startBlockFlows = new HashMap<>();
        Map<BlockPos, Double> startBlockCaps = new HashMap<>();
        Map<BlockPos, Double> startBlockScores = new HashMap<>();
        Map<BlockPos, Integer> startBlockDistances = new HashMap<>();

        for (SourceFaceChannel ch : sourceFaceChannels) {
            double fVal = dinic.getEdgeFlow(ch.sourceEdgeIdx);
            startBlockFlows.put(ch.startPos, startBlockFlows.getOrDefault(ch.startPos, 0.0) + fVal);
            startBlockCaps.put(ch.startPos, startBlockCaps.getOrDefault(ch.startPos, 0.0) + ch.faceCap);
            int prevD = startBlockDistances.getOrDefault(ch.startPos, 999);
            if (ch.dGoal < prevD) {
                startBlockDistances.put(ch.startPos, ch.dGoal);
            }
        }

        for (BlockPos s : starts) {
            double bFlow = startBlockFlows.getOrDefault(s, 0.0);
            double bCap = startBlockCaps.getOrDefault(s, 0.0);

            if (Math.abs(bFlow - Math.round(bFlow)) < 1e-5) {
                bFlow = Math.round(bFlow);
            } else if (Math.abs(bFlow - Math.round(bFlow * 2.0) / 2.0) < 1e-5) {
                bFlow = Math.round(bFlow * 2.0) / 2.0;
            }
            startBlockFlows.put(s, bFlow);

            double scorePct = (bCap > 0.0) ? (bFlow / bCap * 100.0) : 0.0;
            if (Math.abs(scorePct - Math.round(scorePct)) < 1e-5) {
                scorePct = Math.round(scorePct);
            }
            startBlockScores.put(s, scorePct);

            if (distToGoal.containsKey(s)) {
                int directD = distToGoal.get(s);
                int prevD = startBlockDistances.getOrDefault(s, 999);
                if (directD < prevD) {
                    startBlockDistances.put(s, directD);
                }
            }
            if (!startBlockDistances.containsKey(s)) {
                startBlockDistances.put(s, 999);
            }
        }

        double totalMaxFlow = 0.0;
        for (double f : startBlockFlows.values()) {
            totalMaxFlow += f;
        }
        if (Math.abs(totalMaxFlow - Math.round(totalMaxFlow)) < 1e-5) {
            totalMaxFlow = Math.round(totalMaxFlow);
        } else if (Math.abs(totalMaxFlow - Math.round(totalMaxFlow * 2.0) / 2.0) < 1e-5) {
            totalMaxFlow = Math.round(totalMaxFlow * 2.0) / 2.0;
        }

        double totalRequested = 0.0;
        for (double c : startBlockCaps.values()) {
            totalRequested += c;
        }
        if (Math.abs(totalRequested - Math.round(totalRequested)) < 1e-5) {
            totalRequested = Math.round(totalRequested);
        }

        double normalizedAeration = (totalRequested > 0.0) ? (totalMaxFlow / totalRequested) : 0.0;
        if (Math.abs(normalizedAeration - 1.0) < 1e-5) {
            normalizedAeration = 1.0;
        }

        return new MaxFlowResult(
                totalMaxFlow, totalRequested, normalizedAeration,
                startBlockFlows, startBlockCaps, startBlockScores,
                startBlockDistances, distToGoal);
    }

    public static double getNodeInternalCapacity(WorldAccess world, BlockPos pos, BlockState state, ModConfig config) {
        if (state == null || state.isAir()) {
            return config.toxicity.open_sky_ventilation_per_block;
        }
        if (!state.getFluidState().isEmpty()) {
            return 0.0;
        }

        Block block = state.getBlock();

        if (block instanceof GrateBlock) {
            return config.toxicity.copper_grate_ventilation_per_block;
        }
        if (block instanceof LeavesBlock) {
            return config.toxicity.leaves_ventilation_value;
        }

        if (block instanceof DoorBlock) {
            boolean isOpen = state.contains(Properties.OPEN) && state.get(Properties.OPEN);
            return isOpen ? config.toxicity.door_ventilation_value : 0.0;
        }
        if (block instanceof TrapdoorBlock) {
            boolean isOpen = state.contains(Properties.OPEN) && state.get(Properties.OPEN);
            return isOpen ? config.toxicity.trapdoor_ventilation_value : 0.0;
        }

        if (block instanceof FenceGateBlock) {
            boolean isOpen = state.contains(Properties.OPEN) && state.get(Properties.OPEN);
            return isOpen ? config.toxicity.fence_gate_open_ventilation_value : config.toxicity.ventilation_gap_bonus;
        }

        if (block instanceof FenceBlock || state.isOf(Blocks.IRON_BARS)) {
            return config.toxicity.ventilation_gap_bonus;
        }

        if (block instanceof WallBlock) {
            if (BFSExplorer.isWallConnected(state)) {
                return 0.0;
            }
            return config.toxicity.ventilation_gap_bonus;
        }

        if (block instanceof SlabBlock) {
            SlabType type = state.contains(Properties.SLAB_TYPE) ? state.get(Properties.SLAB_TYPE) : SlabType.BOTTOM;
            if (type == SlabType.DOUBLE) {
                return 0.0;
            }
            return config.toxicity.slab_ventilation_value;
        }

        if (block instanceof StairsBlock) {
            return config.toxicity.stairs_ventilation_value;
        }

        if (state.isOpaqueFullCube(world, pos)) {
            return 0.0;
        }

        return config.toxicity.open_sky_ventilation_per_block;
    }

    public static boolean isSusceptibleOrMoldy(BlockState state) {
        if (state == null || state.isAir()) {
            return false;
        }
        if (state.contains(MoldyBlock.STAGE)) {
            return !state.contains(MoldyBlock.WAXED) || !state.get(MoldyBlock.WAXED);
        }
        return ModBlocks.VANILLA_TO_MOLDY.containsKey(state.getBlock());
    }
}
