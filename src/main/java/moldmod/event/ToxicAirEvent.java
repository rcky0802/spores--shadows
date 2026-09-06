package moldmod.event;

import me.shedaniel.autoconfig.AutoConfig;
import moldmod.block.MoldyBlock;
import moldmod.block.MoldyBlockHelper;
import moldmod.config.ModConfig;
import moldmod.item.ModItems;
import moldmod.registry.ModEnchantments;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
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
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Toxic Air Event, Miasma Lifecycle Manager, and Player Toxicity Listener.
 * Delegates 3D geometry and BFS scans to BFSExplorer, FastDinicSolver, and FlowDistributor.
 */
public class ToxicAirEvent {

    private static final Direction[] DIRECTIONS = Direction.values();

    public record PlayerAirCache(BlockPos eyePos, long lastTick, MiasmaResult result) {
    }

    private static final Map<UUID, PlayerAirCache> PLAYER_AIR_CACHE = new ConcurrentHashMap<>();

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            int currentTick = server.getTicks();
            ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
            if (!config.toxicity.enable_toxic_air) {
                return;
            }

            int checkInterval = config.toxicity.check_interval_ticks;
            int radius = Math.max(config.toxicity.scan_radius, config.toxicity.max_euclidean_radius);

            if (currentTick % 1200 == 0) {
                RoomSaturationManager.cleanup(currentTick);
                PLAYER_AIR_CACHE.entrySet().removeIf(e -> (currentTick - e.getValue().lastTick()) > 1200);
            }

            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                if (currentTick % checkInterval != player.getId() % checkInterval) {
                    continue;
                }
                if (player.isSpectator() || player.isCreative()) {
                    continue;
                }

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
            if (defaultPos != null) {
                return defaultPos;
            }
            if (airBlocks == null || airBlocks.isEmpty()) {
                return BlockPos.ORIGIN;
            }
            return airBlocks.iterator().next();
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
            if (elapsedTicks <= 0) {
                return state.currentMiasma();
            }

            double current = state.currentMiasma();
            double alpha = (current > targetMiasma)
                    ? config.toxicity.dissipation_speed_multiplier
                    : config.toxicity.saturation_speed_multiplier;

            double steps = elapsedTicks / (double) Math.max(1, config.toxicity.check_interval_ticks);
            double factor = 1.0 - Math.pow(1.0 - MathHelper.clamp(alpha, 0.01, 1.0), Math.max(1.0, steps));
            double updated = current + factor * (targetMiasma - current);

            if (Math.abs(updated - targetMiasma) < 0.05) {
                updated = targetMiasma;
            }

