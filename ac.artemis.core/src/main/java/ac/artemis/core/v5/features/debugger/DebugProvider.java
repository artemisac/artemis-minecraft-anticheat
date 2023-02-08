package ac.artemis.core.v5.features.debugger;

import ac.artemis.core.v4.check.ArtemisCheck;

public interface DebugProvider {
    void debug(final ArtemisCheck check, String debug);
}
