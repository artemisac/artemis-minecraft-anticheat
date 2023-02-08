package ac.artemis.anticheat.replay;

public interface ReplayBlock {
    ReplayBlockPosition getPosition();

    int getMaterial();
    byte getType();
    int getData();

    interface ReplayBlockPosition {
        int getX();
        int getY();
        int getZ();

        @Override
        int hashCode();
    }
}
