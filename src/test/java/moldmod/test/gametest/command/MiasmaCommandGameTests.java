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

public class MiasmaCommandGameTests {

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testMiasmaCommandOpenAir(TestContext context) {
        PlayerEntity player = context.createMockPlayer(net.minecraft.world.GameMode.SURVIVAL);
        BlockPos center = new BlockPos(2, 2, 2);
        player.setPos(context.getAbsolutePos(center).getX() + 0.5,
                context.getAbsolutePos(center).getY(),
                context.getAbsolutePos(center).getZ() + 0.5);

        ServerCommandSource source = context.getWorld().getServer().getCommandSource().withEntity(player)
                .withPosition(player.getPos());
        context.getWorld().getServer().getCommandManager().executeWithPrefix(source, "miasma");
        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testMiasmaCommandConfinedSpace(TestContext context) {
        BlockPos center = new BlockPos(2, 2, 2);
        // Stanza chiusa in pietra
        for (int x = 1; x <= 3; x++) {
            for (int y = 1; y <= 3; y++) {
                for (int z = 1; z <= 3; z++) {
                    context.setBlockState(new BlockPos(x, y, z), Blocks.STONE.getDefaultState());
                }
            }
        }
        context.setBlockState(center, Blocks.AIR.getDefaultState());

        // Aggiungi tronco marcio
        BlockState rotten = ModBlocks.VANILLA_TO_MOLDY.get(Blocks.OAK_LOG).getDefaultState()
                .with(MoldyLogBlock.STAGE, 3);
        context.setBlockState(new BlockPos(1, 2, 2), rotten);

        PlayerEntity player = context.createMockPlayer(net.minecraft.world.GameMode.SURVIVAL);
        player.setPos(context.getAbsolutePos(center).getX() + 0.5,
                context.getAbsolutePos(center).getY(),
                context.getAbsolutePos(center).getZ() + 0.5);

        ServerCommandSource source = context.getWorld().getServer().getCommandSource().withEntity(player)
                .withPosition(player.getPos());
        context.getWorld().getServer().getCommandManager().executeWithPrefix(source, "miasma");
        context.complete();
    }

    @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
    public void testMiasmaCommandNonPlayer(TestContext context) {
        // Server console source (not a player)
        ServerCommandSource source = context.getWorld().getServer().getCommandSource().withWorld(context.getWorld());
        context.getWorld().getServer().getCommandManager().executeWithPrefix(source, "miasma");
        context.complete();
    }
}
