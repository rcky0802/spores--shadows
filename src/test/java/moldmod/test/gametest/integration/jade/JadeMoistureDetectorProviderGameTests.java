package moldmod.test.gametest.integration.jade;

import moldmod.SporesShadows;
import moldmod.block.ModBlocks;
import moldmod.block.MoistureDetectorBlock;
import moldmod.integration.jade.MoistureDetectorBlockProvider;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.BlockState;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.Identifier;

public class JadeMoistureDetectorProviderGameTests {

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testMoistureDetectorProviderUid(TestContext context) {
        Identifier uid = MoistureDetectorBlockProvider.INSTANCE.getUid();
        context.assertTrue(uid.getNamespace().equals(SporesShadows.MOD_ID),
                "Provider namespace must be " + SporesShadows.MOD_ID + ", got: " + uid.getNamespace());
        context.assertTrue(uid.getPath().equals("moisture_detector_info"),
                "Provider path must be 'moisture_detector_info', got: " + uid.getPath());
        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testMoistureDetectorBlockStateProperties(TestContext context) {
        BlockState defaultState = ModBlocks.MOISTURE_DETECTOR.getDefaultState();

        context.assertTrue(defaultState.contains(MoistureDetectorBlock.MOISTURE_STAGE),
                "Moisture detector state must contain MOISTURE_STAGE property");
        context.assertFalse(defaultState.emitsRedstonePower(),
                "Moisture detector state must not emit redstone power");

        // Test moisture stages 0..3
        for (int stage = 0; stage <= 3; stage++) {
            BlockState stageState = defaultState.with(MoistureDetectorBlock.MOISTURE_STAGE, stage);
            context.assertTrue(stageState.get(MoistureDetectorBlock.MOISTURE_STAGE) == stage,
                    "Moisture stage state must equal " + stage);
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testMoistureDetectorJadeTooltipKeys(TestContext context) {
        java.util.List<String> keys = java.util.List.of(
                "config.jade.plugin_spores--shadows.moisture_detector_info",
                "tooltip.spores--shadows.jade.moisture_detector.effective_moisture",
                "tooltip.spores--shadows.jade.moisture_detector.dry",
                "tooltip.spores--shadows.jade.moisture_detector.moderate",
                "tooltip.spores--shadows.jade.moisture_detector.humid",
                "tooltip.spores--shadows.jade.moisture_detector.critical"
        );
        for (String key : keys) {
            net.minecraft.text.Text text = net.minecraft.text.Text.translatable(key);
            context.assertTrue(!text.getString().isEmpty(), "Jade tooltip key " + key + " must not be empty");
        }
        context.complete();
    }
}
