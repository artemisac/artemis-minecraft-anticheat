package ac.artemis.core.v4.emulator.datawatcher.moderna;

import cc.ghast.packet.buffer.ProtocolByteBuf;

import java.io.IOException;

public interface DataSerializer<T>
{
    void write(ProtocolByteBuf buf, T value);

    T read(ProtocolByteBuf buf) throws IOException;

    DataParameter<T> createKey(int id);

    T copyValue(T value);
}
