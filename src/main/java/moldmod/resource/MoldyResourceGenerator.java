package moldmod.resource;

import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class MoldyResourceGenerator {

    public static void initialize() {
        // Indica a Polymer di includere le risorse base della nostra mod nel pacchetto virtuale
        PolymerResourcePackUtils.addModAssets("spores--shadows");

        // Registriamo una sorgente di asset virtuale
        PolymerResourcePackUtils.RESOURCE_PACK_CREATION_EVENT.register(builder -> {
            
            String[] woods = moldmod.SporesShadows.WOODS;
            String[] stages = {"waxed", "tainted", "moldy", "rotten"};
            
            // Genera TUTTI i JSON dei blocchi (modelli, blockstates) in RAM!
            MoldyJsonGenerator.generateAll(builder, woods);
            
            for (String wood : woods) {
                for (int i = 0; i < stages.length; i++) {
                    String stageName = stages[i];
                    String itemName = stageName + "_" + wood + "_door";
                    
                    String layer0 = i == 0 ? "minecraft:item/" + wood + "_door" : "spores--shadows:item/" + itemName;
                    // 1. GENERAZIONE DEL MODELLO JSON (In Memoria)
                    String modelJson = """
                        {
                          "parent": "minecraft:item/generated",
                          "textures": {
                            "layer0": "%s"
                          }
                        }
                        """.formatted(layer0);
                    
                    builder.addData("assets/spores--shadows/models/item/" + itemName + ".json", modelJson.getBytes());

                    // Se è waxed (stage 0), non serve generare la texture mascherata, usa quella vanilla!
                    if (i == 0) continue;

                    // 2. GENERAZIONE DELLA TEXTURE (In Memoria con Alpha Masking)
                    try {
                        InputStream doorIn = MoldyResourceGenerator.class.getResourceAsStream("/assets/minecraft/textures/item/" + wood + "_door.png");
                        InputStream moldIn = MoldyResourceGenerator.class.getResourceAsStream("/assets/spores--shadows/textures/block/mold_stage_" + i + ".png");
                        
                        if (doorIn != null && moldIn != null) {
                            BufferedImage doorImage = ImageIO.read(doorIn);
                            BufferedImage moldImage = ImageIO.read(moldIn);
                            
                            BufferedImage resultImage = new BufferedImage(doorImage.getWidth(), doorImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
                            
                            for (int x = 0; x < doorImage.getWidth(); x++) {
                                for (int y = 0; y < doorImage.getHeight(); y++) {
                                    int doorPixel = doorImage.getRGB(x, y);
                                    int doorAlpha = (doorPixel >> 24) & 0xff;
                                    
                                    if (doorAlpha > 0) { // Se il pixel della porta NON è completamente trasparente
                                        int moldPixel = moldImage.getRGB(x % moldImage.getWidth(), y % moldImage.getHeight());
                                        int moldAlpha = (moldPixel >> 24) & 0xff;
                                        
                                        // ALPHA MASKING
                                        if (moldAlpha > 20) { // Se c'è della muffa visibile
                                            // Semplice sovrascrittura (o potremmo fare vero alpha blending)
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
                            
                            // Iniettiamo la texture virtuale!
                            builder.addData("assets/spores--shadows/textures/item/" + itemName + ".png", imageBytes);
                        }
                    } catch (Exception e) {
                        System.err.println("Errore durante la generazione dinamica della porta: " + itemName);
                        e.printStackTrace();
                    }
                }
            }
        });
        
        PolymerResourcePackUtils.markAsRequired();
    }
}
