package moldmod.item;

import me.shedaniel.autoconfig.AutoConfig;
import moldmod.block.sensor.SporeDetectorBlock;
import moldmod.atmosphere.RoomAtmosphereCalculator;
import moldmod.atmosphere.RoomAtmosphereCalculator.MiasmaResult;
import moldmod.config.ModConfig;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class SporeDetectorItem extends BlockItem {

    public SporeDetectorItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);

        // Click destro nel vuoto (in aria): Scansione immediata dell'aria con output in Chat privata
        if (!world.isClient && world instanceof ServerWorld serverWorld) {
            BlockPos eyePos = BlockPos.ofFloored(player.getEyePos());
            MiasmaResult result = RoomAtmosphereCalculator.calculateMiasma(serverWorld, eyePos);

            SporeDetectorBlock.sendDiagnosticMessage(player, result);
            world.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.BLOCK_COPPER_BULB_TURN_ON, SoundCategory.PLAYERS, 0.8f, 1.3f);

            ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
            int cooldownTicks = (config != null && config.sporeDetector != null)
                    ? config.sporeDetector.item_use_cooldown_ticks
                    : 10;
            player.getItemCooldownManager().set(this, cooldownTicks);
        }

        return TypedActionResult.success(stack, world.isClient());
    }
}
