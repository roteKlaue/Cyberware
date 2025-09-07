package flaxbeard.cyberware.common.block.entities;

import flaxbeard.cyberware.OverclockedOrgans;
import lombok.Setter;
import net.minecraft.block.BlockState;
import net.minecraft.data.BlockStateVariantBuilder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NameContainerProvider<T extends NameContainerProvider<T>> extends TileEntity implements INamedContainerProvider {
    @Setter
    public ITextComponent customName = null;
    private final String cachedKey;
    private final BlockStateVariantBuilder.ITriFunction<Integer, PlayerInventory, T, Container> container;
    private final Class<T> tClass;

    public NameContainerProvider(TileEntityType<T> tileEntityType,
                                 String blockName,
                                 BlockStateVariantBuilder.ITriFunction<Integer, PlayerInventory, T, Container> containerSupplier,
                                 Class<T> tClass) {
        super(tileEntityType);
        this.cachedKey = "container." + OverclockedOrgans.MOD_ID + "." + blockName;
        container = containerSupplier;
        this.tClass = tClass;
    }

    @Override
    public void load(@Nonnull BlockState state,
                     @Nonnull CompoundNBT tag) {
        super.load(state, tag);

        if (tag.contains("CustomName", 8)) {
            customName = new StringTextComponent(tag.getString("CustomName"));
        }
    }

    @Override
    @Nonnull
    public CompoundNBT save(@Nonnull CompoundNBT tag) {
        super.save(tag);
        if (hasCustomName()) {
            tag.putString("CustomName", customName.getString());
        }
        return tag;
    }

    public boolean hasCustomName() {
        return customName != null;
    }

    @Override
    @Nonnull
    public ITextComponent getDisplayName() {
        return customName != null ? customName :
                new TranslationTextComponent(cachedKey);
    }

    @Nullable
    @Override
    public Container createMenu(int id, @Nonnull PlayerInventory inventory, @Nonnull PlayerEntity entity) {
        if (!tClass.isInstance(this)) return null;
        @SuppressWarnings("unchecked")
        T castedEntity = (T) this;
        return container.apply(id, inventory, castedEntity);
    }
}
