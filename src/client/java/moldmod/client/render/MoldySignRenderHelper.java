package moldmod.client.render;

import moldmod.SporesShadows;
import moldmod.block.MoldyBlock;
import moldmod.block.entity.MoldySignBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.HangingSignBlock;
import net.minecraft.block.WallHangingSignBlock;
import net.minecraft.block.entity.HangingSignBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public final class MoldySignRenderHelper {

    public static final Identifier[] MOLD_TEXTURES = new Identifier[] {
            null,
            SporesShadows.id("textures/block/mold/mold_stage_1.png"),
            SporesShadows.id("textures/block/mold/mold_stage_2.png"),
            SporesShadows.id("textures/block/mold/mold_stage_3.png")
    };

    // Standard Sign board bounds in text coordinate space
    public static final float SIGN_MIN_X = -48.0f;
    public static final float SIGN_MAX_X = 48.0f;
    public static final float SIGN_MIN_Y = -24.0f;
    public static final float SIGN_MAX_Y = 24.0f;

    // Hanging Sign board bounds in text coordinate space
    public static final float HANGING_MIN_X = -31.0f;
    public static final float HANGING_MAX_X = 31.0f;
    public static final float HANGING_MIN_Y = -22.5f;
    public static final float HANGING_MAX_Y = 21.5f;

    // Offset in front of the text plane (Z + 0.005f in block space ~= 0.5f in text space)
    public static final float TEXT_OVERLAY_Z_OFFSET = 0.5f;

    public static final Vec3d DEFAULT_SIGN_OFFSET = new Vec3d(0.0, 0.3333333432674408, 0.046666666865348816);
    public static final float DEFAULT_SIGN_SCALE = 0.6666667f;
    public static final Vec3d DEFAULT_HANGING_OFFSET = new Vec3d(0.0, -0.3199999928474426, 0.0729999989271164);
    public static final float DEFAULT_HANGING_SCALE = 0.9f;

    private MoldySignRenderHelper() {}

    public static void renderMoldOverlay(
            SignBlockEntity entity,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            int overlay,
            BlockState state,
            AbstractSignBlock block,
            Vec3d textOffset,
            float textScale
    ) {
        int stage = getMoldStage(entity, state);
        if (stage <= 0 || stage >= MOLD_TEXTURES.length) {
            return;
        }

        Identifier texture = MOLD_TEXTURES[stage];
        if (texture == null) {
            return;
        }

        boolean isHanging = isHangingSign(entity, block);
        Vec3d offset = textOffset != null ? textOffset : (isHanging ? DEFAULT_HANGING_OFFSET : DEFAULT_SIGN_OFFSET);
        float scaleVal = textScale > 0.0f ? textScale : (isHanging ? DEFAULT_HANGING_SCALE : DEFAULT_SIGN_SCALE);

        // Render Front Face Overlay (over front text)
        renderFaceOverlay(matrices, vertexConsumers, light, overlay, true, isHanging, offset, scaleVal, texture);

        // Render Back Face Overlay (over back text)
        renderFaceOverlay(matrices, vertexConsumers, light, overlay, false, isHanging, offset, scaleVal, texture);
    }

    public static int getMoldStage(SignBlockEntity entity, BlockState state) {
        if (entity instanceof MoldySignBlockEntity moldySign) {
            return moldySign.getMoldStage();
        }
        if (state != null && state.contains(MoldyBlock.STAGE)) {
            return state.get(MoldyBlock.STAGE);
        }
        return 0;
    }

    public static boolean isHangingSign(SignBlockEntity entity, AbstractSignBlock block) {
        return (entity instanceof HangingSignBlockEntity)
                || (block instanceof HangingSignBlock)
                || (block instanceof WallHangingSignBlock);
    }

    private static void renderFaceOverlay(
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            int overlay,
            boolean front,
            boolean isHanging,
            Vec3d textOffset,
            float textScale,
            Identifier texture
    ) {
        matrices.push();

        if (!front) {
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f));
        }

        float scale = 0.015625f * textScale;
        matrices.translate(textOffset.x, textOffset.y, textOffset.z);
        matrices.scale(scale, -scale, scale);

        float minX = isHanging ? HANGING_MIN_X : SIGN_MIN_X;
        float maxX = isHanging ? HANGING_MAX_X : SIGN_MAX_X;
        float minY = isHanging ? HANGING_MIN_Y : SIGN_MIN_Y;
        float maxY = isHanging ? HANGING_MAX_Y : SIGN_MAX_Y;
        float z = TEXT_OVERLAY_Z_OFFSET;

        VertexConsumer consumer = vertexConsumers.getBuffer(RenderLayer.getTextPolygonOffset(texture));
        MatrixStack.Entry entry = matrices.peek();
        Matrix4f positionMatrix = entry.getPositionMatrix();

        // 4 vertices (counter-clockwise quad viewed from the active face):
        // 1. Top-Left
        consumer.vertex(positionMatrix, minX, minY, z)
                .color(255, 255, 255, 255)
                .texture(0.0f, 0.0f)
                .light(light);

        // 2. Bottom-Left
        consumer.vertex(positionMatrix, minX, maxY, z)
                .color(255, 255, 255, 255)
                .texture(0.0f, 1.0f)
                .light(light);

        // 3. Bottom-Right
        consumer.vertex(positionMatrix, maxX, maxY, z)
                .color(255, 255, 255, 255)
                .texture(1.0f, 1.0f)
                .light(light);

        // 4. Top-Right
        consumer.vertex(positionMatrix, maxX, minY, z)
                .color(255, 255, 255, 255)
                .texture(1.0f, 0.0f)
                .light(light);

        matrices.pop();
    }
}
