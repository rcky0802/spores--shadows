package moldmod.test;

import moldmod.block.ModBlocks;
import moldmod.block.MoldyButtonBlock;
import moldmod.block.MoldyPressurePlateBlock;
import moldmod.block.MoldyLogBlock;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public class MoldyRedstoneTests {

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testButtonDurationIncreasesWithStage(TestContext context) {
        BlockPos pos = new BlockPos(0, 2, 0);
        MoldyButtonBlock buttonBlock = (MoldyButtonBlock) ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_BUTTON);

        // Stage 0
        BlockState stage0 = buttonBlock.getDefaultState().with(MoldyLogBlock.STAGE, 0);
        if (buttonBlock.getMoldyPressTicks(stage0) != 30) {
            context.throwPositionedException("Stage 0 button should have 30 ticks duration, got " + buttonBlock.getMoldyPressTicks(stage0), pos);
        }

        // Stage 1
        BlockState stage1 = buttonBlock.getDefaultState().with(MoldyLogBlock.STAGE, 1);
        if (buttonBlock.getMoldyPressTicks(stage1) != 60) {
            context.throwPositionedException("Stage 1 button should have 60 ticks duration, got " + buttonBlock.getMoldyPressTicks(stage1), pos);
        }

        // Stage 2
        BlockState stage2 = buttonBlock.getDefaultState().with(MoldyLogBlock.STAGE, 2);
        if (buttonBlock.getMoldyPressTicks(stage2) != 150) {
            context.throwPositionedException("Stage 2 button should have 150 ticks duration, got " + buttonBlock.getMoldyPressTicks(stage2), pos);
        }

        // Stage 3
        BlockState stage3 = buttonBlock.getDefaultState().with(MoldyLogBlock.STAGE, 3);
        if (buttonBlock.getMoldyPressTicks(stage3) != 450) {
            context.throwPositionedException("Stage 3 button should have 450 ticks duration, got " + buttonBlock.getMoldyPressTicks(stage3), pos);
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testPressurePlateDurationIncreasesWithStage(TestContext context) {
        BlockPos pos = new BlockPos(0, 2, 0);
        MoldyPressurePlateBlock plateBlock = (MoldyPressurePlateBlock) ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_PRESSURE_PLATE);

        // Stage 0
        BlockState stage0 = plateBlock.getDefaultState().with(MoldyLogBlock.STAGE, 0);
        if (plateBlock.getMoldyPressTicks(stage0) != 20) {
            context.throwPositionedException("Stage 0 plate should have 20 ticks duration, got " + plateBlock.getMoldyPressTicks(stage0), pos);
        }

        // Stage 1
        BlockState stage1 = plateBlock.getDefaultState().with(MoldyLogBlock.STAGE, 1);
        if (plateBlock.getMoldyPressTicks(stage1) != 40) {
            context.throwPositionedException("Stage 1 plate should have 40 ticks duration, got " + plateBlock.getMoldyPressTicks(stage1), pos);
        }

        // Stage 2
        BlockState stage2 = plateBlock.getDefaultState().with(MoldyLogBlock.STAGE, 2);
        if (plateBlock.getMoldyPressTicks(stage2) != 100) {
            context.throwPositionedException("Stage 2 plate should have 100 ticks duration, got " + plateBlock.getMoldyPressTicks(stage2), pos);
        }

        // Stage 3
        BlockState stage3 = plateBlock.getDefaultState().with(MoldyLogBlock.STAGE, 3);
        if (plateBlock.getMoldyPressTicks(stage3) != 300) {
            context.throwPositionedException("Stage 3 plate should have 300 ticks duration, got " + plateBlock.getMoldyPressTicks(stage3), pos);
        }

        context.complete();
    }
}
