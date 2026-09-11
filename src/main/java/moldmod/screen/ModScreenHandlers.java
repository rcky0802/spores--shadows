package moldmod.screen;

import moldmod.SporesShadows;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;

public final class ModScreenHandlers {

    private ModScreenHandlers() {}

    public static final ScreenHandlerType<DehumidifierScreenHandler> DEHUMIDIFIER = Registry.register(
            Registries.SCREEN_HANDLER,
            SporesShadows.id("dehumidifier"),
            new ScreenHandlerType<>(DehumidifierScreenHandler::new, FeatureFlags.VANILLA_FEATURES)
    );

    public static void registerModScreenHandlers() {
        // Trigger class loading
    }
}
