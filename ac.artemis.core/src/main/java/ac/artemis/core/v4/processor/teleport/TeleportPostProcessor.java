package ac.artemis.core.v4.processor.teleport;

import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientTransaction;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.processor.AbstractHandler;

public class TeleportPostProcessor extends AbstractHandler {
    public TeleportPostProcessor(PlayerData data) {
        super("Teleport [0x02]", data);
    }

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof GPacketPlayClientTransaction){

        }
    }
}
