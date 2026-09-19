package moldmod.client.registry;

import moldmod.client.screen.AirPurifierScreen;
import moldmod.client.screen.DehumidifierScreen;
import moldmod.screen.ModScreenHandlers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

@Environment(EnvType.CLIENT)
public final class ClientScreenRegistry {

    private ClientScreenRegistry() {}

    public static void registerScreens() {
        HandledScreens.register(ModScreenHandlers.DEHUMIDIFIER, DehumidifierScreen::new);
        HandledScreens.register(ModScreenHandlers.AIR_PURIFIER, AirPurifierScreen::new);
    }
}
