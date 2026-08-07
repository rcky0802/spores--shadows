package moldmod.resource;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import eu.pb4.polymer.resourcepack.api.ResourcePackBuilder;


public class MoldyJsonGenerator {

    private static final int[] STAGES = {0, 1, 2, 3};
    private static final String[] STAGES_NAMES = {"waxed", "tainted", "moldy", "rotten"};

    public static void generateAll(ResourcePackBuilder builder, String[] woods) {
        for (String wood : woods) {
            String p = wood.equals("bamboo") ? "bamboo" : wood;
            genPlanks(builder, wood, p);
            genLog(builder, wood, wood + "_log", false);
            genLog(builder, wood, "stripped_" + wood + "_log", false);
            genLog(builder, wood, wood + "_wood", true);
            genLog(builder, wood, "stripped_" + wood + "_wood", true);
            genSlab(builder, wood, p);
            genStairs(builder, wood, p);
            genDoor(builder, wood, p);
            genTrapdoor(builder, wood, p);
            genFence(builder, wood, p);
            genGate(builder, wood, p);
            genPressurePlate(builder, wood, p);
            genButton(builder, wood, p);
        }
    }

    private static void write(ResourcePackBuilder builder, String path, JsonObject json) {
        builder.addData("assets/spores--shadows/" + path + ".json", json.toString().getBytes());
    }

