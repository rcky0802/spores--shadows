package moldmod;

import net.minecraft.block.BlockSetType;
import net.minecraft.block.WoodType;

import java.util.List;

public final class SporesShadowsConstants {

    private SporesShadowsConstants() {
    }

    public enum MoldStage {
        WAXED(0, "waxed"),
        TAINTED(1, "tainted"),
        MOLDY(2, "moldy"),
        ROTTEN(3, "rotten");

        private final int id;
        private final String name;

        MoldStage(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

    public record MoldyWoodType(String namespace, String name, boolean isNether, BlockSetType setType, WoodType woodType) {
        public String getLogName() {
            return isNether ? name + "_stem" : name + "_log";
        }

        public String getWoodName() {
            return isNether ? name + "_hyphae" : name + "_wood";
        }
    }

    public static final List<MoldyWoodType> WOOD_TYPES = List.of(
            new MoldyWoodType("minecraft", "oak", false, BlockSetType.OAK, WoodType.OAK),
            new MoldyWoodType("minecraft", "spruce", false, BlockSetType.SPRUCE, WoodType.SPRUCE),
            new MoldyWoodType("minecraft", "birch", false, BlockSetType.BIRCH, WoodType.BIRCH),
            new MoldyWoodType("minecraft", "jungle", false, BlockSetType.JUNGLE, WoodType.JUNGLE),
            new MoldyWoodType("minecraft", "acacia", false, BlockSetType.ACACIA, WoodType.ACACIA),
            new MoldyWoodType("minecraft", "dark_oak", false, BlockSetType.DARK_OAK, WoodType.DARK_OAK),
            new MoldyWoodType("minecraft", "mangrove", false, BlockSetType.MANGROVE, WoodType.MANGROVE),
            new MoldyWoodType("minecraft", "cherry", false, BlockSetType.CHERRY, WoodType.CHERRY),
            new MoldyWoodType("minecraft", "crimson", true, BlockSetType.CRIMSON, WoodType.CRIMSON),
            new MoldyWoodType("minecraft", "warped", true, BlockSetType.WARPED, WoodType.WARPED)
    );

    public static final List<String> BLOCK_TYPES = List.of(
            "slab", "stairs", "fence", "fence_gate", "door", "trapdoor", "pressure_plate", "button"
    );
}
