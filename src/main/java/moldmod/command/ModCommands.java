package moldmod.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import me.shedaniel.autoconfig.AutoConfig;

import moldmod.config.ModConfig;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.block.BlockState;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

public class ModCommands {

    public static void registerCommands() {
        CommandRegistrationCallback.EVENT.register(ModCommands::registerCommandsInternal);
    }

    private static void registerCommandsInternal(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("moldrisk")
                .requires(source -> source.hasPermissionLevel(2))
                .executes(context -> executeMoldRisk(context, false))
                .then(CommandManager.literal("verbose")
                        .executes(context -> executeMoldRisk(context, true))));
                
        dispatcher.register(CommandManager.literal("miasma")
                .requires(source -> source.hasPermissionLevel(2))
                .executes(ModCommands::executeMiasma));

        dispatcher.register(CommandManager.literal("spores")
                .requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.literal("reload").executes(ModCommands::executeSporesReload)));
    }
    
    private static int executeSporesReload(CommandContext<ServerCommandSource> context) {
        AutoConfig.getConfigHolder(ModConfig.class).load();
        context.getSource().sendMessage(Text.literal("§a[Spores & Shadows] Configuration reloaded successfully!"));
        return 1;
    }

    private static int executeMiasma(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        if (!(source.getEntity() instanceof ServerPlayerEntity player)) {
            return 0;
        }

        moldmod.config.ModConfig config = me.shedaniel.autoconfig.AutoConfig.getConfigHolder(moldmod.config.ModConfig.class).getConfig();
        moldmod.event.ToxicAirEvent.MiasmaResult result = moldmod.event.ToxicAirEvent.calculateMiasma((net.minecraft.server.world.ServerWorld)player.getWorld(), BlockPos.ofFloored(player.getEyePos()));

        source.sendMessage(Text.literal("§a[Miasma Scanner] §eScanning environment..."));
        
        switch (result.ventilationType) {
            case CLEAN_OPEN_AIR -> source.sendMessage(Text.literal("§7- Ventilation State: §bClean Air §7(Open Air / Sky Exposure - miasma dissipated)"));
            case UNCONFINED_CAVERN -> source.sendMessage(Text.literal("§7- Ventilation State: §aUnconfined Cavern §7(Massive volume ≥ " + config.toxicity.max_air_volume + " - miasma naturally diluted)"));
            case VENTILATED -> source.sendMessage(Text.literal(String.format("§7- Ventilation State: §eVentilated Environment §7(Ventilation modifier: §a-%.2f§7)", result.ventilationScore)));
            case HERMETIC_SEALED -> source.sendMessage(Text.literal("§7- Ventilation State: §cHermetically Sealed §7(Isolated airtight room - zero ventilation)"));
        }

        source.sendMessage(Text.literal(String.format("§7- Volume: %s blocks analyzed", result.volume)));
        source.sendMessage(Text.literal(String.format("§7- Toxicity Score (from mold): §c+%.2f", result.toxicScore)));
        if (result.ventilationScore > 0.0) {
            source.sendMessage(Text.literal(String.format("§7- Ventilation Flow Capacity: §a-%.2f", result.ventilationScore)));
        }
        
        String dynamicStatus = "§aSTABLE";
        if (result.netMiasma > result.targetMiasma + 0.05) {
            dynamicStatus = String.format("§bPURIFYING / DISSIPATING §7(Target: §f%.2f§7)", result.targetMiasma);
        } else if (result.netMiasma < result.targetMiasma - 0.05) {
            dynamicStatus = String.format("§cACCUMULATING / SATURATING §7(Target: §f%.2f§7)", result.targetMiasma);
        }
        source.sendMessage(Text.literal(String.format("§7- Current Miasma M(t): §6%.2f §7[%s§7]", result.netMiasma, dynamicStatus)));
        source.sendMessage(Text.literal(String.format("§7- Spore Density: §d%.3f §7| Exposure Index: §5%.3f", result.density, result.exposureIndex)));

        switch (result.level) {
            case LETHAL_POISON -> source.sendMessage(Text.literal("§4[WARNING] Lethal level! Nausea and Poison imminent!"));
            case MODERATE_HUNGER -> source.sendMessage(Text.literal("§e[WARNING] Moderate level! Hunger imminent."));
            case WARNING -> source.sendMessage(Text.literal("§6[WARNING] Low miasma level. Spores floating in the air."));
            case CLEAN -> source.sendMessage(Text.literal("§a[SAFE] Harmless miasma level."));
        }

        return 1;
    }

    private static int executeMoldRisk(CommandContext<ServerCommandSource> context, boolean verbose) {
        ServerCommandSource source = context.getSource();
        if (!(source.getEntity() instanceof ServerPlayerEntity player)) {
            return 0;
        }

        HitResult hit = player.raycast(10.0, 0.0f, false);
        if (hit.getType() == HitResult.Type.BLOCK) {
            BlockPos pos = ((BlockHitResult) hit).getBlockPos();
            BlockState state = player.getServerWorld().getBlockState(pos);
            
            boolean isWaxed = state.contains(moldmod.block.MoldyLogBlock.WAXED) && state.get(moldmod.block.MoldyLogBlock.WAXED);
            
            ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
            
            moldmod.block.MoldyBlockHelper.MoldRiskResult result = moldmod.block.MoldyBlockHelper.calculateDetailedR(player.getServerWorld(), pos, isWaxed, state);
            double R = result.R();

            String blockName = net.minecraft.registry.Registries.BLOCK.getId(state.getBlock()).toString();
            int stage = state.contains(moldmod.block.MoldyLogBlock.STAGE) ? state.get(moldmod.block.MoldyLogBlock.STAGE) : 0;
            String stageText = stage == 0 ? "Normal" : stage == 1 ? "Tainted" : stage == 2 ? "Moldy" : "Rotten";
            String waxedText = isWaxed ? "§eYes" : "§cNo";

            if (verbose) {
                source.sendMessage(Text.literal(String.format("§a[Mold Risk Verbose] §eBlock at %s, %s, %s", pos.getX(), pos.getY(), pos.getZ())));
                source.sendMessage(Text.literal(String.format("§7- Block: §f%s §7(Stage: §f%s§7, Waxed: %s§7)", blockName, stageText, waxedText)));
                source.sendMessage(Text.literal(String.format("§7- Formula: §f((Heff * Luv * Smat) + DirectCatalysts + MiasmaAir) * Tmult")));
                source.sendMessage(Text.literal(String.format("§7- Raw Humidity: §b%.2f §7[Base: %.2f | Depth: +%.2f | Water: +%.2f]", 
                        result.Hraw(), result.baseHum(), result.depthModifier(), result.localHumidityBonus())));
                source.sendMessage(Text.literal(String.format("§7- Aeration (Airflow): §b%.2f §7[Drying Bonus: §3-%.2f §7| Exposed Faces: §f%d/6 §7| Air Vol: §b%d§7]", 
                        result.aeration(), result.aerationDryingBonus(), result.exposedFaces(), result.airVolume())));
                source.sendMessage(Text.literal(String.format("§7- Effective Humidity (Heff): §b%.2f", result.Heff())));
                source.sendMessage(Text.literal(String.format("§7- Luv (Darkness): §8%.2f §7[Avg Light: %.1f / 15.0]", 
                        result.Luv(), result.avgLight())));
                source.sendMessage(Text.literal(String.format("§7- Smat (Material): §e%.2f §7[Based on block type]", result.Smat())));
                source.sendMessage(Text.literal(String.format("§7- Direct Catalysts: §d%.2f §7[Nearby blocks & mold]", result.catalystBonus())));
                source.sendMessage(Text.literal(String.format("§7- Miasma Air Pressure: §5+%.2f §7[Net Miasma: %.2f]", result.miasmaBonus(), result.netMiasma())));
                
                String tempMod = "";
                if (Math.abs(result.effectiveTemp() - result.surfaceTemp()) > 0.01) {
                    tempMod = result.effectiveTemp() < result.surfaceTemp() ? " (Cooled)" : " (Warmed)";
                }
                source.sendMessage(Text.literal(String.format("§7- Effective Temp: §6%.2f §7[Surface: %.2f%s] => Tmult: §c%.2f", 
                        result.effectiveTemp(), result.surfaceTemp(), tempMod, result.Tmult())));
            } else {
                source.sendMessage(Text.literal(String.format("§a[Mold Risk] §eBlock at %s, %s, %s", pos.getX(), pos.getY(), pos.getZ())));
                source.sendMessage(Text.literal(String.format("§7- Heff (Humidity): §b%.2f §7(Aeration: %.2f)", result.Heff(), result.aeration())));
                source.sendMessage(Text.literal(String.format("§7- Luv (Darkness): §8%.2f", result.Luv())));
                source.sendMessage(Text.literal(String.format("§7- Smat (Material): §e%.2f", result.Smat())));
                source.sendMessage(Text.literal(String.format("§7- Catalysts: §d%.2f §7| Miasma: §5+%.2f", result.catalystBonus(), result.miasmaBonus())));
                source.sendMessage(Text.literal(String.format("§7- Effective Temp: §6%.2f §7(Tmult: §c%.2f§7)", result.effectiveTemp(), result.Tmult())));
            }
            
            source.sendMessage(Text.literal(String.format("§7- Infection Threshold: §f%.2f", config.general.infection_threshold)));
            source.sendMessage(Text.literal(String.format("§c=> R = %.4f %s", R, (R > config.general.infection_threshold ? "§4(WILL GROW)" : "§a(SAFE)"))));
            
        } else {
            source.sendMessage(Text.literal("§cYou must look at a block to check its mold risk."));
        }
        return 1;
    }
}
