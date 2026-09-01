package moldmod.client.render;

import moldmod.SporesShadows;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class SporeMaskArmorRenderer implements ArmorRenderer {
    private static final Identifier TEXTURE = SporesShadows.id("textures/models/armor/spore_mask_layer_1.png");
    private SporeMaskModel model;

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, ItemStack stack, LivingEntity entity, EquipmentSlot slot, int light, BipedEntityModel<LivingEntity> contextModel) {
        if (slot != EquipmentSlot.HEAD) return;

        if (this.model == null) {
            this.model = new SporeMaskModel(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(SporeMaskModel.LAYER_LOCATION));
        }

        contextModel.copyBipedStateTo(this.model);
        this.model.setVisible(false);
        this.model.head.visible = true;
        this.model.hat.visible = true;

        ArmorRenderer.renderPart(matrices, vertexConsumers, light, stack, this.model, TEXTURE);
    }
}
