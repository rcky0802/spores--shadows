package moldmod.block.registry;

import moldmod.SporesShadows;
import moldmod.block.ModBlocks;
import moldmod.block.core.MoldyBlock;
import net.minecraft.block.Block;
import net.minecraft.block.DoorBlock;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BlockStateComponent;
import net.minecraft.item.BlockItem;
import net.minecraft.item.HangingSignItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SignItem;
import net.minecraft.item.TallBlockItem;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

public final class BlockRegistryHelper {

    private BlockRegistryHelper() {}

    public static Block registerBlock(String name, Block block) {
        return Registry.register(Registries.BLOCK, SporesShadows.id(name), block);
    }

    public static void registerVariant(String baseName, Block vanillaBlock, Block moldyBlock, Block waxedBlock) {
        ModBlocks.MOLDY_TO_VANILLA.put(moldyBlock, vanillaBlock);
        ModBlocks.MOLDY_TO_VANILLA.put(waxedBlock, vanillaBlock);
        ModBlocks.VANILLA_TO_MOLDY.put(vanillaBlock, moldyBlock);
        ModBlocks.MOLDY_TO_WAXED.put(moldyBlock, waxedBlock);
        ModBlocks.WAXED_TO_MOLDY.put(waxedBlock, moldyBlock);

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

        ModBlocks.MOLDY_ITEMS_BY_VANILLA.put(vanillaItem, items);
        ModBlocks.MOLDY_ITEMS_BY_BLOCK.put(moldyBlock, items);
        ModBlocks.MOLDY_ITEMS_BY_BLOCK.put(waxedBlock, items);
    }

