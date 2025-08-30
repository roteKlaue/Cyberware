package flaxbeard.cyberware.common.item;

import flaxbeard.cyberware.api.CyberwareAPI;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;

import javax.annotation.Nonnull;

public class BlueprintItem extends Item {
    public BlueprintItem() {
        super(new Properties().stacksTo(1)
                .tab(CreativeModeTabs.MANUFACTURED_GROUP));
    }

    @Override
    public void fillItemCategory(@Nonnull ItemGroup group, @Nonnull NonNullList<ItemStack> items) {
        if (this.allowdedIn(group)) {
            items.add(makeBlueprint("empty"));
            items.add(makeBlueprint("filled"));
        }
    }

    public static ItemStack makeBlueprint(String id) {
        ItemStack stack = new ItemStack(CyberwareItems.BLUEPRINT.get());
        stack.getOrCreateTag().putString("BlueprintId", id);
        return stack;
    }

    public static ItemStack makeBlueprint(ItemStack stack) {
        if (!stack.isEmpty() && CyberwareAPI.canDeconstruct(stack)) {
            ItemStack toBlue = stack.copy();

            toBlue.setCount(1);
            if (toBlue.isDamageableItem()) {
                toBlue.setDamageValue(0);
            }
            toBlue.setTag(null);

            ItemStack ret = new ItemStack(CyberwareItems.BLUEPRINT.get());
            CompoundNBT tagCompound = new CompoundNBT();
            tagCompound.put("blueprintItem", toBlue.save(new CompoundNBT()));

            ret.setTag(tagCompound);
            return ret;
        }
        return ItemStack.EMPTY;
    }
}

