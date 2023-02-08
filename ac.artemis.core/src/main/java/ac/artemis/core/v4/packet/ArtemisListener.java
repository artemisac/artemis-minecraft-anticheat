package ac.artemis.core.v4.packet;

import ac.artemis.core.Artemis;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.utils.graphing.Pair;
import ac.artemis.packet.PacketListener;
import ac.artemis.packet.profile.Profile;
import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.Packet;

/**
 * @author Ghast
 * @since 17/08/2020
 * Artemis © 2020
 */
public class ArtemisListener implements PacketListener {

    @Override
    public void onPacket(Profile profile, Packet packet) {
        Pair<PlayerData, PacketExecutor> data = Artemis.v().getApi().getPlayerDataManager().getDataAndExecutor(profile.getUuid());
        final boolean invalid = packet == null || data == null;
        if (invalid) {
            return;
        }
        data.getY().executePacket(data.getX(), (GPacket) packet);
    }


}
