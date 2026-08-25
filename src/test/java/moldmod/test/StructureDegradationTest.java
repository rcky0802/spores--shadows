package moldmod.test;

import moldmod.block.ModBlocks;
import moldmod.block.MoldyLogBlock;
import moldmod.structure.MoldyStructureContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;

public class StructureDegradationTest {

    @GameTest(templateName = "fabric-gametest-api-v1:empty")
    public void testMineshaftDegradation(TestContext context) {
        // Mock a mineshaft generation
        MoldyStructureContext.setStructure("mineshaft");

        try {
            // Place 100 oak logs and count the results
            int moldy = 0;
            int tainted = 0;
            int rotten = 0;

            for (int x = 0; x < 10; x++) {
                for (int z = 0; z < 10; z++) {
                    BlockPos pos = new BlockPos(x, 2, z);
                    
                    BlockState result = MoldyStructureContext.processBlock(
                            Blocks.OAK_LOG.getDefaultState(), 
                            context.getAbsolutePos(pos), 
                            (StructureWorldAccess) context.getWorld()
                    );
                    
                    if (result.isOf(ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG))) {
                        int stage = result.get(MoldyLogBlock.STAGE);
                        if (stage == 0) moldy++;
                        else if (stage == 1) tainted++;
                        else if (stage == 2) rotten++;
                    }
                }
            }

            context.assertTrue(moldy > 0 || tainted > 0 || rotten > 0, "No degradation occurred in mineshaft!");
            context.complete();

        } finally {
            MoldyStructureContext.clear();
        }
    }

    @GameTest(templateName = "fabric-gametest-api-v1:empty")
    public void testShipwreckCategory1(TestContext context) {
        MoldyStructureContext.setStructure("shipwreck");
        try {
            int rotten = 0;
            for (int x = 0; x < 20; x++) {
                BlockState result = MoldyStructureContext.processBlock(
                        Blocks.OAK_LOG.getDefaultState(),
                        context.getAbsolutePos(new BlockPos(x, 2, 0)),
                        (StructureWorldAccess) context.getWorld()
                );
                if (result.isOf(ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG)) && result.get(MoldyLogBlock.STAGE) == 2) {
                    rotten++;
                }
            }
            context.assertTrue(rotten > 0, "Shipwreck should produce rotten blocks (Category 1)!");
            context.complete();
        } finally {
            MoldyStructureContext.clear();
        }
    }

    @GameTest(templateName = "fabric-gametest-api-v1:empty")
    public void testPillagerOutpostCategory3(TestContext context) {
        MoldyStructureContext.setStructure("pillager_outpost");
        try {
            int tainted = 0;
            for (int x = 0; x < 20; x++) {
                BlockState result = MoldyStructureContext.processBlock(
                        Blocks.OAK_LOG.getDefaultState(),
                        context.getAbsolutePos(new BlockPos(x, 2, 0)),
                        (StructureWorldAccess) context.getWorld()
                );
                if (result.isOf(ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG)) && result.get(MoldyLogBlock.STAGE) == 1) {
                    tainted++;
                }
            }
            context.assertTrue(tainted > 0, "Pillager Outpost should produce tainted blocks (Category 3)!");
            context.complete();
        } finally {
            MoldyStructureContext.clear();
        }
    }
}
