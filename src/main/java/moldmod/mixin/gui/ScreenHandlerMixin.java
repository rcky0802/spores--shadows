package moldmod.mixin.gui;

import moldmod.screen.MoldStageHolder;
import net.minecraft.screen.Property;
import net.minecraft.screen.ScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenHandler.class)
public abstract class ScreenHandlerMixin implements MoldStageHolder {

    @Shadow
    protected abstract Property addProperty(Property property);

    @Unique
    private final Property spores_shadows$moldStage = Property.create();

    @Inject(method = "<init>", at = @At("RETURN"))
    private void moldmod$initProperty(CallbackInfo ci) {
        this.addProperty(this.spores_shadows$moldStage);
    }

    @Override
    public int spores_shadows$getMoldStage() {
        return this.spores_shadows$moldStage.get();
    }

    @Override
    public void spores_shadows$setMoldStage(int stage) {
        this.spores_shadows$moldStage.set(stage);
    }

    @Inject(
            method = "canUse(Lnet/minecraft/screen/ScreenHandlerContext;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/block/Block;)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void moldmod$canUseCustomWorkstations(
            net.minecraft.screen.ScreenHandlerContext context,
            net.minecraft.entity.player.PlayerEntity player,
            net.minecraft.block.Block block,
            org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable<Boolean> cir
    ) {
        Boolean allowed = context.get((world, pos) -> {
            net.minecraft.block.BlockState state = world.getBlockState(pos);
            net.minecraft.block.Block currentBlock = state.getBlock();
            if (currentBlock instanceof moldmod.block.core.MoldyBlock) {
                net.minecraft.block.Block moldyTarget = moldmod.block.ModBlocks.VANILLA_TO_MOLDY.get(block);
                net.minecraft.block.Block waxedTarget = moldyTarget != null ? moldmod.block.ModBlocks.MOLDY_TO_WAXED.get(moldyTarget) : null;
                if (currentBlock == moldyTarget || currentBlock == waxedTarget) {
                    return player.squaredDistanceTo((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5) <= 64.0;
                }
            }
            return null;
        }).orElse(null);

        if (allowed != null) {
            cir.setReturnValue(allowed);
        }
    }
}
