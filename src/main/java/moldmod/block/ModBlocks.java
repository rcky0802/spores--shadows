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
    public static final Map<Item, java.util.List<Item>> MOLDY_ITEMS_BY_VANILLA = new java.util.LinkedHashMap<>();
    public static final Map<Block, java.util.List<Item>> MOLDY_ITEMS_BY_BLOCK = new HashMap<>();

    public static void registerModBlocks() {
        SporesShadows.LOGGER.info("Registering ModBlocks for " + SporesShadows.MOD_ID);

        String[] woods = moldmod.SporesShadows.WOODS;

        for (String wood : woods) {
            if (wood.equals("crimson") || wood.equals("warped")) {
                registerWoodSet(wood, wood + "_stem", wood + "_hyphae");
            } else if (wood.equals("bamboo")) {
                registerWoodSet(wood, wood + "_block", null);
            } else {
                registerWoodSet(wood, wood + "_log", wood + "_wood");
            }
        }

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(entries -> {
            for (Map.Entry<Item, java.util.List<Item>> entry : MOLDY_ITEMS_BY_VANILLA.entrySet()) {
                Item vanillaItem = entry.getKey();
                java.util.List<ItemStack> stacksToAdd = new java.util.ArrayList<>();
                for (Item item : entry.getValue()) {
                    // Unwaxed/Default
                    stacksToAdd.add(new ItemStack(item));
                    
                    // Add Waxed variant only if the item is not already the waxed stage 0 item
                    if (!Registries.ITEM.getId(item).getPath().startsWith("waxed_")) {
                        ItemStack waxedStack = new ItemStack(item);
                        BlockStateComponent defaultComponent = waxedStack.getOrDefault(DataComponentTypes.BLOCK_STATE, BlockStateComponent.DEFAULT);
                        waxedStack.set(DataComponentTypes.BLOCK_STATE, defaultComponent.with(MoldyLogBlock.WAXED, true));
                        stacksToAdd.add(waxedStack);
                    }
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
        BlockSetType setType = switch (prefix) {
            case "spruce" -> BlockSetType.SPRUCE;
            case "birch" -> BlockSetType.BIRCH;
            case "jungle" -> BlockSetType.JUNGLE;
            case "acacia" -> BlockSetType.ACACIA;
            case "dark_oak" -> BlockSetType.DARK_OAK;
            case "mangrove" -> BlockSetType.MANGROVE;
            case "cherry" -> BlockSetType.CHERRY;
            case "crimson" -> BlockSetType.CRIMSON;
            case "warped" -> BlockSetType.WARPED;
            case "bamboo" -> BlockSetType.BAMBOO;
            default -> BlockSetType.OAK;
        };
        WoodType woodType = switch (prefix) {
            case "spruce" -> WoodType.SPRUCE;
            case "birch" -> WoodType.BIRCH;
            case "jungle" -> WoodType.JUNGLE;
            case "acacia" -> WoodType.ACACIA;
            case "dark_oak" -> WoodType.DARK_OAK;
            case "mangrove" -> WoodType.MANGROVE;
            case "cherry" -> WoodType.CHERRY;
            case "crimson" -> WoodType.CRIMSON;
            case "warped" -> WoodType.WARPED;
            case "bamboo" -> WoodType.BAMBOO;
            default -> WoodType.OAK;
        };
        
        Block stairs = registerBlock("moldy_" + prefix + "_stairs", new MoldyStairsBlock(planks.getDefaultState(), AbstractBlock.Settings.copy(vanillaPlanks).ticksRandomly()));
        Block slab = registerBlock("moldy_" + prefix + "_slab", new MoldySlabBlock(AbstractBlock.Settings.copy(vanillaPlanks).ticksRandomly()));
        Block fence = registerBlock("moldy_" + prefix + "_fence", new MoldyFenceBlock(AbstractBlock.Settings.copy(vanillaPlanks).ticksRandomly()));
        Block gate = registerBlock("moldy_" + prefix + "_fence_gate", new MoldyFenceGateBlock(woodType, AbstractBlock.Settings.copy(vanillaPlanks).ticksRandomly()));
        Block door = registerBlock("moldy_" + prefix + "_door", new MoldyDoorBlock(setType, AbstractBlock.Settings.copy(vanillaPlanks).ticksRandomly().nonOpaque()));
        Block trapdoor = registerBlock("moldy_" + prefix + "_trapdoor", new MoldyTrapdoorBlock(setType, AbstractBlock.Settings.copy(vanillaPlanks).ticksRandomly().nonOpaque()));
        Block pressurePlate = registerBlock("moldy_" + prefix + "_pressure_plate", new MoldyPressurePlateBlock(setType, AbstractBlock.Settings.copy(vanillaPlanks).ticksRandomly().noCollision()));
        Block button = registerBlock("moldy_" + prefix + "_button", new MoldyButtonBlock(setType, 30, AbstractBlock.Settings.copy(vanillaPlanks).ticksRandomly().noCollision()));
        Block vanillaStairs = Registries.BLOCK.get(Identifier.of("minecraft", prefix + "_stairs"));
        Block vanillaSlab = Registries.BLOCK.get(Identifier.of("minecraft", prefix + "_slab"));
        Block vanillaFence = Registries.BLOCK.get(Identifier.of("minecraft", prefix + "_fence"));
        Block vanillaGate = Registries.BLOCK.get(Identifier.of("minecraft", prefix + "_fence_gate"));
        Block vanillaDoor = Registries.BLOCK.get(Identifier.of("minecraft", prefix + "_door"));
        Block vanillaTrapdoor = Registries.BLOCK.get(Identifier.of("minecraft", prefix + "_trapdoor"));
        Block vanillaPressurePlate = Registries.BLOCK.get(Identifier.of("minecraft", prefix + "_pressure_plate"));
        Block vanillaButton = Registries.BLOCK.get(Identifier.of("minecraft", prefix + "_button"));

        // Map them
        VANILLA_TO_MOLDY.put(vanillaLog, log);
        VANILLA_TO_MOLDY.put(vanillaStrippedLog, strippedLog);
        VANILLA_TO_MOLDY.put(vanillaPlanks, planks);
        VANILLA_TO_MOLDY.put(vanillaStairs, stairs);
        VANILLA_TO_MOLDY.put(vanillaSlab, slab);
        VANILLA_TO_MOLDY.put(vanillaFence, fence);
        VANILLA_TO_MOLDY.put(vanillaGate, gate);
        VANILLA_TO_MOLDY.put(vanillaDoor, door);
        VANILLA_TO_MOLDY.put(vanillaTrapdoor, trapdoor);
        VANILLA_TO_MOLDY.put(vanillaPressurePlate, pressurePlate);
        VANILLA_TO_MOLDY.put(vanillaButton, button);

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
        registerStageItems(vanillaPressurePlate, prefix + "_pressure_plate", pressurePlate);
        registerStageItems(vanillaButton, prefix + "_button", button);
        
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
        items.add(registerStageItem("waxed_" + baseName, baseBlock, 0, true));
        items.add(registerStageItem("tainted_" + baseName, baseBlock, 1, false));
        items.add(registerStageItem("moldy_" + baseName, baseBlock, 2, false));
        items.add(registerStageItem("rotten_" + baseName, baseBlock, 3, false));
        MOLDY_ITEMS_BY_VANILLA.put(vanillaItem, items);
        MOLDY_ITEMS_BY_BLOCK.put(baseBlock, items);
    }

    private static Item registerStageItem(String name, Block baseBlock, int stage, boolean isWaxed) {
        Item.Settings settings = new Item.Settings().component(
                DataComponentTypes.BLOCK_STATE,
                BlockStateComponent.DEFAULT.with(MoldyLogBlock.STAGE, stage).with(MoldyLogBlock.WAXED, isWaxed)
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
                    appendMoldyTooltip(name, stack, tooltip);
                }
                @Override
                public net.minecraft.text.Text getName(ItemStack stack) {
                    net.minecraft.text.Text originalName = super.getName(stack);
                    net.minecraft.component.type.BlockStateComponent comp = stack.get(net.minecraft.component.DataComponentTypes.BLOCK_STATE);
                    boolean isStackWaxed = comp != null && comp.getValue(MoldyLogBlock.WAXED) == Boolean.TRUE;
                    if (isStackWaxed && !name.startsWith("waxed_")) {
                        return net.minecraft.text.Text.translatable("item.spores--shadows.waxed_format", originalName);
                    }
                    return originalName;
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
                    appendMoldyTooltip(name, stack, tooltip);
                }
                @Override
                public net.minecraft.text.Text getName(ItemStack stack) {
                    net.minecraft.text.Text originalName = super.getName(stack);
                    net.minecraft.component.type.BlockStateComponent comp = stack.get(net.minecraft.component.DataComponentTypes.BLOCK_STATE);
                    boolean isStackWaxed = comp != null && comp.getValue(MoldyLogBlock.WAXED) == Boolean.TRUE;
                    if (isStackWaxed && !name.startsWith("waxed_")) {
                        return net.minecraft.text.Text.translatable("item.spores--shadows.waxed_format", originalName);
                    }
                    return originalName;
                }
            };
        }
        return Registry.register(Registries.ITEM, SporesShadows.id(name), item);
    }
    private static void appendMoldyTooltip(String name, ItemStack stack, java.util.List<net.minecraft.text.Text> tooltip) {
        BlockStateComponent comp = stack.get(DataComponentTypes.BLOCK_STATE);
        boolean isWaxed = comp != null && comp.getValue(MoldyLogBlock.WAXED) == Boolean.TRUE;
        
        if (!isWaxed && !name.startsWith("waxed_")) {
            if (name.contains("log") || name.contains("wood")) {
                tooltip.add(net.minecraft.text.Text.translatable("tooltip.spores--shadows.moldy_log_desc_1").formatted(net.minecraft.util.Formatting.GRAY));
                tooltip.add(net.minecraft.text.Text.translatable("tooltip.spores--shadows.moldy_log_desc_2").formatted(net.minecraft.util.Formatting.GRAY));
            } else if (name.contains("planks")) {
                tooltip.add(net.minecraft.text.Text.translatable("tooltip.spores--shadows.moldy_planks_desc_1").formatted(net.minecraft.util.Formatting.GRAY));
                tooltip.add(net.minecraft.text.Text.translatable("tooltip.spores--shadows.moldy_planks_desc_2").formatted(net.minecraft.util.Formatting.GRAY));
            } else if (name.contains("button") || name.contains("pressure_plate")) {
                tooltip.add(net.minecraft.text.Text.translatable("tooltip.spores--shadows.moldy_redstone_desc_1").formatted(net.minecraft.util.Formatting.GRAY));
                tooltip.add(net.minecraft.text.Text.translatable("tooltip.spores--shadows.moldy_redstone_desc_2").formatted(net.minecraft.util.Formatting.GRAY));
            } else {
                tooltip.add(net.minecraft.text.Text.translatable("tooltip.spores--shadows.moldy_general_desc_1").formatted(net.minecraft.util.Formatting.GRAY));
                tooltip.add(net.minecraft.text.Text.translatable("tooltip.spores--shadows.moldy_general_desc_2").formatted(net.minecraft.util.Formatting.GRAY));
            }
        }
    }
}
