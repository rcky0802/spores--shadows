package moldmod.resource;

import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class MoldyResourceGenerator {

    public static void initialize() {
        // Tells Polymer to include our mod's base resources in the virtual pack
        PolymerResourcePackUtils.addModAssets(moldmod.SporesShadows.MOD_ID);

        // Register a virtual asset source
        PolymerResourcePackUtils.RESOURCE_PACK_CREATION_EVENT.register(builder -> {
            
            // Generate ALL block JSONs (models, blockstates) in RAM!
            MoldyJsonGenerator.generateAll(builder);
            
            for (moldmod.SporesShadowsConstants.MoldyWoodType moldyWoodType : moldmod.SporesShadowsConstants.WOOD_TYPES) {
                String wood = moldyWoodType.name();
                for (moldmod.SporesShadowsConstants.MoldStage stageEnum : moldmod.SporesShadowsConstants.MoldStage.values()) {
                    int i = stageEnum.getId();
                    for (String prefix : new String[]{"moldy_", "waxed_"}) {
                        String itemName;
                        if (prefix.equals("waxed_")) {
                            itemName = i == 0 ? "waxed_" + wood + "_door" : "waxed_" + stageEnum.getName() + "_" + wood + "_door";
                        } else {
                            if (i == 0) continue;
                            itemName = stageEnum.getName() + "_" + wood + "_door";
                        }
                        
                        String texName = i == 0 ? "minecraft:item/" + wood + "_door" : moldmod.SporesShadows.MOD_ID + ":item/" + stageEnum.getName() + "_" + wood + "_door";
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
                        
                        builder.addData("assets/" + moldmod.SporesShadows.MOD_ID + "/models/item/" + itemName + ".json", modelJson.getBytes());
                        
                        // If it is waxed (stage 0) or we are generating for waxed_ prefix (stage > 0), there's no need to generate a new masked texture!
                        if (i == 0 || prefix.equals("waxed_")) continue;

                    // 2. TEXTURE GENERATION (In Memory with Alpha Masking)
                    try {
                        InputStream doorIn = MoldyResourceGenerator.class.getResourceAsStream("/assets/minecraft/textures/item/" + wood + "_door.png");
                        InputStream moldIn = MoldyResourceGenerator.class.getResourceAsStream("/assets/" + moldmod.SporesShadows.MOD_ID + "/textures/block/mold_stage_" + i + ".png");
                        
                        if (doorIn != null && moldIn != null) {
                            BufferedImage doorImage = ImageIO.read(doorIn);
                            BufferedImage moldImage = ImageIO.read(moldIn);
                            
                            BufferedImage resultImage = new BufferedImage(doorImage.getWidth(), doorImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
                            
                            for (int x = 0; x < doorImage.getWidth(); x++) {
                                for (int y = 0; y < doorImage.getHeight(); y++) {
                                    int doorPixel = doorImage.getRGB(x, y);
                                    int doorAlpha = (doorPixel >> 24) & 0xff;
                                    
                                    if (doorAlpha > 0) { // If the door pixel is NOT completely transparent
                                        int moldPixel = moldImage.getRGB(x % moldImage.getWidth(), y % moldImage.getHeight());
                                        int moldAlpha = (moldPixel >> 24) & 0xff;
                                        
                                        // ALPHA MASKING
                                        if (moldAlpha > 20) { // If there is visible mold
                                            // Simple overwrite (or we could do true alpha blending)
                                            resultImage.setRGB(x, y, moldPixel);
                                        } else {
                                            resultImage.setRGB(x, y, doorPixel);
                                        }
                                    } else {
                                        resultImage.setRGB(x, y, 0x00000000);
                                    }
                                }
                            }
                            
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            ImageIO.write(resultImage, "png", baos);
                            byte[] imageBytes = baos.toByteArray();
                            
                            // Inject the virtual texture!
                            builder.addData("assets/" + moldmod.SporesShadows.MOD_ID + "/textures/item/" + itemName + ".png", imageBytes);
                        }
                    } catch (Exception e) {
                        System.err.println("Error during dynamic generation of the door: " + itemName);
                        e.printStackTrace();
                    }
                    }
                }
            }
        });
        
        PolymerResourcePackUtils.markAsRequired();
    }
}
