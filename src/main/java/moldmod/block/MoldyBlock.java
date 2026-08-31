package moldmod.block;

import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;

/**
 * Common interface for all moldy block variations.
 * Provides standard block state properties (STAGE, WAXED, STRUCTURAL).
 */
public interface MoldyBlock {
    IntProperty STAGE = IntProperty.of("stage", 0, 3);
    BooleanProperty WAXED = BooleanProperty.of("waxed");
    BooleanProperty STRUCTURAL = BooleanProperty.of("structural");
}
