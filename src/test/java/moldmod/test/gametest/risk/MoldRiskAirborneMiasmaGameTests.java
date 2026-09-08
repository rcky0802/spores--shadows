package moldmod.test.gametest.risk;

import moldmod.block.ModBlocks;
import moldmod.block.MoldRiskCalculator;
import moldmod.block.MoldRiskCalculator.MoldRiskResult;
import moldmod.block.MoldyLogBlock;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public class MoldRiskAirborneMiasmaGameTests {

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testAirMiasmaSporePressure(TestContext context) {
        // Stanza chiusa 5x4x5 in pietra
        for (int x = 0; x <= 4; x++) {
            for (int y = 0; y <= 3; y++) {
                for (int z = 0; z <= 4; z++) {
                    boolean isWall = (x == 0 || x == 4 || y == 0 || y == 3 || z == 0 || z == 4);
                    context.setBlockState(new BlockPos(x, y, z),
                            isWall ? Blocks.STONE.getDefaultState() : Blocks.AIR.getDefaultState());
                }
            }
        }

        // Blocco bersaglio pulito in una parete interna
        BlockPos targetPos = new BlockPos(1, 1, 1);
        BlockState cleanLog = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState();
        context.setBlockState(targetPos, cleanLog);

        // Blocco marcio posto a distanza 3 blocchi (non adiacente, quindi catalystBonus = 0)
        BlockPos distantRottenPos = new BlockPos(3, 1, 3);
        BlockState rottenLog = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState()
                .with(MoldyLogBlock.STAGE, 3);
        context.setBlockState(distantRottenPos, rottenLog);

        MoldRiskResult result = MoldRiskCalculator.calculate(context.getWorld(), context.getAbsolutePos(targetPos),
                false, cleanLog);

        if (result.miasmaBonus() <= 0.0) {
            context.throwPositionedException(
                    "In una stanza chiusa con legno marcio, il contagio aereo da miasma deve essere > 0.0, trovato: "
                            + result.miasmaBonus(),
                    targetPos);
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testWaxedAndRottenStage3EarlyExit(TestContext context) {
        BlockPos pos = new BlockPos(0, 64, 0);
        BlockState log = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState();
        BlockState rottenStage3 = log.with(MoldyLogBlock.STAGE, 3);

        // 1. Waxed = true -> R must be exactly 0.0
        MoldRiskResult rWaxed = MoldRiskCalculator.calculate(context.getWorld(), pos, true, log);
        if (rWaxed.R() != 0.0) {
            context.throwPositionedException("Il blocco cerato deve avere R = 0.0, trovato: " + rWaxed.R(), pos);
        }

        // 2. Stage = 3 (Rotten) -> R must be exactly 0.0
        MoldRiskResult rRotten = MoldRiskCalculator.calculate(context.getWorld(), pos, false, rottenStage3);
        if (rRotten.R() != 0.0) {
            context.throwPositionedException("Il blocco allo Stadio 3 deve avere R = 0.0, trovato: " + rRotten.R(),
                    pos);
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testMiasmaMitigationByVentilationFencesAndBars(TestContext context) {
        // Stanza chiusa 5x4x5 in pietra posizionata a x=1..5, y=0..3, z=1..5
        for (int x = 1; x <= 5; x++) {
            for (int y = 0; y <= 3; y++) {
                for (int z = 1; z <= 5; z++) {
                    boolean isWall = (x == 1 || x == 5 || y == 0 || y == 3 || z == 1 || z == 5);
                    context.setBlockState(new BlockPos(x, y, z),
                            isWall ? Blocks.STONE.getDefaultState() : Blocks.AIR.getDefaultState());
                }
            }
        }
        // Esterno a x=0 a cielo aperto
        for (int y = 0; y <= 3; y++) {
            for (int z = 1; z <= 5; z++) {
                context.setBlockState(new BlockPos(0, y, z), Blocks.AIR.getDefaultState());
            }
        }

        // Trave bersaglio pulita a (2, 1, 2)
        BlockPos targetPos = new BlockPos(2, 1, 2);
        BlockState cleanLog = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState();
        context.setBlockState(targetPos, cleanLog);

        // Blocco marcio a distanza (4, 1, 4) per generare miasma senza contatto diretto
        BlockState rottenLog = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState()
                .with(MoldyLogBlock.STAGE, 3);
        context.setBlockState(new BlockPos(4, 1, 4), rottenLog);

        // 1. In stanza sigillata: miasma presente, aerazione 0
        MoldRiskResult rSealed = MoldRiskCalculator.calculate(context.getWorld(), context.getAbsolutePos(targetPos),
                false, cleanLog);
        if (rSealed.miasmaBonus() <= 0.0) {
            context.throwPositionedException("In stanza chiusa il miasmaBonus deve essere > 0!", targetPos);
        }
        if (rSealed.aeration() != 0.0) {
            context.throwPositionedException("In stanza chiusa l'aeration deve essere 0.0!", targetPos);
        }

        // 2. Aggiungiamo staccionata e grata comunicanti con l'esterno a (1, 1, 2) e (1, 1, 3)
        context.setBlockState(new BlockPos(1, 1, 2), Blocks.OAK_FENCE.getDefaultState());
        context.setBlockState(new BlockPos(1, 1, 3), Blocks.IRON_BARS.getDefaultState());

        MoldRiskResult rVented = MoldRiskCalculator.calculate(context.getWorld(), context.getAbsolutePos(targetPos),
                false, cleanLog);
        if (rVented.aeration() <= rSealed.aeration()) {
            context.throwPositionedException("L'aerazione deve aumentare grazie a staccionate e grate!", targetPos);
        }
        if (rVented.miasmaBonus() >= rSealed.miasmaBonus()) {
            context.throwPositionedException("La ventilazione deve ridurre la pressione delle spore da miasma!",
                    targetPos);
        }
        if (rVented.R() >= rSealed.R()) {
            context.throwPositionedException(
                    "Il rischio R complessivo deve diminuire nella stanza ventilata! Sigillato: " + rSealed.R()
                            + ", Ventilato: " + rVented.R(),
                    targetPos);
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testDoorPurgesMiasmaAndReducesRisk(TestContext context) {
        // Stanza 5x4x5 in pietra posizionata a x=1..5, y=0..3, z=1..5
        for (int x = 1; x <= 5; x++) {
            for (int y = 0; y <= 3; y++) {
                for (int z = 1; z <= 5; z++) {
                    boolean isWall = (x == 1 || x == 5 || y == 0 || y == 3 || z == 1 || z == 5);
                    context.setBlockState(new BlockPos(x, y, z),
                            isWall ? Blocks.STONE.getDefaultState() : Blocks.AIR.getDefaultState());
                }
            }
        }
        // Esterno a x=0 a cielo aperto
        for (int y = 0; y <= 3; y++) {
            for (int z = 1; z <= 5; z++) {
                context.setBlockState(new BlockPos(0, y, z), Blocks.AIR.getDefaultState());
            }
        }

        // Trave bersaglio pulita a (2, 1, 2)
        BlockPos targetPos = new BlockPos(2, 1, 2);
        BlockState cleanLog = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState();
        context.setBlockState(targetPos, cleanLog);

        // Blocco marcio a distanza a (4, 1, 4)
        BlockState rottenLog = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState()
                .with(MoldyLogBlock.STAGE, 3);
        context.setBlockState(new BlockPos(4, 1, 4), rottenLog);

        // Porta a 2 blocchi su parete esterna a (1, 1, 3) e (1, 2, 3)
        BlockPos doorLower = new BlockPos(1, 1, 3);
        BlockPos doorUpper = new BlockPos(1, 2, 3);
        BlockState closedLower = Blocks.OAK_DOOR.getDefaultState()
                .with(net.minecraft.block.DoorBlock.HALF, net.minecraft.block.enums.DoubleBlockHalf.LOWER)
                .with(net.minecraft.block.DoorBlock.OPEN, false);
        BlockState closedUpper = Blocks.OAK_DOOR.getDefaultState()
                .with(net.minecraft.block.DoorBlock.HALF, net.minecraft.block.enums.DoubleBlockHalf.UPPER)
                .with(net.minecraft.block.DoorBlock.OPEN, false);
        context.setBlockState(doorLower, closedLower);
        context.setBlockState(doorUpper, closedUpper);

        // 1. A porta chiusa: miasma presente, aerazione 0, R elevato
        MoldRiskResult rClosed = MoldRiskCalculator.calculate(context.getWorld(), context.getAbsolutePos(targetPos),
                false, cleanLog);
        if (rClosed.miasmaBonus() <= 0.0) {
            context.throwPositionedException("A porta chiusa deve esserci miasmaBonus > 0!", targetPos);
        }

        // 2. A porta aperta verso l'esterno: miasma azzerato, aerazione massima (1.0), R crolla
        context.setBlockState(doorLower, closedLower.with(net.minecraft.block.DoorBlock.OPEN, true));
        context.setBlockState(doorUpper, closedUpper.with(net.minecraft.block.DoorBlock.OPEN, true));

        MoldRiskResult rOpen = MoldRiskCalculator.calculate(context.getWorld(), context.getAbsolutePos(targetPos),
                false, cleanLog);
        if (rOpen.miasmaBonus() != 0.0) {
            context.throwPositionedException(
                    "A porta aperta verso l'esterno il miasma deve essere azzerato (0.0), trovato: "
                            + rOpen.miasmaBonus(),
                    targetPos);
        }
        if (rOpen.aeration() <= 0.0) {
            context.throwPositionedException(
                    "A porta aperta verso l'esterno l'aerazione deve essere > 0, trovato: " + rOpen.aeration(),
                    targetPos);
        }
        if (rOpen.R() >= rClosed.R()) {
            context.throwPositionedException("Aprire la porta deve ridurre drasticamente il rischio R! Chiusa: "
                    + rClosed.R() + ", Aperta: " + rOpen.R(), targetPos);
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testTrapdoorCeilingPurgesMiasmaAndReducesRisk(TestContext context) {
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

        // Trave bersaglio a (3, 2, 2)
        BlockPos targetPos = new BlockPos(3, 2, 2);
        BlockState cleanLog = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState();
        context.setBlockState(targetPos, cleanLog);

        // Blocco marcio sulla parete opposta a (1, 2, 2)
        BlockState rottenLog = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState()
                .with(MoldyLogBlock.STAGE, 2);
        context.setBlockState(new BlockPos(1, 2, 2), rottenLog);

        // Botola sul soffitto a (2, 3, 2)
        BlockPos trapdoorPos = new BlockPos(2, 3, 2);
        BlockState closedTrapdoor = Blocks.OAK_TRAPDOOR.getDefaultState()
                .with(net.minecraft.block.TrapdoorBlock.HALF, net.minecraft.block.enums.BlockHalf.BOTTOM)
                .with(net.minecraft.block.TrapdoorBlock.OPEN, false);
        context.setBlockState(trapdoorPos, closedTrapdoor);

        // 1. Botola chiusa: miasma presente
        MoldRiskResult rClosed = MoldRiskCalculator.calculate(context.getWorld(), context.getAbsolutePos(targetPos),
                false, cleanLog);
        if (rClosed.miasmaBonus() <= 0.0) {
            context.throwPositionedException("A botola chiusa il miasmaBonus deve essere > 0!", targetPos);
        }

        // 2. Botola aperta verso il cielo: miasma dissipato, aerazione massima
        context.setBlockState(trapdoorPos, closedTrapdoor.with(net.minecraft.block.TrapdoorBlock.OPEN, true));
        MoldRiskResult rOpen = MoldRiskCalculator.calculate(context.getWorld(), context.getAbsolutePos(targetPos),
                false, cleanLog);
        if (rOpen.miasmaBonus() != 0.0) {
            context.throwPositionedException(
                    "A botola aperta verso il cielo il miasma deve dissiparsi (0.0), trovato: " + rOpen.miasmaBonus(),
                    targetPos);
        }
        if (rOpen.aeration() < 0.50) {
            context.throwPositionedException(
                    "A botola aperta verso il cielo l'aerazione deve essere elevata (> 0.50), trovato: " + rOpen.aeration(),
                    targetPos);
        }
        if (rOpen.R() >= rClosed.R()) {
            context.throwPositionedException("La botola aperta sul soffitto deve ridurre il rischio R!", targetPos);
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testWaxingRottenLogsEliminatesAirborneSporeRisk(TestContext context) {
        // Stanza chiusa 5x4x5 in pietra
        for (int x = 0; x <= 4; x++) {
            for (int y = 0; y <= 3; y++) {
                for (int z = 0; z <= 4; z++) {
                    boolean isWall = (x == 0 || x == 4 || y == 0 || y == 3 || z == 0 || z == 4);
                    context.setBlockState(new BlockPos(x, y, z),
                            isWall ? Blocks.STONE.getDefaultState() : Blocks.AIR.getDefaultState());
                }
            }
        }

        // Trave bersaglio pulita a (1, 1, 1)
        BlockPos targetPos = new BlockPos(1, 1, 1);
        BlockState cleanLog = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState();
        context.setBlockState(targetPos, cleanLog);

        // Blocco marcio a distanza a (3, 1, 3) non cerato
        BlockPos distantRottenPos = new BlockPos(3, 1, 3);
        BlockState unwaxedRotten = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState()
                .with(MoldyLogBlock.STAGE, 3)
                .with(MoldyLogBlock.WAXED, false);
        context.setBlockState(distantRottenPos, unwaxedRotten);

        MoldRiskResult rUnwaxed = MoldRiskCalculator.calculate(context.getWorld(), context.getAbsolutePos(targetPos),
                false, cleanLog);
        if (rUnwaxed.miasmaBonus() <= 0.0) {
            context.throwPositionedException("Con legno marcio non cerato il miasmaBonus deve essere > 0!", targetPos);
        }

        // Ceriamo il blocco marcio (Waxed = true)
        BlockState waxedRotten = unwaxedRotten.with(MoldyLogBlock.WAXED, true);
        context.setBlockState(distantRottenPos, waxedRotten);

        MoldRiskResult rWaxed = MoldRiskCalculator.calculate(context.getWorld(), context.getAbsolutePos(targetPos),
                false, cleanLog);
        if (rWaxed.miasmaBonus() != 0.0) {
            context.throwPositionedException(
                    "Cerare il legno marcio deve azzerare la sua emissione di miasma (0.0), trovato: "
                            + rWaxed.miasmaBonus(),
                    targetPos);
        }
        if (rWaxed.R() >= rUnwaxed.R()) {
            context.throwPositionedException(
                    "Cerare i focolai marci deve abbassare il rischio R sui blocchi sani circostanti!", targetPos);
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testRoomVolumeDilutesMiasmaAndLowersRisk(TestContext context) {
        // Stanza Piccola (3x3x3, volume aria = 1 blocco a (2, 2, 2))
        for (int x = 1; x <= 3; x++) {
            for (int y = 1; y <= 3; y++) {
                for (int z = 1; z <= 3; z++) {
                    context.setBlockState(new BlockPos(x, y, z), Blocks.STONE.getDefaultState());
                }
            }
        }
        BlockPos centerSmall = new BlockPos(2, 2, 2);
        context.setBlockState(centerSmall, Blocks.AIR.getDefaultState());

        // Trave bersaglio a (3, 2, 2) e focolare marcio a (1, 2, 2)
        BlockPos targetPosSmall = new BlockPos(3, 2, 2);
        BlockState cleanLog = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState();
        context.setBlockState(targetPosSmall, cleanLog);

        BlockState moldyLog = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState().with(MoldyLogBlock.STAGE,
                2);
        context.setBlockState(new BlockPos(1, 2, 2), moldyLog);

        MoldRiskResult rSmall = MoldRiskCalculator.calculate(context.getWorld(), context.getAbsolutePos(targetPosSmall),
                false, cleanLog);

        // Ora costruiamo una Stanza Grande 6x4x6 attorno al bersaglio
        for (int x = 0; x <= 6; x++) {
            for (int y = 0; y <= 3; y++) {
                for (int z = 0; z <= 6; z++) {
                    boolean isWall = (x == 0 || x == 6 || y == 0 || y == 3 || z == 0 || z == 6);
                    context.setBlockState(new BlockPos(x, y, z),
                            isWall ? Blocks.STONE.getDefaultState() : Blocks.AIR.getDefaultState());
                }
            }
        }
        BlockPos targetPosLarge = new BlockPos(1, 1, 1);
        context.setBlockState(targetPosLarge, cleanLog);
        // Stesso singolo blocco marcio posto a distanza
        context.setBlockState(new BlockPos(5, 1, 5), moldyLog);

        MoldRiskResult rLarge = MoldRiskCalculator.calculate(context.getWorld(), context.getAbsolutePos(targetPosLarge),
                false, cleanLog);

        if (rLarge.miasmaBonus() >= rSmall.miasmaBonus()) {
            context.throwPositionedException(
                    "La diluizione in un volume maggiore deve ridurre la concentrazione di spore (miasmaBonus)! Piccolo: "
                            + rSmall.miasmaBonus() + ", Grande: " + rLarge.miasmaBonus(),
                    targetPosLarge);
        }
        if (rLarge.R() >= rSmall.R()) {
            context.throwPositionedException(
                    "Una stanza più ampia deve avere un rischio R inferiore a parità di muffa emittente!",
                    targetPosLarge);
        }

        context.complete();
    }
}
