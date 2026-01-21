package flaxbeard.cyberware.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import flaxbeard.cyberware.OverclockedOrgans;

import flaxbeard.cyberware.common.item.BlueprintItem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;

public class BlueprintArchiveScreen extends ContainerScreen<BlueprintArchiveContainer> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(OverclockedOrgans.MOD_ID, "textures/gui/blueprint_archive.png");
    private final int inventoryRows;

    public BlueprintArchiveScreen(BlueprintArchiveContainer container, PlayerInventory inv, ITextComponent title) {
        super(container, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 140;
        this.inventoryRows = container.getRowNumber();
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
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

        blit(matrixStack, i, j, 0, 0, this.imageWidth, this.inventoryRows * 18 + 17);
        blit(matrixStack, i, j + this.inventoryRows * 18 + 17, 0, 126, this.imageWidth, 96);
    }

    @Override
    protected void renderLabels(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY) {
        renderStoredItemsAboveBlueprints(matrixStack);
        font.draw(matrixStack, this.title.getString(), 8, 6, 4210752);
        font.draw(matrixStack, this.inventory.getDisplayName().getString(),
                8, this.imageHeight - 84, 4210752);
    }

    private void renderStoredItemsAboveBlueprints(@Nonnull MatrixStack matrixStack) {
        if (this.minecraft == null || this.minecraft.player == null) return;

        final int ITEM_SIZE = 16;
        final int SLOT_SIZE = 18;
        final int H_CENTER = (SLOT_SIZE - ITEM_SIZE) / 2;
        final int VERTICAL_GAP = 2;

        int containerSlots = this.inventoryRows * 9;
        for (int slotIndex = 0; slotIndex < containerSlots && slotIndex < this.menu.slots.size(); slotIndex++) {
            Slot slot = this.menu.getSlot(slotIndex);
            ItemStack stackInSlot = slot.getItem();

            if (!stackInSlot.isEmpty() && stackInSlot.getItem() instanceof BlueprintItem) {
                ItemStack stored = ((BlueprintItem) stackInSlot.getItem()).getResult(stackInSlot);
                if (!stored.isEmpty()) {
                    int slotX = slot.x;
                    int slotY = slot.y;

                    int drawX = slotX + H_CENTER - 1;
                    int drawY = slotY - ITEM_SIZE - VERTICAL_GAP + SLOT_SIZE;

                    this.itemRenderer.blitOffset = 201.0F;
                    this.itemRenderer.renderAndDecorateItem(this.minecraft.player, stored, drawX, drawY);
                    this.itemRenderer.renderGuiItemDecorations(this.font, stored, drawX, drawY, null);
                }
            }
        }
    }
}

