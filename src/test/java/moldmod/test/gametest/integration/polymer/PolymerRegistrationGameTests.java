package moldmod.test.gametest.integration.polymer;

import moldmod.SporesShadowsConstants;
import moldmod.SporesShadowsConstants.MoldStage;
import moldmod.SporesShadowsConstants.MoldyWoodType;
import moldmod.resource.MoldyResourceGenerator;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;

import java.util.List;

public class PolymerRegistrationGameTests {

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testWoodTypesAndMoldStagesConstants(TestContext context) {
        List<MoldyWoodType> woodTypes = SporesShadowsConstants.WOOD_TYPES;
        context.assertTrue(!woodTypes.isEmpty(), "Wood types list must not be empty, found: " + woodTypes.size());
        context.assertTrue(woodTypes.size() == 10, "Wood types list must contain 10 types, found: " + woodTypes.size());

        MoldStage[] stages = MoldStage.values();
        context.assertTrue(stages.length == 4, "Must contain exactly 4 mold stages, found: " + stages.length);
        context.assertTrue(MoldStage.WAXED.getId() == 0, "WAXED stage ID must be 0");
        context.assertTrue(MoldStage.TAINTED.getId() == 1, "TAINTED stage ID must be 1");
        context.assertTrue(MoldStage.MOLDY.getId() == 2, "MOLDY stage ID must be 2");
        context.assertTrue(MoldStage.ROTTEN.getId() == 3, "ROTTEN stage ID must be 3");

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testPolymerResourceGeneratorInitialization(TestContext context) {
        // Verify initialize method runs without throwing exceptions
        MoldyResourceGenerator.initialize();
        context.complete();
    }
}
