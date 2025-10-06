package flaxbeard.cyberware.common.network;

import flaxbeard.cyberware.OverclockedOrgans;
import flaxbeard.cyberware.api.hud.UpdateHudColorPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class CyberwarePackets {
    public static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel NETWORK = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(OverclockedOrgans.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;

    public static void register() {
        NETWORK.registerMessage(
                packetId++,
                EngineeringDestroyPacketHandler.class,
                (pkt, buf) -> buf.writeInt(pkt.getContainerId()),
                buf -> new EngineeringDestroyPacketHandler(buf.readInt()),
                EngineeringDestroyPacketHandler::handle
        );
        NETWORK.registerMessage(packetId++,
                UpdateHudColorPacket.class,
                UpdateHudColorPacket::encode,
                UpdateHudColorPacket::decode,
                UpdateHudColorPacket::handle);
    }
}
