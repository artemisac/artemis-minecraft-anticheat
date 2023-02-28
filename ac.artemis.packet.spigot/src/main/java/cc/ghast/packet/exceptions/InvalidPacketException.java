package cc.ghast.packet.exceptions;

import ac.artemis.packet.spigot.wrappers.GPacket;

/**
 * @author Ghast
 * @since 30/08/2020
 * Artemis © 2020
 */
public class InvalidPacketException extends RuntimeException {
    public InvalidPacketException(Class<? extends GPacket> clazz){
        super("Packet of type " + clazz + " is not getX valid packet!");
    }
}
