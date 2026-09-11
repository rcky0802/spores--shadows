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

    public static void registerModBlockEntities() {
        SporesShadows.LOGGER.info("Registering ModBlockEntities for " + SporesShadows.MOD_ID);

        // Registrazione Fabric Transfer API (FluidStorage) su tutti i lati
        FluidStorage.SIDED.registerForBlockEntity((be, direction) -> be.getFluidStorage(), DEHUMIDIFIER);

        // Registrazione TeamReborn Energy API (EnergyStorage) su tutti i lati per compatibilità universale RF / FE
        team.reborn.energy.api.EnergyStorage.SIDED.registerForBlockEntity((be, direction) -> be.getEnergyStorage(), DEHUMIDIFIER);
    }
}
