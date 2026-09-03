package moldmod.client.datagen;

import me.shedaniel.autoconfig.AutoConfig;
import moldmod.SporesShadowsConstants.MoldStage;
import moldmod.block.ModBlocks;
import moldmod.block.MoldyBlock;
import moldmod.config.ModConfig;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.AnyOfLootCondition;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.AlternativeEntry;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.CopyStateLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.registry.RegistryWrapper;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ModLootTableProvider extends FabricBlockLootTableProvider {
    public ModLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        for (Map.Entry<Block, Block> entry : ModBlocks.VANILLA_TO_MOLDY.entrySet()) {
            Block vanillaBlock = entry.getKey();
            Block moldyBlock = entry.getValue();
            Block waxedBlock = ModBlocks.MOLDY_TO_WAXED.get(moldyBlock);
            List<Item> items = ModBlocks.MOLDY_ITEMS_BY_BLOCK.get(moldyBlock);

            if (items == null || items.size() < 7) continue;

            generateMoldyLoot(moldyBlock, items.get(1), items.get(3), items.get(5), vanillaBlock);

            if (waxedBlock != null && waxedBlock != Blocks.AIR) {
                generateWaxedLoot(waxedBlock, items.get(0), items.get(2), items.get(4), items.get(6));
            }
        }
    }
    
    private void generateMoldyLoot(Block baseBlock, Item stage1, Item stage2, Item stage3, Block vanillaBlock) {
        LootCondition.Builder isWaxed = BlockStatePropertyLootCondition.builder(baseBlock)
            .properties(StatePredicate.Builder.create().exactMatch(MoldyBlock.WAXED, true));

        float stage3Chance = AutoConfig.getConfigHolder(ModConfig.class).getConfig().drops.stage_3_drop_chance;
        float stage2Chance = AutoConfig.getConfigHolder(ModConfig.class).getConfig().drops.stage_2_drop_chance;
            
        addDrop(baseBlock, (block) -> LootTable.builder()
            .pool(LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(1.0F))
                .with(AlternativeEntry.builder(
                    ItemEntry.builder(stage3)
                        .conditionally(BlockStatePropertyLootCondition.builder(baseBlock).properties(StatePredicate.Builder.create().exactMatch(MoldyBlock.STAGE, MoldStage.ROTTEN.getId())))
                        .conditionally(
                            AnyOfLootCondition.builder(
                                this.createSilkTouchCondition(), isWaxed, RandomChanceLootCondition.builder(stage3Chance)
                            )
                        ),
                    ItemEntry.builder(stage2)
                        .conditionally(BlockStatePropertyLootCondition.builder(baseBlock).properties(StatePredicate.Builder.create().exactMatch(MoldyBlock.STAGE, MoldStage.MOLDY.getId())))
                        .conditionally(
                            AnyOfLootCondition.builder(
                                this.createSilkTouchCondition(),
                                isWaxed,
                                RandomChanceLootCondition.builder(stage2Chance)
                            )
                        ),
                    ItemEntry.builder(stage1)
                        .conditionally(BlockStatePropertyLootCondition.builder(baseBlock).properties(StatePredicate.Builder.create().exactMatch(MoldyBlock.STAGE, MoldStage.TAINTED.getId()))),
                    ItemEntry.builder(vanillaBlock)
                        .conditionally(BlockStatePropertyLootCondition.builder(baseBlock).properties(StatePredicate.Builder.create().exactMatch(MoldyBlock.STAGE, MoldStage.WAXED.getId())))
                ))
                .apply(CopyStateLootFunction.builder(baseBlock).addProperty(MoldyBlock.WAXED))
            )
        );
    }
    
    private void generateWaxedLoot(Block baseBlock, Item stage0, Item stage1, Item stage2, Item stage3) {
        addDrop(baseBlock, (block) -> LootTable.builder()
            .pool(LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(1.0F))
                .with(AlternativeEntry.builder(
                    ItemEntry.builder(stage3)
                        .conditionally(BlockStatePropertyLootCondition.builder(baseBlock).properties(StatePredicate.Builder.create().exactMatch(MoldyBlock.STAGE, MoldStage.ROTTEN.getId()))),
                    ItemEntry.builder(stage2)
                        .conditionally(BlockStatePropertyLootCondition.builder(baseBlock).properties(StatePredicate.Builder.create().exactMatch(MoldyBlock.STAGE, MoldStage.MOLDY.getId()))),
                    ItemEntry.builder(stage1)
                        .conditionally(BlockStatePropertyLootCondition.builder(baseBlock).properties(StatePredicate.Builder.create().exactMatch(MoldyBlock.STAGE, MoldStage.TAINTED.getId()))),
                    ItemEntry.builder(stage0)
                        .conditionally(BlockStatePropertyLootCondition.builder(baseBlock).properties(StatePredicate.Builder.create().exactMatch(MoldyBlock.STAGE, MoldStage.WAXED.getId())))
                ))
                .apply(CopyStateLootFunction.builder(baseBlock).addProperty(MoldyBlock.WAXED))
            )
        );
    }
}
