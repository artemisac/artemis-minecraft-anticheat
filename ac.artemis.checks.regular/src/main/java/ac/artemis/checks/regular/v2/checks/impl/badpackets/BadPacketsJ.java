package ac.artemis.checks.regular.v2.checks.impl.badpackets;


import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.packet.spigot.wrappers.GPacket;

@Check(type = Type.BADPACKETS, var = "J", threshold = 1)
public class BadPacketsJ extends ArtemisCheck implements PacketHandler {

    public BadPacketsJ(final PlayerData data, final CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handle(final GPacket packet) {
        // Todo recode
    }
}
