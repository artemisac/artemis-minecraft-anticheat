package ac.artemis.anticheat.api.check;

import java.util.List;

public interface CheckReload {
    void reloadAssets();
    List<CheckInfo> getChecks();
}
