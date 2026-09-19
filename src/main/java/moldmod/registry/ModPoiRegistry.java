package moldmod.registry;

import moldmod.block.ModBlocks;
import moldmod.mixin.world.PointOfInterestTypesAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.poi.PointOfInterestType;
import net.minecraft.world.poi.PointOfInterestTypes;

import java.util.Map;

public final class ModPoiRegistry {

    private ModPoiRegistry() {}

    public static void register() {
        Map<BlockState, RegistryEntry<PointOfInterestType>> map = PointOfInterestTypesAccessor.getPoiStatesToType();
        Registry<PointOfInterestType> registry = Registries.POINT_OF_INTEREST_TYPE;

        registerBlockStates(map, registry, PointOfInterestTypes.FISHERMAN, ModBlocks.MOLDY_BARREL, ModBlocks.WAXED_BARREL);
        registerBlockStates(map, registry, PointOfInterestTypes.FARMER, ModBlocks.MOLDY_COMPOSTER, ModBlocks.WAXED_COMPOSTER);
        registerBlockStates(map, registry, PointOfInterestTypes.FLETCHER, ModBlocks.MOLDY_FLETCHING_TABLE, ModBlocks.WAXED_FLETCHING_TABLE);
        registerBlockStates(map, registry, PointOfInterestTypes.CARTOGRAPHER, ModBlocks.MOLDY_CARTOGRAPHY_TABLE, ModBlocks.WAXED_CARTOGRAPHY_TABLE);
        registerBlockStates(map, registry, PointOfInterestTypes.SHEPHERD, ModBlocks.MOLDY_LOOM, ModBlocks.WAXED_LOOM);
        registerBlockStates(map, registry, PointOfInterestTypes.LIBRARIAN, ModBlocks.MOLDY_LECTERN, ModBlocks.WAXED_LECTERN);
    }

    private static void registerBlockStates(Map<BlockState, RegistryEntry<PointOfInterestType>> map,
                                           Registry<PointOfInterestType> registry,
                                           RegistryKey<PointOfInterestType> key,
                                           Block... blocks) {
        RegistryEntry<PointOfInterestType> entry = registry.entryOf(key);
        for (Block block : blocks) {
            for (BlockState state : block.getStateManager().getStates()) {
                map.put(state, entry);
            }
        }
    }
}
