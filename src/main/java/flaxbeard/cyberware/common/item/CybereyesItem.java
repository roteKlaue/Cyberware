package flaxbeard.cyberware.common.item;

import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.CyberwareUpdateEvent;
import flaxbeard.cyberware.api.ICyberwareUserData;
import flaxbeard.cyberware.api.item.ICyberware;
import flaxbeard.cyberware.api.item.IDeconstructable;
import flaxbeard.cyberware.common.lib.LibConstants;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effects;
import net.minecraft.potion.EffectInstance;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;

public class CybereyesItem extends CyberwareItem {
    private static final CyberwareProperties PROPERTIES = new CyberwareProperties()
            .slot(ICyberware.BodySlot.EYES)
            .essence(8)
            .essential()
            .powerConsumption(LibConstants.CYBEREYES_CONSUMPTION);

    private CybereyesItem(CyberwareProperties properties) {
        super(properties);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public boolean isIncompatible(ItemStack other) {
        if (super.isIncompatible(other)) return true;
        if (!CyberwareAPI.isCyberware(other)) return false;
        return CyberwareAPI.getCyberware(other).isEssential();
    }

    @SubscribeEvent
    public void handleBlindnessImmunity(CyberwareUpdateEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof LivingEntity)) return;
        LivingEntity livingEntity = (LivingEntity) entity;
        if (!livingEntity.hasEffect(Effects.BLINDNESS)) return;

        ICyberwareUserData data = event.getCyberwareUserData();
        if (!data.isCyberwareInstalled(new ItemStack(this))) return;

        livingEntity.removeEffect(Effects.BLINDNESS);
    }

    @SubscribeEvent
    public void handleMissingPower(CyberwareUpdateEvent event) {
        if (!(event.getEntity() instanceof LivingEntity)) return;
        LivingEntity entity = (LivingEntity) event.getEntity();
        if (entity.tickCount % 20 != 0) return;

        ICyberwareUserData data = event.getCyberwareUserData();
        ItemStack cybereyes = data.getCyberware(new ItemStack(this));

        if (cybereyes.isEmpty()) return;

        boolean powered = data.usePower(cybereyes, getPowerConsumption(cybereyes));
        if (powered) return;

        entity.addEffect(new EffectInstance(Effects.BLINDNESS, 40));
    }

    public static CybereyesItem makeManufactured() {
        return new CybereyesItem(PROPERTIES.copy());
    }

    public static CybereyesItem makeSalvaged(RegistryObject<? extends IDeconstructable> manufactured) {
        return new CybereyesItem(PROPERTIES.copy()
                .salvaged()
                .manufactured(manufactured));
    }
}