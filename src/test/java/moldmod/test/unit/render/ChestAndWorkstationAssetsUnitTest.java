package moldmod.test.unit.render;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ChestAndWorkstationAssetsUnitTest {

    private static final String ASSETS_PATH = "src/main/resources/assets/spores--shadows";

    @Test
    @DisplayName("Verify all 18 custom chest entity textures exist, are 64x64, and valid RGBA")
    void testAllChestTexturesExistAndValid() throws IOException {
        String[] types = {"normal", "normal_left", "normal_right", "trapped", "trapped_left", "trapped_right"};
        File chestDir = new File(ASSETS_PATH, "textures/entity/chest");
        assertTrue(chestDir.exists(), "Chest textures directory must exist: " + chestDir.getAbsolutePath());

        for (String type : types) {
            for (int stage = 1; stage <= 3; stage++) {
                String filename = type + "_stage_" + stage + ".png";
                File file = new File(chestDir, filename);
                assertTrue(file.exists(), "Chest texture file missing: " + filename);

                BufferedImage image = ImageIO.read(file);
                assertNotNull(image, "Failed to decode PNG image: " + filename);
                assertEquals(64, image.getWidth(), "Width of " + filename + " must be 64px");
                assertEquals(64, image.getHeight(), "Height of " + filename + " must be 64px");
            }
        }
    }

    @Test
    @DisplayName("Verify all 18 moldy workstation GUI overlay textures exist and are valid")
    void testAllWorkstationGuiOverlayTexturesExistAndValid() throws IOException {
        String[] guiTypes = {
                "mold_crafting_overlay",
                "mold_container_9x3_overlay",
                "mold_container_9x6_overlay",
                "mold_loom_overlay",
                "mold_cartography_table_overlay",
                "mold_book_overlay"
        };
        File guiDir = new File(ASSETS_PATH, "textures/gui");
        assertTrue(guiDir.exists(), "GUI textures directory must exist: " + guiDir.getAbsolutePath());

        for (String gui : guiTypes) {
            for (int stage = 1; stage <= 3; stage++) {
                String filename = gui + "_stage_" + stage + ".png";
                File file = new File(guiDir, filename);
                assertTrue(file.exists(), "GUI overlay texture missing: " + filename);

                BufferedImage image = ImageIO.read(file);
                assertNotNull(image, "Failed to decode PNG image: " + filename);
                assertTrue(image.getWidth() > 0 && image.getHeight() > 0, "Dimensions must be positive for " + filename);
            }
        }
    }

    @Test
    @DisplayName("Verify spores--shadows.mixins.json includes ChestBlockEntityRendererMixin and client mixins")
    void testClientMixinsConfiguration() throws IOException {
        File mixinConfig = new File("src/main/resources/spores--shadows.mixins.json");
        assertTrue(mixinConfig.exists(), "spores--shadows.mixins.json must exist");

        try (FileReader reader = new FileReader(mixinConfig, StandardCharsets.UTF_8)) {
            JsonObject json = new Gson().fromJson(reader, JsonObject.class);
            assertTrue(json.has("client"), "Mixins config must define 'client' array");

            List<String> clientMixins = new Gson().fromJson(json.get("client"), new com.google.gson.reflect.TypeToken<List<String>>() {}.getType());
            assertTrue(clientMixins.stream().anyMatch(m -> m.endsWith("ChestBlockEntityRendererMixin")), "Must register ChestBlockEntityRendererMixin");
            assertTrue(clientMixins.stream().anyMatch(m -> m.endsWith("HandledScreenMixin")), "Must register HandledScreenMixin");
            assertTrue(clientMixins.stream().anyMatch(m -> m.endsWith("TexturedRenderLayersMixin")), "Must register TexturedRenderLayersMixin");
            assertTrue(clientMixins.stream().anyMatch(m -> m.endsWith("BookScreenMixin")), "Must register BookScreenMixin");
        }
    }

    @Test
    @DisplayName("Verify chests atlas json references all moldy chest textures")
    void testChestsAtlasRegistration() throws IOException {
        File atlasFile = new File("src/main/resources/assets/minecraft/atlases/chests.json");
        assertTrue(atlasFile.exists(), "chests.json atlas file must exist");

        try (FileReader reader = new FileReader(atlasFile, StandardCharsets.UTF_8)) {
            JsonObject json = new Gson().fromJson(reader, JsonObject.class);
            assertTrue(json.has("sources"), "Atlas must contain 'sources'");
            String atlasContent = json.toString();

            for (String prefix : new String[]{"normal", "trapped"}) {
                for (String suffix : new String[]{"", "_left", "_right"}) {
                    for (int stage = 1; stage <= 3; stage++) {
                        String id = "spores--shadows:entity/chest/" + prefix + suffix + "_stage_" + stage;
                        assertTrue(atlasContent.contains(id), "Atlas must include entry for: " + id);
                    }
                }
            }
        }
    }
}
