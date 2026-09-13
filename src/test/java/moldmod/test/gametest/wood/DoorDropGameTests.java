package moldmod.test.gametest.wood;

import moldmod.SporesShadows;
import moldmod.block.MoldyBlock;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.GameMode;

import java.util.List;

public class DoorDropGameTests {

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testUpperHalfNeverDropsLoot(TestContext context) {
        String[] doorIds = {
            "moldy_oak_door", "waxed_oak_door",
            "moldy_bamboo_door", "waxed_bamboo_door",
            "moldy_crimson_door", "waxed_crimson_door"
        };

        PlayerEntity survivalPlayer = context.createMockPlayer(GameMode.SURVIVAL);

        for (String id : doorIds) {
            Block doorBlock = Registries.BLOCK.get(SporesShadows.id(id));
            BlockPos pos = new BlockPos(1, 1, 1);

            // Upper half state
            BlockState upperState = doorBlock.getDefaultState()
                    .with(DoorBlock.HALF, DoubleBlockHalf.UPPER)
                    .with(MoldyBlock.STAGE, 1);

            List<ItemStack> upperDrops = Block.getDroppedStacks(
                    upperState, context.getWorld(), context.getAbsolutePos(pos), null, survivalPlayer, survivalPlayer.getMainHandStack());

            context.assertTrue(upperDrops.isEmpty(),
                    "Door " + id + " UPPER half must never drop loot! Got: " + upperDrops);

            // Lower half state MUST drop loot
            BlockState lowerState = doorBlock.getDefaultState()
                    .with(DoorBlock.HALF, DoubleBlockHalf.LOWER)
                    .with(MoldyBlock.STAGE, 1);

            List<ItemStack> lowerDrops = Block.getDroppedStacks(
                    lowerState, context.getWorld(), context.getAbsolutePos(pos), null, survivalPlayer, survivalPlayer.getMainHandStack());

            context.assertTrue(!lowerDrops.isEmpty(),
                    "Door " + id + " LOWER half must drop loot in survival!");
        }

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testCreativeBreakingDoorDropsNoItems(TestContext context) {
        Block doorBlock = Registries.BLOCK.get(SporesShadows.id("moldy_oak_door"));
        BlockPos lowerPos = new BlockPos(1, 1, 1);
        BlockPos upperPos = new BlockPos(1, 2, 1);

        // Place the 2 halves
        BlockState lowerState = doorBlock.getDefaultState()
                .with(DoorBlock.HALF, DoubleBlockHalf.LOWER)
                .with(MoldyBlock.STAGE, 1);
        BlockState upperState = doorBlock.getDefaultState()
                .with(DoorBlock.HALF, DoubleBlockHalf.UPPER)
                .with(MoldyBlock.STAGE, 1);

        context.setBlockState(lowerPos, lowerState);
        context.setBlockState(upperPos, upperState);

        PlayerEntity creativePlayer = context.createMockPlayer(GameMode.CREATIVE);

        // Break lower half in creative
        context.getWorld().breakBlock(context.getAbsolutePos(lowerPos), false, creativePlayer);

        // Check that no item entities spawned
        Box checkArea = new Box(context.getAbsolutePos(lowerPos)).expand(2.0);
        List<ItemEntity> items = context.getWorld().getEntitiesByType(EntityType.ITEM, checkArea, e -> true);

        context.assertTrue(items.isEmpty(),
                "Breaking door in creative must not drop items! Found: " + items.size());

        context.complete();
    }
}
