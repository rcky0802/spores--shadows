package moldmod.item;

import me.shedaniel.autoconfig.AutoConfig;
import moldmod.block.SporeDetectorBlock;
import moldmod.atmosphere.RoomAtmosphereCalculator;
import moldmod.atmosphere.RoomAtmosphereCalculator.MiasmaResult;
import moldmod.config.ModConfig;
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

public final class SporeDetectorItem extends BlockItem {

    public SporeDetectorItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);

        // Click destro nel vuoto (in aria): Scansione immediata dell'aria con output in
        // Chat privata
        if (!world.isClient && world instanceof ServerWorld serverWorld) {
            BlockPos eyePos = BlockPos.ofFloored(player.getEyePos());
            MiasmaResult result = RoomAtmosphereCalculator.calculateMiasma(serverWorld, eyePos);

            SporeDetectorBlock.sendDiagnosticMessage(player, result);
            world.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.BLOCK_COPPER_BULB_TURN_ON, SoundCategory.PLAYERS, 0.8f, 1.4f);

            ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
            int cooldownTicks = (config != null && config.sporeDetector != null)
                    ? config.sporeDetector.item_use_cooldown_ticks
                    : 10;
            player.getItemCooldownManager().set(this, cooldownTicks);
        }

        return TypedActionResult.success(stack, world.isClient());
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (world.isClient)
            return;
        if (!(entity instanceof ServerPlayerEntity player))
            return;

        // Feedback audio passivo stile contatore Geiger se tenuto in mano
        boolean isHeld = (player.getMainHandStack() == stack || player.getOffHandStack() == stack);
        if (!isHeld)
            return;

        // Esegui controllo periodico solo ogni N tick per player
        long time = world.getTime();
        ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        int geigerInterval = (config != null && config.sporeDetector != null)
                ? config.sporeDetector.geiger_check_interval_ticks
                : 20;
        if ((time + player.getId()) % geigerInterval != 0)
            return;

        ServerWorld serverWorld = (ServerWorld) world;
        BlockPos eyePos = BlockPos.ofFloored(player.getEyePos());

        // Pre-filtro rapido prima del calcolo
        int radius = config != null && config.toxicity != null ? config.toxicity.scan_radius : 8;
        if (!RoomAtmosphereCalculator.hasMoldNearby(serverWorld, eyePos, radius))
            return;

        double densityThreshold = (config != null && config.sporeDetector != null)
                ? config.sporeDetector.geiger_density_threshold
                : 0.02;
        MiasmaResult result = RoomAtmosphereCalculator.calculateMiasma(serverWorld, eyePos);
        if (result.density > densityThreshold) {
            float pitch = 1.0f + (float) Math.min(1.0, result.density * 5.0);
            world.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.BLOCK_NOTE_BLOCK_HAT.value(), SoundCategory.PLAYERS, 0.25f, pitch);
        }
    }
}
