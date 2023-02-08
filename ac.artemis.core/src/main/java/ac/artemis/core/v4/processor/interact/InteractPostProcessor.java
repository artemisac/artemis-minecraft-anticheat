package ac.artemis.core.v4.processor.interact;

import ac.artemis.core.v4.processor.AbstractHandler;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.play.client.*;
import ac.artemis.packet.wrapper.client.*;

/**
 * @author Ghast
 * @since 13/09/2020
 * Artemis © 2020
 */
public class InteractPostProcessor extends AbstractHandler {
    public InteractPostProcessor(PlayerData data) {
        super("Interact [0x01]", data);
    }

    @Override
    public void handle(GPacket packet) {
        if (packet instanceof PacketPlayClientFlying) {
            data.combat.setProcessAttack(false);
            data.user.setPlaced(false);
        }
    }
}
