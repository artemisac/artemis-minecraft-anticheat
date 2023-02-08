package ac.artemis.core.v5.emulator.block;

import ac.artemis.core.v5.emulator.TransitionData;

public interface CollisionBlock {
    void onCollidedBlock(final TransitionData emulator);
}
