package ac.artemis.core.v4.data.utils;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * @author Ghast
 * @since 12-May-20
 */
public class PlayerEnums {

    public enum VerticalMotionType {
        UPWARD,
        DOWNWARD,
        STATIC
    }

    @Getter
    public enum GroundType {
        // Todo make this wrapper
    }

    @Getter
    public enum AirType {
        FLY(true, false, true, VerticalMotionType.STATIC, VerticalMotionType.UPWARD, VerticalMotionType.DOWNWARD),
        JUMP(false, false, false, VerticalMotionType.UPWARD, VerticalMotionType.DOWNWARD),
        SLIME(false, true, false, VerticalMotionType.UPWARD, VerticalMotionType.DOWNWARD),
        LIQUID(false, false, true, VerticalMotionType.UPWARD, VerticalMotionType.DOWNWARD),
        LIQUID_JUMP(false, false, false, VerticalMotionType.UPWARD, VerticalMotionType.DOWNWARD),
        COBWEB(false, true, false, VerticalMotionType.DOWNWARD, VerticalMotionType.STATIC),
        FALLING(false, false, false, VerticalMotionType.DOWNWARD),
        VELOCITY(false, false, false, VerticalMotionType.UPWARD, VerticalMotionType.DOWNWARD),
        GROUND(true, false, true, VerticalMotionType.STATIC);

        private final boolean constant, invalidVelocity, freeMove;
        private final List<VerticalMotionType> types;

        AirType(boolean constant, boolean invalidVelocity, boolean freeMove, VerticalMotionType... types) {
            this.constant = constant;
            this.invalidVelocity = invalidVelocity;
            this.freeMove = freeMove;
            this.types = Arrays.asList(types);
        }
    }


    @Getter
    public enum TeleportType {
        RESPAWN(true),
        ROLLBACK(true),
        DISABLER(false),
        UNKNOWN(true);

        private final boolean cancel;

        TeleportType(boolean cancel) {
            this.cancel = cancel;
        }
    }
}
