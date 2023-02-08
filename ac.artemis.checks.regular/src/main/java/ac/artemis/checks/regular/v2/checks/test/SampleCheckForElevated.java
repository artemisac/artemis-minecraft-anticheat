package ac.artemis.checks.regular.v2.checks.test;

import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.play.client.*;
import ac.artemis.packet.wrapper.client.*;

/**
 * @author Ghast
 * @since 26-May-20
 */

@Check(type = Type.AURA, var = "FuckYou", threshold = 15)
public class SampleCheckForElevated  extends ArtemisCheck implements PacketHandler {
    public SampleCheckForElevated(PlayerData data, CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof PacketPlayClientFlying) {
            PacketPlayClientFlying fly = (PacketPlayClientFlying) packet;
        }
    }
}
