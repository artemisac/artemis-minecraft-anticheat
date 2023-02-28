package cc.ghast.packet.wrapper.packet.play.server;

import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.packet.spigot.protocol.PacketLink;
import ac.artemis.packet.wrapper.server.PacketPlayServerChunkLoad;
import cc.ghast.packet.buffer.ProtocolByteBuf;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.mc.ExtendedBlockStorage;
import cc.ghast.packet.wrapper.packet.ReadableBuffer;
import cc.ghast.packet.wrapper.packet.WriteableBuffer;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@PacketLink(PacketPlayServerChunkLoad.class)
public class GPacketPlayServerChunkLoad extends GPacket implements PacketPlayServerChunkLoad, ReadableBuffer, WriteableBuffer {
    public GPacketPlayServerChunkLoad(UUID player, ProtocolVersion version) {
        super("PacketPlayOutMapChunk", player, version);
    }

    private int x;
    private int z;
    private ChunkMap chunkMap;
    private boolean overworld;

    @Override
    public void read(ProtocolByteBuf byteBuf) {

    }

    @Override
    public void write(ProtocolByteBuf byteBuf) {
        byteBuf.writeInt(this.x);
        byteBuf.writeInt(this.z);
        byteBuf.writeBoolean(this.overworld);
        byteBuf.writeShort((short)(this.chunkMap.dataSize & 65535));
        byteBuf.writeBytes(this.chunkMap.data);
    }

    public static ChunkMap serialize(ExtendedBlockStorage[] aextendedblockstorage, byte[] biomeArray, boolean multiBiome, boolean skyLight, int size) {
        ChunkMap chunkMap = new ChunkMap();
        List<ExtendedBlockStorage> list = new ArrayList<>();

        for (int i = 0; i < aextendedblockstorage.length; ++i) {
            ExtendedBlockStorage extendedblockstorage = aextendedblockstorage[i];

            if (extendedblockstorage != null && (!multiBiome || !extendedblockstorage.isEmpty()) && (size & 1 << i) != 0) {
                chunkMap.dataSize |= 1 << i;
                list.add(extendedblockstorage);
            }
        }

        chunkMap.data = new byte[func_180737_a(Integer.bitCount(chunkMap.dataSize), skyLight, multiBiome)];
        int index = 0;

        for (ExtendedBlockStorage storage : list) {
            char[] achar = storage.getData();

            for (char c0 : achar) {
                chunkMap.data[index++] = (byte)(c0 & 255);
                chunkMap.data[index++] = (byte)(c0 >> 8 & 255);
            }
        }

        for (ExtendedBlockStorage storage : list) {
            index = copy(storage.getBlocklightArray().getData(), chunkMap.data, index);
        }

        if (skyLight) {
            for (ExtendedBlockStorage storage : list) {
                index = copy(storage.getSkylightArray().getData(), chunkMap.data, index);
            }
        }

        if (multiBiome) {
            copy(biomeArray, chunkMap.data, index);
        }

        return chunkMap;
    }

    private static int copy(byte[] from, byte[] to, int length) {
        System.arraycopy(from, 0, to, length, from.length);
        return length + from.length;
    }

    protected static int func_180737_a(int p_180737_0_, boolean p_180737_1_, boolean p_180737_2_)
    {
        int i = p_180737_0_ * 2 * 16 * 16 * 16;
        int j = p_180737_0_ * 16 * 16 * 16 / 2;
        int k = p_180737_1_ ? p_180737_0_ * 16 * 16 * 16 / 2 : 0;
        int l = p_180737_2_ ? 256 : 0;
        return i + j + k + l;
    }

    public static class ChunkMap {
        public byte[] data;
        public int dataSize;
    }
}
