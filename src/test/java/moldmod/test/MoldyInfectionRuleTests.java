package moldmod.test;

import me.shedaniel.autoconfig.AutoConfig;
import moldmod.block.ModBlocks;
import moldmod.block.MoldyBlockHelper;
import moldmod.block.MoldyLogBlock;
import moldmod.config.ModConfig;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public class MoldyInfectionRuleTests {

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testCannotInfectWaxedBlock(TestContext context) {
        BlockState waxedLog = ModBlocks.MOLDY_TO_WAXED.get(ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG)).getDefaultState()
                .with(MoldyLogBlock.WAXED, true)
                .with(MoldyLogBlock.STRUCTURAL, false); // Explicitly not structural
                
        if (MoldyBlockHelper.canBeInfected(waxedLog)) {
            context.throwPositionedException("Waxed block should NOT be infectable!", new BlockPos(0,0,0));
        }
        
        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testDefaultAllowsStructuralInfection(TestContext context) {
        ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        config.general.structures_immune = false; // Default setting

        BlockState structuralLog = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState()
                .with(MoldyLogBlock.WAXED, false)
                .with(MoldyLogBlock.STRUCTURAL, true);
                
        if (!MoldyBlockHelper.canBeInfected(structuralLog)) {
            context.throwPositionedException("Structural (generated) Log SHOULD be infectable by default (structures_immune = false)!", new BlockPos(0,0,0));
        }
        
        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testStructuresImmuneConfigProtectsStructuralBlocks(TestContext context) {
        ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        config.general.structures_immune = true;

        BlockState structuralLog = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState()
                .with(MoldyLogBlock.WAXED, false)
                .with(MoldyLogBlock.STRUCTURAL, true);
                
        if (MoldyBlockHelper.canBeInfected(structuralLog)) {
            context.throwPositionedException("Structural Log should NOT be infectable when structures_immune = true!", new BlockPos(0,0,0));
        }
        
        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testCannotInfectStructuralPlanksWhenImmune(TestContext context) {
        ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        config.general.structures_immune = true;

        BlockState structuralPlanks = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_PLANKS).getDefaultState()
                .with(MoldyLogBlock.WAXED, false)
                .with(MoldyLogBlock.STRUCTURAL, true); 
                
        if (MoldyBlockHelper.canBeInfected(structuralPlanks)) {
            context.throwPositionedException("Structural Planks should NOT be infectable when structures_immune = true!", new BlockPos(0,0,0));
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

