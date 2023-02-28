package ac.artemis.packet.protocol.format;

import ac.artemis.packet.protocol.ProtocolState;
import ac.artemis.packet.protocol.ProtocolVersion;

import java.util.Map;

public class WrittenEnumProtocol {
    private final ProtocolVersion version;
    private final Map<ProtocolState, EnumProtocolFormat> formatMap;

    public WrittenEnumProtocol(ProtocolVersion version, Map<ProtocolState, EnumProtocolFormat> formatMap) {
        this.version = version;
        this.formatMap = formatMap;
    }

    public ProtocolVersion getVersion() {
        return version;
    }

    public Map<ProtocolState, EnumProtocolFormat> getFormatMap() {
        return formatMap;
    }
}
