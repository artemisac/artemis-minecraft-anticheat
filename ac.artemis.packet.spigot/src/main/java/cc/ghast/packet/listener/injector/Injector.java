package cc.ghast.packet.listener.injector;

import cc.ghast.packet.profile.ArtemisProfile;
import ac.artemis.packet.callback.LoginCallback;
import ac.artemis.packet.callback.PacketCallback;
import ac.artemis.packet.spigot.wrappers.GPacket;

import java.util.UUID;
import java.util.function.Consumer;

public interface Injector {

    String clientBound = "artemis_client";
    String serverBound = "artemis_server";
    String encoder = "artemis_encoder";

    void injectReader();
    void uninjectReader();
    void injectFuturePlayer(ArtemisProfile profile);
    void uninjectFuturePlayer(ArtemisProfile profile);
    void injectPlayer(ArtemisProfile uuid);
    void uninjectPlayer(UUID uuid);
    ArtemisProfile getProfile(UUID uuid);
    boolean contains(ArtemisProfile profile);

    void addLoginCallback(LoginCallback loginCallback);
    void removeLoginCallback(LoginCallback loginCallback);
    void callLoginCallbacks(ArtemisProfile profile);

    void writePacket(UUID target, GPacket packet, boolean flush, Consumer<PacketCallback> callback);
}
