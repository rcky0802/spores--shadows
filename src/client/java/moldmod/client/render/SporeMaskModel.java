package moldmod.client.render;

import moldmod.SporesShadows;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.entity.LivingEntity;

public class SporeMaskModel extends BipedEntityModel<LivingEntity> {
    public static final EntityModelLayer LAYER_LOCATION = new EntityModelLayer(SporesShadows.id("spore_mask"), "main");

    public SporeMaskModel(ModelPart root) {
        super(root);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();

        // 1. Base Head Cowl (HEAD at UV 0,0 with Dilation 0.5F) - wraps the skull and face
        root.addChild(
            EntityModelPartNames.HEAD,
            ModelPartBuilder.create()
                .uv(0, 0).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.5F)),
            ModelTransform.NONE
        );

        // 2. 3D Outer Relief (HAT at UV 32,0 with Dilation 0.85F) - seamless depth for goggles, snout & side valves
        root.addChild(
            EntityModelPartNames.HAT,
            ModelPartBuilder.create()
                .uv(32, 0).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.85F)),
            ModelTransform.NONE
        );

        // Clear remaining body parts
        root.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create(), ModelTransform.NONE);
        root.addChild(EntityModelPartNames.RIGHT_ARM, ModelPartBuilder.create(), ModelTransform.NONE);
        root.addChild(EntityModelPartNames.LEFT_ARM, ModelPartBuilder.create(), ModelTransform.NONE);
        root.addChild(EntityModelPartNames.RIGHT_LEG, ModelPartBuilder.create(), ModelTransform.NONE);
        root.addChild(EntityModelPartNames.LEFT_LEG, ModelPartBuilder.create(), ModelTransform.NONE);

        return TexturedModelData.of(modelData, 64, 32);
    }
}
