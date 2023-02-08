package ac.artemis.checks.regular.v2.checks.impl.autoclicker;

import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.annotations.Experimental;
import ac.artemis.core.v4.check.annotations.Drop;
import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;

/**
 * @author Ghast
 * @since 04-Apr-20
 */

@Check(type = Type.AUTOCLICKER, var = "D", threshold = 6)
@Drop
@Experimental
public class AutoClickerD  extends ArtemisCheck implements PacketHandler {
    public AutoClickerD(PlayerData data, CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handle(final GPacket packet) {
        // redacted
    }
}
