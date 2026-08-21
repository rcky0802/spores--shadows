package moldmod.mixin;

import moldmod.block.ModBlocks;
import moldmod.block.MoldyLogBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Block.class)
public class BlockPickMixin {
    @Inject(method = "getPickStack", at = @At("HEAD"), cancellable = true)
    private void moldmod$getPickStack(WorldView world, BlockPos pos, BlockState state, CallbackInfoReturnable<ItemStack> cir) {
        Block block = (Block) (Object) this;
        if (ModBlocks.MOLDY_ITEMS_BY_BLOCK != null && ModBlocks.MOLDY_ITEMS_BY_BLOCK.containsKey(block)) {
            List<Item> items = ModBlocks.MOLDY_ITEMS_BY_BLOCK.get(block);
            if (items != null && items.size() == 4) {
                int stage = state.contains(MoldyLogBlock.STAGE) ? state.get(MoldyLogBlock.STAGE) : 0;
                boolean isWaxed = state.contains(MoldyLogBlock.WAXED) && state.get(MoldyLogBlock.WAXED);
                
                Item itemToReturn = items.get(stage);
                ItemStack stack = new ItemStack(itemToReturn);
                if (isWaxed) {
                    net.minecraft.component.type.BlockStateComponent comp = stack.getOrDefault(net.minecraft.component.DataComponentTypes.BLOCK_STATE, net.minecraft.component.type.BlockStateComponent.DEFAULT);
                    stack.set(net.minecraft.component.DataComponentTypes.BLOCK_STATE, comp.with(MoldyLogBlock.WAXED, true));
                }
                
                cir.setReturnValue(stack);
            }
        }
    }
}
