package ac.artemis.anticheat.engine.v2.runner.loop;

import lombok.Data;

@Data
public class LoopReturn {
    private double distance;
    private Runnable runnable;

    public LoopReturn(double distance, Runnable runnable) {
        this.distance = distance;
        this.runnable = runnable;
    }
}
