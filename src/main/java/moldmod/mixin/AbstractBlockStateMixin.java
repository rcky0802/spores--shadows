package moldmod.mixin;

import moldmod.block.MoldyLogBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.AbstractBlockState.class)
public class AbstractBlockStateMixin {
    @Inject(method = "calcBlockBreakingDelta", at = @At("RETURN"), cancellable = true)
    private void slowDownRottenMining(PlayerEntity player, BlockView world, BlockPos pos, CallbackInfoReturnable<Float> cir) {
        BlockState state = (BlockState) (Object) this;
        if (state.contains(MoldyLogBlock.STAGE) && state.get(MoldyLogBlock.STAGE) == 3) {
            float hardness = state.getBlock().getHardness();
            if (hardness != -1.0f) {
                // Ignore player tools, assume bare hands
                int i = player.canHarvest(state) ? 30 : 100;
                float bareHandDelta = 1.0f / hardness / (float)i;
                cir.setReturnValue(bareHandDelta);
            }
        }
    }
}
