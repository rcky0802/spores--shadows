package moldmod.mixin;

import net.minecraft.block.AbstractBlock;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AbstractBlock.AbstractBlockState.class)
public class AbstractBlockStateMixin {
    /*
    @Inject(method = "getHardness", at = @At("RETURN"), cancellable = true)
    private void makeRottenInstant(BlockView world, BlockPos pos, CallbackInfoReturnable<Float> cir) {
        BlockState state = (BlockState) (Object) this;
        if (state.contains(MoldyLogBlock.STAGE) && state.get(MoldyLogBlock.STAGE) == 3) {
            cir.setReturnValue(0.0f);
        }
    }
    */
}
