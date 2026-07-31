package moldmod.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.block.PillarBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import moldmod.block.ModBlocks;

@Mixin(BlockItem.class)
public class ExampleMixin {
    @Inject(method = "getPlacementState", at = @At("RETURN"), cancellable = true)
    private void modifyOakLogPlacement(ItemPlacementContext context, CallbackInfoReturnable<BlockState> cir) {
        BlockState original = cir.getReturnValue();
        // Se il giocatore sta piazzando un blocco di quercia vanilla, lo sostituiamo col nostro custom che ammuffisce
        if (original != null && original.isOf(Blocks.OAK_LOG)) {
            BlockState newState = ModBlocks.PLACED_OAK_LOG.getDefaultState().with(PillarBlock.AXIS, original.get(PillarBlock.AXIS));
            cir.setReturnValue(newState);
        }
    }
}