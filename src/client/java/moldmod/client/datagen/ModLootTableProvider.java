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
        String[] woods = {"oak", "spruce", "birch", "jungle", "acacia", "dark_oak", "mangrove", "cherry", "bamboo"};

        for (String wood : woods) {
            boolean isBamboo = wood.equals("bamboo");
            String logName = isBamboo ? "bamboo_block" : wood + "_log";
            String woodName = isBamboo ? null : wood + "_wood";
            String prefix = isBamboo ? "bamboo" : wood;

            Block vanillaLog = Registries.BLOCK.get(net.minecraft.util.Identifier.of("minecraft", logName));
            Block vanillaStrippedLog = Registries.BLOCK.get(net.minecraft.util.Identifier.of("minecraft", "stripped_" + logName));
            Block vanillaPlanks = Registries.BLOCK.get(net.minecraft.util.Identifier.of("minecraft", prefix + "_planks"));

            Block log = Registries.BLOCK.get(SporesShadows.id("moldy_" + logName));
            Block strippedLog = Registries.BLOCK.get(SporesShadows.id("moldy_stripped_" + logName));
            Block planks = Registries.BLOCK.get(SporesShadows.id("moldy_" + prefix + "_planks"));

            generateMoldyLoot(log, 
                Registries.ITEM.get(SporesShadows.id("tainted_" + logName)), 
                Registries.ITEM.get(SporesShadows.id("moldy_" + logName)), 
                Registries.ITEM.get(SporesShadows.id("rotten_" + logName)), 
                vanillaLog);

            generateMoldyLoot(strippedLog, 
                Registries.ITEM.get(SporesShadows.id("tainted_stripped_" + logName)), 
                Registries.ITEM.get(SporesShadows.id("moldy_stripped_" + logName)), 
                Registries.ITEM.get(SporesShadows.id("rotten_stripped_" + logName)), 
                vanillaStrippedLog);

            if (woodName != null) {
                Block vanillaWood = Registries.BLOCK.get(net.minecraft.util.Identifier.of("minecraft", woodName));
                Block vanillaStrippedWood = Registries.BLOCK.get(net.minecraft.util.Identifier.of("minecraft", "stripped_" + woodName));
                Block woodBlock = Registries.BLOCK.get(SporesShadows.id("moldy_" + woodName));
                Block strippedWood = Registries.BLOCK.get(SporesShadows.id("moldy_stripped_" + woodName));

                generateMoldyLoot(woodBlock, 
                    Registries.ITEM.get(SporesShadows.id("tainted_" + woodName)), 
                    Registries.ITEM.get(SporesShadows.id("moldy_" + woodName)), 
                    Registries.ITEM.get(SporesShadows.id("rotten_" + woodName)), 
                    vanillaWood);

                generateMoldyLoot(strippedWood, 
                    Registries.ITEM.get(SporesShadows.id("tainted_stripped_" + woodName)), 
                    Registries.ITEM.get(SporesShadows.id("moldy_stripped_" + woodName)), 
                    Registries.ITEM.get(SporesShadows.id("rotten_stripped_" + woodName)), 
                    vanillaStrippedWood);
            }

            generateMoldyLoot(planks, 
                Registries.ITEM.get(SporesShadows.id("tainted_" + prefix + "_planks")), 
                Registries.ITEM.get(SporesShadows.id("moldy_" + prefix + "_planks")), 
                Registries.ITEM.get(SporesShadows.id("rotten_" + prefix + "_planks")), 
                vanillaPlanks);
        }
    }
    
    private void generateMoldyLoot(Block baseBlock, net.minecraft.item.Item stage1, net.minecraft.item.Item stage2, net.minecraft.item.Item stage3, Block vanillaBlock) {
        addDrop(baseBlock, (block) -> net.minecraft.loot.LootTable.builder()
            .pool(net.minecraft.loot.LootPool.builder()
                .rolls(net.minecraft.loot.provider.number.ConstantLootNumberProvider.create(1.0F))
                .with(net.minecraft.loot.entry.AlternativeEntry.builder(
                    net.minecraft.loot.entry.ItemEntry.builder(stage3)
                        .conditionally(net.minecraft.loot.condition.BlockStatePropertyLootCondition.builder(baseBlock).properties(net.minecraft.predicate.StatePredicate.Builder.create().exactMatch(MoldyLogBlock.STAGE, 3)))
                        .conditionally(this.createSilkTouchCondition()),
                    net.minecraft.loot.entry.ItemEntry.builder(stage2)
                        .conditionally(net.minecraft.loot.condition.BlockStatePropertyLootCondition.builder(baseBlock).properties(net.minecraft.predicate.StatePredicate.Builder.create().exactMatch(MoldyLogBlock.STAGE, 2)))
                        .conditionally(
                            net.minecraft.loot.condition.AnyOfLootCondition.builder(
                                this.createSilkTouchCondition(),
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
}
