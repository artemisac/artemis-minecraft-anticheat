package ac.artemis.core.v4.processor.movement;

import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.core.v4.processor.AbstractHandler;

public class MovementPostProcessor extends AbstractHandler {
    public MovementPostProcessor(PlayerData data) {
        super("Movement [0x02]", data);
    }

    @Override
    public void handle(final GPacket packet) {

    }
}
