package flaxbeard.cyberware.common.misc.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import lombok.Getter;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;

public class DestructingRecipe implements IRecipe<IInventory> {
    private final ResourceLocation id;
    @Getter
    private final ItemStack input;
    @Getter
    private final NonNullList<ItemStack> outputs;

    public DestructingRecipe(ResourceLocation id, ItemStack input, NonNullList<ItemStack> outputs) {
        this.id = id;
        this.input = input;
        this.outputs = outputs;
    }

    @Override
    public boolean matches(IInventory inv, @Nonnull World world) {
        ItemStack stack = inv.getItem(0);
        return !stack.isEmpty() && ItemStack.isSameIgnoreDurability(stack, input);
    }

    @Override
    @Nonnull
    public ItemStack assemble(@Nonnull IInventory inv) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    @Nonnull
    public ItemStack getResultItem() {
        return ItemStack.EMPTY;
    }

    @Override
    @Nonnull
    public ResourceLocation getId() {
        return id;
    }

    @Override
    @Nonnull
    public IRecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    @Nonnull
    public IRecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements IRecipeType<DestructingRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "destructing";
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>>
            implements IRecipeSerializer<DestructingRecipe> {

        public static final Serializer INSTANCE = new Serializer();

        @Override
        public DestructingRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            JsonObject inputObj = JSONUtils.getAsJsonObject(json, "input");
            String inputId = JSONUtils.getAsString(inputObj, "item");
            Item inputItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(inputId));
            if (inputItem == null) {
                throw new JsonSyntaxException("Unknown item '" + inputId + "' in input for recipe " + recipeId);
            }

            NonNullList<ItemStack> outputs = NonNullList.create();
            JsonArray outArray = JSONUtils.getAsJsonArray(json, "outputs");
            for (JsonElement e : outArray) {
                JsonObject obj = e.getAsJsonObject();

                String outId = JSONUtils.getAsString(obj, "item");
                Item outItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(outId));
                if (outItem == null) {
                    throw new JsonSyntaxException("Unknown item '" + outId + "' in outputs for recipe " + recipeId);
                }

                int count = obj.has("count") && !obj.get("count").isJsonNull()
                        ? obj.get("count").getAsInt()
                        : 1;

                outputs.add(new ItemStack(outItem, count));
            }

            return new DestructingRecipe(recipeId, new ItemStack(inputItem), outputs);
        }

        @Override
        public DestructingRecipe fromNetwork(@Nonnull ResourceLocation recipeId, PacketBuffer buffer) {
            ItemStack input = buffer.readItem();
            int size = buffer.readVarInt();
            NonNullList<ItemStack> outputs = NonNullList.withSize(size, ItemStack.EMPTY);
            for (int i = 0; i < size; i++) {
                outputs.set(i, buffer.readItem());
            }
            return new DestructingRecipe(recipeId, input, outputs);
        }

        @Override
        public void toNetwork(PacketBuffer buffer, DestructingRecipe recipe) {
            buffer.writeItem(recipe.getInput());
            buffer.writeVarInt(recipe.getOutputs().size());
            for (ItemStack stack : recipe.getOutputs()) {
                buffer.writeItem(stack);
            }
        }
    }
}
