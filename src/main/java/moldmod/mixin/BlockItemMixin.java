package moldmod.mixin;

import moldmod.block.ModBlocks;
import moldmod.block.MoldyBlock;
import net.minecraft.block.Block;
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
        if (original != null && ModBlocks.VANILLA_TO_MOLDY.containsKey(original.getBlock())) {
            Block moldyBlock = ModBlocks.VANILLA_TO_MOLDY.get(original.getBlock());
            BlockState newState = moldyBlock.getPlacementState(context);
            if (newState == null)
                newState = moldyBlock.getDefaultState();

            if (newState.contains(MoldyBlock.STAGE)) {
                newState = newState.with(MoldyBlock.STAGE, 0);
            }
            if (newState.contains(MoldyBlock.WAXED)) {
                newState = newState.with(MoldyBlock.WAXED, false);
            }
            if (newState.contains(MoldyBlock.STRUCTURAL)) {
                newState = newState.with(MoldyBlock.STRUCTURAL, false);
            }
            cir.setReturnValue(newState);
        }
    }
}
