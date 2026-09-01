package moldmod.mixin;

import moldmod.item.ModItems;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public abstract class EnchantmentMixin {

    @Shadow
    public abstract boolean isSupportedItem(ItemStack stack);

    /**
     * Restricts Spore Mask enchantments so it behaves similarly to Shears:
     * - Only allows enchantments where Spore Mask is explicitly tagged in supported_items
     *   (i.e. Unbreaking, Mending via #enchantable/durability, and Curse of Vanishing via #enchantable/vanishing).
     * - Completely bypasses primaryItems (which would otherwise allow head armor enchantments like Protection, Respiration, Aqua Affinity, etc.).
     */
    @Inject(method = "isAcceptableItem", at = @At("HEAD"), cancellable = true)
    private void restrictSporeMaskEnchantments(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack.isOf(ModItems.SPORE_MASK)) {
            cir.setReturnValue(this.isSupportedItem(stack));
        }
    }
}
