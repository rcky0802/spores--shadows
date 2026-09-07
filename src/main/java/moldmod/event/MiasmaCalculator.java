package moldmod.event;

import me.shedaniel.autoconfig.AutoConfig;
import moldmod.block.MoldyBlock;
import moldmod.config.ModConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Core mathematical and volumetric engine for Miasma, Air Toxicity, Room Saturation, and Block Air Evaluation.
 * Orchestrates spatial exploration via BFSExplorer and aerodynamic flow via FlowDistributor.
 */
public final class MiasmaCalculator {

    private static final Direction[] DIRECTIONS = Direction.values();

    private MiasmaCalculator() {
    }

    public enum AirToxicityLevel {
        CLEAN,
        WARNING,
        MODERATE_HUNGER,
        LETHAL_POISON
    }

    public enum RoomVentilationType {
        CLEAN_OPEN_AIR,
        UNCONFINED_CAVERN,
        VENTILATED,
        HERMETIC_SEALED
    }

    public enum BlockAerationType {
        OPEN_AIR,
        VENTILATED,
        HERMETIC
    }

    public static class RoomSaturationManager {
        private static final Map<Long, RoomGasState> ACTIVE_ROOMS = new ConcurrentHashMap<>();

        public record RoomGasState(
                double currentMiasma,
                double targetMiasma,
                long lastUpdateTick) {
        }

        public static BlockPos calculateAnchor(Set<BlockPos> airBlocks, BlockPos defaultPos) {
            if (airBlocks != null && !airBlocks.isEmpty()) {
                BlockPos min = null;
                for (BlockPos pos : airBlocks) {
                    if (min == null || pos.compareTo(min) < 0) {
                        min = pos;
                    }
                }
                return min;
            }
            return defaultPos != null ? defaultPos : BlockPos.ORIGIN;
        }

        public static double getDynamicMiasma(WorldAccess world, BlockPos anchor, double targetMiasma) {
            ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
            if (!config.toxicity.enable_dynamic_spore_saturation) {
                return targetMiasma;
            }
            if (!(world instanceof ServerWorld serverWorld)) {
                return targetMiasma;
            }

            long currentTick = serverWorld.getServer() != null ? serverWorld.getServer().getTicks()
                    : serverWorld.getTime();
            long key = anchor.asLong();
            RoomGasState state = ACTIVE_ROOMS.get(key);

            if (state == null) {
                ACTIVE_ROOMS.put(key, new RoomGasState(targetMiasma, targetMiasma, currentTick));
                return targetMiasma;
            }

            long elapsedTicks = currentTick - state.lastUpdateTick();
            double current = state.currentMiasma();

            if (elapsedTicks > 0) {
                double effectiveTarget = (Math.abs(state.targetMiasma() - targetMiasma) > 1e-4) ? targetMiasma : state.targetMiasma();
                double alpha = (current > effectiveTarget)
                        ? config.toxicity.dissipation_speed_multiplier
                        : config.toxicity.saturation_speed_multiplier;

                double steps = elapsedTicks / (double) Math.max(1, config.toxicity.check_interval_ticks);
                double factor = 1.0 - Math.pow(1.0 - MathHelper.clamp(alpha, 0.01, 1.0), Math.max(1.0, steps));
                current = current + factor * (effectiveTarget - current);

                if (Math.abs(current - effectiveTarget) < 0.05) {
                    current = effectiveTarget;
                }
            }

            ACTIVE_ROOMS.put(key, new RoomGasState(current, targetMiasma, currentTick));
            return current;
        }

        public static RoomGasState getState(BlockPos anchor) {
            return ACTIVE_ROOMS.get(anchor.asLong());
        }

        public static void reset(BlockPos anchor) {
            if (anchor != null) {
                ACTIVE_ROOMS.remove(anchor.asLong());
            }
        }

        public static void cleanup(long currentTick) {
            ACTIVE_ROOMS.entrySet().removeIf(e -> (currentTick - e.getValue().lastUpdateTick()) > 1200);
        }

        public static void clear() {
            ACTIVE_ROOMS.clear();
        }
    }

