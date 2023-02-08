package ac.artemis.anticheat.engine.v2.runner.loop.impl;

import ac.artemis.anticheat.engine.v2.optimizer.OptimizedIterationResult;
import ac.artemis.anticheat.engine.v2.runner.BruteforceIteration;
import ac.artemis.anticheat.engine.v2.runner.loop.BruteforceLooper;
import ac.artemis.anticheat.engine.v2.runner.loop.LoopData;
import ac.artemis.anticheat.engine.v2.runner.loop.LoopReturn;
import ac.artemis.anticheat.engine.v2.runner.loop.Looper;
import ac.artemis.core.v5.emulator.TransitionData;

import java.util.List;

public class DumbTickLooperProvider implements BruteforceLooper {
    @Override
    public LoopReturn run(LoopData loopData, OptimizedIterationResult result) {
        final TransitionData snapshot = loopData.getData().snapshot();

        double distance = Double.MAX_VALUE;
        LoopReturn runnable = null;

        final List<BruteforceIteration> sorted = loopData.getSorted();
        loopData.clean();

        for (BruteforceIteration bruteforceIteration : sorted) {
            loopData.getData().apply(bruteforceIteration.getData());

            LoopReturn temp;

            if (loopData.getData().getData().prediction.isPos()) {
                temp = Looper.FULL.run(loopData, result);
            } else {
                temp = Looper.SKIP.run(loopData, result);
            }

            if (temp.getDistance() < distance) {
                runnable = temp;
                distance = temp.getDistance();

                if (loopData.getData().getDistance() < 1E-8) {
                    return runnable;
                }
            }
            loopData.getData().apply(snapshot);
        }

        LoopReturn temp;
        if (loopData.getData().getData().prediction.isPos()) {
            temp = Looper.FULL.run(loopData, result);
        } else {
            temp = Looper.SKIP.run(loopData, result);
        }

        if (temp.getDistance() < distance) {
            runnable = temp;
        }

        return runnable;
    }
}
