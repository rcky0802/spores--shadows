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
import net.minecraft.client.gui.screen.ingame.BookScreen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(BookScreen.class)
public abstract class BookScreenMixin extends Screen {

    @Unique
    private static final Identifier VIGNETTE_TEXTURE = SporesShadows.id("textures/gui/spore_vignette.png");

    protected BookScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void moldmod$renderBookMoldOverlay(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!((Object) this instanceof ScreenHandlerProvider<?> provider)) return;
        if (!(provider.getScreenHandler() instanceof MoldStageHolder holder)) return;

        int stage = holder.spores_shadows$getMoldStage();
        if (stage <= 0) return;

        ModConfig config = null;
        try {
            config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        } catch (Exception ignored) {}

        float intensity = (config != null && config.client != null) ? config.client.gui_mold_overlay_intensity : 1.0f;
        if (intensity <= 0.01f) return;

        Identifier overlayTexture = SporesShadows.id("textures/gui/mold_book_overlay_stage_" + stage + ".png");
        int bookX = (this.width - 192) / 2;
        int bookY = 2;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, intensity);
        context.drawTexture(overlayTexture, bookX, bookY, 0.0f, 0.0f, 192, 192, 192, 192);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();

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
