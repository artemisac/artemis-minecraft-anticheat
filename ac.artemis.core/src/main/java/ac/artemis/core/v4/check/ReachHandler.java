package ac.artemis.core.v4.check;

import ac.artemis.core.v4.utils.reach.ReachEntity;
import ac.artemis.core.v4.utils.reach.ReachModal;

public interface ReachHandler {
    void handle(ReachModal current, ReachEntity opponent);
}
