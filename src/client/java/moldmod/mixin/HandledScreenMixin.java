package moldmod.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import me.shedaniel.autoconfig.AutoConfig;
import moldmod.SporesShadows;
import moldmod.config.ModConfig;
import moldmod.screen.MoldStageHolder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin<T extends ScreenHandler> extends Screen {

    @Shadow protected int x;
    @Shadow protected int y;
    @Shadow protected int backgroundWidth;
    @Shadow protected int backgroundHeight;
    @Shadow @Final protected T handler;

    @Unique
    private static final Identifier VIGNETTE_TEXTURE = SporesShadows.id("textures/gui/spore_vignette.png");

    protected HandledScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void moldmod$renderMoldOverlay(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!(this.handler instanceof MoldStageHolder holder)) return;

        int stage = holder.spores_shadows$getMoldStage();
        if (stage <= 0) return;

        ModConfig config = null;
        try {
            config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        } catch (Exception ignored) {}

        float intensity = (config != null && config.client != null) ? config.client.gui_mold_overlay_intensity : 1.0f;
        if (intensity <= 0.01f) return;

        Identifier overlayTexture = null;
        int texW = 176;
        int texH = 166;

        if (this.handler instanceof CraftingScreenHandler) {
            overlayTexture = SporesShadows.id("textures/gui/mold_crafting_overlay_stage_" + stage + ".png");
            texW = 176;
            texH = 166;
        } else if (this.handler instanceof GenericContainerScreenHandler containerHandler) {
            if (containerHandler.getRows() == 6) {
                overlayTexture = SporesShadows.id("textures/gui/mold_container_9x6_overlay_stage_" + stage + ".png");
                texW = 176;
                texH = 222;
            } else {
                overlayTexture = SporesShadows.id("textures/gui/mold_container_9x3_overlay_stage_" + stage + ".png");
                texW = 176;
                texH = 166;
            }
        } else if (this.handler instanceof net.minecraft.screen.CartographyTableScreenHandler) {
            overlayTexture = SporesShadows.id("textures/gui/mold_cartography_table_overlay_stage_" + stage + ".png");
            texW = 176;
            texH = 166;
        } else if (this.handler instanceof net.minecraft.screen.LoomScreenHandler) {
            overlayTexture = SporesShadows.id("textures/gui/mold_loom_overlay_stage_" + stage + ".png");
            texW = 176;
            texH = 166;
        } else if (this.backgroundWidth == 176 && this.backgroundHeight == 166) {
            overlayTexture = SporesShadows.id("textures/gui/mold_container_9x3_overlay_stage_" + stage + ".png");
            texW = 176;
            texH = 166;
        }

        if (overlayTexture != null) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, intensity);
            context.drawTexture(overlayTexture, this.x, this.y, 0.0f, 0.0f, this.backgroundWidth, this.backgroundHeight, texW, texH);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.disableBlend();
        }

        if (stage >= 2) {
            float vignetteAlpha = (stage == 2 ? 0.35f : 0.70f) * intensity;
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, vignetteAlpha);
            context.drawTexture(VIGNETTE_TEXTURE, 0, 0, 0.0f, 0.0f, this.width, this.height, 256, 256);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.disableBlend();
        }
    }
}
