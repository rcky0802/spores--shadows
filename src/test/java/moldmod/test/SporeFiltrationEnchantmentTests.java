package moldmod.test;

import me.shedaniel.autoconfig.AutoConfig;
import moldmod.block.ModBlocks;
import moldmod.block.MoldyLogBlock;
import moldmod.config.ModConfig;
import moldmod.event.ToxicAirEvent;
import moldmod.item.ModItems;
import moldmod.registry.ModEnchantments;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public class SporeFiltrationEnchantmentTests {

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testSporeMaskEnchantabilityRestrictions(TestContext context) {
        ItemStack mask = new ItemStack(ModItems.SPORE_MASK);
        context.assertTrue(mask.getItem().getEnchantability() == 0, 
                "Spore Mask must have enchantability 0 to disable enchanting table!");
        
        context.assertTrue(ModItems.SPORE_MASK.canRepair(mask, new ItemStack(Items.WHITE_WOOL)),
                "Spore Mask must be repairable with wool (#minecraft:wool)");
        context.assertFalse(ModItems.SPORE_MASK.canRepair(mask, new ItemStack(Items.COBBLESTONE)),
                "Spore Mask must NOT be repairable with cobblestone");
        
        var reg = context.getWorld().getRegistryManager().getOptional(RegistryKeys.ENCHANTMENT).orElseThrow();
        
        // Allowed enchantments
        context.assertTrue(reg.getEntry(Enchantments.UNBREAKING).orElseThrow().value().isAcceptableItem(mask),
                "Spore Mask MUST accept Unbreaking!");
        context.assertTrue(reg.getEntry(Enchantments.MENDING).orElseThrow().value().isAcceptableItem(mask),
                "Spore Mask MUST accept Mending!");
        context.assertTrue(reg.getEntry(Enchantments.VANISHING_CURSE).orElseThrow().value().isAcceptableItem(mask),
                "Spore Mask MUST accept Curse of Vanishing!");

        // Rejected enchantments
        context.assertFalse(reg.getEntry(Enchantments.PROTECTION).orElseThrow().value().isAcceptableItem(mask),
                "Spore Mask must NOT accept Protection!");
        context.assertFalse(reg.getEntry(Enchantments.RESPIRATION).orElseThrow().value().isAcceptableItem(mask),
                "Spore Mask must NOT accept Respiration!");
        context.assertFalse(reg.getEntry(Enchantments.AQUA_AFFINITY).orElseThrow().value().isAcceptableItem(mask),
                "Spore Mask must NOT accept Aqua Affinity!");
        context.assertFalse(reg.getEntry(Enchantments.THORNS).orElseThrow().value().isAcceptableItem(mask),
                "Spore Mask must NOT accept Thorns!");
        context.assertFalse(reg.getEntry(ModEnchantments.SPORE_FILTRATION).orElseThrow().value().isAcceptableItem(mask),
                "Spore Mask must NOT accept Spore Filtration!");

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testSporeFiltrationEnchantmentResolution(TestContext context) {
        var enchantmentRegistry = context.getWorld().getRegistryManager().getOptional(RegistryKeys.ENCHANTMENT).orElseThrow();
        var sporeFiltrationOpt = enchantmentRegistry.getEntry(ModEnchantments.SPORE_FILTRATION);
        context.assertTrue(sporeFiltrationOpt.isPresent(), "Spore Filtration enchantment must be present in registry!");

        RegistryEntry<Enchantment> sporeFiltration = sporeFiltrationOpt.get();

        ItemStack diamondHelmet = new ItemStack(Items.DIAMOND_HELMET);
        diamondHelmet.addEnchantment(sporeFiltration, 1);
        context.assertTrue(EnchantmentHelper.getLevel(sporeFiltration, diamondHelmet) == 1, 
                "Diamond helmet must resolve level 1 Spore Filtration");

        ItemStack netheriteHelmet = new ItemStack(Items.NETHERITE_HELMET);
        netheriteHelmet.addEnchantment(sporeFiltration, 3);
        context.assertTrue(EnchantmentHelper.getLevel(sporeFiltration, netheriteHelmet) == 3, 
                "Netherite helmet must resolve level 3 Spore Filtration");

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testDiamondHelmetWithSporeFiltrationProtectsFromPoison(TestContext context) {
        ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        config.toxicity.enable_toxic_air = true;
        config.toxicity.enable_spore_filtration_enchantment = true;
        config.toxicity.filtration_level_1_durability_cost = 2;

        // Build 3x3x3 sealed room
        for (int x = 0; x <= 4; x++) {
            for (int y = 0; y <= 3; y++) {
                for (int z = 0; z <= 4; z++) {
                    if (x == 0 || x == 4 || z == 0 || z == 4 || y == 0 || y == 3) {
                        context.setBlockState(new BlockPos(x, y, z), Blocks.STONE.getDefaultState());
                    } else {
                        context.setBlockState(new BlockPos(x, y, z), Blocks.AIR.getDefaultState());
                    }
                }
            }
        }

        // Add 10 Rotten Oak Logs (Stage 3): Toxic Score = 10 * (3 * 0.75) = 22.5 > threshold_poison (16.0)
        for (int x = 1; x <= 3; x++) {
            for (int y = 1; y <= 2; y++) {
                context.setBlockState(new BlockPos(x, y, 0),
                        ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState().with(MoldyLogBlock.STAGE, 3).with(MoldyLogBlock.WAXED, false));
            }
        }

        net.minecraft.entity.player.PlayerEntity player = context.createMockPlayer(net.minecraft.world.GameMode.SURVIVAL);
        player.clearStatusEffects();
        player.setPos(context.getAbsolutePos(new BlockPos(2, 1, 2)).getX() + 0.5,
                      context.getAbsolutePos(new BlockPos(2, 1, 2)).getY(),
                      context.getAbsolutePos(new BlockPos(2, 1, 2)).getZ() + 0.5);

        var enchantmentRegistry = context.getWorld().getRegistryManager().getOptional(RegistryKeys.ENCHANTMENT).orElseThrow();
        RegistryEntry<Enchantment> sporeFiltration = enchantmentRegistry.getEntry(ModEnchantments.SPORE_FILTRATION).orElseThrow();

        ItemStack diamondHelmet = new ItemStack(Items.DIAMOND_HELMET);
        diamondHelmet.addEnchantment(sporeFiltration, 1);
        player.equipStack(EquipmentSlot.HEAD, diamondHelmet);

        ToxicAirEvent.checkRoomMiasma(player, 8);

        context.assertFalse(player.hasStatusEffect(StatusEffects.POISON), 
                "Player with Spore Filtration helmet should NOT have Poison effect!");
        context.assertFalse(player.hasStatusEffect(StatusEffects.NAUSEA), 
                "Player with Spore Filtration helmet should NOT have Nausea effect!");
        context.assertTrue(player.getEquippedStack(EquipmentSlot.HEAD).getDamage() == 2, 
                "Level 1 Spore Filtration should consume 2 durability points! Got: " + player.getEquippedStack(EquipmentSlot.HEAD).getDamage());

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testLevel2SporeFiltrationConsumesLessDurability(TestContext context) {
        ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        config.toxicity.enable_toxic_air = true;
        config.toxicity.enable_spore_filtration_enchantment = true;
        config.toxicity.filtration_level_2_durability_cost = 1;

        // Build 3x3x3 sealed room
        for (int x = 0; x <= 4; x++) {
            for (int y = 0; y <= 3; y++) {
                for (int z = 0; z <= 4; z++) {
                    if (x == 0 || x == 4 || z == 0 || z == 4 || y == 0 || y == 3) {
                        context.setBlockState(new BlockPos(x, y, z), Blocks.STONE.getDefaultState());
                    } else {
                        context.setBlockState(new BlockPos(x, y, z), Blocks.AIR.getDefaultState());
                    }
                }
            }
        }

        // Add 10 Rotten Oak Logs (Stage 3)
        for (int x = 1; x <= 3; x++) {
            for (int y = 1; y <= 2; y++) {
                context.setBlockState(new BlockPos(x, y, 0),
                        ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState().with(MoldyLogBlock.STAGE, 3).with(MoldyLogBlock.WAXED, false));
            }
        }

        net.minecraft.entity.player.PlayerEntity player = context.createMockPlayer(net.minecraft.world.GameMode.SURVIVAL);
        player.clearStatusEffects();
        player.setPos(context.getAbsolutePos(new BlockPos(2, 1, 2)).getX() + 0.5,
                      context.getAbsolutePos(new BlockPos(2, 1, 2)).getY(),
                      context.getAbsolutePos(new BlockPos(2, 1, 2)).getZ() + 0.5);

        var enchantmentRegistry = context.getWorld().getRegistryManager().getOptional(RegistryKeys.ENCHANTMENT).orElseThrow();
        RegistryEntry<Enchantment> sporeFiltration = enchantmentRegistry.getEntry(ModEnchantments.SPORE_FILTRATION).orElseThrow();

        ItemStack diamondHelmet = new ItemStack(Items.DIAMOND_HELMET);
        diamondHelmet.addEnchantment(sporeFiltration, 2);
        player.equipStack(EquipmentSlot.HEAD, diamondHelmet);

        ToxicAirEvent.checkRoomMiasma(player, 8);

        context.assertFalse(player.hasStatusEffect(StatusEffects.POISON), 
                "Player with Spore Filtration II helmet should NOT have Poison effect!");
        context.assertTrue(player.getEquippedStack(EquipmentSlot.HEAD).getDamage() == 1, 
                "Level 2 Spore Filtration should consume 1 durability point! Got: " + player.getEquippedStack(EquipmentSlot.HEAD).getDamage());

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testUnenchantedHelmetDoesNotProtect(TestContext context) {
        ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        config.toxicity.enable_toxic_air = true;

        // Build 3x3x3 sealed room
        for (int x = 0; x <= 4; x++) {
            for (int y = 0; y <= 3; y++) {
                for (int z = 0; z <= 4; z++) {
                    if (x == 0 || x == 4 || z == 0 || z == 4 || y == 0 || y == 3) {
                        context.setBlockState(new BlockPos(x, y, z), Blocks.STONE.getDefaultState());
                    } else {
                        context.setBlockState(new BlockPos(x, y, z), Blocks.AIR.getDefaultState());
                    }
                }
            }
        }

        // Add 10 Rotten Oak Logs (Stage 3)
        for (int x = 1; x <= 3; x++) {
            for (int y = 1; y <= 2; y++) {
                context.setBlockState(new BlockPos(x, y, 0),
                        ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState().with(MoldyLogBlock.STAGE, 3).with(MoldyLogBlock.WAXED, false));
            }
        }

        net.minecraft.entity.player.PlayerEntity player = context.createMockPlayer(net.minecraft.world.GameMode.SURVIVAL);
        player.clearStatusEffects();
        player.setPos(context.getAbsolutePos(new BlockPos(2, 1, 2)).getX() + 0.5,
                      context.getAbsolutePos(new BlockPos(2, 1, 2)).getY(),
                      context.getAbsolutePos(new BlockPos(2, 1, 2)).getZ() + 0.5);

        // Unenchanted iron helmet
        ItemStack ironHelmet = new ItemStack(Items.IRON_HELMET);
        player.equipStack(EquipmentSlot.HEAD, ironHelmet);

        ToxicAirEvent.checkRoomMiasma(player, 8);

        context.assertTrue(player.hasStatusEffect(StatusEffects.POISON), 
                "Player with unenchanted helmet MUST suffer Poison in lethal miasma!");
        context.assertTrue(player.getEquippedStack(EquipmentSlot.HEAD).getDamage() == 0, 
                "Unenchanted helmet should not take durability from miasma filtration!");

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testCoexistenceWithRespirationEnchantment(TestContext context) {
        var enchantmentRegistry = context.getWorld().getRegistryManager().getOptional(RegistryKeys.ENCHANTMENT).orElseThrow();
        RegistryEntry<Enchantment> sporeFiltration = enchantmentRegistry.getEntry(ModEnchantments.SPORE_FILTRATION).orElseThrow();
        RegistryEntry<Enchantment> respiration = enchantmentRegistry.getEntry(Enchantments.RESPIRATION).orElseThrow();

        ItemStack diamondHelmet = new ItemStack(Items.DIAMOND_HELMET);
        diamondHelmet.addEnchantment(sporeFiltration, 2);
        diamondHelmet.addEnchantment(respiration, 3);

        context.assertTrue(EnchantmentHelper.getLevel(sporeFiltration, diamondHelmet) == 2, 
                "Should have Spore Filtration 2");
        context.assertTrue(EnchantmentHelper.getLevel(respiration, diamondHelmet) == 3, 
                "Should have Respiration 3 alongside Spore Filtration");

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testAnvilScreenHandlerSporeMaskEnchantmentFiltering(TestContext context) {
        var player = context.createMockPlayer(net.minecraft.world.GameMode.SURVIVAL);
        var reg = context.getWorld().getRegistryManager().getOptional(RegistryKeys.ENCHANTMENT).orElseThrow();
        
        var unbreaking = reg.getEntry(Enchantments.UNBREAKING).orElseThrow();
        var protection = reg.getEntry(Enchantments.PROTECTION).orElseThrow();
        var filtration = reg.getEntry(ModEnchantments.SPORE_FILTRATION).orElseThrow();

        // 1. Rejected: Protection Book
        net.minecraft.screen.AnvilScreenHandler anvil = new net.minecraft.screen.AnvilScreenHandler(0, player.getInventory(), net.minecraft.screen.ScreenHandlerContext.EMPTY);
        anvil.getSlot(0).setStack(new ItemStack(ModItems.SPORE_MASK));
        anvil.getSlot(1).setStack(net.minecraft.item.EnchantedBookItem.forEnchantment(new net.minecraft.enchantment.EnchantmentLevelEntry(protection, 4)));
        anvil.updateResult();
        context.assertTrue(anvil.getSlot(2).getStack().isEmpty(), "Anvil MUST reject Protection IV on Spore Mask!");

        // 2. Rejected: Spore Filtration Book
        anvil = new net.minecraft.screen.AnvilScreenHandler(0, player.getInventory(), net.minecraft.screen.ScreenHandlerContext.EMPTY);
        anvil.getSlot(0).setStack(new ItemStack(ModItems.SPORE_MASK));
        anvil.getSlot(1).setStack(net.minecraft.item.EnchantedBookItem.forEnchantment(new net.minecraft.enchantment.EnchantmentLevelEntry(filtration, 3)));
        anvil.updateResult();
        context.assertTrue(anvil.getSlot(2).getStack().isEmpty(), "Anvil MUST reject Spore Filtration on Spore Mask!");

        // 3. Accepted: Unbreaking Book
        anvil = new net.minecraft.screen.AnvilScreenHandler(0, player.getInventory(), net.minecraft.screen.ScreenHandlerContext.EMPTY);
        anvil.getSlot(0).setStack(new ItemStack(ModItems.SPORE_MASK));
        anvil.getSlot(1).setStack(net.minecraft.item.EnchantedBookItem.forEnchantment(new net.minecraft.enchantment.EnchantmentLevelEntry(unbreaking, 3)));
        anvil.updateResult();
        context.assertFalse(anvil.getSlot(2).getStack().isEmpty(), "Anvil MUST accept Unbreaking III on Spore Mask!");
        context.assertTrue(EnchantmentHelper.getLevel(unbreaking, anvil.getSlot(2).getStack()) == 3, "Output must have Unbreaking III!");

        // 4. Accepted: Wool Repair
        anvil = new net.minecraft.screen.AnvilScreenHandler(0, player.getInventory(), net.minecraft.screen.ScreenHandlerContext.EMPTY);
        ItemStack damaged = new ItemStack(ModItems.SPORE_MASK);
        damaged.setDamage(100);
        anvil.getSlot(0).setStack(damaged);
        anvil.getSlot(1).setStack(new ItemStack(Items.WHITE_WOOL));
        anvil.updateResult();
        context.assertFalse(anvil.getSlot(2).getStack().isEmpty(), "Anvil MUST accept Wool repair on Spore Mask!");

        context.complete();
    }
}
