package moldmod;

import net.fabricmc.api.ModInitializer;

import net.minecraft.util.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SporesShadows implements ModInitializer {
	public static final String MOD_ID = "spores--shadows";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");
		me.shedaniel.autoconfig.AutoConfig.register(moldmod.config.ModConfig.class, me.shedaniel.autoconfig.serializer.JanksonConfigSerializer::new);
		
		moldmod.resource.MoldyResourceGenerator.initialize(); // Register Virtual Resource Pack
		
		moldmod.block.ModBlocks.registerModBlocks();
		moldmod.event.MoldyInteractionEvents.register();
		moldmod.event.ToxicAirEvent.register();
		moldmod.command.ModCommands.registerCommands();
		moldmod.registry.ModFuelRegistry.register();
		moldmod.registry.ModComposterRegistry.register();
		moldmod.registry.ModFlammableRegistry.register();
	}

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}
}
