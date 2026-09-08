package moldmod.test.gametest.integration.jade;

import moldmod.SporesShadows;
import moldmod.block.ModBlocks;
import moldmod.block.SporeDetectorBlock;
import moldmod.integration.jade.SporeDetectorBlockProvider;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.BlockState;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.Identifier;

public class JadeSporeDetectorProviderGameTests {

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testSporeDetectorProviderUid(TestContext context) {
        Identifier uid = SporeDetectorBlockProvider.INSTANCE.getUid();
        context.assertTrue(uid.getNamespace().equals(SporesShadows.MOD_ID),
                "Provider namespace must be " + SporesShadows.MOD_ID + ", got: " + uid.getNamespace());
        context.assertTrue(uid.getPath().equals("spore_detector_info"),
                "Provider path must be 'spore_detector_info', got: " + uid.getPath());
        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testSporeDetectorBlockStateProperties(TestContext context) {
        BlockState defaultState = ModBlocks.SPORE_DETECTOR.getDefaultState();

        context.assertTrue(defaultState.contains(SporeDetectorBlock.TOXICITY_LEVEL),
                "Spore detector state must contain TOXICITY_LEVEL property");
        context.assertTrue(defaultState.contains(SporeDetectorBlock.POWER),
                "Spore detector state must contain POWER property");

        // Test toxicity levels 0..3
        for (int level = 0; level <= 3; level++) {
            BlockState levelState = defaultState.with(SporeDetectorBlock.TOXICITY_LEVEL, level);
            context.assertTrue(levelState.get(SporeDetectorBlock.TOXICITY_LEVEL) == level,
                    "Toxicity level state must equal " + level);
        }

        // Test power levels 0..15
        for (int power = 0; power <= 15; power++) {
            BlockState powerState = defaultState.with(SporeDetectorBlock.POWER, power);
            context.assertTrue(powerState.get(SporeDetectorBlock.POWER) == power,
                    "Power level state must equal " + power);
        }

        context.complete();
    }
}
