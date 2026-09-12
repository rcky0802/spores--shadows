package moldmod.block.purifier;

import net.minecraft.util.StringIdentifiable;

public enum PurifierRedstoneMode implements StringIdentifiable {
    IGNORED("ignored"),
    LOW("low"),
    HIGH("high");

    private final String name;

    PurifierRedstoneMode(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return this.name;
    }

    public PurifierRedstoneMode next() {
        return switch (this) {
            case IGNORED -> LOW;
            case LOW -> HIGH;
            case HIGH -> IGNORED;
        };
    }
}
