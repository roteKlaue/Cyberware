package flaxbeard.cyberware.common.handler;

import com.mojang.blaze3d.matrix.MatrixStack;
import flaxbeard.cyberware.OverclockedOrgans;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.ICyberwareUserData;
import flaxbeard.cyberware.api.hud.*;
import flaxbeard.cyberware.api.item.IHudjack;
import flaxbeard.cyberware.client.KeyBinds;
import flaxbeard.cyberware.client.gui.HudConfigurationGui;
import flaxbeard.cyberware.client.gui.hud.MissingPowerDisplay;
import flaxbeard.cyberware.client.gui.hud.NotificationDisplay;
import flaxbeard.cyberware.client.gui.hud.PowerDisplay;
import flaxbeard.cyberware.common.CyberwareConfig;
import flaxbeard.cyberware.common.item.CyberwareItems;
import flaxbeard.cyberware.common.misc.SizedStack;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HudHandler {
    public static final ResourceLocation HUD_TEXTURE = new ResourceLocation(OverclockedOrgans.MOD_ID + ":textures/gui/hud.png");
    public static final Stack<NotificationInstance> notifications = new SizedStack<>(5);

    private static final PowerDisplay powerDisplay = new PowerDisplay();
    private static final MissingPowerDisplay missingPowerDisplay = new MissingPowerDisplay();
    private static final NotificationDisplay notificationDisplay = new NotificationDisplay();

    static {
        notificationDisplay.setHorizontalAnchor(IHudElement.EnumAnchorHorizontal.LEFT);
        notificationDisplay.setVerticalAnchor(IHudElement.EnumAnchorVertical.BOTTOM);
    }

    @SubscribeEvent
    public static void onAddHudElements(CyberwareHudEvent event) {
        if (event == null || !event.isHudjackAvailable()) return;
        event.addElement(powerDisplay);
        event.addElement(missingPowerDisplay);
        event.addElement(notificationDisplay);
    }

    @SubscribeEvent
    public static void onSaveHudElements(CyberwareHudDataEvent event) {
        if (event == null) return;
        event.addElement(powerDisplay);
        event.addElement(missingPowerDisplay);
        event.addElement(notificationDisplay);
    }

    private static int cacheTickExisted = 0;
    private static float cacheFloatingFactor = 0.0F;
    private static List<IHudElement> cacheHudElements = new ArrayList<>();
    private static boolean cacheIsHUDjackAvailable = false;
    private static boolean cachePromptToOpenMenu = false;
    private static int cacheHudColorHex = 0x00FFFF;

    private static int lastTickExisted = 0;
    private static double lastVelX = 0;
    private static double lastVelY = 0;
    @Getter
    private static double lastLastVelX = 0;
    private static double lastLastVelY = 0;

    @SubscribeEvent
    public static void onRender(@Nonnull RenderGameOverlayEvent.Pre event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.CHAT) {
            drawHUD(new MatrixStack(), ScaledResolution.of(Minecraft.getInstance().getWindow()), event.getPartialTicks());
        }
    }

    private static void drawHUD(MatrixStack ms, ScaledResolution resolution, float partialTick) {
        Minecraft mc = Minecraft.getInstance();

        PlayerEntity player = mc.player;
        if (player == null) return;

        if (player.tickCount != cacheTickExisted) {
            cacheTickExisted = player.tickCount;

            LazyOptional<ICyberwareUserData> cyberwareUserData = CyberwareAPI.getCyberwareData(player);
            if (!cyberwareUserData.isPresent()) return;

            ICyberwareUserData data = cyberwareUserData.orElseThrow(RuntimeException::new);

            cacheFloatingFactor = 0.0F;
            boolean isHUDjackAvailable = false;

            List<ItemStack> listHUDjackItems = data.getHudjackItems();
            for (ItemStack stack : listHUDjackItems) {
                if (stack == null || stack.isEmpty()) continue;
                if (!(CyberwareAPI.getCyberware(stack) instanceof IHudjack)) continue;
                IHudjack hw = (IHudjack) CyberwareAPI.getCyberware(stack);
                if (!hw.isActive(stack)) continue;

                isHUDjackAvailable = true;
                if (CyberwareConfig.ENABLE_FLOAT.get()) {
                    if (CyberwareAPI.getCyberware(stack) == CyberwareItems.HUDLENS_MANUFACTURED.get()) {
                        cacheFloatingFactor = (float) (double) CyberwareConfig.HUDLENS_FLOAT.get();
                    } else {
                        cacheFloatingFactor = (float) (double) CyberwareConfig.HUDJACK_FLOAT.get();
                    }
                }
                break;
            }

            CyberwareHudEvent hudEvent = new CyberwareHudEvent(resolution, isHUDjackAvailable);
            MinecraftForge.EVENT_BUS.post(hudEvent);

            cacheHudElements = hudEvent.getElements();
            cacheIsHUDjackAvailable = hudEvent.isHudjackAvailable();
            cachePromptToOpenMenu = !data.getActiveItems().isEmpty() && !data.hasOpenedRadialMenu();
            cacheHudColorHex = data.getHudColorHex();
        }

        ms.pushPose();

        double accelLastY   = lastVelY - lastLastVelY;
        Vector3d vel        = player.getDeltaMovement();
        double accelY       = vel.y - lastVelY;
        double accelPitch   = accelLastY + (accelY - accelLastY) * (partialTick + player.tickCount - lastTickExisted) / 2F;

        float pitch     = player.xRot;
        float prevPitch = player.xRotO;
        float yaw       = player.yRot;
        float prevYaw   = player.yRotO;

        float ipPitch = net.minecraft.util.math.MathHelper.lerp(partialTick, prevPitch, pitch);
        float ipYaw   = net.minecraft.util.math.MathHelper.lerp(partialTick, prevYaw, yaw);

        double pitchCameraMove = cacheFloatingFactor * (ipPitch - pitch);
        double yawCameraMove   = cacheFloatingFactor * (ipYaw   - yaw);

        ms.translate(yawCameraMove, pitchCameraMove + accelPitch * 50F * cacheFloatingFactor, 0.0);

        if (player.tickCount > lastTickExisted + 1) {
            lastTickExisted = player.tickCount;
            lastLastVelX = lastVelX;
            lastLastVelY = lastVelY;
            lastVelX = vel.x;
            lastVelY = vel.y;
        }

        int guiWidth = resolution.width;
        int guiHeight = resolution.height;

        for (IHudElement hudElement : cacheHudElements) {
            if (hudElement == null) continue;

            if (hudElement.getHeight() + HudConfigurationGui.getAbsoluteY(resolution, hudElement) <= 3) {
                HudConfigurationGui.setYFromAbsolute(resolution, hudElement, -hudElement.getHeight() + 4);
            }

            if (HudConfigurationGui.getAbsoluteY(resolution, hudElement) >= guiHeight - 3) {
                HudConfigurationGui.setYFromAbsolute(resolution, hudElement, guiHeight - 4);
            }

            if (hudElement.getWidth() + HudConfigurationGui.getAbsoluteX(resolution, hudElement) <= 3) {
                HudConfigurationGui.setXFromAbsolute(resolution, hudElement, -hudElement.getWidth() + 4);
            }

            if (HudConfigurationGui.getAbsoluteX(resolution, hudElement) >= guiWidth - 3) {
                HudConfigurationGui.setXFromAbsolute(resolution, hudElement, guiWidth - 4);
            }

            hudElement.render(player, resolution, cacheIsHUDjackAvailable, Minecraft.getInstance().screen instanceof HudConfigurationGui, partialTick, ms);
        }

        if (cachePromptToOpenMenu) {
            String textOpenMenu = new TranslationTextComponent(
                    "gui.overclockedorgans.open_menu",
                    KeyBinds.menu.getTranslatedKeyMessage()
            ).getString();
            FontRenderer font = Minecraft.getInstance().font;
            int x = guiWidth - font.width(textOpenMenu) - 5;
            font.drawShadow(ms, textOpenMenu, x, 5.0F, cacheHudColorHex);
        }

        ms.popPose();
    }

    public static void setLastLastVelX(double lastLastVelX) {
        HudHandler.lastLastVelX = lastLastVelX;
    }

    public static void addNotification(NotificationInstance notificationInstance) {
        notifications.add(notificationInstance);
    }
}
