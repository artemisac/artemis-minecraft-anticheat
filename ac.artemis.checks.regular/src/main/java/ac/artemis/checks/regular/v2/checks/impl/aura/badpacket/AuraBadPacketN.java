package ac.artemis.checks.regular.v2.checks.impl.aura.badpacket;

import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.mc.PlayerEnums;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientArmAnimation;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientUseEntity;

/**
 * @author Ghast
 * @since 30-Mar-20
 */

@Check(type = Type.AURA, var = "N", threshold = 2)
public class AuraBadPacketN extends ArtemisCheck implements PacketHandler {

    private int swingCount, attackCount;

    public AuraBadPacketN(final PlayerData data, final CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof GPacketPlayClientUseEntity) {
            final GPacketPlayClientUseEntity use = (GPacketPlayClientUseEntity) packet;

            if (use.getType().equals(PlayerEnums.UseType.ATTACK)) {
                if (attackCount++ >= 20) {
                    attackCount = 0;
                    swingCount = 0;
                }

                if (attackCount > 5 && swingCount > 5) {
                    if (attackCount > swingCount) {
                        log("Bad-Packet: a=" + swingCount + " u=" + attackCount);
                    }
                }
            }
        } else if (packet instanceof GPacketPlayClientArmAnimation) {
            swingCount++;
        }
    }
}
