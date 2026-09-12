package moldmod.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import me.shedaniel.autoconfig.AutoConfig;
import moldmod.atmosphere.RoomAtmosphereCalculator;
import moldmod.atmosphere.RoomAtmosphereCalculator.MiasmaResult;
import moldmod.block.MoldyBlock;
import moldmod.config.ModConfig;
import moldmod.risk.MoldRiskCalculator;
import moldmod.risk.MoldRiskCalculator.MoldRiskResult;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.block.BlockState;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

public final class ModCommands {

    private ModCommands() {
    }

    public static void registerCommands() {
        CommandRegistrationCallback.EVENT.register(ModCommands::registerCommandsInternal);
    }

    private static void registerCommandsInternal(CommandDispatcher<ServerCommandSource> dispatcher,
            CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("moldrisk")
                .requires(source -> source.hasPermissionLevel(2))
                .executes(ModCommands::executeMoldRisk));

        dispatcher.register(CommandManager.literal("miasma")
                .requires(source -> source.hasPermissionLevel(2))
                .executes(ModCommands::executeMiasma));
    }

    private static int executeMiasma(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        if (!(source.getEntity() instanceof ServerPlayerEntity player)) {
            return 0;
        }

        ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        MiasmaResult result = RoomAtmosphereCalculator.calculateMiasma((ServerWorld) player.getWorld(),
                BlockPos.ofFloored(player.getEyePos()));

        source.sendMessage(Text.literal("§a[Miasma Scanner] §eScanning environment..."));

        switch (result.ventilationType) {
            case CLEAN_OPEN_AIR -> source.sendMessage(
                    Text.literal("§7- Ventilation State: §bClean Air / Open Sky §7(miasma fully dissipated)"));
            case UNCONFINED_CAVERN ->
                source.sendMessage(Text.literal("§7- Ventilation State: §aUnconfined Cavern §7(Massive volume ≥ "
                        + config.toxicity.max_air_volume + " - naturally diluted)"));
            case VENTILATED -> {
                String distStr = (result.distanceToVentilation < 900)
                        ? String.format(" | Dist to Vent: §b%d blocks§7", result.distanceToVentilation)
                        : "";
                source.sendMessage(Text.literal(String.format(
                        "§7- Ventilation State: §eVentilated Environment §7(Ventilation: §a%.1f§7%s | Purge modifier: §a-%.2f§7)",
                        result.ventilationScore, distStr, result.ventilationScore)));
            }
            case HERMETIC_SEALED -> source.sendMessage(
                    Text.literal("§7- Ventilation State: §cHermetically Sealed §7(Isolated room, zero ventilation)"));
        }

        source.sendMessage(Text.literal(String.format("§7- Explored Air Volume: §f%d blocks", result.volume)));
        source.sendMessage(Text.literal(String.format("§7- Mold Toxicity: §c+%.2f §7| Ventilation Purge: §a-%.2f",
                result.toxicScore, result.ventilationScore)));
        source.sendMessage(Text.literal(String.format("§7- Active Purifiers: §b%d §7(Purifier Purge: §b-%.2f§7)",
                result.roomPurifierCount, result.purifierCleaningBonus)));

        String dynamicStatus = "§aSTABLE";
        if (result.netMiasma > result.targetMiasma + 0.05) {
            dynamicStatus = String.format("§bPURIFYING / DISSIPATING §7(Target: §f%.2f§7)", result.targetMiasma);
        } else if (result.netMiasma < result.targetMiasma - 0.05) {
            dynamicStatus = String.format("§cACCUMULATING / SATURATING §7(Target: §f%.2f§7)", result.targetMiasma);
        }
        source.sendMessage(Text.literal(String.format("§7- Current Miasma M(t): §6%.2f §7[%s§7]", result.netMiasma, dynamicStatus)));
        source.sendMessage(Text.literal(String.format("§7- Room Spore Density: §d%.3f/block §7| Exposure Index: §5%.3f",
                result.density, result.exposureIndex)));
        if (config.toxicity.enable_distributed_miasma && !result.openAir && result.volume > 0) {
            source.sendMessage(Text.literal(String.format(
                    "§7- Player Local Microclimate: §a%.1f flow §7(§b%.1f%% aeration§7) | Local Spore Density: §d%.3f/block",
                    result.localFlow, result.localAeration * 100.0, result.localDensity)));
        }

        switch (result.level) {
            case LETHAL_POISON ->
                source.sendMessage(Text.literal("§4[HAZARD] Lethal Level! Nausea & Poison imminent!"));
            case MODERATE_HUNGER -> source.sendMessage(Text.literal("§e[WARNING] Moderate Level! Hunger imminent."));
            case WARNING -> source.sendMessage(Text.literal("§6[NOTICE] Low Level. Airborne spores detected."));
            case CLEAN -> source.sendMessage(Text.literal("§a[SAFE] Clean air quality."));
        }

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

            boolean isWaxed = state.contains(MoldyBlock.WAXED) && state.get(MoldyBlock.WAXED);

            ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();

            MoldRiskResult result = MoldRiskCalculator.calculate(player.getServerWorld(), pos, isWaxed, state);
            double R = result.R();

            String blockName = Registries.BLOCK.getId(state.getBlock()).toString();
            int stage = state.contains(MoldyBlock.STAGE) ? state.get(MoldyBlock.STAGE) : 0;
            String stageText = stage == 0 ? "Normal" : stage == 1 ? "Tainted" : stage == 2 ? "Moldy" : "Rotten";
            String waxedText = isWaxed ? "§eYes" : "§cNo";
            String waterloggedText = result.isWaterlogged() ? "§bYes" : "§7No";

            source.sendMessage(Text.literal(
                    String.format("§a[Mold Risk] §eBlock at (%d, %d, %d)", pos.getX(), pos.getY(), pos.getZ())));
            source.sendMessage(Text.literal(
                    String.format("§7- Block: §f%s §7(Stage: §f%s§7, Waxed: %s§7, Waterlogged: %s§7)", blockName, stageText, waxedText, waterloggedText)));

            switch (result.roomVentilationType()) {
                case CLEAN_OPEN_AIR -> source.sendMessage(
                        Text.literal("§7- Ventilation State: §bClean Air / Open Sky §7(humidity regulated by weather & sunlight)"));
                case UNCONFINED_CAVERN -> source.sendMessage(
                        Text.literal("§7- Ventilation State: §aUnconfined Cavern §7(Massive volume ≥ "
                                + config.toxicity.max_air_volume + " - naturally diluted)"));
                case VENTILATED -> {
                    String distStr = (result.distanceToVentilation() < 900)
                            ? String.format(" | Dist to Vent: §b%d blocks§7", result.distanceToVentilation())
                            : "";
                    source.sendMessage(Text.literal(String.format(
                            "§7- Ventilation State: §eVentilated Environment §7(Ventilation: §a%.1f§7%s | Purge modifier: §a-%.2f§7)",
                            result.aerationFlow(), distStr, result.aerationDryingBonus())));
                }
                case HERMETIC_SEALED -> source.sendMessage(
                        Text.literal("§7- Ventilation State: §cHermetically Sealed §7(Isolated room, zero ventilation)"));
            }

            double waterContr = result.roomWaterSourcesCount() * config.environment.water_source_humidity_contribution;
            if (result.exposedFaces() == 0) {
                source.sendMessage(Text.literal(
                        "§7- Explored Room Volume: §8None §7(Completely buried block) | Connected Water Sources: §80 blocks"));
            } else {
                source.sendMessage(Text.literal(String.format(
                        "§7- Explored Room Volume: §f%d blocks §7| Connected Water Sources: §b%d blocks §7(+%.2f, Cap: %.2f)",
                        result.airVolume(), result.roomWaterSourcesCount(), waterContr, config.environment.max_room_water_humidity_bonus)));
            }

            double totalSources = result.baseHum() + result.depthModifier() + Math.min(config.environment.max_room_water_humidity_bonus, waterContr);
            source.sendMessage(Text.literal(String.format(
                    "§7- Moisture Balance: Sources: §b+%.2f §7(Base: %.2f, Depth: +%.2f, Water: +%.2f) | Purge: §3-%.2f",
                    totalSources, result.baseHum(), result.depthModifier(), Math.min(config.environment.max_room_water_humidity_bonus, waterContr), result.aerationDryingBonus())));

            double currentH = result.currentHumidity();
            double targetH = result.targetHumidity();
            double alphaSat = config.environment.humidity_saturation_speed;
            double alphaDiss = config.environment.humidity_dissipation_speed;
            String dynamicStatus = String.format("§aSTABLE §7(Target: §f%.2f§7)", targetH);
            if (currentH > targetH + 0.02) {
                dynamicStatus = String.format("§bPURIFYING / DISSIPATING §7(Target: §f%.2f§7 | Rate α: §b%.2f§7)", targetH, alphaDiss);
            } else if (currentH < targetH - 0.02) {
                dynamicStatus = String.format("§cACCUMULATING / SATURATING §7(Target: §f%.2f§7 | Rate α: §c%.2f§7)", targetH, alphaSat);
            }

            source.sendMessage(Text.literal(String.format("§7- Target Humidity Htarget: §b%.2f", targetH)));
            source.sendMessage(Text.literal(String.format("§7- Current Room Humidity H(t): §b%.2f §7[%s§7]", currentH, dynamicStatus)));

            source.sendMessage(Text.literal(String.format(
                    "§7- Local Face Aeration: §a%.1f flow §7(§b%.1f%% aeration§7) | Drying Bonus: §3-%.2f",
                    result.aerationFlow(), result.aeration() * 100.0, result.aerationDryingBonus())));
            source.sendMessage(Text.literal(String.format("§7- Effective Humidity Heff: §b%.2f", result.Heff())));

            source.sendMessage(Text.literal(String.format("§7- Luv (Darkness): §8%.2f §7[Avg Light: %.1f / 15.0]",
                    result.Luv(), result.avgLight())));
            source.sendMessage(Text.literal(String.format(
                    "§7- Smat (Susceptibility): §e%.2f §7[%s] | Catalysts: §d%.2f §7| Miasma Pressure: §5+%.2f",
                    result.Smat(), blockName, result.catalystBonus(), result.miasmaBonus())));

            String tempMod = "";
            if (Math.abs(result.effectiveTemp() - result.surfaceTemp()) > 0.01) {
                tempMod = result.effectiveTemp() < result.surfaceTemp() ? " (Cooled)" : " (Warmed)";
            }
            source.sendMessage(
                    Text.literal(String.format("§7- Temperature: §6%.2f §7[Surface: %.2f%s] => Tmult: §c%.2f",
                            result.effectiveTemp(), result.surfaceTemp(), tempMod, result.Tmult())));

            source.sendMessage(
                    Text.literal(String.format("§7- Infection Threshold: §f%.2f", config.general.infection_threshold)));
            source.sendMessage(Text.literal(String.format("§c=> R = %.4f %s", R,
                    (R > config.general.infection_threshold ? "§4(WILL GROW / INFECT)" : "§a(SAFE / IMMUNE)"))));

        } else {
            source.sendMessage(Text.literal("§cYou must look at a block to check its mold risk."));
        }
        return 1;
    }
}
