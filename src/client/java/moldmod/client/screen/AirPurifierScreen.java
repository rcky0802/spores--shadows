package moldmod.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import moldmod.SporesShadows;
import moldmod.block.purifier.PurifierRedstoneMode;
import moldmod.block.purifier.PurifierStatus;
import moldmod.network.AirPurifierRedstonePayload;
import moldmod.screen.AirPurifierScreenHandler;
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

public class AirPurifierScreen extends HandledScreen<AirPurifierScreenHandler> {

    private static final Identifier TEXTURE = SporesShadows.id("textures/gui/container/air_purifier_gui.png");

    public AirPurifierScreen(AirPurifierScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 176;
        this.backgroundHeight = 166;
        this.playerInventoryTitleY = this.backgroundHeight - 94;
    }

    @Override
    protected void init() {
        super.init();
        this.titleX = (this.backgroundWidth - this.textRenderer.getWidth(this.title)) / 2;

        // Pulsante Redstone in alto a destra (18x18, x: 152, y: 5)
        int btnX = this.x + 152;
        int btnY = this.y + 5;
        this.addDrawableChild(new RedstoneModeButton(btnX, btnY, 18, 18));
    }

    private class RedstoneModeButton extends ButtonWidget {
        public RedstoneModeButton(int x, int y, int width, int height) {
            super(x, y, width, height, Text.empty(), button -> {
                ClientPlayNetworking.send(new AirPurifierRedstonePayload(BlockPos.ORIGIN));
            }, DEFAULT_NARRATION_SUPPLIER);
        }

        @Override
        protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
            PurifierRedstoneMode mode = handler.getRedstoneMode();
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

        // 1. Barra Energia / TR Energy (Sinistra, x: 26, y: 20, h: 52, w: 16)
        int energyHeight = this.handler.getEnergyProgress(52);
        if (energyHeight > 0) {
            context.drawTexture(TEXTURE, x + 26, y + 20 + (52 - energyHeight), 174, 166 + (52 - energyHeight), 16, energyHeight);
        }

        // 2. Barra Integrità / Usura Filtro Antispore (Destra, x: 134, y: 20, h: 52, w: 16)
        int filterHeight = this.handler.getFilterProgress(52);
        if (filterHeight > 0) {
            context.drawTexture(TEXTURE, x + 134, y + 20 + (52 - filterHeight), 190, 166 + (52 - filterHeight), 16, filterHeight);
        }

        // 3. Testo Reminder Stato Macchina (Centrato sotto gli slot, y: 60)
        PurifierStatus status = this.handler.getMachineStatus();
        Text statusText = Text.translatable("gui.spores--shadows.air_purifier.status." + status.asString());
        int textColor = switch (status) {
            case RUNNING -> 0x2E7D32;         // Verde (Attivo)
            case FILTER_DEPLETED -> 0xFB8C00; // Arancione (Filtro Esaurito)
            case OFF -> 0xC62828;             // Rosso (Spento)
        };
        int textX = x + (this.backgroundWidth - this.textRenderer.getWidth(statusText)) / 2;
        context.drawText(this.textRenderer, statusText, textX, y + 60, textColor, false);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);

        // Priorità tooltip per oggetti afferrati o slot evidenziati
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

        // Tooltip barra energia (Sinistra, x: 25..42, y: 19..73)
        if (relX >= 25 && relX <= 42 && relY >= 19 && relY <= 73) {
            List<Text> lines = new ArrayList<>();
            lines.add(Text.translatable("tooltip.spores--shadows.air_purifier.energy", this.handler.getEnergy(), this.handler.getEnergyCapacity()));
            lines.add(Text.translatable("tooltip.spores--shadows.air_purifier.energy_usage", 10, 200).formatted(Formatting.GRAY));
            context.drawTooltip(this.textRenderer, lines, mouseX, mouseY);
            return;
        }

        // Tooltip barra integrità filtro (Destra, x: 133..150, y: 19..73)
        if (relX >= 133 && relX <= 150 && relY >= 19 && relY <= 73) {
            List<Text> lines = new ArrayList<>();
            int maxWear = this.handler.getFilterDurabilityTicks();
            int wear = this.handler.getFilterWearTicks();
            int pct = (maxWear > 0) ? (int) Math.round(((double) wear / maxWear) * 100.0) : 0;

            lines.add(Text.translatable("tooltip.spores--shadows.air_purifier.filter_integrity", pct));
            lines.add(Text.translatable("tooltip.spores--shadows.air_purifier.filter_backup", this.handler.getFilterCount()).formatted(Formatting.GRAY));
            context.drawTooltip(this.textRenderer, lines, mouseX, mouseY);
            return;
        }

        // Tooltip pulsante Redstone (In alto a destra, x: 152..170, y: 5..23)
        if (relX >= 152 && relX <= 170 && relY >= 5 && relY <= 23) {
            List<Text> lines = new ArrayList<>();
            PurifierRedstoneMode mode = this.handler.getRedstoneMode();
            lines.add(Text.translatable("tooltip.spores--shadows.air_purifier.redstone_mode", Text.translatable("tooltip.spores--shadows.air_purifier.redstone." + mode.asString())));
            lines.add(Text.translatable("tooltip.spores--shadows.air_purifier.redstone." + mode.asString() + ".desc"));
            context.drawTooltip(this.textRenderer, lines, mouseX, mouseY);
            return;
        }

        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }
}
