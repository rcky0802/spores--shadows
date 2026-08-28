package moldmod.mixin;

import moldmod.block.MoldyLogBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.AbstractBlockState.class)
public class AbstractBlockStateMixin {

    @Inject(method = "getHardness", at = @At("RETURN"), cancellable = true)
    private void scaleHardness(BlockView world, BlockPos pos, CallbackInfoReturnable<Float> cir) {
        BlockState state = (BlockState) (Object) this;
        if (state.contains(MoldyLogBlock.STAGE)) {
            int stage = state.get(MoldyLogBlock.STAGE);
            if (stage == 0) return;

            moldmod.config.ModConfig config = me.shedaniel.autoconfig.AutoConfig.getConfigHolder(moldmod.config.ModConfig.class).getConfig();
            if (config == null || config.hardness == null || !config.hardness.enable_hardness_scaling) return;

            float original = cir.getReturnValue();
            if (original <= 0f) return;

            float multiplier = (stage == 1) ? config.hardness.stage_1_multiplier
                    : (stage == 2) ? config.hardness.stage_2_multiplier
                    : config.hardness.stage_3_multiplier;

            cir.setReturnValue(original * multiplier);
        }
    }

    @Inject(method = "calcBlockBreakingDelta", at = @At("HEAD"), cancellable = true)
    private void ignoreToolEfficiencyOnStage3(PlayerEntity player, BlockView world, BlockPos pos, CallbackInfoReturnable<Float> cir) {
        BlockState state = (BlockState) (Object) this;
        if (state.contains(MoldyLogBlock.STAGE) && state.get(MoldyLogBlock.STAGE) == 3) {
            float hardness = state.getHardness(world, pos);
            if (hardness == -1.0f) {
                cir.setReturnValue(0.0f);
                return;
            }
            int i = player.canHarvest(state) ? 30 : 100;
            // Force tool multiplier to 1.0f (same as hand)
            cir.setReturnValue((float) (player.getAttributes().getValue(EntityAttributes.PLAYER_BLOCK_BREAK_SPEED)) / hardness / (float) i);
        }
    }
}
