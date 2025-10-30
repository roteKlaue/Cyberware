package flaxbeard.cyberware.common.item;

import flaxbeard.cyberware.OverclockedOrgans;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class CyberwareBaseItem extends Item {
    private ItemStack[] itemStackCache;

    public CyberwareBaseItem() {
        this(CreativeModeTabs.MANUFACTURED_GROUP);
    }

    public CyberwareBaseItem(ItemGroup group) {
        super(new Properties()
                .tab(group));
    }

    public ItemStack getCachedStack(int damage) {
        ItemStack itemStack = itemStackCache[damage];
        if (itemStack != null
                && ( itemStack.getItem() != this
                || itemStack.getCount() != 1
                || getDamage(itemStack) != damage)) {
            OverclockedOrgans.LOGGER.error("Corrupted item stack cache: found {} as {}:{}, expected {}:{}", itemStack, itemStack.getItem(), itemStack.getDamageValue(), this, damage);
            itemStack = null;
        }
        if (itemStack == null) {
            itemStack = new ItemStack(this, 1);
            itemStack.setDamageValue(damage);
            itemStackCache[damage] = itemStack;
        }
        return itemStack;
    }

    public static CyberwareBaseItem makeSalvaged() {
        return new CyberwareBaseItem(CreativeModeTabs.SALVAGED_GROUP);
    }
}
