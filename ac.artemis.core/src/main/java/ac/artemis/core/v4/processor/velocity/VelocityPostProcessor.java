package ac.artemis.core.v4.processor.velocity;

import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.processor.AbstractHandler;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientTransaction;

public class VelocityPostProcessor extends AbstractHandler {
    public VelocityPostProcessor(PlayerData data) {
        super("Velocity [0x02]", data);
    }

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof GPacketPlayClientTransaction){
        }
    }
}
