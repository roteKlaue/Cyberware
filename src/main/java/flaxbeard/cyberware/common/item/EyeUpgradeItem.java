package flaxbeard.cyberware.common.item;

import flaxbeard.cyberware.api.item.EnableDisableHelper;
import flaxbeard.cyberware.api.item.IHudjack;
import flaxbeard.cyberware.api.item.IMenuItem;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;

public class EyeUpgradeItem extends CyberwareItem implements IMenuItem, IHudjack {
    public EyeUpgradeItem() {
        super(BodySlot.EYES, 0, new ArrayList<>(), new ArrayList<>());
    }

    @Override
    public String getUnlocalizedLabel(ItemStack stack)
    {
        return EnableDisableHelper.getUnlocalizedLabel(stack);
    }

    private static final float[] f = new float[] { 1F, 0F, 0F };

    @Override
    public float[] getColor(ItemStack stack)
    {
        return EnableDisableHelper.isEnabled(stack) ? f : null;
    }

    @Override
    public boolean isActive(ItemStack stack)
    {
        return EnableDisableHelper.isEnabled(stack);
    }

    @Override
    public boolean hasMenu(ItemStack stack)
    {
        return true;
    }

    @Override
    public void use(Entity entity, ItemStack stack)
    {
        EnableDisableHelper.toggle(stack);
    }
}
