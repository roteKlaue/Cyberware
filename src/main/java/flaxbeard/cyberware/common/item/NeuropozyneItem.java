package flaxbeard.cyberware.common.item;

import flaxbeard.cyberware.common.effect.CyberwarePotionEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class NeuropozyneItem extends Item {
    public NeuropozyneItem(ItemGroup group) {
        super(new Properties().tab(group));
    }

    @Override
    @Nonnull
    public UseAction getUseAnimation(@Nonnull ItemStack stack) {
        return UseAction.BLOCK;
    }

    @Override
    public int getUseDuration(@Nonnull ItemStack stack) {
        return 6;
    }

    @Override
    @Nonnull
    public ActionResult<ItemStack> use(@Nonnull World world, PlayerEntity player, @Nonnull Hand hand) {
        ItemStack stack = player.getItemInHand(hand);

        player.startUsingItem(hand);

        if (!world.isClientSide) {
            player.addEffect(new EffectInstance(
                    CyberwarePotionEffects.NEUROPOZYNE.get(),
                    20 * 60 * 20,
                    0,
                    true,
                    false
            ));

            if (!player.abilities.instabuild) {
                stack.shrink(1);
            }
        }

        return ActionResult.consume(stack);
    }
}
