package ac.artemis.packet.wrapper.client;

public interface PacketPlayClientPosition extends PacketPlayClientFlying {
    /**
     * X coordinate encoded in a double which represents the location of the player
     * at the time of the packet was sent.
     * @return Double X coordinate
     */
    double getX();

    /**
     * Y coordinate encoded in a double which represents the location of the player
     * at the time of the packet was sent.
     * @return Double Y coordinate
     */
    double getY();

    /**
     * Z coordinate encoded in a double which represents the location of the player
     * at the time of the packet was sent.
     * @return Double Z coordinate
     */
    double getZ();
}
