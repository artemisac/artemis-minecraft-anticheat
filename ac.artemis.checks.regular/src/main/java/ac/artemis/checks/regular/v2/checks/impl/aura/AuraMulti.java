package ac.artemis.checks.regular.v2.checks.impl.aura;

import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.client.PacketPlayClientFlying;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientUseEntity;

/**
 * @author Ghast
 * @since 30-Mar-20
 */

@Check(type = Type.AURA, var = "Multi")
public class AuraMulti extends ArtemisCheck implements PacketHandler {

    private int id = -1;
    private boolean sent;

    public AuraMulti(final PlayerData data, final CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof GPacketPlayClientUseEntity) {
            final GPacketPlayClientUseEntity act = (GPacketPlayClientUseEntity) packet;

            if (sent && id != -1) {
                if (act.getEntityId() != id) {
                    log("");
                }
            } else {
                sent = true;
                id = act.getEntityId();
            }
        } else if (packet instanceof PacketPlayClientFlying) {
            sent = false;
        }
    }
}
