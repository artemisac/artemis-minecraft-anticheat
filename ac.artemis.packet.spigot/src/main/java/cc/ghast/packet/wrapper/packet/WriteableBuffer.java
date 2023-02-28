package cc.ghast.packet.wrapper.packet;

import cc.ghast.packet.buffer.ProtocolByteBuf;

public interface WriteableBuffer {
    void write(ProtocolByteBuf byteBuf);
}
