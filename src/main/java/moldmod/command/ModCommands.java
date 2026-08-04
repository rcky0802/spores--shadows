package moldmod.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import me.shedaniel.autoconfig.AutoConfig;
import moldmod.block.ModBlocks;

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
        CommandRegistrationCallback.EVENT.register(ModCommands::registerMoldRiskCommand);
    }

    private static void registerMoldRiskCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("moldrisk")
                .requires(source -> source.hasPermissionLevel(2))
                .executes(ModCommands::executeMoldRisk));
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
            
            boolean isWaxed = moldmod.block.ModBlocks.VANILLA_TO_MOLDY.containsValue(state.getBlock()) && state.get(moldmod.block.MoldyLogBlock.WAXED);
            
            // Replicate the math here purely for the debug display to the player
            float temp = player.getServerWorld().getBiome(pos).value().getTemperature();
            boolean hasPrecipitation = player.getServerWorld().getBiome(pos).value().hasPrecipitation();
            
            double baseHum = hasPrecipitation ? 0.8 : (temp > 1.5 ? 0.0 : 0.2);
            double depthModifier = pos.getY() < 64 ? (64.0 - pos.getY()) / 100.0 : 0.0;
            double Heff = Math.min(1.0, baseHum + depthModifier);
            double Tmult = (temp > 0.15 && temp < 1.5) ? 1.0 : 0.0;
            int light = player.getServerWorld().getLightLevel(net.minecraft.world.LightType.BLOCK, pos);
            double Luv = Math.max(0.0, 1.0 - (light / 15.0));
            double Smat = 1.0;
            if (isWaxed) Smat = 0.0;
            else if (state.isOf(net.minecraft.block.Blocks.STRIPPED_OAK_LOG)) Smat = 1.4;
            else if (state.isOf(net.minecraft.block.Blocks.OAK_PLANKS)) Smat = 0.8;
            
            double R = (Heff * Tmult * Luv) * Smat;

            // Apply global config multiplier for debug
            ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
            R *= config.globalMoldRiskMultiplier;

            source.sendMessage(Text.literal(String.format("§a[Mold Risk] §eBlock at %s, %s, %s", pos.getX(), pos.getY(), pos.getZ())));
            source.sendMessage(Text.literal(String.format("§7- Heff (Humidity): %.2f", Heff)));
            source.sendMessage(Text.literal(String.format("§7- Tmult (Temp Multiplier): %.2f", Tmult)));
            source.sendMessage(Text.literal(String.format("§7- Luv (Light): %.2f (L:%d)", Luv, light)));
            source.sendMessage(Text.literal(String.format("§7- Smat (Material): %.2f", Smat)));
            source.sendMessage(Text.literal(String.format("§7- Config Multiplier: %.2f", config.globalMoldRiskMultiplier)));
            source.sendMessage(Text.literal(String.format("§c=> R = %.4f %s", R, (R > 0.65 ? "(WILL GROW)" : "(SAFE)"))));
            
        } else {
            source.sendMessage(Text.literal("§cYou must look at a block to check its mold risk."));
        }
        return 1;
    }
}
