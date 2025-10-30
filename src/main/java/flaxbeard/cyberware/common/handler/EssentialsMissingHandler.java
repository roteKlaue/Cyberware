package flaxbeard.cyberware.common.handler;

import com.google.common.collect.HashMultimap;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import flaxbeard.cyberware.OverclockedOrgans;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.CyberwareUpdateEvent;
import flaxbeard.cyberware.api.ICyberwareUserData;
import flaxbeard.cyberware.api.item.ICyberware;
import flaxbeard.cyberware.common.CyberwareConfig;
import flaxbeard.cyberware.common.effect.CyberwarePotionEffects;
import flaxbeard.cyberware.common.item.CyberlimbItem;
import flaxbeard.cyberware.common.item.CyberwareItems;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EssentialsMissingHandler {
    public static final DamageSource BRAINLESS = new DamageSource("overclockedorgans.brainless")
            .bypassArmor()
            .bypassMagic()
            .bypassInvul();

    public static final DamageSource HEARTLESS = new DamageSource("overclockedorgans.heartless")
            .bypassArmor()
            .bypassMagic()
            .bypassInvul();

    public static final DamageSource SURGERY = new DamageSource("overclockedorgans.surgery")
            .bypassArmor();

    public static final DamageSource SPINELESS = new DamageSource("overclockedorgans.spineless")
            .bypassArmor()
            .bypassMagic()
            .bypassInvul();

    public static final DamageSource NOMUSCLES = new DamageSource("overclockedorgans.nomuscles")
            .bypassArmor()
            .bypassMagic()
            .bypassInvul();

    public static final DamageSource NOESSENCE = new DamageSource("overclockedorgans.noessence")
            .bypassArmor()
            .bypassMagic()
            .bypassInvul();

    public static final DamageSource LOWESSENCE = new DamageSource("overclockedorgans.lowessence")
            .bypassArmor()
            .bypassMagic()
            .bypassInvul();

    public static final EssentialsMissingHandler INSTANCE = new EssentialsMissingHandler();
    public static final ResourceLocation BLACK_PX = new ResourceLocation(OverclockedOrgans.MOD_ID + ":textures/gui/blackpx.png");

    private static final Map<Integer, Integer> timesLungs = new HashMap<>();
    private static final UUID idMissingLegSpeedAttribute = UUID.fromString("fe00fdea-5044-11e6-beb8-9e71128cae77");
    private static final HashMultimap<Attribute, AttributeModifier> multimapMissingLegSpeedAttribute;

    static {
        multimapMissingLegSpeedAttribute = HashMultimap.create();
        multimapMissingLegSpeedAttribute.put(Attributes.MOVEMENT_SPEED,
                new AttributeModifier(idMissingLegSpeedAttribute, "Missing leg speed", -100F, AttributeModifier.Operation.ADDITION));
    }

    private final Map<Integer, Boolean> last = new HashMap<>();
    private final Map<Integer, Boolean> lastClient = new HashMap<>();
    private final static Map<Integer, Integer> mapHunger = new HashMap<>();
    private final static Map<Integer, Float> mapSaturation = new HashMap<>();

    public static void register() {
        MinecraftForge.EVENT_BUS.register(INSTANCE);
    }

    @SubscribeEvent
    public void triggerCyberwareEvent(LivingEvent.LivingUpdateEvent event) {
        LivingEntity livingEntity = event.getEntityLiving();

        LazyOptional<ICyberwareUserData> cyberwareUserData = CyberwareAPI.getCyberwareData(livingEntity);
        if (cyberwareUserData.isPresent()) {
            CyberwareUpdateEvent cyberwareUpdateEvent = new CyberwareUpdateEvent(livingEntity, cyberwareUserData.orElseThrow(AssertionError::new));
            MinecraftForge.EVENT_BUS.post(cyberwareUpdateEvent);
        }
    }

    @SubscribeEvent(priority= EventPriority.LOWEST)
    public void handleMissingEssentials(CyberwareUpdateEvent event) {
        LivingEntity livingEntity = (LivingEntity) event.getEntity();
        ICyberwareUserData cyberwareUserData = event.getCyberwareUserData();

        if (livingEntity.tickCount % 20 == 0) {
            cyberwareUserData.resetBuffer();
        }

        if (!cyberwareUserData.hasEssential(ICyberware.BodySlot.CRANIUM)) {
            livingEntity.hurt(BRAINLESS, Integer.MAX_VALUE);
        }

        if (livingEntity instanceof PlayerEntity
                && livingEntity.tickCount % 20 == 0) {
            int tolerance = cyberwareUserData.getTolerance(livingEntity);

            if (tolerance <= 0) {
                livingEntity.hurt(NOESSENCE, Integer.MAX_VALUE);
            }

            if (tolerance < CyberwareConfig.CRITICAL_ESSENCE.get()
                    && livingEntity.tickCount % 100 == 0
                    && !livingEntity.hasEffect(CyberwarePotionEffects.NEUROPOZYNE.get())) {
                livingEntity.addEffect(new EffectInstance(CyberwarePotionEffects.REJECTION.get(), 110, 0, true, false));
                livingEntity.hurt(LOWESSENCE, 2F);
            }

            if (!cyberwareUserData.hasEssential(ICyberware.BodySlot.EYES)) {
                livingEntity.addEffect(new EffectInstance(Effects.BLINDNESS, 40));
            }
        }

        int numMissingLegs = 0;
        int numMissingLegsVisible = 0;

        if (!cyberwareUserData.hasEssential(ICyberware.BodySlot.LEG, ICyberware.ISidedLimb.EnumSide.LEFT)) {
            numMissingLegs++;
            numMissingLegsVisible++;
        }
        if (!cyberwareUserData.hasEssential(ICyberware.BodySlot.LEG, ICyberware.ISidedLimb.EnumSide.RIGHT)) {
            numMissingLegs++;
            numMissingLegsVisible++;
        }

        numMissingLegs = getNumMissingLegs(cyberwareUserData, numMissingLegs);

        if (livingEntity instanceof PlayerEntity) {
            if (numMissingLegsVisible == 2) {
                AxisAlignedBB axisalignedbb = livingEntity.getBoundingBox();
                livingEntity.setBoundingBox(new AxisAlignedBB(
                        axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ,
                        axisalignedbb.minX + livingEntity.getBbWidth(),
                        axisalignedbb.minY + 1.8F - (10F / 16F),
                        axisalignedbb.minZ + livingEntity.getBbWidth()));

                if (livingEntity.level.isClientSide) {
                    lastClient.put(livingEntity.getId(), true);
                } else {
                    last.put(livingEntity.getId(), true);
                }
            } else if (last(livingEntity.level.isClientSide, livingEntity)) {
                AxisAlignedBB axisalignedbb = livingEntity.getBoundingBox();
                livingEntity.setBoundingBox(new AxisAlignedBB(
                        axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ,
                        axisalignedbb.minX + livingEntity.getBbWidth(),
                        axisalignedbb.minY + 1.8,
                        axisalignedbb.minZ + livingEntity.getBbWidth() ));

                if (livingEntity.level.isClientSide) {
                    lastClient.put(livingEntity.getId(), false);
                } else {
                    last.put(livingEntity.getId(), false);
                }
            }
        }

        if (numMissingLegs >= 1
                && livingEntity.isOnGround()) {
            livingEntity.getAttributes().addTransientAttributeModifiers(multimapMissingLegSpeedAttribute);
        } else if ( numMissingLegs >= 1
                || livingEntity.tickCount % 20 == 0) {
            livingEntity.getAttributes().removeAttributeModifiers(multimapMissingLegSpeedAttribute);
        }

        if (!cyberwareUserData.hasEssential(ICyberware.BodySlot.HEART)) {
            livingEntity.hurt(HEARTLESS, Integer.MAX_VALUE);
        }

        if (!cyberwareUserData.hasEssential(ICyberware.BodySlot.BONE)) {
            livingEntity.hurt(SPINELESS, Integer.MAX_VALUE);
        }

        if (!cyberwareUserData.hasEssential(ICyberware.BodySlot.MUSCLE)) {
            livingEntity.hurt(NOMUSCLES, Integer.MAX_VALUE);
        }

        if (!cyberwareUserData.hasEssential(ICyberware.BodySlot.LUNGS)) {
            if (getLungsTime(livingEntity) >= 20) {
                timesLungs.put(livingEntity.getId(), livingEntity.tickCount);
                livingEntity.hurt(DamageSource.DROWN, 2F);
            }
        } else if (livingEntity.tickCount % 20 == 0) {
            timesLungs.remove(livingEntity.getId());
        }
    }

    private int getLungsTime(@Nonnull LivingEntity livingEntity) {
        Integer timeLungs = timesLungs.computeIfAbsent(livingEntity.getId(), k -> livingEntity.tickCount);
        return livingEntity.tickCount - timeLungs;
    }

    private boolean last(boolean remote, @Nonnull LivingEntity livingEntity) {
        if (remote) {
            if (!lastClient.containsKey(livingEntity.getId())) {
                lastClient.put(livingEntity.getId(), false);
            }
            return lastClient.get(livingEntity.getId());
        } else {
            if (!last.containsKey(livingEntity.getId())) {
                last.put(livingEntity.getId(), false);
            }
            return last.get(livingEntity.getId());
        }
    }

    @SubscribeEvent
    public void handleJump(LivingEvent.LivingJumpEvent event) {
        LivingEntity livingEntity = event.getEntityLiving();

        LazyOptional<ICyberwareUserData> dataLazyOptional = CyberwareAPI.getCyberwareData(livingEntity);
        if (!dataLazyOptional.isPresent()) return;
        ICyberwareUserData cyberwareUserData = dataLazyOptional.orElseThrow(AssertionError::new);

        int numMissingLegs = 0;

        if (!cyberwareUserData.hasEssential(ICyberware.BodySlot.LEG, ICyberware.ISidedLimb.EnumSide.LEFT)) {
            numMissingLegs++;
        }
        if (!cyberwareUserData.hasEssential(ICyberware.BodySlot.LEG, ICyberware.ISidedLimb.EnumSide.RIGHT)) {
            numMissingLegs++;
        }

        numMissingLegs = getNumMissingLegs(cyberwareUserData, numMissingLegs);

        if (numMissingLegs == 2) {
            livingEntity.setDeltaMovement(livingEntity.getDeltaMovement().x, 0.2F, livingEntity.getDeltaMovement().z);
        }
    }

    private int getNumMissingLegs(ICyberwareUserData cyberwareUserData, int numMissingLegs) {
        ItemStack legLeft = cyberwareUserData.getCyberware(new ItemStack(CyberwareItems.CYBER_LEG_LEFT.get()));
        if (!legLeft.isEmpty() && !CyberlimbItem.isPowered(legLeft)) {
            numMissingLegs++;
        }

        ItemStack legRight = cyberwareUserData.getCyberware(new ItemStack(CyberwareItems.CYBER_LEG_RIGHT.get()));
        if (!legRight.isEmpty() && !CyberlimbItem.isPowered(legRight)) {
            numMissingLegs++;
        }
        return numMissingLegs;
    }

    @SubscribeEvent
    public void handleEatFoodTick(LivingEntityUseItemEvent.Tick event) {
        LivingEntity livingEntity = event.getEntityLiving();
        ItemStack stack = event.getItem();

        if (livingEntity == null) return;

        if (livingEntity instanceof PlayerEntity
                && !stack.isEmpty()
                && stack.getUseAnimation() == UseAction.EAT) {
            PlayerEntity entityPlayer = (PlayerEntity) livingEntity;
            LazyOptional<ICyberwareUserData> dataLazyOptional = CyberwareAPI.getCyberwareData(livingEntity);
            if (!dataLazyOptional.isPresent()) return;
            ICyberwareUserData cyberwareUserData = dataLazyOptional.orElseThrow(AssertionError::new);

            if (!cyberwareUserData.hasEssential(ICyberware.BodySlot.LOWER_ORGANS)) {
                mapHunger.put(entityPlayer.getId(), entityPlayer.getFoodData().getFoodLevel());
                mapSaturation.put(entityPlayer.getId(), entityPlayer.getFoodData().getSaturationLevel());
                return;
            }
        }

        mapHunger.remove(livingEntity.getId());
        mapSaturation.remove(livingEntity.getId());
    }

    @SubscribeEvent
    public void handleEatFoodEnd(LivingEntityUseItemEvent.Finish event) {
        LivingEntity livingEntity = event.getEntityLiving();
        ItemStack stack = event.getItem();

        if (!(livingEntity instanceof PlayerEntity) || stack.isEmpty() || stack.getUseAnimation() != UseAction.EAT)
            return;

        PlayerEntity player = (PlayerEntity) livingEntity;
        LazyOptional<ICyberwareUserData> dataLazyOptional = CyberwareAPI.getCyberwareData(livingEntity);
        if (!dataLazyOptional.isPresent()) return;
        ICyberwareUserData cyberwareUserData = dataLazyOptional.orElseThrow(AssertionError::new);
        if (cyberwareUserData.hasEssential(ICyberware.BodySlot.LOWER_ORGANS))
            return;

        Integer hunger = mapHunger.get(player.getId());
        Float saturation = mapSaturation.get(player.getId());
        if (hunger == null || saturation == null)
            return;

        FoodStats stats = player.getFoodData();
        stats.setFoodLevel(hunger);
        stats.setSaturation(saturation);

        if (!player.level.isClientSide) {
            player.containerMenu.broadcastChanges();
        }

        mapHunger.remove(player.getId());
        mapSaturation.remove(player.getId());
    }

    @SubscribeEvent
    public void handleMissingSkin(LivingHurtEvent event) {
        LivingEntity livingEntity = event.getEntityLiving();

        LazyOptional<ICyberwareUserData> dataLazyOptional = CyberwareAPI.getCyberwareData(livingEntity);
        if (!dataLazyOptional.isPresent()) return;
        ICyberwareUserData cyberwareUserData = dataLazyOptional.orElseThrow(AssertionError::new);

        if (!cyberwareUserData.hasEssential(ICyberware.BodySlot.SKIN)
                && (!event.getSource().isBypassArmor()
                || event.getSource() == DamageSource.FALL)) {
            event.setAmount(event.getAmount() * 3F);
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void overlayPre(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START
                && Minecraft.getInstance().player != null) {
            PlayerEntity entityPlayer = Minecraft.getInstance().player;
            entityPlayer.getAttributes().removeAttributeModifiers(multimapMissingLegSpeedAttribute);
        }
    }

    @SubscribeEvent
    public void handleEntityInteract(PlayerInteractEvent.EntityInteract event) {
        callProcessEvent(event);
    }

    @SubscribeEvent
    public void handleLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        callProcessEvent(event);
    }

    @SubscribeEvent
    public void handleRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        callProcessEvent(event);
    }

    @SubscribeEvent
    public void handleRightClickItem(PlayerInteractEvent.RightClickItem event) {
        callProcessEvent(event);
    }

    private void callProcessEvent(PlayerInteractEvent event) {
        LivingEntity livingEntity = event.getEntityLiving();

        LazyOptional<ICyberwareUserData> dataLazyOptional = CyberwareAPI.getCyberwareData(livingEntity);
        if (!dataLazyOptional.isPresent()) return;
        ICyberwareUserData cyberwareUserData = dataLazyOptional.orElseThrow(AssertionError::new);

        processEvent(event, event.getHand(), event.getPlayer(), cyberwareUserData);
    }

    private void processEvent(Event event, Hand hand, PlayerEntity playerEntity, ICyberwareUserData cyberwareUserData) {
        HandSide mainHand = playerEntity.getMainArm();
        HandSide offHand = ((mainHand == HandSide.LEFT) ? HandSide.RIGHT : HandSide.LEFT);
        ICyberware.ISidedLimb.EnumSide correspondingMainHand = ((mainHand == HandSide.RIGHT) ?
                ICyberware.ISidedLimb.EnumSide.RIGHT : ICyberware.ISidedLimb.EnumSide.LEFT);
        ICyberware.ISidedLimb.EnumSide correspondingOffHand = ((offHand == HandSide.RIGHT) ?
                ICyberware.ISidedLimb.EnumSide.RIGHT : ICyberware.ISidedLimb.EnumSide.LEFT);

        boolean leftUnpowered = false;
        ItemStack armLeft = cyberwareUserData.getCyberware(new ItemStack(CyberwareItems.CYBER_ARM_LEFT.get()));
        if (!armLeft.isEmpty() && !CyberlimbItem.isPowered(armLeft)) {
            leftUnpowered = true;
        }

        boolean rightUnpowered = false;
        ItemStack armRight = cyberwareUserData.getCyberware(new ItemStack(CyberwareItems.CYBER_ARM_RIGHT.get()));
        if (!armRight.isEmpty() && !CyberlimbItem.isPowered(armRight)) {
            rightUnpowered = true;
        }

        if (hand == Hand.MAIN_HAND && (!cyberwareUserData.hasEssential(ICyberware.BodySlot.ARM, correspondingMainHand) || leftUnpowered)) {
            event.setCanceled(true);
        }
        else if (hand == Hand.OFF_HAND && (!cyberwareUserData.hasEssential(ICyberware.BodySlot.ARM, correspondingOffHand) || rightUnpowered)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void overlayPre(RenderGameOverlayEvent.Pre event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;
        Minecraft minecraft = Minecraft.getInstance();

        PlayerEntity player = minecraft.player;
        if (player == null) return;

        LazyOptional<ICyberwareUserData> dataLazyOptional = CyberwareAPI.getCyberwareData(player);
        if (!dataLazyOptional.isPresent()) return;
        ICyberwareUserData cyberwareUserData = dataLazyOptional.orElseThrow(AssertionError::new);

        if (!cyberwareUserData.hasEssential(ICyberware.BodySlot.EYES) && !player.isCreative()) {
            MatrixStack matrixStack = event.getMatrixStack();

            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 0.9F);
            minecraft.getTextureManager().bind(BLACK_PX);

            AbstractGui.blit(
                    matrixStack,
                    0, 0,
                    0, 0,
                    minecraft.getWindow().getWidth(),
                    minecraft.getWindow().getHeight(),
                    minecraft.getWindow().getWidth(),
                    minecraft.getWindow().getHeight()
            );

            RenderSystem.disableBlend();
        }
            // TODO: reimplement later
            // if (TileEntitySurgery.workingOnPlayer)
            // {
            //     float trans = 1.0F;
            //     float ticks = TileEntitySurgery.playerProgressTicks + event.getPartialTicks();
            //     if (ticks < 20F)
            //     {
            //         trans = ticks / 20F;
            //     }
            //     else if (ticks > 60F)
            //     {
            //         trans = (80F - ticks) / 20F;
            //     }
            //     GlStateManager.enableBlend();
            //     GlStateManager.color(1.0F, 1.0F, 1.0F, trans);
            //     Minecraft.getMinecraft().getTextureManager().bindTexture(BLACK_PX);
            //     ClientUtils.drawTexturedModalRect(0, 0, 0, 0, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
            //     GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            //     GlStateManager.disableBlend();
            // }
    }
}
