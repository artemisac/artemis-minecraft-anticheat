package ac.artemis.anticheat.engine.v2.flying;

import ac.artemis.core.v5.emulator.TransitionData;
import ac.artemis.core.v5.emulator.modal.Friction;
import ac.artemis.core.v5.emulator.modal.Motion;

public interface EntityFlyingProvider {
    /**
     * Corresponds to moveFlying in EntityLivingBase. This is the fundamental
     * part which controls the motion acceleration based on moveForward and
     * moveStrafe
     * @param run Transition data object for all the info
     * @return Same reference to an identical or cloned transition data with
     *         all the modifications done
     */
    TransitionData provide(final TransitionData run);
}
