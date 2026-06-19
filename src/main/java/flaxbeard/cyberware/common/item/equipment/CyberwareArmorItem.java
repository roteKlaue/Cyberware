package flaxbeard.cyberware.common.item.equipment;

import flaxbeard.cyberware.api.item.IDeconstructable;
import flaxbeard.cyberware.common.item.CreativeModeTabs;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class CyberwareArmorItem extends ArmorItem implements IDeconstructable {
    private final Supplier<BipedModel<LivingEntity>> modelSupplier;

    @OnlyIn(Dist.CLIENT)
    private BipedModel<LivingEntity> armorModel;

    public CyberwareArmorItem(
            IArmorMaterial material,
            EquipmentSlotType slot,
            Properties properties
    ) {
        this(material, slot, properties, null);
    }

    public CyberwareArmorItem(
            IArmorMaterial material,
            EquipmentSlotType slot,
            Properties properties,
            @Nullable Supplier<BipedModel<LivingEntity>> modelSupplier
    ) {
        super(material, slot, properties.tab(CreativeModeTabs.EQUIPMENT_GROUP));
        this.modelSupplier = modelSupplier;
    }

    @Nullable
    @Override
    @OnlyIn(Dist.CLIENT)
    @SuppressWarnings("unchecked")
    public <A extends BipedModel<?>> A getArmorModel(
            LivingEntity entity,
            ItemStack stack,
            EquipmentSlotType slot,
            A defaultModel
    ) {
        if (modelSupplier == null) return null;

        if (armorModel == null) {
            armorModel = modelSupplier.get();
        }

        armorModel.young = defaultModel.young;
        armorModel.crouching = defaultModel.crouching;
        armorModel.riding = defaultModel.riding;
        armorModel.rightArmPose = defaultModel.rightArmPose;
        armorModel.leftArmPose = defaultModel.leftArmPose;
        armorModel.leftLeg = defaultModel.leftLeg;
        armorModel.rightLeg = defaultModel.rightLeg;

        return (A) armorModel;
    }
}
