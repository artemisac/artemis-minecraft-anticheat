package cc.ghast.packet.wrapper.packet.play.client;

import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.packet.spigot.protocol.PacketLink;
import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.client.PacketPlayClientChat;
import cc.ghast.packet.buffer.ProtocolByteBuf;
import cc.ghast.packet.wrapper.packet.ReadableBuffer;

import java.util.UUID;

@PacketLink(PacketPlayClientChat.class)
public class GPacketPlayClientChat extends GPacket implements PacketPlayClientChat, ReadableBuffer {

    public GPacketPlayClientChat(UUID player, ProtocolVersion version) {
        super("PacketPlayInChat", player, version);
    }

    private String message;

    @Override
    public void read(ProtocolByteBuf byteBuf) {
        if (version.isOrBelow(ProtocolVersion.V1_8_9)) {
            this.message = byteBuf.readString();
        } else {
            this.message = byteBuf.readStringBuf(100);
        }

    }

    public String getMessage() {
        return this.message;
    }
}