    private static void genItemModel(ResourcePackBuilder builder, String wood, String baseName, String parentModelName, int stage, boolean is2d) {
        String itemName = STAGES_NAMES[stage] + "_" + baseName;
        JsonObject json = new JsonObject();
        if (is2d) {
            // For doors, we are already doing this in MoldyResourceGenerator with the custom PNG blending!
            if (!baseName.contains("door") || stage == 0) {
                json.addProperty("parent", "minecraft:item/generated");
                JsonObject textures = new JsonObject();
                if (stage == 0) {
                    textures.addProperty("layer0", "minecraft:item/" + baseName);
                } else {
                    textures.addProperty("layer0", "spores--shadows:item/moldy_" + baseName + "_stage_" + stage);
                }
                json.add("textures", textures);
            } else {
                return; // Handled by image generator
            }
        } else {
            if (stage == 0) {
                json.addProperty("parent", parentModelName);
            } else {
                json.addProperty("parent", "spores--shadows:block/" + parentModelName);
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
        String blockId = "moldy_" + prefix + "_planks";
        JsonObject variants = new JsonObject();
        for (int stage : STAGES) {
            String tex = "minecraft:block/" + prefix + "_planks";
            if (stage > 0) {
                JsonObject model = new JsonObject();
                model.addProperty("parent", "spores--shadows:block/moldy_cube_all");
                JsonObject textures = new JsonObject();
                textures.addProperty("all", tex);
                textures.addProperty("overlay", "spores--shadows:block/mold_stage_" + stage);
                model.add("textures", textures);
                write(builder, "models/block/" + blockId + "_stage_" + stage, model);
            }
            String itemParent = stage == 0 ? "minecraft:block/" + prefix + "_planks" : blockId + "_stage_" + stage;
            genItemModel(builder, wood, prefix + "_planks", itemParent, stage, false);
            
            String m = stage == 0 ? "minecraft:block/" + prefix + "_planks" : "spores--shadows:block/" + blockId + "_stage_" + stage;
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

    private static void genLog(ResourcePackBuilder builder, String wood, String logName, boolean isWood) {
        String blockId = "moldy_" + logName;
        JsonObject variants = new JsonObject();
        
        // For wood blocks, the texture is actually the side texture of the corresponding log
        String textureBase = isWood ? logName.replace("_wood", "_log") : logName;
        String vanillaTex = "minecraft:block/" + textureBase;
        String topTex = isWood ? vanillaTex : vanillaTex + "_top";
        
        for (int stage : STAGES) {
            if (stage > 0) {
                JsonObject model = new JsonObject();
                model.addProperty("parent", "spores--shadows:block/moldy_cube_column");
                JsonObject textures = new JsonObject();
                textures.addProperty("end", topTex);
                textures.addProperty("side", vanillaTex);
                textures.addProperty("overlay", "spores--shadows:block/mold_stage_" + stage);
                model.add("textures", textures);
                write(builder, "models/block/" + blockId + "_stage_" + stage, model);
            }
            String itemParent = stage == 0 ? "minecraft:block/" + logName : blockId + "_stage_" + stage;
            genItemModel(builder, wood, logName, itemParent, stage, false);
            
            String m = stage == 0 ? "minecraft:block/" + logName : "spores--shadows:block/" + blockId + "_stage_" + stage;
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

    private static void genSlab(ResourcePackBuilder builder, String wood, String prefix) {
        String blockId = "moldy_" + prefix + "_slab";
        JsonObject variants = new JsonObject();
        for (int stage : STAGES) {
            String tex = "minecraft:block/" + prefix + "_planks";
            if (stage > 0) {
                JsonObject mBot = new JsonObject();
                mBot.addProperty("parent", "spores--shadows:block/moldy_slab");
                JsonObject tBot = new JsonObject();
                tBot.addProperty("bottom", tex); tBot.addProperty("top", tex); tBot.addProperty("side", tex); tBot.addProperty("overlay", "spores--shadows:block/mold_stage_" + stage);
                mBot.add("textures", tBot);
                write(builder, "models/block/" + blockId + "_stage_" + stage, mBot);

                JsonObject mTop = new JsonObject();
                mTop.addProperty("parent", "spores--shadows:block/moldy_slab_top");
                mTop.add("textures", tBot);
                write(builder, "models/block/" + blockId + "_stage_" + stage + "_top", mTop);
            }
            String itemParent = stage == 0 ? "minecraft:block/" + prefix + "_slab" : blockId + "_stage_" + stage;
            genItemModel(builder, wood, prefix + "_slab", itemParent, stage, false);
            
            String mBottom = stage == 0 ? "minecraft:block/" + prefix + "_slab" : "spores--shadows:block/" + blockId + "_stage_" + stage;
            String mTopStr = stage == 0 ? "minecraft:block/" + prefix + "_slab_top" : "spores--shadows:block/" + blockId + "_stage_" + stage + "_top";
            String mDouble = stage == 0 ? "minecraft:block/" + prefix + "_planks" : "spores--shadows:block/moldy_" + prefix + "_planks_stage_" + stage;
            
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

    private static void genStairs(ResourcePackBuilder builder, String wood, String prefix) {
        String blockId = "moldy_" + prefix + "_stairs";
        JsonObject variants = new JsonObject();
        String[] facings = {"east", "south", "west", "north"};
        int[] yRots = {0, 90, 180, 270};
        String[] shapes = {"straight", "inner_left", "inner_right", "outer_left", "outer_right"};
        String[] halfs = {"bottom", "top"};

        for (int stage : STAGES) {
            String tex = "minecraft:block/" + prefix + "_planks";
            if (stage > 0) {
                JsonObject mDef = new JsonObject(); mDef.addProperty("parent", "spores--shadows:block/moldy_stairs");
                JsonObject tDef = new JsonObject(); tDef.addProperty("bottom", tex); tDef.addProperty("top", tex); tDef.addProperty("side", tex); tDef.addProperty("overlay", "spores--shadows:block/mold_stage_" + stage);
                mDef.add("textures", tDef);
                write(builder, "models/block/" + blockId + "_stage_" + stage, mDef);

                JsonObject mIn = new JsonObject(); mIn.addProperty("parent", "spores--shadows:block/moldy_inner_stairs"); mIn.add("textures", tDef);
                write(builder, "models/block/" + blockId + "_stage_" + stage + "_inner", mIn);

                JsonObject mOut = new JsonObject(); mOut.addProperty("parent", "spores--shadows:block/moldy_outer_stairs"); mOut.add("textures", tDef);
                write(builder, "models/block/" + blockId + "_stage_" + stage + "_outer", mOut);
            }
            String itemParent = stage == 0 ? "minecraft:block/" + prefix + "_stairs" : blockId + "_stage_" + stage;
            genItemModel(builder, wood, prefix + "_stairs", itemParent, stage, false);

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
                        String m = stage == 0 ? "minecraft:block/" + prefix + "_" + mName : "spores--shadows:block/" + blockId + "_stage_" + stage + (shape.contains("inner") ? "_inner" : (shape.contains("outer") ? "_outer" : ""));

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

    private static void genDoor(ResourcePackBuilder builder, String wood, String prefix) {
        String blockId = "moldy_" + prefix + "_door";
        JsonObject variants = new JsonObject();
        for (int stage : STAGES) {
            if (stage > 0) {
                String texBot = "minecraft:block/" + prefix + "_door_bottom";
                String texTop = "minecraft:block/" + prefix + "_door_top";
                for (String half : new String[]{"bottom", "top"}) {
                    for (String hinge : new String[]{"left", "right"}) {
                        for (String openS : new String[]{"", "_open"}) {
                            String mName = blockId + "_" + half + "_" + hinge + openS + "_stage_" + stage;
                            String parent = "spores--shadows:block/moldy_door_" + half + "_" + hinge + openS;
                            JsonObject mDef = new JsonObject(); mDef.addProperty("parent", parent);
                            JsonObject tDef = new JsonObject(); tDef.addProperty("bottom", texBot); tDef.addProperty("top", texTop); tDef.addProperty("overlay", "spores--shadows:block/mold_stage_" + stage);
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
                                                      : "spores--shadows:block/" + blockId + "_" + tHalf + "_" + hinge + openS + "_stage_" + stage;

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

    private static void genTrapdoor(ResourcePackBuilder builder, String wood, String prefix) {
        String blockId = "moldy_" + prefix + "_trapdoor";
        JsonObject variants = new JsonObject();
        String[] facings = {"north", "east", "south", "west"};
        int[] yRots = {0, 90, 180, 270};

        for (int stage : STAGES) {
            String tex = "minecraft:block/" + prefix + "_trapdoor";
            if (stage > 0) {
                JsonObject mBot = new JsonObject(); mBot.addProperty("parent", "spores--shadows:block/moldy_template_orientable_trapdoor_bottom");
                JsonObject tBot = new JsonObject(); tBot.addProperty("texture", tex); tBot.addProperty("overlay", "spores--shadows:block/mold_stage_" + stage);
                mBot.add("textures", tBot); write(builder, "models/block/" + blockId + "_bottom_stage_" + stage, mBot);

                JsonObject mTop = new JsonObject(); mTop.addProperty("parent", "spores--shadows:block/moldy_template_orientable_trapdoor_top"); mTop.add("textures", tBot);
                write(builder, "models/block/" + blockId + "_top_stage_" + stage, mTop);

                JsonObject mOpn = new JsonObject(); mOpn.addProperty("parent", "spores--shadows:block/moldy_template_orientable_trapdoor_open"); mOpn.add("textures", tBot);
                write(builder, "models/block/" + blockId + "_open_stage_" + stage, mOpn);
            }
            String itemParent = stage == 0 ? "minecraft:block/" + prefix + "_trapdoor_bottom" : blockId + "_bottom_stage_" + stage;
            genItemModel(builder, wood, prefix + "_trapdoor", itemParent, stage, false);

            for (int f = 0; f < facings.length; f++) {
                String facing = facings[f];
                int yBase = yRots[f];
                for (String half : new String[]{"bottom", "top"}) {
                    for (String openState : new String[]{"false", "true"}) {
                        for (String powered : new String[]{"false", "true"}) {
                            for (String waterlogged : new String[]{"false", "true"}) {
                                String m;
                                if (openState.equals("true")) m = stage == 0 ? "minecraft:block/" + prefix + "_trapdoor_open" : "spores--shadows:block/" + blockId + "_open_stage_" + stage;
                                else if (half.equals("top")) m = stage == 0 ? "minecraft:block/" + prefix + "_trapdoor_top" : "spores--shadows:block/" + blockId + "_top_stage_" + stage;
                                else m = stage == 0 ? "minecraft:block/" + prefix + "_trapdoor_bottom" : "spores--shadows:block/" + blockId + "_bottom_stage_" + stage;

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

    private static void genFence(ResourcePackBuilder builder, String wood, String prefix) {
        String blockId = "moldy_" + prefix + "_fence";
        JsonArray multipart = new JsonArray();
        for (int stage : STAGES) {
            String tex = "minecraft:block/" + prefix + "_planks";
            if (stage > 0) {
                JsonObject mP = new JsonObject(); mP.addProperty("parent", "spores--shadows:block/moldy_fence_post");
                JsonObject tP = new JsonObject(); tP.addProperty("texture", tex); tP.addProperty("overlay", "spores--shadows:block/mold_stage_" + stage);
                mP.add("textures", tP); write(builder, "models/block/" + blockId + "_post_stage_" + stage, mP);

                JsonObject mS = new JsonObject(); mS.addProperty("parent", "spores--shadows:block/moldy_fence_side"); mS.add("textures", tP);
                write(builder, "models/block/" + blockId + "_side_stage_" + stage, mS);

                JsonObject mI = new JsonObject(); mI.addProperty("parent", "spores--shadows:block/moldy_fence_inventory"); mI.add("textures", tP);
                write(builder, "models/block/" + blockId + "_inventory_stage_" + stage, mI);
            }
            String itemParent = stage == 0 ? "minecraft:block/" + prefix + "_fence_inventory" : blockId + "_inventory_stage_" + stage;
            genItemModel(builder, wood, prefix + "_fence", itemParent, stage, false);

            String mPost = stage == 0 ? "minecraft:block/" + prefix + "_fence_post" : "spores--shadows:block/" + blockId + "_post_stage_" + stage;
            String mSide = stage == 0 ? "minecraft:block/" + prefix + "_fence_side" : "spores--shadows:block/" + blockId + "_side_stage_" + stage;

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
                        JsonObject aD = new JsonObject(); aD.addProperty("model", mSide); 
                        if (yRots[d] != 0) aD.addProperty("y", yRots[d]);
                        aD.addProperty("uvlock", true);
                        pD.add("apply", aD);
                        multipart.add(pD);
                    }
                }
            }
        }
        JsonObject bs = new JsonObject(); bs.add("multipart", multipart); write(builder, "blockstates/" + blockId, bs);
    }

    private static void genGate(ResourcePackBuilder builder, String wood, String prefix) {
        String blockId = "moldy_" + prefix + "_fence_gate";
        JsonObject variants = new JsonObject();
        String[] facings = {"north", "east", "south", "west"};
        int[] yRots = {0, 90, 180, 270};

        for (int stage : STAGES) {
            String tex = "minecraft:block/" + prefix + "_planks";
            if (stage > 0) {
                JsonObject mDef = new JsonObject(); mDef.addProperty("parent", "spores--shadows:block/moldy_template_fence_gate");
                JsonObject tDef = new JsonObject(); tDef.addProperty("texture", tex); tDef.addProperty("overlay", "spores--shadows:block/mold_stage_" + stage);
                mDef.add("textures", tDef); write(builder, "models/block/" + blockId + "_stage_" + stage, mDef);

                JsonObject mOpn = new JsonObject(); mOpn.addProperty("parent", "spores--shadows:block/moldy_template_fence_gate_open"); mOpn.add("textures", tDef); write(builder, "models/block/" + blockId + "_open_stage_" + stage, mOpn);
                JsonObject mWal = new JsonObject(); mWal.addProperty("parent", "spores--shadows:block/moldy_template_fence_gate_wall"); mWal.add("textures", tDef); write(builder, "models/block/" + blockId + "_wall_stage_" + stage, mWal);
                JsonObject mWO = new JsonObject(); mWO.addProperty("parent", "spores--shadows:block/moldy_template_fence_gate_wall_open"); mWO.add("textures", tDef); write(builder, "models/block/" + blockId + "_wall_open_stage_" + stage, mWO);
            }
            String itemParent = stage == 0 ? "minecraft:block/" + prefix + "_fence_gate" : blockId + "_stage_" + stage;
            genItemModel(builder, wood, prefix + "_fence_gate", itemParent, stage, false);

            for (int f = 0; f < facings.length; f++) {
                String facing = facings[f];
                int yBase = yRots[f];
                for (String inWall : new String[]{"false", "true"}) {
                    for (String openState : new String[]{"false", "true"}) {
                        for (String powered : new String[]{"false", "true"}) {
                            String m;
                            if (inWall.equals("true")) {
                                m = openState.equals("true") ? "minecraft:block/" + prefix + "_fence_gate_wall_open" : "minecraft:block/" + prefix + "_fence_gate_wall";
                                if (stage > 0) m = openState.equals("true") ? "spores--shadows:block/" + blockId + "_wall_open_stage_" + stage : "spores--shadows:block/" + blockId + "_wall_stage_" + stage;
                            } else {
                                m = openState.equals("true") ? "minecraft:block/" + prefix + "_fence_gate_open" : "minecraft:block/" + prefix + "_fence_gate";
                                if (stage > 0) m = openState.equals("true") ? "spores--shadows:block/" + blockId + "_open_stage_" + stage : "spores--shadows:block/" + blockId + "_stage_" + stage;
                            }

                            for (String common : getCommonProps()) {
                                JsonObject v = new JsonObject(); v.addProperty("model", m);
                                if (yBase != 0) v.addProperty("y", yBase);
                                v.addProperty("uvlock", true);
                                variants.add("facing=" + facing + ",in_wall=" + inWall + ",open=" + openState + ",powered=" + powered + ",stage=" + stage + "," + common, v);
                            }
                        }
                    }
                }
            }
        }
        JsonObject bs = new JsonObject(); bs.add("variants", variants); write(builder, "blockstates/" + blockId, bs);
    }

    private static void genPressurePlate(ResourcePackBuilder builder, String wood, String prefix) {
        String blockId = "moldy_" + prefix + "_pressure_plate";
        JsonObject variants = new JsonObject();
        
        for (int stage : STAGES) {
            String tex = "minecraft:block/" + prefix + "_planks";
            if (stage > 0) {
                // Normal
                JsonObject mDef = new JsonObject(); mDef.addProperty("parent", "spores--shadows:block/moldy_pressure_plate_up");
                JsonObject tDef = new JsonObject(); tDef.addProperty("texture", tex); tDef.addProperty("overlay", "spores--shadows:block/mold_stage_" + stage);
                mDef.add("textures", tDef);
                write(builder, "models/block/" + blockId + "_stage_" + stage, mDef);
                // Pressed
                JsonObject mPressed = new JsonObject(); mPressed.addProperty("parent", "spores--shadows:block/moldy_pressure_plate_down");
                mPressed.add("textures", tDef);
                write(builder, "models/block/" + blockId + "_down_stage_" + stage, mPressed);
            }
            
            String itemParent = stage == 0 ? "minecraft:block/" + prefix + "_pressure_plate" : blockId + "_stage_" + stage;
            genItemModel(builder, wood, prefix + "_pressure_plate", itemParent, stage, false);
            
            for (String powered : new String[]{"false", "true"}) {
                String m = stage == 0 ? "minecraft:block/" + prefix + "_pressure_plate" + (powered.equals("true") ? "_down" : "") : "spores--shadows:block/" + blockId + (powered.equals("true") ? "_down_" : "_") + "stage_" + stage;
                
                for (String common : getCommonProps()) {
                    JsonObject v = new JsonObject();
                    v.addProperty("model", m);
                    variants.add("powered=" + powered + ",stage=" + stage + "," + common, v);
                }
            }
        }
        JsonObject bs = new JsonObject(); bs.add("variants", variants); write(builder, "blockstates/" + blockId, bs);
    }

    private static void genButton(ResourcePackBuilder builder, String wood, String prefix) {
        String blockId = "moldy_" + prefix + "_button";
        JsonObject variants = new JsonObject();
        String[] facings = {"north", "east", "south", "west"};

        for (int stage : STAGES) {
            String tex = "minecraft:block/" + prefix + "_planks";
            if (stage > 0) {
                // Normal
                JsonObject mDef = new JsonObject(); mDef.addProperty("parent", "spores--shadows:block/moldy_button");
                JsonObject tDef = new JsonObject(); tDef.addProperty("texture", tex); tDef.addProperty("overlay", "spores--shadows:block/mold_stage_" + stage);
                mDef.add("textures", tDef);
                write(builder, "models/block/" + blockId + "_stage_" + stage, mDef);
                
                // Pressed
                JsonObject mPressed = new JsonObject(); mPressed.addProperty("parent", "spores--shadows:block/moldy_button_pressed");
                mPressed.add("textures", tDef);
                write(builder, "models/block/" + blockId + "_pressed_stage_" + stage, mPressed);
                
                // Inventory
                JsonObject mInv = new JsonObject(); mInv.addProperty("parent", "spores--shadows:block/moldy_button_inventory");
                mInv.add("textures", tDef);
                write(builder, "models/block/" + blockId + "_inventory_stage_" + stage, mInv);
            }
            
            String itemParent = stage == 0 ? "minecraft:item/" + prefix + "_button" : blockId + "_inventory_stage_" + stage;
            genItemModel(builder, wood, prefix + "_button", itemParent, stage, false);
            
            for (String face : new String[]{"floor", "wall", "ceiling"}) {
                for (String facing : facings) {
                    for (String powered : new String[]{"false", "true"}) {
                        String m = stage == 0 ? "minecraft:block/" + prefix + "_button" + (powered.equals("true") ? "_pressed" : "") : "spores--shadows:block/" + blockId + (powered.equals("true") ? "_pressed_" : "_") + "stage_" + stage;
                        
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
