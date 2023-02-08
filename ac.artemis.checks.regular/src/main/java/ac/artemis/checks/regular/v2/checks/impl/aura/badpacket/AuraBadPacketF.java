package ac.artemis.checks.regular.v2.checks.impl.aura.badpacket;


import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.client.PacketPlayClientFlying;
import cc.ghast.packet.wrapper.mc.PlayerEnums;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientBlockPlace;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientUseEntity;

/**
 * @author Ghast
 * @since 30-Mar-20
 */

@Check(type = Type.AURA, var = "F")
public class AuraBadPacketF extends ArtemisCheck implements PacketHandler {

    private boolean sentAttack, sentInteract;

    public AuraBadPacketF(final PlayerData data, final CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof GPacketPlayClientBlockPlace) {
            if (sentAttack && !sentInteract) {
                log("Bad-Packet");
            }
        } else if (packet instanceof GPacketPlayClientUseEntity) {
            final GPacketPlayClientUseEntity useEntity = (GPacketPlayClientUseEntity) packet;

            if (useEntity.getType().equals(PlayerEnums.UseType.ATTACK)) {
                sentAttack = true;
            } else {
                sentInteract = true;
            }
        } else if (packet instanceof PacketPlayClientFlying) {
            sentInteract = false;
            sentAttack = false;
        }
    }
}
