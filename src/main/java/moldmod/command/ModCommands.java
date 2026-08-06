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
                .executes(ModCommands::executeMoldRisk));
                
        dispatcher.register(CommandManager.literal("spores")
                .requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.literal("reload").executes(ModCommands::executeSporesReload)));
    }
    
    private static int executeSporesReload(CommandContext<ServerCommandSource> context) {
        AutoConfig.getConfigHolder(ModConfig.class).load();
        context.getSource().sendMessage(Text.literal("§a[Spores & Shadows] Configuration reloaded successfully!"));
        return 1;
    }

    private static int executeMoldRisk(CommandContext<ServerCommandSource> context) {
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
            
            double R = moldmod.block.MoldyBlockHelper.calculateR(player.getServerWorld(), pos, isWaxed, state);

            source.sendMessage(Text.literal(String.format("§a[Mold Risk] §eBlock at %s, %s, %s", pos.getX(), pos.getY(), pos.getZ())));
            source.sendMessage(Text.literal(String.format("§7- Infection Threshold: %.2f", config.general.infection_threshold)));
            source.sendMessage(Text.literal(String.format("§c=> R = %.4f %s", R, (R > config.general.infection_threshold ? "(WILL GROW)" : "(SAFE)"))));
            
        } else {
            source.sendMessage(Text.literal("§cYou must look at a block to check its mold risk."));
        }
        return 1;
    }
}
