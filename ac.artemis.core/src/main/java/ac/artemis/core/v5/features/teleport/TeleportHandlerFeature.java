package ac.artemis.core.v5.features.teleport;

import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.play.server.GPacketPlayServerPosition;

public interface TeleportHandlerFeature {
    boolean isTeleport(final PlayerData data, final GPacket flying);
    void queueTeleport(GPacketPlayServerPosition packet);
}
