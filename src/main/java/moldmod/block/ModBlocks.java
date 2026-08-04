package moldmod.block;

import moldmod.SporesShadows;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
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
    private static final Map<String, Item> ALL_MOLDY_ITEMS = new HashMap<>();

    public static void registerModBlocks() {
        SporesShadows.LOGGER.info("Registering ModBlocks for " + SporesShadows.MOD_ID);

        String[] woods = {"oak", "spruce", "birch", "jungle", "acacia", "dark_oak", "mangrove", "cherry"};

        for (String wood : woods) {
            registerWoodSet(wood, wood + "_log", wood + "_wood");
        }
        
        // Bamboo uses block instead of log/wood
        registerWoodSet("bamboo", "bamboo_block", null);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.NATURAL).register(entries -> {
            for (Item item : ALL_MOLDY_ITEMS.values()) {
                // Unwaxed
                entries.add(new ItemStack(item));
                
                // Waxed
                ItemStack waxedStack = new ItemStack(item);
                BlockStateComponent defaultComponent = waxedStack.getOrDefault(DataComponentTypes.BLOCK_STATE, BlockStateComponent.DEFAULT);
                waxedStack.set(DataComponentTypes.BLOCK_STATE, defaultComponent.with(MoldyLogBlock.WAXED, true));
                entries.add(waxedStack);
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

        // Map them
        VANILLA_TO_MOLDY.put(vanillaLog, log);
        VANILLA_TO_MOLDY.put(vanillaStrippedLog, strippedLog);
        VANILLA_TO_MOLDY.put(vanillaPlanks, planks);

        // Register Items
        registerStageItems(logName, log);
        registerStageItems("stripped_" + logName, strippedLog);
        registerStageItems(prefix + "_planks", planks);
        
        if (woodName != null) {
            Block vanillaWood = Registries.BLOCK.get(Identifier.of("minecraft", woodName));
            Block vanillaStrippedWood = Registries.BLOCK.get(Identifier.of("minecraft", "stripped_" + woodName));
            Block strippedWood = registerBlock("moldy_stripped_" + woodName, new MoldyLogBlock(AbstractBlock.Settings.copy(vanillaStrippedWood).ticksRandomly(), null));
            Block wood = registerBlock("moldy_" + woodName, new MoldyLogBlock(AbstractBlock.Settings.copy(vanillaWood).ticksRandomly(), strippedWood));
            
            VANILLA_TO_MOLDY.put(vanillaWood, wood);
            VANILLA_TO_MOLDY.put(vanillaStrippedWood, strippedWood);
            
            registerStageItems(woodName, wood);
            registerStageItems("stripped_" + woodName, strippedWood);
        }
    }

    private static Block registerBlock(String name, Block block) {
        return Registry.register(Registries.BLOCK, SporesShadows.id(name), block);
    }

    private static void registerStageItems(String baseName, Block baseBlock) {
        ALL_MOLDY_ITEMS.put("tainted_" + baseName, registerStageItem("tainted_" + baseName, baseBlock, 1));
        ALL_MOLDY_ITEMS.put("moldy_" + baseName, registerStageItem("moldy_" + baseName, baseBlock, 2));
        ALL_MOLDY_ITEMS.put("rotten_" + baseName, registerStageItem("rotten_" + baseName, baseBlock, 3));
    }

    private static Item registerStageItem(String name, Block baseBlock, int stage) {
        Item.Settings settings = new Item.Settings().component(
                DataComponentTypes.BLOCK_STATE,
                BlockStateComponent.DEFAULT.with(MoldyLogBlock.STAGE, stage)
        );
        return Registry.register(Registries.ITEM, SporesShadows.id(name), new BlockItem(baseBlock, settings) {
            @Override
            public String getTranslationKey() {
                return "item.spores--shadows." + name;
            }

            @Override
            public void appendTooltip(ItemStack stack, Item.TooltipContext context, java.util.List<net.minecraft.text.Text> tooltip, net.minecraft.item.tooltip.TooltipType type) {
                super.appendTooltip(stack, context, tooltip, type);
                BlockStateComponent comp = stack.get(DataComponentTypes.BLOCK_STATE);
                if (comp != null && comp.getValue(MoldyLogBlock.WAXED) == Boolean.TRUE) {
                    tooltip.add(net.minecraft.text.Text.translatable("tooltip.spores--shadows.waxed").formatted(net.minecraft.util.Formatting.GOLD));
                }
            }
        });
    }
}
