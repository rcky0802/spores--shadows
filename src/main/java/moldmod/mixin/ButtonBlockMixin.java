package moldmod.mixin;

import moldmod.block.MoldyButtonBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ButtonBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import net.minecraft.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ButtonBlock.class)
public class ButtonBlockMixin {

    @Redirect(
        method = "*",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldAccess;scheduleBlockTick(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;I)V")
    )
    private void redirectScheduleTick(WorldAccess world, BlockPos pos, Block block, int delay) {
        if (block instanceof MoldyButtonBlock mbb) {
            BlockState state = world.getBlockState(pos);
            delay = mbb.getMoldyPressTicks(state);
        }
        world.scheduleBlockTick(pos, block, delay);
    }
}
