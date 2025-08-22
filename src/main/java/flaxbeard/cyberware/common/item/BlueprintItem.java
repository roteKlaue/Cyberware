package flaxbeard.cyberware.common.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
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
}

