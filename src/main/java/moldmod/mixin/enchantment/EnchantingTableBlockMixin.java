package moldmod.mixin.enchantment;

import moldmod.block.core.MoldyBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.EnchantingTableBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantingTableBlock.class)
public abstract class EnchantingTableBlockMixin {

    @Inject(method = "canAccessPowerProvider", at = @At("HEAD"), cancellable = true)
    private static void moldmod$disableRottenPowerProvider(World world, BlockPos tablePos, BlockPos providerOffset, CallbackInfoReturnable<Boolean> cir) {
        BlockState state = world.getBlockState(tablePos.add(providerOffset));
        if (state.contains(MoldyBlock.STAGE) && state.contains(MoldyBlock.WAXED)) {
            boolean waxed = state.get(MoldyBlock.WAXED);
            int stage = state.get(MoldyBlock.STAGE);
            if (!waxed && stage == 3) {
                cir.setReturnValue(false);
            }
        }
    }
}
