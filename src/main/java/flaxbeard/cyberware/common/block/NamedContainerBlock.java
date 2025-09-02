package flaxbeard.cyberware.common.block;

import flaxbeard.cyberware.common.block.entities.NameContainerProvider;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NamedContainerBlock<T extends TileEntity> extends DirectionalBlock {
    private final RegistryObject<TileEntityType<T>> tileEntitySupplier;
    private final Class<T> tileEntityClass;
    private final BlockEntityNameAccessor<T> tileNameAccessor;

    public NamedContainerBlock(Properties properties, RegistryObject<TileEntityType<T>> tileEntitySupplier, Class<T> entityClass,
                               BlockEntityNameAccessor<T> tileNameAccessor) {
        super(properties);
        this.tileEntitySupplier = tileEntitySupplier;
        this.tileEntityClass = entityClass;
        this.tileNameAccessor = tileNameAccessor;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return tileEntitySupplier.get().create();
    }

    @Override
    public void setPlacedBy(@Nonnull World world,
                            @Nonnull BlockPos pos,
                            @Nonnull BlockState state,
                            @Nullable LivingEntity placer,
                            @Nonnull ItemStack stack) {
        if (stack.hasCustomHoverName()) {
            TileEntity tile = world.getBlockEntity(pos);
            if (tileEntityClass.isInstance(tile)) {
                @SuppressWarnings("unchecked")
                T tileEntity = (T)tile;
                tileNameAccessor.setName(tileEntity, stack.getHoverName());
            }
        }
    }

    @Override
    @Nonnull
    @SuppressWarnings("deprecation")
    public ItemStack getCloneItemStack(IBlockReader world, @Nonnull BlockPos pos, @Nonnull BlockState state) {
        TileEntity tile = world.getBlockEntity(pos);

        if (tileEntityClass.isInstance(tile)) {
            @SuppressWarnings("unchecked")
            T toReturn = (T) tile;
            ITextComponent component = tileNameAccessor.getName(toReturn);

            if (component != null) {
                ItemStack stack = new ItemStack(this);
                if (!(component instanceof TranslationTextComponent)) {
                    stack.setHoverName(component);
                }
                return stack;
            }
        }

        return super.getCloneItemStack(world, pos, state);
    }

    @Override
    @Nonnull
    @SuppressWarnings("deprecation")
    public ActionResultType use(@Nonnull BlockState state,
                                @Nonnull World world,
                                @Nonnull BlockPos position,
                                @Nonnull PlayerEntity player,
                                @Nonnull Hand hand,
                                @Nonnull BlockRayTraceResult result) {
        if (world.isClientSide) return ActionResultType.PASS;

        if (world.getBlockEntity(position) instanceof INamedContainerProvider) {
            INamedContainerProvider provider = (INamedContainerProvider) world.getBlockEntity(position);
            NetworkHooks.openGui((ServerPlayerEntity) player, provider, position);
            return ActionResultType.SUCCESS;
        }

        return ActionResultType.PASS;
    }

    @FunctionalInterface
    public interface BlockEntityNameAccessor<T extends TileEntity> {
        @Nullable ITextComponent getName(T te);
        default void setName(T te, ITextComponent name) {}
    }

    public static class NamedContainerProviderAccessor<T extends NameContainerProvider<T>> implements BlockEntityNameAccessor<T> {
        @Nullable
        @Override
        public ITextComponent getName(T te) {
            return te.getDisplayName();
        }

        @Override
        public void setName(T te, ITextComponent name) {
            te.setCustomName(name);
        }
    }
}
