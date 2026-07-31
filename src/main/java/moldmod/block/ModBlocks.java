package moldmod.block;

import moldmod.SporesShadows;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.PillarBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroups;

public class ModBlocks {

    public static final Block MOLDY_OAK_LOG_STAGE_3 = registerBlock("moldy_oak_log_stage_3",
            new MoldyOakLogBlock(AbstractBlock.Settings.copy(Blocks.OAK_LOG).ticksRandomly(), null));

    public static final Block MOLDY_OAK_LOG_STAGE_2 = registerBlock("moldy_oak_log_stage_2",
            new MoldyOakLogBlock(AbstractBlock.Settings.copy(Blocks.OAK_LOG).ticksRandomly(), () -> MOLDY_OAK_LOG_STAGE_3));

    public static final Block MOLDY_OAK_LOG_STAGE_1 = registerBlock("moldy_oak_log_stage_1",
            new MoldyOakLogBlock(AbstractBlock.Settings.copy(Blocks.OAK_LOG).ticksRandomly(), () -> MOLDY_OAK_LOG_STAGE_2));

    public static final Block PLACED_OAK_LOG = registerBlock("placed_oak_log",
            new PlacedOakLogBlock(AbstractBlock.Settings.copy(Blocks.OAK_LOG).ticksRandomly()));

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, SporesShadows.id(name), block);
    }

    private static Item registerBlockItem(String name, Block block) {
        return Registry.register(Registries.ITEM, SporesShadows.id(name),
                new BlockItem(block, new Item.Settings()));
    }

    public static void registerModBlocks() {
        SporesShadows.LOGGER.info("Registering ModBlocks for " + SporesShadows.MOD_ID);

        // Aggiunge i blocchi al menu della creativa (Scheda: Blocchi Naturali)
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.NATURAL).register(entries -> {
            entries.add(MOLDY_OAK_LOG_STAGE_1);
            entries.add(MOLDY_OAK_LOG_STAGE_2);
            entries.add(MOLDY_OAK_LOG_STAGE_3);
        });
    }
}
