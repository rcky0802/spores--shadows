package moldmod.test.gametest.miasma;

import moldmod.SporesShadows;
import moldmod.atmosphere.RoomAtmosphereCalculator;
import moldmod.block.ModBlocks;
import moldmod.block.purifier.AirPurifierBlock;
import moldmod.block.purifier.AirPurifierBlockEntity;
import moldmod.block.purifier.PurifierRedstoneMode;
import moldmod.block.purifier.PurifierStatus;
import moldmod.integration.jade.AirPurifierBlockProvider;
import moldmod.item.ModItems;
import moldmod.test.helper.RoomTestBuilder;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;

import java.util.List;

public class AirPurifierGameTests {

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testFuelEfficiency4x(TestContext context) {
        BlockPos pos = new BlockPos(2, 1, 2);
        context.setBlockState(pos, ModBlocks.AIR_PURIFIER.getDefaultState());

        AirPurifierBlockEntity be = (AirPurifierBlockEntity) context.getBlockEntity(pos);
        context.assertTrue(be != null, "BlockEntity must not be null");

        // Equip filter in slot 1
        be.setStack(1, new ItemStack(ModItems.SPORE_FILTER, 1));

        // Insert 1 stick (100 ticks base furnace burn time * 10 E/t * 4.0f multiplier = 4000 E)
        be.setStack(0, new ItemStack(Items.STICK, 1));
        context.assertTrue(be.getStack(0).isOf(Items.STICK), "Slot 0 must contain stick");

        // Tick once
        be.tick(context.getWorld(), context.getAbsolutePos(pos), context.getBlockState(pos));

        // Stick converted to energy: 4000 - 10 consumed = 3990
        context.assertTrue(be.getStack(0).isEmpty(), "Stick must be consumed from slot 0");
        context.assertTrue(be.getEnergy() == 3990, "Energy must be 3990, got: " + be.getEnergy());
        context.assertTrue(be.getStatus() == PurifierStatus.RUNNING, "Status must be RUNNING");

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testHybridElectricPower(TestContext context) {
        BlockPos pos = new BlockPos(2, 1, 2);
        context.setBlockState(pos, ModBlocks.AIR_PURIFIER.getDefaultState());

        AirPurifierBlockEntity be = (AirPurifierBlockEntity) context.getBlockEntity(pos);
        context.assertTrue(be != null, "BlockEntity must not be null");

        // Equip filter in slot 1
        be.setStack(1, new ItemStack(ModItems.SPORE_FILTER, 1));

        // Test insertion from EnergyStorage.SIDED on all 6 sides
        for (Direction dir : Direction.values()) {
            team.reborn.energy.api.EnergyStorage storage = team.reborn.energy.api.EnergyStorage.SIDED.find(
                    context.getWorld(), context.getAbsolutePos(pos), dir);
            context.assertTrue(storage != null, "EnergyStorage must be accessible from: " + dir);
            context.assertTrue(storage.supportsInsertion(), "Must support insertion from: " + dir);
            context.assertTrue(!storage.supportsExtraction(), "Must NOT support extraction");
        }

        // Insert 500 E via EnergyStorage.SIDED
        team.reborn.energy.api.EnergyStorage topStorage = team.reborn.energy.api.EnergyStorage.SIDED.find(
                context.getWorld(), context.getAbsolutePos(pos), Direction.UP);
        try (Transaction tx = Transaction.openOuter()) {
            long inserted = topStorage.insert(500, tx);
            context.assertTrue(inserted == 500, "Must insert exactly 500 E");
            tx.commit();
        }
        context.assertTrue(be.getEnergy() == 500, "BlockEntity energy must be 500 after external insertion");

        // Tick once with electrical energy: 10 E consumed
        be.tick(context.getWorld(), context.getAbsolutePos(pos), context.getBlockState(pos));
        context.assertTrue(be.getEnergy() == 490, "Energy should drop by 10 to 490 (got " + be.getEnergy() + ")");
        context.assertTrue(be.getStatus() == PurifierStatus.RUNNING, "Status must be RUNNING");

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testPowerAbsencePriorityOverFilterDepletion(TestContext context) {
        BlockPos pos = new BlockPos(2, 1, 2);
        context.setBlockState(pos, ModBlocks.AIR_PURIFIER.getDefaultState());

        AirPurifierBlockEntity be = (AirPurifierBlockEntity) context.getBlockEntity(pos);
        context.assertTrue(be != null, "BlockEntity must not be null");

        // Both power is 0 AND filter slot is empty
        be.setEnergy(0);
        be.clear();
        be.setFilterWearTicks(0);

        be.tick(context.getWorld(), context.getAbsolutePos(pos), context.getBlockState(pos));

        // Absence of power must take priority over absence of filter: status must be OFF, NOT FILTER_DEPLETED
        context.assertTrue(be.getStatus() == PurifierStatus.OFF,
                "Absence of power must have higher priority than absence of filter (expected OFF, got: " + be.getStatus() + ")");
        context.assertTrue(context.getBlockState(pos).get(AirPurifierBlock.STATUS) == PurifierStatus.OFF,
                "BlockState status must be OFF");

        // Now supply power (500 E) but keep filter empty:
        be.setEnergy(500);
        be.tick(context.getWorld(), context.getAbsolutePos(pos), context.getBlockState(pos));

        // Now that it has power, absence of filter triggers FILTER_DEPLETED
        context.assertTrue(be.getStatus() == PurifierStatus.FILTER_DEPLETED,
                "When powered but missing filter, status must be FILTER_DEPLETED (got: " + be.getStatus() + ")");
        context.assertTrue(context.getBlockState(pos).get(AirPurifierBlock.STATUS) == PurifierStatus.FILTER_DEPLETED,
                "BlockState status must be FILTER_DEPLETED");

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testFilterDepletionAndStandby(TestContext context) {
        BlockPos pos = new BlockPos(2, 1, 2);
        context.setBlockState(pos, ModBlocks.AIR_PURIFIER.getDefaultState());

        AirPurifierBlockEntity be = (AirPurifierBlockEntity) context.getBlockEntity(pos);
        context.assertTrue(be != null, "BlockEntity must not be null");

        be.setEnergy(1000);
        be.setStack(1, new ItemStack(ModItems.SPORE_FILTER, 1));

        // First tick loads the filter into active cartridge:
        be.tick(context.getWorld(), context.getAbsolutePos(pos), context.getBlockState(pos));
        context.assertTrue(be.getStack(1).isEmpty(), "Slot 1 is now empty because filter is active in cartridge");
        context.assertTrue(be.getStatus() == PurifierStatus.RUNNING, "Purifier is running with active filter");

        // Now advance active filter wear to 1 tick remaining before exhaustion
        be.setFilterWearTicks(1);

        // Tick once: active filter wears out (1 -> 0), no backup available
        be.tick(context.getWorld(), context.getAbsolutePos(pos), context.getBlockState(pos));
        context.assertTrue(be.getFilterWearTicks() == 0, "Filter wear must be 0");
        context.assertTrue(be.getStatus() == PurifierStatus.FILTER_DEPLETED, "Status must be FILTER_DEPLETED");

        int energyAfterDepleted = be.getEnergy();
        // Next tick: machine must NOT consume energy while FILTER_DEPLETED
        be.tick(context.getWorld(), context.getAbsolutePos(pos), context.getBlockState(pos));
        context.assertTrue(be.getEnergy() == energyAfterDepleted, "Energy must not be consumed when FILTER_DEPLETED");

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testFilterStackAutoReload(TestContext context) {
        BlockPos pos = new BlockPos(2, 1, 2);
        context.setBlockState(pos, ModBlocks.AIR_PURIFIER.getDefaultState());

        AirPurifierBlockEntity be = (AirPurifierBlockEntity) context.getBlockEntity(pos);
        context.assertTrue(be != null, "BlockEntity must not be null");

        be.setEnergy(1000);
        // Place 2 filters
        be.setStack(1, new ItemStack(ModItems.SPORE_FILTER, 2));

        // First tick loads 1 filter into active cartridge:
        be.tick(context.getWorld(), context.getAbsolutePos(pos), context.getBlockState(pos));
        context.assertTrue(be.getStack(1).getCount() == 1, "Backup slot has 1 filter remaining");

        // Advance active filter wear to 1 tick remaining before exhaustion
        be.setFilterWearTicks(1);

        // Tick once: active filter wears out (1 -> 0), backup filter is loaded automatically
        be.tick(context.getWorld(), context.getAbsolutePos(pos), context.getBlockState(pos));
        context.assertTrue(be.getStack(1).isEmpty(), "Backup slot is now empty because second filter was loaded");
        context.assertTrue(be.getFilterWearTicks() == be.getFilterDurabilityTicks(), "Filter wear must reset to full durability");
        context.assertTrue(be.getStatus() == PurifierStatus.RUNNING, "Status must remain RUNNING");

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testFilterLevelCalculation(TestContext context) {
        int max = 2400;

        // Level 0: no active filter and no backup
        context.assertTrue(AirPurifierBlockEntity.calculateFilterLevel(0, max, 0) == 0, "Level must be 0 when empty");

        // Level 4: backup available
        context.assertTrue(AirPurifierBlockEntity.calculateFilterLevel(500, max, 1) == 4, "Level must be 4 when backup exists");

        // Single filter wearing down:
        // 2400 active ticks remaining -> level 4
        context.assertTrue(AirPurifierBlockEntity.calculateFilterLevel(2400, max, 0) == 4, "Level must be 4 for full filter");
        // 1600 remaining -> level 3
        context.assertTrue(AirPurifierBlockEntity.calculateFilterLevel(1600, max, 0) == 3, "Level must be 3");
        // 1000 remaining -> level 2
        context.assertTrue(AirPurifierBlockEntity.calculateFilterLevel(1000, max, 0) == 2, "Level must be 2");
        // 300 remaining -> level 1
        context.assertTrue(AirPurifierBlockEntity.calculateFilterLevel(300, max, 0) == 1, "Level must be 1");

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testAtmospherePurificationAndSealedRoomClearance(TestContext context) {
        // Build sealed room 5x3x5
        RoomTestBuilder.of(context)
                .stoneRoom(0, 0, 0, 4, 3, 4);

        // Place a mold block in the room to create toxic score (STAGE 2 = MOLDY)
        BlockPos moldPos = new BlockPos(1, 1, 1);
        context.setBlockState(moldPos, ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState()
                .with(moldmod.block.MoldyBlock.STAGE, moldmod.SporesShadowsConstants.MoldStage.MOLDY.getId()));

        BlockPos insidePos = context.getAbsolutePos(new BlockPos(2, 1, 2));

        // Baseline miasma calculation
        var baselineMiasma = RoomAtmosphereCalculator.calculateMiasma(context.getWorld(), insidePos);
        context.assertTrue(baselineMiasma != null, "Baseline miasma must not be null");
        context.assertTrue(baselineMiasma.roomPurifierCount == 0, "Baseline purifier count must be 0");
        context.assertTrue(baselineMiasma.purifierCleaningBonus == 0.0, "Baseline purifier bonus must be 0.0");
        context.assertTrue(baselineMiasma.toxicScore > 0, "Mold block must create toxicScore");

        // Place active running Air Purifier in wall
        BlockPos purifierPos = new BlockPos(2, 1, 0);
        BlockState runningPurifier = ModBlocks.AIR_PURIFIER.getDefaultState()
                .with(AirPurifierBlock.STATUS, PurifierStatus.RUNNING);
        context.setBlockState(purifierPos, runningPurifier);

        AirPurifierBlockEntity be = (AirPurifierBlockEntity) context.getBlockEntity(purifierPos);
        context.assertTrue(be != null, "BlockEntity must not be null");
        be.setEnergy(5000);
        be.setStack(1, new ItemStack(ModItems.SPORE_FILTER, 2));

        var activeMiasma = RoomAtmosphereCalculator.calculateMiasma(context.getWorld(), insidePos);
        context.assertTrue(activeMiasma != null, "Active miasma must not be null");
        context.assertTrue(activeMiasma.roomPurifierCount == 1, "Active purifier count must be 1, got: " + activeMiasma.roomPurifierCount);
        context.assertTrue(activeMiasma.purifierCleaningBonus == 48.0, "Purifier cleaning bonus must be 48.0, got: " + activeMiasma.purifierCleaningBonus);
        context.assertTrue(activeMiasma.targetMiasma < baselineMiasma.targetMiasma,
                "Target miasma with purifier (" + activeMiasma.targetMiasma + ") must be lower than baseline (" + baselineMiasma.targetMiasma + ")");
        context.assertTrue(activeMiasma.targetMiasma == 0.0, "Purifier bonus (48.0) exceeds single mold block toxicity, target miasma must be 0.0");

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testHopperSidedInventory(TestContext context) {
        BlockPos pos = new BlockPos(2, 1, 2);
        context.setBlockState(pos, ModBlocks.AIR_PURIFIER.getDefaultState());

        AirPurifierBlockEntity be = (AirPurifierBlockEntity) context.getBlockEntity(pos);
        context.assertTrue(be != null, "BlockEntity must not be null");

        ItemStack filter = new ItemStack(ModItems.SPORE_FILTER);
        ItemStack coal = new ItemStack(Items.COAL);

        // Top face (UP) accepts ONLY filters into slot 1
        context.assertTrue(be.canInsert(1, filter, Direction.UP), "Must accept filter from UP");
        context.assertTrue(!be.canInsert(0, filter, Direction.UP), "Must NOT accept filter into slot 0 from UP");
        context.assertTrue(!be.canInsert(0, coal, Direction.UP), "Must NOT accept coal from UP");

        // Side faces accept ONLY fuel into slot 0
        for (Direction side : List.of(Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.DOWN)) {
            context.assertTrue(be.canInsert(0, coal, side), "Must accept coal from " + side);
            context.assertTrue(!be.canInsert(1, coal, side), "Must NOT accept coal into slot 1 from " + side);
            context.assertTrue(!be.canInsert(1, filter, side), "Must NOT accept filter from " + side);
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testRedstoneModes(TestContext context) {
        BlockPos pos = new BlockPos(2, 1, 2);
        context.setBlockState(pos, ModBlocks.AIR_PURIFIER.getDefaultState());

        AirPurifierBlockEntity be = (AirPurifierBlockEntity) context.getBlockEntity(pos);
        context.assertTrue(be != null, "BlockEntity must not be null");

        be.setEnergy(1000);
        be.setStack(1, new ItemStack(ModItems.SPORE_FILTER, 1));

        // 1. IGNORED mode (default) - runs without redstone
        be.setRedstoneMode(PurifierRedstoneMode.IGNORED);
        be.tick(context.getWorld(), context.getAbsolutePos(pos), context.getBlockState(pos));
        context.assertTrue(be.getStatus() == PurifierStatus.RUNNING, "IGNORED mode must run without redstone signal");
        context.assertTrue(context.getBlockState(pos).get(AirPurifierBlock.STATUS) == PurifierStatus.RUNNING, "BlockState must be RUNNING");

        // 2. HIGH mode with 0 redstone signal - should turn OFF
        be.setRedstoneMode(PurifierRedstoneMode.HIGH);
        be.tick(context.getWorld(), context.getAbsolutePos(pos), context.getBlockState(pos));
        context.assertTrue(be.getStatus() == PurifierStatus.OFF, "HIGH mode must pause when redstone signal is 0");
        context.assertTrue(context.getBlockState(pos).get(AirPurifierBlock.STATUS) == PurifierStatus.OFF, "BlockState must be OFF");

        // 3. LOW mode with 0 redstone signal - should RUN
        be.setRedstoneMode(PurifierRedstoneMode.LOW);
        be.tick(context.getWorld(), context.getAbsolutePos(pos), context.getBlockState(pos));
        context.assertTrue(be.getStatus() == PurifierStatus.RUNNING, "LOW mode must run when redstone signal is 0");
        context.assertTrue(context.getBlockState(pos).get(AirPurifierBlock.STATUS) == PurifierStatus.RUNNING, "BlockState must be RUNNING");

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testComparatorOutput(TestContext context) {
        BlockPos pos = new BlockPos(2, 1, 2);
        context.setBlockState(pos, ModBlocks.AIR_PURIFIER.getDefaultState());

        AirPurifierBlockEntity be = (AirPurifierBlockEntity) context.getBlockEntity(pos);
        context.assertTrue(be != null, "BlockEntity must not be null");

        // Empty filter slot -> comparator output 0
        context.assertTrue(be.getComparatorOutput() == 0, "Comparator output must be 0 when empty");

        // 1 full filter -> output > 0
        be.setStack(1, new ItemStack(ModItems.SPORE_FILTER, 1));
        int comp1 = be.getComparatorOutput();
        context.assertTrue(comp1 > 0, "Comparator output must be > 0 with 1 filter");

        // Full stack of 64 filters -> comparator output 15
        be.setStack(1, new ItemStack(ModItems.SPORE_FILTER, 64));
        context.assertTrue(be.getComparatorOutput() == 15, "Comparator output must be 15 with 64 filters, got: " + be.getComparatorOutput());

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testBlockDropBehavior(TestContext context) {
        BlockPos pos = new BlockPos(2, 1, 2);
        context.setBlockState(pos, ModBlocks.AIR_PURIFIER.getDefaultState());

        AirPurifierBlockEntity be = (AirPurifierBlockEntity) context.getBlockEntity(pos);
        context.assertTrue(be != null, "BlockEntity must not be null");

        be.setStack(0, new ItemStack(Items.COAL, 3));
        be.setStack(1, new ItemStack(ModItems.SPORE_FILTER, 4));

        // Break block
        ((AirPurifierBlock) context.getBlockState(pos).getBlock()).onStateReplaced(
                context.getBlockState(pos),
                context.getWorld(),
                context.getAbsolutePos(pos),
                Blocks.AIR.getDefaultState(),
                false);
        context.setBlockState(pos, Blocks.AIR.getDefaultState());

        // Verify items dropped in world
        var droppedCoal = context.getWorld().getEntitiesByClass(ItemEntity.class,
                new Box(context.getAbsolutePos(pos)).expand(3),
                item -> item.getStack().isOf(Items.COAL));
        int totalCoal = droppedCoal.stream().mapToInt(i -> i.getStack().getCount()).sum();
        context.assertTrue(totalCoal == 3, "Expected 3 coal dropped, got: " + totalCoal);

        var droppedFilters = context.getWorld().getEntitiesByClass(ItemEntity.class,
                new Box(context.getAbsolutePos(pos)).expand(3),
                item -> item.getStack().isOf(ModItems.SPORE_FILTER));
        int totalFilters = droppedFilters.stream().mapToInt(i -> i.getStack().getCount()).sum();
        context.assertTrue(totalFilters == 4, "Expected 4 spore filters dropped, got: " + totalFilters);

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testParticleAndSoundTicking(TestContext context) {
        BlockPos pos = new BlockPos(2, 1, 2);

        // RUNNING state
        BlockState runningState = ModBlocks.AIR_PURIFIER.getDefaultState()
                .with(AirPurifierBlock.STATUS, PurifierStatus.RUNNING);
        context.setBlockState(pos, runningState);

        ((AirPurifierBlock) ModBlocks.AIR_PURIFIER).randomDisplayTick(
                runningState,
                context.getWorld(),
                context.getAbsolutePos(pos),
                context.getWorld().getRandom());

        // FILTER_DEPLETED state
        BlockState depletedState = ModBlocks.AIR_PURIFIER.getDefaultState()
                .with(AirPurifierBlock.STATUS, PurifierStatus.FILTER_DEPLETED);
        context.setBlockState(pos, depletedState);

        ((AirPurifierBlock) ModBlocks.AIR_PURIFIER).randomDisplayTick(
                depletedState,
                context.getWorld(),
                context.getAbsolutePos(pos),
                context.getWorld().getRandom());

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testJadeBlockProvider(TestContext context) {
        Identifier uid = AirPurifierBlockProvider.INSTANCE.getUid();
        context.assertTrue(uid.getNamespace().equals(SporesShadows.MOD_ID),
                "Provider namespace must be " + SporesShadows.MOD_ID + ", got: " + uid.getNamespace());
        context.assertTrue(uid.getPath().equals("air_purifier_info"),
                "Provider path must be 'air_purifier_info', got: " + uid.getPath());

        List<String> keys = List.of(
                "config.jade.plugin_spores--shadows.air_purifier_info",
                "tooltip.spores--shadows.jade.air_purifier.filter",
                "tooltip.spores--shadows.jade.air_purifier.status.running",
                "tooltip.spores--shadows.jade.air_purifier.status.off",
                "tooltip.spores--shadows.jade.air_purifier.status.filter_depleted"
        );
        for (String key : keys) {
            Text text = Text.translatable(key);
            context.assertTrue(!text.getString().isEmpty(), "Jade tooltip key " + key + " must not be empty");
        }

        context.complete();
    }
}
