package ac.artemis.core.v5.replay.render;

import ac.artemis.anticheat.replay.ReplayWorld;
import ac.artemis.core.v4.data.PlayerData;

public interface ChunkRenderer {
    void render(final PlayerData data, final ReplayWorld replayWorld);
}
