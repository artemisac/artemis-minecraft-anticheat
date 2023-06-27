package ac.artemis.packet.wrapper.client.v1_8;

import ac.artemis.packet.wrapper.PacketClient;

import java.util.Optional;

public interface PacketPlayClientAbilities extends PacketClient {
    /**
     * @return Returns the flying status of a player
     */
    boolean isFlying();

    /**
     * @return Returns the invulnerable status of a player
     * @deprecated in 1.16
     */
    Optional<Boolean> isInvulnerable();

    /**
     * @return Returns the flight status of a player
     * @deprecated in 1.16
     */
    Optional<Boolean> isAllowedFlight();

    /**
     * @return Returns the creative status of a player
     * @deprecated in 1.16
     */
    Optional<Boolean> isCreativeMode();

    /**
     * @return Returns the fly speed of a player
     * @deprecated in 1.16
     */
    Optional<Float> getFlySpeed();

    /**
     * @return Returns the walk speed status of a player
     * @deprecated in 1.16
     */
    Optional<Float> getWalkSpeed();
}
