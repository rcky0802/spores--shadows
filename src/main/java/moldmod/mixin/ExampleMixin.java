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
        if (original != null) {
            if (original.isOf(Blocks.OAK_LOG)) {
                BlockState newState = ModBlocks.MOLDY_OAK_LOG.getDefaultState().with(PillarBlock.AXIS, original.get(PillarBlock.AXIS));
                cir.setReturnValue(newState);
            } else if (original.isOf(Blocks.STRIPPED_OAK_LOG)) {
                BlockState newState = ModBlocks.MOLDY_STRIPPED_OAK_LOG.getDefaultState().with(PillarBlock.AXIS, original.get(PillarBlock.AXIS));
                cir.setReturnValue(newState);
            } else if (original.isOf(Blocks.OAK_WOOD)) {
                BlockState newState = ModBlocks.MOLDY_OAK_WOOD.getDefaultState().with(PillarBlock.AXIS, original.get(PillarBlock.AXIS));
                cir.setReturnValue(newState);
            } else if (original.isOf(Blocks.STRIPPED_OAK_WOOD)) {
                BlockState newState = ModBlocks.MOLDY_STRIPPED_OAK_WOOD.getDefaultState().with(PillarBlock.AXIS, original.get(PillarBlock.AXIS));
                cir.setReturnValue(newState);
            } else if (original.isOf(Blocks.OAK_PLANKS)) {
                BlockState newState = ModBlocks.MOLDY_OAK_PLANKS.getDefaultState();
                cir.setReturnValue(newState);
            }
        }
    }
}