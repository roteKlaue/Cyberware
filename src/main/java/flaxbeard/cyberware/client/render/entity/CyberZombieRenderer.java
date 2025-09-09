package flaxbeard.cyberware.client.render.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import flaxbeard.cyberware.OverclockedOrgans;
import flaxbeard.cyberware.common.entity.CyberZombieEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.AbstractZombieRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.ZombieModel;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class CyberZombieRenderer extends AbstractZombieRenderer<CyberZombieEntity, ZombieModel<CyberZombieEntity>> {
    private static final ResourceLocation ZOMBIE = new ResourceLocation(OverclockedOrgans.MOD_ID + ":textures/entity/cyberzombie.png");
    private static final ResourceLocation HIGHLIGHT = new ResourceLocation(OverclockedOrgans.MOD_ID + ":textures/entity/cyberzombie_highlight.png");

    public CyberZombieRenderer(EntityRendererManager manager) {
        super(manager, new ZombieModel<>(0.0F, false),
                new ZombieModel<>(0.5F, true),
                new ZombieModel<>(1.0F, true));
        this.addLayer(new LayerCyberZombieHighlight(this));
    }

    @Override
    @Nonnull
    public ResourceLocation getTextureLocation(@Nonnull ZombieEntity zombie) {
        return ZOMBIE;
    }

    private static class LayerCyberZombieHighlight extends LayerRenderer<CyberZombieEntity, ZombieModel<CyberZombieEntity>> {

        public LayerCyberZombieHighlight(IEntityRenderer<CyberZombieEntity, ZombieModel<CyberZombieEntity>> renderer) {
            super(renderer);
        }

        @Override
        public void render(@Nonnull MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight,
                           @Nonnull CyberZombieEntity entity, float limbSwing, float limbSwingAmount, float partialTicks,
                           float ageInTicks, float netHeadYaw, float headPitch) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();

            this.getParentModel().renderToBuffer(matrixStack,
                    buffer.getBuffer(RenderType.entityCutoutNoCull(HIGHLIGHT)),
                    0xF000F0, 0xF000F0, 1F, 1F, 1F, 1F);

            RenderSystem.disableBlend();
        }
    }
}
