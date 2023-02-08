package ac.artemis.anticheat.engine.v2.liquid;

import ac.artemis.anticheat.engine.v2.ArtemisData;
import ac.artemis.core.v5.emulator.TransitionData;
import ac.artemis.core.v5.emulator.attributes.AttributeMap;
import ac.artemis.core.v5.emulator.modal.Motion;

public interface LiquidCollisionProvider {
    Motion provideMotion(final TransitionData emulator, final AttributeMap attributeMap, final Motion motion);
}