    public static class MiasmaResult {
        public final double toxicScore;
        public final double ventilationScore;
        public final double targetMiasma;
        public final double netMiasma;
        public final RoomVentilationType ventilationType;
        public final boolean openAir;
        public final int volume;
        public final Set<BlockPos> airBlocks;
        public final BlockPos anchorPos;
        public final double density;
        public final double exposureIndex;
        public final AirToxicityLevel level;
        public final AirToxicityLevel roomLevel;
        public final int distanceToVentilation;
        public final double roomVentilationScore;
        public final int susceptibleBlockCount;
        public final double totalSusceptibleWeight;
        public final Map<BlockPos, Integer> distToGoal;
        public final Map<BlockPos, Double> nodeFlows;
        public final double localFlow;
        public final double localDensity;
        public final double localNetMiasma;
        public final double localAeration;
        public final double localExposureIndex;
        public final AirToxicityLevel localLevel;

        public MiasmaResult(WorldAccess world, double toxicScore, double ventilationScore,
                boolean openAir, int volume, Set<BlockPos> airBlocks, BlockPos defaultPos,
                int distanceToVentilation, double roomVentilationScore,
                int susceptibleBlockCount, double totalSusceptibleWeight,
                Map<BlockPos, Integer> distToGoal, Map<BlockPos, Double> nodeFlows) {
            this.openAir = openAir;
            this.volume = volume;
            this.airBlocks = airBlocks;
            this.anchorPos = RoomSaturationManager.calculateAnchor(airBlocks, defaultPos);
            this.distanceToVentilation = distanceToVentilation;
            this.roomVentilationScore = Math.max(0.0, roomVentilationScore);
            this.susceptibleBlockCount = susceptibleBlockCount;
            this.totalSusceptibleWeight = totalSusceptibleWeight;
            this.distToGoal = (distToGoal != null) ? distToGoal : Collections.emptyMap();
            this.nodeFlows = (nodeFlows != null) ? nodeFlows : Collections.emptyMap();

            ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();

            this.toxicScore = Math.max(0.0, toxicScore);
            this.ventilationScore = Math.max(0.0, ventilationScore);

            if (openAir) {
                this.ventilationType = RoomVentilationType.CLEAN_OPEN_AIR;
            } else if (volume >= config.toxicity.max_air_volume) {
                this.ventilationType = RoomVentilationType.UNCONFINED_CAVERN;
            } else if (this.ventilationScore > 0.0) {
                this.ventilationType = RoomVentilationType.VENTILATED;
            } else {
                this.ventilationType = RoomVentilationType.HERMETIC_SEALED;
            }

            this.targetMiasma = openAir ? 0.0 : Math.max(0.0, this.toxicScore - this.ventilationScore);
            this.netMiasma = (openAir || this.toxicScore == 0.0) ? 0.0
                    : RoomSaturationManager.getDynamicMiasma(world, this.anchorPos, this.targetMiasma);

            this.density = (volume > 0) ? (this.netMiasma / (double) volume) : 0.0;

            // Room-wide baseline level
            if (volume == 0 || this.netMiasma <= 0.0) {
                this.exposureIndex = 0.0;
                this.roomLevel = AirToxicityLevel.CLEAN;
            } else {
                double netFactor = Math.min(2.0, Math.sqrt(this.netMiasma / 6.0));
                this.exposureIndex = this.density * (0.5 + 0.5 * netFactor);

                if ((this.netMiasma >= config.toxicity.threshold_poison
                        && this.density >= config.toxicity.density_threshold_medium) ||
                        (this.density >= config.toxicity.density_threshold_high
                                && this.netMiasma >= config.toxicity.threshold_nausea)) {
                    this.roomLevel = AirToxicityLevel.LETHAL_POISON;
                } else if ((this.netMiasma >= config.toxicity.threshold_hunger
                        && this.density >= config.toxicity.density_threshold_low) ||
                        (this.density >= config.toxicity.density_threshold_medium
                                && this.netMiasma >= (config.toxicity.threshold_hunger / 2.0))) {
                    this.roomLevel = AirToxicityLevel.MODERATE_HUNGER;
                } else if ((this.netMiasma >= (config.toxicity.threshold_hunger / 3.0)
                        && this.density >= (config.toxicity.density_threshold_low / 2.0)) ||
                        this.density >= config.toxicity.density_threshold_low) {
                    this.roomLevel = AirToxicityLevel.WARNING;
                } else {
                    this.roomLevel = AirToxicityLevel.CLEAN;
                }
            }

            // Local micro-climate evaluation at defaultPos (eyePos / sensorPos)
            if (openAir || volume == 0) {
                this.localFlow = openAir ? config.toxicity.open_sky_ventilation_per_block : 0.0;
                this.localAeration = openAir ? 1.0 : 0.0;
                this.localDensity = 0.0;
                this.localNetMiasma = 0.0;
                this.localExposureIndex = 0.0;
                this.localLevel = AirToxicityLevel.CLEAN;
            } else if (this.netMiasma <= 0.0) {
                double weight = BFSExplorer.computeConductanceWeight(this.distanceToVentilation, config.toxicity.ventilation_distance_alpha);
                this.localFlow = this.roomVentilationScore * weight;
                double threshold = config.environment.ventilation_threshold_full_aeration;
                double baseAeration = (threshold > 0.0) ? Math.min(1.0, this.roomVentilationScore / threshold) : ((this.roomVentilationScore > 0.0) ? 1.0 : 0.0);
                this.localAeration = baseAeration * weight;
                this.localDensity = 0.0;
                this.localNetMiasma = 0.0;
                this.localExposureIndex = 0.0;
                this.localLevel = AirToxicityLevel.CLEAN;
            } else if (!config.toxicity.enable_distributed_miasma) {
                this.localFlow = this.roomVentilationScore;
                this.localAeration = (this.roomVentilationScore > 0.0) ? 1.0 : 0.0;
                this.localDensity = this.density;
                this.localNetMiasma = this.netMiasma;
                this.localExposureIndex = this.exposureIndex;
                this.localLevel = this.roomLevel;
            } else {
                double weight = BFSExplorer.computeConductanceWeight(this.distanceToVentilation, config.toxicity.ventilation_distance_alpha);
                this.localFlow = this.roomVentilationScore * weight;
                double threshold = config.environment.ventilation_threshold_full_aeration;
                double baseAeration = (threshold > 0.0) ? Math.min(1.0, this.roomVentilationScore / threshold) : 0.0;
                this.localAeration = baseAeration * weight;
                this.localDensity = this.density * (1.0 - this.localAeration);
                this.localNetMiasma = this.netMiasma * (1.0 - this.localAeration);

                if (this.localDensity <= 0.0 || this.localNetMiasma <= 0.0) {
                    this.localExposureIndex = 0.0;
                    this.localLevel = AirToxicityLevel.CLEAN;
                } else {
                    double localNetFactor = Math.min(2.0, Math.sqrt(this.localNetMiasma / 6.0));
                    this.localExposureIndex = this.localDensity * (0.5 + 0.5 * localNetFactor);

                    if ((this.localNetMiasma >= config.toxicity.threshold_poison
                            && this.localDensity >= config.toxicity.density_threshold_medium) ||
                            (this.localDensity >= config.toxicity.density_threshold_high
                                    && this.localNetMiasma >= config.toxicity.threshold_nausea)) {
                        this.localLevel = AirToxicityLevel.LETHAL_POISON;
                    } else if ((this.localNetMiasma >= config.toxicity.threshold_hunger
                            && this.localDensity >= config.toxicity.density_threshold_low) ||
                            (this.localDensity >= config.toxicity.density_threshold_medium
                                    && this.localNetMiasma >= (config.toxicity.threshold_hunger / 2.0))) {
                        this.localLevel = AirToxicityLevel.MODERATE_HUNGER;
                    } else if ((this.localNetMiasma >= (config.toxicity.threshold_hunger / 3.0)
                            && this.localDensity >= (config.toxicity.density_threshold_low / 2.0)) ||
                            this.localDensity >= config.toxicity.density_threshold_low) {
                        this.localLevel = AirToxicityLevel.WARNING;
                    } else {
                        this.localLevel = AirToxicityLevel.CLEAN;
                    }
                }
            }

            this.level = this.localLevel;
        }

