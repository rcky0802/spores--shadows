package moldmod.registry;

import moldmod.SporesShadows;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModEnchantments {
    public static final RegistryKey<Enchantment> SPORE_FILTRATION = RegistryKey.of(
            RegistryKeys.ENCHANTMENT,
            Identifier.of(SporesShadows.MOD_ID, "spore_filtration")
    );

    public static void register() {
        // Enchantments in 1.21 are data-driven via RegistryKeys.ENCHANTMENT
    }
}
