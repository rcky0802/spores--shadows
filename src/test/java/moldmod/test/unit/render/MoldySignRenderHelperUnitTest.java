package moldmod.test.unit.render;

import moldmod.client.render.MoldySignRenderHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MoldySignRenderHelperUnitTest {

    @Test
    @DisplayName("Mold overlay textures are correctly mapped for stages 1 to 3")
    void testMoldTexturesMapping() {
        assertNull(MoldySignRenderHelper.MOLD_TEXTURES[0], "Stage 0 (clean/waxed) should have no overlay texture");
        assertEquals("spores--shadows:textures/block/mold/mold_stage_1.png",
                MoldySignRenderHelper.MOLD_TEXTURES[1].toString(),
                "Stage 1 must map to mold_stage_1.png");
        assertEquals("spores--shadows:textures/block/mold/mold_stage_2.png",
                MoldySignRenderHelper.MOLD_TEXTURES[2].toString(),
                "Stage 2 must map to mold_stage_2.png");
        assertEquals("spores--shadows:textures/block/mold/mold_stage_3.png",
                MoldySignRenderHelper.MOLD_TEXTURES[3].toString(),
                "Stage 3 must map to mold_stage_3.png");
    }

    @Test
    @DisplayName("Standard Sign geometry bounds match 24x12 model units in text coordinate space")
    void testStandardSignBounds() {
        assertEquals(-48.0f, MoldySignRenderHelper.SIGN_MIN_X, 0.001f);
        assertEquals(48.0f, MoldySignRenderHelper.SIGN_MAX_X, 0.001f);
        assertEquals(-24.0f, MoldySignRenderHelper.SIGN_MIN_Y, 0.001f);
        assertEquals(24.0f, MoldySignRenderHelper.SIGN_MAX_Y, 0.001f);

        float width = MoldySignRenderHelper.SIGN_MAX_X - MoldySignRenderHelper.SIGN_MIN_X;
        float height = MoldySignRenderHelper.SIGN_MAX_Y - MoldySignRenderHelper.SIGN_MIN_Y;
        assertEquals(96.0f, width, 0.001f);
        assertEquals(48.0f, height, 0.001f);
        assertEquals(2.0f, width / height, 0.001f, "Standard sign aspect ratio must be 2:1");
    }

    @Test
    @DisplayName("Hanging Sign geometry bounds match 14x10 model units in text coordinate space")
    void testHangingSignBounds() {
        assertEquals(-31.0f, MoldySignRenderHelper.HANGING_MIN_X, 0.001f);
        assertEquals(31.0f, MoldySignRenderHelper.HANGING_MAX_X, 0.001f);
        assertEquals(-22.5f, MoldySignRenderHelper.HANGING_MIN_Y, 0.001f);
        assertEquals(21.5f, MoldySignRenderHelper.HANGING_MAX_Y, 0.001f);

        float width = MoldySignRenderHelper.HANGING_MAX_X - MoldySignRenderHelper.HANGING_MIN_X;
        float height = MoldySignRenderHelper.HANGING_MAX_Y - MoldySignRenderHelper.HANGING_MIN_Y;
        assertEquals(62.0f, width, 0.001f);
        assertEquals(44.0f, height, 0.001f);
        assertTrue(width > 60.0f, "Width must comfortably cover hanging sign max text width of 60");
        assertTrue(height > 36.0f, "Height must comfortably cover 4 lines of 9px hanging sign text (36px)");
    }

    @Test
    @DisplayName("Text overlay depth offset is elevated above text plane (Z + 0.005f)")
    void testTextOverlayZOffset() {
        assertEquals(0.5f, MoldySignRenderHelper.TEXT_OVERLAY_Z_OFFSET, 0.001f,
                "Z offset in text coordinates must correspond to ~0.005f blocks in front of the text plane");
    }
}
