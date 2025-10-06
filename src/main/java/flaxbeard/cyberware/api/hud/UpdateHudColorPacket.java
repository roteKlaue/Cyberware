package flaxbeard.cyberware.api.hud;

import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.ICyberwareUserData;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class UpdateHudColorPacket {
    private final int color;

    public UpdateHudColorPacket(int color) {
        this.color = color;
    }

    public static UpdateHudColorPacket decode(PacketBuffer buf) {
        return new UpdateHudColorPacket(buf.readInt());
    }

    public static void encode(UpdateHudColorPacket pkt, PacketBuffer buf) {
        buf.writeInt(pkt.color);
    }

    public static void handle(UpdateHudColorPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity player = ctx.get().getSender();
            if (player == null) return;
            LazyOptional<ICyberwareUserData> cyberwareUserData = CyberwareAPI.getCyberwareData(player);
            if (!cyberwareUserData.isPresent()) return;
            cyberwareUserData.orElseThrow(RuntimeException::new)
                    .setHudColor(pkt.color);
        });
        ctx.get().setPacketHandled(true);
    }
}
