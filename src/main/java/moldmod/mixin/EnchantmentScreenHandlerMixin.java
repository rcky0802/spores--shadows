package moldmod.mixin;

import moldmod.block.MoldyBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.EnchantingTableBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(EnchantmentScreenHandler.class)
public abstract class EnchantmentScreenHandlerMixin {

    @ModifyVariable(
            method = "method_17411",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/random/Random;setSeed(J)V"),
            ordinal = 0
    )
    private int moldmod$adjustBookshelfPower(int originalPower, ItemStack itemStack, World world, BlockPos pos) {
        float totalPower = 0.0f;
        for (BlockPos offset : EnchantingTableBlock.POWER_PROVIDER_OFFSETS) {
            if (EnchantingTableBlock.canAccessPowerProvider(world, pos, offset)) {
                BlockState state = world.getBlockState(pos.add(offset));
                if (state.contains(MoldyBlock.STAGE) && state.contains(MoldyBlock.WAXED)) {
                    boolean waxed = state.get(MoldyBlock.WAXED);
                    int stage = state.get(MoldyBlock.STAGE);
                    if (waxed || stage == 0) {
                        totalPower += 1.0f;
                    } else if (stage == 1) {
                        totalPower += 0.66f;
                    } else if (stage == 2) {
                        totalPower += 0.33f;
                    } else {
                        totalPower += 0.0f;
                    }
                } else {
                    totalPower += 1.0f;
                }
            }
        }
        return Math.round(totalPower);
    }
}
