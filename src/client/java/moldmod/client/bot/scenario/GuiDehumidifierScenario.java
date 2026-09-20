package moldmod.client.bot.scenario;

import moldmod.block.machine.dehumidifier.DehumidifierStatus;
import moldmod.client.screen.DehumidifierScreen;
import moldmod.screen.DehumidifierScreenHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

public class GuiDehumidifierScenario implements BotScenario {
    @Override
    public String getName() {
        return "GUI: Dehumidifier Screen";
    }

    @Override
    public String getScreenshotFilename() {
        return "gui_dehumidifier.png";
    }

    @Override
    public int getWaitTicks() {
        return 30;
    }

    @Override
    public void prepare(ServerWorld world, ServerPlayerEntity player, MinecraftClient client) {
        player.getInventory().clear();
        client.execute(() -> {
            client.player.getInventory().clear();

            ArrayPropertyDelegate props = new ArrayPropertyDelegate(8);
            props.set(0, 16000); // 16,000 FE Energy
            props.set(1, 32000); // 32,000 FE Max Capacity
            props.set(2, 2400);  // 2,400 mB Water in Tank
            props.set(3, 4000);  // 4,000 mB Max Tank
            props.set(4, 0);     // Redstone: Ignored
            props.set(5, DehumidifierStatus.RUNNING.ordinal()); // Status: Running
            props.set(6, 20);    // Energy cost per tick: 20 FE/t
            props.set(7, 0);     // Mode: Dehumidify

            SimpleInventory inv = new SimpleInventory(1);
            inv.setStack(0, new ItemStack(Items.COAL, 16));

            DehumidifierScreenHandler handler = new DehumidifierScreenHandler(1, client.player.getInventory(), inv, props);
            client.setScreen(new DehumidifierScreen(handler, client.player.getInventory(), Text.translatable("container.spores--shadows.dehumidifier")));
        });
    }

    @Override
    public void cleanup(ServerWorld world, ServerPlayerEntity player, MinecraftClient client) {
        client.execute(() -> client.setScreen(null));
    }
}
