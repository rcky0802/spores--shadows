package moldmod.test.unit.integration.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import moldmod.client.config.ModMenuIntegration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

public class ModMenuIntegrationUnitTest {

    @Test
    @DisplayName("ModMenuIntegration implements ModMenuApi correctly")
    void testModMenuApiImplementation() {
        ModMenuIntegration integration = new ModMenuIntegration();
        assertTrue(integration instanceof ModMenuApi, "ModMenuIntegration must implement ModMenuApi");
    }

    @Test
    @DisplayName("ModMenuIntegration provides a non-null ConfigScreenFactory")
    void testConfigScreenFactoryNotNull() {
        ModMenuIntegration integration = new ModMenuIntegration();
        ConfigScreenFactory<?> factory = integration.getModConfigScreenFactory();
        assertNotNull(factory, "ConfigScreenFactory provided by ModMenuIntegration must not be null");
    }

    @Test
    @DisplayName("fabric.mod.json contains the modmenu entrypoint pointing to ModMenuIntegration")
    void testModMenuEntrypointInFabricModJson() {
        java.nio.file.Path mainModJson = java.nio.file.Path.of("src/main/resources/fabric.mod.json");
        assertTrue(java.nio.file.Files.exists(mainModJson), "src/main/resources/fabric.mod.json must exist");

        try {
            String content = java.nio.file.Files.readString(mainModJson, StandardCharsets.UTF_8);
            assertTrue(content.contains("\"modmenu\""), "fabric.mod.json must declare 'modmenu' entrypoint");
            assertTrue(content.contains("moldmod.client.config.ModMenuIntegration"),
                    "fabric.mod.json must point 'modmenu' entrypoint to ModMenuIntegration class");
        } catch (Exception e) {
            fail("Failed reading fabric.mod.json: " + e.getMessage());
        }
    }
}
