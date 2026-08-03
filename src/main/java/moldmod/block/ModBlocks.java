package moldmod.block;

import moldmod.SporesShadows;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BlockStateComponent;

public class ModBlocks {

    public static final Block MOLDY_STRIPPED_OAK_LOG = Registry.register(
            Registries.BLOCK, SporesShadows.id("moldy_stripped_oak_log"),
            new MoldyOakLogBlock(AbstractBlock.Settings.copy(Blocks.STRIPPED_OAK_LOG).ticksRandomly(), null)
    );
    public static final Block MOLDY_OAK_LOG = Registry.register(
            Registries.BLOCK, SporesShadows.id("moldy_oak_log"),
            new MoldyOakLogBlock(AbstractBlock.Settings.copy(Blocks.OAK_LOG).ticksRandomly(), MOLDY_STRIPPED_OAK_LOG)
    );
    public static final Block MOLDY_OAK_PLANKS = Registry.register(
            Registries.BLOCK, SporesShadows.id("moldy_oak_planks"),
            new MoldyOakPlanksBlock(AbstractBlock.Settings.copy(Blocks.OAK_PLANKS).ticksRandomly())
    );
    public static final Block MOLDY_STRIPPED_OAK_WOOD = Registry.register(
            Registries.BLOCK, SporesShadows.id("moldy_stripped_oak_wood"),
            new MoldyOakLogBlock(AbstractBlock.Settings.copy(Blocks.STRIPPED_OAK_WOOD).ticksRandomly(), null)
    );
    public static final Block MOLDY_OAK_WOOD = Registry.register(
            Registries.BLOCK, SporesShadows.id("moldy_oak_wood"),
            new MoldyOakLogBlock(AbstractBlock.Settings.copy(Blocks.OAK_WOOD).ticksRandomly(), MOLDY_STRIPPED_OAK_WOOD)
    );

    // Oak Log Items
    public static final Item TAINTED_OAK_LOG = registerStageItem("tainted_oak_log", MOLDY_OAK_LOG, 1);
    public static final Item MOLDY_OAK_LOG_ITEM = registerStageItem("moldy_oak_log", MOLDY_OAK_LOG, 2);
    public static final Item ROTTEN_OAK_LOG = registerStageItem("rotten_oak_log", MOLDY_OAK_LOG, 3);

    // Stripped Oak Log Items
    public static final Item TAINTED_STRIPPED_OAK_LOG = registerStageItem("tainted_stripped_oak_log", MOLDY_STRIPPED_OAK_LOG, 1);
    public static final Item MOLDY_STRIPPED_OAK_LOG_ITEM = registerStageItem("moldy_stripped_oak_log", MOLDY_STRIPPED_OAK_LOG, 2);
    public static final Item ROTTEN_STRIPPED_OAK_LOG = registerStageItem("rotten_stripped_oak_log", MOLDY_STRIPPED_OAK_LOG, 3);

    // Oak Planks Items
    public static final Item TAINTED_OAK_PLANKS = registerStageItem("tainted_oak_planks", MOLDY_OAK_PLANKS, 1);
    public static final Item MOLDY_OAK_PLANKS_ITEM = registerStageItem("moldy_oak_planks", MOLDY_OAK_PLANKS, 2);
    public static final Item ROTTEN_OAK_PLANKS = registerStageItem("rotten_oak_planks", MOLDY_OAK_PLANKS, 3);

    // Oak Wood Items
    public static final Item TAINTED_OAK_WOOD = registerStageItem("tainted_oak_wood", MOLDY_OAK_WOOD, 1);
    public static final Item MOLDY_OAK_WOOD_ITEM = registerStageItem("moldy_oak_wood", MOLDY_OAK_WOOD, 2);
    public static final Item ROTTEN_OAK_WOOD = registerStageItem("rotten_oak_wood", MOLDY_OAK_WOOD, 3);

    // Stripped Oak Wood Items
    public static final Item TAINTED_STRIPPED_OAK_WOOD = registerStageItem("tainted_stripped_oak_wood", MOLDY_STRIPPED_OAK_WOOD, 1);
    public static final Item MOLDY_STRIPPED_OAK_WOOD_ITEM = registerStageItem("moldy_stripped_oak_wood", MOLDY_STRIPPED_OAK_WOOD, 2);
    public static final Item ROTTEN_STRIPPED_OAK_WOOD = registerStageItem("rotten_stripped_oak_wood", MOLDY_STRIPPED_OAK_WOOD, 3);

    private static Item registerStageItem(String name, Block baseBlock, int stage) {
        Item.Settings settings = new Item.Settings().component(
                DataComponentTypes.BLOCK_STATE,
                BlockStateComponent.DEFAULT.with(MoldyOakLogBlock.STAGE, stage)
        );
        return Registry.register(Registries.ITEM, SporesShadows.id(name), new BlockItem(baseBlock, settings) {
            @Override
            public String getTranslationKey() {
                return "item.spores--shadows." + name;
            }

            @Override
            public void appendTooltip(ItemStack stack, Item.TooltipContext context, java.util.List<net.minecraft.text.Text> tooltip, net.minecraft.item.tooltip.TooltipType type) {
                super.appendTooltip(stack, context, tooltip, type);
                net.minecraft.component.type.BlockStateComponent comp = stack.get(DataComponentTypes.BLOCK_STATE);
                if (comp != null && comp.getValue(MoldyOakLogBlock.WAXED) == Boolean.TRUE) {
                    tooltip.add(net.minecraft.text.Text.translatable("tooltip.spores--shadows.waxed").formatted(net.minecraft.util.Formatting.GOLD));
                }
            }
        });
    }

    public static void registerModBlocks() {
        SporesShadows.LOGGER.info("Registering ModBlocks for " + SporesShadows.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.NATURAL).register(entries -> {
            Item[] items = {
                TAINTED_OAK_LOG, MOLDY_OAK_LOG_ITEM, ROTTEN_OAK_LOG,
                TAINTED_OAK_WOOD, MOLDY_OAK_WOOD_ITEM, ROTTEN_OAK_WOOD,
                TAINTED_STRIPPED_OAK_LOG, MOLDY_STRIPPED_OAK_LOG_ITEM, ROTTEN_STRIPPED_OAK_LOG,
                TAINTED_STRIPPED_OAK_WOOD, MOLDY_STRIPPED_OAK_WOOD_ITEM, ROTTEN_STRIPPED_OAK_WOOD,
                TAINTED_OAK_PLANKS, MOLDY_OAK_PLANKS_ITEM, ROTTEN_OAK_PLANKS
            };
            
            for (Item item : items) {
                // Unwaxed
                entries.add(new ItemStack(item));
                
                // Waxed
                ItemStack waxedStack = new ItemStack(item);
                net.minecraft.component.type.BlockStateComponent defaultComponent = waxedStack.getOrDefault(DataComponentTypes.BLOCK_STATE, net.minecraft.component.type.BlockStateComponent.DEFAULT);
                waxedStack.set(DataComponentTypes.BLOCK_STATE, defaultComponent.with(MoldyOakLogBlock.WAXED, true));
                entries.add(waxedStack);
            }
        });
    }
}
