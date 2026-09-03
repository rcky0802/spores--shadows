package moldmod.mixin;

import me.shedaniel.autoconfig.AutoConfig;
import moldmod.block.MoldyBlock;
import moldmod.config.ModConfig;
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
        if (blockState.contains(MoldyBlock.STAGE)) {
            ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
            if (!config.blastResistance.enable_blast_resistance_scaling) {
                return;
            }
            int stage = blockState.get(MoldyBlock.STAGE);
            Optional<Float> original = cir.getReturnValue();
            if (original.isPresent()) {
                float res = original.get();
                if (stage == 1) res *= config.blastResistance.stage_1_multiplier;
                else if (stage == 2) res *= config.blastResistance.stage_2_multiplier;
                else if (stage == 3) res *= config.blastResistance.stage_3_multiplier;
                cir.setReturnValue(Optional.of(res));
            }
        }
    }
}
