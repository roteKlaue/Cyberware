package flaxbeard.cyberware.common.item.equipment;

import flaxbeard.cyberware.common.item.CyberwareItems;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.item.crafting.Ingredient;

import javax.annotation.Nonnull;

public enum CyberwareItemTiers implements IItemTier {
    KATANA(ItemTier.DIAMOND.getUses(),
            ItemTier.DIAMOND.getSpeed(),
            ItemTier.DIAMOND.getAttackDamageBonus(),
            ItemTier.IRON.getLevel(),
            ItemTier.GOLD.getEnchantmentValue(),
            Ingredient.of(new ItemStack(CyberwareItems.COMPONENTS.get(4).get(), 1)));

    private final int level;
    private final int uses;
    private final float speed;
    private final float attackDamageBonus;
    private final int enchantmentValue;
    private final Ingredient repairIngredient;

    CyberwareItemTiers(int uses, float speed, float attackDamageBonus, int level, int enchantmentLevel, @Nonnull Ingredient repairIngredient) {
        this.level = level;
        this.uses = uses;
        this.speed = speed;
        this.enchantmentValue = enchantmentLevel;
        this.repairIngredient = repairIngredient;
        this.attackDamageBonus = attackDamageBonus;
    }

    @Override
    public int getUses() {
        return this.uses;
    }

    @Override
    public float getSpeed() {
        return this.speed;
    }

    @Override
    public float getAttackDamageBonus() {
        return attackDamageBonus;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public int getEnchantmentValue() {
        return enchantmentValue;
    }

    @Override
    @Nonnull
    public Ingredient getRepairIngredient() {
        return repairIngredient;
    }
}
