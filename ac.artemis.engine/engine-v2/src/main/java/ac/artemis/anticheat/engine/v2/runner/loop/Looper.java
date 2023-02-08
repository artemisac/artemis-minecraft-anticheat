package ac.artemis.anticheat.engine.v2.runner.loop;

import ac.artemis.anticheat.engine.v2.optimizer.OptimizedIterationResult;
import ac.artemis.anticheat.engine.v2.runner.loop.impl.DumbTickLooperProvider;
import ac.artemis.anticheat.engine.v2.runner.loop.impl.EmptyTickLooperProvider;
import ac.artemis.anticheat.engine.v2.runner.loop.impl.FullTickLooperProvider;
import ac.artemis.anticheat.engine.v2.runner.loop.impl.SkipTickLooperProvider;

public enum Looper {
    EMPTY(new EmptyTickLooperProvider()),
    SKIP(new SkipTickLooperProvider()),
    DUMB(new DumbTickLooperProvider()),
    FULL(new FullTickLooperProvider());

    private final BruteforceLooper looper;

    Looper(BruteforceLooper looper) {
        this.looper = looper;
    }

    public LoopReturn run(final LoopData loopData, final OptimizedIterationResult result) {
        return looper.run(loopData, result);
    }
}
