package ac.artemis.anticheat.engine.v2.runner.loop;

import ac.artemis.anticheat.engine.v2.ArtemisData;
import ac.artemis.anticheat.engine.v2.runner.BruteforceCaller;
import ac.artemis.anticheat.engine.v2.runner.BruteforceIteration;
import ac.artemis.anticheat.engine.v2.runner.BruteforceKey;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v5.utils.raytrace.Point;
import lombok.Getter;

import java.util.*;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

@Getter
public class LoopData {
    private final ArtemisData data;
    private final BruteforceCaller caller;

    public LoopData(ArtemisData data, BruteforceCaller caller) {
        this.data = data;
        this.caller = caller;
    }

    private final Map<Point, BruteforceIteration> nextTicks = new HashMap<>();
    private final List<BruteforceIteration> sortedNextTicks = new ArrayList<>();

    public void addIteration(final Point point, final BruteforceIteration iteration) {
        this.nextTicks.put(point, iteration);
    }

    public void sort(final Point received) {
        final List<BruteforceIteration> sorted = nextTicks.entrySet()
                .stream()
                .sorted(Comparator.comparingDouble(new ToDoubleFunction<Map.Entry<Point, BruteforceIteration>>() {
                    @Override
                    public double applyAsDouble(Map.Entry<Point, BruteforceIteration> value) {
                        final Point got = value.getKey();

                        return got.squareDistanceTo(received);
                    }
                }))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());

        this.sortedNextTicks.addAll(sorted);
    }

    public void clean() {
        this.nextTicks.clear();
        this.sortedNextTicks.clear();
    }

    public boolean isNext() {
        return !nextTicks.isEmpty();
    }

    public BruteforceIteration run(final BruteforceKey key) {
        return this.caller.apply(key);
    }

    public List<BruteforceIteration> getSorted() {
        return new ArrayList<>(sortedNextTicks);
    }
}
