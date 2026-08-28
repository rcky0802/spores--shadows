package moldmod.test;

import moldmod.block.ModBlocks;
import moldmod.block.MoldyLogBlock;
import moldmod.event.ToxicAirEvent;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class ToxicAirTests {

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testCanAirPass(TestContext context) {
        BlockPos pos1 = new BlockPos(1, 2, 1);
        BlockPos pos2 = pos1.north();

        // 1. Aria e Aria (passa)
        context.setBlockState(pos1, Blocks.AIR);
        context.setBlockState(pos2, Blocks.AIR);
        if (!ToxicAirEvent.canAirPass(context.getWorld(), context.getAbsolutePos(pos1), Blocks.AIR.getDefaultState(), context.getAbsolutePos(pos2), Blocks.AIR.getDefaultState(), Direction.NORTH)) {
            context.throwPositionedException("L'aria dovrebbe passare tra due blocchi vuoti", pos1);
        }

        // 2. Muro di pietra e Aria (non passa)
        context.setBlockState(pos1, Blocks.STONE);
        if (ToxicAirEvent.canAirPass(context.getWorld(), context.getAbsolutePos(pos1), Blocks.STONE.getDefaultState(), context.getAbsolutePos(pos2), Blocks.AIR.getDefaultState(), Direction.NORTH)) {
            context.throwPositionedException("L'aria NON dovrebbe passare da una pietra", pos1);
        }

        // 3. Staccionata (passa)
        context.setBlockState(pos1, Blocks.OAK_FENCE);
        if (!ToxicAirEvent.canAirPass(context.getWorld(), context.getAbsolutePos(pos1), Blocks.OAK_FENCE.getDefaultState(), context.getAbsolutePos(pos2), Blocks.AIR.getDefaultState(), Direction.NORTH)) {
            context.throwPositionedException("L'aria dovrebbe passare attraverso una staccionata (non è full square)", pos1);
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testHasMoldNearby(TestContext context) {
        BlockPos center = new BlockPos(2, 2, 2);
        
        // 1. Nessuna muffa (FALSO)
        if (ToxicAirEvent.hasMoldNearby(context.getWorld(), context.getAbsolutePos(center), 4)) {
            context.throwPositionedException("Non dovrebbe esserci muffa vicina!", center);
        }

        // 2. Aggiungo blocco di legno sano (FALSO)
        BlockState log = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState();
        context.setBlockState(center.add(2, 0, 0), log);
        if (ToxicAirEvent.hasMoldNearby(context.getWorld(), context.getAbsolutePos(center), 4)) {
            context.throwPositionedException("Il legno sano NON è muffa!", center);
        }

        // 3. Aggiungo blocco di legno ammuffito (VERO)
        BlockState moldyLog = log.with(MoldyLogBlock.STAGE, 2);
        context.setBlockState(center.add(-2, 0, 0), moldyLog);
        if (!ToxicAirEvent.hasMoldNearby(context.getWorld(), context.getAbsolutePos(center), 4)) {
            context.throwPositionedException("Dovrebbe rilevare il legno ammuffito!", center);
        }

        // 4. Se il legno è cerato, non conta (FALSO) - Rimuovo quello precedente e ne metto uno cerato
        context.setBlockState(center.add(-2, 0, 0), Blocks.AIR); // rimuovo il vecchio
        BlockState waxedMoldyLog = moldyLog.with(MoldyLogBlock.WAXED, true);
        context.setBlockState(center.add(0, 2, 0), waxedMoldyLog);
        
        if (ToxicAirEvent.hasMoldNearby(context.getWorld(), context.getAbsolutePos(center), 4)) {
            context.throwPositionedException("Il legno cerato non dovrebbe diffondere miasma!", center);
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testManhattanRadiusLimit(TestContext context) {
        BlockPos center = new BlockPos(2, 2, 2);
        
        // Creiamo un tunnel 1x1x15 sigillato su tutti i lati tranne lungo X
        for (int dx = -1; dx <= 15; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    BlockPos p = center.add(dx, dy, dz);
                    if (dy == 0 && dz == 0 && dx >= 0 && dx < 15) {
                        context.setBlockState(p, Blocks.AIR);
                    } else {
                        context.setBlockState(p, Blocks.STONE);
                    }
                }
            }
        }
        
        ToxicAirEvent.MiasmaResult result = ToxicAirEvent.calculateMiasma(context.getWorld(), context.getAbsolutePos(center));
        
        // Il raggio di manhattan è 8. Quindi partendo da 2,2,2 può esplorare in +X al massimo per 8 blocchi.
        // Volume esplorato non dovrebbe MAI superare 9 (centro + 8 blocchi avanti)
        if (result.volume > 9) {
            context.throwPositionedException("Il raggio di manhattan non sta limitando la ricerca! Volume esplorato: " + result.volume, center);
        }
        
        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testEnclosedRoomToxicity(TestContext context) {
        // Costruiamo una scatola sigillata 3x3x3 di pietra attorno al centro (2,2,2)
        BlockPos center = new BlockPos(2, 2, 2);
        for (int x = 1; x <= 3; x++) {
            for (int y = 1; y <= 3; y++) {
                for (int z = 1; z <= 3; z++) {
                    context.setBlockState(new BlockPos(x, y, z), Blocks.STONE);
                }
            }
        }
        // Centro vuoto (aria)
        context.setBlockState(center, Blocks.AIR);

        // Mettiamo un blocco di legno infetto Stadio 2 su una parete interna (1, 2, 2)
        BlockState moldyLog = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState()
                .with(MoldyLogBlock.STAGE, 2)
                .with(MoldyLogBlock.WAXED, false);
        context.setBlockState(new BlockPos(1, 2, 2), moldyLog);

        ToxicAirEvent.MiasmaResult result = ToxicAirEvent.calculateMiasma(context.getWorld(), context.getAbsolutePos(center));

        if (result.openAir) {
            context.throwPositionedException("La stanza sigillata non dovrebbe essere considerata openAir!", center);
        }
        if (result.volume != 1) {
            context.throwPositionedException("Il volume d'aria della stanza dovrebbe essere esattamente 1 blocco, trovato: " + result.volume, center);
        }
        if (result.toxicScore <= 0.0) {
            context.throwPositionedException("Il punteggio di tossicità dovrebbe essere positivo per il legno allo Stadio 2!", center);
        }
        if (result.ventilationScore != 0.0) {
            context.throwPositionedException("In una stanza sigillata il punteggio di ventilazione deve essere 0!", center);
        }
        if (result.netMiasma != result.toxicScore) {
            context.throwPositionedException("In assenza di ventilazione, netMiasma deve coincidere con toxicScore!", center);
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testOpenAirDissipation(TestContext context) {
        BlockPos center = new BlockPos(2, 2, 2);
        BlockPos absCenter = context.getAbsolutePos(center);
        int topY = context.getWorld().getTopY(net.minecraft.world.Heightmap.Type.MOTION_BLOCKING, absCenter.getX(), absCenter.getZ());
        BlockPos skyPos = new BlockPos(absCenter.getX(), Math.max(topY, absCenter.getY()), absCenter.getZ());

        ToxicAirEvent.MiasmaResult result = ToxicAirEvent.calculateMiasma(context.getWorld(), skyPos);

        if (!result.openAir) {
            context.throwPositionedException("Un blocco alla coordinata topY del cielo deve avere openAir = true!", center);
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testWaxedWoodMiasmaImmunity(TestContext context) {
        // Stanza sigillata con blocchi infetti tutti cerati
        BlockPos center = new BlockPos(2, 2, 2);
        for (int x = 1; x <= 3; x++) {
            for (int y = 1; y <= 3; y++) {
                for (int z = 1; z <= 3; z++) {
                    context.setBlockState(new BlockPos(x, y, z), Blocks.STONE);
                }
            }
        }
        context.setBlockState(center, Blocks.AIR);

        // Blocco Marcio (Stadio 3) ma Cerato
        BlockState waxedRotten = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState()
                .with(MoldyLogBlock.STAGE, 3)
                .with(MoldyLogBlock.WAXED, true);
        context.setBlockState(new BlockPos(1, 2, 2), waxedRotten);

        ToxicAirEvent.MiasmaResult result = ToxicAirEvent.calculateMiasma(context.getWorld(), context.getAbsolutePos(center));

        if (result.toxicScore != 0.0) {
            context.throwPositionedException("Il legno cerato non deve produrre alcuno score di tossicità, trovato: " + result.toxicScore, center);
        }
        if (result.netMiasma != 0.0) {
            context.throwPositionedException("Il miasma netto per legno cerato deve essere 0, trovato: " + result.netMiasma, center);
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testVentilationWithGaps(TestContext context) {
        // Stanza con soffitto parziale per permettere la ventilazione esterna
        BlockPos center = new BlockPos(2, 2, 2);
        for (int x = 1; x <= 3; x++) {
            for (int y = 1; y <= 3; y++) {
                for (int z = 1; z <= 3; z++) {
                    context.setBlockState(new BlockPos(x, y, z), Blocks.STONE);
                }
            }
        }
        context.setBlockState(center, Blocks.AIR);

        // Parete con staccionata verso l'esterno (e aria sopra di essa per comunicare col cielo)
        context.setBlockState(new BlockPos(1, 2, 2), Blocks.OAK_FENCE);
        context.setBlockState(new BlockPos(1, 3, 2), Blocks.AIR);

        // Blocco infetto su altra parete
        BlockState moldy = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState()
                .with(MoldyLogBlock.STAGE, 2)
                .with(MoldyLogBlock.WAXED, false);
        context.setBlockState(new BlockPos(3, 2, 2), moldy);

        ToxicAirEvent.MiasmaResult result = ToxicAirEvent.calculateMiasma(context.getWorld(), context.getAbsolutePos(center));

        if (result.toxicScore <= 0.0) {
            context.throwPositionedException("La tossicità da muffa deve essere presente!", center);
        }
        // Il calcolo deve restituire un netMiasma inferiore o uguale al toxicScore grazie alla ventilazione
        if (result.netMiasma > result.toxicScore) {
            context.throwPositionedException("Il miasma netto non può superare il toxicScore lordo!", center);
        }

        context.complete();
    }
}
