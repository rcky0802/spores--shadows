package moldmod.test.gametest.wood;

import moldmod.SporesShadows;
import moldmod.block.*;
import moldmod.block.entity.*;
import moldmod.screen.MoldStageHolder;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.enums.ChestType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.state.property.Properties;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.poi.PointOfInterestType;
import net.minecraft.world.poi.PointOfInterestTypes;

import java.util.Optional;

public class MoldyWorkstationsAndContainersGameTests {

    // =========================================================================
    // === 1. REGISTRATION & BLOCK ENTITY SUPPORT                            ===
    // =========================================================================

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testWorkstationsAndContainersRegistrationAndTags(TestContext context) {
        Block[] blocks = {
                ModBlocks.MOLDY_CRAFTING_TABLE, ModBlocks.WAXED_CRAFTING_TABLE,
                ModBlocks.MOLDY_BARREL, ModBlocks.WAXED_BARREL,
                ModBlocks.MOLDY_CHEST, ModBlocks.WAXED_CHEST,
                ModBlocks.MOLDY_TRAPPED_CHEST, ModBlocks.WAXED_TRAPPED_CHEST,
                ModBlocks.MOLDY_COMPOSTER, ModBlocks.WAXED_COMPOSTER,
                ModBlocks.MOLDY_FLETCHING_TABLE, ModBlocks.WAXED_FLETCHING_TABLE,
                ModBlocks.MOLDY_CARTOGRAPHY_TABLE, ModBlocks.WAXED_CARTOGRAPHY_TABLE,
                ModBlocks.MOLDY_LOOM, ModBlocks.WAXED_LOOM,
                ModBlocks.MOLDY_LECTERN, ModBlocks.WAXED_LECTERN
        };

        for (Block b : blocks) {
            if (b == null || b == Blocks.AIR) {
                context.throwPositionedException("A workstation or container block is not registered!", BlockPos.ORIGIN);
            }
            if (!b.getDefaultState().isIn(BlockTags.AXE_MINEABLE)) {
                context.throwPositionedException(Registries.BLOCK.getId(b) + " is not axe mineable!", BlockPos.ORIGIN);
            }
        }

        // Check BlockEntity type support
        if (!ModBlockEntities.MOLDY_BARREL.supports(ModBlocks.MOLDY_BARREL.getDefaultState())
                || !ModBlockEntities.MOLDY_BARREL.supports(ModBlocks.WAXED_BARREL.getDefaultState())) {
            context.throwPositionedException("MOLDY_BARREL BlockEntityType does not support its block states!", BlockPos.ORIGIN);
        }
        if (!ModBlockEntities.MOLDY_CHEST.supports(ModBlocks.MOLDY_CHEST.getDefaultState())
                || !ModBlockEntities.MOLDY_CHEST.supports(ModBlocks.WAXED_CHEST.getDefaultState())) {
            context.throwPositionedException("MOLDY_CHEST BlockEntityType does not support its block states!", BlockPos.ORIGIN);
        }
        if (!ModBlockEntities.MOLDY_TRAPPED_CHEST.supports(ModBlocks.MOLDY_TRAPPED_CHEST.getDefaultState())
                || !ModBlockEntities.MOLDY_TRAPPED_CHEST.supports(ModBlocks.WAXED_TRAPPED_CHEST.getDefaultState())) {
            context.throwPositionedException("MOLDY_TRAPPED_CHEST BlockEntityType does not support its block states!", BlockPos.ORIGIN);
        }
        if (!ModBlockEntities.MOLDY_LECTERN.supports(ModBlocks.MOLDY_LECTERN.getDefaultState())
                || !ModBlockEntities.MOLDY_LECTERN.supports(ModBlocks.WAXED_LECTERN.getDefaultState())) {
            context.throwPositionedException("MOLDY_LECTERN BlockEntityType does not support its block states!", BlockPos.ORIGIN);
        }

        context.complete();
    }

