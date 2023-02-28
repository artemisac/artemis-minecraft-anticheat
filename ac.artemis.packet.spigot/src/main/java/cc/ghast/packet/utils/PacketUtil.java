package cc.ghast.packet.utils;

import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.packet.spigot.utils.ServerUtil;

/**
 * @author Ghast
 * @since 15/09/2020
 * ArtemisPacket © 2020
 */
public class PacketUtil {
    public static String BLOCK_PLACE = ServerUtil.getGameVersion().isBelow(ProtocolVersion.V1_11) ? "PacketPlayInBlockPlace" : "PacketPlayInUseItem";
}
