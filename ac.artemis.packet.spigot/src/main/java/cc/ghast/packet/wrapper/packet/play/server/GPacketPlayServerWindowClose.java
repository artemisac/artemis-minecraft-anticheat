package cc.ghast.packet.wrapper.packet.play.server;

import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.packet.spigot.protocol.PacketLink;
import ac.artemis.packet.wrapper.server.PacketPlayServerWindowClose;
import cc.ghast.packet.buffer.ProtocolByteBuf;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.ReadableBuffer;
import cc.ghast.packet.wrapper.packet.WriteableBuffer;
import lombok.Getter;

import java.util.UUID;

@Getter
@PacketLink(PacketPlayServerWindowClose.class)
public class GPacketPlayServerWindowClose extends GPacket implements PacketPlayServerWindowClose, ReadableBuffer, WriteableBuffer {
    public GPacketPlayServerWindowClose(UUID player, ProtocolVersion version) {
        super("PacketPlayOutCloseWindow", player, version);
    }

    public GPacketPlayServerWindowClose(int windowId) {
        super("PacketPlayOutCloseWindow");
        this.windowId = windowId;
    }

    private int windowId;

    @Override
    public void read(ProtocolByteBuf byteBuf) {
        this.windowId = byteBuf.readUnsignedByte();
    }

    @Override
    public void write(ProtocolByteBuf byteBuf) {
        byteBuf.writeByte(windowId);
    }
}
