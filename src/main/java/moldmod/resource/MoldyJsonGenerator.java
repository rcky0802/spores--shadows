package moldmod.resource;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import eu.pb4.polymer.resourcepack.api.ResourcePackBuilder;
import moldmod.SporesShadows;
import moldmod.SporesShadowsConstants;
import moldmod.SporesShadowsConstants.MoldStage;
import moldmod.SporesShadowsConstants.MoldyWoodType;

import java.nio.charset.StandardCharsets;

public final class MoldyJsonGenerator {

    private MoldyJsonGenerator() {
    }

    public static void generateAll(ResourcePackBuilder builder) {
        for (MoldyWoodType moldyWoodType : SporesShadowsConstants.WOOD_TYPES) {
            String wood = moldyWoodType.name();
            String logName = moldyWoodType.getLogName();
            String woodName = moldyWoodType.getWoodName();

            genPlanks(builder, wood, wood);
            genLog(builder, wood, logName, false);
            genLog(builder, wood, "stripped_" + logName, false);
            if (woodName != null) {
                genLog(builder, wood, woodName, true);
                genLog(builder, wood, "stripped_" + woodName, true);
            }
            genSlab(builder, wood, wood);
            genStairs(builder, wood, wood);
            genDoor(builder, wood, wood);
            genTrapdoor(builder, wood, wood);
            genFence(builder, wood, wood);
            genGate(builder, wood, wood);
            genPressurePlate(builder, wood, wood);
            genButton(builder, wood, wood);
            genSign(builder, wood, wood);
            genHangingSign(builder, wood, wood);

            if (moldyWoodType.isBamboo()) {
                genMosaic(builder, wood);
                genMosaicSlab(builder, wood);
                genMosaicStairs(builder, wood);
            }
        }

        genBookshelf(builder);
        genChiseledBookshelf(builder);
        genLadder(builder);
        genNoteBlock(builder);
        genJukebox(builder);
        genCraftingTable(builder);
        genFletchingTable(builder);
        genBarrel(builder);
        genComposter(builder);
        genChests(builder);
        genCartographyTable(builder);
        genLoom(builder);
        genLectern(builder);
    }

    private static void write(ResourcePackBuilder builder, String path, JsonObject json) {
        builder.addData("assets/" + SporesShadows.MOD_ID + "/" + path + ".json", json.toString().getBytes(StandardCharsets.UTF_8));
    }

    private static void genItemModel(ResourcePackBuilder builder, String baseName, String parentModelName, int stage, boolean is2d, String idPrefix) {
        String stageName = "";
        for (MoldStage ms : MoldStage.values()) {
            if (ms.getId() == stage) stageName = ms.getName();
        }
        String itemName;
        if (idPrefix.equals("waxed_")) {
            itemName = stage == 0 ? "waxed_" + baseName : "waxed_" + stageName + "_" + baseName;
        } else {
            itemName = stageName + "_" + baseName;
        }
        JsonObject json = new JsonObject();
        if (is2d) {
            // For doors, we are already doing this in MoldyResourceGenerator with the custom PNG blending!
            if (!baseName.contains("door") || stage == 0) {
                json.addProperty("parent", "minecraft:item/generated");
                JsonObject textures = new JsonObject();
                if (stage == 0) {
                    textures.addProperty("layer0", "minecraft:item/" + baseName);
                } else {
                    textures.addProperty("layer0", moldmod.SporesShadows.MOD_ID + ":item/moldy_" + baseName + "_stage_" + stage);
                }
                json.add("textures", textures);
            } else {
                return; // Handled by image generator
            }
        } else {
            if (stage == 0) {
                json.addProperty("parent", parentModelName);
            } else {
                json.addProperty("parent", moldmod.SporesShadows.MOD_ID + ":block/" + parentModelName);
            }
        }
        write(builder, "models/item/" + itemName, json);
    }

    private static String[] getCommonProps() {
        return new String[]{
            "structural=false,waxed=false",
            "structural=true,waxed=false",
            "structural=false,waxed=true",
            "structural=true,waxed=true"
        };
    }

    private static void genPlanks(ResourcePackBuilder builder, String wood, String prefix) {
        for (String idPrefix : new String[]{"moldy_", "waxed_"}) {
            String blockId = idPrefix + prefix + "_planks";
            JsonObject variants = new JsonObject();
            for (moldmod.SporesShadowsConstants.MoldStage moldStage : moldmod.SporesShadowsConstants.MoldStage.values()) { int stage = moldStage.getId();
                String tex = "minecraft:block/" + prefix + "_planks";
                if (stage > 0) {
                    JsonObject model = new JsonObject();
                    model.addProperty("parent", moldmod.SporesShadows.MOD_ID + ":block/mold/moldy_cube_all");
                    JsonObject textures = new JsonObject();
                    textures.addProperty("all", tex);
                    textures.addProperty("overlay", moldmod.SporesShadows.MOD_ID + ":block/mold/mold_stage_" + stage);
                    model.add("textures", textures);
                    write(builder, "models/block/" + blockId + "_stage_" + stage, model);
                }
                String itemParent = stage == 0 ? "minecraft:block/" + prefix + "_planks" : blockId + "_stage_" + stage;
                if (idPrefix.equals("waxed_") || stage > 0) { genItemModel(builder, prefix + "_planks", itemParent, stage, false, idPrefix); }
                
                String m = stage == 0 ? "minecraft:block/" + prefix + "_planks" : moldmod.SporesShadows.MOD_ID + ":block/" + blockId + "_stage_" + stage;
                for (String common : getCommonProps()) {
                    JsonObject v = new JsonObject();
                    v.addProperty("model", m);
                    variants.add("stage=" + stage + "," + common, v);
                }
            }
            JsonObject bs = new JsonObject();
            bs.add("variants", variants);
            write(builder, "blockstates/" + blockId, bs);
        }
    }

    private static void genLog(ResourcePackBuilder builder, String wood, String logName, boolean isWood) {
        for (String idPrefix : new String[]{"moldy_", "waxed_"}) {
            String blockId = idPrefix + logName;
            JsonObject variants = new JsonObject();
            
            // For wood blocks, the texture is actually the side texture of the corresponding log
            String textureBase = isWood ? logName.replace("_wood", "_log").replace("_hyphae", "_stem") : logName;
            String vanillaTex = "minecraft:block/" + textureBase;
            String topTex = isWood ? vanillaTex : vanillaTex + "_top";
            
        for (moldmod.SporesShadowsConstants.MoldStage moldStage : moldmod.SporesShadowsConstants.MoldStage.values()) { int stage = moldStage.getId();
            JsonObject model = new JsonObject();
            JsonObject textures = new JsonObject();
            if (stage > 0) {
                model.addProperty("parent", moldmod.SporesShadows.MOD_ID + ":block/mold/moldy_cube_column");
                textures.addProperty("end", topTex);
                textures.addProperty("side", vanillaTex);
                textures.addProperty("overlay", moldmod.SporesShadows.MOD_ID + ":block/mold/mold_stage_" + stage);
                model.add("textures", textures);
                write(builder, "models/block/" + blockId + "_stage_" + stage, model);
            }
            
            String itemParent = stage == 0 ? "minecraft:block/" + logName : blockId + "_stage_" + stage;
            if (idPrefix.equals("waxed_") || stage > 0) { genItemModel(builder, logName, itemParent, stage, false, idPrefix); }
            
            String m = stage == 0 ? "minecraft:block/" + logName : moldmod.SporesShadows.MOD_ID + ":block/" + blockId + "_stage_" + stage;
                for (String common : getCommonProps()) {
                    JsonObject vY = new JsonObject(); vY.addProperty("model", m);
                    variants.add("axis=y,stage=" + stage + "," + common, vY);
                    
                    JsonObject vZ = new JsonObject(); vZ.addProperty("model", m); vZ.addProperty("x", 90);
                    variants.add("axis=z,stage=" + stage + "," + common, vZ);
                    
                    JsonObject vX = new JsonObject(); vX.addProperty("model", m); vX.addProperty("x", 90); vX.addProperty("y", 90);
                    variants.add("axis=x,stage=" + stage + "," + common, vX);
                }
            }
            JsonObject bs = new JsonObject();
            bs.add("variants", variants);
            write(builder, "blockstates/" + blockId, bs);
        }
    }

    private static void genSlab(ResourcePackBuilder builder, String wood, String prefix) {
        for (String idPrefix : new String[]{"moldy_", "waxed_"}) {
            String blockId = idPrefix + prefix + "_slab";
            JsonObject variants = new JsonObject();
            for (moldmod.SporesShadowsConstants.MoldStage moldStage : moldmod.SporesShadowsConstants.MoldStage.values()) { int stage = moldStage.getId();
                String tex = "minecraft:block/" + prefix + "_planks";
                if (stage > 0) {
                    JsonObject mBot = new JsonObject();
                    mBot.addProperty("parent", moldmod.SporesShadows.MOD_ID + ":block/mold/moldy_slab");
                    JsonObject tBot = new JsonObject();
                    tBot.addProperty("bottom", tex); tBot.addProperty("top", tex); tBot.addProperty("side", tex); tBot.addProperty("overlay", moldmod.SporesShadows.MOD_ID + ":block/mold/mold_stage_" + stage);
                    mBot.add("textures", tBot);
                    write(builder, "models/block/" + blockId + "_stage_" + stage, mBot);

                    JsonObject mTop = new JsonObject();
                    mTop.addProperty("parent", moldmod.SporesShadows.MOD_ID + ":block/mold/moldy_slab_top");
                    mTop.add("textures", tBot);
                    write(builder, "models/block/" + blockId + "_stage_" + stage + "_top", mTop);
                }
                String itemParent = stage == 0 ? "minecraft:block/" + prefix + "_slab" : blockId + "_stage_" + stage;
                if (idPrefix.equals("waxed_") || stage > 0) { genItemModel(builder, prefix + "_slab", itemParent, stage, false, idPrefix); }
                
                String mBottom = stage == 0 ? "minecraft:block/" + prefix + "_slab" : moldmod.SporesShadows.MOD_ID + ":block/" + blockId + "_stage_" + stage;
                String mTopStr = stage == 0 ? "minecraft:block/" + prefix + "_slab_top" : moldmod.SporesShadows.MOD_ID + ":block/" + blockId + "_stage_" + stage + "_top";
                String mDouble = stage == 0 ? "minecraft:block/" + prefix + "_planks" : moldmod.SporesShadows.MOD_ID + ":block/moldy_" + prefix + "_planks_stage_" + stage;
                if (idPrefix.equals("waxed_")) {
                    mDouble = stage == 0 ? "minecraft:block/" + prefix + "_planks" : moldmod.SporesShadows.MOD_ID + ":block/waxed_" + prefix + "_planks_stage_" + stage;
                }
                
                for (String waterlogged : new String[]{"false", "true"}) {
                    for (String common : getCommonProps()) {
                        JsonObject vBot = new JsonObject(); vBot.addProperty("model", mBottom);
                        variants.add("stage=" + stage + "," + common + ",type=bottom,waterlogged=" + waterlogged, vBot);
                        
                        JsonObject vTop = new JsonObject(); vTop.addProperty("model", mTopStr);
                        variants.add("stage=" + stage + "," + common + ",type=top,waterlogged=" + waterlogged, vTop);
                        
                        JsonObject vDbl = new JsonObject(); vDbl.addProperty("model", mDouble);
                        variants.add("stage=" + stage + "," + common + ",type=double,waterlogged=" + waterlogged, vDbl);
                    }
                }
            }
            JsonObject bs = new JsonObject();
            bs.add("variants", variants);
            write(builder, "blockstates/" + blockId, bs);
        }
    }

