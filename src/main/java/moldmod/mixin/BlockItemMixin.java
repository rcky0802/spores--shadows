package moldmod.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public class BlockItemMixin {
    @Inject(method = "getPlacementState", at = @At("RETURN"), cancellable = true)
    private void modifyPlacementState(ItemPlacementContext context, CallbackInfoReturnable<BlockState> cir) {
        BlockState original = cir.getReturnValue();
        if (original != null && moldmod.block.ModBlocks.VANILLA_TO_MOLDY.containsKey(original.getBlock())) {
            net.minecraft.block.Block moldyBlock = moldmod.block.ModBlocks.VANILLA_TO_MOLDY.get(original.getBlock());
            BlockState newState = moldyBlock.getPlacementState(context);
            if (newState == null)
                newState = moldyBlock.getDefaultState();

            if (newState.contains(moldmod.block.MoldyLogBlock.STAGE)) {
                newState = newState.with(moldmod.block.MoldyLogBlock.STAGE, 0);
            }
            if (newState.contains(moldmod.block.MoldyLogBlock.WAXED)) {
                newState = newState.with(moldmod.block.MoldyLogBlock.WAXED, false);
            }
            if (newState.contains(moldmod.block.MoldyLogBlock.STRUCTURAL)) {
                newState = newState.with(moldmod.block.MoldyLogBlock.STRUCTURAL, false);
            }
            cir.setReturnValue(newState);
        }
    }
}
