package cc.ghast.packet.wrapper.packet.play.server;

import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.packet.spigot.protocol.PacketLink;
import ac.artemis.packet.wrapper.server.PacketPlayServerResourcePackSend;
import cc.ghast.packet.buffer.ProtocolByteBuf;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.ReadableBuffer;
import cc.ghast.packet.wrapper.packet.WriteableBuffer;
import lombok.Getter;

import java.util.UUID;

@Getter
@PacketLink(PacketPlayServerResourcePackSend.class)
public class GPacketPlayServerResourcePackSend extends GPacket implements PacketPlayServerResourcePackSend, ReadableBuffer, WriteableBuffer {
    public GPacketPlayServerResourcePackSend(UUID player, ProtocolVersion version) {
        super("PacketPlayOutResourcePackSend", player, version);
    }

    public GPacketPlayServerResourcePackSend(String url, String hash) {
        super("PacketPlayOutResourcePackSend");
        this.url = url;
        this.hash = hash;
    }

    private String url;
    private String hash;

    @Override
    public void read(ProtocolByteBuf byteBuf) {
        this.url = byteBuf.readStringBuf(32767);
        this.hash = byteBuf.readStringBuf(40);
    }

    @Override
    public void write(ProtocolByteBuf byteBuf) {
        byteBuf.writeString(this.url);
        byteBuf.writeString(this.hash);
    }
}
