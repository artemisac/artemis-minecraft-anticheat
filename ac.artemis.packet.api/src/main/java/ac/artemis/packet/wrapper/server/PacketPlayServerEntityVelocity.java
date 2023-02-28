package ac.artemis.packet.wrapper.server;

import ac.artemis.packet.wrapper.PacketServer;

public interface PacketPlayServerEntityVelocity extends PacketServer {
    /**
     * @return Entity ID of the entity affected by the velocity
     */
    int getEntityId();

    /**
     * @return X value (compressed with a factor) of the modified motion
     */
    short getX();

    /**
     * @return Y value (compressed with a factor) of the modified motion
     */
    short getY();

    /**
     * @return Z value (compressed with a factor) of the modified motion
     */
    short getZ();
}
