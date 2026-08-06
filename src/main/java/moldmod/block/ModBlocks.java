package moldmod.block;

import moldmod.SporesShadows;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;

import net.minecraft.block.BlockSetType;
import net.minecraft.block.WoodType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BlockStateComponent;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class ModBlocks {

    public static final Map<Block, Block> VANILLA_TO_MOLDY = new HashMap<>();
    private static final Map<Item, java.util.List<Item>> MOLDY_ITEMS_BY_VANILLA = new java.util.LinkedHashMap<>();

    public static void registerModBlocks() {
        SporesShadows.LOGGER.info("Registering ModBlocks for " + SporesShadows.MOD_ID);

        String[] woods = moldmod.SporesShadows.WOODS;

        for (String wood : woods) {
            registerWoodSet(wood, wood + "_log", wood + "_wood");
        }

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(entries -> {
            for (Map.Entry<Item, java.util.List<Item>> entry : MOLDY_ITEMS_BY_VANILLA.entrySet()) {
                Item vanillaItem = entry.getKey();
                java.util.List<ItemStack> stacksToAdd = new java.util.ArrayList<>();
                for (Item item : entry.getValue()) {
                    // Unwaxed
                    stacksToAdd.add(new ItemStack(item));
                    
                    // Waxed
                    ItemStack waxedStack = new ItemStack(item);
                    BlockStateComponent defaultComponent = waxedStack.getOrDefault(DataComponentTypes.BLOCK_STATE, BlockStateComponent.DEFAULT);
                    waxedStack.set(DataComponentTypes.BLOCK_STATE, defaultComponent.with(MoldyLogBlock.WAXED, true));
                    stacksToAdd.add(waxedStack);
                }
                entries.addAfter(vanillaItem, stacksToAdd);
            }
        });
    }

    private static void registerWoodSet(String prefix, String logName, String woodName) {
        // Vanilla Blocks
        Block vanillaLog = Registries.BLOCK.get(Identifier.of("minecraft", logName));
        Block vanillaStrippedLog = Registries.BLOCK.get(Identifier.of("minecraft", "stripped_" + logName));
        Block vanillaPlanks = Registries.BLOCK.get(Identifier.of("minecraft", prefix + "_planks"));

        // Mod Blocks
        Block strippedLog = registerBlock("moldy_stripped_" + logName, new MoldyLogBlock(AbstractBlock.Settings.copy(vanillaStrippedLog).ticksRandomly(), null));
        Block log = registerBlock("moldy_" + logName, new MoldyLogBlock(AbstractBlock.Settings.copy(vanillaLog).ticksRandomly(), strippedLog));
        Block planks = registerBlock("moldy_" + prefix + "_planks", new MoldyPlanksBlock(AbstractBlock.Settings.copy(vanillaPlanks).ticksRandomly()));
        BlockSetType setType = BlockSetType.OAK;
        WoodType woodType = WoodType.OAK;
        
        Block stairs = registerBlock("moldy_" + prefix + "_stairs", new MoldyStairsBlock(planks.getDefaultState(), AbstractBlock.Settings.copy(vanillaPlanks).ticksRandomly()));
        Block slab = registerBlock("moldy_" + prefix + "_slab", new MoldySlabBlock(AbstractBlock.Settings.copy(vanillaPlanks).ticksRandomly()));
        Block fence = registerBlock("moldy_" + prefix + "_fence", new MoldyFenceBlock(AbstractBlock.Settings.copy(vanillaPlanks).ticksRandomly()));
        Block gate = registerBlock("moldy_" + prefix + "_fence_gate", new MoldyFenceGateBlock(woodType, AbstractBlock.Settings.copy(vanillaPlanks).ticksRandomly()));
        Block door = registerBlock("moldy_" + prefix + "_door", new MoldyDoorBlock(setType, AbstractBlock.Settings.copy(vanillaPlanks).ticksRandomly().nonOpaque()));
        Block trapdoor = registerBlock("moldy_" + prefix + "_trapdoor", new MoldyTrapdoorBlock(setType, AbstractBlock.Settings.copy(vanillaPlanks).ticksRandomly().nonOpaque()));

        Block vanillaStairs = Registries.BLOCK.get(Identifier.of("minecraft", prefix + "_stairs"));
        Block vanillaSlab = Registries.BLOCK.get(Identifier.of("minecraft", prefix + "_slab"));
        Block vanillaFence = Registries.BLOCK.get(Identifier.of("minecraft", prefix + "_fence"));
        Block vanillaGate = Registries.BLOCK.get(Identifier.of("minecraft", prefix + "_fence_gate"));
        Block vanillaDoor = Registries.BLOCK.get(Identifier.of("minecraft", prefix + "_door"));
        Block vanillaTrapdoor = Registries.BLOCK.get(Identifier.of("minecraft", prefix + "_trapdoor"));

        // Map them
        VANILLA_TO_MOLDY.put(vanillaLog, log);
        VANILLA_TO_MOLDY.put(vanillaStrippedLog, strippedLog);
        VANILLA_TO_MOLDY.put(vanillaPlanks, planks);

        // Register Items
        registerStageItems(vanillaLog, logName, log);
        registerStageItems(vanillaStrippedLog, "stripped_" + logName, strippedLog);
        registerStageItems(vanillaPlanks, prefix + "_planks", planks);
        registerStageItems(vanillaStairs, prefix + "_stairs", stairs);
        registerStageItems(vanillaSlab, prefix + "_slab", slab);
        registerStageItems(vanillaFence, prefix + "_fence", fence);
        registerStageItems(vanillaGate, prefix + "_fence_gate", gate);
        registerStageItems(vanillaDoor, prefix + "_door", door);
        registerStageItems(vanillaTrapdoor, prefix + "_trapdoor", trapdoor);
        
        if (woodName != null) {
            Block vanillaWood = Registries.BLOCK.get(Identifier.of("minecraft", woodName));
            Block vanillaStrippedWood = Registries.BLOCK.get(Identifier.of("minecraft", "stripped_" + woodName));
            Block strippedWood = registerBlock("moldy_stripped_" + woodName, new MoldyLogBlock(AbstractBlock.Settings.copy(vanillaStrippedWood).ticksRandomly(), null));
            Block wood = registerBlock("moldy_" + woodName, new MoldyLogBlock(AbstractBlock.Settings.copy(vanillaWood).ticksRandomly(), strippedWood));
            
            VANILLA_TO_MOLDY.put(vanillaWood, wood);
            VANILLA_TO_MOLDY.put(vanillaStrippedWood, strippedWood);
            
            registerStageItems(vanillaWood, woodName, wood);
            registerStageItems(vanillaStrippedWood, "stripped_" + woodName, strippedWood);
        }
    }

    private static Block registerBlock(String name, Block block) {
        return Registry.register(Registries.BLOCK, SporesShadows.id(name), block);
    }

    private static void registerStageItems(Block vanillaBlock, String baseName, Block baseBlock) {
        Item vanillaItem = vanillaBlock.asItem();
        java.util.List<Item> items = new java.util.ArrayList<>();
        items.add(registerStageItem("tainted_" + baseName, baseBlock, 1));
        items.add(registerStageItem("moldy_" + baseName, baseBlock, 2));
        items.add(registerStageItem("rotten_" + baseName, baseBlock, 3));
        MOLDY_ITEMS_BY_VANILLA.put(vanillaItem, items);
    }

    private static Item registerStageItem(String name, Block baseBlock, int stage) {
        Item.Settings settings = new Item.Settings().component(
                DataComponentTypes.BLOCK_STATE,
                BlockStateComponent.DEFAULT.with(MoldyLogBlock.STAGE, stage)
        );
        Item item;
        if (baseBlock instanceof net.minecraft.block.DoorBlock) {
            item = new net.minecraft.item.TallBlockItem(baseBlock, settings) {
                @Override
                public String getTranslationKey() {
                    return "item.spores--shadows." + name;
                }
                @Override
                public void appendTooltip(ItemStack stack, Item.TooltipContext context, java.util.List<net.minecraft.text.Text> tooltip, net.minecraft.item.tooltip.TooltipType type) {
                    super.appendTooltip(stack, context, tooltip, type);
                    if (name.contains("planks")) {
                        tooltip.add(net.minecraft.text.Text.translatable("tooltip.spores--shadows.moldy_planks_desc_1").formatted(net.minecraft.util.Formatting.GRAY));
                        tooltip.add(net.minecraft.text.Text.translatable("tooltip.spores--shadows.moldy_planks_desc_2").formatted(net.minecraft.util.Formatting.GRAY));
                    } else if (name.contains("log") || name.contains("wood")) {
                        tooltip.add(net.minecraft.text.Text.translatable("tooltip.spores--shadows.moldy_log_desc_1").formatted(net.minecraft.util.Formatting.GRAY));
                        tooltip.add(net.minecraft.text.Text.translatable("tooltip.spores--shadows.moldy_log_desc_2").formatted(net.minecraft.util.Formatting.GRAY));
                    } else {
                        tooltip.add(net.minecraft.text.Text.translatable("tooltip.spores--shadows.moldy_general_desc_1").formatted(net.minecraft.util.Formatting.GRAY));
                        tooltip.add(net.minecraft.text.Text.translatable("tooltip.spores--shadows.moldy_general_desc_2").formatted(net.minecraft.util.Formatting.GRAY));
                    }
                    BlockStateComponent comp = stack.get(DataComponentTypes.BLOCK_STATE);
                    if (comp != null && comp.getValue(MoldyLogBlock.WAXED) == Boolean.TRUE) {
                        tooltip.add(net.minecraft.text.Text.translatable("tooltip.spores--shadows.waxed").formatted(net.minecraft.util.Formatting.GOLD));
                    }
                }
            };
        } else {
            item = new BlockItem(baseBlock, settings) {
                @Override
                public String getTranslationKey() {
                    return "item.spores--shadows." + name;
                }
                @Override
                public void appendTooltip(ItemStack stack, Item.TooltipContext context, java.util.List<net.minecraft.text.Text> tooltip, net.minecraft.item.tooltip.TooltipType type) {
                    super.appendTooltip(stack, context, tooltip, type);
                    if (name.contains("planks")) {
                        tooltip.add(net.minecraft.text.Text.translatable("tooltip.spores--shadows.moldy_planks_desc_1").formatted(net.minecraft.util.Formatting.GRAY));
                        tooltip.add(net.minecraft.text.Text.translatable("tooltip.spores--shadows.moldy_planks_desc_2").formatted(net.minecraft.util.Formatting.GRAY));
                    } else if (name.contains("log") || name.contains("wood")) {
                        tooltip.add(net.minecraft.text.Text.translatable("tooltip.spores--shadows.moldy_log_desc_1").formatted(net.minecraft.util.Formatting.GRAY));
                        tooltip.add(net.minecraft.text.Text.translatable("tooltip.spores--shadows.moldy_log_desc_2").formatted(net.minecraft.util.Formatting.GRAY));
                    } else {
                        tooltip.add(net.minecraft.text.Text.translatable("tooltip.spores--shadows.moldy_general_desc_1").formatted(net.minecraft.util.Formatting.GRAY));
                        tooltip.add(net.minecraft.text.Text.translatable("tooltip.spores--shadows.moldy_general_desc_2").formatted(net.minecraft.util.Formatting.GRAY));
                    }
                    BlockStateComponent comp = stack.get(DataComponentTypes.BLOCK_STATE);
                    if (comp != null && comp.getValue(MoldyLogBlock.WAXED) == Boolean.TRUE) {
                        tooltip.add(net.minecraft.text.Text.translatable("tooltip.spores--shadows.waxed").formatted(net.minecraft.util.Formatting.GOLD));
                    }
                }
            };
        }
        return Registry.register(Registries.ITEM, SporesShadows.id(name), item);
    }
}
