package moldmod.block;

import me.shedaniel.autoconfig.AutoConfig;
import moldmod.config.ModConfig;
import moldmod.event.MiasmaCalculator;
import moldmod.event.MiasmaCalculator.BlockAirEvaluation;
import moldmod.registry.ModCatalystRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluids;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.block.Block;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Dedicated mathematical and environmental calculation engine for Mold Infection Risk (R).
 *
 * Formula:
 *   R = ((Heff * Luv * Smat) + Catalysts + MiasmaBonus) * Tmult
 */
public final class MoldRiskCalculator {

    private static final Direction[] DIRECTIONS = Direction.values();
    private static final Map<Block, Double> SUSCEPTIBILITY_CACHE = new ConcurrentHashMap<>();

    private MoldRiskCalculator() {
    }

    public static double getMaterialSusceptibility(Block block, ModConfig config) {
        if (block == null) {
            return config.susceptibility.default_multiplier;
        }
        Double cached = SUSCEPTIBILITY_CACHE.get(block);
        if (cached != null) {
            return cached;
        }
        String name = Registries.BLOCK.getId(block).getPath();
        double smat = config.susceptibility.default_multiplier;
        if (name.contains("stripped")) {
            smat = config.susceptibility.stripped_wood_multiplier;
        } else if (name.contains("planks")) {
            smat = config.susceptibility.planks_multiplier;
        }
        SUSCEPTIBILITY_CACHE.put(block, smat);
        return smat;
    }

    public record MoldRiskResult(
            double Tmult,
            double Heff,
            double Hraw,
            double baseHum,
            double depthModifier,
            double localHumidityBonus,
            double aerationFlow,
            double aeration,
            double aerationDryingBonus,
            double Luv,
            double avgLight,
            double Smat,
            double catalystBonus,
            double miasmaBonus,
            double netMiasma,
            int airVolume,
            int exposedFaces,
            double R,
            float effectiveTemp,
            float surfaceTemp,
            int distanceToVentilation) {

        public MoldRiskResult(double Tmult, double Heff, double Hraw, double baseHum, double depthModifier,
                double localHumidityBonus, double aerationFlow, double aeration, double aerationDryingBonus, double Luv, double avgLight,
                double Smat, double catalystBonus, double miasmaBonus, double netMiasma, int airVolume, int exposedFaces,
                double R, float effectiveTemp, float surfaceTemp) {
            this(Tmult, Heff, Hraw, baseHum, depthModifier, localHumidityBonus, aerationFlow, aeration, aerationDryingBonus,
                    Luv, avgLight, Smat, catalystBonus, miasmaBonus, netMiasma, airVolume, exposedFaces, R, effectiveTemp, surfaceTemp, 999);
        }
    }

    /**
     * Calculates the scalar infection risk R for a given block.
     */
    public static double calculateR(WorldAccess world, BlockPos pos, boolean isWaxed, BlockState stateToCheck) {
        return calculate(world, pos, isWaxed, stateToCheck).R();
    }

    /**
     * Performs the comprehensive environmental and biological scan to compute all components of Mold Risk R.
     */
    public static MoldRiskResult calculate(WorldAccess world, BlockPos pos,
            boolean isWaxed, BlockState stateToCheck) {
        if (isWaxed || (stateToCheck != null && stateToCheck.contains(MoldyBlock.STAGE)
                && stateToCheck.get(MoldyBlock.STAGE) >= 3)) {
            return new MoldRiskResult(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0, 0, 0.0,
                    0.0f, 0.0f);
        }

        ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();

        float surfaceTemp = world.getBiome(pos).value().getTemperature();
        float temp = surfaceTemp;

        if (world.getBiome(pos).isIn(BiomeTags.IS_NETHER) ||
                (world instanceof World w && w.getRegistryKey() == World.NETHER)) {
            return new MoldRiskResult(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0, 0, 0.0,
                    100.0f, 100.0f);
        }
        if (world.getBiome(pos).isIn(BiomeTags.IS_END) ||
                (world instanceof World w && w.getRegistryKey() == World.END) ||
                world.getBiome(pos).matchesId(Identifier.of("minecraft", "the_end"))) {
            return new MoldRiskResult(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0, 0, 0.0,
                    -100.0f, -100.0f);
        }

        // 1. Depth-based & Altitude-based temperature normalization
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
            float freezingTemp = config.environment.high_altitude_freezing_temperature;
            float range = (float) (config.environment.high_altitude_full_y - config.environment.high_altitude_start_y);
            float altitudeFactor = (pos.getY() - config.environment.high_altitude_start_y) / range;
            altitudeFactor = Math.max(0.0f, Math.min(1.0f, altitudeFactor));
            temp = surfaceTemp + (freezingTemp - surfaceTemp) * altitudeFactor;
        }

        // Early Exit: If temperature is outside survival range, return 0.0 immediately
        double Tmult = (temp >= config.environment.min_temperature_survival
                && temp <= config.environment.max_temperature_survival) ? 1.0 : 0.0;
        if (Tmult == 0.0) {
            return new MoldRiskResult(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0, 0, 0.0,
                    temp, surfaceTemp);
        }

        boolean isRainingAt = false;
        if (world instanceof World realWorld) {
            isRainingAt = realWorld.hasRain(pos.up());
        } else {
            isRainingAt = world.getBiome(pos).value().hasPrecipitation();
        }

        double baseHum = isRainingAt ? config.environment.rain_humidity_base : config.environment.dry_humidity_base;

