package flaxbeard.cyberware.common.network;

import flaxbeard.cyberware.client.gui.EngineeringTableContainer;
import flaxbeard.cyberware.common.block.entities.EngineeringTableBlockEntity;
import lombok.AllArgsConstructor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

@AllArgsConstructor
public class EngineeringSwitchArchivePacket {
    private ResourceLocation dimensionId;
    private int entityId;
    private boolean direction;
    private boolean isComponent;
    private BlockPos pos;

    public EngineeringSwitchArchivePacket(BlockPos pos, PlayerEntity entityPlayer, boolean direction, boolean isComponent)
    {
        this.dimensionId = entityPlayer.level.dimension().location();
        this.entityId = entityPlayer.getId();
        this.pos = pos;
        this.direction = direction;
        this.isComponent = isComponent;
    }


    public static void encode(EngineeringSwitchArchivePacket msg, PacketBuffer buf) {
        buf.writeResourceLocation(msg.dimensionId);
        buf.writeInt(msg.entityId);
        buf.writeBoolean(msg.direction);
        buf.writeBoolean(msg.isComponent);
        buf.writeInt(msg.pos.getX());
        buf.writeInt(msg.pos.getY());
        buf.writeInt(msg.pos.getZ());
    }

    public static EngineeringSwitchArchivePacket decode(PacketBuffer buf) {
        return new EngineeringSwitchArchivePacket(
                buf.readResourceLocation(),
                buf.readInt(),
                buf.readBoolean(),
                buf.readBoolean(),
                new BlockPos(buf.readInt(), buf.readInt(), buf.readInt())
        );
    }
    public static void handle(EngineeringSwitchArchivePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity sender = ctx.get().getSender();
            if (sender == null) return;

            MinecraftServer server = sender.getServer();
            if (server == null) return;

            RegistryKey<World> dimKey = RegistryKey.create(Registry.DIMENSION_REGISTRY, msg.dimensionId);

            ServerWorld level = server.getLevel(dimKey);
            if (level == null) return;

            Entity entity = level.getEntity(msg.entityId);
            if (!(entity instanceof ServerPlayerEntity)) return;
            ServerPlayerEntity player = (ServerPlayerEntity) entity;

            if (!(player.containerMenu instanceof EngineeringTableContainer)) return;
            EngineeringTableContainer container = (EngineeringTableContainer) player.containerMenu;

            if (msg.isComponent) {
                if (msg.direction) {
                    container.nextComponentBox();
                } else {
                    container.prevComponentBox();
                }
            } else {
                if (msg.direction) {
                    container.nextArchive();
                } else {
                    container.prevArchive();
                }

                TileEntity be = level.getBlockEntity(msg.pos);
                if (be instanceof EngineeringTableBlockEntity) {
                    EngineeringTableBlockEntity te = (EngineeringTableBlockEntity) be;
                    te.lastPlayerArchive.put(
                            player.getStringUUID(),
                            container.archive.getBlockPos()
                    );
                }
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
