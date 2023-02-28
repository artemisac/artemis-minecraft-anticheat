package ac.artemis.packet.spigot;

import ac.artemis.packet.PacketAPI;
import ac.artemis.packet.PacketGenerator;
import ac.artemis.packet.PacketListener;
import ac.artemis.packet.callback.LoginCallback;
import ac.artemis.packet.callback.PacketCallback;
import ac.artemis.packet.profile.Profile;
import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.packet.protocol.format.WrittenEnumProtocol;
import ac.artemis.packet.spigot.utils.access.Accessor;
import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.Packet;
import cc.ghast.packet.PacketManager;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class ArtemisSpigotApi extends Accessor implements PacketAPI {
    public ArtemisSpigotApi(Plugin plugin) {
        super(plugin);
    }

    private final Map<ProtocolVersion, WrittenEnumProtocol> protocolMap = new HashMap<>();
    private final Map<ProtocolVersion, PacketGenerator> packetGenerators = new HashMap<>();

    @Override
    public void create() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public void addListener(PacketListener listener) {
        PacketManager.INSTANCE.getManager().injectListener(listener);
    }

    @Override
    public void addProtocol(ProtocolVersion version, WrittenEnumProtocol writtenEnumProtocol) {
        protocolMap.put(version, writtenEnumProtocol);
    }

    @Override
    public void addGenerator(ProtocolVersion version, PacketGenerator generator) {
        packetGenerators.put(version, generator);
    }

    @Override
    public PacketGenerator getGenerator(ProtocolVersion version) {
        return packetGenerators.get(version);
    }

    @Override
    public Profile getProfile(UUID uuid) {
        return PacketManager.INSTANCE.getListener().getInjector().getProfile(uuid);
    }

    @Override
    public boolean isInjected(UUID uuid) {
        return PacketManager.INSTANCE.getListener().getInjector().getProfile(uuid) != null;
    }

    @Override
    public void disinject(UUID player) {
        if (isInjected(player))
            PacketManager.INSTANCE.getListener().getInjector()
                    .uninjectPlayer(player);
    }

    @Override
    public ProtocolVersion getVersion(UUID uuid) {
        return PacketManager.INSTANCE.getListener().getInjector().getProfile(uuid).getVersion();
    }

    @Override
    public void sendPacket(UUID player, Packet packet) {
        sendPacket(player, packet, true,null);
    }

    @Override
    public void sendPacket(UUID player, Packet packet, Consumer<PacketCallback> callback) {
        sendPacket(player, packet, true, callback);
    }

    @Override
    public void sendPacket(UUID player, Packet packet, boolean flush) {
        sendPacket(player, packet, flush, null);
    }

    @Override
    public void sendPacket(UUID player, Packet packet, boolean flush, Consumer<PacketCallback> callback) {
        PacketManager.INSTANCE.getListener().getInjector().writePacket(player, (GPacket) packet, flush, callback);
    }

    @Override
    public void addLoginCallback(LoginCallback loginCallback) {
        PacketManager.INSTANCE.getListener().getInjector().addLoginCallback(loginCallback);
    }

    @Override
    public void removeLoginCallback(LoginCallback loginCallback) {
        PacketManager.INSTANCE.getListener().getInjector().removeLoginCallback(loginCallback);
    }
}
