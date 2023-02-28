package ac.artemis.packet.minecraft.block;

public enum BlockFace {
    NORTH(0, 0, -1),
    EAST(1, 0, 0),
    SOUTH(0, 0, 1),
    WEST(-1, 0, 0),
    UP(0, 1, 0),
    DOWN(0, -1, 0),
    NORTH_EAST(NORTH, EAST),
    NORTH_WEST(NORTH, WEST),
    SOUTH_EAST(SOUTH, EAST),
    SOUTH_WEST(SOUTH, WEST),
    WEST_NORTH_WEST(WEST, NORTH_WEST),
    NORTH_NORTH_WEST(NORTH, NORTH_WEST),
    NORTH_NORTH_EAST(NORTH, NORTH_EAST),
    EAST_NORTH_EAST(EAST, NORTH_EAST),
    EAST_SOUTH_EAST(EAST, SOUTH_EAST),
    SOUTH_SOUTH_EAST(SOUTH, SOUTH_EAST),
    SOUTH_SOUTH_WEST(SOUTH, SOUTH_WEST),
    WEST_SOUTH_WEST(WEST, SOUTH_WEST),
    SELF(0, 0, 0);

    private final int modX;
    private final int modY;
    private final int modZ;

    private BlockFace(int modX, int modY, int modZ) {
        this.modX = modX;
        this.modY = modY;
        this.modZ = modZ;
    }

    private BlockFace(BlockFace face1, BlockFace face2) {
        this.modX = face1.getModX() + face2.getModX();
        this.modY = face1.getModY() + face2.getModY();
        this.modZ = face1.getModZ() + face2.getModZ();
    }

    public int getModX() {
        return this.modX;
    }

    public int getModY() {
        return this.modY;
    }

    public int getModZ() {
        return this.modZ;
    }

    public BlockFace getOppositeFace() {
        switch (this.ordinal()) {
            case 1:
                return SOUTH;
            case 2:
                return WEST;
            case 3:
                return NORTH;
            case 4:
                return EAST;
            case 5:
                return DOWN;
            case 6:
                return UP;
            case 7:
                return SOUTH_WEST;
            case 8:
                return SOUTH_EAST;
            case 9:
                return NORTH_WEST;
            case 10:
                return NORTH_EAST;
            case 11:
                return EAST_SOUTH_EAST;
            case 12:
                return SOUTH_SOUTH_EAST;
            case 13:
                return SOUTH_SOUTH_WEST;
            case 14:
                return WEST_SOUTH_WEST;
            case 15:
                return WEST_NORTH_WEST;
            case 16:
                return NORTH_NORTH_WEST;
            case 17:
                return NORTH_NORTH_EAST;
            case 18:
                return EAST_NORTH_EAST;
            case 19:
                return SELF;
            default:
                return SELF;
        }
    }
}
