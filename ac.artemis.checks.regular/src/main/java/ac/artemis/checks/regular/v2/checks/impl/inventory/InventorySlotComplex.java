package ac.artemis.checks.regular.v2.checks.impl.inventory;


import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.annotations.Experimental;
import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientWindowClick;

/**
 * @author 7x6
 * @since 30/08/2019
 */
@Check(type = Type.INVENTORYWALK, var = "A")
@Experimental
public class InventorySlotComplex extends ArtemisCheck implements PacketHandler {

    public InventorySlotComplex(PlayerData data, CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof GPacketPlayClientWindowClick) {
            int id = ((GPacketPlayClientWindowClick) packet).getWindowId();

            // 0x15, 0 = impl inventory
            if (id != 0)
                return;

            if (!data.user.isInventoryOpen()) {
                log("id=" + id);
            }
        }
    }
}
