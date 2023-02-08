package ac.artemis.anticheat.engine.v2.jump;

import ac.artemis.core.v5.emulator.TransitionData;

public interface JumpMoveProvider {
    TransitionData provide(final TransitionData run);
}
