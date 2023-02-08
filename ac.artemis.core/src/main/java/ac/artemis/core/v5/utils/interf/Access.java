package ac.artemis.core.v5.utils.interf;

import ac.artemis.core.Artemis;
import ac.artemis.core.v4.data.PlayerDataManager;

public interface Access {
    default PlayerDataManager getDataManager() {
        return Artemis.v().getApi().getPlayerDataManager();
    }
}