    public static Item registerStageItem(String name, Block baseBlock, int stage, boolean isWaxed) {
        Item.Settings settings = new Item.Settings().component(
                DataComponentTypes.BLOCK_STATE,
                BlockStateComponent.DEFAULT.with(MoldyBlock.STAGE, stage).with(MoldyBlock.WAXED, isWaxed));
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

    public static void registerSignVariant(String baseName, Block vanillaStanding, Block vanillaWall,
                    Block moldyStanding, Block waxedStanding, Block moldyWall, Block waxedWall) {
        ModBlocks.MOLDY_TO_VANILLA.put(moldyStanding, vanillaStanding);
        ModBlocks.MOLDY_TO_VANILLA.put(waxedStanding, vanillaStanding);
        ModBlocks.VANILLA_TO_MOLDY.put(vanillaStanding, moldyStanding);
        ModBlocks.MOLDY_TO_WAXED.put(moldyStanding, waxedStanding);
        ModBlocks.WAXED_TO_MOLDY.put(waxedStanding, moldyStanding);

        ModBlocks.MOLDY_TO_VANILLA.put(moldyWall, vanillaWall);
        ModBlocks.MOLDY_TO_VANILLA.put(waxedWall, vanillaWall);
        ModBlocks.VANILLA_TO_MOLDY.put(vanillaWall, moldyWall);
        ModBlocks.MOLDY_TO_WAXED.put(moldyWall, waxedWall);
        ModBlocks.WAXED_TO_MOLDY.put(waxedWall, moldyWall);

        ModBlocks.WALL_TO_STANDING.put(moldyWall, moldyStanding);
        ModBlocks.WALL_TO_STANDING.put(waxedWall, waxedStanding);

        ModBlocks.MOLDY_SIGNS.add(moldyStanding);
        ModBlocks.MOLDY_SIGNS.add(waxedStanding);
        ModBlocks.MOLDY_SIGNS.add(moldyWall);
        ModBlocks.MOLDY_SIGNS.add(waxedWall);

        Item vanillaItem = vanillaStanding.asItem();
        List<Item> items = new ArrayList<>();

        // Stage 0 (Waxed)
        items.add(registerSignItem("waxed_" + baseName, moldyStanding, moldyWall, waxedStanding, waxedWall, 0, true));

        // Stage 1
        items.add(registerSignItem("tainted_" + baseName, moldyStanding, moldyWall, waxedStanding, waxedWall, 1, false));
        items.add(registerSignItem("waxed_tainted_" + baseName, moldyStanding, moldyWall, waxedStanding, waxedWall, 1, true));

        // Stage 2
        items.add(registerSignItem("moldy_" + baseName, moldyStanding, moldyWall, waxedStanding, waxedWall, 2, false));
        items.add(registerSignItem("waxed_moldy_" + baseName, moldyStanding, moldyWall, waxedStanding, waxedWall, 2, true));

        // Stage 3
        items.add(registerSignItem("rotten_" + baseName, moldyStanding, moldyWall, waxedStanding, waxedWall, 3, false));
        items.add(registerSignItem("waxed_rotten_" + baseName, moldyStanding, moldyWall, waxedStanding, waxedWall, 3, true));

        ModBlocks.MOLDY_ITEMS_BY_VANILLA.put(vanillaItem, items);
        ModBlocks.MOLDY_ITEMS_BY_BLOCK.put(moldyStanding, items);
        ModBlocks.MOLDY_ITEMS_BY_BLOCK.put(waxedStanding, items);
        ModBlocks.MOLDY_ITEMS_BY_BLOCK.put(moldyWall, items);
        ModBlocks.MOLDY_ITEMS_BY_BLOCK.put(waxedWall, items);
    }

    public static Item registerSignItem(String name, Block moldyStanding, Block moldyWall,
                    Block waxedStanding, Block waxedWall, int stage, boolean isWaxed) {
        Item.Settings settings = new Item.Settings().maxCount(16).component(
                DataComponentTypes.BLOCK_STATE,
                BlockStateComponent.DEFAULT.with(MoldyBlock.STAGE, stage).with(MoldyBlock.WAXED, isWaxed));
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

    public static void registerHangingSignVariant(String baseName, Block vanillaHanging, Block vanillaWallHanging,
                    Block moldyHanging, Block waxedHanging, Block moldyWallHanging, Block waxedWallHanging) {
        ModBlocks.MOLDY_TO_VANILLA.put(moldyHanging, vanillaHanging);
        ModBlocks.MOLDY_TO_VANILLA.put(waxedHanging, vanillaHanging);
        ModBlocks.VANILLA_TO_MOLDY.put(vanillaHanging, moldyHanging);
        ModBlocks.MOLDY_TO_WAXED.put(moldyHanging, waxedHanging);
        ModBlocks.WAXED_TO_MOLDY.put(waxedHanging, moldyHanging);

        ModBlocks.MOLDY_TO_VANILLA.put(moldyWallHanging, vanillaWallHanging);
        ModBlocks.MOLDY_TO_VANILLA.put(waxedWallHanging, vanillaWallHanging);
        ModBlocks.VANILLA_TO_MOLDY.put(vanillaWallHanging, moldyWallHanging);
        ModBlocks.MOLDY_TO_WAXED.put(moldyWallHanging, waxedWallHanging);
        ModBlocks.WAXED_TO_MOLDY.put(waxedWallHanging, moldyWallHanging);

        ModBlocks.WALL_TO_STANDING.put(moldyWallHanging, moldyHanging);
        ModBlocks.WALL_TO_STANDING.put(waxedWallHanging, waxedHanging);

        ModBlocks.MOLDY_HANGING_SIGNS.add(moldyHanging);
        ModBlocks.MOLDY_HANGING_SIGNS.add(waxedHanging);
        ModBlocks.MOLDY_HANGING_SIGNS.add(moldyWallHanging);
        ModBlocks.MOLDY_HANGING_SIGNS.add(waxedWallHanging);

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

        ModBlocks.MOLDY_ITEMS_BY_VANILLA.put(vanillaItem, items);
        ModBlocks.MOLDY_ITEMS_BY_BLOCK.put(moldyHanging, items);
        ModBlocks.MOLDY_ITEMS_BY_BLOCK.put(waxedHanging, items);
        ModBlocks.MOLDY_ITEMS_BY_BLOCK.put(moldyWallHanging, items);
        ModBlocks.MOLDY_ITEMS_BY_BLOCK.put(waxedWallHanging, items);
    }

    public static Item registerHangingSignItem(String name, Block moldyHanging, Block moldyWallHanging,
                    Block waxedHanging, Block waxedWallHanging, int stage, boolean isWaxed) {
        Item.Settings settings = new Item.Settings().maxCount(16).component(
                DataComponentTypes.BLOCK_STATE,
                BlockStateComponent.DEFAULT.with(MoldyBlock.STAGE, stage).with(MoldyBlock.WAXED, isWaxed));
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

    public static void appendMoldyTooltip(String name, ItemStack stack, List<Text> tooltip) {
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
            tooltip.add(Text.translatable("tooltip." + SporesShadows.MOD_ID + ".moldy_chiseled_bookshelf_desc_1")
                    .formatted(Formatting.GRAY));
            tooltip.add(Text.translatable("tooltip." + SporesShadows.MOD_ID + ".moldy_chiseled_bookshelf_desc_2")
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
