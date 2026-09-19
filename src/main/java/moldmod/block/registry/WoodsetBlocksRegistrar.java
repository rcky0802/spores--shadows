package moldmod.block.registry;

import moldmod.SporesShadowsConstants;
import moldmod.SporesShadowsConstants.MoldyWoodType;
import moldmod.block.redstone.MoldyButtonBlock;
import moldmod.block.redstone.MoldyPressurePlateBlock;
import moldmod.block.sign.MoldyHangingSignBlock;
import moldmod.block.sign.MoldySignBlock;
import moldmod.block.sign.MoldyWallHangingSignBlock;
import moldmod.block.sign.MoldyWallSignBlock;
import moldmod.block.wood.MoldyDoorBlock;
import moldmod.block.wood.MoldyFenceBlock;
import moldmod.block.wood.MoldyFenceGateBlock;
import moldmod.block.wood.MoldyLogBlock;
import moldmod.block.wood.MoldyPlanksBlock;
import moldmod.block.wood.MoldySlabBlock;
import moldmod.block.wood.MoldyStairsBlock;
import moldmod.block.wood.MoldyTrapdoorBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSetType;
import net.minecraft.block.WoodType;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import static moldmod.block.registry.BlockRegistryHelper.registerBlock;
import static moldmod.block.registry.BlockRegistryHelper.registerHangingSignVariant;
import static moldmod.block.registry.BlockRegistryHelper.registerSignVariant;
import static moldmod.block.registry.BlockRegistryHelper.registerVariant;

public final class WoodsetBlocksRegistrar {

    private WoodsetBlocksRegistrar() {}

    public static void registerAll() {
        for (MoldyWoodType wood : SporesShadowsConstants.WOOD_TYPES) {
            registerWoodSet(wood);
        }
    }

