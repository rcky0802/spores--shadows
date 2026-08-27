package moldmod.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.minecraft.util.math.MathHelper;

@Config(name = moldmod.SporesShadows.MOD_ID)
public class ModConfig implements ConfigData {

    @ConfigEntry.Category("general")
    @ConfigEntry.Gui.CollapsibleObject
    public General general = new General();

    @ConfigEntry.Category("susceptibility")
    @ConfigEntry.Gui.CollapsibleObject
    public Susceptibility susceptibility = new Susceptibility();

    @ConfigEntry.Category("catalysts")
    @ConfigEntry.Gui.CollapsibleObject
    public Catalysts catalysts = new Catalysts();

    @ConfigEntry.Category("environment")
    @ConfigEntry.Gui.CollapsibleObject
    public Environment environment = new Environment();

    @ConfigEntry.Category("drops")
    @ConfigEntry.Gui.CollapsibleObject
    public Drops drops = new Drops();

    @ConfigEntry.Category("structures")
    @ConfigEntry.Gui.CollapsibleObject
    public Structures structures = new Structures();

    @ConfigEntry.Category("furnace_multipliers")
    @ConfigEntry.Gui.CollapsibleObject
    public FurnaceMultipliers furnaceMultipliers = new FurnaceMultipliers();

    @ConfigEntry.Category("toxicity")
    @ConfigEntry.Gui.CollapsibleObject
    public Toxicity toxicity = new Toxicity();

    @ConfigEntry.Category("client")
    @ConfigEntry.Gui.CollapsibleObject
    public Client client = new Client();

    public static class Client {
        @ConfigEntry.Gui.Tooltip(count = 2)
        public float mold_z_offset = 0.002f;
    }

    public static class General {
        public boolean enable_mold_growth = true;
        public float infection_threshold = 0.40f;
        @ConfigEntry.Gui.Tooltip(count = 1)
        public int scan_radius = 1; // 1 = 3x3x3, 2 = 5x5x5
        @ConfigEntry.Gui.Tooltip(count = 1)
        public boolean structures_immune = true;
        public boolean show_debug_in_chat = false;
        public int axe_scrape_damage = 1;
    }

    public static class Susceptibility {
        public float stripped_wood_multiplier = 1.4f;
        public float planks_multiplier = 0.8f;
        public float default_multiplier = 1.0f;
    }

    public static class Catalysts {
        public float mud_bonus = 0.05f;
        public float podzol_mycelium_bonus = 0.15f;
        public float fungi_bonus = 0.25f;
        public float spore_blossom_bonus = 0.80f;
        public float tainted_block_bonus = 0.05f;
        public float moldy_block_bonus = 0.10f;
        public float rotten_block_bonus = 0.20f;
    }

    public static class Environment {
        public double rain_humidity_base = 0.8;
        public double dry_humidity_base = 0.3;
        public double max_depth_modifier = 1.28;
        public double depth_modifier_per_level = 0.01;
        public double max_local_humidity_bonus = 0.60;
        public double water_adjacent_bonus = 0.15;
        public double cauldron_adjacent_bonus = 0.1;
        public int water_scan_radius = 3;
        
        public float min_temperature_survival = 0.15f;
        public float max_temperature_survival = 1.5f;
        
        @ConfigEntry.Gui.Tooltip(count = 1)
        public float cave_temperature = 0.5f;
        public int cave_start_y = 64;
        public int cave_full_y = 48;
        
        @ConfigEntry.Gui.Tooltip(count = 1)
        public float high_altitude_freezing_temperature = -0.5f;
        public int high_altitude_start_y = 128;
        public int high_altitude_full_y = 256;
    }

    public static class Drops {
        @ConfigEntry.Gui.Tooltip(count = 1)
        public float stage_2_drop_chance = 0.50f;
        @ConfigEntry.Gui.Tooltip(count = 1)
        public float stage_3_drop_chance = 0.00f;
    }

    public static class Structures {
        @ConfigEntry.Gui.CollapsibleObject
        public Category cat1_critical = new Category(10, 30, 60);
        @ConfigEntry.Gui.CollapsibleObject
        public Category cat2_high = new Category(35, 35, 20);
        @ConfigEntry.Gui.CollapsibleObject
        public Category cat3_moderate = new Category(35, 15, 0);
        @ConfigEntry.Gui.CollapsibleObject
        public Category cat4_low = new Category(5, 0, 0);
        
        public static class Category {
            public int moldy_chance;
            public int tainted_chance;
            public int rotten_chance;
            
            public Category() { }
            public Category(int m, int t, int r) {
                this.moldy_chance = m;
                this.tainted_chance = t;
                this.rotten_chance = r;
            }
        }
    }

    public static class FurnaceMultipliers {
        public float stage_0 = 1.0f;
        public float stage_1 = 0.5f;
        public float stage_2 = 0.25f;
        public float stage_3 = 0.125f;
    }

    public static class Toxicity {
        public int check_interval_ticks = 40;
        public int scan_radius = 4;
        public int threshold_nausea = 15;
        public int threshold_poison = 35;
        public int duration_nausea_ticks = 140;
        public int duration_poison_ticks = 100;
        public int nausea_amplifier = 0;
        public int poison_amplifier = 0;
    }

    @Override
    public void validatePostLoad() throws ValidationException {
        general.infection_threshold = MathHelper.clamp(general.infection_threshold, 0.0f, 2.0f);
        general.scan_radius = MathHelper.clamp(general.scan_radius, 1, 5);
        environment.water_scan_radius = MathHelper.clamp(environment.water_scan_radius, 1, 10);
        
        drops.stage_2_drop_chance = MathHelper.clamp(drops.stage_2_drop_chance, 0.0f, 1.0f);
        drops.stage_3_drop_chance = MathHelper.clamp(drops.stage_3_drop_chance, 0.0f, 1.0f);
        
        furnaceMultipliers.stage_0 = MathHelper.clamp(furnaceMultipliers.stage_0, 0.0f, 5.0f);
        furnaceMultipliers.stage_1 = MathHelper.clamp(furnaceMultipliers.stage_1, 0.0f, 5.0f);
        furnaceMultipliers.stage_2 = MathHelper.clamp(furnaceMultipliers.stage_2, 0.0f, 5.0f);
        furnaceMultipliers.stage_3 = MathHelper.clamp(furnaceMultipliers.stage_3, 0.0f, 5.0f);
        
        toxicity.check_interval_ticks = MathHelper.clamp(toxicity.check_interval_ticks, 10, 200);
        toxicity.scan_radius = MathHelper.clamp(toxicity.scan_radius, 1, 10);
    }
}

