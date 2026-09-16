package moldmod.block;

import moldmod.SporesShadows;
import moldmod.SporesShadowsConstants;
import moldmod.SporesShadowsConstants.MoldyWoodType;
import moldmod.item.ModItems;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSetType;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.WoodType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BlockStateComponent;
import net.minecraft.item.BlockItem;
import net.minecraft.item.HangingSignItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SignItem;
import net.minecraft.item.TallBlockItem;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ModBlocks {

        private ModBlocks() {
        }

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

        public static final Block SPORE_DETECTOR = Registry.register(
                        Registries.BLOCK,
                        SporesShadows.id("spore_detector"),
                        new SporeDetectorBlock(AbstractBlock.Settings.copy(Blocks.COPPER_BLOCK)
                                        .nonOpaque()
                                        .strength(1.5f)
                                        .sounds(BlockSoundGroup.COPPER)));

        public static final Block MOISTURE_DETECTOR = Registry.register(
                        Registries.BLOCK,
                        SporesShadows.id("moisture_detector"),
                        new MoistureDetectorBlock(AbstractBlock.Settings.copy(Blocks.COPPER_BLOCK)
                                        .nonOpaque()
                                        .strength(1.5f)
                                        .sounds(BlockSoundGroup.COPPER)));

        public static final Block DEHUMIDIFIER = Registry.register(
                        Registries.BLOCK,
                        SporesShadows.id("dehumidifier"),
                        new moldmod.block.dehumidifier.DehumidifierBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK)
                                        .strength(3.5f)
                                        .sounds(BlockSoundGroup.COPPER)
                                        .luminance(state -> state.get(
                                                        moldmod.block.dehumidifier.DehumidifierBlock.STATUS) == moldmod.block.dehumidifier.DehumidifierStatus.RUNNING
                                                                        ? 7
                                                                        : 0)));

        public static final Item DEHUMIDIFIER_ITEM = Registry.register(
                        Registries.ITEM,
                        SporesShadows.id("dehumidifier"),
                        new BlockItem(DEHUMIDIFIER, new Item.Settings()));

        public static final Block AIR_PURIFIER = Registry.register(
                        Registries.BLOCK,
                        SporesShadows.id("air_purifier"),
                        new moldmod.block.purifier.AirPurifierBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK)
                                        .strength(3.5f)
                                        .sounds(BlockSoundGroup.COPPER)
                                        .luminance(state -> state.get(
                                                        moldmod.block.purifier.AirPurifierBlock.STATUS) == moldmod.block.purifier.PurifierStatus.RUNNING
                                                                        ? 7
                                                                        : 0)));

        public static final Item AIR_PURIFIER_ITEM = Registry.register(
                        Registries.ITEM,
                        SporesShadows.id("air_purifier"),
                        new BlockItem(AIR_PURIFIER, new Item.Settings()));

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

                for (MoldyWoodType wood : SporesShadowsConstants.WOOD_TYPES) {
                        registerWoodSet(wood);
                }

                // Bookshelves
                MOLDY_BOOKSHELF = registerBlock("moldy_bookshelf",
                                new MoldyBookshelfBlock(AbstractBlock.Settings.copy(Blocks.BOOKSHELF).ticksRandomly()));
                WAXED_BOOKSHELF = registerBlock("waxed_bookshelf",
                                new MoldyBookshelfBlock(AbstractBlock.Settings.copy(Blocks.BOOKSHELF).ticksRandomly()));
                registerVariant("bookshelf", Blocks.BOOKSHELF, MOLDY_BOOKSHELF, WAXED_BOOKSHELF);

                // Chiseled Bookshelves
                MOLDY_CHISELED_BOOKSHELF = registerBlock("moldy_chiseled_bookshelf",
                                new MoldyChiseledBookshelfBlock(AbstractBlock.Settings.copy(Blocks.CHISELED_BOOKSHELF)
                                                .ticksRandomly()));
                WAXED_CHISELED_BOOKSHELF = registerBlock("waxed_chiseled_bookshelf",
                                new MoldyChiseledBookshelfBlock(AbstractBlock.Settings.copy(Blocks.CHISELED_BOOKSHELF)
                                                .ticksRandomly()));
                registerVariant("chiseled_bookshelf", Blocks.CHISELED_BOOKSHELF, MOLDY_CHISELED_BOOKSHELF,
                                WAXED_CHISELED_BOOKSHELF);

                // Ladders
                MOLDY_LADDER = registerBlock("moldy_ladder",
                                new MoldyLadderBlock(AbstractBlock.Settings.copy(Blocks.LADDER).ticksRandomly()));
                WAXED_LADDER = registerBlock("waxed_ladder",
                                new MoldyLadderBlock(AbstractBlock.Settings.copy(Blocks.LADDER).ticksRandomly()));
                registerVariant("ladder", Blocks.LADDER, MOLDY_LADDER, WAXED_LADDER);

                // Note Blocks
                MOLDY_NOTE_BLOCK = registerBlock("moldy_note_block",
                                new MoldyNoteBlock(AbstractBlock.Settings.copy(Blocks.NOTE_BLOCK).ticksRandomly()));
                WAXED_NOTE_BLOCK = registerBlock("waxed_note_block",
                                new MoldyNoteBlock(AbstractBlock.Settings.copy(Blocks.NOTE_BLOCK).ticksRandomly()));
                registerVariant("note_block", Blocks.NOTE_BLOCK, MOLDY_NOTE_BLOCK, WAXED_NOTE_BLOCK);

                // Jukeboxes
                MOLDY_JUKEBOX = registerBlock("moldy_jukebox",
                                new MoldyJukeboxBlock(AbstractBlock.Settings.copy(Blocks.JUKEBOX).ticksRandomly()));
                WAXED_JUKEBOX = registerBlock("waxed_jukebox",
                                new MoldyJukeboxBlock(AbstractBlock.Settings.copy(Blocks.JUKEBOX).ticksRandomly()));
                registerVariant("jukebox", Blocks.JUKEBOX, MOLDY_JUKEBOX, WAXED_JUKEBOX);

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

        private static void registerWoodSet(MoldyWoodType moldyWoodType) {
                String namespace = moldyWoodType.namespace();
                String prefix = moldyWoodType.name();
                String logName = moldyWoodType.getLogName();
                String woodName = moldyWoodType.getWoodName();
                BlockSetType setType = moldyWoodType.setType();
                WoodType woodType = moldyWoodType.woodType();

                // 1. Logs & Stripped Logs
                Block vanillaStrippedLog = Registries.BLOCK.get(Identifier.of(namespace, "stripped_" + logName));
                Block strippedLog = registerBlock("moldy_stripped_" + logName,
                                new MoldyLogBlock(AbstractBlock.Settings.copy(vanillaStrippedLog).ticksRandomly(),
                                                null));
                Block waxedStrippedLog = registerBlock("waxed_stripped_" + logName,
                                new MoldyLogBlock(AbstractBlock.Settings.copy(vanillaStrippedLog).ticksRandomly(),
                                                null));
                registerVariant("stripped_" + logName, vanillaStrippedLog, strippedLog, waxedStrippedLog);

                Block vanillaLog = Registries.BLOCK.get(Identifier.of(namespace, logName));
                Block log = registerBlock("moldy_" + logName,
                                new MoldyLogBlock(AbstractBlock.Settings.copy(vanillaLog).ticksRandomly(),
                                                strippedLog));
                Block waxedLog = registerBlock("waxed_" + logName,
                                new MoldyLogBlock(AbstractBlock.Settings.copy(vanillaLog).ticksRandomly(),
                                                waxedStrippedLog));
                registerVariant(logName, vanillaLog, log, waxedLog);

                // 2. Planks
                Block vanillaPlanks = Registries.BLOCK.get(Identifier.of(namespace, prefix + "_planks"));
                Block planks = registerBlock("moldy_" + prefix + "_planks",
                                new MoldyPlanksBlock(AbstractBlock.Settings.copy(vanillaPlanks).ticksRandomly()));
                Block waxedPlanks = registerBlock("waxed_" + prefix + "_planks",
                                new MoldyPlanksBlock(AbstractBlock.Settings.copy(vanillaPlanks).ticksRandomly()));
                registerVariant(prefix + "_planks", vanillaPlanks, planks, waxedPlanks);

                // 3. Stairs & Slabs
                Block vanillaStairs = Registries.BLOCK.get(Identifier.of(namespace, prefix + "_stairs"));
                Block stairs = registerBlock("moldy_" + prefix + "_stairs",
                                new MoldyStairsBlock(planks.getDefaultState(),
                                                AbstractBlock.Settings.copy(vanillaPlanks).ticksRandomly()));
                Block waxedStairs = registerBlock("waxed_" + prefix + "_stairs", new MoldyStairsBlock(
                                waxedPlanks.getDefaultState(),
                                AbstractBlock.Settings.copy(vanillaPlanks).ticksRandomly()));
                registerVariant(prefix + "_stairs", vanillaStairs, stairs, waxedStairs);

                Block vanillaSlab = Registries.BLOCK.get(Identifier.of(namespace, prefix + "_slab"));
                Block slab = registerBlock("moldy_" + prefix + "_slab",
                                new MoldySlabBlock(AbstractBlock.Settings.copy(vanillaPlanks).ticksRandomly()));
                Block waxedSlab = registerBlock("waxed_" + prefix + "_slab",
                                new MoldySlabBlock(AbstractBlock.Settings.copy(vanillaPlanks).ticksRandomly()));
                registerVariant(prefix + "_slab", vanillaSlab, slab, waxedSlab);

                // 4. Fences & Gates
                Block vanillaFence = Registries.BLOCK.get(Identifier.of(namespace, prefix + "_fence"));
                Block fence = registerBlock("moldy_" + prefix + "_fence",
                                new MoldyFenceBlock(AbstractBlock.Settings.copy(vanillaFence).ticksRandomly()));
                Block waxedFence = registerBlock("waxed_" + prefix + "_fence",
                                new MoldyFenceBlock(AbstractBlock.Settings.copy(vanillaFence).ticksRandomly()));
                registerVariant(prefix + "_fence", vanillaFence, fence, waxedFence);

                Block vanillaGate = Registries.BLOCK.get(Identifier.of(namespace, prefix + "_fence_gate"));
                Block gate = registerBlock("moldy_" + prefix + "_fence_gate",
                                new MoldyFenceGateBlock(woodType,
                                                AbstractBlock.Settings.copy(vanillaGate).ticksRandomly()));
                Block waxedGate = registerBlock("waxed_" + prefix + "_fence_gate",
                                new MoldyFenceGateBlock(woodType,
                                                AbstractBlock.Settings.copy(vanillaGate).ticksRandomly()));
                registerVariant(prefix + "_fence_gate", vanillaGate, gate, waxedGate);

                // 5. Doors & Trapdoors
                Block vanillaDoor = Registries.BLOCK.get(Identifier.of(namespace, prefix + "_door"));
                Block door = registerBlock("moldy_" + prefix + "_door",
                                new MoldyDoorBlock(setType,
                                                AbstractBlock.Settings.copy(vanillaDoor).ticksRandomly().nonOpaque()));
                Block waxedDoor = registerBlock("waxed_" + prefix + "_door",
                                new MoldyDoorBlock(setType,
                                                AbstractBlock.Settings.copy(vanillaDoor).ticksRandomly().nonOpaque()));
                registerVariant(prefix + "_door", vanillaDoor, door, waxedDoor);

                Block vanillaTrapdoor = Registries.BLOCK.get(Identifier.of(namespace, prefix + "_trapdoor"));
                Block trapdoor = registerBlock("moldy_" + prefix + "_trapdoor", new MoldyTrapdoorBlock(setType,
                                AbstractBlock.Settings.copy(vanillaTrapdoor).ticksRandomly().nonOpaque()));
                Block waxedTrapdoor = registerBlock("waxed_" + prefix + "_trapdoor", new MoldyTrapdoorBlock(setType,
                                AbstractBlock.Settings.copy(vanillaTrapdoor).ticksRandomly().nonOpaque()));
                registerVariant(prefix + "_trapdoor", vanillaTrapdoor, trapdoor, waxedTrapdoor);

                // 6. Buttons & Pressure Plates
                Block vanillaPressurePlate = Registries.BLOCK.get(Identifier.of(namespace, prefix + "_pressure_plate"));
                Block pressurePlate = registerBlock("moldy_" + prefix + "_pressure_plate",
                                new MoldyPressurePlateBlock(setType,
                                                AbstractBlock.Settings.copy(vanillaPressurePlate).ticksRandomly()));
                Block waxedPressurePlate = registerBlock("waxed_" + prefix + "_pressure_plate",
                                new MoldyPressurePlateBlock(setType,
                                                AbstractBlock.Settings.copy(vanillaPressurePlate).ticksRandomly()));
                registerVariant(prefix + "_pressure_plate", vanillaPressurePlate, pressurePlate, waxedPressurePlate);

                Block vanillaButton = Registries.BLOCK.get(Identifier.of(namespace, prefix + "_button"));
                Block button = registerBlock("moldy_" + prefix + "_button",
                                new MoldyButtonBlock(setType, 30,
                                                AbstractBlock.Settings.copy(vanillaButton).ticksRandomly()));
                Block waxedButton = registerBlock("waxed_" + prefix + "_button",
                                new MoldyButtonBlock(setType, 30,
                                                AbstractBlock.Settings.copy(vanillaButton).ticksRandomly()));
                registerVariant(prefix + "_button", vanillaButton, button, waxedButton);

                // 7. Wood / Hyphae (Bark 6-sides)
                if (woodName != null) {
                        Block vanillaStrippedWood = Registries.BLOCK
                                        .get(Identifier.of(namespace, "stripped_" + woodName));
                        Block strippedWood = registerBlock("moldy_stripped_" + woodName,
                                        new MoldyLogBlock(AbstractBlock.Settings.copy(vanillaStrippedWood)
                                                        .ticksRandomly(), null));
                        Block waxedStrippedWood = registerBlock("waxed_stripped_" + woodName,
                                        new MoldyLogBlock(AbstractBlock.Settings.copy(vanillaStrippedWood)
                                                        .ticksRandomly(), null));
                        registerVariant("stripped_" + woodName, vanillaStrippedWood, strippedWood, waxedStrippedWood);

                        Block vanillaWood = Registries.BLOCK.get(Identifier.of(namespace, woodName));
                        Block wood = registerBlock("moldy_" + woodName,
                                        new MoldyLogBlock(AbstractBlock.Settings.copy(vanillaWood).ticksRandomly(),
                                                        strippedWood));
                        Block waxedWood = registerBlock("waxed_" + woodName,
                                        new MoldyLogBlock(AbstractBlock.Settings.copy(vanillaWood).ticksRandomly(),
                                                        waxedStrippedWood));
                        registerVariant(woodName, vanillaWood, wood, waxedWood);
                }

                // 8. Bamboo Mosaic family (unique to bamboo)
                if (moldyWoodType.isBamboo()) {
                        Block vanillaMosaic = Registries.BLOCK.get(Identifier.of(namespace, prefix + "_mosaic"));
                        Block mosaic = registerBlock("moldy_" + prefix + "_mosaic",
                                        new MoldyPlanksBlock(
                                                        AbstractBlock.Settings.copy(vanillaMosaic).ticksRandomly()));
                        Block waxedMosaic = registerBlock("waxed_" + prefix + "_mosaic",
                                        new MoldyPlanksBlock(
                                                        AbstractBlock.Settings.copy(vanillaMosaic).ticksRandomly()));
                        registerVariant(prefix + "_mosaic", vanillaMosaic, mosaic, waxedMosaic);

                        Block vanillaMosaicStairs = Registries.BLOCK
                                        .get(Identifier.of(namespace, prefix + "_mosaic_stairs"));
                        Block mosaicStairs = registerBlock("moldy_" + prefix + "_mosaic_stairs",
                                        new MoldyStairsBlock(mosaic.getDefaultState(),
                                                        AbstractBlock.Settings.copy(vanillaMosaic).ticksRandomly()));
                        Block waxedMosaicStairs = registerBlock("waxed_" + prefix + "_mosaic_stairs",
                                        new MoldyStairsBlock(waxedMosaic.getDefaultState(),
                                                        AbstractBlock.Settings.copy(vanillaMosaic).ticksRandomly()));
                        registerVariant(prefix + "_mosaic_stairs", vanillaMosaicStairs, mosaicStairs,
                                        waxedMosaicStairs);

                        Block vanillaMosaicSlab = Registries.BLOCK
                                        .get(Identifier.of(namespace, prefix + "_mosaic_slab"));
                        Block mosaicSlab = registerBlock("moldy_" + prefix + "_mosaic_slab",
                                        new MoldySlabBlock(AbstractBlock.Settings.copy(vanillaMosaic).ticksRandomly()));
                        Block waxedMosaicSlab = registerBlock("waxed_" + prefix + "_mosaic_slab",
                                        new MoldySlabBlock(AbstractBlock.Settings.copy(vanillaMosaic).ticksRandomly()));
                        registerVariant(prefix + "_mosaic_slab", vanillaMosaicSlab, mosaicSlab, waxedMosaicSlab);
                }

                // 9. Signs & Hanging Signs
                Block vanillaStandingSign = Registries.BLOCK.get(Identifier.of(namespace, prefix + "_sign"));
                Block vanillaWallSign = Registries.BLOCK.get(Identifier.of(namespace, prefix + "_wall_sign"));
                Block moldyStandingSign = registerBlock("moldy_" + prefix + "_sign",
                                new MoldySignBlock(woodType,
                                                AbstractBlock.Settings.copy(vanillaStandingSign).ticksRandomly()));
                Block waxedStandingSign = registerBlock("waxed_" + prefix + "_sign",
                                new MoldySignBlock(woodType,
                                                AbstractBlock.Settings.copy(vanillaStandingSign).ticksRandomly()));
                Block moldyWallSign = registerBlock("moldy_" + prefix + "_wall_sign",
                                new MoldyWallSignBlock(woodType,
                                                AbstractBlock.Settings.copy(vanillaWallSign).ticksRandomly()));
                Block waxedWallSign = registerBlock("waxed_" + prefix + "_wall_sign",
                                new MoldyWallSignBlock(woodType,
                                                AbstractBlock.Settings.copy(vanillaWallSign).ticksRandomly()));

                registerSignVariant(prefix + "_sign", vanillaStandingSign, vanillaWallSign,
                                moldyStandingSign, waxedStandingSign, moldyWallSign, waxedWallSign);

                Block vanillaHangingSign = Registries.BLOCK.get(Identifier.of(namespace, prefix + "_hanging_sign"));
                Block vanillaWallHangingSign = Registries.BLOCK
                                .get(Identifier.of(namespace, prefix + "_wall_hanging_sign"));
                Block moldyHangingSign = registerBlock("moldy_" + prefix + "_hanging_sign",
                                new MoldyHangingSignBlock(woodType,
                                                AbstractBlock.Settings.copy(vanillaHangingSign).ticksRandomly()));
                Block waxedHangingSign = registerBlock("waxed_" + prefix + "_hanging_sign",
                                new MoldyHangingSignBlock(woodType,
                                                AbstractBlock.Settings.copy(vanillaHangingSign).ticksRandomly()));
                Block moldyWallHangingSign = registerBlock("moldy_" + prefix + "_wall_hanging_sign",
                                new MoldyWallHangingSignBlock(woodType,
                                                AbstractBlock.Settings.copy(vanillaWallHangingSign).ticksRandomly()));
                Block waxedWallHangingSign = registerBlock("waxed_" + prefix + "_wall_hanging_sign",
                                new MoldyWallHangingSignBlock(woodType,
                                                AbstractBlock.Settings.copy(vanillaWallHangingSign).ticksRandomly()));

                registerHangingSignVariant(prefix + "_hanging_sign", vanillaHangingSign, vanillaWallHangingSign,
                                moldyHangingSign, waxedHangingSign, moldyWallHangingSign, waxedWallHangingSign);
        }

        private static Block registerBlock(String name, Block block) {
                return Registry.register(Registries.BLOCK, SporesShadows.id(name), block);
        }

        private static void registerSignVariant(String baseName, Block vanillaStanding, Block vanillaWall,
                        Block moldyStanding, Block waxedStanding, Block moldyWall, Block waxedWall) {
                MOLDY_TO_VANILLA.put(moldyStanding, vanillaStanding);
                MOLDY_TO_VANILLA.put(waxedStanding, vanillaStanding);
                VANILLA_TO_MOLDY.put(vanillaStanding, moldyStanding);
                MOLDY_TO_WAXED.put(moldyStanding, waxedStanding);
                WAXED_TO_MOLDY.put(waxedStanding, moldyStanding);

                MOLDY_TO_VANILLA.put(moldyWall, vanillaWall);
                MOLDY_TO_VANILLA.put(waxedWall, vanillaWall);
                VANILLA_TO_MOLDY.put(vanillaWall, moldyWall);
                MOLDY_TO_WAXED.put(moldyWall, waxedWall);
                WAXED_TO_MOLDY.put(waxedWall, moldyWall);

                WALL_TO_STANDING.put(moldyWall, moldyStanding);
                WALL_TO_STANDING.put(waxedWall, waxedStanding);

                MOLDY_SIGNS.add(moldyStanding);
                MOLDY_SIGNS.add(waxedStanding);
                MOLDY_SIGNS.add(moldyWall);
                MOLDY_SIGNS.add(waxedWall);

                Item vanillaItem = vanillaStanding.asItem();
                List<Item> items = new ArrayList<>();

                // Stage 0 (Waxed)
                items.add(registerSignItem("waxed_" + baseName, moldyStanding, moldyWall, waxedStanding, waxedWall, 0,
                                true));

                // Stage 1
                items.add(registerSignItem("tainted_" + baseName, moldyStanding, moldyWall, waxedStanding, waxedWall, 1,
                                false));
                items.add(registerSignItem("waxed_tainted_" + baseName, moldyStanding, moldyWall, waxedStanding,
                                waxedWall, 1, true));

                // Stage 2
                items.add(registerSignItem("moldy_" + baseName, moldyStanding, moldyWall, waxedStanding, waxedWall, 2,
                                false));
                items.add(registerSignItem("waxed_moldy_" + baseName, moldyStanding, moldyWall, waxedStanding,
                                waxedWall, 2, true));

                // Stage 3
                items.add(registerSignItem("rotten_" + baseName, moldyStanding, moldyWall, waxedStanding, waxedWall, 3,
                                false));
                items.add(registerSignItem("waxed_rotten_" + baseName, moldyStanding, moldyWall, waxedStanding,
                                waxedWall, 3, true));

                MOLDY_ITEMS_BY_VANILLA.put(vanillaItem, items);
                MOLDY_ITEMS_BY_BLOCK.put(moldyStanding, items);
                MOLDY_ITEMS_BY_BLOCK.put(waxedStanding, items);
                MOLDY_ITEMS_BY_BLOCK.put(moldyWall, items);
                MOLDY_ITEMS_BY_BLOCK.put(waxedWall, items);
        }

        private static Item registerSignItem(String name, Block moldyStanding, Block moldyWall,
                        Block waxedStanding, Block waxedWall, int stage, boolean isWaxed) {
                Item.Settings settings = new Item.Settings().maxCount(16).component(
                                DataComponentTypes.BLOCK_STATE,
                                BlockStateComponent.DEFAULT.with(MoldyBlock.STAGE, stage).with(MoldyBlock.WAXED,
                                                isWaxed));
                Block standingBlock = isWaxed ? waxedStanding : moldyStanding;
                Block wallBlock = isWaxed ? waxedWall : moldyWall;
                Item item = new SignItem(settings, standingBlock, wallBlock) {
                        @Override
                        public String getTranslationKey() {
                                return "item." + SporesShadows.MOD_ID + "." + name;
                        }

                        @Override
                        public void appendTooltip(ItemStack stack, Item.TooltipContext context,
                                        List<Text> tooltip, TooltipType type) {
                                super.appendTooltip(stack, context, tooltip, type);
                                appendMoldyTooltip(name, stack, tooltip);
                        }
                };
                return Registry.register(Registries.ITEM, SporesShadows.id(name), item);
        }

        private static void registerHangingSignVariant(String baseName, Block vanillaHanging, Block vanillaWallHanging,
                        Block moldyHanging, Block waxedHanging, Block moldyWallHanging, Block waxedWallHanging) {
                MOLDY_TO_VANILLA.put(moldyHanging, vanillaHanging);
                MOLDY_TO_VANILLA.put(waxedHanging, vanillaHanging);
                VANILLA_TO_MOLDY.put(vanillaHanging, moldyHanging);
                MOLDY_TO_WAXED.put(moldyHanging, waxedHanging);
                WAXED_TO_MOLDY.put(waxedHanging, moldyHanging);

                MOLDY_TO_VANILLA.put(moldyWallHanging, vanillaWallHanging);
                MOLDY_TO_VANILLA.put(waxedWallHanging, vanillaWallHanging);
                VANILLA_TO_MOLDY.put(vanillaWallHanging, moldyWallHanging);
                MOLDY_TO_WAXED.put(moldyWallHanging, waxedWallHanging);
                WAXED_TO_MOLDY.put(waxedWallHanging, moldyWallHanging);

                WALL_TO_STANDING.put(moldyWallHanging, moldyHanging);
                WALL_TO_STANDING.put(waxedWallHanging, waxedHanging);

                MOLDY_HANGING_SIGNS.add(moldyHanging);
                MOLDY_HANGING_SIGNS.add(waxedHanging);
                MOLDY_HANGING_SIGNS.add(moldyWallHanging);
                MOLDY_HANGING_SIGNS.add(waxedWallHanging);

                Item vanillaItem = vanillaHanging.asItem();
                List<Item> items = new ArrayList<>();

                // Stage 0 (Waxed)
                items.add(registerHangingSignItem("waxed_" + baseName, moldyHanging, moldyWallHanging, waxedHanging,
                                waxedWallHanging, 0, true));

                // Stage 1
                items.add(registerHangingSignItem("tainted_" + baseName, moldyHanging, moldyWallHanging, waxedHanging,
                                waxedWallHanging, 1, false));
                items.add(registerHangingSignItem("waxed_tainted_" + baseName, moldyHanging, moldyWallHanging,
                                waxedHanging, waxedWallHanging, 1, true));

                // Stage 2
                items.add(registerHangingSignItem("moldy_" + baseName, moldyHanging, moldyWallHanging, waxedHanging,
                                waxedWallHanging, 2, false));
                items.add(registerHangingSignItem("waxed_moldy_" + baseName, moldyHanging, moldyWallHanging,
                                waxedHanging, waxedWallHanging, 2, true));

                // Stage 3
                items.add(registerHangingSignItem("rotten_" + baseName, moldyHanging, moldyWallHanging, waxedHanging,
                                waxedWallHanging, 3, false));
                items.add(registerHangingSignItem("waxed_rotten_" + baseName, moldyHanging, moldyWallHanging,
                                waxedHanging, waxedWallHanging, 3, true));

                MOLDY_ITEMS_BY_VANILLA.put(vanillaItem, items);
                MOLDY_ITEMS_BY_BLOCK.put(moldyHanging, items);
                MOLDY_ITEMS_BY_BLOCK.put(waxedHanging, items);
                MOLDY_ITEMS_BY_BLOCK.put(moldyWallHanging, items);
                MOLDY_ITEMS_BY_BLOCK.put(waxedWallHanging, items);
        }

        private static Item registerHangingSignItem(String name, Block moldyHanging, Block moldyWallHanging,
                        Block waxedHanging, Block waxedWallHanging, int stage, boolean isWaxed) {
                Item.Settings settings = new Item.Settings().maxCount(16).component(
                                DataComponentTypes.BLOCK_STATE,
                                BlockStateComponent.DEFAULT.with(MoldyBlock.STAGE, stage).with(MoldyBlock.WAXED,
                                                isWaxed));
                Block hangingBlock = isWaxed ? waxedHanging : moldyHanging;
                Block wallHangingBlock = isWaxed ? waxedWallHanging : moldyWallHanging;
                Item item = new HangingSignItem(hangingBlock, wallHangingBlock, settings) {
                        @Override
                        public String getTranslationKey() {
                                return "item." + SporesShadows.MOD_ID + "." + name;
                        }

                        @Override
                        public void appendTooltip(ItemStack stack, Item.TooltipContext context,
                                        List<Text> tooltip, TooltipType type) {
                                super.appendTooltip(stack, context, tooltip, type);
                                appendMoldyTooltip(name, stack, tooltip);
                        }
                };
                return Registry.register(Registries.ITEM, SporesShadows.id(name), item);
        }

        private static void registerVariant(String baseName, Block vanillaBlock, Block moldyBlock, Block waxedBlock) {
                MOLDY_TO_VANILLA.put(moldyBlock, vanillaBlock);
                MOLDY_TO_VANILLA.put(waxedBlock, vanillaBlock);
                VANILLA_TO_MOLDY.put(vanillaBlock, moldyBlock);
                MOLDY_TO_WAXED.put(moldyBlock, waxedBlock);
                WAXED_TO_MOLDY.put(waxedBlock, moldyBlock);

                Item vanillaItem = vanillaBlock.asItem();
                List<Item> items = new ArrayList<>();

                // Stage 0 (Waxed)
                items.add(registerStageItem("waxed_" + baseName, waxedBlock, 0, true));

                // Stage 1
                items.add(registerStageItem("tainted_" + baseName, moldyBlock, 1, false));
                items.add(registerStageItem("waxed_tainted_" + baseName, waxedBlock, 1, true));

                // Stage 2
                items.add(registerStageItem("moldy_" + baseName, moldyBlock, 2, false));
                items.add(registerStageItem("waxed_moldy_" + baseName, waxedBlock, 2, true));

                // Stage 3
                items.add(registerStageItem("rotten_" + baseName, moldyBlock, 3, false));
                items.add(registerStageItem("waxed_rotten_" + baseName, waxedBlock, 3, true));

                MOLDY_ITEMS_BY_VANILLA.put(vanillaItem, items);
                MOLDY_ITEMS_BY_BLOCK.put(moldyBlock, items);
                MOLDY_ITEMS_BY_BLOCK.put(waxedBlock, items);
        }

        private static Item registerStageItem(String name, Block baseBlock, int stage, boolean isWaxed) {
                Item.Settings settings = new Item.Settings().component(
                                DataComponentTypes.BLOCK_STATE,
                                BlockStateComponent.DEFAULT.with(MoldyBlock.STAGE, stage).with(MoldyBlock.WAXED,
                                                isWaxed));
                Item item;
                if (baseBlock instanceof DoorBlock) {
                        item = new TallBlockItem(baseBlock, settings) {
                                @Override
                                public String getTranslationKey() {
                                        return "item." + SporesShadows.MOD_ID + "." + name;
                                }

                                @Override
                                public void appendTooltip(ItemStack stack, Item.TooltipContext context,
                                                List<Text> tooltip, TooltipType type) {
                                        super.appendTooltip(stack, context, tooltip, type);
                                        appendMoldyTooltip(name, stack, tooltip);
                                }

                        };
                } else {
                        item = new BlockItem(baseBlock, settings) {
                                @Override
                                public String getTranslationKey() {
                                        return "item." + SporesShadows.MOD_ID + "." + name;
                                }

                                @Override
                                public void appendTooltip(ItemStack stack, Item.TooltipContext context,
                                                List<Text> tooltip, TooltipType type) {
                                        super.appendTooltip(stack, context, tooltip, type);
                                        appendMoldyTooltip(name, stack, tooltip);
                                }

                        };
                }
                return Registry.register(Registries.ITEM, SporesShadows.id(name), item);
        }

        private static void appendMoldyTooltip(String name, ItemStack stack, List<Text> tooltip) {
                if (name.contains("log") || name.contains("wood")) {
                        tooltip.add(Text.translatable("tooltip." + SporesShadows.MOD_ID + ".moldy_log_desc_1")
                                        .formatted(Formatting.GRAY));
                        tooltip.add(Text.translatable("tooltip." + SporesShadows.MOD_ID + ".moldy_log_desc_2")
                                        .formatted(Formatting.GRAY));
                } else if (name.contains("planks")) {
                        tooltip.add(Text.translatable("tooltip." + SporesShadows.MOD_ID + ".moldy_planks_desc_1")
                                        .formatted(Formatting.GRAY));
                        tooltip.add(Text.translatable("tooltip." + SporesShadows.MOD_ID + ".moldy_planks_desc_2")
                                        .formatted(Formatting.GRAY));
                } else if (name.contains("button") || name.contains("pressure_plate")) {
                        tooltip.add(Text.translatable("tooltip." + SporesShadows.MOD_ID + ".moldy_redstone_desc_1")
                                        .formatted(Formatting.GRAY));
                        tooltip.add(Text.translatable("tooltip." + SporesShadows.MOD_ID + ".moldy_redstone_desc_2")
                                        .formatted(Formatting.GRAY));
                } else if (name.contains("chiseled_bookshelf")) {
                        tooltip.add(Text.translatable(
                                        "tooltip." + SporesShadows.MOD_ID + ".moldy_chiseled_bookshelf_desc_1")
                                        .formatted(Formatting.GRAY));
                        tooltip.add(Text.translatable(
                                        "tooltip." + SporesShadows.MOD_ID + ".moldy_chiseled_bookshelf_desc_2")
                                        .formatted(Formatting.GRAY));
                } else if (name.contains("bookshelf")) {
                        tooltip.add(Text.translatable("tooltip." + SporesShadows.MOD_ID + ".moldy_bookshelf_desc_1")
                                        .formatted(Formatting.GRAY));
                        tooltip.add(Text.translatable("tooltip." + SporesShadows.MOD_ID + ".moldy_bookshelf_desc_2")
                                        .formatted(Formatting.GRAY));
                } else if (name.contains("note_block")) {
                        tooltip.add(Text.translatable("tooltip." + SporesShadows.MOD_ID + ".moldy_note_block_desc_1")
                                        .formatted(Formatting.GRAY));
                        tooltip.add(Text.translatable("tooltip." + SporesShadows.MOD_ID + ".moldy_note_block_desc_2")
                                        .formatted(Formatting.GRAY));
                } else if (name.contains("jukebox")) {
                        tooltip.add(Text.translatable("tooltip." + SporesShadows.MOD_ID + ".moldy_jukebox_desc_1")
                                        .formatted(Formatting.GRAY));
                        tooltip.add(Text.translatable("tooltip." + SporesShadows.MOD_ID + ".moldy_jukebox_desc_2")
                                        .formatted(Formatting.GRAY));
                } else if (name.contains("ladder")) {
                        tooltip.add(Text.translatable("tooltip." + SporesShadows.MOD_ID + ".moldy_ladder_desc_1")
                                        .formatted(Formatting.GRAY));
                        tooltip.add(Text.translatable("tooltip." + SporesShadows.MOD_ID + ".moldy_ladder_desc_2")
                                        .formatted(Formatting.GRAY));
                } else {
                        tooltip.add(Text.translatable("tooltip." + SporesShadows.MOD_ID + ".moldy_general_desc_1")
                                        .formatted(Formatting.GRAY));
                        tooltip.add(Text.translatable("tooltip." + SporesShadows.MOD_ID + ".moldy_general_desc_2")
                                        .formatted(Formatting.GRAY));
                }
        }
}
