package ac.artemis.core.v4.lag;

import ac.artemis.packet.minecraft.entity.impl.Player;
import ac.artemis.core.Artemis;
import ac.artemis.core.v4.api.Manager;

import ac.artemis.core.v4.utils.action.InitializeAction;
import ac.artemis.core.v4.utils.action.ShutdownAction;
import ac.artemis.core.v5.reflect.ReflectBridge;
import cc.ghast.packet.reflections.ReflectUtil;
import lombok.SneakyThrows;

public class LagManager extends Manager {
    public Artemis artemis;

    public LagManager(Artemis plugin) {
        super(plugin, "Lag [Manager]");
    }

    @Override
    public void init(InitializeAction initializeAction) {
        // Do nothing
    }

    @Override
    public void disinit(ShutdownAction shutdownAction) {
        // haha nope nothing to see here
    }

    // why are you calling upon reflection? You can use keep alives. It'd be better performance-wise as well.
    // I know ¯\_(ツ)_/¯
    public static int getPing(Player player) {
        return ReflectBridge.v().getPing(player);
    }
}