    private static void genStairs(ResourcePackBuilder builder, String wood, String prefix) {
        for (String idPrefix : new String[]{"moldy_", "waxed_"}) {
            String blockId = idPrefix + prefix + "_stairs";
            JsonObject variants = new JsonObject();
            String[] facings = {"east", "south", "west", "north"};
            int[] yRots = {0, 90, 180, 270};
            String[] shapes = {"straight", "inner_left", "inner_right", "outer_left", "outer_right"};
            String[] halfs = {"bottom", "top"};

            for (moldmod.SporesShadowsConstants.MoldStage moldStage : moldmod.SporesShadowsConstants.MoldStage.values()) { int stage = moldStage.getId();
                String tex = "minecraft:block/" + prefix + "_planks";
                if (stage > 0) {
                    JsonObject mDef = new JsonObject(); mDef.addProperty("parent", moldmod.SporesShadows.MOD_ID + ":block/mold/moldy_stairs");
                    JsonObject tDef = new JsonObject(); tDef.addProperty("bottom", tex); tDef.addProperty("top", tex); tDef.addProperty("side", tex); tDef.addProperty("overlay", moldmod.SporesShadows.MOD_ID + ":block/mold/mold_stage_" + stage);
                    mDef.add("textures", tDef);
                    write(builder, "models/block/" + blockId + "_stage_" + stage, mDef);

                    JsonObject mIn = new JsonObject(); mIn.addProperty("parent", moldmod.SporesShadows.MOD_ID + ":block/mold/moldy_inner_stairs"); mIn.add("textures", tDef);
                    write(builder, "models/block/" + blockId + "_stage_" + stage + "_inner", mIn);

                    JsonObject mOut = new JsonObject(); mOut.addProperty("parent", moldmod.SporesShadows.MOD_ID + ":block/mold/moldy_outer_stairs"); mOut.add("textures", tDef);
                    write(builder, "models/block/" + blockId + "_stage_" + stage + "_outer", mOut);
                }
                String itemParent = stage == 0 ? "minecraft:block/" + prefix + "_stairs" : blockId + "_stage_" + stage;
                if (idPrefix.equals("waxed_") || stage > 0) { genItemModel(builder, prefix + "_stairs", itemParent, stage, false, idPrefix); }

                for (int f = 0; f < facings.length; f++) {
                    String facing = facings[f];
                    int yBase = yRots[f];
                    for (String half : halfs) {
                        for (String shape : shapes) {
                            int x = half.equals("top") ? 180 : 0;
                            int yRot = yBase;
                            
                            if (half.equals("bottom")) {
                                if (shape.equals("outer_left") || shape.equals("inner_left")) {
                                    yRot = (yBase + 270) % 360;
                                }
                            } else {
                                if (shape.equals("outer_right") || shape.equals("inner_right")) {
                                    yRot = (yBase + 90) % 360;
                                }
                            }

                            String mName = shape.equals("straight") ? "stairs" : (shape.contains("inner") ? "stairs_inner" : "stairs_outer");
                            String m = stage == 0 ? "minecraft:block/" + prefix + "_" + mName : moldmod.SporesShadows.MOD_ID + ":block/" + blockId + "_stage_" + stage + (shape.contains("inner") ? "_inner" : (shape.contains("outer") ? "_outer" : ""));

                            for (String waterlogged : new String[]{"false", "true"}) {
                                for (String common : getCommonProps()) {
                                    JsonObject v = new JsonObject();
                                    v.addProperty("model", m);
                                    if (x != 0) v.addProperty("x", x);
                                    if (yRot != 0) v.addProperty("y", yRot);
                                    v.addProperty("uvlock", true);
                                    variants.add("facing=" + facing + ",half=" + half + ",shape=" + shape + ",stage=" + stage + "," + common + ",waterlogged=" + waterlogged, v);
                                }
                            }
                        }
                    }
                }
            }
            JsonObject bs = new JsonObject(); bs.add("variants", variants); write(builder, "blockstates/" + blockId, bs);
        }
    }

