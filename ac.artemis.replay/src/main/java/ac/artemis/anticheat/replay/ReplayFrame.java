package ac.artemis.anticheat.replay;

import ac.artemis.anticheat.replay.actions.Action;

import java.util.List;

public interface ReplayFrame {
    List<Action> getAllActions();
}