    // =========================================================================
    // === 2. POI WORKSTATION MATCHING (100% VILLAGER COMPATIBILITY)        ===
    // =========================================================================

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testPointOfInterestRegistration(TestContext context) {
        // Barrel -> Fisherman
        for (int stage = 0; stage <= 3; stage++) {
            BlockState barrelState = ModBlocks.MOLDY_BARREL.getDefaultState().with(MoldyBlock.STAGE, stage);
            Optional<RegistryEntry<PointOfInterestType>> poi = PointOfInterestTypes.getTypeForState(barrelState);
            if (poi.isEmpty() || !poi.get().matchesKey(PointOfInterestTypes.FISHERMAN)) {
                context.throwPositionedException("Moldy Barrel at stage " + stage + " not matched to Fisherman POI!", BlockPos.ORIGIN);
            }
        }
        Optional<RegistryEntry<PointOfInterestType>> waxedBarrelPoi = PointOfInterestTypes.getTypeForState(ModBlocks.WAXED_BARREL.getDefaultState());
        if (waxedBarrelPoi.isEmpty() || !waxedBarrelPoi.get().matchesKey(PointOfInterestTypes.FISHERMAN)) {
            context.throwPositionedException("Waxed Barrel not matched to Fisherman POI!", BlockPos.ORIGIN);
        }

        // Composter -> Farmer
        for (int stage = 0; stage <= 3; stage++) {
            BlockState composterState = ModBlocks.MOLDY_COMPOSTER.getDefaultState().with(MoldyBlock.STAGE, stage);
            Optional<RegistryEntry<PointOfInterestType>> poi = PointOfInterestTypes.getTypeForState(composterState);
            if (poi.isEmpty() || !poi.get().matchesKey(PointOfInterestTypes.FARMER)) {
                context.throwPositionedException("Moldy Composter at stage " + stage + " not matched to Farmer POI!", BlockPos.ORIGIN);
            }
        }
        Optional<RegistryEntry<PointOfInterestType>> waxedComposterPoi = PointOfInterestTypes.getTypeForState(ModBlocks.WAXED_COMPOSTER.getDefaultState());
        if (waxedComposterPoi.isEmpty() || !waxedComposterPoi.get().matchesKey(PointOfInterestTypes.FARMER)) {
            context.throwPositionedException("Waxed Composter not matched to Farmer POI!", BlockPos.ORIGIN);
        }

        // Fletching Table -> Fletcher
        for (int stage = 0; stage <= 3; stage++) {
            BlockState fletchingState = ModBlocks.MOLDY_FLETCHING_TABLE.getDefaultState().with(MoldyBlock.STAGE, stage);
            Optional<RegistryEntry<PointOfInterestType>> poi = PointOfInterestTypes.getTypeForState(fletchingState);
            if (poi.isEmpty() || !poi.get().matchesKey(PointOfInterestTypes.FLETCHER)) {
                context.throwPositionedException("Moldy Fletching Table at stage " + stage + " not matched to Fletcher POI!", BlockPos.ORIGIN);
            }
        }
        Optional<RegistryEntry<PointOfInterestType>> waxedFletchingPoi = PointOfInterestTypes.getTypeForState(ModBlocks.WAXED_FLETCHING_TABLE.getDefaultState());
        if (waxedFletchingPoi.isEmpty() || !waxedFletchingPoi.get().matchesKey(PointOfInterestTypes.FLETCHER)) {
            context.throwPositionedException("Waxed Fletching Table not matched to Fletcher POI!", BlockPos.ORIGIN);
        }

        // Cartography Table -> Cartographer
        for (int stage = 0; stage <= 3; stage++) {
            BlockState cartState = ModBlocks.MOLDY_CARTOGRAPHY_TABLE.getDefaultState().with(MoldyBlock.STAGE, stage);
            Optional<RegistryEntry<PointOfInterestType>> poi = PointOfInterestTypes.getTypeForState(cartState);
            if (poi.isEmpty() || !poi.get().matchesKey(PointOfInterestTypes.CARTOGRAPHER)) {
                context.throwPositionedException("Moldy Cartography Table at stage " + stage + " not matched to Cartographer POI!", BlockPos.ORIGIN);
            }
        }
        Optional<RegistryEntry<PointOfInterestType>> waxedCartPoi = PointOfInterestTypes.getTypeForState(ModBlocks.WAXED_CARTOGRAPHY_TABLE.getDefaultState());
        if (waxedCartPoi.isEmpty() || !waxedCartPoi.get().matchesKey(PointOfInterestTypes.CARTOGRAPHER)) {
            context.throwPositionedException("Waxed Cartography Table not matched to Cartographer POI!", BlockPos.ORIGIN);
        }

        // Loom -> Shepherd
        for (int stage = 0; stage <= 3; stage++) {
            BlockState loomState = ModBlocks.MOLDY_LOOM.getDefaultState().with(MoldyBlock.STAGE, stage);
            Optional<RegistryEntry<PointOfInterestType>> poi = PointOfInterestTypes.getTypeForState(loomState);
            if (poi.isEmpty() || !poi.get().matchesKey(PointOfInterestTypes.SHEPHERD)) {
                context.throwPositionedException("Moldy Loom at stage " + stage + " not matched to Shepherd POI!", BlockPos.ORIGIN);
            }
        }
        Optional<RegistryEntry<PointOfInterestType>> waxedLoomPoi = PointOfInterestTypes.getTypeForState(ModBlocks.WAXED_LOOM.getDefaultState());
        if (waxedLoomPoi.isEmpty() || !waxedLoomPoi.get().matchesKey(PointOfInterestTypes.SHEPHERD)) {
            context.throwPositionedException("Waxed Loom not matched to Shepherd POI!", BlockPos.ORIGIN);
        }

        // Lectern -> Librarian
        for (int stage = 0; stage <= 3; stage++) {
            BlockState lecternState = ModBlocks.MOLDY_LECTERN.getDefaultState().with(MoldyBlock.STAGE, stage);
            Optional<RegistryEntry<PointOfInterestType>> poi = PointOfInterestTypes.getTypeForState(lecternState);
            if (poi.isEmpty() || !poi.get().matchesKey(PointOfInterestTypes.LIBRARIAN)) {
                context.throwPositionedException("Moldy Lectern at stage " + stage + " not matched to Librarian POI!", BlockPos.ORIGIN);
            }
        }
        Optional<RegistryEntry<PointOfInterestType>> waxedLecternPoi = PointOfInterestTypes.getTypeForState(ModBlocks.WAXED_LECTERN.getDefaultState());
        if (waxedLecternPoi.isEmpty() || !waxedLecternPoi.get().matchesKey(PointOfInterestTypes.LIBRARIAN)) {
            context.throwPositionedException("Waxed Lectern not matched to Librarian POI!", BlockPos.ORIGIN);
        }

        context.complete();
    }

    // =========================================================================
    // === 3. BARREL INVENTORY PRESERVATION ON DECAY AND WAXING             ===
    // =========================================================================

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testBarrelInventoryPreservation(TestContext context) {
        BlockPos pos = new BlockPos(1, 1, 1);
        BlockState initial = ModBlocks.MOLDY_BARREL.getDefaultState().with(MoldyBlock.STAGE, 1);
        context.setBlockState(pos, initial);

        MoldyBarrelBlockEntity be = (MoldyBarrelBlockEntity) context.getBlockEntity(pos);
        if (be == null) {
            context.throwPositionedException("MoldyBarrelBlockEntity missing!", pos);
        }
        be.setStack(0, new ItemStack(Items.DIAMOND, 5));
        be.setStack(10, new ItemStack(Items.GOLD_INGOT, 12));

        // Advance to Stage 2
        MoldyBlockHelper.setStage(context.getWorld(), context.getAbsolutePos(pos), context.getBlockState(pos), 2);
        MoldyBarrelBlockEntity be2 = (MoldyBarrelBlockEntity) context.getBlockEntity(pos);
        if (be2 == null || be2.getStack(0).getCount() != 5 || be2.getStack(10).getCount() != 12) {
            context.throwPositionedException("Barrel contents corrupted after stage transition 1 -> 2!", pos);
        }

        // Advance to Stage 3 (Rotten)
        MoldyBlockHelper.setStage(context.getWorld(), context.getAbsolutePos(pos), context.getBlockState(pos), 3);
        MoldyBarrelBlockEntity be3 = (MoldyBarrelBlockEntity) context.getBlockEntity(pos);
        if (be3 == null || be3.getStack(0).getCount() != 5 || be3.getStack(10).getCount() != 12) {
            context.throwPositionedException("Barrel contents corrupted after stage transition 2 -> 3!", pos);
        }

        // Wax the barrel
        MoldyBlockHelper.setWaxed(context.getWorld(), context.getAbsolutePos(pos), context.getBlockState(pos), true);
        MoldyBarrelBlockEntity beWaxed = (MoldyBarrelBlockEntity) context.getBlockEntity(pos);
        if (beWaxed == null || beWaxed.getStack(0).getCount() != 5 || beWaxed.getStack(10).getCount() != 12) {
            context.throwPositionedException("Barrel contents corrupted after waxing!", pos);
        }

        context.complete();
    }

