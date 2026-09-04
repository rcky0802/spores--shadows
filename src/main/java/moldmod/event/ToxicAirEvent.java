package moldmod.event;

import me.shedaniel.autoconfig.AutoConfig;
import moldmod.block.MoldyBlock;
import moldmod.block.MoldyBlockHelper;
import moldmod.config.ModConfig;
import moldmod.item.ModItems;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.GrateBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.block.WallBlock;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.block.enums.SlabType;
import net.minecraft.block.enums.StairShape;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

public class ToxicAirEvent {

    private static final Direction[] DIRECTIONS = Direction.values();

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            int currentTick = server.getTicks();
            ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
            if (!config.toxicity.enable_toxic_air)
                return;

            int checkInterval = config.toxicity.check_interval_ticks;
            int radius = Math.max(config.toxicity.scan_radius, config.toxicity.max_euclidean_radius);

            if (currentTick % 1200 == 0) {
                RoomSaturationManager.cleanup(currentTick);
            }

            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                // Load Balancing sui tick
                if (currentTick % checkInterval != player.getId() % checkInterval)
                    continue;
                if (player.isSpectator() || player.isCreative())
                    continue;

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
        private static final java.util.Map<Long, RoomGasState> ACTIVE_ROOMS = new java.util.concurrent.ConcurrentHashMap<>();

        public record RoomGasState(
                double currentMiasma,
                double targetMiasma,
                long lastUpdateTick) {
        }

        public static BlockPos calculateAnchor(Set<BlockPos> airBlocks, BlockPos defaultPos) {
            if (defaultPos != null)
                return defaultPos;
            if (airBlocks == null || airBlocks.isEmpty())
                return BlockPos.ORIGIN;
            return airBlocks.iterator().next();
        }

