package ac.artemis.anticheat.api.listener;

import ac.artemis.packet.minecraft.entity.impl.Player;

public interface InjectListener {
    void onInject(final Player player);

    void onDestroy(final Player player);
}
