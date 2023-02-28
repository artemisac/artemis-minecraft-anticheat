package ac.artemis.packet.protocol.format;

import ac.artemis.packet.protocol.ProtocolState;
import ac.artemis.packet.wrapper.PacketMap;

public class EnumProtocolFormat {
    private final ProtocolState state;
    private final PacketMap inboundPackets;
    private final PacketMap outboundPackets;

    public EnumProtocolFormat(ProtocolState state, PacketMap inboundPackets, PacketMap outboundPackets) {
        this.state = state;
        this.inboundPackets = inboundPackets;
        this.outboundPackets = outboundPackets;
    }

    public ProtocolState getState() {
        return state;
    }

    public PacketMap getInboundPackets() {
        return inboundPackets;
    }

    public PacketMap getOutboundPackets() {
        return outboundPackets;
    }
}
