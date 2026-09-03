package moldmod.mixin;

import me.shedaniel.autoconfig.AutoConfig;
import moldmod.block.MoldyBlock;
import moldmod.config.ModConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.FireBlock;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = FireBlock.class, priority = 900)
public class FireBlockMixin {

    @Inject(method = "getBurnChance(Lnet/minecraft/block/BlockState;)I", at = @At("HEAD"), cancellable = true)
    private void increaseMoldyBurnChance(BlockState state, CallbackInfoReturnable<Integer> cir) {
        if (state.contains(MoldyBlock.STAGE)) {
            Identifier id = Registries.BLOCK.getId(state.getBlock());
            String path = id.getPath();

            if (path.contains("crimson") || path.contains("warped") || path.contains("button") || path.contains("pressure_plate")) {
                cir.setReturnValue(0);
                return;
            }

            int base = 5;

            ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
            if (config == null || config.flammability == null || !config.flammability.enable_flammability) {
                cir.setReturnValue(base);
                return;
            }

            int stage = state.get(MoldyBlock.STAGE);
            boolean isWaxed = (state.contains(MoldyBlock.WAXED) && state.get(MoldyBlock.WAXED))
                    || path.startsWith("waxed_");

            int bonus = 0;
            if (stage == 1) bonus += config.flammability.stage_1_burn_bonus;
            else if (stage == 2) bonus += config.flammability.stage_2_burn_bonus;
            else if (stage == 3) bonus += config.flammability.stage_3_burn_bonus;

            if (isWaxed) {
                bonus += config.flammability.waxed_burn_bonus;
            }

            cir.setReturnValue(base + bonus);
        }
    }

    @Inject(method = "getSpreadChance(Lnet/minecraft/block/BlockState;)I", at = @At("HEAD"), cancellable = true)
    private void increaseMoldySpreadChance(BlockState state, CallbackInfoReturnable<Integer> cir) {
        if (state.contains(MoldyBlock.STAGE)) {
            Identifier id = Registries.BLOCK.getId(state.getBlock());
            String path = id.getPath();

            if (path.contains("crimson") || path.contains("warped") || path.contains("button") || path.contains("pressure_plate")) {
                cir.setReturnValue(0);
                return;
            }

            int base = (path.contains("log") || path.contains("wood")) ? 5 : 20;

            ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
            if (config == null || config.flammability == null || !config.flammability.enable_flammability) {
                cir.setReturnValue(base);
                return;
            }

            int stage = state.get(MoldyBlock.STAGE);
            int bonus = 0;
            if (stage == 1) bonus += config.flammability.stage_1_spread_bonus;
            else if (stage == 2) bonus += config.flammability.stage_2_spread_bonus;
            else if (stage == 3) bonus += config.flammability.stage_3_spread_bonus;

            cir.setReturnValue(base + bonus);
        }
    }
}
