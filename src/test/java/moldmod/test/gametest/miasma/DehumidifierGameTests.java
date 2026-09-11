package moldmod.test.gametest.miasma;

import moldmod.atmosphere.RoomAtmosphereCalculator;
import moldmod.block.ModBlocks;
import moldmod.block.dehumidifier.DehumidifierBlock;
import moldmod.block.dehumidifier.DehumidifierBlockEntity;
import moldmod.block.dehumidifier.DehumidifierMode;
import moldmod.block.dehumidifier.DehumidifierRedstoneMode;
import moldmod.block.dehumidifier.DehumidifierStatus;
import moldmod.test.helper.RoomTestBuilder;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class DehumidifierGameTests {

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testFuelEfficiency4x(TestContext context) {
        BlockPos pos = new BlockPos(2, 1, 2);
        context.setBlockState(pos, ModBlocks.DEHUMIDIFIER.getDefaultState());

        DehumidifierBlockEntity be = (DehumidifierBlockEntity) context.getBlockEntity(pos);
        context.assertTrue(be != null, "BlockEntity must not be null");

        // Insert 1 coal
        be.setStack(0, new ItemStack(Items.COAL, 1));
        context.assertTrue(be.getStack(0).isOf(Items.COAL), "Slot 0 must contain coal");

        // Simulate tick (in open air, room humidity defaults to >= 0.3)
        be.tick(context.getWorld(), context.getAbsolutePos(pos), context.getBlockState(pos));

        // Coal should be consumed and converted immediately into electricity: 1600 * 10 * 1 = 16000 E (minus 10 E consumed for tick = 15990)
        context.assertTrue(be.getStack(0).isEmpty(), "Coal should have been consumed from slot 0");
        context.assertTrue(be.getEnergy() == 15990, "Fuel must be immediately converted to electricity (expected 15990, got " + be.getEnergy() + ")");
        context.assertTrue(be.getStatus() == DehumidifierStatus.RUNNING, "Status must be RUNNING");

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testWaterCondensationAndFullStandby(TestContext context) {
        BlockPos pos = new BlockPos(2, 1, 2);
        context.setBlockState(pos, ModBlocks.DEHUMIDIFIER.getDefaultState());

        DehumidifierBlockEntity be = (DehumidifierBlockEntity) context.getBlockEntity(pos);
        context.assertTrue(be != null, "BlockEntity must not be null");

        // Supply coal
        be.setStack(0, new ItemStack(Items.COAL, 1));

        // Pre-fill tank close to capacity (1999 mB)
        be.setWaterMb(1999);
        be.tick(context.getWorld(), context.getAbsolutePos(pos), context.getBlockState(pos));

        int ticksPerMb = be.getTicksPerMb();
        // Tick until 1 mB is condensed and tank hits 2000 mB
        for (int i = 0; i < ticksPerMb + 5; i++) {
            be.tick(context.getWorld(), context.getAbsolutePos(pos), context.getBlockState(pos));
        }

        context.assertTrue(be.getWaterMb() == 2000, "Tank must be full at 2000 mB, got: " + be.getWaterMb());
        context.assertTrue(be.getStatus() == DehumidifierStatus.FULL, "Status must be FULL");

        int energyWhenFull = be.getEnergy();
        // Additional ticks must NOT consume fuel or energy while FULL
        be.tick(context.getWorld(), context.getAbsolutePos(pos), context.getBlockState(pos));
        context.assertTrue(be.getEnergy() == energyWhenFull, "Energy must not be consumed when FULL (standby)");

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testBucketDrainInteractions(TestContext context) {
        BlockPos pos = new BlockPos(2, 1, 2);
        context.setBlockState(pos, ModBlocks.DEHUMIDIFIER.getDefaultState());

        DehumidifierBlockEntity be = (DehumidifierBlockEntity) context.getBlockEntity(pos);
        context.assertTrue(be != null, "BlockEntity must not be null");

        PlayerEntity player = context.createMockPlayer(net.minecraft.world.GameMode.SURVIVAL);
        player.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.BUCKET, 1));

        BlockHitResult hit = new BlockHitResult(Vec3d.ofCenter(context.getAbsolutePos(pos)), Direction.UP, context.getAbsolutePos(pos), false);

        // 1. When water < 1000 mB (e.g. 500 mB), right click with bucket fails to drain water
        be.setWaterMb(500);
        context.getBlockState(pos).onUse(context.getWorld(), player, hit);
        context.assertTrue(be.getWaterMb() == 500, "Water level should remain 500 mB");
        context.assertTrue(player.getStackInHand(Hand.MAIN_HAND).isOf(Items.BUCKET), "Player must still hold empty bucket");

        // 2. When water >= 1000 mB (e.g. 1500 mB), right click with bucket extracts 1000 mB
        be.setWaterMb(1500);
        ActionResult resSuccess = context.getBlockState(pos).onUse(context.getWorld(), player, hit);
        context.assertTrue(resSuccess == ActionResult.SUCCESS, "Right-click with bucket on >=1000 mB must succeed");
        context.assertTrue(be.getWaterMb() == 500, "Water level should be 1500 - 1000 = 500 mB, got: " + be.getWaterMb());
        context.assertTrue(player.getStackInHand(Hand.MAIN_HAND).isOf(Items.WATER_BUCKET), "Player must receive a water bucket");

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testComparatorOutput(TestContext context) {
        BlockPos pos = new BlockPos(2, 1, 2);
        context.setBlockState(pos, ModBlocks.DEHUMIDIFIER.getDefaultState());

        DehumidifierBlockEntity be = (DehumidifierBlockEntity) context.getBlockEntity(pos);
        context.assertTrue(be != null, "BlockEntity must not be null");

        be.setWaterMb(0);
        context.assertTrue(be.getComparatorOutput() == 0, "0 mB must yield 0 comparator output");

        be.setWaterMb(1000);
        context.assertTrue(be.getComparatorOutput() == 7, "1000 mB must yield 7 comparator output, got: " + be.getComparatorOutput());

        be.setWaterMb(2000);
        context.assertTrue(be.getComparatorOutput() == 15, "2000 mB must yield 15 comparator output, got: " + be.getComparatorOutput());

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testRoomAtmosphereDryingBonus(TestContext context) {
        // Build sealed room 5x3x5 with water inside
        RoomTestBuilder.of(context)
                .stoneRoom(0, 0, 0, 4, 3, 4)
                .set(3, 1, 3, Blocks.WATER);

        BlockPos insidePos = context.getAbsolutePos(new BlockPos(1, 1, 1));

        // Baseline miasma calculation without dehumidifier
        var baselineMiasma = RoomAtmosphereCalculator.calculateMiasma(context.getWorld(), insidePos);
        context.assertTrue(baselineMiasma != null, "Baseline miasma must not be null");
        context.assertTrue(baselineMiasma.roomDehumidifierCount == 0, "Baseline dehumidifier count must be 0");
        context.assertTrue(baselineMiasma.dehumidifierDryingBonus == 0.0, "Baseline drying bonus must be 0.0");

        // Place active running dehumidifier into perimeter wall at (2, 1, 0)
        BlockPos dehumPos = new BlockPos(2, 1, 0);
        BlockState runningState = ModBlocks.DEHUMIDIFIER.getDefaultState()
                .with(DehumidifierBlock.STATUS, DehumidifierStatus.RUNNING);
        context.setBlockState(dehumPos, runningState);

        var activeMiasma = RoomAtmosphereCalculator.calculateMiasma(context.getWorld(), insidePos);
        context.assertTrue(activeMiasma != null, "Active miasma must not be null");
        context.assertTrue(activeMiasma.roomDehumidifierCount == 1, "Active dehumidifier count must be 1, got: " + activeMiasma.roomDehumidifierCount);
        context.assertTrue(activeMiasma.dehumidifierDryingBonus > 0.0, "Drying bonus must be > 0.0");
        context.assertTrue(activeMiasma.targetHumidity < baselineMiasma.targetHumidity,
                "Target humidity with dehumidifier (" + activeMiasma.targetHumidity + ") must be lower than baseline (" + baselineMiasma.targetHumidity + ")");
        context.assertTrue(activeMiasma.rawHumidity == baselineMiasma.rawHumidity,
                "Raw humidity must not be affected by dehumidifier (expected " + baselineMiasma.rawHumidity + ", got " + activeMiasma.rawHumidity + ")");

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testHopperFuelInsertionOmnidirectional(TestContext context) {
        BlockPos pos = new BlockPos(2, 1, 2);
        context.setBlockState(pos, ModBlocks.DEHUMIDIFIER.getDefaultState());

        DehumidifierBlockEntity be = (DehumidifierBlockEntity) context.getBlockEntity(pos);
        context.assertTrue(be != null, "BlockEntity must not be null");

        ItemStack coal = new ItemStack(Items.COAL, 1);
        ItemStack dirt = new ItemStack(Items.DIRT, 1);

        for (Direction dir : Direction.values()) {
            context.assertTrue(be.canInsert(0, coal, dir), "Must allow coal insertion from direction: " + dir);
            context.assertFalse(be.canInsert(0, dirt, dir), "Must reject dirt insertion from direction: " + dir);
            context.assertFalse(be.canExtract(0, coal, dir), "Must not allow fuel extraction from direction: " + dir);
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testFluidExtractionFabricTransferApi(TestContext context) {
        BlockPos pos = new BlockPos(2, 1, 2);
        context.setBlockState(pos, ModBlocks.DEHUMIDIFIER.getDefaultState());

        DehumidifierBlockEntity be = (DehumidifierBlockEntity) context.getBlockEntity(pos);
        context.assertTrue(be != null, "BlockEntity must not be null");

        be.setWaterMb(1000);

        for (Direction dir : Direction.values()) {
            Storage<FluidVariant> storage = FluidStorage.SIDED.find(context.getWorld(), context.getAbsolutePos(pos), dir);
            context.assertTrue(storage != null, "FluidStorage must be accessible from direction: " + dir);

            // Attempt extraction of 100 mB = 8100 droplets
            long dropletsToExtract = 100 * (FluidConstants.BUCKET / 1000);
            try (Transaction tx = Transaction.openOuter()) {
                long extracted = storage.extract(FluidVariant.of(Fluids.WATER), dropletsToExtract, tx);
                context.assertTrue(extracted == dropletsToExtract, "Must extract exactly 100 mB from direction: " + dir);
                tx.commit();
            }

            // Attempt insert (must be rejected - extraction only)
            try (Transaction tx = Transaction.openOuter()) {
                long inserted = storage.insert(FluidVariant.of(Fluids.WATER), dropletsToExtract, tx);
                context.assertTrue(inserted == 0, "Fluid insertion must be rejected from direction: " + dir);
            }
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testHybridElectricPower(TestContext context) {
        BlockPos pos = new BlockPos(2, 1, 2);
        context.setBlockState(pos, ModBlocks.DEHUMIDIFIER.getDefaultState());

        DehumidifierBlockEntity be = (DehumidifierBlockEntity) context.getBlockEntity(pos);
        context.assertTrue(be != null, "BlockEntity must not be null");

        // Test insertion from EnergyStorage.SIDED on all 6 sides (TechReborn / RF / FE compatibility)
        for (Direction dir : Direction.values()) {
            team.reborn.energy.api.EnergyStorage energyStorage = team.reborn.energy.api.EnergyStorage.SIDED.find(context.getWorld(), context.getAbsolutePos(pos), dir);
            context.assertTrue(energyStorage != null, "EnergyStorage must be accessible from direction: " + dir);
            context.assertTrue(energyStorage.supportsInsertion(), "EnergyStorage must support insertion from direction: " + dir);
            context.assertTrue(!energyStorage.supportsExtraction(), "EnergyStorage must NOT support extraction (pure consumer)");
        }

        // Insert 100 E via EnergyStorage.SIDED
        team.reborn.energy.api.EnergyStorage topStorage = team.reborn.energy.api.EnergyStorage.SIDED.find(context.getWorld(), context.getAbsolutePos(pos), Direction.UP);
        try (Transaction tx = Transaction.openOuter()) {
            long inserted = topStorage.insert(100, tx);
            context.assertTrue(inserted == 100, "Must insert exactly 100 E");
            tx.commit();
        }
        context.assertTrue(be.getEnergy() == 100, "BlockEntity energy must be 100 after external insertion");

        // Tick once with only external energy: 10 E consumed
        be.tick(context.getWorld(), context.getAbsolutePos(pos), context.getBlockState(pos));
        context.assertTrue(be.getEnergy() == 90, "Energy should drop by 10 (got " + be.getEnergy() + ")");
        context.assertTrue(be.getStatus() == DehumidifierStatus.RUNNING, "Status must be RUNNING");

        // Now provide fuel in inventory: it converts immediately into electricity
        be.setStack(0, new ItemStack(Items.COAL, 1));
        be.tick(context.getWorld(), context.getAbsolutePos(pos), context.getBlockState(pos));
        // 90 + 16000 - 10 = 16080
        context.assertTrue(be.getStack(0).isEmpty(), "Coal should be converted into energy");
        context.assertTrue(be.getEnergy() == 16080, "Energy should be 90 + 16000 - 10 = 16080 (got " + be.getEnergy() + ")");

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testTechRebornInteroperability(TestContext context) {
        Block trStorageBlock = Registries.BLOCK.get(Identifier.of("techreborn", "low_voltage_su"));
        if (trStorageBlock == Blocks.AIR) {
            // Se Tech Reborn non è presente nell'ambiente di esecuzione, il test termina subito
            context.complete();
            return;
        }

        BlockPos dehumPos = new BlockPos(2, 1, 2);
        context.setBlockState(dehumPos, ModBlocks.DEHUMIDIFIER.getDefaultState());

        BlockPos trPos = new BlockPos(2, 2, 2);
        BlockState trState = trStorageBlock.getDefaultState();
        if (trState.contains(net.minecraft.state.property.Properties.FACING)) {
            trState = trState.with(net.minecraft.state.property.Properties.FACING, Direction.DOWN);
        }
        context.setBlockState(trPos, trState);

        team.reborn.energy.api.EnergyStorage dehumEnergy = team.reborn.energy.api.EnergyStorage.SIDED.find(context.getWorld(), context.getAbsolutePos(dehumPos), Direction.UP);
        team.reborn.energy.api.EnergyStorage trInput = team.reborn.energy.api.EnergyStorage.SIDED.find(context.getWorld(), context.getAbsolutePos(trPos), Direction.UP);
        team.reborn.energy.api.EnergyStorage trOutput = team.reborn.energy.api.EnergyStorage.SIDED.find(context.getWorld(), context.getAbsolutePos(trPos), Direction.DOWN);

        context.assertTrue(dehumEnergy != null, "Dehumidifier EnergyStorage must be accessible");
        context.assertTrue(trInput != null, "TechReborn input storage must be accessible");
        context.assertTrue(trOutput != null, "TechReborn output storage must be accessible");

        // Carica la batteria di Tech Reborn dalla faccia di input (UP)
        try (Transaction tx = Transaction.openOuter()) {
            long insertedTr = trInput.insert(1000, tx);
            context.assertTrue(insertedTr > 0, "Must be able to charge TechReborn battery");
            tx.commit();
        }

        // Trasferisci energia dalla faccia di output (DOWN) di Tech Reborn al Deumidificatore
        long moved = team.reborn.energy.api.EnergyStorageUtil.move(trOutput, dehumEnergy, 200, null);
        context.assertTrue(moved > 0, "Must transfer energy from TechReborn battery to Dehumidifier (got " + moved + ")");

        DehumidifierBlockEntity be = (DehumidifierBlockEntity) context.getBlockEntity(dehumPos);
        context.assertTrue(be.getEnergy() == moved, "Dehumidifier energy must match transferred amount");

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testBuildCraftFluidTransfer(TestContext context) {
        Block bcTankBlock = Registries.BLOCK.get(Identifier.of("buildcraftfactory", "tank"));
        if (bcTankBlock == Blocks.AIR) {
            context.complete();
            return;
        }

        BlockPos dehumPos = new BlockPos(2, 1, 2);
        context.setBlockState(dehumPos, ModBlocks.DEHUMIDIFIER.getDefaultState());
        DehumidifierBlockEntity be = (DehumidifierBlockEntity) context.getBlockEntity(dehumPos);
        be.setWaterMb(1000);

        BlockPos tankPos = new BlockPos(2, 2, 2);
        context.setBlockState(tankPos, bcTankBlock.getDefaultState());

        Storage<FluidVariant> dehumStorage = FluidStorage.SIDED.find(context.getWorld(), context.getAbsolutePos(dehumPos), Direction.UP);
        Storage<FluidVariant> tankStorage = FluidStorage.SIDED.find(context.getWorld(), context.getAbsolutePos(tankPos), Direction.DOWN);

        context.assertTrue(dehumStorage != null, "Dehumidifier fluid storage must be accessible");
        context.assertTrue(tankStorage != null, "BuildCraft Tank fluid storage must be accessible");

        long movedDroplets = 0;
        try (Transaction tx = Transaction.openOuter()) {
            movedDroplets = StorageUtil.move(dehumStorage, tankStorage, f -> true, 500 * (FluidConstants.BUCKET / 1000L), tx);
            tx.commit();
        }

        context.assertTrue(movedDroplets == 500 * (FluidConstants.BUCKET / 1000L), "Must transfer 500 mB to BuildCraft Tank (got " + movedDroplets + ")");
        context.assertTrue(be.getWaterMb() == 500, "Dehumidifier water must drop to 500 mB (got " + be.getWaterMb() + ")");

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testRedstoneModes(TestContext context) {
        BlockPos pos = new BlockPos(2, 1, 2);
        context.setBlockState(pos, ModBlocks.DEHUMIDIFIER.getDefaultState());

        DehumidifierBlockEntity be = (DehumidifierBlockEntity) context.getBlockEntity(pos);
        context.assertTrue(be != null, "BlockEntity must not be null");

        // Supply coal
        be.setStack(0, new ItemStack(Items.COAL, 1));

        // 1. IGNORED mode (default) - runs without redstone
        be.setRedstoneMode(DehumidifierRedstoneMode.IGNORED);
        be.tick(context.getWorld(), context.getAbsolutePos(pos), context.getBlockState(pos));
        context.assertTrue(be.getStatus() == DehumidifierStatus.RUNNING, "IGNORED mode must run without redstone signal");
        context.assertTrue(context.getBlockState(pos).get(DehumidifierBlock.STATUS) == DehumidifierStatus.RUNNING, "BlockState must be RUNNING");

        // 2. HIGH mode with 0 redstone signal - should turn OFF
        be.setRedstoneMode(DehumidifierRedstoneMode.HIGH);
        be.tick(context.getWorld(), context.getAbsolutePos(pos), context.getBlockState(pos));
        context.assertTrue(be.getStatus() == DehumidifierStatus.OFF, "HIGH mode must pause when redstone signal is 0");
        context.assertTrue(context.getBlockState(pos).get(DehumidifierBlock.STATUS) == DehumidifierStatus.OFF, "BlockState must be OFF");

        // 3. LOW mode with 0 redstone signal - should RUN
        be.setRedstoneMode(DehumidifierRedstoneMode.LOW);
        be.tick(context.getWorld(), context.getAbsolutePos(pos), context.getBlockState(pos));
        context.assertTrue(be.getStatus() == DehumidifierStatus.RUNNING, "LOW mode must run when redstone signal is 0");
        context.assertTrue(context.getBlockState(pos).get(DehumidifierBlock.STATUS) == DehumidifierStatus.RUNNING, "BlockState must be RUNNING");

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testBlockDropBehavior(TestContext context) {
        BlockPos pos = new BlockPos(2, 1, 2);
        context.setBlockState(pos, ModBlocks.DEHUMIDIFIER.getDefaultState());

        DehumidifierBlockEntity be = (DehumidifierBlockEntity) context.getBlockEntity(pos);
        context.assertTrue(be != null, "BlockEntity must not be null");

        // Put fuel and water
        be.setStack(0, new ItemStack(Items.COAL, 2));
        be.setWaterMb(1500);

        // Break block (triggers onStateReplaced)
        ((DehumidifierBlock) context.getBlockState(pos).getBlock()).onStateReplaced(
                context.getBlockState(pos),
                context.getWorld(),
                context.getAbsolutePos(pos),
                Blocks.AIR.getDefaultState(),
                false);
        context.setBlockState(pos, Blocks.AIR.getDefaultState());

        // Verify fuel is dropped as an ItemEntity in the world
        var droppedItems = context.getWorld().getEntitiesByClass(net.minecraft.entity.ItemEntity.class,
                new net.minecraft.util.math.Box(context.getAbsolutePos(pos)).expand(3),
                item -> item.getStack().isOf(Items.COAL));

        context.assertTrue(!droppedItems.isEmpty(), "Fuel coal must be dropped on break");
        int totalCoal = droppedItems.stream().mapToInt(item -> item.getStack().getCount()).sum();
        context.assertTrue(totalCoal == 2, "Expected 2 coal dropped, got: " + totalCoal);

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testParticleAndSoundTicking(TestContext context) {
        BlockPos pos = new BlockPos(2, 1, 2);

        // RUNNING state
        BlockState runningState = ModBlocks.DEHUMIDIFIER.getDefaultState()
                .with(DehumidifierBlock.STATUS, DehumidifierStatus.RUNNING);
        context.setBlockState(pos, runningState);

        // Call randomDisplayTick (should not throw exceptions)
        ((DehumidifierBlock) ModBlocks.DEHUMIDIFIER).randomDisplayTick(
                runningState,
                context.getWorld(),
                context.getAbsolutePos(pos),
                context.getWorld().getRandom());

        // FULL state
        BlockState fullState = ModBlocks.DEHUMIDIFIER.getDefaultState()
                .with(DehumidifierBlock.STATUS, DehumidifierStatus.FULL);
        context.setBlockState(pos, fullState);

        ((DehumidifierBlock) ModBlocks.DEHUMIDIFIER).randomDisplayTick(
                fullState,
                context.getWorld(),
                context.getAbsolutePos(pos),
                context.getWorld().getRandom());

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testWaterBucketFillInHumidifyMode(TestContext context) {
        BlockPos pos = new BlockPos(2, 1, 2);
        context.setBlockState(pos, ModBlocks.DEHUMIDIFIER.getDefaultState());

        DehumidifierBlockEntity be = (DehumidifierBlockEntity) context.getBlockEntity(pos);
        context.assertTrue(be != null, "BlockEntity must not be null");

        // Set mode to HUMIDIFY
        be.setMode(DehumidifierMode.HUMIDIFY);
        be.setWaterMb(0);

        PlayerEntity player = context.createMockPlayer(net.minecraft.world.GameMode.SURVIVAL);
        player.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.WATER_BUCKET, 1));

        BlockHitResult hit = new BlockHitResult(Vec3d.ofCenter(context.getAbsolutePos(pos)), Direction.UP, context.getAbsolutePos(pos), false);

        // Right click with water bucket fills 1000 mB
        ActionResult res = context.getBlockState(pos).onUse(context.getWorld(), player, hit);
        context.assertTrue(res == ActionResult.SUCCESS, "Right click with water bucket in HUMIDIFY mode must succeed");
        context.assertTrue(be.getWaterMb() == 1000, "Tank must receive 1000 mB water, got: " + be.getWaterMb());
        context.assertTrue(player.getStackInHand(Hand.MAIN_HAND).isOf(Items.BUCKET), "Player must receive empty bucket");

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testFluidInsertionAndExtractionByMode(TestContext context) {
        BlockPos pos = new BlockPos(2, 1, 2);
        context.setBlockState(pos, ModBlocks.DEHUMIDIFIER.getDefaultState());

        DehumidifierBlockEntity be = (DehumidifierBlockEntity) context.getBlockEntity(pos);
        context.assertTrue(be != null, "BlockEntity must not be null");

        // 1. In DEHUMIDIFY mode: insertion rejected, extraction allowed
        be.setMode(DehumidifierMode.DEHUMIDIFY);
        be.setWaterMb(1000);
        Storage<FluidVariant> storageDehum = FluidStorage.SIDED.find(context.getWorld(), context.getAbsolutePos(pos), Direction.NORTH);
        context.assertTrue(storageDehum != null, "Storage must exist");

        long droplets100Mb = 100 * (FluidConstants.BUCKET / 1000);
        try (Transaction tx = Transaction.openOuter()) {
            long inserted = storageDehum.insert(FluidVariant.of(Fluids.WATER), droplets100Mb, tx);
            context.assertTrue(inserted == 0, "Insertion must be rejected in DEHUMIDIFY mode");
        }
        try (Transaction tx = Transaction.openOuter()) {
            long extracted = storageDehum.extract(FluidVariant.of(Fluids.WATER), droplets100Mb, tx);
            context.assertTrue(extracted == droplets100Mb, "Extraction must succeed in DEHUMIDIFY mode");
            tx.commit();
        }
        context.assertTrue(be.getWaterMb() == 900, "Water level must decrease to 900");

        // 2. In HUMIDIFY mode: insertion allowed (pure water only), extraction rejected
        be.setMode(DehumidifierMode.HUMIDIFY);
        Storage<FluidVariant> storageHum = FluidStorage.SIDED.find(context.getWorld(), context.getAbsolutePos(pos), Direction.NORTH);
        context.assertTrue(storageHum != null, "Storage must exist");

        // Non-water insertion rejected
        try (Transaction tx = Transaction.openOuter()) {
            long insertedLava = storageHum.insert(FluidVariant.of(Fluids.LAVA), droplets100Mb, tx);
            context.assertTrue(insertedLava == 0, "Non-water fluid insertion must be rejected");
        }

        // Pure water insertion succeeds
        try (Transaction tx = Transaction.openOuter()) {
            long insertedWater = storageHum.insert(FluidVariant.of(Fluids.WATER), droplets100Mb, tx);
            context.assertTrue(insertedWater == droplets100Mb, "Water insertion must succeed in HUMIDIFY mode");
            tx.commit();
        }
        context.assertTrue(be.getWaterMb() == 1000, "Water level must be back to 1000 mB");

        // Extraction rejected in HUMIDIFY mode
        try (Transaction tx = Transaction.openOuter()) {
            long extractedHum = storageHum.extract(FluidVariant.of(Fluids.WATER), droplets100Mb, tx);
            context.assertTrue(extractedHum == 0, "Extraction must be rejected in HUMIDIFY mode");
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testHumidifierOperationAndAtmosphereBonus(TestContext context) {
        // Build sealed room 5x3x5
        RoomTestBuilder.of(context)
                .stoneRoom(0, 0, 0, 4, 3, 4);

        BlockPos insidePos = context.getAbsolutePos(new BlockPos(1, 1, 1));

        // Baseline miasma calculation
        var baselineMiasma = RoomAtmosphereCalculator.calculateMiasma(context.getWorld(), insidePos);
        context.assertTrue(baselineMiasma != null, "Baseline miasma must not be null");
        context.assertTrue(baselineMiasma.roomHumidifierCount == 0, "Baseline humidifier count must be 0");
        context.assertTrue(baselineMiasma.humidifierMoistureBonus == 0.0, "Baseline humidifier moisture bonus must be 0.0");

        // Place active running humidifier in perimeter wall
        BlockPos humPos = new BlockPos(2, 1, 0);
        BlockState runningHumidifier = ModBlocks.DEHUMIDIFIER.getDefaultState()
                .with(DehumidifierBlock.STATUS, DehumidifierStatus.RUNNING)
                .with(DehumidifierBlock.MODE, DehumidifierMode.HUMIDIFY);
        context.setBlockState(humPos, runningHumidifier);

        DehumidifierBlockEntity be = (DehumidifierBlockEntity) context.getBlockEntity(humPos);
        context.assertTrue(be != null, "BlockEntity must not be null");
        be.setEnergy(1000);
        be.setWaterMb(500);
        be.setMode(DehumidifierMode.HUMIDIFY);
        be.setStack(0, new ItemStack(Items.COAL, 1));

        var activeMiasma = RoomAtmosphereCalculator.calculateMiasma(context.getWorld(), insidePos);
        context.assertTrue(activeMiasma != null, "Active miasma must not be null");
        context.assertTrue(activeMiasma.roomHumidifierCount == 1, "Active humidifier count must be 1, got: " + activeMiasma.roomHumidifierCount);
        context.assertTrue(activeMiasma.humidifierMoistureBonus > 0.0, "Humidifier moisture bonus must be > 0.0");
        context.assertTrue(activeMiasma.targetHumidity > baselineMiasma.targetHumidity,
                "Target humidity with humidifier (" + activeMiasma.targetHumidity + ") must be higher than baseline (" + baselineMiasma.targetHumidity + ")");
        context.assertTrue(activeMiasma.rawHumidity > baselineMiasma.rawHumidity,
                "Raw humidity with humidifier (" + activeMiasma.rawHumidity + ") must be higher than baseline (" + baselineMiasma.rawHumidity + ")");

        // Tick humidifier: it should consume water and energy
        int initialWater = be.getWaterMb();
        int ticksPerMb = be.getTicksPerMb();
        for (int i = 0; i < ticksPerMb + 5; i++) {
            be.tick(context.getWorld(), context.getAbsolutePos(humPos), context.getBlockState(humPos));
        }
        context.assertTrue(be.getWaterMb() == initialWater - 1, "Humidifier must consume 1 mB of water, expected: " + (initialWater - 1) + ", got: " + be.getWaterMb());

        context.complete();
    }
}
