package flaxbeard.cyberware.common.item;

import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.item.IBlueprint;
import flaxbeard.cyberware.api.item.IDeconstructable;
import flaxbeard.cyberware.common.misc.recipe.EngineeringRecipe;
import flaxbeard.cyberware.common.misc.recipe.IngredientWithAmount;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

public class BlueprintItem extends Item implements IBlueprint {
    private static final String NBT_ITEM_KEY = "blueprintItem";

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

    private static ItemStack makeBlueprint(String id) {
        ItemStack stack = new ItemStack(CyberwareItems.BLUEPRINT.get());
        stack.getOrCreateTag().putString("BlueprintId", id);
        return stack;
    }

    public static ItemStack makeBlueprint(World world, ItemStack stack) {
        if (!stack.isEmpty() && CyberwareAPI.canDeconstruct(world, stack)) {
            if (!(stack.getItem() instanceof IDeconstructable)) return ItemStack.EMPTY;
            IDeconstructable manufactured = ((IDeconstructable) stack.getItem()).getManufactured();
            if (!(manufactured instanceof Item)) return ItemStack.EMPTY;

            ItemStack toBlue = new  ItemStack((Item) manufactured, 1);
            toBlue.setTag(null);

            ItemStack ret = new ItemStack(CyberwareItems.BLUEPRINT.get());
            CompoundNBT tagCompound = new CompoundNBT();
            tagCompound.put(NBT_ITEM_KEY, toBlue.save(new CompoundNBT()));

            ret.setTag(tagCompound);
            return ret;
        }
        return ItemStack.EMPTY;
    }

    @Override
    @Nonnull
    public ITextComponent getName(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        if (tag != null && tag.contains(NBT_ITEM_KEY, Constants.NBT.TAG_COMPOUND)) {
            ItemStack stored = ItemStack.of(tag.getCompound(NBT_ITEM_KEY));
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
    public ItemStack getResult(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        if (tag != null && tag.contains(NBT_ITEM_KEY, Constants.NBT.TAG_COMPOUND)) {
            return ItemStack.of(tag.getCompound(NBT_ITEM_KEY));
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getIconForDisplay(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        if (tag == null || !tag.contains(NBT_ITEM_KEY, Constants.NBT.TAG_COMPOUND))
            return ItemStack.EMPTY;
        return ItemStack.of(tag.getCompound(NBT_ITEM_KEY));
    }

    @Override
    public NonNullList<ItemStack> getRequirementsForDisplay(World world, ItemStack stack) {
        CompoundNBT tagCompound = stack.getTag();
        if (tagCompound != null
                && tagCompound.contains(NBT_ITEM_KEY, Constants.NBT.TAG_COMPOUND)) {
            ItemStack blueprintItem = ItemStack.of(tagCompound.getCompound(NBT_ITEM_KEY));
            if (!blueprintItem.isEmpty() && CyberwareAPI.canDeconstruct(world, blueprintItem)) {
                return CyberwareAPI.getComponents(world, blueprintItem);
            }
        }

        return NonNullList.create();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(@Nonnull ItemStack stack, World world,
                                @Nonnull List<ITextComponent> tooltip,
                                @Nonnull ITooltipFlag flag) {
        if (!Screen.hasShiftDown()) {
            tooltip.add(new TranslationTextComponent("tooltip.overclockedorgans.shift_prompt")
                    .withStyle(TextFormatting.GRAY));
            return;
        }
        if (world == null) return;
        Optional<EngineeringRecipe> recipeOpt = EngineeringRecipe.findByBlueprint(world, stack);
        if (recipeOpt.isPresent()) {
            EngineeringRecipe recipe = recipeOpt.get();
            tooltip.add(new TranslationTextComponent("tooltip.overclockedorgans.blueprint",
                    recipe.getResultItem().getHoverName()).withStyle(TextFormatting.GRAY));
            for (IngredientWithAmount part : recipe.getParts()) {
                tooltip.add(new TranslationTextComponent(
                        "tooltip.overclockedorgans.blueprint.ingredient",
                        part.getIngredient().getItems()[0].getHoverName(),
                        String.valueOf(part.getAmount())
                ).withStyle(TextFormatting.GRAY));
            }
        } else {
            tooltip.add(new TranslationTextComponent("tooltip.overclockedorgans.blueprint.empty")
                    .withStyle(TextFormatting.GRAY));
        }
    }
}

