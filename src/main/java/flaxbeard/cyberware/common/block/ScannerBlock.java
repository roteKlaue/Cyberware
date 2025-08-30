package flaxbeard.cyberware.common.block;

import flaxbeard.cyberware.common.block.entities.BlueprintArchiveBlockEntity;
import flaxbeard.cyberware.common.block.entities.CyberwareBlockEntities;
import flaxbeard.cyberware.common.block.entities.ScannerBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ScannerBlock extends DirectionalBlock {
    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 15, 16);

    public ScannerBlock() {
        super(Properties.copy(Blocks.IRON_BLOCK));
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return CyberwareBlockEntities.SCANNER.get().create();
    }

    @Override
    public void setPlacedBy(@Nonnull World world,
                            @Nonnull BlockPos pos,
                            @Nonnull BlockState state,
                            @Nullable LivingEntity placer,
                            @Nonnull ItemStack stack) {
        if (stack.hasCustomHoverName()) {
            TileEntity tile = world.getBlockEntity(pos);
            if (tile instanceof ScannerBlockEntity) {
                ((ScannerBlockEntity) tile).setCustomInventoryName(stack.getHoverName());
            }
        }
    }

    @Override
    @Nonnull
    @SuppressWarnings("deprecation")
    public ItemStack getCloneItemStack(IBlockReader world, @Nonnull BlockPos pos, @Nonnull BlockState state) {
        TileEntity tile = world.getBlockEntity(pos);
        if (tile instanceof ScannerBlockEntity) {
            ItemStack stack = new ItemStack(this);
            ITextComponent name = ((ScannerBlockEntity) tile).getDisplayName();
            if (tile instanceof ScannerBlockEntity && !(name instanceof TranslationTextComponent)) {
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
        if (world.isClientSide) return ActionResultType.PASS;

        if (world.getBlockEntity(position) instanceof INamedContainerProvider) {
            INamedContainerProvider provider = (INamedContainerProvider) world.getBlockEntity(position);
            NetworkHooks.openGui((ServerPlayerEntity) player, provider, position);
            return ActionResultType.SUCCESS;
        }

        return ActionResultType.PASS;
    }

    @Override
    @Nonnull
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(@Nonnull BlockState state,
                               @Nonnull IBlockReader worldIn,
                               @Nonnull BlockPos pos,
                               @Nonnull ISelectionContext context) {
        return SHAPE;
    }
}