    private static void genDoor(ResourcePackBuilder builder, String wood, String prefix) {
        for (String idPrefix : new String[]{"moldy_", "waxed_"}) {
            String blockId = idPrefix + prefix + "_door";
            JsonObject variants = new JsonObject();
            for (moldmod.SporesShadowsConstants.MoldStage moldStage : moldmod.SporesShadowsConstants.MoldStage.values()) { int stage = moldStage.getId();
                if (stage > 0) {
                    String texBot = "minecraft:block/" + prefix + "_door_bottom";
                    String texTop = "minecraft:block/" + prefix + "_door_top";
                    for (String half : new String[]{"bottom", "top"}) {
                        for (String hinge : new String[]{"left", "right"}) {
                            for (String openS : new String[]{"", "_open"}) {
                                String mName = blockId + "_" + half + "_" + hinge + openS + "_stage_" + stage;
                                String parent = moldmod.SporesShadows.MOD_ID + ":block/mold/moldy_door_" + half + "_" + hinge + openS;
                                JsonObject mDef = new JsonObject(); mDef.addProperty("parent", parent);
                                JsonObject tDef = new JsonObject(); tDef.addProperty("bottom", texBot); tDef.addProperty("top", texTop); tDef.addProperty("overlay", moldmod.SporesShadows.MOD_ID + ":block/mold/mold_stage_" + stage);
                                mDef.add("textures", tDef);
                                write(builder, "models/block/" + mName, mDef);
                            }
                        }
                    }
                    // Don't call genItemModel for door, as MoldyResourceGenerator handles it perfectly!
                }

                for (String facing : new String[]{"north", "east", "south", "west"}) {
                    for (String half : new String[]{"lower", "upper"}) {
                        for (String hinge : new String[]{"left", "right"}) {
                            for (String openState : new String[]{"false", "true"}) {
                                for (String powered : new String[]{"false", "true"}) {
                                    int yRot = 0;
                                    if (facing.equals("east")) yRot = openState.equals("true") ? (hinge.equals("left") ? 90 : 270) : 0;
                                    else if (facing.equals("south")) yRot = openState.equals("true") ? (hinge.equals("left") ? 180 : 0) : 90;
                                    else if (facing.equals("west")) yRot = openState.equals("true") ? (hinge.equals("left") ? 270 : 90) : 180;
                                    else if (facing.equals("north")) yRot = openState.equals("true") ? (hinge.equals("left") ? 0 : 180) : 270;

                                    String tHalf = half.equals("lower") ? "bottom" : "top";
                                    String openS = openState.equals("true") ? "_open" : "";

                                    String m = stage == 0 ? "minecraft:block/" + prefix + "_door_" + tHalf + "_" + hinge + openS 
                                                          : moldmod.SporesShadows.MOD_ID + ":block/" + blockId + "_" + tHalf + "_" + hinge + openS + "_stage_" + stage;

                                    for (String common : getCommonProps()) {
                                        JsonObject v = new JsonObject(); v.addProperty("model", m);
                                        if (yRot != 0) v.addProperty("y", yRot);
                                        variants.add("facing=" + facing + ",half=" + half + ",hinge=" + hinge + ",open=" + openState + ",powered=" + powered + ",stage=" + stage + "," + common, v);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            JsonObject bs = new JsonObject(); bs.add("variants", variants); write(builder, "blockstates/" + blockId, bs);
        }
    }

    private static void genTrapdoor(ResourcePackBuilder builder, String wood, String prefix) {
        for (String idPrefix : new String[]{"moldy_", "waxed_"}) {
            String blockId = idPrefix + prefix + "_trapdoor";
            JsonObject variants = new JsonObject();
            String[] facings = {"north", "east", "south", "west"};
            int[] yRots = {0, 90, 180, 270};

            for (moldmod.SporesShadowsConstants.MoldStage moldStage : moldmod.SporesShadowsConstants.MoldStage.values()) { int stage = moldStage.getId();
                String tex = "minecraft:block/" + prefix + "_trapdoor";
                if (stage > 0) {
                    JsonObject mBot = new JsonObject(); mBot.addProperty("parent", moldmod.SporesShadows.MOD_ID + ":block/mold/moldy_template_orientable_trapdoor_bottom");
                    JsonObject tBot = new JsonObject(); tBot.addProperty("texture", tex); tBot.addProperty("overlay", moldmod.SporesShadows.MOD_ID + ":block/mold/mold_stage_" + stage);
                    mBot.add("textures", tBot); write(builder, "models/block/" + blockId + "_bottom_stage_" + stage, mBot);

                    JsonObject mTop = new JsonObject(); mTop.addProperty("parent", moldmod.SporesShadows.MOD_ID + ":block/mold/moldy_template_orientable_trapdoor_top"); mTop.add("textures", tBot);
                    write(builder, "models/block/" + blockId + "_top_stage_" + stage, mTop);

                    JsonObject mOpn = new JsonObject(); mOpn.addProperty("parent", moldmod.SporesShadows.MOD_ID + ":block/mold/moldy_template_orientable_trapdoor_open"); mOpn.add("textures", tBot);
                    write(builder, "models/block/" + blockId + "_open_stage_" + stage, mOpn);
                }
                String itemParent = stage == 0 ? "minecraft:block/" + prefix + "_trapdoor_bottom" : blockId + "_bottom_stage_" + stage;
                if (idPrefix.equals("waxed_") || stage > 0) { genItemModel(builder, prefix + "_trapdoor", itemParent, stage, false, idPrefix); }

                for (int f = 0; f < facings.length; f++) {
                    String facing = facings[f];
                    int yBase = yRots[f];
                    for (String half : new String[]{"bottom", "top"}) {
                        for (String openState : new String[]{"false", "true"}) {
                            for (String powered : new String[]{"false", "true"}) {
                                for (String waterlogged : new String[]{"false", "true"}) {
                                    String m;
                                    if (openState.equals("true")) m = stage == 0 ? "minecraft:block/" + prefix + "_trapdoor_open" : moldmod.SporesShadows.MOD_ID + ":block/" + blockId + "_open_stage_" + stage;
                                    else if (half.equals("top")) m = stage == 0 ? "minecraft:block/" + prefix + "_trapdoor_top" : moldmod.SporesShadows.MOD_ID + ":block/" + blockId + "_top_stage_" + stage;
                                    else m = stage == 0 ? "minecraft:block/" + prefix + "_trapdoor_bottom" : moldmod.SporesShadows.MOD_ID + ":block/" + blockId + "_bottom_stage_" + stage;

                                    for (String common : getCommonProps()) {
                                        JsonObject v = new JsonObject(); v.addProperty("model", m);
                                        if (yBase != 0) v.addProperty("y", yBase);
                                        variants.add("facing=" + facing + ",half=" + half + ",open=" + openState + ",powered=" + powered + ",stage=" + stage + "," + common + ",waterlogged=" + waterlogged, v);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            JsonObject bs = new JsonObject(); bs.add("variants", variants); write(builder, "blockstates/" + blockId, bs);
        }
    }

    private static void genFence(ResourcePackBuilder builder, String wood, String prefix) {
        boolean isBamboo = prefix.equals("bamboo");
        for (String idPrefix : new String[]{"moldy_", "waxed_"}) {
            String blockId = idPrefix + prefix + "_fence";
            JsonArray multipart = new JsonArray();
            for (moldmod.SporesShadowsConstants.MoldStage moldStage : moldmod.SporesShadowsConstants.MoldStage.values()) { int stage = moldStage.getId();
                String tex = isBamboo ? "minecraft:block/bamboo_fence" : "minecraft:block/" + prefix + "_planks";
                if (stage > 0) {
                    if (isBamboo) {
                        JsonObject mP = new JsonObject();
                        mP.addProperty("parent", moldmod.SporesShadows.MOD_ID + ":block/mold/moldy_custom_fence_post");
                        JsonObject tP = new JsonObject();
                        tP.addProperty("texture", tex);
                        tP.addProperty("overlay", moldmod.SporesShadows.MOD_ID + ":block/mold/mold_stage_" + stage);
                        tP.addProperty("particle", "minecraft:block/bamboo_fence_particle");
                        mP.add("textures", tP);
                        write(builder, "models/block/" + blockId + "_post_stage_" + stage, mP);

                        for (String dir : new String[]{"north", "east", "south", "west"}) {
                            JsonObject mS = new JsonObject();
                            mS.addProperty("parent", moldmod.SporesShadows.MOD_ID + ":block/mold/moldy_custom_fence_side_" + dir);
                            mS.add("textures", tP);
                            write(builder, "models/block/" + blockId + "_side_" + dir + "_stage_" + stage, mS);
                        }

                        JsonObject mI = new JsonObject();
                        mI.addProperty("parent", moldmod.SporesShadows.MOD_ID + ":block/mold/moldy_custom_fence_inventory");
                        mI.add("textures", tP);
                        write(builder, "models/block/" + blockId + "_inventory_stage_" + stage, mI);
                    } else {
                        JsonObject mP = new JsonObject();
                        mP.addProperty("parent", moldmod.SporesShadows.MOD_ID + ":block/mold/moldy_fence_post");
                        JsonObject tP = new JsonObject();
                        tP.addProperty("texture", tex);
                        tP.addProperty("overlay", moldmod.SporesShadows.MOD_ID + ":block/mold/mold_stage_" + stage);
                        mP.add("textures", tP);
                        write(builder, "models/block/" + blockId + "_post_stage_" + stage, mP);

                        JsonObject mS = new JsonObject();
                        mS.addProperty("parent", moldmod.SporesShadows.MOD_ID + ":block/mold/moldy_fence_side");
                        mS.add("textures", tP);
                        write(builder, "models/block/" + blockId + "_side_stage_" + stage, mS);

                        JsonObject mI = new JsonObject();
                        mI.addProperty("parent", moldmod.SporesShadows.MOD_ID + ":block/mold/moldy_fence_inventory");
                        mI.add("textures", tP);
                        write(builder, "models/block/" + blockId + "_inventory_stage_" + stage, mI);
                    }
                }
                String itemParent = stage == 0 ? "minecraft:block/" + prefix + "_fence_inventory" : blockId + "_inventory_stage_" + stage;
                if (idPrefix.equals("waxed_") || stage > 0) { genItemModel(builder, prefix + "_fence", itemParent, stage, false, idPrefix); }

                String mPost = stage == 0 ? "minecraft:block/" + prefix + "_fence_post" : moldmod.SporesShadows.MOD_ID + ":block/" + blockId + "_post_stage_" + stage;
                String mSide = stage == 0 ? "minecraft:block/" + prefix + "_fence_side" : moldmod.SporesShadows.MOD_ID + ":block/" + blockId + "_side_stage_" + stage;

                for (String waterlogged : new String[]{"false", "true"}) {
                    for (String common : getCommonProps()) {
                        String structural = common.contains("structural=true") ? "true" : "false";
                        String waxed = common.contains("waxed=true") ? "true" : "false";
                        
                        JsonObject p1 = new JsonObject();
                        JsonObject w1 = new JsonObject(); w1.addProperty("stage", String.valueOf(stage)); w1.addProperty("structural", structural); w1.addProperty("waxed", waxed); w1.addProperty("waterlogged", waterlogged);
                        p1.add("when", w1);
                        JsonObject a1 = new JsonObject(); a1.addProperty("model", mPost); p1.add("apply", a1);
                        multipart.add(p1);

                        String[] dirs = {"north", "east", "south", "west"};
                        int[] yRots = {0, 90, 180, 270};
                        for (int d = 0; d < dirs.length; d++) {
                            JsonObject pD = new JsonObject();
                            JsonObject wD = new JsonObject(); wD.addProperty("stage", String.valueOf(stage)); wD.addProperty("structural", structural); wD.addProperty("waxed", waxed); wD.addProperty("waterlogged", waterlogged); wD.addProperty(dirs[d], "true");
                            pD.add("when", wD);
                            JsonObject aD = new JsonObject();
                            if (isBamboo) {
                                String sideModel = stage == 0
                                        ? "minecraft:block/bamboo_fence_side_" + dirs[d]
                                        : moldmod.SporesShadows.MOD_ID + ":block/" + blockId + "_side_" + dirs[d] + "_stage_" + stage;
                                aD.addProperty("model", sideModel);
                                aD.addProperty("uvlock", false);
                            } else {
                                aD.addProperty("model", mSide); 
                                if (yRots[d] != 0) aD.addProperty("y", yRots[d]);
                                aD.addProperty("uvlock", true);
                            }
                            pD.add("apply", aD);
                            multipart.add(pD);
                        }
                    }
                }
            }
            JsonObject bs = new JsonObject(); bs.add("multipart", multipart); write(builder, "blockstates/" + blockId, bs);
        }
    }

    private static void genGate(ResourcePackBuilder builder, String wood, String prefix) {
        boolean isBamboo = prefix.equals("bamboo");
        for (String idPrefix : new String[]{"moldy_", "waxed_"}) {
            String blockId = idPrefix + prefix + "_fence_gate";
            JsonObject variants = new JsonObject();
            String[] facings = {"south", "west", "north", "east"};
            int[] yRots = {0, 90, 180, 270};

            for (moldmod.SporesShadowsConstants.MoldStage moldStage : moldmod.SporesShadowsConstants.MoldStage.values()) { int stage = moldStage.getId();
                String tex = isBamboo ? "minecraft:block/bamboo_fence_gate" : "minecraft:block/" + prefix + "_planks";
                String templatePrefix = isBamboo ? "moldy_template_custom_fence_gate" : "moldy_template_fence_gate";
                if (stage > 0) {
                    JsonObject tDef = new JsonObject();
                    tDef.addProperty("texture", tex);
                    tDef.addProperty("overlay", moldmod.SporesShadows.MOD_ID + ":block/mold/mold_stage_" + stage);
                    if (isBamboo) {
                        tDef.addProperty("particle", "minecraft:block/bamboo_fence_gate_particle");
                    }

                    JsonObject mDef = new JsonObject();
                    mDef.addProperty("parent", moldmod.SporesShadows.MOD_ID + ":block/mold/" + templatePrefix);
                    mDef.add("textures", tDef);
                    write(builder, "models/block/" + blockId + "_stage_" + stage, mDef);

                    JsonObject mOpn = new JsonObject();
                    mOpn.addProperty("parent", moldmod.SporesShadows.MOD_ID + ":block/mold/" + templatePrefix + "_open");
                    mOpn.add("textures", tDef);
                    write(builder, "models/block/" + blockId + "_open_stage_" + stage, mOpn);

                    JsonObject mWal = new JsonObject();
                    mWal.addProperty("parent", moldmod.SporesShadows.MOD_ID + ":block/mold/" + templatePrefix + "_wall");
                    mWal.add("textures", tDef);
                    write(builder, "models/block/" + blockId + "_wall_stage_" + stage, mWal);

                    JsonObject mWO = new JsonObject();
                    mWO.addProperty("parent", moldmod.SporesShadows.MOD_ID + ":block/mold/" + templatePrefix + "_wall_open");
                    mWO.add("textures", tDef);
                    write(builder, "models/block/" + blockId + "_wall_open_stage_" + stage, mWO);
                }
                String itemParent = stage == 0 ? "minecraft:block/" + prefix + "_fence_gate" : blockId + "_stage_" + stage;
                if (idPrefix.equals("waxed_") || stage > 0) { genItemModel(builder, prefix + "_fence_gate", itemParent, stage, false, idPrefix); }

                for (int f = 0; f < facings.length; f++) {
                    String facing = facings[f];
                    int yBase = yRots[f];
                    for (String inWall : new String[]{"false", "true"}) {
                        for (String openState : new String[]{"false", "true"}) {
                            for (String powered : new String[]{"false", "true"}) {
                                String m;
                                if (inWall.equals("true")) {
                                    m = openState.equals("true") ? "minecraft:block/" + prefix + "_fence_gate_wall_open" : "minecraft:block/" + prefix + "_fence_gate_wall";
                                    if (stage > 0) m = openState.equals("true") ? moldmod.SporesShadows.MOD_ID + ":block/" + blockId + "_wall_open_stage_" + stage : moldmod.SporesShadows.MOD_ID + ":block/" + blockId + "_wall_stage_" + stage;
                                } else {
                                    m = openState.equals("true") ? "minecraft:block/" + prefix + "_fence_gate_open" : "minecraft:block/" + prefix + "_fence_gate";
                                    if (stage > 0) m = openState.equals("true") ? moldmod.SporesShadows.MOD_ID + ":block/" + blockId + "_open_stage_" + stage : moldmod.SporesShadows.MOD_ID + ":block/" + blockId + "_stage_" + stage;
                                }

                                for (String common : getCommonProps()) {
                                    JsonObject v = new JsonObject();
                                    v.addProperty("model", m);
                                    if (yBase != 0) v.addProperty("y", yBase);
                                    v.addProperty("uvlock", !isBamboo);
                                    variants.add("facing=" + facing + ",in_wall=" + inWall + ",open=" + openState + ",powered=" + powered + ",stage=" + stage + "," + common, v);
                                }
                            }
                        }
                    }
                }
            }
            JsonObject bs = new JsonObject(); bs.add("variants", variants); write(builder, "blockstates/" + blockId, bs);
        }
    }

    private static void genPressurePlate(ResourcePackBuilder builder, String wood, String prefix) {
        for (String idPrefix : new String[]{"moldy_", "waxed_"}) {
            String blockId = idPrefix + prefix + "_pressure_plate";
            JsonObject variants = new JsonObject();
            
            for (moldmod.SporesShadowsConstants.MoldStage moldStage : moldmod.SporesShadowsConstants.MoldStage.values()) { int stage = moldStage.getId();
                String tex = "minecraft:block/" + prefix + "_planks";
                if (stage > 0) {
                    // Normal
                    JsonObject mDef = new JsonObject(); mDef.addProperty("parent", moldmod.SporesShadows.MOD_ID + ":block/mold/moldy_pressure_plate_up");
                    JsonObject tDef = new JsonObject(); tDef.addProperty("texture", tex); tDef.addProperty("overlay", moldmod.SporesShadows.MOD_ID + ":block/mold/mold_stage_" + stage);
                    mDef.add("textures", tDef);
                    write(builder, "models/block/" + blockId + "_stage_" + stage, mDef);
                    // Pressed
                    JsonObject mPressed = new JsonObject(); mPressed.addProperty("parent", moldmod.SporesShadows.MOD_ID + ":block/mold/moldy_pressure_plate_down");
                    mPressed.add("textures", tDef);
                    write(builder, "models/block/" + blockId + "_down_stage_" + stage, mPressed);
                }
                
                String itemParent = stage == 0 ? "minecraft:block/" + prefix + "_pressure_plate" : blockId + "_stage_" + stage;
                if (idPrefix.equals("waxed_") || stage > 0) { genItemModel(builder, prefix + "_pressure_plate", itemParent, stage, false, idPrefix); }
                
                for (String powered : new String[]{"false", "true"}) {
                    String m = stage == 0 ? "minecraft:block/" + prefix + "_pressure_plate" + (powered.equals("true") ? "_down" : "") : moldmod.SporesShadows.MOD_ID + ":block/" + blockId + (powered.equals("true") ? "_down_" : "_") + "stage_" + stage;
                    
                    for (String common : getCommonProps()) {
                        JsonObject v = new JsonObject();
                        v.addProperty("model", m);
                        variants.add("powered=" + powered + ",stage=" + stage + "," + common, v);
                    }
                }
            }
            JsonObject bs = new JsonObject(); bs.add("variants", variants); write(builder, "blockstates/" + blockId, bs);
        }
    }

    private static void genButton(ResourcePackBuilder builder, String wood, String prefix) {
        for (String idPrefix : new String[]{"moldy_", "waxed_"}) {
            String blockId = idPrefix + prefix + "_button";
            JsonObject variants = new JsonObject();
            String[] facings = {"north", "east", "south", "west"};

            for (moldmod.SporesShadowsConstants.MoldStage moldStage : moldmod.SporesShadowsConstants.MoldStage.values()) { int stage = moldStage.getId();
                String tex = "minecraft:block/" + prefix + "_planks";
                if (stage > 0) {
                    // Normal
                    JsonObject mDef = new JsonObject(); mDef.addProperty("parent", moldmod.SporesShadows.MOD_ID + ":block/mold/moldy_button");
                    JsonObject tDef = new JsonObject(); tDef.addProperty("texture", tex); tDef.addProperty("overlay", moldmod.SporesShadows.MOD_ID + ":block/mold/mold_stage_" + stage);
                    mDef.add("textures", tDef);
                    write(builder, "models/block/" + blockId + "_stage_" + stage, mDef);
                    
                    // Pressed
                    JsonObject mPressed = new JsonObject(); mPressed.addProperty("parent", moldmod.SporesShadows.MOD_ID + ":block/mold/moldy_button_pressed");
                    mPressed.add("textures", tDef);
                    write(builder, "models/block/" + blockId + "_pressed_stage_" + stage, mPressed);
                    
                    // Inventory
                    JsonObject mInv = new JsonObject(); mInv.addProperty("parent", moldmod.SporesShadows.MOD_ID + ":block/mold/moldy_button_inventory");
                    mInv.add("textures", tDef);
                    write(builder, "models/block/" + blockId + "_inventory_stage_" + stage, mInv);
                }
                
                String itemParent = stage == 0 ? "minecraft:item/" + prefix + "_button" : blockId + "_inventory_stage_" + stage;
                if (idPrefix.equals("waxed_") || stage > 0) { genItemModel(builder, prefix + "_button", itemParent, stage, false, idPrefix); }
                
                for (String face : new String[]{"floor", "wall", "ceiling"}) {
                    for (String facing : facings) {
                        for (String powered : new String[]{"false", "true"}) {
                            String m = stage == 0 ? "minecraft:block/" + prefix + "_button" + (powered.equals("true") ? "_pressed" : "") : moldmod.SporesShadows.MOD_ID + ":block/" + blockId + (powered.equals("true") ? "_pressed_" : "_") + "stage_" + stage;
                            
                            int x = 0;
                            int y = 0;
                            if (face.equals("ceiling")) {
                                x = 180;
                                if (facing.equals("east")) y = 270;
                                else if (facing.equals("north")) y = 180;
                                else if (facing.equals("south")) y = 0;
                                else if (facing.equals("west")) y = 90;
                            } else if (face.equals("floor")) {
                                if (facing.equals("east")) y = 90;
                                else if (facing.equals("north")) y = 0;
                                else if (facing.equals("south")) y = 180;
                                else if (facing.equals("west")) y = 270;
                            } else if (face.equals("wall")) {
                                x = 90;
                                if (facing.equals("east")) y = 90;
                                else if (facing.equals("north")) y = 0;
                                else if (facing.equals("south")) y = 180;
                                else if (facing.equals("west")) y = 270;
                            }
                            
                            boolean uvlock = face.equals("wall");
                            
                            for (String common : getCommonProps()) {
                                JsonObject v = new JsonObject();
                                v.addProperty("model", m);
                                if (x != 0) v.addProperty("x", x);
                                if (y != 0) v.addProperty("y", y);
                                if (uvlock) v.addProperty("uvlock", true);
                                variants.add("face=" + face + ",facing=" + facing + ",powered=" + powered + ",stage=" + stage + "," + common, v);
                            }
                        }
                    }
                }
            }
            JsonObject bs = new JsonObject(); bs.add("variants", variants); write(builder, "blockstates/" + blockId, bs);
        }
    }

    private static void genMosaic(ResourcePackBuilder builder, String prefix) {
        for (String idPrefix : new String[]{"moldy_", "waxed_"}) {
            String blockId = idPrefix + prefix + "_mosaic";
            JsonObject variants = new JsonObject();
            for (moldmod.SporesShadowsConstants.MoldStage moldStage : moldmod.SporesShadowsConstants.MoldStage.values()) {
                int stage = moldStage.getId();
                String tex = "minecraft:block/" + prefix + "_mosaic";
                if (stage > 0) {
                    JsonObject model = new JsonObject();
                    model.addProperty("parent", moldmod.SporesShadows.MOD_ID + ":block/mold/moldy_cube_all");
                    JsonObject textures = new JsonObject();
                    textures.addProperty("all", tex);
                    textures.addProperty("overlay", moldmod.SporesShadows.MOD_ID + ":block/mold/mold_stage_" + stage);
                    model.add("textures", textures);
                    write(builder, "models/block/" + blockId + "_stage_" + stage, model);
                }
                String itemParent = stage == 0 ? "minecraft:block/" + prefix + "_mosaic" : blockId + "_stage_" + stage;
                if (idPrefix.equals("waxed_") || stage > 0) { genItemModel(builder, prefix + "_mosaic", itemParent, stage, false, idPrefix); }

                String m = stage == 0 ? "minecraft:block/" + prefix + "_mosaic" : moldmod.SporesShadows.MOD_ID + ":block/" + blockId + "_stage_" + stage;
                for (String common : getCommonProps()) {
                    JsonObject v = new JsonObject();
                    v.addProperty("model", m);
                    variants.add("stage=" + stage + "," + common, v);
                }
            }
            JsonObject bs = new JsonObject();
            bs.add("variants", variants);
            write(builder, "blockstates/" + blockId, bs);
        }
    }

    private static void genMosaicSlab(ResourcePackBuilder builder, String prefix) {
        for (String idPrefix : new String[]{"moldy_", "waxed_"}) {
            String blockId = idPrefix + prefix + "_mosaic_slab";
            JsonObject variants = new JsonObject();
            for (moldmod.SporesShadowsConstants.MoldStage moldStage : moldmod.SporesShadowsConstants.MoldStage.values()) {
                int stage = moldStage.getId();
                String tex = "minecraft:block/" + prefix + "_mosaic";
                if (stage > 0) {
                    JsonObject mBot = new JsonObject();
                    mBot.addProperty("parent", moldmod.SporesShadows.MOD_ID + ":block/mold/moldy_slab");
                    JsonObject tBot = new JsonObject();
                    tBot.addProperty("bottom", tex); tBot.addProperty("top", tex); tBot.addProperty("side", tex); tBot.addProperty("overlay", moldmod.SporesShadows.MOD_ID + ":block/mold/mold_stage_" + stage);
                    mBot.add("textures", tBot);
                    write(builder, "models/block/" + blockId + "_stage_" + stage, mBot);

                    JsonObject mTop = new JsonObject();
                    mTop.addProperty("parent", moldmod.SporesShadows.MOD_ID + ":block/mold/moldy_slab_top");
                    mTop.add("textures", tBot);
                    write(builder, "models/block/" + blockId + "_stage_" + stage + "_top", mTop);
                }
                String itemParent = stage == 0 ? "minecraft:block/" + prefix + "_mosaic_slab" : blockId + "_stage_" + stage;
                if (idPrefix.equals("waxed_") || stage > 0) { genItemModel(builder, prefix + "_mosaic_slab", itemParent, stage, false, idPrefix); }

                String mBottom = stage == 0 ? "minecraft:block/" + prefix + "_mosaic_slab" : moldmod.SporesShadows.MOD_ID + ":block/" + blockId + "_stage_" + stage;
                String mTopStr = stage == 0 ? "minecraft:block/" + prefix + "_mosaic_slab_top" : moldmod.SporesShadows.MOD_ID + ":block/" + blockId + "_stage_" + stage + "_top";
                String mDouble = stage == 0 ? "minecraft:block/" + prefix + "_mosaic" : moldmod.SporesShadows.MOD_ID + ":block/" + idPrefix + prefix + "_mosaic_stage_" + stage;

                for (String waterlogged : new String[]{"false", "true"}) {
                    for (String common : getCommonProps()) {
                        JsonObject vBot = new JsonObject(); vBot.addProperty("model", mBottom);
                        variants.add("stage=" + stage + "," + common + ",type=bottom,waterlogged=" + waterlogged, vBot);

                        JsonObject vTop = new JsonObject(); vTop.addProperty("model", mTopStr);
                        variants.add("stage=" + stage + "," + common + ",type=top,waterlogged=" + waterlogged, vTop);

                        JsonObject vDbl = new JsonObject(); vDbl.addProperty("model", mDouble);
                        variants.add("stage=" + stage + "," + common + ",type=double,waterlogged=" + waterlogged, vDbl);
                    }
                }
            }
            JsonObject bs = new JsonObject();
            bs.add("variants", variants);
            write(builder, "blockstates/" + blockId, bs);
        }
    }

    private static void genMosaicStairs(ResourcePackBuilder builder, String prefix) {
        for (String idPrefix : new String[]{"moldy_", "waxed_"}) {
            String blockId = idPrefix + prefix + "_mosaic_stairs";
            JsonObject variants = new JsonObject();
            String[] facings = {"east", "south", "west", "north"};
            int[] yRots = {0, 90, 180, 270};
            String[] shapes = {"straight", "inner_left", "inner_right", "outer_left", "outer_right"};
            String[] halfs = {"bottom", "top"};

            for (moldmod.SporesShadowsConstants.MoldStage moldStage : moldmod.SporesShadowsConstants.MoldStage.values()) {
                int stage = moldStage.getId();
                String tex = "minecraft:block/" + prefix + "_mosaic";
                if (stage > 0) {
                    JsonObject mDef = new JsonObject(); mDef.addProperty("parent", moldmod.SporesShadows.MOD_ID + ":block/mold/moldy_stairs");
                    JsonObject tDef = new JsonObject(); tDef.addProperty("bottom", tex); tDef.addProperty("top", tex); tDef.addProperty("side", tex); tDef.addProperty("overlay", moldmod.SporesShadows.MOD_ID + ":block/mold/mold_stage_" + stage);
                    mDef.add("textures", tDef);
                    write(builder, "models/block/" + blockId + "_stage_" + stage, mDef);

                    JsonObject mIn = new JsonObject(); mIn.addProperty("parent", moldmod.SporesShadows.MOD_ID + ":block/mold/moldy_inner_stairs"); mIn.add("textures", tDef);
                    write(builder, "models/block/" + blockId + "_stage_" + stage + "_inner", mIn);

                    JsonObject mOut = new JsonObject(); mOut.addProperty("parent", moldmod.SporesShadows.MOD_ID + ":block/mold/moldy_outer_stairs"); mOut.add("textures", tDef);
                    write(builder, "models/block/" + blockId + "_stage_" + stage + "_outer", mOut);
                }
                String itemParent = stage == 0 ? "minecraft:block/" + prefix + "_mosaic_stairs" : blockId + "_stage_" + stage;
                if (idPrefix.equals("waxed_") || stage > 0) { genItemModel(builder, prefix + "_mosaic_stairs", itemParent, stage, false, idPrefix); }

                for (int f = 0; f < facings.length; f++) {
                    String facing = facings[f];
                    int yBase = yRots[f];
                    for (String half : halfs) {
                        for (String shape : shapes) {
                            int x = half.equals("top") ? 180 : 0;
                            int yRot = yBase;

                            if (half.equals("bottom")) {
                                if (shape.equals("outer_left") || shape.equals("inner_left")) {
                                    yRot = (yBase + 270) % 360;
                                }
                            } else {
                                if (shape.equals("outer_right") || shape.equals("inner_right")) {
                                    yRot = (yBase + 90) % 360;
                                }
                            }

                            String modelSuffix = "";
                            if (shape.contains("inner")) modelSuffix = "_inner";
                            else if (shape.contains("outer")) modelSuffix = "_outer";

                            String m = stage == 0 ? "minecraft:block/" + prefix + "_mosaic_stairs" + modelSuffix : moldmod.SporesShadows.MOD_ID + ":block/" + blockId + "_stage_" + stage + modelSuffix;

                            for (String waterlogged : new String[]{"false", "true"}) {
                                for (String common : getCommonProps()) {
                                    JsonObject v = new JsonObject();
                                    v.addProperty("model", m);
                                    if (x > 0) v.addProperty("x", x);
                                    if (yRot > 0) v.addProperty("y", yRot);
                                    if (x > 0 || yRot > 0) v.addProperty("uvlock", true);

                                    variants.add("facing=" + facing + ",half=" + half + ",shape=" + shape + ",stage=" + stage + "," + common + ",waterlogged=" + waterlogged, v);
                                }
                            }
                        }
                    }
                }
            }
            JsonObject bs = new JsonObject();
            bs.add("variants", variants);
            write(builder, "blockstates/" + blockId, bs);
        }
    }

    private static void genItem2d(ResourcePackBuilder builder, String itemName, String layer0) {
        JsonObject json = new JsonObject();
        json.addProperty("parent", "minecraft:item/generated");
        JsonObject textures = new JsonObject();
        textures.addProperty("layer0", layer0);
        json.add("textures", textures);
        write(builder, "models/item/" + itemName, json);
    }

    private static void genSign(ResourcePackBuilder builder, String wood, String prefix) {
        for (String idPrefix : new String[]{"moldy_", "waxed_"}) {
            for (String signType : new String[]{"_sign", "_wall_sign"}) {
                String blockId = idPrefix + prefix + signType;
                JsonObject variants = new JsonObject();
                JsonObject model = new JsonObject();
                model.addProperty("model", "minecraft:block/" + prefix + "_sign");
                variants.add("", model);
                JsonObject bs = new JsonObject();
                bs.add("variants", variants);
                write(builder, "blockstates/" + blockId, bs);
            }
        }

        String baseName = prefix + "_sign";
        genItem2d(builder, "waxed_" + baseName, "minecraft:item/" + prefix + "_sign");
        for (MoldStage ms : MoldStage.values()) {
            if (ms == MoldStage.WAXED) continue;
            String moldyTex = SporesShadows.MOD_ID + ":item/" + ms.getName() + "_" + baseName;
            genItem2d(builder, ms.getName() + "_" + baseName, moldyTex);
            genItem2d(builder, "waxed_" + ms.getName() + "_" + baseName, moldyTex);
        }
    }

    private static void genHangingSign(ResourcePackBuilder builder, String wood, String prefix) {
        for (String idPrefix : new String[]{"moldy_", "waxed_"}) {
            for (String signType : new String[]{"_hanging_sign", "_wall_hanging_sign"}) {
                String blockId = idPrefix + prefix + signType;
                JsonObject variants = new JsonObject();
                JsonObject model = new JsonObject();
                model.addProperty("model", "minecraft:block/" + prefix + "_hanging_sign");
                variants.add("", model);
                JsonObject bs = new JsonObject();
                bs.add("variants", variants);
                write(builder, "blockstates/" + blockId, bs);
            }
        }

        String baseName = prefix + "_hanging_sign";
        genItem2d(builder, "waxed_" + baseName, "minecraft:item/" + prefix + "_hanging_sign");
        for (MoldStage ms : MoldStage.values()) {
            if (ms == MoldStage.WAXED) continue;
            String moldyTex = SporesShadows.MOD_ID + ":item/" + ms.getName() + "_" + baseName;
            genItem2d(builder, ms.getName() + "_" + baseName, moldyTex);
            genItem2d(builder, "waxed_" + ms.getName() + "_" + baseName, moldyTex);
        }
    }

    private static void genBookshelf(ResourcePackBuilder builder) {
        for (String idPrefix : new String[]{"moldy_", "waxed_"}) {
            String blockId = idPrefix + "bookshelf";
            JsonObject variants = new JsonObject();
            for (moldmod.SporesShadowsConstants.MoldStage moldStage : moldmod.SporesShadowsConstants.MoldStage.values()) {
                int stage = moldStage.getId();
                if (stage > 0) {
                    JsonObject model = new JsonObject();
                    model.addProperty("parent", moldmod.SporesShadows.MOD_ID + ":block/mold/moldy_cube_column");
                    JsonObject textures = new JsonObject();
                    textures.addProperty("end", "minecraft:block/oak_planks");
                    textures.addProperty("side", "minecraft:block/bookshelf");
                    textures.addProperty("overlay", moldmod.SporesShadows.MOD_ID + ":block/mold/mold_stage_" + stage);
                    model.add("textures", textures);
                    write(builder, "models/block/" + blockId + "_stage_" + stage, model);
                }
                String itemParent = stage == 0 ? "minecraft:block/bookshelf" : blockId + "_stage_" + stage;
                if (idPrefix.equals("waxed_") || stage > 0) {
                    genItemModel(builder, "bookshelf", itemParent, stage, false, idPrefix);
                }

                String m = stage == 0 ? "minecraft:block/bookshelf" : moldmod.SporesShadows.MOD_ID + ":block/" + blockId + "_stage_" + stage;
                for (String common : getCommonProps()) {
                    JsonObject v = new JsonObject();
                    v.addProperty("model", m);
                    variants.add("stage=" + stage + "," + common, v);
                }
            }
            JsonObject bs = new JsonObject();
            bs.add("variants", variants);
            write(builder, "blockstates/" + blockId, bs);
        }
    }

    private static void genChiseledBookshelf(ResourcePackBuilder builder) {
        String[] slotNames = new String[]{
            "top_left", "top_mid", "top_right",
            "bottom_left", "bottom_mid", "bottom_right"
        };
        String[] facings = new String[]{"north", "east", "south", "west"};
        int[] rotY = new int[]{0, 90, 180, 270};

        for (String idPrefix : new String[]{"moldy_", "waxed_"}) {
            String blockId = idPrefix + "chiseled_bookshelf";

            // 1. Models for each stage
            for (moldmod.SporesShadowsConstants.MoldStage moldStage : moldmod.SporesShadowsConstants.MoldStage.values()) {
                int stage = moldStage.getId();
                if (stage > 0) {
                    // Block base model
                    JsonObject blockModel = new JsonObject();
                    blockModel.addProperty("parent", moldmod.SporesShadows.MOD_ID + ":block/mold/moldy_chiseled_bookshelf");
                    JsonObject blockTextures = new JsonObject();
                    blockTextures.addProperty("top", "minecraft:block/chiseled_bookshelf_top");
                    blockTextures.addProperty("side", "minecraft:block/chiseled_bookshelf_side");
                    blockTextures.addProperty("overlay", moldmod.SporesShadows.MOD_ID + ":block/mold/mold_stage_" + stage);
                    blockModel.add("textures", blockTextures);
                    write(builder, "models/block/" + blockId + "_stage_" + stage, blockModel);

                    // Item inventory model
                    JsonObject invModel = new JsonObject();
                    invModel.addProperty("parent", moldmod.SporesShadows.MOD_ID + ":block/mold/moldy_chiseled_bookshelf_inventory");
                    JsonObject invTextures = new JsonObject();
                    invTextures.addProperty("top", "minecraft:block/chiseled_bookshelf_top");
                    invTextures.addProperty("side", "minecraft:block/chiseled_bookshelf_side");
                    invTextures.addProperty("front", "minecraft:block/chiseled_bookshelf_empty");
                    invTextures.addProperty("overlay", moldmod.SporesShadows.MOD_ID + ":block/mold/mold_stage_" + stage);
                    invModel.add("textures", invTextures);
                    write(builder, "models/block/" + blockId + "_stage_" + stage + "_inventory", invModel);
                }

                String itemParent = stage == 0 ? "minecraft:item/chiseled_bookshelf" : blockId + "_stage_" + stage + "_inventory";
                if (idPrefix.equals("waxed_") || stage > 0) {
                    genItemModel(builder, "chiseled_bookshelf", itemParent, stage, false, idPrefix);
                }
            }

            // 2. Multipart Blockstate
            JsonArray multipart = new JsonArray();

            // 2a. Base bodies for all 4 facings and 4 stages
            for (int f = 0; f < facings.length; f++) {
                String facing = facings[f];
                int y = rotY[f];
                for (moldmod.SporesShadowsConstants.MoldStage moldStage : moldmod.SporesShadowsConstants.MoldStage.values()) {
                    int stage = moldStage.getId();
                    String m = stage == 0 ? "minecraft:block/chiseled_bookshelf" : moldmod.SporesShadows.MOD_ID + ":block/" + blockId + "_stage_" + stage;
                    JsonObject part = new JsonObject();
                    JsonObject when = new JsonObject();
                    when.addProperty("facing", facing);
                    when.addProperty("stage", String.valueOf(stage));
                    part.add("when", when);
                    JsonObject apply = new JsonObject();
                    apply.addProperty("model", m);
                    apply.addProperty("uvlock", true);
                    if (y != 0) {
                        apply.addProperty("y", y);
                    }
                    part.add("apply", apply);
                    multipart.add(part);
                }
            }

            // 2b. Slots (occupied / empty) for all 4 facings
            for (int f = 0; f < facings.length; f++) {
                String facing = facings[f];
                int y = rotY[f];
                for (int slot = 0; slot < 6; slot++) {
                    String slotProp = "slot_" + slot + "_occupied";
                    String slotName = slotNames[slot];

                    // Occupied
                    JsonObject occPart = new JsonObject();
                    JsonObject occWhen = new JsonObject();
                    JsonArray occAnd = new JsonArray();
                    JsonObject occF = new JsonObject();
                    occF.addProperty("facing", facing);
                    occAnd.add(occF);
                    JsonObject occS = new JsonObject();
                    occS.addProperty(slotProp, "true");
                    occAnd.add(occS);
                    occWhen.add("AND", occAnd);
                    occPart.add("when", occWhen);
                    JsonObject occApply = new JsonObject();
                    occApply.addProperty("model", "minecraft:block/chiseled_bookshelf_occupied_slot_" + slotName);
                    if (y != 0) {
                        occApply.addProperty("y", y);
                    }
                    occPart.add("apply", occApply);
                    multipart.add(occPart);

                    // Empty
                    JsonObject emptyPart = new JsonObject();
                    JsonObject emptyWhen = new JsonObject();
                    JsonArray emptyAnd = new JsonArray();
                    JsonObject emptyF = new JsonObject();
                    emptyF.addProperty("facing", facing);
                    emptyAnd.add(emptyF);
                    JsonObject emptyS = new JsonObject();
                    emptyS.addProperty(slotProp, "false");
                    emptyAnd.add(emptyS);
                    emptyWhen.add("AND", emptyAnd);
                    emptyPart.add("when", emptyWhen);
                    JsonObject emptyApply = new JsonObject();
                    emptyApply.addProperty("model", "minecraft:block/chiseled_bookshelf_empty_slot_" + slotName);
                    if (y != 0) {
                        emptyApply.addProperty("y", y);
                    }
                    emptyPart.add("apply", emptyApply);
                    multipart.add(emptyPart);
                }
            }

            JsonObject bs = new JsonObject();
            bs.add("multipart", multipart);
            write(builder, "blockstates/" + blockId, bs);
        }
    }

    private static void genLadder(ResourcePackBuilder builder) {
        String[] facings = new String[]{"north", "east", "south", "west"};
        int[] rotY = new int[]{0, 90, 180, 270};

        for (String idPrefix : new String[]{"moldy_", "waxed_"}) {
            String blockId = idPrefix + "ladder";
            JsonObject variants = new JsonObject();

            for (MoldStage moldStage : MoldStage.values()) {
                int stage = moldStage.getId();
                if (stage > 0) {
                    JsonObject model = new JsonObject();
                    model.addProperty("parent", SporesShadows.MOD_ID + ":block/mold/moldy_ladder");
                    JsonObject textures = new JsonObject();
                    textures.addProperty("particle", "minecraft:block/ladder");
                    textures.addProperty("ladder", "minecraft:block/ladder");
                    textures.addProperty("overlay", SporesShadows.MOD_ID + ":block/mold/mold_stage_" + stage);
                    model.add("textures", textures);
                    write(builder, "models/block/" + blockId + "_stage_" + stage, model);
                }

                // Ladder Item Model (2D icon)
                if (idPrefix.equals("waxed_") || stage > 0) {
                    String stageName = moldStage.getName();
                    String itemName = idPrefix.equals("waxed_")
                            ? (stage == 0 ? "waxed_ladder" : "waxed_" + stageName + "_ladder")
                            : stageName + "_ladder";
                    JsonObject itemJson = new JsonObject();
                    itemJson.addProperty("parent", "minecraft:item/generated");
                    JsonObject itemTex = new JsonObject();
                    if (stage == 0) {
                        itemTex.addProperty("layer0", "minecraft:block/ladder");
                    } else {
                        itemTex.addProperty("layer0", SporesShadows.MOD_ID + ":item/" + stageName + "_ladder");
                    }
                    itemJson.add("textures", itemTex);
                    write(builder, "models/item/" + itemName, itemJson);
                }

                String modelName = stage == 0 ? "minecraft:block/ladder" : SporesShadows.MOD_ID + ":block/" + blockId + "_stage_" + stage;
                for (int f = 0; f < facings.length; f++) {
                    String facing = facings[f];
                    int y = rotY[f];
                    for (String common : getCommonProps()) {
                        JsonObject v = new JsonObject();
                        v.addProperty("model", modelName);
                        if (y != 0) {
                            v.addProperty("y", y);
                        }
                        variants.add("facing=" + facing + ",stage=" + stage + "," + common, v);
                    }
                }
            }
            JsonObject bs = new JsonObject();
            bs.add("variants", variants);
            write(builder, "blockstates/" + blockId, bs);
        }
    }

    private static void genNoteBlock(ResourcePackBuilder builder) {
        for (String idPrefix : new String[]{"moldy_", "waxed_"}) {
            String blockId = idPrefix + "note_block";
            JsonObject variants = new JsonObject();

            for (MoldStage moldStage : MoldStage.values()) {
                int stage = moldStage.getId();
                if (stage > 0) {
                    JsonObject model = new JsonObject();
                    model.addProperty("parent", SporesShadows.MOD_ID + ":block/mold/moldy_cube_all");
                    JsonObject textures = new JsonObject();
                    textures.addProperty("all", "minecraft:block/note_block");
                    textures.addProperty("overlay", SporesShadows.MOD_ID + ":block/mold/mold_stage_" + stage);
                    model.add("textures", textures);
                    write(builder, "models/block/" + blockId + "_stage_" + stage, model);
                }

                String itemParent = stage == 0 ? "minecraft:block/note_block" : blockId + "_stage_" + stage;
                if (idPrefix.equals("waxed_") || stage > 0) {
                    genItemModel(builder, "note_block", itemParent, stage, false, idPrefix);
                }

                String m = stage == 0 ? "minecraft:block/note_block" : SporesShadows.MOD_ID + ":block/" + blockId + "_stage_" + stage;
                for (String common : getCommonProps()) {
                    JsonObject v = new JsonObject();
                    v.addProperty("model", m);
                    variants.add("stage=" + stage + "," + common, v);
                }
            }
            JsonObject bs = new JsonObject();
            bs.add("variants", variants);
            write(builder, "blockstates/" + blockId, bs);
        }
    }

    private static void genJukebox(ResourcePackBuilder builder) {
        for (String idPrefix : new String[]{"moldy_", "waxed_"}) {
            String blockId = idPrefix + "jukebox";
            JsonObject variants = new JsonObject();

            for (MoldStage moldStage : MoldStage.values()) {
                int stage = moldStage.getId();
                if (stage > 0) {
                    JsonObject model = new JsonObject();
                    model.addProperty("parent", SporesShadows.MOD_ID + ":block/mold/moldy_cube_column");
                    JsonObject textures = new JsonObject();
                    textures.addProperty("end", "minecraft:block/jukebox_top");
                    textures.addProperty("side", "minecraft:block/jukebox_side");
                    textures.addProperty("overlay", SporesShadows.MOD_ID + ":block/mold/mold_stage_" + stage);
                    model.add("textures", textures);
                    write(builder, "models/block/" + blockId + "_stage_" + stage, model);
                }

                String itemParent = stage == 0 ? "minecraft:block/jukebox" : blockId + "_stage_" + stage;
                if (idPrefix.equals("waxed_") || stage > 0) {
                    genItemModel(builder, "jukebox", itemParent, stage, false, idPrefix);
                }

                String m = stage == 0 ? "minecraft:block/jukebox" : SporesShadows.MOD_ID + ":block/" + blockId + "_stage_" + stage;
                for (String common : getCommonProps()) {
                    JsonObject vFalse = new JsonObject();
                    vFalse.addProperty("model", m);
                    variants.add("has_record=false,stage=" + stage + "," + common, vFalse);

                    JsonObject vTrue = new JsonObject();
                    vTrue.addProperty("model", m);
                    variants.add("has_record=true,stage=" + stage + "," + common, vTrue);
                }
            }
            JsonObject bs = new JsonObject();
            bs.add("variants", variants);
            write(builder, "blockstates/" + blockId, bs);
        }
    }

    private static void genCraftingTable(ResourcePackBuilder builder) {
        for (String idPrefix : new String[]{"moldy_", "waxed_"}) {
            String blockId = idPrefix + "crafting_table";
            JsonObject variants = new JsonObject();

            for (MoldStage moldStage : MoldStage.values()) {
                int stage = moldStage.getId();
                if (stage > 0) {
                    JsonObject model = new JsonObject();
                    model.addProperty("parent", SporesShadows.MOD_ID + ":block/mold/moldy_cube");
                    JsonObject textures = new JsonObject();
                    textures.addProperty("down", "minecraft:block/oak_planks");
                    textures.addProperty("east", "minecraft:block/crafting_table_side");
                    textures.addProperty("north", "minecraft:block/crafting_table_front");
                    textures.addProperty("south", "minecraft:block/crafting_table_side");
                    textures.addProperty("west", "minecraft:block/crafting_table_front");
                    textures.addProperty("up", "minecraft:block/crafting_table_top");
                    textures.addProperty("overlay", SporesShadows.MOD_ID + ":block/mold/mold_stage_" + stage);
                    model.add("textures", textures);
                    write(builder, "models/block/" + blockId + "_stage_" + stage, model);
                }

                String itemParent = stage == 0 ? "minecraft:block/crafting_table" : blockId + "_stage_" + stage;
                if (idPrefix.equals("waxed_") || stage > 0) {
                    genItemModel(builder, "crafting_table", itemParent, stage, false, idPrefix);
                }

                String m = stage == 0 ? "minecraft:block/crafting_table" : SporesShadows.MOD_ID + ":block/" + blockId + "_stage_" + stage;
                for (String common : getCommonProps()) {
                    JsonObject v = new JsonObject();
                    v.addProperty("model", m);
                    variants.add("stage=" + stage + "," + common, v);
                }
            }
            JsonObject bs = new JsonObject();
            bs.add("variants", variants);
            write(builder, "blockstates/" + blockId, bs);
        }
    }

    private static void genFletchingTable(ResourcePackBuilder builder) {
        for (String idPrefix : new String[]{"moldy_", "waxed_"}) {
            String blockId = idPrefix + "fletching_table";
            JsonObject variants = new JsonObject();

            for (MoldStage moldStage : MoldStage.values()) {
                int stage = moldStage.getId();
                if (stage > 0) {
                    JsonObject model = new JsonObject();
                    model.addProperty("parent", SporesShadows.MOD_ID + ":block/mold/moldy_cube");
                    JsonObject textures = new JsonObject();
                    textures.addProperty("down", "minecraft:block/birch_planks");
                    textures.addProperty("east", "minecraft:block/fletching_table_side");
                    textures.addProperty("north", "minecraft:block/fletching_table_front");
                    textures.addProperty("south", "minecraft:block/fletching_table_front");
                    textures.addProperty("west", "minecraft:block/fletching_table_side");
                    textures.addProperty("up", "minecraft:block/fletching_table_top");
                    textures.addProperty("overlay", SporesShadows.MOD_ID + ":block/mold/mold_stage_" + stage);
                    model.add("textures", textures);
                    write(builder, "models/block/" + blockId + "_stage_" + stage, model);
                }

                String itemParent = stage == 0 ? "minecraft:block/fletching_table" : blockId + "_stage_" + stage;
                if (idPrefix.equals("waxed_") || stage > 0) {
                    genItemModel(builder, "fletching_table", itemParent, stage, false, idPrefix);
                }

                String m = stage == 0 ? "minecraft:block/fletching_table" : SporesShadows.MOD_ID + ":block/" + blockId + "_stage_" + stage;
                for (String common : getCommonProps()) {
                    JsonObject v = new JsonObject();
                    v.addProperty("model", m);
                    variants.add("stage=" + stage + "," + common, v);
                }
            }
            JsonObject bs = new JsonObject();
            bs.add("variants", variants);
            write(builder, "blockstates/" + blockId, bs);
        }
    }

    private static void genBarrel(ResourcePackBuilder builder) {
        String[] facings = new String[]{"down", "east", "north", "south", "up", "west"};
        int[] rotX = new int[]{180, 90, 90, 90, 0, 90};
        int[] rotY = new int[]{0, 90, 0, 180, 0, 270};

        for (String idPrefix : new String[]{"moldy_", "waxed_"}) {
            String blockId = idPrefix + "barrel";
            JsonObject variants = new JsonObject();

            for (MoldStage moldStage : MoldStage.values()) {
                int stage = moldStage.getId();
                if (stage > 0) {
                    // Closed model
                    JsonObject closed = new JsonObject();
                    closed.addProperty("parent", SporesShadows.MOD_ID + ":block/mold/moldy_cube_bottom_top");
                    JsonObject cTex = new JsonObject();
                    cTex.addProperty("bottom", "minecraft:block/barrel_bottom");
                    cTex.addProperty("side", "minecraft:block/barrel_side");
                    cTex.addProperty("top", "minecraft:block/barrel_top");
                    cTex.addProperty("overlay", SporesShadows.MOD_ID + ":block/mold/mold_stage_" + stage);
                    closed.add("textures", cTex);
                    write(builder, "models/block/" + blockId + "_stage_" + stage, closed);

                    // Open model
                    JsonObject open = new JsonObject();
                    open.addProperty("parent", SporesShadows.MOD_ID + ":block/mold/moldy_cube_bottom_top");
                    JsonObject oTex = new JsonObject();
                    oTex.addProperty("bottom", "minecraft:block/barrel_bottom");
                    oTex.addProperty("side", "minecraft:block/barrel_side");
                    oTex.addProperty("top", "minecraft:block/barrel_top_open");
                    oTex.addProperty("overlay", SporesShadows.MOD_ID + ":block/mold/mold_stage_" + stage);
                    open.add("textures", oTex);
                    write(builder, "models/block/" + blockId + "_open_stage_" + stage, open);
                }

                String itemParent = stage == 0 ? "minecraft:block/barrel" : blockId + "_stage_" + stage;
                if (idPrefix.equals("waxed_") || stage > 0) {
                    genItemModel(builder, "barrel", itemParent, stage, false, idPrefix);
                }

                String mClosed = stage == 0 ? "minecraft:block/barrel" : SporesShadows.MOD_ID + ":block/" + blockId + "_stage_" + stage;
                String mOpen = stage == 0 ? "minecraft:block/barrel_open" : SporesShadows.MOD_ID + ":block/" + blockId + "_open_stage_" + stage;

                for (int f = 0; f < facings.length; f++) {
                    String facing = facings[f];
                    int x = rotX[f];
                    int y = rotY[f];
                    for (String common : getCommonProps()) {
                        // open=false
                        JsonObject vFalse = new JsonObject();
                        vFalse.addProperty("model", mClosed);
                        if (x != 0) vFalse.addProperty("x", x);
                        if (y != 0) vFalse.addProperty("y", y);
                        variants.add("facing=" + facing + ",open=false,stage=" + stage + "," + common, vFalse);

                        // open=true
                        JsonObject vTrue = new JsonObject();
                        vTrue.addProperty("model", mOpen);
                        if (x != 0) vTrue.addProperty("x", x);
                        if (y != 0) vTrue.addProperty("y", y);
                        variants.add("facing=" + facing + ",open=true,stage=" + stage + "," + common, vTrue);
                    }
                }
            }
            JsonObject bs = new JsonObject();
            bs.add("variants", variants);
            write(builder, "blockstates/" + blockId, bs);
        }
    }

    private static void genComposter(ResourcePackBuilder builder) {
        for (String idPrefix : new String[]{"moldy_", "waxed_"}) {
            String blockId = idPrefix + "composter";

            for (MoldStage moldStage : MoldStage.values()) {
                int stage = moldStage.getId();
                if (stage > 0) {
                    JsonObject model = new JsonObject();
                    model.addProperty("parent", SporesShadows.MOD_ID + ":block/mold/moldy_composter");
                    JsonObject textures = new JsonObject();
                    textures.addProperty("particle", "minecraft:block/composter_side");
                    textures.addProperty("top", "minecraft:block/composter_top");
                    textures.addProperty("bottom", "minecraft:block/composter_bottom");
                    textures.addProperty("side", "minecraft:block/composter_side");
                    textures.addProperty("inside", "minecraft:block/composter_bottom");
                    textures.addProperty("overlay", SporesShadows.MOD_ID + ":block/mold/mold_stage_" + stage);
                    model.add("textures", textures);
                    write(builder, "models/block/" + blockId + "_stage_" + stage, model);
                }

                String itemParent = stage == 0 ? "minecraft:block/composter" : blockId + "_stage_" + stage;
                if (idPrefix.equals("waxed_") || stage > 0) {
                    genItemModel(builder, "composter", itemParent, stage, false, idPrefix);
                }
            }

            JsonArray multipart = new JsonArray();

            // Base body for each stage
            for (MoldStage moldStage : MoldStage.values()) {
                int stage = moldStage.getId();
                String m = stage == 0 ? "minecraft:block/composter" : SporesShadows.MOD_ID + ":block/" + blockId + "_stage_" + stage;
                JsonObject part = new JsonObject();
                JsonObject when = new JsonObject();
                when.addProperty("stage", String.valueOf(stage));
                part.add("when", when);
                JsonObject apply = new JsonObject();
                apply.addProperty("model", m);
                part.add("apply", apply);
                multipart.add(part);
            }

            // Composter contents for level 1..7
            for (int lvl = 1; lvl <= 7; lvl++) {
                JsonObject part = new JsonObject();
                JsonObject when = new JsonObject();
                when.addProperty("level", String.valueOf(lvl));
                part.add("when", when);
                JsonObject apply = new JsonObject();
                apply.addProperty("model", "minecraft:block/composter_contents" + lvl);
                part.add("apply", apply);
                multipart.add(part);
            }

            // Composter contents ready for level 8
            JsonObject part8 = new JsonObject();
            JsonObject when8 = new JsonObject();
            when8.addProperty("level", "8");
            part8.add("when", when8);
            JsonObject apply8 = new JsonObject();
            apply8.addProperty("model", "minecraft:block/composter_contents_ready");
            part8.add("apply", apply8);
            multipart.add(part8);

            JsonObject bs = new JsonObject();
            bs.add("multipart", multipart);
            write(builder, "blockstates/" + blockId, bs);
        }
    }

    private static void genChests(ResourcePackBuilder builder) {
        for (String baseChest : new String[]{"chest", "trapped_chest"}) {
            for (String idPrefix : new String[]{"", "waxed_"}) {
                for (MoldStage moldStage : MoldStage.values()) {
                    int stage = moldStage.getId();
                    if (idPrefix.isEmpty() && stage == 0) continue;

                    String stageName = moldStage.getName();
                    String itemName;
                    if (idPrefix.equals("waxed_")) {
                        itemName = (stage == 0) ? "waxed_" + baseChest : "waxed_" + stageName + "_" + baseChest;
                    } else {
                        itemName = stageName + "_" + baseChest;
                    }

                    JsonObject itemJson = new JsonObject();
                    itemJson.addProperty("parent", "minecraft:item/" + baseChest);
                    write(builder, "models/item/" + itemName, itemJson);
                }
            }

            for (String idPrefix : new String[]{"moldy_", "waxed_"}) {
                String blockId = idPrefix + baseChest;
                String modelRef = SporesShadows.MOD_ID + ":block/" + blockId;

                JsonObject modelJson = new JsonObject();
                JsonObject texturesJson = new JsonObject();
                texturesJson.addProperty("particle", "minecraft:block/oak_planks");
                modelJson.add("textures", texturesJson);
                write(builder, "models/block/" + blockId, modelJson);

                JsonObject variants = new JsonObject();

                String[] facings = {"east", "north", "south", "west"};
                String[] types = {"left", "right", "single"};
                String[] waterloggeds = {"false", "true"};
                String[] structurals = {"false", "true"};
                String[] waxeds = {"false", "true"};

                for (String facing : facings) {
                    for (MoldStage moldStage : MoldStage.values()) {
                        int stage = moldStage.getId();
                        for (String structural : structurals) {
                            for (String type : types) {
                                for (String waterlogged : waterloggeds) {
                                    for (String waxed : waxeds) {
                                        String key = "facing=" + facing + ",stage=" + stage + ",structural=" + structural + ",type=" + type + ",waterlogged=" + waterlogged + ",waxed=" + waxed;
                                        JsonObject variant = new JsonObject();
                                        variant.addProperty("model", modelRef);
                                        variants.add(key, variant);
                                    }
                                }
                            }
                        }
                    }
                }

                JsonObject bs = new JsonObject();
                bs.add("variants", variants);
                write(builder, "blockstates/" + blockId, bs);
            }
        }
    }

    private static void genCartographyTable(ResourcePackBuilder builder) {
        for (String idPrefix : new String[]{"moldy_", "waxed_"}) {
            String blockId = idPrefix + "cartography_table";
            JsonObject variants = new JsonObject();

            for (MoldStage moldStage : MoldStage.values()) {
                int stage = moldStage.getId();
                if (stage > 0) {
                    JsonObject model = new JsonObject();
                    model.addProperty("parent", SporesShadows.MOD_ID + ":block/mold/moldy_cube");
                    JsonObject textures = new JsonObject();
                    textures.addProperty("down", "minecraft:block/dark_oak_planks");
                    textures.addProperty("east", "minecraft:block/cartography_table_side3");
                    textures.addProperty("north", "minecraft:block/cartography_table_side3");
                    textures.addProperty("south", "minecraft:block/cartography_table_side1");
                    textures.addProperty("west", "minecraft:block/cartography_table_side2");
                    textures.addProperty("up", "minecraft:block/cartography_table_top");
                    textures.addProperty("overlay", SporesShadows.MOD_ID + ":block/mold/mold_stage_" + stage);
                    model.add("textures", textures);
                    write(builder, "models/block/" + blockId + "_stage_" + stage, model);
                }

                String itemParent = stage == 0 ? "minecraft:block/cartography_table" : blockId + "_stage_" + stage;
                if (idPrefix.equals("waxed_") || stage > 0) {
                    genItemModel(builder, "cartography_table", itemParent, stage, false, idPrefix);
                }

                String m = stage == 0 ? "minecraft:block/cartography_table" : SporesShadows.MOD_ID + ":block/" + blockId + "_stage_" + stage;
                for (String common : getCommonProps()) {
                    JsonObject v = new JsonObject();
                    v.addProperty("model", m);
                    variants.add("stage=" + stage + "," + common, v);
                }
            }
            JsonObject bs = new JsonObject();
            bs.add("variants", variants);
            write(builder, "blockstates/" + blockId, bs);
        }
    }

    private static void genLoom(ResourcePackBuilder builder) {
        String[] facings = new String[]{"north", "east", "south", "west"};
        int[] rotY = new int[]{0, 90, 180, 270};

        for (String idPrefix : new String[]{"moldy_", "waxed_"}) {
            String blockId = idPrefix + "loom";
            JsonObject variants = new JsonObject();

            for (MoldStage moldStage : MoldStage.values()) {
                int stage = moldStage.getId();
                if (stage > 0) {
                    JsonObject model = new JsonObject();
                    model.addProperty("parent", SporesShadows.MOD_ID + ":block/mold/moldy_cube");
                    JsonObject textures = new JsonObject();
                    textures.addProperty("down", "minecraft:block/loom_bottom");
                    textures.addProperty("up", "minecraft:block/loom_top");
                    textures.addProperty("north", "minecraft:block/loom_front");
                    textures.addProperty("south", "minecraft:block/loom_side");
                    textures.addProperty("east", "minecraft:block/loom_side");
                    textures.addProperty("west", "minecraft:block/loom_side");
                    textures.addProperty("overlay", SporesShadows.MOD_ID + ":block/mold/mold_stage_" + stage);
                    model.add("textures", textures);
                    write(builder, "models/block/" + blockId + "_stage_" + stage, model);
                }

                String itemParent = stage == 0 ? "minecraft:block/loom" : blockId + "_stage_" + stage;
                if (idPrefix.equals("waxed_") || stage > 0) {
                    genItemModel(builder, "loom", itemParent, stage, false, idPrefix);
                }

                String m = stage == 0 ? "minecraft:block/loom" : SporesShadows.MOD_ID + ":block/" + blockId + "_stage_" + stage;

                for (int f = 0; f < facings.length; f++) {
                    String facing = facings[f];
                    int y = rotY[f];
                    for (String common : getCommonProps()) {
                        JsonObject v = new JsonObject();
                        v.addProperty("model", m);
                        if (y != 0) v.addProperty("y", y);
                        variants.add("facing=" + facing + ",stage=" + stage + "," + common, v);
                    }
                }
            }
            JsonObject bs = new JsonObject();
            bs.add("variants", variants);
            write(builder, "blockstates/" + blockId, bs);
        }
    }

    private static void genLectern(ResourcePackBuilder builder) {
        String[] facings = new String[]{"north", "east", "south", "west"};
        int[] rotY = new int[]{0, 90, 180, 270};

        for (String idPrefix : new String[]{"moldy_", "waxed_"}) {
            String blockId = idPrefix + "lectern";
            JsonObject variants = new JsonObject();

            for (MoldStage moldStage : MoldStage.values()) {
                int stage = moldStage.getId();
                if (stage > 0) {
                    JsonObject model = new JsonObject();
                    model.addProperty("parent", SporesShadows.MOD_ID + ":block/mold/moldy_lectern");
                    JsonObject textures = new JsonObject();
                    textures.addProperty("bottom", "minecraft:block/oak_planks");
                    textures.addProperty("base", "minecraft:block/lectern_base");
                    textures.addProperty("front", "minecraft:block/lectern_front");
                    textures.addProperty("sides", "minecraft:block/lectern_sides");
                    textures.addProperty("top", "minecraft:block/lectern_top");
                    textures.addProperty("overlay", SporesShadows.MOD_ID + ":block/mold/mold_stage_" + stage);
                    model.add("textures", textures);
                    write(builder, "models/block/" + blockId + "_stage_" + stage, model);
                }

                String itemParent = stage == 0 ? "minecraft:block/lectern" : blockId + "_stage_" + stage;
                if (idPrefix.equals("waxed_") || stage > 0) {
                    genItemModel(builder, "lectern", itemParent, stage, false, idPrefix);
                }

                String m = stage == 0 ? "minecraft:block/lectern" : SporesShadows.MOD_ID + ":block/" + blockId + "_stage_" + stage;

                for (int f = 0; f < facings.length; f++) {
                    String facing = facings[f];
                    int y = rotY[f];
                    for (String hasBook : new String[]{"false", "true"}) {
                        for (String powered : new String[]{"false", "true"}) {
                            for (String common : getCommonProps()) {
                                JsonObject v = new JsonObject();
                                v.addProperty("model", m);
                                if (y != 0) v.addProperty("y", y);
                                variants.add("facing=" + facing + ",has_book=" + hasBook + ",powered=" + powered + ",stage=" + stage + "," + common, v);
                            }
                        }
                    }
                }
            }
            JsonObject bs = new JsonObject();
            bs.add("variants", variants);
            write(builder, "blockstates/" + blockId, bs);
        }
    }
}

