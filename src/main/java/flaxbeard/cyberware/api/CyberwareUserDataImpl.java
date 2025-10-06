package flaxbeard.cyberware.api;

import flaxbeard.cyberware.OverclockedOrgans;
import flaxbeard.cyberware.api.item.ICyberware;
import flaxbeard.cyberware.api.item.IHudjack;
import flaxbeard.cyberware.api.item.IMenuItem;
import flaxbeard.cyberware.common.CyberwareConfig;
import flaxbeard.cyberware.common.lib.LibConstants;
import flaxbeard.cyberware.common.misc.NNLUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import java.util.*;

public class CyberwareUserDataImpl implements ICyberwareUserData{
    public static final Capability.IStorage<ICyberwareUserData> STORAGE = new CyberwareUserDataStorage();

    private NonNullList<NonNullList<ItemStack>> cyberwaresBySlot = NonNullList.create();
    private boolean[] missingEssentials = new boolean[ICyberware.BodySlot.values().length * 2];

    private int power_stored = 0;
    private int power_production = 0;
    private int power_lastProduction = 0;
    private int power_consumption = 0;
    private int power_lastConsumption = 0;
    private int power_capacity = 0;
    private Map<ItemStack, Integer> power_buffer = new HashMap<>();
    private Map<ItemStack, Integer> power_lastBuffer = new HashMap<>();
    private NonNullList<ItemStack> nnlPowerOutages = NonNullList.create();
    private List<Integer> ticksPowerOutages = new ArrayList<>();
    private int missingEssence = 0;
    private NonNullList<ItemStack> specialBatteries = NonNullList.create();
    private NonNullList<ItemStack> activeItems = NonNullList.create();
    private NonNullList<ItemStack> hudjackItems = NonNullList.create();
    private Map<Integer, ItemStack> hotkeys = new HashMap<>();
    private CompoundNBT hudData;
    private boolean hasOpenedRadialMenu = false;

    private int hudColor = 0x00FFFF;
    private float[] hudColorFloat = new float[] { 0.0F, 1.0F, 1.0F };

    private boolean canGiveOut = true;
    private boolean isImmune = false;

    public CyberwareUserDataImpl() {
        hudData = new CompoundNBT();
        for (ICyberware.BodySlot slot : ICyberware.BodySlot.values()) {
            NonNullList<ItemStack> nnlCyberwaresInSlot = NonNullList.create();
            for (int indexSlot = 0; indexSlot < LibConstants.WARE_PER_SLOT; indexSlot++) {
                nnlCyberwaresInSlot.add(ItemStack.EMPTY);
            }
            cyberwaresBySlot.add(nnlCyberwaresInSlot);
        }
        resetWare(null);
    }

    @Override
    public NonNullList<ItemStack> getInstalledCyberware(ICyberware.BodySlot slot) {
        return cyberwaresBySlot.get(slot.ordinal());
    }

    @Override
    public void setInstalledCyberware(LivingEntity livingEntity, ICyberware.BodySlot slot, List<ItemStack> cyberwaresToInstall) {
        while (cyberwaresToInstall.size() > LibConstants.WARE_PER_SLOT) {
            cyberwaresToInstall.remove(cyberwaresToInstall.size() - 1);
        }
        while (cyberwaresToInstall.size() < LibConstants.WARE_PER_SLOT) {
            cyberwaresToInstall.add(ItemStack.EMPTY);
        }
        setInstalledCyberware(livingEntity, slot, NNLUtil.fromArray(cyberwaresToInstall.toArray(new ItemStack[0])));
    }

