package ac.artemis.checks.regular.v2.checks.impl.pingspoof;


import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.annotations.Experimental;
import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientKeepAlive;

/**
 * @author 7x6
 * @since 28/10/2019
 */
@Check(type = Type.PINGSPOOF, var = "IdDupe", threshold = 5)
@Experimental
public class PingSpoofIdDupe  extends ArtemisCheck implements PacketHandler {

    private long lastId = -1;

    public PingSpoofIdDupe(final PlayerData data, final CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof GPacketPlayClientKeepAlive) {
            final GPacketPlayClientKeepAlive wrapper = (GPacketPlayClientKeepAlive) packet;

            if (wrapper.getId() == lastId) {
                log();
            }

            lastId = wrapper.getId();
        }
    }
}