            ACTIVE_ROOMS.put(key, new RoomGasState(updated, targetMiasma, currentTick));
            return updated;
        }

        public static RoomGasState getState(BlockPos anchor) {
            return ACTIVE_ROOMS.get(anchor.asLong());
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
        public final int distanceToVentilation;
        public final double roomVentilationScore;
        public final int susceptibleBlockCount;
        public final double totalSusceptibleWeight;
        public final Map<BlockPos, Integer> distToGoal;

        public MiasmaResult(WorldAccess world, double toxicScore, double ventilationScore,
                boolean openAir, int volume, Set<BlockPos> airBlocks, BlockPos defaultPos,
                int distanceToVentilation, double roomVentilationScore,
                int susceptibleBlockCount, double totalSusceptibleWeight,
                Map<BlockPos, Integer> distToGoal) {
            this.openAir = openAir;
            this.volume = volume;
            this.airBlocks = airBlocks;
            this.anchorPos = RoomSaturationManager.calculateAnchor(airBlocks, defaultPos);
            this.distanceToVentilation = distanceToVentilation;
            this.roomVentilationScore = Math.max(0.0, roomVentilationScore);
            this.susceptibleBlockCount = susceptibleBlockCount;
            this.totalSusceptibleWeight = totalSusceptibleWeight;
            this.distToGoal = (distToGoal != null) ? distToGoal : Collections.emptyMap();

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

            if (volume == 0 || this.netMiasma <= 0.0) {
                this.exposureIndex = 0.0;
                this.level = AirToxicityLevel.CLEAN;
            } else {
                double netFactor = Math.min(2.0, Math.sqrt(this.netMiasma / 8.0));
                this.exposureIndex = this.density * (0.5 + 0.5 * netFactor);

                if ((this.netMiasma >= config.toxicity.threshold_poison
                        && this.density >= config.toxicity.density_threshold_medium) ||
                        (this.density >= config.toxicity.density_threshold_high
                                && this.netMiasma >= config.toxicity.threshold_nausea)) {
                    this.level = AirToxicityLevel.LETHAL_POISON;
                } else if ((this.netMiasma >= config.toxicity.threshold_hunger
                        && this.density >= config.toxicity.density_threshold_low) ||
                        (this.density >= config.toxicity.density_threshold_medium
                                && this.netMiasma >= (config.toxicity.threshold_hunger / 2.0))) {
                    this.level = AirToxicityLevel.MODERATE_HUNGER;
                } else if ((this.netMiasma >= (config.toxicity.threshold_hunger / 3.0)
                        && this.density >= (config.toxicity.density_threshold_low / 2.0)) ||
                        this.density >= config.toxicity.density_threshold_low) {
                    this.level = AirToxicityLevel.WARNING;
                } else {
                    this.level = AirToxicityLevel.CLEAN;
                }
            }
        }

        public MiasmaResult(WorldAccess world, double toxicScore, double ventilationScore,
                boolean openAir, int volume, Set<BlockPos> airBlocks, BlockPos defaultPos,
                int distanceToVentilation, double roomVentilationScore,
                int susceptibleBlockCount, double totalSusceptibleWeight) {
            this(world, toxicScore, ventilationScore, openAir, volume, airBlocks, defaultPos,
                    distanceToVentilation, roomVentilationScore, susceptibleBlockCount, totalSusceptibleWeight, Collections.emptyMap());
        }

        public MiasmaResult(WorldAccess world, double toxicScore, double ventilationScore,
                boolean openAir, int volume, Set<BlockPos> airBlocks, BlockPos defaultPos,
                int distanceToVentilation, double roomVentilationScore) {
            this(world, toxicScore, ventilationScore, openAir, volume, airBlocks, defaultPos,
                    distanceToVentilation, roomVentilationScore, 0, 1.0, Collections.emptyMap());
        }

        public MiasmaResult(WorldAccess world, double toxicScore, double ventilationScore,
                boolean openAir, int volume, Set<BlockPos> airBlocks, BlockPos defaultPos) {
            this(world, toxicScore, ventilationScore, openAir, volume, airBlocks, defaultPos,
                    openAir ? 0 : 999, ventilationScore, 0, 1.0, Collections.emptyMap());
        }

        public MiasmaResult(double toxicScore, double ventilationScore, boolean openAir, int volume,
                Set<BlockPos> airBlocks) {
            this(null, toxicScore, ventilationScore, openAir, volume, airBlocks, BlockPos.ORIGIN,
                    openAir ? 0 : 999, ventilationScore, 0, 1.0, Collections.emptyMap());
        }
    }

    public static void checkRoomMiasma(PlayerEntity player, int radius) {
        if (!(player.getWorld() instanceof ServerWorld world)) {
            return;
        }
        BlockPos eyePos = BlockPos.ofFloored(player.getEyePos());

        if (!hasMoldNearby(world, eyePos, radius)) {
            PLAYER_AIR_CACHE.remove(player.getUuid());
            return;
        }

        ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        long currentTick = world.getServer() != null ? world.getServer().getTicks() : world.getTime();

        PlayerAirCache cached = PLAYER_AIR_CACHE.get(player.getUuid());
        MiasmaResult result;

        if (cached != null && cached.eyePos().equals(eyePos) && (currentTick - cached.lastTick() < 60)) {
            MiasmaResult prev = cached.result();
            if (config.toxicity.enable_dynamic_spore_saturation && prev.targetMiasma > 0.0) {
                double updatedNet = RoomSaturationManager.getDynamicMiasma(world, prev.anchorPos, prev.targetMiasma);
                if (Math.abs(updatedNet - prev.netMiasma) > 0.05) {
                    result = new MiasmaResult(world, prev.toxicScore, prev.ventilationScore, prev.openAir,
                            prev.volume, prev.airBlocks, prev.anchorPos, prev.distanceToVentilation,
                            prev.roomVentilationScore, prev.susceptibleBlockCount, prev.totalSusceptibleWeight, prev.distToGoal);
                } else {
                    result = prev;
                }
            } else {
                result = prev;
            }
            PLAYER_AIR_CACHE.put(player.getUuid(), new PlayerAirCache(eyePos, currentTick, result));
        } else {
            result = calculateMiasma(world, eyePos);
            PLAYER_AIR_CACHE.put(player.getUuid(), new PlayerAirCache(eyePos, currentTick, result));
        }

        ItemStack headStack = player.getEquippedStack(EquipmentSlot.HEAD);
        boolean hasSporeMask = config.toxicity.enable_spore_mask_protection && headStack.isOf(ModItems.SPORE_MASK);

        int filtrationLevel = 0;
        if (config.toxicity.enable_spore_filtration_enchantment && !headStack.isEmpty()) {
            var regOpt = world.getRegistryManager().getOptional(RegistryKeys.ENCHANTMENT);
            if (regOpt.isPresent()) {
                var entryOpt = regOpt.get().getEntry(ModEnchantments.SPORE_FILTRATION);
                if (entryOpt.isPresent()) {
                    filtrationLevel = EnchantmentHelper.getLevel(entryOpt.get(), headStack);
                }
            }
        }

        boolean isProtected = hasSporeMask || (filtrationLevel > 0);
        int durabilityDamage = 0;
        if (hasSporeMask) {
            durabilityDamage = config.toxicity.spore_mask_damage_per_exposure;
        } else if (filtrationLevel > 0) {
            if (filtrationLevel == 1) {
                durabilityDamage = config.toxicity.filtration_level_1_durability_cost;
            } else if (filtrationLevel == 2) {
                durabilityDamage = config.toxicity.filtration_level_2_durability_cost;
            } else {
                durabilityDamage = (world.random.nextFloat() < config.toxicity.filtration_level_3_save_chance) ? 0 : 1;
            }
        }

        switch (result.level) {
            case LETHAL_POISON -> {
                if (isProtected) {
                    if (durabilityDamage > 0) {
                        headStack.damage(durabilityDamage, player, EquipmentSlot.HEAD);
                    }
                    if (player instanceof ServerPlayerEntity serverPlayer) {
                        world.spawnParticles(serverPlayer, ParticleTypes.CLOUD, false,
                                player.getX(), player.getEyeY() - 0.1, player.getZ(), 2, 0.1, 0.1, 0.1, 0.01);
                        MoldyBlockHelper.grantAdvancement(serverPlayer, "spore_mask_protection");
                        spawnLightParticles(world, serverPlayer, result);
                    }
                } else {
                    player.addStatusEffect(
                            new StatusEffectInstance(StatusEffects.POISON, config.toxicity.duration_poison_ticks,
                                    config.toxicity.poison_amplifier, false, false, true));
                    player.addStatusEffect(
                            new StatusEffectInstance(StatusEffects.NAUSEA, config.toxicity.duration_nausea_ticks,
                                    config.toxicity.nausea_amplifier, false, false, true));
                    if (player instanceof ServerPlayerEntity serverPlayer) {
                        MoldyBlockHelper.grantAdvancement(serverPlayer, "toxic_air");
                        spawnDenseParticles(world, serverPlayer, result);
                    }
                }
            }
            case MODERATE_HUNGER -> {
                if (isProtected) {
                    if (durabilityDamage > 0) {
                        headStack.damage(durabilityDamage, player, EquipmentSlot.HEAD);
                    }
                    if (player instanceof ServerPlayerEntity serverPlayer) {
                        world.spawnParticles(serverPlayer, ParticleTypes.CLOUD, false,
                                player.getX(), player.getEyeY() - 0.1, player.getZ(), 1, 0.1, 0.1, 0.1, 0.01);
                        MoldyBlockHelper.grantAdvancement(serverPlayer, "spore_mask_protection");
                    }
                } else {
                    player.addStatusEffect(
                            new StatusEffectInstance(StatusEffects.HUNGER, config.toxicity.duration_hunger_ticks,
                                    config.toxicity.hunger_amplifier, false, false, true));
                    if (player instanceof ServerPlayerEntity serverPlayer) {
                        spawnLightParticles(world, serverPlayer, result);
                    }
                }
            }
            case WARNING -> {
                if (player instanceof ServerPlayerEntity serverPlayer) {
                    spawnWarningParticles(world, serverPlayer, result);
                }
            }
            case CLEAN -> {
            }
        }
    }

    private static void spawnDenseParticles(ServerWorld world, ServerPlayerEntity player, MiasmaResult result) {
        int count = Math.min(result.volume, 40);
        List<BlockPos> airList = new ArrayList<>(result.airBlocks);
        Collections.shuffle(airList);
        for (int i = 0; i < count; i++) {
            BlockPos p = airList.get(i);
            world.spawnParticles(player, ParticleTypes.SPORE_BLOSSOM_AIR, false,
                    p.getX() + 0.5 + (world.random.nextDouble() - 0.5),
                    p.getY() + 0.5 + (world.random.nextDouble() - 0.5),
                    p.getZ() + 0.5 + (world.random.nextDouble() - 0.5), 1, 0.0, 0.0, 0.0, 0.0);
            if (world.random.nextBoolean()) {
                world.spawnParticles(player, ParticleTypes.FALLING_SPORE_BLOSSOM, false,
                        p.getX() + 0.5 + (world.random.nextDouble() - 0.5), p.getY() + 0.8,
                        p.getZ() + 0.5 + (world.random.nextDouble() - 0.5), 1, 0.0, 0.0, 0.0, 0.0);
            }
        }
    }

    private static void spawnLightParticles(ServerWorld world, ServerPlayerEntity player, MiasmaResult result) {
        int count = Math.min(result.volume / 2, 20);
        List<BlockPos> airList = new ArrayList<>(result.airBlocks);
        Collections.shuffle(airList);
        for (int i = 0; i < count; i++) {
            BlockPos p = airList.get(i);
            world.spawnParticles(player, ParticleTypes.MYCELIUM, false,
                    p.getX() + 0.5 + (world.random.nextDouble() - 0.5),
                    p.getY() + 0.5 + (world.random.nextDouble() - 0.5),
                    p.getZ() + 0.5 + (world.random.nextDouble() - 0.5), 1, 0.0, 0.0, 0.0, 0.0);
        }
    }

    private static void spawnWarningParticles(ServerWorld world, ServerPlayerEntity player, MiasmaResult result) {
        int count = Math.min(result.volume / 4, 10);
        if (count > 0) {
            List<BlockPos> airList = new ArrayList<>(result.airBlocks);
            Collections.shuffle(airList);
            for (int i = 0; i < count; i++) {
                BlockPos p = airList.get(i);
                world.spawnParticles(player, ParticleTypes.MYCELIUM, false,
                        p.getX() + 0.5 + (world.random.nextDouble() - 0.5),
                        p.getY() + 0.5 + (world.random.nextDouble() - 0.5),
                        p.getZ() + 0.5 + (world.random.nextDouble() - 0.5), 1, 0.0, 0.0, 0.0, 0.0);
            }
        }
    }

    public static MiasmaResult calculateMiasma(WorldAccess world, BlockPos eyePos) {
        ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        int maxAirVolume = config.toxicity.max_air_volume;
        int maxEuclideanRadius = config.toxicity.max_euclidean_radius;

        // Scansione della stanza in un unico passaggio BFS consolidato
        BFSExplorer.RoomScanResult scan = BFSExplorer.scanRoom(world, eyePos, maxAirVolume, maxEuclideanRadius, config);

        double toxicScore = scan.toxicScore();
        boolean openAir = scan.openAir();
        Set<BlockPos> airBlocks = scan.airBlocks();

        double ventilationScore = 0.0;
        int distanceToVentilation = openAir ? 0 : 999;
        double roomVentilationScore = 0.0;
        double totalSusceptibleWeight = 0.0;
        Map<BlockPos, Integer> distToGoalMap = Collections.emptyMap();

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
                distanceToVentilation, roomVentilationScore, scan.roomSusceptible().size(), totalSusceptibleWeight, distToGoalMap);
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
            if (!isCoveredByCeiling(world, neighborPos)) {
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
                if (faceResult.openAir && blockDist == 0) {
                    faceFlow = config.toxicity.open_sky_ventilation_per_block;
                    faceAeration = 1.0;
                } else if (roomFlow > 0.0 && blockDist < 900) {
                    double alpha = config.toxicity.ventilation_distance_alpha;
                    double weight = 1.0 / (1.0 + alpha * blockDist);
                    double totalWeight = Math.max(1.0, faceResult.totalSusceptibleWeight);
                    faceFlow = Math.min(roomFlow, roomFlow * (weight / totalWeight));
                    if (config.environment.ventilation_threshold_full_aeration > 0.0) {
                        double maxPossibleRoomAeration = Math.min(1.0, roomFlow / config.environment.ventilation_threshold_full_aeration);
                        faceAeration = Math.min(maxPossibleRoomAeration, faceFlow / config.environment.ventilation_threshold_full_aeration);
                    }
                }
            }

            double faceExposure = 0.0;
            if (faceResult.targetMiasma > 0.0 && faceResult.volume > 0) {
                double density = faceResult.targetMiasma / (double) faceResult.volume;
                double netFactor = Math.min(2.0, Math.sqrt(faceResult.targetMiasma / 8.0));
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

    // =========================================================================
    // FACADE METODI GEOMETRICI E CEILING (delegati a BFSExplorer)
    // =========================================================================

    public static int getFaceOpenMask(BlockView world, BlockPos pos, BlockState state, Direction face) {
        return BFSExplorer.getFaceOpenMask(world, pos, state, face);
    }

    public static int getStairsFaceOpenMask(BlockState state, Direction face) {
        return BFSExplorer.getStairsFaceOpenMask(state, face);
    }

    public static boolean isFaceSolid(BlockView world, BlockPos pos, BlockState state, Direction face) {
        return BFSExplorer.isFaceSolid(world, pos, state, face);
    }

    public static boolean isWallConnected(BlockState state) {
        return BFSExplorer.isWallConnected(state);
    }

    public static BlockAerationType getAerationType(BlockView world, BlockPos pos, BlockState state, Direction entryFace) {
        return BFSExplorer.getAerationType(world, pos, state, entryFace);
    }

    public static boolean isBlockAirflowBlocked(BlockState state, Direction flowDir) {
        return BFSExplorer.isBlockAirflowBlocked(state, flowDir);
    }

    public static boolean canAirPass(BlockView world, BlockPos fromPos, BlockState fromState,
                                     BlockPos toPos, BlockState toState, Direction dir) {
        return BFSExplorer.canAirPass(world, fromPos, fromState, toPos, toState, dir);
    }

    public static boolean isCoveredByCeiling(WorldAccess world, BlockPos pos) {
        return BFSExplorer.isCoveredByCeiling(world, pos);
    }

    public static boolean isCeilingBarrier(WorldAccess world, BlockPos pos, BlockState state) {
        return BFSExplorer.isCeilingBarrier(world, pos, state);
    }

    public static boolean isVentilatedToOutside(WorldAccess world, BlockPos gapPos, Direction outwardDir) {
        return BFSExplorer.isVentilatedToOutside(world, gapPos, outwardDir);
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
