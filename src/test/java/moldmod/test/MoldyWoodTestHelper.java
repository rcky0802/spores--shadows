package moldmod.test;

import moldmod.SporesShadowsConstants;
import java.util.ArrayList;
import java.util.List;

public class MoldyWoodTestHelper {

    public record WoodProductInfo(SporesShadowsConstants.MoldyWoodType woodType, String baseName, int baseFuel) {}

    public static List<WoodProductInfo> getAllWoodProducts() {
        List<WoodProductInfo> products = new ArrayList<>();
        for (SporesShadowsConstants.MoldyWoodType woodType : SporesShadowsConstants.WOOD_TYPES) {
            String logName = woodType.getLogName();
            String woodName = woodType.getWoodName();
            String prefix = woodType.name();

            // Logs and stripped logs
            products.add(new WoodProductInfo(woodType, logName, 300));
            products.add(new WoodProductInfo(woodType, "stripped_" + logName, 300));

            // Wood and stripped wood
            if (woodName != null) {
                products.add(new WoodProductInfo(woodType, woodName, 300));
                products.add(new WoodProductInfo(woodType, "stripped_" + woodName, 300));
            }

            // Processed wood products
            products.add(new WoodProductInfo(woodType, prefix + "_planks", 300));
            products.add(new WoodProductInfo(woodType, prefix + "_stairs", 300));
            products.add(new WoodProductInfo(woodType, prefix + "_slab", 150));
            products.add(new WoodProductInfo(woodType, prefix + "_fence", 300));
            products.add(new WoodProductInfo(woodType, prefix + "_fence_gate", 300));
            products.add(new WoodProductInfo(woodType, prefix + "_door", 200));
            products.add(new WoodProductInfo(woodType, prefix + "_trapdoor", 300));
            products.add(new WoodProductInfo(woodType, prefix + "_button", 100));
            products.add(new WoodProductInfo(woodType, prefix + "_pressure_plate", 300));
        }
        return products;
    }
}
