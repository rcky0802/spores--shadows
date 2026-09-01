package moldmod.integration.jade;

import moldmod.item.ModItems;
import moldmod.registry.ModEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum SporeProtectionEntityProvider implements IEntityComponentProvider {
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
        if (accessor.getEntity() instanceof LivingEntity living) {
            ItemStack headStack = living.getEquippedStack(EquipmentSlot.HEAD);
            if (!headStack.isEmpty()) {
                if (headStack.isOf(ModItems.SPORE_MASK)) {
                    tooltip.add(Text.translatable("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.spore_protection_mask").formatted(Formatting.AQUA));
                } else {
                    var regOpt = accessor.getLevel().getRegistryManager().getOptional(RegistryKeys.ENCHANTMENT);
                    if (regOpt.isPresent()) {
                        var entryOpt = regOpt.get().getEntry(ModEnchantments.SPORE_FILTRATION);
                        if (entryOpt.isPresent()) {
                            int level = EnchantmentHelper.getLevel(entryOpt.get(), headStack);
                            if (level > 0) {
                                tooltip.add(Text.translatable("tooltip." + moldmod.SporesShadows.MOD_ID + ".jade.spore_protection_enchant", level).formatted(Formatting.AQUA));
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public Identifier getUid() {
        return Identifier.of(moldmod.SporesShadows.MOD_ID, "spore_protection_info");
    }
}
