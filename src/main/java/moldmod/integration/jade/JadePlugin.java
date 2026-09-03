package moldmod.integration.jade;

import moldmod.block.MoldyBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class JadePlugin implements IWailaPlugin {
    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(MoldyBlockProvider.INSTANCE, Block.class);
        registration.registerEntityComponent(SporeProtectionEntityProvider.INSTANCE, LivingEntity.class);
        
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
