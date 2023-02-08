package ac.artemis.core.v5.replay.render;

import ac.artemis.anticheat.replay.ReplayPlayerRepository;
import ac.artemis.core.v4.data.PlayerData;

public interface EntitySpawnRenderer {
    void spawn(final PlayerData data, final ReplayPlayerRepository repository);
}
