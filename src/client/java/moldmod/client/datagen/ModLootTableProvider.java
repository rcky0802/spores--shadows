package moldmod.client.datagen;

import moldmod.block.ModBlocks;
import moldmod.block.MoldyBlock;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
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

            if (waxedBlock != null && waxedBlock != net.minecraft.block.Blocks.AIR) {
                generateWaxedLoot(waxedBlock, items.get(0), items.get(2), items.get(4), items.get(6));
            }
        }
    }
    
    private void generateMoldyLoot(Block baseBlock, Item stage1, Item stage2, Item stage3, Block vanillaBlock) {
        net.minecraft.loot.condition.LootCondition.Builder isWaxed = net.minecraft.loot.condition.BlockStatePropertyLootCondition.builder(baseBlock)
            .properties(net.minecraft.predicate.StatePredicate.Builder.create().exactMatch(MoldyBlock.WAXED, true));
            
        addDrop(baseBlock, (block) -> net.minecraft.loot.LootTable.builder()
            .pool(net.minecraft.loot.LootPool.builder()
                .rolls(net.minecraft.loot.provider.number.ConstantLootNumberProvider.create(1.0F))
                .with(net.minecraft.loot.entry.AlternativeEntry.builder(
                    net.minecraft.loot.entry.ItemEntry.builder(stage3)
                        .conditionally(net.minecraft.loot.condition.BlockStatePropertyLootCondition.builder(baseBlock).properties(net.minecraft.predicate.StatePredicate.Builder.create().exactMatch(MoldyBlock.STAGE, moldmod.SporesShadowsConstants.MoldStage.ROTTEN.getId())))
                        .conditionally(
                            net.minecraft.loot.condition.AnyOfLootCondition.builder(
                                this.createSilkTouchCondition(), isWaxed, net.minecraft.loot.condition.RandomChanceLootCondition.builder(me.shedaniel.autoconfig.AutoConfig.getConfigHolder(moldmod.config.ModConfig.class).getConfig().drops.stage_3_drop_chance) )
                        ),
                    net.minecraft.loot.entry.ItemEntry.builder(stage2)
                        .conditionally(net.minecraft.loot.condition.BlockStatePropertyLootCondition.builder(baseBlock).properties(net.minecraft.predicate.StatePredicate.Builder.create().exactMatch(MoldyBlock.STAGE, moldmod.SporesShadowsConstants.MoldStage.MOLDY.getId())))
                        .conditionally(
                            net.minecraft.loot.condition.AnyOfLootCondition.builder(
                                this.createSilkTouchCondition(),
                                isWaxed,
                                net.minecraft.loot.condition.RandomChanceLootCondition.builder(me.shedaniel.autoconfig.AutoConfig.getConfigHolder(moldmod.config.ModConfig.class).getConfig().drops.stage_2_drop_chance)
                            )
                        ),
                    net.minecraft.loot.entry.ItemEntry.builder(stage1)
                        .conditionally(net.minecraft.loot.condition.BlockStatePropertyLootCondition.builder(baseBlock).properties(net.minecraft.predicate.StatePredicate.Builder.create().exactMatch(MoldyBlock.STAGE, moldmod.SporesShadowsConstants.MoldStage.TAINTED.getId()))),
                    net.minecraft.loot.entry.ItemEntry.builder(vanillaBlock)
                        .conditionally(net.minecraft.loot.condition.BlockStatePropertyLootCondition.builder(baseBlock).properties(net.minecraft.predicate.StatePredicate.Builder.create().exactMatch(MoldyBlock.STAGE, moldmod.SporesShadowsConstants.MoldStage.WAXED.getId())))
                ))
                .apply(net.minecraft.loot.function.CopyStateLootFunction.builder(baseBlock).addProperty(MoldyBlock.WAXED))
            )
        );
    }
    
    private void generateWaxedLoot(Block baseBlock, Item stage0, Item stage1, Item stage2, Item stage3) {
        addDrop(baseBlock, (block) -> net.minecraft.loot.LootTable.builder()
            .pool(net.minecraft.loot.LootPool.builder()
                .rolls(net.minecraft.loot.provider.number.ConstantLootNumberProvider.create(1.0F))
                .with(net.minecraft.loot.entry.AlternativeEntry.builder(
                    net.minecraft.loot.entry.ItemEntry.builder(stage3)
                        .conditionally(net.minecraft.loot.condition.BlockStatePropertyLootCondition.builder(baseBlock).properties(net.minecraft.predicate.StatePredicate.Builder.create().exactMatch(MoldyBlock.STAGE, moldmod.SporesShadowsConstants.MoldStage.ROTTEN.getId()))),
                    net.minecraft.loot.entry.ItemEntry.builder(stage2)
                        .conditionally(net.minecraft.loot.condition.BlockStatePropertyLootCondition.builder(baseBlock).properties(net.minecraft.predicate.StatePredicate.Builder.create().exactMatch(MoldyBlock.STAGE, moldmod.SporesShadowsConstants.MoldStage.MOLDY.getId()))),
                    net.minecraft.loot.entry.ItemEntry.builder(stage1)
                        .conditionally(net.minecraft.loot.condition.BlockStatePropertyLootCondition.builder(baseBlock).properties(net.minecraft.predicate.StatePredicate.Builder.create().exactMatch(MoldyBlock.STAGE, moldmod.SporesShadowsConstants.MoldStage.TAINTED.getId()))),
                    net.minecraft.loot.entry.ItemEntry.builder(stage0)
                        .conditionally(net.minecraft.loot.condition.BlockStatePropertyLootCondition.builder(baseBlock).properties(net.minecraft.predicate.StatePredicate.Builder.create().exactMatch(MoldyBlock.STAGE, moldmod.SporesShadowsConstants.MoldStage.WAXED.getId())))
                ))
                .apply(net.minecraft.loot.function.CopyStateLootFunction.builder(baseBlock).addProperty(MoldyBlock.WAXED))
            )
        );
    }
}