    @Override
    public void setInstalledCyberware(LivingEntity livingEntity, ICyberware.BodySlot slot, NonNullList<ItemStack> cyberwaresToInstall) {
        if (cyberwaresToInstall.size() != cyberwaresBySlot.get(slot.ordinal()).size()) {
            OverclockedOrgans.LOGGER.error("Invalid number of cyberware to install: found {}, expecting {}",
                    cyberwaresToInstall.size(),
                    cyberwaresBySlot.get(slot.ordinal()).size());
        }
        NonNullList<ItemStack> cyberwaresInstalled = cyberwaresBySlot.get(slot.ordinal());

        if (livingEntity != null) {
            for (ItemStack itemStackInstalled : cyberwaresInstalled) {
                if (!CyberwareAPI.isCyberware(itemStackInstalled)) continue;

                boolean found = false;
                for (ItemStack itemStackToInstall : cyberwaresToInstall)
                {
                    if ( CyberwareAPI.areCyberwareStacksEqual(itemStackToInstall, itemStackInstalled)
                            && itemStackToInstall.getCount() == itemStackInstalled.getCount() )
                    {
                        found = true;
                        break;
                    }
                }

                if (!found)
                {
                    CyberwareAPI.getCyberware(itemStackInstalled).onRemoved(livingEntity, itemStackInstalled);
                }
            }

            for (ItemStack itemStackToInstall : cyberwaresToInstall)
            {
                if (!CyberwareAPI.isCyberware(itemStackToInstall)) continue;

                boolean found = false;
                for (ItemStack oldWare : cyberwaresInstalled)
                {
                    if ( CyberwareAPI.areCyberwareStacksEqual(itemStackToInstall, oldWare)
                            && itemStackToInstall.getCount() == oldWare.getCount() )
                    {
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    CyberwareAPI.getCyberware(itemStackToInstall).onAdded(livingEntity, itemStackToInstall);
                }
            }
        }

        cyberwaresBySlot.set(slot.ordinal(), cyberwaresToInstall);
    }

    @Override
    public boolean isCyberwareInstalled(ItemStack cyberware) {
        return getCyberwareRank(cyberware) > 0;
    }

    @Override
    public int getCyberwareRank(ItemStack cyberwareTemplate) {
        ItemStack cyberwareFound = getCyberware(cyberwareTemplate);

        if (!cyberwareFound.isEmpty()) {
            return cyberwareFound.getCount();
        }

        return 0;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT tagCompound = new CompoundNBT();
        ListNBT listSlots = new ListNBT();

        for (ICyberware.BodySlot slot : ICyberware.BodySlot.values()) {
            ListNBT listCyberwares = new ListNBT();
            for (ItemStack cyberware : getInstalledCyberware(slot)) {
                CompoundNBT tagCompoundCyberware = new CompoundNBT();
                if (!cyberware.isEmpty()) {
                    cyberware.save(tagCompoundCyberware);
                }
                listCyberwares.add(tagCompoundCyberware);
            }
            listSlots.add(listCyberwares);
        }

        tagCompound.put("cyberware", listSlots);

        ListNBT listEssentials = new ListNBT();
        for (boolean missingEssential : missingEssentials) {
            CompoundNBT b = new CompoundNBT();
            b.putByte("v", (byte) (missingEssential ? 1 : 0));
            listEssentials.add(b);
        }
        tagCompound.put("discard", listEssentials);

        tagCompound.put("powerBuffer", serializeMap(power_buffer));
        tagCompound.put("powerBufferLast", serializeMap(power_lastBuffer));
        tagCompound.putInt("powerCap", power_capacity);
        tagCompound.putInt("storedPower", power_stored);
        tagCompound.putInt("missingEssence", missingEssence);
        tagCompound.put("hud", hudData == null ? new CompoundNBT() : hudData);
        tagCompound.putInt("color", hudColor);
        tagCompound.putBoolean("hasOpenedRadialMenu", hasOpenedRadialMenu);
        return tagCompound;
    }

    private ListNBT serializeMap(@Nonnull Map<ItemStack, Integer> map) {
        ListNBT listMap = new ListNBT();

        for (ItemStack stack : map.keySet()) {
            CompoundNBT tagCompoundEntry = new CompoundNBT();
            tagCompoundEntry.putBoolean("null", stack.isEmpty());
            if (!stack.isEmpty()) {
                CompoundNBT tagCompoundItem = new CompoundNBT();
                stack.save(tagCompoundItem);
                tagCompoundEntry.put("item", tagCompoundItem);
            }
            tagCompoundEntry.putInt("value", map.get(stack));
            listMap.add(tagCompoundEntry);
        }

        return listMap;
    }

    private Map<ItemStack, Integer> deserializeMap(@Nonnull ListNBT listMap) {
        Map<ItemStack, Integer> map = new HashMap<>();
        for (int index = 0; index < listMap.size(); index++) {
            CompoundNBT tagCompoundEntry = listMap.getCompound(index);
            boolean isNull = tagCompoundEntry.getBoolean("null");
            ItemStack stack = ItemStack.EMPTY;
            if (!isNull && tagCompoundEntry.contains("item")) {
                CompoundNBT itemTag = tagCompoundEntry.getCompound("item");
                stack = ItemStack.of(itemTag);
            }

            map.put(stack, tagCompoundEntry.getInt("value"));
        }

        return map;
    }


    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        if (nbt == null) return;

        if (nbt.contains("powerBuffer")) {
            ListNBT l = nbt.getList("powerBuffer", Constants.NBT.TAG_COMPOUND);
            power_buffer = deserializeMap(l);
        } else {
            power_buffer = new HashMap<>();
        }

        power_capacity = nbt.getInt("powerCap");

        if (nbt.contains("powerBufferLast")) {
            ListNBT l2 = nbt.getList("powerBufferLast", Constants.NBT.TAG_COMPOUND);
            power_lastBuffer = deserializeMap(l2);
        } else {
            power_lastBuffer = new HashMap<>();
        }

        power_stored = nbt.getInt("storedPower");

        if (nbt.contains("essence")) {
            missingEssence = getMaxEssence() - nbt.getInt("essence");
        } else {
            missingEssence = nbt.getInt("missingEssence");
        }

        hudData = nbt.contains("hud") ? nbt.getCompound("hud") : new CompoundNBT();
        hasOpenedRadialMenu = nbt.getBoolean("hasOpenedRadialMenu");

        if (nbt.contains("discard", Constants.NBT.TAG_COMPOUND)) {
            ListNBT listEssentials = nbt.getList("discard", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < listEssentials.size() && i < missingEssentials.length; i++) {
                CompoundNBT b = listEssentials.getCompound(i);
                missingEssentials[i] = b.getByte("v") != 0;
            }
        }

        if (nbt.contains("cyberware", Constants.NBT.TAG_LIST)) {
            ListNBT listSlots = nbt.getList("cyberware", Constants.NBT.TAG_LIST);
            for (int indexBodySlot = 0; indexBodySlot < listSlots.size(); indexBodySlot++) {
                ICyberware.BodySlot slot = ICyberware.BodySlot.values()[indexBodySlot];

                ListNBT listCyberwares = (ListNBT) listSlots.get(indexBodySlot);
                NonNullList<ItemStack> nnlCyberwaresOfType = NonNullList.create();
                for (int indexInventorySlot = 0; indexInventorySlot < LibConstants.WARE_PER_SLOT; indexInventorySlot++){
                    nnlCyberwaresOfType.add(ItemStack.EMPTY);
                }

                int countInventorySlots = Math.min(listCyberwares.size(), nnlCyberwaresOfType.size());
                for (int indexInventorySlot = 0; indexInventorySlot < countInventorySlots; indexInventorySlot++) {
                    CompoundNBT itemTag = listCyberwares.getCompound(indexInventorySlot);
                    if (!itemTag.isEmpty()) {
                        nnlCyberwaresOfType.set(indexInventorySlot, ItemStack.of(itemTag));
                    }
                }

                setInstalledCyberware(null, slot, nnlCyberwaresOfType);
            }
        }

        int color = 0x00FFFF;
        if (nbt.contains("color")) {
            color = nbt.getInt("color");
        }
        setHudColor(color);

        updateCapacity();
    }

