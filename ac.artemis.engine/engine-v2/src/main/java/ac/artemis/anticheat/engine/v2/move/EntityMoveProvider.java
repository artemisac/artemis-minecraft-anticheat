package ac.artemis.anticheat.engine.v2.move;


import ac.artemis.anticheat.engine.v2.ArtemisData;
import ac.artemis.core.v5.emulator.TransitionData;

public interface EntityMoveProvider {
    TransitionData provide(final ArtemisData emulator, final TransitionData move);
}
