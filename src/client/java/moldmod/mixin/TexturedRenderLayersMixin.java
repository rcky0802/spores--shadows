package moldmod.mixin;

import moldmod.SporesShadows;
import moldmod.block.entity.MoldyChestBlockEntity;
import moldmod.block.entity.MoldyTrappedChestBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(TexturedRenderLayers.class)
public class TexturedRenderLayersMixin {

    @Inject(
            method = "getChestTextureId(Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/block/enums/ChestType;Z)Lnet/minecraft/client/util/SpriteIdentifier;",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void moldmod$getMoldyChestTextureId(BlockEntity blockEntity, ChestType type, boolean christmas, CallbackInfoReturnable<SpriteIdentifier> cir) {
        if (blockEntity instanceof MoldyChestBlockEntity moldyChest) {
            int stage = moldyChest.getMoldStage();
            boolean isTrapped = blockEntity instanceof MoldyTrappedChestBlockEntity;
            if (stage > 0 && stage <= 3) {
                String prefix = isTrapped ? "trapped" : "normal";
                String typeSuffix = switch (type) {
                    case LEFT -> "_left";
                    case RIGHT -> "_right";
                    default -> "";
                };
                Identifier id = SporesShadows.id("entity/chest/" + prefix + typeSuffix + "_stage_" + stage);
                cir.setReturnValue(new SpriteIdentifier(TexturedRenderLayers.CHEST_ATLAS_TEXTURE, id));
            } else if (isTrapped) {
                cir.setReturnValue(switch (type) {
                    case LEFT -> TexturedRenderLayers.TRAPPED_LEFT;
                    case RIGHT -> TexturedRenderLayers.TRAPPED_RIGHT;
                    default -> TexturedRenderLayers.TRAPPED;
                });
            }
        }
    }
}
