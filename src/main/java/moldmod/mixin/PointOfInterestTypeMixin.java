package moldmod.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.world.poi.PointOfInterestType;
import net.minecraft.world.poi.PointOfInterestTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PointOfInterestType.class)
public class PointOfInterestTypeMixin {
    @Inject(method = "contains", at = @At("HEAD"), cancellable = true)
    private void moldmod$containsCustomState(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        PointOfInterestTypes.getTypeForState(state).ifPresent(entry -> {
            if (entry.value() == (Object) this) {
                cir.setReturnValue(true);
            }
        });
    }
}
