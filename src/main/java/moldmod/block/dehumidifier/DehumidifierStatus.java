package moldmod.block.dehumidifier;

import net.minecraft.util.StringIdentifiable;

public enum DehumidifierStatus implements StringIdentifiable {
    OFF("off"),
    RUNNING("running"),
    FULL("full");

    private final String name;

    DehumidifierStatus(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return this.name;
    }
}
