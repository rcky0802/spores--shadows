package moldmod.structure;

import me.shedaniel.autoconfig.AutoConfig;
import moldmod.block.ModBlocks;
import moldmod.block.MoldyBlock;
import moldmod.block.MoldyBlockHelper;
import moldmod.config.ModConfig;
import moldmod.risk.MoldRiskCalculator;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;

import java.util.Random;

public final class MoldyStructureContext {

    private MoldyStructureContext() {
    }

    private static final ThreadLocal<String> CURRENT_STRUCTURE = new ThreadLocal<>();
    private static final Direction[] DIRECTIONS = Direction.values();

    @FunctionalInterface
    public interface StructureScope extends AutoCloseable {
        @Override
        void close();
    }

    private static final StructureScope NOOP_SCOPE = () -> {};

    public static StructureScope open(String id) {
        if (id == null) return NOOP_SCOPE;
        CURRENT_STRUCTURE.set(id);
        return CURRENT_STRUCTURE::remove;
    }

    public static void setStructure(String id) {
        CURRENT_STRUCTURE.set(id);
    }

    public static void clear() {
        CURRENT_STRUCTURE.remove();
    }

    public static BlockState processBlock(BlockState state, BlockPos pos, StructureWorldAccess world) {
        String structureId = CURRENT_STRUCTURE.get();
        if (structureId == null)
            return state;

        if (!isConvertible(state))
            return state;

        // For doors, ensure both halves use the exact same position and random seed for
        // calculations to prevent them from getting different states and breaking instantly.
        BlockPos basePos = pos;
        if (state.contains(DoorBlock.HALF) && state.get(DoorBlock.HALF) == DoubleBlockHalf.UPPER) {
            basePos = pos.down();
        }

        // Use a deterministic random based on the base position and world seed
        Random random = new Random(basePos.asLong() ^ world.getSeed());
        double r = random.nextDouble() * 100.0;

        int moldy = 0;
        int tainted = 0;
        int rotten = 0;

        ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();

        // Category 1: Degrado Critico
        if (structureId.contains("shipwreck") || structureId.contains("swamp_hut")) {
            rotten = config.structures.cat1_critical.rotten_chance;
            tainted = config.structures.cat1_critical.tainted_chance;
            moldy = config.structures.cat1_critical.moldy_chance;
        }
        // Category 2: Degrado Alto
        else if (structureId.contains("mineshaft") || structureId.contains("village") && structureId.contains("zombie")
                || structureId.contains("trail_ruins")) {
            rotten = config.structures.cat2_high.rotten_chance;
            tainted = config.structures.cat2_high.tainted_chance;
            moldy = config.structures.cat2_high.moldy_chance;
        }
        // Category 3: Degrado Moderato
        else if (structureId.contains("pillager_outpost") || structureId.contains("ruined_portal")) {
            rotten = config.structures.cat3_moderate.rotten_chance;
            tainted = config.structures.cat3_moderate.tainted_chance;
            moldy = config.structures.cat3_moderate.moldy_chance;
        }
        // Category 4: Degrado Minimo
        else if (structureId.contains("village") || structureId.contains("mansion")) {
            rotten = config.structures.cat4_low.rotten_chance;
            tainted = config.structures.cat4_low.tainted_chance;
            moldy = config.structures.cat4_low.moldy_chance;
        }

        // Apply dynamic modifiers based on environment
        boolean isUnderwater = false;
        boolean hasSkyAccess = false;

        for (Direction dir : DIRECTIONS) {
            if (world.getBlockState(basePos.offset(dir)).isOf(Blocks.WATER)) {
                isUnderwater = true;
                break;
            }
        }

        // Approximate sky access check (if above Y=60 and relatively high up in the local terrain)
        if (basePos.getY() > 60 && world.isSkyVisible(basePos.up())) {
            hasSkyAccess = true;
        }

        // Check if near ground (y <= surface approx) or near water
        boolean nearGround = false;
        for (int dy = -1; dy >= -2; dy--) {
            BlockState below = world.getBlockState(basePos.add(0, dy, 0));
            if (below.isOf(Blocks.GRASS_BLOCK) || below.isOf(Blocks.DIRT) || below.isOf(Blocks.WATER)
                    || below.isOf(Blocks.STONE)) {
                nearGround = true;
                break;
            }
        }

        // Rule: If touching water or deep underground (Y < 60), increase Rotten/Tainted
        ModConfig cfg = null;
        try {
            cfg = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        } catch (Exception ignored) {}
        int uwRottenBonus = (cfg != null && cfg.structures != null) ? cfg.structures.underwater_rotten_bonus : 20;
        int uwTaintedBonus = (cfg != null && cfg.structures != null) ? cfg.structures.underwater_tainted_bonus : 20;
        int ugMoldyBonus = (cfg != null && cfg.structures != null) ? cfg.structures.underground_moldy_bonus : 15;
        int gcMoldyBonus = (cfg != null && cfg.structures != null) ? cfg.structures.ground_contact_moldy_bonus : 20;
        int saRottenToMoldy = (cfg != null && cfg.structures != null) ? cfg.structures.sky_access_rotten_to_moldy : 15;
        int saMoldyBonus = (cfg != null && cfg.structures != null) ? cfg.structures.sky_access_moldy_bonus : 25;

        if (isUnderwater || basePos.getY() <= 60) {
            rotten = Math.min(100, rotten + uwRottenBonus);
            tainted = Math.min(100 - rotten, tainted + uwTaintedBonus);
            if (!isUnderwater && basePos.getY() <= 60 && moldy < 35) {
                moldy = Math.min(100 - rotten - tainted, moldy + ugMoldyBonus);
            }
        }

        // Rule: If near ground, boost moldy heavily (simulating rising damp)
        if (nearGround) {
            moldy = Math.min(100 - rotten - tainted, moldy + gcMoldyBonus);
        }

        // Rule: If exposed to air/rain (sky access), shift towards Moldy
        if (hasSkyAccess && !isUnderwater) {
            // Convert some Rotten/Tainted back to Moldy since fresh air preserves it slightly from rotting completely
            int recovered = Math.min(rotten, saRottenToMoldy);
            rotten -= recovered;
            moldy += recovered;

            // Boost Moldy heavily
            moldy = Math.min(100 - rotten - tainted, moldy + saMoldyBonus);
        }

        // --- INFLUENCE BY PLAYER RISK FORMULA (R) ---
        double R = calculateStructureR(world, basePos, state, isUnderwater, hasSkyAccess, config);
        if (R > 0.8) {
            rotten = Math.min(100, rotten + (int) (R * 15));
            tainted = Math.min(100 - rotten, tainted + 15);
        } else if (R > 0.5) {
            moldy = Math.min(100, moldy + 15);
            tainted = Math.min(100 - moldy, tainted + 10);
        } else if (R < 0.2) {
            rotten = Math.max(0, rotten - 15);
            tainted = Math.max(0, tainted - 15);
            moldy = Math.max(0, moldy - 15);
        }
        // ---------------------------------------------

        if (r < rotten) {
            return getConvertedState(state, 3);
        } else if (r < rotten + moldy) {
            return getConvertedState(state, 2);
        } else if (r < rotten + moldy + tainted) {
            return getConvertedState(state, 1);
        }

        return getConvertedState(state, 0);
    }

