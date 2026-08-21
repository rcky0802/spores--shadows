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
                
        dispatcher.register(CommandManager.literal("spores")
                .requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.literal("reload").executes(ModCommands::executeSporesReload)));
    }
    
    private static int executeSporesReload(CommandContext<ServerCommandSource> context) {
        AutoConfig.getConfigHolder(ModConfig.class).load();
        context.getSource().sendMessage(Text.literal("§a[Spores & Shadows] Configuration reloaded successfully!"));
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
                source.sendMessage(Text.literal(String.format("§7- Formula: §f((Heff * Luv * Smat) + Catalysts) * Tmult")));
                source.sendMessage(Text.literal(String.format("§7- Heff (Humidity): §b%.2f §7[Base: %.2f | Depth: +%.2f | Local: +%.2f]", 
                        result.Heff(), result.baseHum(), result.depthModifier(), result.localHumidityBonus())));
                source.sendMessage(Text.literal(String.format("§7- Luv (Darkness): §8%.2f §7[Avg Light: %.1f / 15.0]", 
                        result.Luv(), result.avgLight())));
                source.sendMessage(Text.literal(String.format("§7- Smat (Material): §e%.2f §7[Based on block type]", result.Smat())));
                source.sendMessage(Text.literal(String.format("§7- Catalysts: §d%.2f §7[Nearby blocks & mold]", result.catalystBonus())));
                
                String tempMod = "";
                if (Math.abs(result.effectiveTemp() - result.surfaceTemp()) > 0.01) {
                    tempMod = result.effectiveTemp() < result.surfaceTemp() ? " (Cooled)" : " (Warmed)";
                }
                source.sendMessage(Text.literal(String.format("§7- Effective Temp: §6%.2f §7[Surface: %.2f%s] => Tmult: §c%.2f", 
                        result.effectiveTemp(), result.surfaceTemp(), tempMod, result.Tmult())));
            } else {
                source.sendMessage(Text.literal(String.format("§a[Mold Risk] §eBlock at %s, %s, %s", pos.getX(), pos.getY(), pos.getZ())));
                source.sendMessage(Text.literal(String.format("§7- Heff (Humidity): §b%.2f", result.Heff())));
                source.sendMessage(Text.literal(String.format("§7- Luv (Darkness): §8%.2f", result.Luv())));
                source.sendMessage(Text.literal(String.format("§7- Smat (Material): §e%.2f", result.Smat())));
                source.sendMessage(Text.literal(String.format("§7- Catalysts: §d%.2f", result.catalystBonus())));
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
