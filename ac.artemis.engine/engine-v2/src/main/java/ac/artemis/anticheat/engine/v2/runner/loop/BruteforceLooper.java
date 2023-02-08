package ac.artemis.anticheat.engine.v2.runner.loop;

import ac.artemis.anticheat.engine.v2.optimizer.OptimizedIterationResult;
import ac.artemis.anticheat.engine.v2.runner.BruteforceKey;

public interface BruteforceLooper {
    LoopReturn run(final LoopData loopData, final OptimizedIterationResult result);
}
