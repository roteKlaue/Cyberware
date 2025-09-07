package flaxbeard.cyberware.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import flaxbeard.cyberware.OverclockedOrgans;
import flaxbeard.cyberware.common.CyberwareConfig;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class ScannerScreen extends ContainerScreen<ScannerContainer> {
    private static final ResourceLocation SCANNER_TEXTURES = new ResourceLocation(OverclockedOrgans.MOD_ID, "textures/gui/scanner.png");

    private static final String[] dots = { "", ".", "..", "...", "....", "....." };
    private static final Map<String, Integer> langMax = new HashMap<>();

    private int messageNum = -1;
    private boolean resetLast = false;

    public ScannerScreen(ScannerContainer container, PlayerInventory inv, ITextComponent title) {
        super(container, inv, title);
    }

    @Override
    protected void renderLabels(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY) {
        if (this.minecraft == null) return;
        this.font.draw(matrixStack, this.inventory.getDisplayName().getString(), 8, this.getYSize() - 96 + 2, 0x4210752);

        if (!this.menu.scanner.slots.getStackInSlot(0).isEmpty() &&
                (this.menu.scanner.slots.getStackInSlot(2).isEmpty())) {

            int maxMessage = getMaxMessage(this.minecraft.getLanguageManager().getSelected().getCode());
            int ticks = (this.minecraft.player == null ? 0 :
                    this.minecraft.player.tickCount) / 10;
            int dotsNum = ticks % 6;

            if ((dotsNum == 0 && !resetLast) || messageNum == -1 || messageNum >= maxMessage) {
                if (dotsNum == 0) resetLast = true;
                messageNum = this.minecraft.level == null ? 0 :
                        this.minecraft.level.random.nextInt(maxMessage);
            }
            if (dotsNum != 0) resetLast = false;

            String message = I18n.get("gui." + OverclockedOrgans.MOD_ID + ".scanner_saying." + messageNum) + dots[dotsNum];
            this.font.draw(matrixStack, message, 6, 20, 0x1F6D7C);
        }

        this.font.draw(matrixStack, this.menu.scanner.getDisplayName().getString(), 6, 7, 0x1DA9C1);

        float chance = this.menu.scanner.calculateChance();
        String num = String.format("%.2f%%", chance);
        String chanceText = I18n.get("gui." + OverclockedOrgans.MOD_ID + ".percent", num);
        this.font.draw(matrixStack, chanceText, this.getXSize() - 6 - this.font.width(chanceText), 7, 0x1DA9C1);

        int progress = (int) Math.ceil(this.menu.scanner.getProgress() * 162);
        this.minecraft.getTextureManager().bind(SCANNER_TEXTURES);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.color4f(1f, 1f, 1f, 0.6f);

        blit(matrixStack, 5, 32, 0, 175, progress, 9);
        blit(matrixStack, 5 + progress, 32, progress, 166, 162 - progress, 9);
        RenderSystem.disableBlend();
    }

    @Override
    protected void renderBg(@Nonnull MatrixStack matrixStack, float p_230450_2_, int p_230450_3_, int p_230450_4_) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        if (this.minecraft == null) return;
        this.minecraft.getTextureManager().bind(SCANNER_TEXTURES);

        int i = (this.width - this.getXSize()) / 2;
        int j = (this.height - this.getYSize()) / 2;

        blit(matrixStack, i, j, 0, 0, this.getXSize(), this.getYSize());
    }

    private int getMaxMessage(@Nonnull String language) {
        if (langMax.containsKey(language)) return langMax.get(language);
        int count = Integer.parseInt(I18n.get("gui." + OverclockedOrgans.MOD_ID + ".scanner_saying.count")) - 1;
        langMax.put(language, count);
        return count;
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);

        int barX = this.leftPos + 5;
        int barY = this.topPos + 32;
        int barWidth = 162;
        int barHeight = 9;

        if (mouseX >= barX && mouseX <= barX + barWidth &&
                mouseY >= barY && mouseY <= barY + barHeight) {

            float totalTicks = this.menu.scanner.getProgress();
            OverclockedOrgans.LOGGER.info("Total ticks: {}", totalTicks);
            int ticksLeft = CyberwareConfig.SCANNER_TIME.get() - this.menu.scanner.ticks;
            int seconds = (ticksLeft % 1200) / 20;
            int minutes = (ticksLeft / 1200);
            ITextComponent timeLeft = new TranslationTextComponent("gui.overclockedorgans.time_left", minutes, seconds);

            this.renderTooltip(matrixStack, timeLeft, mouseX, mouseY);
        }
    }
}
