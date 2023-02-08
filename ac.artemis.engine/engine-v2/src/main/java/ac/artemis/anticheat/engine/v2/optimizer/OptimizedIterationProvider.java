package ac.artemis.anticheat.engine.v2.optimizer;

import ac.artemis.anticheat.engine.v2.ArtemisData;

public interface OptimizedIterationProvider {
    OptimizedIterationResult provide(final ArtemisData emulator);
}
