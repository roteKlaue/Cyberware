package flaxbeard.cyberware.common.item;

import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.item.IBlueprint;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;

public class BlueprintItem extends Item implements IBlueprint {
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

    @Override
    @Nonnull
    public ITextComponent getName(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        if (tag != null && tag.contains("blueprintItem", Constants.NBT.TAG_COMPOUND)) {
            ItemStack stored = ItemStack.of(tag.getCompound("blueprintItem"));
            if (!stored.isEmpty()) {
                return new TranslationTextComponent(
                        "item.overclockedorgans.blueprint_not_blank",
                        stored.getHoverName()
                );
            }
        }

        return super.getName(stack);
    }

    @Override
    @Nonnull
    public ItemStack getResult(ItemStack stack, NonNullList<ItemStack> items) {
        CompoundNBT tag = stack.getTag();
        if (tag != null && tag.contains("blueprintItem", Constants.NBT.TAG_COMPOUND)) {
            return ItemStack.of(tag.getCompound("blueprintItem"));
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getIconForDisplay(ItemStack stack)
    {
        CompoundNBT tag = stack.getTag();
        if (tag == null || !tag.contains("blueprintItem", Constants.NBT.TAG_COMPOUND))
            return ItemStack.EMPTY;
        return ItemStack.of(tag.getCompound("blueprintItem"));
    }

    @Override
    public NonNullList<ItemStack> getRequirementsForDisplay(ItemStack stack)
    {
        CompoundNBT tagCompound = stack.getTag();
        if (tagCompound != null
                && tagCompound.contains("blueprintItem", Constants.NBT.TAG_COMPOUND)) {
            ItemStack blueprintItem = ItemStack.of(tagCompound.getCompound("blueprintItem"));
            if (!blueprintItem.isEmpty() && CyberwareAPI.canDeconstruct(blueprintItem)) {
                return CyberwareAPI.getComponents(blueprintItem);
            }
        }

        return NonNullList.create();
    }
}

