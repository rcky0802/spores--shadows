package moldmod.client.datagen;

import moldmod.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.registerLog(ModBlocks.MOLDY_OAK_LOG_STAGE_1).log(ModBlocks.MOLDY_OAK_LOG_STAGE_1);
        blockStateModelGenerator.registerLog(ModBlocks.MOLDY_OAK_LOG_STAGE_2).log(ModBlocks.MOLDY_OAK_LOG_STAGE_2);
        blockStateModelGenerator.registerLog(ModBlocks.MOLDY_OAK_LOG_STAGE_3).log(ModBlocks.MOLDY_OAK_LOG_STAGE_3);
        blockStateModelGenerator.registerLog(ModBlocks.PLACED_OAK_LOG).log(net.minecraft.block.Blocks.OAK_LOG);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
    }
}
