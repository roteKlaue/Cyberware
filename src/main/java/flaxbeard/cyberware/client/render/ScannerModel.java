package flaxbeard.cyberware.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.model.ModelRenderer;

public class ScannerModel {
    private final ModelRenderer bar;
    private final ModelRenderer bar2;
    private final ModelRenderer scanner;
    private final ModelRenderer beam;

    public ScannerModel() {
        int texWidth = 34;
        int texHeight = 10;

        this.bar = new ModelRenderer(texWidth, texHeight, 0, 0);
        this.bar.addBox(-8F, 7F, -7F, 16, 1, 1);

        this.bar2 = new ModelRenderer(texWidth, texHeight, 0, 0);
        this.bar2.addBox(-8F, 5F, -7F, 16, 1, 1);

        this.bar.addChild(bar2); // child offsets remain relative

        this.scanner = new ModelRenderer(texWidth, texHeight, 0, 2);
        this.scanner.addBox(-7F, 2F, -8F, 3, 5, 3);

        this.beam = new ModelRenderer(texWidth, texHeight, 12, 2);
        this.beam.addBox(-6F, -2F, -7F, 1, 4, 1);
    }

    public void renderBar(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay) {
        this.bar.render(matrixStack, buffer, packedLight, packedOverlay);
    }

    public void renderScanner(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay) {
        this.scanner.render(matrixStack, buffer, packedLight, packedOverlay);
    }

    public void renderBeam(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay) {
        this.beam.render(matrixStack, buffer, packedLight, packedOverlay);
    }

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }
}