        double depthModifier = 0.0;
        if (pos.getY() < config.environment.cave_start_y) {
            depthModifier = Math.min(config.environment.max_depth_modifier,
                    (config.environment.cave_start_y - pos.getY()) * config.environment.depth_modifier_per_level);
        }

        double localHumidityBonus = 0.0;
        double catalystBonus = 0.0;

        BlockPos.Mutable mutable = new BlockPos.Mutable();
        int cx = pos.getX();
        int cy = pos.getY();
        int cz = pos.getZ();

        // 2. Optimized Unified Single-Pass Scan for Catalysts, Mold & Water
        int r = config.general.scan_radius;
        int wr = config.environment.water_scan_radius;
        int maxR = Math.max(r, wr);

        int waterBlocksFound = 0;
        int maxWaterBlocksNeeded = (int) Math
                .ceil(config.environment.max_local_humidity_bonus / Math.max(0.001, config.environment.water_adjacent_bonus));

        int lastChunkX = Integer.MIN_VALUE;
        int lastChunkZ = Integer.MIN_VALUE;
        boolean isChunkLoaded = true;
        World realWorld = (world instanceof World w) ? w : null;

        for (int x = -maxR; x <= maxR; x++) {
            int dx = Math.abs(x);
            for (int y = -maxR; y <= maxR; y++) {
                int dy = Math.abs(y);
                for (int z = -maxR; z <= maxR; z++) {
                    if (x == 0 && y == 0 && z == 0) {
                        continue;
                    }
                    int dz = Math.abs(z);

                    boolean inCatalystRange = (dx <= r && dy <= r && dz <= r);
                    boolean inWaterRange = (dx <= wr && dy <= wr && dz <= wr);
                    boolean needWater = inWaterRange && (waterBlocksFound < maxWaterBlocksNeeded);

                    // Skip blocks outside catalyst range if water cap is already reached
                    if (!inCatalystRange && !needWater) {
                        continue;
                    }

                    int posX = cx + x;
                    int posZ = cz + z;
                    int chunkX = posX >> 4;
                    int chunkZ = posZ >> 4;

                    // Micro-cached chunk lookup: only check when crossing chunk boundaries
                    if (realWorld != null && (chunkX != lastChunkX || chunkZ != lastChunkZ)) {
                        lastChunkX = chunkX;
                        lastChunkZ = chunkZ;
                        isChunkLoaded = (realWorld.getChunk(chunkX, chunkZ, ChunkStatus.FULL, false) != null);
                    }

                    if (!isChunkLoaded) {
                        continue;
                    }

                    mutable.set(posX, cy + y, posZ);
                    BlockState nearbyState = world.getBlockState(mutable);

                    // A) Organic catalysts and adjacent mold scan (in radius r)
                    if (inCatalystRange) {
                        ModCatalystRegistry.CatalystContribution contribution = ModCatalystRegistry
                                .getContribution(nearbyState, config);
                        localHumidityBonus += contribution.localHumidityBonus();
                        catalystBonus += contribution.catalystBonus();

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

                    // B) Water scan (in radius wr, early-stops once max water is found)
                    if (needWater) {
                        var fluidState = nearbyState.getFluidState();
                        if (!fluidState.isEmpty()) {
                            if (fluidState.isOf(Fluids.WATER) || fluidState.isOf(Fluids.FLOWING_WATER)) {
                                localHumidityBonus += config.environment.water_adjacent_bonus;
                                waterBlocksFound++;
                            }
                        }
                    }
                }
            }
        }

        localHumidityBonus = Math.min(config.environment.max_local_humidity_bonus, localHumidityBonus);
        double Hraw = baseHum + depthModifier + localHumidityBonus;

        // 4. Aeration & Miasma Evaluation over exposed air faces
        BlockAirEvaluation airEval = MiasmaCalculator.calculateBlockAirEvaluation(world, pos, stateToCheck);

        double aerationFlow = airEval.ventilationFlow();
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

        // 5. Light Sampling (Luv)
        int totalLight = 0;
        int samplePoints = 6;
        for (Direction dir : DIRECTIONS) {
            mutable.set(pos, dir);
            int skyLight = world.getLightLevel(LightType.SKY, mutable);
            int blockLight = world.getLightLevel(LightType.BLOCK, mutable);
            totalLight += Math.max(skyLight, blockLight);
        }
        if (stateToCheck == null || !stateToCheck.isOpaqueFullCube(world, pos)) {
            int selfSky = world.getLightLevel(LightType.SKY, pos);
            int selfBlock = world.getLightLevel(LightType.BLOCK, pos);
            totalLight += Math.max(selfSky, selfBlock);
            samplePoints = 7;
        }

        double avgLight = totalLight / (double) samplePoints;
        double Luv = Math.max(0.0, (15.0 - avgLight) / 15.0);

        // 6. Material Susceptibility (Smat) - memoized O(1) lookup
        double Smat = getMaterialSusceptibility(stateToCheck != null ? stateToCheck.getBlock() : null, config);

        // 7. Final Mold Risk calculation
        double R = ((Heff * Luv * Smat) + catalystBonus + miasmaBonus) * Tmult;

        return new MoldRiskResult(Tmult, Heff, Hraw, baseHum, depthModifier, localHumidityBonus, aerationFlow, aeration,
                aerationDryingBonus, Luv, avgLight, Smat, catalystBonus, miasmaBonus, airEval.averageNetMiasma(),
                airEval.maxVolume(), airEval.exposedFacesCount(), R, temp, surfaceTemp, airEval.distanceToVentilation());
    }
}