    // =========================================================================
    // === 4. CHEST INVENTORY PRESERVATION ON DECAY AND WAXING              ===
    // =========================================================================

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testChestInventoryPreservation(TestContext context) {
        BlockPos pos = new BlockPos(1, 1, 1);
        BlockState initial = ModBlocks.MOLDY_CHEST.getDefaultState().with(MoldyBlock.STAGE, 1);
        context.setBlockState(pos, initial);

        MoldyChestBlockEntity be = (MoldyChestBlockEntity) context.getBlockEntity(pos);
        if (be == null) {
            context.throwPositionedException("MoldyChestBlockEntity missing!", pos);
        }
        be.setStack(0, new ItemStack(Items.EMERALD, 16));
        be.setStack(20, new ItemStack(Items.IRON_INGOT, 32));

        // Advance to Stage 2
        MoldyBlockHelper.setStage(context.getWorld(), context.getAbsolutePos(pos), context.getBlockState(pos), 2);
        MoldyChestBlockEntity be2 = (MoldyChestBlockEntity) context.getBlockEntity(pos);
        if (be2 == null || be2.getStack(0).getCount() != 16 || be2.getStack(20).getCount() != 32) {
            context.throwPositionedException("Chest contents corrupted after stage transition 1 -> 2!", pos);
        }

        // Wax
        MoldyBlockHelper.setWaxed(context.getWorld(), context.getAbsolutePos(pos), context.getBlockState(pos), true);
        MoldyChestBlockEntity beWaxed = (MoldyChestBlockEntity) context.getBlockEntity(pos);
        if (beWaxed == null || beWaxed.getStack(0).getCount() != 16 || beWaxed.getStack(20).getCount() != 32) {
            context.throwPositionedException("Chest contents corrupted after waxing!", pos);
        }

        context.complete();
    }

