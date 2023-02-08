package ac.artemis.checks.regular.v2.checks.impl.badpackets;

import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientHeldItemSlot;

@Check(type = Type.BADPACKETS, var = "P", threshold = 4)
public class BadPacketsP  extends ArtemisCheck implements PacketHandler {

    private int lastSlot = -1;

    public BadPacketsP(final PlayerData data, final CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof GPacketPlayClientHeldItemSlot) {
            final GPacketPlayClientHeldItemSlot wrapper = (GPacketPlayClientHeldItemSlot) packet;

            final int slot = wrapper.getSlot();

            if (slot == lastSlot) {
                log("slot=" + slot + " lastSlot=" + lastSlot);
            }

            lastSlot = slot;
        }
    }
}
