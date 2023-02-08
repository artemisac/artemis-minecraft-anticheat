package ac.artemis.core.v5.reflect;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ReflectBridge {
    private BukkitReflection bukkitReflection;

    public void init(BukkitReflection bukkitReflection) {
        ReflectBridge.bukkitReflection = bukkitReflection;
    }

    public void kill() {
        ReflectBridge.bukkitReflection = null;
    }

    public BukkitReflection v() {
        return bukkitReflection;
    }
}
