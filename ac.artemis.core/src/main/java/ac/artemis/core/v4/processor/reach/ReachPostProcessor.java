package ac.artemis.core.v4.processor.reach;

import ac.artemis.core.v4.config.ConfigManager;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.processor.AbstractHandler;
import ac.artemis.core.v4.utils.reach.ReachEntity;
import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.client.PacketPlayClientFlying;

import java.util.HashSet;
import java.util.Set;

public class ReachPostProcessor extends AbstractHandler {
    public ReachPostProcessor(PlayerData data) {
        super("Reach [0x02]", data);
    }

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof PacketPlayClientFlying){
            final int maxBranch = ConfigManager.getReach().getV1_maxBranch();
            for (ReachEntity e : data.reach.getEntities().values()) {
                final Set<ReachEntity.ReachPosition> pos = new HashSet<>(e.reachPositions);

                if (e.isConfirming()) {
                    if (e.getReachPositions().size() > maxBranch) {
                        data.monke("Err: Invalid R-Type Connection (0x" + maxBranch + ")");
                    }

                    for (ReachEntity.ReachPosition reachPosition : e.reachPositions) {
                        final ReachEntity.ReachPosition rel = reachPosition.clone();
                        rel.setPositionAndRotation2(e.nextReach.getX(), e.nextReach.getY(), e.nextReach.getZ(), 3);
                        rel.skip = true;

                        if (pos.size() >
                                (e.equals(data.reach.getLastAttackedEntity()) ? maxBranch : 32)) {
                            break;
                        }

                        pos.add(rel);
                        pos.add(reachPosition);
                    }
                }

                e.tick();

                e.reachPositions.clear();
                e.reachPositions.addAll(new HashSet<>(pos));
            }
        }
    }
}
