package cc.ghast.packet.wrapper.packet.play.client;

import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.packet.spigot.protocol.PacketLink;
import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.client.v1_8.PacketPlayClientChatMessage;
import cc.ghast.packet.buffer.ProtocolByteBuf;
import cc.ghast.packet.wrapper.packet.ReadableBuffer;

import java.util.UUID;

@PacketLink(PacketPlayClientChatMessage.class)
public class GPacketPlayClientChat extends GPacket implements PacketPlayClientChatMessage, ReadableBuffer {

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
