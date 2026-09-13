package moldmod.test.gametest.integration.polymer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import eu.pb4.polymer.resourcepack.api.PolymerArmorModel;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import eu.pb4.polymer.resourcepack.api.ResourcePackBuilder;
import moldmod.SporesShadows;
import moldmod.SporesShadowsConstants;
import moldmod.resource.MoldyJsonGenerator;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class MoldyJsonGeneratorGameTests {

    private static class TestResourcePackBuilder implements ResourcePackBuilder {
        final Map<String, byte[]> capturedFiles = new HashMap<>();

        @Override
        public boolean addData(String path, byte[] data) {
            capturedFiles.put(path, data);
            return true;
        }

        @Override
        public boolean copyAssets(String namespace) {
            return true;
        }

        @Override
        public boolean copyFromPath(Path path, String target, boolean replace) {
            return true;
        }

        @Override
        public boolean addCustomModelData(PolymerModelData modelData) {
            return true;
        }

        @Override
        public boolean addArmorModel(PolymerArmorModel armorModel) {
            return true;
        }

        @Override
        public byte[] getData(String path) {
            return capturedFiles.get(path);
        }

        @Override
        public byte[] getDataOrSource(String path) {
            return capturedFiles.get(path);
        }

        @Override
        public boolean addAssetsSource(String modId) {
            return true;
        }

        @Override
        public void addWriteConverter(BiFunction<String, byte[], byte[]> converter) {
            // No-op for tests
        }
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testGeneratedJsonSyntaxAndParseValidity(TestContext context) {
        TestResourcePackBuilder builder = new TestResourcePackBuilder();
        MoldyJsonGenerator.generateAll(builder);

        Map<String, byte[]> capturedFiles = builder.capturedFiles;
        context.assertTrue(!capturedFiles.isEmpty(), "Generated files map must not be empty");
        context.assertTrue(capturedFiles.size() >= 100,
                "Should generate hundreds of JSON assets, got: " + capturedFiles.size());

        for (Map.Entry<String, byte[]> entry : capturedFiles.entrySet()) {
            String path = entry.getKey();
            byte[] bytes = entry.getValue();

            context.assertTrue(path.endsWith(".json"), "Path must end with .json: " + path);
            context.assertTrue(path.startsWith("assets/" + SporesShadows.MOD_ID + "/"),
                    "Path must start with mod assets prefix: " + path);

            String jsonString = new String(bytes, StandardCharsets.UTF_8);
            context.assertTrue(jsonString != null && !jsonString.isBlank(),
                    "JSON content must not be blank for " + path);

            // Parse with Gson - throws JsonSyntaxException if invalid
            JsonElement parsedElement = JsonParser.parseString(jsonString);
            context.assertTrue(parsedElement.isJsonObject() || parsedElement.isJsonArray(),
                    "Parsed element must be JsonObject or JsonArray: " + path);

            if (parsedElement.isJsonObject()) {
                JsonObject obj = parsedElement.getAsJsonObject();
                if (path.contains("/models/block/") || path.contains("/models/item/")) {
                    context.assertTrue(obj.has("parent") || obj.has("textures") || obj.has("elements"),
                            "Model JSON must have standard Minecraft model properties: " + path);
                } else if (path.contains("/blockstates/")) {
                    context.assertTrue(obj.has("variants") || obj.has("multipart"),
                            "Blockstate JSON must have variants or multipart: " + path);
                }
            }
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testBlockstatesCoverageForAllWoodTypes(TestContext context) {
        TestResourcePackBuilder builder = new TestResourcePackBuilder();
        MoldyJsonGenerator.generateAll(builder);

        Map<String, byte[]> capturedFiles = builder.capturedFiles;

        for (SporesShadowsConstants.MoldyWoodType woodType : SporesShadowsConstants.WOOD_TYPES) {
            String wood = woodType.name();

            // Planks blockstate
            String planksPath = "assets/" + SporesShadows.MOD_ID + "/blockstates/moldy_" + wood + "_planks.json";
            context.assertTrue(capturedFiles.containsKey(planksPath),
                    "Missing planks blockstate for " + wood + ": " + planksPath);

            // Slab blockstate
            String slabPath = "assets/" + SporesShadows.MOD_ID + "/blockstates/moldy_" + wood + "_slab.json";
            context.assertTrue(capturedFiles.containsKey(slabPath),
                    "Missing slab blockstate for " + wood + ": " + slabPath);

            // Stairs blockstate
            String stairsPath = "assets/" + SporesShadows.MOD_ID + "/blockstates/moldy_" + wood + "_stairs.json";
            context.assertTrue(capturedFiles.containsKey(stairsPath),
                    "Missing stairs blockstate for " + wood + ": " + stairsPath);
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testBambooFenceModelsAndBlockstate(TestContext context) {
        TestResourcePackBuilder builder = new TestResourcePackBuilder();
        MoldyJsonGenerator.generateAll(builder);

        Map<String, byte[]> capturedFiles = builder.capturedFiles;

        // Blockstates
        String moldyBlockstate = "assets/" + SporesShadows.MOD_ID + "/blockstates/moldy_bamboo_fence.json";
        String waxedBlockstate = "assets/" + SporesShadows.MOD_ID + "/blockstates/waxed_bamboo_fence.json";
        context.assertTrue(capturedFiles.containsKey(moldyBlockstate), "Missing moldy_bamboo_fence blockstate");
        context.assertTrue(capturedFiles.containsKey(waxedBlockstate), "Missing waxed_bamboo_fence blockstate");

        // Models for stages 1..3
        for (int stage = 1; stage <= 3; stage++) {
            for (String blockId : new String[]{"moldy_bamboo_fence", "waxed_bamboo_fence"}) {
                String postModel = "assets/" + SporesShadows.MOD_ID + "/models/block/" + blockId + "_post_stage_" + stage + ".json";
                context.assertTrue(capturedFiles.containsKey(postModel), "Missing bamboo fence post model: " + postModel);
                JsonObject postJson = JsonParser.parseString(new String(capturedFiles.get(postModel), StandardCharsets.UTF_8)).getAsJsonObject();
                context.assertEquals(SporesShadows.MOD_ID + ":block/mold/moldy_custom_fence_post", postJson.get("parent").getAsString(),
                        "Bamboo fence post must use moldy_custom_fence_post parent");

                String invModel = "assets/" + SporesShadows.MOD_ID + "/models/block/" + blockId + "_inventory_stage_" + stage + ".json";
                context.assertTrue(capturedFiles.containsKey(invModel), "Missing bamboo fence inventory model: " + invModel);
                JsonObject invJson = JsonParser.parseString(new String(capturedFiles.get(invModel), StandardCharsets.UTF_8)).getAsJsonObject();
                context.assertEquals(SporesShadows.MOD_ID + ":block/mold/moldy_custom_fence_inventory", invJson.get("parent").getAsString(),
                        "Bamboo fence inventory must use moldy_custom_fence_inventory parent");

                for (String dir : new String[]{"north", "east", "south", "west"}) {
                    String sideModel = "assets/" + SporesShadows.MOD_ID + "/models/block/" + blockId + "_side_" + dir + "_stage_" + stage + ".json";
                    context.assertTrue(capturedFiles.containsKey(sideModel), "Missing bamboo fence side model: " + sideModel);
                    JsonObject sideJson = JsonParser.parseString(new String(capturedFiles.get(sideModel), StandardCharsets.UTF_8)).getAsJsonObject();
                    context.assertEquals(SporesShadows.MOD_ID + ":block/mold/moldy_custom_fence_side_" + dir, sideJson.get("parent").getAsString(),
                            "Bamboo fence side " + dir + " must use moldy_custom_fence_side_" + dir + " parent");
                }
            }
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testBambooFenceGateModelsAndBlockstate(TestContext context) {
        TestResourcePackBuilder builder = new TestResourcePackBuilder();
        MoldyJsonGenerator.generateAll(builder);

        Map<String, byte[]> capturedFiles = builder.capturedFiles;

        // Blockstates
        String moldyGateBs = "assets/" + SporesShadows.MOD_ID + "/blockstates/moldy_bamboo_fence_gate.json";
        String waxedGateBs = "assets/" + SporesShadows.MOD_ID + "/blockstates/waxed_bamboo_fence_gate.json";
        context.assertTrue(capturedFiles.containsKey(moldyGateBs), "Missing moldy_bamboo_fence_gate blockstate");
        context.assertTrue(capturedFiles.containsKey(waxedGateBs), "Missing waxed_bamboo_fence_gate blockstate");

        // Verify uvlock is false in bamboo fence gate blockstate
        JsonObject gateBsJson = JsonParser.parseString(new String(capturedFiles.get(moldyGateBs), StandardCharsets.UTF_8)).getAsJsonObject();
        JsonObject variants = gateBsJson.getAsJsonObject("variants");
        for (Map.Entry<String, JsonElement> entry : variants.entrySet()) {
            JsonObject varObj = entry.getValue().getAsJsonObject();
            context.assertTrue(varObj.has("uvlock") && !varObj.get("uvlock").getAsBoolean(),
                    "Bamboo fence gate variants must have uvlock: false: " + entry.getKey());
        }

        // Models for stages 1..3
        for (int stage = 1; stage <= 3; stage++) {
            for (String blockId : new String[]{"moldy_bamboo_fence_gate", "waxed_bamboo_fence_gate"}) {
                String mainModel = "assets/" + SporesShadows.MOD_ID + "/models/block/" + blockId + "_stage_" + stage + ".json";
                context.assertTrue(capturedFiles.containsKey(mainModel), "Missing bamboo fence gate model: " + mainModel);
                JsonObject mainJson = JsonParser.parseString(new String(capturedFiles.get(mainModel), StandardCharsets.UTF_8)).getAsJsonObject();
                context.assertEquals(SporesShadows.MOD_ID + ":block/mold/moldy_template_custom_fence_gate", mainJson.get("parent").getAsString(),
                        "Bamboo fence gate must use moldy_template_custom_fence_gate parent");
                context.assertEquals("minecraft:block/bamboo_fence_gate", mainJson.getAsJsonObject("textures").get("texture").getAsString(),
                        "Bamboo fence gate texture must be bamboo_fence_gate");

                String openModel = "assets/" + SporesShadows.MOD_ID + "/models/block/" + blockId + "_open_stage_" + stage + ".json";
                context.assertTrue(capturedFiles.containsKey(openModel), "Missing bamboo fence gate open model: " + openModel);
                JsonObject openJson = JsonParser.parseString(new String(capturedFiles.get(openModel), StandardCharsets.UTF_8)).getAsJsonObject();
                context.assertEquals(SporesShadows.MOD_ID + ":block/mold/moldy_template_custom_fence_gate_open", openJson.get("parent").getAsString(),
                        "Bamboo fence gate open must use moldy_template_custom_fence_gate_open parent");

                String wallModel = "assets/" + SporesShadows.MOD_ID + "/models/block/" + blockId + "_wall_stage_" + stage + ".json";
                context.assertTrue(capturedFiles.containsKey(wallModel), "Missing bamboo fence gate wall model: " + wallModel);
                JsonObject wallJson = JsonParser.parseString(new String(capturedFiles.get(wallModel), StandardCharsets.UTF_8)).getAsJsonObject();
                context.assertEquals(SporesShadows.MOD_ID + ":block/mold/moldy_template_custom_fence_gate_wall", wallJson.get("parent").getAsString(),
                        "Bamboo fence gate wall must use moldy_template_custom_fence_gate_wall parent");

                String wallOpenModel = "assets/" + SporesShadows.MOD_ID + "/models/block/" + blockId + "_wall_open_stage_" + stage + ".json";
                context.assertTrue(capturedFiles.containsKey(wallOpenModel), "Missing bamboo fence gate wall open model: " + wallOpenModel);
                JsonObject wallOpenJson = JsonParser.parseString(new String(capturedFiles.get(wallOpenModel), StandardCharsets.UTF_8)).getAsJsonObject();
                context.assertEquals(SporesShadows.MOD_ID + ":block/mold/moldy_template_custom_fence_gate_wall_open", wallOpenJson.get("parent").getAsString(),
                        "Bamboo fence gate wall open must use moldy_template_custom_fence_gate_wall_open parent");
            }
        }

        context.complete();
    }
}
