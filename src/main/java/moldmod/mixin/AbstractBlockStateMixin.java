package moldmod.mixin;

import net.minecraft.block.AbstractBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(AbstractBlock.AbstractBlockState.class)
public class AbstractBlockStateMixin {
    @Inject(method = "calcBlockBreakingDelta", at = @At("HEAD"), cancellable = true)
    private void ignoreToolEfficiencyOnStage3(net.minecraft.entity.player.PlayerEntity player, net.minecraft.world.BlockView world, net.minecraft.util.math.BlockPos pos, org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable<Float> cir) {
        net.minecraft.block.BlockState state = (net.minecraft.block.BlockState) (Object) this;
        if (state.contains(moldmod.block.MoldyLogBlock.STAGE) && state.get(moldmod.block.MoldyLogBlock.STAGE) == 3) {
            float hardness = state.getHardness(world, pos);
            if (hardness == -1.0f) {
                cir.setReturnValue(0.0f);
                return;
            }
            int i = player.canHarvest(state) ? 30 : 100;
            // Force tool multiplier to 1.0f (same as hand)
            cir.setReturnValue((float)(player.getAttributes().getValue(net.minecraft.entity.attribute.EntityAttributes.PLAYER_BLOCK_BREAK_SPEED)) / hardness / (float)i);
        }
    }
}
