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

        // 3. Execute alpha masking via MoldyResourceGenerator
        BufferedImage resultImage = moldmod.resource.MoldyResourceGenerator.applyAlphaMask(doorImage, moldImage);

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

    @Test
    @DisplayName("Verify in-memory PNG texture generation with alpha masking for 16x16 signs and hanging signs")
    public void testSignAlphaMaskingPipeline() throws IOException {
        int width = 16;
        int height = 16;

        // 1. Mock 16x16 sign texture (sign board in center 12x8, transparent margins, pole below)
        BufferedImage signImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (x >= 2 && x <= 13 && y >= 2 && y <= 9) {
                    signImage.setRGB(x, y, 0xFFBC9862); // sign board
                } else if (x >= 7 && x <= 8 && y >= 10 && y <= 14) {
                    signImage.setRGB(x, y, 0xFF8B5A2B); // sign pole
                } else {
                    signImage.setRGB(x, y, 0x00000000); // transparent background
                }
            }
        }

        // 2. Mock mold mask texture (16x16 with greenish mold spots)
        BufferedImage moldImage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                if ((x + y) % 2 == 0) {
                    moldImage.setRGB(x, y, 0xFF2E8B57); // visible mold (alpha 255)
                } else {
                    moldImage.setRGB(x, y, 0x00000000); // no mold
                }
            }
        }

        // 3. Execute alpha masking via MoldyResourceGenerator
        BufferedImage resultImage = moldmod.resource.MoldyResourceGenerator.applyAlphaMask(signImage, moldImage);

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
        assertEquals(width, decodedImage.getWidth(), "Decoded image width must match original 16x16");
        assertEquals(height, decodedImage.getHeight(), "Decoded image height must match original 16x16");

        // Transparent border must remain completely transparent (alpha == 0)
        int transparentPixel = decodedImage.getRGB(0, 0);
        assertEquals(0, (transparentPixel >> 24) & 0xff, "Margin pixel (0,0) must remain completely transparent");

        // Sign board pixel with mold ((2+2)%2 == 0) must have mold applied
        int moldPixel = decodedImage.getRGB(2, 2);
        assertEquals(0xFF2E8B57, moldPixel, "Sign pixel (2,2) with mold must have mold RGB");

        // Sign board pixel without mold ((2+3)%2 != 0) must keep wood color
        int woodPixel = decodedImage.getRGB(2, 3);
        assertEquals(0xFFBC9862, woodPixel, "Sign pixel (2,3) without mold must keep sign wood color");
    }
}
