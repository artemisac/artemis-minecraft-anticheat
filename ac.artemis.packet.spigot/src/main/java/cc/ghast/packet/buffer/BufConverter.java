package cc.ghast.packet.buffer;

import ac.artemis.packet.protocol.ProtocolVersion;
import cc.ghast.packet.wrapper.netty.MutableByteBuf;

import java.io.IOException;

/**
 * Credits to Myles for the structure function. Looks dope and honestly is hella stable.
 *
 * @author Ghast
 * @since 01-May-20
 */
public abstract class BufConverter<T> {
    private final Class<? super T> type;
    private final String name;


    public BufConverter(Class<? super T> clazz) {
        this(clazz.getSimpleName(), clazz);
    }

    public BufConverter(String name, Class<? super T> clazz) {
        this.name = name;
        this.type = clazz;
    }

    public Class<? super T> getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public abstract void write(MutableByteBuf buffer, T value) throws IOException;

    public abstract T read(MutableByteBuf buffer, ProtocolVersion version, Object... args) throws IOException;
}
