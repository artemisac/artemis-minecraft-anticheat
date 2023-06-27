package ac.artemis.packet.wrapper.client.v1_8;

import ac.artemis.packet.wrapper.PacketClient;

@Deprecated
/**
 * @deprecated Deprecated in 1.9
 */
public interface PacketPlayClientLoc extends PacketClient {

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
        return PacketPlayClientLocPosition.class.isAssignableFrom(this.getClass());
    }

    /**
     * Basic boolean which defines whether the packet has a look attribute
     * @return Boolean stating whether the packet has a rotation factor
     */
    default boolean isLook() {
        return PacketPlayClientLocLook.class.isAssignableFrom(this.getClass());
    }
}
