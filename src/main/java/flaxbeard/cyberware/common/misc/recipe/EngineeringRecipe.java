package flaxbeard.cyberware.common.misc.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import flaxbeard.cyberware.OverclockedOrgans;
import flaxbeard.cyberware.common.item.BlueprintItem;
import lombok.Getter;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class EngineeringRecipe implements IRecipe<IInventory> {
    private final @Nonnull ResourceLocation id;
    private final Item resultItem;
    @Getter
    private final List<IngredientWithAmount> parts;

    public EngineeringRecipe(@Nonnull ResourceLocation id,
                             @Nonnull Item resultItem,
                             @Nonnull List<IngredientWithAmount> parts) {
        this.id = id;
        this.resultItem = resultItem;
        this.parts = parts;
    }

    @Override
    public boolean matches(@Nonnull IInventory inventory, @Nonnull World world) {
        List<ItemStack> inputs = new ArrayList<>();

        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            inputs.add(stack.copy());
        }

        ItemStack blueprintStack = inputs.get(6);
        Item blueprintItem = blueprintStack.getItem();

        if (blueprintStack.isEmpty()
                || !(blueprintItem instanceof BlueprintItem)
                || !((BlueprintItem) blueprintItem).getResult(blueprintStack)
                    .getItem().equals(resultItem)) {
            return false;
        }

        inputs.remove(6);

        List<IngredientWithAmount> required = new ArrayList<>();
        for (IngredientWithAmount p : parts) {
            required.add(new IngredientWithAmount(p.getIngredient(), p.getAmount()));
        }

        for (ItemStack stack : inputs) {
            if (stack.isEmpty()) continue;

            for (IngredientWithAmount req : required) {
                if (stack.isEmpty()) break;
                if (req.isSatisfied()) continue;
                if (req.matches(stack)) {
                    req.consume(stack);
                }
            }
        }

        return required.stream().allMatch(IngredientWithAmount::isSatisfied);
    }


    @Override
    @Nonnull
    public ItemStack assemble(@Nonnull IInventory inv) {
        return new ItemStack(resultItem);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= parts.size() + 1;
    }

    @Override
    @Nonnull
    public ItemStack getResultItem() {
        return new ItemStack(resultItem);
    }

    @Override
    @Nonnull
    public ResourceLocation getId() { return id; }

    @Override
    @Nonnull
    public IRecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Nonnull
    public NonNullList<ItemStack> getRemainingItems(@Nonnull IInventory inv) {
        NonNullList<ItemStack> remaining = NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);

        List<IngredientWithAmount> required = new ArrayList<>();
        for (IngredientWithAmount p : parts) {
            required.add(new IngredientWithAmount(p.getIngredient(), p.getAmount()));
        }

        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);
            if (stack.isEmpty()) continue;

            if (stack.getItem() instanceof BlueprintItem) {
                remaining.set(i, stack.copy());
                continue;
            }

            ItemStack copy = stack.copy();

            for (IngredientWithAmount req : required) {
                if (copy.isEmpty()) break;
                if (req.isSatisfied()) continue;
                if (req.matches(copy)) {
                    req.consume(copy);
                }
            }

            if (!copy.isEmpty()) {
                remaining.set(i, copy);
            }
        }

        return remaining;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    @Nonnull
    public IRecipeType<?> getType() { return Type.INSTANCE; }

    public static class Type implements IRecipeType<EngineeringRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "engineering";
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>>
            implements IRecipeSerializer<EngineeringRecipe> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        @Nonnull
        public EngineeringRecipe fromJson(@Nonnull ResourceLocation id,
                                          @Nonnull JsonObject json) {
            String resultId = JSONUtils.getAsString(json, "blueprint");
            Item resultItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(resultId));
            assert resultItem != null;

            JsonArray partsArray = JSONUtils.getAsJsonArray(json, "parts");
            List<IngredientWithAmount> parts = new ArrayList<>();

            for (JsonElement el : partsArray) {
                JsonObject obj = el.getAsJsonObject();
                Ingredient ingredient = Ingredient.fromJson(obj.get("item"));
                int amount = JSONUtils.getAsInt(obj, "amount", 1);
                parts.add(new IngredientWithAmount(ingredient, amount));
            }

            return new EngineeringRecipe(id, resultItem, parts);
        }

        @Override
        public EngineeringRecipe fromNetwork(@Nonnull ResourceLocation id, PacketBuffer buf) {
            Item resultItem = ForgeRegistries.ITEMS.getValue(buf.readResourceLocation());
            assert resultItem != null;

            int size = buf.readVarInt();
            List<IngredientWithAmount> parts = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                Ingredient ing = Ingredient.fromNetwork(buf);
                int amount = buf.readVarInt();
                parts.add(new IngredientWithAmount(ing, amount));
            }

            return new EngineeringRecipe(id, resultItem, parts);
        }

        @Override
        public void toNetwork(PacketBuffer buf, EngineeringRecipe recipe) {
            buf.writeResourceLocation(Objects.requireNonNull(recipe.resultItem.getRegistryName()));
            buf.writeVarInt(recipe.parts.size());

            for (IngredientWithAmount part : recipe.parts) {
                part.getIngredient().toNetwork(buf);
                buf.writeVarInt(part.getAmount());
            }
        }
    }

    public static Optional<EngineeringRecipe> findByBlueprint(@Nonnull World world, @Nonnull ItemStack blueprintStack) {
        if (blueprintStack.isEmpty() || !(blueprintStack.getItem() instanceof BlueprintItem)) {
            return Optional.empty();
        }

        BlueprintItem blueprintItem = (BlueprintItem) blueprintStack.getItem();
        Item targetResult = blueprintItem.getResult(blueprintStack).getItem();

        RecipeManager manager = world.getRecipeManager();
        return manager.getAllRecipesFor(EngineeringRecipe.Type.INSTANCE).stream()
                .filter(recipe -> recipe.getResultItem().getItem().equals(targetResult))
                .findFirst();
    }
}
