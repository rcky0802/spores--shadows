package moldmod.client.datagen.loot;

import me.shedaniel.autoconfig.AutoConfig;
import moldmod.SporesShadowsConstants.MoldStage;
import moldmod.block.ModBlocks;
import moldmod.block.core.MoldyBlock;
import moldmod.config.ModConfig;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.AnyOfLootCondition;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.AlternativeEntry;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.CopyStateLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
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

            if (vanillaBlock == Blocks.BOOKSHELF) {
                generateBookshelfLoot(moldyBlock, items);
                if (waxedBlock != null && waxedBlock != Blocks.AIR) {
                    generateWaxedBookshelfLoot(waxedBlock, items);
                }
                continue;
            }

            generateMoldyLoot(moldyBlock, items.get(1), items.get(3), items.get(5), vanillaBlock);

            if (waxedBlock != null && waxedBlock != Blocks.AIR) {
                generateWaxedLoot(waxedBlock, items.get(0), items.get(2), items.get(4), items.get(6));
            }
        }

        addDrop(ModBlocks.SPORE_DETECTOR);
        addDrop(ModBlocks.MOISTURE_DETECTOR);
        addDrop(ModBlocks.DEHUMIDIFIER);
        addDrop(ModBlocks.AIR_PURIFIER);
    }
    
    private void generateMoldyLoot(Block baseBlock, Item stage1, Item stage2, Item stage3, Block vanillaBlock) {
        LootCondition.Builder isWaxed = BlockStatePropertyLootCondition.builder(baseBlock)
            .properties(StatePredicate.Builder.create().exactMatch(MoldyBlock.WAXED, true));

        float stage3Chance = AutoConfig.getConfigHolder(ModConfig.class).getConfig().drops.stage_3_drop_chance;
        float stage2Chance = AutoConfig.getConfigHolder(ModConfig.class).getConfig().drops.stage_2_drop_chance;
            
        LootPool.Builder pool = LootPool.builder()
            .rolls(ConstantLootNumberProvider.create(1.0F));

        if (baseBlock instanceof DoorBlock) {
            pool.conditionally(BlockStatePropertyLootCondition.builder(baseBlock)
                .properties(StatePredicate.Builder.create().exactMatch(DoorBlock.HALF, DoubleBlockHalf.LOWER)));
        }

        pool.with(AlternativeEntry.builder(
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
        .apply(CopyStateLootFunction.builder(baseBlock).addProperty(MoldyBlock.WAXED));

        addDrop(baseBlock, (block) -> LootTable.builder().pool(pool));
    }
    
    private void generateWaxedLoot(Block baseBlock, Item stage0, Item stage1, Item stage2, Item stage3) {
        LootPool.Builder pool = LootPool.builder()
            .rolls(ConstantLootNumberProvider.create(1.0F));

        if (baseBlock instanceof DoorBlock) {
            pool.conditionally(BlockStatePropertyLootCondition.builder(baseBlock)
                .properties(StatePredicate.Builder.create().exactMatch(DoorBlock.HALF, DoubleBlockHalf.LOWER)));
        }

        pool.with(AlternativeEntry.builder(
            ItemEntry.builder(stage3)
                .conditionally(BlockStatePropertyLootCondition.builder(baseBlock).properties(StatePredicate.Builder.create().exactMatch(MoldyBlock.STAGE, MoldStage.ROTTEN.getId()))),
            ItemEntry.builder(stage2)
                .conditionally(BlockStatePropertyLootCondition.builder(baseBlock).properties(StatePredicate.Builder.create().exactMatch(MoldyBlock.STAGE, MoldStage.MOLDY.getId()))),
            ItemEntry.builder(stage1)
                .conditionally(BlockStatePropertyLootCondition.builder(baseBlock).properties(StatePredicate.Builder.create().exactMatch(MoldyBlock.STAGE, MoldStage.TAINTED.getId()))),
            ItemEntry.builder(stage0)
                .conditionally(BlockStatePropertyLootCondition.builder(baseBlock).properties(StatePredicate.Builder.create().exactMatch(MoldyBlock.STAGE, MoldStage.WAXED.getId())))
        ))
        .apply(CopyStateLootFunction.builder(baseBlock).addProperty(MoldyBlock.WAXED));

        addDrop(baseBlock, (block) -> LootTable.builder().pool(pool));
    }

    private void generateBookshelfLoot(Block baseBlock, List<Item> items) {
        LootPool.Builder silkPool = LootPool.builder()
            .rolls(ConstantLootNumberProvider.create(1.0F))
            .conditionally(this.createSilkTouchCondition())
            .with(AlternativeEntry.builder(
                ItemEntry.builder(items.get(6))
                    .conditionally(BlockStatePropertyLootCondition.builder(baseBlock).properties(StatePredicate.Builder.create().exactMatch(MoldyBlock.STAGE, 3).exactMatch(MoldyBlock.WAXED, true))),
                ItemEntry.builder(items.get(5))
                    .conditionally(BlockStatePropertyLootCondition.builder(baseBlock).properties(StatePredicate.Builder.create().exactMatch(MoldyBlock.STAGE, 3))),
                ItemEntry.builder(items.get(4))
                    .conditionally(BlockStatePropertyLootCondition.builder(baseBlock).properties(StatePredicate.Builder.create().exactMatch(MoldyBlock.STAGE, 2).exactMatch(MoldyBlock.WAXED, true))),
                ItemEntry.builder(items.get(3))
                    .conditionally(BlockStatePropertyLootCondition.builder(baseBlock).properties(StatePredicate.Builder.create().exactMatch(MoldyBlock.STAGE, 2))),
                ItemEntry.builder(items.get(2))
                    .conditionally(BlockStatePropertyLootCondition.builder(baseBlock).properties(StatePredicate.Builder.create().exactMatch(MoldyBlock.STAGE, 1).exactMatch(MoldyBlock.WAXED, true))),
                ItemEntry.builder(items.get(1))
                    .conditionally(BlockStatePropertyLootCondition.builder(baseBlock).properties(StatePredicate.Builder.create().exactMatch(MoldyBlock.STAGE, 1))),
                ItemEntry.builder(items.get(0))
                    .conditionally(BlockStatePropertyLootCondition.builder(baseBlock).properties(StatePredicate.Builder.create().exactMatch(MoldyBlock.STAGE, 0).exactMatch(MoldyBlock.WAXED, true))),
                ItemEntry.builder(Blocks.BOOKSHELF)
                    .conditionally(BlockStatePropertyLootCondition.builder(baseBlock).properties(StatePredicate.Builder.create().exactMatch(MoldyBlock.STAGE, 0)))
            ));

        LootPool.Builder bookPool = LootPool.builder()
            .rolls(ConstantLootNumberProvider.create(1.0F))
            .conditionally(this.createWithoutSilkTouchCondition())
            .with(AlternativeEntry.builder(
                ItemEntry.builder(Items.BOOK)
                    .conditionally(BlockStatePropertyLootCondition.builder(baseBlock).properties(StatePredicate.Builder.create().exactMatch(MoldyBlock.STAGE, 0)))
                    .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(3.0F))),
                ItemEntry.builder(Items.BOOK)
                    .conditionally(BlockStatePropertyLootCondition.builder(baseBlock).properties(StatePredicate.Builder.create().exactMatch(MoldyBlock.STAGE, 1)))
                    .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(2.0F))),
                ItemEntry.builder(Items.BOOK)
                    .conditionally(BlockStatePropertyLootCondition.builder(baseBlock).properties(StatePredicate.Builder.create().exactMatch(MoldyBlock.STAGE, 2)))
                    .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F)))
            ));

        addDrop(baseBlock, (block) -> LootTable.builder().pool(silkPool).pool(bookPool));
    }

    private void generateWaxedBookshelfLoot(Block baseBlock, List<Item> items) {
        LootPool.Builder silkPool = LootPool.builder()
            .rolls(ConstantLootNumberProvider.create(1.0F))
            .conditionally(this.createSilkTouchCondition())
            .with(AlternativeEntry.builder(
                ItemEntry.builder(items.get(6))
                    .conditionally(BlockStatePropertyLootCondition.builder(baseBlock).properties(StatePredicate.Builder.create().exactMatch(MoldyBlock.STAGE, 3))),
                ItemEntry.builder(items.get(4))
                    .conditionally(BlockStatePropertyLootCondition.builder(baseBlock).properties(StatePredicate.Builder.create().exactMatch(MoldyBlock.STAGE, 2))),
                ItemEntry.builder(items.get(2))
                    .conditionally(BlockStatePropertyLootCondition.builder(baseBlock).properties(StatePredicate.Builder.create().exactMatch(MoldyBlock.STAGE, 1))),
                ItemEntry.builder(items.get(0))
                    .conditionally(BlockStatePropertyLootCondition.builder(baseBlock).properties(StatePredicate.Builder.create().exactMatch(MoldyBlock.STAGE, 0)))
            ));

        LootPool.Builder bookPool = LootPool.builder()
            .rolls(ConstantLootNumberProvider.create(1.0F))
            .conditionally(this.createWithoutSilkTouchCondition())
            .with(AlternativeEntry.builder(
                ItemEntry.builder(Items.BOOK)
                    .conditionally(BlockStatePropertyLootCondition.builder(baseBlock).properties(StatePredicate.Builder.create().exactMatch(MoldyBlock.STAGE, 0)))
                    .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(3.0F))),
                ItemEntry.builder(Items.BOOK)
                    .conditionally(BlockStatePropertyLootCondition.builder(baseBlock).properties(StatePredicate.Builder.create().exactMatch(MoldyBlock.STAGE, 1)))
                    .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(2.0F))),
                ItemEntry.builder(Items.BOOK)
                    .conditionally(BlockStatePropertyLootCondition.builder(baseBlock).properties(StatePredicate.Builder.create().exactMatch(MoldyBlock.STAGE, 2)))
                    .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F)))
            ));

        addDrop(baseBlock, (block) -> LootTable.builder().pool(silkPool).pool(bookPool));
    }
}
