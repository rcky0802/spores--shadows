package moldmod.client.bot.scenario;

import moldmod.block.ModBlocks;
import moldmod.block.core.MoldyBlock;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class GuiCraftingTableScenario implements BotScenario {
    @Override
    public String getName() {
        return "GUI: Moldy Crafting Table Screen (Stage 3 Overlay & Vignette)";
    }

    @Override
    public String getScreenshotFilename() {
        return "gui_moldy_crafting_table.png";
    }

    @Override
    public int getWaitTicks() {
        return 30;
    }

    @Override
    public void prepare(ServerWorld world, ServerPlayerEntity player, MinecraftClient client) {
        BlockPos craftPos = new BlockPos(CellarScenario.OX - 1, CellarScenario.BASE_Y, CellarScenario.OZ + 4);
        player.teleport(world, craftPos.getX() + 0.5, CellarScenario.BASE_Y, craftPos.getZ() - 1.2, 0f, 0f);

        BlockState craftState = ModBlocks.MOLDY_CRAFTING_TABLE.getDefaultState().with(MoldyBlock.STAGE, 3);
        world.setBlockState(craftPos, craftState);
        player.openHandledScreen(craftState.createScreenHandlerFactory(world, craftPos));
    }

    @Override
    public void cleanup(ServerWorld world, ServerPlayerEntity player, MinecraftClient client) {
        client.setScreen(null);
        player.closeHandledScreen();
    }
}
