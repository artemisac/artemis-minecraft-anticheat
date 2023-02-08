package ac.artemis.core.v4.check;

import ac.artemis.core.v4.utils.position.PlayerMovement;
import ac.artemis.core.v4.utils.reach.ReachEntity;

public interface ClickHandler {
    void handle(PlayerMovement current, ReachEntity opponent);
}
