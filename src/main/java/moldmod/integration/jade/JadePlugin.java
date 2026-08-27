package moldmod.integration.jade;

import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;
import net.minecraft.block.Block;

@WailaPlugin
public class JadePlugin implements IWailaPlugin {
    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(MoldyBlockProvider.INSTANCE, Block.class);
        
        registration.addRayTraceCallback((hitResult, accessor, originalAccessor) -> {
            if (accessor instanceof snownee.jade.api.BlockAccessor blockAccessor) {
                net.minecraft.block.BlockState state = blockAccessor.getBlockState();
                if (state.contains(moldmod.block.MoldyLogBlock.STAGE)) {
                    int stage = state.get(moldmod.block.MoldyLogBlock.STAGE);
                    boolean waxed = state.contains(moldmod.block.MoldyLogBlock.WAXED) && state.get(moldmod.block.MoldyLogBlock.WAXED);
                    
                    net.minecraft.item.ItemStack stackToDisplay = null;
                    
                    if (!waxed && stage == 0) {
                        Block vanillaBlock = null;
                        for (java.util.Map.Entry<Block, Block> entry : moldmod.block.ModBlocks.VANILLA_TO_MOLDY.entrySet()) {
                            if (entry.getValue() == state.getBlock()) {
                                vanillaBlock = entry.getKey();
                                break;
                            }
                        }
                        if (vanillaBlock != null) {
                            stackToDisplay = new net.minecraft.item.ItemStack(vanillaBlock.asItem());
                        }
                    } else {
                        java.util.List<net.minecraft.item.Item> items = moldmod.block.ModBlocks.MOLDY_ITEMS_BY_BLOCK.get(state.getBlock());
                        if (items != null && items.size() == 4) {
                            stackToDisplay = new net.minecraft.item.ItemStack(items.get(stage));
                            if (waxed) {
                                net.minecraft.component.type.BlockStateComponent comp = stackToDisplay.getOrDefault(net.minecraft.component.DataComponentTypes.BLOCK_STATE, net.minecraft.component.type.BlockStateComponent.DEFAULT);
                                stackToDisplay.set(net.minecraft.component.DataComponentTypes.BLOCK_STATE, comp.with(moldmod.block.MoldyLogBlock.WAXED, true));
                            }
                        }
                    }
                    
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
