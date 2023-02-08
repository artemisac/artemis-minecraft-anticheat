package ac.artemis.checks.regular.v2.checks.impl.pingspoof;


import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientKeepAlive;

/**
 * @author 7x6
 * @since 28/10/2019
 */
@Check(type = Type.PINGSPOOF, var = "Duplicate", threshold = 1)
public class PingSpoofDuplicate  extends ArtemisCheck implements PacketHandler {

    private int verbose;

    public PingSpoofDuplicate(final PlayerData data, final CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof GPacketPlayClientKeepAlive) {
            final GPacketPlayClientKeepAlive wrapper = (GPacketPlayClientKeepAlive) packet;

            if (wrapper.getId() == 10000 && ++verbose > 1) {
                log("Sigma");
            }
        }
    }
}
