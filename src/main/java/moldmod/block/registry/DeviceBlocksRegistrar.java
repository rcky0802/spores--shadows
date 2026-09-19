package moldmod.block.registry;

import moldmod.SporesShadows;
import moldmod.block.machine.dehumidifier.DehumidifierBlock;
import moldmod.block.machine.dehumidifier.DehumidifierStatus;
import moldmod.block.machine.purifier.AirPurifierBlock;
import moldmod.block.machine.purifier.PurifierStatus;
import moldmod.block.sensor.MoistureDetectorBlock;
import moldmod.block.sensor.SporeDetectorBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;

public final class DeviceBlocksRegistrar {

    private DeviceBlocksRegistrar() {}

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
            new DehumidifierBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK)
                    .strength(3.5f)
                    .sounds(BlockSoundGroup.COPPER)
                    .luminance(state -> state.get(DehumidifierBlock.STATUS) == DehumidifierStatus.RUNNING ? 7 : 0)));

    public static final Item DEHUMIDIFIER_ITEM = Registry.register(
            Registries.ITEM,
            SporesShadows.id("dehumidifier"),
            new BlockItem(DEHUMIDIFIER, new Item.Settings()));

    public static final Block AIR_PURIFIER = Registry.register(
            Registries.BLOCK,
            SporesShadows.id("air_purifier"),
            new AirPurifierBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK)
                    .strength(3.5f)
                    .sounds(BlockSoundGroup.COPPER)
                    .luminance(state -> state.get(AirPurifierBlock.STATUS) == PurifierStatus.RUNNING ? 7 : 0)));

    public static final Item AIR_PURIFIER_ITEM = Registry.register(
            Registries.ITEM,
            SporesShadows.id("air_purifier"),
            new BlockItem(AIR_PURIFIER, new Item.Settings()));

    public static void registerAll() {
        // Triggers classloading and static registration
    }
}