    public static void registerWoodSet(MoldyWoodType moldyWoodType) {
        String namespace = moldyWoodType.namespace();
        String prefix = moldyWoodType.name();
        String logName = moldyWoodType.getLogName();
        String woodName = moldyWoodType.getWoodName();
        BlockSetType setType = moldyWoodType.setType();
        WoodType woodType = moldyWoodType.woodType();

        // 1. Logs & Stripped Logs
        Block vanillaStrippedLog = Registries.BLOCK.get(Identifier.of(namespace, "stripped_" + logName));
        Block strippedLog = registerBlock("moldy_stripped_" + logName,
                new MoldyLogBlock(AbstractBlock.Settings.copy(vanillaStrippedLog).ticksRandomly(), null));
        Block waxedStrippedLog = registerBlock("waxed_stripped_" + logName,
                new MoldyLogBlock(AbstractBlock.Settings.copy(vanillaStrippedLog).ticksRandomly(), null));
        registerVariant("stripped_" + logName, vanillaStrippedLog, strippedLog, waxedStrippedLog);

        Block vanillaLog = Registries.BLOCK.get(Identifier.of(namespace, logName));
        Block log = registerBlock("moldy_" + logName,
                new MoldyLogBlock(AbstractBlock.Settings.copy(vanillaLog).ticksRandomly(), strippedLog));
        Block waxedLog = registerBlock("waxed_" + logName,
                new MoldyLogBlock(AbstractBlock.Settings.copy(vanillaLog).ticksRandomly(), waxedStrippedLog));
        registerVariant(logName, vanillaLog, log, waxedLog);

        // 2. Planks
        Block vanillaPlanks = Registries.BLOCK.get(Identifier.of(namespace, prefix + "_planks"));
        Block planks = registerBlock("moldy_" + prefix + "_planks",
                new MoldyPlanksBlock(AbstractBlock.Settings.copy(vanillaPlanks).ticksRandomly()));
        Block waxedPlanks = registerBlock("waxed_" + prefix + "_planks",
                new MoldyPlanksBlock(AbstractBlock.Settings.copy(vanillaPlanks).ticksRandomly()));
        registerVariant(prefix + "_planks", vanillaPlanks, planks, waxedPlanks);

        // 3. Stairs & Slabs
        Block vanillaStairs = Registries.BLOCK.get(Identifier.of(namespace, prefix + "_stairs"));
        Block stairs = registerBlock("moldy_" + prefix + "_stairs",
                new MoldyStairsBlock(planks.getDefaultState(), AbstractBlock.Settings.copy(vanillaPlanks).ticksRandomly()));
        Block waxedStairs = registerBlock("waxed_" + prefix + "_stairs",
                new MoldyStairsBlock(waxedPlanks.getDefaultState(), AbstractBlock.Settings.copy(vanillaPlanks).ticksRandomly()));
        registerVariant(prefix + "_stairs", vanillaStairs, stairs, waxedStairs);

        Block vanillaSlab = Registries.BLOCK.get(Identifier.of(namespace, prefix + "_slab"));
        Block slab = registerBlock("moldy_" + prefix + "_slab",
                new MoldySlabBlock(AbstractBlock.Settings.copy(vanillaPlanks).ticksRandomly()));
        Block waxedSlab = registerBlock("waxed_" + prefix + "_slab",
                new MoldySlabBlock(AbstractBlock.Settings.copy(vanillaPlanks).ticksRandomly()));
        registerVariant(prefix + "_slab", vanillaSlab, slab, waxedSlab);

        // 4. Fences & Gates
        Block vanillaFence = Registries.BLOCK.get(Identifier.of(namespace, prefix + "_fence"));
        Block fence = registerBlock("moldy_" + prefix + "_fence",
                new MoldyFenceBlock(AbstractBlock.Settings.copy(vanillaFence).ticksRandomly()));
        Block waxedFence = registerBlock("waxed_" + prefix + "_fence",
                new MoldyFenceBlock(AbstractBlock.Settings.copy(vanillaFence).ticksRandomly()));
        registerVariant(prefix + "_fence", vanillaFence, fence, waxedFence);

        Block vanillaGate = Registries.BLOCK.get(Identifier.of(namespace, prefix + "_fence_gate"));
        Block gate = registerBlock("moldy_" + prefix + "_fence_gate",
                new MoldyFenceGateBlock(woodType, AbstractBlock.Settings.copy(vanillaGate).ticksRandomly()));
        Block waxedGate = registerBlock("waxed_" + prefix + "_fence_gate",
                new MoldyFenceGateBlock(woodType, AbstractBlock.Settings.copy(vanillaGate).ticksRandomly()));
        registerVariant(prefix + "_fence_gate", vanillaGate, gate, waxedGate);

        // 5. Doors & Trapdoors
        Block vanillaDoor = Registries.BLOCK.get(Identifier.of(namespace, prefix + "_door"));
        Block door = registerBlock("moldy_" + prefix + "_door",
                new MoldyDoorBlock(setType, AbstractBlock.Settings.copy(vanillaDoor).ticksRandomly().nonOpaque()));
        Block waxedDoor = registerBlock("waxed_" + prefix + "_door",
                new MoldyDoorBlock(setType, AbstractBlock.Settings.copy(vanillaDoor).ticksRandomly().nonOpaque()));
        registerVariant(prefix + "_door", vanillaDoor, door, waxedDoor);

        Block vanillaTrapdoor = Registries.BLOCK.get(Identifier.of(namespace, prefix + "_trapdoor"));
        Block trapdoor = registerBlock("moldy_" + prefix + "_trapdoor",
                new MoldyTrapdoorBlock(setType, AbstractBlock.Settings.copy(vanillaTrapdoor).ticksRandomly().nonOpaque()));
        Block waxedTrapdoor = registerBlock("waxed_" + prefix + "_trapdoor",
                new MoldyTrapdoorBlock(setType, AbstractBlock.Settings.copy(vanillaTrapdoor).ticksRandomly().nonOpaque()));
        registerVariant(prefix + "_trapdoor", vanillaTrapdoor, trapdoor, waxedTrapdoor);

        // 6. Buttons & Pressure Plates
        Block vanillaPressurePlate = Registries.BLOCK.get(Identifier.of(namespace, prefix + "_pressure_plate"));
        Block pressurePlate = registerBlock("moldy_" + prefix + "_pressure_plate",
                new MoldyPressurePlateBlock(setType, AbstractBlock.Settings.copy(vanillaPressurePlate).ticksRandomly()));
        Block waxedPressurePlate = registerBlock("waxed_" + prefix + "_pressure_plate",
                new MoldyPressurePlateBlock(setType, AbstractBlock.Settings.copy(vanillaPressurePlate).ticksRandomly()));
        registerVariant(prefix + "_pressure_plate", vanillaPressurePlate, pressurePlate, waxedPressurePlate);

        Block vanillaButton = Registries.BLOCK.get(Identifier.of(namespace, prefix + "_button"));
        Block button = registerBlock("moldy_" + prefix + "_button",
                new MoldyButtonBlock(setType, 30, AbstractBlock.Settings.copy(vanillaButton).ticksRandomly()));
        Block waxedButton = registerBlock("waxed_" + prefix + "_button",
                new MoldyButtonBlock(setType, 30, AbstractBlock.Settings.copy(vanillaButton).ticksRandomly()));
        registerVariant(prefix + "_button", vanillaButton, button, waxedButton);

        // 7. Wood / Hyphae (Bark 6-sides)
        if (woodName != null) {
            Block vanillaStrippedWood = Registries.BLOCK.get(Identifier.of(namespace, "stripped_" + woodName));
            Block strippedWood = registerBlock("moldy_stripped_" + woodName,
                    new MoldyLogBlock(AbstractBlock.Settings.copy(vanillaStrippedWood).ticksRandomly(), null));
            Block waxedStrippedWood = registerBlock("waxed_stripped_" + woodName,
                    new MoldyLogBlock(AbstractBlock.Settings.copy(vanillaStrippedWood).ticksRandomly(), null));
            registerVariant("stripped_" + woodName, vanillaStrippedWood, strippedWood, waxedStrippedWood);

            Block vanillaWood = Registries.BLOCK.get(Identifier.of(namespace, woodName));
            Block wood = registerBlock("moldy_" + woodName,
                    new MoldyLogBlock(AbstractBlock.Settings.copy(vanillaWood).ticksRandomly(), strippedWood));
            Block waxedWood = registerBlock("waxed_" + woodName,
                    new MoldyLogBlock(AbstractBlock.Settings.copy(vanillaWood).ticksRandomly(), waxedStrippedWood));
            registerVariant(woodName, vanillaWood, wood, waxedWood);
        }

        // 8. Bamboo Mosaic family (unique to bamboo)
        if (moldyWoodType.isBamboo()) {
            Block vanillaMosaic = Registries.BLOCK.get(Identifier.of(namespace, prefix + "_mosaic"));
            Block mosaic = registerBlock("moldy_" + prefix + "_mosaic",
                    new MoldyPlanksBlock(AbstractBlock.Settings.copy(vanillaMosaic).ticksRandomly()));
            Block waxedMosaic = registerBlock("waxed_" + prefix + "_mosaic",
                    new MoldyPlanksBlock(AbstractBlock.Settings.copy(vanillaMosaic).ticksRandomly()));
            registerVariant(prefix + "_mosaic", vanillaMosaic, mosaic, waxedMosaic);

            Block vanillaMosaicStairs = Registries.BLOCK.get(Identifier.of(namespace, prefix + "_mosaic_stairs"));
            Block mosaicStairs = registerBlock("moldy_" + prefix + "_mosaic_stairs",
                    new MoldyStairsBlock(mosaic.getDefaultState(), AbstractBlock.Settings.copy(vanillaMosaic).ticksRandomly()));
            Block waxedMosaicStairs = registerBlock("waxed_" + prefix + "_mosaic_stairs",
                    new MoldyStairsBlock(waxedMosaic.getDefaultState(), AbstractBlock.Settings.copy(vanillaMosaic).ticksRandomly()));
            registerVariant(prefix + "_mosaic_stairs", vanillaMosaicStairs, mosaicStairs, waxedMosaicStairs);

            Block vanillaMosaicSlab = Registries.BLOCK.get(Identifier.of(namespace, prefix + "_mosaic_slab"));
            Block mosaicSlab = registerBlock("moldy_" + prefix + "_mosaic_slab",
                    new MoldySlabBlock(AbstractBlock.Settings.copy(vanillaMosaic).ticksRandomly()));
            Block waxedMosaicSlab = registerBlock("waxed_" + prefix + "_mosaic_slab",
                    new MoldySlabBlock(AbstractBlock.Settings.copy(vanillaMosaic).ticksRandomly()));
            registerVariant(prefix + "_mosaic_slab", vanillaMosaicSlab, mosaicSlab, waxedMosaicSlab);
        }

        // 9. Signs & Hanging Signs
        Block vanillaStandingSign = Registries.BLOCK.get(Identifier.of(namespace, prefix + "_sign"));
        Block vanillaWallSign = Registries.BLOCK.get(Identifier.of(namespace, prefix + "_wall_sign"));
        Block moldyStandingSign = registerBlock("moldy_" + prefix + "_sign",
                new MoldySignBlock(woodType, AbstractBlock.Settings.copy(vanillaStandingSign).ticksRandomly()));
        Block waxedStandingSign = registerBlock("waxed_" + prefix + "_sign",
                new MoldySignBlock(woodType, AbstractBlock.Settings.copy(vanillaStandingSign).ticksRandomly()));
        Block moldyWallSign = registerBlock("moldy_" + prefix + "_wall_sign",
                new MoldyWallSignBlock(woodType, AbstractBlock.Settings.copy(vanillaWallSign).ticksRandomly()));
        Block waxedWallSign = registerBlock("waxed_" + prefix + "_wall_sign",
                new MoldyWallSignBlock(woodType, AbstractBlock.Settings.copy(vanillaWallSign).ticksRandomly()));

        registerSignVariant(prefix + "_sign", vanillaStandingSign, vanillaWallSign,
                moldyStandingSign, waxedStandingSign, moldyWallSign, waxedWallSign);

        Block vanillaHangingSign = Registries.BLOCK.get(Identifier.of(namespace, prefix + "_hanging_sign"));
        Block vanillaWallHangingSign = Registries.BLOCK.get(Identifier.of(namespace, prefix + "_wall_hanging_sign"));
        Block moldyHangingSign = registerBlock("moldy_" + prefix + "_hanging_sign",
                new MoldyHangingSignBlock(woodType, AbstractBlock.Settings.copy(vanillaHangingSign).ticksRandomly()));
        Block waxedHangingSign = registerBlock("waxed_" + prefix + "_hanging_sign",
                new MoldyHangingSignBlock(woodType, AbstractBlock.Settings.copy(vanillaHangingSign).ticksRandomly()));
        Block moldyWallHangingSign = registerBlock("moldy_" + prefix + "_wall_hanging_sign",
                new MoldyWallHangingSignBlock(woodType, AbstractBlock.Settings.copy(vanillaWallHangingSign).ticksRandomly()));
        Block waxedWallHangingSign = registerBlock("waxed_" + prefix + "_wall_hanging_sign",
                new MoldyWallHangingSignBlock(woodType, AbstractBlock.Settings.copy(vanillaWallHangingSign).ticksRandomly()));

        registerHangingSignVariant(prefix + "_hanging_sign", vanillaHangingSign, vanillaWallHangingSign,
                moldyHangingSign, waxedHangingSign, moldyWallHangingSign, waxedWallHangingSign);
    }
}
