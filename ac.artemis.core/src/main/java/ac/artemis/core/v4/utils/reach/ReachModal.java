package ac.artemis.core.v4.utils.reach;

import ac.artemis.core.v5.utils.bounding.BoundingBox;
import ac.artemis.core.v5.utils.raytrace.Point;
import lombok.Data;

/**
 * @author Ghast
 * @since 26/01/2021
 * Artemis © 2021
 */

@Data
public class ReachModal {
    private final Type type;
    private final Point eyePos;
    private final Point eyeVec;
    private final Point hitVec;
    private final BoundingBox boundingBox;
    private final int ticks;
    private final double distance;

    public ReachModal(Type type, Point eyePos, Point eyeVec, Point hitVec, BoundingBox boundingBox, int ticks, double distance) {
        this.type = type;
        this.eyePos = eyePos;
        this.eyeVec = eyeVec;
        this.hitVec = hitVec;
        this.boundingBox = boundingBox;
        this.ticks = ticks;
        this.distance = distance;
    }

    public enum Type {
        HIT,
        HIT_MISS,
        CLICK,
        CLICK_MISS,
        TICK;
    }
}
