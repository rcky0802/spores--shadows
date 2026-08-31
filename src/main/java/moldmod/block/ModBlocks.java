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

    public static final Map<Block, Block> MOLDY_TO_VANILLA = new HashMap<>();
    public static final Map<Block, Block> VANILLA_TO_MOLDY = new HashMap<>();
    public static final Map<Block, Block> MOLDY_TO_WAXED = new HashMap<>();
    public static final Map<Block, Block> WAXED_TO_MOLDY = new HashMap<>();
    public static final Map<Item, java.util.List<Item>> MOLDY_ITEMS_BY_VANILLA = new java.util.LinkedHashMap<>();
    public static final Map<Block, java.util.List<Item>> MOLDY_ITEMS_BY_BLOCK = new HashMap<>();

    public static void registerModBlocks() {
        SporesShadows.LOGGER.info("Registering ModBlocks for " + SporesShadows.MOD_ID);

        for (moldmod.SporesShadowsConstants.MoldyWoodType wood : moldmod.SporesShadowsConstants.WOOD_TYPES) {
            registerWoodSet(wood);
        }

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(entries -> {
            for (Map.Entry<Item, java.util.List<Item>> entry : MOLDY_ITEMS_BY_VANILLA.entrySet()) {
                Item vanillaItem = entry.getKey();
                java.util.List<ItemStack> stacksToAdd = new java.util.ArrayList<>();
                java.util.List<Item> items = entry.getValue();

                for (Item item : items) {
                    stacksToAdd.add(new ItemStack(item));
                }
                entries.addAfter(vanillaItem, stacksToAdd);
            }
        });
    }

    private static void registerWoodSet(moldmod.SporesShadowsConstants.MoldyWoodType moldyWoodType) {
        String prefix = moldyWoodType.name();
        String logName = moldyWoodType.getLogName();
        String woodName = moldyWoodType.getWoodName();
        BlockSetType setType = moldyWoodType.setType();
        WoodType woodType = moldyWoodType.woodType();

        // 1. Logs & Stripped Logs
        Block vanillaStrippedLog = Registries.BLOCK.get(Identifier.of("minecraft", "stripped_" + logName));
        Block strippedLog = registerBlock("moldy_stripped_" + logName,
                new MoldyLogBlock(AbstractBlock.Settings.copy(vanillaStrippedLog).ticksRandomly(), null));
        Block waxedStrippedLog = registerBlock("waxed_stripped_" + logName,
                new MoldyLogBlock(AbstractBlock.Settings.copy(vanillaStrippedLog).ticksRandomly(), null));
        registerVariant("stripped_" + logName, vanillaStrippedLog, strippedLog, waxedStrippedLog);

        Block vanillaLog = Registries.BLOCK.get(Identifier.of("minecraft", logName));
        Block log = registerBlock("moldy_" + logName,
                new MoldyLogBlock(AbstractBlock.Settings.copy(vanillaLog).ticksRandomly(), strippedLog));
        Block waxedLog = registerBlock("waxed_" + logName,
                new MoldyLogBlock(AbstractBlock.Settings.copy(vanillaLog).ticksRandomly(), waxedStrippedLog));
        registerVariant(logName, vanillaLog, log, waxedLog);

        // 2. Planks
        Block vanillaPlanks = Registries.BLOCK.get(Identifier.of("minecraft", prefix + "_planks"));
        Block planks = registerBlock("moldy_" + prefix + "_planks",
                new MoldyPlanksBlock(AbstractBlock.Settings.copy(vanillaPlanks).ticksRandomly()));
        Block waxedPlanks = registerBlock("waxed_" + prefix + "_planks",
                new MoldyPlanksBlock(AbstractBlock.Settings.copy(vanillaPlanks).ticksRandomly()));
        registerVariant(prefix + "_planks", vanillaPlanks, planks, waxedPlanks);

        // 3. Stairs & Slabs
        Block vanillaStairs = Registries.BLOCK.get(Identifier.of("minecraft", prefix + "_stairs"));
        Block stairs = registerBlock("moldy_" + prefix + "_stairs", new MoldyStairsBlock(planks.getDefaultState(),
                AbstractBlock.Settings.copy(vanillaPlanks).ticksRandomly()));
        Block waxedStairs = registerBlock("waxed_" + prefix + "_stairs", new MoldyStairsBlock(
                waxedPlanks.getDefaultState(), AbstractBlock.Settings.copy(vanillaPlanks).ticksRandomly()));
        registerVariant(prefix + "_stairs", vanillaStairs, stairs, waxedStairs);

        Block vanillaSlab = Registries.BLOCK.get(Identifier.of("minecraft", prefix + "_slab"));
        Block slab = registerBlock("moldy_" + prefix + "_slab",
                new MoldySlabBlock(AbstractBlock.Settings.copy(vanillaPlanks).ticksRandomly()));
        Block waxedSlab = registerBlock("waxed_" + prefix + "_slab",
                new MoldySlabBlock(AbstractBlock.Settings.copy(vanillaPlanks).ticksRandomly()));
        registerVariant(prefix + "_slab", vanillaSlab, slab, waxedSlab);

        // 4. Fences & Gates
        Block vanillaFence = Registries.BLOCK.get(Identifier.of("minecraft", prefix + "_fence"));
        Block fence = registerBlock("moldy_" + prefix + "_fence",
                new MoldyFenceBlock(AbstractBlock.Settings.copy(vanillaPlanks).ticksRandomly()));
        Block waxedFence = registerBlock("waxed_" + prefix + "_fence",
                new MoldyFenceBlock(AbstractBlock.Settings.copy(vanillaPlanks).ticksRandomly()));
        registerVariant(prefix + "_fence", vanillaFence, fence, waxedFence);

        Block vanillaGate = Registries.BLOCK.get(Identifier.of("minecraft", prefix + "_fence_gate"));
        Block gate = registerBlock("moldy_" + prefix + "_fence_gate",
                new MoldyFenceGateBlock(woodType, AbstractBlock.Settings.copy(vanillaPlanks).ticksRandomly()));
        Block waxedGate = registerBlock("waxed_" + prefix + "_fence_gate",
                new MoldyFenceGateBlock(woodType, AbstractBlock.Settings.copy(vanillaPlanks).ticksRandomly()));
        registerVariant(prefix + "_fence_gate", vanillaGate, gate, waxedGate);

        // 5. Doors & Trapdoors
        Block vanillaDoor = Registries.BLOCK.get(Identifier.of("minecraft", prefix + "_door"));
        Block door = registerBlock("moldy_" + prefix + "_door",
                new MoldyDoorBlock(setType, AbstractBlock.Settings.copy(vanillaPlanks).ticksRandomly().nonOpaque()));
        Block waxedDoor = registerBlock("waxed_" + prefix + "_door",
                new MoldyDoorBlock(setType, AbstractBlock.Settings.copy(vanillaPlanks).ticksRandomly().nonOpaque()));
        registerVariant(prefix + "_door", vanillaDoor, door, waxedDoor);

        Block vanillaTrapdoor = Registries.BLOCK.get(Identifier.of("minecraft", prefix + "_trapdoor"));
        Block trapdoor = registerBlock("moldy_" + prefix + "_trapdoor", new MoldyTrapdoorBlock(setType,
                AbstractBlock.Settings.copy(vanillaPlanks).ticksRandomly().nonOpaque()));
        Block waxedTrapdoor = registerBlock("waxed_" + prefix + "_trapdoor", new MoldyTrapdoorBlock(setType,
                AbstractBlock.Settings.copy(vanillaPlanks).ticksRandomly().nonOpaque()));
        registerVariant(prefix + "_trapdoor", vanillaTrapdoor, trapdoor, waxedTrapdoor);

        // 6. Redstone (Pressure Plates & Buttons)
        Block vanillaPressurePlate = Registries.BLOCK.get(Identifier.of("minecraft", prefix + "_pressure_plate"));
        Block pressurePlate = registerBlock("moldy_" + prefix + "_pressure_plate", new MoldyPressurePlateBlock(setType,
                AbstractBlock.Settings.copy(vanillaPlanks).ticksRandomly().noCollision()));
        Block waxedPressurePlate = registerBlock("waxed_" + prefix + "_pressure_plate", new MoldyPressurePlateBlock(
                setType, AbstractBlock.Settings.copy(vanillaPlanks).ticksRandomly().noCollision()));
        registerVariant(prefix + "_pressure_plate", vanillaPressurePlate, pressurePlate, waxedPressurePlate);

        Block vanillaButton = Registries.BLOCK.get(Identifier.of("minecraft", prefix + "_button"));
        Block button = registerBlock("moldy_" + prefix + "_button", new MoldyButtonBlock(setType, 30,
                AbstractBlock.Settings.copy(vanillaPlanks).ticksRandomly().noCollision()));
        Block waxedButton = registerBlock("waxed_" + prefix + "_button", new MoldyButtonBlock(setType, 30,
                AbstractBlock.Settings.copy(vanillaPlanks).ticksRandomly().noCollision()));
        registerVariant(prefix + "_button", vanillaButton, button, waxedButton);

        // 7. Wood & Stripped Wood
        if (woodName != null) {
            Block vanillaStrippedWood = Registries.BLOCK.get(Identifier.of("minecraft", "stripped_" + woodName));
            Block strippedWood = registerBlock("moldy_stripped_" + woodName,
                    new MoldyLogBlock(AbstractBlock.Settings.copy(vanillaStrippedWood).ticksRandomly(), null));
            Block waxedStrippedWood = registerBlock("waxed_stripped_" + woodName,
                    new MoldyLogBlock(AbstractBlock.Settings.copy(vanillaStrippedWood).ticksRandomly(), null));
            registerVariant("stripped_" + woodName, vanillaStrippedWood, strippedWood, waxedStrippedWood);

            Block vanillaWood = Registries.BLOCK.get(Identifier.of("minecraft", woodName));
            Block wood = registerBlock("moldy_" + woodName,
                    new MoldyLogBlock(AbstractBlock.Settings.copy(vanillaWood).ticksRandomly(), strippedWood));
            Block waxedWood = registerBlock("waxed_" + woodName,
                    new MoldyLogBlock(AbstractBlock.Settings.copy(vanillaWood).ticksRandomly(), waxedStrippedWood));
            registerVariant(woodName, vanillaWood, wood, waxedWood);
        }
    }

    private static void registerVariant(String baseName, Block vanillaBlock, Block moldyBlock, Block waxedBlock) {
        VANILLA_TO_MOLDY.put(vanillaBlock, moldyBlock);
        MOLDY_TO_VANILLA.put(moldyBlock, vanillaBlock);
        MOLDY_TO_WAXED.put(moldyBlock, waxedBlock);
        WAXED_TO_MOLDY.put(waxedBlock, moldyBlock);
        registerStageItems(vanillaBlock, baseName, moldyBlock, waxedBlock);
    }

    private static Block registerBlock(String name, Block block) {
        return Registry.register(Registries.BLOCK, SporesShadows.id(name), block);
    }

    private static void registerStageItems(Block vanillaBlock, String baseName, Block moldyBlock, Block waxedBlock) {
        Item vanillaItem = vanillaBlock.asItem();
        java.util.List<Item> items = new java.util.ArrayList<>();
        for (moldmod.SporesShadowsConstants.MoldStage stage : moldmod.SporesShadowsConstants.MoldStage.values()) {
            if (stage == moldmod.SporesShadowsConstants.MoldStage.WAXED) {
                items.add(registerStageItem("waxed_" + baseName, waxedBlock, stage.getId(), true));
            } else {
                items.add(registerStageItem(stage.getName() + "_" + baseName, moldyBlock, stage.getId(), false));
                items.add(registerStageItem("waxed_" + stage.getName() + "_" + baseName, waxedBlock, stage.getId(),
                        true));
            }
        }
        MOLDY_ITEMS_BY_VANILLA.put(vanillaItem, items);
        MOLDY_ITEMS_BY_BLOCK.put(moldyBlock, items);
        MOLDY_ITEMS_BY_BLOCK.put(waxedBlock, items);
    }

    private static Item registerStageItem(String name, Block baseBlock, int stage, boolean isWaxed) {
        Item.Settings settings = new Item.Settings().component(
                DataComponentTypes.BLOCK_STATE,
                BlockStateComponent.DEFAULT.with(MoldyLogBlock.STAGE, stage).with(MoldyLogBlock.WAXED, isWaxed));
        Item item;
        if (baseBlock instanceof net.minecraft.block.DoorBlock) {
            item = new net.minecraft.item.TallBlockItem(baseBlock, settings) {
                @Override
                public String getTranslationKey() {
                    return "item." + moldmod.SporesShadows.MOD_ID + "." + name;
                }

                @Override
                public void appendTooltip(ItemStack stack, Item.TooltipContext context,
                        java.util.List<net.minecraft.text.Text> tooltip, net.minecraft.item.tooltip.TooltipType type) {
                    super.appendTooltip(stack, context, tooltip, type);
                    appendMoldyTooltip(name, stack, tooltip);
                }

            };
        } else {
            item = new BlockItem(baseBlock, settings) {
                @Override
                public String getTranslationKey() {
                    return "item." + moldmod.SporesShadows.MOD_ID + "." + name;
                }

                @Override
                public void appendTooltip(ItemStack stack, Item.TooltipContext context,
                        java.util.List<net.minecraft.text.Text> tooltip, net.minecraft.item.tooltip.TooltipType type) {
                    super.appendTooltip(stack, context, tooltip, type);
                    appendMoldyTooltip(name, stack, tooltip);
                }

            };
        }
        return Registry.register(Registries.ITEM, SporesShadows.id(name), item);
    }

    private static void appendMoldyTooltip(String name, ItemStack stack,
            java.util.List<net.minecraft.text.Text> tooltip) {
        if (name.contains("log") || name.contains("wood")) {
            tooltip.add(net.minecraft.text.Text
                    .translatable("tooltip." + moldmod.SporesShadows.MOD_ID + ".moldy_log_desc_1")
                    .formatted(net.minecraft.util.Formatting.GRAY));
            tooltip.add(net.minecraft.text.Text
                    .translatable("tooltip." + moldmod.SporesShadows.MOD_ID + ".moldy_log_desc_2")
                    .formatted(net.minecraft.util.Formatting.GRAY));
        } else if (name.contains("planks")) {
            tooltip.add(net.minecraft.text.Text
                    .translatable("tooltip." + moldmod.SporesShadows.MOD_ID + ".moldy_planks_desc_1")
                    .formatted(net.minecraft.util.Formatting.GRAY));
            tooltip.add(net.minecraft.text.Text
                    .translatable("tooltip." + moldmod.SporesShadows.MOD_ID + ".moldy_planks_desc_2")
                    .formatted(net.minecraft.util.Formatting.GRAY));
        } else if (name.contains("button") || name.contains("pressure_plate")) {
            tooltip.add(net.minecraft.text.Text
                    .translatable("tooltip." + moldmod.SporesShadows.MOD_ID + ".moldy_redstone_desc_1")
                    .formatted(net.minecraft.util.Formatting.GRAY));
            tooltip.add(net.minecraft.text.Text
                    .translatable("tooltip." + moldmod.SporesShadows.MOD_ID + ".moldy_redstone_desc_2")
                    .formatted(net.minecraft.util.Formatting.GRAY));
        } else {
            tooltip.add(net.minecraft.text.Text
                    .translatable("tooltip." + moldmod.SporesShadows.MOD_ID + ".moldy_general_desc_1")
                    .formatted(net.minecraft.util.Formatting.GRAY));
            tooltip.add(net.minecraft.text.Text
                    .translatable("tooltip." + moldmod.SporesShadows.MOD_ID + ".moldy_general_desc_2")
                    .formatted(net.minecraft.util.Formatting.GRAY));
        }
    }
}
