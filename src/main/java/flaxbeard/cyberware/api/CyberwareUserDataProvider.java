package flaxbeard.cyberware.api;

import flaxbeard.cyberware.OverclockedOrgans;
import lombok.Getter;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CyberwareUserDataProvider implements ICapabilitySerializable<CompoundNBT> {
    public static final ResourceLocation NAME = new ResourceLocation(
            OverclockedOrgans.MOD_ID,
            "cyberware_userdata"
    );

    /**
     * -- GETTER --
     *  Used internally to access the actual implementation.
     */
    @Getter
    private final CyberwareUserDataImpl backend = new CyberwareUserDataImpl();
    private final LazyOptional<ICyberwareUserData> optional = LazyOptional.of(() -> backend);

    @Override
    public <T> @Nonnull LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == CyberwareAPI.CYBERWARE_CAPABILITY
                ? optional.cast()
                : LazyOptional.empty();
    }

    @Override
    public CompoundNBT serializeNBT() {
        return backend.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        backend.deserializeNBT(nbt);
    }
}
