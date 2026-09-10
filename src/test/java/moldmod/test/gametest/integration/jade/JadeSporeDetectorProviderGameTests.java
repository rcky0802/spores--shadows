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
        context.assertFalse(defaultState.emitsRedstonePower(),
                "Spore detector state must not emit redstone power");

        // Test toxicity levels 0..3
        for (int level = 0; level <= 3; level++) {
            BlockState levelState = defaultState.with(SporeDetectorBlock.TOXICITY_LEVEL, level);
            context.assertTrue(levelState.get(SporeDetectorBlock.TOXICITY_LEVEL) == level,
                    "Toxicity level state must equal " + level);
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testSporeDetectorJadeTooltipKeys(TestContext context) {
        java.util.List<String> keys = java.util.List.of(
                "config.jade.plugin_spores--shadows.spore_detector_info",
                "tooltip.spores--shadows.jade.spore_detector.air_quality",
                "tooltip.spores--shadows.jade.spore_detector.clean",
                "tooltip.spores--shadows.jade.spore_detector.warning",
                "tooltip.spores--shadows.jade.spore_detector.moderate",
                "tooltip.spores--shadows.jade.spore_detector.lethal"
        );
        for (String key : keys) {
            net.minecraft.text.Text text = net.minecraft.text.Text.translatable(key);
            context.assertTrue(!text.getString().isEmpty(), "Jade tooltip key " + key + " must not be empty");
        }
        context.complete();
    }
}
