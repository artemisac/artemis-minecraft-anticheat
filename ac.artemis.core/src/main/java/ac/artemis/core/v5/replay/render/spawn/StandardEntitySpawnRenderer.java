package ac.artemis.core.v5.replay.render.spawn;

import ac.artemis.anticheat.replay.ReplayPlayerInfo;
import ac.artemis.anticheat.replay.ReplayPlayerRepository;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v5.replay.render.EntitySpawnRenderer;
import ac.artemis.packet.PacketManager;
import cc.ghast.packet.wrapper.packet.play.server.GPacketPlayServerSpawnNamedEntity;

public class StandardEntitySpawnRenderer implements EntitySpawnRenderer {
    @Override
    public void spawn(final PlayerData data, final ReplayPlayerRepository repository) {
        int id = data.getPlayer().getEntityId();

        for (final ReplayPlayerInfo entity : repository.getEntities()) {
            final GPacketPlayServerSpawnNamedEntity spawn = new GPacketPlayServerSpawnNamedEntity(data.getPlayerID(), data.getVersion());

            spawn.setEntityId(id++);
            spawn.setObjectUUID(entity.getUuid());

            spawn.setX(entity.getSpawnX());
            spawn.setY(entity.getSpawnY());
            spawn.setZ(entity.getSpawnZ());

            spawn.setYaw(entity.getSpawnYaw());
            spawn.setPitch(entity.getSpawnPitch());

            PacketManager.getApi().sendPacket(data.getPlayerID(), spawn, false, null);
        }
    }
}
