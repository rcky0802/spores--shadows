package moldmod.test.unit.integration.polymer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class MoldyTextureGeneratorUnitTest {

    @Test
    @DisplayName("Verify in-memory PNG texture generation with alpha masking and ImageIO encoding/decoding")
    public void testDoorAlphaMaskingPipeline() throws IOException {
        int width = 16;
        int height = 32;

        // 1. Mock door texture (16x32 with transparent upper window parts)
        BufferedImage doorImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (x >= 4 && x <= 11 && y >= 4 && y <= 11) {
                    doorImage.setRGB(x, y, 0x00000000); // transparent window
                } else {
                    doorImage.setRGB(x, y, 0xFF8B5A2B); // solid wood brown
                }
            }
        }

        // 2. Mock mold mask texture (16x16 with greenish mold spots)
        BufferedImage moldImage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                if (x % 3 == 0) {
                    moldImage.setRGB(x, y, 0xFF2E8B57); // visible mold (alpha 255)
                } else {
                    moldImage.setRGB(x, y, 0x00000000); // no mold (alpha 0)
                }
            }
        }

        // 3. Execute the exact alpha masking loop from MoldyResourceGenerator
        BufferedImage resultImage = new BufferedImage(doorImage.getWidth(), doorImage.getHeight(), BufferedImage.TYPE_INT_ARGB);

        for (int x = 0; x < doorImage.getWidth(); x++) {
            for (int y = 0; y < doorImage.getHeight(); y++) {
                int doorPixel = doorImage.getRGB(x, y);
                int doorAlpha = (doorPixel >> 24) & 0xff;

                if (doorAlpha > 0) { // If the door pixel is NOT completely transparent
                    int moldPixel = moldImage.getRGB(x % moldImage.getWidth(), y % moldImage.getHeight());
                    int moldAlpha = (moldPixel >> 24) & 0xff;

                    // ALPHA MASKING
                    if (moldAlpha > 20) {
                        resultImage.setRGB(x, y, moldPixel);
                    } else {
                        resultImage.setRGB(x, y, doorPixel);
                    }
                } else {
                    resultImage.setRGB(x, y, 0x00000000);
                }
            }
        }

        // 4. Encode to PNG byte stream
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        boolean writeSuccess = ImageIO.write(resultImage, "png", baos);
        assertTrue(writeSuccess, "ImageIO must successfully write PNG format");

        byte[] imageBytes = baos.toByteArray();
        assertNotNull(imageBytes);
        assertTrue(imageBytes.length > 0, "Generated PNG bytes must not be empty");

        // 5. Decode back with ImageIO to verify PNG validity
        BufferedImage decodedImage = ImageIO.read(new ByteArrayInputStream(imageBytes));
        assertNotNull(decodedImage, "Decoded PNG image must not be null");
        assertEquals(width, decodedImage.getWidth(), "Decoded image width must match original");
        assertEquals(height, decodedImage.getHeight(), "Decoded image height must match original");

        // Transparent part must remain transparent
        int transparentPixel = decodedImage.getRGB(6, 6);
        assertEquals(0, (transparentPixel >> 24) & 0xff, "Transparent window pixel must remain transparent");

        // Solid part with mold must have mold RGB
        int moldPixel = decodedImage.getRGB(0, 0);
        assertEquals(0xFF2E8B57, moldPixel, "Pixel (0,0) should have mold color applied");
    }
}
