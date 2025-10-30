package flaxbeard.cyberware.common.network;

import flaxbeard.cyberware.client.gui.EngineeringTableContainer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

@AllArgsConstructor
@Getter
@Data
public class EngineeringDestroyPacketHandler {
    private final int containerId;

    public static void handle(EngineeringDestroyPacketHandler pkt,
                              @Nonnull Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            PlayerEntity player = ctx.get().getSender();
            if (player != null &&
                    player.containerMenu.containerId == pkt.containerId) {
                if (player.containerMenu instanceof EngineeringTableContainer) {
                    EngineeringTableContainer container = (EngineeringTableContainer) player.containerMenu;
                    container.notifyButtonClick(player);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public static void encode(EngineeringDestroyPacketHandler pkt, PacketBuffer buf) {
        buf.writeInt(pkt.containerId);
    }

    public static EngineeringDestroyPacketHandler decode(PacketBuffer buf) {
        return new EngineeringDestroyPacketHandler(buf.readInt());
    }
}
