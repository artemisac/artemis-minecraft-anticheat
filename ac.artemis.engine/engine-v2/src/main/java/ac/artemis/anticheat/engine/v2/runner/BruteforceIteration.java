package ac.artemis.anticheat.engine.v2.runner;

import ac.artemis.anticheat.engine.v2.ArtemisData;
import ac.artemis.core.v5.emulator.TransitionData;
import lombok.Getter;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

@Getter
public class BruteforceIteration {
    private final TransitionData data;
    private final double distance;
    private final double distanceX;
    private final double distanceY;
    private final double distanceZ;
    private final double length;

    private final List<Consumer<ArtemisData>> confirmedDataChanges;

    public BruteforceIteration(final TransitionData data, final double distance,
                               final double distanceX, final double distanceY,
                               final double distanceZ, final double length,
                               final List<Consumer<ArtemisData>> confirmedDataChanges) {
        this.data = data;
        this.distance = distance;
        this.distanceX = distanceX;
        this.distanceY = distanceY;
        this.distanceZ = distanceZ;
        this.length = length;
        this.confirmedDataChanges = confirmedDataChanges;
    }

    public void addConfirmedChanges(final Collection<Consumer<ArtemisData>> changes) {
        confirmedDataChanges.addAll(changes);
    }
}