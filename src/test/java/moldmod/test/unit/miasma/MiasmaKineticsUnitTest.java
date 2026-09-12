package moldmod.test.unit.miasma;

import me.shedaniel.autoconfig.AutoConfig;
import moldmod.config.ModConfig;
import moldmod.atmosphere.BFSExplorer;
import moldmod.atmosphere.RoomAtmosphereCalculator.AirToxicityLevel;
import moldmod.atmosphere.RoomAtmosphereCalculator.MiasmaResult;
import moldmod.atmosphere.RoomSaturationManager;
import net.minecraft.util.math.BlockPos;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class MiasmaKineticsUnitTest {

    @BeforeAll
    public static void setup() {
        try {
            AutoConfig.register(ModConfig.class, (cfg, clazz) -> new me.shedaniel.autoconfig.serializer.ConfigSerializer<ModConfig>() {
                private final ModConfig configInstance = new ModConfig();
                @Override public void serialize(ModConfig data) {}
                @Override public ModConfig deserialize() { return configInstance; }
                @Override public ModConfig createDefault() { return configInstance; }
            });
        } catch (RuntimeException ignored) {
            // Already registered
        }
    }

    @Test
    @DisplayName("Verify exponential transition formula for gradual dissipation")
    public void testDissipationExponentialFormula() {
        double initialMiasma = 10.0;
        double targetMiasma = 0.0;
        double dissipationAlpha = 0.35;
        int checkInterval = 20;

        // After 20 ticks (1 step)
        long elapsedTicks1 = 20;
        double steps1 = elapsedTicks1 / (double) checkInterval;
        double factor1 = 1.0 - Math.pow(1.0 - dissipationAlpha, steps1);
        double result1 = initialMiasma + factor1 * (targetMiasma - initialMiasma);
        assertEquals(6.5, result1, 0.001, "After 1 step (20t), miasma should reduce by 35% from 10.0 to 6.5");

        // After 40 ticks (2 steps)
        long elapsedTicks2 = 40;
        double steps2 = elapsedTicks2 / (double) checkInterval;
        double factor2 = 1.0 - Math.pow(1.0 - dissipationAlpha, steps2);
        double result2 = initialMiasma + factor2 * (targetMiasma - initialMiasma);
        assertEquals(4.225, result2, 0.001, "After 2 steps (40t), miasma should be 10.0 * (0.65)^2 = 4.225");

        // After 100 ticks (5 steps)
        long elapsedTicks5 = 100;
        double steps5 = elapsedTicks5 / (double) checkInterval;
        double factor5 = 1.0 - Math.pow(1.0 - dissipationAlpha, steps5);
        double result5 = initialMiasma + factor5 * (targetMiasma - initialMiasma);
        assertEquals(1.160, result5, 0.001, "After 5 steps (100t), miasma should be 10.0 * (0.65)^5 = 1.160");
    }

    @Test
    @DisplayName("Verify exponential transition formula for gradual saturation")
    public void testSaturationExponentialFormula() {
        double initialMiasma = 0.0;
        double targetMiasma = 10.0;
        double saturationAlpha = 0.15;
        int checkInterval = 20;

        // After 20 ticks (1 step)
        long elapsedTicks1 = 20;
        double steps1 = elapsedTicks1 / (double) checkInterval;
        double factor1 = 1.0 - Math.pow(1.0 - saturationAlpha, steps1);
        double result1 = initialMiasma + factor1 * (targetMiasma - initialMiasma);
        assertEquals(1.5, result1, 0.001, "After 1 step (20t), miasma should reach 15% of target = 1.5");

        // After 40 ticks (2 steps)
        long elapsedTicks2 = 40;
        double steps2 = elapsedTicks2 / (double) checkInterval;
        double factor2 = 1.0 - Math.pow(1.0 - saturationAlpha, steps2);
        double result2 = initialMiasma + factor2 * (targetMiasma - initialMiasma);
        assertEquals(2.775, result2, 0.001, "After 2 steps (40t), miasma should reach 2.775");
    }

    @Test
    @DisplayName("Verify spatial conductance weight computation")
    public void testConductanceWeights() {
        double alpha = 0.25;

        // Distance 0 (at the opening / exit)
        assertEquals(1.0, BFSExplorer.computeConductanceWeight(0, alpha), 1e-6);

        // Distance 1 (1 block away)
        assertEquals(1.0 / (1.0 + 0.25 * 1), BFSExplorer.computeConductanceWeight(1, alpha), 1e-6); // 0.80

        // Distance 4 (4 blocks away)
        assertEquals(1.0 / (1.0 + 0.25 * 4), BFSExplorer.computeConductanceWeight(4, alpha), 1e-6); // 0.50

        // Distance >= 900 (unreachable)
        assertEquals(0.0, BFSExplorer.computeConductanceWeight(999, alpha), 1e-6);
    }

    @Test
    @DisplayName("Verify deterministic anchor calculation on air block sets")
    public void testDeterministicAnchorCalculation() {
        Set<BlockPos> airBlocks = new HashSet<>();
        airBlocks.add(new BlockPos(5, 2, 4));
        airBlocks.add(new BlockPos(2, 1, 3));
        airBlocks.add(new BlockPos(7, 3, 9));
        airBlocks.add(new BlockPos(1, 1, 1));
        airBlocks.add(new BlockPos(3, 2, 2));

        BlockPos anchor1 = RoomSaturationManager.calculateAnchor(airBlocks, new BlockPos(5, 2, 4));
        BlockPos anchor2 = RoomSaturationManager.calculateAnchor(airBlocks, new BlockPos(7, 3, 9));

        assertEquals(new BlockPos(1, 1, 1), anchor1, "Anchor must always be the deterministic min BlockPos");
        assertEquals(anchor1, anchor2, "Querying different default positions in the same room must return the same anchor");
    }

    @Test
    @DisplayName("Verify toxic level thresholds classification")
    public void testToxicityLevelThresholds() {
        // 1. Clean air (M=0)
        MiasmaResult rClean = new MiasmaResult(0.0, 0.0, false, 20, Set.of());
        assertEquals(AirToxicityLevel.CLEAN, rClean.level);

        // 2. Warning level (M=2.0, D=0.1)
        MiasmaResult rWarning = new MiasmaResult(2.0, 0.0, false, 20, Set.of());
        assertEquals(AirToxicityLevel.WARNING, rWarning.level);

        // 3. Hunger level (M=6.0, D=0.3)
        MiasmaResult rHunger = new MiasmaResult(6.0, 0.0, false, 20, Set.of());
        assertEquals(AirToxicityLevel.MODERATE_HUNGER, rHunger.level);

        // 4. Poison level (M=18.0, D=0.18)
        MiasmaResult rPoison = new MiasmaResult(18.0, 0.0, false, 100, Set.of());
        assertEquals(AirToxicityLevel.LETHAL_POISON, rPoison.level);
    }

    @Test
    @DisplayName("Verify Air Purifier cleaning bonus, target miasma reduction, and multi-purifier scaling")
    public void testAirPurifierCleaningBonusAndTargetMiasma() {
        Set<BlockPos> airBlocks = Set.of(new BlockPos(0, 60, 0), new BlockPos(0, 61, 0));
        Set<BlockPos> onePurifier = Set.of(new BlockPos(1, 60, 0));
        Set<BlockPos> twoPurifiers = Set.of(new BlockPos(1, 60, 0), new BlockPos(2, 60, 0));
        Set<BlockPos> threePurifiers = Set.of(new BlockPos(1, 60, 0), new BlockPos(2, 60, 0), new BlockPos(3, 60, 0));

        // Case A: 1 Purifier cleans 8.0 toxic load to 0.0
        MiasmaResult rCleaned = new MiasmaResult(null, 8.0, 0.0, false, 2, airBlocks, BlockPos.ORIGIN,
                999, 0.0, 0, 1.0, null, null, false, Set.of(), Set.of(), Set.of(), onePurifier);
        assertEquals(1, rCleaned.roomPurifierCount);
        assertEquals(48.0, rCleaned.purifierCleaningBonus, 1e-4);
        assertEquals(0.0, rCleaned.targetMiasma, 1e-4, "Purifier power (48) > toxic load (8) -> target miasma must be 0.0");

        // Case B: High mold load (70.0), 1 Purifier reduces load to 22.0 (accumulation still occurs)
        MiasmaResult rHighMold1 = new MiasmaResult(null, 70.0, 0.0, false, 2, airBlocks, BlockPos.ORIGIN,
                999, 0.0, 0, 1.0, null, null, false, Set.of(), Set.of(), Set.of(), onePurifier);
        assertEquals(48.0, rHighMold1.purifierCleaningBonus, 1e-4);
        assertEquals(22.0, rHighMold1.targetMiasma, 1e-4, "High load (70) - Purifier (48) = 22.0");

        // Case C: High mold load (100.0), 2 Purifiers reduce load to 4.0
        MiasmaResult rHighMold2 = new MiasmaResult(null, 100.0, 0.0, false, 2, airBlocks, BlockPos.ORIGIN,
                999, 0.0, 0, 1.0, null, null, false, Set.of(), Set.of(), Set.of(), twoPurifiers);
        assertEquals(96.0, rHighMold2.purifierCleaningBonus, 1e-4);
        assertEquals(4.0, rHighMold2.targetMiasma, 1e-4, "High load (100) - 2 Purifiers (96) = 4.0");

        // Case D: High mold load (100.0), 3 Purifiers overcome production (144 >= 100 -> 0.0)
        MiasmaResult rHighMold3 = new MiasmaResult(null, 100.0, 0.0, false, 2, airBlocks, BlockPos.ORIGIN,
                999, 0.0, 0, 1.0, null, null, false, Set.of(), Set.of(), Set.of(), threePurifiers);
        assertEquals(144.0, rHighMold3.purifierCleaningBonus, 1e-4);
        assertEquals(0.0, rHighMold3.targetMiasma, 1e-4, "3 Purifiers (144) >= High load (100) -> target miasma is 0.0");

        // Case E: Open air always has target miasma 0.0
        MiasmaResult rOpen = new MiasmaResult(null, 25.0, 0.0, true, 2, airBlocks, BlockPos.ORIGIN,
                0, 0.0, 0, 1.0, null, null, false, Set.of(), Set.of(), Set.of(), onePurifier);
        assertEquals(0.0, rOpen.targetMiasma, 1e-4, "Open air target miasma is always 0.0");
    }
}
