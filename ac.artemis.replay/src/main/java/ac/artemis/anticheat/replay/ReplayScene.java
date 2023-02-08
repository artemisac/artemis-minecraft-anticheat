package ac.artemis.anticheat.replay;

public interface ReplayScene {
    ReplayWorld getWorld();

    ReplayPlayerRepository getEntityRepository();

    void render(ReplayViewer replayViewer);
}
