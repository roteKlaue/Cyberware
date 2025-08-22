package flaxbeard.cyberware.common.block;

import flaxbeard.cyberware.common.block.entities.BlueprintArchiveBlockEntity;
import flaxbeard.cyberware.common.block.entities.CyberwareBlockEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlueprintArchiveBlock extends Block {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public BlueprintArchiveBlock() {
        super(Properties.copy(Blocks.IRON_BLOCK).sound(SoundType.STONE));
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return CyberwareBlockEntities.BLUEPRINT_ARCHIVE.get().create();
    }

    @Override
    public void setPlacedBy(@Nonnull World world,
                            @Nonnull BlockPos pos,
                            @Nonnull BlockState state,
                            @Nullable LivingEntity placer,
                            @Nonnull ItemStack stack) {
        if (stack.hasCustomHoverName()) {
            TileEntity tile = world.getBlockEntity(pos);
            if (tile instanceof BlueprintArchiveBlockEntity) {
                ((BlueprintArchiveBlockEntity) tile).setCustomName(stack.getHoverName());
            }
        }
    }

    @Override
    @Nonnull
    @SuppressWarnings("deprecation")
    public ItemStack getCloneItemStack(IBlockReader world, @Nonnull BlockPos pos, @Nonnull BlockState state) {
        TileEntity tile = world.getBlockEntity(pos);
        if (tile instanceof BlueprintArchiveBlockEntity) {
            ItemStack stack = new ItemStack(this);
            ITextComponent name = ((BlueprintArchiveBlockEntity) tile).getDisplayName();
            if (tile instanceof BlueprintArchiveBlockEntity && !(name instanceof TranslationTextComponent)) {
                stack.setHoverName(name);
            }
            return stack;
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
        if (!world.isClientSide) {
            if (world.getBlockEntity(position) instanceof INamedContainerProvider) {
                INamedContainerProvider provider = (INamedContainerProvider) world.getBlockEntity(position);
                NetworkHooks.openGui((ServerPlayerEntity) player, provider, position);
                return ActionResultType.SUCCESS;
            }
        }

        return ActionResultType.PASS;
    }
}
