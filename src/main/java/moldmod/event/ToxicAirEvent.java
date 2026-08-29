package moldmod.event;

import moldmod.block.MoldyBlockHelper;
import moldmod.block.MoldyLogBlock;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.GrateBlock;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.block.WallBlock;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

public class ToxicAirEvent {

    private static final Direction[] DIRECTIONS = Direction.values();

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            int currentTick = server.getTicks();
            moldmod.config.ModConfig config = me.shedaniel.autoconfig.AutoConfig.getConfigHolder(moldmod.config.ModConfig.class).getConfig();
            if (!config.toxicity.enable_toxic_air) return;

            int checkInterval = config.toxicity.check_interval_ticks;
            int radius = config.toxicity.scan_radius;

            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                // Load Balancing sui tick
                if (currentTick % checkInterval != player.getId() % checkInterval) continue;
                if (player.isSpectator() || player.isCreative()) continue;

                checkRoomMiasma(player, radius);
            }
        });
    }

    public enum AirToxicityLevel {
        CLEAN,
        WARNING,
        MODERATE_HUNGER,
        LETHAL_POISON
    }

    public enum RoomVentilationType {
        CLEAN_OPEN_AIR,
        VENTILATED,
        HERMETIC_SEALED
    }

    public enum BlockAerationType {
        OPEN_AIR,
        VENTILATED,
        HERMETIC
    }

    public static class MiasmaResult {
        public final double toxicScore;
        public final double ventilationScore;
        public final double netMiasma;
        public final RoomVentilationType ventilationType;
        public final boolean openAir;
        public final int volume;
        public final Set<BlockPos> airBlocks;
        public final double density;
        public final double exposureIndex;
        public final AirToxicityLevel level;

        public MiasmaResult(double toxicScore, double ventilationScore, boolean openAir, int volume, Set<BlockPos> airBlocks) {
            this.openAir = openAir;
            this.toxicScore = openAir ? 0.0 : Math.max(0.0, toxicScore);
            this.ventilationScore = openAir ? 0.0 : Math.max(0.0, ventilationScore);
            this.volume = volume;
            this.airBlocks = airBlocks;

            moldmod.config.ModConfig config = me.shedaniel.autoconfig.AutoConfig.getConfigHolder(moldmod.config.ModConfig.class).getConfig();

            if (openAir || volume >= config.toxicity.max_air_volume) {
                this.ventilationType = RoomVentilationType.CLEAN_OPEN_AIR;
                this.netMiasma = 0.0;
            } else if (this.ventilationScore > 0.0) {
                this.ventilationType = RoomVentilationType.VENTILATED;
                this.netMiasma = Math.max(0.0, this.toxicScore - this.ventilationScore);
            } else {
                this.ventilationType = RoomVentilationType.HERMETIC_SEALED;
                this.netMiasma = this.toxicScore;
            }

            this.density = (volume > 0 && this.ventilationType != RoomVentilationType.CLEAN_OPEN_AIR) ? (this.netMiasma / (double) volume) : 0.0;

            if (this.ventilationType == RoomVentilationType.CLEAN_OPEN_AIR || volume == 0 || this.netMiasma <= 0.0) {
                this.exposureIndex = 0.0;
                this.level = AirToxicityLevel.CLEAN;
            } else {
                // Modello Fisico-Biologico: concentrazione locale (densità) combinata con il carico di emissione
                double netFactor = Math.min(2.0, Math.sqrt(this.netMiasma / 8.0));
                this.exposureIndex = this.density * (0.5 + 0.5 * netFactor);

                if (this.netMiasma >= config.toxicity.threshold_poison || 
                    (this.density >= config.toxicity.density_threshold_high && this.netMiasma >= config.toxicity.threshold_nausea)) {
                    this.level = AirToxicityLevel.LETHAL_POISON;
                } else if (this.netMiasma >= config.toxicity.threshold_hunger || 
                    (this.density >= config.toxicity.density_threshold_medium && this.netMiasma >= (config.toxicity.threshold_hunger / 2.0))) {
                    this.level = AirToxicityLevel.MODERATE_HUNGER;
                } else if (this.netMiasma >= (config.toxicity.threshold_hunger / 3.0) || this.density >= config.toxicity.density_threshold_low) {
                    this.level = AirToxicityLevel.WARNING;
                } else {
                    this.level = AirToxicityLevel.CLEAN;
                }
            }
        }
    }

    private static void checkRoomMiasma(ServerPlayerEntity player, int radius) {
        ServerWorld world = (ServerWorld) player.getWorld();
        BlockPos eyePos = BlockPos.ofFloored(player.getEyePos());

        // Pre-filtro rapido: se non c'è muffa nelle vicinanze esci subito O(R^3)
        if (!hasMoldNearby(world, eyePos, radius)) {
            return;
        }

        moldmod.config.ModConfig config = me.shedaniel.autoconfig.AutoConfig.getConfigHolder(moldmod.config.ModConfig.class).getConfig();
        MiasmaResult result = calculateMiasma(world, eyePos);

        switch (result.level) {
            case LETHAL_POISON -> {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, config.toxicity.duration_poison_ticks, config.toxicity.poison_amplifier, false, false, true));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, config.toxicity.duration_nausea_ticks, config.toxicity.nausea_amplifier, false, false, true));
                MoldyBlockHelper.grantAdvancement(player, "toxic_air");
                spawnDenseParticles(world, player, result);
            }
            case MODERATE_HUNGER -> {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.HUNGER, config.toxicity.duration_hunger_ticks, config.toxicity.hunger_amplifier, false, false, true));
                spawnLightParticles(world, player, result);
            }
            case WARNING -> {
                spawnWarningParticles(world, player, result);
            }
            case CLEAN -> {}
        }
    }

    private static void spawnDenseParticles(ServerWorld world, ServerPlayerEntity player, MiasmaResult result) {
        int count = Math.min(result.volume, 40);
        java.util.List<BlockPos> airList = new java.util.ArrayList<>(result.airBlocks);
        java.util.Collections.shuffle(airList);
        for (int i = 0; i < count; i++) {
            BlockPos p = airList.get(i);
            world.spawnParticles(player, ParticleTypes.SPORE_BLOSSOM_AIR, false, 
                    p.getX() + 0.5 + (world.random.nextDouble() - 0.5), p.getY() + 0.5 + (world.random.nextDouble() - 0.5), p.getZ() + 0.5 + (world.random.nextDouble() - 0.5), 1, 0.0, 0.0, 0.0, 0.0);
            if (world.random.nextBoolean()) {
                world.spawnParticles(player, ParticleTypes.FALLING_SPORE_BLOSSOM, false, 
                    p.getX() + 0.5 + (world.random.nextDouble() - 0.5), p.getY() + 0.8, p.getZ() + 0.5 + (world.random.nextDouble() - 0.5), 1, 0.0, 0.0, 0.0, 0.0);
            }
        }
    }

    private static void spawnLightParticles(ServerWorld world, ServerPlayerEntity player, MiasmaResult result) {
        int count = Math.min(result.volume / 2, 20);
        java.util.List<BlockPos> airList = new java.util.ArrayList<>(result.airBlocks);
        java.util.Collections.shuffle(airList);
        for (int i = 0; i < count; i++) {
            BlockPos p = airList.get(i);
            world.spawnParticles(player, ParticleTypes.MYCELIUM, false, 
                    p.getX() + 0.5 + (world.random.nextDouble() - 0.5), p.getY() + 0.5 + (world.random.nextDouble() - 0.5), p.getZ() + 0.5 + (world.random.nextDouble() - 0.5), 1, 0.0, 0.0, 0.0, 0.0);
        }
    }

    private static void spawnWarningParticles(ServerWorld world, ServerPlayerEntity player, MiasmaResult result) {
        int count = Math.min(result.volume / 4, 10);
        if (count > 0) {
            java.util.List<BlockPos> airList = new java.util.ArrayList<>(result.airBlocks);
            java.util.Collections.shuffle(airList);
            for (int i = 0; i < count; i++) {
                BlockPos p = airList.get(i);
                world.spawnParticles(player, ParticleTypes.MYCELIUM, false, 
                        p.getX() + 0.5 + (world.random.nextDouble() - 0.5), p.getY() + 0.5 + (world.random.nextDouble() - 0.5), p.getZ() + 0.5 + (world.random.nextDouble() - 0.5), 1, 0.0, 0.0, 0.0, 0.0);
            }
        }
    }

    public static MiasmaResult calculateMiasma(net.minecraft.world.WorldAccess world, BlockPos eyePos) {
        moldmod.config.ModConfig config = me.shedaniel.autoconfig.AutoConfig.getConfigHolder(moldmod.config.ModConfig.class).getConfig();
        int maxAirVolume = config.toxicity.max_air_volume;
        int maxManhattanRadius = config.toxicity.max_manhattan_radius;
        float moldToxMult = config.toxicity.mold_toxicity_multiplier;
        float ventBonus = config.toxicity.ventilation_gap_bonus;

        Queue<BlockPos> queue = new ArrayDeque<>();
        Set<BlockPos> visited = new HashSet<>();
        Set<BlockPos> countedMold = new HashSet<>();
        Set<BlockPos> countedVentilation = new HashSet<>();

        queue.add(eyePos);
        visited.add(eyePos);

        double toxicScore = 0.0;
        double ventilationScore = 0.0;
        boolean openAir = false;

        while (!queue.isEmpty() && visited.size() < maxAirVolume) {
            BlockPos currentPos = queue.poll();
            BlockState currentState = world.getBlockState(currentPos);

            // Controllo cielo aperto: se il blocco d'aria corrente comunica direttamente con l'esterno
            if (!isCoveredByCeiling(world, currentPos)) {
                openAir = true;
                break;
            }

            // Scansione dei vicini nelle 6 direzioni
            for (Direction dir : DIRECTIONS) {
                BlockPos neighborPos = currentPos.offset(dir);
                BlockState neighborState = world.getBlockState(neighborPos);

                if (canAirPass(world, currentPos, currentState, neighborPos, neighborState, dir)) {
                    int distManhattan = Math.abs(eyePos.getX() - neighborPos.getX()) + 
                                        Math.abs(eyePos.getY() - neighborPos.getY()) + 
                                        Math.abs(eyePos.getZ() - neighborPos.getZ());
                    
                    if (distManhattan <= maxManhattanRadius) {
                        if (visited.add(neighborPos)) {
                            queue.add(neighborPos);
                        }
                    }
                } else {
                    // Parete o perimetro solido/ostruzione
                    // A. Calcolo tossicità da muffa sul confine
                    if (neighborState.contains(MoldyLogBlock.STAGE)) {
                        boolean isWaxed = neighborState.contains(MoldyLogBlock.WAXED) && neighborState.get(MoldyLogBlock.WAXED);
                        if (!isWaxed) {
                            if (countedMold.add(neighborPos)) {
                                int stage = neighborState.get(MoldyLogBlock.STAGE);
                                toxicScore += (stage * moldToxMult);
                            }
                        }
                    }

                    // B. Calcolo Modificatore di Ventilazione / Fessure
                    BlockAerationType toType = getAerationType(world, neighborPos, neighborState, dir.getOpposite());
                    if (toType == BlockAerationType.VENTILATED) {
                        if (isVentilatedToOutside(world, neighborPos, dir)) {
                            BlockPos canonicalPos = getCanonicalVentilationPos(neighborPos, neighborState);
                            if (countedVentilation.add(canonicalPos)) {
                                ventilationScore += ventBonus;
                            }
                        }
                    }
                }
            }
        }

        return new MiasmaResult(toxicScore, ventilationScore, openAir, visited.size(), visited);
    }

    public record BlockAirEvaluation(
        double averageAeration,
        double averageExposureIndex,
        double averageNetMiasma,
        int exposedFacesCount,
        int maxVolume,
        boolean anyOpenAir
    ) {}

    public static BlockAirEvaluation calculateBlockAirEvaluation(net.minecraft.world.WorldAccess world, BlockPos blockPos, BlockState blockState) {
        moldmod.config.ModConfig config = me.shedaniel.autoconfig.AutoConfig.getConfigHolder(moldmod.config.ModConfig.class).getConfig();
        
        int exposedFaces = 0;
        double sumAeration = 0.0;
        double sumExposure = 0.0;
        double sumNetMiasma = 0.0;
        int maxVol = 0;
        boolean anyOpen = false;

        java.util.List<MiasmaResult> computedResults = new java.util.ArrayList<>();

        for (Direction dir : DIRECTIONS) {
            BlockPos neighborPos = blockPos.offset(dir);
            BlockState neighborState = world.getBlockState(neighborPos);

            // Is this face exposed to an open air/permeable medium?
            BlockAerationType type = getAerationType(world, neighborPos, neighborState, dir.getOpposite());
            if (type == BlockAerationType.OPEN_AIR) {
                exposedFaces++;

                // Check if neighborPos is already in a previously computed air space
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

                double faceAeration = 0.0;
                if (config.environment.enable_ventilation_drying) {
                    if (faceResult.ventilationType == RoomVentilationType.CLEAN_OPEN_AIR) {
                        faceAeration = 1.0;
                    } else if (faceResult.ventilationType == RoomVentilationType.VENTILATED && config.environment.ventilation_threshold_full_aeration > 0.0) {
                        faceAeration = Math.min(1.0, faceResult.ventilationScore / config.environment.ventilation_threshold_full_aeration);
                    }
                }

                sumAeration += faceAeration;
                sumExposure += faceResult.exposureIndex;
                sumNetMiasma += faceResult.netMiasma;
                maxVol = Math.max(maxVol, faceResult.volume);
                if (faceResult.openAir) {
                    anyOpen = true;
                }
            }
        }

        if (exposedFaces == 0) {
            return new BlockAirEvaluation(0.0, 0.0, 0.0, 0, 0, false);
        }

        double avgAeration = sumAeration / (double) exposedFaces;
        double avgExposure = sumExposure / (double) exposedFaces;
        double avgNetMiasma = sumNetMiasma / (double) exposedFaces;

        return new BlockAirEvaluation(avgAeration, avgExposure, avgNetMiasma, exposedFaces, maxVol, anyOpen);
    }

    public static MiasmaResult calculateBlockAirEnvironment(net.minecraft.world.WorldAccess world, BlockPos blockPos, BlockState blockState) {
        moldmod.config.ModConfig config = me.shedaniel.autoconfig.AutoConfig.getConfigHolder(moldmod.config.ModConfig.class).getConfig();
        int maxAirVolume = config.toxicity.max_air_volume;
        int maxManhattanRadius = config.toxicity.max_manhattan_radius;
        float moldToxMult = config.toxicity.mold_toxicity_multiplier;
        float ventBonus = config.toxicity.ventilation_gap_bonus;

        Queue<BlockPos> queue = new ArrayDeque<>();
        Set<BlockPos> visited = new HashSet<>();
        Set<BlockPos> countedMold = new HashSet<>();
        Set<BlockPos> countedVentilation = new HashSet<>();

        BlockState startState = world.getBlockState(blockPos);
        BlockAerationType startType = getAerationType(world, blockPos, startState, Direction.UP);
        if (startType != BlockAerationType.OPEN_AIR && isFaceSolid(world, blockPos, startState, Direction.UP)) {
            return new MiasmaResult(0.0, 0.0, false, 0, visited);
        }

        visited.add(blockPos);
        queue.add(blockPos);

        double toxicScore = 0.0;
        double ventilationScore = 0.0;
        boolean openAir = false;

        while (!queue.isEmpty() && visited.size() < maxAirVolume) {
            BlockPos currentPos = queue.poll();
            BlockState currentState = world.getBlockState(currentPos);

            // Controllo cielo aperto: se il blocco d'aria corrente comunica direttamente con l'esterno
            if (!isCoveredByCeiling(world, currentPos)) {
                openAir = true;
                break;
            }

            // Scansione dei vicini nelle 6 direzioni
            for (Direction dir : DIRECTIONS) {
                BlockPos neighborPos = currentPos.offset(dir);
                BlockState neighborState = world.getBlockState(neighborPos);

                if (canAirPass(world, currentPos, currentState, neighborPos, neighborState, dir)) {
                    int distManhattan = Math.abs(blockPos.getX() - neighborPos.getX()) + 
                                        Math.abs(blockPos.getY() - neighborPos.getY()) + 
                                        Math.abs(blockPos.getZ() - neighborPos.getZ());
                    
                    if (distManhattan <= maxManhattanRadius) {
                        if (visited.add(neighborPos)) {
                            queue.add(neighborPos);
                        }
                    }
                } else {
                    // Parete o perimetro solido: processa il blocco di confine
                    if (neighborState.contains(MoldyLogBlock.STAGE)) {
                        boolean isWaxed = neighborState.contains(MoldyLogBlock.WAXED) && neighborState.get(MoldyLogBlock.WAXED);
                        if (!isWaxed) {
                            if (countedMold.add(neighborPos)) {
                                int stage = neighborState.get(MoldyLogBlock.STAGE);
                                toxicScore += (stage * moldToxMult);
                            }
                        }
                    }

                    // Modificatore di ventilazione
                    BlockAerationType toType = getAerationType(world, neighborPos, neighborState, dir.getOpposite());
                    if (toType == BlockAerationType.VENTILATED) {
                        if (isVentilatedToOutside(world, neighborPos, dir)) {
                            BlockPos canonicalPos = getCanonicalVentilationPos(neighborPos, neighborState);
                            if (countedVentilation.add(canonicalPos)) {
                                ventilationScore += ventBonus;
                            }
                        }
                    }
                }
            }
        }

        return new MiasmaResult(toxicScore, ventilationScore, openAir, visited.size(), visited);
    }

    public static BlockPos getCanonicalVentilationPos(BlockPos pos, BlockState state) {
        if (state != null && state.getBlock() instanceof DoorBlock && state.contains(DoorBlock.HALF)) {
            if (state.get(DoorBlock.HALF) == net.minecraft.block.enums.DoubleBlockHalf.UPPER) {
                return pos.down();
            }
        }
        return pos;
    }

    public static boolean isFaceSolid(net.minecraft.world.BlockView world, BlockPos pos, BlockState state, Direction face) {
        if (state == null || state.isAir()) return false;

        net.minecraft.block.Block block = state.getBlock();

        // Copper grates are completely open like air
        if (block instanceof GrateBlock) {
            return false;
        }

        // Fences and Fence Gates are not solid faces (allow airflow / ventilation)
        if (block instanceof FenceBlock || block instanceof FenceGateBlock) {
            return false;
        }

        // Wall blocks (Muretti): open vertically; on horizontal walls solid ONLY if connected to adjacent walls/blocks
        if (block instanceof WallBlock) {
            return face.getAxis().isHorizontal() && isWallConnected(state);
        }

        // Doors, Trapdoors: open = permeable, closed = hermetic along blocking axis
        if (block instanceof DoorBlock || block instanceof TrapdoorBlock) {
            return isBlockAirflowBlocked(state, face);
        }

        if (state.isOf(Blocks.IRON_BARS)) {
            return false;
        }

        return state.isSideSolidFullSquare(world, pos, face);
    }

    public static boolean isWallConnected(BlockState state) {
        if (!(state.getBlock() instanceof WallBlock)) return false;
        boolean ns = (state.contains(WallBlock.NORTH_SHAPE) && state.get(WallBlock.NORTH_SHAPE) != net.minecraft.block.enums.WallShape.NONE) &&
                     (state.contains(WallBlock.SOUTH_SHAPE) && state.get(WallBlock.SOUTH_SHAPE) != net.minecraft.block.enums.WallShape.NONE);
        boolean ew = (state.contains(WallBlock.EAST_SHAPE) && state.get(WallBlock.EAST_SHAPE) != net.minecraft.block.enums.WallShape.NONE) &&
                     (state.contains(WallBlock.WEST_SHAPE) && state.get(WallBlock.WEST_SHAPE) != net.minecraft.block.enums.WallShape.NONE);
        return ns || ew;
    }

    public static BlockAerationType getAerationType(net.minecraft.world.BlockView world, BlockPos pos, BlockState state, Direction entryFace) {
        if (state == null || state.isAir()) return BlockAerationType.OPEN_AIR;

        net.minecraft.block.Block block = state.getBlock();

        // 1. Copper Grates: treated like air (total aeration)
        if (block instanceof GrateBlock) {
            return BlockAerationType.OPEN_AIR;
        }

        // 2. Fences (Staccionate) & Fence Gates (Cancelletti):
        // Fences: sempre VENTILATED.
        // Fence Gates: aperto = OPEN_AIR (passaggio diretto), chiuso = VENTILATED (agisce come staccionata)
        if (block instanceof FenceBlock) {
            return BlockAerationType.VENTILATED;
        }
        if (block instanceof FenceGateBlock) {
            boolean isOpen = state.contains(Properties.OPEN) && state.get(Properties.OPEN);
            return isOpen ? BlockAerationType.OPEN_AIR : BlockAerationType.VENTILATED;
        }

        // 3. Wall blocks (Muretti): VENTILATED on floor/ceiling (vertical) and when unconnected; HERMETIC when connected horizontally on walls
        if (block instanceof WallBlock) {
            if (entryFace.getAxis().isVertical() || !isWallConnected(state)) {
                return BlockAerationType.VENTILATED;
            }
            return BlockAerationType.HERMETIC;
        }

        // 4. Doors, Trapdoors: open = OPEN_AIR, closed = HERMETIC along blocking axis
        if (block instanceof DoorBlock || block instanceof TrapdoorBlock) {
            Direction flowDir = entryFace.getOpposite();
            boolean isBlockingFlow = isBlockAirflowBlocked(state, flowDir);
            return isBlockingFlow ? BlockAerationType.HERMETIC : BlockAerationType.OPEN_AIR;
        }

        // 5. Grates / Panes (Iron Bars)
        if (block instanceof net.minecraft.block.PaneBlock) {
            if (state.isOf(Blocks.IRON_BARS)) return BlockAerationType.VENTILATED;
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

        if (block instanceof net.minecraft.block.SlabBlock || block instanceof net.minecraft.block.StairsBlock) {
            return BlockAerationType.VENTILATED;
        }

        return BlockAerationType.OPEN_AIR;
    }

    public static boolean isBlockAirflowBlocked(BlockState state, Direction flowDir) {
        if (state == null) return false;
        if (state.getBlock() instanceof TrapdoorBlock) {
            boolean isOpen = state.contains(Properties.OPEN) && state.get(Properties.OPEN);
            if (!isOpen) {
                // Piastra orizzontale: chiude passaggi verticali (soffitto/pavimento)
                return flowDir.getAxis().isVertical();
            } else {
                // Piastra verticale: chiude finestre/botole su parete laterale
                Direction facing = state.contains(Properties.HORIZONTAL_FACING) ? state.get(Properties.HORIZONTAL_FACING) : Direction.NORTH;
                return flowDir.getAxis() == facing.getAxis();
            }
        }
        if (state.getBlock() instanceof DoorBlock) {
            boolean isOpen = state.contains(Properties.OPEN) && state.get(Properties.OPEN);
            return !isOpen;
        }
        return false;
    }

    public static boolean canAirPass(net.minecraft.world.BlockView world, BlockPos fromPos, BlockState fromState, BlockPos toPos, BlockState toState, Direction dir) {
        if (fromState == null || toState == null) return false;
        if (!toState.getFluidState().isEmpty()) return false;

        if (isFaceSolid(world, fromPos, fromState, dir)) {
            return false;
        }
        if (isFaceSolid(world, toPos, toState, dir.getOpposite())) {
            return false;
        }

        BlockAerationType fromType = getAerationType(world, fromPos, fromState, dir.getOpposite());
        if (fromType != BlockAerationType.OPEN_AIR) return false;

        BlockAerationType toType = getAerationType(world, toPos, toState, dir.getOpposite());
        return toType == BlockAerationType.OPEN_AIR;
    }

    public static boolean isCoveredByCeiling(net.minecraft.world.WorldAccess world, BlockPos pos) {
        for (int dy = 0; dy <= 24; dy++) {
            BlockPos upPos = pos.up(dy);
            BlockState upState = world.getBlockState(upPos);
            if (upState.isOf(Blocks.BARRIER) || upState.isOf(Blocks.STRUCTURE_BLOCK) || upState.isOf(Blocks.STRUCTURE_VOID)) {
                continue;
            }
            if (isCeilingBarrier(world, upPos, upState)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isCeilingBarrier(net.minecraft.world.WorldAccess world, BlockPos pos, BlockState state) {
        if (state == null || state.isAir()) return false;
        if (state.getBlock() instanceof net.minecraft.block.GrateBlock) return false;
        BlockAerationType type = getAerationType(world, pos, state, Direction.DOWN);
        return type == BlockAerationType.HERMETIC || type == BlockAerationType.VENTILATED;
    }

    public static boolean isVentilatedToOutside(net.minecraft.world.WorldAccess world, BlockPos gapPos, Direction outwardDir) {
        Direction entryFace = outwardDir.getOpposite();
        BlockState gapState = world.getBlockState(gapPos);

        if (isFaceSolid(world, gapPos, gapState, entryFace)) {
            return false;
        }

        // 1. Se outwardDir è verso l'alto (soffitto, es. staccionata/grata sul soffitto)
        if (outwardDir == Direction.UP) {
            if (!isFaceSolid(world, gapPos, gapState, Direction.UP)) {
                return !isCoveredByCeiling(world, gapPos.up());
            }
            return false;
        }

        // 2. Se outwardDir è verso il basso (pavimento, es. staccionata/grata sul pavimento)
        if (outwardDir == Direction.DOWN) {
            if (isFaceSolid(world, gapPos, gapState, Direction.DOWN)) {
                return false;
            }
            for (int step = 1; step <= 3; step++) {
                BlockPos checkPos = gapPos.down(step);
                BlockState checkState = world.getBlockState(checkPos);
                if (checkState.isOf(Blocks.BARRIER) || checkState.isOf(Blocks.STRUCTURE_BLOCK)) {
                    break;
                }
                if (isFaceSolid(world, checkPos, checkState, Direction.UP)) {
                    break;
                }
                if (!isCoveredByCeiling(world, checkPos)) {
                    return true;
                }
                for (Direction side : Direction.Type.HORIZONTAL) {
                    for (int s = 1; s <= 3; s++) {
                        BlockPos sidePos = checkPos.offset(side, s);
                        if (isFaceSolid(world, sidePos, world.getBlockState(sidePos), side.getOpposite())) {
                            break;
                        }
                        if (!isCoveredByCeiling(world, sidePos)) {
                            return true;
                        }
                        if (isFaceSolid(world, sidePos, world.getBlockState(sidePos), side)) {
                            break;
                        }
                    }
                }
                if (isFaceSolid(world, checkPos, checkState, Direction.DOWN)) {
                    break;
                }
            }
            return false;
        }

        // 3. Uscita orizzontale in direzione outwardDir (parete laterale)
        // Se la faccia del blocco rivolta verso l'uscita è solida (es. retro scala), blocca l'uscita
        if (isFaceSolid(world, gapPos, gapState, outwardDir)) {
            return false;
        }

        for (int step = 1; step <= 3; step++) {
            BlockPos checkPos = gapPos.offset(outwardDir, step);
            BlockState checkState = world.getBlockState(checkPos);

            if (checkState.isOf(Blocks.BARRIER) || checkState.isOf(Blocks.STRUCTURE_BLOCK)) {
                break;
            }
            if (isFaceSolid(world, checkPos, checkState, outwardDir.getOpposite())) {
                break;
            }
            if (!isCoveredByCeiling(world, checkPos)) {
                return true;
            }
            if (isFaceSolid(world, checkPos, checkState, outwardDir)) {
                break;
            }
        }
        return false;
    }

    public static boolean hasMoldNearby(net.minecraft.world.BlockView world, BlockPos center, int radius) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        int cx = center.getX();
        int cy = center.getY();
        int cz = center.getZ();

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    mutable.set(cx + x, cy + y, cz + z);
                    BlockState state = world.getBlockState(mutable);
                    if (state.contains(MoldyLogBlock.STAGE) && state.get(MoldyLogBlock.STAGE) > 0) {
                        if (!state.contains(MoldyLogBlock.WAXED) || !state.get(MoldyLogBlock.WAXED)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
