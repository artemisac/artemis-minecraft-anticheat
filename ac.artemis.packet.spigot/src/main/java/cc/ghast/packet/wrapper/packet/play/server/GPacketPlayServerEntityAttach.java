package cc.ghast.packet.wrapper.packet.play.server;

import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.packet.spigot.protocol.PacketLink;
import ac.artemis.packet.wrapper.server.PacketPlayServerEntityAttach;
import cc.ghast.packet.buffer.ProtocolByteBuf;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.ReadableBuffer;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Vehicle;

import java.util.UUID;

@Getter
@PacketLink(PacketPlayServerEntityAttach.class)
public class GPacketPlayServerEntityAttach extends GPacket implements PacketPlayServerEntityAttach, ReadableBuffer {
    public GPacketPlayServerEntityAttach(UUID player, ProtocolVersion version) {
        super("PacketPlayOutAttachEntity", player, version);
    }

    private int entityId;
    private int vehicleId;
    private boolean leash;

    @Override
    public void read(ProtocolByteBuf byteBuf) {
        this.entityId = byteBuf.readInt();
        this.vehicleId = byteBuf.readInt();

        if (version.isOrAbove(ProtocolVersion.V1_8_9)) {
            this.leash = byteBuf.readUnsignedByte() == 1;
        } else {
            this.leash = false;
        }
    }

    public Entity getEntity() {
        return Bukkit.getPlayer(uuid).getWorld().getLivingEntities().parallelStream().filter(e-> e.getEntityId() == entityId).findFirst().orElse(null);
    }

    public Entity getVehicle() {
        return Bukkit.getPlayer(uuid).getWorld().getEntitiesByClass(Vehicle.class).parallelStream().filter(e-> e.getEntityId() == vehicleId).findFirst().orElse(null);
    }
}