        public MiasmaResult(WorldAccess world, double toxicScore, double ventilationScore,
                boolean openAir, int volume, Set<BlockPos> airBlocks, BlockPos defaultPos,
                int distanceToVentilation, double roomVentilationScore,
                int susceptibleBlockCount, double totalSusceptibleWeight,
                Map<BlockPos, Integer> distToGoal) {
            this(world, toxicScore, ventilationScore, openAir, volume, airBlocks, defaultPos,
                    distanceToVentilation, roomVentilationScore, susceptibleBlockCount, totalSusceptibleWeight, distToGoal, Collections.emptyMap());
        }

        public MiasmaResult(WorldAccess world, double toxicScore, double ventilationScore,
                boolean openAir, int volume, Set<BlockPos> airBlocks, BlockPos defaultPos,
                int distanceToVentilation, double roomVentilationScore,
                int susceptibleBlockCount, double totalSusceptibleWeight) {
            this(world, toxicScore, ventilationScore, openAir, volume, airBlocks, defaultPos,
                    distanceToVentilation, roomVentilationScore, susceptibleBlockCount, totalSusceptibleWeight, Collections.emptyMap(), Collections.emptyMap());
        }

        public MiasmaResult(WorldAccess world, double toxicScore, double ventilationScore,
                boolean openAir, int volume, Set<BlockPos> airBlocks, BlockPos defaultPos,
                int distanceToVentilation, double roomVentilationScore) {
            this(world, toxicScore, ventilationScore, openAir, volume, airBlocks, defaultPos,
                    distanceToVentilation, roomVentilationScore, 0, 1.0, Collections.emptyMap(), Collections.emptyMap());
        }

