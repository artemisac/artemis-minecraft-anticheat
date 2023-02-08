package ac.artemis.checks.enterprise.protocol;

import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.debug.Debug;
import ac.artemis.core.v4.check.packet.PacketExcludable;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.client.PacketPlayClientLook;
import cc.ghast.packet.wrapper.packet.play.client.*;
import ac.artemis.packet.wrapper.client.*;

/**
 * @author Ghast
 * @since 04/12/2020
 * Artemis © 2020
 *
 * Simple pitch rotation check. This accounts for ladders and teleports (the weird bug).
 * Considering this should most definitely account for 99.99% of all scenarios in which
 * the pitch has to be clamped, it should most definitely be un-falsable.
 */

@Check(type = Type.PROTOCOL, var = "F", threshold = 1)
public class ProtocolF extends ArtemisCheck implements PacketHandler {

    public ProtocolF(PlayerData data, CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handle(final GPacket packet) {
        final PacketPlayClientLook wrapper = (PacketPlayClientLook) packet;

        final float pitch = Math.abs(wrapper.getPitch());
        final float maxPitch = data.movement.isOnLadder() ? 91.2F : 90.F;

        flag: {
            final boolean flag = pitch > maxPitch;

            if (!flag) break flag;

            this.log(
                    new Debug<>("pitch", pitch),
                    new Debug<>("maxPitch", maxPitch)
            );
        }

        this.debug("pitch=%a max=%a", pitch, maxPitch);
    }
}
