package ac.artemis.core.v5.utils;

import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v5.utils.misc.Step;
import ac.artemis.core.v5.utils.bounding.BoundingBox;
import ac.artemis.packet.PacketManager;
import cc.ghast.packet.wrapper.bukkit.Particle;
import cc.ghast.packet.wrapper.packet.play.server.GPacketPlayServerWorldParticles;
import lombok.experimental.UtilityClass;

@UtilityClass
public class BoundingUtil {
    public void drawBox(final BoundingBox bb, final Particle particle, final PlayerData data) {
        Step.GenericStepper<Float> x = Step.step((float) bb.minX, 0.241f, (float) bb.maxX);
        Step.GenericStepper<Float> y = Step.step((float) bb.minY, 0.241f, (float) bb.maxY);
        Step.GenericStepper<Float> z = Step.step((float) bb.minZ, 0.241f, (float) bb.maxZ);
        for (float fx : x) {
            for (float fy : y) {
                for (float fz : z) {
                    int check = 0;
                    if (x.first() || x.last()) check++;
                    if (y.first() || y.last()) check++;
                    if (z.first() || z.last()) check++;
                    if (check >= 2) {
                        final GPacketPlayServerWorldParticles packet = new GPacketPlayServerWorldParticles("",
                                data.getPlayer().getUniqueId(), data.getVersion(),
                                particle, fx, fy, fz, 0F, 0F, 0F, 0.F, 1, true);

                        PacketManager.getApi().sendPacket(data.getPlayer().getUniqueId(), packet);
                    }
                }
            }
        }
    }
}
