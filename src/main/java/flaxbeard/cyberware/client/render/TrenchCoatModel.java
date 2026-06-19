package flaxbeard.cyberware.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;

import javax.annotation.Nonnull;

public class TrenchCoatModel extends BipedModel<LivingEntity> {
    private final ModelRenderer coatBottom;

    public TrenchCoatModel(float modelSize) {
        super(modelSize);

        this.texWidth = 64;
        this.texHeight = 32;

        coatBottom = new ModelRenderer(this, 16, 0);
        coatBottom.addBox(
                -4.0F,
                0.0F,
                -1.7F,
                8.0F,
                12.0F,
                4.0F,
                modelSize
        );

        coatBottom.setPos(0.0F, 12.0F, 0.0F);

        body.addChild(coatBottom);
    }

    @Override
    public void setupAnim(
            @Nonnull LivingEntity entity,
            float limbSwing,
            float limbSwingAmount,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
        super.setupAnim(
                entity,
                limbSwing,
                limbSwingAmount,
                ageInTicks,
                netHeadYaw,
                headPitch
        );

        coatBottom.xRot = Math.max(leftLeg.xRot, rightLeg.xRot) + 0.055F;
    }

    @Override
    public void renderToBuffer(
            @Nonnull MatrixStack matrixStack,
            @Nonnull IVertexBuilder vertexBuilder,
            int packedLight,
            int packedOverlay,
            float red,
            float green,
            float blue,
            float alpha
    ) {
        super.renderToBuffer(
                matrixStack,
                vertexBuilder,
                packedLight,
                packedOverlay,
                red,
                green,
                blue,
                alpha
        );
    }
}