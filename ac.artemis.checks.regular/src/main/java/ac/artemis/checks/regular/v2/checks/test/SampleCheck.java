package ac.artemis.checks.regular.v2.checks.test;

import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.play.client.*;
import ac.artemis.packet.wrapper.client.*;

import java.util.Arrays;

/**
 * @author Ghast
 * @since 15-Mar-20
 */
public class SampleCheck  extends ArtemisCheck implements PacketHandler {
    public SampleCheck(PlayerData data, CheckInformation info) {
        super(data, info);
    }


    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof PacketPlayClientFlying) {
            debug("collision0=" + Arrays.toString(data.collision.getCollidingBlocks0().toArray()));
            debug("collision1=" + Arrays.toString(data.collision.getCollidingBlocks0().toArray()));
            debug("collisionY-1=" + Arrays.toString(data.collision.getCollidingBlocks0().toArray()));
        }
    }
}
