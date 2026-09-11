package moldmod.block.dehumidifier;

import net.minecraft.util.StringIdentifiable;

public enum DehumidifierMode implements StringIdentifiable {
    DEHUMIDIFY("dehumidify"),
    HUMIDIFY("humidify");

    private final String name;

    DehumidifierMode(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return this.name;
    }

    public DehumidifierMode next() {
        return this == DEHUMIDIFY ? HUMIDIFY : DEHUMIDIFY;
    }
}
