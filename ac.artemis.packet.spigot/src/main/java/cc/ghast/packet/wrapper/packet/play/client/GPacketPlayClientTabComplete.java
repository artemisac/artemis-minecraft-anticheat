package cc.ghast.packet.wrapper.packet.play.client;

import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.packet.spigot.protocol.PacketLink;
import ac.artemis.packet.wrapper.client.v1_8.PacketPlayClientTabComplete;
import cc.ghast.packet.buffer.ProtocolByteBuf;
import cc.ghast.packet.wrapper.bukkit.BlockPosition;
import cc.ghast.packet.wrapper.packet.ReadableBuffer;
import ac.artemis.packet.spigot.wrappers.GPacket;
import lombok.Getter;

import java.util.Optional;
import java.util.UUID;

@Getter
@PacketLink(PacketPlayClientTabComplete.class)
public class GPacketPlayClientTabComplete extends GPacket implements PacketPlayClientTabComplete, ReadableBuffer {
    public GPacketPlayClientTabComplete(UUID player, ProtocolVersion version) {
        super("PacketPlayInTabComplete", player, version);
    }

    private String value;
    private Optional<Boolean> assumeCommand;
    private Optional<BlockPosition> blockPosition;
    private Optional<Integer> transactionId;

    @Override
    public void read(ProtocolByteBuf byteBuf) {
        if (version.isOrAbove(ProtocolVersion.V1_13)) {
            this.transactionId = Optional.of(byteBuf.readVarInt());
            this.blockPosition = Optional.empty();
            this.assumeCommand = Optional.empty();
            this.value = byteBuf.readStringBuf(32500);
        } else {
            this.value = byteBuf.readStringBuf(32767);

            if (version.isOrAbove(ProtocolVersion.V1_9)) {
                assumeCommand = Optional.of(byteBuf.readBoolean());
            } else {
                assumeCommand = Optional.empty();
            }

            final boolean flag = byteBuf.readBoolean();

            if (flag) {
                this.blockPosition = Optional.of(byteBuf.readBlockPositionFromLong());
            } else {
                this.blockPosition = Optional.empty();
            }

            this.transactionId = Optional.empty();
        }
    }
}
