package ac.artemis.core.v5.emulator.block;

import ac.artemis.core.v5.emulator.TransitionData;

public interface CollisionBlockState {
    void onCollidedBlockState(final TransitionData emulator);
}
