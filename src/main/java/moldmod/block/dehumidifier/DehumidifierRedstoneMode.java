package moldmod.block.dehumidifier;

import net.minecraft.util.StringIdentifiable;

public enum DehumidifierRedstoneMode implements StringIdentifiable {
    IGNORED("ignored"),
    LOW("low"),
    HIGH("high");

    private final String name;

    DehumidifierRedstoneMode(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return this.name;
    }

    public DehumidifierRedstoneMode next() {
        return switch (this) {
            case IGNORED -> LOW;
            case LOW -> HIGH;
            case HIGH -> IGNORED;
        };
    }
}
