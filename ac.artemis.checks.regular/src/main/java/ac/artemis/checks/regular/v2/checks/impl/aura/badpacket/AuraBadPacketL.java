package ac.artemis.checks.regular.v2.checks.impl.aura.badpacket;

import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.packet.spigot.wrappers.GPacket;

/**
 * @author Ghast
 * @since 30-Mar-20
 */

@Check(type = Type.AURA, var = "L", threshold = 2)
public class AuraBadPacketL extends ArtemisCheck implements PacketHandler {
    public AuraBadPacketL(final PlayerData data, final CheckInformation info) {
        super(data, info);
    }


    @Override
    public void handle(final GPacket packet) {
        // Todo recode
    }
}
