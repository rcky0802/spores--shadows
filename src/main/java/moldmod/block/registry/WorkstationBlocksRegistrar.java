package moldmod.block.registry;

import moldmod.block.ModBlocks;
import moldmod.block.redstone.MoldyJukeboxBlock;
import moldmod.block.redstone.MoldyNoteBlock;
import moldmod.block.wood.MoldyLadderBlock;
import moldmod.block.workstation.MoldyBarrelBlock;
import moldmod.block.workstation.MoldyBookshelfBlock;
import moldmod.block.workstation.MoldyCartographyTableBlock;
import moldmod.block.workstation.MoldyChestBlock;
import moldmod.block.workstation.MoldyChiseledBookshelfBlock;
import moldmod.block.workstation.MoldyComposterBlock;
import moldmod.block.workstation.MoldyCraftingTableBlock;
import moldmod.block.workstation.MoldyFletchingTableBlock;
import moldmod.block.workstation.MoldyLecternBlock;
import moldmod.block.workstation.MoldyLoomBlock;
import moldmod.block.workstation.MoldyTrappedChestBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;

import static moldmod.block.registry.BlockRegistryHelper.registerBlock;
import static moldmod.block.registry.BlockRegistryHelper.registerVariant;

public final class WorkstationBlocksRegistrar {

    private WorkstationBlocksRegistrar() {}

