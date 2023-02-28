package cc.ghast.packet.wrapper.packet.play.server;

import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.packet.spigot.protocol.PacketLink;
import ac.artemis.packet.wrapper.server.PacketPlayServerSpawnEntityWeather;
import cc.ghast.packet.buffer.ProtocolByteBuf;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.ReadableBuffer;
import lombok.Getter;

import java.util.UUID;

@Getter
@PacketLink(PacketPlayServerSpawnEntityWeather.class)
public class GPacketPlayServerSpawnEntityWeather extends GPacket implements PacketPlayServerSpawnEntityWeather, ReadableBuffer {
    public GPacketPlayServerSpawnEntityWeather(UUID player, ProtocolVersion version) {
        super("PacketPlayOutSpawnEntityWeather", player, version);
    }

    private int entityId;
    private byte type;
    private double x;
    private double y;
    private double z;

    @Override
    public void read(ProtocolByteBuf byteBuf) {
        this.entityId = byteBuf.readVarInt();
        this.type = byteBuf.readByte();

        if (version.isAbove(ProtocolVersion.V1_9)) {
            this.x = byteBuf.readDouble();
            this.y = byteBuf.readDouble();
            this.z = byteBuf.readDouble();
        } else {
            this.x = byteBuf.readInt();
            this.y = byteBuf.readInt();
            this.z = byteBuf.readInt();
        }

    }
}
