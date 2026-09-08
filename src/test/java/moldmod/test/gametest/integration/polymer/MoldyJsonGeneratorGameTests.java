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
}
