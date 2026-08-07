package moldmod.mixin;

import moldmod.block.MoldyLogBlock;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(ExplosionBehavior.class)
public class ExplosionBehaviorMixin {

    @Inject(method = "getBlastResistance", at = @At("RETURN"), cancellable = true)
    private void modifyMoldyBlastResistance(Explosion explosion, BlockView world, BlockPos pos, BlockState blockState, FluidState fluidState, CallbackInfoReturnable<Optional<Float>> cir) {
        if (blockState.contains(MoldyLogBlock.STAGE)) {
            int stage = blockState.get(MoldyLogBlock.STAGE);
            Optional<Float> original = cir.getReturnValue();
            if (original.isPresent()) {
                float res = original.get();
                // Reduce resistance: Stage 1 = 80%, Stage 2 = 50%, Stage 3 = 10%
                if (stage == 1) res *= 0.8f;
                else if (stage == 2) res *= 0.5f;
                else if (stage == 3) res = 0.1f; // Instantly destroyed
                cir.setReturnValue(Optional.of(res));
            }
        }
    }
}
