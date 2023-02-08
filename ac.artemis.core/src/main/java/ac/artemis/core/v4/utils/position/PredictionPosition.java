package ac.artemis.core.v4.utils.position;

import lombok.NoArgsConstructor;

public class PredictionPosition {
    private final PlayerMovement was;
    private final PlayerMovement got;
    private final PlayerMovement expected;

    public PredictionPosition(PlayerMovement was, PlayerMovement got, PlayerMovement expected) {
        this.was = was;
        this.got = got;
        this.expected = expected;
    }

    public PlayerMovement was() {
        return was;
    }

    public PlayerMovement got() {
        return got;
    }

    public PlayerMovement expected() {
        return expected;
    }

    public double deltaSquared() {
        return was.distanceSquare(got);
    }

    public double deltaSquaredXZ() {
        return was.distanceSquareXZ(got);
    }

    public double deltaY() {
        return expected.distanceY(got);
    }

    public double predictDeltaSquared() {
        return was.distanceSquare(expected);
    }

    public double differenceSquared() {
        return got.distanceSquare(expected);
    }

    public double predictDeltaSquaredXZ() {
        return was.distanceSquareXZ(expected);
    }

    public double differenceSquaredXZ() {
        return got.distanceSquareXZ(expected);
    }

    public boolean isPredictSmaller() {
        return deltaSquared() > predictDeltaSquared();
    }

    public double wasGotX() {
        return Math.abs(was.x - got.x);
    }

    public double wasExpectX() {
        return Math.abs(was.x - expected.x);
    }

    public boolean isPredictSmallerX() {
        return wasGotX() - wasExpectX() > 1E-7D;
    }

    public double wasGotY() {
        return Math.abs(was.y - got.y);
    }

    public double wasExpectY() {
        return Math.abs(was.y - expected.y);
    }

    public boolean isPredictSmallerY() {
        return wasGotY() - wasExpectY() > 1E-7D;
    }

    public double wasGotZ() {
        return Math.abs(was.z - got.z);
    }

    public double wasExpectZ() {
        return Math.abs(was.z - expected.z);
    }

    public boolean isPredictSmallerZ() {
        return wasGotZ() - wasExpectZ() > 1E-7D;
    }

    public boolean isPredictSmallerXZ() {
        return deltaSquaredXZ() > predictDeltaSquaredXZ();
    }
}
