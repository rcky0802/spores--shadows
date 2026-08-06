package moldmod.testing;

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

    @GameTest(templateName = "fabric-api-base:empty")
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

    @GameTest(templateName = "fabric-api-base:empty")
    public void testVillageCategory4(TestContext context) {
        MoldyStructureContext.setStructure("village_plains");

        try {
            // Place an oak log high in the sky (simulating roof)
            BlockPos roofPos = new BlockPos(1, 10, 1);
            BlockState roofResult = MoldyStructureContext.processBlock(
                    Blocks.OAK_LOG.getDefaultState(),
                    context.getAbsolutePos(roofPos),
                    (StructureWorldAccess) context.getWorld()
            );
            context.assertTrue(roofResult.isOf(Blocks.OAK_LOG), "Roof block should not degrade in Village!");

            // Place an oak log near grass (simulating foundation)
            BlockPos grassPos = new BlockPos(2, 2, 2);
            context.setBlockState(new BlockPos(2, 1, 2), Blocks.GRASS_BLOCK); // foundation
            
            // Loop a few times to beat randomness
            boolean degraded = false;
            for (int i = 0; i < 50; i++) {
                BlockState result = MoldyStructureContext.processBlock(
                        Blocks.OAK_LOG.getDefaultState(),
                        context.getAbsolutePos(grassPos),
                        (StructureWorldAccess) context.getWorld()
                );
                if (result.isOf(ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG))) {
                    degraded = true;
                    break;
                }
            }
            context.assertTrue(degraded, "Foundation block should eventually degrade in Village!");
            context.complete();
        } finally {
            MoldyStructureContext.clear();
        }
    }
}
