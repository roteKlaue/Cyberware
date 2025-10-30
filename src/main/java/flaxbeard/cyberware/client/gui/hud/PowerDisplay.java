package flaxbeard.cyberware.client.gui.hud;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import flaxbeard.cyberware.OverclockedOrgans;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.ICyberwareUserData;
import flaxbeard.cyberware.api.hud.BaseHudElement;
import flaxbeard.cyberware.api.hud.ScaledResolution;
import flaxbeard.cyberware.client.ClientUtils;
import flaxbeard.cyberware.common.handler.HudHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.LazyOptional;

public class PowerDisplay extends BaseHudElement {
    private final static float[] colorLowPowerFloats = { 1.0F, 0.0F, 0.125F };
    private final static int colorLowPowerHex = 0xFF0020;

    private static float cache_percentFull = 0;
    private static int cachePowerCapacity = 0;
    private static int cachePowerStored = 0;
    private static int cachePowerProduction = 0;
    private static int cachePowerConsumption = 0;
    private static float[] cacheHudColor = colorLowPowerFloats;
    private static int cacheHudColorHex = 0x00FFFF;

    private static final ResourceLocation HUD_TEXTURE = HudHandler.HUD_TEXTURE;

    public PowerDisplay() {
        super(OverclockedOrgans.MOD_ID + ":power");
        setDefaultX(5);
        setDefaultY(5);
        setHeight(25);
        setWidth(101);
    }

    @Override
    public void renderElement(int x, int y, PlayerEntity player, ScaledResolution resolution, boolean hudjackAvailable, boolean configOpen, float partialTicks, MatrixStack matrixStack) {
        if (isHidden() || !hudjackAvailable) return;

        boolean isRightAnchored = getHorizontalAnchor() == EnumAnchorHorizontal.RIGHT;
        if (player.tickCount % 20 == 0) {
            LazyOptional<ICyberwareUserData> cyberwareUserData = CyberwareAPI.getCyberwareData(player);
            if (!cyberwareUserData.isPresent()) return;
            ICyberwareUserData data = cyberwareUserData.orElseThrow(RuntimeException::new);

            cache_percentFull = data.getPercentFull();
            cachePowerCapacity = data.getCapacity();
            cachePowerStored = data.getStoredPower();
            cachePowerProduction = data.getProduction();
            cachePowerConsumption = data.getConsumption();
            cacheHudColor = data.getHudColor();
            cacheHudColorHex = data.getHudColorHex();
        }

        if (cachePowerCapacity == 0) return;

        boolean isLowPower = cache_percentFull <= 0.2F;
        boolean isCriticalPower = cache_percentFull <= 0.05F;
        if (isCriticalPower && player.tickCount % 4 == 0) return;

        float[] colorFloats = isLowPower ? colorLowPowerFloats : cacheHudColor;
        int colorHex = isLowPower ? colorLowPowerHex : cacheHudColorHex;

        Minecraft mc = Minecraft.getInstance();
        FontRenderer fontRenderer = mc.font;

        mc.getTextureManager().bind(HUD_TEXTURE);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableAlphaTest();
        RenderSystem.color4f(colorFloats[0], colorFloats[1], colorFloats[2], 1.0F);

        int uOffset = isLowPower ? 39 : 0;
        int xOffset = isRightAnchored ? (x + getWidth() - 13) : x;
        int yBatterySize = Math.round(21F * cache_percentFull);

        ClientUtils.drawTexturedModalRect(matrixStack, xOffset, y, uOffset, 0, 13, 2 + (21 - yBatterySize));
        ClientUtils.drawTexturedModalRect(matrixStack, xOffset, y + 2 + (21 - yBatterySize),
                13 + uOffset, 2 + (21 - yBatterySize), 13, yBatterySize + 2);
        ClientUtils.drawTexturedModalRect(matrixStack, xOffset, y + 2 + (21 - yBatterySize),
                26 + uOffset, 2 + (21 - yBatterySize), 13, yBatterySize + 2);

        String textPowerStorage = cachePowerStored + " / " + cachePowerCapacity;
        int xPowerStorage = isRightAnchored
                ? x + getWidth() - 15 - fontRenderer.width(textPowerStorage)
                : x + 15;
        fontRenderer.drawShadow(matrixStack, textPowerStorage, xPowerStorage, y + 4, colorHex);

        String textPowerProgression = "-" + cachePowerConsumption + " / +" + cachePowerProduction;
        int xPowerProgression = isRightAnchored
                ? x + getWidth() - 15 - fontRenderer.width(textPowerProgression)
                : x + 15;
        fontRenderer.drawShadow(matrixStack, textPowerProgression, xPowerProgression, y + 14, colorHex);

        RenderSystem.enableAlphaTest();
        RenderSystem.disableBlend();
    }
}
