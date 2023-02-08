package ac.artemis.core.v5.features.tick.impl;

import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.utils.chat.StringUtil;
import ac.artemis.packet.PacketManager;
import cc.ghast.packet.PacketAPI;
import ac.artemis.packet.protocol.ProtocolVersion;
import cc.ghast.packet.wrapper.mc.PlayerEnums;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientResourcePackStatus;
import cc.ghast.packet.wrapper.packet.play.server.GPacketPlayServerResourcePackSend;

public class ResourcePackTickHandler extends AbstractTickHandler<GPacketPlayClientResourcePackStatus> {
    public ResourcePackTickHandler(final PlayerData data) {
        super(data);
    }

    @Override
    public boolean canReceive() {
        return data.getVersion().isOrBelow(ProtocolVersion.V1_8_9);
    }

    @Override
    public boolean isPacket(final GPacket packet) {
        if (!(packet instanceof GPacketPlayClientResourcePackStatus))
        return false;

        final GPacketPlayClientResourcePackStatus wrapper = (GPacketPlayClientResourcePackStatus) packet;
        /*
         * Theoretically speaking, the client can ONLY respond with FAILED_DOWNLOAD. Anything other
         * than that is completely invalid. Hence we can just skip over it.
         */
        if (!wrapper.getStatus().equals(PlayerEnums.ResourcePackStatus.FAILED_DOWNLOAD))
            return false;

        if (!wrapper.getUrl().isPresent())
            return false;

        if (!wrapper.getUrl().get().contains(data.getPlayer().getName()))
            return false;

        return StringUtil.isNumeric(wrapper.getUrl().get());
    }

    @Override
    public short getPacketId(final GPacket packet) {
        return Short.parseShort(((GPacketPlayClientResourcePackStatus) packet).getUrl().get());
    }

    @Override
    public void onConsume(final GPacketPlayClientResourcePackStatus packet) {
        data.connection.resourcePackTick = Integer.parseInt(packet.getUrl().get());
    }

    @Override
    public void onPush(final boolean flush, final short id) {
        final GPacket packet = new GPacketPlayServerResourcePackSend(
                "level://" + data.getPlayer().getName() + "_" + Math.random() + "/resources.zip",
                Short.toString(id)
        );
        PacketManager.getApi().sendPacket(data.getPlayer().getUniqueId(), packet);
    }
}
