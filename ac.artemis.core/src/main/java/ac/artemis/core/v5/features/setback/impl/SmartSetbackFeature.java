package ac.artemis.core.v5.features.setback.impl;

import ac.artemis.packet.minecraft.Server;
import ac.artemis.core.Artemis;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.utils.position.PlayerMovement;
import ac.artemis.core.v4.utils.time.TimeUtil;
import ac.artemis.core.v5.features.setback.SetbackFeature;

public class SmartSetbackFeature implements SetbackFeature {
    private PlayerMovement lastLegitLocation;
    private long lastSetback;

    @Override
    public void tick(final PlayerData data, final boolean flag, final boolean force) {
        if (!flag) {
            if (force || data.prediction.isGround() && data.prediction.isLastGround())
                this.lastLegitLocation = data.prediction.getMovement();
            return;
        }

        // Make sure at least 250ms happened since the last set back. We don't need no 20k teleports in a second.
        // Also make sure there was previously a legit location.
        if (lastLegitLocation == null) {
            return;
        }

        // If last setback was too soon, return
        if (!TimeUtil.elapsed(lastSetback, 1000) && lastLegitLocation.distanceXZ(data.prediction.getMovement()) < 10) {
            return;
        }

        // Make sure it's not caused by lag or rubberbanding
        if (!TimeUtil.hasExpired(data.user.getJoin(), 10) || !TimeUtil.hasExpired(data.user.getLastRespawn(), 1)) {
            return;
        }

        // Teleport to the last legit location using Bukkit to properly get the event called.
        Server.v().getScheduler().runTask(
                () -> {
                    data.resetMotion();
                    data.getPlayer().teleport(lastLegitLocation.toBukkitLocation());
                }
        );

        data.user.setSetback(false);

        // Set the last setback to when this happened.
        this.lastSetback = System.currentTimeMillis();
    }
}
