package cc.ghast.packet.wrapper.packet;

import ac.artemis.packet.protocol.ProtocolVersion;
import lombok.Getter;

import java.util.function.Predicate;

/**
 * @author Ghast
 * @since 31/10/2020
 * ArtemisPacket © 2020
 */

@Getter
public class PacketInformation {
    private final String nmsName;
    private final Predicate<ProtocolVersion>[] versionPredicate;

    public PacketInformation(String nmsName, Predicate<ProtocolVersion>... versionPredicate) {
        this.nmsName = nmsName;
        this.versionPredicate = versionPredicate;
    }

    public PacketInformation(String nmsName) {
        this.nmsName = nmsName;
        this.versionPredicate = new Predicate[0];
    }

    public boolean isValid(ProtocolVersion version) {
        for (Predicate<ProtocolVersion> pred : versionPredicate) {
            if (!pred.test(version)) return false;
        }

        return true;
    }
}
