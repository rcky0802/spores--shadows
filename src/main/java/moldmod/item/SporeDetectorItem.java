package moldmod.item;

import moldmod.block.SporeDetectorBlock;
import moldmod.event.ToxicAirEvent;
import moldmod.event.ToxicAirEvent.MiasmaResult;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SporeDetectorItem extends BlockItem {

    public SporeDetectorItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);

        // Click destro nel vuoto (in aria): Scansione immediata dell'aria con output in Chat privata
        if (!world.isClient && world instanceof ServerWorld serverWorld) {
            BlockPos eyePos = BlockPos.ofFloored(player.getEyePos());
            MiasmaResult result = ToxicAirEvent.calculateMiasma(serverWorld, eyePos);
            
            int redstoneEquiv = 0;
            if (result.level == ToxicAirEvent.AirToxicityLevel.LETHAL_POISON) redstoneEquiv = 15;
            else if (result.level == ToxicAirEvent.AirToxicityLevel.MODERATE_HUNGER) redstoneEquiv = 8;
            else if (result.level == ToxicAirEvent.AirToxicityLevel.WARNING) redstoneEquiv = 3;

            SporeDetectorBlock.sendDiagnosticMessage((ServerPlayerEntity) player, result, redstoneEquiv);
            world.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.BLOCK_COPPER_BULB_TURN_ON, SoundCategory.PLAYERS, 0.8f, 1.4f);
            
            player.getItemCooldownManager().set(this, 10);
        }

        return TypedActionResult.success(stack, world.isClient());
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (world.isClient) return;
        if (!(entity instanceof ServerPlayerEntity player)) return;

        // Feedback audio passivo stile contatore Geiger se tenuto in mano
        boolean isHeld = (player.getMainHandStack() == stack || player.getOffHandStack() == stack);
        if (!isHeld) return;

        // Esegui controllo periodico solo ogni 20 tick per player
        long time = world.getTime();
        if ((time + player.getId()) % 20 != 0) return;

        ServerWorld serverWorld = (ServerWorld) world;
        BlockPos eyePos = BlockPos.ofFloored(player.getEyePos());

        // Pre-filtro rapido prima del calcolo
        if (!ToxicAirEvent.hasMoldNearby(serverWorld, eyePos, 8)) return;

        MiasmaResult result = ToxicAirEvent.calculateMiasma(serverWorld, eyePos);
        if (result.density > 0.02) {
            float pitch = 1.0f + (float) Math.min(1.0, result.density * 5.0);
            world.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.BLOCK_NOTE_BLOCK_HAT.value(), SoundCategory.PLAYERS, 0.25f, pitch);
        }
    }
}
