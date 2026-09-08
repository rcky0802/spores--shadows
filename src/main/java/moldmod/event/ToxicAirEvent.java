package moldmod.event;

import me.shedaniel.autoconfig.AutoConfig;
import moldmod.block.MoldyBlockHelper;
import moldmod.config.ModConfig;
import moldmod.event.MiasmaCalculator.MiasmaResult;

import moldmod.item.ModItems;
import moldmod.registry.ModEnchantments;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Toxic Air Event, Player Toxicity Listener, and Particle Spawner.
 * Delegates miasma calculations and gas dynamics to MiasmaCalculator.
 */
public class ToxicAirEvent {

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

    public static void checkRoomMiasma(PlayerEntity player, int radius) {
        if (!(player.getWorld() instanceof ServerWorld world)) {
            return;
        }
        BlockPos eyePos = BlockPos.ofFloored(player.getEyePos());

        if (!MiasmaCalculator.hasMoldNearby(world, eyePos, radius)) {
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
                            prev.volume, prev.airBlocks, eyePos, prev.distanceToVentilation,
                            prev.roomVentilationScore, prev.susceptibleBlockCount, prev.totalSusceptibleWeight,
                            prev.distToGoal, prev.nodeFlows);
                } else {
                    result = prev;
                }
            } else {
                result = prev;
            }
            PLAYER_AIR_CACHE.put(player.getUuid(), new PlayerAirCache(eyePos, currentTick, result));
        } else {
            result = MiasmaCalculator.calculateMiasma(world, eyePos);
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
}
