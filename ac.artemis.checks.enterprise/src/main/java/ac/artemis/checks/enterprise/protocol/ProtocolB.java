package ac.artemis.checks.enterprise.protocol;

import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientEntityAction;
import cc.ghast.packet.wrapper.packet.play.client.*;
import ac.artemis.packet.wrapper.client.*;
/**
 * @author Ghast
 * @since 29/11/2020
 * Artemis © 2020
 *
 * Per tick (flying packet), only one sprint and one sneak packet may be sent. This
 * henceforth makes the detection reliable on non flying-fuck versions ( [1.7;1.8] U [1.16;Native] )
 */
@Check(type = Type.PROTOCOL, var = "B", threshold = 1)
public class ProtocolB extends ArtemisCheck implements PacketHandler {
    public ProtocolB(PlayerData data, CheckInformation info) {
        super(data, info);
    }

    private boolean sentSprint, sentSneak;

    @Override
    public void handle(GPacket packet) {
        if (packet instanceof GPacketPlayClientEntityAction) {
            GPacketPlayClientEntityAction act = (GPacketPlayClientEntityAction) packet;

            boolean sprint = false, sneak = false;

            switch (act.getAction()) {
                case START_SNEAKING:
                case STOP_SNEAKING:
                    sneak = true;
                    break;
                case START_SPRINTING:
                case STOP_SPRINTING:
                    sprint = true;
                    break;
            }

            final boolean alreadySent = (sprint && sentSprint) || (sneak && sentSneak);

            // Only one entity action can be updated between ticks
            if (alreadySent) {
                log("sprint=" + sentSprint + " sneak=" + sentSneak);
            }

            this.sentSprint = sprint;
            this.sentSneak = sneak;
        }

        else if (packet instanceof PacketPlayClientFlying) {
            this.sentSprint = this.sentSneak = false;
        }
    }
}
