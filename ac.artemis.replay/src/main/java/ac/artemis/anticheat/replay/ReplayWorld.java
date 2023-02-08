package ac.artemis.anticheat.replay;

import java.util.Collection;

public interface ReplayWorld {
    ReplayBlock getBlock(final int x, final int y, final int z);

    Collection<ReplayBlock> getBlocks();
}
