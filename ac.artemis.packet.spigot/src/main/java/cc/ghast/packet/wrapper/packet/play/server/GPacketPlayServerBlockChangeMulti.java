package cc.ghast.packet.wrapper.packet.play.server;

import ac.artemis.packet.modal.WrappedBlock;
import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.packet.spigot.protocol.PacketLink;
import ac.artemis.packet.wrapper.server.PacketPlayServerBlockChangeMulti;
import cc.ghast.packet.buffer.ProtocolByteBuf;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.bukkit.BlockPosition;
import cc.ghast.packet.wrapper.packet.ReadableBuffer;
import cc.ghast.packet.wrapper.packet.WriteableBuffer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Getter
@Setter
@PacketLink(PacketPlayServerBlockChangeMulti.class)
public class GPacketPlayServerBlockChangeMulti extends GPacket implements PacketPlayServerBlockChangeMulti, ReadableBuffer, WriteableBuffer {
    public GPacketPlayServerBlockChangeMulti(UUID player, ProtocolVersion version) {
        super("PacketPlayOutMultiBlockChange", player, version);
    }

    private Integer chunkX;
    private Optional<Integer> chunkY;
    private Integer chunkZ;
    private List<BlockChange> records;

    @Override
    public void read(ProtocolByteBuf byteBuf) {
        if (version.isOrBelow(ProtocolVersion.V1_12_2)) {
            this.chunkX = byteBuf.readInt();
            this.chunkY = Optional.empty();
            this.chunkZ = byteBuf.readInt();

            final int length = byteBuf.readVarInt();

            this.records = new ArrayList<>(length);

            for (int i = 0; i < length; i++) {
                final short pos = byteBuf.readShort();

                final int x = pos >> 12 & 15;
                final int y = pos & 255;
                final int z = pos >> 8 & 15;
                final BlockPosition position = new BlockPosition(x, y, z);

                final int compressed = byteBuf.readVarInt();

                records.add(new BlockChange(compressed, position));
            }
        }
    }

    @Override
    public void write(ProtocolByteBuf byteBuf) {
        if (version.isOrBelow(ProtocolVersion.V1_12_2)) {
            int chunkX = this.records.get(0).getPosition().getX() >> 4;
            int chunkZ = this.records.get(0).getPosition().getZ() >> 4;
            byteBuf.writeInt(chunkX);
            byteBuf.writeInt(chunkZ);
            byteBuf.writeVarInt(this.records.size());
            for(BlockChange record : this.records) {
                byteBuf.writeShort((
                        record.getPosition().getX() - (chunkX << 4)) << 12
                        | (record.getPosition().getZ() - (chunkZ << 4)) << 8
                        | record.getPosition().getY()
                );

                byteBuf.writeVarInt(record.getBlock());
            }
        }

    }

    @Data
    public static class BlockChange {
        private int block;
        private BlockPosition position;

        public BlockChange(int block, BlockPosition position) {
            this.block = block;
            this.position = position;
        }

        public BlockChange(WrappedBlock block, BlockPosition position) {
            this.block = (block.getId() << 4)
                    | (block.getData() & 0xF);
            this.position = position;
        }
    }
}
