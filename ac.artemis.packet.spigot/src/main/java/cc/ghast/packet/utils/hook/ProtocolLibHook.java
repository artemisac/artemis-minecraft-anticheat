package cc.ghast.packet.utils.hook;

import com.comphenix.protocol.ProtocolLib;

/**
 * @author Ghast
 * @since 31/08/2020
 * Artemis © 2020
 */

public class ProtocolLibHook {
    static {
        ProtocolLib.getPlugin(ProtocolLib.class);
    }
}