        public MiasmaResult(WorldAccess world, double toxicScore, double ventilationScore,
                boolean openAir, int volume, Set<BlockPos> airBlocks, BlockPos defaultPos) {
            this(world, toxicScore, ventilationScore, openAir, volume, airBlocks, defaultPos,
                    openAir ? 0 : 999, ventilationScore, 0, 1.0, Collections.emptyMap(), Collections.emptyMap());
        }

        public MiasmaResult(double toxicScore, double ventilationScore, boolean openAir, int volume,
                Set<BlockPos> airBlocks) {
            this(null, toxicScore, ventilationScore, openAir, volume, airBlocks, BlockPos.ORIGIN,
                    openAir ? 0 : 999, ventilationScore, 0, 1.0, Collections.emptyMap(), Collections.emptyMap());
        }
    }

    public record BlockAirEvaluation(
            double ventilationFlow,
            double averageAeration,
            double averageExposureIndex,
            double averageNetMiasma,
            int exposedFacesCount,
            int maxVolume,
            boolean anyOpenAir,
            int distanceToVentilation) {

        public BlockAirEvaluation(double ventilationFlow, double averageAeration, double averageExposureIndex,
                                  double averageNetMiasma, int exposedFacesCount, int maxVolume, boolean anyOpenAir) {
            this(ventilationFlow, averageAeration, averageExposureIndex, averageNetMiasma, exposedFacesCount, maxVolume, anyOpenAir, 999);
        }
    }

    public static MiasmaResult calculateMiasma(WorldAccess world, BlockPos eyePos) {
        ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        int maxAirVolume = config.toxicity.max_air_volume;
        int maxEuclideanRadius = config.toxicity.max_euclidean_radius;

        // Unified single-pass BFS room scan
        BFSExplorer.RoomScanResult scan = BFSExplorer.scanRoom(world, eyePos, maxAirVolume, maxEuclideanRadius, config);

        double toxicScore = scan.toxicScore();
        boolean openAir = scan.openAir();
        Set<BlockPos> airBlocks = scan.airBlocks();

        double ventilationScore = 0.0;
        int distanceToVentilation = openAir ? 0 : 999;
        double roomVentilationScore = 0.0;
        double totalSusceptibleWeight = 0.0;
        Map<BlockPos, Integer> distToGoalMap = Collections.emptyMap();
        Map<BlockPos, Double> nodeFlows = Collections.emptyMap();

        if (openAir) {
            ventilationScore = config.toxicity.open_sky_ventilation_per_block;
            roomVentilationScore = ventilationScore;
            distanceToVentilation = 0;
            totalSusceptibleWeight = 1.0;
        } else {
            FlowDistributor.MaxFlowResult flowResult = FlowDistributor.solve(
                    world, eyePos, maxAirVolume, maxEuclideanRadius);
            roomVentilationScore = flowResult.totalMaxFlow();
            int d = flowResult.startBlockDistances().getOrDefault(eyePos, 999);
            distanceToVentilation = d;
            ventilationScore = roomVentilationScore;
            distToGoalMap = flowResult.distToGoal();
            nodeFlows = flowResult.nodeFlows();

            if (roomVentilationScore > 0.0) {
                double alpha = config.toxicity.ventilation_distance_alpha;
                for (BlockPos sPos : scan.roomSusceptible()) {
                    int sMinDist = 999;
                    for (Direction sDir : DIRECTIONS) {
                        BlockPos airAdj = sPos.offset(sDir);
                        if (airBlocks.contains(airAdj)) {
                            int distAdj = flowResult.distToGoal().getOrDefault(
                                     airAdj, flowResult.startBlockDistances().getOrDefault(airAdj, 999));
                            if (distAdj < sMinDist) {
                                sMinDist = distAdj;
                            }
                        }
                    }
                    int sDist = (sMinDist < 900) ? sMinDist : 999;
                    double sWeight = (sDist < 900) ? (1.0 / (1.0 + alpha * sDist)) : 0.0;
                    totalSusceptibleWeight += sWeight;
                }
            }
        }

        return new MiasmaResult(world, toxicScore, ventilationScore, openAir, airBlocks.size(), airBlocks, eyePos,
                distanceToVentilation, roomVentilationScore, scan.roomSusceptible().size(), totalSusceptibleWeight, distToGoalMap, nodeFlows);
    }