    @Override
    public boolean hasEssential(ICyberware.BodySlot slot) {
        return !missingEssentials[slot.ordinal() * 2];
    }

    @Override
    public void setHasEssential(ICyberware.BodySlot slot, boolean hasLeft, boolean hasRight) {
        missingEssentials[slot.ordinal() * 2    ] = !hasLeft;
        missingEssentials[slot.ordinal() * 2 + 1] = !hasRight;
    }

    @Override
    public ItemStack getCyberware(ItemStack cyberware) {
        if (!CyberwareAPI.isCyberware(cyberware)) return ItemStack.EMPTY;
        ICyberware ic = CyberwareAPI.getCyberware(cyberware);
        for (ItemStack itemStack : getInstalledCyberware(ic.getSlot())) {
            if (!itemStack.isEmpty()
                    && itemStack.getItem() == cyberware.getItem()
                    && itemStack.getDamageValue() == cyberware.getDamageValue()) {
                return itemStack;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void updateCapacity() {
        power_capacity = 0;
        specialBatteries = NonNullList.create();
        activeItems = NonNullList.create();
        hudjackItems = NonNullList.create();
        hotkeys = new HashMap<>();

        for (ICyberware.BodySlot slot : ICyberware.BodySlot.values()) {
            for (ItemStack itemStackCyberware : getInstalledCyberware(slot)) {
                if (CyberwareAPI.isCyberware(itemStackCyberware)) {
                    ICyberware cyberware = CyberwareAPI.getCyberware(itemStackCyberware);

                    if (cyberware instanceof IMenuItem
                            && ((IMenuItem) cyberware).hasMenu(itemStackCyberware)) {
                        activeItems.add(itemStackCyberware);

                        // TODO: readd HotKey Support
                        // int hotkey = HotkeyHelper.getHotkey(itemStackCyberware);
                        // if (hotkey != -1) {
                        //     hotkeys.put(hotkey, itemStackCyberware);
                        // }
                    }

                    if (cyberware instanceof IHudjack) {
                        hudjackItems.add(itemStackCyberware);
                    }

                    if (cyberware instanceof ISpecialBattery) {
                        specialBatteries.add(itemStackCyberware);
                    } else {
                        power_capacity += cyberware.getCapacity(itemStackCyberware);
                    }
                }
            }
        }

        power_stored = Math.min(power_stored, power_capacity);
    }

    @Override
    public void resetBuffer() {
        canGiveOut = true;
        storePower(power_lastBuffer);
        power_lastBuffer = power_buffer;
        power_buffer = new HashMap<>(power_buffer.size());
        isImmune = false;

        power_lastConsumption = power_consumption;
        power_lastProduction = power_production;
        power_production = 0;
        power_consumption = 0;
    }

    private void storePower(Map<ItemStack, Integer> map) {
        for (ItemStack itemStackSpecialBattery : specialBatteries) {
            ISpecialBattery specialBattery = (ISpecialBattery) CyberwareAPI.getCyberware(itemStackSpecialBattery);
            for (Map.Entry<ItemStack, Integer> entryBuffer : map.entrySet()) {
                int amountBuffer = entryBuffer.getValue();
                int amountTaken = specialBattery.add(itemStackSpecialBattery, entryBuffer.getKey(), amountBuffer, false);
                entryBuffer.setValue(amountBuffer - amountTaken);
            }
        }
        power_stored = Math.min(power_capacity, power_stored + ComputeSum(map));
    }

    @Override
    public void addPower(int amount, ItemStack inputter) {
        if (amount < 0)
        {
            throw new IllegalArgumentException("Amount must be positive!");
        }

        ItemStack stack = inputter;
        if (!inputter.isEmpty()) {
            if (inputter.hasTag() || inputter.getCount() != 1) {
                stack = new ItemStack(inputter.getItem(), 1);
            }
        }

        power_buffer.compute(stack, (k, amountExisting) ->
                amount + (amountExisting == null ? 0 : amountExisting));

        power_production += amount;
    }

    @Override
    public boolean isAtCapacity(ItemStack stack) {
        return isAtCapacity(stack, 0);
    }

    @Override
    public boolean isAtCapacity(ItemStack stack, int buffer) {
        int leftOverSpaceNormal = power_capacity - power_stored;

        if (leftOverSpaceNormal > buffer) return false;

        int leftOverSpaceSpecial = 0;

        for (ItemStack batteryStack : specialBatteries) {
            ISpecialBattery battery = (ISpecialBattery) CyberwareAPI.getCyberware(batteryStack);
            int spaceInThisSpecial = battery.add(batteryStack, stack, buffer + 1, true);
            leftOverSpaceSpecial += spaceInThisSpecial;

            if (leftOverSpaceNormal + leftOverSpaceSpecial > buffer) return false;
        }

        return true;
    }

    @Override
    public float getPercentFull() {
        if (getCapacity() == 0) return -1F;
        return getStoredPower() / (float) getCapacity();
    }

    @Override
    public int getCapacity() {
        int specialCap = 0;
        for (ItemStack item : specialBatteries) {
            ISpecialBattery battery = (ISpecialBattery) CyberwareAPI.getCyberware(item);
            specialCap += battery.getCapacity(item);
        }
        return power_capacity + specialCap;
    }

    @Override
    public int getStoredPower() {
        int specialStored = 0;
        for (ItemStack item : specialBatteries) {
            ISpecialBattery battery = (ISpecialBattery) CyberwareAPI.getCyberware(item);
            specialStored += battery.getStoredEnergy(item);
        }
        return power_stored + specialStored;
    }

    @Override
    public int getProduction() {
        return power_lastProduction;
    }

    @Override
    public int getConsumption() {
        return power_lastConsumption;
    }

    @Override
    public boolean usePower(ItemStack stack, int amount) {
        return usePower(stack, amount, true);
    }

    @Override
    public List<ItemStack> getPowerOutages() {
        return nnlPowerOutages;
    }

    @Override
    public List<Integer> getPowerOutageTimes() {
        return ticksPowerOutages;
    }

    @Override
    public void setImmune() {
        isImmune = true;
    }

    @Override
    public boolean usePower(ItemStack stack, int amount, boolean isPassive) {
        if (isImmune) return true;

        if (!canGiveOut) {
            if (Minecraft.getInstance().player != null &&
                    !Minecraft.getInstance().isPaused()) {
                setOutOfPower(stack);
            }
            return false;
        }

        power_consumption += amount;

        int sumPowerBufferLast = ComputeSum(power_lastBuffer);
        int amountAvailable = power_stored + sumPowerBufferLast;

        int amountAvailableSpecial = 0;
        if (amountAvailable < amount) {
            int amountMissing = amount - amountAvailable;

            for (ItemStack batteryStack : specialBatteries) {
                ISpecialBattery battery = (ISpecialBattery) CyberwareAPI.getCyberware(batteryStack);
                int extract = battery.extract(batteryStack, amountMissing, true);

                amountMissing -= extract;
                amountAvailableSpecial += extract;

                if (amountMissing <= 0) break;
            }

            if (amountAvailableSpecial + amountAvailable >= amount) {
                amountMissing = amount - amountAvailable;

                for (ItemStack batteryStack : specialBatteries) {
                    ISpecialBattery battery = (ISpecialBattery) CyberwareAPI.getCyberware(batteryStack);
                    int extract = battery.extract(batteryStack, amountMissing, false);

                    amountMissing -= extract;

                    if (amountMissing <= 0) break;
                }

                amount -= amountAvailableSpecial;
            }
        }

        if (amountAvailable < amount) {
            if (Minecraft.getInstance().player != null) {
                setOutOfPower(stack);
            }
            if (isPassive) {
                canGiveOut = false;
            }
            return false;
        }

        int leftAfterBuffer = Math.max(0, amount - sumPowerBufferLast);
        subtractFromBufferLast(amount);
        power_stored -= leftAfterBuffer;
        return true;
    }

    private int ComputeSum(@Nonnull Map<ItemStack, Integer> map) {
        int total = 0;
        for (ItemStack key : map.keySet()) {
            Integer v = map.get(key);
            if (v != null) total += v;
        }
        return total;
    }

    private void subtractFromBufferLast(int amount) {
        for (ItemStack key : power_lastBuffer.keySet()) {
            int get = power_lastBuffer.get(key);
            int amountToSubtract = Math.min(get, amount);
            amount -= amountToSubtract;
            power_lastBuffer.put(key, get - amountToSubtract);
            if (amount <= 0) break;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void setOutOfPower(ItemStack stack)
    {
        PlayerEntity entityPlayer = Minecraft.getInstance().player;
        if (entityPlayer != null
                && !stack.isEmpty()) {
            int indexFound = -1;
            int indexLoop = 0;
            for (ItemStack stackExisting : nnlPowerOutages) {
                if (!stackExisting.isEmpty()
                        && stackExisting.getItem() == stack.getItem()
                        && stackExisting.getDamageValue() == stack.getDamageValue()) {
                    indexFound = indexLoop;
                    break;
                }
                indexLoop++;
            }
            if (indexFound != -1) {
                nnlPowerOutages.remove(indexFound);
                ticksPowerOutages.remove(indexFound);
            }
            nnlPowerOutages.add(stack);
            ticksPowerOutages.add(entityPlayer.tickCount);
            if (nnlPowerOutages.size() >= 8) {
                nnlPowerOutages.remove(0);
                ticksPowerOutages.remove(0);
            }
        }
    }

    @Override
    public boolean hasEssential(ICyberware.BodySlot slot, ICyberware.ISidedLimb.EnumSide side) {
        return !missingEssentials[slot.ordinal() * 2 + (side == ICyberware.ISidedLimb.EnumSide.LEFT ? 0 : 1)];
    }

    @Override
    public void resetWare(LivingEntity livingEntity) {
        for (NonNullList<ItemStack> nnlCyberwaresInSlot : cyberwaresBySlot) {
            for (ItemStack item : nnlCyberwaresInSlot) {
                if (CyberwareAPI.isCyberware(item)) {
                    CyberwareAPI.getCyberware(item).onRemoved(livingEntity, item);
                }
            }
        }
        missingEssence = 0;
        for (ICyberware.BodySlot slot : ICyberware.BodySlot.values()) {
            NonNullList<ItemStack> nnlCyberwaresInSlot = NonNullList.create();
            // TODO: reimplement starting items
            // NonNullList<ItemStack> startItems = CyberwareConfig.getStartingItems(slot);
            // for (ItemStack startItem : startItems)
            // {
            //     nnlCyberwaresInSlot.add(startItem.copy());
            // }
            cyberwaresBySlot.set(slot.ordinal(), nnlCyberwaresInSlot);
        }
        missingEssentials = new boolean[ICyberware.BodySlot.values().length * 2];
        updateCapacity();
    }

    @Override
    public int getNumActiveItems() {
        return activeItems.size();
    }

    @Override
    public List<ItemStack> getActiveItems() {
        return activeItems;
    }

    @Override
    public void removeHotkey(int i) {
        hotkeys.remove(i);
    }

    @Override
    public void addHotkey(int i, ItemStack stack) {
        hotkeys.put(i, stack);
    }

    @Override
    public ItemStack getHotkey(int i) {
        return hotkeys.getOrDefault(i, ItemStack.EMPTY);
    }

    @Override
    public Iterable<Integer> getHotkeys() {
        return hotkeys.keySet();
    }

    @Override
    public List<ItemStack> getHudjackItems() {
        return hudjackItems;
    }

    @Override
    public void setHudData(CompoundNBT tagCompound) {
        hudData = tagCompound;
    }

    @Override
    public CompoundNBT getHudData() {
        return hudData;
    }

    @Override
    public boolean hasOpenedRadialMenu() {
        return hasOpenedRadialMenu;
    }

    @Override
    public void setOpenedRadialMenu(boolean hasOpenedRadialMenu) {
        this.hasOpenedRadialMenu = hasOpenedRadialMenu;
    }

    @Override
    public void setHudColor(int color) {
        float r = ((color >> 16) & 0x0000FF) / 255F;
        float g = ((color >> 8) & 0x0000FF) / 255F;
        float b = ((color) & 0x0000FF) / 255F;
        setHudColor(new float[] { r, g, b });
    }

    @Override
    public void setHudColor(float[] color) {
        hudColorFloat = color;
        int ri = Math.round(color[0] * 255);
        int gi = Math.round(color[1] * 255);
        int bi = Math.round(color[2] * 255);

        int rp = (ri << 16) & 0xFF0000;
        int gp = (gi << 8) & 0x00FF00;
        int bp = (bi) & 0x0000FF;
        hudColor = rp | gp | bp;
    }

    @Override
    public int getHudColorHex() {
        return hudColor;
    }

    @Override
    public float[] getHudColor() {
        return hudColorFloat;
    }

    @Override
    public int getMaxTolerance(@Nonnull LivingEntity livingEntity) {
        ModifiableAttributeInstance inst = livingEntity.getAttribute(CyberwareAPI.TOLERANCE_ATTR);
        if (inst == null) return getMaxEssence();
        return (int) inst.getValue();
    }

    @Override
    public void setTolerance(@Nonnull LivingEntity livingEntity, int amount) {
        missingEssence = getMaxTolerance(livingEntity) - amount;
    }

    @Override
    public int getTolerance(@Nonnull LivingEntity livingEntity) {
        return getMaxTolerance(livingEntity) - missingEssence;
    }

    @Override
    public int getEssence() {
        return getMaxEssence() - missingEssence;
    }

    @Override
    public void setEssence(int essence) {
        missingEssence = getMaxEssence() - essence;
    }

    @Override
    public int getMaxEssence() {
        return CyberwareConfig.ESSENCE.get();
    }

    private static class CyberwareUserDataStorage implements Capability.IStorage<ICyberwareUserData> {
        @Override
        public INBT writeNBT(Capability<ICyberwareUserData> capability, ICyberwareUserData cyberwareUserData, Direction side) {
            return cyberwareUserData.serializeNBT();
        }

        @Override
        public void readNBT(Capability<ICyberwareUserData> capability, ICyberwareUserData cyberwareUserData, Direction side, INBT nbt) {
            if (!(nbt instanceof CompoundNBT)) throw new IllegalStateException("Cyberware NBT should be a CompoundNBT!");
            cyberwareUserData.deserializeNBT((CompoundNBT) nbt);
        }
    }
}
