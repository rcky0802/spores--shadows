package moldmod.test.gametest.integration.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import moldmod.config.ModConfig;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public class ClothConfigRuntimeGameTests {

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testClothConfigHolderRegistered(TestContext context) {
        ConfigHolder<ModConfig> holder = AutoConfig.getConfigHolder(ModConfig.class);
        if (holder == null) {
            context.throwPositionedException("AutoConfig holder for ModConfig was not registered!",
                    context.getRelativePos(BlockPos.ORIGIN));
            return;
        }

        ModConfig config = holder.getConfig();
        if (config == null) {
            context.throwPositionedException("ModConfig instance inside AutoConfig holder is null!",
                    context.getRelativePos(BlockPos.ORIGIN));
            return;
        }

        if (config.general == null || config.environment == null || config.toxicity == null || config.structures == null) {
            context.throwPositionedException("ModConfig categories are not properly initialized at runtime!",
                    context.getRelativePos(BlockPos.ORIGIN));
            return;
        }

        context.complete();
    }
}
