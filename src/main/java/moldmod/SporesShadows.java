package moldmod;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import moldmod.block.ModBlocks;
import moldmod.command.ModCommands;
import moldmod.config.ModConfig;
import moldmod.event.MoldyInteractionEvents;
import moldmod.event.ToxicAirEvent;
import moldmod.item.ModItems;
import moldmod.registry.ModComposterRegistry;
import moldmod.registry.ModEnchantments;
import moldmod.registry.ModFlammableRegistry;
import moldmod.registry.ModFuelRegistry;
import moldmod.resource.MoldyResourceGenerator;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SporesShadows implements ModInitializer {
	public static final String MOD_ID = "spores--shadows";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing {}", MOD_ID);
		AutoConfig.register(ModConfig.class, JanksonConfigSerializer::new);
		
		MoldyResourceGenerator.initialize(); // Register Virtual Resource Pack
		
		ModItems.registerModItems();
		ModBlocks.registerModBlocks();
		MoldyInteractionEvents.register();
		ToxicAirEvent.register();
		ModCommands.registerCommands();
		ModFuelRegistry.register();
		ModComposterRegistry.register();
		ModFlammableRegistry.register();
		ModEnchantments.register();
	}

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}
}
