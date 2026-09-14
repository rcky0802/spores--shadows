package moldmod.resource;

import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import moldmod.SporesShadows;
import moldmod.SporesShadowsConstants;
import moldmod.SporesShadowsConstants.MoldStage;
import moldmod.SporesShadowsConstants.MoldyWoodType;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public final class MoldyResourceGenerator {

    private MoldyResourceGenerator() {
    }

    public static void initialize() {
        // Tells Polymer to include our mod's base resources in the virtual pack
        PolymerResourcePackUtils.addModAssets(SporesShadows.MOD_ID);

        // Register a virtual asset source
        PolymerResourcePackUtils.RESOURCE_PACK_CREATION_EVENT.register(builder -> {
            
            // Generate ALL block JSONs (models, blockstates) in RAM!
            MoldyJsonGenerator.generateAll(builder);

            // Generate Spore Mask item model
            String sporeMaskModelJson = """
                {
                  "parent": "minecraft:item/generated",
                  "textures": {
                    "layer0": "spores--shadows:item/gear/spore_mask"
                  }
                }
                """;
            builder.addData("assets/" + SporesShadows.MOD_ID + "/models/item/spore_mask.json", sporeMaskModelJson.getBytes(StandardCharsets.UTF_8));
            
            for (MoldyWoodType moldyWoodType : SporesShadowsConstants.WOOD_TYPES) {
                String wood = moldyWoodType.name();
                for (String itemType : new String[]{"door", "sign", "hanging_sign"}) {
                    for (MoldStage stageEnum : MoldStage.values()) {
                        int i = stageEnum.getId();
                        for (String prefix : new String[]{"moldy_", "waxed_"}) {
                            String itemName;
                            if (prefix.equals("waxed_")) {
                                itemName = i == 0 ? "waxed_" + wood + "_" + itemType : "waxed_" + stageEnum.getName() + "_" + wood + "_" + itemType;
                            } else {
                                if (i == 0) continue;
                                itemName = stageEnum.getName() + "_" + wood + "_" + itemType;
                            }

                            String texName = i == 0 ? "minecraft:item/" + wood + "_" + itemType : SporesShadows.MOD_ID + ":item/" + stageEnum.getName() + "_" + wood + "_" + itemType;
                            String layer0 = texName;

                            // 1. JSON MODEL GENERATION (In Memory)
                            String modelJson = """
                                {
                                  "parent": "minecraft:item/generated",
                                  "textures": {
                                    "layer0": "%s"
                                  }
                                }
                                """.formatted(layer0);

                            builder.addData("assets/" + SporesShadows.MOD_ID + "/models/item/" + itemName + ".json", modelJson.getBytes(StandardCharsets.UTF_8));

                            // If it is waxed (stage 0) or we are generating for waxed_ prefix (stage > 0), there's no need to generate a new masked texture!
                            if (i == 0 || prefix.equals("waxed_")) continue;

                            // 2. TEXTURE GENERATION (In Memory with Alpha Masking)
                            try {
                                InputStream itemIn = MoldyResourceGenerator.class.getResourceAsStream("/assets/minecraft/textures/item/" + wood + "_" + itemType + ".png");
                                InputStream moldIn = MoldyResourceGenerator.class.getResourceAsStream("/assets/" + SporesShadows.MOD_ID + "/textures/block/mold/mold_stage_" + i + ".png");

                                if (itemIn != null && moldIn != null) {
                                    BufferedImage baseImage = ImageIO.read(itemIn);
                                    BufferedImage moldImage = ImageIO.read(moldIn);

                                    BufferedImage resultImage = applyAlphaMask(baseImage, moldImage);

                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    ImageIO.write(resultImage, "png", baos);
                                    byte[] imageBytes = baos.toByteArray();

                                    // Inject the virtual texture!
                                    builder.addData("assets/" + SporesShadows.MOD_ID + "/textures/item/" + itemName + ".png", imageBytes);
                                }
                            } catch (Exception e) {
                                SporesShadows.LOGGER.error("Error during dynamic generation of the item {}: {}", itemName, e.getMessage(), e);
                            }
                        }
                    }
                }
            }

            // Generate alpha-masked textures for ladders (tainted, moldy, rotten)
            for (MoldStage stageEnum : MoldStage.values()) {
                int i = stageEnum.getId();
                if (i == 0) continue;
                String itemName = stageEnum.getName() + "_ladder";

                try {
                    InputStream itemIn = MoldyResourceGenerator.class.getResourceAsStream("/assets/minecraft/textures/block/ladder.png");
                    InputStream moldIn = MoldyResourceGenerator.class.getResourceAsStream("/assets/" + SporesShadows.MOD_ID + "/textures/block/mold/mold_stage_" + i + ".png");

                    if (itemIn != null && moldIn != null) {
                        BufferedImage baseImage = ImageIO.read(itemIn);
                        BufferedImage moldImage = ImageIO.read(moldIn);

                        BufferedImage resultImage = applyAlphaMask(baseImage, moldImage);

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ImageIO.write(resultImage, "png", baos);
                        byte[] imageBytes = baos.toByteArray();

                        builder.addData("assets/" + SporesShadows.MOD_ID + "/textures/item/" + itemName + ".png", imageBytes);
                    }
                } catch (Exception e) {
                    SporesShadows.LOGGER.error("Error during dynamic generation of ladder item {}: {}", itemName, e.getMessage(), e);
                }
            }
        });

        PolymerResourcePackUtils.markAsRequired();
    }

    public static BufferedImage applyAlphaMask(BufferedImage baseImage, BufferedImage moldImage) {
        BufferedImage resultImage = new BufferedImage(baseImage.getWidth(), baseImage.getHeight(), BufferedImage.TYPE_INT_ARGB);

        for (int x = 0; x < baseImage.getWidth(); x++) {
            for (int y = 0; y < baseImage.getHeight(); y++) {
                int basePixel = baseImage.getRGB(x, y);
                int baseAlpha = (basePixel >> 24) & 0xff;

                if (baseAlpha > 0) { // If base pixel is NOT completely transparent
                    int moldPixel = moldImage.getRGB(x % moldImage.getWidth(), y % moldImage.getHeight());
                    int moldAlpha = (moldPixel >> 24) & 0xff;

                    // ALPHA MASKING
                    if (moldAlpha > 20) { // If there is visible mold
                        resultImage.setRGB(x, y, moldPixel);
                    } else {
                        resultImage.setRGB(x, y, basePixel);
                    }
                } else {
                    resultImage.setRGB(x, y, 0x00000000);
                }
            }
        }
        return resultImage;
    }
}
