package ac.artemis.packet.generator.util;

import ac.artemis.packet.generator.reflections.Reflection;
import ac.artemis.packet.protocol.ProtocolVersion;
import lombok.Getter;

import static ac.artemis.packet.protocol.ProtocolVersion.UNKNOWN;

public class ServerUtil {
    private static final ProtocolVersion gameVersion = fetchGameVersion();

    private static ProtocolVersion fetchGameVersion() {
        for (ProtocolVersion version : ProtocolVersion.values()) {
            if (version.getServerVersion() != null && version.getServerVersion().equalsIgnoreCase(Reflection.VERSION))
                return version;

        }
        return UNKNOWN;
    }

    public static ProtocolVersion getGameVersion() {
        return gameVersion;
    }
}
