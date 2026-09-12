package moldmod.integration.jade;

import moldmod.block.MoldyBlock;
import moldmod.block.dehumidifier.DehumidifierBlock;
import moldmod.block.dehumidifier.DehumidifierBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.JadeIds;
import snownee.jade.api.WailaPlugin;
import snownee.jade.api.view.HideThingsExtensionProvider;

@WailaPlugin
public class JadePlugin implements IWailaPlugin {
    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(MoldyBlockProvider.INSTANCE, Block.class);
        registration.registerBlockDataProvider(DehumidifierBlockProvider.INSTANCE, Block.class);
        registration.registerBlockDataProvider(AirPurifierBlockProvider.INSTANCE, Block.class);
        // Nasconde le informazioni generiche di Jade per il deumidificatore (fluidi, inventario, energia, progresso)
        registration.registerFluidStorage(HideThingsExtensionProvider.instance(), DehumidifierBlockEntity.class);
        registration.registerFluidStorage(HideThingsExtensionProvider.instance(), DehumidifierBlock.class);
        registration.registerItemStorage(HideThingsExtensionProvider.instance(), DehumidifierBlockEntity.class);
        registration.registerItemStorage(HideThingsExtensionProvider.instance(), DehumidifierBlock.class);
        registration.registerEnergyStorage(HideThingsExtensionProvider.instance(), DehumidifierBlockEntity.class);
        registration.registerEnergyStorage(HideThingsExtensionProvider.instance(), DehumidifierBlock.class);
        registration.registerProgress(HideThingsExtensionProvider.instance(), DehumidifierBlockEntity.class);
        registration.registerProgress(HideThingsExtensionProvider.instance(), DehumidifierBlock.class);

        // Nasconde le informazioni generiche di Jade per il depuratore d'aria
        registration.registerItemStorage(HideThingsExtensionProvider.instance(), moldmod.block.purifier.AirPurifierBlockEntity.class);
        registration.registerItemStorage(HideThingsExtensionProvider.instance(), moldmod.block.purifier.AirPurifierBlock.class);
        registration.registerEnergyStorage(HideThingsExtensionProvider.instance(), moldmod.block.purifier.AirPurifierBlockEntity.class);
        registration.registerEnergyStorage(HideThingsExtensionProvider.instance(), moldmod.block.purifier.AirPurifierBlock.class);
        registration.registerProgress(HideThingsExtensionProvider.instance(), moldmod.block.purifier.AirPurifierBlockEntity.class);
        registration.registerProgress(HideThingsExtensionProvider.instance(), moldmod.block.purifier.AirPurifierBlock.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(MoldyBlockProvider.INSTANCE, Block.class);
        registration.registerBlockComponent(SporeDetectorBlockProvider.INSTANCE, Block.class);
        registration.registerBlockComponent(MoistureDetectorBlockProvider.INSTANCE, Block.class);
        registration.registerBlockComponent(DehumidifierBlockProvider.INSTANCE, Block.class);
        registration.registerBlockComponent(AirPurifierBlockProvider.INSTANCE, Block.class);
        registration.registerEntityComponent(SporeProtectionEntityProvider.INSTANCE, LivingEntity.class);

        // Rimuove tutte le informazioni generiche universali per mostrare solo lo stato compatto
        registration.addTooltipCollectedCallback((box, accessor) -> {
            if (accessor instanceof BlockAccessor blockAccessor) {
                if (blockAccessor.getBlock() instanceof DehumidifierBlock || blockAccessor.getBlock() instanceof moldmod.block.purifier.AirPurifierBlock) {
                    box.getTooltip().remove(JadeIds.UNIVERSAL_FLUID_STORAGE);
                    box.getTooltip().remove(JadeIds.UNIVERSAL_ITEM_STORAGE);
                    box.getTooltip().remove(JadeIds.UNIVERSAL_ENERGY_STORAGE);
                    box.getTooltip().remove(JadeIds.UNIVERSAL_PROGRESS);
                }
            }
        });
        
        registration.addRayTraceCallback((hitResult, accessor, originalAccessor) -> {
            if (accessor instanceof BlockAccessor blockAccessor) {
                BlockState state = blockAccessor.getBlockState();
                if (state.contains(MoldyBlock.STAGE)) {
                    ItemStack stackToDisplay = state.getBlock().getPickStack(blockAccessor.getLevel(), blockAccessor.getPosition(), state);
                    if (stackToDisplay != null && !stackToDisplay.isEmpty()) {
                        return registration.blockAccessor()
                            .from(blockAccessor)
                            .fakeBlock(stackToDisplay)
                            .build();
                    }
                }
            }
            return accessor;
        });
    }
}
