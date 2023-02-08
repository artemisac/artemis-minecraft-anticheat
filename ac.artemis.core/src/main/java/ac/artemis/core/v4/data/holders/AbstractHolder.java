package ac.artemis.core.v4.data.holders;

import ac.artemis.core.v4.data.PlayerData;

public abstract class AbstractHolder {
    protected final PlayerData data;

    public AbstractHolder(PlayerData data) {
        this.data = data;
    }
}
