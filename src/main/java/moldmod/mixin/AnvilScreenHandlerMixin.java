package moldmod.mixin;

import moldmod.item.ModItems;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.Property;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin extends ForgingScreenHandler {

    @Shadow
    private int repairItemUsage;

    @Shadow
    @Final
    private Property levelCost;

    public AnvilScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(type, syncId, playerInventory, context);
    }

    /**
     * Enforces strict anvil rules for the Spore Mask:
     * 1. Only Unbreaking, Mending, and Curse of Vanishing are allowed.
     * 2. Repairing with a Spore Filter restores 100% durability (damage = 0) and consumes exactly 1 filter.
     */
    @Inject(method = "updateResult", at = @At("TAIL"))
    private void handleSporeMaskAnvilRules(CallbackInfo ci) {
        ItemStack leftStack = this.input.getStack(0);
        if (leftStack.isOf(ModItems.SPORE_MASK)) {
            ItemStack outputStack = this.output.getStack(0);
            if (!outputStack.isEmpty()) {
                ItemEnchantmentsComponent enchantments = outputStack.getOrDefault(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT);
                for (RegistryEntry<Enchantment> entry : enchantments.getEnchantments()) {
                    boolean isAllowed = entry.matchesKey(Enchantments.UNBREAKING)
                            || entry.matchesKey(Enchantments.MENDING)
                            || entry.matchesKey(Enchantments.VANISHING_CURSE);
                    if (!isAllowed) {
                        this.output.setStack(0, ItemStack.EMPTY);
                        this.sendContentUpdates();
                        return;
                    }
                }

                ItemStack rightStack = this.input.getStack(1);
                if (rightStack.isOf(ModItems.SPORE_FILTER) && leftStack.isDamaged()) {
                    outputStack.setDamage(0);
                    int previousUsage = this.repairItemUsage;
                    this.repairItemUsage = 1;
                    if (previousUsage > 1) {
                        this.levelCost.set(Math.max(1, this.levelCost.get() - (previousUsage - 1)));
                    } else if (this.levelCost.get() <= 0) {
                        this.levelCost.set(1);
                    }
                    this.sendContentUpdates();
                }
            }
        }
    }
}
