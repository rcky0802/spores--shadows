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
		moldmod.block.ModBlocks.registerModBlocks();
		moldmod.command.ModCommands.registerCommands();

		net.fabricmc.fabric.api.registry.FuelRegistry.INSTANCE.add(net.minecraft.registry.tag.ItemTags.LOGS_THAT_BURN, 300);
		// Note: The custom items are registered in ModBlocks and we can iterate them to set custom fuel times!
		for (String wood : new String[]{"oak", "spruce", "birch", "jungle", "acacia", "dark_oak", "mangrove", "cherry", "bamboo"}) {
			String logName = wood.equals("bamboo") ? "bamboo_block" : wood + "_log";
			String prefix = wood.equals("bamboo") ? "bamboo" : wood;
			
			// Stage 1 (Tainted) halves it -> 150
			net.fabricmc.fabric.api.registry.FuelRegistry.INSTANCE.add(net.minecraft.registry.Registries.ITEM.get(id("tainted_" + logName)), 150);
			net.fabricmc.fabric.api.registry.FuelRegistry.INSTANCE.add(net.minecraft.registry.Registries.ITEM.get(id("tainted_stripped_" + logName)), 150);
			net.fabricmc.fabric.api.registry.FuelRegistry.INSTANCE.add(net.minecraft.registry.Registries.ITEM.get(id("tainted_" + prefix + "_planks")), 150);

			if (!wood.equals("bamboo")) {
				net.fabricmc.fabric.api.registry.FuelRegistry.INSTANCE.add(net.minecraft.registry.Registries.ITEM.get(id("tainted_" + wood + "_wood")), 150);
				net.fabricmc.fabric.api.registry.FuelRegistry.INSTANCE.add(net.minecraft.registry.Registries.ITEM.get(id("tainted_stripped_" + wood + "_wood")), 150);
			}

			// Stage 2 (Moldy) divide by 4 -> 75
			net.fabricmc.fabric.api.registry.FuelRegistry.INSTANCE.add(net.minecraft.registry.Registries.ITEM.get(id("moldy_" + logName)), 75);
			net.fabricmc.fabric.api.registry.FuelRegistry.INSTANCE.add(net.minecraft.registry.Registries.ITEM.get(id("moldy_stripped_" + logName)), 75);
			net.fabricmc.fabric.api.registry.FuelRegistry.INSTANCE.add(net.minecraft.registry.Registries.ITEM.get(id("moldy_" + prefix + "_planks")), 75);

			if (!wood.equals("bamboo")) {
				net.fabricmc.fabric.api.registry.FuelRegistry.INSTANCE.add(net.minecraft.registry.Registries.ITEM.get(id("moldy_" + wood + "_wood")), 75);
				net.fabricmc.fabric.api.registry.FuelRegistry.INSTANCE.add(net.minecraft.registry.Registries.ITEM.get(id("moldy_stripped_" + wood + "_wood")), 75);
			}

			// Stage 3 (Rotten) -> 25
			net.fabricmc.fabric.api.registry.FuelRegistry.INSTANCE.add(net.minecraft.registry.Registries.ITEM.get(id("rotten_" + logName)), 25);
			net.fabricmc.fabric.api.registry.FuelRegistry.INSTANCE.add(net.minecraft.registry.Registries.ITEM.get(id("rotten_stripped_" + logName)), 25);
			net.fabricmc.fabric.api.registry.FuelRegistry.INSTANCE.add(net.minecraft.registry.Registries.ITEM.get(id("rotten_" + prefix + "_planks")), 25);

			if (!wood.equals("bamboo")) {
				net.fabricmc.fabric.api.registry.FuelRegistry.INSTANCE.add(net.minecraft.registry.Registries.ITEM.get(id("rotten_" + wood + "_wood")), 25);
				net.fabricmc.fabric.api.registry.FuelRegistry.INSTANCE.add(net.minecraft.registry.Registries.ITEM.get(id("rotten_stripped_" + wood + "_wood")), 25);
			}
		}
	}

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}
}
