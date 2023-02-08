package ac.artemis.checks.regular.v2.checks.impl.badpackets;

import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.debug.Debug;
import ac.artemis.core.v4.check.packet.PacketExcludable;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.bukkit.Vector3D;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientBlockPlace;

@Check(type = Type.BADPACKETS, var = "Q", threshold = 1)
public final class BadPacketsQ extends ArtemisCheck implements PacketHandler, PacketExcludable {

    public BadPacketsQ(final PlayerData data, final CheckInformation info) {
        super(data, info);

        this.setCompatiblePackets(GPacketPlayClientBlockPlace.class);
    }

    @Override
    public void handle(final GPacket packet) {
        final GPacketPlayClientBlockPlace wrapper = (GPacketPlayClientBlockPlace) packet;

        final Vector3D vector = wrapper.getVector();

        /*
         * The variable value cannot be larger than 1 or smaller than 0, as stated here.
         * https://wiki.vg/Protocol#Player_Block_Placement
         */
        if (vector.getX() > 1.0 || vector.getX() < 0.0) this.log(new Debug<>("x", vector.getX()));
        if (vector.getY() > 1.0 || vector.getY() < 0.0) this.log(new Debug<>("y", vector.getY()));
        if (vector.getZ() > 1.0 || vector.getZ() < 0.0) this.log(new Debug<>("z", vector.getY()));
    }
}
