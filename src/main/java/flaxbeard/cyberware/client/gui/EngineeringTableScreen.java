package flaxbeard.cyberware.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import flaxbeard.cyberware.OverclockedOrgans;
import flaxbeard.cyberware.client.gui.widget.EngineeringImageButton;
import flaxbeard.cyberware.common.CyberwareConfig;
import flaxbeard.cyberware.common.block.entities.BlueprintArchiveBlockEntity;
import flaxbeard.cyberware.common.block.entities.EngineeringTableBlockEntity;
import flaxbeard.cyberware.common.network.CyberwarePackets;
import flaxbeard.cyberware.common.network.EngineeringDestroyPacketHandler;
import flaxbeard.cyberware.common.network.EngineeringSwitchArchivePacket;

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
    private static final ResourceLocation ADDITIONAL_TEXTURE = new ResourceLocation(OverclockedOrgans.MOD_ID, "textures/gui/component_box.png");
    private static final ResourceLocation ENGINEERING_GUI_TEXTURES = new ResourceLocation(OverclockedOrgans.MOD_ID, "textures/gui/engineering.png");
    private final EngineeringTableBlockEntity entity;
    private EngineeringImageButton destroyButton;
    private EngineeringImageButton nextCompButton;
    private EngineeringImageButton prevCompButton;
    private EngineeringImageButton nextBlueButton;
    private EngineeringImageButton prevBlueButton;
    private boolean hasArchive;
    private boolean hasComponentBoxes;

    public EngineeringTableScreen(EngineeringTableContainer container, PlayerInventory inventory, ITextComponent name) {
        super(container, inventory, name);
        entity = container.getBlockEntity();
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
                ENGINEERING_GUI_TEXTURES,
                256, 256,
                this::onClick
        );

        final int guiLeft = this.leftPos;
        final int guiTop = this.topPos;

        nextCompButton = new EngineeringImageButton(
                guiLeft - 21, guiTop + 131,
                23, 13,
                21, 166,
                44, 166,
                ENGINEERING_GUI_TEXTURES,
                256, 256,
                this::onClick
        );

        prevCompButton = new EngineeringImageButton(
                guiLeft - 57, guiTop + 131,
                23, 13,
                21, 179,
                44, 179,
                ENGINEERING_GUI_TEXTURES,
                256, 256,
                this::onClick
        );

        nextBlueButton = new EngineeringImageButton(
                guiLeft + 216, guiTop + 131,
                23, 13,
                21, 166,
                44, 166,
                ENGINEERING_GUI_TEXTURES,
                256, 256,
                this::onClick
        );

        prevBlueButton = new EngineeringImageButton(
                guiLeft + 180, guiTop + 131,
                23, 13,
                21, 179,
                44, 179,
                ENGINEERING_GUI_TEXTURES,
                256, 256,
                this::onClick
        );

        this.addButton(destroyButton);
        this.addButton(nextCompButton);
        this.addButton(prevCompButton);
        this.addButton(nextBlueButton);
        this.addButton(prevBlueButton);

        hasArchive = this.menu != null && this.menu.archive != null && this.menu.archiveList != null && this.menu.archiveList.size() > 1;
        hasComponentBoxes = this.menu != null && this.menu.componentBoxList != null && this.menu.componentBoxList.size() > 1;

        nextBlueButton.visible = prevBlueButton.visible = hasArchive;
        nextCompButton.visible = prevCompButton.visible = hasComponentBoxes;
    }

    public void onClick(EngineeringImageButton button) {
        if (button == nextCompButton) {
            nextComponentBox();
            return;
        }

        if (button == prevCompButton) {
            prevComponentBox();
            return;
        }

        if (button == nextBlueButton) {
            nextArchive();
            return;
        }

        if (button == prevBlueButton) {
            prevArchive();
            return;
        }

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

    private EngineeringImageButton getHoveredImageButton() {
        if (destroyButton != null && destroyButton.visible && destroyButton.isHovered()) return destroyButton;
        if (nextCompButton != null && nextCompButton.visible && nextCompButton.isHovered()) return nextCompButton;
        if (prevCompButton != null && prevCompButton.visible && prevCompButton.isHovered()) return prevCompButton;
        if (nextBlueButton != null && nextBlueButton.visible && nextBlueButton.isHovered()) return nextBlueButton;
        if (prevBlueButton != null && prevBlueButton.visible && prevBlueButton.isHovered()) return prevBlueButton;
        return null;
    }

    @Override
    protected void renderTooltip(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY) {
        super.renderTooltip(matrixStack, mouseX, mouseY);

        EngineeringImageButton hovered = getHoveredImageButton();
        if (hovered != null && hovered.isHovered()) {
            if (hovered == destroyButton) {
                List<ITextComponent> tooltip = new ArrayList<>();
                tooltip.add(new TranslationTextComponent("gui.overclockedorgans.destroy"));

                if (this.menu.getSlot(1).hasItem()
                        && this.menu.getSlot(1).getItem().getItem() == Items.PAPER) {
                    tooltip.add(new TranslationTextComponent("gui.overclockedorgans.chance_for_blueprint", CyberwareConfig.ENGINEERING_CHANCE.get()));
                }

                this.renderComponentTooltip(matrixStack, tooltip, mouseX, mouseY);
            }
        }

        renderSlotTooltip(0,"gui.overclockedorgans.to_destroy", matrixStack, mouseX, mouseY);
        renderSlotTooltip(1,"gui.overclockedorgans.insert_paper", matrixStack, mouseX, mouseY);
        renderSlotTooltip(8,"gui.overclockedorgans.insert_blueprint", matrixStack, mouseX, mouseY);
    }

    private void renderSlotTooltip(int index, String tooltip, MatrixStack matrixStack, int mouseX, int mouseY) {
        Slot slot8 = this.menu.slots.stream()
                .filter(s -> s.getSlotIndex() == index)
                .findFirst()
                .orElseThrow(IllegalStateException::new);

        if (!slot8.hasItem() && isHovering(slot8.x, slot8.y, 16, 16, mouseX, mouseY)) {
            List<ITextComponent> ttp = new ArrayList<>();
            ttp.add(new TranslationTextComponent(tooltip));
            this.renderComponentTooltip(matrixStack, ttp, mouseX, mouseY);
        }
    }

    @Override
    protected void renderBg(@Nonnull MatrixStack matrixStack, float p_230450_2_, int p_230450_3_, int p_230450_4_) {
        if (this.minecraft == null) return;
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        this.minecraft.getTextureManager().bind(ENGINEERING_GUI_TEXTURES);

        int mainX = (this.width - this.imageWidth) / 2;
        int mainY = (this.height - this.imageHeight) / 2;
        int leftWidth = hasComponentBoxes ? (18 * 3 + 10) : 0;
        int rightWidth = hasArchive ? (18 * 3 + 10) : 0;

        if (hasComponentBoxes) {
            this.minecraft.getTextureManager().bind(ADDITIONAL_TEXTURE);
            blit(matrixStack,
                    mainX - leftWidth,
                    mainY,
                    this.imageWidth,
                    0,
                    leftWidth,
                    this.imageHeight);
        }

        this.minecraft.getTextureManager().bind(ENGINEERING_GUI_TEXTURES);
        blit(matrixStack,
                mainX,
                mainY,
                0,
                0,
                this.imageWidth + rightWidth,
                this.imageHeight);
    }

    private BlueprintArchiveBlockEntity archive() {
        return menu.archive;
    }

    private EngineeringImageButton getButton() {
        return destroyButton;
    }

    private void nextComponentBox() {
        if (minecraft == null || minecraft.player == null) return;
        CyberwarePackets.NETWORK.sendToServer(
                new EngineeringSwitchArchivePacket(entity.getBlockPos(), minecraft.player, true, true)
        );
        menu.nextComponentBox();
    }

    private void prevComponentBox() {
        if (minecraft == null || minecraft.player == null) return;
        CyberwarePackets.NETWORK.sendToServer(new EngineeringSwitchArchivePacket(entity.getBlockPos(), minecraft.player, false, true));

        menu.prevComponentBox();
    }

    private void nextArchive() {
        if (minecraft == null || minecraft.player == null) return;
        CyberwarePackets.NETWORK.sendToServer(new EngineeringSwitchArchivePacket(entity.getBlockPos(), minecraft.player, true, false));

        menu.nextArchive();
        entity.lastPlayerArchive.put(minecraft.player.getStringUUID(), archive().getBlockPos());
    }

    private void prevArchive() {
        if (minecraft == null || minecraft.player == null) return;
        CyberwarePackets.NETWORK.sendToServer(new EngineeringSwitchArchivePacket(entity.getBlockPos(), minecraft.player, false, false));

        menu.prevArchive();
        entity.lastPlayerArchive.put(minecraft.player.getStringUUID(), archive().getBlockPos());
    }
}