        public static double getDynamicMiasma(net.minecraft.world.WorldAccess world, BlockPos anchor,
                double targetMiasma) {
            moldmod.config.ModConfig config = me.shedaniel.autoconfig.AutoConfig
                    .getConfigHolder(moldmod.config.ModConfig.class).getConfig();
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
            double factor = 1.0
                    - Math.pow(1.0 - net.minecraft.util.math.MathHelper.clamp(alpha, 0.01, 1.0), Math.max(1.0, steps));
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

        public MiasmaResult(net.minecraft.world.WorldAccess world, double toxicScore, double ventilationScore,
                boolean openAir, int volume, Set<BlockPos> airBlocks, BlockPos defaultPos) {
            this.openAir = openAir;
            this.volume = volume;
            this.airBlocks = airBlocks;
            this.anchorPos = RoomSaturationManager.calculateAnchor(airBlocks, defaultPos);

            moldmod.config.ModConfig config = me.shedaniel.autoconfig.AutoConfig
                    .getConfigHolder(moldmod.config.ModConfig.class).getConfig();

            this.toxicScore = Math.max(0.0, toxicScore);

            double effectiveVent = Math.max(0.0, ventilationScore);
            if (openAir && effectiveVent == 0.0) {
                effectiveVent = Math.max(1, volume) * config.toxicity.open_sky_ventilation_per_block;
            }
            this.ventilationScore = effectiveVent;

            if (openAir) {
                this.ventilationType = RoomVentilationType.CLEAN_OPEN_AIR;
            } else if (volume >= config.toxicity.max_air_volume) {
                this.ventilationType = RoomVentilationType.UNCONFINED_CAVERN;
            } else if (this.ventilationScore > 0.0) {
                this.ventilationType = RoomVentilationType.VENTILATED;
            } else {
                this.ventilationType = RoomVentilationType.HERMETIC_SEALED;
            }

            this.targetMiasma = Math.max(0.0, this.toxicScore - this.ventilationScore);
            this.netMiasma = (this.toxicScore == 0.0) ? 0.0
                    : RoomSaturationManager.getDynamicMiasma(world, this.anchorPos, this.targetMiasma);

            this.density = (volume > 0) ? (this.netMiasma / (double) volume) : 0.0;

            if (volume == 0 || this.netMiasma <= 0.0) {
                this.exposureIndex = 0.0;
                this.level = AirToxicityLevel.CLEAN;
            } else {
                // Modello Fisico-Biologico: concentrazione locale (densità) combinata con il
                // carico di emissione
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

        public MiasmaResult(double toxicScore, double ventilationScore, boolean openAir, int volume,
                Set<BlockPos> airBlocks) {
            this(null, toxicScore, ventilationScore, openAir, volume, airBlocks, BlockPos.ORIGIN);
        }
    }

    public static void checkRoomMiasma(net.minecraft.entity.player.PlayerEntity player, int radius) {
        if (!(player.getWorld() instanceof ServerWorld world)) {
            return;
        }
        BlockPos eyePos = BlockPos.ofFloored(player.getEyePos());

        // Pre-filtro rapido: se non c'è muffa nelle vicinanze esci subito O(R^3)
        if (!hasMoldNearby(world, eyePos, radius)) {
            return;
        }

        moldmod.config.ModConfig config = me.shedaniel.autoconfig.AutoConfig
                .getConfigHolder(moldmod.config.ModConfig.class).getConfig();
        MiasmaResult result = calculateMiasma(world, eyePos);

        ItemStack headStack = player.getEquippedStack(EquipmentSlot.HEAD);
        boolean hasSporeMask = config.toxicity.enable_spore_mask_protection && headStack.isOf(ModItems.SPORE_MASK);

        int filtrationLevel = 0;
        if (config.toxicity.enable_spore_filtration_enchantment && !headStack.isEmpty()) {
            var regOpt = world.getRegistryManager().getOptional(RegistryKeys.ENCHANTMENT);
            if (regOpt.isPresent()) {
                var entryOpt = regOpt.get().getEntry(moldmod.registry.ModEnchantments.SPORE_FILTRATION);
                if (entryOpt.isPresent()) {
                    filtrationLevel = net.minecraft.enchantment.EnchantmentHelper.getLevel(entryOpt.get(), headStack);
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
        java.util.List<BlockPos> airList = new java.util.ArrayList<>(result.airBlocks);
        java.util.Collections.shuffle(airList);
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
        java.util.List<BlockPos> airList = new java.util.ArrayList<>(result.airBlocks);
        java.util.Collections.shuffle(airList);
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
            java.util.List<BlockPos> airList = new java.util.ArrayList<>(result.airBlocks);
            java.util.Collections.shuffle(airList);
            for (int i = 0; i < count; i++) {
                BlockPos p = airList.get(i);
                world.spawnParticles(player, ParticleTypes.MYCELIUM, false,
                        p.getX() + 0.5 + (world.random.nextDouble() - 0.5),
                        p.getY() + 0.5 + (world.random.nextDouble() - 0.5),
                        p.getZ() + 0.5 + (world.random.nextDouble() - 0.5), 1, 0.0, 0.0, 0.0, 0.0);
            }
        }
    }

    public static class Zone {
        public final int id;
        public double directVentilation = 0.0;
        public final java.util.Map<Integer, ZoneConnection> children = new java.util.HashMap<>();

        public Zone(int id) {
            this.id = id;
        }

        public double calculateEffectiveVentilation(Set<Integer> visitedZones) {
            if (!visitedZones.add(this.id)) {
                return 0.0;
            }
            double total = this.directVentilation;
            for (ZoneConnection conn : children.values()) {
                double childVent = conn.targetZone.calculateEffectiveVentilation(visitedZones);
                total += Math.min(conn.portalCapacity, childVent);
            }
            return total;
        }
    }

    public static class ZoneConnection {
        public final Zone targetZone;
        public double portalCapacity = 0.0;

        public ZoneConnection(Zone targetZone) {
            this.targetZone = targetZone;
        }
    }

    public static boolean isConstrictedPortal(net.minecraft.world.BlockView world, BlockPos pos, BlockState state,
            Direction flowDir) {
        if (state == null)
            return false;
        net.minecraft.block.Block b = state.getBlock();
        if (b instanceof DoorBlock || b instanceof TrapdoorBlock || b instanceof FenceGateBlock ||
                b instanceof GrateBlock || b instanceof FenceBlock || state.isOf(Blocks.IRON_BARS) ||
                b instanceof WallBlock || b instanceof SlabBlock || b instanceof StairsBlock) {
            return true;
        }
        if (state.isAir()) {
            // Per qualsiasi direzione di flusso flowDir (X, Y, Z, orizzontale o verticale):
            // Controlla se le direzioni complanari perpendicolari a flowDir hanno delimitazioni solide (es. stipiti, pavimento, soffitto, angoli)
            for (Direction perp : DIRECTIONS) {
                if (perp.getAxis() != flowDir.getAxis()) {
                    BlockPos sidePos = pos.offset(perp);
                    if (isFaceSolid(world, sidePos, world.getBlockState(sidePos), perp.getOpposite())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static double getTransitionCapacity(net.minecraft.world.BlockView world, BlockPos fromPos,
            BlockState fromState, BlockPos toPos, BlockState toState, Direction dir, ModConfig config) {
        int fromMask = getFaceOpenMask(world, fromPos, fromState, dir);
        int toMask = getFaceOpenMask(world, toPos, toState, dir.getOpposite());
        int sharedBits = Integer.bitCount(fromMask & toMask);
        double areaFraction = sharedBits / 4.0;

        net.minecraft.block.Block toBlock = toState.getBlock();
        if (toBlock instanceof DoorBlock) {
            return config.toxicity.door_ventilation_value * areaFraction;
        }
        if (toBlock instanceof TrapdoorBlock) {
            return config.toxicity.trapdoor_ventilation_value * areaFraction;
        }
        if (toBlock instanceof GrateBlock) {
            return config.toxicity.copper_grate_ventilation_per_block * areaFraction;
        }
        if (toBlock instanceof FenceBlock || toState.isOf(Blocks.IRON_BARS)) {
            return config.toxicity.ventilation_gap_bonus * areaFraction;
        }
        return config.toxicity.open_sky_ventilation_per_block * areaFraction;
    }

    public static double getBlockVentilationValue(BlockState state, ModConfig config) {
        if (state == null)
            return config.toxicity.open_sky_ventilation_per_block;
        net.minecraft.block.Block b = state.getBlock();
        if (b instanceof DoorBlock) {
            return config.toxicity.door_ventilation_value;
        }
        if (b instanceof TrapdoorBlock) {
            return config.toxicity.trapdoor_ventilation_value;
        }
        if (b instanceof GrateBlock) {
            return config.toxicity.copper_grate_ventilation_per_block;
        }
        if (b instanceof FenceBlock || state.isOf(Blocks.IRON_BARS)) {
            return config.toxicity.ventilation_gap_bonus;
        }
        if (b instanceof SlabBlock) {
            return config.toxicity.open_sky_ventilation_per_block * 0.5;
        }
        if (b instanceof StairsBlock) {
            return config.toxicity.open_sky_ventilation_per_block * 0.25;
        }
        return config.toxicity.open_sky_ventilation_per_block;
    }

    public static MiasmaResult calculateMiasma(net.minecraft.world.WorldAccess world, BlockPos eyePos) {
        ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        int maxAirVolume = config.toxicity.max_air_volume;
        int maxEuclideanRadius = config.toxicity.max_euclidean_radius;
        int maxEuclideanRadiusSq = maxEuclideanRadius * maxEuclideanRadius;
        float moldToxMult = config.toxicity.mold_toxicity_multiplier;
        float ventBonus = config.toxicity.ventilation_gap_bonus;

        Queue<BlockPos> queue = new ArrayDeque<>();
        Set<BlockPos> visited = new HashSet<>();
        Set<BlockPos> countedMold = new HashSet<>();
        Set<BlockPos> countedVentilation = new HashSet<>();

        Zone rootZone = new Zone(0);
        java.util.Map<BlockPos, Zone> blockZoneMap = new java.util.HashMap<>();
        int[] nextZoneId = new int[] { 1 };

        queue.add(eyePos);
        visited.add(eyePos);
        blockZoneMap.put(eyePos, rootZone);

        double toxicScore = 0.0;
        boolean openAir = !isCoveredByCeiling(world, eyePos);

        while (!queue.isEmpty() && visited.size() < maxAirVolume) {
            BlockPos currentPos = queue.poll();
            Zone currentZone = blockZoneMap.getOrDefault(currentPos, rootZone);
            BlockState currentState = world.getBlockState(currentPos);

            // Se il blocco d'aria corrente comunica direttamente con il cielo aperto (è all'aria aperta)
            if (!isCoveredByCeiling(world, currentPos)) {
                if (countedVentilation.add(currentPos)) {
                    currentZone.directVentilation += config.toxicity.open_sky_ventilation_per_block;
                }
                // Il ramo che sta esplorando termina qui bloccando il passaggio agli altri blocchi esterni
                continue;
            }

            // Scansione dei vicini nelle 6 direzioni
            for (Direction dir : DIRECTIONS) {
                BlockPos neighborPos = currentPos.offset(dir);
                BlockState neighborState = world.getBlockState(neighborPos);

                if (canAirPass(world, currentPos, currentState, neighborPos, neighborState, dir)) {
                    // Controllo se neighborPos è un'interfaccia/varco di ventilazione verso l'esterno
                    if (neighborState.getBlock() instanceof DoorBlock
                            && isVentilatedToOutside(world, neighborPos, dir)) {
                        if (countedVentilation.add(neighborPos)) {
                            currentZone.directVentilation += config.toxicity.door_ventilation_value;
                        }
                        visited.add(neighborPos); // Confine esterno, non espanso nel mondo
                    } else if (neighborState.getBlock() instanceof TrapdoorBlock
                            && isVentilatedToOutside(world, neighborPos, dir)) {
                        if (countedVentilation.add(neighborPos)) {
                            currentZone.directVentilation += config.toxicity.trapdoor_ventilation_value;
                        }
                        visited.add(neighborPos); // Confine esterno, non espanso nel mondo
                    } else if (neighborState.getBlock() instanceof GrateBlock
                            && isVentilatedToOutside(world, neighborPos, dir)) {
                        if (countedVentilation.add(neighborPos)) {
                            currentZone.directVentilation += config.toxicity.copper_grate_ventilation_per_block;
                        }
                        visited.add(neighborPos); // Confine esterno, non espanso nel mondo
                    } else if (isVentilatedToOutside(world, neighborPos, dir)) {
                        if (countedVentilation.add(neighborPos)) {
                            currentZone.directVentilation += getBlockVentilationValue(neighborState, config);
                        }
                        visited.add(neighborPos); // Confine esterno, non espanso nel mondo
                    } else {
                        // Aria interna della stanza o passaggio comunicante (orizzontale/verticale/asimmetrico)
                        int dx = eyePos.getX() - neighborPos.getX();
                        int dy = eyePos.getY() - neighborPos.getY();
                        int dz = eyePos.getZ() - neighborPos.getZ();
                        int distSq = dx * dx + dy * dy + dz * dz;

                        if (distSq <= maxEuclideanRadiusSq) {
                            if (visited.add(neighborPos)) {
                                double transitionCap = getTransitionCapacity(world, currentPos, currentState,
                                        neighborPos, neighborState, dir, config);

                                if (isConstrictedPortal(world, neighborPos, neighborState, dir)) {
                                    // Trova se tra i vicini di neighborPos c'è già una Zone figlia esistente diversa da currentZone
                                    Zone existingChildZone = null;
                                    for (Direction d : DIRECTIONS) {
                                        BlockPos adj = neighborPos.offset(d);
                                        Zone adjZone = blockZoneMap.get(adj);
                                        if (adjZone != null && adjZone != currentZone) {
                                            existingChildZone = adjZone;
                                            break;
                                        }
                                    }

                                    Zone targetZone = (existingChildZone != null) ? existingChildZone
                                            : new Zone(nextZoneId[0]++);
                                    blockZoneMap.put(neighborPos, targetZone);
                                    ZoneConnection conn = currentZone.children.computeIfAbsent(targetZone.id,
                                            id -> new ZoneConnection(targetZone));
                                    conn.portalCapacity += transitionCap;
                                } else {
                                    // Stessa stanza / zona aperta
                                    blockZoneMap.put(neighborPos, currentZone);
                                }
                                queue.add(neighborPos);
                            } else {
                                // Blocco già visitato: se è una transizione tra due zone distinte, accumula la capacità del portale
                                Zone targetZone = blockZoneMap.get(neighborPos);
                                if (targetZone != null && targetZone != currentZone) {
                                    double transitionCap = getTransitionCapacity(world, currentPos, currentState,
                                            neighborPos, neighborState, dir, config);
                                    ZoneConnection conn = currentZone.children.computeIfAbsent(targetZone.id,
                                            id -> new ZoneConnection(targetZone));
                                    conn.portalCapacity += transitionCap;
                                }
                            }
                        }
                    }
                } else {
                    // Parete o perimetro solido/ostruzione
                    // A. Calcolo tossicità da muffa sul confine
                    if (neighborState.contains(MoldyBlock.STAGE)) {
                        boolean isWaxed = neighborState.contains(MoldyBlock.WAXED)
                                && neighborState.get(MoldyBlock.WAXED);
                        if (!isWaxed) {
                            if (countedMold.add(neighborPos)) {
                                int stage = neighborState.get(MoldyBlock.STAGE);
                                toxicScore += (stage * moldToxMult);
                            }
                        }
                    }

                    // B. Calcolo Modificatore di Ventilazione / Fessure
                    BlockAerationType toType = getAerationType(world, neighborPos, neighborState, dir.getOpposite());
                    if (toType == BlockAerationType.VENTILATED) {
                        if (isVentilatedToOutside(world, neighborPos, dir)) {
                            if (countedVentilation.add(neighborPos)) {
                                currentZone.directVentilation += ventBonus;
                            }
                        }
                    }
                }
            }
        }

        double ventilationScore = rootZone.calculateEffectiveVentilation(new HashSet<>());
        return new MiasmaResult(world, toxicScore, ventilationScore, openAir, visited.size(), visited, eyePos);
    }

    public record BlockAirEvaluation(
            double averageAeration,
            double averageExposureIndex,
            double averageNetMiasma,
            int exposedFacesCount,
            int maxVolume,
            boolean anyOpenAir) {
    }

    public static BlockAirEvaluation calculateBlockAirEvaluation(net.minecraft.world.WorldAccess world,
            BlockPos blockPos, BlockState blockState) {
        moldmod.config.ModConfig config = me.shedaniel.autoconfig.AutoConfig
                .getConfigHolder(moldmod.config.ModConfig.class).getConfig();

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
                    } else if (config.environment.ventilation_threshold_full_aeration > 0.0
                            && faceResult.ventilationScore > 0.0) {
                        faceAeration = Math.min(1.0,
                                faceResult.ventilationScore / config.environment.ventilation_threshold_full_aeration);
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

    public static MiasmaResult calculateBlockAirEnvironment(net.minecraft.world.WorldAccess world, BlockPos blockPos,
            BlockState blockState) {
        BlockState startState = (blockState != null) ? blockState : world.getBlockState(blockPos);
        BlockAerationType startType = getAerationType(world, blockPos, startState, Direction.UP);
        if (startType != BlockAerationType.OPEN_AIR && isFaceSolid(world, blockPos, startState, Direction.UP)) {
            return new MiasmaResult(world, 0.0, 0.0, false, 0, java.util.Collections.emptySet(), blockPos);
        }
        return calculateMiasma(world, blockPos);
    }

    public static BlockPos getCanonicalVentilationPos(BlockPos pos, BlockState state) {
        if (state != null && state.getBlock() instanceof DoorBlock && state.contains(DoorBlock.HALF)) {
            if (state.get(DoorBlock.HALF) == net.minecraft.block.enums.DoubleBlockHalf.UPPER) {
                return pos.down();
            }
        }
        return pos;
    }

    public static int getFaceOpenMask(net.minecraft.world.BlockView world, BlockPos pos, BlockState state,
            Direction face) {
        if (state == null || state.isAir()) {
            return 0b1111;
        }
        if (!state.getFluidState().isEmpty()) {
            return 0b0000;
        }

        net.minecraft.block.Block block = state.getBlock();

        // Copper grates: 100% aperto
        if (block instanceof GrateBlock) {
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
                if (face == Direction.DOWN)
                    return 0b0000;
                if (face == Direction.UP)
                    return 0b1111;
                return 0b0011; // bit 0, 1 (Top) aperti, bit 2, 3 (Bottom) solidi
            } else { // TOP slab
                if (face == Direction.UP)
                    return 0b0000;
                if (face == Direction.DOWN)
                    return 0b1111;
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

        // 8 Ottanti (0..7):
        // 0: Top-North-West, 1: Top-North-East, 2: Top-South-West, 3: Top-South-East
        // 4: Bottom-North-West, 5: Bottom-North-East, 6: Bottom-South-West, 7: Bottom-South-East
        int solidOctants = 0;

        if (half == BlockHalf.BOTTOM) {
            // Base inferiore solida
            solidOctants |= (1 << 4) | (1 << 5) | (1 << 6) | (1 << 7);
            solidOctants |= getStairStepOctants(facing, shape);
        } else {
            // Base superiore solida
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
        // Octants: 0: North-West, 1: North-East, 2: South-West, 3: South-East
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

    public static boolean isFaceSolid(net.minecraft.world.BlockView world, BlockPos pos, BlockState state,
            Direction face) {
        return getFaceOpenMask(world, pos, state, face) == 0;
    }

    public static boolean isWallConnected(BlockState state) {
        if (!(state.getBlock() instanceof WallBlock))
            return false;
        boolean ns = (state.contains(WallBlock.NORTH_SHAPE)
                && state.get(WallBlock.NORTH_SHAPE) != net.minecraft.block.enums.WallShape.NONE) &&
                (state.contains(WallBlock.SOUTH_SHAPE)
                        && state.get(WallBlock.SOUTH_SHAPE) != net.minecraft.block.enums.WallShape.NONE);
        boolean ew = (state.contains(WallBlock.EAST_SHAPE)
                && state.get(WallBlock.EAST_SHAPE) != net.minecraft.block.enums.WallShape.NONE) &&
                (state.contains(WallBlock.WEST_SHAPE)
                        && state.get(WallBlock.WEST_SHAPE) != net.minecraft.block.enums.WallShape.NONE);
        return ns || ew;
    }

    public static BlockAerationType getAerationType(net.minecraft.world.BlockView world, BlockPos pos, BlockState state,
            Direction entryFace) {
        if (state == null || state.isAir())
            return BlockAerationType.OPEN_AIR;

        net.minecraft.block.Block block = state.getBlock();

        // 1. Copper Grates: treated like air (total aeration)
        if (block instanceof GrateBlock) {
            return BlockAerationType.OPEN_AIR;
        }

        // 2. Fences (Staccionate) & Fence Gates (Cancelletti):
        if (block instanceof FenceBlock) {
            return BlockAerationType.VENTILATED;
        }
        if (block instanceof FenceGateBlock) {
            boolean isOpen = state.contains(Properties.OPEN) && state.get(Properties.OPEN);
            return isOpen ? BlockAerationType.OPEN_AIR : BlockAerationType.VENTILATED;
        }

        // 3. Wall blocks (Muretti): VENTILATED on floor/ceiling (vertical) and when
        // unconnected; HERMETIC when connected horizontally on walls
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
            if (state.isOf(Blocks.IRON_BARS))
                return BlockAerationType.VENTILATED;
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
        if (state == null)
            return false;
        if (state.getBlock() instanceof TrapdoorBlock) {
            boolean isOpen = state.contains(Properties.OPEN) && state.get(Properties.OPEN);
            if (!isOpen) {
                // Piastra orizzontale: chiude passaggi verticali (soffitto/pavimento)
                return flowDir.getAxis().isVertical();
            } else {
                // Piastra verticale: chiude finestre/botole su parete laterale
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

    public static boolean canAirPass(net.minecraft.world.BlockView world, BlockPos fromPos, BlockState fromState,
            BlockPos toPos, BlockState toState, Direction dir) {
        if (fromState == null || toState == null)
            return false;
        if (!toState.getFluidState().isEmpty())
            return false;

        int fromMask = getFaceOpenMask(world, fromPos, fromState, dir);
        int toMask = getFaceOpenMask(world, toPos, toState, dir.getOpposite());
        if ((fromMask & toMask) == 0) {
            return false;
        }

        BlockAerationType fromType = getAerationType(world, fromPos, fromState, dir.getOpposite());
        if (fromType != BlockAerationType.OPEN_AIR)
            return false;

        BlockAerationType toType = getAerationType(world, toPos, toState, dir.getOpposite());
        return toType == BlockAerationType.OPEN_AIR;
    }

    public static boolean isCoveredByCeiling(net.minecraft.world.WorldAccess world, BlockPos pos) {
        for (int dy = 0; dy <= 24; dy++) {
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
        return false;
    }

    public static boolean isCeilingBarrier(net.minecraft.world.WorldAccess world, BlockPos pos, BlockState state) {
        if (state == null || state.isAir())
            return false;
        if (state.getBlock() instanceof net.minecraft.block.GrateBlock)
            return false;
        BlockAerationType type = getAerationType(world, pos, state, Direction.DOWN);
        return type == BlockAerationType.HERMETIC || type == BlockAerationType.VENTILATED;
    }

    public static boolean isVentilatedToOutside(net.minecraft.world.WorldAccess world, BlockPos gapPos,
            Direction outwardDir) {
        Direction entryFace = outwardDir.getOpposite();
        BlockState gapState = world.getBlockState(gapPos);

        if (isFaceSolid(world, gapPos, gapState, entryFace)) {
            return false;
        }

        // Se gapPos stesso è già a cielo aperto (nessun soffitto sopra di esso)
        if (!isCoveredByCeiling(world, gapPos)) {
            return true;
        }

        // 1. Se outwardDir è verso l'alto (soffitto, es. staccionata/grata sul soffitto)
        if (outwardDir == Direction.UP) {
            int exitMask = getFaceOpenMask(world, gapPos, gapState, Direction.UP);
            int enterMask = getFaceOpenMask(world, gapPos.up(), world.getBlockState(gapPos.up()), Direction.DOWN);
            if ((exitMask & enterMask) != 0) {
                return !isCoveredByCeiling(world, gapPos.up());
            }
            return false;
        }

        // 2. Se outwardDir è verso il basso (pavimento, es. staccionata/grata sul pavimento)
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

        // 3. Uscita orizzontale in direzione outwardDir (parete laterale)
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
                // Il flusso d'aria è fisicamente interrotto (es. slab bassa seguita da slab alta o muro)
                break;
            }

            if (!isCoveredByCeiling(world, checkPos)) {
                return true;
            }

            // Controllo se l'aria può sfogare lateralmente all'esterno (es. tettoia aperta sui lati)
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
