package moldmod.test.gametest.integration.jade;

import me.shedaniel.autoconfig.AutoConfig;
import moldmod.SporesShadows;
import moldmod.block.ModBlocks;
import moldmod.block.MoldyBlock;
import moldmod.block.MoldyLogBlock;
import moldmod.config.ModConfig;
import moldmod.integration.jade.MoldyBlockProvider;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.Identifier;

public class JadeMoldyBlockProviderGameTests {

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testMoldyBlockProviderUid(TestContext context) {
        Identifier uid = MoldyBlockProvider.INSTANCE.getUid();
        context.assertTrue(uid.getNamespace().equals(SporesShadows.MOD_ID),
                "Provider namespace must be " + SporesShadows.MOD_ID + ", got: " + uid.getNamespace());
        context.assertTrue(uid.getPath().equals("moldy_info"),
                "Provider path must be 'moldy_info', got: " + uid.getPath());
        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testMoldyBlockStateProperties(TestContext context) {
        BlockState normalLog = Blocks.OAK_LOG.getDefaultState();
        context.assertFalse(normalLog.contains(MoldyBlock.STAGE),
                "Vanilla oak log must not contain STAGE property");

        BlockState moldyLog = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState();
        context.assertTrue(moldyLog.contains(MoldyBlock.STAGE),
                "Moldy oak log must contain STAGE property");
        context.assertTrue(moldyLog.contains(MoldyBlock.WAXED),
                "Moldy oak log must contain WAXED property");

        // Verify stage values
        BlockState stage1 = moldyLog.with(MoldyLogBlock.STAGE, 1);
        context.assertTrue(stage1.get(MoldyLogBlock.STAGE) == 1, "Stage must be 1");

        BlockState stage3 = moldyLog.with(MoldyLogBlock.STAGE, 3);
        context.assertTrue(stage3.get(MoldyLogBlock.STAGE) == 3, "Stage must be 3");

        BlockState waxed = moldyLog.with(MoldyLogBlock.WAXED, true);
        context.assertTrue(waxed.get(MoldyLogBlock.WAXED), "Waxed must be true");

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testInfectionThresholdConfig(TestContext context) {
        ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        context.assertTrue(config.general.infection_threshold > 0.0 && config.general.infection_threshold <= 1.0,
                "Infection threshold must be in (0.0, 1.0]");
        context.complete();
    }
}