    public static BlockAirEvaluation calculateBlockAirEvaluation(WorldAccess world,
            BlockPos blockPos, BlockState blockState) {
        ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();

        List<Direction> exposedDirs = new ArrayList<>();
        List<BlockPos> exposedAirPositions = new ArrayList<>();

        for (Direction dir : DIRECTIONS) {
            BlockPos neighborPos = blockPos.offset(dir);
            BlockState neighborState = world.getBlockState(neighborPos);

            BlockAerationType type = getAerationType(world, neighborPos, neighborState, dir.getOpposite());
            if (type == BlockAerationType.OPEN_AIR) {
                exposedDirs.add(dir);
                exposedAirPositions.add(neighborPos);
            }
        }

        int exposedFaces = exposedDirs.size();
        if (exposedFaces == 0) {
            return new BlockAirEvaluation(0.0, 0.0, 0.0, 0.0, 0, 0, false, 999);
        }

        double sumAeration = 0.0;
        double sumExposure = 0.0;
        double sumNetMiasma = 0.0;
        double sumVentilationFlow = 0.0;
        int maxVol = 0;
        boolean anyOpen = false;
        int minDistance = 999;

        List<MiasmaResult> computedResults = new ArrayList<>();

        for (BlockPos neighborPos : exposedAirPositions) {
            // A) Direct open sky
            if (isFaceOpenToSky(world, neighborPos, blockPos)) {
                anyOpen = true;
                double faceFlow = config.toxicity.open_sky_ventilation_per_block;
                double faceAeration = 1.0;
                sumAeration += faceAeration;
                sumVentilationFlow += faceFlow;
                minDistance = 0;
                maxVol = Math.max(maxVol, 1);
                continue;
            }

            // B) Indoor room air domain
            MiasmaResult faceResult = null;
            for (MiasmaResult r : computedResults) {
                if (r.airBlocks.contains(neighborPos)) {
                    faceResult = r;
                    break;
                }
            }
            if (faceResult == null) {
                faceResult = calculateMiasma(world, neighborPos);
                computedResults.add(faceResult);
            }

            double roomFlow = faceResult.roomVentilationScore;
            int blockDist = faceResult.distToGoal.getOrDefault(neighborPos, faceResult.distanceToVentilation);
            if (blockDist < minDistance) {
                minDistance = blockDist;
            }

            double faceFlow = 0.0;
            double faceAeration = 0.0;

            if (config.environment.enable_ventilation_drying) {
                if (faceResult.openAir) {
                    faceFlow = config.toxicity.open_sky_ventilation_per_block;
                    faceAeration = 1.0;
                } else if (roomFlow > 0.0 && blockDist < 900) {
                    double alpha = config.toxicity.ventilation_distance_alpha;
                    double weight = 1.0 / (1.0 + alpha * blockDist);
                    faceFlow = Math.min(roomFlow, roomFlow * weight);
                    if (config.environment.ventilation_threshold_full_aeration > 0.0) {
                        double maxPossibleRoomAeration = Math.min(1.0, roomFlow / config.environment.ventilation_threshold_full_aeration);
                        faceAeration = Math.min(1.0, maxPossibleRoomAeration * weight);
                    }
                }
            }

            double faceExposure = 0.0;
            if (faceResult.targetMiasma > 0.0 && faceResult.volume > 0) {
                double density = faceResult.targetMiasma / (double) faceResult.volume;
                double netFactor = Math.min(2.0, Math.sqrt(faceResult.targetMiasma / 6.0));
                faceExposure = density * (0.5 + 0.5 * netFactor);
            }

            sumAeration += faceAeration;
            sumExposure += faceExposure;
            sumNetMiasma += faceResult.targetMiasma;
            sumVentilationFlow += faceFlow;

            maxVol = Math.max(maxVol, faceResult.volume);
            if (faceResult.openAir) {
                anyOpen = true;
            }
        }

        double avgAeration = sumAeration / (double) exposedFaces;
        double avgExposure = sumExposure / (double) exposedFaces;
        double avgNetMiasma = sumNetMiasma / (double) exposedFaces;

        double finalFlow = sumVentilationFlow;
        double finalAeration = avgAeration;

        if (!anyOpen) {
            double totalAvailableRoomFlow = 0.0;
            for (MiasmaResult r : computedResults) {
                totalAvailableRoomFlow += r.roomVentilationScore;
            }
            finalFlow = Math.min(sumVentilationFlow, totalAvailableRoomFlow);
            if (config.environment.ventilation_threshold_full_aeration > 0.0) {
                double maxAllowedAeration = Math.min(1.0, totalAvailableRoomFlow / config.environment.ventilation_threshold_full_aeration);
                finalAeration = Math.min(avgAeration, maxAllowedAeration);
            }
        }

        return new BlockAirEvaluation(finalFlow, finalAeration, avgExposure, avgNetMiasma, exposedFaces, maxVol, anyOpen, minDistance);
    }

