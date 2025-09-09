package flaxbeard.cyberware.common.misc.recipe;

import lombok.Getter;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

public class IngredientWithAmount {
    @Getter
    private final Ingredient ingredient;
    @Getter
    private final int amount;
    private int matched;

    public IngredientWithAmount(Ingredient ingredient, int amount) {
        this.ingredient = ingredient;
        this.amount = amount;
        this.matched = 0;
    }

    public boolean matches(ItemStack stack) {
        return ingredient.test(stack);
    }

    public void consume(ItemStack stack) {
        int needed = amount - matched;
        int take = Math.min(stack.getCount(), needed);
        matched += take;
    }

    public boolean isSatisfied() {
        return matched >= amount;
    }
}
