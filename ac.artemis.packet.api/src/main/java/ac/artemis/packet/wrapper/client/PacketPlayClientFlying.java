package ac.artemis.packet.wrapper.client;

import ac.artemis.packet.wrapper.PacketClient;

public interface PacketPlayClientFlying extends PacketClient {

    /**
     * Ground status of the player sent via the flying packet
     * @return Boolean representing whether the player is onGround
     */
    boolean isOnGround();

    /**
     * Basic boolean which defines whether the packet has the position attribute
     * @return Boolean stating whether the packet has a position factor
     */
    default boolean isPos() {
        return PacketPlayClientPosition.class.isAssignableFrom(this.getClass());
    }

    /**
     * Basic boolean which defines whether the packet has a look attribute
     * @return Boolean stating whether the packet has a rotation factor
     */
    default boolean isLook() {
        return PacketPlayClientLook.class.isAssignableFrom(this.getClass());
    }
}
