package moldmod.client.datagen;

import moldmod.SporesShadows;
import moldmod.block.MoldyLogBlock;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class ModLootTableProvider extends FabricBlockLootTableProvider {
    public ModLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        String[] woods = moldmod.SporesShadows.WOODS;

        for (String wood : woods) {
            String logName = (wood.equals("crimson") || wood.equals("warped")) ? wood + "_stem" : wood + "_log"; String woodName = (wood.equals("crimson") || wood.equals("warped")) ? wood + "_hyphae" : wood + "_wood";
            String prefix = wood;

            Block vanillaLog = Registries.BLOCK.get(net.minecraft.util.Identifier.of("minecraft", logName));
            Block vanillaStrippedLog = Registries.BLOCK.get(net.minecraft.util.Identifier.of("minecraft", "stripped_" + logName));
            Block vanillaPlanks = Registries.BLOCK.get(net.minecraft.util.Identifier.of("minecraft", prefix + "_planks"));

            Block log = Registries.BLOCK.get(SporesShadows.id("moldy_" + logName));
            Block strippedLog = Registries.BLOCK.get(SporesShadows.id("moldy_stripped_" + logName));
            Block planks = Registries.BLOCK.get(SporesShadows.id("moldy_" + prefix + "_planks"));

            Block waxedLog = Registries.BLOCK.get(SporesShadows.id("waxed_" + logName));
            Block waxedStrippedLog = Registries.BLOCK.get(SporesShadows.id("waxed_stripped_" + logName));
            Block waxedPlanks = Registries.BLOCK.get(SporesShadows.id("waxed_" + prefix + "_planks"));

            generateMoldyLoot(log, 
                Registries.ITEM.get(SporesShadows.id("tainted_" + logName)), 
                Registries.ITEM.get(SporesShadows.id("moldy_" + logName)), 
                Registries.ITEM.get(SporesShadows.id("rotten_" + logName)), 
                vanillaLog);
            generateWaxedLoot(waxedLog,
                Registries.ITEM.get(SporesShadows.id("waxed_" + logName)),
                Registries.ITEM.get(SporesShadows.id("waxed_tainted_" + logName)),
                Registries.ITEM.get(SporesShadows.id("waxed_moldy_" + logName)),
                Registries.ITEM.get(SporesShadows.id("waxed_rotten_" + logName)));

            generateMoldyLoot(strippedLog, 
                Registries.ITEM.get(SporesShadows.id("tainted_stripped_" + logName)), 
                Registries.ITEM.get(SporesShadows.id("moldy_stripped_" + logName)), 
                Registries.ITEM.get(SporesShadows.id("rotten_stripped_" + logName)), 
                vanillaStrippedLog);
            if (waxedStrippedLog != net.minecraft.block.Blocks.AIR) {
                generateWaxedLoot(waxedStrippedLog,
                    Registries.ITEM.get(SporesShadows.id("waxed_stripped_" + logName)),
                    Registries.ITEM.get(SporesShadows.id("waxed_tainted_stripped_" + logName)),
                    Registries.ITEM.get(SporesShadows.id("waxed_moldy_stripped_" + logName)),
                    Registries.ITEM.get(SporesShadows.id("waxed_rotten_stripped_" + logName)));
            }

            if (woodName != null) {
                Block vanillaWood = Registries.BLOCK.get(net.minecraft.util.Identifier.of("minecraft", woodName));
                Block vanillaStrippedWood = Registries.BLOCK.get(net.minecraft.util.Identifier.of("minecraft", "stripped_" + woodName));
                Block woodBlock = Registries.BLOCK.get(SporesShadows.id("moldy_" + woodName));
                Block strippedWood = Registries.BLOCK.get(SporesShadows.id("moldy_stripped_" + woodName));
                Block waxedWood = Registries.BLOCK.get(SporesShadows.id("waxed_" + woodName));
                Block waxedStrippedWood = Registries.BLOCK.get(SporesShadows.id("waxed_stripped_" + woodName));

                generateMoldyLoot(woodBlock, 
                    Registries.ITEM.get(SporesShadows.id("tainted_" + woodName)), 
                    Registries.ITEM.get(SporesShadows.id("moldy_" + woodName)), 
                    Registries.ITEM.get(SporesShadows.id("rotten_" + woodName)), 
                    vanillaWood);
                generateWaxedLoot(waxedWood,
                    Registries.ITEM.get(SporesShadows.id("waxed_" + woodName)),
                    Registries.ITEM.get(SporesShadows.id("waxed_tainted_" + woodName)),
                    Registries.ITEM.get(SporesShadows.id("waxed_moldy_" + woodName)),
                    Registries.ITEM.get(SporesShadows.id("waxed_rotten_" + woodName)));

                generateMoldyLoot(strippedWood, 
                    Registries.ITEM.get(SporesShadows.id("tainted_stripped_" + woodName)), 
                    Registries.ITEM.get(SporesShadows.id("moldy_stripped_" + woodName)), 
                    Registries.ITEM.get(SporesShadows.id("rotten_stripped_" + woodName)), 
                    vanillaStrippedWood);
                if (waxedStrippedWood != net.minecraft.block.Blocks.AIR) {
                    generateWaxedLoot(waxedStrippedWood,
                        Registries.ITEM.get(SporesShadows.id("waxed_stripped_" + woodName)),
                        Registries.ITEM.get(SporesShadows.id("waxed_tainted_stripped_" + woodName)),
                        Registries.ITEM.get(SporesShadows.id("waxed_moldy_stripped_" + woodName)),
                        Registries.ITEM.get(SporesShadows.id("waxed_rotten_stripped_" + woodName)));
                }
            }

            generateMoldyLoot(planks, 
                Registries.ITEM.get(SporesShadows.id("tainted_" + prefix + "_planks")), 
                Registries.ITEM.get(SporesShadows.id("moldy_" + prefix + "_planks")), 
                Registries.ITEM.get(SporesShadows.id("rotten_" + prefix + "_planks")), 
                vanillaPlanks);
            if (waxedPlanks != net.minecraft.block.Blocks.AIR) {
                generateWaxedLoot(waxedPlanks,
                    Registries.ITEM.get(SporesShadows.id("waxed_" + prefix + "_planks")),
                    Registries.ITEM.get(SporesShadows.id("waxed_tainted_" + prefix + "_planks")),
                    Registries.ITEM.get(SporesShadows.id("waxed_moldy_" + prefix + "_planks")),
                    Registries.ITEM.get(SporesShadows.id("waxed_rotten_" + prefix + "_planks")));
            }

            String[] suffixes = {"_stairs", "_slab", "_fence", "_fence_gate", "_door", "_trapdoor", "_pressure_plate", "_button"};
            for (String suffix : suffixes) {
                Block vanillaPart = Registries.BLOCK.get(net.minecraft.util.Identifier.of("minecraft", prefix + suffix));
                Block partBlock = Registries.BLOCK.get(SporesShadows.id("moldy_" + prefix + suffix));
                Block waxedPart = Registries.BLOCK.get(SporesShadows.id("waxed_" + prefix + suffix));
                if (partBlock != net.minecraft.block.Blocks.AIR) {
                    generateMoldyLoot(partBlock, 
                        Registries.ITEM.get(SporesShadows.id("tainted_" + prefix + suffix)), 
                        Registries.ITEM.get(SporesShadows.id("moldy_" + prefix + suffix)), 
                        Registries.ITEM.get(SporesShadows.id("rotten_" + prefix + suffix)), 
                        vanillaPart);
                }
                if (waxedPart != net.minecraft.block.Blocks.AIR) {
                    generateWaxedLoot(waxedPart,
                        Registries.ITEM.get(SporesShadows.id("waxed_" + prefix + suffix)),
                        Registries.ITEM.get(SporesShadows.id("waxed_tainted_" + prefix + suffix)),
                        Registries.ITEM.get(SporesShadows.id("waxed_moldy_" + prefix + suffix)),
                        Registries.ITEM.get(SporesShadows.id("waxed_rotten_" + prefix + suffix)));
                }
            }
        }
    }
    
    private void generateMoldyLoot(Block baseBlock, net.minecraft.item.Item stage1, net.minecraft.item.Item stage2, net.minecraft.item.Item stage3, Block vanillaBlock) {
        net.minecraft.loot.condition.LootCondition.Builder isWaxed = net.minecraft.loot.condition.BlockStatePropertyLootCondition.builder(baseBlock)
            .properties(net.minecraft.predicate.StatePredicate.Builder.create().exactMatch(MoldyLogBlock.WAXED, true));
            
        addDrop(baseBlock, (block) -> net.minecraft.loot.LootTable.builder()
            .pool(net.minecraft.loot.LootPool.builder()
                .rolls(net.minecraft.loot.provider.number.ConstantLootNumberProvider.create(1.0F))
                .with(net.minecraft.loot.entry.AlternativeEntry.builder(
                    net.minecraft.loot.entry.ItemEntry.builder(stage3)
                        .conditionally(net.minecraft.loot.condition.BlockStatePropertyLootCondition.builder(baseBlock).properties(net.minecraft.predicate.StatePredicate.Builder.create().exactMatch(MoldyLogBlock.STAGE, 3)))
                        .conditionally(
                            net.minecraft.loot.condition.AnyOfLootCondition.builder(
                                this.createSilkTouchCondition(),
                                isWaxed
                            )
                        ),
                    net.minecraft.loot.entry.ItemEntry.builder(stage2)
                        .conditionally(net.minecraft.loot.condition.BlockStatePropertyLootCondition.builder(baseBlock).properties(net.minecraft.predicate.StatePredicate.Builder.create().exactMatch(MoldyLogBlock.STAGE, 2)))
                        .conditionally(
                            net.minecraft.loot.condition.AnyOfLootCondition.builder(
                                this.createSilkTouchCondition(),
                                isWaxed,
                                net.minecraft.loot.condition.RandomChanceLootCondition.builder(0.5F)
                            )
                        ),
                    net.minecraft.loot.entry.ItemEntry.builder(stage1)
                        .conditionally(net.minecraft.loot.condition.BlockStatePropertyLootCondition.builder(baseBlock).properties(net.minecraft.predicate.StatePredicate.Builder.create().exactMatch(MoldyLogBlock.STAGE, 1))),
                    net.minecraft.loot.entry.ItemEntry.builder(vanillaBlock)
                        .conditionally(net.minecraft.loot.condition.BlockStatePropertyLootCondition.builder(baseBlock).properties(net.minecraft.predicate.StatePredicate.Builder.create().exactMatch(MoldyLogBlock.STAGE, 0)))
                ))
                .apply(net.minecraft.loot.function.CopyStateLootFunction.builder(baseBlock).addProperty(MoldyLogBlock.WAXED))
            )
        );
    }
    
    private void generateWaxedLoot(Block baseBlock, net.minecraft.item.Item stage0, net.minecraft.item.Item stage1, net.minecraft.item.Item stage2, net.minecraft.item.Item stage3) {
        addDrop(baseBlock, (block) -> net.minecraft.loot.LootTable.builder()
            .pool(net.minecraft.loot.LootPool.builder()
                .rolls(net.minecraft.loot.provider.number.ConstantLootNumberProvider.create(1.0F))
                .with(net.minecraft.loot.entry.AlternativeEntry.builder(
                    net.minecraft.loot.entry.ItemEntry.builder(stage3)
                        .conditionally(net.minecraft.loot.condition.BlockStatePropertyLootCondition.builder(baseBlock).properties(net.minecraft.predicate.StatePredicate.Builder.create().exactMatch(MoldyLogBlock.STAGE, 3))),
                    net.minecraft.loot.entry.ItemEntry.builder(stage2)
                        .conditionally(net.minecraft.loot.condition.BlockStatePropertyLootCondition.builder(baseBlock).properties(net.minecraft.predicate.StatePredicate.Builder.create().exactMatch(MoldyLogBlock.STAGE, 2))),
                    net.minecraft.loot.entry.ItemEntry.builder(stage1)
                        .conditionally(net.minecraft.loot.condition.BlockStatePropertyLootCondition.builder(baseBlock).properties(net.minecraft.predicate.StatePredicate.Builder.create().exactMatch(MoldyLogBlock.STAGE, 1))),
                    net.minecraft.loot.entry.ItemEntry.builder(stage0)
                        .conditionally(net.minecraft.loot.condition.BlockStatePropertyLootCondition.builder(baseBlock).properties(net.minecraft.predicate.StatePredicate.Builder.create().exactMatch(MoldyLogBlock.STAGE, 0)))
                ))
                .apply(net.minecraft.loot.function.CopyStateLootFunction.builder(baseBlock).addProperty(MoldyLogBlock.WAXED))
            )
        );
    }
}

