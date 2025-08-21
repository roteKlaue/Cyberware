package flaxbeard.cyberware.common.block;

import flaxbeard.cyberware.OverclockedOrgans;
import flaxbeard.cyberware.common.block.entities.ComponentBoxBlockEntity;
import flaxbeard.cyberware.common.block.entities.CyberwareBlockEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ComponentBoxBlock extends Block {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    private static final VoxelShape SHAPE_NS = Block.box(1, 0, 4, 15, 10, 12);
    private static final VoxelShape SHAPE_EW = Block.box(4, 0, 1, 12, 10, 15);

    public ComponentBoxBlock() {
        super(Properties.of(Material.WOOD));
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(@Nonnull BlockState state,
                               @Nonnull IBlockReader worldIn,
                               @Nonnull BlockPos pos,
                               @Nonnull ISelectionContext context) {
        Direction facing = state.getValue(FACING);
        switch (facing) {
            case NORTH: case SOUTH: return SHAPE_NS;
            case WEST: case EAST:   return SHAPE_EW;
            default:    return VoxelShapes.block();
        }
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.defaultBlockState().setValue(FACING, context.getPlayer().getDirection().getOpposite());
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
        if (!world.isClientSide) {
            ItemStack heldStack = player.getItemInHand(hand);
            if (heldStack.isEmpty() && player.isShiftKeyDown()) {
                ItemStack stack = new ItemStack(this);
                TileEntity entity = world.getBlockEntity(position);
                if (entity instanceof ComponentBoxBlockEntity) {
                    ComponentBoxBlockEntity comp = (ComponentBoxBlockEntity) entity;
                    CompoundNBT beTag = comp.saveToItemStack();
                    stack.getOrCreateTag().put("BlockEntityTag", beTag);
                }
                player.setItemInHand(hand, stack);
                world.removeBlock(position, false);
                return ActionResultType.SUCCESS;
            }

            if (world.getBlockEntity(position) instanceof INamedContainerProvider) {
                INamedContainerProvider provider = (INamedContainerProvider) world.getBlockEntity(position);
                NetworkHooks.openGui((ServerPlayerEntity) player, provider, position);
                return ActionResultType.SUCCESS;
            }
        }

        return ActionResultType.PASS;
    }

    @Override
    public void playerDestroy(@Nonnull World world,
                              @Nonnull PlayerEntity player,
                              @Nonnull BlockPos position,
                              @Nonnull BlockState state,
                              @Nullable TileEntity tileEntity,
                              @Nonnull ItemStack stack) {
        ItemStack drop = new ItemStack(this);

        if (tileEntity instanceof ComponentBoxBlockEntity) {
            CompoundNBT beTag = ((ComponentBoxBlockEntity) tileEntity).saveToItemStack();
            drop.getOrCreateTag().put("BlockEntityTag", beTag);
        }

        if (!world.isClientSide) {
            ItemEntity itemEntity = new ItemEntity(
                    world,
                    position.getX() + 0.5,
                    position.getY() + 0.5,
                    position.getZ() + 0.5,
                    drop
            );
            world.addFreshEntity(itemEntity);
        }
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return CyberwareBlockEntities.COMPONENT_BOX.get().create();
    }
}
