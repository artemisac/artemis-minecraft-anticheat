package cc.ghast.packet.wrapper.packet.play.client;

import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.packet.spigot.protocol.PacketLink;
import ac.artemis.packet.wrapper.client.v1_8.PacketPlayClientSettings;
import cc.ghast.packet.buffer.ProtocolByteBuf;
import cc.ghast.packet.wrapper.mc.PlayerEnums;
import cc.ghast.packet.wrapper.packet.ReadableBuffer;
import ac.artemis.packet.spigot.wrappers.GPacket;
import lombok.Getter;

import java.util.UUID;

@Getter
@PacketLink(PacketPlayClientSettings.class)
public class GPacketPlayClientSettings extends GPacket implements PacketPlayClientSettings, ReadableBuffer {
    public GPacketPlayClientSettings(UUID player, ProtocolVersion version) {
        super("PacketPlayInSettings", player, version);
    }

    //Client's language
    private String locale;
    //Client's view distance
    private int viewDistance;
    private PlayerEnums.ChatVisibility visibility;
    //Chat colors setting on the client
    private boolean chatColors;
    private int displayedSkinPartsMask;
    private PlayerEnums.Hand hand;

    @Override
    public void read(ProtocolByteBuf byteBuf) {
        if (version.isBelow(ProtocolVersion.V1_16)) {
            this.locale = byteBuf.readStringBuf(7);
        } else {
            this.locale = byteBuf.readStringBuf(16);
        }
        this.viewDistance = byteBuf.readByte();
        this.visibility = PlayerEnums.ChatVisibility.values()[byteBuf.readVarInt()];
        this.chatColors = byteBuf.readBoolean();
        this.displayedSkinPartsMask = byteBuf.readUnsignedByte();
        if (version.isBelow(ProtocolVersion.V1_9)) {
            this.hand = PlayerEnums.Hand.MAIN_HAND;
        }
        else {
            this.hand = PlayerEnums.Hand.values()[byteBuf.readVarInt()];
        }
    }


}
