package moldmod.client.render;

import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import org.joml.Vector3f;

import java.util.function.Supplier;

public class MoldyBakedModel extends ForwardingBakedModel {
    private Sprite moldSprite;
    private final int stage;

    public MoldyBakedModel(BakedModel baseModel, int stage) {
        this.wrapped = baseModel;
        this.stage = stage;
    }

    private Sprite getMoldSprite() {
        if (this.moldSprite == null) {
            this.moldSprite = net.minecraft.client.MinecraftClient.getInstance()
                .getBakedModelManager()
                .getAtlas(net.minecraft.screen.PlayerScreenHandler.BLOCK_ATLAS_TEXTURE)
                .getSprite(net.minecraft.util.Identifier.of("spores--shadows", "block/mold_stage_" + this.stage));
        }
        return this.moldSprite;
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public void emitBlockQuads(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {
        super.emitBlockQuads(blockView, state, pos, randomSupplier, context);

        Sprite sprite = getMoldSprite();
        if (sprite != null && !sprite.getContents().getId().getPath().equals("missingno")) {
            context.pushTransform(quad -> {
                Vector3f normal = quad.faceNormal();
                float offset = 0.002f;
                
                for (int i = 0; i < 4; i++) {
                    float x = quad.x(i) + normal.x * offset;
                    float y = quad.y(i) + normal.y * offset;
                    float z = quad.z(i) + normal.z * offset;
                    quad.pos(i, x, y, z);
                }
                
                quad.spriteBake(sprite, MutableQuadView.BAKE_NORMALIZED);
                return true;
            });

            super.emitBlockQuads(blockView, state, pos, randomSupplier, context);
            context.popTransform();
        }
    }

    @Override
    public void emitItemQuads(net.minecraft.item.ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {
        super.emitItemQuads(stack, randomSupplier, context);

        Sprite sprite = getMoldSprite();
        if (sprite != null && !sprite.getContents().getId().getPath().equals("missingno")) {
            context.pushTransform(quad -> {
                Vector3f normal = quad.faceNormal();
                float offset = 0.002f;
                
                for (int i = 0; i < 4; i++) {
                    float x = quad.x(i) + normal.x * offset;
                    float y = quad.y(i) + normal.y * offset;
                    float z = quad.z(i) + normal.z * offset;
                    quad.pos(i, x, y, z);
                }
                
                quad.spriteBake(sprite, MutableQuadView.BAKE_NORMALIZED);
                return true;
            });

            super.emitItemQuads(stack, randomSupplier, context);
            context.popTransform();
        }
    }
}
