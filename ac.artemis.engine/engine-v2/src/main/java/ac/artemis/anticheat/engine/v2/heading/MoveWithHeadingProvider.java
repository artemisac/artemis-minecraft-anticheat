package ac.artemis.anticheat.engine.v2.heading;

import ac.artemis.core.v5.emulator.TransitionData;

public interface MoveWithHeadingProvider {
    TransitionData moveWithHeading(final TransitionData heading);
}
