package cc.ghast.packet.wrapper.packet.play.client;

import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.packet.spigot.protocol.PacketLink;
import ac.artemis.packet.wrapper.server.PacketPlayServerVehicleMove;
import cc.ghast.packet.buffer.ProtocolByteBuf;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.ReadableBuffer;
import lombok.Getter;

import java.util.UUID;

/**
 * @author Ghast
 * @since 31/10/2020
 * ArtemisPacket © 2020
 */

@Getter
@PacketLink(PacketPlayServerVehicleMove.class)
public class GPacketPlayClientVehicleMove extends GPacket implements PacketPlayServerVehicleMove, ReadableBuffer {
    public GPacketPlayClientVehicleMove(UUID player, ProtocolVersion version) {
        super("PacketPlayInVehicleMove", player, version, e -> e.isOrAbove(ProtocolVersion.V1_9));
    }

    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;

    @Override
    public void read(ProtocolByteBuf byteBuf) {
        this.x = byteBuf.readDouble();
        this.y = byteBuf.readDouble();
        this.z = byteBuf.readDouble();
        this.yaw = byteBuf.readFloat();
        this.pitch = byteBuf.readFloat();
    }
}
