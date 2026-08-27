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
                    net.minecraft.item.ItemStack stackToDisplay = state.getBlock().getPickStack(blockAccessor.getLevel(), blockAccessor.getPosition(), state);
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
