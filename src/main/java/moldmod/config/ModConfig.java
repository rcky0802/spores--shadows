package moldmod.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "spores_shadows")
public class ModConfig implements ConfigData {

    @ConfigEntry.Category("general")
    @ConfigEntry.Gui.Tooltip
    public boolean enableMoldSpread = true;

    @ConfigEntry.Category("general")
    @ConfigEntry.Gui.Tooltip(count = 2)
    public float globalMoldRiskMultiplier = 1.0f;

    @ConfigEntry.Category("debug")
    @ConfigEntry.Gui.Tooltip
    public boolean showDebugInChat = false;

    public ModConfig() {
    }
}
