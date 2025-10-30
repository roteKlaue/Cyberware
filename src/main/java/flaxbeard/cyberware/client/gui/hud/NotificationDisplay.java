package flaxbeard.cyberware.client.gui.hud;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import flaxbeard.cyberware.api.hud.BaseHudElement;
import flaxbeard.cyberware.api.hud.INotification;
import flaxbeard.cyberware.api.hud.NotificationInstance;
import flaxbeard.cyberware.api.hud.ScaledResolution;
import flaxbeard.cyberware.client.gui.hud.notification.NotificationArmor;
import flaxbeard.cyberware.client.gui.hud.notification.NotificationRadio;
import flaxbeard.cyberware.common.block.entities.BeaconBlockEntity;
import flaxbeard.cyberware.common.handler.HudHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class NotificationDisplay extends BaseHudElement {
    public NotificationDisplay() {
        super("cyberware:notification");
        setDefaultX(5);
        setDefaultY(5 - 20);
        setWidth(5 * 18);
        setHeight(14 + 20 + 4);
        setDefaultVerticalAnchor(EnumAnchorVertical.BOTTOM);
    }

    private static int cache_tickExisted = -1;
    private static int cache_tierRadio = -1;
    private static boolean cache_isWearingLightArmor = false;
    private static final NotificationInstance[] examples = new NotificationInstance[] {
            new NotificationInstance(0, new NotificationArmor(true)),
            new NotificationInstance(0, new NotificationArmor(false)),
            new NotificationInstance(0, new NotificationArmor(true)),
            new NotificationInstance(0, new NotificationArmor(false))
    };

    @Override
    public void renderElement(int x, int y, PlayerEntity player, ScaledResolution resolution, boolean hudjackAvailable, boolean configOpen, float partialTicks, MatrixStack mx) {
        if (isHidden() || !hudjackAvailable) {
            return;
        }

        boolean isTopAnchored = getVerticalAnchor() == EnumAnchorVertical.TOP;
        boolean isRightAnchored = getHorizontalAnchor() == EnumAnchorHorizontal.RIGHT;

        float currTime = player.tickCount + partialTicks;

        mx.pushPose();
        RenderSystem.enableBlend();

        Minecraft.getInstance().getTextureManager().bind(HudHandler.HUD_TEXTURE);

        if (player.tickCount != cache_tickExisted) {
            cache_tickExisted = player.tickCount;
            boolean wasWearingLightArmor = cache_isWearingLightArmor;
            cache_isWearingLightArmor = isWearingLightOrNone(player);
            if (cache_isWearingLightArmor != wasWearingLightArmor) {
                HudHandler.addNotification(new NotificationInstance(currTime, new NotificationArmor(cache_isWearingLightArmor)));
            }

            int tierRadioPrevious = cache_tierRadio;
            cache_tierRadio = BeaconBlockEntity.isInRange(player.level, player.position().x, player.position().y, player.position().z);
            if (cache_tierRadio != tierRadioPrevious) {
                HudHandler.addNotification(new NotificationInstance(currTime, new NotificationRadio(cache_tierRadio)));
            }
        }

        if (configOpen) {
            for (int indexNotification = 0; indexNotification < examples.length; indexNotification++) {
                NotificationInstance notificationInstance = examples[indexNotification];
                INotification notification = notificationInstance.getNotification();
                double percentVisible = 0F;
                if (indexNotification == 0) {
                    percentVisible = (player.tickCount % 40F) / 40F;
                }

                float yOffset = (float) (20F * Math.sin(percentVisible * Math.PI / 2F));

                mx.pushPose();
                RenderSystem.color3f(1.0F, 1.0F, 1.0F);
                mx.translate(0F, isTopAnchored ? -yOffset : yOffset, 0F);
                int index = (examples.length - 1) - indexNotification;
                int xPos = isRightAnchored ? (x + getWidth() - ((index + 1) * 18)) : (x + index * 18);
                notification.render(mx, xPos, y + (isTopAnchored ? 20 : 0));
                mx.popPose();
            }
        } else {
            List<NotificationInstance> notificationsElapsed = new ArrayList<>();
            for (int indexNotification = 0; indexNotification < HudHandler.notifications.size(); indexNotification++) {
                NotificationInstance notificationInstance = HudHandler.notifications.get(indexNotification);
                INotification notification = notificationInstance.getNotification();
                if (currTime - notificationInstance.getTime() < notification.getDuration() + 25) {
                    double percentVisible = Math.max(0F, (currTime - notificationInstance.getTime() - notification.getDuration()) / 30F);

                    float yOffset = (float) (20F * Math.sin(percentVisible * Math.PI / 2F));

                    mx.pushPose();
                    RenderSystem.color3f(1.0F, 1.0F, 1.0F);
                    mx.translate(0F, isTopAnchored ? -yOffset : yOffset, 0F);
                    int index = (HudHandler.notifications.size() - 1) - indexNotification;
                    int xPos = isRightAnchored ? (x + getWidth() - ((index + 1) * 18)) : (x + index * 18);
                    notification.render(mx, xPos, y + (isTopAnchored ? 20 : 0));
                    mx.popPose();
                } else {
                    notificationsElapsed.add(notificationInstance);
                }
            }

            for (NotificationInstance notificationInstance : notificationsElapsed) {
                HudHandler.notifications.remove(notificationInstance);
            }
        }

        mx.popPose();
    }

    public static boolean isWearingLightOrNone(PlayerEntity player) {
        for (ItemStack armorPiece : player.getArmorSlots()) {
            if (armorPiece.isEmpty()) continue;

            ArmorItem armor = armorPiece.getItem() instanceof ArmorItem ? (ArmorItem) armorPiece.getItem() : null;
            if (armor == null) return false;

            IArmorMaterial mat = armor.getMaterial();
            if (mat != ArmorMaterial.LEATHER) return false;
        }
        return true;
    }
}
