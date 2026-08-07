package moldmod.mixin;

import moldmod.block.MoldyLogBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.FireBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FireBlock.class)
public class FireBlockMixin {

    @Inject(method = "getBurnChance(Lnet/minecraft/block/BlockState;)I", at = @At("RETURN"), cancellable = true)
    private void increaseMoldyBurnChance(BlockState state, CallbackInfoReturnable<Integer> cir) {
        if (state.contains(MoldyLogBlock.STAGE)) {
            int stage = state.get(MoldyLogBlock.STAGE);
            if (stage > 0) {
                // Vanilla wood is usually 5 burn chance. Moldy is drier/more flammable.
                int original = cir.getReturnValue();
                // If it wasn't flammable at all, we don't make it flammable, unless we want to?
                // Wood is 5. Let's add 15 per stage.
                if (original > 0) {
                    cir.setReturnValue(original + (stage * 15));
                }
            }
        }
    }

    @Inject(method = "getSpreadChance(Lnet/minecraft/block/BlockState;)I", at = @At("RETURN"), cancellable = true)
    private void increaseMoldySpreadChance(BlockState state, CallbackInfoReturnable<Integer> cir) {
        if (state.contains(MoldyLogBlock.STAGE)) {
            int stage = state.get(MoldyLogBlock.STAGE);
            if (stage > 0) {
                // Vanilla wood is usually 5 spread chance.
                int original = cir.getReturnValue();
                if (original > 0) {
                    cir.setReturnValue(original + (stage * 15));
                }
            }
        }
    }
}
