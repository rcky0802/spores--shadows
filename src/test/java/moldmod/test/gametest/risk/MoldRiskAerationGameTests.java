package moldmod.test.gametest.risk;

import moldmod.block.ModBlocks;
import moldmod.block.MoldRiskCalculator;
import moldmod.block.MoldRiskCalculator.MoldRiskResult;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public class MoldRiskAerationGameTests {

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testAerationDryingOpenAir(TestContext context) {
        BlockPos center = new BlockPos(2, 2, 2);
        BlockState log = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState();
        context.setBlockState(center, log);

        MoldRiskResult result = MoldRiskCalculator.calculate(context.getWorld(), context.getAbsolutePos(center), false,
                log);
        if (result.aeration() != 1.0) {
            context.throwPositionedException("All'aperto l'areazione deve essere 1.0, trovato: " + result.aeration(),
                    center);
        }
        if (result.aerationDryingBonus() <= 0.0) {
            context.throwPositionedException(
                    "All'aperto il bonus di asciugatura deve essere > 0.0, trovato: " + result.aerationDryingBonus(),
                    center);
        }
        if (result.Heff() >= result.Hraw() && result.Hraw() > 0.0) {
            context.throwPositionedException("L'areazione dovrebbe ridurre Heff rispetto a Hraw!", center);
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testSealedCellNoAeration(TestContext context) {
        // Costruiamo una cella sigillata 3x3x3 di pietra attorno al centro
        for (int x = 1; x <= 3; x++) {
            for (int y = 1; y <= 3; y++) {
                for (int z = 1; z <= 3; z++) {
                    context.setBlockState(new BlockPos(x, y, z), Blocks.STONE.getDefaultState());
                }
            }
        }
        // Il blocco al centro (2, 2, 2) è circondato da pietra solida su tutte le 6 facce
        BlockPos center = new BlockPos(2, 2, 2);
        BlockState log = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState();
        context.setBlockState(center, log);

        MoldRiskResult result = MoldRiskCalculator.calculate(context.getWorld(), context.getAbsolutePos(center), false,
                log);
        if (result.aeration() != 0.0) {
            context.throwPositionedException(
                    "In una cella completamente sigillata l'areazione deve essere 0.0, trovato: " + result.aeration(),
                    center);
        }
        if (result.aerationDryingBonus() != 0.0) {
            context.throwPositionedException("In una cella sigillata il bonus asciugatura deve essere 0.0, trovato: "
                    + result.aerationDryingBonus(), center);
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testVentilationGapsIncreasesAerationAndReducesHumidity(TestContext context) {
        // Stanza 3x3x3 in pietra
        for (int x = 1; x <= 3; x++) {
            for (int y = 1; y <= 3; y++) {
                for (int z = 1; z <= 3; z++) {
                    context.setBlockState(new BlockPos(x, y, z), Blocks.STONE.getDefaultState());
                }
            }
        }
        BlockPos center = new BlockPos(2, 2, 2);
        context.setBlockState(center, Blocks.AIR.getDefaultState());

        BlockPos targetPos = new BlockPos(3, 2, 2);
        BlockState targetLog = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState();
        context.setBlockState(targetPos, targetLog);

        // Misura in stanza chiusa sigillata
        MoldRiskResult rSealed = MoldRiskCalculator.calculate(context.getWorld(), context.getAbsolutePos(targetPos),
                false, targetLog);

        // Aggiungi fessura/staccionata comunicante con l'esterno su parete opposta (1, 2, 2) con aria sopra (1, 3, 2)
        context.setBlockState(new BlockPos(1, 2, 2), Blocks.IRON_BARS.getDefaultState());
        context.setBlockState(new BlockPos(1, 3, 2), Blocks.AIR.getDefaultState());

        MoldRiskResult rVented = MoldRiskCalculator.calculate(context.getWorld(), context.getAbsolutePos(targetPos),
                false, targetLog);

        if (rVented.aeration() <= rSealed.aeration()) {
            context.throwPositionedException("Le sbarre di ferro verso l'esterno dovrebbero aumentare l'areazione!",
                    targetPos);
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testExposedFacesAerationAveraging(TestContext context) {
        // Costruiamo una parete divisoria con blocco bersaglio a (2, 2, 2)
        // Lato Ovest (X=1): stanza sigillata chiusa (Aer = 0.0)
        // Lato Est (X=3): all'aperto / cielo (Aer = 1.0)
        // Sopra, sotto, nord, sud: pietra solida
        for (int x = 0; x <= 2; x++) {
            for (int y = 1; y <= 3; y++) {
                for (int z = 1; z <= 3; z++) {
                    context.setBlockState(new BlockPos(x, y, z), Blocks.STONE.getDefaultState());
                }
            }
        }
        // Interno stanza ovest a (1, 2, 2): aria sigillata
        context.setBlockState(new BlockPos(1, 2, 2), Blocks.AIR.getDefaultState());

        // Blocco bersaglio al centro della parete a (2, 2, 2)
        BlockPos targetPos = new BlockPos(2, 2, 2);
        BlockState log = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState();
        context.setBlockState(targetPos, log);

        // Esterno a est (3, 2, 2) è aria aperta sotto il cielo
        context.setBlockState(new BlockPos(3, 2, 2), Blocks.AIR.getDefaultState());

        MoldRiskResult result = MoldRiskCalculator.calculate(context.getWorld(), context.getAbsolutePos(targetPos),
                false, log);

        // Il blocco ha 2 facce esposte: 1 a est (Aer=1.0) e 1 a ovest (Aer=0.0)
        if (result.exposedFaces() != 2) {
            context.throwPositionedException("Facce esposte attese: 2, trovate: " + result.exposedFaces(), targetPos);
        }
        // La media dell'areazione deve essere (1.0 + 0.0) / 2 = 0.50
        if (Math.abs(result.aeration() - 0.50) > 0.05) {
            context.throwPositionedException(
                    "L'areazione media attesa su 2 facce (aperta e chiusa) è ~0.50, trovata: " + result.aeration(),
                    targetPos);
        }

        context.complete();
    }
}
