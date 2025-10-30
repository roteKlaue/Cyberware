package flaxbeard.cyberware.common.network;

import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.ICyberwareUserData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class SyncHudDataPacket {
    private CompoundNBT compoundTag;

    public SyncHudDataPacket(CompoundNBT compoundTag) {
        this.compoundTag = compoundTag;
    }

    public static void handle(SyncHudDataPacket pkt, @Nonnull Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            PlayerEntity player = ctx.get().getSender();
            if (player == null) return;
            if (pkt == null || pkt.compoundTag == null) return;

            LazyOptional<ICyberwareUserData> cyberware = CyberwareAPI.getCyberwareData(player);
            if (!cyberware.isPresent()) return;

            cyberware.orElseThrow(RuntimeException::new).setHudData(pkt.compoundTag);
        });

        ctx.get().setPacketHandled(true);
    }

    public static void encode(SyncHudDataPacket pkt, PacketBuffer buf) {
        if (pkt == null || pkt.compoundTag == null) {
            buf.writeNbt(new CompoundNBT());
            return;
        }
        buf.writeNbt(pkt.compoundTag);
    }

    public static SyncHudDataPacket decode(PacketBuffer buf) {
        CompoundNBT tag = buf.readNbt();
        if (tag == null) tag = new CompoundNBT();
        return new SyncHudDataPacket(tag);
    }
}
