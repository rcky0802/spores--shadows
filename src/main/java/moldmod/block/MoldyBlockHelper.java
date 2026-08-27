package moldmod.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class MoldyBlockHelper {

    public static boolean canBeInfected(BlockState state) {
        // Return false if WAXED == true
        if (state.contains(MoldyLogBlock.WAXED) && state.get(MoldyLogBlock.WAXED)) {
            return false;
        }

        // Return false if STRUCTURAL == true AND structures are immune.
        // ALL generated structures (villages, shipwrecks) are passive until interacted with (unless disabled in config).
        moldmod.config.ModConfig config = me.shedaniel.autoconfig.AutoConfig
                .getConfigHolder(moldmod.config.ModConfig.class).getConfig();
        if (state.contains(MoldyLogBlock.STRUCTURAL) && state.get(MoldyLogBlock.STRUCTURAL)) {
            if (config.general.structures_immune) {
                return false;
            }
        }

        return true;
    }

    public static boolean hasRandomTicks(BlockState state) {
        return canBeInfected(state);
    }

    public record MoldRiskResult(double Tmult, double Heff, double baseHum, double depthModifier,
            double localHumidityBonus, double Luv, double avgLight, double Smat, double catalystBonus, double R,
            float effectiveTemp, float surfaceTemp) {
    }

    public static MoldRiskResult calculateDetailedR(net.minecraft.world.WorldAccess world, BlockPos pos,
            boolean isWaxed, BlockState stateToCheck) {
        if (isWaxed || (stateToCheck.contains(MoldyLogBlock.STAGE) && stateToCheck.get(MoldyLogBlock.STAGE) >= 3))
            return new MoldRiskResult(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0f, 0.0f);

        moldmod.config.ModConfig config = me.shedaniel.autoconfig.AutoConfig
                .getConfigHolder(moldmod.config.ModConfig.class).getConfig();

        float surfaceTemp = world.getBiome(pos).value().getTemperature();
        float temp = surfaceTemp;
        
        if (world.getBiome(pos).isIn(net.minecraft.registry.tag.BiomeTags.IS_NETHER) || 
            (world instanceof net.minecraft.world.World w && w.getRegistryKey() == net.minecraft.world.World.NETHER)) {
            return new MoldRiskResult(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 100.0f, 100.0f);
        }
        if (world.getBiome(pos).isIn(net.minecraft.registry.tag.BiomeTags.IS_END) || 
            (world instanceof net.minecraft.world.World w && w.getRegistryKey() == net.minecraft.world.World.END) ||
            world.getBiome(pos).matchesId(net.minecraft.util.Identifier.of("minecraft", "the_end"))) {
            return new MoldRiskResult(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -100.0f, -100.0f);
        }

        // Depth-based temperature normalization
        // From cave_start_y to cave_full_y, temperature transitions to
        // cave_temperature.
        // Below cave_full_y, temperature is perfectly stable at cave_temperature.
        if (pos.getY() < config.environment.cave_start_y) {
            float caveTemp = config.environment.cave_temperature;
            if (pos.getY() <= config.environment.cave_full_y) {
                temp = caveTemp;
            } else {
                float range = (float) (config.environment.cave_start_y - config.environment.cave_full_y);
                float depthFactor = (config.environment.cave_start_y - pos.getY()) / range;
                depthFactor = Math.max(0.0f, Math.min(1.0f, depthFactor));
                temp = surfaceTemp + (caveTemp - surfaceTemp) * depthFactor;
            }
        } else if (pos.getY() > config.environment.high_altitude_start_y) {
            // Altitude-based cooling
            // From high_altitude_start_y to high_altitude_full_y, temperature drops towards
            // high_altitude_freezing_temperature
            float freezingTemp = config.environment.high_altitude_freezing_temperature;
            float range = (float) (config.environment.high_altitude_full_y - config.environment.high_altitude_start_y);
            float altitudeFactor = (pos.getY() - config.environment.high_altitude_start_y) / range;
            altitudeFactor = Math.max(0.0f, Math.min(1.0f, altitudeFactor));
            temp = surfaceTemp + (freezingTemp - surfaceTemp) * altitudeFactor;
        }

        // Early Exit: If temperature is frozen or desert, return 0.0 immediately
        double Tmult = (temp >= config.environment.min_temperature_survival
                && temp <= config.environment.max_temperature_survival) ? 1.0 : 0.0;
        if (Tmult == 0.0)
            return new MoldRiskResult(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, temp, surfaceTemp);

        boolean isRainingAt = false;
        if (world instanceof net.minecraft.world.World realWorld) {
            isRainingAt = realWorld.isRaining() && realWorld.isSkyVisible(pos.up());
        } else {
            isRainingAt = world.getBiome(pos).value().hasPrecipitation();
        }

        double baseHum = isRainingAt ? config.environment.rain_humidity_base : config.environment.dry_humidity_base;

        double depthModifier = 0.0;
        if (pos.getY() < config.environment.cave_start_y) {
            // Cap depth modifier
            depthModifier = Math.min(config.environment.max_depth_modifier,
                    (config.environment.cave_start_y - pos.getY()) * config.environment.depth_modifier_per_level);
        }

        double localHumidityBonus = 0.0;
        double catalystBonus = 0.0;

        BlockPos.Mutable mutable = new BlockPos.Mutable();
        int cx = pos.getX();
        int cy = pos.getY();
        int cz = pos.getZ();

        // 1. Scansione standard (Raggio piccolo) per catalizzatori e muffa adiacente
        int r = config.general.scan_radius;
        for (int x = -r; x <= r; x++) {
            for (int y = -r; y <= r; y++) {
                for (int z = -r; z <= r; z++) {
                    if (x == 0 && y == 0 && z == 0) continue;

                    mutable.set(cx + x, cy + y, cz + z);
                    if (world instanceof World realWorld && realWorld.getChunk(mutable.getX() >> 4, mutable.getZ() >> 4, net.minecraft.world.chunk.ChunkStatus.FULL, false) == null) continue;
                    BlockState nearbyState = world.getBlockState(mutable);

                    if (nearbyState.isOf(Blocks.MUD) || nearbyState.isOf(Blocks.WATER_CAULDRON)) {
                        localHumidityBonus += config.environment.cauldron_adjacent_bonus;
                        if (nearbyState.isOf(Blocks.MUD))
                            catalystBonus += config.catalysts.mud_bonus;
                    } else if (nearbyState.isOf(Blocks.MYCELIUM) || nearbyState.isOf(Blocks.PODZOL)) {
                        catalystBonus += config.catalysts.podzol_mycelium_bonus;
                    } else if (nearbyState.isOf(Blocks.BROWN_MUSHROOM) || nearbyState.isOf(Blocks.RED_MUSHROOM) ||
                            nearbyState.isOf(Blocks.BROWN_MUSHROOM_BLOCK) || nearbyState.isOf(Blocks.RED_MUSHROOM_BLOCK) ||
                            nearbyState.isOf(Blocks.MUSHROOM_STEM)) {
                        catalystBonus += config.catalysts.fungi_bonus;
                    } else if (nearbyState.isOf(Blocks.SPORE_BLOSSOM)) {
                        catalystBonus += config.catalysts.spore_blossom_bonus;
                    }

                    // Blocco muffito agisce da catalizzatore
                    if (nearbyState.contains(MoldyLogBlock.STAGE) && nearbyState.get(MoldyLogBlock.STAGE) > 0) {
                        if (nearbyState.contains(MoldyLogBlock.WAXED) && !nearbyState.get(MoldyLogBlock.WAXED)) {
                            int stage = nearbyState.get(MoldyLogBlock.STAGE);
                            if (stage == 1) {
                                catalystBonus += config.catalysts.tainted_block_bonus;
                            } else if (stage == 2) {
                                catalystBonus += config.catalysts.moldy_block_bonus;
                            } else if (stage == 3) {
                                catalystBonus += config.catalysts.rotten_block_bonus;
                            }
                        }
                    }
                }
            }
        }

        // 2. Scansione Estesa per Acqua
        // Per ottimizzare ulteriormente, ci fermiamo appena troviamo abbastanza acqua per il bonus massimo
        int waterBlocksFound = 0;
        int maxWaterBlocksNeeded = (int) Math.ceil(config.environment.max_local_humidity_bonus / config.environment.water_adjacent_bonus);
        int wr = config.environment.water_scan_radius;
        
        waterSearch:
        for (int x = -wr; x <= wr; x++) {
            for (int y = -wr; y <= wr; y++) {
                for (int z = -wr; z <= wr; z++) {
                    mutable.set(cx + x, cy + y, cz + z);
                    if (world instanceof World realWorld && realWorld.getChunk(mutable.getX() >> 4, mutable.getZ() >> 4, net.minecraft.world.chunk.ChunkStatus.FULL, false) == null) continue;
                    BlockState nearbyState = world.getBlockState(mutable);
                    
                    if (!nearbyState.getFluidState().isEmpty()) {
                        if (nearbyState.getFluidState().isOf(net.minecraft.fluid.Fluids.WATER)
                                || nearbyState.getFluidState().isOf(net.minecraft.fluid.Fluids.FLOWING_WATER)) {
                            localHumidityBonus += config.environment.water_adjacent_bonus;
                            waterBlocksFound++;
                            if (waterBlocksFound >= maxWaterBlocksNeeded) {
                                break waterSearch; // Ottimizzazione estrema: esce dal loop se ha raggiunto il cap di umidità
                            }
                        }
                    }
                }
            }
        }

        // Cap local humidity bonus so a pool of water doesn't guarantee 100% moisture
        // but helps significantly
        localHumidityBonus = Math.min(config.environment.max_local_humidity_bonus, localHumidityBonus);

        // Cap Effective Humidity at 1.0 (100%)
        double Heff = Math.min(1.0, baseHum + depthModifier + localHumidityBonus);

        int totalLight = 0;
        for (net.minecraft.util.math.Direction dir : net.minecraft.util.math.Direction.values()) {
            mutable.set(pos, dir);
            int skyLight = world.getLightLevel(net.minecraft.world.LightType.SKY, mutable);
            int blockLight = world.getLightLevel(net.minecraft.world.LightType.BLOCK, mutable);
            totalLight += Math.max(skyLight, blockLight);
        }
        // Also check the block itself (for transparent blocks like doors/buttons)
        int selfSky = world.getLightLevel(net.minecraft.world.LightType.SKY, pos);
        int selfBlock = world.getLightLevel(net.minecraft.world.LightType.BLOCK, pos);
        totalLight += Math.max(selfSky, selfBlock);

        double avgLight = totalLight / 7.0; // 6 faces + the center block space

        double Luv = Math.max(0.0, (15.0 - avgLight) / 15.0);

        double Smat = config.susceptibility.default_multiplier;
        if (stateToCheck != null) {
            String name = net.minecraft.registry.Registries.BLOCK.getId(stateToCheck.getBlock()).getPath();
            if (name.contains("stripped"))
                Smat = config.susceptibility.stripped_wood_multiplier;
            else if (name.contains("planks"))
                Smat = config.susceptibility.planks_multiplier;
        }

        double R = ((Heff * Luv * Smat) + catalystBonus) * Tmult;
        return new MoldRiskResult(Tmult, Heff, baseHum, depthModifier, localHumidityBonus, Luv, avgLight, Smat,
                catalystBonus, R, temp, surfaceTemp);
    }

    public static double calculateR(net.minecraft.world.WorldAccess world, BlockPos pos, boolean isWaxed,
            BlockState stateToCheck) {
        return calculateDetailedR(world, pos, isWaxed, stateToCheck).R();
    }

    public static void setStage(World world, BlockPos pos, BlockState state, int newStage) {
        world.setBlockState(pos, state.with(MoldyLogBlock.STAGE, newStage));
        syncDoorHalf(world, pos, state, MoldyLogBlock.STAGE, newStage);
    }

    public static void setWaxed(World world, BlockPos pos, BlockState state, boolean isWaxed) {
        world.setBlockState(pos, state.with(MoldyLogBlock.WAXED, isWaxed));
        syncDoorHalf(world, pos, state, MoldyLogBlock.WAXED, isWaxed);
    }

    private static <T extends Comparable<T>> void syncDoorHalf(World world, BlockPos pos, BlockState state,
            net.minecraft.state.property.Property<T> property, T value) {
        if (state.getBlock() instanceof net.minecraft.block.DoorBlock) {
            net.minecraft.block.enums.DoubleBlockHalf half = state.get(net.minecraft.block.DoorBlock.HALF);
            BlockPos otherPos = half == net.minecraft.block.enums.DoubleBlockHalf.LOWER ? pos.up() : pos.down();
            BlockState otherState = world.getBlockState(otherPos);
            if (otherState.isOf(state.getBlock()) && otherState.get(net.minecraft.block.DoorBlock.HALF) != half) {
                BlockState newState = otherState.with(property, value);

                // Ensure we also sync the structural tag removal if the original block had it
                // removed
                if (state.contains(MoldyLogBlock.STRUCTURAL) && !state.get(MoldyLogBlock.STRUCTURAL) &&
                        otherState.contains(MoldyLogBlock.STRUCTURAL) && otherState.get(MoldyLogBlock.STRUCTURAL)) {
                    newState = newState.with(MoldyLogBlock.STRUCTURAL, false);
                }

                world.setBlockState(otherPos, newState);
            }
        }
    }

    public static void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random,
            net.minecraft.block.Block block) {
        moldmod.config.ModConfig config = me.shedaniel.autoconfig.AutoConfig
                .getConfigHolder(moldmod.config.ModConfig.class).getConfig();
        if (!config.general.enable_mold_growth)
            return;

        if (state.get(MoldyLogBlock.WAXED))
            return;

        if (state.contains(MoldyLogBlock.STRUCTURAL) && state.get(MoldyLogBlock.STRUCTURAL)) {
            if (config.general.structures_immune) {
                return;
            }
        }

        int currentStage = state.get(MoldyLogBlock.STAGE);

        // CRITICAL OPTIMIZATION: If the block is already at max stage (3),
        // it makes no sense to calculate R.
        if (currentStage == 3)
            return;

        double R = calculateR(world, pos, false, state);

        if (config.general.show_debug_in_chat) {
            System.out.println("Mold tick at " + pos + ", R = " + R);
        }

        if (R > config.general.infection_threshold) {
            if (currentStage < 3) {
                setStage(world, pos, state, currentStage + 1);
            }
        }
    }

    public static void grantAdvancement(net.minecraft.entity.player.PlayerEntity player, String advancementName) {
        if (player instanceof net.minecraft.server.network.ServerPlayerEntity serverPlayer) {
            net.minecraft.advancement.AdvancementEntry entry = serverPlayer.getServer().getAdvancementLoader().get(net.minecraft.util.Identifier.of("spores--shadows", advancementName));
            if (entry != null) {
                net.minecraft.advancement.AdvancementProgress progress = serverPlayer.getAdvancementTracker().getProgress(entry);
                if (!progress.isDone()) {
                    for (String criterion : progress.getUnobtainedCriteria()) {
                        serverPlayer.getAdvancementTracker().grantCriterion(entry, criterion);
                    }
                }
            }
        }
    }
}
