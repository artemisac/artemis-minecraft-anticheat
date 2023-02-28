package ac.artemis.packet;

import ac.artemis.packet.callback.LoginCallback;
import ac.artemis.packet.callback.PacketCallback;
import ac.artemis.packet.profile.Profile;
import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.packet.protocol.format.WrittenEnumProtocol;
import ac.artemis.packet.wrapper.Packet;

import java.util.UUID;
import java.util.function.Consumer;

public interface PacketAPI {
    void addListener(PacketListener listener);

    void addProtocol(ProtocolVersion version, WrittenEnumProtocol writtenEnumProtocol);

    void addGenerator(ProtocolVersion version, PacketGenerator generator);

    PacketGenerator getGenerator(ProtocolVersion version);

    Profile getProfile(UUID uuid) ;

    boolean isInjected(UUID uuid) ;

    void disinject(UUID player);

    ProtocolVersion getVersion(UUID uuid) ;

    void sendPacket(UUID player, Packet packet);

    void sendPacket(UUID player, Packet packet, boolean flush);

    void sendPacket(UUID player, Packet packet, Consumer<PacketCallback> callback);

    void sendPacket(UUID player, Packet packet, boolean flush, Consumer<PacketCallback> callback);

    void addLoginCallback(LoginCallback loginCallback) ;

    void removeLoginCallback(LoginCallback loginCallback) ;
}
