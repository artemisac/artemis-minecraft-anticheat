package ac.artemis.core.v5.features.setback;

import ac.artemis.core.v4.data.PlayerData;

public interface SetbackFeature {
    void tick(final PlayerData data, boolean flag, final boolean force);
}
