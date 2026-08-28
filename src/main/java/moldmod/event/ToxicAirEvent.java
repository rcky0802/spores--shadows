package moldmod.event;

import moldmod.block.MoldyBlockHelper;
import moldmod.block.MoldyLogBlock;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.BlockState;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;

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

    public static class MiasmaResult {
        public final double toxicScore;
        public final double ventilationScore;
        public final double netMiasma;
        public final boolean openAir;
        public final int volume;
        public final Set<BlockPos> airBlocks;
        public final double density;
        public final double exposureIndex;
        public final AirToxicityLevel level;

        public MiasmaResult(double toxicScore, double ventilationScore, double netMiasma, boolean openAir, int volume, Set<BlockPos> airBlocks) {
            this.toxicScore = toxicScore;
            this.ventilationScore = ventilationScore;
            this.netMiasma = Math.max(0.0, netMiasma);
            this.openAir = openAir;
            this.volume = volume;
            this.airBlocks = airBlocks;
            this.density = (volume > 0) ? (this.netMiasma / (double) volume) : 0.0;

            moldmod.config.ModConfig config = me.shedaniel.autoconfig.AutoConfig.getConfigHolder(moldmod.config.ModConfig.class).getConfig();

            if (openAir || volume >= config.toxicity.max_air_volume || volume == 0 || this.netMiasma <= 0.0) {
                this.exposureIndex = 0.0;
                this.level = AirToxicityLevel.CLEAN;
            } else {
                // Modello Fisico-Biologico Realistico:
                // L'esposizione combina la concentrazione locale (densità) con il carico di emissione complessivo (netMiasma).
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

        queue.add(eyePos);
        visited.add(eyePos);

        double toxicScore = 0.0;
        double ventilationScore = 0.0;
        boolean openAir = false;

        while (!queue.isEmpty() && visited.size() < maxAirVolume) {
            BlockPos currentPos = queue.poll();
            BlockState currentState = world.getBlockState(currentPos);

            // Controllo O(1) cielo aperto tramite Heightmap
            int topY = world.getTopY(Heightmap.Type.MOTION_BLOCKING, currentPos.getX(), currentPos.getZ());
            if (currentPos.getY() >= topY) {
                openAir = true;
                break;
            }

            // Se il blocco corrente nello spazio d'aria è un blocco parziale (stair, slab, fence, etc.) con cielo sopra
            if (!currentState.isFullCube(world, currentPos) && !currentState.isAir()) {
                if (currentPos.getY() + 1 >= topY) {
                    ventilationScore += ventBonus;
                }
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
                    // Parete o perimetro perimetrale: processa il blocco di confine
                    boolean faceOpenToRoom = !neighborState.isSideSolidFullSquare(world, neighborPos, dir.getOpposite());
                    
                    // A. Calcolo Tossicità da Muffa (se la faccia è esposta alla stanza)
                    if (neighborState.contains(MoldyLogBlock.STAGE)) {
                        boolean isWaxed = neighborState.contains(MoldyLogBlock.WAXED) && neighborState.get(MoldyLogBlock.WAXED);
                        if (!isWaxed) {
                            int stage = neighborState.get(MoldyLogBlock.STAGE);
                            toxicScore += (stage * moldToxMult);
                        }
                    }

                    // B. Ventilazione / Fessure (es. Fence, Sbarre di ferro, Lastre, Scale aperte verso la stanza, porte/botole aperte)
                    if (faceOpenToRoom && !neighborState.isFullCube(world, neighborPos)) {
                        boolean isClosedDoorOrTrapdoor = neighborState.contains(net.minecraft.state.property.Properties.OPEN) && !neighborState.get(net.minecraft.state.property.Properties.OPEN);
                        if (!isClosedDoorOrTrapdoor) {
                            int neighborTopY = world.getTopY(Heightmap.Type.MOTION_BLOCKING, neighborPos.getX(), neighborPos.getZ());
                            if (neighborPos.getY() + 1 >= neighborTopY) {
                                ventilationScore += ventBonus;
                            }
                        }
                    }
                }
            }
        }

        double netMiasma = Math.max(0.0, toxicScore - ventilationScore);
        return new MiasmaResult(toxicScore, ventilationScore, netMiasma, openAir, visited.size(), visited);
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
            if (!neighborState.isSideSolidFullSquare(world, neighborPos, dir.getOpposite())) {
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
                    if (faceResult.openAir || faceResult.volume >= config.toxicity.max_air_volume) {
                        faceAeration = 1.0;
                    } else if (faceResult.volume > 0 && config.environment.ventilation_threshold_full_aeration > 0.0) {
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

        // Add initial adjacent positions where the neighbor face facing blockPos is open (not solid full square)
        for (Direction dir : DIRECTIONS) {
            BlockPos neighborPos = blockPos.offset(dir);
            BlockState neighborState = world.getBlockState(neighborPos);
            if (!neighborState.isSideSolidFullSquare(world, neighborPos, dir.getOpposite())) {
                if (visited.add(neighborPos)) {
                    queue.add(neighborPos);
                }
            }
        }

        if (queue.isEmpty()) {
            // Block is completely enclosed in solid blocks on all 6 sides
            return new MiasmaResult(0.0, 0.0, 0.0, false, 0, visited);
        }

        double toxicScore = 0.0;
        double ventilationScore = 0.0;
        boolean openAir = false;

        while (!queue.isEmpty() && visited.size() < maxAirVolume) {
            BlockPos currentPos = queue.poll();
            BlockState currentState = world.getBlockState(currentPos);

            // Controllo O(1) cielo aperto tramite Heightmap
            int topY = world.getTopY(Heightmap.Type.MOTION_BLOCKING, currentPos.getX(), currentPos.getZ());
            if (currentPos.getY() >= topY) {
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
                    boolean faceOpenToRoom = !neighborState.isSideSolidFullSquare(world, neighborPos, dir.getOpposite());

                    if (!neighborPos.equals(blockPos)) {
                        if (neighborState.contains(MoldyLogBlock.STAGE)) {
                            boolean isWaxed = neighborState.contains(MoldyLogBlock.WAXED) && neighborState.get(MoldyLogBlock.WAXED);
                            if (!isWaxed) {
                                int stage = neighborState.get(MoldyLogBlock.STAGE);
                                toxicScore += (stage * moldToxMult);
                            }
                        }
                    }

                    // B. Ventilazione / Fessure (es. Fence, Sbarre di ferro, Lastre, Scale aperte verso la stanza, porte/botole aperte)
                    if (faceOpenToRoom && !neighborState.isFullCube(world, neighborPos)) {
                        boolean isClosedDoorOrTrapdoor = neighborState.contains(net.minecraft.state.property.Properties.OPEN) && !neighborState.get(net.minecraft.state.property.Properties.OPEN);
                        if (!isClosedDoorOrTrapdoor) {
                            int neighborTopY = world.getTopY(Heightmap.Type.MOTION_BLOCKING, neighborPos.getX(), neighborPos.getZ());
                            if (neighborPos.getY() + 1 >= neighborTopY) {
                                ventilationScore += ventBonus;
                            }
                        }
                    }
                }
            }
        }

        double netMiasma = Math.max(0.0, toxicScore - ventilationScore);
        return new MiasmaResult(toxicScore, ventilationScore, netMiasma, openAir, visited.size(), visited);
    }

    public static boolean canAirPass(net.minecraft.world.BlockView world, BlockPos fromPos, BlockState fromState, BlockPos toPos, BlockState toState, Direction dir) {
        if (fromState != null) {
            if (fromState.isSideSolidFullSquare(world, fromPos, dir)) {
                return false;
            }
            if (fromState.contains(net.minecraft.state.property.Properties.OPEN) && !fromState.get(net.minecraft.state.property.Properties.OPEN)) {
                return false;
            }
        }
        if (toState == null || toState.isSideSolidFullSquare(world, toPos, dir.getOpposite())) {
            return false;
        }
        if (toState.contains(net.minecraft.state.property.Properties.OPEN)) {
            return toState.get(net.minecraft.state.property.Properties.OPEN);
        }
        return toState.isAir();
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
