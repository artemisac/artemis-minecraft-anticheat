package ac.artemis.checks.regular.v2.checks.impl.fastbow;

import ac.artemis.anticheat.api.material.NMSMaterial;
import cc.ghast.packet.wrapper.mc.PlayerEnums;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientBlockDig;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientBlockPlace;
import ac.artemis.packet.wrapper.client.*;
import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;

public class FastBowA  extends ArtemisCheck implements PacketHandler {
    public FastBowA(PlayerData data, CheckInformation info) {
        super(data, info);
    }

    private int ticks;
    private boolean shooting;
    private long lastShot;

    @Override
    public void handle(final GPacket packet) {
        // Listen to when the user starts using the bow
        if (packet instanceof GPacketPlayClientBlockPlace) {
            final GPacketPlayClientBlockPlace plc = (GPacketPlayClientBlockPlace) packet;

            // Ensure the user is using a bow
            final boolean valid = plc.getItem().isPresent() &&
                    NMSMaterial.matchNMSMaterial(plc.getItem().get().getType())
                            .equals(NMSMaterial.BOW);

            // If invalid just return
            if (!valid) {
                debug("AItemStack is not a bow!");
                return;
            }

            // If the user is already shooting, flag it and return
            if (shooting) {
                log("ticks=" + ticks);
                return;
            }

            this.ticks = 0;
            this.shooting = true;
        }

        // Listen to Flying packet to update the ticks
        if (packet instanceof PacketPlayClientFlying) {
            if (shooting) this.ticks++;
        }

        // Listen to release packet to calculate the force
        if (packet instanceof GPacketPlayClientBlockDig) {
            final GPacketPlayClientBlockDig wrapper = (GPacketPlayClientBlockDig) packet;

            // Ensure first that we're shooting
            if (!shooting) {
                return;
            }

            // Packet validation
            final boolean valid = wrapper.getType().equals(PlayerEnums.DigType.RELEASE_USE_ITEM);

            // Ensure the player isn't changing slots or whatnot
            final boolean changed = wrapper.getType().equals(PlayerEnums.DigType.SWAP_HELD_ITEMS)
                    || wrapper.getType().equals(PlayerEnums.DigType.DROP_ITEM)
                    || wrapper.getType().equals(PlayerEnums.DigType.DROP_ALL_ITEMS);

            // If player action changed, just return
            if (changed) {
                reset();
                return;
            }

            // Ignore other packets, our other bad packets pick it up
            if (!valid) {
                return;
            }

            // Todo balance type check (a bit like timer... timer picks fastbow up what am I doing bruh)

            this.lastShot = packet.getTimestamp();
        }
    }

    private void reset() {
        this.shooting = false;
        this.ticks = 0;
    }
}
