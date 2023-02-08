package ac.artemis.anticheat.replay;

import java.util.List;

public interface Replay {
    List<ReplayFrame> getFrames();
    ReplayScene getScene();
}
