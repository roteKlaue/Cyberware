package flaxbeard.cyberware.common.item;

import flaxbeard.cyberware.api.item.*;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.RegistryObject;

public class EyeUpgradeItem extends CyberwareItem implements IMenuItem, IHudjack {
    private static final float[] ENABLED_COLOR = new float[] { 1F, 0F, 0F };
    private static final CyberwareProperties PROPERTIES = new CyberwareProperties()
                        .slot(ICyberware.BodySlot.EYES)
                        .essence(0)
                        .maxInstallations(1)
                        .incompatibleWith(CyberwareItems.CYBER_EYES_MANUFACTURED, CyberwareItems.CYBER_EYES_SALVAGED);

    public EyeUpgradeItem(CyberwareProperties cyberwareProperties) {
        super(cyberwareProperties);
    }

    @Override
    public String getUnlocalizedLabel(ItemStack stack) {
        return EnableDisableHelper.getUnlocalizedLabel(stack);
    }

    @Override
    public float[] getColor(ItemStack stack) {
        if (!EnableDisableHelper.isEnabled(stack)) return null;
        return ENABLED_COLOR;
    }

    @Override
    public boolean isActive(ItemStack stack) {
        return EnableDisableHelper.isEnabled(stack);
    }

    @Override
    public boolean hasMenu(ItemStack stack) {
        return true;
    }

    @Override
    public void use(Entity entity, ItemStack stack) {
        EnableDisableHelper.toggle(stack);
    }

    public static EyeUpgradeItem makeManufactured() {
        return new EyeUpgradeItem(PROPERTIES.copy());
    }

    public static EyeUpgradeItem makeSalvaged(RegistryObject<? extends IDeconstructable> manufactured) {
        return new EyeUpgradeItem(PROPERTIES.copy().salvaged().manufactured(manufactured));
    }
}