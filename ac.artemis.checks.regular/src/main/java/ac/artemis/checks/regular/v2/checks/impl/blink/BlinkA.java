package ac.artemis.checks.regular.v2.checks.impl.blink;

import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.annotations.Experimental;
import ac.artemis.core.v4.check.enums.CheckType;
import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.lag.LagManager;
import ac.artemis.packet.minecraft.entity.impl.Player;
import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.client.*;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientKeepAlive;
import ac.artemis.core.v4.utils.time.TimeUtil;

@Check(type = Type.BLINK, var = "A")
@Experimental
public class BlinkA  extends ArtemisCheck implements PacketHandler {

    public BlinkA(PlayerData data, CheckInformation info) {
        super(data, info);
    }

    private boolean blinking;
    private int forLoc = 0;
    private long lastMillis, lastStandTicks;

    @Override
    public void handle(final GPacket packet) {
        int ping = LagManager.getPing(data.getPlayer());
        if (ping > 150 || !TimeUtil.hasExpired(data.combat.lastDamage, 1)) return;
        Player player = data.getPlayer();

        if (packet instanceof GPacketPlayClientKeepAlive) {
            if (player.isInsideVehicle() || player.isDead()) return;
            if (isNull(CheckType.POSITION)) return;
            long deltaMillis = (long) TimeUtil.convert(TimeUtil.elapsed(data.movement.getLastLocation().getTimestamp()),
                    3, TimeUtil.TimeUnits.SECONDS);
            if (deltaMillis > 1 && data.movement.standTicks < 20 && data.movement.standTicks > 0
                    && data.user.isLagging()) {
                blinking = true;
                this.lastMillis = deltaMillis;
                this.lastStandTicks = data.movement.standTicks;
                //processor.getPlayer().teleport(processor.movement.getLastLocation().toBukkitLocation());
            }
        } else if (packet instanceof PacketPlayClientFlying) {
            if (blinking) {
                if (forLoc == 0) {
                    ++forLoc;
                } else if (forLoc == 1) {

                    final double xDiff = data.movement.getLastLocation().getX() - data.movement.getLocation().getX();
                    final double yDiff = data.movement.getLastLocation().getY() - data.movement.getLocation().getY();
                    final double zDiff = data.movement.getLastLocation().getZ() - data.movement.getLocation().getZ();

                    if (xDiff * yDiff * zDiff == 0) {
                        debug("One of the values is null");
                        return;
                    }

                    if (xDiff > 0.21 || xDiff < -0.21 || yDiff < -0.21 || yDiff > 0.21 || zDiff < -0.21 || zDiff > 0.21) {
                        log("xDiff=" + xDiff + " yDiff=" + yDiff + " zDiff=" + zDiff + " stand="
                                + lastStandTicks + " lastMillis=" + lastMillis);
                    }
                    blinking = false;
                    forLoc = 0;
                }
            }
        }
    }
}
