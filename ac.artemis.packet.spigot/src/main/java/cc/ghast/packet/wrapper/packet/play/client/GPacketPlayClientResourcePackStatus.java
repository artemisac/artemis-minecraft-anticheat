package cc.ghast.packet.wrapper.packet.play.client;

import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.packet.spigot.protocol.PacketLink;
import ac.artemis.packet.wrapper.client.v1_8.PacketPlayClientResourcePackStatus;
import cc.ghast.packet.buffer.ProtocolByteBuf;
import cc.ghast.packet.wrapper.mc.PlayerEnums;
import cc.ghast.packet.wrapper.packet.ReadableBuffer;
import ac.artemis.packet.spigot.wrappers.GPacket;
import lombok.Getter;

import java.util.Optional;
import java.util.UUID;

@Getter
@PacketLink(PacketPlayClientResourcePackStatus.class)
public class GPacketPlayClientResourcePackStatus extends GPacket implements PacketPlayClientResourcePackStatus, ReadableBuffer {
    public GPacketPlayClientResourcePackStatus(UUID player, ProtocolVersion version) {
        super("PacketPlayInResourcePackStatus", player, version);
    }

    private Optional<String> url;
    private PlayerEnums.ResourcePackStatus status;

    @Override
    public void read(ProtocolByteBuf byteBuf) {
        if (version.isBelow(ProtocolVersion.V1_10)) {
            this.url = Optional.of(byteBuf.readStringBuf(40));
        }
        else {
            //Not sent on 1.10 and above
            this.url = Optional.empty();
        }
        this.status = PlayerEnums.ResourcePackStatus.values()[byteBuf.readVarInt()];
    }


}
