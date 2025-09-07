package flaxbeard.cyberware.common.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Supplier;

public class TallBlock<T extends TileEntity> extends DirectionalBlock {
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;

    private final Supplier<T> supplier;
    private final Class<? extends TileEntity> tileClass;

    public TallBlock(@Nonnull AbstractBlock.Properties properties,
                     @Nonnull Supplier<T> supplier,
                     @Nonnull Class<? extends TileEntity> tileClass) {
        super(properties);
        this.supplier = Objects.requireNonNull(supplier, "supplier");
        this.tileClass = Objects.requireNonNull(tileClass, "tileClass");

        this.registerDefaultState(this.stateDefinition.any()
                .setValue(HALF, DoubleBlockHalf.LOWER));
    }

    @Deprecated
    public TallBlock(@Nonnull AbstractBlock.Properties properties,
                     @Nonnull Supplier<T> supplier) {
        this(properties, supplier, Objects.requireNonNull(supplier).get().getClass());
    }

    @Override
    public void setPlacedBy(@Nonnull World world,
                            @Nonnull BlockPos pos,
                            @Nonnull BlockState state,
                            @Nullable LivingEntity placer,
                            @Nonnull ItemStack stack) {
        super.setPlacedBy(world, pos, state, placer, stack);

        if (world.isClientSide) return;

        BlockPos above = pos.above();
        BlockState upper = state.setValue(HALF, DoubleBlockHalf.UPPER);
        world.setBlock(above, upper, 3);

        if (stack.hasTag()) {
            T te = getMainTileEntity(world, pos, state);
            if (te != null) {
                assert stack.getTag() != null;
                if (stack.getTag().contains("BlockEntityTag")) {
                    te.load(state, stack.getTag().getCompound("BlockEntityTag"));
                }
            }
        }
    }

    @Override
    public void playerWillDestroy(World world,
                                  @Nonnull BlockPos pos,
                                  BlockState state,
                                  @Nonnull PlayerEntity player) {
        DoubleBlockHalf half = state.getValue(HALF);
        BlockPos otherPos = (half == DoubleBlockHalf.LOWER) ? pos.above() : pos.below();
        BlockState otherState = world.getBlockState(otherPos);

        if (otherState.getBlock() == this && otherState.getValue(HALF) != half) {
            world.destroyBlock(otherPos, !player.isCreative());
        }

        super.playerWillDestroy(world, pos, state, player);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockState state = super.getStateForPlacement(context);
        BlockPos pos = context.getClickedPos();
        World world = context.getLevel();

        if (!world.getBlockState(pos.above()).canBeReplaced(context)
            || state == null) {
            return null;
        }

        return state.setValue(HALF, DoubleBlockHalf.LOWER);
    }

    protected boolean isTop(BlockState state) {
        return state.getValue(HALF) == DoubleBlockHalf.UPPER;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return isTop(state);
    }

    @Nullable
    @Override
    public T createTileEntity(BlockState state, IBlockReader world) {
        return isTop(state) ? supplier.get() : null;
    }

    @Nullable
    public T getMainTileEntity(World world, BlockPos pos, BlockState state) {
        if (!(state.getBlock() instanceof TallBlock)) return null;

        if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
            pos = pos.above();
        }

        TileEntity te = world.getBlockEntity(pos);
        if (te == null) return null;

        if (!tileClass.isInstance(te)) return null;

        @SuppressWarnings("unchecked")
        T typed = (T) te;
        return typed;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(BlockState state,
                         @Nonnull World world,
                         @Nonnull BlockPos pos,
                         BlockState newState,
                         boolean isMoving) {

        if (state.getBlock() != newState.getBlock()) {
            DoubleBlockHalf half = state.getValue(HALF);
            BlockPos otherPos = (half == DoubleBlockHalf.LOWER) ? pos.above() : pos.below();
            BlockState otherState = world.getBlockState(otherPos);

            if (otherState.getBlock() == this && otherState.getValue(HALF) != half) {
                world.removeBlock(otherPos, false);
            }
        }

        super.onRemove(state, world, pos, newState, isMoving);
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HALF);
    }
}
