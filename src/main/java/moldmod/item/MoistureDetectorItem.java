package moldmod.item;

import me.shedaniel.autoconfig.AutoConfig;
import moldmod.block.MoistureDetectorBlock;
import moldmod.config.ModConfig;
import moldmod.risk.MoldRiskCalculator;
import moldmod.risk.MoldRiskCalculator.MoldRiskResult;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class MoistureDetectorItem extends BlockItem {

    public MoistureDetectorItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);

        // Right-click in the air: instantaneous silent environmental scan
        if (!world.isClient && world instanceof ServerWorld serverWorld) {
            BlockPos eyePos = BlockPos.ofFloored(player.getEyePos());
            MoldRiskResult result = MoldRiskCalculator.calculate(serverWorld, eyePos, false, null);
            MoistureDetectorBlock.sendDiagnosticMessage(player, result);

            ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
            int cooldownTicks = (config != null && config.moistureDetector != null)
                    ? config.moistureDetector.item_use_cooldown_ticks : 10;
            player.getItemCooldownManager().set(this, cooldownTicks);
        }

        return TypedActionResult.success(stack, world.isClient());
    }
}
