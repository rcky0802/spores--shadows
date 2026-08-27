package moldmod.event;

import moldmod.block.MoldyLogBlock;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.BlockState;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class ToxicAirEvent {

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            int currentTick = server.getTicks();
            moldmod.config.ModConfig config = me.shedaniel.autoconfig.AutoConfig.getConfigHolder(moldmod.config.ModConfig.class).getConfig();
            
            int checkInterval = config.toxicity.check_interval_ticks;
            int radius = config.toxicity.scan_radius;
            int nauseaThreshold = config.toxicity.threshold_nausea;
            int poisonThreshold = config.toxicity.threshold_poison;
            int nauseaDuration = config.toxicity.duration_nausea_ticks;
            int poisonDuration = config.toxicity.duration_poison_ticks;

            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                // Load Balancing: Spalma il carico in modo che ogni giocatore sia calcolato in un tick diverso
                if (currentTick % checkInterval != player.getId() % checkInterval) continue;

                // I giocatori in creativa o spettatore sono immuni
                if (player.isSpectator() || player.isCreative()) continue;

                int toxicity = 0;
                BlockPos playerPos = player.getBlockPos();
                int px = playerPos.getX();
                int py = playerPos.getY();
                int pz = playerPos.getZ();

                // Ottimizzazione: Usiamo Mutable per azzerare l'impatto sulla memoria (Garbage Collection)
                BlockPos.Mutable mutable = new BlockPos.Mutable();

                for (int x = -radius; x <= radius; x++) {
                    for (int y = -radius; y <= radius; y++) {
                        for (int z = -radius; z <= radius; z++) {
                            mutable.set(px + x, py + y, pz + z);
                            BlockState state = player.getWorld().getBlockState(mutable);
                            
                            if (state.contains(MoldyLogBlock.STAGE)) {
                                if (state.contains(MoldyLogBlock.WAXED) && state.get(MoldyLogBlock.WAXED)) {
                                    continue;
                                }

                                int stage = state.get(MoldyLogBlock.STAGE);
                                if (stage > 0) {
                                    toxicity += stage;
                                }
                            }
                        }
                    }
                }

                // Applica gli effetti in base alla tossicità accumulata
                if (toxicity >= nauseaThreshold) {
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, nauseaDuration, config.toxicity.nausea_amplifier, false, false, true));
                    moldmod.block.MoldyBlockHelper.grantAdvancement(player, "toxic_air");
                }
                
                if (toxicity >= poisonThreshold) {
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, poisonDuration, config.toxicity.poison_amplifier, false, false, true));
                    moldmod.block.MoldyBlockHelper.grantAdvancement(player, "toxic_air");
                }
            }
        });
    }
}
