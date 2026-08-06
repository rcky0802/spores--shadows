package moldmod.block;

import net.minecraft.block.BlockState;

public class MoldyLogicHelper {

    /**
     * Ritorna false se il blocco è cerato (WAXED == true) o se è generato naturalmente (STRUCTURAL == true).
     */
    public static boolean canBeInfected(BlockState state) {
        if (state.contains(MoldyLogBlock.WAXED) && state.get(MoldyLogBlock.WAXED)) {
            return false;
        }
        if (state.contains(MoldyLogBlock.STRUCTURAL) && state.get(MoldyLogBlock.STRUCTURAL)) {
            return false;
        }
        return true;
    }
}
