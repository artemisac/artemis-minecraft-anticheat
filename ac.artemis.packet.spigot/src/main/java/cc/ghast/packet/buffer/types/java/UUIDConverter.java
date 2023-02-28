package cc.ghast.packet.buffer.types.java;

import ac.artemis.packet.protocol.ProtocolVersion;
import cc.ghast.packet.wrapper.netty.MutableByteBuf;
import cc.ghast.packet.buffer.BufConverter;

import java.util.UUID;

public class UUIDConverter extends BufConverter<UUID> {
    public UUIDConverter() {
        super("uuid", UUID.class);
    }

    @Override
    public void write(MutableByteBuf buffer, UUID value) {
        buffer.writeLong(value.getMostSignificantBits());
        buffer.writeLong(value.getLeastSignificantBits());
    }

    @Override
    public UUID read(MutableByteBuf buffer, ProtocolVersion version, Object... args) {
        return new UUID(buffer.readLong(), buffer.readLong());
    }
}
