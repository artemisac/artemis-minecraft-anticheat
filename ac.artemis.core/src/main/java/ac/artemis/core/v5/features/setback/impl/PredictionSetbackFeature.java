package ac.artemis.core.v5.features.setback.impl;

import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v5.utils.raytrace.Point;
import ac.artemis.core.v5.features.setback.SetbackFeature;

public class PredictionSetbackFeature implements SetbackFeature {
    private Point lastLegitLocation;
    private long lastSetback;

    @Override
    public void tick(final PlayerData data, final boolean flag, final boolean force) {
        if (!flag) {
            if (force || data.prediction.isGround())
                this.lastLegitLocation = data.entity.getPosition();
            return;
        }

        // Make sure at least 250ms happened since the last set back. We don't need no 20k teleports in a second.
        // Also make sure there was previously a legit location.
        if (lastLegitLocation == null) {
            return;
        }

        //PacketAPI.sendPacket(data.getPlayer(), new GPacketPlayServerPosition());

        data.user.setSetback(false);

        // Set the last setback to when this happened.
        this.lastSetback = System.currentTimeMillis();
    }
}
