package cc.ghast.packet.chain;

import ac.artemis.packet.PacketListener;
import cc.ghast.packet.profile.ArtemisProfile;
import ac.artemis.packet.spigot.wrappers.GPacket;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author Ghast
 * @since 15/08/2020
 * Artemis © 2020
 */
public class ChainManager {
    private final List<PacketListener> packetListeners = new ArrayList<>();

    public void callPacket(ArtemisProfile profile, GPacket packet){
        this.packetListeners.forEach(listener -> {
            Runnable exec = () -> listener.onPacket(profile, packet);

            if (listener.isAsync()) {
                CompletableFuture.runAsync(exec);
                return;
            }

            exec.run();
        });
    }

    public void injectListener(PacketListener listener) {
        packetListeners.add(listener);
    }
}
