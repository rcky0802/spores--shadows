package moldmod.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.block.PillarBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public class ExampleMixin {
    @Inject(method = "getPlacementState", at = @At("RETURN"), cancellable = true)
    private void modifyPlacementState(ItemPlacementContext context, CallbackInfoReturnable<BlockState> cir) {
        BlockState original = cir.getReturnValue();
        if (original != null && moldmod.block.ModBlocks.VANILLA_TO_MOLDY.containsKey(original.getBlock())) {
            net.minecraft.block.Block moldyBlock = moldmod.block.ModBlocks.VANILLA_TO_MOLDY.get(original.getBlock());
            BlockState newState = moldyBlock.getDefaultState();
            
            if (original.contains(PillarBlock.AXIS) && newState.contains(PillarBlock.AXIS)) {
                newState = newState.with(PillarBlock.AXIS, original.get(PillarBlock.AXIS));
            }
            // By default placed by player means STAGE=0, WAXED=false, STRUCTURAL=false
            cir.setReturnValue(newState);
        }
    }
}