package ac.artemis.packet.wrapper.client;

public interface PacketPlayClientLook extends PacketPlayClientFlying {
    /**
     * Float representation of the player's yaw on a degree based trigonometrical
     * circle. The value can be % by 360 without issue. It's basic trigonometry.
     * @return Float representing the player's yaw
     */
    float getYaw();

    /**
     * Float representation of the player's pitch. This pitch is forcefully clamped
     * between -90 and 90 and covers the entire 180 angle from top to bottom.
     * @return Float representing the player's pitch
     */
    float getPitch();
}
