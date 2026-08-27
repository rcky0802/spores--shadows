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
import net.minecraft.world.World;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

public class ToxicAirEvent {

    private static final int MAX_AIR_VOLUME = 180;
    private static final int MAX_MANHATTAN_RADIUS = 8;
    private static final Direction[] DIRECTIONS = Direction.values();

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            int currentTick = server.getTicks();
            moldmod.config.ModConfig config = me.shedaniel.autoconfig.AutoConfig.getConfigHolder(moldmod.config.ModConfig.class).getConfig();

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

    public static class MiasmaResult {
        public final double toxicScore;
        public final double ventilationScore;
        public final double netMiasma;
        public final boolean openAir;
        public final int volume;
        public final Set<BlockPos> airBlocks;

        public MiasmaResult(double toxicScore, double ventilationScore, double netMiasma, boolean openAir, int volume, Set<BlockPos> airBlocks) {
            this.toxicScore = toxicScore;
            this.ventilationScore = ventilationScore;
            this.netMiasma = netMiasma;
            this.openAir = openAir;
            this.volume = volume;
            this.airBlocks = airBlocks;
        }
    }

    private static void checkRoomMiasma(ServerPlayerEntity player, int radius) {
        ServerWorld world = (ServerWorld) player.getWorld();
        BlockPos eyePos = BlockPos.ofFloored(player.getEyePos());

        // Pre-filtro rapido: se non c'è muffa nelle vicinanze esci subito O(R^3)
        if (!hasMoldNearby(world, eyePos, radius)) {
            return;
        }

        MiasmaResult result = calculateMiasma(world, eyePos);

        // Se l'ambiente è aperto o supera il volume massimo, il gas si disperde
        if (result.openAir || result.volume >= MAX_AIR_VOLUME) {
            return;
        }

        // Applicazione Effetti e Particelle
        double densita = result.netMiasma / Math.max(result.volume, 1);

        moldmod.config.ModConfig config = me.shedaniel.autoconfig.AutoConfig.getConfigHolder(moldmod.config.ModConfig.class).getConfig();

        if (result.netMiasma >= 16.0 || (densita >= 0.18 && result.netMiasma >= 10.0)) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, config.toxicity.duration_poison_ticks, config.toxicity.poison_amplifier, false, false, true));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, config.toxicity.duration_nausea_ticks, config.toxicity.nausea_amplifier, false, false, true));
            MoldyBlockHelper.grantAdvancement(player, "toxic_air");
            
            // Dense particles in the room
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
        } else if (result.netMiasma >= 8.0 || (densita >= 0.09 && result.netMiasma >= 5.0)) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 80, 0, false, false, true));
            
            // Light particles in the room
            int count = Math.min(result.volume / 2, 20);
            java.util.List<BlockPos> airList = new java.util.ArrayList<>(result.airBlocks);
            java.util.Collections.shuffle(airList);
            for (int i = 0; i < count; i++) {
                BlockPos p = airList.get(i);
                world.spawnParticles(player, ParticleTypes.MYCELIUM, false, 
                        p.getX() + 0.5 + (world.random.nextDouble() - 0.5), p.getY() + 0.5 + (world.random.nextDouble() - 0.5), p.getZ() + 0.5 + (world.random.nextDouble() - 0.5), 1, 0.0, 0.0, 0.0, 0.0);
            }
        } else if (result.netMiasma >= 3.0 || densita >= 0.04) {
            // Warning particles in the room (no status effects yet)
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
    }

    public static MiasmaResult calculateMiasma(ServerWorld world, BlockPos eyePos) {
        Queue<BlockPos> queue = new ArrayDeque<>();
        Set<BlockPos> visited = new HashSet<>();

        queue.add(eyePos);
        visited.add(eyePos);

        double toxicScore = 0.0;
        double ventilationScore = 0.0;
        boolean openAir = false;

        while (!queue.isEmpty() && visited.size() < MAX_AIR_VOLUME) {
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
                    // Il flusso d'aria passa (aria, porte/botole aperte, spazi vuoti)
                    int distManhattan = Math.abs(eyePos.getX() - neighborPos.getX()) + 
                                        Math.abs(eyePos.getY() - neighborPos.getY()) + 
                                        Math.abs(eyePos.getZ() - neighborPos.getZ());
                    
                    if (distManhattan <= MAX_MANHATTAN_RADIUS) {
                        if (visited.add(neighborPos)) {
                            queue.add(neighborPos);
                        }
                    }
                } else {
                    // Parete o perimetro solido: processa il blocco di confine
                    
                    // A. Calcolo Tossicità da Muffa
                    if (neighborState.contains(MoldyLogBlock.STAGE)) {
                        boolean isWaxed = neighborState.contains(MoldyLogBlock.WAXED) && neighborState.get(MoldyLogBlock.WAXED);
                        if (!isWaxed) {
                            int stage = neighborState.get(MoldyLogBlock.STAGE);
                            toxicScore += (stage * 0.75);
                        }
                    }

                    // B. Ventilazione / Fessure (es. Fence, Sbarre di ferro, Lastre parziali)
                    if (!neighborState.isFullCube(world, neighborPos)) {
                        int neighborTopY = world.getTopY(Heightmap.Type.MOTION_BLOCKING, neighborPos.getX(), neighborPos.getZ());
                        if (neighborPos.getY() + 1 >= neighborTopY) {
                            ventilationScore += 3.0;
                        }
                    }
                }
            }
        }

        double netMiasma = toxicScore - ventilationScore;
        return new MiasmaResult(toxicScore, ventilationScore, netMiasma, openAir, visited.size(), visited);
    }

    public static boolean canAirPass(World world, BlockPos fromPos, BlockState fromState, BlockPos toPos, BlockState toState, Direction dir) {
        if (fromState.isSideSolidFullSquare(world, fromPos, dir)) {
            return false;
        }
        return !toState.isSideSolidFullSquare(world, toPos, dir.getOpposite());
    }

    public static boolean hasMoldNearby(World world, BlockPos center, int radius) {
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
