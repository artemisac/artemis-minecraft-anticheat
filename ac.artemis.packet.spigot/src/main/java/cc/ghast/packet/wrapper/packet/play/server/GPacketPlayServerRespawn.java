package cc.ghast.packet.wrapper.packet.play.server;

import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.packet.spigot.protocol.PacketLink;
import ac.artemis.packet.wrapper.server.PacketPlayServerRespawn;
import cc.ghast.packet.buffer.ProtocolByteBuf;
import cc.ghast.packet.wrapper.bukkit.GameMode;
import cc.ghast.packet.wrapper.bukkit.WorldType;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.ReadableBuffer;
import lombok.Getter;
import org.bukkit.Difficulty;

import java.util.UUID;

@Getter
@PacketLink(PacketPlayServerRespawn.class)
public class GPacketPlayServerRespawn extends GPacket implements PacketPlayServerRespawn, ReadableBuffer {
    public GPacketPlayServerRespawn(UUID player, ProtocolVersion version) {
        super("PacketPlayOutRespawn", player, version);
    }

    private int id;
    private Difficulty difficulty;
    private GameMode gamemode;
    private WorldType dimension;

    @Override
    public void read(ProtocolByteBuf byteBuf) {
        if (version.isAbove(ProtocolVersion.V1_14)) {

        } else {
            this.id = byteBuf.readInt();
            this.difficulty = Difficulty.values()[byteBuf.readUnsignedByte()];
            this.gamemode = GameMode.getById(byteBuf.readUnsignedByte());
            this.dimension = WorldType.getByName(byteBuf.readStringBuf(16).toUpperCase());
            if (this.dimension == null) {
                this.dimension = WorldType.DEFAULT;
            }
        }

    }
}
