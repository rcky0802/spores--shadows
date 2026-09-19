package moldmod.block;

import moldmod.SporesShadows;
import moldmod.block.registry.DeviceBlocksRegistrar;
import moldmod.block.registry.WoodsetBlocksRegistrar;
import moldmod.block.registry.WorkstationBlocksRegistrar;
import moldmod.item.ModItems;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ModBlocks {

    private ModBlocks() {}

    public static final Map<Block, Block> MOLDY_TO_VANILLA = new HashMap<>();
    public static final Map<Block, Block> VANILLA_TO_MOLDY = new HashMap<>();
    public static final Map<Block, Block> MOLDY_TO_WAXED = new HashMap<>();
    public static final Map<Block, Block> WAXED_TO_MOLDY = new HashMap<>();
    public static final Map<Item, List<Item>> MOLDY_ITEMS_BY_VANILLA = new LinkedHashMap<>();
    public static final Map<Block, List<Item>> MOLDY_ITEMS_BY_BLOCK = new HashMap<>();
    public static final List<Block> MOLDY_SIGNS = new ArrayList<>();
    public static final List<Block> MOLDY_HANGING_SIGNS = new ArrayList<>();
    public static final Map<Block, Block> WALL_TO_STANDING = new HashMap<>();

    public static Block MOLDY_BOOKSHELF;
    public static Block WAXED_BOOKSHELF;
    public static Block MOLDY_CHISELED_BOOKSHELF;
    public static Block WAXED_CHISELED_BOOKSHELF;
    public static Block MOLDY_LADDER;
    public static Block WAXED_LADDER;
    public static Block MOLDY_NOTE_BLOCK;
    public static Block WAXED_NOTE_BLOCK;
    public static Block MOLDY_JUKEBOX;
    public static Block WAXED_JUKEBOX;
    public static Block MOLDY_CRAFTING_TABLE;
    public static Block WAXED_CRAFTING_TABLE;
    public static Block MOLDY_BARREL;
    public static Block WAXED_BARREL;
    public static Block MOLDY_CHEST;
    public static Block WAXED_CHEST;
    public static Block MOLDY_TRAPPED_CHEST;
    public static Block WAXED_TRAPPED_CHEST;
    public static Block MOLDY_COMPOSTER;
    public static Block WAXED_COMPOSTER;
    public static Block MOLDY_FLETCHING_TABLE;
    public static Block WAXED_FLETCHING_TABLE;
    public static Block MOLDY_CARTOGRAPHY_TABLE;
    public static Block WAXED_CARTOGRAPHY_TABLE;
    public static Block MOLDY_LOOM;
    public static Block WAXED_LOOM;
    public static Block MOLDY_LECTERN;
    public static Block WAXED_LECTERN;

    public static final Block SPORE_DETECTOR = DeviceBlocksRegistrar.SPORE_DETECTOR;
    public static final Block MOISTURE_DETECTOR = DeviceBlocksRegistrar.MOISTURE_DETECTOR;
    public static final Block DEHUMIDIFIER = DeviceBlocksRegistrar.DEHUMIDIFIER;
    public static final Item DEHUMIDIFIER_ITEM = DeviceBlocksRegistrar.DEHUMIDIFIER_ITEM;
    public static final Block AIR_PURIFIER = DeviceBlocksRegistrar.AIR_PURIFIER;
    public static final Item AIR_PURIFIER_ITEM = DeviceBlocksRegistrar.AIR_PURIFIER_ITEM;

    public static final RegistryKey<ItemGroup> SPORES_SHADOWS_GROUP_KEY = RegistryKey.of(
            RegistryKeys.ITEM_GROUP,
            SporesShadows.id("blocks"));

    public static final ItemGroup SPORES_SHADOWS_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(Registries.ITEM.get(SporesShadows.id("moldy_oak_log"))))
            .displayName(Text.translatable("itemGroup.spores--shadows.blocks"))
            .entries((displayContext, entries) -> {
                entries.add(ModItems.SPORE_FILTER);
                entries.add(ModItems.SPORE_MASK);
                entries.add(ModItems.SPORE_DETECTOR);
                entries.add(ModItems.MOISTURE_DETECTOR);
                entries.add(DEHUMIDIFIER_ITEM);
                entries.add(AIR_PURIFIER_ITEM);
                for (List<Item> items : MOLDY_ITEMS_BY_VANILLA.values()) {
                    for (Item item : items) {
                        entries.add(item);
                    }
                }
            })
            .build();

    public static void registerModBlocks() {
        SporesShadows.LOGGER.info("Registering ModBlocks for " + SporesShadows.MOD_ID);

        WoodsetBlocksRegistrar.registerAll();
        WorkstationBlocksRegistrar.registerAll();
        DeviceBlocksRegistrar.registerAll();

        Registry.register(Registries.ITEM_GROUP, SPORES_SHADOWS_GROUP_KEY, SPORES_SHADOWS_GROUP);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(entries -> {
            entries.add(DEHUMIDIFIER_ITEM);
            entries.add(AIR_PURIFIER_ITEM);
        });

        for (RegistryKey<ItemGroup> groupKey : List.of(
                ItemGroups.BUILDING_BLOCKS,
                ItemGroups.NATURAL,
                ItemGroups.FUNCTIONAL,
                ItemGroups.REDSTONE)) {
            ItemGroupEvents.modifyEntriesEvent(groupKey).register(entries -> {
                for (Map.Entry<Item, List<Item>> entry : MOLDY_ITEMS_BY_VANILLA.entrySet()) {
                    Item vanillaItem = entry.getKey();
                    boolean containsVanilla = false;
                    for (ItemStack displayStack : entries.getDisplayStacks()) {
                        if (displayStack.isOf(vanillaItem)) {
                            containsVanilla = true;
                            break;
                        }
                    }
                    if (containsVanilla) {
                        List<ItemStack> stacksToAdd = new ArrayList<>();
                        for (Item item : entry.getValue()) {
                            stacksToAdd.add(new ItemStack(item));
                        }
                        entries.addAfter(vanillaItem, stacksToAdd);
                    }
                }
            });
        }
    }
}