    private static boolean isConvertible(BlockState state) {
        return ModBlocks.VANILLA_TO_MOLDY.containsKey(state.getBlock());
    }

    private static BlockState getConvertedState(BlockState original, int stage) {
        if (ModBlocks.VANILLA_TO_MOLDY.containsKey(original.getBlock())) {
            Block moldyBlock = ModBlocks.VANILLA_TO_MOLDY.get(original.getBlock());
            BlockState newState = MoldyBlockHelper.copyMatchingProperties(original, moldyBlock.getDefaultState());

            if (newState.contains(MoldyBlock.STAGE)) {
                newState = newState.with(MoldyBlock.STAGE, stage);
            }

            if (newState.contains(MoldyBlock.STRUCTURAL)) {
                newState = newState.with(MoldyBlock.STRUCTURAL, true);
            }

            return newState;
        }
        return original;
    }

    private static double calculateStructureR(StructureWorldAccess world, BlockPos pos, BlockState state,
            boolean isUnderwater, boolean hasSkyAccess, ModConfig config) {
        float surfaceTemp = 0.8f;
        boolean hasRain = false;
        try {
            var biomeEntry = world.getBiome(pos);
            surfaceTemp = biomeEntry.value().getTemperature();
            hasRain = biomeEntry.value().hasPrecipitation();
            if (biomeEntry.isIn(BiomeTags.IS_NETHER) || biomeEntry.isIn(BiomeTags.IS_END)) {
                return 0.0;
            }
        } catch (Exception ignored) {}

        float temp = surfaceTemp;

        // Depth-based temperature normalization
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

        // Temperature survival check
        double Tmult = (temp >= config.environment.min_temperature_survival
                && temp <= config.environment.max_temperature_survival) ? 1.0 : 0.0;
        if (Tmult == 0.0) {
            return 0.0;
        }

        // Humidity
        double baseHum = hasRain ? config.environment.rain_humidity_base : config.environment.dry_humidity_base;
        double depth = 0.0;
        if (pos.getY() < config.environment.cave_start_y) {
            depth = Math.min(config.environment.max_depth_modifier,
                    (config.environment.cave_start_y - pos.getY()) * config.environment.depth_modifier_per_level);
        }
        double humidity = isUnderwater ? 1.0 : Math.min(1.0, baseHum + depth);

        // Light approximation (underground/dark vs open sky)
        double Luv = hasSkyAccess ? 0.3 : 1.0;

        // Material susceptibility
        double Smat = MoldRiskCalculator.getMaterialSusceptibility(state.getBlock(), config);

        return humidity * Luv * Smat * Tmult;
    }
}
