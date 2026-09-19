package moldmod.mixin.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.entity.ChestBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ChestBlockEntityRenderer.class)
public class ChestBlockEntityRendererMixin {

    @Inject(
            method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;Lnet/minecraft/client/model/ModelPart;Lnet/minecraft/client/model/ModelPart;Lnet/minecraft/client/model/ModelPart;FII)V",
            at = @At("HEAD")
    )
    private void moldmod$preventSeamZFighting(
            MatrixStack matrices,
            VertexConsumer vertices,
            ModelPart lid,
            ModelPart latch,
            ModelPart base,
            float openProgress,
            int light,
            int overlay,
            CallbackInfo ci
    ) {
        // Vanilla chests define base from Y=0 to 10 and lid pivot at Y=9 to 14.
        // Between Y=9 and 10, both base and lid draw coplanar vertical quads at the black seam strip,
        // causing severe z-fighting/flickering.
        // Scaling base.yScale to 0.9001f when closed stops the base cuboid at Y=9.001,
        // letting the lid exclusively render the black seam with zero coplanar overlapping quads.
        if (openProgress <= 1.0e-4f) {
            base.yScale = 0.9001f;
        } else if (openProgress < 0.1f) {
            base.yScale = 0.9001f + (openProgress / 0.1f) * 0.0999f;
        } else {
            base.yScale = 1.0f;
        }
    }

    @Inject(
            method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;Lnet/minecraft/client/model/ModelPart;Lnet/minecraft/client/model/ModelPart;Lnet/minecraft/client/model/ModelPart;FII)V",
            at = @At("RETURN")
    )
    private void moldmod$restoreBaseScale(
            MatrixStack matrices,
            VertexConsumer vertices,
            ModelPart lid,
            ModelPart latch,
            ModelPart base,
            float openProgress,
            int light,
            int overlay,
            CallbackInfo ci
    ) {
        base.yScale = 1.0f;
    }
}
