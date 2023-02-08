package ac.artemis.core.v4.check;

import ac.artemis.core.v4.utils.position.PlayerMovement;
import ac.artemis.core.v4.utils.position.PredictionPosition;

public interface PredictionHandler {
    void handle(PredictionPosition prediction);
}
