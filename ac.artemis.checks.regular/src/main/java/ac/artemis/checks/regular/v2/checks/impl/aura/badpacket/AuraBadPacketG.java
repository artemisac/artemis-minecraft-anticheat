package ac.artemis.checks.regular.v2.checks.impl.aura.badpacket;

import ac.artemis.anticheat.api.check.type.Stage;
import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.anticheat.api.material.NMSMaterial;
import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.annotations.Experimental;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.client.PacketPlayClientFlying;
import cc.ghast.packet.wrapper.mc.PlayerEnums;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientBlockPlace;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientUseEntity;

import java.util.Arrays;

/**
 * @author Ghast
 * @since 30-Mar-20
 */

@Check(type = Type.AURA, var = "G", threshold = 2)
@Experimental(stage = Stage.PRE_RELEASE)
public class AuraBadPacketG extends ArtemisCheck implements PacketHandler {

    private final NMSMaterial[] invalidItems = new NMSMaterial[]{
            NMSMaterial.ENDER_PEARL, NMSMaterial.POTION, NMSMaterial.BUCKET};
    private boolean sent;

    public AuraBadPacketG(final PlayerData data, final CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof GPacketPlayClientUseEntity
                && ((GPacketPlayClientUseEntity) packet).getType().equals(PlayerEnums.UseType.ATTACK)) {
            sent = true;
        } else if (packet instanceof PacketPlayClientFlying) {
            sent = false;
        } else if (packet instanceof GPacketPlayClientBlockPlace) {
            final GPacketPlayClientBlockPlace blockPlace = (GPacketPlayClientBlockPlace) packet;
            if (sent && blockPlace.getDirection().isPresent()
                    && blockPlace.getDirection().get().getX() != 255
                    && blockPlace.getItem().isPresent()
                    && Arrays.stream(invalidItems).noneMatch(e ->
                        e.equals(NMSMaterial.matchNMSMaterial(blockPlace.getItem().get().getType())))
                    && !blockPlace.getItem().get().getType().isEdible()
                    && !blockPlace.getItem().get().getType().toString().toLowerCase().contains("sword")
                    && !blockPlace.getItem().get().getType().isBlock()
                    && !blockPlace.getItem().get().getType().name().contains("egg")
                    && !blockPlace.isCancelled()
            ) {
                log("Bad-Packet");
            }
        }
    }
}
