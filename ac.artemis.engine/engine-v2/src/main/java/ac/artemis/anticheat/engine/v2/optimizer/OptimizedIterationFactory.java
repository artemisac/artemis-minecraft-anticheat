package ac.artemis.anticheat.engine.v2.optimizer;

import ac.artemis.anticheat.engine.v2.optimizer.impl.LegacyOptimizedIterationProvider;
import ac.artemis.core.v5.utils.interf.Factory;

public class OptimizedIterationFactory implements Factory<OptimizedIterationProvider> {
    @Override
    public OptimizedIterationProvider build() {
        return new LegacyOptimizedIterationProvider();
    }
}
