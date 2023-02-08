package ac.artemis.anticheat.engine.v2.block;

import ac.artemis.core.v5.emulator.TransitionData;

public interface BlockCollisionProvider {
    /**
     * Handles the block collision for a specific transition data
     *
     * @param data TransitionData object
     */
    void doBlockCollisions(final TransitionData data);
}
