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
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientArmAnimation;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientBlockPlace;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientUseEntity;

/**
 * @author Ghast
 * @since 30-Mar-20
 */

@Check(type = Type.AURA, var = "B")
public class AuraBadPacketB extends ArtemisCheck implements PacketHandler {
    public AuraBadPacketB(final PlayerData data, final CheckInformation info) {
        super(data, info);
    }

    private boolean sentArmAnimation, sentAttack, sentBlockPlace, sentUseEntity;

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof GPacketPlayClientArmAnimation) {
            sentArmAnimation = true;
        } else if (packet instanceof GPacketPlayClientUseEntity) {

            final GPacketPlayClientUseEntity att = (GPacketPlayClientUseEntity) packet;

            if (att.getType().equals(PlayerEnums.UseType.ATTACK)) {
                sentAttack = true;
            } else {
                sentUseEntity = true;
            }
        } else if (packet instanceof GPacketPlayClientBlockPlace) {
            final GPacketPlayClientBlockPlace plc = (GPacketPlayClientBlockPlace) packet;

            if (plc.getItem() != null
                    && plc.getItem().isPresent()
                    && plc.getItem().get().getType() != null
                    && plc.getItem().get().getType().toString().toLowerCase().contains("sword")) {
                sentBlockPlace = true;
            }
        } else if (packet instanceof PacketPlayClientFlying) {
            if (sentArmAnimation && !sentAttack && sentBlockPlace && sentUseEntity)
                log("Bad-Packet");

            sentUseEntity = false;
            sentBlockPlace = false;
            sentAttack = false;
            sentArmAnimation = false;
        }
    }
}
