package ac.artemis.core.v5.emulator.block;

import ac.artemis.core.v5.emulator.TransitionData;

public interface CollisionLandable {
    void onLanded(final TransitionData emulator);
}
