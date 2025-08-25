package flaxbeard.cyberware.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

import javax.annotation.Nonnull;

public class SurgeryChamberModel extends Model {
    public final ModelRenderer left;
    public final ModelRenderer right;

    public SurgeryChamberModel() {
        super(RenderType::entityCutout);
        this.texWidth = 14;
        this.texHeight = 29;

        this.left = new ModelRenderer(this, 0, 0);
        this.left.addBox(0F, -22F, -1F, 6, 28, 1);

        this.right = new ModelRenderer(this, 0, 0);
        this.right.addBox(-6F, -22F, -1F, 6, 28, 1);
    }

    @Override
    public void renderToBuffer(@Nonnull MatrixStack matrixStack, @Nonnull IVertexBuilder buffer,
                               int packedLight, int packedOverlay,
                               float red, float green, float blue, float alpha) {
        this.left.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        this.right.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