    public static MiasmaResult calculateBlockAirEnvironment(WorldAccess world, BlockPos blockPos,
            BlockState blockState) {
        BlockState startState = (blockState != null) ? blockState : world.getBlockState(blockPos);
        BlockAerationType startType = getAerationType(world, blockPos, startState, Direction.UP);
        if (startType != BlockAerationType.OPEN_AIR && isFaceSolid(world, blockPos, startState, Direction.UP)) {
            return new MiasmaResult(world, 0.0, 0.0, false, 0, Collections.emptySet(), blockPos);
        }
        return calculateMiasma(world, blockPos);
    }

    public static BlockPos getCanonicalVentilationPos(BlockPos pos, BlockState state) {
        if (state != null && state.getBlock() instanceof DoorBlock && state.contains(DoorBlock.HALF)) {
            if (state.get(DoorBlock.HALF) == DoubleBlockHalf.UPPER) {
                return pos.down();
            }
        }
        return pos;
    }

    public static boolean isFaceSolid(BlockView world, BlockPos pos, BlockState state, Direction face) {
        return BFSExplorer.isFaceSolid(world, pos, state, face);
    }

    public static BlockAerationType getAerationType(BlockView world, BlockPos pos, BlockState state, Direction entryFace) {
        return BFSExplorer.getAerationType(world, pos, state, entryFace);
    }

    public static boolean isCoveredByCeiling(WorldAccess world, BlockPos pos) {
        return BFSExplorer.isCoveredByCeiling(world, pos);
    }

    public static boolean isFaceOpenToSky(WorldAccess world, BlockPos neighborPos, BlockPos blockPos) {
        if (!isCoveredByCeiling(world, neighborPos)) {
            return true;
        }
        if (neighborPos.equals(blockPos.down()) && !isCoveredByCeiling(world, blockPos)) {
            return true;
        }
        return false;
    }

    public static boolean hasMoldNearby(BlockView world, BlockPos center, int radius) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        int cx = center.getX();
        int cy = center.getY();
        int cz = center.getZ();

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    mutable.set(cx + x, cy + y, cz + z);
                    BlockState state = world.getBlockState(mutable);
                    if (state.contains(MoldyBlock.STAGE) && state.get(MoldyBlock.STAGE) > 0) {
                        if (!state.contains(MoldyBlock.WAXED) || !state.get(MoldyBlock.WAXED)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
