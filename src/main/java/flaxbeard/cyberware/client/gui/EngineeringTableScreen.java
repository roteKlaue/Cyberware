package flaxbeard.cyberware.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import flaxbeard.cyberware.OverclockedOrgans;
import flaxbeard.cyberware.client.gui.widget.EngineeringImageButton;
import flaxbeard.cyberware.common.CyberwareConfig;
import flaxbeard.cyberware.common.network.CyberwarePackets;
import flaxbeard.cyberware.common.network.EngineeringDestroyPacketHandler;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class EngineeringTableScreen extends ContainerScreen<EngineeringTableContainer> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(OverclockedOrgans.MOD_ID, "textures/gui/engineering.png");
    private EngineeringImageButton destroyButton;

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

        destroyButton = new EngineeringImageButton(
                x, y, 21, 21,
                leftAdd, topAdd,
                0, 166,
                TEXTURE, 256, 256,
                this::onClick
        );

        this.addButton(destroyButton);
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
    protected void renderTooltip(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY) {
        super.renderTooltip(matrixStack, mouseX, mouseY);

        if (this.getButton() != null && this.getButton().isHovered()) {
            List<ITextComponent> tooltip = new ArrayList<>();
            tooltip.add(new TranslationTextComponent("gui.overclockedorgans.destroy"));

            if (this.menu.getSlot(1).hasItem()
                    && this.menu.getSlot(1).getItem().getItem() == Items.PAPER) {
                tooltip.add(new TranslationTextComponent("gui.overclockedorgans.chance_for_blueprint", CyberwareConfig.ENGINEERING_CHANCE.get()));
            }

            this.renderComponentTooltip(matrixStack, tooltip, mouseX, mouseY);
        }

        Slot slot0 = this.menu.getSlot(0);
        if (!slot0.hasItem() && isHovering(slot0.x, slot0.y, 16, 16, mouseX, mouseY)) {
            List<ITextComponent> tooltip = new ArrayList<>();
            tooltip.add(new TranslationTextComponent("gui.overclockedorgans.to_destroy"));
            this.renderComponentTooltip(matrixStack, tooltip, mouseX, mouseY);
        }

        Slot slot1 = this.menu.getSlot(1);
        if (!slot1.hasItem() && isHovering(slot1.x, slot1.y, 16, 16, mouseX, mouseY)) {
            List<ITextComponent> tooltip = new ArrayList<>();
            tooltip.add(new TranslationTextComponent("gui.overclockedorgans.insert_paper"));
            this.renderComponentTooltip(matrixStack, tooltip, mouseX, mouseY);
        }

        Slot slot8 = this.menu.getSlot(8);
        if (!slot8.hasItem() && isHovering(slot8.x, slot8.y, 16, 16, mouseX, mouseY)) {
            List<ITextComponent> tooltip = new ArrayList<>();
            tooltip.add(new TranslationTextComponent("gui.overclockedorgans.insert_blueprint"));
            this.renderComponentTooltip(matrixStack, tooltip, mouseX, mouseY);
        }
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

    private EngineeringImageButton getButton() {
        return destroyButton;
    }
}
