package ac.artemis.checks.regular.v2.checks.impl.autoclicker.badpacket;

import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientArmAnimation;

/**
 * @author Ghast
 * @since 18-Apr-20
 */

@Check(type = Type.AUTOCLICKER, var = "X3")
public class AutoClicker3 extends ArtemisCheck implements PacketHandler {
    public AutoClicker3(final PlayerData data, final CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof GPacketPlayClientArmAnimation) {
            if (data.combat.getCps() >= 20 && !data.user.isDigging() && !data.user.isPlaced()) {
                log("cps=" + data.combat.getCps());
            }
        }
    }
}
