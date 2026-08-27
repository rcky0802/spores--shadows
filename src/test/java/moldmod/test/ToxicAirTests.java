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
}
