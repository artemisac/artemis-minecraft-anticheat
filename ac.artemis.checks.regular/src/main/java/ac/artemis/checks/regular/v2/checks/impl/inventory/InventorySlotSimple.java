package ac.artemis.checks.regular.v2.checks.impl.inventory;

import ac.artemis.anticheat.api.material.NMSMaterial;
import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.annotations.Experimental;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.packet.minecraft.inventory.ItemStack;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientWindowClick;

/**
 * @author Elevated
 * @since 18-Apr-20
 */

@Check(type = Type.INVENTORYWALK, var = "SlotSimple")
@Experimental
public class InventorySlotSimple  extends ArtemisCheck implements PacketHandler {
    public InventorySlotSimple(PlayerData data, CheckInformation info) {
        super(data, info);
    }

    private int lastSlot, buffer;
    private long lastTimestamp, lastClickDelay;

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof GPacketPlayClientWindowClick) {
            final GPacketPlayClientWindowClick wrapper = (GPacketPlayClientWindowClick) packet;
            final int slot = wrapper.getSlot();

            if (Math.abs(slot) > 30 || slot > data.getPlayer().getInventory().getSize())
                return;

            if (wrapper.getShiftedMode() == 4)
                return;

            final ItemStack itemStack = data.getPlayer().getInventory().getItem(slot);

            if (itemStack.v() != null && NMSMaterial.matchNMSMaterial(itemStack.getType()) == NMSMaterial.POTION) {
                final long now = System.currentTimeMillis();

                final int deltaSlot = Math.abs(slot - this.lastSlot);

                final long clickDelay = now - this.lastTimestamp;
                final long deltaDelay = Math.abs(clickDelay - this.lastClickDelay);

                if (deltaSlot == 1 && clickDelay == this.lastClickDelay) {
                    buffer += 2;

                    if (buffer > 2) {
                        log("buffer=" + buffer + " delay=" + clickDelay);
                    }
                }

                if (deltaDelay <= 4L && clickDelay <= 110L) {
                    buffer += 0.5;

                    if (buffer > 2) {
                        log("buffer=" + buffer + " delay=" + clickDelay);
                    }
                } else {
                    buffer = Math.max(buffer - 1, 0);
                }

                if (deltaDelay <= 4 && clickDelay <= 110L && deltaSlot == 1) {
                    buffer += 2;

                    if (buffer > 2) {
                        log("buffer=" + buffer + " delay=" + clickDelay);
                    }
                }

                /*
                 Theory behind this is that you cannot have a big slot change and flag the difference check
                 As it would take around 3ms + the click time to change 4 slots
                  */
                if (deltaDelay <= 4 && clickDelay <= 110L && deltaSlot >= 4) {
                    buffer += 2;

                    if (buffer > 3) {
                        log("buffer=" + buffer + " delay=" + clickDelay);
                    }
                }

                this.lastTimestamp = now;
                this.lastSlot = slot;
                this.lastClickDelay = clickDelay;
            }

        }
    }
}
