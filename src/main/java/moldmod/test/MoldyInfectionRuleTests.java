package moldmod.test;

import moldmod.block.ModBlocks;
import moldmod.block.MoldyBlockHelper;
import moldmod.block.MoldyLogBlock;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public class MoldyInfectionRuleTests {

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testCannotInfectWaxedBlock(TestContext context) {
        BlockState waxedLog = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState()
                .with(MoldyLogBlock.WAXED, true)
                .with(MoldyLogBlock.STRUCTURAL, false); // Explicitly not structural
                
        if (MoldyBlockHelper.canBeInfected(waxedLog)) {
            context.throwPositionedException("Waxed block should NOT be infectable!", new BlockPos(0,0,0));
        }
        
        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testCannotInfectStructuralLog(TestContext context) {
        BlockState structuralLog = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState()
                .with(MoldyLogBlock.WAXED, false)
                .with(MoldyLogBlock.STRUCTURAL, true); // Natural tree log
                
        if (MoldyBlockHelper.canBeInfected(structuralLog)) {
            context.throwPositionedException("Structural (generated) Log should NOT be infectable!", new BlockPos(0,0,0));
        }
        
        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testCannotInfectStructuralWood(TestContext context) {
        BlockState structuralWood = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_WOOD).getDefaultState()
                .with(MoldyLogBlock.WAXED, false)
                .with(MoldyLogBlock.STRUCTURAL, true); // Natural tree wood
                
        if (MoldyBlockHelper.canBeInfected(structuralWood)) {
            context.throwPositionedException("Structural (generated) Wood should NOT be infectable!", new BlockPos(0,0,0));
        }
        
        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testCannotInfectStructuralPlanks(TestContext context) {
        // Planks CANNOT be infected if generated in a structure (e.g. mineshaft). They act like waxed.
        BlockState structuralPlanks = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_PLANKS).getDefaultState()
                .with(MoldyLogBlock.WAXED, false)
                .with(MoldyLogBlock.STRUCTURAL, true); 
                
        if (MoldyBlockHelper.canBeInfected(structuralPlanks)) {
            context.throwPositionedException("Structural (generated) Planks should NOT be infectable!", new BlockPos(0,0,0));
        }
        
        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testCanInfectNormalLog(TestContext context) {
        // Player-placed log
        BlockState normalLog = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState()
                .with(MoldyLogBlock.WAXED, false)
                .with(MoldyLogBlock.STRUCTURAL, false); 
                
        if (!MoldyBlockHelper.canBeInfected(normalLog)) {
            context.throwPositionedException("Normal player-placed Log SHOULD be infectable!", new BlockPos(0,0,0));
        }
        
        context.complete();
    }
}
