package ac.artemis.core.v5.replay.render.chunk;

import ac.artemis.anticheat.replay.ReplayBlock;
import ac.artemis.anticheat.replay.ReplayWorld;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v5.replay.render.ChunkRenderer;
import ac.artemis.core.v5.utils.minecraft.MathHelper;
import ac.artemis.packet.PacketManager;
import ac.artemis.packet.modal.WrappedBlock;
import cc.ghast.packet.wrapper.bukkit.BlockPosition;
import cc.ghast.packet.wrapper.packet.play.server.GPacketPlayServerBlockChangeMulti;
import cc.ghast.packet.wrapper.packet.play.server.GPacketPlayServerChunkLoad;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StandardChunkRenderer implements ChunkRenderer {
    @Override
    public void render(final PlayerData data, final ReplayWorld replayWorld) {
        int minX = MathHelper.floor_double(data.prediction.getX());
        int minY = MathHelper.floor_double(data.prediction.getY());
        int minZ = MathHelper.floor_double(data.prediction.getZ());
        int maxX = MathHelper.ceiling_double_int(data.prediction.getX());
        int maxY = MathHelper.ceiling_double_int(data.prediction.getY());
        int maxZ = MathHelper.ceiling_double_int(data.prediction.getZ());

        for (final ReplayBlock block : replayWorld.getBlocks()) {
            // X
            final int x = block.getPosition().getX();
            if (x < minX)
                minX = x;
            else if (x > maxX)
                maxX = x;

            // Y
            final int y = block.getPosition().getY();
            if (y < minY)
                minY = y;
            else if (y > maxY)
                maxY = y;

            // Z
            final int z = block.getPosition().getZ();
            if (z < minZ)
                minZ = z;
            else if (z > maxZ)
                maxZ = z;
        }

        // Todo chunk(s) generation
        for (int chunkX = minX >> 4; chunkX < maxX >> 4; chunkX++) {
            for (int chunkZ = minZ >> 4; chunkZ < maxZ >> 4; chunkZ++) {
                final GPacketPlayServerChunkLoad.ChunkMap chunkMap = new GPacketPlayServerChunkLoad.ChunkMap();
                chunkMap.data = new byte[serialize(Integer.bitCount(chunkMap.dataSize))];
                chunkMap.dataSize = 0;

                final GPacketPlayServerChunkLoad chunkLoad = new GPacketPlayServerChunkLoad(data.getPlayerID(), data.getVersion());
                chunkLoad.setChunkMap(chunkMap);
                chunkLoad.setX(chunkX);
                chunkLoad.setZ(chunkZ);
                chunkLoad.setOverworld(true);

                PacketManager.getApi().sendPacket(data.getPlayerID(), chunkLoad, false, null);

                final List<ReplayBlock> wrappedBlockList = new ArrayList<>();
                for (int x = chunkX; x < chunkX + 16; x++) {
                    for (int y = minY; y < maxY; y++) {
                        for (int z = chunkZ; z < chunkZ + 16; z++) {
                            final ReplayBlock block = replayWorld.getBlock(x, y, z);

                            if (block == null)
                                continue;

                            wrappedBlockList.add(block);
                        }
                    }
                }

                final GPacketPlayServerBlockChangeMulti multiBlock = new GPacketPlayServerBlockChangeMulti(data.getPlayerID(), data.getVersion());
                multiBlock.setRecords(wrappedBlockList.stream().map(e ->
                        new GPacketPlayServerBlockChangeMulti.BlockChange(
                                new WrappedBlock(e.getMaterial(), e.getData()),
                                new BlockPosition(e.getPosition().getX(), e.getPosition().getY(), e.getPosition().getZ())
                        )
                ).collect(Collectors.toList()));

                PacketManager.getApi().sendPacket(data.getPlayerID(), multiBlock, false, null);
            }
        }
    }

    protected static int serialize(final int size) {
        final int i = size * 2 * 16 * 16 * 16;
        final int j = size * 16 * 16 * 16 / 2;
        final int k = 0;
        final int l = 0;
        return i + j + k + l;
    }
}
