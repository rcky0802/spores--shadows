package moldmod.risk;

import me.shedaniel.autoconfig.AutoConfig;
import moldmod.atmosphere.RoomAtmosphereCalculator;
import moldmod.atmosphere.RoomAtmosphereCalculator.BlockAirEvaluation;
import moldmod.block.MoldyBlock;
import moldmod.block.MoldyLogBlock;
import moldmod.block.MoldyPlanksBlock;
import moldmod.config.ModConfig;
import moldmod.registry.ModCatalystRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.registry.tag.BlockTags;
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
        
        BlockState state = block.getDefaultState();
        String name = Registries.BLOCK.getId(block).getPath();
        double smat = config.susceptibility.default_multiplier;
        
        if (state.isIn(BlockTags.PLANKS) || block instanceof MoldyPlanksBlock) {
            smat = config.susceptibility.planks_multiplier;
        } else if ((state.isIn(BlockTags.LOGS) || block instanceof MoldyLogBlock) && name.contains("stripped")) {
            smat = config.susceptibility.stripped_wood_multiplier;
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
            int distanceToVentilation,
            double targetHumidity,
            double currentHumidity,
            int roomWaterSourcesCount,
            boolean isWaterlogged,
            RoomAtmosphereCalculator.RoomVentilationType roomVentilationType,
            BlockPos anchorPos,
            int dehumidifierCount,
            double dehumidifierDryingBonus,
            int humidifierCount,
            double humidifierMoistureBonus,
            int purifierCount,
            double purifierCleaningBonus) {

        public MoldRiskResult(double Tmult, double Heff, double Hraw, double baseHum, double depthModifier,
                double localHumidityBonus, double aerationFlow, double aeration, double aerationDryingBonus, double Luv, double avgLight,
                double Smat, double catalystBonus, double miasmaBonus, double netMiasma, int airVolume, int exposedFaces,
                double R, float effectiveTemp, float surfaceTemp, int distanceToVentilation, double targetHumidity,
                double currentHumidity, int roomWaterSourcesCount, boolean isWaterlogged,
                RoomAtmosphereCalculator.RoomVentilationType roomVentilationType, BlockPos anchorPos) {
            this(Tmult, Heff, Hraw, baseHum, depthModifier, localHumidityBonus, aerationFlow, aeration, aerationDryingBonus,
                    Luv, avgLight, Smat, catalystBonus, miasmaBonus, netMiasma, airVolume, exposedFaces, R, effectiveTemp, surfaceTemp,
                    distanceToVentilation, targetHumidity, currentHumidity, roomWaterSourcesCount, isWaterlogged,
                    roomVentilationType, anchorPos, 0, 0.0, 0, 0.0, 0, 0.0);
        }

        public MoldRiskResult(double Tmult, double Heff, double Hraw, double baseHum, double depthModifier,
                double localHumidityBonus, double aerationFlow, double aeration, double aerationDryingBonus, double Luv, double avgLight,
                double Smat, double catalystBonus, double miasmaBonus, double netMiasma, int airVolume, int exposedFaces,
                double R, float effectiveTemp, float surfaceTemp, int distanceToVentilation) {
            this(Tmult, Heff, Hraw, baseHum, depthModifier, localHumidityBonus, aerationFlow, aeration, aerationDryingBonus,
                    Luv, avgLight, Smat, catalystBonus, miasmaBonus, netMiasma, airVolume, exposedFaces, R, effectiveTemp, surfaceTemp,
                    distanceToVentilation, Hraw, Heff, 0, false, RoomAtmosphereCalculator.RoomVentilationType.HERMETIC_SEALED, BlockPos.ORIGIN);
        }

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

        // If not a full World (e.g. ChunkRegion during worldgen), use a safe local evaluation without BFS
        if (!(world instanceof World)) {
            float surfaceTemp = 0.8f;
            boolean hasRain = false;
            try {
                var biomeEntry = world.getBiome(pos);
                surfaceTemp = biomeEntry.value().getTemperature();
                hasRain = biomeEntry.value().hasPrecipitation();
                if (biomeEntry.isIn(BiomeTags.IS_NETHER) || biomeEntry.isIn(BiomeTags.IS_END) ||
                        biomeEntry.matchesId(Identifier.of("minecraft", "the_end"))) {
                    return new MoldRiskResult(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0, 0, 0.0,
                            100.0f, 100.0f);
                }
            } catch (Exception ignored) {}

            float temp = surfaceTemp;
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
            }

            double Tmult = (temp >= config.environment.min_temperature_survival
                    && temp <= config.environment.max_temperature_survival) ? 1.0 : 0.0;
            if (Tmult == 0.0) {
                return new MoldRiskResult(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0, 0, 0.0,
                        temp, surfaceTemp);
            }

            boolean isWaterlogged = (stateToCheck != null) &&
                    ((stateToCheck.contains(net.minecraft.state.property.Properties.WATERLOGGED) && stateToCheck.get(net.minecraft.state.property.Properties.WATERLOGGED))
                    || stateToCheck.getFluidState().isIn(net.minecraft.registry.tag.FluidTags.WATER));

            double baseHum = hasRain ? config.environment.rain_humidity_base : config.environment.dry_humidity_base;
            double depthModifier = 0.0;
            if (pos.getY() < config.environment.cave_start_y) {
                depthModifier = Math.min(config.environment.max_depth_modifier,
                        (config.environment.cave_start_y - pos.getY()) * config.environment.depth_modifier_per_level);
            }

            double Heff = isWaterlogged ? 1.0 : Math.min(1.0, baseHum + depthModifier);
            double Hraw = Heff;
            boolean skyVisible = false;
            try {
                skyVisible = world.isSkyVisible(pos.up());
            } catch (Exception ignored) {}
            double Luv = skyVisible ? 0.3 : 1.0;
            double Smat = getMaterialSusceptibility(stateToCheck != null ? stateToCheck.getBlock() : null, config);
            double R = ((Heff * Luv * Smat)) * Tmult;

            return new MoldRiskResult(Tmult, Heff, Hraw, baseHum, depthModifier, 0.0, 0.0, 0.0, 0.0,
                    Luv, 0.0, Smat, 0.0, 0.0, 0.0, 1, 1, R, temp, surfaceTemp, 999,
                    Heff, Heff, 0, isWaterlogged,
                    skyVisible ? RoomAtmosphereCalculator.RoomVentilationType.CLEAN_OPEN_AIR : RoomAtmosphereCalculator.RoomVentilationType.HERMETIC_SEALED,
                    pos);
        }

        float surfaceTemp = 0.8f;
        boolean isNether = (world instanceof World w && w.getRegistryKey() == World.NETHER);
        boolean isEnd = (world instanceof World w && w.getRegistryKey() == World.END);

        try {
            var biomeEntry = world.getBiome(pos);
            surfaceTemp = biomeEntry.value().getTemperature();
            if (biomeEntry.isIn(BiomeTags.IS_NETHER)) isNether = true;
            if (biomeEntry.isIn(BiomeTags.IS_END) || biomeEntry.matchesId(Identifier.of("minecraft", "the_end"))) isEnd = true;
        } catch (Exception ignored) {}

        if (isNether) {
            return new MoldRiskResult(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0, 0, 0.0,
                    100.0f, 100.0f);
        }
        if (isEnd) {
            return new MoldRiskResult(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0, 0, 0.0,
                    -100.0f, -100.0f);
        }
        float temp = surfaceTemp;

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

        boolean isWaterlogged = (stateToCheck != null) &&
                ((stateToCheck.contains(net.minecraft.state.property.Properties.WATERLOGGED) && stateToCheck.get(net.minecraft.state.property.Properties.WATERLOGGED))
                || stateToCheck.getFluidState().isIn(net.minecraft.registry.tag.FluidTags.WATER));

        // 2. Scan for Organic Catalysts & Adjacent Mold
        double catalystBonus = 0.0;
        double catalystHumidityBonus = 0.0;
        int r = config.general.scan_radius;

        BlockPos.Mutable mutable = new BlockPos.Mutable();
        int cx = pos.getX();
        int cy = pos.getY();
        int cz = pos.getZ();

        int lastChunkX = Integer.MIN_VALUE;
        int lastChunkZ = Integer.MIN_VALUE;
        boolean isChunkLoaded = true;
        World realWorld = (world instanceof World w) ? w : null;

        for (int x = -r; x <= r; x++) {
            for (int y = -r; y <= r; y++) {
                for (int z = -r; z <= r; z++) {
                    if (x == 0 && y == 0 && z == 0) {
                        continue;
                    }

                    int posX = cx + x;
                    int posZ = cz + z;
                    int chunkX = posX >> 4;
                    int chunkZ = posZ >> 4;

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

                    ModCatalystRegistry.CatalystContribution contribution = ModCatalystRegistry
                            .getContribution(nearbyState, config);
                    catalystHumidityBonus += contribution.localHumidityBonus();
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
            }
        }

        // 3. Aeration, Room Atmosphere & Miasma Evaluation over exposed air faces
        BlockAirEvaluation airEval = RoomAtmosphereCalculator.calculateBlockAirEvaluation(world, pos, stateToCheck);

        double baseHum = airEval.baseHumidity();
        double depthModifier = airEval.depthModifier();
        double roomWaterBonus;
        int roomWaterSourcesCount;
        double targetHumidity;
        double currentHumidity;
        double aerationFlow;
        double aeration;
        double aerationDryingBonus;
        double Heff;
        double Hraw;

        if (isWaterlogged) {
            baseHum = 1.0;
            depthModifier = 0.0;
            roomWaterBonus = config.environment.max_room_water_humidity_bonus;
            roomWaterSourcesCount = 1;
            targetHumidity = 1.0;
            currentHumidity = 1.0;
            Hraw = 1.0;
            aerationFlow = 0.0;
            aeration = 0.0;
            aerationDryingBonus = 0.0;
            Heff = 1.0;
        } else if (airEval.exposedFacesCount() == 0) {
            // Case 4: Fully buried block without air exposure
            roomWaterBonus = 0.0;
            roomWaterSourcesCount = 0;
            targetHumidity = Math.min(1.0, baseHum + depthModifier + catalystHumidityBonus);
            currentHumidity = targetHumidity;
            Hraw = targetHumidity;
            aerationFlow = 0.0;
            aeration = 0.0;
            aerationDryingBonus = 0.0;
            Heff = Math.max(0.0, Math.min(1.0, Hraw));
        } else {
            roomWaterBonus = airEval.waterBonus();
            roomWaterSourcesCount = airEval.waterSourcesCount();
            targetHumidity = Math.min(1.0, airEval.targetHumidity() + catalystHumidityBonus);
            currentHumidity = Math.min(1.0, airEval.currentHumidity() + catalystHumidityBonus);
            // Hraw rappresenta l'umidità grezza della stanza (inclusi i contributi ambientali e del nebulizzatore/umidificatore)
            Hraw = Math.max(0.0, Math.min(1.0, airEval.rawHumidity() + catalystHumidityBonus));

            aerationFlow = airEval.ventilationFlow();
            aeration = config.environment.enable_ventilation_drying ? airEval.averageAeration() : 0.0;
            aerationDryingBonus = aeration * config.environment.aeration_drying_bonus;
            // Heff è l'umidità effettiva risultante sia dall'eventuale deumidificatore (in currentHumidity) che dalla ventilazione
            Heff = Math.max(0.0, Math.min(1.0, currentHumidity - aerationDryingBonus));
        }

        double localHumidityBonus = roomWaterBonus + catalystHumidityBonus;

        double miasmaBonus = 0.0;
        if (config.environment.enable_miasma_spore_pressure && airEval.averageExposureIndex() > 0.0) {
            miasmaBonus = airEval.averageExposureIndex() * config.environment.miasma_spore_multiplier;
        }

        // 4. Light Sampling (Luv)
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

        // 5. Material Susceptibility (Smat) - memoized O(1) lookup
        double Smat = getMaterialSusceptibility(stateToCheck != null ? stateToCheck.getBlock() : null, config);

        // 6. Final Mold Risk calculation
        double R = ((Heff * Luv * Smat) + catalystBonus + miasmaBonus) * Tmult;

        return new MoldRiskResult(Tmult, Heff, Hraw, baseHum, depthModifier, localHumidityBonus, aerationFlow, aeration,
                aerationDryingBonus, Luv, avgLight, Smat, catalystBonus, miasmaBonus, airEval.averageNetMiasma(),
                airEval.maxVolume(), airEval.exposedFacesCount(), R, temp, surfaceTemp, airEval.distanceToVentilation(),
                targetHumidity, currentHumidity, roomWaterSourcesCount, isWaterlogged,
                airEval.primaryVentilationType(), airEval.anchorPos(),
                airEval.dehumidifierCount(), airEval.dehumidifierBonus(),
                airEval.humidifierCount(), airEval.humidifierBonus(),
                airEval.purifierCount(), airEval.purifierBonus());
    }
}
