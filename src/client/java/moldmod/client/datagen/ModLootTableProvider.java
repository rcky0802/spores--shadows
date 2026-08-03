package moldmod.client.datagen;

import moldmod.block.ModBlocks;
import moldmod.block.MoldyOakLogBlock;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class ModLootTableProvider extends FabricBlockLootTableProvider {
    public ModLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        generateMoldyLoot(ModBlocks.MOLDY_OAK_LOG, ModBlocks.TAINTED_OAK_LOG, ModBlocks.MOLDY_OAK_LOG_ITEM, ModBlocks.ROTTEN_OAK_LOG, Blocks.OAK_LOG);
        generateMoldyLoot(ModBlocks.MOLDY_STRIPPED_OAK_LOG, ModBlocks.TAINTED_STRIPPED_OAK_LOG, ModBlocks.MOLDY_STRIPPED_OAK_LOG_ITEM, ModBlocks.ROTTEN_STRIPPED_OAK_LOG, Blocks.STRIPPED_OAK_LOG);
        generateMoldyLoot(ModBlocks.MOLDY_OAK_WOOD, ModBlocks.TAINTED_OAK_WOOD, ModBlocks.MOLDY_OAK_WOOD_ITEM, ModBlocks.ROTTEN_OAK_WOOD, Blocks.OAK_WOOD);
        generateMoldyLoot(ModBlocks.MOLDY_STRIPPED_OAK_WOOD, ModBlocks.TAINTED_STRIPPED_OAK_WOOD, ModBlocks.MOLDY_STRIPPED_OAK_WOOD_ITEM, ModBlocks.ROTTEN_STRIPPED_OAK_WOOD, Blocks.STRIPPED_OAK_WOOD);
        generateMoldyLoot(ModBlocks.MOLDY_OAK_PLANKS, ModBlocks.TAINTED_OAK_PLANKS, ModBlocks.MOLDY_OAK_PLANKS_ITEM, ModBlocks.ROTTEN_OAK_PLANKS, Blocks.OAK_PLANKS);
    }
    
    private void generateMoldyLoot(net.minecraft.block.Block baseBlock, net.minecraft.item.Item stage1, net.minecraft.item.Item stage2, net.minecraft.item.Item stage3, net.minecraft.block.Block vanillaBlock) {
        addDrop(baseBlock, (block) -> net.minecraft.loot.LootTable.builder()
            .pool(net.minecraft.loot.LootPool.builder()
                .rolls(net.minecraft.loot.provider.number.ConstantLootNumberProvider.create(1.0F))
                .with(net.minecraft.loot.entry.AlternativeEntry.builder(
                    net.minecraft.loot.entry.ItemEntry.builder(stage3)
                        .conditionally(net.minecraft.loot.condition.BlockStatePropertyLootCondition.builder(baseBlock).properties(net.minecraft.predicate.StatePredicate.Builder.create().exactMatch(MoldyOakLogBlock.STAGE, 3)))
                        .conditionally(this.createSilkTouchCondition()),
                    net.minecraft.loot.entry.ItemEntry.builder(stage2)
                        .conditionally(net.minecraft.loot.condition.BlockStatePropertyLootCondition.builder(baseBlock).properties(net.minecraft.predicate.StatePredicate.Builder.create().exactMatch(MoldyOakLogBlock.STAGE, 2)))
                        .conditionally(
                            net.minecraft.loot.condition.AnyOfLootCondition.builder(
                                this.createSilkTouchCondition(),
                                net.minecraft.loot.condition.RandomChanceLootCondition.builder(0.5F)
                            )
                        ),
                    net.minecraft.loot.entry.ItemEntry.builder(stage1)
                        .conditionally(net.minecraft.loot.condition.BlockStatePropertyLootCondition.builder(baseBlock).properties(net.minecraft.predicate.StatePredicate.Builder.create().exactMatch(MoldyOakLogBlock.STAGE, 1))),
                    net.minecraft.loot.entry.ItemEntry.builder(vanillaBlock)
                        .conditionally(net.minecraft.loot.condition.BlockStatePropertyLootCondition.builder(baseBlock).properties(net.minecraft.predicate.StatePredicate.Builder.create().exactMatch(MoldyOakLogBlock.STAGE, 0)))
                ))
                .apply(net.minecraft.loot.function.CopyStateLootFunction.builder(baseBlock).addProperty(MoldyOakLogBlock.WAXED))
            )
        );
    }
}
