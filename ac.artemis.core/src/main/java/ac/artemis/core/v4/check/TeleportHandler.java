package ac.artemis.core.v4.check;

import ac.artemis.core.v4.utils.position.ModifiableFlyingLocation;

public interface TeleportHandler {
    void handle(final ModifiableFlyingLocation confirmedLocation);
}