    // =========================================================================
    // === 5. DOUBLE CHEST PAIRING AND SYNCHRONIZATION                      ===
    // =========================================================================

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testDoubleChestPairingAndSync(TestContext context) {
        BlockPos pos1 = new BlockPos(1, 1, 1);
        BlockPos pos2 = new BlockPos(2, 1, 1);

        // Place two chests side by side facing North at stage 1
        BlockState chestState1 = ModBlocks.MOLDY_CHEST.getDefaultState()
                .with(Properties.HORIZONTAL_FACING, Direction.NORTH)
                .with(MoldyBlock.STAGE, 1)
                .with(Properties.CHEST_TYPE, ChestType.RIGHT);
        BlockState chestState2 = ModBlocks.MOLDY_CHEST.getDefaultState()
                .with(Properties.HORIZONTAL_FACING, Direction.NORTH)
                .with(MoldyBlock.STAGE, 1)
                .with(Properties.CHEST_TYPE, ChestType.LEFT);

        context.setBlockState(pos1, chestState1);
        context.setBlockState(pos2, chestState2);

        // Now advance pos1 to stage 2 -> syncChestHalf should advance pos2 to stage 2 as well
        MoldyBlockHelper.setStage(context.getWorld(), context.getAbsolutePos(pos1), context.getBlockState(pos1), 2);

        BlockState state2AfterSync = context.getBlockState(pos2);
        if (state2AfterSync.get(MoldyBlock.STAGE) != 2) {
            context.throwPositionedException("Double chest partner was not synced to stage 2! Got: " + state2AfterSync.get(MoldyBlock.STAGE), pos2);
        }

        // Wax pos1 -> syncChestHalf should wax pos2 as well
        MoldyBlockHelper.setWaxed(context.getWorld(), context.getAbsolutePos(pos1), context.getBlockState(pos1), true);
        BlockState state2AfterWax = context.getBlockState(pos2);
        if (!state2AfterWax.get(MoldyBlock.WAXED)) {
            context.throwPositionedException("Double chest partner was not synced to waxed!", pos2);
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testDoubleChestPlacementConnecting(TestContext context) {
        Direction[] facings = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};
        float[] yaws = new float[]{180.0f, 0.0f, 270.0f, 90.0f};

        for (int i = 0; i < facings.length; i++) {
            Direction playerFacing = facings[i];
            float yaw = yaws[i];

            // pos1 at y=1, pos2 to the right of player facing
            Direction rightDir = playerFacing.rotateYClockwise();
            BlockPos basePos = new BlockPos(2, 1, 2);
            BlockPos p1 = basePos;
            BlockPos p2 = basePos.offset(rightDir);

            // Clean up test area
            context.setBlockState(p1, Blocks.AIR.getDefaultState());
            context.setBlockState(p2, Blocks.AIR.getDefaultState());

            Item chestItem = Registries.ITEM.get(SporesShadows.id("moldy_chest"));
            PlayerEntity player = context.createMockPlayer(GameMode.SURVIVAL);
            player.setYaw(yaw);

            BlockHitResult hit1 = new BlockHitResult(net.minecraft.util.math.Vec3d.ofCenter(context.getAbsolutePos(p1)), Direction.UP, context.getAbsolutePos(p1), false);
            ItemPlacementContext ctx1 = new ItemPlacementContext(player, net.minecraft.util.Hand.MAIN_HAND, new ItemStack(chestItem), hit1);
            ((net.minecraft.item.BlockItem) chestItem).place(ctx1);

            BlockHitResult hit2 = new BlockHitResult(net.minecraft.util.math.Vec3d.ofCenter(context.getAbsolutePos(p2)), Direction.UP, context.getAbsolutePos(p2), false);
            ItemPlacementContext ctx2 = new ItemPlacementContext(player, net.minecraft.util.Hand.MAIN_HAND, new ItemStack(chestItem), hit2);
            ((net.minecraft.item.BlockItem) chestItem).place(ctx2);

            BlockState state1 = context.getBlockState(p1);
            BlockState state2 = context.getBlockState(p2);

            if (state1.get(Properties.CHEST_TYPE) == ChestType.SINGLE || state2.get(Properties.CHEST_TYPE) == ChestType.SINGLE) {
                context.throwPositionedException("Chests did not connect facing " + playerFacing + "! Pos1: " + state1.get(Properties.CHEST_TYPE) + ", Pos2: " + state2.get(Properties.CHEST_TYPE), p1);
            }

            // Also test reverse placement order (p2 first, then p1 to the left)
            context.setBlockState(p1, Blocks.AIR.getDefaultState());
            context.setBlockState(p2, Blocks.AIR.getDefaultState());

            ((net.minecraft.item.BlockItem) chestItem).place(ctx2);
            ((net.minecraft.item.BlockItem) chestItem).place(ctx1);

            state1 = context.getBlockState(p1);
            state2 = context.getBlockState(p2);

            if (state1.get(Properties.CHEST_TYPE) == ChestType.SINGLE || state2.get(Properties.CHEST_TYPE) == ChestType.SINGLE) {
                context.throwPositionedException("Chests in reverse did not connect facing " + playerFacing + "! Pos1: " + state1.get(Properties.CHEST_TYPE) + ", Pos2: " + state2.get(Properties.CHEST_TYPE), p1);
            }
        }

        String[] itemNames = new String[]{
                "waxed_chest", "tainted_chest", "waxed_tainted_chest", "moldy_chest", "waxed_moldy_chest", "rotten_chest", "waxed_rotten_chest",
                "waxed_trapped_chest", "tainted_trapped_chest", "waxed_tainted_trapped_chest", "moldy_trapped_chest", "waxed_moldy_trapped_chest", "rotten_trapped_chest", "waxed_rotten_trapped_chest"
        };

        for (String itemName : itemNames) {
            Item item = Registries.ITEM.get(SporesShadows.id(itemName));
            BlockPos p1 = new BlockPos(2, 1, 2);
            BlockPos p2 = new BlockPos(3, 1, 2);

            context.setBlockState(p1, Blocks.AIR.getDefaultState());
            context.setBlockState(p2, Blocks.AIR.getDefaultState());

            PlayerEntity player = context.createMockPlayer(GameMode.SURVIVAL);
            player.setYaw(0.0f); // Looking SOUTH

            // Place first chest
            BlockHitResult hit1 = new BlockHitResult(net.minecraft.util.math.Vec3d.ofCenter(context.getAbsolutePos(p1)), Direction.UP, context.getAbsolutePos(p1), false);
            ItemPlacementContext ctx1 = new ItemPlacementContext(player, net.minecraft.util.Hand.MAIN_HAND, new ItemStack(item), hit1);
            ((net.minecraft.item.BlockItem) item).place(ctx1);

            // Place second chest by clicking side of first chest while sneaking (bl = true)
            BlockHitResult hitSide = new BlockHitResult(net.minecraft.util.math.Vec3d.ofCenter(context.getAbsolutePos(p1)), Direction.EAST, context.getAbsolutePos(p1), false);
            player.setSneaking(true);
            ItemPlacementContext ctxSide = new ItemPlacementContext(player, net.minecraft.util.Hand.MAIN_HAND, new ItemStack(item), hitSide);
            ((net.minecraft.item.BlockItem) item).place(ctxSide);

            BlockState s1 = context.getBlockState(p1);
            BlockState s2 = context.getBlockState(p2);

            if (s1.get(Properties.CHEST_TYPE) == ChestType.SINGLE || s2.get(Properties.CHEST_TYPE) == ChestType.SINGLE) {
                context.throwPositionedException("Chests by side click did not connect for " + itemName + "! Pos1: " + s1.get(Properties.CHEST_TYPE) + ", Pos2: " + s2.get(Properties.CHEST_TYPE), p1);
            }
        }

        context.complete();
    }

    // =========================================================================
    // === 6. TRAPPED CHEST JAMMING LOGIC                                   ===
    // =========================================================================

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testTrappedChestJamming(TestContext context) {
        BlockPos pos = new BlockPos(1, 1, 1);
        BlockState state = ModBlocks.MOLDY_TRAPPED_CHEST.getDefaultState()
                .with(Properties.HORIZONTAL_FACING, Direction.NORTH)
                .with(MoldyBlock.STAGE, 2);
        context.setBlockState(pos, state);

        MoldyTrappedChestBlockEntity be = (MoldyTrappedChestBlockEntity) context.getBlockEntity(pos);
        if (be == null) {
            context.throwPositionedException("MoldyTrappedChestBlockEntity missing!", pos);
        }

        // Initially not jammed
        if (be.isJammed()) {
            context.throwPositionedException("Trapped chest should not be initially jammed!", pos);
        }

        // Force jammed state
        be.setJammed(true);
        int power = context.getBlockState(pos).getWeakRedstonePower(context.getWorld(), context.getAbsolutePos(pos), Direction.NORTH);
        if (power != 0) {
            context.throwPositionedException("Jammed trapped chest must emit 0 redstone power! Got: " + power, pos);
        }

        context.complete();
    }

    // =========================================================================
    // === 7. COMPOSTER FUNCTIONALITY & COMPARATOR                          ===
    // =========================================================================

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testComposterFunctionality(TestContext context) {
        BlockPos pos = new BlockPos(1, 1, 1);

        for (int level = 0; level <= 8; level++) {
            BlockState state = ModBlocks.MOLDY_COMPOSTER.getDefaultState()
                    .with(MoldyComposterBlock.LEVEL, level)
                    .with(MoldyBlock.STAGE, 1);
            context.setBlockState(pos, state);

            int compOutput = state.getComparatorOutput(context.getWorld(), context.getAbsolutePos(pos));
            if (compOutput != level) {
                context.throwPositionedException("Composter comparator output mismatch at level " + level + "! Got: " + compOutput, pos);
            }
        }

        context.complete();
    }

    // =========================================================================
    // === 8. CRAFTING TABLE STAGE HOLDER                                   ===
    // =========================================================================

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testCraftingTableStageHolder(TestContext context) {
        PlayerEntity player = context.createMockPlayer(GameMode.SURVIVAL);
        BlockPos pos = new BlockPos(1, 1, 1);
        BlockState state = ModBlocks.MOLDY_CRAFTING_TABLE.getDefaultState().with(MoldyBlock.STAGE, 2);
        context.setBlockState(pos, state);

        CraftingScreenHandler handler = new CraftingScreenHandler(1, player.getInventory(), ScreenHandlerContext.create(context.getWorld(), context.getAbsolutePos(pos)));
        if (handler instanceof MoldStageHolder holder) {
            holder.spores_shadows$setMoldStage(2);
            if (holder.spores_shadows$getMoldStage() != 2) {
                context.throwPositionedException("MoldStageHolder failed to store stage 2 in CraftingScreenHandler!", pos);
            }
        } else {
            context.throwPositionedException("CraftingScreenHandler does not implement MoldStageHolder!", pos);
        }

        context.complete();
    }

    // =========================================================================
    // === 9. DOUBLE CHEST WAXING & CROSS-VARIANT SYNCHRONIZATION            ===
    // =========================================================================

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testDoubleChestCrossVariantWaxingAndInventory(TestContext context) {
        BlockPos p1 = new BlockPos(1, 1, 1);
        BlockPos p2 = new BlockPos(2, 1, 1);

        context.setBlockState(p1, Blocks.AIR.getDefaultState());
        context.setBlockState(p2, Blocks.AIR.getDefaultState());

        Item moldyChestItem = Registries.ITEM.get(SporesShadows.id("moldy_chest"));
        PlayerEntity player = context.createMockPlayer(GameMode.SURVIVAL);
        player.setYaw(0.0f); // Looking SOUTH

        BlockHitResult hit1 = new BlockHitResult(net.minecraft.util.math.Vec3d.ofCenter(context.getAbsolutePos(p1)), Direction.UP, context.getAbsolutePos(p1), false);
        ItemPlacementContext ctx1 = new ItemPlacementContext(player, net.minecraft.util.Hand.MAIN_HAND, new ItemStack(moldyChestItem), hit1);
        ((net.minecraft.item.BlockItem) moldyChestItem).place(ctx1);

        BlockHitResult hit2 = new BlockHitResult(net.minecraft.util.math.Vec3d.ofCenter(context.getAbsolutePos(p2)), Direction.UP, context.getAbsolutePos(p2), false);
        ItemPlacementContext ctx2 = new ItemPlacementContext(player, net.minecraft.util.Hand.MAIN_HAND, new ItemStack(moldyChestItem), hit2);
        ((net.minecraft.item.BlockItem) moldyChestItem).place(ctx2);

        BlockState s1 = context.getBlockState(p1);
        BlockState s2 = context.getBlockState(p2);

        context.assertTrue(s1.get(Properties.CHEST_TYPE) != ChestType.SINGLE && s2.get(Properties.CHEST_TYPE) != ChestType.SINGLE,
                "Two placed moldy chests must connect into double chest!");

        // Put items in chest
        MoldyChestBlockEntity be1 = (MoldyChestBlockEntity) context.getBlockEntity(p1);
        MoldyChestBlockEntity be2 = (MoldyChestBlockEntity) context.getBlockEntity(p2);
        be1.setStack(0, new ItemStack(Items.DIAMOND, 10));
        be2.setStack(0, new ItemStack(Items.GOLD_INGOT, 20));

        // Wax p1: should synchronize p2 and convert both to WAXED_CHEST
        Block waxedBlock = ModBlocks.MOLDY_TO_WAXED.get(s1.getBlock());
        BlockState newWaxedState = MoldyBlockHelper.copyMatchingProperties(s1, waxedBlock.getDefaultState()).with(MoldyBlock.WAXED, true);
        MoldyBlockHelper.setWaxed(context.getWorld(), context.getAbsolutePos(p1), newWaxedState, true);

        BlockState s1Waxed = context.getBlockState(p1);
        BlockState s2Waxed = context.getBlockState(p2);

        context.assertTrue(s1Waxed.isOf(ModBlocks.WAXED_CHEST), "p1 must be WAXED_CHEST after waxing");
        context.assertTrue(s2Waxed.isOf(ModBlocks.WAXED_CHEST), "p2 must be WAXED_CHEST after waxing p1");
        context.assertTrue(s1Waxed.get(MoldyBlock.WAXED), "p1 must have WAXED=true");
        context.assertTrue(s2Waxed.get(MoldyBlock.WAXED), "p2 must have WAXED=true");
        context.assertTrue(s1Waxed.get(Properties.CHEST_TYPE) != ChestType.SINGLE, "p1 must remain double chest");
        context.assertTrue(s2Waxed.get(Properties.CHEST_TYPE) != ChestType.SINGLE, "p2 must remain double chest");

        // Verify inventory preserved
        MoldyChestBlockEntity be1AfterWax = (MoldyChestBlockEntity) context.getBlockEntity(p1);
        MoldyChestBlockEntity be2AfterWax = (MoldyChestBlockEntity) context.getBlockEntity(p2);
        context.assertEquals(Items.DIAMOND, be1AfterWax.getStack(0).getItem(), "p1 inventory must be preserved after waxing");
        context.assertEquals(10, be1AfterWax.getStack(0).getCount(), "p1 stack count must be preserved after waxing");
        context.assertEquals(Items.GOLD_INGOT, be2AfterWax.getStack(0).getItem(), "p2 inventory must be preserved after waxing");
        context.assertEquals(20, be2AfterWax.getStack(0).getCount(), "p2 stack count must be preserved after waxing");

        // Dewax p1: should synchronize p2 and convert both back to MOLDY_CHEST
        Block moldyBlock = ModBlocks.WAXED_TO_MOLDY.get(s1Waxed.getBlock());
        BlockState newMoldyState = MoldyBlockHelper.copyMatchingProperties(s1Waxed, moldyBlock.getDefaultState()).with(MoldyBlock.WAXED, false);
        MoldyBlockHelper.setWaxed(context.getWorld(), context.getAbsolutePos(p1), newMoldyState, false);

        BlockState s1Unwaxed = context.getBlockState(p1);
        BlockState s2Unwaxed = context.getBlockState(p2);

        context.assertTrue(s1Unwaxed.isOf(ModBlocks.MOLDY_CHEST), "p1 must be MOLDY_CHEST after dewaxing");
        context.assertTrue(s2Unwaxed.isOf(ModBlocks.MOLDY_CHEST), "p2 must be MOLDY_CHEST after dewaxing");
        context.assertTrue(!s1Unwaxed.get(MoldyBlock.WAXED), "p1 must have WAXED=false");
        context.assertTrue(!s2Unwaxed.get(MoldyBlock.WAXED), "p2 must have WAXED=false");

        // Mismatched stage refusal: clear and place stage 1 next to stage 2
        BlockPos p3 = new BlockPos(1, 1, 3);
        BlockPos p4 = new BlockPos(2, 1, 3);
        context.setBlockState(p3, Blocks.AIR.getDefaultState());
        context.setBlockState(p4, Blocks.AIR.getDefaultState());

        Item taintedChestItem = Registries.ITEM.get(SporesShadows.id("tainted_chest")); // Stage 1
        BlockHitResult hit3 = new BlockHitResult(net.minecraft.util.math.Vec3d.ofCenter(context.getAbsolutePos(p3)), Direction.UP, context.getAbsolutePos(p3), false);
        ItemPlacementContext ctx3 = new ItemPlacementContext(player, net.minecraft.util.Hand.MAIN_HAND, new ItemStack(taintedChestItem), hit3);
        ((net.minecraft.item.BlockItem) taintedChestItem).place(ctx3);

        BlockHitResult hit4 = new BlockHitResult(net.minecraft.util.math.Vec3d.ofCenter(context.getAbsolutePos(p4)), Direction.UP, context.getAbsolutePos(p4), false);
        ItemPlacementContext ctx4 = new ItemPlacementContext(player, net.minecraft.util.Hand.MAIN_HAND, new ItemStack(moldyChestItem), hit4); // Stage 2
        ((net.minecraft.item.BlockItem) moldyChestItem).place(ctx4);

        BlockState s3 = context.getBlockState(p3);
        BlockState s4 = context.getBlockState(p4);
        context.assertTrue(s3.get(Properties.CHEST_TYPE) == ChestType.SINGLE, "Stage 1 chest must NOT connect with Stage 2 chest");
        context.assertTrue(s4.get(Properties.CHEST_TYPE) == ChestType.SINGLE, "Stage 2 chest must NOT connect with Stage 1 chest");

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testTrappedChestRedstonePower(TestContext context) {
        BlockPos pos = new BlockPos(1, 1, 1);
        BlockState state = ModBlocks.MOLDY_TRAPPED_CHEST.getDefaultState().with(MoldyBlock.STAGE, 0);
        context.setBlockState(pos, state);

        MoldyTrappedChestBlockEntity be = (MoldyTrappedChestBlockEntity) context.getBlockEntity(pos);
        if (be == null) {
            context.throwPositionedException("MoldyTrappedChestBlockEntity missing!", pos);
        }

        // Test power when closed
        int powerClosed = context.getBlockState(pos).getWeakRedstonePower(context.getWorld(), context.getAbsolutePos(pos), Direction.NORTH);
        context.assertTrue(powerClosed == 0, "Trapped chest should emit 0 power when closed, got " + powerClosed);

        // Open chest with mock player
        PlayerEntity player = context.createMockPlayer(net.minecraft.world.GameMode.SURVIVAL);
        be.onOpen(player);

        int powerOpen = context.getBlockState(pos).getWeakRedstonePower(context.getWorld(), context.getAbsolutePos(pos), Direction.NORTH);
        context.assertTrue(powerOpen > 0, "Trapped chest should emit power > 0 when opened, got " + powerOpen);

        be.onClose(player);
        int powerClosedAgain = context.getBlockState(pos).getWeakRedstonePower(context.getWorld(), context.getAbsolutePos(pos), Direction.NORTH);
        context.assertTrue(powerClosedAgain == 0, "Trapped chest should emit 0 power after closing, got " + powerClosedAgain);

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testTrappedChestOnUseRedstonePower(TestContext context) {
        BlockPos pos = new BlockPos(1, 1, 1);
        BlockState state = ModBlocks.MOLDY_TRAPPED_CHEST.getDefaultState().with(MoldyBlock.STAGE, 0);
        context.setBlockState(pos, state);

        BlockPos dustPos = new BlockPos(1, 1, 2);
        context.setBlockState(dustPos, Blocks.REDSTONE_WIRE.getDefaultState());

        PlayerEntity player = context.createMockPlayer(net.minecraft.world.GameMode.SURVIVAL);
        context.useBlock(pos, player);

        int wirePower = context.getBlockState(dustPos).get(net.minecraft.state.property.Properties.POWER);
        context.assertTrue(wirePower > 0, "Redstone wire should be powered when opened via useBlock, got " + wirePower);

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testTrappedChestLampAndRepeater(TestContext context) {
        BlockPos pos = new BlockPos(2, 1, 2);
        BlockState state = ModBlocks.MOLDY_TRAPPED_CHEST.getDefaultState().with(MoldyBlock.STAGE, 0);
        context.setBlockState(pos, state);

        BlockPos lampPos = new BlockPos(2, 1, 3);
        context.setBlockState(lampPos, Blocks.REDSTONE_LAMP.getDefaultState());

        BlockPos repeaterPos = new BlockPos(3, 1, 2);
        context.setBlockState(repeaterPos, Blocks.REPEATER.getDefaultState().with(Properties.HORIZONTAL_FACING, Direction.EAST));

        MoldyTrappedChestBlockEntity be = (MoldyTrappedChestBlockEntity) context.getBlockEntity(pos);
        if (be == null) {
            context.throwPositionedException("MoldyTrappedChestBlockEntity missing!", pos);
        }

        PlayerEntity player = context.createMockPlayer(net.minecraft.world.GameMode.SURVIVAL);
        be.onOpen(player);

        boolean lampLit = context.getBlockState(lampPos).get(Properties.LIT);
        context.assertTrue(lampLit, "Adjacent Redstone lamp should be lit when trapped chest is opened!");

        boolean repeaterPowered = context.getBlockState(repeaterPos).get(net.minecraft.state.property.Properties.POWERED);
        context.assertTrue(repeaterPowered, "Adjacent Repeater facing away should be powered when trapped chest is opened!");

        be.onClose(player);
        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testWaxedTrappedChestRedstonePower(TestContext context) {
        BlockPos pos = new BlockPos(1, 1, 1);
        BlockState state = ModBlocks.WAXED_TRAPPED_CHEST.getDefaultState().with(MoldyBlock.STAGE, 0).with(MoldyBlock.WAXED, true);
        context.setBlockState(pos, state);

        BlockPos lampPos = new BlockPos(1, 1, 2);
        context.setBlockState(lampPos, Blocks.REDSTONE_LAMP.getDefaultState());

        MoldyTrappedChestBlockEntity be = (MoldyTrappedChestBlockEntity) context.getBlockEntity(pos);
        if (be == null) {
            context.throwPositionedException("MoldyTrappedChestBlockEntity missing on waxed trapped chest!", pos);
        }

        PlayerEntity player = context.createMockPlayer(net.minecraft.world.GameMode.SURVIVAL);
        be.onOpen(player);

        boolean lampLit = context.getBlockState(lampPos).get(Properties.LIT);
        context.assertTrue(lampLit, "Redstone lamp should be lit when waxed trapped chest is opened!");

        be.onClose(player);
        context.complete();
    }

    // =========================================================================
    // === 12. ALL WORKSTATIONS & CONTAINERS MOLD STAGE HOLDER SYNC          ===
    // =========================================================================

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testAllWorkstationsMoldStageHolder(TestContext context) {
        PlayerEntity player = context.createMockPlayer(GameMode.SURVIVAL);

        // 1. Crafting Table (Stage 2)
        BlockPos craftPos = new BlockPos(1, 1, 1);
        BlockState craftState = ModBlocks.MOLDY_CRAFTING_TABLE.getDefaultState().with(MoldyBlock.STAGE, 2);
        context.setBlockState(craftPos, craftState);
        NamedScreenHandlerFactory craftFactory = craftState.createScreenHandlerFactory(context.getWorld(), context.getAbsolutePos(craftPos));
        context.assertFalse(craftFactory == null, "Crafting table screen factory is null");
        ScreenHandler craftMenu = craftFactory.createMenu(1, player.getInventory(), player);
        context.assertTrue(craftMenu instanceof MoldStageHolder holder && holder.spores_shadows$getMoldStage() == 2,
                "CraftingScreenHandler did not sync mold stage 2");

        // 2. Loom (Stage 1)
        BlockPos loomPos = new BlockPos(2, 1, 1);
        BlockState loomState = ModBlocks.MOLDY_LOOM.getDefaultState().with(MoldyBlock.STAGE, 1);
        context.setBlockState(loomPos, loomState);
        NamedScreenHandlerFactory loomFactory = loomState.createScreenHandlerFactory(context.getWorld(), context.getAbsolutePos(loomPos));
        context.assertFalse(loomFactory == null, "Loom screen factory is null");
        ScreenHandler loomMenu = loomFactory.createMenu(2, player.getInventory(), player);
        context.assertTrue(loomMenu instanceof MoldStageHolder holder && holder.spores_shadows$getMoldStage() == 1,
                "LoomScreenHandler did not sync mold stage 1");

        // 3. Cartography Table (Stage 3)
        BlockPos cartPos = new BlockPos(3, 1, 1);
        BlockState cartState = ModBlocks.MOLDY_CARTOGRAPHY_TABLE.getDefaultState().with(MoldyBlock.STAGE, 3);
        context.setBlockState(cartPos, cartState);
        NamedScreenHandlerFactory cartFactory = cartState.createScreenHandlerFactory(context.getWorld(), context.getAbsolutePos(cartPos));
        context.assertFalse(cartFactory == null, "Cartography table screen factory is null");
        ScreenHandler cartMenu = cartFactory.createMenu(3, player.getInventory(), player);
        context.assertTrue(cartMenu instanceof MoldStageHolder holder && holder.spores_shadows$getMoldStage() == 3,
                "CartographyTableScreenHandler did not sync mold stage 3");

        // 4. Barrel (Stage 2)
        BlockPos barrelPos = new BlockPos(1, 1, 2);
        BlockState barrelState = ModBlocks.MOLDY_BARREL.getDefaultState().with(MoldyBlock.STAGE, 2);
        context.setBlockState(barrelPos, barrelState);
        context.useBlock(barrelPos, player);
        ScreenHandler barrelMenu = player.currentScreenHandler;
        context.assertTrue(barrelMenu instanceof GenericContainerScreenHandler && barrelMenu instanceof MoldStageHolder holder && holder.spores_shadows$getMoldStage() == 2,
                "Barrel screen did not sync mold stage 2");
        if (player instanceof net.minecraft.server.network.ServerPlayerEntity sp) {
            sp.closeHandledScreen();
        }

        // 5. Single Chest (Stage 1)
        BlockPos chestPos = new BlockPos(2, 1, 2);
        BlockState chestState = ModBlocks.MOLDY_CHEST.getDefaultState().with(MoldyBlock.STAGE, 1).with(ChestBlock.CHEST_TYPE, ChestType.SINGLE);
        context.setBlockState(chestPos, chestState);
        NamedScreenHandlerFactory chestFactory = chestState.createScreenHandlerFactory(context.getWorld(), context.getAbsolutePos(chestPos));
        context.assertFalse(chestFactory == null, "Single chest factory is null");
        ScreenHandler chestMenu = chestFactory.createMenu(4, player.getInventory(), player);
        context.assertTrue(chestMenu instanceof GenericContainerScreenHandler singleHandler && singleHandler.getRows() == 3 && chestMenu instanceof MoldStageHolder holder && holder.spores_shadows$getMoldStage() == 1,
                "Single chest screen (9x3) did not sync mold stage 1");

        // 6. Double Chest (Stage 2)
        BlockPos p1 = new BlockPos(1, 1, 3);
        BlockPos p2 = new BlockPos(2, 1, 3);
        Item moldyChestItem = Registries.ITEM.get(SporesShadows.id("moldy_chest"));
        player.setYaw(0.0f); // Looking SOUTH
        BlockHitResult hit1 = new BlockHitResult(Vec3d.ofCenter(context.getAbsolutePos(p1)), Direction.UP, context.getAbsolutePos(p1), false);
        ((net.minecraft.item.BlockItem) moldyChestItem).place(new ItemPlacementContext(player, Hand.MAIN_HAND, new ItemStack(moldyChestItem), hit1));
        BlockHitResult hit2 = new BlockHitResult(Vec3d.ofCenter(context.getAbsolutePos(p2)), Direction.UP, context.getAbsolutePos(p2), false);
        ((net.minecraft.item.BlockItem) moldyChestItem).place(new ItemPlacementContext(player, Hand.MAIN_HAND, new ItemStack(moldyChestItem), hit2));
        BlockState s1 = context.getBlockState(p1).with(MoldyBlock.STAGE, 2);
        BlockState s2 = context.getBlockState(p2).with(MoldyBlock.STAGE, 2);
        context.setBlockState(p1, s1);
        context.setBlockState(p2, s2);
        NamedScreenHandlerFactory doubleFactory = s1.createScreenHandlerFactory(context.getWorld(), context.getAbsolutePos(p1));
        context.assertFalse(doubleFactory == null, "Double chest factory is null");
        ScreenHandler doubleMenu = doubleFactory.createMenu(5, player.getInventory(), player);
        context.assertTrue(doubleMenu instanceof GenericContainerScreenHandler doubleHandler && doubleHandler.getRows() == 6 && doubleMenu instanceof MoldStageHolder holder && holder.spores_shadows$getMoldStage() == 2,
                "Double chest screen (9x6) did not sync mold stage 2");

        // 7. Lectern (Stage 2)
        BlockPos lecternPos = new BlockPos(3, 1, 2);
        BlockState lecternState = ModBlocks.MOLDY_LECTERN.getDefaultState().with(MoldyBlock.STAGE, 2).with(MoldyLecternBlock.HAS_BOOK, true);
        context.setBlockState(lecternPos, lecternState);
        MoldyLecternBlockEntity lecternBe = (MoldyLecternBlockEntity) context.getBlockEntity(lecternPos);
        context.assertFalse(lecternBe == null, "Lectern BlockEntity is null");
        ScreenHandler lecternMenu = lecternBe.createMenu(6, player.getInventory(), player);
        context.assertTrue(lecternMenu instanceof MoldStageHolder holder && holder.spores_shadows$getMoldStage() == 2,
                "Lectern screen did not sync mold stage 2");

        context.complete();
    }

    // =========================================================================
    // === 13. SNEAKING AXE SCRAPING & HONEYCOMB WAXING INTERACTIONS         ===
    // =========================================================================

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testAxeScrapingAndWaxingInteractions(TestContext context) {
        BlockPos pos = new BlockPos(1, 1, 1);
        PlayerEntity player = context.createMockPlayer(GameMode.SURVIVAL);
        BlockHitResult hit = new BlockHitResult(Vec3d.ofCenter(context.getAbsolutePos(pos)), Direction.UP, context.getAbsolutePos(pos), false);

        // 1. Not sneaking -> UseBlockCallback returns PASS, state unchanged
        BlockState stage2Moldy = ModBlocks.MOLDY_BARREL.getDefaultState().with(MoldyBlock.STAGE, 2).with(MoldyBlock.WAXED, false);
        context.setBlockState(pos, stage2Moldy);
        player.setSneaking(false);
        player.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.HONEYCOMB, 2));
        ActionResult passResult = UseBlockCallback.EVENT.invoker().interact(player, context.getWorld(), Hand.MAIN_HAND, hit);
        context.assertTrue(passResult == ActionResult.PASS, "Expected PASS when player is not sneaking");
        context.assertTrue(context.getBlockState(pos).getBlock() == ModBlocks.MOLDY_BARREL, "Block state should not change when not sneaking");

        // 2. Sneaking + Honeycomb -> Converts MOLDY_TO_WAXED, sets WAXED = true, consumes 1 honeycomb
        player.setSneaking(true);
        ActionResult waxResult = UseBlockCallback.EVENT.invoker().interact(player, context.getWorld(), Hand.MAIN_HAND, hit);
        context.assertTrue(waxResult == ActionResult.SUCCESS, "Expected SUCCESS when waxing");
        BlockState waxedState = context.getBlockState(pos);
        context.assertTrue(waxedState.getBlock() == ModBlocks.WAXED_BARREL, "Expected WAXED_BARREL block");
        context.assertTrue(waxedState.get(MoldyBlock.WAXED), "Expected WAXED = true");
        context.assertTrue(waxedState.get(MoldyBlock.STAGE) == 2, "Expected stage 2 preserved upon waxing");
        context.assertTrue(player.getStackInHand(Hand.MAIN_HAND).getCount() == 1, "Honeycomb count should decrement by 1 in survival");

        // 3. Sneaking + Axe on Waxed Block -> Strips wax, converts back to MOLDY_BARREL with WAXED = false, damages axe
        ItemStack axeStack = new ItemStack(Items.IRON_AXE);
        player.setStackInHand(Hand.MAIN_HAND, axeStack);
        ActionResult unwaxResult = UseBlockCallback.EVENT.invoker().interact(player, context.getWorld(), Hand.MAIN_HAND, hit);
        context.assertTrue(unwaxResult == ActionResult.SUCCESS, "Expected SUCCESS when stripping wax");
        BlockState unwaxedState = context.getBlockState(pos);
        context.assertTrue(unwaxedState.getBlock() == ModBlocks.MOLDY_BARREL, "Expected MOLDY_BARREL block after scraping wax");
        context.assertFalse(unwaxedState.get(MoldyBlock.WAXED), "Expected WAXED = false");
        context.assertTrue(unwaxedState.get(MoldyBlock.STAGE) == 2, "Expected stage 2 preserved after stripping wax");
        context.assertTrue(player.getStackInHand(Hand.MAIN_HAND).getDamage() > 0, "Axe should take durability damage from scraping wax");

        // 4. Sneaking + Axe on Moldy Stage 2 -> Decrements stage to 1, damages axe further
        int damageBeforeMoldScrape = player.getStackInHand(Hand.MAIN_HAND).getDamage();
        ActionResult scrapeResult = UseBlockCallback.EVENT.invoker().interact(player, context.getWorld(), Hand.MAIN_HAND, hit);
        context.assertTrue(scrapeResult == ActionResult.SUCCESS, "Expected SUCCESS when scraping mold");
        BlockState scrapedState = context.getBlockState(pos);
        context.assertTrue(scrapedState.get(MoldyBlock.STAGE) == 1, "Expected stage decremented from 2 to 1");
        context.assertTrue(player.getStackInHand(Hand.MAIN_HAND).getDamage() > damageBeforeMoldScrape, "Axe should take durability damage from scraping mold");

        // 5. Sneaking + Axe on Moldy Stage 1 -> Decrements stage to 0
        int damageBeforeScrape0 = player.getStackInHand(Hand.MAIN_HAND).getDamage();
        ActionResult scrape0Result = UseBlockCallback.EVENT.invoker().interact(player, context.getWorld(), Hand.MAIN_HAND, hit);
        context.assertTrue(scrape0Result == ActionResult.SUCCESS, "Expected SUCCESS when scraping stage 1");
        BlockState cleanState = context.getBlockState(pos);
        context.assertTrue(cleanState.get(MoldyBlock.STAGE) == 0, "Expected stage decremented from 1 to 0");
        context.assertTrue(player.getStackInHand(Hand.MAIN_HAND).getDamage() > damageBeforeScrape0, "Axe should take durability damage from scraping stage 1");

        // 6. Sneaking + Axe on Incurable Stage 3 -> Consumed (SUCCESS), stage remains 3, axe is NOT damaged
        context.setBlockState(pos, ModBlocks.MOLDY_BARREL.getDefaultState().with(MoldyBlock.STAGE, 3).with(MoldyBlock.WAXED, false));
        int axeDamageBeforeStage3 = player.getStackInHand(Hand.MAIN_HAND).getDamage();
        ActionResult stage3Result = UseBlockCallback.EVENT.invoker().interact(player, context.getWorld(), Hand.MAIN_HAND, hit);
        context.assertTrue(stage3Result == ActionResult.SUCCESS, "Expected SUCCESS on stage 3 to consume interaction");
        context.assertTrue(context.getBlockState(pos).get(MoldyBlock.STAGE) == 3, "Stage 3 must be incurable and remain 3");
        context.assertTrue(player.getStackInHand(Hand.MAIN_HAND).getDamage() == axeDamageBeforeStage3, "Axe should not take damage on incurable stage 3");

        context.complete();
    }
}

