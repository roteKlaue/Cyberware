package flaxbeard.cyberware.common.item.equipment;

import flaxbeard.cyberware.OverclockedOrgans;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.LazyValue;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;

import java.util.function.Supplier;

public enum CyberwareArmorMaterials implements IArmorMaterial {
    SHADES("shades", 5, new int[]{1, 2, 3, 1}, 15,
            SoundEvents.ARMOR_EQUIP_GENERIC, 0.0F, 0.0F,
            () -> Ingredient.of(Items.GLASS)),

    SHADES2("shades2", 5, new int[]{1, 2, 3, 1}, 15,
            SoundEvents.ARMOR_EQUIP_GENERIC, 0.0F, 0.0F,
            () -> Ingredient.of(Items.GLASS)),

    JACKET("jacket", 5, new int[]{1, 2, 3, 1}, 15,
            SoundEvents.ARMOR_EQUIP_LEATHER, 0.0F, 0.0F,
            () -> Ingredient.of(Items.LEATHER)),

    TRENCHCOAT("trenchcoat", 5, new int[]{1, 2, 3, 1}, 15,
            SoundEvents.ARMOR_EQUIP_LEATHER, 0.0F, 0.0F,
            () -> Ingredient.of(Items.LEATHER));

    private static final int[] DURABILITY_PER_SLOT = {13, 15, 16, 11};

    private final String name;
    private final int durabilityMultiplier;
    private final int[] slotProtections;
    private final int enchantability;
    private final SoundEvent equipSound;
    private final float toughness;
    private final float knockbackResistance;
    private final LazyValue<Ingredient> repairIngredient;

    CyberwareArmorMaterials(String name, int durabilityMultiplier, int[] slotProtections, int enchantability,
                      SoundEvent equipSound, float toughness, float knockbackResistance,
                      Supplier<Ingredient> repairIngredient) {
        this.name = name;
        this.durabilityMultiplier = durabilityMultiplier;
        this.slotProtections = slotProtections;
        this.enchantability = enchantability;
        this.equipSound = equipSound;
        this.toughness = toughness;
        this.knockbackResistance = knockbackResistance;
        this.repairIngredient = new LazyValue<>(repairIngredient);
    }

    @Override
    public int getDurabilityForSlot(EquipmentSlotType slot) {
        return DURABILITY_PER_SLOT[slot.getIndex()] * durabilityMultiplier;
    }

    @Override
    public int getDefenseForSlot(EquipmentSlotType slot) {
        return slotProtections[slot.getIndex()];
    }

    @Override
    public int getEnchantmentValue() {
        return enchantability;
    }

    @Override
    public SoundEvent getEquipSound() {
        return equipSound;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return repairIngredient.get();
    }

    @Override
    public String getName() {
        return OverclockedOrgans.MOD_ID + ":" + name;
    }

    @Override
    public float getToughness() {
        return toughness;
    }

    @Override
    public float getKnockbackResistance() {
        return knockbackResistance;
    }
}
