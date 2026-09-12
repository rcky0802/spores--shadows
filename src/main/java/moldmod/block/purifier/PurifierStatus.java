package moldmod.block.purifier;

import net.minecraft.util.StringIdentifiable;

public enum PurifierStatus implements StringIdentifiable {
    OFF("off"),
    RUNNING("running"),
    FILTER_DEPLETED("filter_depleted");

    private final String name;

    PurifierStatus(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return this.name;
    }
}
