package moldmod.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import moldmod.SporesShadows;
import moldmod.block.dehumidifier.DehumidifierMode;
import moldmod.block.dehumidifier.DehumidifierRedstoneMode;
import moldmod.block.dehumidifier.DehumidifierStatus;
import moldmod.network.DehumidifierModePayload;
import moldmod.network.DehumidifierRedstonePayload;
import moldmod.screen.DehumidifierScreenHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class DehumidifierScreen extends HandledScreen<DehumidifierScreenHandler> {

    private static final Identifier TEXTURE = SporesShadows.id("textures/gui/container/dehumidifier_gui.png");

    public DehumidifierScreen(DehumidifierScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 176;
        this.backgroundHeight = 166;
        this.playerInventoryTitleY = this.backgroundHeight - 94;
    }

    @Override
    protected void init() {
        super.init();
        this.titleX = (this.backgroundWidth - this.textRenderer.getWidth(this.title)) / 2;

        // Pulsante Modalità Operativa in alto a sinistra (18x18, x: 7, y: 5)
        int modeBtnX = this.x + 7;
        int modeBtnY = this.y + 5;
        this.addDrawableChild(new OperationModeButton(modeBtnX, modeBtnY, 18, 18));

        // Pulsante Redstone in alto a destra (18x18, x: 152, y: 5)
        int btnX = this.x + 152;
        int btnY = this.y + 5;
        this.addDrawableChild(new RedstoneModeButton(btnX, btnY, 18, 18));
    }

    private class OperationModeButton extends ButtonWidget {
        public OperationModeButton(int x, int y, int width, int height) {
            super(x, y, width, height, Text.empty(), button -> {
                ClientPlayNetworking.send(new DehumidifierModePayload(BlockPos.ORIGIN));
            }, DEFAULT_NARRATION_SUPPLIER);
        }

        @Override
        protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
            DehumidifierMode mode = handler.getMode();
            int uOffset = (mode == DehumidifierMode.DEHUMIDIFY) ? 206 : 224;
            int vOffset = 186;
            RenderSystem.setShader(GameRenderer::getPositionTexProgram);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            context.drawTexture(TEXTURE, this.getX(), this.getY(), uOffset, vOffset, 18, 18);
            if (this.isSelected()) {
                context.fill(this.getX(), this.getY(), this.getX() + 18, this.getY() + 18, 0x40FFFFFF);
            }
        }
    }

    private class RedstoneModeButton extends ButtonWidget {
        public RedstoneModeButton(int x, int y, int width, int height) {
            super(x, y, width, height, Text.empty(), button -> {
                ClientPlayNetworking.send(new DehumidifierRedstonePayload(BlockPos.ORIGIN));
            }, DEFAULT_NARRATION_SUPPLIER);
        }

        @Override
        protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
            DehumidifierRedstoneMode mode = handler.getRedstoneMode();
            int uOffset = switch (mode) {
                case IGNORED -> 206;
                case LOW -> 224;
                case HIGH -> 242;
            };
            RenderSystem.setShader(GameRenderer::getPositionTexProgram);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            context.drawTexture(TEXTURE, this.getX(), this.getY(), uOffset, 166, 18, 18);
            if (this.isSelected()) {
                context.fill(this.getX(), this.getY(), this.getX() + 18, this.getY() + 18, 0x40FFFFFF);
            }
        }
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        int x = (this.width - this.backgroundWidth) / 2;
        int y = (this.height - this.backgroundHeight) / 2;

        // Disegna sfondo base (176x166)
        context.drawTexture(TEXTURE, x, y, 0, 0, this.backgroundWidth, this.backgroundHeight);

        // 1. Barra Energia / Corrente (Sinistra, x: 26, y: 20, h: 52, w: 16)
        int energyHeight = this.handler.getEnergyProgress(52);
        if (energyHeight > 0) {
            context.drawTexture(TEXTURE, x + 26, y + 20 + (52 - energyHeight), 174, 166 + (52 - energyHeight), 16, energyHeight);
        }

        // 2. Barra Acqua / Condensa (Destra, x: 134, y: 20, h: 52, w: 16)
        int waterHeight = this.handler.getWaterProgress(52);
        if (waterHeight > 0) {
            context.drawTexture(TEXTURE, x + 134, y + 20 + (52 - waterHeight), 190, 166 + (52 - waterHeight), 16, waterHeight);
        }

        // 3. Scritta Reminder Stato Macchina (Centrata sotto lo slot combustibile)
        var status = this.handler.getMachineStatus();
        var mode = this.handler.getMode();
        Text statusText;
        int textColor;

        if (mode == DehumidifierMode.HUMIDIFY) {
            if (status == DehumidifierStatus.RUNNING) {
                statusText = Text.translatable("gui.spores--shadows.dehumidifier.status.humidifying");
                textColor = 0x00ACC1;
            } else if (this.handler.getWaterMb() <= 0) {
                statusText = Text.translatable("gui.spores--shadows.dehumidifier.status.water_empty");
                textColor = 0xFB8C00;
            } else {
                statusText = Text.translatable("gui.spores--shadows.dehumidifier.status.off");
                textColor = 0xC62828;
            }
        } else {
            statusText = Text.translatable("gui.spores--shadows.dehumidifier.status." + status.asString());
            textColor = switch (status) {
                case RUNNING -> 0x2E7D32; // Verde (attivo)
                case FULL -> 0x1565C0;    // Blu (fermo perché deposito pieno)
                default -> 0xC62828;      // Rosso (spenta)
            };
        }
        int textX = x + (this.backgroundWidth - this.textRenderer.getWidth(statusText)) / 2;
        context.drawText(this.textRenderer, statusText, textX, y + 60, textColor, false);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);

        // Se il cursore è su uno slot con un oggetto o ha un oggetto afferrato, dà priorità assoluta ed evita sovrapposizioni
        if (this.focusedSlot != null && this.focusedSlot.hasStack()) {
            this.drawMouseoverTooltip(context, mouseX, mouseY);
            return;
        }
        if (!this.handler.getCursorStack().isEmpty()) {
            this.drawMouseoverTooltip(context, mouseX, mouseY);
            return;
        }

        int relX = mouseX - this.x;
        int relY = mouseY - this.y;

        // Tooltip pulsante Modalità (In alto a sinistra, x: 7..25, y: 5..23)
        if (relX >= 7 && relX <= 25 && relY >= 5 && relY <= 23) {
            List<Text> lines = new ArrayList<>();
            DehumidifierMode mode = this.handler.getMode();
            lines.add(Text.translatable("tooltip.spores--shadows.dehumidifier.mode", Text.translatable("tooltip.spores--shadows.dehumidifier.mode." + mode.asString())));
            lines.add(Text.translatable("tooltip.spores--shadows.dehumidifier.mode." + mode.asString() + ".desc").formatted(Formatting.GRAY));
            lines.add(Text.translatable("tooltip.spores--shadows.dehumidifier.mode.fluid_info." + mode.asString()).formatted(Formatting.DARK_AQUA));
            context.drawTooltip(this.textRenderer, lines, mouseX, mouseY);
            return;
        }

        // Tooltip barra energia (Sinistra, x: 25..42, y: 19..73)
        if (relX >= 25 && relX <= 42 && relY >= 19 && relY <= 73) {
            List<Text> lines = new ArrayList<>();
            lines.add(Text.translatable("tooltip.spores--shadows.dehumidifier.energy", this.handler.getEnergy(), this.handler.getEnergyCapacity()));
            int cost = this.handler.getEnergyCostPerTick();
            lines.add(Text.translatable("tooltip.spores--shadows.dehumidifier.energy_usage", cost, cost * 20).formatted(Formatting.GRAY));
            context.drawTooltip(this.textRenderer, lines, mouseX, mouseY);
            return;
        }

        // Tooltip barra acqua (Destra, x: 133..150, y: 19..73)
        if (relX >= 133 && relX <= 150 && relY >= 19 && relY <= 73) {
            List<Text> lines = new ArrayList<>();
            lines.add(Text.translatable("tooltip.spores--shadows.dehumidifier.water", this.handler.getWaterMb(), this.handler.getCapacityMb()));
            context.drawTooltip(this.textRenderer, lines, mouseX, mouseY);
            return;
        }

        // Tooltip pulsante Redstone (In alto a destra, x: 152..170, y: 5..23)
        if (relX >= 152 && relX <= 170 && relY >= 5 && relY <= 23) {
            List<Text> lines = new ArrayList<>();
            DehumidifierRedstoneMode mode = this.handler.getRedstoneMode();
            lines.add(Text.translatable("tooltip.spores--shadows.dehumidifier.redstone_mode", Text.translatable("tooltip.spores--shadows.dehumidifier.redstone." + mode.asString())));
            lines.add(Text.translatable("tooltip.spores--shadows.dehumidifier.redstone." + mode.asString() + ".desc"));
            context.drawTooltip(this.textRenderer, lines, mouseX, mouseY);
            return;
        }

        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }
}
