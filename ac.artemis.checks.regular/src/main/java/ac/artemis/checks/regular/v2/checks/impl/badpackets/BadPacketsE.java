package ac.artemis.checks.regular.v2.checks.impl.badpackets;


import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.anticheat.api.material.NMSMaterial;
import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.mc.PlayerEnums;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientBlockDig;

import java.util.Arrays;
import java.util.List;

@Check(type = Type.BADPACKETS, var = "E", threshold = 1)
public class BadPacketsE extends ArtemisCheck implements PacketHandler {

    private static final List<NMSMaterial> INVALID_ITEMS = Arrays.asList(NMSMaterial.FISHING_ROD);
    private boolean sent;

    public BadPacketsE(final PlayerData data, final CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof GPacketPlayClientBlockDig
                && ((GPacketPlayClientBlockDig) packet).getType() == PlayerEnums.DigType.RELEASE_USE_ITEM
                && data.user.isPlaced()) {
            final GPacketPlayClientBlockDig dig = (GPacketPlayClientBlockDig) packet;

            if (dig.getType() == PlayerEnums.DigType.RELEASE_USE_ITEM
                    && data.user.isPlaced()
                    && !INVALID_ITEMS.contains(
                            NMSMaterial.matchNMSMaterial(
                                    data.getPlayer().getInventory().getItemInMainHand().getType()))
            ) {
                log(1);
            }
        }
    }
}
