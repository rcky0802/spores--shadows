package moldmod.test.gametest.command;

import moldmod.block.ModBlocks;
import moldmod.block.MoldyLogBlock;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public class MoldRiskCommandGameTests {

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testMoldRiskLookingAtBlock(TestContext context) {
        BlockPos targetPos = new BlockPos(2, 2, 3);
        BlockState log = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState().with(MoldyLogBlock.STAGE, 1);
        context.setBlockState(targetPos, log);

        PlayerEntity player = context.createMockPlayer(net.minecraft.world.GameMode.SURVIVAL);
        player.setPos(context.getAbsolutePos(new BlockPos(2, 2, 1)).getX() + 0.5,
                context.getAbsolutePos(new BlockPos(2, 2, 1)).getY(),
                context.getAbsolutePos(new BlockPos(2, 2, 1)).getZ() + 0.5);
        player.setPitch(0.0f);
        player.setYaw(0.0f); // Guardando verso +Z
        player.setSneaking(true); // Sneaking allows raycasting targeted block in moldrisk logic

        ServerCommandSource source = context.getWorld().getServer().getCommandSource().withEntity(player)
                .withPosition(player.getPos());

        context.getWorld().getServer().getCommandManager().executeWithPrefix(source, "moldrisk");

        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testMoldRiskLookingAtAir(TestContext context) {
        PlayerEntity player = context.createMockPlayer(net.minecraft.world.GameMode.SURVIVAL);
        player.setPos(context.getAbsolutePos(new BlockPos(2, 2, 2)).getX() + 0.5,
                context.getAbsolutePos(new BlockPos(2, 2, 2)).getY(),
                context.getAbsolutePos(new BlockPos(2, 2, 2)).getZ() + 0.5);
        player.setPitch(-90.0f); // Guardando verso l'alto nel cielo vuoto

        ServerCommandSource source = context.getWorld().getServer().getCommandSource().withEntity(player)
                .withPosition(player.getPos());
        context.getWorld().getServer().getCommandManager().executeWithPrefix(source, "moldrisk");
        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testMoldRiskCommandNonPlayer(TestContext context) {
        // Server console source (not a player)
        ServerCommandSource source = context.getWorld().getServer().getCommandSource().withWorld(context.getWorld());
        context.getWorld().getServer().getCommandManager().executeWithPrefix(source, "moldrisk");
        context.complete();
    }
}
