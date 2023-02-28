package cc.ghast.packet.wrapper.packet.login;

import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.packet.spigot.protocol.PacketLink;
import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.server.PacketLoginOutSuccess;
import cc.ghast.packet.buffer.ProtocolByteBuf;
import cc.ghast.packet.wrapper.mc.GameProfile;
import cc.ghast.packet.wrapper.packet.ReadableBuffer;

import java.util.UUID;

@PacketLink(PacketLoginOutSuccess.class)
public class GPacketLoginServerSuccess extends GPacket implements ReadableBuffer {
    public GPacketLoginServerSuccess(UUID player, ProtocolVersion version) {
        super("PacketLoginOutServerSuccess", player, version);
    }

    private GameProfile gameProfile;

    @Override
    public void read(ProtocolByteBuf byteBuf) {
        final UUID uuid;
        final String username;

        if (version.isOrAbove(ProtocolVersion.V1_16)) {
            int[] aint = new int[4];

            for(int i = 0; i < aint.length; ++i) {
                aint[i] = byteBuf.readInt();
            }

            uuid = decodeUUID(aint);
            username = byteBuf.readStringBuf(16);
        } else {
            final String uid = byteBuf.readStringBuf(36);
            uuid = UUID.fromString(uid);
            username = byteBuf.readStringBuf(16);
        }

        this.gameProfile = new GameProfile(uuid, username);
    }

    private static UUID decodeUUID(int[] bits) {
        return new UUID((long)bits[0] << 32 | (long)bits[1] & 4294967295L, (long)bits[2] << 32 | (long)bits[3] & 4294967295L);
    }

    public GameProfile getGameProfile() {
        return this.gameProfile;
    }
}
