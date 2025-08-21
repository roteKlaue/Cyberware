package flaxbeard.cyberware.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import flaxbeard.cyberware.OverclockedOrgans;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;

public class ComponentBoxScreen extends ContainerScreen<ComponentBoxContainer> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(OverclockedOrgans.MOD_ID, "textures/gui/component_box.png");
    private final int inventoryRows;

    public ComponentBoxScreen(ComponentBoxContainer container, PlayerInventory inv, ITextComponent title) {
        super(container, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 140;
        this.inventoryRows = container.getRowNumber();
    }

    @Override
    protected void renderBg(@Nonnull MatrixStack matrixStack, float p_230450_2_, int p_230450_3_, int p_230450_4_) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        this.minecraft.getTextureManager().bind(TEXTURE);

        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;

        this.blit(matrixStack, i, j, 0, 0, this.imageWidth, this.inventoryRows * 18 + 17);
        this.blit(matrixStack, i, j + this.inventoryRows * 18 + 17, 0, 126, this.imageWidth, 96);
    }

    @Override
    protected void renderLabels(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY) {
        this.font.draw(matrixStack, this.title.getString(), 8, 6, 4210752);
        this.font.draw(matrixStack, this.inventory.getDisplayName().getString(),
                8, this.imageHeight - 84, 4210752);
    }
}
