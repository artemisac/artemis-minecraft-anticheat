package ac.artemis.checks.regular.v2.checks.impl.badpackets;

import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.enums.CheckType;
import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.play.client.*;
import ac.artemis.packet.wrapper.client.*;

@Check(type = Type.BADPACKETS, var = "A", threshold = 1)
public class BadPacketsA  extends ArtemisCheck implements PacketHandler {

    public BadPacketsA(final PlayerData data, final CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof PacketPlayClientFlying) {
            if (isNull(CheckType.POSITION) || isNull(CheckType.ROTATION)) return;

            final float pitch = Math.abs(data.movement.rotation.getPitch());

            debug("pitch=" + pitch);

            final float threshold = data.movement.isOnLadder() ? 91.2F : 90.f;

            if (pitch > threshold) {
                log("pitch=" + pitch);
            }
        }
    }
}
