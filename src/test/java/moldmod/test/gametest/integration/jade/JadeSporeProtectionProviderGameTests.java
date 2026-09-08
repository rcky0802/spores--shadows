package moldmod.test.gametest.integration.jade;

import moldmod.SporesShadows;
import moldmod.integration.jade.SporeProtectionEntityProvider;
import moldmod.item.ModItems;
import moldmod.registry.ModEnchantments;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.Identifier;

public class JadeSporeProtectionProviderGameTests {

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testSporeProtectionProviderUid(TestContext context) {
        Identifier uid = SporeProtectionEntityProvider.INSTANCE.getUid();
        context.assertTrue(uid.getNamespace().equals(SporesShadows.MOD_ID),
                "Provider namespace must be " + SporesShadows.MOD_ID + ", got: " + uid.getNamespace());
        context.assertTrue(uid.getPath().equals("spore_protection_info"),
                "Provider path must be 'spore_protection_info', got: " + uid.getPath());
        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testPlayerSporeMaskEquipment(TestContext context) {
        PlayerEntity player = context.createMockPlayer(net.minecraft.world.GameMode.SURVIVAL);

        // Initially no helmet
        context.assertTrue(player.getEquippedStack(EquipmentSlot.HEAD).isEmpty(), "Player head should be empty initially");

        // Equip Spore Mask
        ItemStack mask = new ItemStack(ModItems.SPORE_MASK);
        player.equipStack(EquipmentSlot.HEAD, mask);

        ItemStack equipped = player.getEquippedStack(EquipmentSlot.HEAD);
        context.assertTrue(equipped.isOf(ModItems.SPORE_MASK), "Equipped item must be Spore Mask");

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testPlayerSporeFiltrationHelmetEquipment(TestContext context) {
        PlayerEntity player = context.createMockPlayer(net.minecraft.world.GameMode.SURVIVAL);

        var regOpt = context.getWorld().getRegistryManager().getOptional(RegistryKeys.ENCHANTMENT);
        context.assertTrue(regOpt.isPresent(), "Enchantment registry must be present");

        var entryOpt = regOpt.get().getEntry(ModEnchantments.SPORE_FILTRATION);
        context.assertTrue(entryOpt.isPresent(), "Spore Filtration enchantment entry must be present");

        ItemStack helm = new ItemStack(Items.DIAMOND_HELMET);
        helm.addEnchantment(entryOpt.get(), 2);
        player.equipStack(EquipmentSlot.HEAD, helm);

        int level = EnchantmentHelper.getLevel(entryOpt.get(), player.getEquippedStack(EquipmentSlot.HEAD));
        context.assertTrue(level == 2, "Spore filtration level must be 2, got: " + level);

        context.complete();
    }
}
