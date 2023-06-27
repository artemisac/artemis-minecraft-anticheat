package cc.ghast.packet.wrapper.packet.play.client;

import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.packet.spigot.protocol.PacketLink;
import ac.artemis.packet.wrapper.client.v1_8.PacketPlayClientSteerVehicle;
import cc.ghast.packet.buffer.ProtocolByteBuf;
import cc.ghast.packet.wrapper.packet.ReadableBuffer;
import ac.artemis.packet.spigot.wrappers.GPacket;
import lombok.Getter;

import java.util.UUID;

@Getter
@PacketLink(PacketPlayClientSteerVehicle.class)
public class GPacketPlayClientSteerVehicle extends GPacket implements PacketPlayClientSteerVehicle, ReadableBuffer {
    public GPacketPlayClientSteerVehicle(UUID player, ProtocolVersion version) {
        super("PacketPlayInSteerVehicle", player, version);
    }

    private float moveForward;
    private float moveStrafing;
    private boolean jumping;
    private boolean sneaking;

    @Override
    public void read(ProtocolByteBuf byteBuf) {
        this.moveForward = byteBuf.readFloat();
        this.moveStrafing = byteBuf.readFloat();
        byte b0 = byteBuf.readByte();

        this.jumping = (b0 & 1) > 0;
        this.sneaking = (b0 & 2) > 0;
    }
}
