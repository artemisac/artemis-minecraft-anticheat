package cc.ghast.packet.wrapper.packet.play.server;

import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.packet.spigot.protocol.PacketLink;
import ac.artemis.packet.wrapper.server.PacketPlayServerChat;
import cc.ghast.packet.buffer.ProtocolByteBuf;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.ReadableBuffer;
import lombok.Getter;

import java.util.UUID;

@Getter
@PacketLink(PacketPlayServerChat.class)
public class GPacketPlayServerChat extends GPacket implements PacketPlayServerChat, ReadableBuffer {
    public GPacketPlayServerChat(UUID player, ProtocolVersion version) {
        super("PacketPlayOutChat", player, version);
    }

    private String text;
    //1.7.10 clients don't send this setting...
    private ChatMessageType type;

    @Override
    public void read(ProtocolByteBuf byteBuf) {
        final int stringBufArg;
        if (version.isBelow(ProtocolVersion.V1_13)) {
            stringBufArg = 32767;
        }
        else {
            stringBufArg = 262144;
        }
        this.text = byteBuf.readStringBuf(stringBufArg);

        if (version.isLegacy()) {
            this.type = ChatMessageType.CHAT;
        }
        else {
            byte position = byteBuf.readByte();
            this.type = ChatMessageType.getChatMessageType(position);
        }
    }

    public enum ChatMessageType {
        CHAT((byte)0),
        SYSTEM((byte)1),
        GAME_INFO((byte)2);

        private final byte position;

        ChatMessageType(byte position) {
            this.position = position;
        }

        public byte getPosition() {
            return this.position;
        }

        public static ChatMessageType getChatMessageType(final byte position) {
            ChatMessageType[] values = values();

            for (ChatMessageType type : values) {
                if (position == type.position) {
                    return type;
                }
            }
            return CHAT;
        }
    }

}
