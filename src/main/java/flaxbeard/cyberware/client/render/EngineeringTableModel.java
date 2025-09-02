package flaxbeard.cyberware.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class EngineeringTableModel extends Model {
    private final ModelRenderer head;
    private final ModelRenderer bar;

    public EngineeringTableModel() {
        super(RenderType::entityCutout);
        texWidth = 24;
        texHeight = 17;

        head = new ModelRenderer(this, 0, 0);
        head.addBox(-3F, -2F, -3F, 6, 2, 6, 0.0F);
        head.setPos(0.0F, 0.0F, 0.0F);

        bar = new ModelRenderer(this, 0, 8);
        bar.addBox(-1F, 0F, -1F, 2, 7, 2, 0.0F);
        bar.setPos(0.0F, 0.0F, 0.0F);

        head.addChild(bar);
    }

    @Override
    public void renderToBuffer(@Nonnull MatrixStack matrixStack, @Nonnull IVertexBuilder buffer,
                               int packedLight, int packedOverlay,
                               float red, float green, float blue, float alpha) {
        head.render(matrixStack, buffer, packedLight, packedOverlay);
    }
}
