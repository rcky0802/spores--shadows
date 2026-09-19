package moldmod.mixin.client.render;

import moldmod.client.render.MoldySignRenderHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.WoodType;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.SignBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(SignBlockEntityRenderer.class)
public abstract class SignBlockEntityRendererMixin {

    @Shadow
    abstract Vec3d getTextOffset();

    @Shadow
    public abstract float getTextScale();

    @Inject(
            method = "render(Lnet/minecraft/block/entity/SignBlockEntity;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/block/BlockState;Lnet/minecraft/block/AbstractSignBlock;Lnet/minecraft/block/WoodType;Lnet/minecraft/client/model/Model;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/util/math/MatrixStack;pop()V",
                    shift = At.Shift.BEFORE
            )
    )
    private void moldmod$renderMoldOverlay(
            SignBlockEntity entity,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            int overlay,
            BlockState state,
            AbstractSignBlock block,
            WoodType woodType,
            Model model,
            CallbackInfo ci
    ) {
        MoldySignRenderHelper.renderMoldOverlay(
                entity,
                matrices,
                vertexConsumers,
                light,
                overlay,
                state,
                block,
                this.getTextOffset(),
                this.getTextScale()
        );
    }
}
