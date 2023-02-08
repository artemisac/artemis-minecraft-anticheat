package ac.artemis.core.v5.replay;

import ac.artemis.anticheat.replay.ReplayPlayerRepository;
import ac.artemis.anticheat.replay.ReplayScene;
import ac.artemis.anticheat.replay.ReplayViewer;
import ac.artemis.anticheat.replay.ReplayWorld;

public class ArtemisReplayScene implements ReplayScene {
    @Override
    public ReplayWorld getWorld() {
        return null;
    }

    @Override
    public ReplayPlayerRepository getEntityRepository() {
        return null;
    }

    @Override
    public void render(ReplayViewer replayViewer) {

    }
}
