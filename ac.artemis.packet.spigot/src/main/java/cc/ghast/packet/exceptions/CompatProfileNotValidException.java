package cc.ghast.packet.exceptions;

import java.util.UUID;

/**
 * @author Ghast
 * @since 15/09/2020
 * ArtemisPacket © 2020
 */
public class CompatProfileNotValidException extends RuntimeException{
    public CompatProfileNotValidException(UUID uuid) {
        super(uuid.toString() + "'s profile has been found but is invalid!");
    }
}
