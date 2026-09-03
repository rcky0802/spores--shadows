package moldmod.block;

import me.shedaniel.autoconfig.AutoConfig;
import moldmod.SporesShadows;
import moldmod.config.ModConfig;
import moldmod.registry.ModCatalystRegistry;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

import java.util.List;

public class MoldyBlockHelper {

    private static final Direction[] DIRECTIONS = Direction.values();

    public static BlockState initMoldyDefaultState(BlockState state) {
        return state
                .with(MoldyBlock.STAGE, 0)
                .with(MoldyBlock.WAXED, false)
                .with(MoldyBlock.STRUCTURAL, false);
    }

    public static void appendMoldyProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(MoldyBlock.STAGE, MoldyBlock.WAXED, MoldyBlock.STRUCTURAL);
    }

    public static boolean tryBreakRottenBlock(World world, BlockPos pos, BlockState state, float chance) {
        if (!world.isClient && state.contains(MoldyBlock.STAGE) && state.contains(MoldyBlock.WAXED)) {
            int stage = state.get(MoldyBlock.STAGE);
            boolean waxed = state.get(MoldyBlock.WAXED);
            if (stage == 3 && !waxed) {
                if (world.random.nextFloat() < chance) {
                    world.breakBlock(pos, false);
                    world.playSound(null, pos, SoundEvents.BLOCK_WOOD_BREAK,
                            SoundCategory.BLOCKS, 1.0f, 0.8f);
                    return true;
                }
            }
        }
        return false;
    }

    public static BlockState copyMatchingProperties(BlockState from, BlockState to) {
        BlockState result = to;
        for (Property<?> property : from.getProperties()) {
            if (result.contains(property)) {
                result = copyProperty(from, result, property);
            }
        }
        return result;
    }

    private static <T extends Comparable<T>> BlockState copyProperty(BlockState from, BlockState to,
            Property<T> property) {
        return to.with(property, from.get(property));
    }

    public static boolean canBeInfected(BlockState state) {
        // Return false if WAXED == true
        if (state.contains(MoldyBlock.WAXED) && state.get(MoldyBlock.WAXED)) {
            return false;
        }

        // Return false if STRUCTURAL == true AND structures are immune.
        // ALL generated structures (villages, shipwrecks) are passive until interacted
        // with (unless disabled in config).
        ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        if (state.contains(MoldyBlock.STRUCTURAL) && state.get(MoldyBlock.STRUCTURAL)) {
            if (config.general.structures_immune) {
                return false;
            }
        }

        return true;
    }

    public static boolean hasRandomTicks(BlockState state) {
        if (Registries.BLOCK.getId(state.getBlock()).getPath().startsWith("waxed_")) {
            return false;
        }
        return canBeInfected(state);
    }

    public record MoldRiskResult(double Tmult, double Heff, double Hraw, double baseHum, double depthModifier,
            double localHumidityBonus, double aeration, double aerationDryingBonus, double Luv, double avgLight,
            double Smat, double catalystBonus, double miasmaBonus, double netMiasma, int airVolume, int exposedFaces,
            double R, float effectiveTemp, float surfaceTemp) {
    }

    public static MoldRiskResult calculateDetailedR(WorldAccess world, BlockPos pos,
            boolean isWaxed, BlockState stateToCheck) {
        if (isWaxed || (stateToCheck != null && stateToCheck.contains(MoldyBlock.STAGE)
                && stateToCheck.get(MoldyBlock.STAGE) >= 3))
            return new MoldRiskResult(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0, 0, 0.0,
                    0.0f, 0.0f);

        ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();

        float surfaceTemp = world.getBiome(pos).value().getTemperature();
        float temp = surfaceTemp;

        if (world.getBiome(pos).isIn(BiomeTags.IS_NETHER) ||
                (world instanceof World w
                        && w.getRegistryKey() == World.NETHER)) {
            return new MoldRiskResult(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0, 0, 0.0,
                    100.0f, 100.0f);
        }
        if (world.getBiome(pos).isIn(BiomeTags.IS_END) ||
                (world instanceof net.minecraft.world.World w && w.getRegistryKey() == net.minecraft.world.World.END) ||
                world.getBiome(pos).matchesId(net.minecraft.util.Identifier.of("minecraft", "the_end"))) {
            return new MoldRiskResult(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0, 0, 0.0,
                    -100.0f, -100.0f);
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
            return new MoldRiskResult(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0, 0, 0.0,
                    temp, surfaceTemp);

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
                    if (x == 0 && y == 0 && z == 0)
                        continue;

                    mutable.set(cx + x, cy + y, cz + z);
                    if (world instanceof World realWorld && realWorld.getChunk(mutable.getX() >> 4, mutable.getZ() >> 4,
                            net.minecraft.world.chunk.ChunkStatus.FULL, false) == null)
                        continue;
                    BlockState nearbyState = world.getBlockState(mutable);

                    ModCatalystRegistry.CatalystContribution contribution = ModCatalystRegistry
                            .getContribution(nearbyState, config);
                    localHumidityBonus += contribution.localHumidityBonus();
                    catalystBonus += contribution.catalystBonus();

                    // Blocco muffito agisce da catalizzatore
                    if (nearbyState.contains(MoldyBlock.STAGE) && nearbyState.get(MoldyBlock.STAGE) > 0) {
                        if (!nearbyState.contains(MoldyBlock.WAXED) || !nearbyState.get(MoldyBlock.WAXED)) {
                            int stage = nearbyState.get(MoldyBlock.STAGE);
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
        // Per ottimizzare ulteriormente, ci fermiamo appena troviamo abbastanza acqua
        // per il bonus massimo
        int waterBlocksFound = 0;
        int maxWaterBlocksNeeded = (int) Math
                .ceil(config.environment.max_local_humidity_bonus / config.environment.water_adjacent_bonus);
        int wr = config.environment.water_scan_radius;

        waterSearch: for (int x = -wr; x <= wr; x++) {
            for (int y = -wr; y <= wr; y++) {
                for (int z = -wr; z <= wr; z++) {
                    mutable.set(cx + x, cy + y, cz + z);
                    if (world instanceof World realWorld && realWorld.getChunk(mutable.getX() >> 4, mutable.getZ() >> 4,
                            net.minecraft.world.chunk.ChunkStatus.FULL, false) == null)
                        continue;
                    BlockState nearbyState = world.getBlockState(mutable);

                    if (!nearbyState.getFluidState().isEmpty()) {
                        if (nearbyState.getFluidState().isOf(net.minecraft.fluid.Fluids.WATER)
                                || nearbyState.getFluidState().isOf(net.minecraft.fluid.Fluids.FLOWING_WATER)) {
                            localHumidityBonus += config.environment.water_adjacent_bonus;
                            waterBlocksFound++;
                            if (waterBlocksFound >= maxWaterBlocksNeeded) {
                                break waterSearch; // Ottimizzazione estrema: esce dal loop se ha raggiunto il cap di
                                                   // umidità
                            }
                        }
                    }
                }
            }
        }

        // Cap local humidity bonus so a pool of water doesn't guarantee 100% moisture
        // but helps significantly
        localHumidityBonus = Math.min(config.environment.max_local_humidity_bonus, localHumidityBonus);

        double Hraw = baseHum + depthModifier + localHumidityBonus;

        // BFS Aeration and Miasma calculation averaged over exposed faces
        moldmod.event.ToxicAirEvent.BlockAirEvaluation airEval = moldmod.event.ToxicAirEvent
                .calculateBlockAirEvaluation(world, pos, stateToCheck);

        double aeration = 0.0;
        if (config.environment.enable_ventilation_drying) {
            aeration = airEval.averageAeration();
        }

        double aerationDryingBonus = aeration * config.environment.aeration_drying_bonus;
        double Heff = Math.max(0.0, Math.min(1.0, Hraw - aerationDryingBonus));

        double miasmaBonus = 0.0;
        if (config.environment.enable_miasma_spore_pressure && airEval.averageExposureIndex() > 0.0) {
            miasmaBonus = airEval.averageExposureIndex() * config.environment.miasma_spore_multiplier;
        }

        int totalLight = 0;
        int samplePoints = 6;
        for (net.minecraft.util.math.Direction dir : DIRECTIONS) {
            mutable.set(pos, dir);
            int skyLight = world.getLightLevel(net.minecraft.world.LightType.SKY, mutable);
            int blockLight = world.getLightLevel(net.minecraft.world.LightType.BLOCK, mutable);
            totalLight += Math.max(skyLight, blockLight);
        }
        // Also check the block itself for transparent/partial blocks (doors, buttons, trapdoors, slabs)
        if (stateToCheck == null || !stateToCheck.isOpaqueFullCube(world, pos)) {
            int selfSky = world.getLightLevel(net.minecraft.world.LightType.SKY, pos);
            int selfBlock = world.getLightLevel(net.minecraft.world.LightType.BLOCK, pos);
            totalLight += Math.max(selfSky, selfBlock);
            samplePoints = 7;
        }

        double avgLight = totalLight / (double) samplePoints;

        double Luv = Math.max(0.0, (15.0 - avgLight) / 15.0);

        double Smat = config.susceptibility.default_multiplier;
        if (stateToCheck != null) {
            String name = net.minecraft.registry.Registries.BLOCK.getId(stateToCheck.getBlock()).getPath();
            if (name.contains("stripped"))
                Smat = config.susceptibility.stripped_wood_multiplier;
            else if (name.contains("planks"))
                Smat = config.susceptibility.planks_multiplier;
        }

        double R = ((Heff * Luv * Smat) + catalystBonus + miasmaBonus) * Tmult;
        return new MoldRiskResult(Tmult, Heff, Hraw, baseHum, depthModifier, localHumidityBonus, aeration,
                aerationDryingBonus, Luv, avgLight, Smat, catalystBonus, miasmaBonus, airEval.averageNetMiasma(),
                airEval.maxVolume(), airEval.exposedFacesCount(), R, temp, surfaceTemp);
    }

    public static double calculateR(net.minecraft.world.WorldAccess world, BlockPos pos, boolean isWaxed,
            BlockState stateToCheck) {
        return calculateDetailedR(world, pos, isWaxed, stateToCheck).R();
    }

    public static void setStage(World world, BlockPos pos, BlockState state, int newStage) {
        world.setBlockState(pos, state.with(MoldyBlock.STAGE, newStage));
        syncDoorHalf(world, pos, state, MoldyBlock.STAGE, newStage);
    }

    public static void setWaxed(World world, BlockPos pos, BlockState state, boolean isWaxed) {
        world.setBlockState(pos, state.with(MoldyBlock.WAXED, isWaxed));
        syncDoorHalf(world, pos, state, MoldyBlock.WAXED, isWaxed);
    }

    private static <T extends Comparable<T>> void syncDoorHalf(World world, BlockPos pos, BlockState state,
            Property<T> property, T value) {
        if (state.getBlock() instanceof DoorBlock) {
            DoubleBlockHalf half = state.get(DoorBlock.HALF);
            BlockPos otherPos = half == DoubleBlockHalf.LOWER ? pos.up() : pos.down();
            BlockState otherState = world.getBlockState(otherPos);
            if (otherState.isOf(state.getBlock()) && otherState.get(DoorBlock.HALF) != half) {
                BlockState newState = otherState.with(property, value);

                // Ensure we also sync the structural tag removal if the original block had it removed
                if (state.contains(MoldyBlock.STRUCTURAL) && !state.get(MoldyBlock.STRUCTURAL) &&
                        otherState.contains(MoldyBlock.STRUCTURAL) && otherState.get(MoldyBlock.STRUCTURAL)) {
                    newState = newState.with(MoldyBlock.STRUCTURAL, false);
                }

                world.setBlockState(otherPos, newState);
            }
        }
    }

    public static void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        if (!config.general.enable_mold_growth)
            return;

        int currentStage = state.get(MoldyBlock.STAGE);

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

    public static void grantAdvancement(PlayerEntity player, String advancementName) {
        if (player instanceof ServerPlayerEntity serverPlayer) {
            AdvancementEntry entry = serverPlayer.getServer().getAdvancementLoader()
                    .get(Identifier.of(SporesShadows.MOD_ID, advancementName));
            if (entry != null) {
                AdvancementProgress progress = serverPlayer.getAdvancementTracker()
                        .getProgress(entry);
                if (!progress.isDone()) {
                    for (String criterion : progress.getUnobtainedCriteria()) {
                        serverPlayer.getAdvancementTracker().grantCriterion(entry, criterion);
                    }
                }
            }
        }
    }

    public static ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state) {
        Block block = state.getBlock();
        int stage = state.contains(MoldyBlock.STAGE) ? state.get(MoldyBlock.STAGE) : 0;
        boolean waxed = state.contains(MoldyBlock.WAXED) && state.get(MoldyBlock.WAXED);

        List<Item> items = ModBlocks.MOLDY_ITEMS_BY_BLOCK.get(block);
        if (items != null && items.size() == 7) {
            if (stage == 0 && !waxed) {
                Block moldyBlock = ModBlocks.WAXED_TO_MOLDY.getOrDefault(block, block);
                Block vanillaBlock = ModBlocks.MOLDY_TO_VANILLA.get(moldyBlock);
                if (vanillaBlock != null) {
                    return new ItemStack(vanillaBlock.asItem());
                }
            }
            int index = (stage == 0) ? 0 : (stage * 2 - (waxed ? 0 : 1));
            if (index >= 0 && index < items.size()) {
                return new ItemStack(items.get(index));
            }
        }
        return new ItemStack(block.asItem());
    }
}
