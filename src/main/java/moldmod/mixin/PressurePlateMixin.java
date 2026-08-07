package moldmod.mixin;

import moldmod.block.MoldyPressurePlateBlock;
import net.minecraft.block.AbstractPressurePlateBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import net.minecraft.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractPressurePlateBlock.class)
public class PressurePlateMixin {

    @Redirect(
        method = "*",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldAccess;scheduleBlockTick(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;I)V")
    )
    private void redirectScheduleTick(WorldAccess world, BlockPos pos, Block block, int delay) {
        if (block instanceof MoldyPressurePlateBlock mppb) {
            BlockState state = world.getBlockState(pos);
            delay = mppb.getMoldyPressTicks(state);
        }
        world.scheduleBlockTick(pos, block, delay);
    }
}
