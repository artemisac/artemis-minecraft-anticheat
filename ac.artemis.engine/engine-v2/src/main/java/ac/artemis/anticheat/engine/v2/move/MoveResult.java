package ac.artemis.anticheat.engine.v2.move;

import ac.artemis.core.v5.utils.bounding.BoundingBox;
import ac.artemis.core.v5.utils.raytrace.Point;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

/**
 * @author Ghast
 * @since 03/02/2021
 * Artemis © 2021
 */

@Data
public class MoveResult {
    private final BoundingBox result;
    private final Point position;
    private final List<Runnable> updateEntity;

    public MoveResult(BoundingBox result, Runnable... updateEntity) {
        this.result = result;
        this.position = new Point(
                (result.maxX + result.minX) / 2.D,
                result.minY,
                (result.maxZ + result.minZ) / 2.D
        );
        this.updateEntity = Arrays.asList(updateEntity);
    }

    public MoveResult(BoundingBox result, List<Runnable> updateEntity) {
        this.result = result;
        this.position = new Point(
                (result.maxX + result.minX) / 2.D,
                result.minY,
                (result.maxZ + result.minZ) / 2.D
        );
        this.updateEntity = updateEntity;
    }

    public Point getPosition() {
        return position;
    }
}
