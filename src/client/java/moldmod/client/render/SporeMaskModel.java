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
        ModelData modelData = BipedEntityModel.getModelData(new Dilation(0.5F), 0.0F);
        ModelPartData root = modelData.getRoot();

        // 3D Gas Mask Head piece with protruding snout filter and cheek canisters
        root.addChild(
            EntityModelPartNames.HEAD,
            ModelPartBuilder.create()
                // 1. Base head cowl & straps (8x8x8)
                .uv(0, 0).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.5F))
                // 2. Goggles 3D frame (pops out over the eyes)
                .uv(0, 16).cuboid(-4.0F, -6.5F, -5.0F, 8.0F, 3.0F, 1.0F, new Dilation(0.1F))
                // 3. Protruding Gas Mask Snout / Filter Canister (Sticks straight out from the mouth like a movie gas mask!)
                .uv(32, 0).cuboid(-2.5F, -3.5F, -7.5F, 5.0F, 4.0F, 3.5F, new Dilation(0.0F))
                // 4. Front Filter Intake Cap / Valve
                .uv(49, 0).cuboid(-1.5F, -2.5F, -8.5F, 3.0F, 3.0F, 1.0F, new Dilation(0.0F))
                // 5. Right Cheek Canister
                .uv(32, 8).cuboid(-5.5F, -3.0F, -3.5F, 1.5F, 3.0F, 3.0F, new Dilation(0.0F))
                // 6. Left Cheek Canister
                .uv(48, 8).cuboid(4.0F, -3.0F, -3.5F, 1.5F, 3.0F, 3.0F, new Dilation(0.0F)),
            ModelTransform.NONE
        );

        return TexturedModelData.of(modelData, 64, 32);
    }
}
