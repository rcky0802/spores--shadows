package moldmod.block.entity;

import moldmod.SporesShadows;
import moldmod.block.ModBlocks;
import moldmod.block.machine.dehumidifier.DehumidifierBlockEntity;
import moldmod.block.machine.purifier.AirPurifierBlockEntity;
import moldmod.block.redstone.MoldyJukeboxBlockEntity;
import moldmod.block.sign.MoldyHangingSignBlockEntity;
import moldmod.block.sign.MoldySignBlockEntity;
import moldmod.block.workstation.MoldyBarrelBlockEntity;
import moldmod.block.workstation.MoldyChestBlockEntity;
import moldmod.block.workstation.MoldyChiseledBookshelfBlockEntity;
import moldmod.block.workstation.MoldyLecternBlockEntity;
import moldmod.block.workstation.MoldyTrappedChestBlockEntity;
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

    public static final BlockEntityType<AirPurifierBlockEntity> AIR_PURIFIER = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            SporesShadows.id("air_purifier"),
            BlockEntityType.Builder.create(AirPurifierBlockEntity::new, ModBlocks.AIR_PURIFIER).build()
    );

    public static BlockEntityType<MoldySignBlockEntity> MOLDY_SIGN;
    public static BlockEntityType<MoldyHangingSignBlockEntity> MOLDY_HANGING_SIGN;
    public static BlockEntityType<MoldyChiseledBookshelfBlockEntity> MOLDY_CHISELED_BOOKSHELF;
    public static BlockEntityType<MoldyJukeboxBlockEntity> MOLDY_JUKEBOX;
    public static BlockEntityType<MoldyBarrelBlockEntity> MOLDY_BARREL;
    public static BlockEntityType<MoldyChestBlockEntity> MOLDY_CHEST;
    public static BlockEntityType<MoldyTrappedChestBlockEntity> MOLDY_TRAPPED_CHEST;
    public static BlockEntityType<MoldyLecternBlockEntity> MOLDY_LECTERN;

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

        MOLDY_BARREL = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                SporesShadows.id("moldy_barrel"),
                BlockEntityType.Builder.create(MoldyBarrelBlockEntity::new,
                        ModBlocks.MOLDY_BARREL,
                        ModBlocks.WAXED_BARREL
                ).build()
        );

        MOLDY_CHEST = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                SporesShadows.id("moldy_chest"),
                BlockEntityType.Builder.create(MoldyChestBlockEntity::new,
                        ModBlocks.MOLDY_CHEST,
                        ModBlocks.WAXED_CHEST
                ).build()
        );

        MOLDY_TRAPPED_CHEST = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                SporesShadows.id("moldy_trapped_chest"),
                BlockEntityType.Builder.create(MoldyTrappedChestBlockEntity::new,
                        ModBlocks.MOLDY_TRAPPED_CHEST,
                        ModBlocks.WAXED_TRAPPED_CHEST
                ).build()
        );

        MOLDY_LECTERN = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                SporesShadows.id("moldy_lectern"),
                BlockEntityType.Builder.create(MoldyLecternBlockEntity::new,
                        ModBlocks.MOLDY_LECTERN,
                        ModBlocks.WAXED_LECTERN
                ).build()
        );

        // Registrazione Fabric Transfer API (FluidStorage) su tutti i lati
        FluidStorage.SIDED.registerForBlockEntity((be, direction) -> be.getFluidStorage(), DEHUMIDIFIER);

        // Registrazione TeamReborn Energy API (EnergyStorage) su tutti i lati per compatibilità universale RF / FE
        team.reborn.energy.api.EnergyStorage.SIDED.registerForBlockEntity((be, direction) -> be.getEnergyStorage(), DEHUMIDIFIER);
        team.reborn.energy.api.EnergyStorage.SIDED.registerForBlockEntity((be, direction) -> be.getEnergyStorage(), AIR_PURIFIER);
    }
}
