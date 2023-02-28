package cc.ghast.packet.exceptions;

import java.util.UUID;

/**
 * @author Ghast
 * @since 15/09/2020
 * ArtemisPacket © 2020
 */
public class CompatProfileNotFoundException extends RuntimeException {
    public CompatProfileNotFoundException(UUID uuid) {
        super(uuid.toString() + "'s profile has not been found!");
    }
}
