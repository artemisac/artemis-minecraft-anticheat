package ac.artemis.core.v4.check.manager;

import ac.artemis.core.v4.check.exceptions.CheckNotFoundException;
import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.data.PlayerData;
import com.google.common.collect.ImmutableClassToInstanceMap;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public abstract class AbstractCheckManager {
    @Getter
    @Setter
    public ImmutableClassToInstanceMap<ArtemisCheck> checks;
    @Getter
    private PlayerData data;

    public AbstractCheckManager(PlayerData data) {
        this.data = data;
        this.initChecks();
    }

    public abstract void initChecks();

    public Collection<ArtemisCheck> getAbstractChecks() {
        return Collections.unmodifiableCollection(checks.values());
    }

    public <T extends ArtemisCheck> T getCheck(Class<T> clazz) {
        return checks.getInstance(clazz);
    }

    public ArtemisCheck getCheckByName(String name) {
        Map.Entry<Class<? extends ArtemisCheck>, ArtemisCheck> var = checks.entrySet()
                .stream()
                .filter(entry -> (entry.getValue().info.getType().name() + entry.getValue().info.getVar()).equalsIgnoreCase(name))
                .findFirst().orElse(null);
        if (var == null) throw new CheckNotFoundException();
        return var.getValue();
    }
}
