package ac.artemis.core.v4.data.holders;

import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v5.utils.raytrace.NaivePoint;
import ac.artemis.core.v5.utils.raytrace.Point;

public class WorldHolder extends AbstractHolder {
    public WorldHolder(PlayerData data) {
        super(data);
    }

    /*private final NMSCache<NaivePoint, GhostBlock> ghostBlockMap = new NMSCache<NaivePoint, GhostBlock>().expireAfterWrite(250, TimeUnit.MILLISECONDS).build();

    public void addBlock(final ItemStack type, final NaivePoint coordinate) {
        final GhostBlock ghostBlock = new GhostBlock(type,
                new Location(
                        data.getPlayer().getWorld(),
                        coordinate.getX(),
                        coordinate.getY(),
                        coordinate.getZ()
                )
        );

        ghostBlockMap.put(coordinate, ghostBlock);
    }

    public void removeBlock(final NaivePoint coordinate) {
        ghostBlockMap.remove(coordinate);
    }

    public void clear() {
        ghostBlockMap.asMap().clear();
    }

    public Block getBlock(Point point) {
        return this.getBlock(point.getBlockX(), point.getBlockY(), point.getBlockZ());
    }

    public org.bukkit.block.Block getBlock(final int x, final int y, final int z) {
        final NaivePoint naivePoint = new NaivePoint(x, y, z);

        if (ghostBlockMap.containsKey(naivePoint)) {
            return ghostBlockMap.getIfPresent(naivePoint);
        }

        return BlockUtil.getBlockAsync(data.getPlayer().getWorld(), x, y, z);
    }

    public Stream<GhostBlock> streamBlocks() {
        return ghostBlockMap.asMap().values().stream();
    }*/
}
