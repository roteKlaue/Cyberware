package flaxbeard.cyberware.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import flaxbeard.cyberware.OverclockedOrgans;
import flaxbeard.cyberware.client.gui.widget.EngineeringImageButton;
import flaxbeard.cyberware.common.network.CyberwarePackets;
import flaxbeard.cyberware.common.network.EngineeringDestroyPacketHandler;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;

public class EngineeringTableScreen extends ContainerScreen<EngineeringTableContainer> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(OverclockedOrgans.MOD_ID, "textures/gui/engineering.png");

    public EngineeringTableScreen(EngineeringTableContainer container, PlayerInventory inventory, ITextComponent name) {
        super(container, inventory, name);
    }

    @Override
    protected void init() {
        super.init();

        final int topAdd = 34;
        final int leftAdd = 39;

        int x = this.leftPos + leftAdd;
        int y = this.topPos + topAdd;

        this.addButton(new EngineeringImageButton(
                x, y, 21, 21,
                leftAdd, topAdd,
                0, 166,
                TEXTURE, 256, 256,
                this::onClick
        ));
    }

    public void onClick(EngineeringImageButton button) {
        CyberwarePackets.NETWORK.sendToServer(
                new EngineeringDestroyPacketHandler(this.menu.containerId)
        );
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderBg(@Nonnull MatrixStack matrixStack, float p_230450_2_, int p_230450_3_, int p_230450_4_) {
        if (this.minecraft == null) return;
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        this.minecraft.getTextureManager().bind(TEXTURE);

        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;

        blit(matrixStack, i, j, 0, 0, this.imageWidth, 166);
    }
}
