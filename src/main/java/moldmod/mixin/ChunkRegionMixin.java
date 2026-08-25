package moldmod.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkRegion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import moldmod.structure.MoldyStructureContext;

@Mixin(ChunkRegion.class)
public abstract class ChunkRegionMixin {

    @ModifyVariable(method = "setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;II)Z", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private BlockState modifyStructureBlock(BlockState state, BlockPos pos) {
        return MoldyStructureContext.processBlock(state, pos, (ChunkRegion)(Object)this);
    }
}
