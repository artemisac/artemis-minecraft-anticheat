package cc.ghast.packet.exceptions;

import ac.artemis.packet.protocol.ProtocolDirection;
import ac.artemis.packet.spigot.wrappers.GPacket;

public class AlreadyConsumedPacketIdException extends RuntimeException {

    public AlreadyConsumedPacketIdException(ProtocolDirection dir, Class<? extends GPacket> packet, int exist) {
        super(dir + "cc/ghast/packet " + packet + " is already known to ID " + exist);
    }
}
