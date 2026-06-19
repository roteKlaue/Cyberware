package flaxbeard.cyberware.common.item;

import flaxbeard.cyberware.common.effect.CyberwarePotionEffects;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectUtils;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

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

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@Nonnull ItemStack itemStack,
                                @Nullable World level,
                                @Nonnull List<ITextComponent> components,
                                @Nonnull ITooltipFlag flag) {
        EffectInstance effectinstance = new EffectInstance(
                CyberwarePotionEffects.NEUROPOZYNE.get(),
                20 * 60 * 20,
                0,
                true,
                false
        );
        IFormattableTextComponent component = new TranslationTextComponent(CyberwarePotionEffects.NEUROPOZYNE.get().getDescriptionId());
        Effect effect = CyberwarePotionEffects.NEUROPOZYNE.get().getEffect();

        if (effectinstance.getDuration() > 20) {
            component = new TranslationTextComponent("potion.withDuration", component, EffectUtils.formatDuration(effectinstance, 1F));
        }

        components.add(component.withStyle(effect.getCategory().getTooltipFormatting()));
    }
}
