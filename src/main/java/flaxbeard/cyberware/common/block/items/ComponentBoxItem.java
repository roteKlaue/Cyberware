package flaxbeard.cyberware.common.block.items;

import flaxbeard.cyberware.OverclockedOrgans;
import flaxbeard.cyberware.client.gui.ComponentBoxContainer;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import java.util.Objects;

public class ComponentBoxItem extends BlockItem {
    public ComponentBoxItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    @Nonnull
    public ActionResultType useOn(@Nonnull ItemUseContext context) {
        if (!(context.getPlayer() != null
                && context.getPlayer().isShiftKeyDown())) {
            return ActionResultType.PASS;
        }
        return super.useOn(context);
    }

    @Override
    @Nonnull
    public ActionResult<ItemStack> use(@Nonnull World world,
                                       @Nonnull PlayerEntity player,
                                       @Nonnull Hand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (world.isClientSide) {
            return ActionResult.success(stack);
        }

        if (player instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;

            INamedContainerProvider provider = new INamedContainerProvider() {
                @Override
                @Nonnull
                public ITextComponent getDisplayName() {
                    if (stack.hasCustomHoverName()) {
                        return stack.getHoverName();
                    }

                    CompoundNBT tag = stack.getTag();
                    if (tag != null && tag.contains("BlockEntityTag")) {
                        CompoundNBT beTag = tag.getCompound("BlockEntityTag");
                        if (beTag.contains("CustomName", 8)) {
                            return Objects.requireNonNull(ITextComponent.Serializer.fromJson(beTag.getString("CustomName")));
                        }
                    }

                    return new TranslationTextComponent("container." + OverclockedOrgans.MOD_ID + ".component_box");
                }

                @Override
                public Container createMenu(int id, @Nonnull PlayerInventory playerInventory, @Nonnull PlayerEntity playerEntity) {
                    CompoundNBT tag = stack.getTag();
                    CompoundNBT beTag = tag == null ? new CompoundNBT() : tag.getCompound("BlockEntityTag");
                    return new ComponentBoxContainer(id, playerInventory, beTag);
                }
            };

            NetworkHooks.openGui(serverPlayer, provider, buf -> {
                CompoundNBT tag = stack.getTag();
                CompoundNBT beTag = tag == null ? new CompoundNBT() : tag.getCompound("BlockEntityTag");
                buf.writeNbt(beTag);
            });
            return ActionResult.consume(stack);
        }

        return ActionResult.pass(stack);
    }
}
