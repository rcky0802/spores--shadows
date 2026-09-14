package moldmod.block.entity;

import moldmod.SporesShadows;
import moldmod.block.ModBlocks;
import moldmod.block.dehumidifier.DehumidifierBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public final class ModBlockEntities {

    private ModBlockEntities() {}

    public static final BlockEntityType<DehumidifierBlockEntity> DEHUMIDIFIER = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            SporesShadows.id("dehumidifier"),
            BlockEntityType.Builder.create(DehumidifierBlockEntity::new, ModBlocks.DEHUMIDIFIER).build()
    );

    public static final BlockEntityType<moldmod.block.purifier.AirPurifierBlockEntity> AIR_PURIFIER = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            SporesShadows.id("air_purifier"),
            BlockEntityType.Builder.create(moldmod.block.purifier.AirPurifierBlockEntity::new, ModBlocks.AIR_PURIFIER).build()
    );

    public static BlockEntityType<MoldySignBlockEntity> MOLDY_SIGN;
    public static BlockEntityType<MoldyHangingSignBlockEntity> MOLDY_HANGING_SIGN;
    public static BlockEntityType<MoldyChiseledBookshelfBlockEntity> MOLDY_CHISELED_BOOKSHELF;
    public static BlockEntityType<MoldyJukeboxBlockEntity> MOLDY_JUKEBOX;

    public static void registerModBlockEntities() {
        SporesShadows.LOGGER.info("Registering ModBlockEntities for " + SporesShadows.MOD_ID);

        MOLDY_SIGN = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                SporesShadows.id("moldy_sign"),
                BlockEntityType.Builder.create(MoldySignBlockEntity::new, ModBlocks.MOLDY_SIGNS.toArray(net.minecraft.block.Block[]::new)).build()
        );

        MOLDY_HANGING_SIGN = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                SporesShadows.id("moldy_hanging_sign"),
                BlockEntityType.Builder.create(MoldyHangingSignBlockEntity::new, ModBlocks.MOLDY_HANGING_SIGNS.toArray(net.minecraft.block.Block[]::new)).build()
        );

        MOLDY_CHISELED_BOOKSHELF = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                SporesShadows.id("moldy_chiseled_bookshelf"),
                BlockEntityType.Builder.create(MoldyChiseledBookshelfBlockEntity::new,
                        ModBlocks.MOLDY_CHISELED_BOOKSHELF,
                        ModBlocks.WAXED_CHISELED_BOOKSHELF
                ).build()
        );

        MOLDY_JUKEBOX = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                SporesShadows.id("moldy_jukebox"),
                BlockEntityType.Builder.create(MoldyJukeboxBlockEntity::new,
                        ModBlocks.MOLDY_JUKEBOX,
                        ModBlocks.WAXED_JUKEBOX
                ).build()
        );

        // Registrazione Fabric Transfer API (FluidStorage) su tutti i lati
        FluidStorage.SIDED.registerForBlockEntity((be, direction) -> be.getFluidStorage(), DEHUMIDIFIER);

        // Registrazione TeamReborn Energy API (EnergyStorage) su tutti i lati per compatibilità universale RF / FE
        team.reborn.energy.api.EnergyStorage.SIDED.registerForBlockEntity((be, direction) -> be.getEnergyStorage(), DEHUMIDIFIER);
        team.reborn.energy.api.EnergyStorage.SIDED.registerForBlockEntity((be, direction) -> be.getEnergyStorage(), AIR_PURIFIER);
    }
}