    public static void registerAll() {
        // Bookshelves
        ModBlocks.MOLDY_BOOKSHELF = registerBlock("moldy_bookshelf",
                new MoldyBookshelfBlock(AbstractBlock.Settings.copy(Blocks.BOOKSHELF).ticksRandomly()));
        ModBlocks.WAXED_BOOKSHELF = registerBlock("waxed_bookshelf",
                new MoldyBookshelfBlock(AbstractBlock.Settings.copy(Blocks.BOOKSHELF).ticksRandomly()));
        registerVariant("bookshelf", Blocks.BOOKSHELF, ModBlocks.MOLDY_BOOKSHELF, ModBlocks.WAXED_BOOKSHELF);

        // Chiseled Bookshelves
        ModBlocks.MOLDY_CHISELED_BOOKSHELF = registerBlock("moldy_chiseled_bookshelf",
                new MoldyChiseledBookshelfBlock(AbstractBlock.Settings.copy(Blocks.CHISELED_BOOKSHELF).ticksRandomly()));
        ModBlocks.WAXED_CHISELED_BOOKSHELF = registerBlock("waxed_chiseled_bookshelf",
                new MoldyChiseledBookshelfBlock(AbstractBlock.Settings.copy(Blocks.CHISELED_BOOKSHELF).ticksRandomly()));
        registerVariant("chiseled_bookshelf", Blocks.CHISELED_BOOKSHELF, ModBlocks.MOLDY_CHISELED_BOOKSHELF,
                ModBlocks.WAXED_CHISELED_BOOKSHELF);

        // Ladders
        ModBlocks.MOLDY_LADDER = registerBlock("moldy_ladder",
                new MoldyLadderBlock(AbstractBlock.Settings.copy(Blocks.LADDER).ticksRandomly()));
        ModBlocks.WAXED_LADDER = registerBlock("waxed_ladder",
                new MoldyLadderBlock(AbstractBlock.Settings.copy(Blocks.LADDER).ticksRandomly()));
        registerVariant("ladder", Blocks.LADDER, ModBlocks.MOLDY_LADDER, ModBlocks.WAXED_LADDER);

        // Note Blocks
        ModBlocks.MOLDY_NOTE_BLOCK = registerBlock("moldy_note_block",
                new MoldyNoteBlock(AbstractBlock.Settings.copy(Blocks.NOTE_BLOCK).ticksRandomly()));
        ModBlocks.WAXED_NOTE_BLOCK = registerBlock("waxed_note_block",
                new MoldyNoteBlock(AbstractBlock.Settings.copy(Blocks.NOTE_BLOCK).ticksRandomly()));
        registerVariant("note_block", Blocks.NOTE_BLOCK, ModBlocks.MOLDY_NOTE_BLOCK, ModBlocks.WAXED_NOTE_BLOCK);

        // Jukeboxes
        ModBlocks.MOLDY_JUKEBOX = registerBlock("moldy_jukebox",
                new MoldyJukeboxBlock(AbstractBlock.Settings.copy(Blocks.JUKEBOX).ticksRandomly()));
        ModBlocks.WAXED_JUKEBOX = registerBlock("waxed_jukebox",
                new MoldyJukeboxBlock(AbstractBlock.Settings.copy(Blocks.JUKEBOX).ticksRandomly()));
        registerVariant("jukebox", Blocks.JUKEBOX, ModBlocks.MOLDY_JUKEBOX, ModBlocks.WAXED_JUKEBOX);

        // Crafting Tables
        ModBlocks.MOLDY_CRAFTING_TABLE = registerBlock("moldy_crafting_table",
                new MoldyCraftingTableBlock(AbstractBlock.Settings.copy(Blocks.CRAFTING_TABLE).ticksRandomly()));
        ModBlocks.WAXED_CRAFTING_TABLE = registerBlock("waxed_crafting_table",
                new MoldyCraftingTableBlock(AbstractBlock.Settings.copy(Blocks.CRAFTING_TABLE).ticksRandomly()));
        registerVariant("crafting_table", Blocks.CRAFTING_TABLE, ModBlocks.MOLDY_CRAFTING_TABLE, ModBlocks.WAXED_CRAFTING_TABLE);

        // Barrels
        ModBlocks.MOLDY_BARREL = registerBlock("moldy_barrel",
                new MoldyBarrelBlock(AbstractBlock.Settings.copy(Blocks.BARREL).ticksRandomly()));
        ModBlocks.WAXED_BARREL = registerBlock("waxed_barrel",
                new MoldyBarrelBlock(AbstractBlock.Settings.copy(Blocks.BARREL).ticksRandomly()));
        registerVariant("barrel", Blocks.BARREL, ModBlocks.MOLDY_BARREL, ModBlocks.WAXED_BARREL);

        // Chests
        ModBlocks.MOLDY_CHEST = registerBlock("moldy_chest",
                new MoldyChestBlock(AbstractBlock.Settings.copy(Blocks.CHEST).ticksRandomly()));
        ModBlocks.WAXED_CHEST = registerBlock("waxed_chest",
                new MoldyChestBlock(AbstractBlock.Settings.copy(Blocks.CHEST).ticksRandomly()));
        registerVariant("chest", Blocks.CHEST, ModBlocks.MOLDY_CHEST, ModBlocks.WAXED_CHEST);

        // Trapped Chests
        ModBlocks.MOLDY_TRAPPED_CHEST = registerBlock("moldy_trapped_chest",
                new MoldyTrappedChestBlock(AbstractBlock.Settings.copy(Blocks.TRAPPED_CHEST).ticksRandomly()));
        ModBlocks.WAXED_TRAPPED_CHEST = registerBlock("waxed_trapped_chest",
                new MoldyTrappedChestBlock(AbstractBlock.Settings.copy(Blocks.TRAPPED_CHEST).ticksRandomly()));
        registerVariant("trapped_chest", Blocks.TRAPPED_CHEST, ModBlocks.MOLDY_TRAPPED_CHEST, ModBlocks.WAXED_TRAPPED_CHEST);

        // Composters
        ModBlocks.MOLDY_COMPOSTER = registerBlock("moldy_composter",
                new MoldyComposterBlock(AbstractBlock.Settings.copy(Blocks.COMPOSTER).ticksRandomly()));
        ModBlocks.WAXED_COMPOSTER = registerBlock("waxed_composter",
                new MoldyComposterBlock(AbstractBlock.Settings.copy(Blocks.COMPOSTER).ticksRandomly()));
        registerVariant("composter", Blocks.COMPOSTER, ModBlocks.MOLDY_COMPOSTER, ModBlocks.WAXED_COMPOSTER);

        // Fletching Tables
        ModBlocks.MOLDY_FLETCHING_TABLE = registerBlock("moldy_fletching_table",
                new MoldyFletchingTableBlock(AbstractBlock.Settings.copy(Blocks.FLETCHING_TABLE).ticksRandomly()));
        ModBlocks.WAXED_FLETCHING_TABLE = registerBlock("waxed_fletching_table",
                new MoldyFletchingTableBlock(AbstractBlock.Settings.copy(Blocks.FLETCHING_TABLE).ticksRandomly()));
        registerVariant("fletching_table", Blocks.FLETCHING_TABLE, ModBlocks.MOLDY_FLETCHING_TABLE, ModBlocks.WAXED_FLETCHING_TABLE);

        // Cartography Tables
        ModBlocks.MOLDY_CARTOGRAPHY_TABLE = registerBlock("moldy_cartography_table",
                new MoldyCartographyTableBlock(AbstractBlock.Settings.copy(Blocks.CARTOGRAPHY_TABLE).ticksRandomly()));
        ModBlocks.WAXED_CARTOGRAPHY_TABLE = registerBlock("waxed_cartography_table",
                new MoldyCartographyTableBlock(AbstractBlock.Settings.copy(Blocks.CARTOGRAPHY_TABLE).ticksRandomly()));
        registerVariant("cartography_table", Blocks.CARTOGRAPHY_TABLE, ModBlocks.MOLDY_CARTOGRAPHY_TABLE, ModBlocks.WAXED_CARTOGRAPHY_TABLE);

        // Looms
        ModBlocks.MOLDY_LOOM = registerBlock("moldy_loom",
                new MoldyLoomBlock(AbstractBlock.Settings.copy(Blocks.LOOM).ticksRandomly()));
        ModBlocks.WAXED_LOOM = registerBlock("waxed_loom",
                new MoldyLoomBlock(AbstractBlock.Settings.copy(Blocks.LOOM).ticksRandomly()));
        registerVariant("loom", Blocks.LOOM, ModBlocks.MOLDY_LOOM, ModBlocks.WAXED_LOOM);

        // Lecterns
        ModBlocks.MOLDY_LECTERN = registerBlock("moldy_lectern",
                new MoldyLecternBlock(AbstractBlock.Settings.copy(Blocks.LECTERN).ticksRandomly()));
        ModBlocks.WAXED_LECTERN = registerBlock("waxed_lectern",
                new MoldyLecternBlock(AbstractBlock.Settings.copy(Blocks.LECTERN).ticksRandomly()));
        registerVariant("lectern", Blocks.LECTERN, ModBlocks.MOLDY_LECTERN, ModBlocks.WAXED_LECTERN);
    }
}
